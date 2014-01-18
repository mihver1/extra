package ru.mihver1.android.yaph.gui;

import ru.mihver1.android.yaph.R;

/**
 * Created by mihver1 on 18.01.14.
 */
public class PortraitRowModel {
    public WebImageView leftImage;
    public WebImageView rightImage;

    PortraitRowModel(String left, String right) {
        leftImage.setPlaceholderImage(R.drawable.ic_launcher);
        rightImage.setPlaceholderImage(R.drawable.ic_launcher);
        leftImage.setImageUrl(left);
        rightImage.setImageUrl(right);
    }

}
