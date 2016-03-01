package com.weiguan.kejian.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weiguan.kejian.model.Menber;
import com.weiguan.kejian.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/20 0020.
 */
public class MenberAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Menber> menbers = new ArrayList<>();

    public MenberAdapter(Context context, ArrayList<Menber> menbers) {
        this.context = context;
        this.menbers = menbers;
    }

    @Override
    public int getCount() {
        return menbers.size();
    }

    @Override
    public Menber getItem(int position) {
        return menbers.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_menber, null);
            holder.menber_logo = (ImageView) convertView.findViewById(R.id.menber_logo);
            holder.menber_name = (TextView) convertView.findViewById(R.id.menber_name);
            holder.menber_intro = (TextView) convertView.findViewById(R.id.menber_intro);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Menber menber = menbers.get(position);

        holder.menber_name.setText(menber.name);
        holder.menber_intro.setText(menber.intro);
        holder.menber_logo.setImageResource(menber.logores);

        return convertView;
    }

    class ViewHolder {
        public TextView menber_name, menber_intro;
        public ImageView menber_logo;
    }
}
