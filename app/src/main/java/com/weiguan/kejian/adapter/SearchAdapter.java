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
import com.weiguan.kejian.model.SearchResult;
import com.weiguan.kejian.util.PXUtils;
import com.weiguan.kejian.view.CFTextView;
import com.weiguan.kejian.view.CircleImageView;
import com.weiguan.kejian.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/26 0026.
 */
public class SearchAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SearchResult> results = new ArrayList<>();
    private SimpleDateFormat sdf;

    public SearchAdapter(Context context, ArrayList<SearchResult> results) {
        this.context = context;
        this.results = results;
        sdf = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void setColor(CFTextView tv, String color) {
        if("red".equals(color)) {
            tv.setBgColor(ContextCompat.getColor(context, R.color.pink_divide));
        } else if("blue".equals(color)) {
            tv.setBgColor(ContextCompat.getColor(context, R.color.blue_divide));
        } else if("green".equals(color)) {
            tv.setBgColor(ContextCompat.getColor(context, R.color.green));
        } else {
            tv.setBgColor(ContextCompat.getColor(context, R.color.black));
        }
    }


    public void notifyData(ArrayList<SearchResult> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_result, null);
            holder.result_title = (CFTextView) convertView.findViewById(R.id.result_title);
            holder.result_content = (TextView) convertView.findViewById(R.id.result_content);
            holder.publish_time = (TextView) convertView.findViewById(R.id.publish_time);
            holder.publish_name = (TextView) convertView.findViewById(R.id.publish_name);
            holder.iscollect = (ImageView) convertView.findViewById(R.id.iscollect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SearchResult sr = results.get(position);
        holder.result_title.setText(sr.title);
        ViewGroup.LayoutParams lp = holder.result_title.getLayoutParams();
        if(holder.result_title.lines == 1) {
            lp.height = PXUtils.dip2px(context, 20);
        } else if(holder.result_title.lines == 2) {
            lp.height = PXUtils.dip2px(context, 46);
        } else {

        }

        holder.result_title.setLayoutParams(lp);

        setColor(holder.result_title, sr.titleColor);

        holder.result_content.setText(sr.des);
        holder.publish_time.setText(sr.time);
        holder.publish_name.setText(sr.author);

        MyApplication myApplication = (MyApplication) context.getApplicationContext();
        myApplication.displayImage(sr.author_image, holder.iscollect);

        return convertView;
    }

    class ViewHolder {
        private CFTextView result_title;
        private TextView result_content, publish_time, publish_name;
        private ImageView iscollect;
    }
}
