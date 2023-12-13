package com.baidu.mapautosdk.multiscreen;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.aidl.IBMAutoSdkBridge;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.base.CoordType;
import com.baidu.mapautosdk.api.location.AutoLocationChangeListener;
import com.baidu.mapautosdk.api.location.AutoLocationOption;
import com.baidu.mapautosdk.api.map.AutoMapStatus;
import com.baidu.mapautosdk.api.map.AutoMultiScreenNaviMap;
import com.baidu.mapautosdk.interfaces.map.IMultiNaviMapInterface;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviManager;
import com.baidu.mapautosdk.model.AutoLocData;
import com.baidu.platform.comapi.sdk.mapapi.model.LatLng;

public class DemoSurfaceService extends Service {
    private AutoMultiScreenNaviMap multiScreenNaviMapView;
    private int surfaceFormat = 0;
    private int surfaceWidth = 0;
    private int surfaceHeight = 0;
    private boolean isSurfaceChanged = false;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IMultiNaviMapInterface.AutoNavMapMode isNaviMode =
                IMultiNaviMapInterface.AutoNavMapMode.CRUISE;
        if (intent != null) {
            boolean isNaviBegin = intent.getBooleanExtra("isNaviBegin", false);
            isNaviMode = isNaviBegin ?
                    IMultiNaviMapInterface.AutoNavMapMode.NORMAL :
                    IMultiNaviMapInterface.AutoNavMapMode.CRUISE;
        }
        Log.i("DemoSurfaceService", "isNaviBegin:   " + isNaviMode);
        if (multiScreenNaviMapView != null) {
            multiScreenNaviMapView.setNaviMode(isNaviMode);
            if (isNaviMode == IMultiNaviMapInterface.AutoNavMapMode.CRUISE) {
                AutoLocData curLocation = BDAutoMapFactory.getAutoLocationManager().getCurLocation(CoordType.BD09LL);
                AutoMapStatus mapStatus = multiScreenNaviMapView.getMapStatus();
                mapStatus.centerPoint = new LatLng(curLocation.latitude, curLocation.longitude);
                mapStatus.zoomLevel = 18;
                mapStatus.overlooking = 0;
                mapStatus.rotation = 0;
                multiScreenNaviMapView.setMapStatus(mapStatus);
                multiScreenNaviMapView.setTrafficMap(false);
                multiScreenNaviMapView.setCarPos(curLocation.longitude, curLocation.latitude,curLocation.direction);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final IBMAutoSdkBridge.Stub binder = new IBMAutoSdkBridge.Stub() {
        @Override
        public void surfaceCreated(Surface surface) throws RemoteException {
            if (multiScreenNaviMapView == null) {
                multiScreenNaviMapView = new AutoMultiScreenNaviMap();
            }
            isSurfaceChanged = false;
            // 导航是否初始化成功
//            if (isSuccess) {
//                multiScreenNaviMapView.onCreateWithSurface(DemoSurfaceService.this, "widget",
//                        surface);
//                if (multiScreenNaviMapView != null) {
//                    multiScreenNaviMapView.surfaceCreated(surface);
//                    showSurfaceMap();
//                }
//            }else {
                initNavi(surface);
//            }
        }

        @Override
        public void surfaceChanged(String key, int format, int width, int height) throws RemoteException {

            isSurfaceChanged = true;
            surfaceFormat = format;
            surfaceWidth = width;
            surfaceHeight = height;

            if (multiScreenNaviMapView != null) {
                multiScreenNaviMapView.surfaceChanged(format, width, height);
            }
        }

        @Override
        public void surfaceResume() throws RemoteException {
            if (multiScreenNaviMapView != null) {
                multiScreenNaviMapView.onResume();
            }
        }

        @Override
        public void surfacePause() throws RemoteException {
            if (multiScreenNaviMapView != null) {
                multiScreenNaviMapView.onPause();
            }
        }

        @Override
        public void surfaceDestroyed(String key) throws RemoteException {
            if (multiScreenNaviMapView != null) {
                multiScreenNaviMapView.onDestroy();
            }
        }
    };

    private void initNavi(Surface surface) {
        BDAutoMapFactory.getAutoNaviManager().init(this, getSdcardDir(), "BaiduAutoSDKDemo",
                new IAutoNaviManager.IAutoNaviInitListener() {
                    @Override
                    public void initStart() {
                        Log.d("initNaviManager", "initStart: ");
                    }

                    @Override
                    public void initSuccess() {

                        multiScreenNaviMapView.onCreateWithSurface(DemoSurfaceService.this, "widget",
                                surface);
                        if (multiScreenNaviMapView != null) {
                            multiScreenNaviMapView.surfaceCreated(surface);
                        }
                        if (isSurfaceChanged) {
                            if (multiScreenNaviMapView != null) {
                                multiScreenNaviMapView.surfaceChanged(surfaceFormat, surfaceWidth, surfaceHeight);
                                showSurfaceMap();
                            }
                        }

                    }

                    @Override
                    public void initFailed(int errorCode) {
                        Log.d("initNaviManager", "initFailed: " + errorCode);
                    }
                });
    }

    private void showSurfaceMap() {
        multiScreenNaviMapView.setTrafficMap(true);
        AutoMapStatus mapStatus = multiScreenNaviMapView.getMapStatus();
        mapStatus.centerPoint = new LatLng(40.056515, 116.307683);
        mapStatus.zoomLevel = 18;
        mapStatus.rotation = 0;
        multiScreenNaviMapView.setMapStatus(mapStatus);
        multiScreenNaviMapView.setCarPos(40.056515, 116.307683,118.0);

        /*AutoLocData curLocation = BDAutoMapFactory.getAutoLocationManager().getCurLocation(CoordType.BD09LL);
        AutoMapStatus mapStatus = multiScreenNaviMapView.getMapStatus();
        mapStatus.centerPoint = new LatLng(curLocation.latitude, curLocation.longitude);
        mapStatus.zoomLevel = 19;
        mapStatus.rotation = 0;
        multiScreenNaviMapView.setMapStatus(mapStatus);
        multiScreenNaviMapView.setCarPos(curLocation.longitude, curLocation.latitude,curLocation.direction);
        multiScreenNaviMapView.setTrafficMap(true);*/
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

}
