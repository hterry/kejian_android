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
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.http.NetworkData;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/2/2 0002.
 */
public class ForgetPWActivity extends BaseUserActivity implements View.OnClickListener {
    private static final String TAG = "忘记密码页面";

    private EditText findtel, etverifycode;
    private ImageView verifystatus;
    private Button nextfindstep, requestverify;

    private String tel, verifyCode;

    private boolean isVerifySuccess = false;

    private CountDownTimer cdt = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            requestverify.setBackgroundColor(ContextCompat.getColor(ForgetPWActivity.this, R.color.gray));
            requestverify.setTextColor(Color.WHITE);
            requestverify.setText("发送验证码 (" + (millisUntilFinished / 1000) + ")");
            requestverify.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {}
            });
        }

        @Override
        public void onFinish() {
            requestverify.setBackgroundColor(ContextCompat.getColor(ForgetPWActivity.this, R.color.pink_divide));
            requestverify.setTextColor(Color.WHITE);
            requestverify.setText("发 送 验 证 码");
            requestverify.setOnClickListener(ForgetPWActivity.this);
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(ForgetPWActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
//                    isVerifySuccess = true;
                    verifystatus.setVisibility(View.GONE);
                    break;
                case 3:
                    verifystatus.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpw);

        StatService.onPageStart(this, TAG);

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

        findtel = (EditText) findViewById(R.id.findtel);
        etverifycode = (EditText) findViewById(R.id.etverifycode);
        verifystatus = (ImageView) findViewById(R.id.verifystatus);
        nextfindstep = (Button) findViewById(R.id.nextfindstep);
        requestverify = (Button) findViewById(R.id.requestverify);

        nextfindstep.setOnClickListener(this);
        requestverify.setOnClickListener(this);

        etverifycode.addTextChangedListener(new TextWatcher() {
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

//        etverifycode.addTextChangedListener(new TextWatcher() {
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
//                String content = s.toString();
//                if(content.length() == 6) {
//                    verifyCode = etverifycode.getText().toString();
//                    if(verifyCode.length() > 0 && tel != null && tel.length() == 11) {
//                        StatService.onEvent(ForgetPWActivity.this, Event.EVENT_ID_FORGETPW, "确认-send");
//                        NetworkData.verifySMS(tel, verifyCode, new NetworkData.NetworkCallback() {
//                            @Override
//                            public void callback(String data) {
//                                try {
//                                    JSONObject jData = new JSONObject(data);
//                                    String result = jData.getString("result");
//                                    if("0".equals(result)) {
//                                        //成功
//                                        StatService.onEvent(ForgetPWActivity.this, Event.EVENT_ID_FORGETPW, "确认-success");
//                                        handler.sendEmptyMessage(2);
//                                    } else {
//                                        StatService.onEvent(ForgetPWActivity.this, Event.EVENT_ID_FORGETPW, "确认-fail-" + jData.getString("info"));
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
            case R.id.requestverify:
                StatService.onEvent(ForgetPWActivity.this, Event.EVENT_ID_FORGETPW, "发送验证码-select");
                tel = findtel.getText().toString();
                if(tel.length() == 11) {
                    StatService.onEvent(ForgetPWActivity.this, Event.EVENT_ID_FORGETPW, "检查手机号是否已注册-send");
                    NetworkData.checkMobile(tel, new NetworkData.NetworkCallback() {
                        @Override
                        public void callback(String data) {
                            try {
                                JSONObject jData = new JSONObject(data);
                                String result = jData.getString("result");
                                if ("1".equals(result)) {
                                    StatService.onEvent(ForgetPWActivity.this, Event.EVENT_ID_FORGETPW, "检查手机号是否已注册-success");
                                    cdt.start();
                                    StatService.onEvent(ForgetPWActivity.this, Event.EVENT_ID_FORGETPW, "发送验证码-send");
                                    NetworkData.sendSMS(tel, new NetworkData.NetworkCallback() {
                                        @Override
                                        public void callback(String data) {
                                            try {
                                                JSONObject jData = new JSONObject(data);
                                                String result = jData.getString("ReturnCode");
                                                if ("0".equals(result)) {
                                                    //成功
                                                    StatService.onEvent(ForgetPWActivity.this, Event.EVENT_ID_FORGETPW, "发送验证码-success");
                                                } else {
                                                    //验证码错误
                                                    StatService.onEvent(ForgetPWActivity.this, Event.EVENT_ID_FORGETPW, "发送验证码-fail-" + jData.getString("info"));
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    Message obtain = Message.obtain();
                                    obtain.obj = "手机号未被注册";
                                    obtain.what = 1;
                                    handler.sendMessage(obtain);
                                    StatService.onEvent(ForgetPWActivity.this, Event.EVENT_ID_FORGETPW, "检查手机号是否已注册-fail-" + jData.getString("info"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ForgetPWActivity.this, "手机号码长度不正确", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nextfindstep:
                StatService.onEvent(ForgetPWActivity.this, Event.EVENT_ID_FORGETPW, "确认-select");
                if(isVerifySuccess) {
                    Intent intent = new Intent(ForgetPWActivity.this, ResetPWActivity.class);
                    intent.putExtra("tel", tel);
                    intent.putExtra("verifyCode", verifyCode);
                    startActivityForResult(intent, 0);
                } else {
                    tel = findtel.getText().toString();
                    verifyCode = etverifycode.getText().toString();
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(etverifycode.getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    if(tel != null && tel.length() == 11) {
                        if(verifyCode.length() == 6) {
                            StatService.onEvent(ForgetPWActivity.this, Event.EVENT_ID_FORGETPW, "确认-send");
                            NetworkData.verifySMS(tel, verifyCode, new NetworkData.NetworkCallback() {
                                @Override
                                public void callback(String data) {
                                    try {
                                        JSONObject jData = new JSONObject(data);
                                        String result = jData.getString("result");
                                        if("0".equals(result)) {
                                            //成功
                                            handler.sendEmptyMessage(3);
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    nextfindstep.setOnClickListener(ForgetPWActivity.this);
                                                    Intent intent = new Intent(ForgetPWActivity.this, ResetPWActivity.class);
                                                    intent.putExtra("tel", tel);
                                                    intent.putExtra("verifyCode", verifyCode);
                                                    startActivityForResult(intent, 0);
                                                    dismissStatus();
                                                }
                                            }, 1000);
                                            nextfindstep.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {

                                                }
                                            });
                                        } else {
                                            //验证码错误
                                            Message obtain = Message.obtain();
                                            obtain.obj = jData.getString("info");
                                            obtain.what = 1;
                                            handler.sendMessage(obtain);
                                            StatService.onEvent(ForgetPWActivity.this, Event.EVENT_ID_FORGETPW, "确认-fail-" + jData.getString("info"));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ForgetPWActivity.this, "验证码长度不正确", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ForgetPWActivity.this, "手机号码长度不正确", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void dismissStatus() {
        handler.sendEmptyMessageDelayed(2, 1500);
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
