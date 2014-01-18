package ru.mihver1.android.yaph.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import ru.mihver1.android.yaph.R;

/**
 * Created by mihver1 on 19.01.14.
 */
public class ImageStorage extends SQLiteOpenHelper {

    private static final String DB_NAME = "URLS";
    private static final int DB_VERSION = 1;
    SQLiteDatabase data;
    Context context;

    public ImageStorage(Context context) {
        super(context, DB_NAME, null,DB_VERSION);
        this.context = context;
        data = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(context.getResources().getString(R.string.SQL_CREATE));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean hasImage(String url) {
        Cursor cur = data.rawQuery("SELECT * FROM URLS WHERE URL = "+ url, null);
        int cnt = cur.getCount();
        return cnt != 0;
    }
}
