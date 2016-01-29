package com.example.luo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/1/28 0028.
 */
public class CFTextView extends View {
    private String text;
    private int backgrounColor;
    private int textPaddingLeft;
    private int textPaddingRight;

    public CFTextView(Context context) {
        super(context);
    }

    public CFTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CFTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(text != null) {
            showText(canvas);
        }
    }

    private void showText(Canvas canvas) {
        canvas.drawText(text, 0, 0, new Paint());
    }
}
