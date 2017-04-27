package com.tsi.android.tdlapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tsi.android.tdlapp.R;
import com.tsi.android.tdlapp.TodoItem;
import com.tsi.android.tdlapp.database.TodoDbHelper;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.graphics.Typeface.*;
import static android.text.TextUtils.isEmpty;

public class DetailsActivity extends AppCompatActivity {

    public static final String TODO_ITEM = "todo";
    private static final int CAMERA_REQUEST = 1888;
    private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);
    final TodoDbHelper db = new TodoDbHelper(this);
    TodoItem todoItem = null;
    EditText description = null;


    public DetailsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        todoItem = getIntent().getExtras().getParcelable(TODO_ITEM);
        ((TextView) findViewById(R.id.description_title_text)).setText(todoItem.getTitle());

        if (todoItem.getCheckedDate() == null) {
            ((TextView) findViewById(R.id.description_checked_date)).setText(R.string.message_not_checked);
        } else {
            String formattedDate = df.format(todoItem.getCheckedDate());
            TextView checkedDate = (TextView) findViewById(R.id.description_checked_date);
            checkedDate.setText(getString(R.string.completed_on).concat(formattedDate));
            checkedDate.setTypeface(null, BOLD);
        }

        ImageButton share = (ImageButton) findViewById(R.id.share_btn);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharingToSocialMedia(todoItem.getTitle());
            }
        });

        CheckBox checkBox = (CheckBox) findViewById(R.id.element_description_check);
        checkBox.setChecked(todoItem.isChecked());

        countUncompletedItems();

        findViewById(R.id.camera_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.camera_image);

        if (todoItem.getImage() != null) {
            imageView.setImageBitmap(getImage(todoItem.getImage()));
        }

        description = (EditText) findViewById(R.id.description);
        final Button saveButton = (Button) findViewById(R.id.save_description_btn);
        final Button editButton = (Button) findViewById(R.id.edit_description_btn);

        if (isEmpty(todoItem.getDescription())) {
            description.setFocusableInTouchMode(true);
            saveButton.setEnabled(true);
            editButton.setEnabled(false);
        } else {
            description.setFocusable(false);
            saveButton.setEnabled(false);
            editButton.setEnabled(true);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (description.getText().length() != 0) {
                    todoItem.setDescription(description.getText().toString());
                    db.updateTodoItem(todoItem);
                    description.setFocusable(false);
                    saveButton.setEnabled(false);
                    editButton.setEnabled(true);
                }
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description.setFocusableInTouchMode(true);
                saveButton.setEnabled(true);
                editButton.setEnabled(false);
            }
        });

        description.setText(todoItem.getDescription());
    }

    @Override
    protected void onResume() {
        super.onResume();
        countUncompletedItems();
        description.setText(todoItem.getDescription());
    }

    public void SharingToSocialMedia(String description) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, description);
        startActivity(Intent.createChooser(intent, getString(R.string.share_via)));
    }

    public void countUncompletedItems() {
        int count = db.getTodoItemsCount();
        int uncompleted = 0;

        TextView countItems = (TextView) findViewById(R.id.items_count);
        countItems.setText(getString(R.string.items_count) + count);
        countItems.setTypeface(null, Typeface.ITALIC);
        ArrayList<TodoItem> todoItems = db.getAllTodoItems();
        for (TodoItem todoItem : todoItems) {
            if (!todoItem.isChecked()) {
                uncompleted++;
            }
        }
        TextView countUncompletedItems = (TextView) findViewById(R.id.items_count_uncompleted);
        countUncompletedItems.setText(getString(R.string.items_count_uncompleted) + uncompleted);
        countUncompletedItems.setTypeface(null, ITALIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ImageView imageView = (ImageView) findViewById(R.id.camera_image);
            imageView.setImageBitmap(photo);
            TodoItem todoItem = getIntent().getExtras().getParcelable(TODO_ITEM);
            todoItem.setImage(getBytes(photo));
            db.updateTodoItem(todoItem);
        }
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
