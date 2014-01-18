package ru.mihver1.android.yaph;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

/**
 * Created by mihver1 on 19.01.14.
 */
public class ImagePage extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);

        Bundle bundle = getIntent().getExtras();

        ImageView fullscreen = (ImageView) findViewById(R.id.imageView2);
        fullscreen.setTag(bundle.getString("image"));
        fullscreen.setImageResource(R.drawable.ic_launcher);

        new DownloadTask(fullscreen, bundle.getString("image")).execute();
    }

    private class DownloadTask extends AsyncTask<Void, Void, Bitmap> {
        final ImageView view;
        final String url;

        private DownloadTask(ImageView view, String url) {
            this.view = view;
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                return BitmapFactory.decodeStream(new URL(url).openStream());
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //if (view.getTag() == url) {
            view.setImageBitmap(bitmap);
            //}
        }
    }
}