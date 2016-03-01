package com.weiguan.kejian;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.weiguan.kejian.commons.MyGesture;

/**
 * Created by Administrator on 2016/2/2 0002.
 */
public class BaseUserActivity extends BaseAnimationActivity {
    private Button title_user_back;
    private TextView tv_title;
    private MyGesture myGesture;
    public GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myGesture = new MyGesture(this);
        detector = new GestureDetector(this, myGesture);
        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        title_user_back = (Button) findViewById(R.id.title_user_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        if(title_user_back != null) {
            title_user_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    protected void setTitle(String title) {
        if(tv_title != null) {
            tv_title.setText(title);
            tv_title.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if(title_user_back != null)
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(title_user_back.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
