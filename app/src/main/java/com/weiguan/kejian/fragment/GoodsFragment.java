package com.weiguan.kejian.fragment;

import android.content.Intent;
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
import com.weiguan.kejian.WebActivity;
import com.weiguan.kejian.adapter.GoodAdapter;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.http.NetworkData;
import com.weiguan.kejian.model.Good;
import com.weiguan.kejian.util.ListViewUtils;
import com.weiguan.kejian.util.NetworkUtils;
import com.weiguan.kejian.view.lib.internal.PLA_AdapterView;
import com.weiguan.kejian.view.view.XListView;
import com.weiguan.kejian.view.view.XListViewFooter;
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
                        StatService.onEvent(getActivity(), Event.EVENT_ID_SHOPLIST, "列表刷新");
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
                            StatService.onEvent(getActivity(), Event.EVENT_ID_SHOPLIST, "加载更多");
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
                    if (id >= 0) {
                        Good good = goods.get((int) id);
                        StatService.onEvent(getActivity(), Event.EVENT_ID_SHOPLIST, "列表-select:" + good.title);
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        intent.putExtra("url", good.url);
                        intent.putExtra("goodname", good.title);
                        startActivity(intent);
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
