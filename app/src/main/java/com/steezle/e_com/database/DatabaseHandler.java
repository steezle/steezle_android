package com.steezle.e_com.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.steezle.e_com.model.SearchItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hiren Patel on 2/9/2018.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ecom";

    // Contacts table name
    private static final String TABLE_SEARCH = "search";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SEARCH_TEXT= "searchText";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SEARCH + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SEARCH_TEXT + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH);

        // Create tables again
        onCreate(db);
    }


    public long searchInsertUpdateData (SearchItem searchItem) {

        SQLiteDatabase db =  getWritableDatabase();
        ContentValues values = new ContentValues();


        // values.put(KEY_ID , searchItem.getId());
        values.put(KEY_SEARCH_TEXT, searchItem.getSearchText());

        return db.insert(TABLE_SEARCH, null, values);

    }

    /**
     * @return
     */
    public ArrayList<SearchItem> getHealthRecordByPatientId() {

        ArrayList<SearchItem> healthRecordItems = new ArrayList<SearchItem>();
        String selectQuery= "SELECT DISTINCT " + " * FROM " + TABLE_SEARCH  +"' DESC '";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        try {
            if (c.moveToFirst()) {
                do {

                    SearchItem healthRecordItem = new SearchItem();
                    healthRecordItem.setId(c.getString((c.getColumnIndex(KEY_ID))));
                    healthRecordItem.setSearchText(c.getString((c.getColumnIndex(KEY_SEARCH_TEXT))));

                    healthRecordItems.add(healthRecordItem);

                } while (c.moveToNext());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        c.close();
        return healthRecordItems;
    }

    public void removeAllData() {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_SEARCH, null, null);
    }

}