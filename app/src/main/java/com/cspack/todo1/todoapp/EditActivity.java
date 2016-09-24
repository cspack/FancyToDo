package com.cspack.todo1.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {
    TodoNote note;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        note = (TodoNote) getIntent().getSerializableExtra("note");
        editText = (EditText) findViewById(R.id.editText);
        editText.requestFocus();
        // using .append places text at the end (as per project requirements).
        editText.append(note.getText());

        RadioButton radPriorityHigh = (RadioButton) findViewById(R.id.radPriorityHigh);
        RadioButton radPriorityMed = (RadioButton) findViewById(R.id.radPriorityMedium);
        RadioButton radPriorityLow = (RadioButton) findViewById(R.id.radPriorityLow);
        switch (note.getPriority()) {
            case HIGH:
                radPriorityHigh.setChecked(true);
                break;
            case MEDIUM:
                radPriorityMed.setChecked(true);
                break;
            case LOW:
                radPriorityLow.setChecked(true);
                break;
            default:
                ((RadioGroup) findViewById(R.id.radPriority)).clearCheck();
        }
    }

    public void handleSave(View view) {
        String itemText = editText.getText().toString();
        if (itemText.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.note_text_missing, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Intent result = new Intent();
        note.setText(itemText);

        // TODO(chris): Make this radio buton logic less ugly.
        RadioButton radPriorityHigh = (RadioButton) findViewById(R.id.radPriorityHigh);
        RadioButton radPriorityMed = (RadioButton) findViewById(R.id.radPriorityMedium);
        RadioButton radPriorityLow = (RadioButton) findViewById(R.id.radPriorityLow);
        if (radPriorityHigh.isChecked()) {
            note.setPriority(TodoNote.Priority.HIGH);
        }
        if (radPriorityMed.isChecked()) {
            note.setPriority(TodoNote.Priority.MEDIUM);
        }
        if (radPriorityLow.isChecked()) {
            note.setPriority(TodoNote.Priority.LOW);
        }

        final int pos = getIntent().getIntExtra("pos", -1);
        result.putExtra("pos", pos);
        result.putExtra("note", note);

        // Exit and return result.
        setResult(RESULT_OK, result);
        finish();
    }
}
