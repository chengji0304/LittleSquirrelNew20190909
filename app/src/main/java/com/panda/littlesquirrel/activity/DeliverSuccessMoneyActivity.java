package com.panda.littlesquirrel.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.orhanobut.logger.Logger;
import com.panda.littlesquirrel.R;
import com.panda.littlesquirrel.base.BaseActivity;
import com.panda.littlesquirrel.config.Constant;
import com.panda.littlesquirrel.entity.GarbageParam;
import com.panda.littlesquirrel.utils.CornerTransform;
import com.panda.littlesquirrel.utils.DefaultExceptionHandler;
import com.panda.littlesquirrel.utils.PreferencesUtil;
import com.panda.littlesquirrel.utils.ScreenAdaptUtil;
import com.panda.littlesquirrel.utils.ScreenUtil;
import com.panda.littlesquirrel.utils.SoundPlayUtil;
import com.panda.littlesquirrel.view.BackAndTimerView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.zhouyou.http.callback.CallBack;
import com.zhouyou.http.exception.ApiException;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class DeliverSuccessMoneyActivity extends BaseActivity {

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
    @Bind(R.id.user_image)
    CircleImageView userImage;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.tv_user_mobile)
    TextView tvUserMobile;
    @Bind(R.id.tv_money)
    TextView tvMoney;
    @Bind(R.id.tv_moneymsg)
    TextView tvMoneymsg;
    @Bind(R.id.tv_level)
    TextView tvLevel;
    @Bind(R.id.iv_icon)
    ImageView ivIcon;
    @Bind(R.id.tv_num)
    TextView tvNum;
    @Bind(R.id.btn_over)
    Button btnOver;
    @Bind(R.id.btn_look)
    Button btnLook;
    @Bind(R.id.ll_mid)
    FrameLayout llMid;
    @Bind(R.id.banner_buttom)
    Banner bannerButtom;
    @Bind(R.id.ll_buttom)
    FrameLayout llButtom;
    @Bind(R.id.backAndTime)
    BackAndTimerView backAndTime;
    @Bind(R.id.activity_deliver_success)
    RelativeLayout activityDeliverSuccess;
    @Bind(R.id.ll_qrcode)
    LinearLayout llQrcode;
    @Bind(R.id.ll_level)
    LinearLayout llLevel;
    @Bind(R.id.ll_totallevel)
    LinearLayout llTotallevel;
    private String imageUrl;
    private String money;
    private String jifen;
    private PreferencesUtil prf;
    private SpannableString spannableString;
    private GarbageParam garbageParam;
    private ArrayList<Integer> images;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdaptUtil.setCustomDesity(this, getApplication(), 360);
        setContentView(R.layout.activity_deliver_success_money);
        ButterKnife.bind(this);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        prf = new PreferencesUtil(this);
        imageUrl = prf.readPrefs(Constant.USER_IMAGE);
        initData();

    }

    private void initData() {
        sendTimerBoaadCastReceiver(this);
        initBanner();
        Glide.with(this)
                .load(imageUrl)
                .error(R.drawable.icon_user)
                .fallback(R.drawable.icon_user)
                .skipMemoryCache(true)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .into(userImage);
        tvDeviceNum.setText("设备编号:" + prf.readPrefs(Constant.DEVICEID));
        btnMyRecycler.setVisibility(View.GONE);
        llQrcode.setVisibility(View.VISIBLE);
        getLevel();
        money = getIntent().getStringExtra("money");
        jifen = getIntent().getStringExtra("jifen");
        spannableString = new SpannableString(money + " 环保金");
        spannableString.setSpan(new RelativeSizeSpan(0.8F), money.length(), spannableString.length(), 17);
        tvMoney.setText(spannableString);
        tvUserName.setText(prf.readPrefs(Constant.USER_NAME));
        tvUserMobile.setText(prf.readPrefs(Constant.USER_MOBILE));
        tvMoneymsg.setText("(等于" + money + "元、可在微信中提现)");

    }

    private void getLevel() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceid", prf.readPrefs(Constant.DEVICEID));
            jsonObject.put("phone_num", prf.readPrefs(Constant.USER_MOBILE));
            addSubscription(Constant.HTTP_URL + "php/v1/machine/delivery_rank", jsonObject.toString(), new CallBack<String>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(ApiException e) {
                    llTotallevel.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onSuccess(String s) {
                    Logger.e("s--->" + s);
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(s);
                    String stateCode = jsonObject.getString("stateCode");
                    if (stateCode.equals("1")) {
                        String result = jsonObject.getString("result");
                        com.alibaba.fastjson.JSONObject object = JSON.parseObject(result);
                        com.alibaba.fastjson.JSONObject obj = JSON.parseObject(object.getString("rank_info"));
                        String level = obj.getString("now_rank");
                        String has_rank = obj.getString("has_rank");
                        tvLevel.setText("第" + level + "名");
                        if (has_rank.equals("0")) {
                            llLevel.setVisibility(View.GONE);
                        } else {
                            int change_rank = obj.getIntValue("change_rank");
                            if (change_rank >= 0) {
                                ivIcon.setImageResource(R.drawable.icon_up);
                                tvNum.setText(change_rank+"");
                                tvNum.setTextColor(Color.parseColor("#1FD67F"));
                            } else if (change_rank < 0) {
                                ivIcon.setImageResource(R.drawable.icon_down);
                                tvNum.setText(Math.abs(change_rank)+"");
                                tvNum.setTextColor(Color.parseColor("#FC6261"));
                            }

                        }

                    } else {
                        llTotallevel.setVisibility(View.INVISIBLE);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTimer();
        SoundPlayUtil.play(16);
    }

    @Override
    public void getFindData(String reciveData) {

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

    private void initTimer() {
        backAndTime.setTimer(60);
        backAndTime.setVisableStatue(Boolean.valueOf(true));
        backAndTime.setBackVisableStatue(false);
        backAndTime.start();

        backAndTime.setOnTimerFinishListener(new BackAndTimerView.OnTimerFinishListener() {
            @Override
            public void onTimerFinish() {
                // backAndTime.stop();
                btnOver.setEnabled(false);
                clearStatus();

            }
        });
    }

    private void clearStatus() {
        try {
            backAndTime.stop();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceid", prf.readPrefs(Constant.DEVICEID));
            //jsonObject.put("teleNum", prf.readPrefs(Constant.USER_MOBILE));
            addSubscription(Constant.HTTP_URL + "php/v1/machine/clearlogin", jsonObject.toString(), new CallBack<String>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(ApiException e) {

                    backAndTime.stop();
                    prf.deletPrefs(Constant.USER_IMAGE);
                    prf.deletPrefs(Constant.USER_NAME);
                    prf.deletPrefs(Constant.LOGIN_STATUS);
                    prf.deletPrefs(Constant.USER_MOBILE);
                    prf.deletPrefs(Constant.DELIVER_LIST);
                    prf.deletPrefs(Constant.SAVE_LIST);
                    openActivity(UserSelectActivity.class);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }

                @Override
                public void onSuccess(String s) {
                    Logger.e("s--->" + s);
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(s);
                    String stateCode = jsonObject.getString("stateCode");
                    if (stateCode.equals("1")) {
                        //   backAndTime.stop();
                        prf.deletPrefs(Constant.USER_NAME);
                        prf.deletPrefs(Constant.USER_IMAGE);
                        prf.deletPrefs(Constant.LOGIN_STATUS);
                        prf.deletPrefs(Constant.USER_MOBILE);
                        prf.deletPrefs(Constant.DELIVER_LIST);
                        prf.deletPrefs(Constant.SAVE_LIST);
                        openActivity(UserSelectActivity.class);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    } else {
                        //  backAndTime.stop();
                        prf.deletPrefs(Constant.USER_IMAGE);
                        prf.deletPrefs(Constant.USER_NAME);
                        prf.deletPrefs(Constant.LOGIN_STATUS);
                        prf.deletPrefs(Constant.USER_MOBILE);
                        prf.deletPrefs(Constant.DELIVER_LIST);
                        prf.deletPrefs(Constant.SAVE_LIST);
                        openActivity(UserSelectActivity.class);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                    }


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.btn_over, R.id.btn_look})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_over:
                clearStatus();
                break;
            case R.id.btn_look:
                backAndTime.stop();
                final Dialog dialog = new Dialog(this, R.style.NobackDialog);
                dialog.setCancelable(true);
                View viewlook = getLayoutInflater().inflate(R.layout.user_waste_direction, null);
                Button confirm = viewlook.findViewById(R.id.btn_ok_look);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (timer != null) {
                            timer.cancel();
                        }
                        dialog.dismiss();
                        backAndTime.setTimer(backAndTime.getCurrentTime());
                        backAndTime.start();
                    }
                });
                dialog.setContentView(viewlook);
                Window window = dialog.getWindow();
                WindowManager m = getWindowManager();
                Display d = m.getDefaultDisplay(); // 获取屏幕宽、高
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.height = (int) (d.getHeight() * 1.2f); // 高度设置为屏幕的0.5
                lp.width = (int) (d.getWidth() * 1f); // 宽度设置为屏幕的0.6
                window.setAttributes(lp);
                dialog.show();
                //倒计时
                timer = new CountDownTimer(1000 * 10, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
                        if (secondsRemaining > 0) {

                        }
                    }

                    @Override
                    public void onFinish() {
                        dialog.dismiss();
                        backAndTime.setTimer(backAndTime.getCurrentTime());
                        backAndTime.start();

                    }
                }.start();
                break;
        }
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
    }
}
