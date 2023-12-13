package com.baidu.mapautosdk.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * 屏幕相关信息
 *
 * @author dengjian01
 * @author caoyujie
 */
public class ScreenUtil {

    private static final String TAG = "ScreenUtil";
    public static final int DEFAULT_WIDTH = 1080;
    public static final int DEFAULT_HEIGHT = 1920;

    private Display mDefaultDisplay;
    private DisplayMetrics mDM;
    private float mDensity = 0;
    private int mWidthPixels = 0;
    private int mHeightPixels = 0;
    private int mAbsoluteWidth = 0;
    private int mAbsoluteHeight = 0;
    private int mStatusBarHeight = 0;
    private int mDPI = 0;
    private int mDensityDpi;


    private static ScreenUtil mInstance = null;

    private ScreenUtil() {

    }

    public static ScreenUtil getInstance() {
        if (mInstance == null) {
            mInstance = new ScreenUtil();
        }

        return mInstance;
    }

    /**
     * 初始化赋值
     *
     * @param context 建议使用Activity, Context下的屏幕宽高值不一定是当前Activity的
     */
    public void init(Context context) {
        if (context == null) {
            return;
        }
        mDM = context.getResources().getDisplayMetrics();
        int width = Math.min(mDM.widthPixels, mDM.heightPixels);
        int height = Math.max(mDM.widthPixels, mDM.heightPixels);
        if (width == mWidthPixels && height == mHeightPixels) {
            return;
        }
        mWidthPixels = width;
        mHeightPixels = height;
        mDensity = mDM.density;
        mDensityDpi = mDM.densityDpi;
        // 获取手机屏幕绝对尺寸，包括虚拟按键
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager windowMgr = (WindowManager) context.getSystemService(Context
                    .WINDOW_SERVICE);
            windowMgr.getDefaultDisplay().getRealMetrics(dm);
            mAbsoluteWidth = Math.min(dm.widthPixels, dm.heightPixels);
            mAbsoluteHeight = Math.max(dm.widthPixels, dm.heightPixels);
        } else {
            mAbsoluteWidth = mWidthPixels;
            mAbsoluteHeight = mHeightPixels;
        }

