package ru.mihver1.android.yaph.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import java.net.URL;
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
                return BitmapFactory.decodeStream(new URL(url).openStream());
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
