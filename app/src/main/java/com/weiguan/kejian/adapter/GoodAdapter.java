package com.weiguan.kejian.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weiguan.kejian.MyApplication;
import com.weiguan.kejian.model.Good;
import com.weiguan.kejian.util.PXUtils;
import com.weiguan.kejian.util.ScreenUtils;
import com.weiguan.kejian.view.CFTextView;
import com.weiguan.kejian.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class GoodAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Good> goods;

    private int windowWidth, imageHeight;

    public GoodAdapter(Context context, ArrayList<Good> goods) {
        this.context = context;
        this.goods = goods;

        windowWidth = ScreenUtils.getScreenWidth(context);

        imageHeight = (int)((windowWidth * 466f) / 750f);
    }

    public void notifyData(ArrayList<Good> goods) {
        this.goods = goods;
        notifyDataSetChanged();
    }

    public void setColor(TextView tv, String color) {
        if("red".equals(color)) {
            tv.setBackgroundColor(ContextCompat.getColor(context, R.color.pink_divide));
        } else if("blue".equals(color)) {
            tv.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_divide));
        } else if("green".equals(color)) {
            tv.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
        } else {
            tv.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    @Override
    public int getCount() {
        return goods.size();
    }

    @Override
    public Good getItem(int position) {
        return goods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_good, null);
            holder.good_logo = (ImageView) convertView.findViewById(R.id.good_logo);
            holder.source = (TextView) convertView.findViewById(R.id.source);
            holder.test = (CFTextView) convertView.findViewById(R.id.test);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        ViewGroup.LayoutParams params = holder.good_logo.getLayoutParams();
//        params.height = imageHeight;
//        params.width = windowWidth;
//        holder.good_logo.setLayoutParams(params);

        Good good = goods.get(position);
        MyApplication myApp = (MyApplication) context.getApplicationContext();

        myApp.displayImage(good.image, holder.good_logo);
        holder.source.setText(good.location);
        holder.test.setText(good.title);
        ViewGroup.LayoutParams lp = holder.test.getLayoutParams();
        if(holder.test.lines == 1) {
            lp.height = PXUtils.dip2px(context, 20);
        } else if(holder.test.lines == 2) {
            lp.height = PXUtils.dip2px(context, 46);
        }
        holder.test.setLayoutParams(lp);
        holder.test.invalidate();
        setColor(holder.source, "");

        return convertView;
    }

    class ViewHolder {
        ImageView good_logo;
        TextView source;
        CFTextView test;
    }
}
