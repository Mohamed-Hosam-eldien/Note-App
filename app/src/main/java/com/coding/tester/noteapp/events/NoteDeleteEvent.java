package com.coding.tester.noteapp.events;

import com.coding.tester.noteapp.database.Note;

public class NoteDeleteEvent {

    private Note note;

    public NoteDeleteEvent(Note note) {
        this.note = note;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }
}
