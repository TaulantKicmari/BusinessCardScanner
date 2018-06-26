package com.example.taulant.businesscards;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by taulant on 13/3/17.
 */

public class BusinessCardSQLHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "Card_Database";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_JOB = "Job_Title";
    public static final String COLUMN_COMPANY = "Company";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_PHONE = "Phone";
    public static final String COLUMN_IMAGE_RESOURCE = "image_resource";


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cards.db";

    public BusinessCardSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_JOB + " TEXT, " +
                    COLUMN_COMPANY + " TEXT, " +
                    COLUMN_EMAIL + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_IMAGE_RESOURCE + " INTEGER)");
        } catch (Exception e) {
            Log.d("Error", "Error encountered. Exception is: " + e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertCard(BusinessCard card) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, card.getName());
        contentValues.put(COLUMN_JOB, card.getJob());
        contentValues.put(COLUMN_COMPANY, card.getCompany());
        contentValues.put(COLUMN_EMAIL, card.getEmail());
        contentValues.put(COLUMN_PHONE, card.getPhone());
        contentValues.put(COLUMN_IMAGE_RESOURCE, card.getImage());
        db.insert(TABLE_NAME, null, contentValues);
        return true;


    }

    public Integer deleteCard(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "id = ? ", new String[]{id});
    }

    public boolean updateCard(String id, BusinessCard card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, card.getName());
        contentValues.put(COLUMN_JOB, card.getJob());
        contentValues.put(COLUMN_COMPANY, card.getCompany());
        contentValues.put(COLUMN_EMAIL, card.getEmail());
        contentValues.put(COLUMN_PHONE, card.getPhone());
//        contentValues.put(COLUMN_IMAGE_RESOURCE, card.getImage());
        db.update(TABLE_NAME, contentValues, "id = ? ", new String[]{id});
        return true;
    }

    public ArrayList<BusinessCard> getAllCards() {
        ArrayList<BusinessCard> cardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);

        res.moveToFirst();
        while (res.isAfterLast() == false) {
            BusinessCard card = new BusinessCard(
                    res.getString(res.getColumnIndex(COLUMN_NAME)),
                    res.getString(res.getColumnIndex(COLUMN_PHONE)),
                    res.getString(res.getColumnIndex(COLUMN_EMAIL)),
                    res.getString(res.getColumnIndex(COLUMN_JOB)),
                    res.getString(res.getColumnIndex(COLUMN_COMPANY)),
                    res.getBlob(res.getColumnIndex(COLUMN_IMAGE_RESOURCE))
                    ,res.getString(res.getColumnIndex(COLUMN_ID))
            );
            cardList.add(card);
            res.moveToNext();
        }

        return cardList;
    }
}
