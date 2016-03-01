package com.weiguan.kejian.util;

import android.content.Context;
import android.view.View;

public class DisplayChangedUtils {
	//private static final String TAG = "AndroidUtil";

	/**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * 
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context,float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
     return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * 
     * @param d
     * @return
     */
    public static int dip2px(Context context,float d) {
    	final float scale = context.getResources().getDisplayMetrics().density;
    	return (int) (d * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * 
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context,float pxValue) {
    	final float scale = context.getResources().getDisplayMetrics().density;
    	return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param spValue
     * @return
     */
    public static int sp2px(Context context,float spValue) {
    	final float scale = context.getResources().getDisplayMetrics().density;
    	return (int) (spValue * scale + 0.5f);
    }
    
    /** 
     * 获取控件宽 
     * @param view需要获取宽度的控件
     */  
    public static int getViewWidth(View view)
    {  
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);  
        return (view.getMeasuredWidth());         
    }  
    /** 
     * 获取控件高 
     * @param view需要获取高度的控件
     */  
    public static int getViewHeight(View view)
    {  
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);  
        return (view.getMeasuredHeight());         
    }  
    
}
