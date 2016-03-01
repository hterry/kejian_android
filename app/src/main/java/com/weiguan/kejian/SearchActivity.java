package com.weiguan.kejian;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weiguan.kejian.adapter.SearchAdapter;
import com.weiguan.kejian.commons.MyGesture;
import com.weiguan.kejian.http.NetworkData;
import com.weiguan.kejian.model.SearchResult;
import com.weiguan.kejian.view.lib.internal.PLA_AdapterView;
import com.weiguan.kejian.view.view.XListView;
import com.weiguan.kejian.view.view.XListViewFooter;
import com.umeng.message.PushAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/20 0020.
 */
public class SearchActivity extends BaseAnimationActivity implements View.OnClickListener {
    private RelativeLayout search_bg;
    private RelativeLayout tvly;
    private Button startsearch;
    private EditText keyword;
    private XListView result_list;
    private SearchAdapter adapter;
    private ArrayList<SearchResult> searchResults;

    private MyApplication myApp;

    private ImageView drow;
    private CheckBox issortfromuser;
    private TextView tv, select_sort;
    private LinearLayout sortly;
    private String[] selData;
    private TextView sort1, sort2, sort3;

    private String kw;
    private String sort;
    private String userid;

    private MyGesture myGesture;
    private GestureDetector detector;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            result_list.setVisibility(View.VISIBLE);
            result_list.stopRefresh();
            result_list.stopLoadMore();
            switch (msg.what) {
                case 1:
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
        selData = getResources().getStringArray(R.array.sorttype);
        myApp = (MyApplication) getApplicationContext();

        PushAgent.getInstance(this).onAppStart();

        initView();

        myGesture = new MyGesture(this);
        detector = new GestureDetector(this, myGesture);
        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        result_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

    }

    private void initView() {
        tvly = (RelativeLayout) findViewById(R.id.tvly);

        sortly = (LinearLayout) findViewById(R.id.sortly);
        sort1 = (TextView) findViewById(R.id.sort1);
        sort2 = (TextView) findViewById(R.id.sort2);
        sort3 = (TextView) findViewById(R.id.sort3);

        sort1.setOnClickListener(this);
        sort2.setOnClickListener(this);
        sort3.setOnClickListener(this);

        drow = (ImageView) findViewById(R.id.drow);
        issortfromuser = (CheckBox) findViewById(R.id.issortfromuser);
        tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(this);
        tvly.setOnClickListener(this);

        if(myApp.user.userid != null && !"".equals(myApp.user.userid)) {

        } else {
            tv.setVisibility(View.GONE);
            issortfromuser.setVisibility(View.GONE);
        }

        select_sort = (TextView) findViewById(R.id.select_sort);

        select_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sortly.getVisibility() == View.GONE) {
                    //开启动画淡入
                    sortlyIn();
                } else {
                    //开启动画淡出
                    sortlyOut();
                }
            }
        });

//        if(myApp.user.userid != null && !"".equals(myApp.user.userid)) {
//            issortfromuser.setChecked(true);
//        } else {
//            issortfromuser.setChecked(false);
//        }

        issortfromuser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (myApp.user.userid != null && !"".equals(myApp.user.userid)) {
                } else {
                    issortfromuser.setChecked(false);
                    startActivity(new Intent(SearchActivity.this, LoginActivity.class));
                    return;
                }
                initSearchExtraData();
                requestSearchResult(kw, String.valueOf(System.currentTimeMillis() / 1000), true);
            }
        });

        search_bg = (RelativeLayout) findViewById(R.id.search_bg);
        startsearch = (Button) findViewById(R.id.startsearch);
        keyword = (EditText) findViewById(R.id.keyword);
        keyword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    gotoSearch();
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(keyword.getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        result_list = (XListView) findViewById(R.id.result_list);
        searchResults = new ArrayList<>();
        adapter = new SearchAdapter(this, searchResults);
        result_list.isGifShow(false);
        result_list.setVisibility(View.GONE);
        result_list.setAdapter(adapter);

        result_list.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                if (id == -1) {
                    result_list.mFooterView.setState(XListViewFooter.STATE_LOADING);
                    loadMore();
                }
                if (searchResults.size() > 0 && id >= 0) {
                    SearchResult sr = searchResults.get((int) id);
                    Intent intent = new Intent(SearchActivity.this, WebActivity.class);
                    intent.putExtra("id", sr.id);
                    intent.putExtra("catId", sr.catid);
                    startActivity(intent);
                }
            }
        });

        startsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSearch();
            }
        });

        result_list.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
//                kw = keyword.getText().toString();
                initSearchExtraData();
                requestSearchResult(kw, String.valueOf(System.currentTimeMillis() / 1000), true);
            }

            @Override
            public void onLoadMore() {
                loadMore();
            }
        });
    }

    private void gotoSearch() {
        if(!"".equals(keyword.getText().toString())) {
            kw = keyword.getText().toString();
            initSearchExtraData();
            requestSearchResult(kw, String.valueOf(System.currentTimeMillis() / 1000), true);
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(keyword.getWindowToken(), 0) ;
        } else {
            Toast.makeText(SearchActivity.this, "请输入搜索内容", Toast.LENGTH_SHORT).show();
        }
    }

    private void sortlyOut() {
        sortly.startAnimation(AnimationUtils.loadAnimation(this, R.anim.select_out));
        sortly.setVisibility(View.GONE);
    }

    private void sortlyIn() {
        sortly.startAnimation(AnimationUtils.loadAnimation(this, R.anim.select_in));
        sortly.setVisibility(View.VISIBLE);
    }

    private void initSearchExtraData() {
        userid = null;
        sort = null;
        String s = select_sort.getText().toString();
        out:
        for (int i=0;i<selData.length;i++) {
            if(s.equals(selData[i])) {
                switch(i) {
                    case 0:
                        sort = "comment";
                        break out;
                    case 1:
                        sort = "like";
                        break out;
                    case 2:
                        sort = "time";
                        break out;
                }
            }
        }

        if(issortfromuser.isChecked() && myApp.user.userid != null && !"".equals(myApp.user.userid)) {
            userid = myApp.user.userid;
        }
    }

    private void loadMore() {
        if(searchResults.size() > 0) {
            SearchResult searchResult = searchResults.get(searchResults.size() - 1);
            requestSearchResult(kw, searchResult.inputTime, false);
        }
    }

    public void requestSearchResult(String keyword, String time, final boolean isClear) {
        if(keyword != null && !"".equals(keyword)) {
            NetworkData.search(keyword, time, sort, userid, new NetworkData.NetworkCallback() {
                @Override
                public void callback(String data) {
                    Log.i("search", data);
                    if (isClear) {
                        searchResults.clear();
                    }
                    try {
                        JSONObject json = new JSONObject(data);
                        JSONArray list = json.getJSONArray("list");
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
                            sr.titleColor = index.getString("titleColor");
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

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.tvly:
            case R.id.tv:
                issortfromuser.performClick();
                break;
            case R.id.sort1:
                sortlyOut();
                select_sort.setText(selData[0]);
                break;
            case R.id.sort2:
                sortlyOut();
                select_sort.setText(selData[1]);
                break;
            case R.id.sort3:
                sortlyOut();
                select_sort.setText(selData[2]);
                break;
        }
        initSearchExtraData();
        requestSearchResult(kw, String.valueOf(System.currentTimeMillis() / 1000), true);
    }
}
