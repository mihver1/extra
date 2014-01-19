package ru.mihver1.android.yaph.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by mihver1 on 19.01.14.
 */
public class UrlRecord {
    public byte[] blobImage;
    public String url;
    public boolean full;
    public int width;

    public UrlRecord(String url, byte[] image, int width) {
        this.url = url;
        blobImage = image;
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
        return BitmapFactory.decodeByteArray(blobImage, 0, width);
    }



}
