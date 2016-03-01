package com.weiguan.kejian;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.weiguan.kejian.errorhandle.CrashHandler;
import com.weiguan.kejian.imgage.ImageLoaderUtil;
import com.weiguan.kejian.model.User;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.socialize.PlatformConfig;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class MyApplication extends Application {
    public ImageLoaderUtil imageLoaderUtil;

    public User user;

    public SharedPreferences sp;
    public SharedPreferences userInfo;
    public String uuid;

    public HashMap<String, String> catIdText = new HashMap<>();

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
        com.umeng.socialize.utils.Log.LOG = false;

        sp = getSharedPreferences("cache", MODE_PRIVATE);
        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE);
        String u = sp.getString("uuid", "");
        if("".equals(u)) {
            uuid = UUID.randomUUID().toString().replaceAll("-", "");
            sp.edit().putString("uuid", uuid).commit();
        } else {
            uuid = u;
        }

        initUser();
    }

    private void initUser() {
        if(user == null) {
            user = new User();
        }
        user.userid = userInfo.getString("user_userid", "");
        user.username = userInfo.getString("user_username", "");
        user.nickname = userInfo.getString("user_nickname", "");
        user.avatar = userInfo.getString("user_avatar", "");
        user.des = userInfo.getString("user_des", "");
        user.weibo_openid = userInfo.getString("user_weibo_openid", "");
        user.qq_openid = userInfo.getString("user_qq_openid", "");
        user.weixin_openid = userInfo.getString("user_wx_openid", "");
        user.hasPassword = userInfo.getString("user_hasPassword", "");
        user.mobile = userInfo.getString("user_mobile", "");
        user.token = userInfo.getString("user_token", "");
    }

    public void saveUser(JSONObject json) {
        try {
            if(user == null) {
                user = new User();
            }
            user.userid = json.getString("userid");
            user.username = json.getString("username");
            user.nickname = json.getString("nickname");
            user.avatar = json.getString("avatar");
            user.des = json.getString("des");
            user.weibo_openid = json.getString("weibo_openid");
            user.qq_openid = json.getString("qq_openid");
            user.weixin_openid = json.getString("weixin_openid");
            user.hasPassword = json.getString("hasPassword");
            user.mobile = json.getString("mobile");
            user.token = json.getString("token");

            SharedPreferences.Editor edit = userInfo.edit();
            edit.putString("user_userid", user.userid);
            edit.putString("user_username", user.username);
            edit.putString("user_nickname", user.nickname);
            edit.putString("user_avatar", user.avatar);
            edit.putString("user_des", user.des);
            edit.putString("user_weibo_openid", user.weibo_openid);
            edit.putString("user_qq_openid", user.qq_openid);
            edit.putString("user_wx_openid", user.weixin_openid);
            edit.putString("user_hasPassword", user.hasPassword);
            edit.putString("user_mobile", user.mobile);
            edit.putString("user_token", user.token);
            edit.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveUser() {
        try {
            SharedPreferences.Editor edit = userInfo.edit();
            edit.putString("user_userid", user.userid);
            edit.putString("user_username", user.username);
            edit.putString("user_nickname", user.nickname);
            edit.putString("user_avatar", user.avatar);
            edit.putString("user_des", user.des);
            edit.putString("user_weibo_openid", user.weibo_openid);
            edit.putString("user_qq_openid", user.qq_openid);
            edit.putString("user_wx_openid", user.weixin_openid);
            edit.putString("user_hasPassword", user.hasPassword);
            edit.putString("user_mobile", user.mobile);
            edit.putString("user_token", user.token);
            edit.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * 加载图片并展示到界面上
     * @param imagePath
     * @param imgView
     */
    public void displayImageUserLogo(String imagePath, ImageView imgView){
        DisplayImageOptions dio = new DisplayImageOptions.Builder()
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .bitmapConfig(Bitmap.Config.ARGB_8888)
                        //.delayBeforeLoading(100)
//                .resetViewBeforeLoading(true)
                .showImageOnLoading(R.drawable.default_user_pic)		// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.default_user_pic)	// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.default_user_pic)		// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)	// 设置下载的图片是否缓存在SD卡中
                .build();

        if(imagePath != null && !"".equals(imagePath))
        imageLoaderUtil.displayImage(imagePath, imgView, dio);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //imageLoaderUtil.getImageLoader().clearMemoryCache();
        System.gc();
    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public void clearUser() {
        SharedPreferences.Editor edit = userInfo.edit();
        edit.clear().commit();
        user = new User();
    }
}
