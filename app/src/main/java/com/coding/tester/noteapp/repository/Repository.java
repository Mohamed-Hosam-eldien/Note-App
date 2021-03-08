package com.coding.tester.noteapp.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import com.coding.tester.noteapp.database.Note;
import com.coding.tester.noteapp.database.NoteDao;


public class Repository {

    private final NoteDao noteDao;

    @Inject
    public Repository(NoteDao noteDao) {
        this.noteDao = noteDao;
    }

    public LiveData<List<Note>> getAllNotes() {
        return noteDao.getAllNotes();
    }

    public void insertNotes(Note note) {
        noteDao.insertNote(note);
    }

    public void deleteNote(int id){
        noteDao.deleteNoteItem(id);
    }

    public void deleteAllNote(){
        noteDao.deleteAllNotes();
    }

    public void updateNote(Note note){
        noteDao.updateNoteItem(note.getTime(), note.getTitle(), note.getDescription(), note.getId());
    }

    public void updateNoteItem(Note note){
        noteDao.updateNoteItem(note);
    }

    public File[] getRecordFiles(Context context){
        String path = context.getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        return directory.listFiles();
    }


}
