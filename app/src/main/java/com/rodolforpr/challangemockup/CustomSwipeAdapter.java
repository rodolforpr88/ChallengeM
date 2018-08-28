package com.rodolforpr.challangemockup;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomSwipeAdapter extends PagerAdapter {
    private Context ctx;
    private LayoutInflater layoutInflater;
    ImageView iv;
    Bitmap imageTaken;
    ArrayList<Bitmap> bitMapList;
    public CustomSwipeAdapter(Context ctx, ImageView iv, ArrayList<Bitmap> bitMapList) {
        this.ctx = ctx;
        this.iv = iv;
        this.bitMapList=bitMapList;
    }

    @Override
    public int getCount() {
        return bitMapList.size();  //Need to get Image Count here...
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)      ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);
        TextView tv = item_view.findViewById(R.id.image_count);
        iv = item_view.findViewById(R.id.imageView1);
        tv.setText("Imagem " + position);
        imageTaken = bitMapList.get(position);
        iv.setImageBitmap(imageTaken);



        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}