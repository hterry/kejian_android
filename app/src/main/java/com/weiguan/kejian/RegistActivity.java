package com.weiguan.kejian;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.http.NetworkData;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/2/2 0002.
 */
public class RegistActivity extends BaseUserActivity implements View.OnClickListener {
    private static final String user_democracy = "http://www.iflabs.cn/app/hellojames/html/lanuch20/term.html";
    private static final String TAG = "注册页面";

    private EditText telnumber, pw, smscode;
    private ImageView login_eye;
    private CheckBox isreadprotocol;
    private Button requestsms, regist_next;
    private TextView user_proctol;

    private String moible;
    private String verifyCode;
    private String password;

    private MyApplication myApp;

    private ProgressDialog progressDialog;

    private CountDownTimer cdt = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            requestsms.setBackgroundColor(ContextCompat.getColor(RegistActivity.this, R.color.gray));
            requestsms.setTextColor(Color.WHITE);
            requestsms.setText("发 送 验 证 码 (" + (millisUntilFinished / 1000) + ")");
            requestsms.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {}
            });
        }

        @Override
        public void onFinish() {
            requestsms.setBackgroundColor(ContextCompat.getColor(RegistActivity.this, R.color.pink_divide));
            requestsms.setTextColor(Color.WHITE);
            requestsms.setText("发 送 验 证 码");
            requestsms.setOnClickListener(RegistActivity.this);
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(RegistActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
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

        telnumber = (EditText) findViewById(R.id.telnumber);
        pw = (EditText) findViewById(R.id.pw);
        smscode = (EditText) findViewById(R.id.smscode);
        login_eye = (ImageView) findViewById(R.id.login_eye);
        isreadprotocol = (CheckBox) findViewById(R.id.isreadprotocol);
        isreadprotocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatService.onEvent(RegistActivity.this, Event.EVENT_ID_REGISTER, "同意注册协议-select");
            }
        });
        requestsms = (Button) findViewById(R.id.requestsms);
        regist_next = (Button) findViewById(R.id.regist_next);

        user_proctol = (TextView) findViewById(R.id.user_proctol);
        user_proctol.setOnClickListener(this);

        regist_next.setOnClickListener(this);
        requestsms.setOnClickListener(this);
        login_eye.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在注册");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.user_proctol:
                StatService.onEvent(RegistActivity.this, Event.EVENT_ID_REGISTER, "注册协议-select");
                Intent intent = new Intent(RegistActivity.this, WebActivity.class);
                intent.putExtra("url", user_democracy);
                startActivity(intent);
                break;
            case R.id.login_eye:
                int inputType = pw.getInputType();
                if(inputType == 129) {
                    pw.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    pw.setInputType(129);
                }
                break;
            case R.id.requestsms:
                if(isreadprotocol.isChecked()) {
                    StatService.onEvent(RegistActivity.this, Event.EVENT_ID_REGISTER, "发送验证码-select");

                    moible = telnumber.getText().toString();
                    password = pw.getText().toString();

                    if (moible != null && !"".equals(moible) && moible.length() == 11) {
                        if (password != null && (password.length() >= 6 && password.length() <= 20)) {

                            StatService.onEvent(RegistActivity.this, Event.EVENT_ID_REGISTER, "检查手机号是否已注册-send");
                            NetworkData.checkMobile(moible, new NetworkData.NetworkCallback() {
                                @Override
                                public void callback(String data) {
                                    try {
                                        JSONObject jData = new JSONObject(data);
                                        String result = jData.getString("result");
                                        if ("0".equals(result)) {
                                            StatService.onEvent(RegistActivity.this, Event.EVENT_ID_REGISTER, "检查手机号是否已注册-success");
                                            StatService.onEvent(RegistActivity.this, Event.EVENT_ID_REGISTER, "发送验证码-send");
                                            //开启倒计时功能
                                            cdt.start();
                                            NetworkData.sendSMS(moible, new NetworkData.NetworkCallback() {
                                                public void callback(String data) {
                                                    try {
                                                        JSONObject jData = new JSONObject(data);
                                                        String result = jData.getString("ReturnCode");
                                                        if ("0".equals(result)) {
                                                            StatService.onEvent(RegistActivity.this, Event.EVENT_ID_REGISTER, "发送验证码-success");
                                                        } else {
                                                            StatService.onEvent(RegistActivity.this, Event.EVENT_ID_REGISTER, "发送验证码-fail-" + jData.getString("info"));
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
                                            StatService.onEvent(RegistActivity.this, Event.EVENT_ID_REGISTER, "检查手机号是否已注册-fail-" + jData.getString("info"));
                                            handler.sendMessage(obtain);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
//                                          Toast.makeText(RegistActivity.this, "密码长度应为6至20", Toast.LENGTH_SHORT).show();
                            Toast.makeText(RegistActivity.this, "手机号或密码不正确", Toast.LENGTH_SHORT).show();
                        }
                    } else {
//                                      Toast.makeText(RegistActivity.this, "手机号码长度不正确", Toast.LENGTH_SHORT).show();
                        Toast.makeText(RegistActivity.this, "手机号或密码不正确", Toast.LENGTH_SHORT).show();

                    }
                }
                break;
            case R.id.regist_next:
                if(isreadprotocol.isChecked()) {
                    moible = telnumber.getText().toString();
                    password = pw.getText().toString();
                    verifyCode = smscode.getText().toString();
                    StatService.onEvent(RegistActivity.this, Event.EVENT_ID_REGISTER, "下一步-select");
                    if(verifyCode.length() == 6) {
                        if(moible != null && !"".equals(moible) && moible.length() == 11) {
                            if(password != null && (password.length() >= 6 && password.length() <= 20)) {
                                if(!progressDialog.isShowing()) {
                                    progressDialog.show();
                                }
                                StatService.onEvent(RegistActivity.this, Event.EVENT_ID_REGISTER, "下一步-send");
                                NetworkData.regist(moible, password, myApp.uuid, verifyCode, new NetworkData.NetworkCallback() {
                                    @Override
                                    public void callback(String data) {
                                        try {
                                            handler.sendEmptyMessage(2);
                                            JSONObject jData = new JSONObject(data);
                                            String result = jData.getString("result");
                                            if ("0".equals(result)) {
                                                StatService.onEvent(RegistActivity.this, Event.EVENT_ID_REGISTER, "下一步-success");
                                                myApp.saveUser(jData.getJSONObject("userInfo"));
                                                startActivityForResult(new Intent(RegistActivity.this, SetUserPicActivity.class), 0);
                                                setResult(999);
                                            } else {
                                                Message obtain = Message.obtain();
                                                obtain.obj = jData.getString("info");
                                                obtain.what = 1;
                                                StatService.onEvent(RegistActivity.this, Event.EVENT_ID_REGISTER, "下一步-fail-" + jData.getString("info"));
                                                handler.sendMessage(obtain);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegistActivity.this, "密码长度应为6至20", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegistActivity.this, "手机号码长度不正确", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegistActivity.this, "验证码长度不正确", Toast.LENGTH_SHORT).show();
                    }
                }
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
        switch(resultCode) {
            case RESULT_OK:
                finish();
                break;
        }
    }
}
