package com.example.luo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.baidu.mobstat.StatService;
import com.example.luo.http.NetworkData;
import com.example.luo.view.GifView;
import com.umeng.message.PushAgent;
import com.weiguan.kejian.R;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class WelcomeActivity extends Activity {
    private GifView welcome_anim;

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

        PushAgent.getInstance(this).onAppStart();

        welcome_anim = (GifView) findViewById(R.id.welcome_anim);
        welcome_anim.setMovieResource(R.raw.welcome_gif);

        handler.sendEmptyMessageDelayed(1, 1920);

        handler.sendEmptyMessageDelayed(0, 2000);
    }

}
