package com.baidu.mapautosdk.aidl;

import android.view.Surface;

interface IBMAutoSdkBridge {
    void surfaceCreated(in Surface surface);
    void surfaceChanged(in String key, in int format, in int width, in int height);
    void surfaceResume();
    void surfacePause();
    void surfaceDestroyed(in String key);
}