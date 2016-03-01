package com.weiguan.kejian;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.commons.MyGesture;
import com.weiguan.kejian.http.NetworkData;
import com.weiguan.kejian.view.CircleImageView;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2016/2/4 0004.
 */
public class UserCenterActivity extends BaseUserActivity implements View.OnClickListener {
    private static final String TAG = "个人信息页面";

    private CircleImageView user_logo;
    private TextView user_nickname;
    private TextView user_intro;
    private TextView user_lv;
    private TextView user_ex;
    private ImageView sina, weixin, qq;
    private TextView logout;

    private Button dialog_sure, dialog_cancel;

    private MyApplication myApp;

    private Dialog d;
    private ProgressDialog progressDialog;

    private UMShareAPI mShareAPI;

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if(platform.equals(SHARE_MEDIA.QQ)) {
                mShareAPI.getPlatformInfo(UserCenterActivity.this, SHARE_MEDIA.QQ, getAuthData);
            } else if(platform.equals(SHARE_MEDIA.WEIXIN)) {
                mShareAPI.getPlatformInfo(UserCenterActivity.this, SHARE_MEDIA.WEIXIN, getAuthData);
            } else if(platform.equals(SHARE_MEDIA.SINA)) {
                mShareAPI.getPlatformInfo(UserCenterActivity.this, SHARE_MEDIA.SINA, getAuthData);
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };

