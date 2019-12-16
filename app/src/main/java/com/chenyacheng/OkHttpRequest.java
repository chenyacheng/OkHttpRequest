package com.chenyacheng;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author chenyacheng
 * @date 2019/12/16
 */
public class OkHttpRequest {

    public static String POST = "POST";
    public static String GET = "GET";
    private ProgressDialog progressDialog;
    private boolean showProgressDialog;
    private WeakReference<Context> weakReference;
    private String httpType;
    private Map<String, String> map;
    private String url;
    private ResponseListener responseListener;
    private OkHttpClient okHttpClient;
    private Call call;
    private Handler handler;

    /**
     * 封装的网络请求类
     *
     * @param showProgressDialog 是否显示等待框
     * @param context            context
     * @param httpType           POST||GET
     * @param map                参数集合
     */
    public OkHttpRequest(boolean showProgressDialog, Context context, String httpType, Map<String, String> map) {
        this.showProgressDialog = showProgressDialog;
        this.weakReference = new WeakReference<>(context);
        this.httpType = httpType;
        this.map = map;
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 设置请求地址
     *
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 对外暴露个方法,可以创建个监听器传进来
     *
     * @param responseListener 监听器
     */
    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }

    /**
     * 执行网络请求
     */
    public void execute() {
        if (showProgressDialog) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(weakReference.get());
                String message = "请稍候";
                progressDialog.showProgress(message);
                progressDialog.setCancelable(showProgressDialog);
                if (showProgressDialog) {
                    progressDialog.setOnCancelListener(dialog -> cancel());
                }
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
            }
        }
        if (httpType.equals(GET)) {
            getRequest(url);
        } else if (httpType.equals(POST)) {
            postRequest(url, map);
        }
    }

    /**
     * 取消请求
     */
    private void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    /**
     * get请求
     */
    private void getRequest(String url) {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(getLevel())
                .build();
        Request request = new Request.Builder().url(url).get().build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.post(() -> failure());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (null != response.body()) {
                    String data = Objects.requireNonNull(response.body()).string();
                    handler.post(() -> success(data));
                }
            }
        });
    }

    /**
     * post请求
     */
    private void postRequest(String url, Map<String, String> map) {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(getLevel())
                .build();
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : map.keySet()) {
            String keyValues = map.get(key);
            keyValues = keyValues != null ? keyValues : "";
            builder.add(key, keyValues);
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                handler.post(() -> failure());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (null != response.body()) {
                    String data = Objects.requireNonNull(response.body()).string();
                    handler.post(() -> success(data));
                }
            }
        });
    }

    private HttpLoggingInterceptor getLevel() {
        // 日志级别分为4类：NONE、BASIC、HEADERS、BODY；NONE无；BASIC请求/响应行；HEADERS请求/响应行+头；BODY请求/响应行+头+体
        // 日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        // 新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> Log.v("message", "OkHttp====Message:" + message));

        return loggingInterceptor.setLevel(level);
    }

    private void failure() {
        responseListener.failure("网络错误");
        dismissProgressDialog();
    }

    private void success(String data) {
        BaseResponse baseResponse = GsonUtils.fromJson(data, BaseResponse.class);
        if ("200".equals(baseResponse.getCode())) {
            responseListener.success(baseResponse.getData());
        } else {
            responseListener.failure("服务端异常");
        }
        dismissProgressDialog();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        responseListener = null;
        handler.removeCallbacksAndMessages(null);
    }
}
