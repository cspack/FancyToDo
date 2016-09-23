package com.cspack.todo1.todoapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chris on 9/22/16.
 */

public class TodoNoteAdapter extends ArrayAdapter<TodoNote> {
    protected static final String TAG = "TodoNoteAdapter";
    protected final Context context;
    protected final ArrayList<TodoNote> itemList;

    public TodoNoteAdapter(Context context, ArrayList<TodoNote> items) {
        super(context, R.layout.list_item_tmpl, items);
        this.context = context;
        this.itemList = items;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View retView = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            retView = inflater.inflate(R.layout.list_item_tmpl, parent, false);
        }
        CheckBox checkbox = (CheckBox) retView.findViewById(R.id.checkbox);
        checkbox.setChecked(this.itemList.get(position).getCompleted());
        checkbox.setTag(position);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update data model.
                int position = (int) buttonView.getTag();
                Log.d(TAG, "Check state changed for pos " + position + " to " + isChecked);
                itemList.get(position).setCompleted(isChecked);
                TodoNoteAdapter.this.notifyDataSetChanged();
            }
        });
        TextView text = (TextView) retView.findViewById(R.id.text1);
        text.setText(this.itemList.get(position).getText());
        return retView;
    }
}
