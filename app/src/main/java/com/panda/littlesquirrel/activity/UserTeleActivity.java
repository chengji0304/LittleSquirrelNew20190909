package com.panda.littlesquirrel.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.orhanobut.logger.Logger;
import com.panda.littlesquirrel.R;
import com.panda.littlesquirrel.base.BaseActivity;
import com.panda.littlesquirrel.base.BaseDialogFragment;
import com.panda.littlesquirrel.config.Constant;
import com.panda.littlesquirrel.entity.SelcetInfo;
import com.panda.littlesquirrel.utils.Base64Utils;
import com.panda.littlesquirrel.utils.CornerTransform;
import com.panda.littlesquirrel.utils.DefaultExceptionHandler;
import com.panda.littlesquirrel.utils.ForbiddenSysKeyBoardUtils;
import com.panda.littlesquirrel.utils.PreferencesUtil;
import com.panda.littlesquirrel.utils.ScreenUtil;
import com.panda.littlesquirrel.utils.SoundPlayUtil;

import com.panda.littlesquirrel.utils.StringUtil;
import com.panda.littlesquirrel.utils.Utils;
import com.panda.littlesquirrel.view.BackAndTimerView;
import com.panda.littlesquirrel.view.DigitalKeyboard;
import com.panda.littlesquirrel.view.ErrorStatusDialog;
import com.panda.littlesquirrel.view.UserDigitalKeyboard;
import com.panda.littlesquirrel.view.UserProtolDialog;
import com.panda.littlesquirrel.view.UserTelLoginDialog;
import com.panda.littlesquirrel.view.UserTelRegistDialog;
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


public class UserTeleActivity extends BaseActivity {

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
    @Bind(R.id.tv_line_01)
    TextView tvLine01;
    @Bind(R.id.tv_tip_01)
    TextView tvTip01;
    @Bind(R.id.tv_line_02)
    TextView tvLine02;
    @Bind(R.id.tv_tip_02)
    TextView tvTip02;
    @Bind(R.id.ed_tel)
    EditText edTel;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.tv_tip_03)
    TextView tvTip03;
    @Bind(R.id.check_protocol)
    CheckBox checkProtocol;
    @Bind(R.id.tv_tip_04)
    TextView tvTip04;
    @Bind(R.id.tv_protocol)
    TextView tvProtocol;
    @Bind(R.id.tv_scan)
    TextView tvScan;
    @Bind(R.id.tv_switch_scan)
    TextView tvSwitchScan;
    @Bind(R.id.user_digital_keyboard)
    UserDigitalKeyboard userDigitalKeyboard;
    @Bind(R.id.ll_mid)
    FrameLayout llMid;
    @Bind(R.id.banner_buttom)
    Banner bannerButtom;
    @Bind(R.id.ll_buttom)
    FrameLayout llButtom;
    @Bind(R.id.backAndTime)
    BackAndTimerView backAndTime;
    @Bind(R.id.activity_user_tele)
    RelativeLayout activityUserTele;
    // private UserTelRightDialog dialog = new UserTelRightDialog();
    private UserTelLoginDialog lDialog;
    private UserTelRegistDialog rDialog;

    private ErrorStatusDialog errorStatusDialog = new ErrorStatusDialog();
    private UserProtolDialog protolDialog = new UserProtolDialog();
    private CountDownTimer ctimer;
    private PreferencesUtil prf;
    // private SelcetInfo info;
    private ArrayList<Integer> images;
    // 特殊下标位置
    private static final int PHONE_INDEX_3 = 3;
    private static final int PHONE_INDEX_4 = 4;
    private static final int PHONE_INDEX_8 = 8;
    private static final int PHONE_INDEX_9 = 9;
    private Bitmap bitmap;
    private Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tele);
        ButterKnife.bind(this);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        //  System.loadLibrary("serial_port");
