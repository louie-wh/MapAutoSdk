package com.baidu.mapautosdk.ui.fragment.setting;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.interfaces.IAutoCommonConfig;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviSettingManager;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviSettingParams;
import com.baidu.mapautosdk.interfaces.theme.IAutoDayNightManager;
import com.baidu.mapautosdk.ui.BaseFragment;

public class NavigationFragment extends BaseFragment {
    private ViewGroup mContentView;
    private TextView tvPerspectiveHeadstock;
    private TextView tvNorthUp;
    private TextView tvAutomatic;
    private TextView tvDay;
    private TextView tvNight;
    private ImageView imgZoom;
    private ImageView imgEnlarge;
    private ImageView imgConnection;
    private ImageView imgMap;
    private ImageView imgRoad;
    private IAutoNaviSettingManager.IProfessionalNaviSetting mSetting;
    private boolean mEnlarge;
    private boolean mShowRadLine;
    private boolean mAutoScale;
    private int mVehicleType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_navigation, container, false);
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
        // 获取车辆类型
        mVehicleType = BDAutoMapFactory.getNaviCommonSettingManager().getVehicleType();

        mSetting = BDAutoMapFactory.getProfessionalNaviSettingManager();

        // 跟随车头
        tvPerspectiveHeadstock = view.findViewById(R.id.navigation_tv_perspective_headstock);
        tvPerspectiveHeadstock.setOnClickListener(clickListener);
        // 正北朝上
        tvNorthUp = view.findViewById(R.id.navigation_tv_perspective_north_up);
        tvNorthUp.setOnClickListener(clickListener);
        int guideViewMode = mSetting.getGuideViewMode(mVehicleType);
        if (guideViewMode == IAutoNaviSettingParams.NaviPerspectiveMode.CAR_3D){
            mSetting.setGuideViewMode(IAutoNaviSettingParams.NaviPerspectiveMode.CAR_3D, mVehicleType);
            tvPerspectiveHeadstock.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvPerspectiveHeadstock.setTextColor(Color.parseColor("#FFFFFF"));
            tvNorthUp.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvNorthUp.setTextColor(Color.parseColor("#11131A"));
        }else if (guideViewMode == IAutoNaviSettingParams.NaviPerspectiveMode.NORTH_2D){
            mSetting.setGuideViewMode(IAutoNaviSettingParams.NaviPerspectiveMode.NORTH_2D, mVehicleType);
            tvPerspectiveHeadstock.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvPerspectiveHeadstock.setTextColor(Color.parseColor("#11131A"));
            tvNorthUp.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvNorthUp.setTextColor(Color.parseColor("#FFFFFF"));
        }

        // 自动
        tvAutomatic = view.findViewById(R.id.navigation_tv_day_and_night_automatic);
        tvAutomatic.setOnClickListener(clickListener);
        // 白天
        tvDay = view.findViewById(R.id.navigation_tv_day_and_night_day);
        tvDay.setOnClickListener(clickListener);
        // 黑夜
        tvNight = view.findViewById(R.id.navigation_tv_day_and_night_night);
        tvNight.setOnClickListener(clickListener);
        int dayNightMode = mSetting.getDayNightMode(mVehicleType);
        if (dayNightMode == IAutoNaviSettingParams.DayNightMode.DAY_NIGHT_MODE_AUTO){
            mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_AUTO, mVehicleType);
            BDAutoMapFactory.getAutoDayNightManager()
                    .setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_AUTO);
            tvAutomatic.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvAutomatic.setTextColor(Color.parseColor("#FFFFFF"));
            tvDay.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvDay.setTextColor(Color.parseColor("#11131A"));
            tvNight.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvNight.setTextColor(Color.parseColor("#11131A"));
        }else if (dayNightMode == IAutoNaviSettingParams.DayNightMode.DAY_NIGHT_MODE_DAY){
            mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_DAY, mVehicleType);
            BDAutoMapFactory.getAutoDayNightManager()
                    .setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_DAY);
            tvAutomatic.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvAutomatic.setTextColor(Color.parseColor("#11131A"));
            tvDay.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvDay.setTextColor(Color.parseColor("#FFFFFF"));
            tvNight.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvNight.setTextColor(Color.parseColor("#11131A"));
        }else if (dayNightMode == IAutoNaviSettingParams.DayNightMode.DAY_NIGHT_MODE_NIGHT){
            mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_NIGHT, mVehicleType);
            BDAutoMapFactory.getAutoDayNightManager()
                    .setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_NIGHT);
            tvAutomatic.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvAutomatic.setTextColor(Color.parseColor("#11131A"));
            tvDay.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvDay.setTextColor(Color.parseColor("#11131A"));
            tvNight.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvNight.setTextColor(Color.parseColor("#FFFFFF"));
        }

        // 全览小窗
        imgMap = view.findViewById(R.id.navigation_img_map);
        imgMap.setOnClickListener(clickListener);
        // 路况条
        imgRoad = view.findViewById(R.id.navigation_img_road);
        imgRoad.setOnClickListener(clickListener);
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

        // 导航比例尺只能缩放
        imgZoom = view.findViewById(R.id.navigation_img_zoom);
        imgZoom.setOnClickListener(clickListener);
        mAutoScale = mSetting.isAutoScale(IAutoCommonConfig.Vehicle.CAR);
        if (mAutoScale){
            imgZoom.setImageResource(R.mipmap.ic_switch_open_white);
        }else {
            imgZoom.setImageResource(R.mipmap.ic_switch_close_white);
        }

        // 显示路口放大图
        imgEnlarge = view.findViewById(R.id.navigation_img_enlarge);
        imgEnlarge.setOnClickListener(clickListener);
        mEnlarge = BDAutoMapFactory.getProfessionalNaviSettingManager()
                .isShowRoadEnlargeView(mVehicleType);
        if (mEnlarge) {
            imgEnlarge.setImageResource(R.mipmap.ic_switch_open_white);
        } else {
            imgEnlarge.setImageResource(R.mipmap.ic_switch_close_white);
        }

        // 显示车标到终点红色连线
        imgConnection = view.findViewById(R.id.navigation_img_connection);
        imgConnection.setOnClickListener(clickListener);
        mShowRadLine = BDAutoMapFactory.getProfessionalNaviSettingManager()
                .isShowCarLogoToEndRedLine(IAutoCommonConfig.Vehicle.CAR);
        if (mShowRadLine) {
            imgConnection.setImageResource(R.mipmap.ic_switch_open_white);
            BDAutoMapFactory.getProfessionalNaviSettingManager()
                    .setShowCarLogoToEndRedLine(true, mVehicleType);
        } else {
            imgConnection.setImageResource(R.mipmap.ic_switch_close_white);
            BDAutoMapFactory.getProfessionalNaviSettingManager()
                    .setShowCarLogoToEndRedLine(false, mVehicleType);
        }
    }

    private int mNavingState;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.navigation_tv_perspective_headstock:
                    mSetting.setGuideViewMode(IAutoNaviSettingParams.NaviPerspectiveMode.CAR_3D, mVehicleType);
                    mNavingState = IAutoNaviSettingParams.NaviPerspectiveMode.CAR_3D;
                    tvPerspectiveHeadstock.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvPerspectiveHeadstock.setTextColor(Color.parseColor("#FFFFFF"));
                    tvNorthUp.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvNorthUp.setTextColor(Color.parseColor("#11131A"));
                    break;
                case R.id.navigation_tv_perspective_north_up:
                    mSetting.setGuideViewMode(IAutoNaviSettingParams.NaviPerspectiveMode.NORTH_2D, mVehicleType);
                    mNavingState = IAutoNaviSettingParams.NaviPerspectiveMode.NORTH_2D;
                    tvPerspectiveHeadstock.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvPerspectiveHeadstock.setTextColor(Color.parseColor("#11131A"));
                    tvNorthUp.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvNorthUp.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case R.id.navigation_tv_day_and_night_automatic:
                    mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_AUTO, mVehicleType);
                    BDAutoMapFactory.getAutoDayNightManager()
                            .setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_AUTO);
                    tvAutomatic.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvAutomatic.setTextColor(Color.parseColor("#FFFFFF"));
                    tvDay.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvDay.setTextColor(Color.parseColor("#11131A"));
                    tvNight.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvNight.setTextColor(Color.parseColor("#11131A"));
                    break;
                case R.id.navigation_tv_day_and_night_day:
                    mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_DAY, mVehicleType);
                    BDAutoMapFactory.getAutoDayNightManager()
                        .setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_DAY);
                    tvAutomatic.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvAutomatic.setTextColor(Color.parseColor("#11131A"));
                    tvDay.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvDay.setTextColor(Color.parseColor("#FFFFFF"));
                    tvNight.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvNight.setTextColor(Color.parseColor("#11131A"));
                    break;
                case R.id.navigation_tv_day_and_night_night:
                    mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_NIGHT, mVehicleType);
                    BDAutoMapFactory.getAutoDayNightManager()
                            .setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_NIGHT);
                    tvAutomatic.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvAutomatic.setTextColor(Color.parseColor("#11131A"));
                    tvDay.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvDay.setTextColor(Color.parseColor("#11131A"));
                    tvNight.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvNight.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case R.id.navigation_img_zoom:
                    if (mAutoScale == false){
                        imgZoom.setImageResource(R.mipmap.ic_switch_open_white);
                        mSetting.setAutoScale(true, mVehicleType);
                        mAutoScale = true;
                    }else {
                        imgZoom.setImageResource(R.mipmap.ic_switch_close_white);
                        mSetting.setAutoScale(false, mVehicleType);
                        mAutoScale = false;
                    }
                    break;
                case R.id.navigation_img_enlarge:
                    if (mEnlarge == false){
                        imgEnlarge.setImageResource(R.mipmap.ic_switch_open_white);
                        BDAutoMapFactory.getProfessionalNaviSettingManager()
                                .setShowRoadEnlargeView(true, mVehicleType);
                        mEnlarge = true;
                    }else {
                        imgEnlarge.setImageResource(R.mipmap.ic_switch_close_white);
                        BDAutoMapFactory.getProfessionalNaviSettingManager()
                                .setShowRoadEnlargeView(false, mVehicleType);
                        mEnlarge = false;
                    }
                    break;
                case R.id.navigation_img_connection:
                    if (mShowRadLine == false){
                        imgConnection.setImageResource(R.mipmap.ic_switch_open_white);
                        BDAutoMapFactory.getProfessionalNaviSettingManager()
                                .setShowCarLogoToEndRedLine(true, mVehicleType);
                        mShowRadLine = true;
                    }else {
                        imgConnection.setImageResource(R.mipmap.ic_switch_close_white);
                        BDAutoMapFactory.getProfessionalNaviSettingManager()
                                .setShowCarLogoToEndRedLine(false, mVehicleType);
                        mShowRadLine = false;
                    }
                    break;
                case R.id.navigation_img_map:
                    mSetting.setFullViewMode(IAutoNaviSettingParams.PreViewMode.MapMini, mVehicleType);
                    imgMap.setImageResource(R.mipmap.nsdk_map_switch_checked);
                    imgRoad.setImageResource(R.mipmap.nsdk_road_condition_normal);
                    break;
                case R.id.navigation_img_road:
                    mSetting.setFullViewMode(IAutoNaviSettingParams.PreViewMode.RoadBar, mVehicleType);
                    imgMap.setImageResource(R.mipmap.nsdk_map_switch_normal);
                    imgRoad.setImageResource(R.mipmap.nsdk_road_condition_checked);
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onBackPressed() {
        NavHostFragment.findNavController(NavigationFragment.this).popBackStack();
        return false;
    }
}