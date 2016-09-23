package com.cspack.todo1.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditActivity extends AppCompatActivity {
    TodoNote note;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        note = (TodoNote) getIntent().getSerializableExtra("note");
        editText = (EditText) findViewById(R.id.editText);
        editText.setText(note.getText());
        editText.requestFocus();
    }

    public void handleSave(View view) {
        Intent result = new Intent();
        note.setText(editText.getText().toString());
        final int pos = getIntent().getIntExtra("pos", -1);
        result.putExtra("pos", pos);
        result.putExtra("note", note);

        // Exit and return result.
        setResult(RESULT_OK, result);
        finish();
    }
}
