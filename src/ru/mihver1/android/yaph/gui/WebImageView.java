package ru.mihver1.android.yaph.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;
import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: mihver1
 * Date: 02.10.13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class WebImageView extends ImageView {

    private Drawable placeholder, image;
    private Bitmap original, cropped;

    public WebImageView(Context context) {
        super(context);
    }
    public WebImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public WebImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPlaceholderImage(Drawable drawable) {
        placeholder = drawable;
        if (image == null) {
            setImageDrawable(placeholder);
        }
    }
    public void setPlaceholderImage(int resid) {
        placeholder = getResources().getDrawable(resid);
        if (image == null) {
            setImageDrawable(placeholder);
        }
    }

    public void setImageUrl(String url) {
        DownloadTask task = new DownloadTask();
        task.execute(url);
    }

    public Bitmap original() {
        return original;
    }

    private class DownloadTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
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
            original = result;
            cropped = ThumbnailUtils.extractThumbnail(result, 100, 100);
            image = new BitmapDrawable(getResources(), cropped);
            if (image != null) {
                setImageDrawable(image);
            }
        }
    }
}
