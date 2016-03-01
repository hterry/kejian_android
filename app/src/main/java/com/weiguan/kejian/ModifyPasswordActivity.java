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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.http.NetworkData;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/2/4 0004.
 */
public class ModifyPasswordActivity extends BaseUserActivity implements View.OnClickListener {
    private EditText et_oldpw, et_newpw, et_newpwreapt;
    private ImageView mpw_e1, mpw_e2, mpw_e3;
    private Button setcomplete;
    private TextView sp_hint;
    private RelativeLayout oldrelayout;

    private MyApplication myApp;

    private ProgressDialog progressDialog;

    private String oldPW, newPW, newPWreapt;

    private String content;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(ModifyPasswordActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_modifypw);
        myApp = (MyApplication) getApplicationContext();
        initeView();
    }

    private void initeView() {
        findViewById(R.id.scview).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        sp_hint = (TextView) findViewById(R.id.sp_hint);
        oldrelayout = (RelativeLayout) findViewById(R.id.oldrelayout);

        et_oldpw = (EditText) findViewById(R.id.et_oldpw);
        et_newpw = (EditText) findViewById(R.id.et_newpw);
        et_newpwreapt = (EditText) findViewById(R.id.et_newpwreapt);
        mpw_e1 = (ImageView) findViewById(R.id.mpw_e1);
        mpw_e2 = (ImageView) findViewById(R.id.mpw_e2);
        mpw_e3 = (ImageView) findViewById(R.id.mpw_e3);
        setcomplete = (Button) findViewById(R.id.setcomplete);

        setcomplete.setOnClickListener(this);
        mpw_e1.setOnClickListener(this);
        mpw_e2.setOnClickListener(this);
        mpw_e3.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在修改");

        if("0".equals(myApp.user.hasPassword)) {
            content = "创建密码页面";
            StatService.onPageStart(this, content);
            oldrelayout.setVisibility(View.GONE);
            sp_hint.setVisibility(View.VISIBLE);
        } else {
            content = "修改密码页面";
            StatService.onPageStart(this, content);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.setcomplete:
                if("0".equals(myApp.user.hasPassword)) {
                    StatService.onEvent(ModifyPasswordActivity.this, Event.EVENT_ID_CHANGEPW_C, "确认-select");
                } else {
                    StatService.onEvent(ModifyPasswordActivity.this, Event.EVENT_ID_CHANGEPW_U, "确认-select");
                }
                oldPW = et_oldpw.getText().toString();
                newPW = et_newpw.getText().toString();
                newPWreapt = et_newpwreapt.getText().toString();

                if("0".equals(myApp.user.hasPassword)) {
                    //不用管旧密码
                    oldPW = "0";
                } else {
                    if(!isPwRight(oldPW)) {
                        Toast.makeText(ModifyPasswordActivity.this, "旧密码长度不正确", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(isPwRight(newPW)) {
                    if(isPwRight(newPWreapt)) {
                        if(newPWreapt.equals(newPW)) {
                            if(!progressDialog.isShowing())
                                progressDialog.show();

                            if("0".equals(myApp.user.hasPassword)) {
                                StatService.onEvent(ModifyPasswordActivity.this, Event.EVENT_ID_CHANGEPW_C, "确认-send");
                            } else {
                                StatService.onEvent(ModifyPasswordActivity.this, Event.EVENT_ID_CHANGEPW_U, "确认-send");
                            }
                            NetworkData.updateUserPassword(myApp.user.userid, myApp.user.token, myApp.uuid, oldPW, newPW, new NetworkData.NetworkCallback() {
                                @Override
                                public void callback(String data) {
                                    try {
                                        handler.sendEmptyMessage(2);
                                        JSONObject jData = new JSONObject(data);
                                        if ("0".equals(jData.getString("result"))) {
                                            if("0".equals(myApp.user.hasPassword)) {
                                                StatService.onEvent(ModifyPasswordActivity.this, Event.EVENT_ID_CHANGEPW_C, "确认-success");
                                            } else {
                                                StatService.onEvent(ModifyPasswordActivity.this, Event.EVENT_ID_CHANGEPW_U, "确认-success");
                                            }
                                            myApp.user.hasPassword = "1";
                                            myApp.saveUser();
                                            setResult(RESULT_OK);
                                            finish();
                                        } else {
                                            Message obtain = Message.obtain();
                                            obtain.obj = jData.getString("info");
                                            obtain.what = 1;
                                            handler.sendMessage(obtain);
                                            if("0".equals(myApp.user.hasPassword)) {
                                                StatService.onEvent(ModifyPasswordActivity.this, Event.EVENT_ID_CHANGEPW_C, "确认-fail-" + jData.getString("info"));
                                            } else {
                                                StatService.onEvent(ModifyPasswordActivity.this, Event.EVENT_ID_CHANGEPW_U, "确认-fail-" + jData.getString("info"));
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ModifyPasswordActivity.this, "新密码和重复密码不相同", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ModifyPasswordActivity.this, "重复密码的长度不正确", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ModifyPasswordActivity.this, "新密码长度不正确", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.mpw_e1:
                if(et_oldpw.getInputType() == 129) {
                    et_oldpw.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    et_oldpw.setInputType(129);
                }
                break;
            case R.id.mpw_e2:
                if(et_newpw.getInputType() == 129) {
                    et_newpw.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    et_newpw.setInputType(129);
                }
                break;
            case R.id.mpw_e3:
                if(et_newpwreapt.getInputType() == 129) {
                    et_newpwreapt.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    et_newpwreapt.setInputType(129);
                }
                break;
        }
    }

    private boolean isPwRight(String s) {
        return (s != null && s.length() >=6 && s.length() <= 20);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, content);
    }
}
