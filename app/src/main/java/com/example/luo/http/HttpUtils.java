package com.example.luo.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by Administrator on 2015/10/23 0023.
 */
public class HttpUtils {
    public static String httpPost(String address, HashMap<String, String> params) {
        try {
//            Log.i("httpPost_url", address);
//            Log.i("httpPost_param", params.toString());
            URL url = new URL(address);
            HttpURLConnection hConn = (HttpURLConnection) url.openConnection();
            hConn.setUseCaches(false);
            hConn.setDoOutput(true);
            hConn.setDoInput(true);
            hConn.setRequestMethod("POST");
            hConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            hConn.connect();
            //DataOutputStream流
            StringBuilder sb = new StringBuilder();
            if(params != null && params.size() > 0) {
                DataOutputStream out = new DataOutputStream(hConn.getOutputStream());
                Set<String> keys = params.keySet();
                for(String key : keys) {
                    String value = params.get(key);
                    value = URLEncoder.encode(value, "UTF-8");
                    sb.append(key + "=").append(value + "&");
                }
                sb.delete(sb.length() - 1, sb.length());
                out.writeBytes(sb.toString());
                out.flush();
                out.close();
            }

            sb = new StringBuilder();
            InputStreamReader in = new InputStreamReader(hConn.getInputStream());
            BufferedReader buffer = new BufferedReader(in);
            String inputLine = null;
            while (((inputLine = buffer.readLine()) != null)){
                sb.append(inputLine);
            }
            in.close();
//            Log.i("httpPost_result", sb.toString());
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-2";
    }

    public static String httpGet(String address) {
        try {
//            Log.i("httpGet_url", address);
            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestMethod("GET");// 设置请求的方式
            urlConnection.setReadTimeout(5000);// 设置超时的时间
            urlConnection.setConnectTimeout(5000);// 设置链接超时的时间
            // 设置请求的头
            urlConnection
                    .setRequestProperty("User-Agent",
                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
            // 获取响应的状态码 404 200 505 302
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();

                // 创建字节输出流对象
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    os.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                os.close();
                // 返回字符串
                String result = new String(os.toByteArray());
//                Log.i("httpGet_result", result);
                return result;
            } else {
                System.out.println("------------------链接失败-----------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("httpGet_result", "-2 ");
        return "-2";
    }
}
