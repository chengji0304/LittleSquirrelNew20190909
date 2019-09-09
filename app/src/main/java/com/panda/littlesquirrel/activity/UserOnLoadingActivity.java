package com.panda.littlesquirrel.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
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
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.zhouyou.http.callback.CallBack;
import com.zhouyou.http.exception.ApiException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserOnLoadingActivity extends BaseActivity {

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
    @Bind(R.id.tv_usermobile)
    TextView tvUsermobile;
    @Bind(R.id.user_image)
    CircleImageView userImage;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.btn_cancel)
    Button btnCancel;
    @Bind(R.id.ll_login_show)
    LinearLayout llLoginShow;
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
    private CountDownTimer timer;
    private String phone_num;
    private String nick_name;
    private String avatar_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdaptUtil.setCustomDesity(this, getApplication(), 360);
        setContentView(R.layout.activity_user_on_loading);
        ButterKnife.bind(this);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        prf = new PreferencesUtil(this);
        phone_num=getIntent().getStringExtra("phone_num");
        nick_name=getIntent().getStringExtra("nick_name");
        avatar_url=getIntent().getStringExtra("avatar_url");
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new CountDownTimer(1000 * 3, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
                if (secondsRemaining > 0) {
                    tvLoginMsg.setText(secondsRemaining + " 秒后自动打开箱门");
                }
            }

            @Override
            public void onFinish() {
                //打开箱门
                prf.writePrefs(Constant.USER_MOBILE,phone_num);
                prf.writePrefs(Constant.LOGIN_STATUS,"1");
                prf.writePrefs(Constant.USER_IMAGE,avatar_url);
                openActivity(UserTypeSelectActivity.class);
                finish();

            }
        }.start();

    }

    private void initData() {
        sendTimerBoaadCastReceiver(this);
        initBanner();
        tvDeviceNum.setText("设备编号:"+prf.readPrefs(Constant.DEVICEID));
        btnMyRecycler.setVisibility(View.GONE);
        tvUsermobile.setText(phone_num);
        tvUserName.setText(nick_name);
        Glide.with(this)
                .load(avatar_url)
                .placeholder(R.drawable.icon_user)
                .skipMemoryCache(true)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .into(userImage);

        initTimer();

    }

    private void initTimer() {
        backAndTime.setTimer(100);
        backAndTime.setBackVisableStatue(true);
        backAndTime.setVisableStatue(Boolean.valueOf(true));
        backAndTime.start();
        backAndTime.setOnBackListener(new BackAndTimerView.OnBackListener() {
            @Override
            public void onBack() {
                clearStatus();
//                openActivity(UserSelectActivity.class);
//                finish();
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });

        backAndTime.setOnTimerFinishListener(new BackAndTimerView.OnTimerFinishListener() {
            @Override
            public void onTimerFinish() {
                clearStatus();
//                openActivity(UserSelectActivity.class);
//                finish();
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });

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
        bannerButtom.setDelayTime(1000*30);
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
    public void getFindData(String reciveData) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
        }
    }

    @OnClick(R.id.btn_cancel)
    public void onViewClicked() {
        clearStatus();
    }

    private void clearStatus() {
        try{
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("deviceid",prf.readPrefs(Constant.DEVICEID));
            addSubscription(Constant.HTTP_URL + "php/v1/machine/clearlogin", jsonObject.toString(), new CallBack<String>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(ApiException e) {

                }

                @Override
                public void onSuccess(String s) {
                    Logger.e("s--->"+s);
                    JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(s);
                    String stateCode = jsonObject.getString("stateCode");
                    if(stateCode.equals("1")){
                        prf.deletPrefs(Constant.USER_MOBILE);
                        prf.deletPrefs(Constant.LOGIN_STATUS);
                        openActivity(UserSelectActivity.class);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }else{
                        prf.deletPrefs(Constant.USER_MOBILE);
                        prf.deletPrefs(Constant.LOGIN_STATUS);
                        openActivity(UserSelectActivity.class);
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
