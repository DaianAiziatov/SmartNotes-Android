package com.lambton.daianaiziatov.smartnotes.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DatabaseNote {

    public static final String TABLE_NAME = "Notes_Table";
    public static final String KEY_NOTE_ID = "noteId";
    public static final String KEY_NOTE_DETAILS = "noteDetails";
    public static final String KEY_NOTE_DATE = "noteDate";
    public static final String KEY_NOTE_LONGITUDE = "noteLongitude";
    public static final String KEY_NOTE_LATITUDE = "noteLatitude";
    public static final String KEY_NOTE_RECORDINGS = "noteRecordings";

    private DatabaseHelper databaseHelper;

    public DatabaseNote(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void insert(Note note) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_NOTE_ID, note.getNoteId());
        contentValues.put(KEY_NOTE_DETAILS, note.getDetails());
        contentValues.put(KEY_NOTE_DATE, note.getDateAsLong());
        contentValues.put(KEY_NOTE_LONGITUDE, note.getLocationLongitude());
        contentValues.put(KEY_NOTE_LATITUDE, note.getLocationLatitude());
        contentValues.put(KEY_NOTE_RECORDINGS, note.getRecordingsAsString());
        database.insert(TABLE_NAME, null, contentValues);
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    public void update(Note note) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        database.beginTransaction();
        contentValues.put(KEY_NOTE_DETAILS, note.getDetails());
        contentValues.put(KEY_NOTE_DATE, note.getDateAsLong());
        contentValues.put(KEY_NOTE_LONGITUDE, note.getLocationLongitude());
        contentValues.put(KEY_NOTE_LATITUDE, note.getLocationLatitude());
        contentValues.put(KEY_NOTE_RECORDINGS, note.getRecordingsAsString());
        database.update(TABLE_NAME,contentValues,
                KEY_NOTE_ID + "=?",
                new String[]
                        {
                                String.valueOf(note.getNoteId())
                        });
        database.setTransactionSuccessful();
        database.endTransaction();
        database.close();
    }

    public void deleteByID(String noteId) {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.delete(TABLE_NAME, KEY_NOTE_ID + "=?",
                new String[]
                        {
                                String.valueOf(noteId)
                        });
    }

    public ArrayList<Note> getAllNotes(String orderBy, String order) {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_NAME +
                        " ORDER BY " + orderBy + (order.equals("DESC") ? " DESC" : " ASC"), null);
        ArrayList<Note> students = new ArrayList<>();
        if (cursor != null)
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Note student = new Note();
                student.setNoteId(cursor.getString(0));
                student.setDetails(cursor.getString(1));
                student.setDateFromLong(cursor.getInt(2));
                student.setLocationLatitude(cursor.getDouble(3));
                student.setLocationLongitude(cursor.getDouble(4));
                student.setRecordingsFromString(cursor.getString(5));

                students.add(student);
                cursor.moveToNext();
            }
        }
        database.close();
        return students;
    }

}
