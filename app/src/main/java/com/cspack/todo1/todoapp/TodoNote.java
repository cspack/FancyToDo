package com.cspack.todo1.todoapp;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by chris on 9/23/16.
 */

public class TodoNote implements Serializable {
    protected static final String TAG = "TodoNote";

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
    }

    private boolean completed;
    private String text;
    private Priority priority;

    public TodoNote(boolean completed, String text, Priority priority) {
        this.completed = completed;
        this.text = text;
        this.priority = priority;
    }

    public boolean getCompleted() {
        return this.completed;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public String getText() {
        return this.text;
    }

    public void setCompleted(boolean isCompleted) {
        this.completed = isCompleted;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
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
}
