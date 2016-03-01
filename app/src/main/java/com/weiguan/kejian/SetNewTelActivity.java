package com.weiguan.kejian;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.http.NetworkData;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/2/4 0004.
 */
public class SetNewTelActivity extends BaseUserActivity implements View.OnClickListener {
    private EditText et_newtel;
    private Button sendsms;
    private EditText inputverify;
    private ImageView verifystatus;
    private Button setcomplete;
    private TextView setting_tv;

    private MyApplication myApp;

    private boolean isVerifySuccess;
    private ProgressDialog progressDialog;

    private String newTel, verifyCode, verifyCode2;

    private String content;

    private CountDownTimer cdt = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            sendsms.setBackgroundColor(ContextCompat.getColor(SetNewTelActivity.this, R.color.gray));
            sendsms.setTextColor(Color.WHITE);
            sendsms.setText("发送验证码 (" + (millisUntilFinished / 1000) + ")");
            sendsms.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {}
            });
        }

        @Override
        public void onFinish() {
            sendsms.setBackgroundColor(ContextCompat.getColor(SetNewTelActivity.this, R.color.pink_divide));
            sendsms.setTextColor(Color.WHITE);
            sendsms.setText("发 送 验 证 码");
            sendsms.setOnClickListener(SetNewTelActivity.this);
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(SetNewTelActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    verifystatus.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    progressDialog.dismiss();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setnewtel);
        myApp = (MyApplication) getApplicationContext();
        verifyCode = getIntent().getStringExtra("verifyCode");
        initView();
    }

    private void initView() {
        findViewById(R.id.scview).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        setting_tv = (TextView) findViewById(R.id.setting_tv);
        et_newtel = (EditText) findViewById(R.id.et_newtel);
        inputverify = (EditText) findViewById(R.id.inputverify);
        verifystatus = (ImageView) findViewById(R.id.verifystatus);
        sendsms = (Button) findViewById(R.id.sendsms);
        setcomplete = (Button) findViewById(R.id.setcomplete);

        setcomplete.setOnClickListener(this);
        sendsms.setOnClickListener(this);

        if("".equals(myApp.user.mobile) || "".equals(myApp.user.username) || verifyCode == null) {
            content = "绑定手机页面";
            StatService.onPageStart(this, content);
            setting_tv.setText("绑定手机号码");
        } else {
            content = "修改手机页面";
            StatService.onPageStart(this, content);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在修改");

//        inputverify.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String c = s.toString();
//                if (c.length() == 6) {
//                    if(newTel != null && newTel.length() == 11) {
//                        if("绑定手机页面".equals(content)) {
//                            StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "发送验证码-send");
//                        } else if("修改手机页面".equals(content)) {
//                            StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "发送验证码-send");
//                        }
//                        NetworkData.verifySMS(newTel, c, new NetworkData.NetworkCallback() {
//                            @Override
//                            public void callback(String data) {
//                                try {
//                                    JSONObject jData = new JSONObject(data);
//                                    if("0".equals(jData.getString("result"))) {
//                                        isVerifySuccess = true;
//                                        handler.sendEmptyMessage(2);
//                                        if("绑定手机页面".equals(content)) {
//                                            StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "发送验证码-success");
//                                        } else if("修改手机页面".equals(content)) {
//                                            StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "发送验证码-success");
//                                        }
//                                    } else {
//                                        if("绑定手机页面".equals(content)) {
//                                            StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "发送验证码-fail" + jData.getString("info"));
//                                        } else if("修改手机页面".equals(content)) {
//                                            StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "发送验证码-fail" + jData.getString("info"));
//                                        }
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.sendsms:
                if("绑定手机页面".equals(content)) {
                    StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "发送验证码-select");
                } else if("修改手机页面".equals(content)) {
                    StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "发送验证码-select");
                }
                newTel = et_newtel.getText().toString();
                if(newTel.length() == 11) {
                    if("绑定手机页面".equals(content)) {
                        StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "检查手机号是否已注册-send");
                    } else if("修改手机页面".equals(content)) {
                        StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "检查手机号是否已注册-send");
                    }
                    NetworkData.checkMobile(newTel, new NetworkData.NetworkCallback() {
                        @Override
                        public void callback(String data) {
                            try {
                                JSONObject jData = new JSONObject(data);
                                String result = jData.getString("result");
                                if ("0".equals(result)) {
                                    cdt.start();
                                    if ("绑定手机页面".equals(content)) {
                                        StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "发送验证码-send");
                                        StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "检查手机号是否已注册-success");
                                    } else if ("修改手机页面".equals(content)) {
                                        StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "检查手机号是否已注册-success");
                                        StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "发送验证码-send");
                                    }
                                    NetworkData.sendSMS(newTel, new NetworkData.NetworkCallback() {
                                        @Override
                                        public void callback(String data) {
                                            try {
                                                JSONObject jData = new JSONObject(data);
                                                if ("0".equals(jData.getString("ReturnCode"))) {
                                                    if ("绑定手机页面".equals(content)) {
                                                        StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "发送验证码-success");
                                                    } else if ("修改手机页面".equals(content)) {
                                                        StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "发送验证码-success");
                                                    }
                                                } else {
                                                    if ("绑定手机页面".equals(content)) {
                                                        StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "发送验证码-fail" + jData.getString("info"));
                                                    } else if ("修改手机页面".equals(content)) {
                                                        StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "发送验证码-fail" + jData.getString("info"));
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    Message obtain = Message.obtain();
                                    obtain.obj = jData.getString("info");
                                    obtain.what = 1;
                                    handler.sendMessage(obtain);
                                    if ("绑定手机页面".equals(content)) {
                                        StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "检查手机号是否已注册-fail-" + jData.getString("info"));
                                    } else if ("修改手机页面".equals(content)) {
                                        StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "检查手机号是否已注册-fail-" + jData.getString("info"));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SetNewTelActivity.this, "手机号码长度不正确", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.setcomplete:
                if("绑定手机页面".equals(content)) {
                    StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "确认-select");
                } else if("修改手机页面".equals(content)) {
                    StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "确认-select");
                }

                newTel = et_newtel.getText().toString();

                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(et_newtel.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                if(newTel.length() == 11) {
                    verifyCode2 = inputverify.getText().toString();
                    if(verifyCode2.length() == 6) {
                        Log.i("verifyCode", verifyCode + "--");
                        if(!progressDialog.isShowing())
                            progressDialog.show();
                        if(verifyCode != null) {
                            StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "确认-send");
                            NetworkData.updateUserMobile(myApp.user.userid, myApp.user.token, myApp.uuid, newTel, verifyCode2, myApp.user.mobile, verifyCode, new NetworkData.NetworkCallback() {
                                @Override
                                public void callback(String data) {
                                    try {
                                        handler.sendEmptyMessage(3);
                                        JSONObject jData = new JSONObject(data);
                                        if ("0".equals(jData.getString("result"))) {
                                            StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "确认-success");
                                            myApp.user.username = newTel;
                                            setResult(RESULT_OK);
                                            handler.sendEmptyMessage(2);
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                }
                                            }, 1000);
                                            setcomplete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                }
                                            });
                                        } else {
                                            Message obtain = Message.obtain();
                                            obtain.obj = jData.getString("info");
                                            obtain.what = 1;
                                            handler.sendMessage(obtain);
                                            StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_UPDATETEL, "确认-fail-" + jData.getString("info"));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "确认-send");
                            NetworkData.updateUserMobile(myApp.user.userid, myApp.user.token, myApp.uuid, newTel, verifyCode2, "", "", new NetworkData.NetworkCallback() {
                                @Override
                                public void callback(String data) {
                                    try {
                                        handler.sendEmptyMessage(3);
                                        JSONObject jData = new JSONObject(data);
                                        if ("0".equals(jData.getString("result"))) {
                                            StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "确认-success");
                                            myApp.user.username = newTel;
                                            myApp.user.mobile = newTel;
                                            myApp.saveUser();
                                            setResult(RESULT_OK);
                                            handler.sendEmptyMessage(2);
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                }
                                            }, 1000);
                                            setcomplete.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                }
                                            });
                                        } else {
                                            Message obtain = Message.obtain();
                                            obtain.obj = jData.getString("info");
                                            obtain.what = 1;
                                            handler.sendMessage(obtain);
                                            StatService.onEvent(SetNewTelActivity.this, Event.EVENT_ID_BINDTEL, "确认-fail-" + jData.getString("info"));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(SetNewTelActivity.this, "验证码长度不正确", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SetNewTelActivity.this, "手机号码长度不正确", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, content);
    }
}