    private void bind(final String openid, final String sns, String nickname, String avatar) {
        try {
            showDD();
        } catch (Exception e) {
            e.printStackTrace();
        }
        NetworkData.addSnsLogin(myApp.user.userid, myApp.user.token, myApp.uuid, openid, sns, nickname, avatar, new NetworkData.NetworkCallback() {
            @Override
            public void callback(String data) {
                try {
                    handler.sendEmptyMessage(2);
                    JSONObject jData = new JSONObject(data);
                    if("0".equals(jData.getString("result"))) {
                        if(sns.equals("qq")) {
                            StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "qq-bind-success");
                            myApp.user.qq_openid = openid;
                        } else if(sns.equals("weibo")) {
                            StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微博-bind-success");
                            myApp.user.weibo_openid = openid;
                        } else if(sns.equals("weixin")) {
                            StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微信-bind-success");
                            myApp.user.weixin_openid = openid;
                        }
                        myApp.saveUser();
                        handler.sendEmptyMessage(3);
                    } else {
                        Message obtain = Message.obtain();
                        obtain.obj = jData.getString("info");
                        obtain.what = 1;
                        UserCenterActivity.this.handler.sendMessage(obtain);
                        if(sns.equals("qq")) {
                            StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "qq-bind-fail");
                        } else if(sns.equals("weibo")) {
                            StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微博-bind-fail");
                        } else if(sns.equals("weixin")) {
                            StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微信-bind-fail");
                        }
                        handler.sendEmptyMessage(2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private UMAuthListener getAuthData = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if(platform == SHARE_MEDIA.QQ) {
                String openid = data.get("openid");
                String nickname = data.get("screen_name");
                String avatar = data.get("profile_image_url");
                String sns = "qq";
                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "qq-bind-send");
                bind(openid, sns, nickname, avatar);
            } else if(platform == SHARE_MEDIA.WEIXIN) {
                String openid = data.get("openid");
                String sns = "weixin";
                String nickname = data.get("nickname");
                String headimgurl = data.get("headimgurl");
                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微信-bind-send");
                bind(openid, sns, nickname, headimgurl);
            } else if(platform == SHARE_MEDIA.SINA) {
                String uid = data.get("uid");
                String sns = "weibo";
                String screen_name = data.get("screen_name");
                String profile_image_url = data.get("profile_image_url");
                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微博-bind-send");
                bind(uid, sns, screen_name, profile_image_url);
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(UserCenterActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case 3:
                    initView();
                    updateView();
                    break;
                case 4:
                    progressDialog.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usercenter);

        StatService.onPageStart(this, TAG);

        myApp = (MyApplication) getApplicationContext();
        mShareAPI = UMShareAPI.get(this);
        initView();
        updateView();
    }

    private void updateView() {
        myApp.displayImageUserLogo(myApp.user.avatar, user_logo);
        user_nickname.setText(myApp.user.nickname);
        user_intro.setText(myApp.user.des);
    }

    private void initView() {
        setTitle("用户信息");
        user_logo = (CircleImageView) findViewById(R.id.user_logo);
        user_nickname = (TextView) findViewById(R.id.user_nickname);
        user_intro = (TextView) findViewById(R.id.user_intro);
        user_lv = (TextView) findViewById(R.id.user_lv);
        user_ex = (TextView) findViewById(R.id.user_ex);
        logout = (TextView) findViewById(R.id.logout);
        sina = (ImageView) findViewById(R.id.sina);
        weixin = (ImageView) findViewById(R.id.weixin);
        qq = (ImageView) findViewById(R.id.qq);

        logout.setOnClickListener(this);
        user_logo.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("请稍后");
        progressDialog.setCancelable(true);
        d = new Dialog(this, R.style.whitedialog);
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_cancelbind, null);
        dialog_cancel = (Button) inflate.findViewById(R.id.dialog_cancel);
        dialog_sure = (Button) inflate.findViewById(R.id.dialog_sure);
        d.setContentView(inflate);
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        if(myApp.user.qq_openid != null && !"".equals(myApp.user.qq_openid)) {
            qq.setImageResource(R.drawable.login_qq);
            qq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!d.isShowing()) {
                        d.show();
                        dialog_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                d.dismiss();
                                unbind("qq");
                            }
                        });
                    }
                }
            });
        } else {
            qq.setImageResource(R.drawable.qq_unlogin);
            qq.setOnClickListener(this);
        }
        if(myApp.user.weibo_openid != null && !"".equals(myApp.user.weibo_openid)) {
            sina.setImageResource(R.drawable.login_sina);

            sina.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!d.isShowing()) {
                        d.show();
                        dialog_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                d.dismiss();
                                unbind("weibo");
                            }
                        });
                    }
                }
            });
        } else {
            sina.setImageResource(R.drawable.sina_unlogin);
            sina.setOnClickListener(this);
        }
        if(myApp.user.weixin_openid != null && !"".equals(myApp.user.weixin_openid)) {
            weixin.setImageResource(R.drawable.login_weixin);
            weixin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!d.isShowing()) {
                        d.show();
                        dialog_sure.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                d.dismiss();
                                unbind("weixin");
                            }
                        });
                    }
                }
            });
        } else {
            weixin.setImageResource(R.drawable.weixin_unlogin);
            weixin.setOnClickListener(this);
        }
    }

    private void unbind(final String sns) {
        String openid = null;
        if(sns.equals("qq")) {
            StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "qq-unbind-select");
            StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "qq-unbind-send");
            openid = myApp.user.qq_openid;
        } else if(sns.equals("weibo")) {
            StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微博-unbind-select");
            StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微博-unbind-send");
            openid = myApp.user.weibo_openid;
            Log.i("weibo_openid", openid);
        } else if(sns.equals("weixin")) {
            StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微信-unbind-select");
            StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微信-unbind-send");
            openid = myApp.user.weixin_openid;
        }
        if(openid != null) {
            try {
                showDD();
            } catch (Exception e) {
                e.printStackTrace();
            }
            NetworkData.removeSnsLogin(myApp.user.userid, myApp.user.token, myApp.uuid, openid, sns, new NetworkData.NetworkCallback() {
                @Override
                public void callback(String data) {
                    try {
                        handler.sendEmptyMessage(2);
                        JSONObject jData = new JSONObject(data);
                        Log.i("sns_unbind", data);
                        if("0".equals(jData.getString("result"))) {
                            if(sns.equals("qq")) {
                                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "qq-unbind-success");
                                myApp.user.qq_openid = "";
                            } else if(sns.equals("weibo")) {
                                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微博-unbind-success");
                                myApp.user.weibo_openid = "";
                            } else if(sns.equals("weixin")) {
                                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微信-unbind-success");
                                myApp.user.weixin_openid = "";
                            }
                            myApp.saveUser();
                            handler.sendEmptyMessage(3);
                        } else {
                            Message obtain = Message.obtain();
                            obtain.obj = jData.getString("info");
                            obtain.what = 1;
                            UserCenterActivity.this.handler.sendMessage(obtain);
                            if(sns.equals("qq")) {
                                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "qq-unbind-fail");
                            } else if(sns.equals("weibo")) {
                                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微博-unbind-fail");
                            } else if(sns.equals("weixin")) {
                                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微信-unbind-fail");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void showDD() {
        handler.sendEmptyMessage(4);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.sina:
                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微博-bind-select");
                SHARE_MEDIA platform = SHARE_MEDIA.SINA;
                mShareAPI.doOauthVerify(UserCenterActivity.this, platform, umAuthListener);
                break;
            case R.id.weixin:
                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "微信-bind-select");
                SHARE_MEDIA p = SHARE_MEDIA.WEIXIN;
                mShareAPI.doOauthVerify(UserCenterActivity.this, p, umAuthListener);
                break;
            case R.id.qq:
                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "qq-bind-select");
                SHARE_MEDIA pp = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(UserCenterActivity.this, pp, umAuthListener);
                break;
            case R.id.logout:
                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "退出账号-select");
                myApp.clearUser();
                setResult(RESULT_OK);
                finish();
                break;
            case R.id.user_logo:
                StatService.onEvent(UserCenterActivity.this, Event.EVENT_ID_USERINFO, "确认-select");
                Intent intent = new Intent(UserCenterActivity.this, ModifyInfoActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            updateView();
        }
    }
}