        mDPI = mDM.densityDpi;
        if (mDPI == 0) {
            mDPI = 160;
        }
        //        isInitialed = true;
    }

    public DisplayMetrics getDisplayMetrics() {
        return mDM;
    }

    public float getDensity() {
        return mDensity;
    }

    public int getDPI() {
        return mDPI;
    }

    /**
     * 获取屏幕宽，不包括虚拟按键
     *
     * @return
     */
    public int getWidthPixels() {
        return mWidthPixels;
    }

    /**
     * 获取屏幕长，不包括虚拟按键
     *
     * @return
     */
    public int getHeightPixels() {
        return mHeightPixels;
    }

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return
     */
    public int getStatusBarHeight(Activity activity) {
        if (mStatusBarHeight == 0) {
            if (activity == null) {
                return 0;
            }
            resetStatusBarHeight(activity);
        }
        return mStatusBarHeight;
    }

    public void resetStatusBarHeight(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            mStatusBarHeight = activity.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            Log.e(TAG, "an error occurred when getField status_bar_height ", e);
            Rect rect = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            mStatusBarHeight = rect.top;
        }
    }

    public int dip2px(int dip) {
        return (int) (0.5F + mDensity * dip);
    }

    public int dip2px(float dip) {
        return (int) (0.5F + mDensity * dip);
    }

    public int px2dip(int px) {
        return (int) (0.5F + px / mDensity);
    }

    public int px2dip(float px) {
        return (int) (0.5F + px / mDensity);
    }

    public int getDp(Context ctx, int dimenId) {
        if (ctx == null || ctx.getResources() == null) {
            return 0;
        }
        return px2dip(ctx.getResources().getDimensionPixelSize(dimenId));
    }

    public int percentHeight(float percent) {
        return (int) (percent * getHeightPixels());
    }

    public int percentWidth(float percent) {
        return (int) (percent * getWidthPixels());
    }

    /**
     * 5.0以上获取状态栏高度，低于5.0会调用getStatusBarHeight方法
     *
     * @param context
     * @return
     */
    public int getStatusBarHeightFullScreen(Context context) {
        if (context == null) {
            return 0;
        }
        if (Build.VERSION.SDK_INT < 21) {
            return getStatusBarHeightForLowApi(context);
        }
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result > 0 ? result : dip2px(25);
    }

    private int getStatusBarHeightForLowApi(Context context) {
        if (context instanceof Activity) {
            Rect rectangle = new Rect();
            Window window = ((Activity) context).getWindow();
            if (window != null && window.getDecorView() != null) {
                window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
            }
            return rectangle.top;
        } else {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            return (int) Math.ceil(25 * metrics.density);
        }
    }

    public static final int ROTATION_INVALID = -1;    // 无效
    public static final int ROTATION_TOP = 0;        // 方向向上
    public static final int ROTATION_RIGHT = 1;        // 方向向右
    public static final int ROTATION_BOTTOM = 2;    // 方向向下
    public static final int ROTATION_LEFT = 3;        // 方向向左

    /**
     * 获取当前屏幕旋转角度
     * 如果传感器的旋转角度无效，则使用windowmanager中的值
     *
     * @return
     */
    public int getDisplayRotation(Activity activity) {
        if (activity == null) {
            return ROTATION_TOP;
        }
        // 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                rotation = ROTATION_TOP;
                break;
            case Surface.ROTATION_90:
                rotation = ROTATION_LEFT;
                break;
            case Surface.ROTATION_180:
                rotation = ROTATION_BOTTOM;
                break;
            case Surface.ROTATION_270:
                rotation = ROTATION_RIGHT;
                break;
            default:
                rotation = ROTATION_TOP;
                break;
        }
        return rotation;
    }

    /**
     * 获取屏幕短边的绝对尺寸，包括虚拟按键
     *
     * @return
     */
    public int getAbsoluteWidth() {
        return mAbsoluteWidth;
    }

    /**
     * 获取屏幕长边的绝对尺寸，包括虚拟按键
     *
     * @return
     */
    public int getAbsoluteHeight() {
        return mAbsoluteHeight;
    }

    /**
     * 获取屏幕dp值
     *
     * @return
     */
    public int getDensityDpi() {
        return mDensityDpi;
    }

    /**
     * 保持屏幕常亮
     *
     * @param on 是否保持常量
     */
    public void keepScreenOn(boolean on, Activity activity) {
        Activity a = activity;
        if (a == null || a.isFinishing()) {
            return;
        }

        if (on) {
            a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public int getScreenOrientation(Activity activity) {
        if (activity == null) {
            return -1;
        }
        return activity.getResources().getConfiguration().orientation;
    }

    public void setScreenOrientation(Activity activity, int requestedOrientation) {
        if (activity != null) {
            activity.setRequestedOrientation(requestedOrientation);
        }
    }

    /**
     * 获取屏幕宽度，该值不随视图改变而改变
     *
     * @param context
     * @return
     */
    public int getDefaultWidth(Context context) {
        Display display = getDefaultDisplay(context);
        if (display != null) {
            return display.getWidth();
        }
        return DEFAULT_WIDTH;
    }

    /**
     * 获取屏幕宽度，该值不随视图改变而改变
     *
     * @param context
     * @return
     */
    public int getDefaultHeight(Context context) {
        Display display = getDefaultDisplay(context);
        if (display != null) {
            return display.getHeight();
        }
        return DEFAULT_HEIGHT;
    }

    private Display getDefaultDisplay(Context context) {
        if (mDefaultDisplay == null && context != null) {
            mDefaultDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        }
        return mDefaultDisplay;
    }
}
