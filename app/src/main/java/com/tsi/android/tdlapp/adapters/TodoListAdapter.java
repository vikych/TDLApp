package com.tsi.android.tdlapp.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.tsi.android.tdlapp.R;
import com.tsi.android.tdlapp.TodoItem;
import com.tsi.android.tdlapp.TodoItemViewHolder;
import com.tsi.android.tdlapp.activities.MainActivity;
import com.tsi.android.tdlapp.database.TodoDbHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TodoListAdapter extends BaseAdapter implements ListAdapter {
    public static final int MILLISECONDS_TO_VIBRATE = 200;
    private ArrayList<TodoItem> todoItems = new ArrayList<>();
    private Context context;
    private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);

    public TodoListAdapter(ArrayList<TodoItem> todoItems, Context context) {
        this.todoItems = todoItems;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TodoItemViewHolder vh;
        if (convertView == null) {
            vh = new TodoItemViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_todolist_item, parent, false);

            vh.listItemText = (TextView) convertView.findViewById(R.id.list_element);
            vh.delete = (ImageButton) convertView.findViewById(R.id.delete_btn);
            vh.checkBox = (CheckBox) convertView.findViewById(R.id.element_check);
            vh.date = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(vh);
        } else {
            vh = (TodoItemViewHolder) convertView.getTag();
        }

        final TodoItem todoItem = todoItems.get(position);
        vh.listItemText.setText(todoItem.getTitle());

        vh.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                vib.vibrate(MILLISECONDS_TO_VIBRATE);

                new AlertDialog.Builder(context)
                        .setTitle(R.string.message)
                        .setMessage(R.string.message_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                todoItems.remove(position);
                                ((MainActivity) context).dbTodoItemDelete(todoItem);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

        String formattedDate = df.format(todoItem.getDate());

        vh.date.setText(formattedDate);

        vh.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                todoItem.setChecked(isChecked);
                if (isChecked) {
                    if (todoItem.getCheckedDate() == null) {
                        todoItem.setCheckedDate(new Date());
                    }
                    vh.listItemText.setPaintFlags(vh.date.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    vh.date.setPaintFlags(vh.date.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    todoItem.setCheckedDate(null);
                    vh.listItemText.setPaintFlags(0);
                    vh.date.setPaintFlags(0);
                }
                ((MainActivity) context).dbTodoItemUpdate(todoItem);
            }
        });
        vh.checkBox.setChecked(todoItem.isChecked());

        return convertView;
    }

    @Override
    public int getCount() {
        return todoItems.size();
    }

    @Override
    public TodoItem getItem(int position) {
        return todoItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(TodoItem item) {
        todoItems.add(item);
        notifyDataSetChanged();
    }
}