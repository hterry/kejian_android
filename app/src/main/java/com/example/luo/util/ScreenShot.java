package com.example.luo.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.view.View;

import java.io.InputStream;

public class ScreenShot {
	/*
	 * 获取指定Activity的截屏，保存到png文件
	 */
	public static Bitmap takeScreenShot(View v) {
		v.clearFocus(); // 清除视图焦点
		v.setPressed(false);// 将视图设为不可点击

		boolean willNotCache = v.willNotCacheDrawing(); // 返回视图是否可以保存他的画图缓存
		v.setWillNotCacheDrawing(false);

		// for the duration of this operation //将视图在此操作时置为透明
		int color = v.getDrawingCacheBackgroundColor(); // 获得绘制缓存位图的背景颜色
		v.setDrawingCacheBackgroundColor(0); // 设置绘图背景颜色
		if (color != 0) { // 如果获得的背景不是黑色的则释放以前的绘图缓存
			v.destroyDrawingCache(); // 释放绘图资源所使用的缓存
		}
		v.buildDrawingCache(); // 重新创建绘图缓存，此时的背景色是黑色
		Bitmap cacheBitmap = v.getDrawingCache(); // 将绘图缓存得到的,注意这里得到的只是一个图像的引用
		if (cacheBitmap == null) {
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap); // 将位图实例化

		// Restore the view //恢复视图
		v.destroyDrawingCache();// 释放位图内存
		v.setWillNotCacheDrawing(willNotCache);// 返回以前缓存设置
		v.setDrawingCacheBackgroundColor(color);// 返回以前的缓存颜色设置
		return bitmap;
	}

	/**
	 * 
	 * 加水印
	 * 
	 * @param src
	 *            原图
	 * @param watermark
	 *            水印
	 * @return
	 */
	public static Bitmap createWaterMaskImage(Activity gContext, Bitmap src,
			Bitmap watermark) {

		if (src == null) {
			return null;
		}
		DisplayMetrics metric = new DisplayMetrics();
		gContext.getWindowManager().getDefaultDisplay().getMetrics(metric);
		// 获取屏幕长和高
		int height = metric.heightPixels - watermark.getHeight() - 20;
		int width = metric.widthPixels - watermark.getWidth() - 20;
		int w = src.getWidth();
		int h = src.getHeight();
		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		// draw src into
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// draw watermark into
		cv.drawBitmap(watermark, width, height, null);// 在src的右下角画入水印
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newb;
	}

	/*
	 * 将资源文件转换成Bitmap
	 */
	public static Bitmap setIconReturnBitmap(Activity activity, int id) {
		InputStream is = activity.getResources().openRawResource(id);
		Bitmap mBitmap = BitmapFactory.decodeStream(is);
		return mBitmap;
	}
}
