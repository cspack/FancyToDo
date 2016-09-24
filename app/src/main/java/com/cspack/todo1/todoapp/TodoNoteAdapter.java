package com.cspack.todo1.todoapp;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * RecyclerView adapter which will handle actions on the Note list in MainActivity.
 */

public class TodoNoteAdapter extends RecyclerView.Adapter<TodoNoteAdapter.ViewHolder> {
    protected static final String TAG = "TodoNoteAdapter";
    private MainActivity mActivity;
    protected ArrayList<TodoNote> mItems;

    public TodoNoteAdapter(MainActivity activity, ArrayList<TodoNote> items) {
        this.mActivity = activity;
        this.mItems = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View mMainView;
        private final CheckBox mCheckBox;
        private final TextView tvText;
        private final TextView tvPriority;

        public ViewHolder(View v) {
            super(v);
            mMainView = v;
            mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
            tvText = (TextView) v.findViewById(R.id.text1);
            tvPriority = (TextView) v.findViewById(R.id.tvPriority);
        }
    }

    @Override
    public TodoNoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_tmpl, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Delay notifyDataSetChanged to the end for UI driven data set changes.
     */
    public void delayedNotifyDataSetChanged() {
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                TodoNoteAdapter.this.notifyDataSetChanged();
            }
        };
        handler.post(r);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TodoNote note = mItems.get(position);
        // Erase old check change listener, to prevent recycleview from notifying the wrong item.
        holder.mCheckBox.setOnCheckedChangeListener(null);
        holder.mCheckBox.setChecked(note.getCompleted());
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                note.setCompleted(isChecked);
                TodoNoteAdapter.this.delayedNotifyDataSetChanged();
            }
        });
        holder.tvText.setText(note.getText());
        holder.tvPriority.setText(note.getPriorityTextRes());
        holder.tvPriority.setTextColor(ContextCompat.getColor(mActivity.getApplicationContext(),
                note.getPriorityColorRes()));

        holder.mMainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                // Deprecated intent method:
                Intent intent = new Intent(v.getContext(), EditActivity.class);
                intent.putExtra("pos", mItems.indexOf(note));
                intent.putExtra("note", note);
                mActivity.startActivityForResult(intent, MainActivity.REQUEST_EDIT_ITEM);
                */
                FragmentManager fm = mActivity.getSupportFragmentManager();
                EditDialogFragment editDialogFragment = EditDialogFragment.newInstance
                        (note, mItems.indexOf(note));
                editDialogFragment.show(fm, "item_editor");

            }
        });
        holder.mMainView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TodoNoteAdapter.this.mItems.remove(note);
                TodoNoteAdapter.this.delayedNotifyDataSetChanged();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
