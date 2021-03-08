package com.coding.tester.noteapp.di;

import android.app.Application;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;
import com.coding.tester.noteapp.database.NoteDao;
import com.coding.tester.noteapp.database.NoteDatabase;

@Module
@InstallIn(ApplicationComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public static NoteDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(application, NoteDatabase.class, "note_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }


    @Provides
    @Singleton
    public static NoteDao provideNoteDao(NoteDatabase database) {
        return database.noteDao();
    }


}
