package com.baidu.mapautosdk.ui.fragment;

import com.baidu.android.common.BuildConfig;
import com.baidu.mapautosdk.MessageEvent;
import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.map.AutoMapStatus;
import com.baidu.mapautosdk.api.navi.AutoNaviCommonParams;
import com.baidu.mapautosdk.interfaces.IAutoCommonConfig;
import com.baidu.mapautosdk.interfaces.map.IAutoMapListener;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviListener;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviSettingManager;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviSettingParams;
import com.baidu.mapautosdk.interfaces.route.IAutoRoutPlanManager;
import com.baidu.mapautosdk.interfaces.route.IAutoRouteGuideManager;
import com.baidu.mapautosdk.interfaces.theme.IAutoDayNightManager;
import com.baidu.mapautosdk.model.AutoMapPoiObj;
import com.baidu.mapautosdk.multiscreen.DemoSurfaceService;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.util.StringFormatUtils;
import com.baidu.navisdk.adapter.struct.BNGuideConfig;
import com.baidu.navisdk.adapter.struct.BNHighwayInfo;
import com.baidu.navisdk.adapter.struct.BNRoadCondition;
import com.baidu.navisdk.adapter.struct.BNavLineItem;
import com.baidu.navisdk.adapter.struct.BNaviInfo;
import com.baidu.navisdk.adapter.struct.BNaviLocation;
import com.baidu.navisdk.adapter.struct.BNaviRoadConditionItem;
import com.baidu.platform.comapi.sdk.mapapi.model.LatLng;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 自定义UI
 * */
public class DemoCustomUiFragment extends BaseFragment {
    private static final String TAG = "demoTag";

    private LinearLayout mGuideInfoLayout;
    private TextView mSpeedView;
    private ViewGroup mEnlargeContainer;
    private ViewGroup mEnlargeMap;
    private TextView mEnlargeInfo;
    private ViewGroup mSettingLayout;
    private ViewGroup mLaneLayout;
    private TextView mCarProgress;
    private TextView mRoadConditions;
    private Button mMainSideRoadBtn;
    private Button mOnUnderBridgeBtn;
    private Button mRoadEnlargeView;
    private Button mRecoverNavi;

    private IAutoRouteGuideManager mRouteGuideManager;
    private IAutoNaviSettingManager.IProfessionalNaviSetting mSetting;
    private int mNavingState = IAutoNaviSettingParams.NaviPerspectiveMode.CAR_3D;
    private int mRoutePlanState = IAutoNaviSettingParams.NeRoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT;
    private int mPreState = IAutoNaviSettingParams.PreViewMode.MapMini;
    private int mVoiceState = IAutoNaviSettingParams.VoiceMode.PLAY;
    private int mVoiceDiyState = IAutoNaviSettingParams.VoiceDiyMode.STANDARD;
    private TextView tvRecoverNavi;
    private LinearLayout llExit;
    private LinearLayout llSetting;
    private ImageView imgRouteCondition;
    private TextView tvAllDistance;
    private TextView tvTakesTime;
    private TextView tvRoadDistance;
    private TextView tvRoadName;
    private TextView tvRoadNameTwo;
    private ImageView imgDirectionIcon;
    private ImageView imgDirectionIconSmall;
    private TextView tvArrivalTime;
    private TextView tvFull;
    private LinearLayout llViaductMsg;
    private LinearLayout llRoadsMsg;
    private ImageView imgViaductMsg;
    private ImageView imgRoadsMsg;
    private TextView tvViaductMsg;
    private TextView tvRoadsMsg;
    private LinearLayout llRefreshAndFull;
    private boolean realRoadConditionOpen;
    private IAutoNaviSettingManager.ICommonSetting mCommonSetting;
    private int mVehicleType;
    private int naviType;
    private ViewGroup mEnlargeView;

//    private AutoRoadConditionView roadConditionView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        naviType = arguments.getInt("naviType");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mRouteGuideManager = BDAutoMapFactory.getAutoRouteGuideManager();
        if (naviType == 1){
            mRouteGuideManager.onCreate(getActivity(), null);
        }else if (naviType == 2){
            // 模拟导航
            BNGuideConfig.Builder build = new BNGuideConfig.Builder();
            Bundle bundle = new Bundle();
            bundle.putBoolean("is_realnavi", false);
            build.params(bundle);
            mRouteGuideManager.onCreate(getActivity(), build.build());
        }

        return inflater.inflate(R.layout.normal_demo_activity_custom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
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

    private void initView(View view) {
        // 获取车辆类型
        mVehicleType = BDAutoMapFactory.getNaviCommonSettingManager().getVehicleType();
        mSetting = BDAutoMapFactory.getProfessionalNaviSettingManager();
        mCommonSetting = BDAutoMapFactory.getNaviCommonSettingManager();


//        mRouteGuideManager.onCreate(getActivity(), null);

        mEnlargeView = view.findViewById(R.id.enlarge_view);

        mEnlargeContainer = view.findViewById(R.id.enlarge_container);
        mEnlargeMap = mEnlargeContainer.findViewById(R.id.enlarge_map);
        mEnlargeInfo = mEnlargeContainer.findViewById(R.id.enlarge_info);
        mGuideInfoLayout = view.findViewById(R.id.guide_info_layout);
        mSpeedView = view.findViewById(R.id.speed_view);
        mSettingLayout = view.findViewById(R.id.setting_layout);
        mLaneLayout = view.findViewById(R.id.lane_layout);
        mCarProgress = view.findViewById(R.id.car_progress);
        mRoadConditions = view.findViewById(R.id.road_conditions);
        mMainSideRoadBtn = view.findViewById(R.id.main_side_road_btn);
        mOnUnderBridgeBtn = view.findViewById(R.id.on_under_bridge);

        view.findViewById(R.id.btn_zoom_in).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_zoom_out).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_full_view).setOnClickListener(clickListener);
        mRecoverNavi = view.findViewById(R.id.btn_recover_navi);
        mRecoverNavi.setOnClickListener(clickListener);
        mRecoverNavi.setVisibility(View.INVISIBLE);
        view.findViewById(R.id.btn_refresh_route).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_route_condition).setOnClickListener(clickListener);
        view.findViewById(R.id.setting_btn).setOnClickListener(clickListener);
        view.findViewById(R.id.on_under_bridge).setOnClickListener(clickListener);
        view.findViewById(R.id.main_side_road_btn).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_voice_quite).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_new_user).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_god_user).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_smart_scale_close).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_smart_scale_open).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_night_mode_auto).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_night_mode_night).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_night_mode_day).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_2d_3d).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_change_icon).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_camera_status).setOnClickListener(clickListener);
        mRoadEnlargeView = view.findViewById(R.id.btn_road_enlarge_view);
        mRoadEnlargeView.setOnClickListener(clickListener);
        boolean show =
                BDAutoMapFactory.getProfessionalNaviSettingManager().isShowRoadEnlargeView(
                        IAutoCommonConfig.Vehicle.CAR);
        if (show) {
            mRoadEnlargeView.setText("路口放大图(已开启)");
        } else {
            mRoadEnlargeView.setText("路口放大图(已关闭)");
        }

        view.findViewById(R.id.btn_car_end_line).setOnClickListener(clickListener);
