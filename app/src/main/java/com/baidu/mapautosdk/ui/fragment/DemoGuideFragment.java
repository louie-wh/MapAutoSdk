package com.baidu.mapautosdk.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapautosdk.MessageEvent;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.map.AutoMultiScreenNaviMap;
import com.baidu.mapautosdk.api.route.AutoRoutePlanNode;
import com.baidu.mapautosdk.bean.AutoRouteSearchPoi;
import com.baidu.mapautosdk.interfaces.IAutoCommonConfig;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviListener;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviSettingParams;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviViewListener;
import com.baidu.mapautosdk.interfaces.route.IAutoRoutPlanManager;
import com.baidu.mapautosdk.interfaces.route.IAutoRouteGuideManager;
import com.baidu.mapautosdk.model.AutoHighWayInfo;
import com.baidu.mapautosdk.model.AutoNaviLocation;
import com.baidu.mapautosdk.model.AutoTruckCameraInfo;
import com.baidu.mapautosdk.model.AutoTruckLimitInfo;
import com.baidu.mapautosdk.multiscreen.DemoSurfaceService;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.navisdk.adapter.struct.BNGuideConfig;
import com.baidu.navisdk.adapter.struct.BNHighwayInfo;
import com.baidu.navisdk.adapter.struct.BNRoadCondition;
import com.baidu.navisdk.adapter.struct.BNavLineItem;
import com.baidu.navisdk.adapter.struct.BNaviInfo;
import com.baidu.navisdk.adapter.struct.BNaviRoadConditionItem;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import org.greenrobot.eventbus.EventBus;

/**
 * 使用导航默认UI
 */
public class DemoGuideFragment extends BaseFragment {
    private static final String TAG = DemoGuideFragment.class.getName();

    private IAutoRouteGuideManager mRouteGuideManager;

    private String endNode;
    private int naviType;
    private ViewGroup view;
    private AutoMultiScreenNaviMap autoMultiScreenNaviMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        endNode = arguments.getString("endNode");
        naviType = arguments.getInt("naviType");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        createHandler();

        mRouteGuideManager = BDAutoMapFactory.getAutoRouteGuideManager();


        mRouteGuideManager.setNaviViewListener(new IAutoNaviViewListener() {
            @Override
            public void onMainInfoPanCLick() {

            }

            @Override
            public void onNaviTurnClick() {

            }

            @Override
            public void onFullViewButtonClick(boolean b) {
                Log.d(TAG, "onFullViewButtonClick: " + b);
            }

            @Override
            public void onFullViewWindowClick(boolean b) {
                Log.d(TAG, "onFullViewWindowClick: " + b);
            }

            @Override
            public void onNaviBackClick() {

            }

            @Override
            public void onBottomBarClick(Action action) {

            }

            @Override
            public void onNaviSettingClick() {

            }

            @Override
            public void onRefreshBtnClick() {

            }

            @Override
            public void onZoomLevelChange(int i) {
                Log.d(TAG, "onZoomLevelChange: " + i);
            }

            @Override
            public void onMapClicked(double v, double v1) {

            }

            @Override
            public void onMapMoved() {

            }

            @Override
            public void onFloatViewClicked() {

            }

            @Override
            public void onVoiceModeButtonClick(int voiceMode) {
                Log.d(TAG, "onVoiceModeButtonClick: " + voiceMode);
            }

            @Override
            public void onTrafficButtonClick(boolean isOpen) {
                Log.d(TAG, "onTrafficButtonClick: " + isOpen);
            }

            @Override
            public void onSearchPoiData(ArrayList<AutoRouteSearchPoi> list) {
                Log.d(TAG, "onSearchPoiData: " + list.toString());
            }

            @Override
            public void onSearchPoiClick(AutoRouteSearchPoi autoRouteSearchPoi) {
                Log.d(TAG, "onSearchPoiClick: " + autoRouteSearchPoi.toString());
            }

            @Override
            public void onHighWayViewShow(int tage) {
                Log.i("IAutoNaviViewListener", "onHighWayViewShow tage = " + tage);
            }

            @Override
            public void onHighWayInfoUpDate(ArrayList<AutoHighWayInfo> list) {
                Log.d(TAG, "onHeightWayInfoUpDate: " + list.toString());
            }

            @Override
            public void onCameraInfoAndLimitInfo(ArrayList<AutoTruckCameraInfo> cameraInfo, ArrayList<AutoTruckLimitInfo> limitInfo) {
                Log.d(TAG, "onCameraInfoAndLimitInfo: " + cameraInfo.toString());
            }
        });

