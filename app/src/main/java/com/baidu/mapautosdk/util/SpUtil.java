package com.baidu.mapautosdk.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.baidu.mapautosdk.AppContext;

public class SpUtil {
    private static final String SP= "oAuthInfo";

    private SpUtil() {
    }

    private static SpUtil inStace = new SpUtil();
    private static SharedPreferences mSp = null;

    public static SpUtil getInStace() {
        if (mSp == null) {
            Context context = AppContext.getContext();
            mSp = context.getSharedPreferences(SP, Context.MODE_PRIVATE);
        }
        return inStace;
    }

    /**
     * 保存数据
     *
     * @param key   键
     * @param value 值
     */
    public void save(String key, Object value) {
        if (value instanceof String) {
            mSp.edit().putString(key, (String) value).commit();
        } else if (value instanceof Boolean) {
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Integer) {
            mSp.edit().putInt(key, (Integer) value).commit();
        } else if (value instanceof Long) {
            mSp.edit().putLong(key, (Long) value).commit();
        }
    }

    // 读取String类型数据
    public String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    // 读取boolean类型数据
    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    // 读取int类型数据
    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

    public Long getLong(String key, int defValue) {
        return mSp.getLong(key, defValue);
    }
}
