package com.weiguan.kejian;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.weiguan.kejian.commons.Event;
import com.weiguan.kejian.http.NetworkData;
import com.weiguan.kejian.listener.PicassoPauseOnScrollListener;
import com.weiguan.kejian.loader.PicassoImageLoader;
import com.weiguan.kejian.util.ScreenUtils;
import com.weiguan.kejian.view.CircleImageView;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.PauseOnScrollListener;
import cn.finalteam.galleryfinal.PhotoEditActivity;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by Administrator on 2016/2/4 0004.
 */
public class ModifyInfoActivity extends BaseUserActivity implements View.OnClickListener {
    private static final String TAG = "修改个人信息页面";
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;

    private CircleImageView modify_logo;
    private EditText et_nickname;
    private EditText et_intro;
    private Button modify_compete, safty_set;

    private File file;
    private Uri uri;
    private Dialog d;
    private TextView way_take, way_album;

    private MyApplication myApp;
    private Bitmap bitmap;

    private ProgressDialog progressDialog;

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null && resultList.size() > 0) {
                try {
                    PhotoInfo photoInfo = resultList.get(0);
                    File f = new File(photoInfo.getPhotoPath());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    bitmap = BitmapFactory.decodeFile(f.getPath(),
                            options);
                    // 压缩图片
//                    bitmap = compressImage(bitmap,500);

                    if (bitmap != null) {
                        // 显示图片
                        StatService.onEvent(ModifyInfoActivity.this, Event.EVENT_ID_USERINFOEDIT, "头像-success");
                        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 250, 250);
                        modify_logo.setImageBitmap(bitmap);
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
            Toast.makeText(ModifyInfoActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(ModifyInfoActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_modifyinfo);

        StatService.onPageStart(this, TAG);

        myApp = (MyApplication) getApplicationContext();
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

        String fileName = "image.jpg";
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Project/crash/";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        file = new File(path + fileName);
        if(file.exists()) {
            file.delete();
        }
        uri = Uri.fromFile(file);
        View v = LayoutInflater.from(this).inflate(R.layout.chosse_pic, null);
        way_take = (TextView) v.findViewById(R.id.way_take);
        way_album = (TextView) v.findViewById(R.id.way_album);
        d = new Dialog(this, R.style.whitedialog);
        d.setContentView(v);
        Window window = d.getWindow();
        window.setWindowAnimations(R.style.dialogWindowAnim);
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = ScreenUtils.getScreenWidth(ModifyInfoActivity.this);
        window.setAttributes(attributes);
        way_take.setOnClickListener(this);
        way_album.setOnClickListener(this);

        setTitle("修改个人信息");
        modify_logo = (CircleImageView) findViewById(R.id.modify_logo);
        et_nickname = (EditText) findViewById(R.id.et_nickname);
        et_nickname.clearFocus();
        et_intro = (EditText) findViewById(R.id.et_intro);
        et_intro.clearFocus();
        modify_compete = (Button) findViewById(R.id.modify_compete);
        safty_set = (Button) findViewById(R.id.safty_set);

        safty_set.setOnClickListener(this);
        modify_logo.setOnClickListener(this);
        modify_compete.setOnClickListener(this);

        myApp.displayImageUserLogo(myApp.user.avatar, modify_logo);

        et_nickname.setText(myApp.user.nickname);
        et_intro.setText(myApp.user.des);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在保存");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.modify_compete:
                StatService.onEvent(ModifyInfoActivity.this, Event.EVENT_ID_USERINFOEDIT, "确认-select");
                myApp.user.nickname = et_nickname.getText().toString();
                myApp.user.des = et_intro.getText().toString();
                if(myApp.user.nickname.length() > 0) {
                    if(!progressDialog.isShowing()) {
                        progressDialog.show();
                    }
                    StatService.onEvent(ModifyInfoActivity.this, Event.EVENT_ID_USERINFOEDIT, "确认-send");
                    NetworkData.saveUserInfo(myApp.user.userid, myApp.user.token, myApp.uuid, myApp.user.nickname, Bitmap2Bytes(bitmap), myApp.user.des, new NetworkData.NetworkCallback() {
                        @Override
                        public void callback(String data) {
                            try {
                                handler.sendEmptyMessage(2);
                                JSONObject jData = new JSONObject(data);
                                String result = jData.getString("result");
                                if("0".equals(result)) {
                                    //成功
                                    String avatarResult = jData.getString("avatarResult");
                                    if("0".equals(avatarResult)) {
                                        StatService.onEvent(ModifyInfoActivity.this, Event.EVENT_ID_USERINFOEDIT, "确认-success");
                                        String avatar = jData.getString("avatar");
                                        myApp.user.avatar = avatar;
                                        Log.i("avatar", myApp.user.avatar);
                                    }
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    Message obtain = Message.obtain();
                                    obtain.obj = jData.getString("info");
                                    obtain.what = 1;
                                    StatService.onEvent(ModifyInfoActivity.this, Event.EVENT_ID_USERINFOEDIT, "确认-fail-" + jData.getString("info"));
                                    handler.sendMessage(obtain);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ModifyInfoActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.modify_logo:
                StatService.onEvent(ModifyInfoActivity.this, Event.EVENT_ID_USERINFOEDIT, "头像-select");
                d.show();
                break;
            case R.id.way_take:
                StatService.onEvent(ModifyInfoActivity.this, Event.EVENT_ID_USERINFOEDIT, "头像-拍照");
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
                functionConfigBuilder.setCropSquare(true);
                functionConfigBuilder.setForceCrop(true);
                functionConfigBuilder.setEnableCamera(true);
                functionConfigBuilder.setEnablePreview(true);
                final FunctionConfig functionConfig = functionConfigBuilder.build();
                CoreConfig coreConfig = new CoreConfig.Builder(ModifyInfoActivity.this, imageLoader, themeConfig)
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
                StatService.onEvent(ModifyInfoActivity.this, Event.EVENT_ID_USERINFOEDIT, "头像-相册");
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
                CoreConfig coreConfig2 = new CoreConfig.Builder(ModifyInfoActivity.this, imageLoader2, themeConfig2)
                        .setDebug(true)
                        .setFunctionConfig(functionConfig2)
                        .setPauseOnScrollListener(pauseOnScrollListener2)
                        .setNoAnimcation(false)
                        .build();
                GalleryFinal.init(coreConfig2);
                initImageLoader(this);
                GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, functionConfig2, mOnHanlderResultCallback);
                break;
            case R.id.safty_set:
                StatService.onEvent(ModifyInfoActivity.this, Event.EVENT_ID_USERINFOEDIT, "安全设置-select");
                Intent i2 = new Intent(ModifyInfoActivity.this, SaftySettingActivity.class);
                startActivity(i2);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == 4) {
                try {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    File f = new File(picturePath);
                    FileOutputStream fos = new FileOutputStream(file);
                    FileInputStream fis = new FileInputStream(f);
                    int read = -1;
                    while((read = fis.read()) != -1) {
                        fos.write(read);
                    }
                    uri = Uri.fromFile(file);
                    startPhotoZoom(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                Toast.makeText(ModifyInfoActivity.this, uri.getPath().toString(), Toast.LENGTH_SHORT).show();
            } else if (requestCode == 2) {
                startPhotoZoom(uri);
            } else if(requestCode == 3) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    bitmap = BitmapFactory.decodeFile(file.getPath(),
                            options);
                    // 压缩图片
//                    bitmap = compressImage(bitmap,500);

                    if (bitmap != null) {
                        // 显示图片
                        StatService.onEvent(ModifyInfoActivity.this, Event.EVENT_ID_USERINFOEDIT, "头像-success");
                        modify_logo.setImageBitmap(bitmap);
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

        } else {
            StatService.onEvent(ModifyInfoActivity.this, Event.EVENT_ID_USERINFOEDIT, "头像-取消");
        }

    }

    public byte[] Bitmap2Bytes(Bitmap bm) {
        if(bm != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        }
        return null;
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
        intent.putExtra("outputFormat", "jpeg");// 返回格式
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
