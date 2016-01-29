package com.example.luo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.InputEvent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.baidu.mobstat.StatService;
import com.example.luo.commons.Event;
import com.example.luo.commons.MyGesture;
import com.example.luo.imgage.ImageLoaderUtil;
import com.example.luo.util.AccessTokenKeeper;
import com.example.luo.util.Constants;
import com.example.luo.util.NetworkUtils;
import com.example.luo.view.GifView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.umeng.message.PushAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.weiguan.kejian.R;

import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class WebActivity extends Activity implements OnClickListener, IWeiboHandler.Response {
	private static final String BASE_URL = "http://www.iflabs.cn/app/hellojames/html/lanuch10/index.html?id=";
//	private static final String BASE_URL = "http://www.iflabs.cn/app/hellojames/android/html/index.html?id=";

	private WebView wv;
	private MyGesture myGesture;
	private GestureDetector detector;
	private RelativeLayout loadingcontent;
	private GifView gifcontent;

	private Button bottom_back, bottom_share;
	private Dialog shareDialog, moreShareDialog;
	private Button sharemore;
	private ImageView s1, s2, s3, s4, s5;
	private ImageView bs1, bs2, bs3, bs4, bs5, bs6, bs7, bs8, bs9, bs10;
	private UMImage image;
	private String id;
	private String des;
	private String thumb;
	private String title;
	private String webUrl;
	private String articleCount;

	private IWeiboShareAPI mWeiboShareAPI = null;

	public void setId(String id) {
		this.id = id;
	}

	public void setDes(String des) {
		this.des = des;
	}
	public void setThumb(String thumb) {
		this.thumb = thumb;
//		image = new UMImage(this, thumb);
		//处理图片，保证在32KB以下
		ImageLoader imageLoader = ImageLoaderUtil.getInstance(this).getImageLoader();
		imageLoader.loadImage(thumb, new ImageLoadingListener() {
			public void onLoadingStarted(String s, View view) {}
			public void onLoadingFailed(String s, View view, FailReason failReason) {}
			public void onLoadingComplete(String s, View view, Bitmap bitmap) {
				Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(bitmap, 80, 80);
				image = new UMImage(WebActivity.this, bitmap1);
				WeakReference wrf = new WeakReference(bitmap1);
			}
			public void onLoadingCancelled(String s, View view) {}
		});

	}

	public void setTitle(String title) {
		articleCount = "文章：" + title;
		StatService.onPageStart(this, articleCount);
		this.title = title;
	}

	private OnClickListener shareClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(id != null && des != null && thumb != null && title != null) {
				ShareAction shareAction = new ShareAction(WebActivity.this)
						.setCallback(new UMShareListener() {
							@Override
							public void onResult(SHARE_MEDIA share_media) {
							}

							@Override
							public void onError(SHARE_MEDIA share_media, Throwable throwable) {
							}

							@Override
							public void onCancel(SHARE_MEDIA share_media) {
							}
						})
						.withText(des)
						.withTitle(title + " | 课间");
						if(image != null) {
							shareAction.withMedia(image);
						}
				switch(v.getId()) {
					case R.id.bs1:
					case R.id.s1:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_SHARESELECT, "微信朋友圈");
						shareAction.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
						shareAction.withTargetUrl(webUrl + "platform=wx_timeline");
						shareAction.share();
						break;
					case R.id.bs2:
					case R.id.s2:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_SHARESELECT, "微信好友");
						shareAction.setPlatform(SHARE_MEDIA.WEIXIN);
						shareAction.withTargetUrl(webUrl + "platform=wx_friend");
						shareAction.share();
						break;
					case R.id.bs3:
					case R.id.s3:
						shareAction.withText("#课间#" + des);
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_SHARESELECT, "微博");
						shareAction.withTargetUrl(webUrl + "platform=weibo");
						shareAction.setPlatform(SHARE_MEDIA.SINA);
						shareAction.share();
