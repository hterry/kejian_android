package com.weiguan.kejian.model;

/**
 * Created by Administrator on 2016/2/3 0003.
 */
public class User {
    public String userid;
    public String username;
    public String nickname;
    public String avatar;
    public String des;
    public String weibo_openid;
    public String qq_openid;
    public String weixin_openid;
    public String hasPassword;
    public String mobile;
    public String token;

    public User() {
        userid = username = nickname = avatar = des = weibo_openid = qq_openid = weixin_openid = hasPassword = mobile = token = "";
    }
}
