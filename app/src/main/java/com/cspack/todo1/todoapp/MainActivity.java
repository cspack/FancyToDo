package com.cspack.todo1.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

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
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ArrayList<TodoNote> itemList;
    TodoNoteAdapter itemAdapter;
    ListView lvItems;
    File todoFile;
    // Child activity result types.
    private final int REQUEST_EDIT_ITEM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Data.
        todoFile = new File(getFilesDir(), "todo.txt");
        readItems();

        // Setup UI Elements.
        lvItems = (ListView) findViewById(R.id.lvItems);
        itemAdapter = new TodoNoteAdapter(this, itemList);
        lvItems.setAdapter(itemAdapter);
        setupListViewHandlers();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_EDIT_ITEM) {
            int pos = data.getIntExtra("pos", -1);
            if (pos < 0 || pos >= itemList.size()) {
                Log.e(TAG, "Invalid position sent for edit item: " + pos);
                return;
            }
            TodoNote note = (TodoNote) data.getSerializableExtra("note");
            itemList.set(pos, note);
            itemAdapter.notifyDataSetChanged();
            writeItems();
        }
    }

    private void setupListViewHandlers() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           long id) {
                itemList.remove(position);
                itemAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("note", itemList.get(position));
                intent.putExtra("pos", position);
                startActivityForResult(intent, REQUEST_EDIT_ITEM);
            }
        });
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
        // Do nothing.
        EditText newItem = (EditText) findViewById(R.id.etNewItem);
        itemList.add(new TodoNote(false, newItem.getText().toString(), TodoNote.Priority.MEDIUM));
        newItem.setText("");
        itemAdapter.notifyDataSetChanged();
        writeItems();
    }
}
