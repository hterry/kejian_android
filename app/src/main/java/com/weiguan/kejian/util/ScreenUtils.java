package com.weiguan.kejian.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.opengl.GLES10;
import android.opengl.GLES30;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.nio.IntBuffer;

/**
 * 获得屏幕相关的辅助类
 * 
 * @author zhy
 * 
 */
public class ScreenUtils
{
	private ScreenUtils()
	{
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context)
	{
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context)
	{
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}

	/**
	 * 获得状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context)
	{

		/*int statusHeight = -1;
		try
		{
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e)
		{
			e.printStackTrace();
		}*/
		return getInternalDimensionSize(context.getResources(), "status_bar_height");
	}
	
	private static int getInternalDimensionSize(Resources res, String key) {
        int result = 0;
        int resourceId = res.getIdentifier(key, "dimen", "android");
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId);
        }
        return result;
    }
	
	/**
	 * 隐藏导航栏
	 * @param context
	 */
	@SuppressLint("NewApi")
	public static void hideNavigationBar(Context context){
		if (VERSION.SDK_INT > 16) {
		    View decorView = ((Activity) context).getWindow().getDecorView();
		    // Hide both the navigation bar and the status bar.  
		    // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as  
		    // a general rule, you should design your app to hide the status bar whenever you  
		    // hide the navigation bar.  
		    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
		                  | View.SYSTEM_UI_FLAG_FULLSCREEN;
		    decorView.setSystemUiVisibility(uiOptions); 
		}
	}
	
	/**
	 * 设置状态栏和导航栏透明
	 * @param context
	 */
	@TargetApi(VERSION_CODES.KITKAT)
	public static void setStatusAlpa(Context context, boolean navigaApha) {
		if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
			// 透明状态栏
			((Activity) context).getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			// 透明导航栏
			if(navigaApha){
				((Activity) context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			}
		}
	}
	
	/**
	 * 获取当前屏幕截图，包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity)
	{
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;

	}

	/**
	 * 获取当前屏幕截图，不包含状态栏
	 * 
	 * @param activity
	 * @return
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity)
	{
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;

		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
				- statusBarHeight);
		view.destroyDrawingCache();
		return bp;

	}
	
	/**
	 * OPENGL 截图
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param gl
	 * @return
	 */
	public static Bitmap SavePixels(int x, int y, int w, int h, GLES30 gl)

	{
		int b[] = new int[w * h];

		int bt[] = new int[w * h];

		IntBuffer ib = IntBuffer.wrap(b);

		ib.position(0);

		GLES10.glReadPixels(x, y, w, h, GLES10.GL_RGBA, GLES10.GL_UNSIGNED_BYTE, ib);

		for (int i = 0; i < h; i++)

		{

			for (int j = 0; j < w; j++)

			{

				int pix = b[i * w + j];

				int pb = (pix >> 16) & 0xff;

				int pr = (pix << 16) & 0x00ff0000;

				int pix1 = (pix & 0xff00ff00) | pr | pb;

				bt[(h - i - 1) * w + j] = pix1;

			}

		}
		
		Bitmap sb = Bitmap.createBitmap(bt, w, h, Config.ARGB_8888);

		return sb;

	}
	
	/**
	 * 获取软件版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionCode(Context context) {
		String versionCode = "1.0";
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = context.getPackageManager().getPackageInfo(
					"com.easyget.community", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

}
