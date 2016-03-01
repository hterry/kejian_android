package com.weiguan.kejian.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.MainActivity;
import com.weiguan.kejian.MyApplication;
import com.weiguan.kejian.adapter.InformationAdapter;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.http.NetworkData;
import com.weiguan.kejian.model.Info;
import com.weiguan.kejian.model.Information;
import com.weiguan.kejian.model.OneInformation;
import com.weiguan.kejian.model.TwoInformation;
import com.weiguan.kejian.util.ListViewUtils;
import com.weiguan.kejian.util.NetworkUtils;
import com.weiguan.kejian.view.view.XListView;
import com.weiguan.kejian.view.view.XListViewFooter;
import com.weiguan.kejian.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class InformationFragment extends Fragment {
    private View view;

    private int id = -1;

    private XListView infolist;
    private InformationAdapter adapter;
    private ArrayList<Information> infos = new ArrayList<>();

    private ArrayList<Info> is = new ArrayList<>();

    private MainActivity activity;
    private Info lastInfo;
    private boolean isLastRunning;

    private MyApplication myApp;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 1:
                    adapter.notifyData((ArrayList<Information>) infos.clone());
                    infolist.stopRefresh();
                    infolist.stopLoadMore();
                    isLastRunning = false;
                    break;
                case 2:
//                    Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                    isLastRunning = false;
//                    infolist.removeFooterView(footView);
                    //这里设置已经查完所有数据“已经全部加载完毕”
                    infolist.stopLoadMore();
                    infolist.mFooterView.setState(XListViewFooter.STATE_LOAD_END);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            activity = (MainActivity) getActivity();
            myApp = (MyApplication) activity.getApplicationContext();
            view = LayoutInflater.from(getActivity()).inflate(R.layout.info_fragment, null);
            infolist = (XListView) view.findViewById(R.id.infolist);
            adapter = new InformationAdapter(getActivity(), (ArrayList<Information>) infos.clone());
            infolist.setAdapter(adapter);
            infolist.setPullLoadEnable(true);
            infolist.setPullLoadEnable(true);
            infolist.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    if(NetworkUtils.isNetworkAvailable(getActivity())) {
                        StatService.onEvent(getActivity(), Event.EVENT_ID_ARTICLELIST, activity.catIdText.get(id + "") + "列表刷新");
                        requestHomeInfo(id);
                    } else {
                        Toast.makeText(getActivity(), "网路不可用", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onLoadMore() {
                    if(NetworkUtils.isNetworkAvailable(getActivity())) {
                        if (!isLastRunning) {
                            StatService.onEvent(getActivity(), Event.EVENT_ID_ARTICLELIST, activity.catIdText.get(id + "") + "加载更多");
                            if(lastInfo == null) {
                                onRefresh();
                            } else {
                                isLastRunning = true;
                                requestMoreHomeInfo(id, lastInfo.inputTime);
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "网路不可用", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            requestHomeInfo(id);
        }
        return view;
    }

    private String content;
    public void requestHomeInfo(int id) {
        if(id == -2) {
            if(content != null) {
                StatService.onPageEnd(getActivity(), content);
                content = null;
            }
            content = "收藏列表";
            StatService.onPageStart(getActivity(), content);
            this.id = -2;
            NetworkData.getHomeList(String.valueOf(id), String.valueOf(System.currentTimeMillis() / 1000), myApp.user.userid, new NetworkData.NetworkCallback() {
                @Override
                public void callback(String data) {
                    dealServerBack(data);
                }
            });
            return;
        }


        if(content == null) {
            content = "首页列表";
            StatService.onPageStart(getActivity(), content);
        } else {
            StatService.onPageEnd(getActivity(), content);
            if(activity.catIdText.get(id + "") != null) {
                content = activity.catIdText.get(id + "") + "列表";
                StatService.onPageStart(getActivity(), content);
            }
        }
        this.id = id;
        NetworkData.getHomeList(String.valueOf(id), String.valueOf(System.currentTimeMillis() / 1000), new NetworkData.NetworkCallback() {
            @Override
            public void callback(String data) {
                dealServerBack(data);
            }
        });
    }

    private void dealServerBack(String data) {
        try {
            JSONObject jsonData = new JSONObject(data);
            JSONArray list = jsonData.getJSONArray("list");
            is.clear();
            for (int i = 0; i < list.length(); i++) {
                Info info = new Info();
                JSONObject index = list.getJSONObject(i);
                info.id = index.getString("id");
                info.catid = index.getString("catid");
                info.thumb = index.getString("thumb");
                info.title = index.getString("title");
                info.catidText = activity.catIdText.get(info.catid);
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

    public void requestMoreHomeInfo(int id, String time) {
        this.id = id;
        NetworkData.getHomeList(String.valueOf(id), time, new NetworkData.NetworkCallback() {
            @Override
            public void callback(String data) {
                try {
                    JSONObject jsonData = new JSONObject(data);
                    JSONArray list = jsonData.getJSONArray("list");

                    if (list.length() == 0) {
                        handler.sendEmptyMessage(2);
                        return;
                    }

                    for (int i = 0; i < list.length(); i++) {
                        Info info = new Info();
                        JSONObject index = list.getJSONObject(i);
                        info.id = index.getString("id");
                        info.catid = index.getString("catid");
                        info.thumb = index.getString("thumb");
                        info.title = index.getString("title");
                        info.catidText = activity.catIdText.get(info.catid);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(getActivity(), content);
    }
}
