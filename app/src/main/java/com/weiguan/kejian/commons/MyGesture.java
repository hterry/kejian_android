package com.weiguan.kejian.commons;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/1/21 0021.
 */
public class MyGesture extends GestureDetector.SimpleOnGestureListener {
    private Activity activity;
    public MyGesture(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(e2 != null && e1 != null)
        if(e2.getX() - e1.getX() > 200 && e2.getY() - e1.getY() < 200) {
            activity.finish();
        }
        return super.onFling(e1, e2, velocityX, velocityY);
    }
}
