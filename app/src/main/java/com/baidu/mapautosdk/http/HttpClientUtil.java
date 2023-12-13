package com.baidu.mapautosdk.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;


import com.baidu.mapautosdk.AppContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClientUtil {

    private String TAG = HttpClientUtil.class.getSimpleName();
    private HttpCallbackListener mCallback;
    private String mMethod;
    private String mResult;

    public interface HttpCallbackListener {
        // 网络请求成功
        void onSuccess(String response);

        // 网络请求失败
        void onError(HttpStateError error);
    }

    public HttpClientUtil(String method, HttpCallbackListener httpCallbackListener) {
        mMethod = method;
        mCallback = httpCallbackListener;
    }

    public enum HttpStateError {
        PARAMETER_ERROR,
        NETWORK_ERROR,
        INNER_ERROR,
        REQUEST_ERROR,
        SERVER_ERROR,
    }

    protected boolean checkNetwork(Context context) {
        try {
            ConnectivityManager manager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null == manager) {
                return false;
            }

            if (Build.VERSION.SDK_INT >= 29) {
                Network network = manager.getActiveNetwork();
                NetworkCapabilities networkCapabilities = manager.getNetworkCapabilities(network);
                if (networkCapabilities != null && networkCapabilities
                        .hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    return true;
                }
                return false;
            }

            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable()) {
                return false;
            }
        } catch (Exception manager) {
            manager.printStackTrace();
            return false;
        }
        return true;
    }

    protected void request(String requestUrl) {
        Log.e(TAG, "request  url: " + requestUrl );
        if (TextUtils.isEmpty(requestUrl)) {
            mCallback.onError(HttpStateError.PARAMETER_ERROR);
            return;
        }

        if (!checkNetwork(AppContext.getContext())) {
            mCallback.onError(HttpStateError.NETWORK_ERROR);
            return;
        }
        HttpURLConnection connection = inItURLConnection(requestUrl);

        if (connection == null) {
            Log.e(TAG, "url connection failed");
            mCallback.onError(HttpStateError.INNER_ERROR);
            return;
        }

        int responseCode = -1;

        try {
            BufferedReader reader = null;
            connection.connect();
            InputStream inputStream = null;

            try {
                responseCode = connection.getResponseCode();

                if (HttpURLConnection.HTTP_OK == responseCode) {
                    inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    StringBuffer stringBuffer = new StringBuffer();
                    int code;
                    while ((code = reader.read()) != -1) {
                        stringBuffer.append((char) code);
                    }

                    mResult = stringBuffer.toString();
                } else {
                    Log.e(TAG, "responseCode is: " + responseCode);
                    HttpStateError error;

                    if (responseCode >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
                        error = HttpStateError.SERVER_ERROR;
                    } else if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
                        error = HttpStateError.REQUEST_ERROR;
                    } else {
                        error = HttpStateError.INNER_ERROR;
                    }

                    mCallback.onError(error);
                    return;
                }
            } catch (Exception e) {
                Log.e(TAG, "Catch exception. INNER_ERROR", e);
                mCallback.onError(HttpStateError.INNER_ERROR);
                return;
            } finally {
                if (inputStream != null && reader != null) {
                    reader.close();
                    inputStream.close();
                }

                connection.disconnect();
            }
        } catch (Exception e) {
            Log.e(TAG, "Catch connection exception, INNER_ERROR", e);
            mCallback.onError(HttpStateError.INNER_ERROR);
            return;
        }

        mCallback.onSuccess(mResult);
    }


    private HttpURLConnection inItURLConnection(String srtUrl) {
        if (TextUtils.isEmpty(srtUrl)) {
            return null;
        }
        try {
            URL url = new URL(srtUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            /* 3. 设置请求参数（过期时间，输入、输出流、访问方式），以流的形式进行连接 */
            // 设置是否向HttpURLConnection输出
            urlConnection.setDoOutput(false);
            // 设置是否从httpUrlConnection读入
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod(mMethod);
            // 设置连接主机服务器的超时时间：15000毫秒
            urlConnection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            urlConnection.setReadTimeout(60000);
            // 设置是否使用缓存
            urlConnection.setUseCaches(true);
            // 连接
            urlConnection.connect();
            return urlConnection;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
