package com.weiguan.kejian;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.weiguan.kejian.http.NetworkData;
import com.weiguan.kejian.view.CFTextView;
import com.weiguan.kejian.view.GifView;
import com.umeng.message.PushAgent;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class WelcomeActivity extends BaseAnimationActivity {
    private GifView welcome_anim;

    private MyApplication myApp;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 1:
                    welcome_anim.setPaused(true);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        myApp = (MyApplication) getApplicationContext();
        PushAgent.getInstance(this).onAppStart();

        welcome_anim = (GifView) findViewById(R.id.welcome_anim);
        welcome_anim.setMovieResource(R.raw.welcome_gif);

        if(myApp.user != null && !"".equals(myApp.user.userid) && !"".equals(myApp.user.token) && !"".equals(myApp.uuid)) {
            Log.i("info", "更新登陆信息");
            Log.i("myApp.user.userid", myApp.user.userid);
            Log.i("myApp.user.token", myApp.user.token);
            Log.i("myApp.uuid", myApp.uuid);
            NetworkData.checkLogin(myApp.user.userid, myApp.user.token, myApp.uuid, new NetworkData.NetworkCallback() {
                @Override
                public void callback(String data) {
                    try {
                        Log.i("checkLogin", data);
                        JSONObject jData = new JSONObject(data);
                        if("0".equals(jData.getString("result"))) {
                            myApp.saveUser(jData.getJSONObject("userInfo"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        handler.sendEmptyMessageDelayed(1, 1920);

        handler.sendEmptyMessageDelayed(0, 2000);
    }

}
