package com.baidu.mapautosdk;

import com.baidu.mapautosdk.api.BDAutoMapInitializer;
import com.baidu.mapautosdk.interfaces.IMapAutoInitCallBack;
import com.baidu.mapautosdk.interfaces.network.IAutoNetWorkProxy;
import com.baidu.mapautosdk.util.ProcessUtil;
import com.baidu.mapautosdk.util.ScreenUtil;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * DemoApplication
 *
 * @author Baidu
 * @version 1.0.0
 * @date 2022/6/6 16:45
 * @description
 */
public class DemoApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        BDAutoMapInitializer.onApplicationAttachBaseContext(this, new BDMapAutoInitConfig(this));
        AppContext.setContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!ProcessUtil.isMainProcess(this)) {
            return;
        }
        BDAutoMapInitializer.setNetWorkProxy(new IAutoNetWorkProxy() {
            @Override
            public boolean isNetworkAvailable() {
                return true;
            }

            @Override
            public boolean isWifiConnected() {
                return true;
            }

            @Override
            public String getCurrentNetMode() {
                return null;
            }
        });
        BDAutoMapInitializer.initBaseEngine(new IMapAutoInitCallBack() {
            @Override
            public void onSuccess() {
                Log.i("initBaseEngine", "onSuccess");
            }

            @Override
            public void onFailed(int status, String message) {
                Log.e("initBaseEngine", status + message);
            }
        });

        /*IAutoAdcuManager iAutoAdcuManager = BDAutoMapFactory.getAutoAdcuManager();
        AutoMapRouteSenderOption.Builder builder = new AutoMapRouteSenderOption.Builder();
        builder.maxLength(5000)
                .sendInterval(1000)
                .separatePacketWaitInterval(500)
                .separatePacketSendInterval(5)
                .setSeparatePacketMaxLength(112)
                .naviStatus(new AutoMapRouteSenderOption.ServiceOption(0x1701, 0x8001, 0, 30491, "127.0.0.1"))
                .naviSdlink(new AutoMapRouteSenderOption.ServiceOption(0x1702, 0x8001, 0, 30491, "127.0.0.1"));
        iAutoAdcuManager.init(builder.build());*/

        ScreenUtil.getInstance().init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

}
