package ru.mihver1.android.yaph.gui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import ru.mihver1.android.yaph.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihver1 on 18.01.14.
 */

public class PortraitRowAdapter extends BaseAdapter {

    List<PortraitRowModel> content = new ArrayList<PortraitRowModel>();
    Activity context;
    ArrayList<String> urls;

    public PortraitRowAdapter(Activity ctx, ArrayList<String> urls) {
        context = ctx;
        this.urls = urls;
    }



    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.rowlayout, null);
        WebImageView leftView = (WebImageView) view.findViewById(R.id.leftImage);
        WebImageView rightView = (WebImageView) view.findViewById(R.id.rightImage);
        ViewHolder viewHolder = new ViewHolder(leftView, rightView);
        view.setTag(viewHolder);
        holder = viewHolder;
        holder = (ViewHolder) view.getTag();
        holder.left.setPlaceholderImage(R.drawable.ic_launcher);
        holder.right.setPlaceholderImage(R.drawable.ic_launcher);
        holder.left.setImageUrl(urls.get(position * 2));
        holder.right.setImageUrl(urls.get(position * 2 + 1));

        return view;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        WebImageView left;
        WebImageView right;

        ViewHolder(WebImageView l, WebImageView r) {
            left = l;
            right = r;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.rowlayout, null);
        WebImageView leftView = (WebImageView) view.findViewById(R.id.leftImage);
        WebImageView rightView = (WebImageView) view.findViewById(R.id.rightImage);
        ViewHolder viewHolder = new ViewHolder(leftView, rightView);
        view.setTag(viewHolder);
        holder = viewHolder;
        holder = (ViewHolder) view.getTag();
        holder.left.setPlaceholderImage(R.drawable.ic_launcher);
        holder.right.setPlaceholderImage(R.drawable.ic_launcher);
        holder.left.setImageUrl(urls.get(position * 2));
        holder.right.setImageUrl(urls.get(position * 2 + 1));

        return view;
    }

}
