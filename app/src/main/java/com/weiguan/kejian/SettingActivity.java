package com.weiguan.kejian;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.commons.MyGesture;
import com.umeng.message.PushAgent;

/**
 * Created by Administrator on 2016/1/20 0020.
 */
public class SettingActivity extends BaseAnimationActivity {
    private TextView title_text;
    private Button title_close;

    private TextView setting_vscode, pushstatus;
    private RelativeLayout setting_point, setting_advice, settingpush, clear_cache;

    private MyGesture myGesture;
    private GestureDetector detector;
    private PushAgent mPushAgent;

    private AlertDialog dialog;

    private SharedPreferences sp;

    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.onAppStart();

        initView();
        myGesture = new MyGesture(this);
        detector = new GestureDetector(this, myGesture);
        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        sp = getSharedPreferences("cache", MODE_PRIVATE);

        content = "设置";
        StatService.onPageEnd(this, content);
    }

    private void initView() {
        title_text = (TextView) findViewById(R.id.title_text);
        title_text.setText("设置");
        title_text.setTextColor(ContextCompat.getColor(this, R.color.green));
        title_close = (Button) findViewById(R.id.title_close);
        title_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setting_vscode = (TextView) findViewById(R.id.setting_vscode);
        pushstatus = (TextView) findViewById(R.id.pushstatus);

        setting_point = (RelativeLayout) findViewById(R.id.setting_point);
        setting_advice = (RelativeLayout) findViewById(R.id.setting_advice);
        settingpush = (RelativeLayout) findViewById(R.id.settingpush);
        clear_cache = (RelativeLayout) findViewById(R.id.clear_cache);

        dialog = new AlertDialog.Builder(this)
                .setMessage("已经清除所有缓存信息")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            setting_vscode.setText("当前版本号：" + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(mPushAgent.isEnabled()) {
            settingpush.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            pushstatus.setText("开启");
        } else {
            settingpush.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            pushstatus.setText("未开启");
        }
        setting_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatService.onEvent(SettingActivity.this, Event.EVENT_ID_SETTING, "打赏分数");
            }
        });
        settingpush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mPushAgent.isEnabled()) {
                    StatService.onEvent(SettingActivity.this, Event.EVENT_ID_SETTING, "开启推送");
                    mPushAgent.enable();
                    settingpush.setBackgroundColor(ContextCompat.getColor(SettingActivity.this, R.color.green));
                    pushstatus.setText("开启");
                    sp.edit().putBoolean("isPush", true).commit();
                } else {
                    StatService.onEvent(SettingActivity.this, Event.EVENT_ID_SETTING, "关闭推送");
                    mPushAgent.disable();
                    settingpush.setBackgroundColor(ContextCompat.getColor(SettingActivity.this, R.color.gray));
                    pushstatus.setText("未开启");
                    sp.edit().putBoolean("isPush", false).commit();
                }
            }
        });

        setting_advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatService.onEvent(SettingActivity.this, Event.EVENT_ID_SETTING, "提交建议");

                Intent intent = new Intent(SettingActivity.this, AdviceActivity.class);
                startActivity(intent);
            }
        });

        clear_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatService.onEvent(SettingActivity.this, Event.EVENT_ID_SETTING, "清除缓存");

                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

}
