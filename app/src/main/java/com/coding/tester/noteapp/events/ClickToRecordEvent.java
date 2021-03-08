package com.coding.tester.noteapp.events;

import java.io.File;

public class ClickToRecordEvent {

    private File file;

    public ClickToRecordEvent(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

}
