package com.coding.tester.noteapp.events;

public class RecordFromMenu {

    private boolean isRecordFromMenu;

    public RecordFromMenu(boolean isRecordFromMenu) {
        this.isRecordFromMenu = isRecordFromMenu;
    }

    public boolean isRecordFromMenu() {
        return isRecordFromMenu;
    }

    public void setRecordFromMenu(boolean recordFromMenu) {
        isRecordFromMenu = recordFromMenu;
    }
}
