package com.example.luo.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.luo.MyApplication;
import com.example.luo.model.LeftModel;
import com.weiguan.kejian.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/15 0015.
 */
public class LeftListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<LeftModel> leftModes = new ArrayList<>();

    public LeftListAdapter(Context context, ArrayList<LeftModel> leftModes) {
        this.context = context;
        this.leftModes = leftModes;
    }

    public void notifyData(ArrayList<LeftModel> leftModes) {
        this.leftModes = leftModes;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return leftModes.size();
    }

    @Override
    public LeftModel getItem(int position) {
        return leftModes.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_left, null);
            holder.left_logo = (ImageView) convertView.findViewById(R.id.left_logo);
            holder.left_text = (TextView) convertView.findViewById(R.id.left_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LeftModel lm = leftModes.get(position);
        holder.left_text.setText(lm.name);
        if(lm.isClick) {
            holder.left_text.setTextColor(ContextCompat.getColor(context, R.color.pink_divide));
        } else {
            holder.left_text.setTextColor(ContextCompat.getColor(context, R.color.gray));
        }

        MyApplication myApp = (MyApplication) context.getApplicationContext();
        myApp.displayImage(lm.image, holder.left_logo);

        return convertView;
    }

    class ViewHolder {
        ImageView left_logo;
        TextView left_text;
    }
}
