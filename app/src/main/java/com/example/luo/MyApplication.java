package com.example.luo;

import android.app.Application;
import android.widget.ImageView;

import com.example.luo.errorhandle.CrashHandler;
import com.example.luo.imgage.ImageLoaderUtil;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.media.WeiXinShareContent;

import java.io.File;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class MyApplication extends Application {
    public ImageLoaderUtil imageLoaderUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(getApplicationContext());
        imageLoaderUtil = ImageLoaderUtil.getInstance(this);
        File cacheDir = StorageUtils.getOwnCacheDirectory(this,
                "imageloader/project");

        PlatformConfig.setWeixin("wx97ed15df8f6ea971", "5682c0baf2e6b7d4da3026bf037f4bc7");
        //微信 appid appsecret
//        PlatformConfig.setSinaWeibo("752137463", "2b7830041448659f54bed288a9fb721c");
        PlatformConfig.setSinaWeibo("1947160717", "c41ff8c95c132008950363f4344863af");
        //新浪微博 appkey appsecret
        PlatformConfig.setQQZone("1104927341", "UoiCAkhGgRdV8ySY");
        // QQ和Qzone appid appkey
//        PlatformConfig.setAlipay("2015111700822536");
        //支付宝 appid
        com.umeng.socialize.utils.Log.LOG = true;

    }

    /**
     * 加载图片并展示到界面上
     * @param imagePath
     * @param imgView
     */
    public void displayImage(String imagePath, ImageView imgView){
        if(imagePath != null && !"".equals(imagePath))
        imageLoaderUtil.displayImage(imagePath, imgView);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //imageLoaderUtil.getImageLoader().clearMemoryCache();
        System.gc();
    }

}
