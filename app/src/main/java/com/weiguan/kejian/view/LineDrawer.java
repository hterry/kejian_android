package com.weiguan.kejian.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nineoldandroids.view.ViewHelper;
import com.weiguan.kejian.R;

public class LineDrawer extends LinearLayout {
    private RelativeLayout line_pink, line_blue, line_green;

    public LineDrawer(Context context) {
        super(context);
        init(context);
    }

    public LineDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LineDrawer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        line_pink = (RelativeLayout) findViewById(R.id.line_pink);
        line_blue = (RelativeLayout) findViewById(R.id.line_blue);
        line_green = (RelativeLayout) findViewById(R.id.line_green);
    }

    public void start() {
        ViewHelper.setRotation(line_pink, 0.4f);
        ViewHelper.setAlpha(line_blue, 0f);
        ViewHelper.setRotation(line_green, -0.4f);
    }
}
