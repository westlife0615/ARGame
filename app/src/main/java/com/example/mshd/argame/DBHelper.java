package com.example.mshd.argame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mshd on 2016-11-15.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "ARgame.db", null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE monster ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL , address TEXT, lat TEXT NOT NULL, lon TEXT NOT NULL);");


        db.execSQL("INSERT INTO monster VALUES(null, '사회교육관', '태전1동', '35.929253', '128.544209'); ");

        db.execSQL("INSERT INTO monster VALUES(null, '경북대학교 정보전산원', '복현1동', '35.891571', '128.613550'); ");
        db.execSQL("INSERT INTO monster VALUES(null, '경북대학교 어학교육원', '복현1동', '35.891172', '128.614471'); ");
        db.execSQL("INSERT INTO monster VALUES(null, '경북대학교 모바일테크노빌딩', '복현1동', '35.891172', '128.614471'); ");

        db.execSQL("INSERT INTO monster VALUES(null, '경북대학교 우체국', '산격3동', '35.889321', '128.614372'); ");

        db.execSQL("INSERT INTO monster VALUES(null, '경북대학교 사회과학대학', '신암1동', '35.888525', '128.615443'); ");

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}




