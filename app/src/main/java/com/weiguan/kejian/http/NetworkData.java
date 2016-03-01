package com.weiguan.kejian.http;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/1/12 0012.
 */
public class NetworkData {
    public static final String BASE_URL = "http://www.iflabs.cn/app/hellojames/api/api.php?type=";
//    public static final String BASE_URL = "http://www.iflabs.cn/app/hellojames/api/api_dev.php?type=";
    public static final String USER_URL = "https://www.iflabs.cn/api/user_api.php";
//    public static final String USER_URL = "https://www.iflabs.cn/api/user_api_dev.php";
    public static final String SMS_URL = "http://www.iflabs.cn/php/testcms/phpcms_test/index.php?m=sms_yz&c=index&a=sendsms";
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

    public static void getHomeList(final String id, final String time, final String userid, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = BASE_URL + "getNewsList&catid=" + id + "&updateTime=" + time + "&userid=" + userid;
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

    public static void search(final String keyword, final String time, final String sort, final String userid, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = BASE_URL + "search";
                if(keyword != null) {
                    try {
                        url = url + "&keywords=" + URLEncoder.encode(keyword, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                if(time != null) {
                    url = url + "&updateTime=" + time;
                }
                if(userid != null) {
                    url = url +  "&userid=" + userid;
                }
                if(sort != null) {
                    url = url +  "&sort=" + sort;
                }
                String data = HttpUtils.httpGet(url);
                callback.callback(data);
            }
        }.start();
    }

    public static void getCommentMessageList(final String userid, final String updateTime, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = BASE_URL + "getCommentMessageList&userid=" + userid + "&updateTime=" + updateTime;
                String data = HttpUtils.httpGet(url);
                callback.callback(data);
            }
        }.start();
    }

