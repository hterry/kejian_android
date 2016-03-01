package com.weiguan.kejian.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.weiguan.kejian.util.PXUtils;
import com.weiguan.kejian.util.ScreenUtils;

/**
 * Created by Administrator on 2016/1/28 0028.
 */
public class CFTextView extends View {
    private String text;
    private int bgColor = Color.BLACK;
    private int textColor = Color.WHITE;
    private int lineHeight = 10;
    private int textPaddingLeft = 10;
    private int textPaddingRight = 30;
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Paint backgroundPaint;
    public int lines;

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
        initPaint();
//        invalidate();
    }

    private void initPaint() {
        textPaint.setColor(textColor);
        textPaint.setTextSize(PXUtils.dip2px(getContext(), 14));
        backgroundPaint = new Paint();
        backgroundPaint.setColor(bgColor);

        float width = textPaint.measureText(text);
        int cWidth = ScreenUtils.getScreenWidth(getContext());
        lines = (int) Math.ceil(width / cWidth); //计算行数
//        int height = (lines - 1) * 2 * lineHeight + lines * r.height() + lines * 2 * lineHeight;
//        ViewGroup.LayoutParams layoutParams = getLayoutParams();
//        layoutParams.height = height;
//        setLayoutParams(layoutParams);
    }

    public void setBgColor(int c) {
        this.bgColor = c;
        backgroundPaint.setColor(c);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(text != null) {
            Paint.FontMetrics fm = textPaint.getFontMetrics();

            float baseline = fm.descent - fm.ascent;
            float x = textPaddingLeft;
            float y =  baseline - lineHeight / 2;  //由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。

            //文本自动换行
            String[] texts = autoSplit(text, textPaint, getWidth() - textPaddingRight);

            float temp = 0;
            for(String text : texts) {
                if(textPaint != null && text != null) {
                    Rect rect = new Rect();
                    textPaint.getTextBounds(text, 0, text.length(), rect);
                    canvas.drawRect(0, temp, rect.width() + textPaddingRight, baseline + fm.leading + temp + lineHeight, backgroundPaint);
                    temp += baseline + fm.leading +  (2 * lineHeight); //添加字体行间距
                }
            }
            for(String text : texts) {
                if(textPaint != null && text != null) {
                    canvas.drawText(text, x, y, textPaint);  //坐标以控件左上角为原点
                    y += baseline + fm.leading + (2 * lineHeight); //添加字体行间距
                }
            }
//            showText(canvas);
        }
    }

    /**
     * 自动分割文本
     * @param content 需要分割的文本
     * @param p  画笔，用来根据字体测量文本的宽度
     * @param width 最大的可显示像素（一般为控件的宽度）
     * @return 一个字符串数组，保存每行的文本
     */
    private String[] autoSplit(String content, Paint p, float width) {
        int length = content.length();
        float textWidth = p.measureText(content);
        if(textWidth <= width) {
            return new String[]{content};
        }

        int start = 0, end = 1, i = 0;
        int lines = (int) Math.ceil(textWidth / width); //计算行数
        String[] lineTexts = new String[lines];
        while(start < length) {
            if(p.measureText(content, start, end) > width) { //文本宽度超出控件宽度时
                lineTexts[i++] = (String) content.subSequence(start, end);
                start = end;
            }
            if(end == length) { //不足一行的文本
                lineTexts[i] = (String) content.subSequence(start, end);
                break;
            }
            end += 1;
        }
        return lineTexts;
    }
}
