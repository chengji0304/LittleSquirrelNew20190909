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

import com.panda.littlesquirrel.R;

/**
 * Created by jinjing on 2019/9/2.
 */

public class LoginTipDialog extends DialogFragment {

    private boolean onTouchOutside = false;
    private Button btnClose;
    private CloseCallBack mcloseCallBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 设置背景透明
        Window dialogWindow =  getDialog().getWindow();
       // WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
//        // 去掉标题 死恶心死恶心的
//        dialogWindow.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
//        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogWindow.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        //设置margin为屏幕的20%
//        WindowManager.LayoutParams lps = dialogWindow.getAttributes();
//        lps.verticalMargin = 0.14f;
//        dialogWindow.setAttributes(lps);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogWindow.setGravity(Gravity.CENTER);
        // set cancel on touch outside
        // getDialog().setCanceledOnTouchOutside(onTouchOutside);
        dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogWindow.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //设置margin为屏幕的20%
        WindowManager.LayoutParams lps = dialogWindow.getAttributes();
        lps.verticalMargin = -0.14f;
        dialogWindow.setAttributes(lps);
      //  dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL);
//        // set cancel on touch outside
//        lp.height = 300; // 高度
        getDialog().setCanceledOnTouchOutside(onTouchOutside);
        View hintView = inflater.inflate(R.layout.reclye_login_tip, null);
        initView(hintView);
        return hintView;
    }

    private void initView(View hintView) {
        btnClose=hintView.findViewById(R.id.btn_cancel);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mcloseCallBack.onClose();
            }
        });


    }

    /* 取消点击事件
    *
    * @param closeCallBack
    * @return
            */
    public LoginTipDialog setOnCloseClickListener(CloseCallBack closeCallBack) {
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
