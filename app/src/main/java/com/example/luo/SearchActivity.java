package com.example.luo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.example.luo.adapter.SearchAdapter;
import com.example.luo.http.NetworkData;
import com.example.luo.model.SearchResult;
import com.example.luo.view.lib.internal.PLA_AdapterView;
import com.example.luo.view.view.XListView;
import com.example.luo.view.view.XListViewFooter;
import com.umeng.message.PushAgent;
import com.weiguan.kejian.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/20 0020.
 */
public class SearchActivity extends Activity {
    private LinearLayout search_bg;
    private Button startsearch;
    private EditText keyword;
    private XListView result_list;
    private SearchAdapter adapter;
    private ArrayList<SearchResult> searchResults;

    private String kw;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            result_list.setVisibility(View.VISIBLE);
            result_list.stopRefresh();
            result_list.stopLoadMore();
            switch (msg.what) {
                case 1:
                    Log.i("info", "message 1 ---- searchResults = " + searchResults.size());
                    if(searchResults.size() == 0) {
                        Toast.makeText(SearchActivity.this, "亲，搜不出结果，换个关键字试试", Toast.LENGTH_SHORT).show();
                        result_list.setVisibility(View.GONE);
                    }
                    adapter.notifyData(searchResults);
                    break;
                case 2:
                    result_list.mFooterView.setState(XListViewFooter.STATE_LOAD_END);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        PushAgent.getInstance(this).onAppStart();

        initView();
    }

    private void initView() {
        search_bg = (LinearLayout) findViewById(R.id.search_bg);
        startsearch = (Button) findViewById(R.id.startsearch);
        keyword = (EditText) findViewById(R.id.keyword);
        result_list = (XListView) findViewById(R.id.result_list);
        searchResults = new ArrayList<>();
        adapter = new SearchAdapter(this, searchResults);

        result_list.setVisibility(View.GONE);
        result_list.setAdapter(adapter);

        result_list.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                if(searchResults.size() > 0 && id > 0) {
                    SearchResult sr = searchResults.get((int) id);
                    Intent intent = new Intent(SearchActivity.this, WebActivity.class);
                    intent.putExtra("id", sr.id);
                    startActivity(intent);
                }
            }
        });

        startsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kw = keyword.getText().toString();
                requestSearchResult(kw, String.valueOf(System.currentTimeMillis() / 1000), true);
            }
        });

        result_list.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
//                kw = keyword.getText().toString();
                requestSearchResult(kw, String.valueOf(System.currentTimeMillis() / 1000), true);
            }

            @Override
            public void onLoadMore() {
                if(searchResults.size() > 0) {
                    SearchResult searchResult = searchResults.get(searchResults.size() - 1);
                    requestSearchResult(kw, searchResult.inputTime, false);
                }
            }
        });
    }

    public void requestSearchResult(String keyword, String time, final boolean isClear) {
        NetworkData.search(keyword, time, new NetworkData.NetworkCallback() {
            @Override
            public void callback(String data) {
                Log.i("info", "callback ------" + data);
                if (isClear) {
                    searchResults.clear();
                }
                try {
                    JSONObject json = new JSONObject(data);
                    JSONArray list = json.getJSONArray("list");
                    Log.i("info", "list.length() ------" + list.length());
                    if (!isClear && list.length() == 0) {
                        handler.sendEmptyMessage(2);
                        return;
                    }
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject index = list.getJSONObject(i);
                        SearchResult sr = new SearchResult();
                        sr.id = index.getString("id");
                        sr.inputTime = index.getString("inputTime");
                        sr.catid = index.getString("catid");
                        sr.thumb = index.getString("thumb");
                        sr.title = index.getString("title");
                        sr.des = index.getString("des");
                        sr.time = index.getString("time");
                        sr.author = index.getString("author");
                        sr.author_image = index.getString("author_image");
                        searchResults.add(sr);
                    }
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
