package com.baidu.mapautosdk.oauth;

import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapautosdk.http.AsyncHttpClient;
import com.baidu.mapautosdk.http.HttpClientUtil;
import com.baidu.mapautosdk.util.SpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OAuthManager {

    private String TAG = OAuthManager.class.getSimpleName();
    public OAuthManager() {
    }

    private static class OAuthManagerHolder {
        private static final OAuthManager INSTANCE = new OAuthManager();
    }

    public static OAuthManager getInstance() {
        return OAuthManagerHolder.INSTANCE;
    }


//    public void getCode() {
//        StringBuilder stringBuffer = new StringBuilder();
//        stringBuffer.append(OAuthConstants.OAUTH_DOMAIN).append(OAuthConstants.OAUTH_CODE_PATH);
//        stringBuffer.append("response_type=").append(OAuthConstants.RESPONSE_TYPE);
//        stringBuffer.append("&client_id=").append(OAuthConstants.OAUTH_API_KEY);
//        stringBuffer.append("&redirect_uri=").append(OAuthConstants.OAUTH_REDIRECT_URL);
//        stringBuffer.append("&display=").append("mobile");
//        AsyncHttpClient.getInstance().get(stringBuffer.toString(), new HttpClientUtil.HttpCallbackListener() {
//            @Override
//            public void onSuccess(String response) {
//                Log.e(TAG, "onSuccess: " + response.toString());
//            }
//
//            @Override
//            public void onError(HttpClientUtil.HttpStateError error) {
//
//            }
//        });
//    }

    public void getAccessToken(String code, OAuthCallBackListener oAuthCallBackListener) {
        if (TextUtils.isEmpty(code)) {
            oAuthCallBackListener.onOAuthCallBack(false);
            return;
        }
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(OAuthConstants.OAUTH_DOMAIN).append(OAuthConstants.OAUTH_TOKEN_PATH);
        stringBuffer.append("grant_type=").append(OAuthConstants.GRANT_TYPE);
        stringBuffer.append("&code=").append(code);
        stringBuffer.append("&client_id=").append(OAuthConstants.OAUTH_API_KEY);
        stringBuffer.append("&client_secret=").append(OAuthConstants.OAUTH_CLIENT_SECRET);
        stringBuffer.append("&redirect_uri=").append(OAuthConstants.OAUTH_REDIRECT_URL);
        AsyncHttpClient.getInstance().get(stringBuffer.toString(), new HttpClientUtil.HttpCallbackListener() {
            @Override
            public void onSuccess(String response) {
                parseAuthTokenResult(response, oAuthCallBackListener);
                Log.e(TAG, "onSuccess: " + response.toString());
            }

            @Override
            public void onError(HttpClientUtil.HttpStateError error) {
                parseAuthTokenResult(null, oAuthCallBackListener);
            }
        });
    }

    /**
     * 解析Token
     * @param response json字符串
     */
    private void parseAuthTokenResult(String response, OAuthCallBackListener oAuthCallBackListener) {
        if (TextUtils.isEmpty(response)) {
            Log.e(TAG, "parseAuthTokenResult error or null");
            oAuthCallBackListener.onOAuthCallBack(false);
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("access_token")) {
                // 获取到的网页授权接口调用凭证
                SpUtil.getInStace().save("access_token", jsonObject.optString("access_token"));
            }
            if (jsonObject.has("expires_in")) {
                // Access Token的有效期，以秒为单位
                int expiresIn = jsonObject.optInt("expires_in");
                SpUtil.getInStace().save("expires_in", expiresIn);
                if (expiresIn > 0) {
                    long tokenSuccessTimeMillis = System.currentTimeMillis() / 1000;
                    SpUtil.getInStace().save("tokenSuccessTimeMillis", tokenSuccessTimeMillis);
                }
            }
            if (jsonObject.has("refresh_token")) {
                // 用于刷新Access Token的Refresh Token，所有应用都会返回该参数**（10年的有效期**）
                SpUtil.getInStace().save("refresh_token", jsonObject.optString("refresh_token"));
            }
            if (jsonObject.has("scope")) {
                SpUtil.getInStace().save("scope", jsonObject.optString("scope"));
            }
            if (jsonObject.has("session_secret")) {
                SpUtil.getInStace().save("session_secret", jsonObject.optString("session_secret"));
            }
            if (jsonObject.has("session_key")) {
                SpUtil.getInStace().save("session_key", jsonObject.optString("session_key"));
            }
            oAuthCallBackListener.onOAuthCallBack(true);
        } catch (JSONException e) {
            oAuthCallBackListener.onOAuthCallBack(false);
            e.printStackTrace();
        }
    }

    /**
     * 判断token是否超过有效时间
     * @return true 是没超有效期
     */
    public boolean isOAuthTokenExpired() {
        Long tokenSuccessTimeMillis = SpUtil.getInStace().getLong("tokenSuccessTimeMillis", 0);
        int expiresIn = SpUtil.getInStace().getInt("expires_in", 0);
        if (tokenSuccessTimeMillis == 0 || expiresIn == 0) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        int secondTimes= (int) (currentTimeMillis - tokenSuccessTimeMillis);
        return secondTimes < expiresIn;
    }

    public String getTRefreshToken() {
       return SpUtil.getInStace().getString("refresh_token", null);
    }

    public void refreshAccessToken(String refreshToken, OAuthCallBackListener oAuthCallBackListener) {
        if (TextUtils.isEmpty(refreshToken)) {
            oAuthCallBackListener.onOAuthCallBack(false);
            return;
        }
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(OAuthConstants.OAUTH_DOMAIN).append(OAuthConstants.OAUTH_TOKEN_PATH);
        stringBuffer.append("grant_type=").append(OAuthConstants.REFRESH_GRANT_TYPE);
        stringBuffer.append("&refresh_token=").append(refreshToken);
        stringBuffer.append("&client_id=").append(OAuthConstants.OAUTH_API_KEY);
        stringBuffer.append("&client_secret=").append(OAuthConstants.OAUTH_CLIENT_SECRET);
        AsyncHttpClient.getInstance().get(stringBuffer.toString(), new HttpClientUtil.HttpCallbackListener() {
            @Override
            public void onSuccess(String response) {
                parseAuthTokenResult(response, oAuthCallBackListener);
                Log.e(TAG, "onSuccess: " + response.toString());
            }

            @Override
            public void onError(HttpClientUtil.HttpStateError error) {
                parseAuthTokenResult(null, oAuthCallBackListener);
            }
        });
    }

    public  void getOpenIdInfo(String accessToken) {
        if (TextUtils.isEmpty(accessToken)) {
            return;
        }
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(OAuthConstants.OAUTH_OPENID);
        stringBuffer.append("access_token=").append(accessToken);

        AsyncHttpClient.getInstance().get(stringBuffer.toString(), new HttpClientUtil.HttpCallbackListener() {
            @Override
            public void onSuccess(String response) {
                Log.e(TAG, "onSuccess: " + response.toString());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    if (jsonObject.has("openid")) {
                        String openid = jsonObject.optString("openid");
                        // 获取到的网页授权接口调用凭证
                        SpUtil.getInStace().save("openid", openid);
                        Log.e(TAG, "onSuccess openid: " + openid);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(HttpClientUtil.HttpStateError error) {
            }
        });
    }
}
