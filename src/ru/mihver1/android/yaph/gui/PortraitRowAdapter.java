package ru.mihver1.android.yaph.gui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.util.Log;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import ru.mihver1.android.yaph.IconCache;
import ru.mihver1.android.yaph.ImagePage;
import ru.mihver1.android.yaph.R;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mihver1 on 18.01.14.
 */

public class PortraitRowAdapter extends BaseAdapter {
    LayoutInflater inflater;
    ArrayList<String> urls, fullscreen;
    ArrayList<DownloadTask> tasks = new ArrayList<DownloadTask>();
    IconCache cache;
    private int width;

    public PortraitRowAdapter(Context context, ArrayList<String> urls, ArrayList<String> fullscreen, IconCache cache) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        this.inflater = LayoutInflater.from(context);
        this.urls = urls;
        this.fullscreen = fullscreen;
        this.cache = cache;

        new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                 for(DownloadTask t: tasks) {
                     t.cancel(true);
                 }
            }
        };
    }

    @Override
    public int getCount() {
        return urls.size() / 2;
    }

    @Override
    public String getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.rowlayout, parent, false);
            holder = new ViewHolder(
                    (ImageView) view.findViewById(R.id.leftImage),
                    (ImageView) view.findViewById(R.id.rightImage)
            );
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        loadBitmap(holder.left, urls.get(position * 2));
        loadBitmap(holder.right, urls.get(position * 2 + 1));

        return view;
    }

    private void loadBitmap(ImageView view, String url) {
        view.setTag(R.integer.prev, url);
        view.setTag(R.integer.full, url.substring(0, url.length() - 4) + "_b.jpg");
        Bitmap bm = ThumbnailUtils.extractThumbnail(cache.getBitmapFromCache(url), (int)(width * 0.35), (int)(width * 0.35));
        if (bm != null) {
            view.setImageBitmap(bm);
        } else {
            view.setImageResource(R.drawable.ic_launcher);
            DownloadTask t = (DownloadTask) new DownloadTask(view, url).execute();
            tasks.add(t);
        }
    }

    private static class ViewHolder {
        ImageView left;
        ImageView right;

        ViewHolder(ImageView l, ImageView r) {
            left = l;
            right = r;
        }
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
                return cache.getOrLoadBitmap(url);
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (view.getTag(R.integer.prev) == url) {
                view.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, (int)(width * 0.35), (int)(width * 0.35)));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(v.getContext(), ImagePage.class).putExtra("image", (String) v.getTag(R.integer.full));
                        v.getContext().startActivity(myIntent);
                    }
                });
            }
        }
    }
}
