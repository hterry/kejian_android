package com.weiguan.kejian;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.weiguan.kejian.adapter.InformationAdapter;
import com.weiguan.kejian.http.NetworkData;
import com.weiguan.kejian.model.Info;
import com.weiguan.kejian.model.Information;
import com.weiguan.kejian.model.OneInformation;
import com.weiguan.kejian.model.TwoInformation;
import com.weiguan.kejian.util.NetworkUtils;
import com.weiguan.kejian.view.view.XListView;
import com.weiguan.kejian.view.view.XListViewFooter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 2016/2/16 0016.
 */
public class MyFavouriteActivity extends BaseUserActivity2 {
    private int id = -1;

    private XListView favourlist;
    private InformationAdapter adapter;
    private ArrayList<Information> infos = new ArrayList<>();

    private ArrayList<Info> is = new ArrayList<>();

    private Info lastInfo;
    private boolean isLastRunning;

    private MyApplication myApp;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    Toast.makeText(MyFavouriteActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    adapter.notifyData((ArrayList<Information>) infos.clone());
                    favourlist.stopRefresh();
                    favourlist.stopLoadMore();
                    isLastRunning = false;
                    break;
                case 2:
//                    Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                    isLastRunning = false;
//                    infolist.removeFooterView(footView);
                    //这里设置已经查完所有数据“已经全部加载完毕”
                    favourlist.stopLoadMore();
                    favourlist.mFooterView.setState(XListViewFooter.STATE_LOAD_END);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myfavour);
        myApp = (MyApplication) getApplicationContext();
        setTitle("收藏");
        adapter = new InformationAdapter(this, (ArrayList<Information>) infos.clone());
        favourlist = (XListView) findViewById(R.id.favourlist);
        favourlist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
        favourlist.setAdapter(adapter);
        favourlist.setPullLoadEnable(true);
        favourlist.setPullLoadEnable(true);


        favourlist.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtils.isNetworkAvailable(MyFavouriteActivity.this)) {
                    requestData(id, true);
                } else {
                    handler.sendMessage(Message.obtain(handler, 0, "网络不可用"));
                }
            }

            @Override
            public void onLoadMore() {
                if (NetworkUtils.isNetworkAvailable(MyFavouriteActivity.this)) {
                    if (!isLastRunning) {
                        if (lastInfo == null) {
                            onRefresh();
                        } else {
                            isLastRunning = true;
                            requestData(id, false);
                        }
                    }
                } else {
                    handler.sendMessage(Message.obtain(handler, 0, "网络不可用"));
                }
            }
        });

        requestData(id, true);
    }

    private void requestData(int id, final boolean isClear) {
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        if(!isClear) {
            time = lastInfo.inputTime;
        }
        NetworkData.getHomeList(String.valueOf(id), time, myApp.user.userid, new NetworkData.NetworkCallback() {
            @Override
            public void callback(String data) {
                try {
                    JSONObject jsonData = new JSONObject(data);
                    JSONArray list = jsonData.getJSONArray("list");
                    if (list.length() == 0) {
                        handler.sendEmptyMessage(2);
                    }
                    if (isClear) {
                        is.clear();
                    }
                    for (int i = 0; i < list.length(); i++) {
                        Info info = new Info();
                        JSONObject index = list.getJSONObject(i);
                        info.id = index.getString("id");
                        info.catid = index.getString("catid");
                        info.thumb = index.getString("thumb");
                        info.title = index.getString("title");
                        info.catidText = myApp.catIdText.get(info.catid);
                        info.subTitle1 = index.getString("subTitle1");
                        info.subTitle2 = index.getString("subTitle2");
                        info.subTitle3 = index.getString("subTitle3");
                        info.titleColor = index.getString("titleColor");
                        info.showType = index.getString("showType");
                        info.inputTime = index.getString("inputTime");
                        is.add(info);
                    }
                    dealInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void dealInfo() {
        infos.clear();
        Collections.sort(is, new SortByTime());
        Information two = null;
        for(Info info : is) {
            if(info.showType.equals("0")) {
                Information information = new OneInformation();
                information.info1 = info;
                infos.add(information);
            } else if(info.showType.equals("1")) {
                if(two == null) {
                    two = new TwoInformation();
                    two.info1 = info;
                } else {
                    two.info2 = info;
                    infos.add(two);
                    two = null;
                }
            }

            if(is.indexOf(info) == is.size() - 1) {
                lastInfo = info;
                if(two != null) {
                    infos.add(two);
                    two = null;
                }
            }
        }
        handler.sendEmptyMessage(1);
    }

    class SortByTime implements Comparator {

        @Override
        public int compare(Object lhs, Object rhs) {
            Info i1 = (Info) lhs;
            Info i2 = (Info) rhs;
            if(Integer.parseInt(i1.inputTime) > Integer.parseInt(i2.inputTime)) {
                return 1;
            }
            return 0;
        }
    }

}
