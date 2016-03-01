package com.weiguan.kejian;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.adapter.UserCommontAdapter;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.http.NetworkData;
import com.weiguan.kejian.model.Commont;
import com.weiguan.kejian.view.lib.internal.PLA_AdapterView;
import com.weiguan.kejian.view.view.XListView;
import com.weiguan.kejian.view.view.XListViewFooter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2016/2/16 0016.
 */
public class UserCommentActivity extends BaseUserActivity2 {
    private static final String TAG = "我的评论页面";

    private MyApplication myApp;

    private XListView usercommontlist;
    private ArrayList<Commont> commonts;
    private UserCommontAdapter adapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    usercommontlist.stopLoadMore();
                    usercommontlist.stopRefresh();
                    adapter.notifyData(commonts);
                    break;
                case 1:
                    Toast.makeText(UserCommentActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    usercommontlist.mFooterView.setState(XListViewFooter.STATE_LOAD_END);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercommont);

        StatService.onPageStart(this, TAG);

        myApp = (MyApplication) getApplicationContext();
        commonts = new ArrayList<>();
        usercommontlist = (XListView) findViewById(R.id.usercommontlist);
        adapter = new UserCommontAdapter(this, commonts);
        usercommontlist.setAdapter(adapter);
        usercommontlist.setPullLoadEnable(true);
        usercommontlist.setPullRefreshEnable(true);
        usercommontlist.isGifShow(false);
        setTitle("我的评论");

        usercommontlist.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                StatService.onEvent(UserCommentActivity.this, Event.EVENT_ID_MYCOMMONTMSG, "列表消息-select");
            }
        });

        usercommontlist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        usercommontlist.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                requestData((System.currentTimeMillis() / 1000) + "", true);
                StatService.onEvent(UserCommentActivity.this, Event.EVENT_ID_MYCOMMONTMSG, "列表刷新");
            }

            @Override
            public void onLoadMore() {
                if (commonts.size() > 0) {
                    StatService.onEvent(UserCommentActivity.this, Event.EVENT_ID_MYCOMMONTMSG, "加载更多");
                    Commont c = commonts.get(commonts.size() - 1);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        Date parse = sdf.parse(c.time);
                        requestData((parse.getTime() / 1000) + "", false);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    requestData((System.currentTimeMillis() / 1000) + "", true);
                }
            }
        });


        requestData((System.currentTimeMillis() / 1000) + "", false);
    }

    private void requestData(String s, final boolean isClear) {
        NetworkData.getMySendedCommentList(myApp.user.userid, s, new NetworkData.NetworkCallback() {
            @Override
            public void callback(String data) {
                try {
                    JSONObject jData = new JSONObject(data);
                    JSONArray list = jData.getJSONArray("list");
                    if(list.length() == 0) {
                        handler.sendEmptyMessage(3);
                    }
                    if(isClear) {
                        commonts.clear();
                    }
                    for (int i=0;i<list.length();i++) {
                        JSONObject index = list.getJSONObject(i);
                        Commont c = new Commont();
                        c.id = index.getString("id");
                        c.commentid = index.getString("commentid");
                        c.userid = index.getString("userid");
                        c.username = index.getString("username");
                        c.time = index.getString("time");
                        c.content = index.getString("content");
                        c.avatar = index.getString("avatar");
                        c.reply_commentid = index.getString("reply_commentid");
                        c.content2 = index.getString("content2");
                        c.username2 = index.getString("username2");
                        c.newsid = index.getString("newsid");
                        c.catid = index.getString("catid");
                        commonts.add(c);
                    }
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, TAG);
    }
}
