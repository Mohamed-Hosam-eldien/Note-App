package com.coding.tester.noteapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import dagger.hilt.android.AndroidEntryPoint;
import com.coding.tester.noteapp.NoteActivity;
import com.coding.tester.noteapp.R;
import com.coding.tester.noteapp.adapter.NoteAdapter;
import com.coding.tester.noteapp.callback.ClickToAddNote;
import com.coding.tester.noteapp.database.Note;
import com.coding.tester.noteapp.databinding.NotesFragmentBinding;
import com.coding.tester.noteapp.events.NoteDeleteEvent;
import com.coding.tester.noteapp.viewModel.NoteViewModel;

@AndroidEntryPoint
public class NotesFragment extends Fragment implements ClickToAddNote {

    private NotesFragmentBinding binding;
    private NoteViewModel viewModel;
    private NoteAdapter adapter;
    private Context thisContext;


    public NotesFragment() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        thisContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = NotesFragmentBinding.bind(inflater.inflate(R.layout.notes_fragment, container, false));
        binding.setClickToAddNote(this);

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        viewModel.getAllNotes();

        adapter = new NoteAdapter(thisContext);
        binding.recyclerNotes.setHasFixedSize(true);
        binding.recyclerNotes.setAdapter(adapter);

        viewModel.notesList.observe(getViewLifecycleOwner(), notes -> {
            if(notes.size() > 0) {
                adapter.seNoteList(notes);
                binding.recyclerNotes.setVisibility(View.VISIBLE);
                binding.txtClick.setVisibility(View.GONE);
                binding.view.setVisibility(View.GONE);
            } else {
                binding.recyclerNotes.setVisibility(View.GONE);
                binding.txtClick.setVisibility(View.VISIBLE);
                binding.view.setVisibility(View.VISIBLE);
            }
        });


        return binding.getRoot();
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onClickToNote(NoteDeleteEvent event){
        if(event != null){
            deleteNote(event.getNote());
        }
    }

    private void deleteNote(Note note) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(thisContext);
        dialog.setTitle(R.string.delete_title);
        dialog.setMessage(getString(R.string.delete_message));

        dialog.setPositiveButton("OK", (dialogInterface, i) -> viewModel.deleteNote(note.getId()));

        dialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        dialog.show();

    }

    @Override
    public void gotoNoteActivity() {
        startActivity(new Intent(thisContext, NoteActivity.class));
    }

    @Override
    public void onStop() {
        super.onStop();
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
