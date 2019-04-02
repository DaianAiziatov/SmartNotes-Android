package com.lambton.daianaiziatov.smartnotes.Database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "NotesDatabase";
    private static final int DB_Version = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String studentTable = "CREATE TABLE " + DatabaseNote.TABLE_NAME
                + "(" + DatabaseNote.KEY_NOTE_ID + " TEXT PRIMARY KEY,"
                + DatabaseNote.KEY_NOTE_DETAILS + " TEXT,"
                + DatabaseNote.KEY_NOTE_DATE + " INTEGER,"
                + DatabaseNote.KEY_NOTE_LATITUDE + " DOUBLE,"
                + DatabaseNote.KEY_NOTE_LONGITUDE + " DOUBLE,"
                + DatabaseNote.KEY_NOTE_RECORDINGS + " TEXT)";

        db.execSQL(studentTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE " + DatabaseNote.TABLE_NAME);
        onCreate(db);
    }
}
