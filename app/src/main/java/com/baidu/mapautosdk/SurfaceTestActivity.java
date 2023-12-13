package com.baidu.mapautosdk;

import com.baidu.mapautosdk.api.map.AutoMultiScreenNaviMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * SurfaceTestActivity
 *
 * @author Baidu
 * @version 1.0.0
 * @date 2022/9/6 08:52
 * @description
 */
public class SurfaceTestActivity extends Activity implements SurfaceHolder.Callback {

    AutoMultiScreenNaviMap multiScreenNaviMapView;
    SurfaceView surfaceView;
    SurfaceHolder mSurfaceHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);
        surfaceView = findViewById(R.id.surface_view);
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (multiScreenNaviMapView != null) {
            multiScreenNaviMapView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (multiScreenNaviMapView != null) {
            multiScreenNaviMapView.onDestroy();
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        multiScreenNaviMapView = new AutoMultiScreenNaviMap();
        multiScreenNaviMapView.onCreateWithSurface(SurfaceTestActivity.this, "widget",
                surfaceView.getHolder().getSurface());
        if (multiScreenNaviMapView != null) {
            multiScreenNaviMapView.surfaceCreated(mSurfaceHolder.getSurface());
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (multiScreenNaviMapView != null) {
            multiScreenNaviMapView.surfaceChanged(i, i1, i2);
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        if (multiScreenNaviMapView != null) {
            multiScreenNaviMapView.surfaceDestroyed();
        }
    }
}
