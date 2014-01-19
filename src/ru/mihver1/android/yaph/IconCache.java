package ru.mihver1.android.yaph;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import ru.mihver1.android.yaph.db.ImageStorage;
import ru.mihver1.android.yaph.db.UrlRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class IconCache {
    private final ConcurrentMap<String, Lock> lockMap = new ConcurrentHashMap<String, Lock>();
    private final ConcurrentMap<String, Bitmap> cache = new ConcurrentHashMap<String, Bitmap>();
    private ImageStorage db = null;

    public IconCache(ImageStorage db) {
        this.db = db;
    }

    private Lock getLock(String url) {
        if (!lockMap.containsKey(url)) {
            lockMap.putIfAbsent(url, new ReentrantLock());
        }
        return lockMap.get(url);
    }

    public Bitmap getOrLoadBitmap(String url) throws IOException {
        Bitmap result = getBitmapFromCache(url);
        if (result == null) {
            result = loadBitmap(url);
            if (cache.putIfAbsent(url, result) != null) {
                result.recycle();
            }
        }
        return getBitmapFromCache(url);
    }

    private Bitmap loadBitmap(String url) throws IOException {
        Lock lock = getLock(url);
        lock.lock();
        Log.d("IconCache", "start " + url);
        try {
            Bitmap bt = BitmapFactory.decodeStream(new URL(url).openStream());
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/flickr_cache");
            myDir.mkdirs();
            String fname = url.substring(url.length() - 9);
            File file = new File(myDir, fname);
            if(file.exists ()) {
                file.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                bt.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            db.addItem(new UrlRecord(url, file.getAbsolutePath(), 0));

            return bt;
        } finally {
            lock.unlock();
            Log.d("IconCache", "end " + url);
        }
    }

    public Bitmap getBitmapFromCache(String url) {
        return cache.get(url);
    }


}