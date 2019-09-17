package com.panda.littlesquirrel.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.orhanobut.logger.Logger;
import com.panda.littlesquirrel.R;
import com.panda.littlesquirrel.base.BaseActivity;
import com.panda.littlesquirrel.config.Constant;
import com.panda.littlesquirrel.utils.CornerTransform;
import com.panda.littlesquirrel.utils.DefaultExceptionHandler;
import com.panda.littlesquirrel.utils.PreferencesUtil;
import com.panda.littlesquirrel.utils.ScreenAdaptUtil;
import com.panda.littlesquirrel.utils.ScreenUtil;
import com.panda.littlesquirrel.utils.SerialPortUtils;
import com.panda.littlesquirrel.view.BackAndTimerView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;

import android_serialport_api.SerialPort;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.handle;

public class RightBoxSettingActivity extends BaseActivity {

    @Bind(R.id.iv_logo)
    ImageView ivLogo;
    @Bind(R.id.image_service_tel)
    ImageView imageServiceTel;
    @Bind(R.id.tv_service_tel)
    TextView tvServiceTel;
    @Bind(R.id.image_device_num)
    ImageView imageDeviceNum;
    @Bind(R.id.tv_device_num)
    TextView tvDeviceNum;
    @Bind(R.id.ll_image)
    LinearLayout llImage;
    @Bind(R.id.btn_my_recycler)
    Button btnMyRecycler;
    @Bind(R.id.ll_top)
    FrameLayout llTop;
    @Bind(R.id.tv_line_05)
    TextView tvLine05;
    @Bind(R.id.tv_tip_03)
    TextView tvTip03;
    @Bind(R.id.tv_line_06)
    TextView tvLine06;
    @Bind(R.id.tv_fabric)
    TextView tvFabric;
    @Bind(R.id.tv_plastic)
    TextView tvPlastic;
    @Bind(R.id.tv_metal)
    TextView tvMetal;
    @Bind(R.id.btn_openbox)
    Button btnOpenbox;
    @Bind(R.id.tv_boxtype)
    TextView tvBoxtype;
    @Bind(R.id.btn_clear)
    Button btnClear;
    @Bind(R.id.btn_jiaozhun)
    Button btnJiaozhun;
    @Bind(R.id.btn_weight)
    Button btnWeight;
    @Bind(R.id.tv_weight)
    TextView tvWeight;
    @Bind(R.id.ll_mid)
    FrameLayout llMid;
    @Bind(R.id.banner_buttom)
    Banner bannerButtom;
    @Bind(R.id.ll_buttom)
    FrameLayout llButtom;
    @Bind(R.id.backAndTime)
    BackAndTimerView backAndTime;
    @Bind(R.id.activity_setting)
    RelativeLayout activitySetting;
    private String type = "0";
    private Handler handler;
    //   public SerialPort serialPort;
//    public Handler mStartHandler;
//    public byte[] mBuffer;
    //public  String reciveData = "";
    //   public StringBuilder reciveData;
    //  public SerialPortUtils serialPortUtils = new SerialPortUtils("/dev/ttyS4", 9600);
    public boolean isClose;
    private PreferencesUtil prf;
    private ArrayList<Integer> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdaptUtil.setCustomDesity(this, getApplication(), 360);
        setContentView(R.layout.activity_right_box_setting);
        ButterKnife.bind(this);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        prf = new PreferencesUtil(this);
        System.loadLibrary("serial_port");
        serialPort = serialPortUtils.openSerialPort();
        mStartHandler = new Handler();
        reciveData = new StringBuilder();
        Logger.e("mhanld-->" + mStartHandler);
        Logger.e("recive-->" + reciveData);
        handler = new Handler();
        setListener();
        initData();
    }

    private void initData() {
        sendTimerBoaadCastReceiver(this);
        initBanner();
        tvDeviceNum.setText("设备编号:" + prf.readPrefs(Constant.DEVICEID));
        btnMyRecycler.setVisibility(View.GONE);
        backAndTime.setBackVisableStatue(true);
        backAndTime.setTimerVisableStatue(false);
        backAndTime.setOnBackListener(new BackAndTimerView.OnBackListener() {
            @Override
            public void onBack() {
               // openActivity(UserSelectActivity.class);

                 openActivity(SettingActivity.class);
                finish();


            }
        });

    }

    @Override
    public void getFindData(String s) {
        Logger.e("RightBoxSettingActivity--->" + s);
        if (s.equals("W3:1;")) {
            //  btnClear.setEnabled(false);
            btnJiaozhun.setEnabled(true);
        } else if (s.equals("W3:0;")) {
            btnClear.setEnabled(true);
        } else if (s.equals("W4:1;")) {
            btnOpenbox.setEnabled(true);
            //btnClear.setEnabled(false);
            // btnJiaozhun.setEnabled(false);
        } else if (s.equals("W4:0;")) {
            btnJiaozhun.setEnabled(true);
        } else if (s.contains("E29")) {
            btnClear.setEnabled(true);
            btnJiaozhun.setEnabled(false);
        } else if (s.contains("E26")) {
            isClose = true;
            btnClear.setEnabled(false);
            btnJiaozhun.setEnabled(false);
            btnOpenbox.setEnabled(true);

        }

    }

    @OnClick({R.id.tv_fabric, R.id.tv_plastic, R.id.tv_metal, R.id.btn_openbox, R.id.btn_clear, R.id.btn_jiaozhun, R.id.btn_weight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_fabric:
                type = "0";
                tvFabric.setTextColor(Color.parseColor("#179BFF"));
                tvFabric.setBackgroundResource(R.drawable.setting_box);
                tvPlastic.setTextColor(Color.parseColor("#666666"));
                tvPlastic.setBackgroundResource(R.drawable.setting_box_en);
                tvMetal.setTextColor(Color.parseColor("#666666"));
                tvMetal.setBackgroundResource(R.drawable.setting_box_en);

                break;
            case R.id.tv_plastic:
                type = "1";
                tvFabric.setTextColor(Color.parseColor("#666666"));
                tvFabric.setBackgroundResource(R.drawable.setting_box_en);
                tvPlastic.setTextColor(Color.parseColor("#179BFF"));
                tvPlastic.setBackgroundResource(R.drawable.setting_box);
                tvMetal.setTextColor(Color.parseColor("#666666"));
                tvMetal.setBackgroundResource(R.drawable.setting_box_en);

                break;
            case R.id.tv_metal:
                type = "2";
                tvFabric.setTextColor(Color.parseColor("#666666"));
                tvFabric.setBackgroundResource(R.drawable.setting_box_en);
                tvPlastic.setTextColor(Color.parseColor("#666666"));
                tvPlastic.setBackgroundResource(R.drawable.setting_box_en);
                tvMetal.setTextColor(Color.parseColor("#179BFF"));
                tvMetal.setBackgroundResource(R.drawable.setting_box);

                break;
            case R.id.btn_openbox:
                btnOpenbox.setEnabled(false);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        serialPortUtils.sendSerialPort("androidC54:" + type + ";");
                    }
                }, 2000);

                break;
            case R.id.btn_clear:
                btnClear.setEnabled(false);
                switch (type) {
                    case "0":
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                serialPortUtils.sendSerialPort("androidP602:0;");
                            }
                        }, 2000);
                        break;
                    case "1":
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                serialPortUtils.sendSerialPort("androidP603:1;");
                            }
                        }, 2000);
                        break;
                    case "2":
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                serialPortUtils.sendSerialPort("androidP604:2;");
                            }
                        }, 2000);
                        break;
                }


                break;
            case R.id.btn_jiaozhun:
                btnJiaozhun.setEnabled(false);
                switch (type) {
                    case "0":
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                serialPortUtils.sendSerialPort("androidP607:0;");
                            }
                        }, 2000);
                        break;
                    case "1":
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                serialPortUtils.sendSerialPort("androidP608:1;");
                            }
                        }, 2000);
                        break;
                    case "2":
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                serialPortUtils.sendSerialPort("androidP609:2;");
                            }
                        }, 2000);
                        break;
                }

                break;
            case R.id.btn_weight:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                    }
                }, 2000);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  sendTimerBoaadCastReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mStartHandler != null) {
            mStartHandler.removeCallbacksAndMessages(null);
        }

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        serialPortUtils.closeSerialPort();
    }

    private void initBanner() {
        images = new ArrayList<>();
        images.add(R.drawable.banner_setting);
//        images.add(R.drawable.banner222);
//        images.add(R.drawable.banner333);
        //设置banner样式(显示圆形指示器)
        bannerButtom.setBannerStyle(BannerConfig.NOT_INDICATOR);
        //设置指示器位置（指示器居右）
        bannerButtom.setIndicatorGravity(BannerConfig.RIGHT);
        //设置图片加载器
        bannerButtom.setImageLoader(new GlideImageLoader());
        //设置图片集合
        bannerButtom.setImages(images);
        bannerButtom.isAutoPlay(true);
        //设置轮播时间
        bannerButtom.setDelayTime(4000);
        //banner设置方法全部调用完毕时最后调用
        bannerButtom.setOnBannerListener(new OnBannerListener() {

            @Override
            public void OnBannerClick(int position) {
                Logger.e("position" + position);

            }
        });
        bannerButtom.start();
    }

    class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(final Context context, Object path, final ImageView imageView) {
            CornerTransform transformation = new CornerTransform(context, ScreenUtil.dip2px(context, 30));
            //只是绘制左上角和右上角圆角
            transformation.setExceptCorner(false, false, false, false);
            Glide.with(context)
                    .load(path)
                    //.asBitmap()
                    .skipMemoryCache(true)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                    // .transform(transformation)
                    .into(imageView);

        }

    }
}
