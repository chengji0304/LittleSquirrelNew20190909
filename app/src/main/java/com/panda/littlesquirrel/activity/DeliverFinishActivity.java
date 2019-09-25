package com.panda.littlesquirrel.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.orhanobut.logger.Logger;
import com.panda.littlesquirrel.R;
import com.panda.littlesquirrel.base.BaseActivity;
import com.panda.littlesquirrel.config.Constant;
import com.panda.littlesquirrel.entity.GarbageParam;
import com.panda.littlesquirrel.entity.RecordBean;
import com.panda.littlesquirrel.entity.SaveDeliveryRecord;
import com.panda.littlesquirrel.utils.CornerTransform;
import com.panda.littlesquirrel.utils.DefaultExceptionHandler;
import com.panda.littlesquirrel.utils.Encrypt;
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

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class DeliverFinishActivity extends BaseActivity {

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
    @Bind(R.id.ll_top)
    FrameLayout llTop;
    @Bind(R.id.ll_none)
    LinearLayout llNone;
    @Bind(R.id.tv_icon)
    TextView tvIcon;
    @Bind(R.id.tv_tip_01)
    TextView tvTip01;
    @Bind(R.id.img_catogry)
    ImageView imgCatogry;
    @Bind(R.id.tv_quantity)
    TextView tvQuantity;
    @Bind(R.id.tv_value)
    TextView tvValue;
    @Bind(R.id.ll_money)
    LinearLayout llMoney;
    @Bind(R.id.btn_over)
    Button btnOver;
    @Bind(R.id.btn_again)
    Button btnAgain;
    @Bind(R.id.ll_mid)
    FrameLayout llMid;
    @Bind(R.id.banner_buttom)
    Banner bannerButtom;
    @Bind(R.id.ll_buttom)
    FrameLayout llButtom;
    @Bind(R.id.backAndTime)
    BackAndTimerView backAndTime;
    @Bind(R.id.activity_deliver_finish)
    RelativeLayout activityDeliverFinish;
    @Bind(R.id.tv_none_moneypoint)
    TextView tvNoneMoneypoint;
    @Bind(R.id.tv_describe)
    TextView tvDescribe;
    @Bind(R.id.iv_logo)
    ImageView ivLogo;
    @Bind(R.id.ll_image)
    LinearLayout llImage;
    @Bind(R.id.user_image)
    CircleImageView userImage;
    // private SelcetInfo info;
    private PreferencesUtil prf;
    private ArrayList<GarbageParam> prfList;
    private GarbageParam garbageParam;
    private SpannableString spannableString;
    private String moeny = "";
    private String jifen = "";
    private String category;
    private ArrayList<Integer> images;
    private String key;
    private String sign;
    private String weight;
    private ArrayList<GarbageParam> saveList;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAdaptUtil.setCustomDesity(this, getApplication(), 360);
        setContentView(R.layout.activity_deliver_finish);
        ButterKnife.bind(this);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        prf = new PreferencesUtil(this);
        saveList = prf.getDataList(Constant.SAVE_LIST);
        prfList = prf.getDataList(Constant.DELIVER_LIST);
        key = prf.readPrefs(Constant.SIGNKEY);
        imageUrl=prf.readPrefs(Constant.USER_IMAGE);
        initData();
        initTimer();
    }

    private void initData() {
        sendTimerBoaadCastReceiver(this);
        initBanner();
        Glide.with(this)
                .load(imageUrl)
                .error( R.drawable.icon_user)
                .fallback( R.drawable.icon_user)
                .skipMemoryCache(true)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存//设置缓存
                .into(userImage);
        tvDeviceNum.setText("设备编号:" + prf.readPrefs(Constant.DEVICEID));
        btnMyRecycler.setVisibility(View.GONE);
        garbageParam = prfList.get(0);
        //info = getIntent().getBundleExtra("message").getParcelable("type");
        // Logger.e("info--->" + info.getTyep());
        switch (garbageParam.getCategory()) {
            case "1":
                if (Integer.valueOf(garbageParam.getQuantity()) <= 0) {
                    llNone.setVisibility(View.VISIBLE);
                    llMoney.setVisibility(View.GONE);
                    tvNoneMoneypoint.setText("本次投递未获得环保金");
                    moeny = "";
                } else {
                    imgCatogry.setImageResource(R.drawable.bottle);
                    spannableString = new SpannableString("饮料瓶 " + garbageParam.getQuantity() + "个");
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 3, spannableString.length(), 17);
                    tvQuantity.setText(spannableString);
                    moeny = StringUtil.getTotalPrice(garbageParam.getMoney(), garbageParam.getQuantity(), "6");
                    spannableString = new SpannableString("获得环保金 " + moeny);
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 5, spannableString.length(), 17);
                    tvValue.setText(spannableString);

                }


                break;
            case "2":
                // case "纸类2箱":
                if (Integer.valueOf(garbageParam.getQuantity()) < 6) {
                    llNone.setVisibility(View.VISIBLE);
                    llMoney.setVisibility(View.GONE);
                    tvNoneMoneypoint.setText("本次投递未获得环保金");
                    moeny = "";
                } else {
                    imgCatogry.setImageResource(R.drawable.paper);
                    spannableString = new SpannableString("纸类 " + StringUtil.getTotalWeight(garbageParam.getQuantity()) + "公斤");
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 3, spannableString.length(), 17);
                    tvQuantity.setText(spannableString);
                    moeny = StringUtil.getTotalPrice(garbageParam.getMoney(), garbageParam.getQuantity(), "1");
                    spannableString = new SpannableString("获得环保金 " + moeny);
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 5, spannableString.length(), 17);
                    tvValue.setText(spannableString);

                }


                break;
            case "3":
                if (Integer.valueOf(garbageParam.getQuantity()) < 6) {
                    llNone.setVisibility(View.VISIBLE);
                    llMoney.setVisibility(View.GONE);
                    tvNoneMoneypoint.setText("本次投递未获得环保金");
                    moeny = "";
                } else {
                    imgCatogry.setImageResource(R.drawable.book);
                    spannableString = new SpannableString("书籍 " + StringUtil.getTotalWeight(garbageParam.getQuantity()) + "公斤");
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 3, spannableString.length(), 17);
                    tvQuantity.setText(spannableString);
                    moeny = StringUtil.getTotalPrice(garbageParam.getMoney(), garbageParam.getQuantity(), "1");
                    spannableString = new SpannableString("获得环保金 " + moeny);
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 5, spannableString.length(), 17);
                    tvValue.setText(spannableString);

                }


                break;
            case "4":
                if (Integer.valueOf(garbageParam.getQuantity()) < 6) {
                    llNone.setVisibility(View.VISIBLE);
                    llMoney.setVisibility(View.GONE);
                    tvNoneMoneypoint.setText("本次投递未获得环保金");
                    moeny = "";
                } else {
                    imgCatogry.setImageResource(R.drawable.plastic);
                    spannableString = new SpannableString("塑料 " + StringUtil.getTotalWeight(garbageParam.getQuantity()) + "公斤");
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 3, spannableString.length(), 17);
                    tvQuantity.setText(spannableString);
                    moeny = StringUtil.getTotalPrice(garbageParam.getMoney(), garbageParam.getQuantity(), "1");
                    spannableString = new SpannableString("获得环保金 " + moeny);
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 5, spannableString.length(), 17);
                    tvValue.setText(spannableString);
                    //moeny = garbageParam.getMoney();
                }


                break;
            case "5":
                //case "纺织物2箱":
                if (Integer.valueOf(garbageParam.getQuantity()) < 6) {
                    llNone.setVisibility(View.VISIBLE);
                    llMoney.setVisibility(View.GONE);
                    tvNoneMoneypoint.setText("本次投递未获得环保金");

                    moeny = "";
                } else {
                    imgCatogry.setImageResource(R.drawable.fabric);
                    spannableString = new SpannableString("纺织物 " + StringUtil.getTotalWeight(garbageParam.getQuantity()) + "公斤");
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 3, spannableString.length(), 17);
                    tvQuantity.setText(spannableString);
                    moeny = StringUtil.getTotalPrice(garbageParam.getMoney(), garbageParam.getQuantity(), "1");
                    spannableString = new SpannableString("获得环保金 " + moeny);
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 5, spannableString.length(), 17);
                    tvValue.setText(spannableString);
                    // moeny = garbageParam.getMoney();
                }


                break;
            case "6":
                if (Integer.valueOf(garbageParam.getQuantity()) < 6) {
                    llNone.setVisibility(View.VISIBLE);
                    llMoney.setVisibility(View.GONE);
                    tvNoneMoneypoint.setText("本次投递未获得环保金");
                    moeny = "";
                } else {
                    imgCatogry.setImageResource(R.drawable.metal);
                    spannableString = new SpannableString("金属 " + StringUtil.getTotalWeight(garbageParam.getQuantity()) + "公斤");
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 3, spannableString.length(), 17);
                    tvQuantity.setText(spannableString);
                    moeny = StringUtil.getTotalPrice(garbageParam.getMoney(), garbageParam.getQuantity(), "1");
                    spannableString = new SpannableString("获得环保金 " + moeny);
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 5, spannableString.length(), 17);
                    tvValue.setText(spannableString);
                    //  moeny = garbageParam.getMoney();
                }


                break;
            case "7":
                if (Integer.valueOf(garbageParam.getQuantity()) < 6) {
                    llNone.setVisibility(View.VISIBLE);
                    llMoney.setVisibility(View.GONE);
                    tvNoneMoneypoint.setText("本次投递未获得环保积分");
                    jifen = "";
                } else {
                    imgCatogry.setImageResource(R.drawable.glass);
                    spannableString = new SpannableString("玻璃 " + 1 + "次");
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 2, spannableString.length(), 17);
                    tvQuantity.setText(spannableString);
                    spannableString = new SpannableString("获得环保积分 " + 1);
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 6, spannableString.length(), 17);
                    tvValue.setText(spannableString);
                    jifen = garbageParam.getMoney();
                }


                break;
            case "8":
                if (Integer.valueOf(garbageParam.getQuantity()) < 6) {
                    llNone.setVisibility(View.VISIBLE);
                    llMoney.setVisibility(View.GONE);
                    tvNoneMoneypoint.setText("本次投递未获得环保积分");
                    jifen = "";
                } else {
                    imgCatogry.setImageResource(R.drawable.harm);
                    spannableString = new SpannableString("有害垃圾 " + garbageParam.getMoney() + "次");
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 4, spannableString.length(), 17);
                    tvQuantity.setText(spannableString);
                    spannableString = new SpannableString("获得环保积分 " + garbageParam.getMoney());
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#268EF1")), 6, spannableString.length(), 17);
                    tvValue.setText(spannableString);
                    jifen = garbageParam.getMoney();
                }

                break;

        }

        // info = getIntent().getBundleExtra("message").getParcelable("type");
