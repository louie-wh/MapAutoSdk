package com.baidu.mapautosdk;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.tts.AutoTTSInitConfig;
import com.baidu.mapautosdk.interfaces.IAutoCommonConfig;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviManager;
import com.baidu.mapautosdk.oauth.OAuthCallBackListener;
import com.baidu.mapautosdk.oauth.OAuthManager;
import com.baidu.mapautosdk.util.SpUtil;
import com.baidu.oauth.sdk.auth.AuthInfo;
import com.baidu.oauth.sdk.auth.BdOauthSdk;
import com.baidu.oauth.sdk.auth.BdSsoHandler;
import com.baidu.oauth.sdk.callback.BdOauthCallback;
import com.baidu.oauth.sdk.dto.BdOauthDTO;
import com.baidu.oauth.sdk.result.BdOauthResult;
import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;

public class WelcomeActivity extends AppCompatActivity implements OAuthCallBackListener {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1001;
    private static final String TAG = "WelcomeActivity";
    private BdSsoHandler bdSsoHandler;
    String[] permissions = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    List<String> mPermissionList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        requestPermission();
//        initSdk();
        findViewById(R.id.go_to_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
//                oauthLogin();
            }
        });
    }

    private void oauthLogin() {
        if (OAuthManager.getInstance().isOAuthTokenExpired()) {
            String accessToken = SpUtil.getInStace().getString("access_token", null);
            if (TextUtils.isEmpty(accessToken)) {
                return;
            }
            OAuthManager.getInstance().getOpenIdInfo(accessToken);
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            return;
        }
        String refreshToken = OAuthManager.getInstance().getTRefreshToken();
        if (!TextUtils.isEmpty(refreshToken)) {
            OAuthManager.getInstance().refreshAccessToken(refreshToken, WelcomeActivity.this);
            return;
        }

        bdSsoHandler = new BdSsoHandler(WelcomeActivity.this);
        BdOauthDTO bdOauthDTO = new BdOauthDTO();
        bdOauthDTO.oauthType = BdOauthDTO.OAUTH_TYPE_BOTH;
        // 重定向后会携带state参数，建议开发者利用state参数来防止CSRF攻击
        bdOauthDTO.state = UUID.randomUUID().toString();
        bdSsoHandler.authorize(bdOauthDTO, new BdOauthCallback() {
            @Override
            public void onSuccess(BdOauthResult result) {
                String code = result.getCode();
                Log.e(TAG, "code = " + code + " state = " + result.getState());
                if (TextUtils.isEmpty(code)) {
                    return;
                }
                OAuthManager.getInstance().getAccessToken(code, WelcomeActivity.this);
            }

            @Override
            public void onFailure(BdOauthResult result) {
                Log.e(TAG, "result code = " + result.getResultCode()
                        + " msg = " + result.getResultMsg());
            }
        });

    }

    private void initSdk() {
        String redirectUrl = "https://passport.baidu.com";
        String scope = "basic";
        String appKey = "1nk5PzXZeHxFtIrFvokaDgrN";
        AuthInfo authInfo = new AuthInfo(WelcomeActivity.this, appKey, redirectUrl, scope);
        BdOauthSdk.init(authInfo);
        authInfo.isDebug(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void requestPermission() {
        mPermissionList.clear();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        }
        if (!mPermissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissions,
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(!Environment.isExternalStorageManager()){
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        }*/
    }

    @Override
    public void onOAuthCallBack(boolean isSuccess) {
        if (isSuccess) {
            String accessToken = SpUtil.getInStace().getString("access_token", null);
            if (TextUtils.isEmpty(accessToken)) {
                return;
            }
            OAuthManager.getInstance().getOpenIdInfo(accessToken);
//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        Log.e(TAG, "onOAuthCallBack: " + isSuccess);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        String result = data == null ? "" : data.getStringExtra("extra_oauth_result_json");
        Log.e(TAG, "on Activity Result data is ");
        // 处理授权回调
        if (bdSsoHandler != null) {
            bdSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }

//        if (data != null) {
//            // 处理扫码pass sdk二维码通过手百授权登录设备，同时返回调用方accessToken信息
//            handlerQrLogin(requestCode, resultCode, data);
//        }
    }
}