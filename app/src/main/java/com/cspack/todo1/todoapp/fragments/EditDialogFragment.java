package com.cspack.todo1.todoapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cspack.todo1.todoapp.R;
import com.cspack.todo1.todoapp.models.TodoNote;

/**
 * Editor Fragment for TodoNote.
 */

public class EditDialogFragment extends DialogFragment {
    TodoNote note;
    EditText editText;
    Button saveButton;

    public static EditDialogFragment newInstance(TodoNote note, int position) {
        EditDialogFragment frag = new EditDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("note", note);
        args.putInt("pos", position);
        frag.setArguments(args);
        return frag;
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit, container);
    }

    @Override

    public void onViewCreated(final View parentView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(parentView, savedInstanceState);
        // Get field from view
        note = (TodoNote) getArguments().getSerializable("note");
        editText = (EditText) parentView.findViewById(R.id.editText);
        editText.requestFocus();
        // using .append places text at the end (as per project requirements).
        editText.append(note.getText());


        RadioButton radPriorityHigh = (RadioButton) parentView.findViewById(R.id.radPriorityHigh);
        RadioButton radPriorityMed = (RadioButton) parentView.findViewById(R.id.radPriorityMedium);
        RadioButton radPriorityLow = (RadioButton) parentView.findViewById(R.id.radPriorityLow);

        saveButton = (Button) parentView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSave(parentView);
            }
        });

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
                ((RadioGroup) parentView.findViewById(R.id.radPriority)).clearCheck();
        }

        getDialog().setTitle(R.string.editor_title);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void handleSave(View view) {
        String itemText = editText.getText().toString();
        if (itemText.isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(),
                    R.string.note_text_missing, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Intent result = new Intent();
        note.setText(itemText);

        // TODO(chris): Make this radio buton logic less ugly.
        RadioButton radPriorityHigh = (RadioButton) view.findViewById(R.id.radPriorityHigh);
        RadioButton radPriorityMed = (RadioButton) view.findViewById(R.id.radPriorityMedium);
        RadioButton radPriorityLow = (RadioButton) view.findViewById(R.id.radPriorityLow);
        if (radPriorityHigh.isChecked()) {
            note.setPriority(TodoNote.Priority.HIGH);
        }
        if (radPriorityMed.isChecked()) {
            note.setPriority(TodoNote.Priority.MEDIUM);
        }
        if (radPriorityLow.isChecked()) {
            note.setPriority(TodoNote.Priority.LOW);
        }

        final int pos = getArguments().getInt("pos", -1);
        EditDialogListener listener = (EditDialogListener) getActivity();
        listener.onFinishEditNote(pos, note);
        dismiss();
    }

    public interface EditDialogListener {
        void onFinishEditNote(int position, TodoNote note);
    }
}

