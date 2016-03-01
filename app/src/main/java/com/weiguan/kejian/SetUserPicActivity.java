package com.weiguan.kejian;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.weiguan.kejian.listener.PicassoPauseOnScrollListener;
import com.weiguan.kejian.loader.PicassoImageLoader;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.http.NetworkData;
import com.weiguan.kejian.util.ScreenUtils;
import com.weiguan.kejian.view.CircleImageView;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.PauseOnScrollListener;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by Administrator on 2016/2/2 0002.
 */
public class SetUserPicActivity extends BaseUserActivity implements View.OnClickListener {
    private static final String TAG = "注册输入个人信息页面";
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;

    private CircleImageView select_pic;
    private EditText nickname, intro;
    private Button complete;
    private TextView way_take, way_album;

    private MyApplication myApp;
    private byte[] picData = {};
    private File file;
    private Uri uri;
    private Dialog d;

    private ProgressDialog progressDialog;

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                try {
                    PhotoInfo photoInfo = resultList.get(0);
                    File f = new File(photoInfo.getPhotoPath());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bitmap = BitmapFactory.decodeFile(f.getPath(),
                            options);
                    // 压缩图片
//                    bitmap = compressImage(bitmap,500);

                    if (bitmap != null) {
                        // 显示图片
                        StatService.onEvent(SetUserPicActivity.this, Event.EVENT_ID_USERINFOEDIT, "头像-success");
                        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 250, 250);
                        select_pic.setImageBitmap(bitmap);
                        picData = Bitmap2Bytes(bitmap);
                        // 保存图片
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(SetUserPicActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(SetUserPicActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    progressDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setpic);

        StatService.onPageStart(this, TAG);

        myApp = (MyApplication) getApplicationContext();
        String fileName = "image.jpg";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Project/crash/";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        file = new File(path + fileName);
        uri = Uri.fromFile(file);
        initView();
    }

    private void initView() {
        findViewById(R.id.scview).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        View v = LayoutInflater.from(this).inflate(R.layout.chosse_pic, null);
        way_take = (TextView) v.findViewById(R.id.way_take);
        way_album = (TextView) v.findViewById(R.id.way_album);
        d = new Dialog(this, R.style.whitedialog);
        d.setContentView(v);
        Window window = d.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = ScreenUtils.getScreenWidth(SetUserPicActivity.this);
        window.setAttributes(attributes);

        way_take.setOnClickListener(this);
        way_album.setOnClickListener(this);

        select_pic = (CircleImageView) findViewById(R.id.select_pic);
        nickname = (EditText) findViewById(R.id.nickname);
        intro = (EditText) findViewById(R.id.intro);
        complete = (Button) findViewById(R.id.complete);

        select_pic.setOnClickListener(this);
        complete.setOnClickListener(this);
        way_take.setOnClickListener(this);
        way_album.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在保存");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_pic:
                StatService.onEvent(SetUserPicActivity.this, Event.EVENT_ID_REGISTUSERINFO, "头像-select");
                d.show();
                break;
            case R.id.way_take:
                StatService.onEvent(SetUserPicActivity.this, Event.EVENT_ID_REGISTUSERINFO, "头像-拍照");
                d.dismiss();
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                startActivityForResult(intent, 2);                //拍完照startActivityForResult() 结果返回onActivityResult()函数
                ThemeConfig themeConfig = ThemeConfig.DARK;
                FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
                cn.finalteam.galleryfinal.ImageLoader imageLoader;
                PauseOnScrollListener pauseOnScrollListener = null;
                imageLoader = new PicassoImageLoader();
                pauseOnScrollListener = new PicassoPauseOnScrollListener(false, true);
                boolean muti = false;
                functionConfigBuilder.setEnableEdit(true);
                functionConfigBuilder.setEnableRotate(true);
                functionConfigBuilder.setEnableCrop(true);
                functionConfigBuilder.setCropWidth(250);
                functionConfigBuilder.setCropHeight(250);
                functionConfigBuilder.setForceCrop(true);
                functionConfigBuilder.setCropSquare(true);
                functionConfigBuilder.setEnableCamera(true);
                functionConfigBuilder.setEnablePreview(true);
                final FunctionConfig functionConfig = functionConfigBuilder.build();
                CoreConfig coreConfig = new CoreConfig.Builder(SetUserPicActivity.this, imageLoader, themeConfig)
                        .setDebug(true)
                        .setFunctionConfig(functionConfig)
                        .setPauseOnScrollListener(pauseOnScrollListener)
                        .setNoAnimcation(false)
                        .build();
                GalleryFinal.init(coreConfig);
                initImageLoader(this);
                GalleryFinal.openCamera(REQUEST_CODE_CAMERA, functionConfig, mOnHanlderResultCallback);
                break;
            case R.id.way_album:
                StatService.onEvent(SetUserPicActivity.this, Event.EVENT_ID_REGISTUSERINFO, "头像-相册");
                d.dismiss();
//                Intent i = new Intent(Intent.ACTION_PICK);
//                i.setType("image/*");//相片类型
//                startActivityForResult(i, 4);
                ThemeConfig themeConfig2 = ThemeConfig.DARK;
                FunctionConfig.Builder functionConfigBuilder2 = new FunctionConfig.Builder();
                cn.finalteam.galleryfinal.ImageLoader imageLoader2;
                PauseOnScrollListener pauseOnScrollListener2 = null;
                imageLoader2 = new PicassoImageLoader();
                pauseOnScrollListener2 = new PicassoPauseOnScrollListener(false, true);
                boolean muti2 = false;
                functionConfigBuilder2.setEnableEdit(true);
                functionConfigBuilder2.setEnableRotate(true);
                functionConfigBuilder2.setEnableCrop(true);
                functionConfigBuilder2.setCropWidth(250);
                functionConfigBuilder2.setCropHeight(250);
                functionConfigBuilder2.setForceCrop(true);
                functionConfigBuilder2.setCropSquare(true);
                functionConfigBuilder2.setEnableCamera(true);
                functionConfigBuilder2.setEnablePreview(true);
                final FunctionConfig functionConfig2 = functionConfigBuilder2.build();
                CoreConfig coreConfig2 = new CoreConfig.Builder(SetUserPicActivity.this, imageLoader2, themeConfig2)
                        .setDebug(true)
                        .setFunctionConfig(functionConfig2)
                        .setPauseOnScrollListener(pauseOnScrollListener2)
                        .setNoAnimcation(false)
                        .build();
                GalleryFinal.init(coreConfig2);
                initImageLoader(this);
                GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, functionConfig2, mOnHanlderResultCallback);
                break;
            case R.id.complete:
                StatService.onEvent(SetUserPicActivity.this, Event.EVENT_ID_REGISTUSERINFO, "确认-select");
                myApp.user.des = intro.getText().toString();
                myApp.user.nickname = nickname.getText().toString();
                if(myApp.user.nickname.length() > 0) {
                    if(!progressDialog.isShowing())
                        progressDialog.show();
                    StatService.onEvent(SetUserPicActivity.this, Event.EVENT_ID_REGISTUSERINFO, "确认-send");
                    NetworkData.saveUserInfo(myApp.user.userid, myApp.user.token, myApp.uuid, myApp.user.nickname, picData, myApp.user.des, new NetworkData.NetworkCallback() {
                        @Override
                        public void callback(String data) {
                            try {
                                handler.sendEmptyMessage(2);
                                JSONObject jData = new JSONObject(data);
                                String result = jData.getString("result");
                                if ("0".equals(result)) {
                                    //成功
                                    StatService.onEvent(SetUserPicActivity.this, Event.EVENT_ID_REGISTUSERINFO, "确认-success");
                                    if(jData.getString("avatarResult").equals("0")) {
                                        myApp.user.avatar = jData.getString("avatar");
                                    }
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    Message obtain = Message.obtain();
                                    obtain.obj = jData.getString("info");
                                    obtain.what = 1;
                                    StatService.onEvent(SetUserPicActivity.this, Event.EVENT_ID_REGISTUSERINFO, "确认-fail-" + jData.getString("info"));
                                    handler.sendMessage(obtain);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SetUserPicActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == 4) {
                uri = data.getData();
                file = new File(uri.getPath());
                startPhotoZoom(uri);
//                Toast.makeText(SetUserPicActivity.this, uri.getPath().toString(), Toast.LENGTH_SHORT).show();
            } else if (requestCode == 2) {
                startPhotoZoom(uri);
            } else if(requestCode == 3) {
                try {
                    StatService.onEvent(SetUserPicActivity.this, Event.EVENT_ID_REGISTUSERINFO, "头像-success");
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(),
                            options);
                    // 压缩图片
//                    bitmap = compressImage(bitmap,500);

                    if (bitmap != null) {
                        // 显示图片
                        select_pic.setImageBitmap(bitmap);
                        picData = Bitmap2Bytes(bitmap);
                        // 保存图片
                        FileOutputStream fos = null;
                        fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            StatService.onEvent(SetUserPicActivity.this, Event.EVENT_ID_REGISTUSERINFO, "头像-取消");
        }

    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
         return baos.toByteArray();
    }

    /**
     * 将图片image压缩成大小为 size的图片（size表示图片大小，单位是KB）
     *
     * @param image
     *            图片资源
     * @param size
     *            图片大小
     * @return Bitmap
     */
    private Bitmap compressImage(Bitmap image, int size) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > size) {
            // 重置baos即清空baos
            baos.reset();
            // 每次都减少10
            options -= 10;
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("aspectX", 2);// 这两项为裁剪框的比例.
        intent.putExtra("aspectY", 2);// x:y=1:1
        intent.putExtra("outputX", 250);//图片输出大小
        intent.putExtra("outputY", 250);
        intent.putExtra("output", uri);
        intent.putExtra("outputFormat", "png");// 返回格式
        startActivityForResult(intent, 3);
    }

    private void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StatService.onPageEnd(this, TAG);
    }
}