        if (naviType == 1){
            view = (ViewGroup) mRouteGuideManager.onCreate(getActivity(), null);
        }else if (naviType == 2){
            // 模拟导航
            BNGuideConfig.Builder build = new BNGuideConfig.Builder();
            Bundle bundle = new Bundle();
            bundle.putBoolean("is_realnavi", false);
            build.params(bundle);
            view = (ViewGroup) mRouteGuideManager.onCreate(getActivity(), build.build());
        }

        if (view == null) {

        } else {
            initListener();
            Button button = new Button(getActivity());
            button.setText("测试用按钮");
            button.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            view.addView(button);
            button.setOnClickListener(v -> {
                BDAutoMapFactory.getNaviCommonSettingManager().setRouteSortMode(prefer);
                prefer = prefer << shift[index];
                if (index == shift.length - 1) {
                    index = 0;
                    prefer = IAutoRoutPlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT;
                } else {
                    index++;
                }
                Log.i("DemoGuideFragment",
                        "getRouteSortMode" + BDAutoMapFactory.getNaviCommonSettingManager()
                                .getRouteSortMode());
            });
        }

        return view;
    }
    int index = 0;
    int[] shift = {2, 1, 1, 3, 1, 1, 1, 1};
    private int prefer = IAutoRoutPlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT;
    @Override
    public boolean onBackPressed() {
        mRouteGuideManager.stopNavi();
        NavHostFragment.findNavController(DemoGuideFragment.this).popBackStack();
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mRouteGuideManager.onStart();
        startRouteGuide();
    }

    private void startRouteGuide() {
        Intent startNvai = new Intent(getActivity(), DemoSurfaceService.class);
        startNvai.putExtra("isNaviBegin", true);
        getActivity().startService(startNvai);
    }
    private void stopRouteGuide() {
        Intent startNvai = new Intent(getActivity(), DemoSurfaceService.class);
        startNvai.putExtra("isNaviBegin", false);
        getActivity().startService(startNvai);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mRouteGuideManager.onResume();
    }

    public void onPause() {
        super.onPause();
        mRouteGuideManager.onPause();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onStop() {
        super.onStop();
        mRouteGuideManager.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRouteGuideManager.onDestroy(false);
        mRouteGuideManager = null;
        stopRouteGuide();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRouteGuideManager.onDestroyView();
    }

    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mRouteGuideManager.onConfigurationChanged(newConfig);
    }

    private static final int MSG_RESET_NODE = 3;

    private Handler hd = null;

    private void createHandler() {
        if (hd == null) {
            hd = new Handler(getContext().getMainLooper()) {
                public void handleMessage(Message msg) {
                    if (msg.what == MSG_RESET_NODE) {
                        mRouteGuideManager.resetEndNodeInNavi(new AutoRoutePlanNode.Builder()
                                /*.latitude(40.85087).longitude(116.21142)*/.name(endNode).build());
                    }
                }
            };
        }
    }

    private void initListener() {
        mRouteGuideManager.setNaviListener(new IAutoNaviListener() {
            @Override
            public void onNaviGuideEnd() {
                Log.i("IAutoNaviListener", "onNaviGuideEnd");
                mRouteGuideManager.stopNavi();
                // 回到首页
//                NavHostFragment.findNavController(DemoGuideFragment.this)
//                        .navigate(R.id.action_mainFragment_to_mainFragment);
                // 关闭当前页
                NavHostFragment.findNavController(DemoGuideFragment.this).popBackStack();
                EventBus.getDefault().postSticky(new MessageEvent(true));
            }

            @Override
            public void onRoadNameUpdate(String name) {
                Log.i("IAutoNaviListener", "onRoadNameUpdate name = " + name);
            }

            @Override
            public void onRemainInfoUpdate(int remainDistance, int remainTime) {
                Log.i("IAutoNaviListener",
                        "onRemainInfoUpdate remainDistance = " + remainDistance + ";remainTime ="
                                + " " + remainTime);
            }

            @Override
            public void onViaListRemainInfoUpdate(int viaCount, int[] viaRemainDistance,
                                                  int[] viaRemainTime) {
                Log.i("IAutoNaviListener", "onViaListRemainInfoUpdate");
            }

            @Override
            public void onGuideInfoUpdate(BNaviInfo naviInfo) {
                Log.i("IAutoNaviListener", "onGuideInfoUpdate naviInfo = " + naviInfo.getRoadName() + "," +
                        naviInfo.getDistance() + "," + naviInfo.getTurnIconName());
            }

            @Override
            public void onHighWayInfoUpdate(Action action, BNHighwayInfo info) {
                Log.i("IAutoNaviListener", "onHighWayInfoUpdate info = " + info.toString());
            }

            @Override
            public void onFastExitWayInfoUpdate(Action action, String name, int dist, String id) {
                Log.i("IAutoNaviListener",
                        "onFastExitWayInfoUpdate name = " + name + "; dist = " + dist);
            }

            @Override
            public void onEnlargeMapUpdate(Action action, View enlargeMap, String remainDistance,
                                           int progress, String roadName, Bitmap turnIcon) {
                Log.i("IAutoNaviListener", "onEnlargeMapUpdate roadName = " + roadName);
            }

            @Override
            public void onDayNightChanged(DayNightMode style) {
                Log.i("IAutoNaviListener", "onDayNightChanged style = " + style);
            }

            @Override
            public void onRoadConditionInfoUpdate(double progress, List<BNRoadCondition> items) {
                Log.i("IAutoNaviListener", "onRoadConditionInfoUpdate progress = " + progress);
            }

            @Override
            public void onMainSideBridgeUpdate(int type) {
                Log.i("IAutoNaviListener", "onMainSideBridgeUpdate type = " + type);
            }

            @Override
            public void onLaneInfoUpdate(Action action, List<BNavLineItem> laneItems) {
                Log.i("IAutoNaviListener", "onLaneInfoUpdate action = " + action);
            }

            @Override
            public void onSpeedUpdate(int speed, int speedLimit) {
                Log.i("IAutoNaviListener",
                        "onSpeedUpdate speed = " + speed + "; speedLimit = " + speedLimit);
            }

            @Override
            public void onArriveDestination() {
                Log.i("IAutoNaviListener", "onArriveDestination");
            }

            @Override
            public void onArrivedWayPoint(int index) {
                Log.i("IAutoNaviListener", "onArrivedWayPoint index = " + index);
            }

            @Override
            public void onLocationChange(AutoNaviLocation naviLocation) {
                Log.i("IAutoNaviListener", "onLocationChange");
            }

            @Override
            public void onMapStateChange(MapStateMode mapStateMode) {
                Log.i("IAutoNaviListener", "onMapStateChange mapStateMode = " + mapStateMode);
            }

            @Override
            public void onFullviewStateChange(boolean isFullView) {
                Log.i("IAutoNaviListener", "onFullviewStateChange isFullView = " + isFullView);

            }

            @Override
            public void onStartYawing(String flag) {
                Log.i("IAutoNaviListener", "onStartYawing flag = " + flag);

            }

            @Override
            public void onYawingSuccess() {
                Log.i("IAutoNaviListener", "onYawingSuccess");
            }

            @Override
            public void onYawingArriveViaPoint(int index) {
                Log.i("IAutoNaviListener", "onYawingArriveViaPoint index = " + index);

            }

            @Override
            public void onNotificationShow(String msg) {
                Log.i("IAutoNaviListener", "onNotificationShow msg = " + msg);

            }

            @Override
            public void onHeavyTraffic() {
                Log.i("IAutoNaviListener", "onHeavyTraffic");

            }

            @Override
            public void onSatelliteNumUpdate(int num) {
                Log.i("IAutoNaviListener", "onSatelliteNumUpdate num = " + num);

            }

            @Override
            public void onRoadConditionChange(List<BNaviRoadConditionItem> items,
                                              BNaviRoadConditionItem roadCondition) {
                Log.i("IAutoNaviListener", "onRoadConditionChange");

            }
        });

    }

}
