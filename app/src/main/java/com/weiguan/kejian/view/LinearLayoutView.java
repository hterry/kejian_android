package com.weiguan.kejian.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class LinearLayoutView extends LinearLayout {
    public static final int KEYBORAD_HIDE = 0;
    public static final int KEYBORAD_SHOW = 1;
    private static final int SOFTKEYPAD_MIN_HEIGHT = 50;
    private Handler uiHandler = new Handler();

    public LinearLayoutView(Context context) {
        super(context);
    }

    public LinearLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, final int h, int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (oldh - h > SOFTKEYPAD_MIN_HEIGHT) {
                    keyBordStateListener.stateChange(KEYBORAD_SHOW);
                } else {
                    if (keyBordStateListener != null) {
                        keyBordStateListener.stateChange(KEYBORAD_HIDE);
                    }
                }
            }
        });
    }

    private KeyBordStateListener keyBordStateListener;
    public void setKeyBordStateListener(KeyBordStateListener keyBordStateListener) {
        this.keyBordStateListener = keyBordStateListener;
    }

    public interface KeyBordStateListener {
        void stateChange(int state);
    }
}