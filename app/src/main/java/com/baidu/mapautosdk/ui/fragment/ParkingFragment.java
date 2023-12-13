package com.baidu.mapautosdk.ui.fragment;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapautosdk.MessageEvent;
import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.base.CoordType;
import com.baidu.mapautosdk.api.map.AutoMapBound;
import com.baidu.mapautosdk.api.map.AutoMapStatus;
import com.baidu.mapautosdk.api.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapautosdk.api.search.poi.PoiDetailSearchResult;
import com.baidu.mapautosdk.api.search.poi.PoiNearbySearchOption;
import com.baidu.mapautosdk.api.search.poi.PoiResult;
import com.baidu.mapautosdk.bean.AutoPoiInfo;
import com.baidu.mapautosdk.interfaces.overlay.IAutoItemizedOverlayManager;
import com.baidu.mapautosdk.interfaces.search.IAutoPoiSearchManager;
import com.baidu.mapautosdk.model.AutoLocData;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.ui.adapter.ParkingAdapter;
import com.baidu.mapautosdk.ui.presenter.ParkingPresenter;
import com.baidu.mapautosdk.util.ScreenUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.MapSurfaceView;
import com.baidu.platform.comapi.map.OverlayItem;
import com.baidu.platform.comapi.sdk.mapapi.model.LatLng;
import com.baidu.tts.tools.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ParkingFragment extends BaseFragment {
    private ViewGroup mContentView;
    private TextView parkingEt;
    private IAutoPoiSearchManager poiSearch;
    private ParkingPresenter parkingPresenter;
    private View mView;
    private List<AutoPoiInfo> allPoi;
    private int cityId;
    private int poiType;
    private AutoMapStatus st;
    private List<String> poiNameList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        poiType = getArguments().getInt("poiType");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boolean registered = EventBus.getDefault().isRegistered(this);
        if (registered == false){
            EventBus.getDefault().register(this);
        }

        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_parking, container, false);
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

        mView = view;

        cityId = SharedPreferencesUtils.getInt(getActivity(), "cityId", 131);
        AutoLocData curLocation =
                BDAutoMapFactory.getAutoLocationManager().getCurLocation(CoordType.BD09LL);

        parkingPresenter = new ParkingPresenter();

        view.findViewById(R.id.parking_img_back).setOnClickListener(clickListener);
        parkingEt = view.findViewById(R.id.parking_et);

        poiSearch = BDAutoMapFactory.getAutoPoiSearchManager();
        poiSearch.setOnGetPoiSearchResultListener(listener);
        if (poiType == 1){
//            poiSearch.searchNearby(new PoiNearbySearchOption().city(cityId).keyword("停车场")
//                    .location(new LatLng(curLocation.latitude, curLocation.longitude)));
            poiSearch.searchNearby(new PoiNearbySearchOption().keyword("西青智能网连测试场")
                    .location(new LatLng(39.01543, 116.462139))
                    .radius(5000).pageSize(10).pageNum(0));
            parkingEt.setText("停车场");
        }else if (poiType == 2){
            poiSearch.searchNearby(new PoiNearbySearchOption().city(cityId).keyword("加油站")
                    .location(new LatLng(curLocation.latitude, curLocation.longitude)));
            parkingEt.setText("加油站");
        }else if (poiType == 3){
            poiSearch.searchNearby(new PoiNearbySearchOption().city(cityId).keyword("洗车")
                    .location(new LatLng(curLocation.latitude, curLocation.longitude)));
            parkingEt.setText("洗车");
        }

        BDAutoMapFactory.getAutoItemizedOverlayManager().setOnTapClick(
                new IAutoItemizedOverlayManager.OnTapListener() {

                    private double longitude;
                    private double latitude;

                    @Override
                    public boolean onTap(int i) {
                        SharedPreferencesUtils.putBoolean(getActivity(), "isRoute", true);
                        Bundle bundle = new Bundle();
                        bundle.putString("poiName", poiNameList.get(i));
                        bundle.putDouble("latitude" , latitude);
                        bundle.putDouble("longitude" , longitude);
                        NavHostFragment.findNavController(ParkingFragment.this)
                                .navigate(R.id.action_routePlanFragment_to_recommendedRouteFragment, bundle);
                        return true;
                    }

                    @Override
                    public boolean onTap(@Nullable GeoPoint geoPoint, @Nullable MapSurfaceView mapSurfaceView) {
                        latitude = geoPoint.getLatitude();
                        longitude = geoPoint.getLongitude();
                        return false;
                    }

                    @Override
                    public boolean onTap(int i, int i1, @Nullable GeoPoint geoPoint) {
                        return false;
                    }
                });
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.parking_img_back:
                    onBackPressed();
                    break;
                default:
                    break;
            }
        }
    };

    OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult.isSuccessful()) {
                allPoi = poiResult.getAllPoi();
                for (AutoPoiInfo autoPoiInfo : allPoi) {
                    addMarker(autoPoiInfo.location.latitude,
                            autoPoiInfo.location.longitude,
                            autoPoiInfo.name);
                }
                showMapBoundArea(allPoi);
                parkingPresenter.initListView(mView, allPoi);
                parkingPresenter.setItemOnclickListener(new ParkingAdapter.ItemOnclickListener() {
                    @Override
                    public void itemClick(View v) {
                        int position;
                        position = (int) v.getTag();
                        AutoPoiInfo autoPoiInfo = allPoi.get(position);
                        Bundle bundle = new Bundle();
                        switch (v.getId()){
                            case R.id.item_parking_rl_go_poi:

                                EventBus.getDefault().postSticky(new MessageEvent(false));
                                String uid = autoPoiInfo.uid;
                                String poiNamePoi = autoPoiInfo.name;
                                bundle.putString("uid", uid);
                                bundle.putInt("cityId", cityId);
                                bundle.putString("poiName", poiNamePoi);
                                NavHostFragment.findNavController(ParkingFragment.this)
                                        .navigate(R.id.action_routePlanFragment_to_poiDetailsFragment, bundle);

                                break;
                            case R.id.item_parking_img_go_here:

                                EventBus.getDefault().postSticky(new MessageEvent(false));
                                String poiName = autoPoiInfo.name;
                                bundle.putString("poiName", poiName);
                                bundle.putDouble("latitude" , autoPoiInfo.location.latitude);
                                bundle.putDouble("longitude" , autoPoiInfo.location.longitude);
                                SharedPreferencesUtils.putBoolean(getActivity(), "isRoute", true);
                                NavHostFragment.findNavController(ParkingFragment.this)
                                        .navigate(R.id.action_routePlanFragment_to_recommendedRouteFragment, bundle);

                                break;
                            default:
                                break;
                        }
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Fail error = " + poiResult.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            Toast.makeText(getActivity(), "poiDetailSearchResult", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onBackPressed() {
        NavHostFragment.findNavController(ParkingFragment.this).popBackStack();
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (st != null){
            st.xOffset = 0;
            st.yOffset = 0;
            st.animationTime = 0;
        }
        BDAutoMapFactory.getAutoMapManager().setAutoMapStatus(st);
        BDAutoMapFactory.getAutoItemizedOverlayManager().clearItems();
        BDAutoMapFactory.getAutoItemizedOverlayManager().refresh();
        poiNameList.clear();
    }

    // 添加marker
    private void addMarker(double v, double v1, String name) {
        poiNameList.add(name);
        // 在地图上添加Marker，并显示
        GeoPoint geoPoint = new GeoPoint(v, v1);
        OverlayItem item = new OverlayItem(geoPoint, name, "");
        if (poiType == 1){
            item.setMarker(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_parking));
        }else if (poiType == 2){
            item.setMarker(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_gas_station));
        }else if (poiType == 3){
            item.setMarker(ContextCompat.getDrawable(getActivity(), R.mipmap.ic_car_wash));
        }
        item.setAnchor(0.5f, 1); // 以矩形下边线中间位置作为anchor
        item.setAnimateEffect(OverlayItem.AnimEffect.GROWTH);
//        item.setAnimateEffect(OverlayItem.AnimEffect.ANCHOR_GROUTH);
//        item.setAnimateDuration(2000);
        item.setCoordType(OverlayItem.CoordType.CoordType_BD09LL);
        BDAutoMapFactory.getAutoItemizedOverlayManager().addItem(item);
        BDAutoMapFactory.getAutoItemizedOverlayManager().show();
    }

    /**
     * 在指定的区域显示
     * @param list
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showMapBoundArea(List<AutoPoiInfo> list) {
        /**
        * 右上角坐标值 > 左下角坐标值
        * 经纬度坐标系中，(0,0)坐标点的右侧和上侧的值是逐渐趋大的，类似于数学坐标系，不同于手机屏幕坐标系
        */
        double leftBX = list.stream().map(point -> ((double)
                point.getLocation().longitude)).min(Comparator.comparingDouble(o -> o)).get();
        double leftBY = list.stream().map(point -> ((double)
                point.getLocation().latitude)).min(Comparator.comparingDouble(o -> o)).get();
        double rightTX = list.stream().map(point -> ((double)
                point.getLocation().longitude)).max(Comparator.comparingDouble(o -> o)).get();
        double rightTY = list.stream().map(point -> ((double)
                point.getLocation().latitude)).max(Comparator.comparingDouble(o -> o)).get();

        AutoMapBound mapBound = new AutoMapBound(leftBX, leftBY, rightTX, rightTY);
        // 默认宽高中最小 - 横屏翻转
        int screenHeight = ScreenUtil.getInstance().getAbsoluteWidth();
        int screenWidth = ScreenUtil.getInstance().getAbsoluteHeight();

        // 右下角屏幕显示区域
        Rect screenRect = new Rect(screenWidth / 2, screenHeight / 4,
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void eventBusMessage(MessageEvent myMessage){

        if (myMessage.message == true){
            onBackPressed();
        }

    }
}