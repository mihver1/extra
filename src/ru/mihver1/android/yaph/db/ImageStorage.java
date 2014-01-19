package ru.mihver1.android.yaph.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import ru.mihver1.android.yaph.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mihver1 on 19.01.14.
 */
public class ImageStorage {

    private static final String DB_NAME = "URLS.db";
    private static final int DB_VERSION = 3;

    private static final String TABLE_NAME = "cache_urls";
    private static final String FIELDS = "URLS TEXT, IMAGE TEXT, SIZE INTEGER";
    private static final String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" + FIELDS + ")";

    private SQLiteDatabase db;

    public ImageStorage(Context context) {
        SQLiteOpenHelper dbhelper = new SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(SQL_CREATE);
                Log.d("YOLO", SQL_CREATE);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                Log.d("YOLO", "Upgrading db");
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                onCreate(db);
            }
        };
        db = dbhelper.getWritableDatabase();
    }

    public ArrayList<UrlRecord> getCache() {
        ArrayList<UrlRecord> answer = new ArrayList<UrlRecord>();
        Cursor cur = db.rawQuery("SELECT * FROM "+ TABLE_NAME , null);
        cur.moveToFirst();
        if(cur.getCount() == 0) {
            return null;
        }
        while(!cur.isAfterLast()) {
            UrlRecord rec = new UrlRecord(cur.getString(0), cur.getString(1), cur.getInt(2));
            answer.add(rec);
            cur.moveToNext();
        }

        return answer;
    }

    public boolean hasImage(String url) {
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE URLS = '" + url + "'", null);
        return cur.getCount() != 0;
    }

//    public UrlRecord getItem(String url) {
//        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE URLS = '" + url + "'", null);
//        cur.moveToFirst();
//        if(hasImage(url)) {
//            return new UrlRecord(cur.getString(0), cur.getString(1), cur.getInt(3));
//        } else {
//            return null;
//        }
//    }

    public void addItem(UrlRecord rec) {
        if(!hasImage(rec.getUrl())) {
            ContentValues values = new ContentValues();
            values.put("URLS", rec.getUrl());
            values.put("IMAGE", rec.path2Image);
            values.put("SIZE", rec.width);
            db.insert(TABLE_NAME, null, values);
        }
    }

    public void dropCache() {
        ArrayList<UrlRecord> rec = getCache();
        if(rec != null) {
            for(UrlRecord r: rec) {
                File del = new File(r.path2Image);
                del.delete();
            }
        }
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(SQL_CREATE);
    }

}
