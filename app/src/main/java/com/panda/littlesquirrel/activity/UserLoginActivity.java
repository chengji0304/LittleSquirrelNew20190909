package com.panda.littlesquirrel.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
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
import com.panda.littlesquirrel.utils.Base64Utils;
import com.panda.littlesquirrel.utils.CornerTransform;
import com.panda.littlesquirrel.utils.DefaultExceptionHandler;
import com.panda.littlesquirrel.utils.PreferencesUtil;
import com.panda.littlesquirrel.utils.ScreenAdaptUtil;
import com.panda.littlesquirrel.utils.ScreenUtil;
import com.panda.littlesquirrel.utils.SoundPlayUtil;

import com.panda.littlesquirrel.utils.StringUtil;
import com.panda.littlesquirrel.view.BackAndTimerView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.zhouyou.http.callback.CallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserLoginActivity extends BaseActivity {

    @Bind(R.id.image_service_tel)
    ImageView imageServiceTel;
    @Bind(R.id.image_device_num)
    ImageView imageDeviceNum;
    @Bind(R.id.tv_service_tel)
    TextView tvServiceTel;
    @Bind(R.id.tv_device_num)
    TextView tvDeviceNum;
    @Bind(R.id.btn_my_recycler)
    Button btnMyRecycler;
    @Bind(R.id.tv_line_09)
    TextView tvLine09;
    @Bind(R.id.tv_line_10)
    TextView tvLine10;
    @Bind(R.id.tv_tip_09)
    TextView tvTip09;
    @Bind(R.id.img_user_qrcode)
    ImageView imgUserQrcode;

    @Bind(R.id.btn_switch_tel)
    Button btnSwitchTel;

    @Bind(R.id.banner_buttom)
    Banner bannerButtom;
    @Bind(R.id.backAndTime)
    BackAndTimerView backAndTime;
    @Bind(R.id.activity_user_login)
    RelativeLayout activityUserLogin;
    @Bind(R.id.ll_wait)
    LinearLayout llWait;
    @Bind(R.id.ll_scan)
    LinearLayout llScan;
    private String code;
    private Bitmap bitmap;
    private int requestNumber = 0;
    PreferencesUtil prf;
    private Timer timer = null;
    private ArrayList<Integer> images;
//    private Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {  //这个是发送过来的消息
//            // 处理从子线程发送过来的消息
//            switch (msg.what) {
//                case 1:
//                    if (timer != null) {
//                        timer.cancel();
//                        timer = null;
//                    }
//                    getUserOrCode();
//                    break;
//                default:
//                    break;
//            }
//
//        }
//
//
//    };

    //  private SelcetInfo info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  hideNavigationBar();
        ScreenAdaptUtil.setCustomDesity(this, getApplication(), 360);
        setContentView(R.layout.activity_user_login);
        ButterKnife.bind(this);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        prf = new PreferencesUtil(this);
        clearStatus();
        initData();
    }

    private void initData() {
        sendTimerBoaadCastReceiver(this);
        initBanner();
        tvDeviceNum.setText("设备编号:" + prf.readPrefs(Constant.DEVICEID));
        //tvDeviceNum.setText("设备编号:" + "3203120008");
        btnMyRecycler.setVisibility(View.GONE);
        //  getUserOrCode();


    }

    private void clearStatus() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceid", prf.readPrefs(Constant.DEVICEID));
            //jsonObject.put("deviceid", "3203120008");
            addSubscription(Constant.HTTP_URL + "php/v1/machine/clearlogin", jsonObject.toString(), new CallBack<String>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(ApiException e) {
                    openActivity(UserSelectActivity.class);
                }

                @Override
                public void onSuccess(String s) {
                    Logger.e("s--->" + s);
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(s);
                    String stateCode = jsonObject.getString("stateCode");
                    if (stateCode.equals("1")) {
                        com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSON.parseObject(jsonObject.getString("result"));
                        String status = object.getString("status");
                        if (status.equals("1")) {
                            getUserOrCode();
                        } else {
                            //故障页面
                          //  openActivity(UserSelectActivity.class);
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     获取二维码
     */
    private void getUserOrCode() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceid", prf.readPrefs(Constant.DEVICEID));
            // jsonObject.put("deviceid", "3203120008");
            addSubscription(Constant.HTTP_URL + "php/v1/machine/getqrcode", jsonObject.toString(), new CallBack<String>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(ApiException e) {
                    openActivity(UserSelectActivity.class);
                }

                @Override
                public void onSuccess(String s) {
                    Logger.e("s--->" + s);
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(s);
                    String stateCode = jsonObject.getString("stateCode");
                    if (stateCode.equals("1")) {
                        com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSON.parseObject(jsonObject.getString("result"));
                        String code = object.getString("qrcode");
                        bitmap = Base64Utils.base64ToBitmap(code);
                        imgUserQrcode.setImageBitmap(bitmap);
                        // prf.writePrefs(Constant.USER_QRCODE,code);
                        llScan.setVisibility(View.VISIBLE);
                        llWait.setVisibility(View.GONE);
                        // initTimer();
                        SoundPlayUtil.play(11);
                        timer = new Timer();
                        MyTimerTask myTimerTask = new MyTimerTask();//定时器
                        timer.schedule(myTimerTask, 1000, 6000);//每隔5秒
                        // initTimer();
                        //machine/verification/getScanRecycler
                        //getScanRecycler();
                    } else {
                        openActivity(UserSelectActivity.class);
                       // finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
           // Logger.e("requestNumber-->" + (requestNumber));
//            requestNumber++;
//            if (requestNumber >= 24) {
//                if (timer != null) {
//                    timer.cancel();
//                    timer = null;
//                }
//                requestNumber=0;
//                //生成新码
//                handler.sendEmptyMessage(1);
//            }
            //查询条码
            Logger.e("查询中。。。。");
            getScanRecycler();


        }
    }

    /*
     查询
     */
    private void getScanRecycler() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceid", prf.readPrefs(Constant.DEVICEID));
            //jsonObject.put("deviceid", "3203120008");
            addSubscription(Constant.HTTP_URL + "php/v1/machine/getscanuser", jsonObject.toString(), new CallBack<String>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(ApiException e) {
                    openActivity(UserSelectActivity.class);
                }

                @Override
                public void onSuccess(String s) {
                    Logger.e("s--->" + s);
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(s);
                    String stateCode = jsonObject.getString("stateCode");
                    if (stateCode.equals("1")) {
                        com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSON.parseObject(jsonObject.getString("result"));
                        String open_id = object.getString("open_id");
                        String phone_num = object.getString("phone_num");
                        String nick_name = object.getString("nick_name");
                        String avatar_url = object.getString("avatar_url");
                        prf.writePrefs(Constant.USER_IMAGE, avatar_url);
                        if (StringUtil.isEmpty(open_id)) {
                        } else if (!StringUtil.isEmpty(open_id) && StringUtil.isEmpty(phone_num)) {
                            backAndTime.stop();
                            if(timer!=null){
                                timer.cancel();
                            }
                            openActivity(PrepareLoginActivity.class);
                            finish();
                        } else if (!StringUtil.isEmpty(open_id) && !StringUtil.isEmpty(phone_num)) {
                            backAndTime.stop();
                            if(timer!=null){
                                timer.cancel();
                            }
                            Intent intent = new Intent(UserLoginActivity.this, UserOnLoadingActivity.class);
                            intent.putExtra("phone_num", phone_num);
                            intent.putExtra("nick_name", nick_name);
                            intent.putExtra("avatar_url", avatar_url);
                            startActivity(intent);
                            finish();
                        }
                    }else{

                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // SoundPlayUtils.StartMusic(15);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTimer();

    }

    public void initTimer() {
        backAndTime.setTimer(120);
        backAndTime.setBackVisableStatue(true);
        backAndTime.setVisableStatue(Boolean.valueOf(true));
        backAndTime.start();
        backAndTime.setOnBackListener(new BackAndTimerView.OnBackListener() {
            @Override
            public void onBack() {
                if(timer!=null){
                    timer.cancel();
                }
                backAndTime.stop();
                openActivity(UserSelectActivity.class);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        backAndTime.setOnTimerFinishListener(new BackAndTimerView.OnTimerFinishListener() {
            @Override
            public void onTimerFinish() {
                if(timer!=null){
                    timer.cancel();
                }
                backAndTime.stop();
                openActivity(UserSelectActivity.class);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backAndTime != null) {
            backAndTime.stop();
        }

        //  SoundPlayUtils.release();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        if (timer != null) {
            timer.cancel();
        }
        //backAndTime.stop();
//        if (handler != null) {
//            handler.removeCallbacksAndMessages(null);
//        }

    }

    @Override
    public void getFindData(String reciveData) {

    }


    @OnClick(R.id.btn_switch_tel)
    public void onViewClicked() {
        backAndTime.stop();
        if(timer!=null){
            timer.cancel();
        }
        openActivity(UserTeleActivity.class);
        finish();

    }

    private void initBanner() {
        images = new ArrayList<>();
        images.add(R.drawable.banner111);
        images.add(R.drawable.banner222);
        images.add(R.drawable.banner333);
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
        bannerButtom.setDelayTime(1000 * 30);
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
                    .asBitmap()
                    .skipMemoryCache(true)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                    .transform(transformation)
                    .into(imageView);

        }

    }

}
