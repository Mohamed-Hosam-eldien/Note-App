package com.coding.tester.noteapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dagger.hilt.android.AndroidEntryPoint;
import com.coding.tester.noteapp.callback.OnButtonsClick;
import com.coding.tester.noteapp.database.Note;
import com.coding.tester.noteapp.databinding.ActivityNoteBinding;
import com.coding.tester.noteapp.events.NoteClickEvent;
import com.coding.tester.noteapp.viewModel.NoteViewModel;


@AndroidEntryPoint
public class NoteActivity extends AppCompatActivity implements OnButtonsClick {

    private ActivityNoteBinding binding;
    private NoteViewModel viewModel;
    private boolean isUpdate;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_note);
        binding.setNoteCallback(this);

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        binding.btnSave.setOnClickListener(view -> saveNote());

    }


    public void saveNote() {
        if(!binding.edtDescription.getText().toString().isEmpty()) {
            Note currentNote = new Note(String.valueOf(System.currentTimeMillis()), binding.edtTitle.getText().toString(), binding.edtDescription.getText().toString());
            viewModel.insertNotes(currentNote);
            finish();
            Toast.makeText(this, R.string.saved_successfully, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, R.string.empty_message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSaveButtonClick() {
        if(isUpdate)
            updateNote();
        else
            saveNote();
    }

    private void updateNote() {
        if(!binding.edtDescription.getText().toString().isEmpty()) {
            Note currentNote = new Note(String.valueOf(System.currentTimeMillis()), binding.edtTitle.getText().toString(), binding.edtDescription.getText().toString());
            currentNote.setId(id);
            viewModel.updateNoteItem(currentNote);
            finish();
            Toast.makeText(NoteActivity.this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, R.string.empty_message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCancelButtonClick() {
        finish();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onClickToNote(NoteClickEvent event){
        if(event != null){
            isUpdate = true;
            id = event.getNote().getId();
            binding.edtTitle.setText(event.getNote().getTitle());
            binding.edtDescription.setText(event.getNote().getDescription());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        EventBus.getDefault().removeAllStickyEvents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

}