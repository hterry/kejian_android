package com.example.luo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.example.luo.commons.MyGesture;
import com.example.luo.http.NetworkData;
import com.example.luo.util.NetworkUtils;
import com.umeng.message.PushAgent;
import com.weiguan.kejian.R;

/**
 * Created by Administrator on 2016/1/20 0020.
 */
public class AdviceActivity extends Activity implements View.OnClickListener {
    private TextView title_text;
    private Button title_close;

    private EditText advice;
    private Button advice_submit, btn_left;

    private MyGesture myGesture;
    private GestureDetector detector;

    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);

        PushAgent.getInstance(this).onAppStart();

        title_text = (TextView) findViewById(R.id.title_text);
        title_text.setText("提建议");
        title_text.setTextColor(ContextCompat.getColor(this, R.color.green));
        title_close = (Button) findViewById(R.id.title_close);
        btn_left = (Button) findViewById(R.id.btn_left);
        title_close.setText("发送");
        title_close.setOnClickListener(this);
        btn_left.setVisibility(View.VISIBLE);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        advice = (EditText) findViewById(R.id.advice);
        advice_submit = (Button) findViewById(R.id.advice_submit);

        advice_submit.setOnClickListener(this);

        myGesture = new MyGesture(this);
        detector = new GestureDetector(this, myGesture);
        advice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        content = "提建议给我们";
        StatService.onPageStart(this, content);
    }

    @Override
    public void onClick(View v) {
        if(NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_LONG).show();
            String ad = advice.getText().toString();
            finish();
            NetworkData.sendAdvice(ad, new NetworkData.NetworkCallback() {
                @Override
                public void callback(String data) {
                }
            });
        } else {
            Toast.makeText(AdviceActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, content);
    }
}
