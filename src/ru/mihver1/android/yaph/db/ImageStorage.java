package ru.mihver1.android.yaph.db;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mihver1 on 18.01.14.
 */
public class ImageStorage {

    Map<String, Bitmap> cache = new HashMap<String, Bitmap>();

    public  Bitmap getBitmap(String s) {
        Log.d("YOLO", "Get "+s);
        if(cache.containsKey(s)) {
            return cache.get(s);
        } else {
            cache.put(s, Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888));
            DownloadTask task = new DownloadTask();
            task.execute(s);
        }

        return cache.get(s);
    }

    private class DownloadTask extends AsyncTask<String, Void, Bitmap> {

        String urlT = "";

        @Override
        protected Bitmap doInBackground(String... params) {


            Log.d("YOLO", "ASYNC");
            String url = params[0];
            Log.d("YOLO", "**"+url+"**");
            urlT = url;
            try {
                URLConnection conn = (new URL(url)).openConnection();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                int current = 0;
                while ((current = bis.read()) != -1) {
                    baf.append((byte)current);
                }

                byte[] imageData = baf.toByteArray();
                return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            cache.put(urlT, result);
            Log.d("YOLO", urlT+" got");
        }
    }
}
