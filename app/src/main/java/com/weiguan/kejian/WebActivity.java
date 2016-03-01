package com.weiguan.kejian;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.mobstat.StatService;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.commons.MyGesture;
import com.weiguan.kejian.http.AndroidInterface;
import com.weiguan.kejian.http.NetworkData;
import com.weiguan.kejian.imgage.ImageLoaderUtil;
import com.weiguan.kejian.util.AccessTokenKeeper;
import com.weiguan.kejian.util.Constants;
import com.weiguan.kejian.util.NetworkUtils;
import com.weiguan.kejian.util.ScreenUtils;
import com.weiguan.kejian.view.GifView;
import com.weiguan.kejian.view.MatrixImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.constant.WBConstants;
import com.umeng.message.PushAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class WebActivity extends BaseAnimationActivity implements OnClickListener, IWeiboHandler.Response {
	private static final String BASE_URL = "http://www.iflabs.cn/app/hellojames/html/lanuch21/index.html?id=";
//	private static final String BASE_URL = "http://www.iflabs.cn/app/hellojames/android/html/index.html?id=";

	private WebView wv;
	private MyGesture myGesture;
	private GestureDetector detector;
	private RelativeLayout loadingcontent;
	private GifView gifcontent;
	private RelativeLayout likely, talkly;
	private Button like, talk;
	private TextView likecount, talkcount;
	private MyApplication myApp;
	private LinearLayout commently, bottombar, hiddenly;
	private CheckBox btn_isnoname;
	private EditText et_comment;

	private Dialog d;

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

	private String commentId;
	private String commentUsername;
	private String commentUserId;

	public void setCommentUserId(String commentUserId) {
		this.commentUserId = commentUserId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public void setCommentUsername(String commentUsername) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if(myApp.user.userid != null && !"".equals(myApp.user.userid)) {
					showComment();
				} else {
					startActivity(new Intent(WebActivity.this, LoginActivity.class));
				}
			}
		});
		this.commentUsername = commentUsername;
	}

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
			public void onLoadingStarted(String s, View view) {
			}

			public void onLoadingFailed(String s, View view, FailReason failReason) {
			}

			public void onLoadingComplete(String s, View view, Bitmap bitmap) {
				if(bitmap.getByteCount() < 32 * 1024) {
					image = new UMImage(WebActivity.this, bitmap);
				} else {
					Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(bitmap, 320, 320);
					WeakReference wrf = new WeakReference(bitmap1);
					image = new UMImage(WebActivity.this, bitmap1);
				}
			}

			public void onLoadingCancelled(String s, View view) {
			}
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
								if(share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
									StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-微信朋友圈-success");
								} else if(share_media == SHARE_MEDIA.WEIXIN) {
									StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-微信好友-success");
								} else if(share_media == SHARE_MEDIA.SINA) {
									StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-微博-success");
								} else if(share_media == SHARE_MEDIA.QQ) {
									StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-QQ好友-success");
								} else if(share_media == SHARE_MEDIA.QZONE) {
									StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-QQ空间-success");
								} else if(share_media == SHARE_MEDIA.ALIPAY) {
									StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-支付宝-success");
								} else if(share_media == SHARE_MEDIA.EMAIL) {
									StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-邮件-success");
								} else if(share_media == SHARE_MEDIA.EVERNOTE) {
									StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-印象笔记-success");
								} else if(share_media == SHARE_MEDIA.SMS) {
									StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-短信-success");
								} else if(share_media == SHARE_MEDIA.TENCENT) {
									StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-QQ微博-success");
								}
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
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-微信朋友圈-select");
						shareAction.setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE);
						shareAction.withTargetUrl(webUrl + "&platform=wx_timeline");
						shareAction.share();
						break;
					case R.id.bs2:
					case R.id.s2:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-微信好友-select");
						shareAction.setPlatform(SHARE_MEDIA.WEIXIN);
						shareAction.withTargetUrl(webUrl + "&platform=wx_friend");
						shareAction.share();
						break;
					case R.id.bs3:
					case R.id.s3:
						shareAction.withText("#课间#" + des);
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-微博-select");
						shareAction.withTargetUrl(webUrl + "&platform=weibo");
						shareAction.setPlatform(SHARE_MEDIA.SINA);
						shareAction.share();
