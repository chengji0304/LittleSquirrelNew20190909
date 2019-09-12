package com.panda.littlesquirrel.view;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.panda.littlesquirrel.R;

/**
 * Created by jinjing on 2019/9/3.
 */

public class UserTelRegistDialog extends DialogFragment {

    private boolean onTouchOutside = false;
    private TextView tvMoblie;
    private ImageView qrCode;
    private Button btnChange;
    private String content;
    private Bitmap imgUrl;



    private CloseCallBack mcloseCallBack;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 设置背景透明
//        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        // 去掉标题 死恶心死恶心的
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getDialog().getWindow().setGravity(Gravity.CENTER);
        // set cancel on touch outside
        //getDialog().setCanceledOnTouchOutside(onTouchOutside);
//        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        // 去掉标题 死恶心死恶心的
        Window dialogWindow=  getDialog().getWindow();
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setGravity(Gravity.CENTER);
        // set cancel on touch outside
       // getDialog().setCanceledOnTouchOutside(onTouchOutside);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //设置margin为屏幕的20%
        WindowManager.LayoutParams lps = dialogWindow.getAttributes();
        lps.verticalMargin = -0.2f;
        dialogWindow.setAttributes(lps);
        // set cancel on touch outside
        getDialog().setCanceledOnTouchOutside(onTouchOutside);
        View hintView = inflater.inflate(R.layout.user_regist_tel_dialog, null);
        initView(hintView);
        return hintView;
    }

    private void initView(View hintView) {
        tvMoblie=hintView.findViewById(R.id.tv_user_mobile);
        qrCode=hintView.findViewById(R.id.img_user_qrcode);
        btnChange=hintView.findViewById(R.id.btn_close);
        tvMoblie.setText(content.substring(0,3)+" "+content.substring(3,7)+" "+content.substring(7,11));
        qrCode.setImageBitmap(imgUrl);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcloseCallBack.onClose();
            }
        });

    }

    /**
     * 设置内容
     *
     * @param content
     * @return
     */
    public UserTelRegistDialog setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 设置内容
     *
     * @param imgUrl
     * @return
     */
    public UserTelRegistDialog setImage(Bitmap imgUrl) {
        this.imgUrl=imgUrl;
        return this;
    }

    /**
     * 确定点击事件
     *
     * @param confirmCallBack
     * @return
     */
    /**
     * 取消点击事件
     *
     * @param closeCallBack
     * @return
     */
    public UserTelRegistDialog setOnCloseClickListener(CloseCallBack closeCallBack) {
        this.mcloseCallBack = closeCallBack;
        return this;
    }

    /**
     * 取消回调
     */
    public interface CloseCallBack {

        void onClose();

    }
}
