package com.coding.tester.noteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import com.coding.tester.noteapp.adapter.ViewPagerAdapter;
import com.coding.tester.noteapp.databinding.ActivityMainBinding;
import com.coding.tester.noteapp.databinding.RecordDialogBinding;
import com.coding.tester.noteapp.events.RecordFromMenu;
import com.coding.tester.noteapp.fragments.NotesFragment;
import com.coding.tester.noteapp.fragments.RecordsFragment;

import org.greenrobot.eventbus.EventBus;


@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private boolean isRecording;
    private MediaRecorder mediaRecorder;
    private RecordDialogBinding recordDialogBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        pagerAdapter.addFragment(new NotesFragment(), "Notes");
        pagerAdapter.addFragment(new RecordsFragment(), "Records");

        binding.viewPager.setAdapter(pagerAdapter);
        binding.tapLayout.setupWithViewPager(binding.viewPager);

        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.addRecord)
                showRecordDialog();
            else
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
            return true;
        });

    }


    private void showRecordDialog() {

        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = LayoutInflater.from(this).inflate(R.layout.record_dialog, null);
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
            EventBus.getDefault().postSticky(new RecordFromMenu(true));
            dialog.dismiss();
            Toast.makeText(this, R.string.record_saved_successfully, Toast.LENGTH_SHORT).show();

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
        String recordPath = this.getExternalFilesDir("/").getAbsolutePath();
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
        if(ActivityCompat.checkSelfPermission(this, recordPermission)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            int recordCode = 11;
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, recordCode);
            return false;
        }

    }

}