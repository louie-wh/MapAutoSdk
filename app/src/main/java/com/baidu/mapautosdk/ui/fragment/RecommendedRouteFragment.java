package com.baidu.mapautosdk.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapautosdk.MessageEvent;
import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.base.CoordType;
import com.baidu.mapautosdk.api.navi.AutoNaviCommonParams;
import com.baidu.mapautosdk.api.route.AutoRoutePlanNode;
import com.baidu.mapautosdk.api.route.DrivingRoutePlanOption;
import com.baidu.mapautosdk.api.route.DrivingRoutePlanPoiOption;
import com.baidu.mapautosdk.api.route.DrivingRoutePoiResult;
import com.baidu.mapautosdk.api.route.DrivingRouteResult;
import com.baidu.mapautosdk.api.route.OnGetRoutePlanResultListener;
import com.baidu.mapautosdk.api.route.OnRouteClickListener;
import com.baidu.mapautosdk.api.route.RouteLine;
import com.baidu.mapautosdk.api.route.RouteSortManager;
import com.baidu.mapautosdk.bean.AutoRouteSearchPoi;
import com.baidu.mapautosdk.interfaces.IAutoCommonConfig;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviSettingParams;
import com.baidu.mapautosdk.interfaces.overlay.IAutoItemizedOverlayManager;
import com.baidu.mapautosdk.interfaces.route.IAutoRoutPlanManager;
import com.baidu.mapautosdk.interfaces.route.IAutoRouteResultManager;
import com.baidu.mapautosdk.model.AutoLocData;
import com.baidu.mapautosdk.model.CityWeather;
import com.baidu.mapautosdk.platform.route.model.CarPassCityInfo;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.ui.adapter.RecommendedRouteAdapter;
import com.baidu.mapautosdk.ui.presenter.RecommendedRoutePresenter;
import com.baidu.mapautosdk.WaypointBean;
import com.baidu.mapautosdk.view.RouteCarNearbySearchPopup;
import com.baidu.mapautosdk.view.WaypointDeletePopup;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.MapSurfaceView;
import com.baidu.platform.comapi.map.OverlayItem;
import com.baidu.tts.tools.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecommendedRouteFragment extends BaseFragment {

    private ViewGroup mContentView;
    private String poiName;
    private String subTitle;
    private IAutoRoutPlanManager mSearch;
    private DrivingRoutePlanOption routePlanOption;
    private Handler mHandler;
    private RecommendedRoutePresenter recommendedRoutePresenter;
    AutoRoutePlanNode stNode = null;
    AutoRoutePlanNode enNode = null;
    private boolean isBackFromNavi = false;
    private View mView;
    private AutoLocData curLocation;
    private double latitude;
    private double longitude;
    private String mPoiUid;
    // 途径点集合
    ArrayList<AutoRoutePlanNode> autoRoutePlanNodesList = new ArrayList<>();
    private AutoRouteSearchPoi mAutoRouteSearchPoi;
    private OverlayItem mWaypointLabel;
    private int mWaypointIndex;
    private TextView startNavigation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler(Looper.getMainLooper());
        Bundle arguments = getArguments();
        poiName = arguments.getString("poiName");
        mPoiUid = arguments.getString("poiUid");
        subTitle = arguments.getString("subTitle");
        latitude = arguments.getDouble("latitude", 0);
        longitude = arguments.getDouble("longitude", 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        boolean registered = EventBus.getDefault().isRegistered(this);
        if (registered == false){
            EventBus.getDefault().register(this);
        }
        EventBus.getDefault().postSticky(new MessageEvent(false));

        BDAutoMapFactory.getAutoRouteResultManager().onCreate(getContext());
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_recommended_route, container, false);
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
        // 关闭本页面
        view.findViewById(R.id.recommended_route_img_back).setOnClickListener(clickListener);
        // 增加途经点
        view.findViewById(R.id.recommended_route_tv_add_passing_point).setOnClickListener(clickListener);
        TextView recommendedRouteEt = view.findViewById(R.id.recommended_route_tv);
        // 路线偏好
        view.findViewById(R.id.recommended_route_tv_lxph).setOnClickListener(clickListener);
        // 开始导航
        startNavigation = view.findViewById(R.id.recommended_route_tv_start_navigation);
        startNavigation.setOnClickListener(clickListener);
        recommendedRouteEt.setText(Html.fromHtml(poiName));
        // 路线全览
        view.findViewById(R.id.recommended_route_rl_location).setOnClickListener(clickListener);

        recommendedRoutePresenter = new RecommendedRoutePresenter();

        curLocation = BDAutoMapFactory.getAutoLocationManager().getCurLocation(CoordType.BD09LL);
        stNode = new AutoRoutePlanNode.Builder().name(curLocation.addr)
                .latitude(curLocation.latitude).longitude(curLocation.longitude)
                .build();

//        stNode = new AutoRoutePlanNode.Builder()
//                .name("百度大厦")
//                .latitude(40.056681).longitude(116.307539)
//                .build();
        if (latitude == 0 || longitude == 0){
            enNode= new AutoRoutePlanNode.Builder().name(Html.fromHtml(poiName).toString())
                    /*.latitude(latitude).longitude(longitude)*/
                    .id(mPoiUid)
                    .build();
        }else {
            enNode= new AutoRoutePlanNode.Builder().name(poiName)
                    .latitude(latitude).longitude(longitude)
                    .id(mPoiUid)
                    .build();
//            enNode= new AutoRoutePlanNode.Builder().name("中轻大厦-停车场")
//                    .latitude(40.01129745339661).longitude(116.49695503498847).id("e9bafcd54411dc815d17c3e7")
//                    .build();
        }
        if (routePlanOption == null) {
            routePlanOption = new DrivingRoutePlanOption();
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setScreenShow();
            }
        });

        boolean isRoute = SharedPreferencesUtils.getBoolean(getActivity(), "isRoute", false);
        if (isRoute){
            planRoute(null, false);
            SharedPreferencesUtils.putBoolean(getActivity(), "isRoute", false);
        }

        BDAutoMapFactory.getAutoItemizedOverlayManager().setOnTapClick(
                new IAutoItemizedOverlayManager.OnTapListener() {
                    @Override
                    public boolean onTap(int i) {
                        if (autoRoutePlanNodesList.size() > 0 && autoRoutePlanNodesList != null) {
                            autoRoutePlanNodesList.remove(mWaypointIndex);
                        }
                        planRoute(mAutoRouteSearchPoi, true);
                        BDAutoMapFactory.getAutoItemizedOverlayManager().removeItem(mWaypointLabel);
                        BDAutoMapFactory.getAutoItemizedOverlayManager().refresh();
                        return false;
                    }

                    @Override
                    public boolean onTap(@Nullable GeoPoint geoPoint, @Nullable MapSurfaceView mapSurfaceView) {
                        return false;
                    }

                    @Override
                    public boolean onTap(int index, int i1, @Nullable GeoPoint geoPoint) {
                        AutoRoutePlanNode build = new AutoRoutePlanNode.Builder()
                                .name(mAutoRouteSearchPoi.getName())
                                .latitude(mAutoRouteSearchPoi.getGuidePoint().latitude)
                                .longitude(mAutoRouteSearchPoi.getGuidePoint().longitude)
                                .build();
                        autoRoutePlanNodesList.add(build);
                        planRoute(mAutoRouteSearchPoi, true);
                        BDAutoMapFactory.getAutoItemizedOverlayManager().clearItems();
                        BDAutoMapFactory.getAutoRouteResultManager().showBkgLayer(false);
                        return false;
                    }
                });
        // 沿途poi搜索结果点击
        BDAutoMapFactory.getAutoRouteResultManager().setAlongPoiSearchResultClickListener(
                        new IAutoRouteResultManager.IAlongPoiSearchResultClickedListener() {
                    @Override
                    public void onAlongPoiSearchResultClicked(int i, AutoRouteSearchPoi autoRouteSearchPoi) {
                        RouteCarNearbySearchPopup routeCarNearbySearchPopup = new RouteCarNearbySearchPopup(
                                getActivity());
                        routeCarNearbySearchPopup.setPoiName(autoRouteSearchPoi.getName());
                        routeCarNearbySearchPopup.setPoiInfo(autoRouteSearchPoi.getRouteCost());
                        OverlayItem routeNearbySearchLabel = getRouteNearbySearchLabel(
                                routeCarNearbySearchPopup, autoRouteSearchPoi);
                        routeNearbySearchLabel.setCoordType(OverlayItem.CoordType.CoordType_BD09LL);
                        BDAutoMapFactory.getAutoItemizedOverlayManager().clearItems();
                        BDAutoMapFactory.getAutoItemizedOverlayManager().addItem(routeNearbySearchLabel);
                        BDAutoMapFactory.getAutoItemizedOverlayManager().show();
                        mAutoRouteSearchPoi = autoRouteSearchPoi;
                    }
                });

        // 途径点点击事件
        BDAutoMapFactory.getAutoRouteResultManager().setOnThroughNodeClickListener(
                new IAutoRouteResultManager.IThroughNodeClickedListener() {
            @Override
            public void onThroughNodeClicked(int index, AutoRoutePlanNode node) {
                WaypointDeletePopup waypointDeletePopup = new WaypointDeletePopup(
                        getActivity());
                waypointDeletePopup.setPoiName(node.getName());
                mWaypointLabel = getWaypointLabel(waypointDeletePopup, node);
                mWaypointLabel.setCoordType(OverlayItem.CoordType.CoordType_BD09LL);
                BDAutoMapFactory.getAutoItemizedOverlayManager().clearItems();
                BDAutoMapFactory.getAutoItemizedOverlayManager().addItem(mWaypointLabel);
                BDAutoMapFactory.getAutoItemizedOverlayManager().show();
                mWaypointIndex = index;
            }
        });
    }

    // 通过view创建OverlayItem
    private OverlayItem getRouteNearbySearchLabel(RouteCarNearbySearchPopup routeNearbySearchPopup,
                                                  AutoRouteSearchPoi node) {
        GeoPoint geoPoint = new GeoPoint(node.getGuidePoint().latitude, node.getGuidePoint().longitude);
        OverlayItem item = new OverlayItem(geoPoint, node.getName(), "");
        item.setAnchor(0.5f, 1); // 以矩形下边线中间位置作为anchor
        item.setAnimateEffect(OverlayItem.AnimEffect.GROWTH);

        routeNearbySearchPopup.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        routeNearbySearchPopup
                .layout(0, 0, routeNearbySearchPopup.getMeasuredWidth(), routeNearbySearchPopup.getMeasuredHeight());
        routeNearbySearchPopup.buildDrawingCache();
        Bitmap bitmap = routeNearbySearchPopup.getDrawingCache();
        item.addClickRect(routeNearbySearchPopup.getLeftContentSizeBundle());
        item.addClickRect(routeNearbySearchPopup.getRightContentSizeBundle());

        if (bitmap != null) {
            Drawable drawable = new BitmapDrawable(bitmap);
            routeNearbySearchPopup.setDrawingCacheEnabled(false);
            item.setMarker(drawable);
            return item;
        }

        return null;
    }

    // 通过view创建OverlayItem
    private OverlayItem getWaypointLabel(WaypointDeletePopup waypointDeletePopup, AutoRoutePlanNode node) {
        GeoPoint geoPoint = new GeoPoint(node.getLatitude(), node.getLongitude());
        OverlayItem item = new OverlayItem(geoPoint, node.getName(), "");
        item.setAnchor(0.5f, 1); // 以矩形下边线中间位置作为anchor
        item.setAnimateEffect(OverlayItem.AnimEffect.GROWTH);

        waypointDeletePopup.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        waypointDeletePopup
                .layout(0, 0, waypointDeletePopup.getMeasuredWidth(), waypointDeletePopup.getMeasuredHeight());
        waypointDeletePopup.buildDrawingCache();
        Bitmap bitmap = waypointDeletePopup.getDrawingCache();

        if (bitmap != null) {
            Drawable drawable = new BitmapDrawable(bitmap);
            waypointDeletePopup.setDrawingCacheEnabled(false);
            item.setMarker(drawable);
            return item;
        }

        return null;
    }

    // 路线
    private void planRoute(AutoRouteSearchPoi autoRouteSearchPoi, boolean isWayPoint) {

        mSearch = BDAutoMapFactory.getAutoRoutePlanManager();
        if (stNode != null) {
            routePlanOption.from(stNode);
        }
        if (enNode != null) {
            routePlanOption.to(enNode);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(AutoNaviCommonParams.RoutePlanKey.VEHICLE_TYPE,
                IAutoCommonConfig.Vehicle.TRUCK);
        routePlanOption.extra(bundle);
        routePlanOption.carPlate("京A88789");
        int routeSortMode = BDAutoMapFactory.getNaviCommonSettingManager().getRouteSortMode();
        Log.d("routePlanOption", "planRoute: routeSortMode = " + routeSortMode);
        routePlanOption.prefer(routeSortMode);
//        routePlanOption.prefer(IAutoRoutPlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_ECONOMIC_ROUTE);
        if (isWayPoint == true){
            routePlanOption.through(autoRoutePlanNodesList);
        }
        mSearch.setOnGetRoutePlanResultListener(routePlanResultListener);
//        routePlanOption.offlineCalc(true);
        mSearch.routePlan(routePlanOption);

        mSearch.setRouteSelectListener(new OnRouteClickListener() {
            @Override
            public void onRouteClick(int i) {
                mSearch.selectRoute(i);
                recommendedRoutePresenter.selectRoute(i);
            }
        });

    }

    // 添加途经点之后算路
    public void routePoi(String keyword) {
        DrivingRoutePlanPoiOption option = new DrivingRoutePlanPoiOption();
        option.keyword(keyword);
        BDAutoMapFactory.getAutoRouteResultManager().alongPoiSearch(option,
                new IAutoRouteResultManager.AlongPoiSearchListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(DrivingRoutePoiResult drivingRoutePoiResult) {
                        if (drivingRoutePoiResult.isSuccessful()){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setScreenShow();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailed() {

                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void eventBusWaypoint(WaypointBean keyword){
        routePoi(keyword.getKeyword());
    }

    private OnGetRoutePlanResultListener routePlanResultListener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            if (drivingRouteResult.isSuccessful()) {
                if (drivingRouteResult.isRouteLine()) {
                    if (drivingRouteResult.getRouteLines().size() > 0) {
                        startNavigation.setVisibility(View.VISIBLE);
                        final List<RouteLine> routeLines = drivingRouteResult.getRouteLines();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
//                                setScreenShow();
                                showRoute(routeLines);
//                                longDistanceSearch();
                            }
                        });
                    }
                } else if (drivingRouteResult.isShowCitySelect()) {
                    // 有城市先选择城市 传入城市id 再调起算路
                    // 0起点 1终点 2途经点 drivingRouteResult.nodeType
                    Toast.makeText(getActivity(), "type:" + drivingRouteResult.nodeType
                            + "size:" + drivingRouteResult.getCityList().size(), Toast.LENGTH_LONG).show();
                } else if (drivingRouteResult.isAddrList()) {
                    // 0起点 1终点 2途经点 drivingRouteResult.nodeType
                    Toast.makeText(getActivity(), "type:" + drivingRouteResult.nodeType
                            + "size:" + drivingRouteResult.getAddrList().size(), Toast.LENGTH_LONG).show();
                }
                String limInfo = drivingRouteResult.getLimInfo();
                Toast.makeText(getActivity(), limInfo, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "算路失败" + drivingRouteResult.getErrorCode(),
                        Toast.LENGTH_SHORT).show();
            }

        }
    };

    // 调整蚯蚓条显示区域
    public void setScreenShow() {
        int top = 48;     // 距离屏幕上边缘48dp
        int bottom = 48;  // 距离屏幕底部48dp
        int left = 460;
        int right = 48;
        BDAutoMapFactory.getAutoMapManager().setAutoScreenShow(left, top, right, bottom);
//        BDAutoMapFactory.getNaviRouteResultSettingManager().setRouteMargin(360, 48, 48, 48);
    }

    private void showRoute(List<RouteLine> routeLines) {
        updateBottomBar(routeLines, true);
        updateRouteSort();
    }

    private void updateBottomBar(List<RouteLine> routeLines, boolean isFromPlan) {
        recommendedRoutePresenter.initRouteCarPlanView(mView, routeLines);
        recommendedRoutePresenter.setRouteSelectListener(new RecommendedRoutePresenter.RouteSelectListener() {
            @Override
            public void onSelectRoute(int index) {
                if (mSearch == null) {
                    mSearch = BDAutoMapFactory.getAutoRoutePlanManager();
                }
                mSearch.selectRoute(index);

            }
        });
        recommendedRoutePresenter.setItemOnclickListener(new RecommendedRouteAdapter.ItemOnclickListener() {
            @Override
            public void itemClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("poiName", poiName);
                NavHostFragment.findNavController(RecommendedRouteFragment.this)
                        .navigate(R.id.action_routePlanFragment_to_simulationNaviFragment, bundle);
            }
        });
    }

    private void updateRouteSort() {
        String str = RouteSortManager.getInstance().getCurrentRouteSortName();
        if (TextUtils.isEmpty(str)) {
//            tvRouteSort.setText("智能推荐");
        } else {
//            tvRouteSort.setText(str);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            switch (v.getId()) {
                case R.id.recommended_route_img_back:
                    BDAutoMapFactory.getAutoItemizedOverlayManager().clearItems();
                    onBackPressed();
                    break;
                case R.id.recommended_route_tv_add_passing_point:
                    BDAutoMapFactory.getAutoItemizedOverlayManager().clearItems();
                    NavHostFragment.findNavController(RecommendedRouteFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_addWaypointFragment);
                    break;
                case R.id.recommended_route_tv_lxph:
                    BDAutoMapFactory.getAutoItemizedOverlayManager().clearItems();
                    NavHostFragment.findNavController(RecommendedRouteFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_routePreferenceFragment, bundle);
                    break;
                case R.id.recommended_route_tv_start_navigation:
                    BDAutoMapFactory.getAutoItemizedOverlayManager().clearItems();
                    bundle.putString("endNode", poiName);
                    bundle.putInt("naviType", 1);
                    NavHostFragment.findNavController(RecommendedRouteFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_demoGuideFragment, bundle);

//                    NavHostFragment.findNavController(RecommendedRouteFragment.this)
//                            .navigate(R.id.action_routePlanFragment_to_demoCustomUiFragment, bundle);
                    break;
                case R.id.recommended_route_rl_location:
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setScreenShow();
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onBackPressed() {
        autoRoutePlanNodesList.clear();
        BDAutoMapFactory.getAutoRoutePlanManager().clearRestrictedArea();
        NavHostFragment.findNavController(RecommendedRouteFragment.this).popBackStack();
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        BDAutoMapFactory.getAutoItemizedOverlayManager().clearItems();
        BDAutoMapFactory.getAutoRouteResultManager().onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        BDAutoMapFactory.getAutoItemizedOverlayManager().clearItems();
        BDAutoMapFactory.getAutoRouteResultManager().onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        BDAutoMapFactory.getAutoItemizedOverlayManager().clearItems();
        isBackFromNavi = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BDAutoMapFactory.getAutoItemizedOverlayManager().clearItems();
        BDAutoMapFactory.getAutoRouteResultManager().onDestroy();
        autoRoutePlanNodesList.clear();
//        BDAutoMapFactory.getAutoDayNightManager().restoreMapTheme();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void eventBusMessage(MessageEvent myMessage){

        if (myMessage.message == true){
            onBackPressed();
        }

    }

    private void longDistanceSearch() {
        try {
            // 长距离算路成功后才可以调用 如果不是长距离返回null
            Map<Integer, Object> resultOri = BDAutoMapFactory.getAutoRouteResultManager().getPassCityInfo();
            if (resultOri != null) {
                // IAutoRouteResultManager.CAR_CITY_TYPE途径市、CAR_ROUTE_TYPE途径路、CAR_SERVICE_TYPE途径服务区
                // CarPassCityInfo \ CarPassRouteInfo \ CarPassServiceInfo
                if (resultOri.get(IAutoRouteResultManager.CAR_CITY_TYPE) != null) {
                    SparseArray<ArrayList<CarPassCityInfo>> carPassCity =
                            (SparseArray<ArrayList<CarPassCityInfo>>) resultOri.get(
                                    IAutoRouteResultManager.CAR_CITY_TYPE);
                    for (int i = 0; i < carPassCity.size(); i++) {
                        ArrayList<CarPassCityInfo> cityInfos = carPassCity.get(i);
                        for (int j = 0; j < cityInfos.size(); j++) {
                            cityInfos.stream().forEach(
                                    carPassCityInfo -> Log.i("CarPassCityInfo", carPassCityInfo.toString()));
                        }
                    }
                }
            }
        } catch (com.baidu.mapautosdk.exception.RouteResultException e) {
            e.printStackTrace();
        }
    }

    private void longDistanceWeatherSearch() {
        // 获取沿途天气
        String cityIdStr = "119|340"; // 城市id 从沿途市、路、服务区返回的CarPassCityInfo mCityCode | 拼接
        String arrStr = "3411|3427"; // 到达时间字符串 CarPassCityInfo mArriveTime
        BDAutoMapFactory.getAutoRouteResultManager().fetchWeather(cityIdStr, arrStr,
                new IAutoRouteResultManager.IOnWeatherResult() {
                    @Override
                    public void onResult(List<CityWeather> list) {
                        CityWeather cityWeather = list.get(0);
                    }
                });
    }

}