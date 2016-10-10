package com.cspack.todo1.todoapp;

import android.databinding.ObservableList;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cspack.todo1.todoapp.activities.MainActivity;
import com.cspack.todo1.todoapp.db.TodoNoteDatabase;
import com.cspack.todo1.todoapp.fragments.EditDialogFragment;
import com.cspack.todo1.todoapp.models.TodoNote;

/**
 * RecyclerView adapter which will handle actions on the Note list in MainActivity.
 */

public class TodoNoteAdapter extends RecyclerView.Adapter<TodoNoteAdapter.ViewHolder> {
    public static final String TAG = "TodoNoteAdapter";
    // Callback to register this RecyclerView Adapter with the database Observable list.
    final ObservableList.OnListChangedCallback changeCallback = new ObservableList
            .OnListChangedCallback<ObservableList<TodoNote>>() {
        @Override
        public void onChanged(ObservableList<TodoNote> todoNotes) {
            TodoNoteAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(ObservableList<TodoNote> todoNotes, int start, int
                count) {
            TodoNoteAdapter.this.notifyItemRangeChanged(start, count);
        }

        @Override
        public void onItemRangeInserted(ObservableList<TodoNote> todoNotes, int start, int
                count) {
            TodoNoteAdapter.this.notifyItemRangeInserted(start, count);
        }

        @Override
        public void onItemRangeMoved(ObservableList<TodoNote> todoNotes, int from, int to, int
                count) {
            TodoNoteAdapter.this.notifyItemMoved(from, to);
        }

        @Override
        public void onItemRangeRemoved(ObservableList<TodoNote> todoNotes, int start, int
                count) {
            TodoNoteAdapter.this.notifyItemRangeRemoved(start, count);
        }
    };
    protected ObservableList<TodoNote> mItems;
    private MainActivity mActivity;

    public TodoNoteAdapter(MainActivity activity, ObservableList<TodoNote> items) {
        this.mActivity = activity;
        this.mItems = items;
    }

    @Override
    public TodoNoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_tmpl, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mItems.addOnListChangedCallback(changeCallback);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mItems.removeOnListChangedCallback(changeCallback);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TodoNote note = mItems.get(position);
        holder.bindNote(note);
        // Erase old event handlers, to prevent triggering while processing.
        holder.mCheckBox.setOnCheckedChangeListener(null);
        holder.mMainView.setOnClickListener(null);
        holder.mMainView.setOnLongClickListener(null);

        // Fill values.
        holder.mCheckBox.setChecked(note.getCompleted());
        holder.tvText.setText(note.getText());
        holder.tvPriority.setText(note.getPriorityTextRes());
        holder.tvPriority.setTextColor(
                ContextCompat.getColor(mActivity, note.getPriorityColorRes()));

        // Register event handlers.
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                note.setCompleted(isChecked);
                TodoNoteDatabase.getInstance(mActivity).updateNote(note);
            }
        });
        holder.mMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = mActivity.getSupportFragmentManager();
                EditDialogFragment editDialogFragment = EditDialogFragment.newInstance
                        (note, mItems.indexOf(note));
                editDialogFragment.show(fm, "item_editor");
            }
        });
        holder.mMainView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TodoNoteDatabase.getInstance(mActivity).deleteNote(note);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View mMainView;
        private final CheckBox mCheckBox;
        private final TextView tvText;
        private final TextView tvPriority;
        private TodoNote mNote = null;

        public ViewHolder(View v) {
            super(v);
            mMainView = v;
            mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
            tvText = (TextView) v.findViewById(R.id.text1);
            tvPriority = (TextView) v.findViewById(R.id.tvPriority);
        }

        public void bindNote(TodoNote note) {
            mNote = note;
        }

        @Nullable
        public TodoNote getNote() {
            return mNote;
        }
    }
}
