package com.coding.tester.noteapp.viewModel;

import android.content.Context;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.List;

import com.coding.tester.noteapp.database.Note;
import com.coding.tester.noteapp.repository.Repository;

public class NoteViewModel extends ViewModel {

    private final Repository repository;
    public LiveData<List<Note>> notesList = null;

    @ViewModelInject
    public NoteViewModel(Repository repository) {
        this.repository = repository;
    }


    public void getAllNotes() {
        notesList = repository.getAllNotes();
    }

    public void insertNotes(Note note) {
        repository.insertNotes(note);
    }

    public void deleteNote(int id){
        repository.deleteNote(id);
    }

    public void deleteAllNote(){
        repository.deleteAllNote();
    }

    public void updateNoteItem(Note note){
        repository.updateNoteItem(note);
    }

    public File[] getRecordFiles(Context context){
        return repository.getRecordFiles(context);
    }

}
