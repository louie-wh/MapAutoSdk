package com.baidu.mapautosdk;

import com.baidu.mapautosdk.interfaces.IMapAutoInitConfig;

import android.content.Context;

/**
 * BDMapAutoInitConfig
 *
 * @author Baidu
 * @version 1.0.0
 * @date 2022/6/8 12:54
 * @description
 */
public class BDMapAutoInitConfig implements IMapAutoInitConfig {

    private Context mContext;

    public BDMapAutoInitConfig(Context context) {
        mContext = context;
    }

    @Override
    public String getApiKey() {
        return "TUrqH2bKy1zWKoGSaCBu5AM4QGo09pNK";
    }

    @Override
    public String getDeviceID() {
        return "68AC639C18DC7F8A4603D40297AE4F4D|0";
    }

    @Override
    public String getDataFolderName() {
        return null;
    }

    @Override
    public String getDataPath() {
        if (mContext != null) {
            return mContext.getExternalFilesDir(null).getAbsolutePath();
//            return "sdcard/naviSdk";
        }
        return null;
    }

    @Override
    public String getChannelId() {
        return "auto_test2";
    }

    @Override
    public String getVehicleType() {
        return "default";
    }
}
