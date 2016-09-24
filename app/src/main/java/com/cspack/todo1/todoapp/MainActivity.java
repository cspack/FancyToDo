package com.cspack.todo1.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/*
   Simple implementation of ToDo app main activity.
   Adds a very weak checkbox implementation using files.
   This is not an efficient implementation and should be improved.
 */
public class MainActivity extends AppCompatActivity implements EditDialogFragment.EditDialogListener {
    private static final String TAG = "MainActivity";
    ArrayList<TodoNote> itemList;
    TodoNoteAdapter itemAdapter;
    RecyclerView rvItems;
    LinearLayoutManager itemsLayoutManager;
    EditText newItem;
    File todoFile;
    // Child activity result types.
    public static final int REQUEST_EDIT_ITEM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Data.
        todoFile = new File(getFilesDir(), "todo.txt");
        readItems();

        itemAdapter = new TodoNoteAdapter(MainActivity.this, itemList);
        itemsLayoutManager = new LinearLayoutManager(this);

        // Setup UI Elements.
        newItem = (EditText) findViewById(R.id.etNewItem);

        rvItems = (RecyclerView) findViewById(R.id.rvItems);
        rvItems.setHasFixedSize(true);
        itemsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvItems.setAdapter(itemAdapter);
        rvItems.setLayoutManager(itemsLayoutManager);

        itemAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                writeItems();
            }
        });
        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.btnAddItem);
        addButton.setImageResource(R.drawable.ic_add_white_24dp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_EDIT_ITEM) {
            int pos = data.getIntExtra("pos", -1);
            if (pos < 0 || pos >= itemList.size()) {
                Log.e(TAG, "Invalid position sent for edit item: " + pos);
                return;
            }
            Log.d(TAG, "Updating note position " + pos);
            TodoNote note = (TodoNote) data.getSerializableExtra("note");
            itemList.set(pos, note);
            itemAdapter.notifyDataSetChanged();
        }
    }

    // Read items into itemList from todoFile.
    private void readItems() {
        try {
            itemList = new ArrayList<>();
            for (String line : FileUtils.readLines(todoFile)) {
                TodoNote note = TodoNote.FromBase64String(line);
                if (note == null) {
                    Log.e(TAG, "Invalid base64 encoded note: " + line);
                } else {
                    itemList.add(note);
                }
            }
        } catch (IOException e) {
            itemList = new ArrayList<>();
        }
    }

    // Update items to todoFile from the item list.
    private void writeItems() {
        try {
            PrintWriter writer = new PrintWriter(todoFile);
            for (TodoNote items : itemList) {
                writer.println(items.toBase64String());
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleAddItem(View view) {
        String itemText = newItem.getText().toString();

        if (itemText.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.note_text_missing, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        itemList.add(new TodoNote(/*completed=*/false, itemText, TodoNote.Priority.MEDIUM));
        itemAdapter.delayedNotifyDataSetChanged();
        newItem.setText("");

        rvItems.smoothScrollToPosition(itemAdapter.getItemCount());

    }

    @Override
    public void onFinishEditItem(TodoNote note, int position) {
        if (position < 0 || position >= itemList.size()) {
            Log.e(TAG, "Invalid position sent for edit item: " + position);
            return;
        }
        Log.d(TAG, "Updating note position " + position);
        itemList.set(position, note);
        itemAdapter.notifyDataSetChanged();
    }
}
