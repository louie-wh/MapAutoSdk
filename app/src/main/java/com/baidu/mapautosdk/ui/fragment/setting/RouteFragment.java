package com.baidu.mapautosdk.ui.fragment.setting;

import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.interfaces.IAutoCommonConfig;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviSettingManager;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviSettingParams;
import com.baidu.mapautosdk.interfaces.route.IAutoRoutPlanManager;
import com.baidu.mapautosdk.ui.BaseFragment;

public class RouteFragment extends BaseFragment {
    private ViewGroup mContentView;
    private ImageView imgSwitch;
    private TextView tvPassengerCar;
    private TextView tvMotorcycle;
    private ImageView imgOnlineRouteCalculation;
    private IAutoNaviSettingManager.ICommonSetting mCommonSetting;
    private boolean mOpenPlateLimit;
    private int vehicleType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_route, container, false);
        } else {
            ViewGroup parentView = (ViewGroup) mContentView.getParent();
            if (parentView != null) {
                parentView.removeView(mContentView);
            }
        }
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vehicleType = BDAutoMapFactory.getNaviCommonSettingManager().getVehicleType();

        mCommonSetting = BDAutoMapFactory.getNaviCommonSettingManager();

        // 修改车牌
        view.findViewById(R.id.route_rl_license_plate).setOnClickListener(clickListener);
        // 车牌限行
        imgSwitch = view.findViewById(R.id.route_img_switch);
        imgSwitch.setOnClickListener(clickListener);
        // 是否开启车牌限行
        mOpenPlateLimit = mCommonSetting.isOpenPlateLimit();
        if (mOpenPlateLimit){
            mCommonSetting.setTruckLimitSwitch(true);
            imgSwitch.setImageResource(R.mipmap.ic_switch_open_white);
        }else {
            mCommonSetting.setTruckLimitSwitch(false);
            imgSwitch.setImageResource(R.mipmap.ic_switch_close_white);
        }

        // 小客车
        tvPassengerCar = view.findViewById(R.id.route_tv_passenger_car);
        tvPassengerCar.setOnClickListener(clickListener);
        // 货车
        tvMotorcycle = view.findViewById(R.id.route_tv_motorcycle);
        tvMotorcycle.setOnClickListener(clickListener);
        if (vehicleType == IAutoCommonConfig.Vehicle.CAR){
            mCommonSetting.setVehicleType(IAutoCommonConfig.Vehicle.CAR);
            tvPassengerCar.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvPassengerCar.setTextColor(Color.parseColor("#FFFFFF"));
            tvMotorcycle.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvMotorcycle.setTextColor(Color.parseColor("#11131A"));
        }else if (vehicleType == IAutoCommonConfig.Vehicle.TRUCK){
            mCommonSetting.setVehicleType(IAutoCommonConfig.Vehicle.TRUCK);
            tvPassengerCar.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvPassengerCar.setTextColor(Color.parseColor("#11131A"));
            tvMotorcycle.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvMotorcycle.setTextColor(Color.parseColor("#FFFFFF"));
        }
        // 在线算路优先
        imgOnlineRouteCalculation = view.findViewById(R.id.route_img_online_route_calculation);
        imgOnlineRouteCalculation.setOnClickListener(clickListener);

        // 导航路线
        RadioGroup rg = view.findViewById(R.id.route_rg);
        // 智能推荐
        RadioButton rbRecommend = view.findViewById(R.id.route_rb_recommend);
        // 躲避拥堵
        RadioButton rbJam = view.findViewById(R.id.route_rb_jam);
        // 时间优先
        RadioButton rbTime = view.findViewById(R.id.route_rb_time);
        // 少收费
        RadioButton rbLess = view.findViewById(R.id.route_rb_less);
        // 不走高速
        RadioButton rbNoHighway = view.findViewById(R.id.route_rb_no_highway);
        // 高速优先
        RadioButton rbHighway = view.findViewById(R.id.route_rb_highway);
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
                    case R.id.route_rb_recommend:
                        mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.
                                RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT);
                        break;
                    case R.id.route_rb_jam:
                        mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.RoutePlanPreference.
                                ROUTE_PLAN_PREFERENCE_AVOID_TRAFFIC_JAM);
                        break;
                    case R.id.route_rb_time:
                        mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.RoutePlanPreference.
                                ROUTE_PLAN_PREFERENCE_TIME_FIRST);
                        break;
                    case R.id.route_rb_less:
                        mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.RoutePlanPreference.
                                ROUTE_PLAN_PREFERENCE_NOTOLL);
                        break;
                    case R.id.route_rb_no_highway:
                        mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.RoutePlanPreference.
                                ROUTE_PLAN_PREFERENCE_NOHIGHWAY);
                        break;
                    case R.id.route_rb_highway:
                        mCommonSetting.setRouteSortMode(IAutoRoutPlanManager.RoutePlanPreference.
                                ROUTE_PLAN_PREFERENCE_ROAD_FIRST);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private boolean isOnlineRouteCalculation = false;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.route_rl_license_plate:
//                    NavHostFragment.findNavController(RouteFragment.this)
//                            .navigate(R.id.action_mainFragment_to_modifyLicensePlateNumberFragment);
                    break;
                case R.id.route_img_switch:
                    if (mOpenPlateLimit == false){
                        mCommonSetting.setTruckLimitSwitch(true);
                        imgSwitch.setImageResource(R.mipmap.ic_switch_open_white);
                        mOpenPlateLimit = true;
                    }else {
                        mCommonSetting.setTruckLimitSwitch(false);
                        imgSwitch.setImageResource(R.mipmap.ic_switch_close_white);
                        mOpenPlateLimit = false;
                    }
                    break;
                case R.id.route_tv_passenger_car:
                    mCommonSetting.setVehicleType(IAutoCommonConfig.Vehicle.CAR);
                    tvPassengerCar.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvPassengerCar.setTextColor(Color.parseColor("#FFFFFF"));
                    tvMotorcycle.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvMotorcycle.setTextColor(Color.parseColor("#11131A"));
                    break;
                case R.id.route_tv_motorcycle:
                    mCommonSetting.setVehicleType(IAutoCommonConfig.Vehicle.TRUCK);
                    tvPassengerCar.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvPassengerCar.setTextColor(Color.parseColor("#11131A"));
                    tvMotorcycle.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvMotorcycle.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case R.id.route_img_online_route_calculation:
                    if (isOnlineRouteCalculation == false){
                        imgOnlineRouteCalculation.setImageResource(R.mipmap.ic_switch_open_white);
                        isOnlineRouteCalculation = true;
                    }else {
                        imgOnlineRouteCalculation.setImageResource(R.mipmap.ic_switch_close_white);
                        isOnlineRouteCalculation = false;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onBackPressed() {
        NavHostFragment.findNavController(RouteFragment.this).popBackStack();
        return false;
    }
}