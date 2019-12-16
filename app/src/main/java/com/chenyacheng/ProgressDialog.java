package com.chenyacheng;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

/**
 * 自定义progressDialog对话框，用于网络请求时提示用户当前操作正在运行，让用户等待.
 *
 * @author chenyacheng
 * @date 2019/12/16
 */
public class ProgressDialog extends Dialog {

    public ProgressDialog(@NonNull Context context) {
        super(context, R.style.network_request_progress_dialog);
    }

    public void showProgress(String message) {
        // 自定义的layout样式
        setContentView(R.layout.network_request_progress_dialog);
        // 设置不可通过点击外面区域取消
        setCanceledOnTouchOutside(false);
        TextView textView = findViewById(R.id.progress_textView);
        textView.setText(message);
    }
}
