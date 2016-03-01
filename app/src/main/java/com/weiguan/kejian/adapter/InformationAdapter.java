package com.weiguan.kejian.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.MyApplication;
import com.weiguan.kejian.WebActivity;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.model.Information;
import com.weiguan.kejian.model.TwoInformation;
import com.weiguan.kejian.util.ScreenUtils;
import com.felipecsl.gifimageview.library.GifImageView;
import com.weiguan.kejian.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class InformationAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Information> infos;

    private int windowWidth, imageHeight1, imageHeight2;

    public InformationAdapter(Context context, ArrayList<Information> infos) {
        this.context = context;
        this.infos = infos;
        windowWidth = ScreenUtils.getScreenWidth(context);

        imageHeight1 = (int)((windowWidth * 466f) / 750f);

        imageHeight2 = (int)(((windowWidth / 2) * 284) / 376);
    }

    public void notifyData(ArrayList<Information> infos) {
        this.infos = infos;
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
        return infos.size();
    }

    @Override
    public Information getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Information information = infos.get(position);
        if(convertView == null) {
            holder = new ViewHolder();
            holder.showType = information.getInfoType();
            if(holder.showType == 1) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_info, null);
                holder.c1 = new ChildHolder();
                holder.c1.info_bg = (ImageView) convertView.findViewById(R.id.info_bg);
                holder.c1.catid_text = (TextView) convertView.findViewById(R.id.catid_text);
                holder.c1.subtitle1 = (TextView) convertView.findViewById(R.id.subtitle1);
                holder.c1.subtitle2 = (TextView) convertView.findViewById(R.id.subtitle2);
                holder.c1.subtitle3 = (TextView) convertView.findViewById(R.id.subtitle3);
                holder.c1.gifbg = (GifImageView) convertView.findViewById(R.id.gifbg);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_info2, null);
                holder.c1 = new ChildHolder();
                holder.c1.info_bg = (ImageView) convertView.findViewById(R.id.info2_bg);
                holder.c1.catid_text = (TextView) convertView.findViewById(R.id.catid_text);
                holder.c1.info_title = (TextView) convertView.findViewById(R.id.info2_title);
                holder.c1.gifbg = (GifImageView) convertView.findViewById(R.id.gifbg1);

                holder.c2 = new ChildHolder();
                holder.c2.info_bg = (ImageView) convertView.findViewById(R.id.info2_bg2);
                holder.c2.catid_text = (TextView) convertView.findViewById(R.id.catid_text2);
                holder.c2.info_title = (TextView) convertView.findViewById(R.id.info2_title2);
                holder.c2.gifbg = (GifImageView) convertView.findViewById(R.id.gifbg2);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            if(information.getInfoType() != holder.showType) {
                return getView(position, null, parent);
            }
        }

        MyApplication myApp = (MyApplication) context.getApplicationContext();

        if(holder.showType == 2) {
            final TwoInformation tInfo = (TwoInformation) information;
            if(tInfo.getInfo2() != null) {
                ViewGroup.LayoutParams params = holder.c2.info_bg.getLayoutParams();
                params.height = imageHeight2;
                params.width = windowWidth / 2;
//                holder.c2.info_bg.setLayoutParams(params);
                holder.c2.gifbg.clear();
//                if(false) {
                if(tInfo.getInfo2().thumb.endsWith("gif")) {
                    holder.c2.info_bg.setVisibility(View.GONE);
                    holder.c2.gifbg.setVisibility(View.VISIBLE);
                    myApp.imageLoaderUtil.setResource(holder.c2.gifbg, tInfo.getInfo2().thumb);
                    holder.c2.gifbg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StatService.onEvent(context, Event.EVENT_ID_ARTICLELIST, tInfo.getInfo2().catidText + "列表小图-select");
                            Intent intent = new Intent(context, WebActivity.class);
                            intent.putExtra("id", tInfo.getInfo2().id);
                            intent.putExtra("catText", tInfo.getInfo2().catidText);
                            intent.putExtra("catId", tInfo.getInfo2().catid);
                            context.startActivity(intent);
                        }
                    });
                } else {
                    holder.c2.info_bg.setVisibility(View.VISIBLE);
                    holder.c2.gifbg.setVisibility(View.GONE);
                    myApp.displayImage(tInfo.getInfo2().thumb, holder.c2.info_bg);
                    holder.c2.info_bg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StatService.onEvent(context, Event.EVENT_ID_ARTICLELIST, tInfo.getInfo2().catidText + "列表小图-select");
                            Intent intent = new Intent(context, WebActivity.class);
                            intent.putExtra("id", tInfo.getInfo2().id);
                            intent.putExtra("catText", tInfo.getInfo2().catidText);
                            intent.putExtra("catId", tInfo.getInfo2().catid);
                            context.startActivity(intent);
                        }
                    });
                }

                holder.c2.info_title.setText(tInfo.getInfo2().title);
