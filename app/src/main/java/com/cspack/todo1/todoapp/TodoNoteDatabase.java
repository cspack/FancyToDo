package com.cspack.todo1.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.util.Log;

import java.util.List;

/**
 * SQLite Database handler for TodoNote database. Not sure why I'm not using an ORM.
 */

public class TodoNoteDatabase extends SQLiteOpenHelper {
    private static final String TAG = "TodoNoteDatabase";
    private static final String DATABASE_NAME = "todoNoteDb";
    private static final int DATABASE_VERSION = 1;
    // Singleton instance.
    private static TodoNoteDatabase sInstance;
    public final ObservableList<TodoNote> noteList = new ObservableArrayList<>();

    private TodoNoteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized TodoNoteDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodoNoteDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE TodoNotes (" +
                        "NoteId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "Text TEXT, " +
                        "Priority INTEGER, " +
                        "Completed INTEGER" +
                        ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing yet, when a new version comes along, columns of table will need to change.
    }

    public void insertNote(TodoNote note) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            if (note.getId() > 0) {
                values.put("NoteId", note.getId());
            }
            values.put("Text", note.getText());
            values.put("Priority", note.getPriority().ordinal());
            values.put("Completed", note.getCompleted() ? 1 : 0);
            long rowKey = db.insertOrThrow("TodoNotes", null, values);
            db.setTransactionSuccessful();
            noteList.add(new TodoNote(rowKey, note.getCompleted(), note.getText(), note
                    .getPriority()));
        } catch (SQLException e) {
            Log.e(TAG, "Error adding note to db.");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void resetNotesFromList(List<TodoNote> notes) {
        eraseNotes();
        for (TodoNote note : notes) {
            insertNote(note);
        }
    }

    public void eraseNotes() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete("TodoNotes", null, null);
            db.setTransactionSuccessful();
            noteList.clear();
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting all notes.");
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteNote(TodoNote note) {
        if (note.getId() < 0) {
            Log.e(TAG, "Invalid note: " + note.toString());
            return;
        }

        String NOTE_PREDICATE = String.format("NoteId = %1d", note.getId());
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int rows = db.delete("TodoNotes", NOTE_PREDICATE, null);
            if (rows != 1) {
                Log.e(TAG, String.format("Row not deleted: %1d, Note: %2s", rows, note));
            } else {
                Log.d(TAG, String.format("Row deleted success: %1d, Note: %2s", rows, note));
            }
            db.setTransactionSuccessful();
            int pos = findNotePosition(note);
            if (pos >= 0) {
                // Update only one note.
                noteList.remove(pos);
            } else {
                // Can't find note. Restore consistency.
                Log.i(TAG, "deleteNote needed full db reload");
                refreshNotes();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting note id " + note.getId());
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void updateNote(TodoNote note) {
        if (note.getId() < 0) {
            Log.e(TAG, "Invalid note: " + note.toString());
            return;
        }

        String NOTE_PREDICATE = String.format("NoteId = %1d", note.getId());
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("Text", note.getText());
            values.put("Priority", note.getPriority().ordinal());
            values.put("Completed", note.getCompleted() ? 1 : 0);
            db.update("TodoNotes", values, NOTE_PREDICATE, null);
            db.setTransactionSuccessful();
            int pos = findNotePosition(note);
            if (pos >= 0) {
                // Update only one note.
                noteList.set(pos, note);
            } else {
                // Can't find note. Restore consistency.
                Log.i(TAG, "updateNote needed full db reload");
                refreshNotes();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error updating note id " + note.getId());
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void refreshNotes() {
        noteList.clear();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM TodoNotes ORDER BY NoteId", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("NoteId"));
            String text = cursor.getString(cursor.getColumnIndex("Text"));
            int priorityColumn = cursor.getColumnIndex("Priority");
            TodoNote.Priority priority = TodoNote.Priority.values()[cursor.getInt(priorityColumn)];
            boolean completed = cursor.getInt(cursor.getColumnIndex("Completed")) == 1;
            TodoNote note = new TodoNote(id, completed, text, priority);
            noteList.add(note);
            Log.d(TAG, "Listing note: " + note.toString());
        }
    }

    private int findNotePosition(TodoNote note) {
        long id = note.getId();
        for (int i = 0; i < noteList.size(); i++) {
            if (noteList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }
}

