package ru.mihver1.android.yaph.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/**
 * Created by mihver1 on 19.01.14.
 */
public class UrlRecord {
    public String path2Image;
    public String url;
    public boolean full;
    public int width;

    public UrlRecord(String url, String image, int width) {
        this.url = url;
        path2Image = image;
        this.width = width;
        full = false;
    }

    public void setFull(boolean flag) {
        full = flag;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getBitmap() {
        return BitmapFactory.decodeFile(path2Image);
    }



}
