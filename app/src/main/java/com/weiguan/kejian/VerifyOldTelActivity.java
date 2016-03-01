package com.weiguan.kejian;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
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
public class VerifyOldTelActivity extends BaseUserActivity implements View.OnClickListener {
    private static final String TAG = "发送验证码到旧手机页面";

    private TextView cip_tel;
    private Button sendsms;
    private EditText inputverify;
    private ImageView verifystatus;
    private Button ns;

    private MyApplication myApp;

    private boolean isVerifySuccess;
    private String verifyCode;

    private CountDownTimer cdt = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            sendsms.setBackgroundColor(ContextCompat.getColor(VerifyOldTelActivity.this, R.color.gray));
            sendsms.setTextColor(Color.WHITE);
            sendsms.setText("发送验证码 (" + (millisUntilFinished / 1000) + ")");
            sendsms.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {}
            });
        }

        @Override
        public void onFinish() {
            sendsms.setBackgroundColor(ContextCompat.getColor(VerifyOldTelActivity.this, R.color.pink_divide));
            sendsms.setTextColor(Color.WHITE);
            sendsms.setText("发 送 验 证 码");
            sendsms.setOnClickListener(VerifyOldTelActivity.this);
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(VerifyOldTelActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    verifystatus.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    verifystatus.setVisibility(View.GONE);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyoldtel);

        StatService.onPageStart(this, TAG);

        myApp = (MyApplication) getApplicationContext();
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

        cip_tel = (TextView) findViewById(R.id.cip_tel);
        sendsms = (Button) findViewById(R.id.sendsms);
        ns = (Button) findViewById(R.id.ns);
        inputverify = (EditText) findViewById(R.id.inputverify);
        verifystatus = (ImageView) findViewById(R.id.verifystatus);

        cip_tel.setText(myApp.user.username.substring(0, 3) + "***" + myApp.user.username.substring(myApp.user.username.length() - 3));

        ns.setOnClickListener(this);
        sendsms.setOnClickListener(this);

        inputverify.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() != 6) {
                    verifystatus.setVisibility(View.GONE);
                }
            }
        });

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
//                if(c.length() == 6) {
//                    StatService.onEvent(VerifyOldTelActivity.this, Event.EVENT_ID_VERIFYOLDTEL, "确认-send");
//                    NetworkData.verifySMS(myApp.user.username, c, new NetworkData.NetworkCallback() {
//                        @Override
//                        public void callback(String data) {
//                            try {
//                                JSONObject jData = new JSONObject(data);
//                                if ("0".equals(jData.getString("result"))) {
//                                    StatService.onEvent(VerifyOldTelActivity.this, Event.EVENT_ID_VERIFYOLDTEL, "确认-success");
//                                    isVerifySuccess = true;
//                                    handler.sendEmptyMessage(2);
//                                } else {
//                                    StatService.onEvent(VerifyOldTelActivity.this, Event.EVENT_ID_VERIFYOLDTEL, "确认-fail-" + jData.getString("info"));
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.sendsms:
                StatService.onEvent(VerifyOldTelActivity.this, Event.EVENT_ID_VERIFYOLDTEL, "发送验证码-select");
                StatService.onEvent(VerifyOldTelActivity.this, Event.EVENT_ID_VERIFYOLDTEL, "发送验证码-send");
                cdt.start();
                NetworkData.sendSMS(myApp.user.username, new NetworkData.NetworkCallback() {
                    @Override
                    public void callback(String data) {
                        try {
                            JSONObject jData = new JSONObject(data);
                            if("0".equals(jData.getString("result"))) {
                                StatService.onEvent(VerifyOldTelActivity.this, Event.EVENT_ID_VERIFYOLDTEL, "发送验证码-success");
                            } else {
                                StatService.onEvent(VerifyOldTelActivity.this, Event.EVENT_ID_VERIFYOLDTEL, "发送验证码-fail-" + jData.getString("info"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.ns:
                StatService.onEvent(VerifyOldTelActivity.this, Event.EVENT_ID_VERIFYOLDTEL, "确认-select");
                verifyCode = inputverify.getText().toString();
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(inputverify.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                if(isVerifySuccess) {
                    Intent intent = new Intent(VerifyOldTelActivity.this, SetNewTelActivity.class);
                    intent.putExtra("verifyCode", verifyCode);
                    startActivityForResult(intent, 1);
                } else {
                    if(verifyCode.length() == 6) {
                        StatService.onEvent(VerifyOldTelActivity.this, Event.EVENT_ID_VERIFYOLDTEL, "确认-send");
                        NetworkData.verifySMS(myApp.user.username, verifyCode, new NetworkData.NetworkCallback() {
                            @Override
                            public void callback(String data) {
                                try {
                                    JSONObject jData = new JSONObject(data);
                                    if("0".equals(jData.getString("result"))) {
                                        StatService.onEvent(VerifyOldTelActivity.this, Event.EVENT_ID_VERIFYOLDTEL, "确认-success");
                                        handler.sendEmptyMessage(2);
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ns.setOnClickListener(VerifyOldTelActivity.this);
                                                dismissStatus();
                                                Intent intent = new Intent(VerifyOldTelActivity.this, SetNewTelActivity.class);
                                                intent.putExtra("verifyCode", verifyCode);
                                                startActivityForResult(intent, 1);
                                            }
                                        }, 1000);
                                        ns.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                            }
                                        });
                                    } else {
                                        Message obtain = Message.obtain();
                                        obtain.obj = jData.getString("info");
                                        obtain.what = 1;
                                        handler.sendMessage(obtain);
                                        StatService.onEvent(VerifyOldTelActivity.this, Event.EVENT_ID_VERIFYOLDTEL, "确认-fail-" + jData.getString("info"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(VerifyOldTelActivity.this, "验证码长度不正确", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void dismissStatus() {
        handler.sendEmptyMessageDelayed(3, 1500);
//        verifystatus.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode) {
            case RESULT_OK:
                finish();
                break;
        }
    }
}
