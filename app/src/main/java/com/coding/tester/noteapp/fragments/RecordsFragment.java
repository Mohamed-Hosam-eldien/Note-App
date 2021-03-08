package com.coding.tester.noteapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.coding.tester.noteapp.Utilities;
import com.coding.tester.noteapp.databinding.MediaLayoutBinding;
import com.coding.tester.noteapp.events.DeleteRecordEvent;
import com.coding.tester.noteapp.events.RecordFromMenu;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import dagger.hilt.android.AndroidEntryPoint;
import com.coding.tester.noteapp.R;
import com.coding.tester.noteapp.adapter.RecordAdapter;
import com.coding.tester.noteapp.callback.ClickToAddNote;
import com.coding.tester.noteapp.databinding.RecordDialogBinding;
import com.coding.tester.noteapp.databinding.RecordsFragmentBinding;
import com.coding.tester.noteapp.events.ClickToRecordEvent;
import com.coding.tester.noteapp.viewModel.NoteViewModel;


@AndroidEntryPoint
public class RecordsFragment extends Fragment implements ClickToAddNote {

    private boolean isRecording;
    private MediaRecorder mediaRecorder;
    private RecordDialogBinding recordDialogBinding;
    private Context thisContext;
    private RecordAdapter adapter;
    private MediaPlayer mediaPlayer;
    private NoteViewModel viewModel;
    private boolean isPlaying;
    private boolean isCompleted;
    private File fileToPlaying;
    private BottomSheetDialog bottomDialog;
    private MediaLayoutBinding mediaBinding;
    private Handler seekBarHandler;
    private Runnable updateSeekBar;
    private final Utilities utils = new Utilities();
    private RecordsFragmentBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        thisContext = context;
    }

    public RecordsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = RecordsFragmentBinding.bind(inflater.inflate(R.layout.records_fragment, container, false));
        binding.setClickToAddRecord(this);

        initBottomSheetDialog();

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        adapter = new RecordAdapter();
        binding.recyclerRecords.setHasFixedSize(true);
        binding.recyclerRecords.setAdapter(adapter);

        loadRecords();

        return binding.getRoot();
    }

    private void loadRecords() {
        File[] files = viewModel.getRecordFiles(thisContext);
        if(files.length > 0){
            adapter.seRecordList(files);
            binding.recyclerRecords.setVisibility(View.VISIBLE);
            binding.txtClick.setVisibility(View.GONE);
            binding.view.setVisibility(View.GONE);
        } else {
            binding.recyclerRecords.setVisibility(View.GONE);
            binding.txtClick.setVisibility(View.VISIBLE);
            binding.view.setVisibility(View.VISIBLE);
        }
    }

    private void resumeAudio() {
        mediaPlayer.start();
        mediaBinding.imgPLay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
        isPlaying = true;

        updateSeekRunnable();
        seekBarHandler.postDelayed(updateSeekBar,0);
    }

    private void pauseAudio() {
        mediaPlayer.pause();
        mediaBinding.imgPLay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
        isPlaying = false;
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    private void initBottomSheetDialog() {
        bottomDialog = new BottomSheetDialog(thisContext, R.style.BottomSheetDialogTheme);
        bottomDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = LayoutInflater.from(thisContext).inflate(R.layout.media_layout, null);
        mediaBinding = MediaLayoutBinding.bind(view);
        bottomDialog.setContentView(view);

        mediaBinding.seekBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        mediaBinding.seekBar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);


        mediaBinding.imgPLay.setOnClickListener(view2 ->  {
            if(isPlaying)
                pauseAudio();
            else {
                if (fileToPlaying != null) {
                    if (isCompleted)
                        playAudio(fileToPlaying);
                    else
                        resumeAudio();
                }
            } });

        mediaBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(fileToPlaying != null) {
                    pauseAudio();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(fileToPlaying != null){
                    mediaPlayer.seekTo(seekBar.getProgress());
                    resumeAudio();
                }
            }
        });

        bottomDialog.setOnDismissListener(dialogInterface -> stopAudio());

    }

    @Override // show record dialog
    public void gotoNoteActivity() {
        showRecordDialog(); }

    private void showRecordDialog() {

        Dialog dialog = new Dialog(thisContext);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = LayoutInflater.from(thisContext).inflate(R.layout.record_dialog, null);
        dialog.setContentView(view);
        recordDialogBinding = RecordDialogBinding.bind(view);

        recordDialogBinding.imgRecord.setOnClickListener(view1 -> clickToRecordImage(dialog));

        dialog.show();

    }

    private void clickToRecordImage(Dialog dialog){

        if(isRecording) {

            // stop record
            stopRecord();
            recordDialogBinding.imgRecord.setImageResource(R.drawable.record_img_gray);
            isRecording = false;
            loadRecords();
            dialog.dismiss();
            Toast.makeText(thisContext, R.string.record_saved_successfully, Toast.LENGTH_SHORT).show();

        } else {

            // start record
            if(checkRecordPermission()) {

                startRecord();
                recordDialogBinding.imgRecord.setImageResource(R.drawable.record_img_green);
                recordDialogBinding.txtStateRecord.setText(R.string.recording);
                recordDialogBinding.txtStateRecord.setTextColor(Color.GREEN);
                isRecording = true;
            }

        }

    }

    private void stopRecord() {

        recordDialogBinding.chronometer.stop();

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void startRecord() {

        recordDialogBinding.chronometer.setBase(SystemClock.elapsedRealtime());
        recordDialogBinding.chronometer.start();
        String recordPath = thisContext.getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss", Locale.ENGLISH);
        Date nowDate = new Date();
        String recordFile = "Recording..." + format.format(nowDate) + ".3gb";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();

    }

    private boolean checkRecordPermission() {

        String recordPermission = Manifest.permission.RECORD_AUDIO;
        if(ActivityCompat.checkSelfPermission(thisContext, recordPermission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            int recordCode = 11;
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, recordCode);
            return false;
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onClickToRecord(ClickToRecordEvent event){

        if(event != null) {
            fileToPlaying = event.getFile();
            bottomDialog.show();
            if(isPlaying){
                stopAudio();
            } else {
                playAudio(fileToPlaying);
            }

        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onRecordFromMenu(RecordFromMenu event) {
        if(event.isRecordFromMenu())
            loadRecords();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onDeletedRecord(DeleteRecordEvent event) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(thisContext);
        dialog.setTitle(R.string.delete_title);
        dialog.setMessage(getString(R.string.delete_message));

        dialog.setPositiveButton("OK", (dialogInterface, i) -> {
            if(event.getFile() != null){
                File dir = thisContext.getExternalFilesDir("/");
                File file = new File(dir, event.getFile().getName());
                if(file.delete()) {
                    Toast.makeText(thisContext, "file is deleted", Toast.LENGTH_SHORT).show();
                    loadRecords();
                } else
                    Toast.makeText(thisContext, "error", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        dialog.show();

    }

    private void playAudio(File fileToPlaying) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(fileToPlaying.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaBinding.txtFileName.setText(fileToPlaying.getName());
        mediaBinding.imgPLay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);

        isPlaying = true;
        isCompleted = false;

        mediaPlayer.setOnCompletionListener(mediaPlayer -> {
            stopAudio();
            isCompleted = true;
        });
        mediaBinding.seekBar.setMax(mediaPlayer.getDuration());

        seekBarHandler = new Handler();
        updateSeekRunnable();
        seekBarHandler.postDelayed(updateSeekBar,0);

    }

    private void updateSeekRunnable() {

        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                long totalDuration = mediaPlayer.getDuration();
                long currentDuration = mediaPlayer.getCurrentPosition();

                mediaBinding.txtTime.setText(new StringBuilder(utils.milliSecondsToTimer(currentDuration))
                        .append(" / ").append(utils.milliSecondsToTimer(totalDuration)));

                mediaBinding.seekBar.setProgress(mediaPlayer.getCurrentPosition());

                seekBarHandler.postDelayed(this, 500);
            }
        };


    }

    private void stopAudio() {
        mediaBinding.imgPLay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
        isPlaying = false;
        mediaPlayer.stop();
        seekBarHandler.removeCallbacks(updateSeekBar);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(isPlaying) {
            stopAudio();
        }

        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        EventBus.getDefault().removeAllStickyEvents();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

}
