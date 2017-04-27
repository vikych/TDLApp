package com.tsi.android.tdlapp.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.tsi.android.tdlapp.TodoItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.text.TextUtils.*;

public class TodoDbHelper extends SQLiteOpenHelper {

    private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);

    public static final int DATABASE_VERSION = 11;
    public static final String DATABASE_NAME = "toDoItems";

    private static final String TABLE_TODO_ITEMS = "items";
    private static final String ITEM_ID = "id";
    private static final String ITEM_DATE = "date";
    private static final String ITEM_CHECKED_DATE = "checkedDate";
    private static final String ITEM_DESCRIPTION = "description";
    private static final String ITEM_TITLE = "title";
    private static final String ITEM_CHECKED = "checked";
    private static final String ITEM_IMAGE = "image";

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_TODO_ITEMS + "("
                + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + ITEM_TITLE + " TEXT,"
                + ITEM_DATE + " TEXT," + ITEM_CHECKED + " TEXT,"
                + ITEM_CHECKED_DATE + " TEXT," + ITEM_DESCRIPTION + " TEXT," + ITEM_IMAGE + " BLOB" + ")";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO_ITEMS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addTodoItem(TodoItem todoItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = setValues(todoItem);

        db.insert(TABLE_TODO_ITEMS, null, values);
    }

    public TodoItem getTodoItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TODO_ITEMS, new String[]{ITEM_ID,
                        ITEM_TITLE, ITEM_DATE, ITEM_CHECKED, ITEM_CHECKED_DATE, ITEM_DESCRIPTION, ITEM_IMAGE}, ITEM_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Date date = formatDate(cursor, 2);
        Date checkedDate = null;
        if (!isEmpty(cursor.getString(4))) {
            checkedDate = formatDate(cursor, 4);
        }

        byte[] image = cursor.getBlob(6);

        return new TodoItem(Integer.parseInt(cursor.getString(0)), cursor.getString(1), date, Boolean.valueOf(cursor.getString(3)), checkedDate, cursor.getString(5), image);
    }

    public ArrayList<TodoItem> getAllTodoItems() {
        ArrayList<TodoItem> todoItems = new ArrayList<TodoItem>();
        String selectQuery = "SELECT  * FROM " + TABLE_TODO_ITEMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Date date = formatDate(cursor, 2);
                Date checkedDate = null;

                if (!isEmpty(cursor.getString(4))) {
                    checkedDate = formatDate(cursor, 4);
                }

                TodoItem todoItem = new TodoItem();
                todoItem.setId(cursor.getInt(0));
                todoItem.setTitle(cursor.getString(1));
                todoItem.setDate(date);
                todoItem.setChecked(Boolean.valueOf(cursor.getString(3)));
                todoItem.setCheckedDate(checkedDate);
                todoItem.setDescription(cursor.getString(5));
                todoItem.setImage(cursor.getBlob(6));
                todoItems.add(todoItem);
            } while (cursor.moveToNext());
        }
        return todoItems;
    }

    public int getTodoItemsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TODO_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    public int updateTodoItem(TodoItem todoItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = setValues(todoItem);

        return db.update(TABLE_TODO_ITEMS, values, ITEM_ID + "=?",
                new String[]{String.valueOf(todoItem.getId())});
    }

    public void deleteTodoItem(TodoItem todoItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO_ITEMS, ITEM_ID + "=?", new String[]{String.valueOf(todoItem.getId())});
        db.close();
    }

    private ContentValues setValues(TodoItem todoItem) {
        ContentValues values = new ContentValues();
        values.put(ITEM_TITLE, todoItem.getTitle());
        values.put(ITEM_DATE, format.format(todoItem.getDate()));
        values.put(ITEM_CHECKED, Boolean.toString(todoItem.isChecked()));
        values.put(ITEM_CHECKED_DATE, todoItem.getCheckedDate() != null ? format.format(todoItem.getCheckedDate()) : "");
        values.put(ITEM_DESCRIPTION, todoItem.getDescription());
        values.put(ITEM_IMAGE, todoItem.getImage());
        return values;
    }

    private Date formatDate(Cursor cursor, int i) {
        Date date = new Date();
        try {
            date = format.parse(cursor.getString(i));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
