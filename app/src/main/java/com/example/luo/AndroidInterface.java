package com.example.luo;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.baidu.mobstat.StatService;
import com.example.luo.commons.Event;
import com.example.luo.util.MailUtil;
import com.weiguan.kejian.R;

/**
 * Created by Administrator on 2016/1/19 0019.
 */
public class AndroidInterface {
    private Activity webActivity;
    private static String CHARGE_URL = "http://www.iflabs.cn/app/hellojames/html/legal.html";
    private static String MAIL_ADDRESS = "january.zhang@iflabs.cn";

    public AndroidInterface(Activity webActivity) {
        this.webActivity = webActivity;
    }

    @JavascriptInterface
    public void openNewWeb(String id) {

        Intent intent = new Intent(webActivity.getApplicationContext(), WebActivity.class);
        intent.putExtra("id", id);
        webActivity.startActivity(intent);
        webActivity.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
    }

    @JavascriptInterface
    public void openOutsideNewWeb(String url) {

        Intent intent = new Intent(webActivity.getApplicationContext(), WebActivity.class);
        intent.putExtra("url", url);
        webActivity.startActivity(intent);
    }

    @JavascriptInterface
    public void refreshWebview() {
        Log.i("info", "refreshWebview()");
    }

    @JavascriptInterface
    public void gainShareId(String id) {
        Log.i("gainShareId", id);
        if(webActivity != null && webActivity instanceof WebActivity) {
            ((WebActivity) webActivity).setId(id);
        }
    }

    @JavascriptInterface
    public void gainShareThumb(String thumb) {
        Log.i("gainShareId", thumb);
        if(webActivity != null && webActivity instanceof WebActivity) {
            ((WebActivity) webActivity).setThumb(thumb);
        }
    }

    @JavascriptInterface
    public void gainShareTitle(String title) {
        Log.i("gainShareId", title);
        if(webActivity != null && webActivity instanceof WebActivity) {
            ((WebActivity) webActivity).setTitle(title);
        }
    }

    @JavascriptInterface
    public void gainShareDes(String des) {
        Log.i("gainShareId", des);
        if(webActivity != null && webActivity instanceof WebActivity) {
            ((WebActivity) webActivity).setDes(des);
        }
    }

    @JavascriptInterface
    public void sendEmailForBusness() {
        MailUtil.sendMail(webActivity, MAIL_ADDRESS, "我要跟课间进行商务合作");
    }

    @JavascriptInterface
    public void sendEmailForJoinus() {
        MailUtil.sendMail(webActivity, MAIL_ADDRESS, "我要加入课间的团队");
    }

    @JavascriptInterface
    public void charge() {
        Intent intent = new Intent(webActivity, WebActivity.class);
        intent.putExtra("url", CHARGE_URL);
        intent.putExtra("tag", "版权举报");
        webActivity.startActivity(intent);
    }

    @JavascriptInterface
    public void countGoods(String des) {
        StatService.onEvent(webActivity, Event.EVENT_ID_GOTOSHOPPING, des);
    }

    @JavascriptInterface
    public void countBigPic(String des) {
        StatService.onEvent(webActivity, Event.EVENT_ID_GOTOSHOPPING, des + "是大图");
    }

    @JavascriptInterface
    public void countSmallPic(String des) {
        StatService.onEvent(webActivity, Event.EVENT_ID_GOTOSHOPPING, des + "否大图");
    }
}
