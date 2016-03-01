package com.weiguan.kejian;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.weiguan.kejian.adapter.LeftListAdapter;
import com.weiguan.kejian.adapter.MyFragmentPagerAdapter;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.fragment.GoodsFragment;
import com.weiguan.kejian.fragment.InformationFragment;
import com.weiguan.kejian.http.HttpUtils;
import com.weiguan.kejian.http.NetworkData;
import com.weiguan.kejian.model.LeftModel;
import com.weiguan.kejian.util.AutoInstall;
import com.weiguan.kejian.util.HexStringUtil;
import com.weiguan.kejian.util.ListViewUtils;
import com.weiguan.kejian.util.MailUtil;
import com.weiguan.kejian.util.NetworkUtils;
import com.weiguan.kejian.view.CircleImageView;
import com.weiguan.kejian.view.LineDrawer;
import com.jeremyfeinstein.slidingmenu.lib.CustomViewAbove;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nineoldandroids.animation.ObjectAnimator;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private static final int CANFINISH = -1;
    private static final int UPDATE_LEFTLIST = 1;
    private static final int UPDATE_HOMELIST = 2;
    private static final int VERSION_UPDATE = 3;
    private static String APK_URL = Environment.getExternalStorageDirectory() + "/Download";

    public int activityCloseEnterAnimation;
    public int activityCloseExitAnimation;
    public int activityOpenEnterAnimation;
    public int activityOpenExitAnimation;


    private boolean canFinish;

    private MyApplication myApp;

    private ViewPager mainpager;
    private ViewPager.OnPageChangeListener pageListener;
    private MyFragmentPagerAdapter adapter;
    private ArrayList<Fragment> fragments;
    private RelativeLayout line_pink, line_blue, line_green;

    private InformationFragment information;
    private GoodsFragment goods;

    private RelativeLayout mohulayout;
    private LinearLayout blayout1;
    private RelativeLayout blayout2;
    private TextView centertext;
    private boolean hasChoose;
    private RelativeLayout btn1, btn2, btn3, btn4;
    private LinearLayout btn4line, btn3line;

    private SlidingMenu menu;
    private Button search;
    private LinearLayout leftmonitor;
    private RelativeLayout line_view_blue;

    private ScrollView LeftView;
    private LinearLayout loginlayout, logoutlayout, comment, collect_article;
    private CircleImageView user_pic;
    private TextView user_name, user_level, collect_tv;
    private Button left_btn_login, about, contribute, setting;
    private ListView leftlist;
    private ArrayList<LeftModel> leftModels;
    private LeftListAdapter leftAdapter;
    public HashMap<String, String> catIdText = new HashMap<>();

    private long myDownloadReference;
    private Dialog updateDialog;
    private DownloadManager downloadManager;
    IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (myDownloadReference == reference) {
                //安装APK
                Uri uriForDownloadedFile = downloadManager.getUriForDownloadedFile(myDownloadReference);
                APK_URL = uriForDownloadedFile.getPath();
                AutoInstall.setUrl(APK_URL);
                AutoInstall.install(MainActivity.this);
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case CANFINISH:
                    canFinish = false;
                    break;
                case UPDATE_LEFTLIST:
                    leftAdapter.notifyData(leftModels);
                    ListViewUtils.setListViewHeightBasedOnChildren(leftlist);
                    break;
                case VERSION_UPDATE:
                    String serviceString = Context.DOWNLOAD_SERVICE;
                    downloadManager = (DownloadManager) getSystemService(serviceString);
                    if(updateDialog == null) {
                        Bundle data = msg.getData();
                        final String applink = data.getString("applink");
                        updateDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("更新提示")
                        .setMessage(data.getString("updateContent"))
                        .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateDialog.dismiss();
                                //开始下载Apk
                                File f = new File(APK_URL);
                                if(!f.exists()) {
                                    f.mkdirs();
                                }
                                APK_URL = APK_URL + "/Kejian.apk";
                                f = new File(APK_URL);
                                if(applink != null && !"".equals(applink)) {
                                    Uri uri = Uri.parse(applink);
                                    DownloadManager.Request request = new DownloadManager.Request(uri);
                                    request.setDestinationUri(Uri.fromFile(f));
                                    myDownloadReference = downloadManager.enqueue(request);
                                }
                            }
                        }).create();
                    }
                    updateDialog.show();
                    break;
            }
        }
    };
    private PushAgent mPushAgent;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[] {android.R.attr.windowAnimationStyle});
