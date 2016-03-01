package com.weiguan.kejian.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weiguan.kejian.CommentListActivity;
import com.weiguan.kejian.MyApplication;
import com.weiguan.kejian.model.Commont;

import com.weiguan.kejian.view.CircleImageView;
import com.weiguan.kejian.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/16 0016.
 */
public class CommontListAdapter extends BaseAdapter {
    private CommentListActivity context;
    private ArrayList<Commont> commonts = new ArrayList<>();

    public CommontListAdapter(CommentListActivity context, ArrayList<Commont> commonts) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_comment, null);
            holder.cavator = (CircleImageView) convertView.findViewById(R.id.cavator);
            holder.ccomment = (ImageView) convertView.findViewById(R.id.ccomment);
            holder.ccontent = (TextView) convertView.findViewById(R.id.ccontent);
            holder.ctime = (TextView) convertView.findViewById(R.id.ctime);
            holder.cuserid = (TextView) convertView.findViewById(R.id.cuserid);
            holder.mycontent = (TextView) convertView.findViewById(R.id.mycontent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Commont commont = commonts.get(position);

        MyApplication myApp = (MyApplication) context.getApplicationContext();
        myApp.displayImageUserLogo(commont.avatar, holder.cavator);

        holder.cuserid.setText(commont.username + "：");
        holder.ctime.setText(commont.time + " 评论了你");

        holder.ccontent.setText(commont.content);

        holder.mycontent.setText("我：" + commont.content2);

        holder.ccomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.showComment(commont.id, commont.username, commont.userid, commont.newsid, commont.catid);
            }
        });

        return convertView;
    }

    class ViewHolder {
        CircleImageView cavator;
        TextView cuserid;
        TextView ctime;
        TextView ccontent;
        TextView mycontent;
        ImageView ccomment;
    }
}
