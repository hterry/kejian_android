package com.weiguan.kejian;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.http.NetworkData;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.weiguan.kejian.view.LinearLayoutView;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Administrator on 2016/2/1 0001.
 */
public class LoginActivity extends BaseUserActivity implements View.OnClickListener {
    private static final String TAG = "登陆页面";

    private ScrollView loginly;
    private TextView forgetpw, tv_regist;
    private Button btn_login, btn_cancel;
    private EditText telnumber, pw;
    private ImageView sinalogin, wxlogin, qqlogin, login_eye, tete;

    private MyApplication myApp;
    private String username, password;
    private ProgressDialog pDialog;
    private UMShareAPI mShareAPI;

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if(platform.equals(SHARE_MEDIA.QQ)) {
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "社交账号登录-qq-success");
                mShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.QQ, getAuthData);
            } else if(platform.equals(SHARE_MEDIA.WEIXIN)) {
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "社交账号登录-微信-success");
//                Toast.makeText(LoginActivity.this, "WEIXIN" + data.toString(), Toast.LENGTH_SHORT).show();
                mShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.WEIXIN, getAuthData);
            } else if(platform.equals(SHARE_MEDIA.SINA)) {
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "社交账号登录-微博-success");
//                Toast.makeText(LoginActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
                mShareAPI.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, getAuthData);
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            if(platform.equals(SHARE_MEDIA.QQ)) {
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "社交账号登录-qq-fail");
            } else if(platform.equals(SHARE_MEDIA.WEIXIN)) {
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "社交账号登录-微信-fail");
            } else if(platform.equals(SHARE_MEDIA.SINA)) {
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "社交账号登录-微博-fail");
            }
            Toast.makeText( getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            if(platform.equals(SHARE_MEDIA.QQ)) {
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "社交账号登录-qq-cancel");
            } else if(platform.equals(SHARE_MEDIA.WEIXIN)) {
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "社交账号登录-微信-cancel");
            } else if(platform.equals(SHARE_MEDIA.SINA)) {
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "社交账号登录-微博-fail");
            }
            Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };

    private void login(String openid, String sns, String nickname, String avatar) {
        handler.sendEmptyMessage(3);
        NetworkData.snsLogin(openid, sns, nickname, avatar, myApp.uuid, new NetworkData.NetworkCallback() {
            @Override
            public void callback(String data) {
                try {
                    Log.i("snsLogin", data);
                    LoginActivity.this.handler.sendEmptyMessage(2);
                    JSONObject jData = new JSONObject(data);
                    if ("0".equals(jData.getString("result"))) {
                        myApp.saveUser(jData.getJSONObject("userInfo"));
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Message msg = Message.obtain();
                        msg.what = 1;
                        msg.obj = jData.getString("info");
                        LoginActivity.this.handler.sendMessage(msg);
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
                login(openid, sns, nickname, avatar);
            } else if(platform == SHARE_MEDIA.WEIXIN) {
                Log.i("weixin", data.toString());
                String openid = data.get("openid");
                String sns = "weixin";
                String nickname = data.get("nickname");
                String headimgurl = data.get("headimgurl");
                login(openid, sns, nickname, headimgurl);
            } else if(platform == SHARE_MEDIA.SINA) {
                String uid = data.get("uid");
                String sns = "weibo";
                String screen_name = data.get("screen_name");
                String profile_image_url = data.get("profile_image_url");
                login(uid, sns, screen_name, profile_image_url);

                //通过URL去获取微博信息
//                String access_token = data.get("access_token");
//                String url = "https://api.weibo.com/2/users/show.json?access_token=" + access_token + "&uid=" + uid;
//                NetworkData.getWeiboData(url, new NetworkData.NetworkCallback() {
//                    @Override
//                    public void callback(String data) {
//                        try {
//                            handler.sendEmptyMessage(2);
//                            JSONObject jData = new JSONObject(data);
//                            String idstr = jData.getString("idstr");
//                            login(idstr, "weibo", screen_name, profile_image_url);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

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
                    Toast.makeText(LoginActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    try {
                        pDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        pDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginly = (ScrollView) findViewById(R.id.loginly);
        loginly.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        StatService.onPageStart(this, TAG);

        myApp = (MyApplication) getApplicationContext();
        mShareAPI = UMShareAPI.get(this);
        initView();
    }

    private void initView() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("正在登陆");

        forgetpw = (TextView) findViewById(R.id.forgetpw);
        tv_regist = (TextView) findViewById(R.id.tv_regist);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        telnumber = (EditText) findViewById(R.id.telnumber);
        pw = (EditText) findViewById(R.id.pw);
        sinalogin = (ImageView) findViewById(R.id.sinalogin);
        wxlogin = (ImageView) findViewById(R.id.wxlogin);
        qqlogin = (ImageView) findViewById(R.id.qqlogin);
        login_eye = (ImageView) findViewById(R.id.login_eye);

        login_eye.setOnClickListener(this);
        sinalogin.setOnClickListener(this);
        wxlogin.setOnClickListener(this);
        qqlogin.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        forgetpw.setOnClickListener(this);
        tv_regist.setOnClickListener(this);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.login_eye:
                int inputType = pw.getInputType();
                if(inputType == 129) {
                    pw.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    pw.setInputType(129);
                }
                break;
            case R.id.sinalogin:
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "社交账号登录-微博-select");
                SHARE_MEDIA platform = SHARE_MEDIA.SINA;
                mShareAPI.doOauthVerify(this, platform, umAuthListener);
                break;
            case R.id.wxlogin:
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "社交账号登录-微信-select");
                SHARE_MEDIA p = SHARE_MEDIA.WEIXIN;
                mShareAPI.doOauthVerify(this, p, umAuthListener);
                break;
            case R.id.qqlogin:
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "社交账号登录-qq-select");
                SHARE_MEDIA pp = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(this, pp, umAuthListener);
                break;
            case R.id.btn_login:
                StatService.onEvent(this, Event.EVENT_ID_LOGIN, "手机号登录-select");
                username = telnumber.getText().toString();
                password = pw.getText().toString();
                if(username.length() == 11) {
                    if(password.length() >=6 && password.length() <= 20) {
                        if(!pDialog.isShowing()) {
                            try {
                                pDialog.show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        StatService.onEvent(this, Event.EVENT_ID_LOGIN, "手机号登录-send");
                        NetworkData.login(username, password, myApp.uuid, new NetworkData.NetworkCallback() {
                            @Override
                            public void callback(String data) {
                                try {
                                    LoginActivity.this.handler.sendEmptyMessage(2);
                                    JSONObject jData = new JSONObject(data);
                                    String result = jData.getString("result");
                                    if ("0".equals(result)) {
                                        //登陆成功，保存信息，关闭页面，更新首页
                                        StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "手机号登录-success");
                                        myApp.saveUser(jData.getJSONObject("userInfo"));
                                        setResult(RESULT_OK);
                                        finish();
                                    } else {
                                        Message obtain = Message.obtain(handler);
                                        obtain.obj = jData.getString("info");
                                        obtain.what = 1;
                                        LoginActivity.this.handler.sendMessage(obtain);
                                        StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "手机号登录-fail-" + jData.getString("info"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
//                        Toast.makeText(LoginActivity.this, "密码长度应为6至20", Toast.LENGTH_SHORT).show();
                        Toast.makeText(LoginActivity.this, "手机号或密码不正确", Toast.LENGTH_SHORT).show();
                    }
                } else {
//                    Toast.makeText(LoginActivity.this, "手机号码长度不正确", Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, "手机号或密码不正确", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.forgetpw:
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "忘记密码-select");
                startActivityForResult(new Intent(LoginActivity.this, ForgetPWActivity.class), 1);
                break;
            case R.id.tv_regist:
                StatService.onEvent(LoginActivity.this, Event.EVENT_ID_LOGIN, "注册-select");
                startActivityForResult(new Intent(LoginActivity.this, RegistActivity.class), 1);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(pDialog.isShowing()) {
            try {
                pDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.onBackPressed();
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
        if(resultCode == 999) {
            setResult(RESULT_OK);
            finish();
            return;
        }
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
}
