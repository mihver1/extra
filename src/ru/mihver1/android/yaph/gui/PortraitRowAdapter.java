package ru.mihver1.android.yaph.gui;

import android.app.Activity;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import ru.mihver1.android.yaph.R;
import ru.mihver1.android.yaph.db.ImageStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihver1 on 18.01.14.
 */

public class PortraitRowAdapter extends BaseAdapter {

    List<PortraitRowModel> content = new ArrayList<PortraitRowModel>();
    Activity context;
    ArrayList<String> urls;
    ImageStorage us;

    public PortraitRowAdapter(Activity ctx, ArrayList<String> urls, ImageStorage is) {
        context = ctx;
        this.urls = urls;
        us = is;
    }



    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView left;
        ImageView right;

        ViewHolder(ImageView l, ImageView r) {
            left = l;
            right = r;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.rowlayout, parent, false);
        ImageView leftView = (ImageView) view.findViewById(R.id.leftImage);
        ImageView rightView = (ImageView) view.findViewById(R.id.rightImage);
        ViewHolder viewHolder = new ViewHolder(leftView, rightView);
        view.setTag(viewHolder);
        holder = viewHolder;
        holder = (ViewHolder) view.getTag();
        leftView.setImageResource(R.drawable.ic_launcher);
        rightView.setImageResource(R.drawable.ic_launcher);

        leftView.setImageBitmap(ThumbnailUtils.extractThumbnail(us.getBitmap(urls.get(position * 2)), 200, 200));
        rightView.setImageBitmap(ThumbnailUtils.extractThumbnail(us.getBitmap(urls.get(position * 2 + 1)), 200, 200));

        return view;
    }

}
