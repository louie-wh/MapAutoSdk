package com.baidu.mapautosdk.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapautosdk.MessageEvent;
import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.map.AutoMapBound;
import com.baidu.mapautosdk.api.map.AutoMapStatus;
import com.baidu.mapautosdk.api.search.poi.AutoPoiDetailInfo;
import com.baidu.mapautosdk.api.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapautosdk.api.search.poi.PoiDetailSearchOption;
import com.baidu.mapautosdk.api.search.poi.PoiDetailSearchResult;
import com.baidu.mapautosdk.api.search.poi.PoiNearbySearchOption;
import com.baidu.mapautosdk.api.search.poi.PoiResult;
import com.baidu.mapautosdk.api.search.poi.PoiSearch;
import com.baidu.mapautosdk.bean.AutoPoiInfo;
import com.baidu.mapautosdk.interfaces.search.IAutoPoiSearchManager;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.util.ScreenUtil;
import com.baidu.platform.comapi.basestruct.Point;
import com.baidu.platform.comapi.sdk.map.BitmapDescriptor;
import com.baidu.platform.comapi.sdk.map.BitmapDescriptorFactory;
import com.baidu.platform.comapi.sdk.map.MarkerOptions;
import com.baidu.platform.comapi.sdk.map.SDKOverlay;
import com.baidu.platform.comapi.sdk.mapapi.model.LatLng;
import com.baidu.tts.tools.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class PoiDetailsFragment extends BaseFragment {

    private ViewGroup mContentView;
    private String uid;
    private int cityId;
    private String poiName;
    private String address;
    private String district;
    private PoiSearch poiSearch;
    private TextView tvName;
    private TextView tvAddrs;
    private String name;
    private String poiInfoUid;
    private double latitude;
    private double longitude;
    private IAutoPoiSearchManager mPoiSearch;
    private double latitude1;
    private double longitude1;
    private SDKOverlay mMarker = null;
    private AutoMapStatus st;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        uid = arguments.getString("uid");
        cityId = arguments.getInt("cityId");
        poiName = arguments.getString("poiName", "");
        address = arguments.getString("address", "");
        district = arguments.getString("district", "");
        latitude1 = arguments.getDouble("latitude", 0);
        longitude1 = arguments.getDouble("longitude", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        boolean registered = EventBus.getDefault().isRegistered(this);
        if (registered == false){
            EventBus.getDefault().register(this);
        }

        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_poi_details, container, false);
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

        tvName = view.findViewById(R.id.poi_details_tv_name); // 地点
        tvAddrs = view.findViewById(R.id.poi_details_tv_addrs); // 地址
        view.findViewById(R.id.poi_details_img_back).setOnClickListener(clickListener);
        view.findViewById(R.id.poi_details_ll_go_here).setOnClickListener(clickListener);

        if (!TextUtils.isEmpty(uid)){
            mPoiSearch = BDAutoMapFactory.getAutoPoiSearchManager();
            mPoiSearch.setOnGetPoiSearchResultListener(listener);
            mPoiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(uid));
        }else {
            if (!TextUtils.isEmpty(poiName)) {
                mPoiSearch = BDAutoMapFactory.getAutoPoiSearchManager();
                mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword(poiName).radius(1000)
                        .location(new LatLng(latitude1, longitude1)));
                mPoiSearch.setOnGetPoiSearchResultListener(listener);
                tvName.setText(address);
                tvAddrs.setText(poiName);
                addMarker(latitude1, longitude1);
                showMapBoundArea(longitude1, latitude1);
            }else {
                tvName.setText(district);
                tvAddrs.setText(address);
                addMarker(latitude1, longitude1);
                showMapBoundArea(longitude1, latitude1);
            }
        }

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            switch (v.getId()) {
                case R.id.poi_details_img_back:
                    onBackPressed();
                    break;
                case R.id.poi_details_ll_go_here:
                    if (!TextUtils.isEmpty(name)) {
                        EventBus.getDefault().postSticky(new MessageEvent(false));
                        bundle.putString("poiName", name);
                        bundle.putString("poiUid", poiInfoUid);
                        bundle.putDouble("latitude", latitude);
                        bundle.putDouble("longitude", longitude);
                        SharedPreferencesUtils.putBoolean(getActivity(), "isRoute", true);
                        if (mMarker != null) {
                            mMarker.remove();
                        }
                        NavHostFragment.findNavController(PoiDetailsFragment.this)
                                .navigate(R.id.action_routePlanFragment_to_recommendedRouteFragment, bundle);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult != null) {
                if (poiResult.isSuccessful()) {
                    List<AutoPoiInfo> allPoi = poiResult.getAllPoi();
                    AutoPoiInfo poiInfo = allPoi.get(0);
//                    tvName.setText(poiInfo.name);
//                    tvAddrs.setText(poiInfo.address);
                    name = poiInfo.name;
                    poiInfoUid = poiInfo.uid;
                    latitude = poiInfo.location.latitude;
                    longitude = poiInfo.location.longitude;
//                    addMarker(latitude, longitude);
//                    showMapBoundArea(longitude, latitude);
                } else {
                    Toast.makeText(getContext(), "Fail error = " + poiResult.getErrorCode(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            List<AutoPoiDetailInfo> allPoi = poiDetailSearchResult.getPoiDetailInfoList();
            if (allPoi != null) {
                AutoPoiDetailInfo poiInfo = allPoi.get(0);
                tvName.setText(poiInfo.name);
                tvAddrs.setText(poiInfo.addr);
                name = poiInfo.name;
                poiInfoUid = poiInfo.uid;
                latitude = poiInfo.geo.getDoubleY();
                longitude = poiInfo.geo.getDoubleX();
                addMarker(latitude, longitude);
                showMapBoundArea(longitude, latitude);
            }
        }
    };

    private void addMarker(double v, double v1) {
        // 定义Maker坐标点
        LatLng point = new LatLng(v, v1);
        // 构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(this.getActivity(),
                R.drawable.icon_gcoding);
        // 构建MarkerOption，用于在地图上添加Marker
        MarkerOptions markerOption = new MarkerOptions().position(point) // 必传参数
                .icon(bitmap);    // 必传参数
        // 在地图上添加Marker，并显示
        mMarker = BDAutoMapFactory.getAutoMapManager().addSDKOverlayItem(markerOption);
    }

    private void showMapBoundArea(double v, double v1) {
        /**
         * 右上角坐标值 > 左下角坐标值
         * 经纬度坐标系中，(0,0)坐标点的右侧和上侧的值是逐渐趋大的，类似于数学坐标系，不同于手机屏幕坐标系
         */
        Point point1 = new Point(v, v1);
        Point point2 = new Point(v, v1);

        /**
         * 右上角坐标值 > 左下角坐标值
         * 经纬度坐标系中，(0,0)坐标点的右侧和上侧的值是逐渐趋大的，类似于数学坐标系，不同于手机屏幕坐标系
         */
        double leftBottomX = Math.min(point1.getDoubleX(), point2.getDoubleX());
        double leftBottomY = Math.min(point1.getDoubleY(), point2.getDoubleY());

        double rightTopX = Math.max(point1.getDoubleX(), point2.getDoubleX());
        double rightTopY = Math.max(point1.getDoubleY(), point2.getDoubleY());

        AutoMapBound mapBound = new AutoMapBound(leftBottomX, leftBottomY, rightTopX, rightTopY);
        // 默认宽高中最小 - 横屏翻转
        int screenHeight = ScreenUtil.getInstance().getAbsoluteWidth();
        int screenWidth = ScreenUtil.getInstance().getAbsoluteHeight();

        // 右下角屏幕显示区域
        Rect screenRect = new Rect(screenWidth / 4, screenHeight / 6,
                screenWidth, screenHeight);

        /**
         * 注：得到的MapStatus的offset 不再是屏幕的中心点，而变成了Rect区域的中心点
         * 使用完此功能后，务必将MapStatus的offset再置为(0,0)点，即屏幕中心点
         */
        st = BDAutoMapFactory.getAutoMapManager().calcMapStatusByBounds(mapBound,
                screenRect);
        st.animationTime = 1000;

        BDAutoMapFactory.getAutoMapManager().setAutoMapStatus(st);
    }

    @Override
    public boolean onBackPressed() {
        NavHostFragment.findNavController(PoiDetailsFragment.this).popBackStack();
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        poiSearch.destroy();
        if (mMarker != null) {
            mMarker.remove();
        }
        if (st != null){
            st.xOffset = 0;
            st.yOffset = 0;
            st.animationTime = 0;
        }
        BDAutoMapFactory.getAutoMapManager().setAutoMapStatus(st);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void eventBusMessage(MessageEvent myMessage){

        if (myMessage.message == true){
            onBackPressed();
        }

    }
}