//        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
//        activityStyle.recycle();
//        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId, new int[] {android.R.attr.activityCloseEnterAnimation, android.R.attr.activityCloseExitAnimation,
//                android.R.attr.activityOpenEnterAnimation, android.R.attr.activityOpenExitAnimation});
//        activityCloseEnterAnimation = activityStyle.getResourceId(0, 0);
//        activityCloseExitAnimation = activityStyle.getResourceId(1, 0);
//        activityOpenEnterAnimation = activityStyle.getResourceId(2, 0);
//        activityOpenExitAnimation = activityStyle.getResourceId(3, 0);
//        activityStyle.recycle();

        setContentView(R.layout.activity_main);

        registerReceiver(receiver, filter);
        myApp = (MyApplication) getApplicationContext();
        catIdText = myApp.catIdText;

        sp = getSharedPreferences("cache", MODE_PRIVATE);
        mPushAgent = PushAgent.getInstance(this);

        mPushAgent.setDebugMode(true);

        if(sp.getBoolean("isPush", true)) {
            mPushAgent.enable(new IUmengRegisterCallback() {
                @Override
                public void onRegistered(String s) {
                    Log.i("onRegistered", s);
                }
            });
        }
        PushAgent.getInstance(this).onAppStart();

        init();

        initMenu();

        requestLeftInfo();

        NetworkData.getVerionInfo(new NetworkData.NetworkCallback() {
            @Override
            public void callback(String data) {
                try {
                    Log.i("versionInfo", data);
                    JSONObject json = new JSONObject(data);
                    String version = json.getString("version");
                    String applink = json.getString("applink");
                    String updateContent = json.getString("updateContent");
                    updateContent = updateContent.replace("\\n", "\n");
                    Log.i("versionInfo", updateContent);
                    PackageManager manager = getPackageManager();
                    PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
                    int versionCode = info.versionCode;
                    if(Float.parseFloat(version) * 10 > versionCode) {
                        Message obtain = Message.obtain();
                        Bundle b = new Bundle();
                        b.putString("version", version);
                        b.putString("applink", applink);
                        b.putString("updateContent", updateContent);
                        obtain.what = VERSION_UPDATE;
                        obtain.setData(b);
                        handler.sendMessage(obtain);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserInfo();
    }

    private void requestHomeInfo(int id) {
        information.requestHomeInfo(id);
    }

    private void requestLeftInfo() {
        if(NetworkUtils.isNetworkAvailable(MainActivity.this)) {
            NetworkData.getLeftMenuData(new NetworkData.NetworkCallback() {
                @Override
                public void callback(String data) {
                    try {
                        JSONObject jsonData = new JSONObject(data);
                        JSONArray catList = jsonData.getJSONArray("catList");
                        leftModels.clear();
                        for (int i = 0; i < catList.length(); i++) {
                            JSONObject jo = catList.getJSONObject(i);
                            LeftModel lm = new LeftModel();
                            if (i == 0) {
                                lm.isClick = true;
                            }
                            lm.color = jo.getString("color");
                            lm.id = jo.getString("id");
                            lm.image = jo.getString("image");
                            lm.name = jo.getString("name");
                            leftModels.add(lm);
                            catIdText.put(lm.id, lm.name);
                        }
                        handler.sendEmptyMessage(UPDATE_LEFTLIST);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
        }
    }

    public void initMenu() {
        LeftView = (ScrollView) LayoutInflater.from(this).inflate(R.layout.leftmenu, null);
        LeftView.setVerticalScrollBarEnabled(false);

        logoutlayout = (LinearLayout) LeftView.findViewById(R.id.logoutlayout);
        comment = (LinearLayout) LeftView.findViewById(R.id.comment);
        collect_article = (LinearLayout) LeftView.findViewById(R.id.collect_article);
        collect_tv = (TextView) LeftView.findViewById(R.id.collect_tv);
        loginlayout = (LinearLayout) LeftView.findViewById(R.id.loginlayout);
        user_pic = (CircleImageView) LeftView.findViewById(R.id.user_pic);
        user_name = (TextView) LeftView.findViewById(R.id.user_name);
        user_level = (TextView) LeftView.findViewById(R.id.user_level);

        left_btn_login = (Button) LeftView.findViewById(R.id.left_btn_login);
        about = (Button) LeftView.findViewById(R.id.about);
        contribute = (Button) LeftView.findViewById(R.id.contribute);
        setting = (Button) LeftView.findViewById(R.id.setting);

        comment.setOnClickListener(this);
        collect_article.setOnClickListener(this);
        user_pic.setOnClickListener(this);
        left_btn_login.setOnClickListener(this);
        about.setOnClickListener(this);
        contribute.setOnClickListener(this);
        setting.setOnClickListener(this);

        leftlist = (ListView) LeftView.findViewById(R.id.leftlist);
        leftModels = new ArrayList<>();
        leftAdapter = new LeftListAdapter(this, leftModels);
        leftlist.setAdapter(leftAdapter);
        leftlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(NetworkUtils.isNetworkAvailable(MainActivity.this)) {
                    collect_tv.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.gray));
                    menu.showContent();
                    LeftModel leftModel = leftModels.get(position);
                    if (position == 0) {
                        StatService.onEvent(MainActivity.this, Event.EVENT_ID_NAVIGATION, "首页-栏目-select");
                        requestHomeInfo(-1);
                        blayout2.setVisibility(View.GONE);
                        blayout1.setVisibility(View.VISIBLE);

                        mainpager.removeAllViews();
                        fragments.clear();
                        fragments.add(information);
                        fragments.add(goods);
                        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
                        mainpager.setAdapter(adapter);

                    } else {
                        blayout2.setVisibility(View.VISIBLE);
                        blayout1.setVisibility(View.INVISIBLE);
                        centertext.setText(leftModel.name);

                        mainpager.removeAllViews();
                        fragments.clear();
                        fragments.add(information);
                        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
                        mainpager.setAdapter(adapter);
                        StatService.onEvent(MainActivity.this, Event.EVENT_ID_NAVIGATION, myApp.catIdText.get(leftModel.id) + "-栏目-select");
                        requestHomeInfo(Integer.parseInt(leftModel.id));
                    }
                    for (LeftModel lm : leftModels) {
                        lm.isClick = false;
                    }
                    leftModel.isClick = true;
                    leftAdapter.notifyDataSetChanged();

                    leftToTop();
                } else {
                    Toast.makeText(MainActivity.this, "网络不可用", Toast.LENGTH_SHORT).show();
                }
            }
        });

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
//        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
//        menu.setShadowDrawable(R.drawable.shadow);

        // 设置滑动菜单视图的宽度
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0f);
        /**
         * SLIDING_WINDOW will include the Title/ActionBar in the content
         * section of the SlidingMenu, while SLIDING_CONTENT does not.
         */
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

        //为侧滑菜单设置布局
        menu.setMenu(LeftView);

        menu.setCanResponseView(search);

        menu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
            @Override
            public void onOpen() {
                leftToTop();
                start();
            }
        });

        menu.setOnCloseListener(new SlidingMenu.OnCloseListener() {
            @Override
            public void onClose() {
                leftToTop();
                end();
            }
        });

        menu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                leftToTop();
                showSearch();
            }
        });
        menu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
            @Override
            public void onClosed() {
//                leftAdapter.notifyDataSetChanged();
                leftToTop();
                hideSearch();
                mohulayout.setVisibility(View.GONE);
            }
        });

        menu.mViewAbove.setmMenuScrollListener(new CustomViewAbove.MenuScrollListener() {
            @Override
            public void onMenuScroll(float length, float scroll) {
                mohulayout.setVisibility(View.VISIBLE);
                String color = null;
                float v = scroll / length;
                if(v >= 1) {
                    return;
                } else {
                    v = 1 - v;
                    v = v * 256f;
                    String s = HexStringUtil.byteToString((byte) (v / 6));
                    color = "#" + s + "000000";
                    int c = Color.parseColor(color);
                    mohulayout.setBackgroundColor(c);
                }
            }
        });
    }

    private void leftToTop() {
//        LeftView.scrollTo(0, 0);
//        user_pic.requestFocus();
    }

    private void showSearch() {
        if(search.getVisibility() == View.GONE) {
            search.setVisibility(View.VISIBLE);
            search.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.pull_inx));
        }
    }

    private void hideSearch() {
        if(search.getVisibility() == View.VISIBLE) {
            search.setVisibility(View.GONE);
            search.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.pull_outx));
        }
    }

    private void init() {
        initMonitor();
        leftmonitor = (LinearLayout) findViewById(R.id.leftmonitor);

        leftmonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.showMenu();
            }
        });

        search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.showContent();
                hideSearch();
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        blayout1 = (LinearLayout) findViewById(R.id.blayout1);
        blayout2 = (RelativeLayout) findViewById(R.id.blayout2);
        centertext = (TextView) findViewById(R.id.centertext);

        mohulayout = (RelativeLayout) findViewById(R.id.mohulayout);

        btn1 = (RelativeLayout) findViewById(R.id.btn1);
        btn2 = (RelativeLayout) findViewById(R.id.btn2);
        btn3 = (RelativeLayout) findViewById(R.id.btn3);
        btn4 = (RelativeLayout) findViewById(R.id.btn4);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);

        btn4line = (LinearLayout) findViewById(R.id.btn4line);
        btn3line = (LinearLayout) findViewById(R.id.btn3line);

        mainpager = (ViewPager) findViewById(R.id.mainpager);
        information = new InformationFragment();
        goods = new GoodsFragment();
        fragments = new ArrayList<>();
        fragments.add(information);
        fragments.add(goods);
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mainpager.setAdapter(adapter);
        if(pageListener != null) {
            mainpager.removeOnPageChangeListener(pageListener);
        }
        pageListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                blayout1.setVisibility(View.VISIBLE);
                switch (position) {
                    case 0:
                        onClick(btn1);
                        menu.setSlidingEnabled(true);
                        break;
                    case 1:
                        StatService.onPageStart(MainActivity.this, "传送门");
                        onClick(btn2);
                        menu.setSlidingEnabled(false);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mainpager.addOnPageChangeListener(pageListener);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.collect_article:
                if(myApp.user.userid != null && !"".equals(myApp.user.userid)) {
                    StatService.onEvent(MainActivity.this, Event.EVENT_ID_NAVIGATION, "我的收藏-select");
//                    startActivity(new Intent(MainActivity.this, MyFavouriteActivity.class));

                    menu.showContent();
                    for (LeftModel lm : leftModels) {
                        lm.isClick = false;
                    }
                    leftAdapter.notifyDataSetChanged();
                    collect_tv.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.pink_divide));

                    blayout2.setVisibility(View.VISIBLE);
                    blayout1.setVisibility(View.INVISIBLE);
                    centertext.setText("收藏");

                    mainpager.removeAllViews();
                    fragments.clear();
                    fragments.add(information);
                    adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
                    mainpager.setAdapter(adapter);
                    StatService.onEvent(MainActivity.this, Event.EVENT_ID_NAVIGATION, "收藏-栏目-select");
                    requestHomeInfo(-2);
                } else {
                    StatService.onEvent(MainActivity.this, Event.EVENT_ID_NAVIGATION, "我的收藏-未登录");
                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 0);
                }
                break;
            case R.id.comment:
                menu.showContent();
                if(myApp.user.userid != null && !"".equals(myApp.user.userid)) {
                    StatService.onEvent(MainActivity.this, Event.EVENT_ID_NAVIGATION, "评论留言-select");
                    startActivity(new Intent(MainActivity.this, CommentListActivity.class));
                } else {
                    StatService.onEvent(MainActivity.this, Event.EVENT_ID_NAVIGATION, "评论留言-未登录");
                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
                }
                break;
            case R.id.user_pic:
                StatService.onEvent(MainActivity.this, Event.EVENT_ID_NAVIGATION, "用户头像-select");
                Intent it = new Intent(MainActivity.this, UserCenterActivity.class);
                menu.showContent();
                hideSearch();
                startActivityForResult(it, 1);
                break;
            case R.id.left_btn_login:
                StatService.onEvent(MainActivity.this, Event.EVENT_ID_NAVIGATION, "登录按钮-select");
                menu.showContent();
                hideSearch();
                startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
                break;
            case R.id.contribute:
                Toast.makeText(MainActivity.this, "发送邮件给我们", Toast.LENGTH_LONG).show();
                MailUtil.mailContact(this, "january.zhang@iflabs.cn");
                StatService.onEvent(this, Event.EVENT_ID_NAVIGATION, "投稿-select");
