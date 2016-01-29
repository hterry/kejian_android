package com.example.luo.http;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/1/12 0012.
 */
public class NetworkData {
    public static final String BASE_URL = "http://www.iflabs.cn/app/hellojames/api/api.php?type=";
    public static final String ERROR_URL = "http://www.iflabs.cn/php/testcms/phpcms_test/index.php?m=log&c=index&a=add";

    public static void getLeftMenuData(final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = BASE_URL + "getCatList";
                String data = HttpUtils.httpGet(url);
                callback.callback(data);
            }
        }.start();

    }

    public static void getHomeList(final String id, final String time, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = BASE_URL + "getNewsList&catid=" + id + "&updateTime=" + time;
                String data = HttpUtils.httpGet(url);
                callback.callback(data);
            }
        }.start();
    }

    public static void getGoodList(final String updateTime, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = BASE_URL + "getProductList&updateTime=" + updateTime;
                String data = HttpUtils.httpGet(url);
                callback.callback(data);
            }
        }.start();
    }

    public static void getVerionInfo(final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = BASE_URL + "getAndroidAppInfo";
                String data = HttpUtils.httpGet(url);
                callback.callback(data);
            }
        }.start();
    }

    public static void sendAdvice(final String advice, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("suggestcontent", advice);
                String url = BASE_URL + "sendAndroidSuggest";
                String data = HttpUtils.httpPost(url, pm);
                callback.callback(data);
            }
        }.start();
    }

    public static void search(final String keyword, final String time, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = BASE_URL + "search&keywords=" + keyword + "&updateTime=" + time;
                String data = HttpUtils.httpGet(url);
                callback.callback(data);
            }
        }.start();
    }

    public static void sendError(final String s1, final String s2, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("error_no", s1);
                pm.put("content", s2);
                callback.callback(HttpUtils.httpPost(ERROR_URL, pm));
            }
        }.start();
    }

    public interface NetworkCallback {
        void callback(String data);
    }
}
