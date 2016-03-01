package com.weiguan.kejian.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weiguan.kejian.MyApplication;
import com.weiguan.kejian.WebActivity;
import com.weiguan.kejian.model.Commont;
import com.weiguan.kejian.view.CircleImageView;
import com.weiguan.kejian.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/16 0016.
 */
public class UserCommontAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Commont> commonts = new ArrayList<>();

    public UserCommontAdapter(Context context, ArrayList<Commont> commonts) {
        this.commonts = commonts;
        this.context = context;
    }

    public void notifyData(ArrayList<Commont> commonts) {
        this.commonts = (ArrayList<Commont>) commonts.clone();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return commonts.size();
    }

    @Override
    public Object getItem(int position) {
        return commonts.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_usercommont, null);
            holder.utime = (TextView) convertView.findViewById(R.id.utime);
            holder.ucontent = (TextView) convertView.findViewById(R.id.ucontent);
            holder.tuserid = (TextView) convertView.findViewById(R.id.tuserid);
            holder.tcontent = (TextView) convertView.findViewById(R.id.tcontent);
            holder.uavator = (CircleImageView) convertView.findViewById(R.id.uavator);
            holder.tly = (LinearLayout) convertView.findViewById(R.id.tly);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Commont c = commonts.get(position);
        final MyApplication myApp = (MyApplication) context.getApplicationContext();
        myApp.displayImageUserLogo(c.avatar, holder.uavator);

        holder.utime.setText(c.time);

        holder.ucontent.setText(c.content);
        holder.tuserid.setText(c.username2);
        holder.tcontent.setText("留言内容：" + c.content2);
        holder.tly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("id", c.newsid);
                intent.putExtra("catText", myApp.catIdText.get(c.catid));
                intent.putExtra("catId", c.catid);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView utime;
        TextView ucontent;
        CircleImageView uavator;
        TextView tuserid;
        TextView tcontent;
        LinearLayout tly;
    }
}