//        switch (info.getTyep()) {
//
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
       // sendTimerBoaadCastReceiver(this);
        backAndTime.start();
        SoundPlayUtil.play(14);

    }

    private void initTimer() {
        backAndTime.setTimer(60);
        backAndTime.setVisableStatue(Boolean.valueOf(true));
        backAndTime.setBackVisableStatue(false);
        backAndTime.setOnTimerFinishListener(new BackAndTimerView.OnTimerFinishListener() {
            @Override
            public void onTimerFinish() {
              //  backAndTime.stop();
                btnOver.setEnabled(false);
                saveRecord();

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        backAndTime.stop();
        if (mStartHandler != null) {
            mStartHandler.removeCallbacksAndMessages(null);
        }
//       mStartHandler.removeCallbacksAndMessages(null);
//        serialPortUtils.closeSerialPort();
    }


    @Override
    public void getFindData(String reciveData) {

    }

    public void saveRecord() {
        backAndTime.stop();
        try {
            String serialNum = StringUtil.getSerialNumber();
            SaveDeliveryRecord record = new SaveDeliveryRecord();
            record.setDeviceID(prf.readPrefs(Constant.DEVICEID));
            record.setSerialNum(serialNum);
            record.setAccount(prf.readPrefs(Constant.USER_MOBILE));
            RecordBean bean = null;
            if (saveList.size() == 1) {
                bean = new RecordBean();
                bean.setCategory(Integer.valueOf(garbageParam.getCategory()));
                if (garbageParam.getCategory().equals("1")) {
                    bean.setCount(Integer.valueOf(garbageParam.getQuantity()));
                    bean.setWeight(0);
                    bean.setCanNum(Integer.valueOf(garbageParam.getCanNum()));
                    sign = prf.readPrefs(Constant.USER_MOBILE) + prf.readPrefs(Constant.DEVICEID) + garbageParam.getCategory() + garbageParam.getQuantity() + "0" + key;
                    record.getRecord().add(bean);
                } else {
                    bean.setCount(0);
                    bean.setCanNum(Integer.valueOf(garbageParam.getCanNum()));
                    weight = StringUtil.getTotalWeight(garbageParam.getQuantity());
                    if (weight.substring(weight.length() - 2, weight.length()).equals("00")) {
                        Logger.e("1");
                        bean.setWeight(Double.valueOf(weight.substring(0, weight.indexOf("."))));
                    } else if (weight.substring(weight.length() - 1, weight.length()).equals("0")) {
                        Logger.e("2");
                        bean.setWeight(Double.valueOf(weight.substring(0, weight.length() - 1)));
                    } else {
                        Logger.e("3");
                        bean.setWeight(Double.valueOf(StringUtil.getTotalWeight(garbageParam.getQuantity())));
                    }
                    record.getRecord().add(bean);
                    if (garbageParam.getQuantity().equals("0")) {
                        sign = prf.readPrefs(Constant.USER_MOBILE) + prf.readPrefs(Constant.DEVICEID) + garbageParam.getCategory() + "0" + "0" + key;
                    } else {
                        Logger.e("weight--->" + StringUtil.getTotalWeight(prfList.get(0).getQuantity()));
                        if (weight.substring(weight.length() - 2, weight.length()).equals("00")) {
                            Logger.e("11");
                            //  bean.setWeight(Double.valueOf(weight.substring(0,weight.indexOf("."))));
                            sign = prf.readPrefs(Constant.USER_MOBILE) + prf.readPrefs(Constant.DEVICEID) + garbageParam.getCategory() + "0" + Integer.valueOf(weight.substring(0, weight.indexOf("."))) + key;

                        } else if (weight.substring(weight.length() - 1, weight.length()).equals("0")) {
                            Logger.e("22");
                            //  bean.setWeight(Double.valueOf(weight.substring(0,weight.length()-1)));
                            sign = prf.readPrefs(Constant.USER_MOBILE) + prf.readPrefs(Constant.DEVICEID) + garbageParam.getCategory() + "0" + Double.valueOf(weight.substring(0, weight.length() - 1)) + key;
                        } else {
                            Logger.e("33");
                            sign = prf.readPrefs(Constant.USER_MOBILE) + prf.readPrefs(Constant.DEVICEID) + garbageParam.getCategory() + "0" + StringUtil.getTotalWeight(garbageParam.getQuantity()) + key;
                        }


                    }
                }
            } else {
                for (int i = 0; i < saveList.size(); i++) {
                    bean = new RecordBean();
                    bean.setCategory(Integer.valueOf(saveList.get(i).getCategory()));
                    if (saveList.get(i).getCategory().equals("1")) {
                        bean.setCount(Integer.valueOf(saveList.get(i).getQuantity()));
                        bean.setWeight(0);
                        bean.setCanNum(Integer.valueOf(saveList.get(i).getCanNum()));
                    } else {
                        weight = StringUtil.getTotalWeight(saveList.get(i).getQuantity());
                        if (weight.substring(weight.length() - 2, weight.length()).equals("00")) {
                            Logger.e("1");
                            bean.setWeight(Double.valueOf(weight.substring(0, weight.indexOf("."))));
                        } else if (weight.substring(weight.length() - 1, weight.length()).equals("0")) {
                            Logger.e("2");
                            bean.setWeight(Double.valueOf(weight.substring(0, weight.length() - 1)));
                        } else {
                            Logger.e("3");
                            bean.setWeight(Double.valueOf(StringUtil.getTotalWeight(saveList.get(i).getQuantity())));
                        }
                        bean.setCount(0);
                        bean.setCanNum(Integer.valueOf(saveList.get(i).getCanNum()));
                        // bean.setWeight(Double.valueOf(StringUtil.getTotalWeight(prfList.get(i).getQuantity())));
                    }

                    record.getRecord().add(bean);
                }

                if (saveList.get(0).getCategory().equals("1")) {
                    sign = prf.readPrefs(Constant.USER_MOBILE) + prf.readPrefs(Constant.DEVICEID) + saveList.get(0).getCategory() + saveList.get(0).getQuantity() + "0" + key;
                } else {

                    if (saveList.get(0).getQuantity().equals("0")) {
                        sign = prf.readPrefs(Constant.USER_MOBILE) + prf.readPrefs(Constant.DEVICEID) + saveList.get(0).getCategory() + "0" + "0" + key;
                    } else {
                        weight = StringUtil.getTotalWeight(saveList.get(0).getQuantity());
                        Logger.e("weight--->" + StringUtil.getTotalWeight(saveList.get(0).getQuantity()));
                        if (weight.substring(weight.length() - 2, weight.length()).equals("00")) {
                            Logger.e("11");
                            //  bean.setWeight(Double.valueOf(weight.substring(0,weight.indexOf("."))));
                            sign = prf.readPrefs(Constant.USER_MOBILE) + prf.readPrefs(Constant.DEVICEID) + saveList.get(0).getCategory() + "0" + Integer.valueOf(weight.substring(0, weight.indexOf("."))) + key;

                        } else if (weight.substring(weight.length() - 1, weight.length()).equals("0")) {
                            Logger.e("22");
                            //  bean.setWeight(Double.valueOf(weight.substring(0,weight.length()-1)));
                            sign = prf.readPrefs(Constant.USER_MOBILE) + prf.readPrefs(Constant.DEVICEID) + saveList.get(0).getCategory() + "0" + Double.valueOf(weight.substring(0, weight.length() - 1)) + key;
                        } else {
                            Logger.e("33");
                            sign = prf.readPrefs(Constant.USER_MOBILE) + prf.readPrefs(Constant.DEVICEID) + saveList.get(0).getCategory() + "0" + StringUtil.getTotalWeight(saveList.get(0).getQuantity()) + key;
                        }

                        // Logger.e("weight--->"+StringUtil.getTotalWeight(prfList.get(0).getQuantity()));
                        // sign=prf.readPrefs(Constant.USER_MOBILE)+prf.readPrefs(Constant.DEVICEID)+prfList.get(0).getCategory()+"0"+StringUtil.getTotalWeight(prfList.get(0).getQuantity())+key;

                    }
                }

            }
//            switch (garbageParam.getCategory()) {
//                case "饮料瓶":
//                    category = "1";
//                    break;
//                case "纸类1箱":
//                case "纸类2箱":
//                    category = "2";
//                    break;
//                case "书籍":
//                    category = "3";
//                    break;
//                case "塑料":
//                    category = "4";
//                    break;
//                case "纺织物1箱":
//                case "纺织物2箱":
//                    category = "5";
//                    break;
//                case "金属":
//                    category = "6";
//                    break;
//                case "玻璃":
//                    category = "7";
//                    break;
//                case "有害垃圾":
//                    category = "8";
//                    break;
//            }

            Logger.e("sign---->" + sign);

            record.setSignedKey(Encrypt.SHA512(sign));
            String jsonString = JSON.toJSONString(record);
            Logger.e("json--->" + jsonString);
            addSubscription(Constant.HTTP_URL + "machine/delivery/saveRecord", jsonString, new CallBack<String>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(ApiException e) {
                    if(!StringUtil.isEmpty(moeny)){
                        Intent intent = new Intent(DeliverFinishActivity.this, DeliverSuccessMoneyActivity.class);
                        intent.putExtra("money", moeny);
                        intent.putExtra("jifen", jifen);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }else {
                        Intent intent = new Intent(DeliverFinishActivity.this, DeliverSuccessActivity.class);
                        intent.putExtra("money", moeny);
                        intent.putExtra("jifen", jifen);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                    //  backAndTime.setBackEnable(true);
//                    Intent intent = new Intent(DeliverFinishActivity.this, DeliverSuccessActivity.class);
//                    intent.putExtra("money", moeny);
//                    intent.putExtra("jifen", jifen);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

                @Override
                public void onSuccess(String s) {
                    Logger.e("s--->" + s);
                    JSONObject jsonObject = JSON.parseObject(s);
                    String stateCode = jsonObject.getString("stateCode");
                    if (stateCode.equals("1")) {
                        if(!StringUtil.isEmpty(moeny)){
                            Intent intent = new Intent(DeliverFinishActivity.this, DeliverSuccessMoneyActivity.class);
                            intent.putExtra("money", moeny);
                            intent.putExtra("jifen", jifen);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }else {
                            Intent intent = new Intent(DeliverFinishActivity.this, DeliverSuccessActivity.class);
                            intent.putExtra("money", moeny);
                            intent.putExtra("jifen", jifen);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }

                    } else {
                        //backAndTime.setBackEnable(true);
                        if(!StringUtil.isEmpty(moeny)){
                            Intent intent = new Intent(DeliverFinishActivity.this, DeliverSuccessMoneyActivity.class);
                            intent.putExtra("money", moeny);
                            intent.putExtra("jifen", jifen);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }else {
                            Intent intent = new Intent(DeliverFinishActivity.this, DeliverSuccessActivity.class);
                            intent.putExtra("money", moeny);
                            intent.putExtra("jifen", jifen);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.btn_over, R.id.btn_again})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_over:
//                Intent intent = new Intent(DeliverFinishActivity.this, DeliverSuccessActivity.class);
//                intent.putExtra("money", moeny);
//                intent.putExtra("jifen", jifen);
//                startActivity(intent);
//                finish();
                btnOver.setEnabled(false);
                saveRecord();


                break;
            case R.id.btn_again:
                backAndTime.stop();

                openActivity(UserSelectActivity.class);
//                Intent again = new Intent(DeliverFinishActivity.this, UserSelectActivity.class);
//                again.putExtra("status","2");
//                startActivity(again);
                finish();
                break;
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
}
