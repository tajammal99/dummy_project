package com.amina.campfootainment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] images = {R.drawable.slide_x,R.drawable.slide_y,R.drawable.slide_z };

    public ViewPagerAdapter(Context context)
    {
        this.context =  context;
    }

    @Override
    public int getCount()
    {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o ;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout_view_pager,null);


        ImageView imageView = view.findViewById(R.id.myImageViewer);
        imageView.setImageResource(images[position]);

        ViewPager viewPager = (ViewPager)container;
        viewPager.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        ViewPager viewPager = (ViewPager) container;
        View view = (View)object;
        viewPager.removeView(view);
    }
}
