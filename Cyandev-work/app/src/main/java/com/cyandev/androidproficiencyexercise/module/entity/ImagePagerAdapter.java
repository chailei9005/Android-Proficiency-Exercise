package com.cyandev.androidproficiencyexercise.module.entity;

import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.style.UpdateAppearance;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by cyandev on 2016/11/25.
 */
public class ImagePagerAdapter extends PagerAdapter {

    private ViewPager pager;

    private List<ImageView> cachedViews = new ArrayList<>();
    private List<String> urls;

    public ImagePagerAdapter(ViewPager pager) {
        this.pager = pager;
    }

    public void setUrls(final List<String> urls) {
        this.urls = urls;

        for (int i = 0; i < urls.size() && i < cachedViews.size(); i++) {
            configureImageView(cachedViews.get(i), urls.get(i));
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (urls == null) {
            return 0;
        }
        return urls.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView;
        if (position >= cachedViews.size()) {
            imageView = new ImageView(container.getContext());
            imageView.setTag(position);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            cachedViews.add(imageView);
        } else {
            imageView = cachedViews.get(position);
        }

        configureImageView(imageView, urls.get(position));

        container.addView(imageView, 0);

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return Objects.equals(view, object);
    }

    private void configureImageView(ImageView view, String url) {
        Picasso.with(view.getContext())
                .load(url)
                .noFade()
                .into(view);
    }

}
