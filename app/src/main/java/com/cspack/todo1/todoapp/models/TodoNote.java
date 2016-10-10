package com.cspack.todo1.todoapp.models;

import android.util.Base64;
import android.util.Log;

import com.cspack.todo1.todoapp.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * TodoNote data handler. Provides helpers for serializing and associating with Android resources.
 */

public class TodoNote implements Serializable {
    protected static final String TAG = "TodoNote";
    private long id;
    private boolean completed;
    private String text;
    private Priority priority;
    public TodoNote(long id, boolean completed, String text, Priority priority) {
        this.id = id;
        this.completed = completed;
        this.text = text;
        this.priority = priority;
    }

    // Static helper to construct a TodoNote from a base64 string.
    public static TodoNote FromBase64String(String serializedNote) {
        try {
            byte b[] = Base64.decode(serializedNote, Base64.NO_WRAP);
            ByteArrayInputStream bi = new ByteArrayInputStream(b);
            ObjectInputStream si = new ObjectInputStream(bi);
            TodoNote note = (TodoNote) si.readObject();
            return note;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Base64 decode error from " + serializedNote);
            return null;
        }
    }

    public long getId() {
        return id;
    }
    public boolean getCompleted() {
        return this.completed;
    }

    public void setCompleted(boolean isCompleted) {
        this.completed = isCompleted;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public int getPriorityTextRes() {
        switch (this.priority) {
            case HIGH:
                return R.string.priority_high;
            case MEDIUM:
                return R.string.priority_medium;
            case LOW:
                return R.string.priority_low;
        }
        Log.e(TAG, "Unknown priority Id requested");
        return 0;
    }

    public int getPriorityColorRes() {
        switch (this.priority) {
            case HIGH:
                return R.color.color_high_priority;
            case MEDIUM:
                return R.color.color_med_priority;
            case LOW:
                return R.color.color_low_priority;
        }
        Log.e(TAG, "Unknown priority Id requested");
        return 0;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String toBase64String() {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(this);
            so.flush();
            String b64Str = Base64.encodeToString(bo.toByteArray(), Base64.NO_WRAP);
            return b64Str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String toString() {
        return String.format("{id: %1d, text: '%2s', priority: '%3s', completed: '%4s'}", id, text,
                priority.toString(), completed);
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
    }
}
