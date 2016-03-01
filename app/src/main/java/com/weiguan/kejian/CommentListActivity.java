package com.weiguan.kejian;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.adapter.CommontListAdapter;
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
public class CommentListActivity extends BaseUserActivity2 implements View.OnClickListener {
    private static final String TAG = "评论消息页面";

    private MyApplication myApp;

    private RelativeLayout mycmly;
    private XListView commentlist;
    private ArrayList<Commont> commonts;
    private CommontListAdapter adapter;
    private LinearLayout commently;
    private CheckBox btn_isnoname;
    private EditText et_comment;

    private String commentId;
    private String commentUsername;
    private String commentUserId;
    private String newsId;
    private String catId;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 0:
                    commentlist.stopLoadMore();
                    commentlist.stopRefresh();
                    adapter.notifyData(commonts);
                    break;
                case 1:
                    Toast.makeText(CommentListActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    commentlist.mFooterView.setState(XListViewFooter.STATE_LOAD_END);
                    break;
                case 6:
                    commently.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentlist);

        StatService.onPageStart(this, TAG);

        myApp = (MyApplication) getApplicationContext();
        commently = (LinearLayout) findViewById(R.id.commently);
        btn_isnoname = (CheckBox) findViewById(R.id.btn_isnoname);
        et_comment = (EditText) findViewById(R.id.et_comment);

        et_comment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("info", "onKey");
                if(keyCode == KeyEvent.KEYCODE_BACK) {
                    dismissComment();
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    StatService.onEvent(CommentListActivity.this, Event.EVENT_ID_MYCOMMONT, "回复消息-select");
                    String news_comment_private = btn_isnoname.isChecked() ? "1" : "0";
                    String content = et_comment.getText().toString().trim();
                    if(content.length() > 0) {
                        StatService.onEvent(CommentListActivity.this, Event.EVENT_ID_MYCOMMONT, "回复消息-send");
                        NetworkData.addNewsComment(myApp.user.userid, myApp.user.token, myApp.uuid,
                                newsId, catId, myApp.user.nickname, content,
                                commentUserId, commentUsername, commentId, news_comment_private, new NetworkData.NetworkCallback() {
                                    @Override
                                    public void callback(String data) {
                                        Log.i("info", data);
                                        try {
                                            JSONObject jData = new JSONObject(data);
                                        String result = jData.getString("result");
                                        if ("0".equals(result)) {
                                            StatService.onEvent(CommentListActivity.this, Event.EVENT_ID_MYCOMMONT, "回复消息-success");
                                        } else {
                                            StatService.onEvent(CommentListActivity.this, Event.EVENT_ID_MYCOMMONT, "回复消息-fail-" + jData.getString("info"));
                                        }
                                        Message msg = Message.obtain();
                                        msg.what = 2;
                                        msg.obj = jData.getString("info");
                                        handler.sendMessage(msg);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        );
                        dismissComment();
                    } else {
                        Toast.makeText(CommentListActivity.this, "请输入评论内容", Toast.LENGTH_SHORT).show();
                    }
                }
                return false;
            }
        });

        commonts = new ArrayList<>();
        adapter = new CommontListAdapter(this, commonts);
        commentlist = (XListView) findViewById(R.id.commentlist);
        commentlist.isGifShow(false);
        commentlist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    dismissComment();
                }
                detector.onTouchEvent(event);
                return false;
            }
        });

        commentlist.setAdapter(adapter);
        commentlist.setPullLoadEnable(true);
        commentlist.setPullRefreshEnable(true);
        commentlist.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                requestData((System.currentTimeMillis() / 1000) + "", true);
                StatService.onEvent(CommentListActivity.this, Event.EVENT_ID_MYCOMMONT, "列表刷新");
            }

            @Override
            public void onLoadMore() {
                if (commonts.size() > 0) {
                    StatService.onEvent(CommentListActivity.this, Event.EVENT_ID_MYCOMMONT, "加载更多");
                    Commont c = commonts.get(commonts.size() - 1);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    try {
                        Date parse = sdf.parse(c.time);
                        requestData((parse.getTime() / 1000) + "", false);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        commentlist.setOnItemClickListener(new PLA_AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(PLA_AdapterView<?> parent, View view, int position, long id) {
                StatService.onEvent(CommentListActivity.this, Event.EVENT_ID_MYCOMMONT, "列表消息-select");
                Intent intent = new Intent(CommentListActivity.this, WebActivity.class);
                Commont commont = commonts.get((int) id);
                intent.putExtra("id", commont.newsid);
                intent.putExtra("catText", myApp.catIdText.get(commont + "'"));
                intent.putExtra("catId", commont.catid);
                startActivity(intent);
            }
        });

        mycmly = (RelativeLayout) findViewById(R.id.mycmly);
        mycmly.setOnClickListener(this);

        setTitle("评论消息");

        requestData((System.currentTimeMillis() / 1000) + "", true);
    }

    public void showComment(String commentId, String commentUsername, String commentUserId, String newsId, String catId) {
        this.newsId = newsId;
        this.catId = catId;
        this.commentId = commentId;
        this.commentUserId = commentUserId;
        this.commentUsername = commentUsername;
        commently.setVisibility(View.VISIBLE);
        et_comment.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager)et_comment.getContext().getSystemService(INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et_comment, 0);
        if(commentUsername != null) {
            et_comment.setHint("@" + commentUsername);
        } else {
            et_comment.setHint("请输入评论");
        }
    }

    public void dismissComment() {
        if(commently.getVisibility() == View.VISIBLE) {
            try {
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(et_comment.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                this.commentUsername = null;
                this.commentUserId = null;
                this.commentId = null;
                et_comment.getText().clear();
                et_comment.clearFocus();
                commently.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.sendEmptyMessage(6);
        }
    }


    private void requestData(String time, final boolean isClear) {
        NetworkData.getCommentMessageList(myApp.user.userid, time, new NetworkData.NetworkCallback() {
            @Override
            public void callback(String data) {
                try {
                    JSONObject jData = new JSONObject(data);
                    JSONArray list = jData.getJSONArray("list");
                    if (list.length() == 0) {
                        handler.sendEmptyMessage(3);
                    }
                    if (isClear) {
                        commonts.clear();
                    }
                    for (int i = 0; i < list.length(); i++) {
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
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.mycmly:
                StatService.onEvent(CommentListActivity.this, Event.EVENT_ID_MYCOMMONT, "我的评论-select");
                startActivity(new Intent(CommentListActivity.this, UserCommentActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, TAG);
    }

    @Override
    public void onBackPressed() {
        Log.i("info", "onBackPressed");
        if(commently.getVisibility() == View.VISIBLE) {
            commently.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
