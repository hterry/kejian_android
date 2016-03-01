package com.weiguan.kejian;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.commons.MyGesture;
import com.weiguan.kejian.http.AndroidInterface;
import com.weiguan.kejian.util.NetworkUtils;
import com.weiguan.kejian.view.GifView;
import com.umeng.message.PushAgent;

/**
 * Created by Administrator on 2016/1/20 0020.
 */
public class AboutActivity extends BaseAnimationActivity {
    public static final String URL = "http://www.iflabs.cn/app/hellojames/html/aboutus.html";
//    public static final String URL = "http://www.iflabs.cn/app/hellojames/android/html/aboutus.html";

    private TextView title_text;
    private Button title_close;
    private RelativeLayout loadingcontent;
    private GifView gifcontent;

    private WebView about_webview;

    private MyGesture myGesture;
    private GestureDetector detector;
    private String content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if(!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(AboutActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
        }

        PushAgent.getInstance(this).onAppStart();

        initView();

        web();
        myGesture = new MyGesture(this);
        detector = new GestureDetector(this, myGesture);
        about_webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        loadurl(about_webview, URL);

        content = "关于我们";
        StatService.onPageStart(this, content);
    }

    private void initView() {
        title_text = (TextView) findViewById(R.id.title_text);
        title_text.setText("关于我们");
        title_text.setTextColor(ContextCompat.getColor(this, R.color.pink_divide));
        title_close = (Button) findViewById(R.id.title_close);
        title_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gifcontent = (GifView) findViewById(R.id.gifcontent);
        loadingcontent = (RelativeLayout) findViewById(R.id.loadingcontent);
        loadingcontent.setVisibility(View.VISIBLE);
        gifcontent.setMovieResource(R.raw.loading);

    }

    private void web() {
        about_webview = (WebView) findViewById(R.id.about_webview);

        about_webview.getSettings().setJavaScriptEnabled(true);// 可用JS
        about_webview.setScrollBarStyle(0);// 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
        about_webview.getSettings().setDomStorageEnabled(true);
        about_webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        about_webview.addJavascriptInterface(new AndroidInterface(this), "AndroidInterface");
        about_webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingcontent.setVisibility(View.GONE);
                gifcontent.setPaused(true);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i("webviewInfo", "onPageStarted");
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                Log.i("webviewInfo", "onLoadResource");
            }

            @Override
            public void onPageCommitVisible(WebView view, String url) {
                super.onPageCommitVisible(view, url);
                Log.i("webviewInfo", "onPageCommitVisible");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.i("webviewInfo", "onReceivedError");
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Log.i("webviewInfo", "onReceivedHttpError");
            }

            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                super.onFormResubmission(view, dontResend, resend);
                Log.i("webviewInfo", "onFormResubmission");
            }

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                Log.i("webviewInfo", "doUpdateVisitedHistory");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Log.i("webviewInfo", "onReceivedSslError");
                handler.proceed();
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                super.onReceivedClientCertRequest(view, request);
                Log.i("webviewInfo", "onReceivedClientCertRequest");
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
                Log.i("webviewInfo", "onReceivedHttpAuthRequest");
            }

            @Override
            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
                Log.i("webviewInfo", "shouldOverrideKeyEvent");
                return super.shouldOverrideKeyEvent(view, event);
            }

            @Override
            public void onUnhandledInputEvent(WebView view, InputEvent event) {
                super.onUnhandledInputEvent(view, event);
                Log.i("webviewInfo", "onUnhandledInputEvent");
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
                Log.i("webviewInfo", "onScaleChanged");
            }

            @Override
            public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
                super.onReceivedLoginRequest(view, realm, account, args);
                Log.i("webviewInfo", "onReceivedLoginRequest");
            }

            public boolean shouldOverrideUrlLoading(final WebView view,
                                                    final String url) {
                Log.i("webviewInfo", "shouldOverrideUrlLoading");
                if (url.startsWith("http")) {
                    loadurl(view, url);// 载入网页
                }
                return true;
            }// 重写点击动作,用webview载入

        });

        about_webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingcontent.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Log.i("webviewInfo", "onReceivedSslError");
                handler.proceed();
            }

            public boolean shouldOverrideUrlLoading(final WebView view,
                                                    final String url) {
                Log.i("webviewInfo", "shouldOverrideUrlLoading");
                if (url.startsWith("http")) {
                    loadurl(view, url);// 载入网页
                }
                return true;
            }// 重写点击动作,用webview载入

        });
        about_webview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
                super.onProgressChanged(view, progress);
            }
        });

    }

    public void loadurl(final WebView view, final String url) {
        view.loadUrl(url);// 载入网页
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, content);
    }
}