//                setColor(holder.c2.info_title, tInfo.getInfo2().titleColor);
                holder.c2.catid_text.setText(tInfo.getInfo2().catidText);
//                setColor(holder.c2.catid_text, tInfo.getInfo2().titleColor);
            }
        }

        if(holder.showType == 2) {
            ViewGroup.LayoutParams params = holder.c1.info_bg.getLayoutParams();
            params.height = imageHeight2;
            params.width = windowWidth / 2;
//            holder.c1.info_bg.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = holder.c1.info_bg.getLayoutParams();
            params.height = imageHeight1;
            params.width = windowWidth;
//            holder.c1.info_bg.setLayoutParams(params);
        }

        holder.c1.gifbg.clear();
//        if(false) {
        if(information.getInfo().thumb.endsWith("gif")) {
            holder.c1.info_bg.setVisibility(View.GONE);
            holder.c1.gifbg.setVisibility(View.VISIBLE);
            myApp.imageLoaderUtil.setResource(holder.c1.gifbg, information.getInfo().thumb);
            holder.c1.gifbg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (information.getInfoType() == 2) {
                        TwoInformation tInfo = (TwoInformation) information;
                        StatService.onEvent(context, Event.EVENT_ID_ARTICLELIST, tInfo.getInfo2().catidText + "列表小图-select");
                    } else {
                        StatService.onEvent(context, Event.EVENT_ID_ARTICLELIST, information.getInfo().catidText + "列表大图-select");
                    }
                    Intent intent = new Intent(context, WebActivity.class);
                    intent.putExtra("id", information.getInfo().id);
                    intent.putExtra("catText", information.getInfo().catidText);
                    intent.putExtra("catId", information.getInfo().catid);
                    context.startActivity(intent);
                }
            });
        } else {
            holder.c1.info_bg.setVisibility(View.VISIBLE);
            holder.c1.gifbg.setVisibility(View.GONE);
            myApp.displayImage(information.getInfo().thumb, holder.c1.info_bg);
            holder.c1.info_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (information.getInfoType() == 2) {
                        TwoInformation tInfo = (TwoInformation) information;
                        StatService.onEvent(context, Event.EVENT_ID_ARTICLELIST, tInfo.getInfo2().catidText + "列表小图-select");
                    } else {
                        StatService.onEvent(context, Event.EVENT_ID_ARTICLELIST, information.getInfo().catidText + "列表大图-select");
                    }
                    Intent intent = new Intent(context, WebActivity.class);
                    intent.putExtra("id", information.getInfo().id);
                    intent.putExtra("catId", information.getInfo().catid);
                    intent.putExtra("catText", information.getInfo().catidText);
                    context.startActivity(intent);
                }
            });
        }


        if(holder.showType == 2) {
            holder.c1.info_title.setText(information.getInfo().title);
        }
        holder.c1.catid_text.setText(information.getInfo().catidText);
        if(holder.showType == 1)
        setColor(holder.c1.catid_text, information.getInfo().titleColor);

        if(holder.showType == 1 && information.getInfo().subTitle1 != null && !"".equals(information.getInfo().subTitle1)) {
            holder.c1.subtitle1.setVisibility(View.VISIBLE);
            holder.c1.subtitle1.setText(information.getInfo().subTitle1);
            if(holder.showType == 1)
            setColor(holder.c1.subtitle1, information.getInfo().titleColor);

        } else {
            if(holder.c1.subtitle1 != null)
            holder.c1.subtitle1.setVisibility(View.GONE);
        }

        if(holder.showType == 1 && information.getInfo().subTitle2 != null && !"".equals(information.getInfo().subTitle2)) {
            holder.c1.subtitle2.setVisibility(View.VISIBLE);
            holder.c1.subtitle2.setText(information.getInfo().subTitle2);
            if(holder.showType == 1)
            setColor(holder.c1.subtitle2, information.getInfo().titleColor);

        } else {
            if(holder.c1.subtitle2 != null)
                holder.c1.subtitle2.setVisibility(View.GONE);
        }

        if(holder.showType == 1 && information.getInfo().subTitle3 != null && !"".equals(information.getInfo().subTitle3)) {
            holder.c1.subtitle3.setVisibility(View.VISIBLE);
            holder.c1.subtitle3.setText(information.getInfo().subTitle3);
            if(holder.showType == 1)
            setColor(holder.c1.subtitle3, information.getInfo().titleColor);
        } else {
            if(holder.c1.subtitle3 != null)
            holder.c1.subtitle3.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        int showType;
        ChildHolder c1;
        ChildHolder c2;
    }

    class ChildHolder {
        ImageView info_bg;
        TextView catid_text, info_title, subtitle1, subtitle2, subtitle3;
        GifImageView gifbg;
    }
}
