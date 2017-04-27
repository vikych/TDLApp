package com.tsi.android.tdlapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.tsi.android.tdlapp.R;
import com.tsi.android.tdlapp.TodoItem;
import com.tsi.android.tdlapp.adapters.TodoListAdapter;
import com.tsi.android.tdlapp.database.TodoDbHelper;
import com.tsi.android.tdlapp.preference.PreferencesManager;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int MILLISECONDS_TO_VIBRATE = 150;
    final TodoDbHelper db = new TodoDbHelper(this);
    private EditText inputTitle;
    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayList<TodoItem> todoItems = db.getAllTodoItems();
        final TodoListAdapter adapter = new TodoListAdapter(todoItems, this);

        final ListView todoList = (ListView) findViewById(R.id.todo_list);
        todoList.setAdapter(adapter);
        todoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TodoItem item = adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(DetailsActivity.TODO_ITEM, item);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        inputTitle = (EditText) findViewById(R.id.input_text);

        buttonAdd = (Button) findViewById(R.id.add_btn);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputTitle.getText().length() != 0) {
                    Toast.makeText(MainActivity.this, R.string.reminder, Toast.LENGTH_SHORT).show();
                    Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vib.vibrate(MILLISECONDS_TO_VIBRATE);

                    TodoItem newItem = new TodoItem(inputTitle.getText().toString(), new Date(), false);
                    adapter.addItem(newItem);
                    db.addTodoItem(newItem);
                    inputTitle.setText("");
                    inputTitle.clearFocus();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        int color = PreferencesManager.getInstance(this).getColorPreference("color");
        inputTitle.setTextColor(color);
        buttonAdd.setTextColor(color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_color) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void dbTodoItemUpdate(TodoItem todoItem) {
        db.updateTodoItem(todoItem);
    }

    public void dbTodoItemDelete(TodoItem todoItem) {
        db.deleteTodoItem(todoItem);
    }
}