//                Intent test = new Intent(MainActivity.this, TestActivity.class);
//                startActivity(test);
                break;
            case R.id.about:
                StatService.onEvent(this, Event.EVENT_ID_NAVIGATION, "关于我们-select");

                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.setting:
//                requestLeftInfo();
                StatService.onEvent(this, Event.EVENT_ID_NAVIGATION, "设置-select");

                Intent setting = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(setting);
                break;
            case R.id.btn1:
            case R.id.btn3:
                if(hasChoose) {
                    hasChoose = false;
                    mainpager.setCurrentItem(0);
                    btn3.setVisibility(View.VISIBLE);
                    btn4.setVisibility(View.VISIBLE);
                    btn1.setVisibility(View.GONE);
                    btn2.setVisibility(View.GONE);

                    btn3.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pull_out));
                    btn4.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pull_in));
                    btn1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pull_out2));
                    btn2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pull_in2));

                }
                break;
            case R.id.btn2:
            case R.id.btn4:
                if(!hasChoose) {
                    hasChoose = true;
                    mainpager.setCurrentItem(1);

                    btn3.setVisibility(View.GONE);
                    btn4.setVisibility(View.GONE);
                    btn1.setVisibility(View.VISIBLE);
                    btn2.setVisibility(View.VISIBLE);

                    btn3.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pull_in2));
                    btn1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pull_in));
                    btn4.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pull_out2));
                    btn2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pull_out));
                }
                break;
        }
    }

    public void btnClick(View v) {
        switch (v.getId()) {
            case R.id.btn1_inside:
                onClick(btn1);
                break;
            case R.id.btn2_inside:
                onClick(btn2);
                break;
            case R.id.btn3_inside:
                onClick(btn3);
                break;
            case R.id.btn4_inside:
                onClick(btn4);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(menu.isMenuShowing()) {
            menu.showContent();
            return;
        } else {
//            super.onBackPressed();
            if(canFinish) {
                handler.removeMessages(CANFINISH);
                finish();
            } else {
                canFinish = true;
                Toast.makeText(this, "再点一次退出程序", Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessageDelayed(CANFINISH, 2000);
            }
        }
    }

    public void initMonitor() {
        line_pink = (RelativeLayout) findViewById(R.id.line_pink);
        line_blue = (RelativeLayout) findViewById(R.id.line_blue);
        line_green = (RelativeLayout) findViewById(R.id.line_green);
        line_view_blue = (RelativeLayout) findViewById(R.id.line_view_blue);
    }

    public void start() {
        ObjectAnimator.ofFloat(line_pink,"rotation",0,-45).setDuration(100).start();
        ObjectAnimator.ofFloat(line_blue,"alpha",1,0).setDuration(100).start();
        line_view_blue.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(line_green,"rotation",0,45).setDuration(100).start();
    }

    public void end() {
        ObjectAnimator.ofFloat(line_pink,"rotation",-45,0).setDuration(100).start();
        ObjectAnimator.ofFloat(line_blue,"alpha",0,1).setDuration(100).start();
        line_view_blue.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(line_green,"rotation",45,0).setDuration(100).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode) {
            case RESULT_OK:
                if(requestCode == 1) {
                    //更新界面,登陆成功
                    updateUserInfo();
                }
                break;
        }
    }

    private void updateUserInfo() {
        myApp = (MyApplication) getApplicationContext();
        if(myApp.user != null && !"".equals(myApp.user.userid) && !"".equals(myApp.user.token)) {
            logoutlayout.setVisibility(View.GONE);
            loginlayout.setVisibility(View.VISIBLE);
            user_name.setText(myApp.user.nickname);
//            user_level.setText(myApp.user.des);
            myApp.displayImageUserLogo(myApp.user.avatar, user_pic);
        } else {
            logoutlayout.setVisibility(View.VISIBLE);
            loginlayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
//        overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
//        overridePendingTransition(activityOpenEnterAnimation, activityOpenExitAnimation);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
//        overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
//        overridePendingTransition(activityOpenEnterAnimation, activityOpenExitAnimation);
    }


    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.activity_start, R.anim.activity_finish);
//        overridePendingTransition(activityCloseEnterAnimation, activityCloseExitAnimation);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(MainActivity.this, "传送门");
        unregisterReceiver(receiver);
    }
}
