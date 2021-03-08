package com.coding.tester.noteapp.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface NoteDao {

    @Insert
    void insertNote(Note note);

    @Query("UPDATE note_table SET title =:title , description=:description,time =:time WHERE id =:id")
    void updateNoteItem(String time, String title, String description, int id);

    @Update
    void updateNoteItem(Note note);

    @Query("select * from note_table")
    LiveData<List<Note>> getAllNotes();

    @Query("delete from note_table")
    void deleteAllNotes();

    @Query("delete from note_table where id=:noteId")
    void deleteNoteItem(int noteId);

}
