package com.coding.tester.noteapp.events;

import com.coding.tester.noteapp.database.Note;

public class NoteClickEvent {

    private Note note;

    public NoteClickEvent(Note note) {
        this.note = note;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }
}
