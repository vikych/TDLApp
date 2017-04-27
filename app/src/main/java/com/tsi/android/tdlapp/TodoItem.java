package com.tsi.android.tdlapp;


import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import java.util.Date;

public class TodoItem implements Parcelable, BaseColumns {

    private int id;
    private String title;
    private String description;
    private Date checkedDate;
    private Date date;
    private boolean checked;
    private byte[] image;

    public TodoItem() {
    }

    public TodoItem(int id, String title, Date date, boolean checkbox, Date checkedDate, String description,  byte[] image) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.checkedDate = checkedDate;
        this.date = date;
        this.checked = checkbox;
        this.image = image;
    }

    public TodoItem(String title, Date date, boolean checkbox) {
        this.title = title;
        this.date = date;
        this.checked = checkbox;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public Date getCheckedDate() {
        return checkedDate;
    }

    public void setCheckedDate(Date checkedDate) {
        this.checkedDate = checkedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeSerializable(date);
        dest.writeByte((byte) (checked ? 1 : 0));
        dest.writeSerializable(checkedDate);
        dest.writeString(description);
        dest.writeSerializable(image);
    }

    protected TodoItem(Parcel in) {
        id = in.readInt();
        title = in.readString();
        date = (Date) in.readSerializable();
        checked = in.readByte() != 0;
        checkedDate = (Date) in.readSerializable();
        description = in.readString();
        image = (byte []) in.readSerializable();
    }

    public static final Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
        @Override
        public TodoItem createFromParcel(Parcel in) {
            return new TodoItem(in);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };

}