//        serialPort = serialPortUtils.openSerialPort();
//        setListener();
        prf = new PreferencesUtil(this);
        initData();

    }

    private void initData() {
       // sendTimerBoaadCastReceiver(this);
        initBanner();
        //3203120008
        tvDeviceNum.setText("设备编号:" + prf.readPrefs(Constant.DEVICEID));
        //tvDeviceNum.setText("设备编号:" + "3203120008");
        ForbiddenSysKeyBoardUtils.bannedSysKeyBoard(UserTeleActivity.this, edTel);
        // ForbiddenSysKeyBoardUtils.bannedSysKeyBoard(UserTeleActivity.this,);
        btnMyRecycler.setVisibility(View.GONE);
        edTel.post(new Runnable() {
            @Override
            public void run() {
                edTel.getText().clear();
            }
        });
        edTel.post(new Runnable() {
            @Override
            public void run() {
                SoundPlayUtil.enablePlay = true;
                edTel.requestFocus();
            }
        });
        checkProtocol.post(new Runnable() {
            @Override
            public void run() {
                checkProtocol.setChecked(true);
            }
        });

        edTel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s == null || s.length() == 0) {
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length(); i++) {
                    if (i != PHONE_INDEX_3 && i != PHONE_INDEX_8 && s.charAt(i) == ' ') {
                        continue;
                    } else {
                        sb.append(s.charAt(i));
                        if ((sb.length() == PHONE_INDEX_4 || sb.length() == PHONE_INDEX_9) && sb.charAt(sb.length() - 1) != ' ') {
                            sb.insert(sb.length() - 1, ' ');
                        }
                    }
                }
                if (!sb.toString().equals(s.toString())) {
                    int index = start + 1;
                    if (sb.charAt(start) == ' ') {
                        if (before == 0) {
                            index++;
                        } else {
                            index--;
                        }
                    } else {
                        if (before == 1) {
                            index--;
                        }
                    }

                    edTel.setText(sb.toString());
                    edTel.setSelection(index);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 13) {
                    btnOk.setEnabled(false);
                    btnOk.setTextColor(Color.parseColor("#999999"));
                    return;
                }

                if (checkProtocol.isChecked() == false) {
                    btnOk.setEnabled(false);
                    btnOk.setTextColor(Color.parseColor("#999999"));
                    return;
                }

                btnOk.setEnabled(true);
                btnOk.setTextColor(Color.parseColor("#FFFFFF"));

            }
        });
        userDigitalKeyboard.setEditText(edTel);
        checkProtocol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if ((isChecked) && (edTel.getText().length() == 13)) {
                    btnOk.setEnabled(true);
                    btnOk.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    btnOk.setEnabled(false);
                    btnOk.setTextColor(Color.parseColor("#999999"));
                }


            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendTimerBoaadCastReceiver(this);
        initTimer();
    }

    private void initTimer() {
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
                if(rDialog!=null){
                    rDialog.dismiss();
                }
                if(lDialog!=null){
                    lDialog.dismiss();
                }
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
                if(rDialog!=null){
                    rDialog.dismiss();
                }
                if(lDialog!=null){
                    lDialog.dismiss();
                }
                openActivity(UserSelectActivity.class);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    @OnClick({R.id.tv_switch_scan, R.id.tv_scan, R.id.btn_ok, R.id.tv_protocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_switch_scan:
                openActivity(UserLoginActivity.class);
                finish();
                break;
            case R.id.tv_scan:
                openActivity(UserLoginActivity.class);
                finish();
                break;
            case R.id.btn_ok:
                if (ctimer != null) {
                    ctimer.cancel();
                }

                Logger.e("mobile--->" + edTel.getText().toString().trim().replaceAll(" ", ""));
                if (!StringUtil.isMobileExact(edTel.getText().toString().trim().replaceAll(" ", ""))) {
                    //SoundPlayUtils.StartMusic(18);
                    SoundPlayUtil.play(12);
                    errorStatusDialog.setContent("您输入的手机号有误请重新输入");
                    errorStatusDialog.setImage(R.drawable.error);
                    errorStatusDialog.setOnConfirmClickListener(new ErrorStatusDialog.ConfirmCallBack() {
                        @Override
                        public void onConfirm() {
                            backAndTime.stop();
                            if (ctimer != null) {
                                ctimer.cancel();
                            }
                            edTel.setText("");
                            errorStatusDialog.dismiss();

                        }
                    });
                    errorStatusDialog.show(getFragmentManager(), "error_dialog");
                    //倒计时
                    ctimer = new CountDownTimer(1000 * 10, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
                            int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
                            if (secondsRemaining > 0) {

                            }
                        }

                        @Override
                        public void onFinish() {

                            if (errorStatusDialog != null) {
                                errorStatusDialog.dismiss();
                            }
                            edTel.setText("");
                            ctimer.cancel();
                            backAndTime.setTimer(backAndTime.getCurrentTime());
                            backAndTime.start();

                        }
                    }.start();
                } else {
                   clearStatus();
                }


                break;
            case R.id.tv_protocol:
                if (backAndTime != null) {
                    backAndTime.stop();
                }
                protolDialog.setOnCloseClickListener(new UserProtolDialog.CloseCallBack() {
                    @Override
                    public void onClose() {
                        protolDialog.dismiss();
                        backAndTime.setTimer(backAndTime.getCurrentTime());
                        backAndTime.start();
                    }
                });
//                UserProtolDialog.getInstance().setOnCloseClickListener(new OnCloseDialogListener() {
//                    @Override
//                    public void onCloseDialog() {
//                        backAndTime.setTimer(backAndTime.getCurrentTime());
//                        backAndTime.start();
//                    }
//                });
                protolDialog.show(getFragmentManager(), "user_protocol");
                ctimer = new CountDownTimer(1000 * 20, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                        int secondsRemaining = (int) (millisUntilFinished / 1000) - 1;
                        if (secondsRemaining > 0) {

                        }
                    }

                    @Override
                    public void onFinish() {
                        if (protolDialog != null) {
                            protolDialog.dismiss();
                        }

                        ctimer.cancel();
                        backAndTime.setTimer(backAndTime.getCurrentTime());
                        backAndTime.start();

                    }
                }.start();

                break;

        }
    }

    private void clearStatus() {
        try {
            JSONObject jsonObject = new JSONObject();
            //3203120008
            jsonObject.put("deviceid", prf.readPrefs(Constant.DEVICEID));
            //  jsonObject.put("deviceid", "3203120008");
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
                    Logger.e("s--->" + s);
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(s);
                    String stateCode = jsonObject.getString("stateCode");
                    if (stateCode.equals("1")) {
                        com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSON.parseObject(jsonObject.getString("result"));
                        String status = object.getString("status");
                        if (status.equals("1")) {
                            // 手机号登录
                            mobileLogin();
                        } else {
                            //故障页面
                            openActivity(UserSelectActivity.class);
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mobileLogin() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceid", prf.readPrefs(Constant.DEVICEID));
            // jsonObject.put("deviceid", "3203120008");
            jsonObject.put("phone_num", StringUtil.getPhoneText(edTel));
            addSubscription(Constant.HTTP_URL + "php/v1/machine/mobilelogin", jsonObject.toString(), new CallBack<String>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(ApiException e) {
                    //openActivity(UserSelectActivity.class);

                }

                @Override
                public void onSuccess(String s) {
                    Logger.e("s--->" + s);
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(s);
                    String stateCode = jsonObject.getString("stateCode");
                    if (stateCode.equals("1")) {
                        final com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSON.parseObject(jsonObject.getString("result"));
                        String status = object.getString("status");
                        final String phone_num = object.getString("phone_num");
                        String nick_name = object.getString("nick_name");
                        final String avatar_url = object.getString("avatar_url");
                        if (status.equals("1")) {
                            if (timer != null) {
                                timer.cancel();
                            }
                            if (lDialog != null) {
                                lDialog.dismiss();
                            }

                            if (rDialog != null) {
                                rDialog.dismiss();
                            }
                            lDialog = new UserTelLoginDialog();
                            Logger.e("mobile---》" + phone_num);
                            Logger.e("nick_name---》" + nick_name);
                            lDialog.setContent(phone_num);
                            lDialog.setImage(object.getString("avatar_url"));
                            lDialog.setUserName(nick_name);
                            lDialog.setOnConfirmClickListener(new UserTelLoginDialog.ConfirmCallBack() {
                                @Override
                                public void onConfirm() {
                                    //更换手机号
                                    lDialog.dismiss();
                                }
                            });
                            lDialog.setOnCloseClickListener(new UserTelLoginDialog.CloseCallBack() {
                                @Override
                                public void onClose() {
                                    backAndTime.stop();
                                    lDialog.dismiss();
                                    //开箱
                                    prf.writePrefs(Constant.LOGIN_STATUS, "1");
                                    prf.writePrefs(Constant.USER_MOBILE, phone_num);
                                    prf.writePrefs(Constant.USER_IMAGE, avatar_url);
                                    openActivity(UserTypeSelectActivity.class);
                                    finish();

                                }
                            });
                            lDialog.show(getFragmentManager(), "user_login");
                        } else if (status.equals("2")) {
                            //首次登录
                            getQrCode();

                        } else {
                            //故障
                            openActivity(UserSelectActivity.class);
                        }
                    } else {
                        //故障
                        openActivity(UserSelectActivity.class);

                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getQrCode() {
        try {
            JSONObject jsonObject = new JSONObject();
            //3203120008
            jsonObject.put("deviceid", prf.readPrefs(Constant.DEVICEID));
            //jsonObject.put("deviceid", "3203120008");
            addSubscription(Constant.HTTP_URL + "php/v1/machine/getqrcode", jsonObject.toString(), new CallBack<String>() {
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
                    Logger.e("s---->" + s);
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(s);
                    String stateCode = jsonObject.getString("stateCode");
                    if (stateCode.equals("1")) {
                        com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSON.parseObject(jsonObject.getString("result"));
                        String qrCode = object.getString("qrcode");
                        bitmap = Base64Utils.base64ToBitmap(qrCode);
                        // .setImageBitmap(bitmap);

                        if (rDialog != null) {
                            rDialog.dismiss();
                        }
                        rDialog = new UserTelRegistDialog();
                        rDialog.setContent(StringUtil.getPhoneText(edTel));
                        rDialog.setImage(bitmap);
                        timer = new Timer();
                        MyTimerTask myTimerTask = new MyTimerTask();//定时器
                        timer.schedule(myTimerTask, 1000, 5000);//每隔5秒

                        rDialog.setOnCloseClickListener(new UserTelRegistDialog.CloseCallBack() {
                            @Override
                            public void onClose() {
                                rDialog.dismiss();
                                if (timer != null) {
                                    timer.cancel();
                                }
                            }
                        });
                        rDialog.show(getFragmentManager(), "regist_dialog");

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
            //   Logger.e("requestNumber-->" + (requestNumber));
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

    /*
    查询
    */
    private void getScanRecycler() {
        try {
            JSONObject jsonObject = new JSONObject();
            //3203120008
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
                  //  openActivity(UserSelectActivity.class);
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

                        if (StringUtil.isEmpty(open_id)) {

                        } else if (!StringUtil.isEmpty(open_id) && StringUtil.isEmpty(phone_num)) {
                            if(rDialog!=null){
                                rDialog.dismiss();
                            }
                            if(timer!=null){
                                timer.cancel();
                            }
                            backAndTime.stop();
                            openActivity(PrepareLoginActivity.class);
                        } else if (!StringUtil.isEmpty(phone_num) && !StringUtil.isEmpty(phone_num)) {
                          // Logger.e("info--->"+0);
                          if(rDialog!=null){
                             rDialog.dismiss();
                          }
                            if(timer!=null){
                               timer.cancel();
                            }
                            backAndTime.stop();
                            Intent intent = new Intent(UserTeleActivity.this, UserOnLoadingActivity.class);
                            intent.putExtra("phone_num", phone_num);
                            intent.putExtra("nick_name", nick_name);
                            intent.putExtra("avatar_url", avatar_url.trim());
                            startActivity(intent);
                            finish();
                          //  prf.writePrefs(Constant.USER_IMAGE, avatar_url);
                           // prf.writePrefs(Constant.LOGIN_STATUS, "1");
                           // Logger.e("info--->"+1);
                           // openActivity(UserOnLoadingActivity.class);
                            //Logger.e("info--->"+2);
                            //finish();
                        }

                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    private void UserLogin() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("deviceID", prf.readPrefs(Constant.DEVICEID));
            jsonObject.put("teleNum", edTel.getText().toString().trim().replaceAll(" ", ""));
            addSubscription(Constant.HTTP_URL + "machine/verification/userLoginByPhoneNum", jsonObject.toString(), new CallBack<String>() {
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
                    Logger.e("s--->" + s);
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSON.parseObject(s);
                    String stateCode = jsonObject.getString("stateCode");
                    if (stateCode.equals("1")) {
                        prf.writePrefs(Constant.LOGIN_STATUS, "1");
                        prf.writePrefs(Constant.USER_MOBILE, edTel.getText().toString().trim().replaceAll(" ", ""));
                        openActivity(UserTypeSelectActivity.class);
                        finish();

                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (backAndTime != null) {
            backAndTime.stop();
        }

        if (ctimer != null) {
            ctimer.cancel();
        }
        //userDigitalKeyboard.release();
        if(rDialog!=null){
            rDialog.dismiss();
        }
        if(lDialog!=null){
           lDialog.dismiss();
        }
        if (timer != null) {
            timer.cancel();
        }

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
