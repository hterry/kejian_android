package com.weiguan.kejian.http;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.BaseAnimationActivity;
import com.weiguan.kejian.WebActivity;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.util.MailUtil;

/**
 * Created by Administrator on 2016/1/19 0019.
 */
public class AndroidInterface {
    private BaseAnimationActivity webActivity;
    private static String CHARGE_URL = "http://www.iflabs.cn/app/hellojames/html/legal.html";
    private static String MAIL_ADDRESS = "january.zhang@iflabs.cn";

    public AndroidInterface(BaseAnimationActivity webActivity) {
        this.webActivity = webActivity;
    }

    @JavascriptInterface
    public void openNewWeb(String id) {

        Intent intent = new Intent(webActivity.getApplicationContext(), WebActivity.class);
        intent.putExtra("id", id);
        webActivity.startActivity(intent);
//        webActivity.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
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
//        if(webActivity != null && webActivity instanceof WebActivity) {
//            ((WebActivity) webActivity).dismissComment();
//        }
    }

    @JavascriptInterface
    public void gainShareId(String id) {
        if(webActivity != null && webActivity instanceof WebActivity) {
            ((WebActivity) webActivity).setId(id);
        }
    }

    @JavascriptInterface
    public void gainShareThumb(String thumb) {
        if(webActivity != null && webActivity instanceof WebActivity) {
            ((WebActivity) webActivity).setThumb(thumb);
        }
    }

    @JavascriptInterface
    public void gainShareTitle(String title) {
        if(webActivity != null && webActivity instanceof WebActivity) {
            ((WebActivity) webActivity).setTitle(title);
        }
    }

    @JavascriptInterface
    public void gainShareDes(String des) {
        if(webActivity != null && webActivity instanceof WebActivity) {
            ((WebActivity) webActivity).setDes(des);
        }
    }

    @JavascriptInterface
    public void gainCommentId(String id) {
        if(webActivity != null && webActivity instanceof WebActivity) {
            ((WebActivity) webActivity).setCommentId(id);
        }
    }

    @JavascriptInterface
    public void gainCommentUserId(String id) {
        if(webActivity != null && webActivity instanceof WebActivity) {
            ((WebActivity) webActivity).setCommentUserId(id);
        }
    }

    @JavascriptInterface
    public void gainCommentUserName(String name) {
        if(webActivity != null && webActivity instanceof WebActivity) {
            ((WebActivity) webActivity).setCommentUsername(name);
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
        StatService.onEvent(webActivity, Event.EVENT_ID_ARTICLE, "商品跳转:" + des);
    }

    @JavascriptInterface
    public void countBigPic(String des) {
        StatService.onEvent(webActivity, Event.EVENT_ID_ARTICLE, "文章跳转:" + des + "是大图");
    }

    @JavascriptInterface
    public void countSmallPic(String des) {
        StatService.onEvent(webActivity, Event.EVENT_ID_ARTICLE, "文章跳转:" + des + "否大图");
    }

    @JavascriptInterface
    public void showBigPic(String url) {
        Log.i("showBigPic", url);
        if(webActivity != null && webActivity instanceof WebActivity) {
            ((WebActivity) webActivity).showBigPic(url);
        }
    }

    @JavascriptInterface
    public void removeLike(String id) {
        if(webActivity != null && webActivity instanceof WebActivity) {
            StatService.onEvent(webActivity, Event.EVENT_ID_ARTICLE, "取消点赞评论");
            ((WebActivity) webActivity).removeLike(id);
        }
    }

    @JavascriptInterface
    public void addLike(String id) {
        if(webActivity != null && webActivity instanceof WebActivity) {
            StatService.onEvent(webActivity, Event.EVENT_ID_ARTICLE, "点赞评论");
            ((WebActivity) webActivity).addLike(id);
        }
    }
}