    public static void getMySendedCommentList(final String userid, final String updateTime, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = BASE_URL + "getMySendedCommentList&userid=" + userid + "&updateTime=" + updateTime;
                String data = HttpUtils.httpGet(url);
                callback.callback(data);
            }
        }.start();
    }

    public static void checkNewsLike(final String userid, final String newsid, final String catid, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                if(userid == null) {
                    String url = BASE_URL + "checkNewsLike&userid=" + "-1" + "&newsid=" + newsid + "&catid=" + catid;
                }
                String url = BASE_URL + "checkNewsLike&userid=" + userid + "&newsid=" + newsid + "&catid=" + catid;
                Log.i("url__", url);
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

    public static void sendSMS(final String moible, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = SMS_URL + "&mobile=" + moible + "&app=kejian&time=1";
                String data = HttpUtils.httpGet(url);
                callback.callback(data);
            }
        }.start();
    }

    public static void verifySMS(final String mobile, final String verifyCode, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=getMobileCodeResult";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("mobile", new String(Base64.encode(AESTool.encrypt(mobile), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("code", new String(Base64.encode(AESTool.encrypt(verifyCode), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void login(final String username, final String password, final String deviceid, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=usernameLogin";
                HashMap<String, String> pm = new HashMap<String, String>();
                String s = new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(password)), Base64.NO_WRAP));
                String replace = s.replace("+", "%2B");
                pm.put("password", replace);
                pm.put("username", new String(Base64.encode(AESTool.encrypt(username), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void getWeiboData(final String url, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String s = HttpsHelper.httpsGet(url);
                callback.callback(s);
            }
        }.start();
    }

    public static void snsLogin(final String openid, final String sns, final String nickname, final String avatar, final String deviceid, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=snsLogin";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("openid", new String(Base64.encode(AESTool.encrypt(openid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("sns", new String(Base64.encode(AESTool.encrypt(sns), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("nickname", new String(Base64.encode(AESTool.encrypt(nickname), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("avatar", new String(Base64.encode(AESTool.encrypt(avatar), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void checkMobile(final String mobile, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=checkMobile";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("mobile", new String(Base64.encode(AESTool.encrypt(mobile), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void regist(final String mobile, final String password, final String deviceid, final String verifyCode, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=usernameRegister";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("username", new String(Base64.encode(AESTool.encrypt(mobile), Base64.NO_WRAP)).replace("+", "%2B"));
                String s = new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(password)), Base64.NO_WRAP));
                String replace = s.replace("+", "%2B");
                pm.put("password", replace);
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("code", new String(Base64.encode(AESTool.encrypt(verifyCode), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void saveUserInfo(final String userid, final String token, final String deviceid, final String nickname, final byte[] avatar, final String des, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=saveUserInfo";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("userid", new String(Base64.encode(AESTool.encrypt(userid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("token", new String(Base64.encode(AESTool.encrypt(token), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("nickname", new String(Base64.encode(AESTool.encrypt(nickname), Base64.NO_WRAP)).replace("+", "%2B"));
                if(avatar != null) {
                    pm.put("avatarData", new String(Base64.encode(avatar, Base64.NO_WRAP)).replace("+", "%2B"));
                }
                pm.put("des", new String(Base64.encode(AESTool.encrypt(des), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void checkLogin(final String userid, final String token, final String deviceid, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=checkLogin";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("userid", new String(Base64.encode(AESTool.encrypt(userid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("token", new String(Base64.encode(AESTool.encrypt(token), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void addFavorites(final String userid, final String token, final String deviceid, final String newsid, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=addFavorites";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("userid", new String(Base64.encode(AESTool.encrypt(userid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("token", new String(Base64.encode(AESTool.encrypt(token), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("newsid", new String(Base64.encode(AESTool.encrypt(newsid), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void addNewsCommentSupport(final String userid, final String token, final String deviceid, final String commentid, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=addNewsCommentSupport";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("userid", new String(Base64.encode(AESTool.encrypt(userid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("token", new String(Base64.encode(AESTool.encrypt(token), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("commentid", new String(Base64.encode(AESTool.encrypt(commentid), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void removeNewsCommentSupport(final String userid, final String token, final String deviceid, final String commentid, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=removeNewsCommentSupport";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("userid", new String(Base64.encode(AESTool.encrypt(userid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("token", new String(Base64.encode(AESTool.encrypt(token), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("commentid", new String(Base64.encode(AESTool.encrypt(commentid), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void removeFavorites(final String userid, final String token, final String deviceid, final String newsid, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=removeFavorites";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("userid", new String(Base64.encode(AESTool.encrypt(userid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("token", new String(Base64.encode(AESTool.encrypt(token), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("newsid", new String(Base64.encode(AESTool.encrypt(newsid), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void addNewsComment(final String userid, final String token, final String deviceid, final String newsid, final String catid, final String username, final String content, final String reply_userid, final String reply_username, final String reply_commentid, final String news_comment_private, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=addNewsComment";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("userid", new String(Base64.encode(AESTool.encrypt(userid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("token", new String(Base64.encode(AESTool.encrypt(token), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("newsid", new String(Base64.encode(AESTool.encrypt(newsid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("catid", new String(Base64.encode(AESTool.encrypt(catid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("username", new String(Base64.encode(AESTool.encrypt(username), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("content", new String(Base64.encode(AESTool.encrypt(content), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("news_comment_private", new String(Base64.encode(AESTool.encrypt(news_comment_private), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("reply_userid", new String(Base64.encode(AESTool.encrypt(reply_userid == null ? "0" : reply_userid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("reply_username", new String(Base64.encode(AESTool.encrypt(reply_username == null ? "0" : reply_username), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("reply_commentid", new String(Base64.encode(AESTool.encrypt(reply_commentid == null ? "0" : reply_commentid), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void findUserPassword(final String mobile, final String code, final String password, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=findUserPassword";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("mobile", new String(Base64.encode(AESTool.encrypt(mobile), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("code", new String(Base64.encode(AESTool.encrypt(code), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("password", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(password)), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void updateUserMobile(final String userid, final String token, final String deviceid, final String mobile, final String code, final String mobile2, final String code2, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=updateUserMobile";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("userid", new String(Base64.encode(AESTool.encrypt(userid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("token", new String(Base64.encode(AESTool.encrypt(token), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("mobile", new String(Base64.encode(AESTool.encrypt(mobile), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("code", new String(Base64.encode(AESTool.encrypt(code), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("mobile2", new String(Base64.encode(AESTool.encrypt(mobile2), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("code2", new String(Base64.encode(AESTool.encrypt(code2), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void updateUserPassword(final String userid, final String token, final String deviceid, final String password1, final String password2, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=updateUserPassword";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("userid", new String(Base64.encode(AESTool.encrypt(userid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("token", new String(Base64.encode(AESTool.encrypt(token), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("password1", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(password1)), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("password2", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(password2)), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void addSnsLogin(final String userid, final String token, final String deviceid, final String openid, final String sns, final String nickname, final String avatar, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
                String url = USER_URL + "?type=addSnsLogin";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("userid", new String(Base64.encode(AESTool.encrypt(userid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("token", new String(Base64.encode(AESTool.encrypt(token), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("openid", new String(Base64.encode(AESTool.encrypt(openid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("sns", new String(Base64.encode(AESTool.encrypt(sns), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("nickname", new String(Base64.encode(AESTool.encrypt(nickname), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("avatar", new String(Base64.encode(AESTool.encrypt(avatar), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    public static void removeSnsLogin(final String userid, final String token, final String deviceid, final String openid, final String sns, final NetworkCallback callback) {
        new Thread() {
            @Override
            public void run() {
//                Log.i("userid", userid);
//                Log.i("token", token);
//                Log.i("deviceid", deviceid);
//                Log.i("openid", openid);
//                Log.i("sns", sns);
                String url = USER_URL + "?type=removeSnsLogin";
                HashMap<String, String> pm = new HashMap<String, String>();
                pm.put("userid", new String(Base64.encode(AESTool.encrypt(userid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("token", new String(Base64.encode(AESTool.encrypt(token), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("deviceid", new String(Base64.encode(AESTool.encrypt(MD5Tool.getMd5Value(deviceid)), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("openid", new String(Base64.encode(AESTool.encrypt(openid), Base64.NO_WRAP)).replace("+", "%2B"));
                pm.put("sns", new String(Base64.encode(AESTool.encrypt(sns), Base64.NO_WRAP)).replace("+", "%2B"));
                String data = HttpsHelper.getHttpsContentUTF(pm, url);
                callback.callback(dealData(data));
            }
        }.start();
    }

    private static String dealData(String data) {
        String a = null;
        try {
            JSONObject jData = new JSONObject(data);
            String code = jData.getString("code");
            a = new String(AESTool.decrypt(Base64.decode(code.getBytes(), Base64.NO_WRAP)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;
    }

    public interface NetworkCallback {
        void callback(String data);
    }
}
