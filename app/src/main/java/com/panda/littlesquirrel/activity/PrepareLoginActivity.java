package com.panda.littlesquirrel.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.panda.littlesquirrel.utils.StringUtil;
import com.panda.littlesquirrel.view.BackAndTimerView;
import com.panda.littlesquirrel.view.LoginTipDialog;
import com.panda.littlesquirrel.view.UserTelRightDialog;
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

public class PrepareLoginActivity extends BaseActivity {


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
    @Bind(R.id.tv_login_tip)
    TextView tvLoginTip;
    @Bind(R.id.tv_login_msg)
    TextView tvLoginMsg;
    @Bind(R.id.iv_login_tip)
    ImageView ivLoginTip;
    @Bind(R.id.ll_login_show)
    FrameLayout llLoginShow;
    @Bind(R.id.ll_mid)
    FrameLayout llMid;
    @Bind(R.id.banner_buttom)
    Banner bannerButtom;
    @Bind(R.id.ll_buttom)
    FrameLayout llButtom;
    @Bind(R.id.backAndTime)
    BackAndTimerView backAndTime;
    @Bind(R.id.activity_collect_login)
    RelativeLayout activityCollectLogin;
    private PreferencesUtil prf;
    private ArrayList<Integer> images;
    private LoginTipDialog dialog;
    private Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdaptUtil.setCustomDesity(this, getApplication(), 360);
        setContentView(R.layout.activity_prepare_login);
        ButterKnife.bind(this);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        prf = new PreferencesUtil(this);

        initData();

    }

    private void initData() {

        initBanner();
        btnMyRecycler.setVisibility(View.GONE);
        tvDeviceNum.setText("设备编号:" + prf.readPrefs(Constant.DEVICEID));
        initTimer();
        timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();//定时器
        timer.schedule(myTimerTask, 1000, 6000);//每隔5秒

    }

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
//            Logger.e("requestNumber-->" + (requestNumber));
//            requestNumber++;
//            if (requestNumber >= 50) {
//                if (timer != null) {
//                    timer.cancel();
//                    timer = null;
//                }
//                requestNumber = 0;
//                //生成新码
//                handler.sendEmptyMessage(1);
//            }
            //查询条码
            Logger.e("查询中。。。。");
            //barCodeQuery();
            getScanRecycler();


        }
    }

    private void getScanRecycler() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceid", prf.readPrefs(Constant.DEVICEID));
            //3203120008
            //jsonObject.put("deviceID","3203120008");
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

                        if(StringUtil.isEmpty(open_id)){

                        }else if(!StringUtil.isEmpty(open_id)&&!StringUtil.isEmpty(phone_num)&&!StringUtil.isEmpty(avatar_url)){
                            if(timer!=null){
                              timer.cancel();
                            }
                            if(dialog!=null){
                                dialog.dismiss();
                            }
                            backAndTime.stop();
                           // prf.writePrefs(Constant.USER_IMAGE, avatar_url.trim());
                            Intent intent = new Intent(PrepareLoginActivity.this, UserOnLoadingActivity.class);
                            intent.putExtra("phone_num", phone_num);
                            intent.putExtra("nick_name", nick_name);
                            intent.putExtra("avatar_url", avatar_url.trim());
                            startActivity(intent);
                            finish();
                           // Logger.e("zhucechengg");
                        }
//                        String mobile = object.getString("account");
////                        prf.writePrefs(Constant.COLLECTOR_MOBILE, mobile);
////                        openActivity(RecylerSelectActivity.class);
////                        finish();
                    }else{
                        //故障
                      //  openActivity(UserSelectActivity.class);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backAndTime.stop();
        if (timer != null) {
            timer.cancel();
        }
        if(dialog!=null){
            dialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendTimerBoaadCastReceiver(this);

    }

    private void initTimer() {
        backAndTime.setTimer(120);
        backAndTime.setBackVisableStatue(true);
        backAndTime.setVisableStatue(Boolean.valueOf(true));
        backAndTime.start();
        backAndTime.setOnBackListener(new BackAndTimerView.OnBackListener() {
            @Override
            public void onBack() {
                if(dialog!=null){
                    dialog.dismiss();
                }
                if(timer!=null){
                    timer.cancel();
                }
                backAndTime.stop();
               openActivity(UserSelectActivity.class);
                finish();
              //  clearStatus();
//                openActivity(UserSelectActivity.class);
//                finish();
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });

        backAndTime.setOnTimerFinishListener(new BackAndTimerView.OnTimerFinishListener() {
            @Override
            public void onTimerFinish() {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if(timer!=null){
                    timer.cancel();
                }
                backAndTime.stop();
                openActivity(UserSelectActivity.class);
                finish();
            //    clearStatus();
//                openActivity(UserSelectActivity.class);
//                finish();
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
    }



    @Override
    public void getFindData(String reciveData) {

    }

    @OnClick(R.id.iv_login_tip)
    public void onViewClicked() {
        dialog = new LoginTipDialog();
        dialog.setOnCloseClickListener(new LoginTipDialog.CloseCallBack() {
            @Override
            public void onClose() {
                dialog.dismiss();
            }
        });
        dialog.show(getFragmentManager(), "login_tip");
    }


}