//						sendShareToWB();
						break;
					case R.id.bs4:
					case R.id.s4:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_SHARESELECT, "QQ好友");
						shareAction.setPlatform(SHARE_MEDIA.QQ);
						shareAction.withTargetUrl(webUrl + "platform=qq");
						shareAction.share();
						break;
					case R.id.bs5:
					case R.id.s5:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_SHARESELECT, "QQ空间");
						shareAction.setPlatform(SHARE_MEDIA.QZONE);
						shareAction.withTargetUrl(webUrl + "platform=qzone");
						shareAction.share();
						break;
					case R.id.bs6:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_SHARESELECT, "支付宝");
						shareAction.setPlatform(SHARE_MEDIA.ALIPAY);
						shareAction.withTargetUrl(webUrl);
						shareAction.share();
						break;
					case R.id.bs7:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_SHARESELECT, "发邮件");
						shareAction.withTargetUrl(webUrl);
						shareAction.setPlatform(SHARE_MEDIA.EMAIL);
						shareAction.share();
						break;
					case R.id.bs8:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_SHARESELECT, "印象笔记");
						shareAction.withTargetUrl(webUrl);
						shareAction.setPlatform(SHARE_MEDIA.EVERNOTE);
						shareAction.share();
						break;
					case R.id.bs9:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_SHARESELECT, "短信");
						shareAction.withTargetUrl(webUrl);
						shareAction.setPlatform(SHARE_MEDIA.SMS);
						shareAction.share();
						break;
					case R.id.bs10:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_SHARESELECT, "腾讯微博	");
						shareAction.withTargetUrl(webUrl);
						shareAction.setPlatform(SHARE_MEDIA.TENCENT);
						shareAction.share();
						break;
				}
			} else {
				Toast.makeText(WebActivity.this, "data not ready", Toast.LENGTH_LONG).show();
			}
		}
	};

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
		// 来接收微博客户端返回的数据；执行成功，返回 true，并调用
		// {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);

		if(!NetworkUtils.isNetworkAvailable(this)) {
			Toast.makeText(WebActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
		}
		
		PushAgent.getInstance(this).onAppStart();

		if (Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

//		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);
//		mWeiboShareAPI.registerApp();
//		if (savedInstanceState != null) {
//			mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
//		}

		gifcontent = (GifView) findViewById(R.id.gifcontent);
		loadingcontent = (RelativeLayout) findViewById(R.id.loadingcontent);
		loadingcontent.setVisibility(View.VISIBLE);
		gifcontent.setMovieResource(R.raw.loading);

		bottom_back = (Button) findViewById(R.id.bottom_back);
		bottom_share = (Button) findViewById(R.id.bottom_share);
		bottom_back.setOnClickListener(this);
		bottom_share.setOnClickListener(this);
		init();

		shareDialog = new Dialog(WebActivity.this, R.style.dialog);
		View shareDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_share, null);
		sharemore = (Button) shareDialogView.findViewById(R.id.sharemore);
		sharemore.setOnClickListener(this);
		s1 = (ImageView) shareDialogView.findViewById(R.id.s1);
		s2 = (ImageView) shareDialogView.findViewById(R.id.s2);
		s3 = (ImageView) shareDialogView.findViewById(R.id.s3);
		s4 = (ImageView) shareDialogView.findViewById(R.id.s4);
		s5 = (ImageView) shareDialogView.findViewById(R.id.s5);
		s1.setOnClickListener(shareClick);
		s2.setOnClickListener(shareClick);
		s3.setOnClickListener(shareClick);
		s4.setOnClickListener(shareClick);
		s5.setOnClickListener(shareClick);
		shareDialog.setContentView(shareDialogView);
		int w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		shareDialogView.measure(w, h);
		int height = shareDialogView.getMeasuredHeight();
		int width = shareDialogView.getMeasuredWidth();
		Window window = shareDialog.getWindow();
		WindowManager.LayoutParams pm = window.getAttributes();
		pm.height = height;
		window.setAttributes(pm);
		window.setGravity(Gravity.BOTTOM);

		moreShareDialog = new Dialog(WebActivity.this, R.style.dialog);
		View shareMore = LayoutInflater.from(this).inflate(R.layout.dialog_allshare, null);
		bs1 = (ImageView) shareMore.findViewById(R.id.bs1);
		bs2 = (ImageView) shareMore.findViewById(R.id.bs2);
		bs3 = (ImageView) shareMore.findViewById(R.id.bs3);
		bs4 = (ImageView) shareMore.findViewById(R.id.bs4);
		bs5 = (ImageView) shareMore.findViewById(R.id.bs5);
		bs6 = (ImageView) shareMore.findViewById(R.id.bs6);
		bs7 = (ImageView) shareMore.findViewById(R.id.bs7);
		bs8 = (ImageView) shareMore.findViewById(R.id.bs8);
		bs9 = (ImageView) shareMore.findViewById(R.id.bs9);
		bs10 = (ImageView) shareMore.findViewById(R.id.bs10);
		bs1.setOnClickListener(shareClick);
		bs2.setOnClickListener(shareClick);
		bs3.setOnClickListener(shareClick);
		bs4.setOnClickListener(shareClick);
		bs5.setOnClickListener(shareClick);
		bs6.setOnClickListener(shareClick);
		bs7.setOnClickListener(shareClick);
		bs8.setOnClickListener(shareClick);
		bs9.setOnClickListener(shareClick);
		bs10.setOnClickListener(shareClick);
		moreShareDialog.setContentView(shareMore);
		w = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		h = View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED);
		shareMore.measure(w, h);
		height = shareMore.getMeasuredHeight();
		width = shareMore.getMeasuredWidth();
		window = moreShareDialog.getWindow();
		pm = window.getAttributes();
		pm.height = height;
		window.setAttributes(pm);
		window.setGravity(Gravity.BOTTOM);
	}

	public void initShareContent(String data) {
		try {
			JSONObject json = new JSONObject(data);
			id = json.getString("id");
			thumb = json.getString("thumb");
			title = json.getString("title");
			des = json.getString("des");
			image = new UMImage(WebActivity.this, thumb);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String content;
	private void init() {
		Intent intent = getIntent();
		String id = intent.getStringExtra("id");
		String catText = intent.getStringExtra("catText");
		String goodname = intent.getStringExtra("goodname");
		String tag = intent.getStringExtra("tag");
		if(catText != null) {
			content = "从" + catText + "进入文章";
			StatService.onPageStart(this, content);
		} else if(goodname != null) {
			content = "购物：" + goodname;
			StatService.onPageStart(this, content);
		} else if(tag != null) {
			content = tag;
			StatService.onPageStart(this, content);
		}
		if (id == null || "".equals(id)) {
			webUrl = intent.getStringExtra("url");
			Log.i("isUrl" , webUrl);
			bottom_share.setVisibility(View.GONE);
		} else {
			webUrl = BASE_URL + id;
		}
		web();
		loadurl(wv, webUrl);
	}

	@SuppressLint("AddJavascriptInterface")
	private void web() {
		wv = (WebView) findViewById(R.id.webv);
		wv.getSettings().setJavaScriptEnabled(true);// 可用JS
		wv.setScrollBarStyle(0);// 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
		wv.addJavascriptInterface(new AndroidInterface(this), "AndroidInterface");
		wv.setWebViewClient(new WebViewClient() {
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
				if(url.startsWith("http")) {
					loadurl(view, url);// 载入网页
				}
				return true;
			}// 重写点击动作,用webview载入

		});
		wv.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}

			@Override
			public void onReceivedIcon(WebView view, Bitmap icon) {
				super.onReceivedIcon(view, icon);
			}

			@Override
			public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
				super.onReceivedTouchIconUrl(view, url, precomposed);
			}

			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				super.onShowCustomView(view, callback);
			}

			@Override
			public void onHideCustomView() {
				super.onHideCustomView();
			}

			@Override
			public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
				return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
			}

			@Override
			public void onRequestFocus(WebView view) {
				super.onRequestFocus(view);
			}

			@Override
			public void onCloseWindow(WebView window) {
				super.onCloseWindow(window);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}

			@Override
			public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
				return super.onJsConfirm(view, url, message, result);
			}

			@Override
			public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
				return super.onJsPrompt(view, url, message, defaultValue, result);
			}

			@Override
			public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
				return super.onJsBeforeUnload(view, url, message, result);
			}

			@Override
			public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}

			@Override
			public void onGeolocationPermissionsHidePrompt() {
				super.onGeolocationPermissionsHidePrompt();
			}

			@Override
			public void onPermissionRequest(PermissionRequest request) {
				super.onPermissionRequest(request);
			}

			@Override
			public void onPermissionRequestCanceled(PermissionRequest request) {
				super.onPermissionRequestCanceled(request);
			}

			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				return super.onConsoleMessage(consoleMessage);
			}

			@Override
			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
				return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
			}

			public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
				super.onProgressChanged(view, progress);
			}
		});
		myGesture = new MyGesture(this);
		detector = new GestureDetector(WebActivity.this, myGesture);
		wv.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				detector.onTouchEvent(event);
				return false;
			}
		});

	}


	public void loadurl(final WebView view, final String url) {
		view.loadUrl(url);// 载入网页
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sharemore:
				shareDialog.dismiss();
				moreShareDialog.show();
				break;
			case R.id.bottom_back:
				finish();
				break;
			case R.id.bottom_share:
				if (!shareDialog.isShowing()) {
					StatService.onEvent(this, Event.EVENT_ID_SHAREOPEN, title + "");
					shareDialog.show();
				}
				break;
		}
	}

	@Override
	public void onResponse(BaseResponse baseResponse) {
		switch (baseResponse.errCode) {
			case WBConstants.ErrorCode.ERR_OK:
				Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
				break;
			case WBConstants.ErrorCode.ERR_CANCEL:
				Toast.makeText(this, "分享取消", Toast.LENGTH_LONG).show();
				break;
			case WBConstants.ErrorCode.ERR_FAIL:
				Toast.makeText(this,
						"Error Message: " + baseResponse.errMsg,
						Toast.LENGTH_LONG).show();
				break;
		}
	}

	public void sendShareToWB() {
		WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
//		weiboMessage.textObject = getTextObj();
//		weiboMessage.imageObject = getImageObj();
		weiboMessage.mediaObject = getWebpageObj();

		// 2. 初始化从第三方到微博的消息请求
		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		// 用transaction唯一标识一个请求
		request.transaction = String.valueOf(System.currentTimeMillis());
		request.multiMessage = weiboMessage;
		AuthInfo authInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
		Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
		String token = "";
		if (accessToken != null) {
			token = accessToken.getToken();
		}
		mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {

			@Override
			public void onWeiboException( WeiboException e ) {
				Toast.makeText(WebActivity.this,
						"Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
						.show();

			}

			@Override
			public void onComplete( Bundle bundle ) {
				// TODO Auto-generated method stub
				Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
				AccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
			}

			@Override
			public void onCancel() {
			}
		});

	}

	/**
	 * 创建多媒体（网页）消息对象。
	 *
	 * @return 多媒体（网页）消息对象。
	 */
	private WebpageObject getWebpageObj() {
		WebpageObject mediaObject = new WebpageObject();
		mediaObject.identify = Utility.generateGUID();
		mediaObject.title = title + " | 课间";
		mediaObject.description = des;

		Bitmap bitmap = ImageLoaderUtil.getInstance(this).getImageLoader().loadImageSync(thumb);

		// 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
		mediaObject.setThumbImage(bitmap);
		mediaObject.actionUrl = webUrl;
		mediaObject.defaultText = "Webpage";
		return mediaObject;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult( requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		wv.reload();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(content != null) {
			StatService.onPageEnd(this, content);
		}
		if(articleCount != null) {
			StatService.onPageEnd(this, articleCount);
		}
	}
}
