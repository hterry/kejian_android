package com.weiguan.kejian;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
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
public class ResetPWActivity extends BaseUserActivity implements View.OnClickListener {
    private static final String TAG = "找回密码页面";

    private EditText currentpw;
    private ImageView reset_eye;
    private Button ensurereset;

    private String tel, verifyCode, pw;

    private ProgressDialog progressDialog;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(ResetPWActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(ResetPWActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_resetpw);

        StatService.onPageStart(this, TAG);

        getData();
        initView();
    }

    private void getData() {
        tel = getIntent().getStringExtra("tel");
        verifyCode = getIntent().getStringExtra("verifyCode");
    }

    private void initView() {
        findViewById(R.id.scview).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        currentpw = (EditText) findViewById(R.id.currentpw);
        reset_eye = (ImageView) findViewById(R.id.reset_eye);
        ensurereset = (Button) findViewById(R.id.ensurereset);

        ensurereset.setOnClickListener(this);
        reset_eye.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在修改");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.reset_eye:
                if(currentpw.getInputType() == 129) {
                    currentpw.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    currentpw.setInputType(129);
                }
                break;
            case R.id.ensurereset:
                StatService.onEvent(ResetPWActivity.this, Event.EVENT_ID_CHANGEPW_F, "确认-select");
                pw = currentpw.getText().toString();
                if(pw.length() >= 6 && pw.length()<= 20) {
                    if(!progressDialog.isShowing())
                        progressDialog.show();

                    StatService.onEvent(ResetPWActivity.this, Event.EVENT_ID_CHANGEPW_F, "确认-send");

                    NetworkData.findUserPassword(tel, verifyCode, pw, new NetworkData.NetworkCallback() {
                        @Override
                        public void callback(String data) {
                            try {
                                handler.sendEmptyMessage(3);
                                JSONObject jData = new JSONObject(data);
                                String result = jData.getString("result");
                                if ("0".equals(result)) {
                                    //成功
                                    StatService.onEvent(ResetPWActivity.this, Event.EVENT_ID_CHANGEPW_F, "确认-success");
                                    handler.sendEmptyMessage(2);
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    //验证码错误
                                    Message obtain = Message.obtain();
                                    obtain.obj = jData.getString("info");
                                    obtain.what = 1;
                                    handler.sendMessage(obtain);
                                    StatService.onEvent(ResetPWActivity.this, Event.EVENT_ID_CHANGEPW_F, "确认-fail-" + jData.getString("info"));

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    Toast.makeText(ResetPWActivity.this, "密码长度应为6至20", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, TAG);
    }
}
