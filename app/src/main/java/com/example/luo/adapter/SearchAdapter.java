package com.example.luo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.luo.MyApplication;
import com.example.luo.model.SearchResult;
import com.example.luo.view.CircleImageView;
import com.weiguan.kejian.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
        Log.i("search info", "getView");
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_result, null);
            holder.result_title = (TextView) convertView.findViewById(R.id.result_title);
            holder.result_content = (TextView) convertView.findViewById(R.id.result_content);
            holder.publish_time = (TextView) convertView.findViewById(R.id.publish_time);
            holder.publish_name = (TextView) convertView.findViewById(R.id.publish_name);
            holder.iscollect = (CircleImageView) convertView.findViewById(R.id.iscollect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SearchResult sr = results.get(position);
        holder.result_title.setText(sr.title);
        holder.result_content.setText(sr.des);
        holder.publish_time.setText(sr.time);
        holder.publish_name.setText(sr.author);

        MyApplication myApplication = (MyApplication) context.getApplicationContext();
        myApplication.displayImage(sr.author_image, holder.iscollect);

        return convertView;
    }

    class ViewHolder {
        private TextView result_title, result_content, publish_time, publish_name;
        private CircleImageView iscollect;
    }
}
