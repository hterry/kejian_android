package com.weiguan.kejian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.commons.Event;

/**
 * Created by Administrator on 2016/2/4 0004.
 */
public class SaftySettingActivity extends BaseUserActivity implements View.OnClickListener {
    private static final String TAG = "安全设置页面";

    private Button modify_tel;
    private Button modify_pw;

    private MyApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saftyset);

        StatService.onPageStart(this, TAG);

        myApp = (MyApplication) getApplicationContext();
        initView();
    }

    private void initView() {
        modify_tel = (Button) findViewById(R.id.modify_tel);
        modify_pw = (Button) findViewById(R.id.modify_pw);

        modify_tel.setOnClickListener(this);

        if("".equals(myApp.user.mobile) || "".equals(myApp.user.username)) {
            modify_pw.setVisibility(View.GONE);
            modify_tel.setText("绑定手机号码");
        } else {
            modify_pw.setVisibility(View.VISIBLE);
            modify_tel.setText("修改手机号码");
        }

        if("0".equals(myApp.user.hasPassword)) {
            modify_pw.setText("设置密码");
        } else {
            modify_pw.setText("修改密码");
        }
        modify_pw.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch(v.getId()) {
            case R.id.modify_tel:
                if("".equals(myApp.user.mobile) || "".equals(myApp.user.username)) {
                    StatService.onEvent(SaftySettingActivity.this, Event.EVENT_ID_SAFESETTING, "绑定手机号-select");
                    intent = new Intent(SaftySettingActivity.this, SetNewTelActivity.class);
                } else {
                    StatService.onEvent(SaftySettingActivity.this, Event.EVENT_ID_SAFESETTING, "修改手机号-select");
                    intent = new Intent(SaftySettingActivity.this, VerifyOldTelActivity.class);
                }
                break;
            case R.id.modify_pw:
                StatService.onEvent(SaftySettingActivity.this, Event.EVENT_ID_SAFESETTING, "修改密码-select");
                intent = new Intent(SaftySettingActivity.this, ModifyPasswordActivity.class);
                break;
        }
        if(intent != null) {
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            initView();
        }
    }
}
