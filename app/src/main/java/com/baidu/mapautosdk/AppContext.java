package com.baidu.mapautosdk;

import android.content.Context;

/**
 * AppContext
 *
 * @author Baidu
 * @version 1.0.0
 * @date 2022/7/26 09:03
 * @description
 */
public class AppContext {
    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}
