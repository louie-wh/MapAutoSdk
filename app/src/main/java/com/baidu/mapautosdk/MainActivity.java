package com.baidu.mapautosdk;

import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.tts.AutoTTSInitConfig;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviManager;
import com.baidu.mapautosdk.ui.BackHandleInterface;
import com.baidu.mapautosdk.ui.BaseFragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

public class MainActivity extends FragmentActivity implements BackHandleInterface {

    public static final String APP_FOLDER_NAME = "BaiduAutoSDKDemo";

    private View mapView;
    private BaseFragment baseFragment;

    private Fragment hostFragment;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_main);
        initNavi();

        hostFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        fm = hostFragment.getChildFragmentManager();

        addMapView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BDAutoMapFactory.getAutoLocationManager().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BDAutoMapFactory.getAutoLocationManager().onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BDAutoMapFactory.getAutoMapManager().onDestroy();
    }

    @Override
    public void onSelectedFragment(BaseFragment baseFragment) {
        this.baseFragment = baseFragment;
    }

    @Override
    public void onBackPressed() {
        if (baseFragment != null && !baseFragment.onBackPressed()) {
            if (fm.getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                Navigation.findNavController(this, R.id.fragment).navigateUp();
            }
        }else {
            Navigation.findNavController(this, R.id.fragment).navigate(R.id.mainFragment);
        }
    }

    private void addMapView() {
        mapView = BDAutoMapFactory.getAutoMapManager().getMapView();
        if (mapView != null && mapView.getParent() != null) {
            ViewGroup parent = (ViewGroup) mapView.getParent();
            parent.removeView(mapView);
        }

        // 将底图view添加到ViewGroup中
        ViewGroup mapContainer = findViewById(R.id.map_container);
        mapContainer.addView(mapView);
    }

    private void initNavi() {
        BDAutoMapFactory.getAutoNaviManager().init(this, getSdcardDir(), APP_FOLDER_NAME,
                new IAutoNaviManager.IAutoNaviInitListener() {
                    @Override
                    public void initStart() {
                        Toast.makeText(MainActivity.this,
                                "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void initSuccess() {
                        BDAutoMapFactory.getCruiserManager().start(MainActivity.this);

                        Toast.makeText(MainActivity.this,
                                "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();

                        BDAutoMapFactory.getAutoTTSManager().setTTsStreamType(3);
                        AutoTTSInitConfig config = new AutoTTSInitConfig.Builder()
                                .context(getApplicationContext())
                                .appId("11213224")
                                .appKey("gT2XSUgoMFysCzwLCUtrIItTUdclThsf")
                                .secretKey("MEokc3O8y95Lh9fOLX7lrxY1jD9OkWFf")
                                .authSN("8092f102-684cde5d-01-0050-006d-0091-01")
                                .build();

                        BDAutoMapFactory.getAutoTTSManager().initTTS(MainActivity.this, config);
                        BDAutoMapFactory.getAutoTTSManager().setOnTTSStateChangedListener(null);

                    }

                    @Override
                    public void initFailed(int errorCode) {
                        Toast.makeText(MainActivity.this,
                                "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

}