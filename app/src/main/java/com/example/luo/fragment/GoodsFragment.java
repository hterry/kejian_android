package com.example.luo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.example.luo.WebActivity;
import com.example.luo.adapter.GoodAdapter;
import com.example.luo.commons.Event;
import com.example.luo.http.NetworkData;
import com.example.luo.model.Good;
import com.example.luo.util.ListViewUtils;
import com.example.luo.util.NetworkUtils;
import com.example.luo.view.lib.internal.PLA_AdapterView;
import com.example.luo.view.view.XListView;
import com.example.luo.view.view.XListViewFooter;
import com.weiguan.kejian.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class GoodsFragment extends Fragment {
    private View view;
    private XListView goodlist;
    private GoodAdapter adapter;
    private ArrayList<Good> goods = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 1:
                    goodlist.stopLoadMore();
                    goodlist.stopRefresh();
                    goodlist.mFooterView.setState(XListViewFooter.STATE_LOAD_END);
                    break;
                case 2:
                    goodlist.stopLoadMore();
                    goodlist.stopRefresh();
                    adapter.notifyData(goods);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.goods_fragment, null);
            goodlist = (XListView) view.findViewById(R.id.goodlist);
            goodlist.setPullLoadEnable(true);
            goodlist.setPullRefreshEnable(true);
            adapter = new GoodAdapter(getActivity(), goods);
            goodlist.setAdapter(adapter);

            goodlist.setXListViewListener(new XListView.IXListViewListener() {
                @Override
                public void onRefresh() {
                    if (NetworkUtils.isNetworkAvailable(getActivity())) {
                        StatService.onEvent(getActivity(), Event.EVENT_ID_LISTREFRESH, "传送门");
                        requestGoods(System.currentTimeMillis() / 1000);
                    } else {
                        Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(2);
                    }
                }

                @Override
                public void onLoadMore() {
                    if (NetworkUtils.isNetworkAvailable(getActivity())) {
                        if (goods.size() > 0) {
                            StatService.onEvent(getActivity(), Event.EVENT_ID_LISTGETMORE, "传送门");
                            requestGoods(goods.get(goods.size() - 1).inputTime);
                        }
                    } else {
                        Toast.makeText(getActivity(), "网络不可用", Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(1);
                    }
                }
            });

            goodlist.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                    if (id > 0) {
                        Good good = goods.get((int) id);
                        StatService.onEvent(getActivity(), Event.EVENT_ID_SHOPLISTSELECT, good.title);
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        intent.putExtra("url", good.url);
                        intent.putExtra("goodname", good.title);
                        startActivity(intent);
                        getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    }
                }
            });

            requestGoods(System.currentTimeMillis() / 1000);
        }
        return view;
    }

    private void requestGoods(String inputTime) {
        NetworkData.getGoodList(inputTime, new NetworkData.NetworkCallback() {
            @Override
            public void callback(String data) {
                try {
                    JSONObject jsonData = new JSONObject(data);
                    JSONArray list = jsonData.getJSONArray("list");
                    if(list.length() == 0) {
                        handler.sendEmptyMessage(1);
                    }

                    for(int i=0;i<list.length();i++) {
                        JSONObject index = list.getJSONObject(i);
                        Good good = new Good();
                        good.id = index.getString("id");
                        good.image = index.getString("image");
                        good.title = index.getString("title");
                        good.location = index.getString("location");
                        good.url = index.getString("url");
                        good.inputTime = index.getString("inputTime");
                        goods.add(good);
                    }

                    handler.sendEmptyMessage(2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void requestGoods(long l) {
        NetworkData.getGoodList(String.valueOf(l), new NetworkData.NetworkCallback() {
            @Override
            public void callback(String data) {
                try {
                    JSONObject jsonData = new JSONObject(data);
                    JSONArray list = jsonData.getJSONArray("list");
                    goods.clear();
                    for(int i=0;i<list.length();i++) {
                        JSONObject index = list.getJSONObject(i);
                        Good good = new Good();
                        good.id = index.getString("id");
                        good.image = index.getString("image");
                        good.title = index.getString("title");
                        good.location = index.getString("location");
                        good.url = index.getString("url");
                        good.inputTime = index.getString("inputTime");
                        goods.add(good);
                    }
                    handler.sendEmptyMessage(2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
