package com.coding.tester.noteapp.events;

import java.io.File;

public class DeleteRecordEvent {

    private File file;

    public DeleteRecordEvent(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