//						sendShareToWB();
						break;
					case R.id.bs4:
					case R.id.s4:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-QQ好友-select");
						shareAction.setPlatform(SHARE_MEDIA.QQ);
						shareAction.withTargetUrl(webUrl + "&platform=qq");
						shareAction.share();
						break;
					case R.id.bs5:
					case R.id.s5:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-QQ空间-select");
						shareAction.setPlatform(SHARE_MEDIA.QZONE);
						shareAction.withTargetUrl(webUrl + "&platform=qzone");
						shareAction.share();
						break;
					case R.id.bs6:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-支付宝-select");
						shareAction.setPlatform(SHARE_MEDIA.ALIPAY);
						shareAction.withTargetUrl(webUrl);
						shareAction.share();
						break;
					case R.id.bs7:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-发邮件-select");
						shareAction.withTargetUrl(webUrl);
						shareAction.setPlatform(SHARE_MEDIA.EMAIL);
						shareAction.share();
						break;
					case R.id.bs8:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-印象笔记-select");
						shareAction.withTargetUrl(webUrl);
						shareAction.setPlatform(SHARE_MEDIA.EVERNOTE);
						shareAction.share();
						break;
					case R.id.bs9:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-短信-select");
						shareAction.withTargetUrl(webUrl);
						shareAction.setPlatform(SHARE_MEDIA.SMS);
						shareAction.share();
						break;
					case R.id.bs10:
						StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "分享-腾讯微博-select");
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
		myApp = (MyApplication) getApplicationContext();

		bottombar = (LinearLayout) findViewById(R.id.bottombar);
		hiddenly = (LinearLayout) findViewById(R.id.hiddenly);
		commently = (LinearLayout) findViewById(R.id.commently);
		commently.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		btn_isnoname = (CheckBox) findViewById(R.id.btn_isnoname);
		et_comment = (EditText) findViewById(R.id.et_comment);

		hiddenly.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btn_isnoname.performClick();
			}
		});

		btn_isnoname.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "选择匿名");
				} else {
					StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "取消匿名");
				}
			}
		});

		et_comment.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				Log.i("onKey", keyCode + "--" + event.getAction());
				if(keyCode == KeyEvent.KEYCODE_BACK) {
					dismissComment();
				}
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					String news_comment_private = btn_isnoname.isChecked() ? "1" : "0";
					String content = et_comment.getText().toString().trim();
					if(content.length() > 0) {
						if(!et_comment.getHint().toString().equals("")) {
							StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "回复评论-send");
						} else {
							StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "评论-send");
						}
						NetworkData.addNewsComment(myApp.user.userid, myApp.user.token, myApp.uuid,
								getIntent().getStringExtra("id"), getIntent().getStringExtra("catId"),
								myApp.user.nickname, content,
								commentUserId, commentUsername, commentId, news_comment_private, new NetworkData.NetworkCallback() {
									@Override
									public void callback(String data) {
										Log.i("info", data);
										try {
											JSONObject jData = new JSONObject(data);
											String result = jData.getString("result");
											if ("0".equals(result)) {
												handler.sendEmptyMessage(5);
												Message msg = Message.obtain();
												msg.what = 2;
												msg.obj = "评论成功";
												handler.sendMessage(msg);
											} else {
												Message msg = Message.obtain();
												msg.what = 2;
												msg.obj = jData.getString("info");
												handler.sendMessage(msg);
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
						);
						dismissComment();
					} else {
						Toast.makeText(WebActivity.this, "请输入评论内容", Toast.LENGTH_SHORT).show();
					}
				}
				return false;
			}
		});


		likely = (RelativeLayout) findViewById(R.id.likely);
		talkly = (RelativeLayout) findViewById(R.id.talkly);
		like = (Button) findViewById(R.id.like);
		talk = (Button) findViewById(R.id.talk);
		likecount = (TextView) findViewById(R.id.likecount);
		talkcount = (TextView) findViewById(R.id.talkcount);

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
		window.setWindowAnimations(R.style.dialogWindowAnim);
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
		window.setWindowAnimations(R.style.dialogWindowAnim);
		pm = window.getAttributes();
		pm.height = height;
		window.setAttributes(pm);
		window.setGravity(Gravity.BOTTOM);
	}

	public void showComment() {
		bottombar.setVisibility(View.GONE);
		commently.setVisibility(View.VISIBLE);
		commently.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
		et_comment.requestFocus();
		InputMethodManager inputManager =
				(InputMethodManager)et_comment.getContext().getSystemService(INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(et_comment, 0);
		if(commentUsername != null) {
			et_comment.setHint("@" + commentUsername);
		} else {
			et_comment.setHint("请输入评论");
		}
	}

	public void dismissComment() {
		if(commently.getVisibility() == View.VISIBLE) {
			try {
				Animation outAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out);
				outAnim.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						bottombar.setVisibility(View.VISIBLE);
						bottombar.startAnimation(AnimationUtils.loadAnimation(WebActivity.this, R.anim.fade_in));
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});
				commently.setVisibility(View.GONE);
				commently.startAnimation(outAnim);

				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(et_comment.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				this.commentUsername = null;
				setCommentUserId(null);
				setCommentId(null);
				et_comment.getText().clear();
				et_comment.clearFocus();
			} catch (Exception e) {
				e.printStackTrace();
			}
//			handler.sendEmptyMessage(6);
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case 7:
					if(d != null && d.isShowing()) {
						d.dismiss();
					}
					View v = LayoutInflater.from(WebActivity.this).inflate(R.layout.dialog_bigpic, null);
					MatrixImageView iv = (MatrixImageView) v.findViewById(R.id.iv);
					d = new Dialog(WebActivity.this, R.style.dialog);
					d.setContentView(v);
					Window window = d.getWindow();
					WindowManager.LayoutParams lp = window.getAttributes();
					lp.width = ScreenUtils.getScreenWidth(WebActivity.this);
					lp.height = ScreenUtils.getScreenHeight(WebActivity.this);
					window.setAttributes(lp);
					myApp.displayImage(msg.obj.toString(), iv);
					iv.setClickListener(new MatrixImageView.MIVOnClickListener() {
						@Override
						public void onClick(View v) {
							d.dismiss();
						}
					});
					d.show();

					break;
				case 6:
					bottombar.setVisibility(View.VISIBLE);
					break;
				case 5:
					wv.loadUrl("javascript:getCommentData()");
					if(msg.obj != null && "1".equals(msg.obj.toString())) {
						//评论点赞
					} else if(msg.obj != null && "0".equals(msg.obj.toString())) {
						//评论取消点赞
					} else {
						talkcount.setText((Integer.parseInt(talkcount.getText().toString()) + 1) + "");
					}
					break;
				case 4:
					//取消收藏
					like.setBackgroundResource(R.drawable.like_btn);
					like.setTag("1");
					String text = likecount.getText().toString();
					int i = Integer.parseInt(text);
					if(i > 0) {
						i = i - 1;
						likecount.setText(i + "");
					} else {
						likecount.setText("0");
					}
					break;
				case 3:
					//收藏
					like.setBackgroundResource(R.drawable.like_btn_select);
					like.setTag("2");
					String t = likecount.getText().toString();
					int ii = Integer.parseInt(t) + 1;
					likecount.setText(ii + "");
					break;
				case 2:
					Toast.makeText(WebActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
					break;
				case 1:
					try {
						Log.i("like", msg.obj.toString());
						JSONObject jData = new JSONObject(msg.obj.toString());
						String result = jData.getString("result");
						String totalLike = jData.getString("totalLike");
						String totalComment = jData.getString("totalComment");
						if("0".equals(result)) {
							//收藏了
							like.setBackgroundResource(R.drawable.like_btn_select);
							like.setTag("2");
						} else {
							like.setBackgroundResource(R.drawable.like_btn);
							like.setTag("1");
						}
						likecount.setText(totalLike);
						talkcount.setText(totalComment);

						initListener();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
			}
		}
	};

	private void initListener() {
		OnClickListener talkListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (myApp.user.userid != null && !"".equals(myApp.user.userid)) {
					StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "评论-select");
					showComment();
				} else {
					StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "评论-未登录");
					startActivity(new Intent(WebActivity.this, LoginActivity.class));
				}
			}
		};
		talkly.setOnClickListener(talkListener);
		talk.setOnClickListener(talkListener);

		OnClickListener likeListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (myApp.user.userid != null && !"".equals(myApp.user.userid)) {
					if (getIntent().getStringExtra("id") != null && !"".equals(getIntent().getStringExtra("id"))) {
						if (like.getTag().equals("1")) {
							StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "收藏-select");
							addLike();
						} else if (like.getTag().equals("2")) {
							StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "取消收藏-select");
							removeLike();
						}
					}
				} else {
					StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "收藏-未登录");
					startActivity(new Intent(WebActivity.this, LoginActivity.class));
				}
			}
		};

		like.setOnClickListener(likeListener);
		likely.setOnClickListener(likeListener);
	}

	public void removeLike(String id) {
		NetworkData.removeNewsCommentSupport(myApp.user.userid, myApp.user.token, myApp.uuid, id, new NetworkData.NetworkCallback() {
			@Override
			public void callback(String data) {
				try {
					Log.i("addNewsCommentSupport", data);
					JSONObject jData = new JSONObject(data);
					String result = jData.getString("result");
					if ("0".equals(result)) {
						Message m = Message.obtain(handler, 5, "0");
						handler.sendMessage(m);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void removeLike() {
		NetworkData.removeFavorites(myApp.user.userid, myApp.user.token, myApp.uuid, getIntent().getStringExtra("id"), new NetworkData.NetworkCallback() {
			@Override
			public void callback(String data) {
				try {
					Log.i("removelike", data);
					JSONObject jData = new JSONObject(data);
					String result = jData.getString("result");
					if ("0".equals(result)) {
						handler.sendEmptyMessage(4);
					}
					Message msg = Message.obtain();
					msg.what = 2;
					msg.obj = jData.getString("info");
					handler.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void addLike(String id) {
		if(myApp.user.userid != null && !"".equals(myApp.user.userid)) {
			NetworkData.addNewsCommentSupport(myApp.user.userid, myApp.user.token, myApp.uuid, id, new NetworkData.NetworkCallback() {
				@Override
				public void callback(String data) {
					try {
						Log.i("addNewsCommentSupport", data);
						JSONObject jData = new JSONObject(data);
						String result = jData.getString("result");
						if ("0".equals(result)) {
							Message m = Message.obtain(handler, 5, "1");
							handler.sendMessage(m);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			startActivity(new Intent(WebActivity.this, LoginActivity.class));
		}
	}

	private void addLike() {
		NetworkData.addFavorites(myApp.user.userid, myApp.user.token, myApp.uuid, getIntent().getStringExtra("id"), new NetworkData.NetworkCallback() {
			@Override
			public void callback(String data) {
				try {
					Log.i("addlike", data);
					JSONObject jData = new JSONObject(data);
					String result = jData.getString("result");
					if ("0".equals(result)) {
						handler.sendEmptyMessage(3);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void requestLikeInfo() {
		String newsId = getIntent().getStringExtra("id");
		String catId = getIntent().getStringExtra("catId");
		if(myApp.user.userid != null && !"".equals(myApp.user.userid)) {
			NetworkData.checkNewsLike(myApp.user.userid, newsId, catId, new NetworkData.NetworkCallback() {
				@Override
				public void callback(String data) {
					handler.sendMessage(Message.obtain(handler, 1, data));
				}
			});
		} else {
			NetworkData.checkNewsLike("-1", newsId, catId, new NetworkData.NetworkCallback() {
				@Override
				public void callback(String data) {
					handler.sendMessage(Message.obtain(handler, 1, data));
				}
			});
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
			likely.setVisibility(View.GONE);
			talkly.setVisibility(View.GONE);
		} else {
			requestLikeInfo();
			webUrl = BASE_URL + id;
			if(myApp.user.userid != null && !"".equals(myApp.user.userid)) {
				webUrl = webUrl + "&userid=" + myApp.user.userid;
			} else {
				webUrl = webUrl + "&userid=0";
			}
		}
		Log.i("web_url", webUrl);
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
		wv.setWebChromeClient(new WebChromeClient());
		myGesture = new MyGesture(this);
		detector = new GestureDetector(WebActivity.this, myGesture);
		wv.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (commently.getVisibility() == View.VISIBLE) {
					dismissComment();
				}
				detector.onTouchEvent(event);
				return false;
			}
		});

	}


	public void loadurl(final WebView view, final String url) {
		view.loadUrl(url);// 载入网页
	}

	/**
	 * 基于质量的压缩算法， 此方法未 解决压缩后图像失真问题
	 * <br> 可先调用比例压缩适当压缩图片后，再调用此方法可解决上述问题
	 * @param bts
	 * @param maxBytes 压缩后的图像最大大小 单位为byte
	 * @return
	 */
	public final static Bitmap compressBitmap(Bitmap bitmap, long maxBytes) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			int options = 90;
			while (baos.toByteArray().length > maxBytes) {
				baos.reset();
				bitmap.compress(Bitmap.CompressFormat.PNG, options, baos);
				options -= 10;
			}
			byte[] bts = baos.toByteArray();
			Bitmap bmp = BitmapFactory.decodeByteArray(bts, 0, bts.length);
			baos.close();
			return bmp;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
					StatService.onEvent(WebActivity.this, Event.EVENT_ID_ARTICLE, "转发按钮-select：" + title);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		if(commently.getVisibility() == View.VISIBLE) {
			commently.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
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

	public void showBigPic(String url) {
		handler.sendMessage(Message.obtain(handler, 7, url));
	}
}