//        roadConditionView = view.findViewById(R.id.bnav_rg_cp_roadconditionbar);

        llRefreshAndFull = view.findViewById(R.id.ll_refresh_and_full);
        // 刷新
        view.findViewById(R.id.ll_refresh_route).setOnClickListener(clickListener);
        // 全览
        view.findViewById(R.id.ll_full_view).setOnClickListener(clickListener);
        tvFull = view.findViewById(R.id.tv_full);
        // 退出
        llExit = view.findViewById(R.id.ll_exit);
        llExit.setOnClickListener(clickListener);
        llExit.setVisibility(View.INVISIBLE);
        // 设置
        llSetting = view.findViewById(R.id.ll_setting);
        llSetting.setOnClickListener(clickListener);
        llSetting.setVisibility(View.INVISIBLE);
        // 恢复导航
        tvRecoverNavi = view.findViewById(R.id.tv_recover_navi);
        tvRecoverNavi.setOnClickListener(clickListener);
        tvRecoverNavi.setVisibility(View.INVISIBLE);
        // 路况
        view.findViewById(R.id.ll_route_condition).setOnClickListener(clickListener);
        imgRouteCondition = view.findViewById(R.id.img_route_condition);
        realRoadConditionOpen = BDAutoMapFactory.getProfessionalNaviSettingManager()
                .isRealRoadConditionOpen(mVehicleType);
        if (realRoadConditionOpen){
            imgRouteCondition.setImageResource(R.mipmap.ic_traffic_s_white);
        }else {
            imgRouteCondition.setImageResource(R.mipmap.ic_traffic_n_white);
        }
        // 转向图标
        imgDirectionIcon = view.findViewById(R.id.img_direction_icon);
        // 转向图标
        imgDirectionIconSmall = view.findViewById(R.id.img_direction_icon_small);
        // 到下条路的距离
        tvRoadDistance = view.findViewById(R.id.tv_road_distance);
        // 下条路路名
        tvRoadName = view.findViewById(R.id.tv_road);
        // 下条路路名
        tvRoadNameTwo = view.findViewById(R.id.tv_road_two);
        // 总距离
        tvAllDistance = view.findViewById(R.id.tv_all_distance);
        // 需要时间
        tvTakesTime = view.findViewById(R.id.tv_takes_time);
        // 到达时间
        tvArrivalTime = view.findViewById(R.id.tv_arrival_time);
        // 高架桥信息
        llViaductMsg = view.findViewById(R.id.ll_viaduct_msg);
        imgViaductMsg = view.findViewById(R.id.img_viaduct_msg);
        tvViaductMsg = view.findViewById(R.id.tv_viaduct_msg);
        // 主辅路信息
        llRoadsMsg = view.findViewById(R.id.ll_roads_msg);
        imgRoadsMsg = view.findViewById(R.id.img_roads_msg);
        tvRoadsMsg = view.findViewById(R.id.tv_roads_msg);


        view.findViewById(R.id.button_one).setOnClickListener(clickListener);
        view.findViewById(R.id.button_two).setOnClickListener(clickListener);

        mRouteGuideManager.setNaviListener(mAutoNaviListener);

        BDAutoMapFactory.getAutoMapManager().setAutoMapListener(new IAutoMapListener() {
            @Override
            public void onMapTouch(MotionEvent motionEvent) {
                llExit.setVisibility(View.VISIBLE);
                llSetting.setVisibility(View.VISIBLE);
                tvRecoverNavi.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMapClick(LatLng latLng) {

            }

            @Override
            public void onMapPoiClick(List<AutoMapPoiObj> list) {

            }

            @Override
            public void onMapLongClick(LatLng latLng) {

            }

            @Override
            public void onMapLoadFinish() {

            }

            @Override
            public void onMapStatusChangeFinish(AutoMapStatus autoMapStatus) {

            }
        });
    }

    @Override
    public boolean onBackPressed() {
        mRouteGuideManager.onBackPressed(false, true);
        NavHostFragment.findNavController(DemoCustomUiFragment.this).popBackStack();
        EventBus.getDefault().postSticky(new MessageEvent(true));
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        mRouteGuideManager.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mRouteGuideManager.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mRouteGuideManager.onPause();
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
        mRouteGuideManager.setNaviListener(null);
        mRouteGuideManager.setNaviViewListener(null);
        mRouteGuideManager = null;
        stopRouteGuide();
    }

    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mRouteGuideManager.onConfigurationChanged(newConfig);
    }

    public void onZoomInClick(View view) {
        BDAutoMapFactory.getAutoMapManager().zoomIn();
    }

    public void onZoomOutClick(View view) {
        BDAutoMapFactory.getAutoMapManager().zoomOut();
    }

    public void onChangeIconClick(View view) {
//        Dialog dialog = new DIYImageDialog(getActivity());
//        dialog.show();
    }

    public void onCameraStatusClick(View view) {
//        Dialog dialog = new CameraTypeDialog(getActivity());
//        dialog.show();
    }

    public void onRecoverNaviClick(View view) {
        if (mFullView == false){
            BDAutoMapFactory.getAutoRouteGuideManager().fullView(mFullView);
            mFullView = true;
            tvFull.setText("全览");
        }
        continueNavi();
        llExit.setVisibility(View.INVISIBLE);
        llSetting.setVisibility(View.INVISIBLE);
        mRecoverNavi.setVisibility(View.INVISIBLE);
        tvRecoverNavi.setVisibility(View.INVISIBLE);
    }

    boolean mFullView = true;
    public void onFullViewClick(View view) {
        if (mFullView == true){
            BDAutoMapFactory.getAutoRouteGuideManager().fullView(mFullView);
            mFullView = false;
            tvFull.setText("退出全览");
        }else {
            BDAutoMapFactory.getAutoRouteGuideManager().fullView(mFullView);
            mFullView = true;
            tvFull.setText("全览");
        }
    }

    public void on2D3DClick(View view) {
        if (mNavingState == IAutoNaviSettingParams.NaviPerspectiveMode.NORTH_2D) {
            mSetting.setGuideViewMode(IAutoNaviSettingParams.NaviPerspectiveMode.CAR_3D,
                    IAutoCommonConfig.Vehicle.CAR);
            mNavingState = IAutoNaviSettingParams.NaviPerspectiveMode.CAR_3D;
        } else if (mNavingState == IAutoNaviSettingParams.NaviPerspectiveMode.CAR_3D) {
            mSetting.setGuideViewMode(IAutoNaviSettingParams.NaviPerspectiveMode.NORTH_2D,
                    IAutoCommonConfig.Vehicle.CAR);
            mNavingState = IAutoNaviSettingParams.NaviPerspectiveMode.NORTH_2D;
        }
    }

    public void onDayNightDayClick(View view) {
        mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_DAY,
                IAutoCommonConfig.Vehicle.CAR);
    }

    public void onDayNightNightClick(View view) {
        mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_NIGHT,
                IAutoCommonConfig.Vehicle.CAR);
    }

    public void onDayNightAutoClick(View view) {
        mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_AUTO,
                IAutoCommonConfig.Vehicle.CAR);
    }

    public void onSmartScaleOpenClick(View view) {
        mSetting.setAutoScale(true, IAutoCommonConfig.Vehicle.CAR);
    }

    public void onSmartScaleCloseClick(View view) {
        mSetting.setAutoScale(false, IAutoCommonConfig.Vehicle.CAR);
    }

    public void onVoiceQuiteClick(View view) {
        mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.QUITE, IAutoCommonConfig.Vehicle.CAR);
    }

    public void onGodUserClick(View view) {
        mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.QUITE,
                IAutoCommonConfig.Vehicle.CAR);
    }

    public void onNewUserClick(View view) {
        mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.JustPlayWarning,
                IAutoCommonConfig.Vehicle.CAR);
    }

    public void onRefreshRouteClick(View view) {
        mRouteGuideManager.refreshRoute(new IAutoRouteGuideManager.RefreshRouteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getActivity(), "已为您切换路线", Toast.LENGTH_SHORT).show();
                continueNavi();
            }

            @Override
            public void onFail() {
                Toast.makeText(getActivity(), "切换失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoNewRoute() {
                Toast.makeText(getActivity(), "当前已是最优路线", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onRoadConditionClick(View view) {
        if (realRoadConditionOpen == true){
            imgRouteCondition.setImageResource(R.mipmap.ic_traffic_n_white);
            mSetting.setRealRoadCondition(false);
            realRoadConditionOpen = false;
        }else {
            imgRouteCondition.setImageResource(R.mipmap.ic_traffic_s_white);
            mSetting.setRealRoadCondition(true);
            realRoadConditionOpen = true;
        }
    }

    public void onSettingClick(View view) {
        /*if (mSettingLayout.getVisibility() == View.GONE) {
            mSettingLayout.setVisibility(View.VISIBLE);
        } else {
            mSettingLayout.setVisibility(View.GONE);
        }*/

        mGuideInfoLayout.setVisibility(View.GONE);
        llRefreshAndFull.setVisibility(View.GONE);
        llExit.setVisibility(View.GONE);
        llSetting.setVisibility(View.GONE);
        tvRecoverNavi.setVisibility(View.GONE);
        showSetting();
    }

    private void showSetting() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.navi_setting, null, false);
        PopupWindow morePopupWindow = new PopupWindow(inflate,ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT,true);
        morePopupWindow.setOutsideTouchable(true);
        morePopupWindow.setFocusable(true);
        morePopupWindow.setTouchable(true);
        darkenBackground(1f);
        morePopupWindow.showAtLocation(mGuideInfoLayout, Gravity.LEFT, 0, 0);
        morePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mGuideInfoLayout.setVisibility(View.VISIBLE);
                llRefreshAndFull.setVisibility(View.VISIBLE);
                llExit.setVisibility(View.VISIBLE);
                llSetting.setVisibility(View.VISIBLE);
                tvRecoverNavi.setVisibility(View.VISIBLE);
                darkenBackground(1f);
            }
        });
        // 导航路线
        RadioGroup rg = inflate.findViewById(R.id.navi_setting_rg);
        // 智能推荐
        RadioButton rbRecommend = inflate.findViewById(R.id.navi_setting_rb_recommend);
        // 躲避拥堵
        RadioButton rbJam = inflate.findViewById(R.id.navi_setting_rb_jam);
        // 时间优先
        RadioButton rbTime = inflate.findViewById(R.id.navi_setting_rb_time);
        // 少收费
        RadioButton rbLess = inflate.findViewById(R.id.navi_setting_rb_less);
        // 不走高速
        RadioButton rbNoHighway = inflate.findViewById(R.id.navi_setting_rb_no_highway);
        // 高速优先
        RadioButton rbHighway = inflate.findViewById(R.id.navi_setting_rb_highway);
        int routeSortMode = mCommonSetting.getRouteSortMode();
        if (routeSortMode == IAutoNaviSettingParams.NeRoutePlanPreference.
                ROUTE_PLAN_PREFERENCE_DEFAULT){
            mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.
                RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT);
            rbRecommend.setChecked(true);
            rbJam.setChecked(false);
            rbTime.setChecked(false);
            rbLess.setChecked(false);
            rbNoHighway.setChecked(false);
            rbHighway.setChecked(false);
        }else if (routeSortMode == IAutoNaviSettingParams.NeRoutePlanPreference.
                ROUTE_PLAN_PREFERENCE_AVOID_TRAFFIC_JAM){
            mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.
                    RoutePlanPreference.ROUTE_PLAN_PREFERENCE_AVOID_TRAFFIC_JAM);
            rbRecommend.setChecked(false);
            rbJam.setChecked(true);
            rbTime.setChecked(false);
            rbLess.setChecked(false);
            rbNoHighway.setChecked(false);
            rbHighway.setChecked(false);
        }else if (routeSortMode == IAutoNaviSettingParams.NeRoutePlanPreference.
                ROUTE_PLAN_PREFERENCE_TIME_FIRST){
            mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.
                    RoutePlanPreference.ROUTE_PLAN_PREFERENCE_TIME_FIRST);
            rbRecommend.setChecked(false);
            rbJam.setChecked(false);
            rbTime.setChecked(true);
            rbLess.setChecked(false);
            rbNoHighway.setChecked(false);
            rbHighway.setChecked(false);
        }else if (routeSortMode == IAutoNaviSettingParams.NeRoutePlanPreference.
                ROUTE_PLAN_PREFERENCE_NOTOLL){
            mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.
                    RoutePlanPreference.ROUTE_PLAN_PREFERENCE_NOTOLL);
            rbRecommend.setChecked(false);
            rbJam.setChecked(false);
            rbTime.setChecked(false);
            rbLess.setChecked(true);
            rbNoHighway.setChecked(false);
            rbHighway.setChecked(false);
        }else if (routeSortMode == IAutoNaviSettingParams.NeRoutePlanPreference.
                ROUTE_PLAN_PREFERENCE_NOHIGHWAY){
            mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.
                    RoutePlanPreference.ROUTE_PLAN_PREFERENCE_NOHIGHWAY);
            rbRecommend.setChecked(false);
            rbJam.setChecked(false);
            rbTime.setChecked(false);
            rbLess.setChecked(false);
            rbNoHighway.setChecked(true);
            rbHighway.setChecked(false);
        }else if (routeSortMode == IAutoNaviSettingParams.NeRoutePlanPreference.
                ROUTE_PLAN_PREFERENCE_ROAD_FIRST){
            mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.
                    RoutePlanPreference.ROUTE_PLAN_PREFERENCE_ROAD_FIRST);
            rbRecommend.setChecked(false);
            rbJam.setChecked(false);
            rbTime.setChecked(false);
            rbLess.setChecked(false);
            rbNoHighway.setChecked(false);
            rbHighway.setChecked(true);
        }
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.navi_setting_rb_recommend:
                        mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.
                                RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT);
                        break;
                    case R.id.navi_setting_rb_jam:
                        mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.RoutePlanPreference.
                                ROUTE_PLAN_PREFERENCE_AVOID_TRAFFIC_JAM);
                        break;
                    case R.id.navi_setting_rb_time:
                        mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.RoutePlanPreference.
                                ROUTE_PLAN_PREFERENCE_TIME_FIRST);
                        break;
                    case R.id.navi_setting_rb_less:
                        mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.RoutePlanPreference.
                                ROUTE_PLAN_PREFERENCE_NOTOLL);
                        break;
                    case R.id.navi_setting_rb_no_highway:
                        mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.RoutePlanPreference.
                                ROUTE_PLAN_PREFERENCE_NOHIGHWAY);
                        break;
                    case R.id.navi_setting_rb_highway:
                        mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.RoutePlanPreference.
                                ROUTE_PLAN_PREFERENCE_ROAD_FIRST);
                        break;
                    default:
                        break;
                }
            }
        });
        // 修改车牌号
        RelativeLayout rlLicensePlate = inflate.findViewById(R.id.navi_setting_rl_license_plate);
        TextView tvLicensePlate = inflate.findViewById(R.id.navi_setting_tv_license_plate);
        // 车牌限行
        ImageView imgSwitch = inflate.findViewById(R.id.navi_setting_img_switch);
        final boolean[] mOpenPlateLimit = {mCommonSetting.isOpenPlateLimit()};
        if (mOpenPlateLimit[0]){
            mCommonSetting.setTruckLimitSwitch(true);
            imgSwitch.setImageResource(R.mipmap.ic_switch_open_white);
        }else {
            mCommonSetting.setTruckLimitSwitch(false);
            imgSwitch.setImageResource(R.mipmap.ic_switch_close_white);
        }
        imgSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOpenPlateLimit[0] == false){
                    mCommonSetting.setTruckLimitSwitch(true);
                    imgSwitch.setImageResource(R.mipmap.ic_switch_open_white);
                    mOpenPlateLimit[0] = true;
                }else {
                    mCommonSetting.setTruckLimitSwitch(false);
                    imgSwitch.setImageResource(R.mipmap.ic_switch_close_white);
                    mOpenPlateLimit[0] = false;
                }
            }
        });

        // 跟随车头
        TextView tvPerspectiveHeadstock = inflate.findViewById(R.id.navi_setting_tv_perspective_headstock);
        // 正北朝上
        TextView tvPerspectiveNorthUp = inflate.findViewById(R.id.navi_setting_tv_perspective_north_up);
        int guideViewMode = mSetting.getGuideViewMode(mVehicleType);
        if (guideViewMode == IAutoNaviSettingParams.NaviPerspectiveMode.CAR_3D){
            mSetting.setGuideViewMode(IAutoNaviSettingParams.NaviPerspectiveMode.CAR_3D, mVehicleType);
            tvPerspectiveHeadstock.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvPerspectiveHeadstock.setTextColor(Color.parseColor("#FFFFFF"));
            tvPerspectiveNorthUp.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvPerspectiveNorthUp.setTextColor(Color.parseColor("#11131A"));
        }else if (guideViewMode == IAutoNaviSettingParams.NaviPerspectiveMode.NORTH_2D){
            mSetting.setGuideViewMode(IAutoNaviSettingParams.NaviPerspectiveMode.NORTH_2D, mVehicleType);
            tvPerspectiveHeadstock.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvPerspectiveHeadstock.setTextColor(Color.parseColor("#11131A"));
            tvPerspectiveNorthUp.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvPerspectiveNorthUp.setTextColor(Color.parseColor("#FFFFFF"));
        }
        tvPerspectiveHeadstock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetting.setGuideViewMode(IAutoNaviSettingParams.NaviPerspectiveMode.CAR_3D, mVehicleType);
                tvPerspectiveHeadstock.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                tvPerspectiveHeadstock.setTextColor(Color.parseColor("#FFFFFF"));
                tvPerspectiveNorthUp.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvPerspectiveNorthUp.setTextColor(Color.parseColor("#11131A"));
            }
        });
        tvPerspectiveNorthUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetting.setGuideViewMode(IAutoNaviSettingParams.NaviPerspectiveMode.NORTH_2D, mVehicleType);
                tvPerspectiveHeadstock.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvPerspectiveHeadstock.setTextColor(Color.parseColor("#11131A"));
                tvPerspectiveNorthUp.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                tvPerspectiveNorthUp.setTextColor(Color.parseColor("#FFFFFF"));
            }
        });
        // 自动
        TextView tvDayAndNightAutomatic = inflate.findViewById(R.id.navi_setting_tv_day_and_night_automatic);
        // 白天
        TextView tvDayAndNightDay = inflate.findViewById(R.id.navi_setting_tv_day_and_night_day);
        // 黑夜
        TextView tvDayAndNightNight = inflate.findViewById(R.id.navi_setting_tv_day_and_night_night);
        int dayNightMode = mSetting.getDayNightMode(mVehicleType);
        if (dayNightMode == IAutoNaviSettingParams.DayNightMode.DAY_NIGHT_MODE_AUTO){
            mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_AUTO, mVehicleType);
            tvDayAndNightAutomatic.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvDayAndNightAutomatic.setTextColor(Color.parseColor("#FFFFFF"));
            tvDayAndNightDay.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvDayAndNightDay.setTextColor(Color.parseColor("#11131A"));
            tvDayAndNightNight.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvDayAndNightNight.setTextColor(Color.parseColor("#11131A"));
        }else if (dayNightMode == IAutoNaviSettingParams.DayNightMode.DAY_NIGHT_MODE_DAY){
            mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_DAY, mVehicleType);
            tvDayAndNightAutomatic.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvDayAndNightAutomatic.setTextColor(Color.parseColor("#11131A"));
            tvDayAndNightDay.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvDayAndNightDay.setTextColor(Color.parseColor("#FFFFFF"));
            tvDayAndNightNight.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvDayAndNightNight.setTextColor(Color.parseColor("#11131A"));
        }else if (dayNightMode == IAutoNaviSettingParams.DayNightMode.DAY_NIGHT_MODE_NIGHT){
            mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_NIGHT, mVehicleType);
            tvDayAndNightAutomatic.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvDayAndNightAutomatic.setTextColor(Color.parseColor("#11131A"));
            tvDayAndNightDay.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvDayAndNightDay.setTextColor(Color.parseColor("#11131A"));
            tvDayAndNightNight.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvDayAndNightNight.setTextColor(Color.parseColor("#FFFFFF"));
        }
        tvDayAndNightAutomatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_AUTO, mVehicleType);
                tvDayAndNightAutomatic.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                tvDayAndNightAutomatic.setTextColor(Color.parseColor("#FFFFFF"));
                tvDayAndNightDay.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvDayAndNightDay.setTextColor(Color.parseColor("#11131A"));
                tvDayAndNightNight.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvDayAndNightNight.setTextColor(Color.parseColor("#11131A"));
            }
        });
        tvDayAndNightDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_DAY, mVehicleType);
                tvDayAndNightAutomatic.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvDayAndNightAutomatic.setTextColor(Color.parseColor("#11131A"));
                tvDayAndNightDay.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                tvDayAndNightDay.setTextColor(Color.parseColor("#FFFFFF"));
                tvDayAndNightNight.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvDayAndNightNight.setTextColor(Color.parseColor("#11131A"));
            }
        });
        tvDayAndNightNight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_NIGHT, mVehicleType);
                tvDayAndNightAutomatic.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvDayAndNightAutomatic.setTextColor(Color.parseColor("#11131A"));
                tvDayAndNightDay.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvDayAndNightDay.setTextColor(Color.parseColor("#11131A"));
                tvDayAndNightNight.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                tvDayAndNightNight.setTextColor(Color.parseColor("#FFFFFF"));
            }
        });
        // 全览小窗
        ImageView imgMap = inflate.findViewById(R.id.navi_setting_img_map);
        // 路况条
        ImageView imgRoad = inflate.findViewById(R.id.navi_setting_img_road);
        int fullViewMode = mSetting.getFullViewMode(mVehicleType);
        if (fullViewMode == IAutoNaviSettingParams.PreViewMode.MapMini){
            mSetting.setFullViewMode(IAutoNaviSettingParams.PreViewMode.MapMini, mVehicleType);
            imgMap.setImageResource(R.mipmap.nsdk_map_switch_checked);
            imgRoad.setImageResource(R.mipmap.nsdk_road_condition_normal);
        }else if (fullViewMode == IAutoNaviSettingParams.PreViewMode.RoadBar){
            mSetting.setFullViewMode(IAutoNaviSettingParams.PreViewMode.RoadBar, mVehicleType);
            imgMap.setImageResource(R.mipmap.nsdk_map_switch_normal);
            imgRoad.setImageResource(R.mipmap.nsdk_road_condition_checked);
        }
        imgMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetting.setFullViewMode(IAutoNaviSettingParams.PreViewMode.MapMini, mVehicleType);
                imgMap.setImageResource(R.mipmap.nsdk_map_switch_checked);
                imgRoad.setImageResource(R.mipmap.nsdk_road_condition_normal);
            }
        });
        imgRoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetting.setFullViewMode(IAutoNaviSettingParams.PreViewMode.RoadBar, mVehicleType);
                imgMap.setImageResource(R.mipmap.nsdk_map_switch_normal);
                imgRoad.setImageResource(R.mipmap.nsdk_road_condition_checked);
            }
        });
        // 是否显示导航比例尺智能缩放
        ImageView imgZoom = inflate.findViewById(R.id.navi_setting_img_zoom);
        final boolean[] isAutoScale = {mSetting.isAutoScale(mVehicleType)};
        imgZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAutoScale[0]){
                    imgZoom.setImageResource(R.mipmap.ic_switch_open_white);
                    mSetting.setAutoScale(true, mVehicleType);
                    isAutoScale[0] = true;
                }else {
                    imgZoom.setImageResource(R.mipmap.ic_switch_close_white);
                    mSetting.setAutoScale(false, mVehicleType);
                    isAutoScale[0] = false;
                }
            }
        });
        // 是否显示路口放大图
        ImageView imgEnlarge = inflate.findViewById(R.id.navi_setting_img_enlarge);
        final boolean[] show = {BDAutoMapFactory.getProfessionalNaviSettingManager()
                .isShowRoadEnlargeView(mVehicleType)};
        if (show[0]) {
            imgEnlarge.setImageResource(R.mipmap.ic_switch_open_white);
        } else {
            imgEnlarge.setImageResource(R.mipmap.ic_switch_close_white);
        }
        imgEnlarge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (show[0]) {
                    imgEnlarge.setImageResource(R.mipmap.ic_switch_close_white);
                    BDAutoMapFactory.getProfessionalNaviSettingManager()
                            .setShowRoadEnlargeView(false, mVehicleType);
                    show[0] = false;
                } else {
                    imgEnlarge.setImageResource(R.mipmap.ic_switch_open_white);
                    BDAutoMapFactory.getProfessionalNaviSettingManager()
                            .setShowRoadEnlargeView(true, mVehicleType);
                    show[0] = true;
                }
            }
        });
        // 是否显示车标到终点红色连线
        ImageView imgConnection = inflate.findViewById(R.id.navi_setting_img_connection);
        final boolean[] showLine = {BDAutoMapFactory.getProfessionalNaviSettingManager()
                .isShowCarLogoToEndRedLine(mVehicleType)};
        if (showLine[0]) {
            imgConnection.setImageResource(R.mipmap.ic_switch_open_white);
            BDAutoMapFactory.getProfessionalNaviSettingManager()
                    .setShowCarLogoToEndRedLine(true, mVehicleType);
        } else {
            imgConnection.setImageResource(R.mipmap.ic_switch_close_white);
            BDAutoMapFactory.getProfessionalNaviSettingManager()
                    .setShowCarLogoToEndRedLine(false, mVehicleType);
        }
        imgConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showLine[0]) {
                    imgConnection.setImageResource(R.mipmap.ic_switch_close_white);
                    BDAutoMapFactory.getProfessionalNaviSettingManager()
                            .setShowCarLogoToEndRedLine(false, mVehicleType);
                    showLine[0] = false;
                } else {
                    imgConnection.setImageResource(R.mipmap.ic_switch_open_white);
                    BDAutoMapFactory.getProfessionalNaviSettingManager()
                            .setShowCarLogoToEndRedLine(true, mVehicleType);
                    showLine[0] = true;
                }
            }
        });
        // 导航播报
        TextView tvBroadcasting = inflate.findViewById(R.id.navi_setting_tv_broadcasting);
        // 导航静音
        TextView tvMute = inflate.findViewById(R.id.navi_setting_tv_mute);
        // 仅提示音
        TextView tvTips = inflate.findViewById(R.id.navi_setting_tv_tips);
        int voiceMode = mSetting.getVoiceMode(mVehicleType);
        if (voiceMode == IAutoNaviSettingParams.VoiceMode.PLAY){
            mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.PLAY, mVehicleType);
            tvBroadcasting.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvBroadcasting.setTextColor(Color.parseColor("#FFFFFF"));
            tvMute.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvMute.setTextColor(Color.parseColor("#11131A"));
            tvTips.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvTips.setTextColor(Color.parseColor("#11131A"));
        }else if (voiceMode == IAutoNaviSettingParams.VoiceMode.QUITE){
            mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.QUITE, mVehicleType);
            tvBroadcasting.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
            tvMute.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvMute.setTextColor(Color.parseColor("#FFFFFF"));
            tvTips.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvTips.setTextColor(Color.parseColor("#11131A"));
        }else if (voiceMode == IAutoNaviSettingParams.VoiceMode.JustPlayWarning){
            mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.JustPlayWarning, mVehicleType);
            tvBroadcasting.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
            tvMute.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvMute.setTextColor(Color.parseColor("#11131A"));
            tvTips.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvTips.setTextColor(Color.parseColor("#FFFFFF"));
        }
        tvBroadcasting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.PLAY, mVehicleType);
                tvBroadcasting.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                tvBroadcasting.setTextColor(Color.parseColor("#FFFFFF"));
                tvMute.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvMute.setTextColor(Color.parseColor("#11131A"));
                tvTips.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvTips.setTextColor(Color.parseColor("#11131A"));
            }
        });
        tvMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.QUITE, mVehicleType);
                tvBroadcasting.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
                tvMute.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                tvMute.setTextColor(Color.parseColor("#FFFFFF"));
                tvTips.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvTips.setTextColor(Color.parseColor("#11131A"));
            }
        });
        tvTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.JustPlayWarning, mVehicleType);
                tvBroadcasting.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
                tvMute.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvMute.setTextColor(Color.parseColor("#11131A"));
                tvTips.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                tvTips.setTextColor(Color.parseColor("#FFFFFF"));
            }
        });
        // 标准播报
        TextView tvStandard = inflate.findViewById(R.id.navi_setting_tv_standard);
        // 简洁播报
        TextView tvConcise = inflate.findViewById(R.id.navi_setting_tv_concise);
        // 播报简介
        TextView tvContentTips = inflate.findViewById(R.id.navi_setting_tv_content_tips);
        int diyVoiceMode = mSetting.getDiyVoiceMode(mVehicleType);
        if (diyVoiceMode == IAutoNaviSettingParams.VoiceDiyMode.STANDARD){
            mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceDiyMode.STANDARD, mVehicleType);
            tvStandard.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvStandard.setTextColor(Color.parseColor("#FFFFFF"));
            tvConcise.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvConcise.setTextColor(Color.parseColor("#11131A"));
            tvContentTips.setText("标准播报：默认模式，适合多数人使用");
        }else if (diyVoiceMode == IAutoNaviSettingParams.VoiceDiyMode.SIMPLE){
            mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceDiyMode.SIMPLE, mVehicleType);
            tvStandard.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvStandard.setTextColor(Color.parseColor("#11131A"));
            tvConcise.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvConcise.setTextColor(Color.parseColor("#FFFFFF"));
            tvContentTips.setText("简洁播报：减少直行路况播报，降低转弯播报频次等，熟练司机使用");
        }
        tvStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceDiyMode.STANDARD, mVehicleType);
                tvStandard.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                tvStandard.setTextColor(Color.parseColor("#FFFFFF"));
                tvConcise.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvConcise.setTextColor(Color.parseColor("#11131A"));
                tvContentTips.setText("标准播报：默认模式，适合多数人使用");
            }
        });
        tvConcise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceDiyMode.SIMPLE,
                        mVehicleType);
                tvStandard.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                tvStandard.setTextColor(Color.parseColor("#11131A"));
                tvConcise.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                tvConcise.setTextColor(Color.parseColor("#FFFFFF"));
                tvContentTips.setText("简洁播报：减少直行路况播报，降低转弯播报频次等，熟练司机使用");
            }
        });
    }

    private void darkenBackground(float alpha){
        WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
        attributes.alpha = alpha;
        getActivity().getWindow().setAttributes(attributes);
    }

    public void onMainSideClick(View view) {
        final CharSequence text = ((Button) view).getText();
        IAutoRouteGuideManager.ChangeRouteListener listener = new IAutoRouteGuideManager
                .ChangeRouteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getActivity(), text + "切换成功", Toast.LENGTH_SHORT).show();
                continueNavi();
            }

            @Override
            public void onFail() {
                Toast.makeText(getActivity(), "切换失败", Toast.LENGTH_SHORT).show();
            }
        };

        if (TextUtils.equals("在主路", text)) {
            mRouteGuideManager.changeRouteByMainSideBridge(AutoNaviCommonParams.MainSideBridge
                    .MAIN_ROAD, listener);
        } else if (TextUtils.equals("在辅路", text)) {
            mRouteGuideManager.changeRouteByMainSideBridge(AutoNaviCommonParams.MainSideBridge
                    .SIDE_ROAD, listener);
        }
    }

    public void onBridgeClick(View view) {
        final CharSequence text = ((Button) view).getText();
        IAutoRouteGuideManager.ChangeRouteListener listener = new IAutoRouteGuideManager
                .ChangeRouteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getActivity(), text + "切换成功", Toast.LENGTH_SHORT).show();
                continueNavi();
            }

            @Override
            public void onFail() {
                Toast.makeText(getActivity(), "切换失败", Toast.LENGTH_SHORT).show();
            }
        };

        if (TextUtils.equals("在桥上", text)) {
            mRouteGuideManager.changeRouteByMainSideBridge(AutoNaviCommonParams.MainSideBridge
                    .ON_BRIDGE, listener);
        } else if (TextUtils.equals("在桥下", text)) {
            mRouteGuideManager.changeRouteByMainSideBridge(AutoNaviCommonParams.MainSideBridge
                    .UNDER_BRIDGE, listener);
        }
    }

    private void continueNavi() {
        BDAutoMapFactory.getAutoRouteGuideManager().continueNavi();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_zoom_in:
                    onZoomInClick(v);
                    break;
                case R.id.btn_zoom_out:
                    onZoomOutClick(v);
                    break;
                case R.id.btn_full_view:
                case R.id.ll_full_view:
                    onFullViewClick(v);
                    break;
                case R.id.btn_recover_navi:
                case R.id.tv_recover_navi:
                    onRecoverNaviClick(v);
                    break;
                case R.id.btn_refresh_route:
                case R.id.ll_refresh_route:
                    onRefreshRouteClick(v);
                    break;
                case R.id.btn_route_condition:
                case R.id.ll_route_condition:
                    onRoadConditionClick(v);
                    break;
                case R.id.setting_btn:
                case R.id.ll_setting:
                    onSettingClick(v);
                    break;
                case R.id.on_under_bridge:
                    onBridgeClick(v);
                    break;
                case R.id.main_side_road_btn:
                    onMainSideClick(v);
                    break;
                case R.id.btn_voice_quite:
                    onVoiceQuiteClick(v);
                    break;
                case R.id.btn_new_user:
                    onNewUserClick(v);
                    break;
                case R.id.btn_god_user:
                    onGodUserClick(v);
                    break;
                case R.id.btn_smart_scale_close:
                    onSmartScaleCloseClick(v);
                    break;
                case R.id.btn_smart_scale_open:
                    onSmartScaleOpenClick(v);
                    break;
                case R.id.btn_night_mode_auto:
                    onDayNightAutoClick(v);
                    break;
                case R.id.btn_night_mode_night:
                    onDayNightNightClick(v);
                    break;
                case R.id.btn_night_mode_day:
                    onDayNightDayClick(v);
                    break;
                case R.id.btn_2d_3d:
                    on2D3DClick(v);
                    break;
                case R.id.btn_change_icon:
                    onChangeIconClick(v);
                    break;
                case R.id.btn_camera_status:
                    onCameraStatusClick(v);
                    break;
                case R.id.btn_road_enlarge_view:
                    boolean show =
                            BDAutoMapFactory.getProfessionalNaviSettingManager()
                                    .isShowRoadEnlargeView(IAutoCommonConfig.Vehicle.CAR);
                    BDAutoMapFactory.getProfessionalNaviSettingManager()
                            .setShowRoadEnlargeView(!show, IAutoCommonConfig.Vehicle.CAR);

                    if (show) {
                        mRoadEnlargeView.setText("路口放大图(已关闭)");
                    } else {
                        mRoadEnlargeView.setText("路口放大图(已开启)");
                    }
                    break;
                case R.id.btn_car_end_line:
                    boolean showLine = BDAutoMapFactory.getProfessionalNaviSettingManager()
                            .isShowCarLogoToEndRedLine(IAutoCommonConfig.Vehicle.CAR);
                    BDAutoMapFactory.getProfessionalNaviSettingManager()
                            .setShowCarLogoToEndRedLine(!showLine, IAutoCommonConfig.Vehicle.CAR);
                    break;
                case R.id.ll_exit:
                    onBackPressed();
                    break;
                case R.id.button_one:
                    mEnlargeView.setVisibility(View.VISIBLE);
                    View miniMapView = BDAutoMapFactory.getAutoRouteGuideManager().getMiniMapView();
                    if (miniMapView != null && miniMapView.getParent() != null) {
                        ViewGroup parent = (ViewGroup) miniMapView.getParent();
                        parent.removeView(miniMapView);
                    }
                    mEnlargeView.addView(miniMapView);
//                    mSetting.setCarIconOffsetForNavi(500, 50);
                    break;
                case R.id.button_two:
                    mSetting.setCarIconOffsetForNavi(0, 0);
                    break;
                default:
                    break;
            }
        }
    };

    private final IAutoNaviListener mAutoNaviListener = new IAutoNaviListener() {

        @Override
        public void onRoadNameUpdate(String name) {

        }

        @Override
        public void onRemainInfoUpdate(int remainDistance, int remainTime) {
            StringBuilder distBuffer = new StringBuilder();
//            String.formatDistance(remainDistance, StringUtils.UnitLangEnum.ZH, distBuffer);
            String s = StringFormatUtils.formatDistanceStringForRouteResult(remainDistance);
            tvAllDistance.setText(s);

            StringBuilder timeBuffer = new StringBuilder();
//            StringUtils.formatTime(remainTime, StringUtils.UnitLangEnum.ZH, timeBuffer);
            String s1 = StringFormatUtils.carFormatTimeStringProcessDay(remainTime, true);
            tvTakesTime.setText(s1);

            String dateStr = StringFormatUtils.getDateStr();
            Long s2 = StringFormatUtils.dateToStamp(dateStr);
            String arrivalTime = StringFormatUtils.stampToDate(s2 + remainTime * 1000);
            tvArrivalTime.setText(arrivalTime + "到达");
        }

        @Override
        public void onViaListRemainInfoUpdate(Message message) {

        }

        @Override
        public void onGuideInfoUpdate(BNaviInfo bNaviInfo) {
            if (bNaviInfo == null) {
                return;
            }

//            if (mNaviMsgListener != null) {
//                Drawable turnDrawable =
//                        RGSimpleGuideModel.getInstance().getTurnDrawable(bNaviInfo.turnIconName);
//                mNaviMsgListener.onRoadTurnInfoIconUpdate(turnDrawable);
//            }
//            String turnIconNameFix4Audi = bNaviInfo.turnIconName;
//
//            OpenNotifierManager.getInstance().getOpenNotifier().notifyNaviIncludeInfo(
//                    NaviInducedMethod.NEXT_ROAD_NAME, bNaviInfo.roadName, getAutoDirection());
//            AutoWidgetModel.getInstance().setLinkToInfoS(AutoWidgetController.getLinkToInfo(bNaviInfo.roadName));
//            AutoWidgetModel.getInstance().setNextRoadNameS(AutoWidgetController.getNextRoadName(bNaviInfo.roadName));

            StringBuilder distBuffer = new StringBuilder();
//            StringUtils.formatDistance(bNaviInfo.distance, StringUtils.UnitLangEnum.ZH, distBuffer);

            StringFormatUtils.formatDistanceStringForRouteResult(bNaviInfo.distance);

            tvRoadDistance.setText(bNaviInfo.distance + "");
            tvRoadName.setText(bNaviInfo.roadName);
            imgDirectionIcon.setImageBitmap(bNaviInfo.turnIcon);
        }

        @Override
        public void onHighWayInfoUpdate(Action action, BNHighwayInfo bnHighwayInfo) {
            Log.d(TAG, "onHighWayInfoUpdate: " + bnHighwayInfo);
        }

        @Override
        public void onFastExitWayInfoUpdate(Action action, String s, int i, String s1) {
            Log.d(TAG, "onFastExitWayInfoUpdate: " + s1);
        }

        @Override
        public void onEnlargeMapUpdate(Action action, View view, String remainDistance,
                                       int progress, String roadName, Bitmap turnIcon) {
            Log.d(TAG, "onEnlargeMapUpdate: " + roadName);
        }

        @Override
        public void onDayNightChanged(DayNightMode dayNightMode) {
            Log.d(TAG, "onDayNightChanged: " + dayNightMode);
        }

        @Override
        public void onRoadConditionInfoUpdate(double v, List<BNRoadCondition> list) {
        }

        @Override
        public void onRoadConditionChange(List<BNaviRoadConditionItem> items,
                                          BNaviRoadConditionItem roadCondition) {
            Log.d(TAG, "onRoadConditionChange: " + items);
        }

        @Override
        public void onMainSideBridgeUpdate(int i) {
            if (i == 1){
                llViaductMsg.setVisibility(View.INVISIBLE);
                llRoadsMsg.setVisibility(View.VISIBLE);
                imgRoadsMsg.setImageResource(R.mipmap.ic_main_road_white);
                tvRoadsMsg.setText("在主路");
            }else if (i == 2){
                llViaductMsg.setVisibility(View.INVISIBLE);
                llRoadsMsg.setVisibility(View.VISIBLE);
                imgRoadsMsg.setImageResource(R.mipmap.ic_side_road_white);
                tvRoadsMsg.setText("在辅路");
            }else if (i == 4){
                llViaductMsg.setVisibility(View.VISIBLE);
                llRoadsMsg.setVisibility(View.INVISIBLE);
                imgViaductMsg.setImageResource(R.mipmap.ic_on_the_bridge_white);
                tvViaductMsg.setText("在桥上");
            }else if (i == 5){
                llViaductMsg.setVisibility(View.VISIBLE);
                llRoadsMsg.setVisibility(View.VISIBLE);
                imgRoadsMsg.setImageResource(R.mipmap.ic_main_road_white);
                imgViaductMsg.setImageResource(R.mipmap.ic_on_the_bridge_white);
                tvRoadsMsg.setText("在主路");
                tvViaductMsg.setText("在桥上");
            }else if (i == 8){
                llViaductMsg.setVisibility(View.VISIBLE);
                llRoadsMsg.setVisibility(View.INVISIBLE);
                imgViaductMsg.setImageResource(R.mipmap.ic_under_the_bridge_white);
                tvViaductMsg.setText("在桥下");
            }else {
                llViaductMsg.setVisibility(View.GONE);
                llRoadsMsg.setVisibility(View.GONE);
            }
        }

        @Override
        public void onLaneInfoUpdate(Action action, List<BNavLineItem> list) {

            JSONObject json = new JSONObject();
            int index = 0;
            try {
                if (action == Action.SHOW || action == Action.UPDATE) {
                    json.put("isShow", 1);
                } else if (action == Action.HIDE) {
                    json.put("isShow", 0);
                }
                if (list != null) {
                    json.put("laneNum", list.size());
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) == null) {
                            break;
                        }
                        if ("lixiang".equals(BuildConfig.FLAVOR)) {
                            Log.e(TAG, "onLaneInfoUpdate i: " + i + "; resName: "
                                    + list.get(i).getResName());
//                            int hashMapValue = AutoNavigationInformationData.getInstance().
//                                    getHashMapValue(list.get(i).getResName());
//                            Log.e(TAG, "onLaneInfoUpdate hashMapValue = " + hashMapValue);
//                            if (hashMapValue == 0) {
//                                break;
//                            }
//                            json.put("lane-" + i, hashMapValue);
                        } else {
                            json.put("lane-" + i, list.get(i).getResName());
                        }
                        index = i;
                    }
                    // 这里一旦有一个资源没有找到，则不返回车道线数据,第三方可以不做展示，避免出现只有部分数据时展示异常问题
                    if (index != list.size() - 1) {
                        Log.e(TAG, "onLaneInfoUpdate index = " + index + "; size = "
                                + list.size());
                        JSONObject jsonError = new JSONObject();
                        jsonError.put("isShow", 0);
                        jsonError.put("laneNum", 0);
                        json = jsonError;
                    }
                } else {
                    json.put("laneNum", 0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSpeedUpdate(int speed, int speedLimit) {
            Log.d(TAG, "onSpeedUpdate: " + speedLimit);
        }

        @Override
        public void onArriveDestination() {
            onBackPressed();
        }

        @Override
        public void onArrivedWayPoint(int i) {

        }

        @Override
        public void onLocationChange(BNaviLocation bNaviLocation) {

        }

        @Override
        public void onMapStateChange(MapStateMode mapStateMode) {
            Log.i("IAutoNaviListener", "onMapStateChange mapStateMode = " + mapStateMode);
        }

        @Override
        public void onFullviewStateChange(boolean isFullView) {
            // TODO::
        }

        @Override
        public void onStartYawing(String s) {

        }

        @Override
        public void onYawingSuccess() {

        }

        @Override
        public void onYawingArriveViaPoint(int i) {

        }

        @Override
        public void onNotificationShow(String s) {

        }

        @Override
        public void onHeavyTraffic() {

        }

        @Override
        public void onNaviGuideEnd() {
            mRouteGuideManager.stopNavi();
            // 关闭当前页
            NavHostFragment.findNavController(DemoCustomUiFragment.this).popBackStack();
            EventBus.getDefault().postSticky(new MessageEvent(true));
        }

        @Override
        public void onSatelliteNumUpdate(int i) {
            // TODO::
        }
    };

}
