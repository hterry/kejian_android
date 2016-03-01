package com.weiguan.kejian.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by Administrator on 2016/1/21 0021.
 */
public class MailUtil {



    public static void sendMail(String path,Context c)
    {
        File file = new File(path); //附件文件地址

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra("subject", file.getName()); //
        intent.putExtra("body", "Email from CodePad"); //正文
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)); //添加附件，附件为file对象
        if (file.getName().endsWith(".gz")) {
            intent.setType("application/x-gzip"); //如果是gz使用gzip的mime
        } else if (file.getName().endsWith(".txt")) {
            intent.setType("text/plain"); //纯文本则用text/plain的mime
        } else {
            intent.setType("application/octet-stream"); //其他的均使用流当做二进制数据来发送
        }
        c.startActivity(intent); //调用系统的mail客户端进行发送}
    }

    public static void mailContact(Context c,String mailAdress)
    {
        Intent it = new Intent(Intent.ACTION_SEND);
        String[] receiver;
        receiver=new String[]{mailAdress};
        it.putExtra(Intent.EXTRA_EMAIL, receiver);
        it.putExtra(Intent.EXTRA_SUBJECT, "投稿");
        it.putExtra(Intent.EXTRA_TEXT, "/*Thanks advance for any tips.*/");

        it.setType("text/plain");
        c.startActivity(Intent.createChooser(it, "请选择邮件App")); //调用系统的mail客户端进行发送
    }

    public static void sendMail(Context c,String mailAdress, String title)
    {
        Intent it = new Intent(Intent.ACTION_SEND);
        String[] receiver;
        receiver=new String[]{mailAdress};
        it.putExtra(Intent.EXTRA_EMAIL, receiver);
        it.putExtra(Intent.EXTRA_SUBJECT, title);
//        it.putExtra(Intent.EXTRA_TEXT, title);

        it.setType("text/plain");
        c.startActivity(Intent.createChooser(it, "请选择邮件App")); //调用系统的mail客户端进行发送
    }

}

