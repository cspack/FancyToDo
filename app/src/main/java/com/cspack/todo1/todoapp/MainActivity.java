package com.cspack.todo1.todoapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/*
   Simple implementation of ToDo app main activity.
   Adds a very weak checkbox implementation using files.
   This is not an efficient implementation and should be improved.
 */
public class MainActivity extends AppCompatActivity implements EditDialogFragment.EditDialogListener {
    private static final String TAG = "MainActivity";
    private TodoNoteDatabase mNoteDb;
    TodoNoteAdapter itemAdapter;
    RecyclerView rvItems;
    LinearLayoutManager itemsLayoutManager;
    EditText newItem;

    public class TodoNoteAdapterCallback extends ItemTouchHelper.Callback {
        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder) {
            int dragFlags = 0;
            // TODO(chris): Implement re-ordering.
            // int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            Log.e(TAG, "Movement not supported!");
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            Log.d(TAG, "Swiped away!");
            TodoNoteAdapter.ViewHolder noteHolder = (TodoNoteAdapter.ViewHolder) viewHolder;
            mNoteDb.deleteNote(noteHolder.getNote());
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Data.
        mNoteDb = TodoNoteDatabase.getInstance(getApplicationContext());
        // Initially populate notes.
        mNoteDb.refreshNotes();

        itemAdapter = new TodoNoteAdapter(MainActivity.this, mNoteDb.noteList);
        itemsLayoutManager = new LinearLayoutManager(this);

        // Setup UI Elements.
        newItem = (EditText) findViewById(R.id.etNewItem);

        rvItems = (RecyclerView) findViewById(R.id.rvItems);
        rvItems.setHasFixedSize(true);
        itemsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvItems.setAdapter(itemAdapter);
        rvItems.setLayoutManager(itemsLayoutManager);

        // Add swipe handler for deletion.
        ItemTouchHelper touchHelper = new ItemTouchHelper(new TodoNoteAdapterCallback());
        touchHelper.attachToRecyclerView(rvItems);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.btnAddItem);
        addButton.setImageResource(R.drawable.ic_add_white_24dp);
    }

    public void handleAddItem(View view) {
        String itemText = newItem.getText().toString();

        if (itemText.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.note_text_missing, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        mNoteDb.insertNote(new TodoNote(/*unused id=*/-1, /*completed=*/false,
                itemText, TodoNote.Priority.MEDIUM));
        newItem.setText("");

        // Scroll to the bottom.
        rvItems.smoothScrollToPosition(itemAdapter.getItemCount());
    }

    @Override
    public void onFinishEditNote(int position, TodoNote note) {
        if (position < 0 || position >= mNoteDb.noteList.size()) {
            Log.e(TAG, "Invalid position sent for edit item: " + position);
            return;
        }
        Log.d(TAG, "Updating note position " + position);
        mNoteDb.updateNote(note);
    }
}
