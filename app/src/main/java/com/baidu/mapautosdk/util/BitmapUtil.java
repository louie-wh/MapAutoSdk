package com.baidu.mapautosdk.util;

import com.baidu.mapautosdk.AppContext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class BitmapUtil {
    private static float mDensity = 0;

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = px2dip(drawable.getIntrinsicWidth(), AppContext.getContext());
        int height = px2dip(drawable.getIntrinsicHeight(), AppContext.getContext());

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, width, height);

        drawable.draw(canvas);

        return bitmap;

    }

    public static int px2dip(int px, Context context) {
        return (int) (0.5F + px / getDensity(context));
    }

    public static float getDensity(Context context) {
        if (mDensity == 0) {
            mDensity = context.getResources().getDisplayMetrics().density;
        }
        return mDensity;
    }
}
