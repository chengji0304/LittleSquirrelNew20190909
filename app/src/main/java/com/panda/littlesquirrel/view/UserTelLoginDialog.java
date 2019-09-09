package com.panda.littlesquirrel.view;

import android.app.DialogFragment;
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
import com.orhanobut.logger.Logger;
import com.panda.littlesquirrel.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jinjing on 2019/9/2.
 */

public class UserTelLoginDialog extends DialogFragment {
    private boolean onTouchOutside = false;
    private TextView tvMobile;
    private CircleImageView ivUser;
    private TextView tvUser;
    private Button btnChange;
    private Button btnClose;
    private String content;
    private String userName;
    private String imgUrl;
    private ConfirmCallBack mconfirmCallBack;
    private CloseCallBack mcloseCallBack;
    /**
     * 设置内容
     *
     * @param content
     * @return
     */
    public UserTelLoginDialog setContent(String content) {
        this.content = content;
        return this;
    }
    /**
     * 设置内容
     *
     * @param userName
     * @return
     */
    public UserTelLoginDialog setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * 设置内容
     *
     * @param imgUrl
     * @return
     */
    public UserTelLoginDialog setImage(String imgUrl) {
        this.imgUrl = imgUrl;

        return this;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 设置背景透明
//        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transpar    ent);
//        // 去掉标题 死恶心死恶心的
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getDialog().getWindow().setGravity(Gravity.CENTER);
        Window dialogWindow =  getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        // 去掉标题 死恶心死恶心的
        dialogWindow.setGravity(Gravity.TOP|Gravity.CENTER_VERTICAL);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
       // dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        //设置margin为屏幕的20%
        WindowManager.LayoutParams lps = dialogWindow.getAttributes();
        lps.verticalMargin = 0.2f;
        dialogWindow.setAttributes(lps);
        // set cancel on touch outside
        getDialog().setCanceledOnTouchOutside(onTouchOutside);
        View hintView = inflater.inflate(R.layout.user_login_tel_dialog, null);
        initView(hintView);
        return hintView;
    }

    private void initView(View hintView) {
        Logger.e("url--->"+imgUrl);
        Logger.e("content--->"+content);
        tvMobile = hintView.findViewById(R.id.tv_tel_num);
        tvMobile.setText(content.substring(0, 3) + " " + content.substring(3, 7) + " " + content.substring(7, 11));
        ivUser=hintView.findViewById(R.id.user_image);
        tvUser=hintView.findViewById(R.id.tv_username);
        btnChange=hintView.findViewById(R.id.btn_change);
        btnClose=hintView.findViewById(R.id.btn_open);
        tvUser.setText(userName);

        Glide.with(this)
                .load(imgUrl)
                .skipMemoryCache(true)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .into(ivUser);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcloseCallBack.onClose();

            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mconfirmCallBack.onConfirm();
            }
        });

    }

    /**
     * 确定点击事件
     *
     * @param confirmCallBack
     * @return
     */
    public UserTelLoginDialog setOnConfirmClickListener(ConfirmCallBack confirmCallBack) {
        this.mconfirmCallBack = confirmCallBack;
        return this;
    }

    /**
     * 取消点击事件
     *
     * @param closeCallBack
     * @return
     */
    public UserTelLoginDialog setOnCloseClickListener(CloseCallBack closeCallBack) {
        this.mcloseCallBack = closeCallBack;
        return this;
    }
    /**
     * 取消回调
     */
    public interface ConfirmCallBack {

        void onConfirm();

    }

    /**
     * 取消回调
     */
    public interface CloseCallBack {

        void onClose();

    }
}
