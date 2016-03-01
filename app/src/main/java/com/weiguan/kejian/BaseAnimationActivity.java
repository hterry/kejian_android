package com.weiguan.kejian;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.animation.AnimationUtils;

/**
 * Created by Administrator on 2016/2/19 0019.
 */
public class BaseAnimationActivity extends Activity {
    public int activityCloseEnterAnimation;
    public int activityCloseExitAnimation;
    public int activityOpenEnterAnimation;
    public int activityOpenExitAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowAnimationStyle});
//        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
//        activityStyle.recycle();
//        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[] {android.R.attr.activityCloseEnterAnimation, android.R.attr.activityCloseExitAnimation,
//                android.R.attr.activityOpenEnterAnimation, android.R.attr.activityOpenExitAnimation});
//        activityCloseEnterAnimation = activityStyle.getResourceId(0, 0);
//        activityCloseExitAnimation = activityStyle.getResourceId(1, 0);
//        activityOpenEnterAnimation = activityStyle.getResourceId(2, 0);
//        activityOpenExitAnimation = activityStyle.getResourceId(3, 0);
//        activityStyle.recycle();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
//        overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
//        overridePendingTransition(activityOpenEnterAnimation, activityOpenExitAnimation);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
//        overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
//        overridePendingTransition(activityOpenEnterAnimation, activityOpenExitAnimation);
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(activityCloseEnterAnimation, activityCloseExitAnimation);
    }
}
