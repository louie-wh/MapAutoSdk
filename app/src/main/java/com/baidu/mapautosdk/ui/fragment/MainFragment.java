package com.baidu.mapautosdk.ui.fragment;

import static com.baidu.mapautosdk.api.BDAutoMapFactory.getAutoGeoCoderManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;

import org.greenrobot.eventbus.EventBus;

import com.baidu.mapautosdk.MessageEvent;
import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.BDAutoMapInitializer;
import com.baidu.mapautosdk.api.base.CoordType;
import com.baidu.mapautosdk.api.coordinate.util.CoordinateUtil;
import com.baidu.mapautosdk.api.coordinate.util.DistanceUtil;
import com.baidu.mapautosdk.api.location.AutoLocationChangeListener;
import com.baidu.mapautosdk.api.location.AutoLocationOption;
import com.baidu.mapautosdk.api.map.AutoMapBound;
import com.baidu.mapautosdk.api.map.AutoMapStatus;
import com.baidu.mapautosdk.api.route.AutoRoutePlanNode;
import com.baidu.mapautosdk.api.search.geo.OnGetGeoCoderResultListener;
import com.baidu.mapautosdk.api.search.geo.ReverseGeoCodeResult;
import com.baidu.mapautosdk.bean.adapter.AutoBNTruckInfo;
import com.baidu.mapautosdk.bean.adapter.AutoVehicleConstant;
import com.baidu.mapautosdk.cluster.Cluster;
import com.baidu.mapautosdk.cluster.ClusterItem;
import com.baidu.mapautosdk.cluster.ClusterManager;
import com.baidu.mapautosdk.interfaces.IAutoCommonConfig;
import com.baidu.mapautosdk.interfaces.map.IAutoMapLevelListener;
import com.baidu.mapautosdk.interfaces.map.IAutoMapListener;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviSettingManager;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviSettingParams;
import com.baidu.mapautosdk.interfaces.theme.IAutoDayNightManager;
import com.baidu.mapautosdk.interfaces.ugc.IAutoUgcManager;
import com.baidu.mapautosdk.model.AutoHistorySearchInfo;
import com.baidu.mapautosdk.model.AutoLocData;
import com.baidu.mapautosdk.model.AutoMapPoiObj;
import com.baidu.mapautosdk.model.MapLevelModel;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.ui.cruiser.AutoCruiserControls;
import com.baidu.mapautosdk.util.BitmapUtil;
import com.baidu.mapautosdk.util.ScreenUtil;
import com.baidu.mapautosdk.util.SpUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import com.baidu.platform.comapi.basestruct.Point;
import com.baidu.platform.comapi.sdk.map.BitmapDescriptor;
import com.baidu.platform.comapi.sdk.map.BitmapDescriptorFactory;
import com.baidu.platform.comapi.sdk.map.InfoWindow;
import com.baidu.platform.comapi.sdk.map.MarkerOptions;
import com.baidu.platform.comapi.sdk.map.SDKOverlay;
import com.baidu.platform.comapi.sdk.mapapi.model.LatLng;
import com.baidu.tts.tools.SharedPreferencesUtils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.fragment.NavHostFragment;

public class MainFragment extends BaseFragment {

    private ViewGroup mContentView;
    private Handler mainHandler;

    private SDKOverlay mMarker0 = null;
    private SDKOverlay mMarker1 = null;
    private SDKOverlay mMarker2 = null;

    private Button mBtnMarker;
    private InfoWindow mInfoWindow = null;

    private AutoCruiserControls autoCruiserControls;

    private RelativeLayout rlAllUi;
    private LinearLayout llNavigationSound;
    private RelativeLayout rlPromptTone;
    private ImageView imgBeep;
    private TextView tvBeep;
    private RelativeLayout rlNavigationMute;
    private ImageView imgMute;
    private TextView tvMute;
    private RelativeLayout rlNavigationBroadcast;
    private ImageView imgBroadcast;
    private TextView tvBroadcast;
    private ImageView imgMore;
    private TextView tvMore;
    private ImageView imgHeadPortrait;

    private Bitmap iconBitmap;

    private AutoLocData curLocation;
    boolean isMainFragment = true;
    private long lastTouchTime;
    private Timer timer;
    private ClusterManager<MyItem> clusterManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initLocation();
//        BDAutoMapFactory.getAutoLocationManager().init();
        View mapView = BDAutoMapFactory.getAutoMapManager().getMapView();
        mapView.setPadding(0,0,250,30);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
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

        view.findViewById(R.id.btn_navi_search).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_navi_ui).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_route_plan).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_map_zoom_in).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_map_zoom_out).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_map_road_condition).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_select_point).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_map_tocenter).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_poi_search).setOnClickListener(clickListener);

        view.findViewById(R.id.btn_local_map).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_local_nav_map0).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_local_nav_map1).setOnClickListener(clickListener);

        view.findViewById(R.id.btn_local_map_call).setOnClickListener(clickListener);

        mBtnMarker = view.findViewById(R.id.btn_marker);
        mBtnMarker.setOnClickListener(clickListener);

        view.findViewById(R.id.btn_calc_distance).setOnClickListener(clickListener);

        view.findViewById(R.id.btn_show_area).setOnClickListener(clickListener);

        // 白天黑夜模式
        view.findViewById(R.id.btn_day).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_night).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_auto).setOnClickListener(clickListener);

        CheckBox infoWindow = view.findViewById(R.id.btn_InfoWindow);
        infoWindow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setText("InfoWin-Show");
                    hideInfoWindow();
                } else {
                    buttonView.setText("InfoWin-Hide");
                    showInfoWindow();
                }
            }
        });

        CheckBox convertScr2Loc = view.findViewById(R.id.btn_convert_scr);
        convertScr2Loc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setText("Screen->Loc");
                    GeoPoint geoPt = new GeoPoint(4846474, 12947471);
                    Point srcPt = new Point();
                    BDAutoMapFactory.getAutoMapManager().geoPtToScrPoint(geoPt, srcPt);

                    Toast.makeText(MainFragment.this.getActivity(),
                            "屏幕坐标 " + srcPt.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    buttonView.setText("Loc->Screen");
                    GeoPoint geoPoint = BDAutoMapFactory.getAutoMapManager().scrPtToGeoPoint(390
                            , 502);

                    Toast.makeText(MainFragment.this.getActivity(),
                            "地理坐标 " + geoPoint.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        CheckBox moveCenter = view.findViewById(R.id.btn_move_center);
        moveCenter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setText("移动中心");

                    /*
                     * 使用完此功能后，务必将MapStatus的offset再置为(0,0)点，即屏幕中心点
                     */
                    AutoMapStatus st = BDAutoMapFactory.getAutoMapManager().getAutoMapStatus();
                    st.xOffset = 200;        // 屏幕中心点右侧 200 pixels
                    st.yOffset = -200;       // 屏幕中心点上侧 200 pixels
                    st.animationTime = 500;  // 动画持续时间
                    BDAutoMapFactory.getAutoMapManager().setAutoMapStatus(st);

                    Toast.makeText(MainFragment.this.getActivity(),
                            "移动【地图中心点】在屏幕上显示位置 ", Toast.LENGTH_SHORT).show();
                } else {
                    buttonView.setText("恢复中心");

                    AutoMapStatus st = BDAutoMapFactory.getAutoMapManager().getAutoMapStatus();
                    st.xOffset = 0;
                    st.yOffset = 0;
                    st.animationTime = 500;
                    BDAutoMapFactory.getAutoMapManager().setAutoMapStatus(st);

                    Toast.makeText(MainFragment.this.getActivity(),
                            "恢复【地图中心点】在屏幕中心点显示 ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mainHandler = new Handler(Looper.getMainLooper());

        /*BDAutoMapFactory.getAutoMapManager().setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getContext(), "Marker On Clicked: " + marker.id,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });*/

        // 开启巡航播报
        BDAutoMapFactory.getCruiserManager().setEnable(true);
        if (autoCruiserControls == null) {
            autoCruiserControls = new AutoCruiserControls(view.findViewById(R.id.cruise_panel));
        }

        // 整体UI
        rlAllUi = view.findViewById(R.id.rl_all_ui);
        view.findViewById(R.id.rl_search).setOnClickListener(clickListener);
        // 仅提示音，导航静音
        llNavigationSound = view.findViewById(R.id.ll_navigation_sound);
        // 仅提示音
        rlPromptTone = view.findViewById(R.id.rl_prompt_tone);
        imgBeep = view.findViewById(R.id.img_beep);
        tvBeep = view.findViewById(R.id.tv_beep);
        // 导航静音
        rlNavigationMute = view.findViewById(R.id.rl_navigation_mute);
        imgMute = view.findViewById(R.id.img_mute);
        tvMute = view.findViewById(R.id.tv_mute);
        // 导航播报
        rlNavigationBroadcast = view.findViewById(R.id.rl_navigation_broadcast);
        imgBroadcast = view.findViewById(R.id.img_broadcast);
        tvBroadcast = view.findViewById(R.id.tv_broadcast);
        // 更多
        imgMore = view.findViewById(R.id.img_more);
        tvMore = view.findViewById(R.id.tv_more);
        // 仅提示音
        view.findViewById(R.id.rl_prompt_tone).setOnClickListener(clickListener);
        // 导航静音
        view.findViewById(R.id.rl_navigation_mute).setOnClickListener(clickListener);
        // 导航播报
        view.findViewById(R.id.rl_navigation_broadcast).setOnClickListener(clickListener);
        // 更多
        view.findViewById(R.id.rl_more).setOnClickListener(clickListener);

        // 放大
        view.findViewById(R.id.rl_map_zoom_in).setOnClickListener(clickListener);
        // 缩小
        view.findViewById(R.id.rl_map_zoom_out).setOnClickListener(clickListener);

        // 头像
        imgHeadPortrait = view.findViewById(R.id.img_head_portrait);
        imgHeadPortrait.setOnClickListener(clickListener);

        // 定位
        view.findViewById(R.id.main_rl_location).setOnClickListener(clickListener);
        // 回家
        view.findViewById(R.id.rl_go_home).setOnClickListener(clickListener);
        // 去公司
        view.findViewById(R.id.rl_go_company).setOnClickListener(clickListener);

        // 选择poi、长按地图选点
        BDAutoMapFactory.getAutoMapManager().setAutoMapListener(new IAutoMapListener() {
            @Override
            public void onMapTouch(MotionEvent motionEvent) {
                lastTouchTime = SystemClock.elapsedRealtime();
            }

            @Override
            public void onMapClick(LatLng latLng) {

            }

            @Override
            public void onMapPoiClick(List<AutoMapPoiObj> list) {
                if (isMainFragment == true) {
                    if (list != null) {
                        EventBus.getDefault().postSticky(new MessageEvent(false));
                        goToMapPoiDetailPage(list.get(0).getUid());
                    }
                }
            }

            @Override
            public void onMapLongClick(LatLng latLng) {
                if (isMainFragment == true) {
                    EventBus.getDefault().postSticky(new MessageEvent(false));
                    goToReGeoCoder(latLng);
                }
            }

            @Override
            public void onMapLoadFinish() {

            }

            @Override
            public void onMapStatusChangeFinish(AutoMapStatus autoMapStatus) {
                if (clusterManager != null) {
                    clusterManager.onMapStatusChangeFinish(autoMapStatus);
                }
                Log.d("AutoMapStatus", "onMapGestureListener: " + autoMapStatus.toString());
            }
        });

        BDAutoMapFactory.getAutoMapManager().addMapLevelObserver(new IAutoMapLevelListener() {
            @Override
            public void mapLevelObserverListener(MapLevelModel.MapLevel mapLevel) {
                Log.d("mainFragment", "mapLevelObserverListener: " + mapLevel.mapLevel);
                Log.d("mainFragment", "mapLevelObserverListener: " + mapLevel.level);
            }
        });

        String openid = SpUtil.getInStace().getString("openid", "");
        Log.d("main", "onViewCreated: " + openid);

        BDAutoMapFactory.getAutoUgcManager().requestHistorySearchInfo(openid,
                new IAutoUgcManager.OnHistorySearchResultListener() {
            @Override
            public void onGetHistorySearchResult(ArrayList<AutoHistorySearchInfo> info) {
                Log.d("main", "onGetHistorySearchResult: " + info.toString());
            }
        });

//        BDAutoMapFactory.getAutoTrafficManager().requestRoadTrafficSearchInfo(new RoadTrafficOption().
//                setRoadName("北二环").setCity("北京市"));
//        BDAutoMapFactory.getAutoTrafficManager().setOnRoadTrafficSearchResultListener(
//                new IAutoTrafficManager.OnRoadTrafficSearchResultListener() {
//            @Override
//            public void onGetRoadTrafficSearchResult(AutoTrafficSearchInfo info) {
//                Log.d("main", "onGetRoadTrafficSearchResult: " + info);
//            }
//        });

//        BDAutoMapFactory.getAutoTrafficManager().requestAroundTrafficSearchInfo(new AroundTrafficOption().
//                setLatLng(new LatLng(40.05548,116.307988)).setRadius(200).setCoordType(CoordType.BD09LL));
//        BDAutoMapFactory.getAutoTrafficManager().setOnAroundTrafficSearchResultListener(
//                new IAutoTrafficManager.OnAroundTrafficSearchResultListener() {
//            @Override
//            public void onGetAroundTrafficSearchResult(AutoTrafficSearchInfo info) {
//                Log.d("main", "onGetAroundTrafficSearchResult: " + info.toString());
//            }
//        });

        Point point = com.baidu.platform.comapi.location.CoordinateUtil.bd09mcTobd09ll(1.2969618949953E7, 4838255.656945);
        Point point1 = CoordinateUtil.gcj02ToBD09ll(117.301772, 38.860292);
        Log.d("MainFragemnt", "onViewCreated: " + point1);
    }

    private void goToMapPoiDetailPage(String strUid) {
        Bundle bundle = new Bundle();
        bundle.putString("uid", strUid);
        NavHostFragment.findNavController(MainFragment.this)
                .navigate(R.id.action_routePlanFragment_to_poiDetailsFragment, bundle);
    }

    private void goToReGeoCoder(LatLng p) {
        Point point = new Point(p.longitude, p.latitude);
        getAutoGeoCoderManager().setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult poiResult) {
                Bundle bundle = new Bundle();
                String nearby = poiResult.getNearby();
                bundle.putDouble("latitude", p.latitude);
                bundle.putDouble("longitude", p.longitude);
                bundle.putString("poiName", nearby);
                bundle.putString("address", poiResult.getAddress());
                bundle.putString("district", poiResult.getDistrict());
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.action_routePlanFragment_to_poiDetailsFragment, bundle);
            }
        });
        getAutoGeoCoderManager().reverseGeoCode(point);
    }

    @Override
    public void onResume() {
        super.onResume();
        initLocation();
        BDAutoMapFactory.getCruiserManager().start(getActivity());
        BDAutoMapFactory.getAutoLocationManager().onResume();
        BDAutoMapFactory.getAutoMapManager().setMyLocationEnabled(true);
        autoCruiserControls.onResume();
        AutoLocData curLocation =
                BDAutoMapFactory.getAutoLocationManager().getCurLocation(CoordType.BD09LL);
        if (BDAutoMapFactory.getAutoLocationManager().isLocationValid()) {
            if (!TextUtils.isEmpty(curLocation.cityCode)){
                SharedPreferencesUtils.putInt(getContext(), "cityId", Integer.valueOf(curLocation.cityCode));
            }
            BDAutoMapFactory.getAutoMapManager()
                    .animateToMapCenter(new LatLng(curLocation.latitude, curLocation.longitude), 1000);
        }
        isMainFragment = true;
        BDAutoMapFactory.getNaviCommonSettingManager().setVehicleType(IAutoCommonConfig.Vehicle.TRUCK);
        BDAutoMapFactory.getAutoMapManager().onResume();
        BDAutoMapFactory.getAutoMapManager().setRotateGesturesEnabled(true);
        BDAutoMapFactory.getAutoMapManager().setOverlookGestureEnable(true);
        /*double lat[] = {40.055673,40.055618,40.055576,40.055542,40.055487,40.055435,
                40.055283,40.055231,40.055159,40.055107,40.055062,40.055207,40.055376,
                40.055759,40.055825,40.055839,40.056122,40.05646,40.056867,40.057392,
                40.057834,40.057924,40.058055,40.058131,40.058296,40.058317};
        double lng[] = {116.308756,116.308509,116.308312,116.308137,116.307858,116.307616,
                116.306933,116.306659,116.306295,116.306088,116.305868,116.305684,116.305639,
                116.305491,116.30545,116.305459,116.30536,116.305244,116.305109,116.304938,
                116.304803,116.304785,116.305055,116.305495,116.306322,116.306501};
        final int[] i = {0};
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (i[0] < lat.length - 1) {
                    AutoOuterLocData autoLocData = new AutoOuterLocData();
                    autoLocData.latitude = lat[i[0]];
                    autoLocData.longitude = lng[i[0]];
                    autoLocData.locType = 1;
                    autoLocData.speed = 0;
                    autoLocData.direction = 174;
                    autoLocData.accuracy = 6;
                    autoLocData.altitude = 0;
                    autoLocData.satellitesNum = 23;
                    autoLocData.coordType = CoordType.BD09LL;
                    BDAutoMapFactory.getAutoLocationManager().setLocationData(autoLocData);
                    i[0]++;
                }else {
                    i[0] = 0;
                }
            }
        }, 1000, 1000);

        BDAutoMapFactory.getAutoLocationManager().addLocationChangeLister(autoLocationChangeListener);
        if (iconBitmap == null) {
            iconBitmap = BitmapUtil.drawableToBitmap(ContextCompat.getDrawable(getActivity(),
                    R.drawable.auto_carpoint));
        }
        BDAutoMapFactory.getAutoMapManager().setMapLocationIcon(iconBitmap);*/

        LatLng llA = new LatLng(39.963175, 116.400244);
        LatLng llB = new LatLng(39.942821, 116.369199);
        LatLng llC = new LatLng(39.939723, 116.425541);
        LatLng llD = new LatLng(39.906965, 116.401394);
        LatLng llE = new LatLng(39.956965, 116.331394);
        ArrayList<MyItem> markerOptions = new ArrayList<>();
        markerOptions.add(new MyItem(llA));
        markerOptions.add(new MyItem(llB));
        markerOptions.add(new MyItem(llC));
        markerOptions.add(new MyItem(llD));
        markerOptions.add(new MyItem(llE));
        clusterManager = new ClusterManager<>(getContext(), BDAutoMapFactory.getAutoMapManager().getMapView());
        clusterManager.addItems(markerOptions);
        BDAutoMapFactory.getAutoMapManager().setOnMarkerClickListener(clusterManager);
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                Toast.makeText(getContext(), "有" + cluster.getSize() + "个点", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
//                clusterManager.removeItem(item);
                clusterManager.onClusterClearOneListener(item);
                Toast.makeText(getContext(), "点击单个Item", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        AutoBNTruckInfo.Builder truckInfo = new AutoBNTruckInfo.Builder()
                .plate("京A88789").truckType(AutoVehicleConstant.TruckType.HEAVY)
                .emissionLimit(0).powerType(AutoVehicleConstant.PowerType.ELECTRIC)
                .plateType(AutoVehicleConstant.PlateType.BLUE).weight(74).length(7)
                .width(2.5f).height(4).axlesNumber(7).axlesWeight(0).oilCost("0")
                .loadWeight(49);
        BDAutoMapFactory.getNaviCommonSettingManager().setTruckInfo(truckInfo.build());
        BDAutoMapFactory.getNaviCommonSettingManager().setTruckWeightLimitSwitch(true);

        BDAutoMapInitializer.setCoordType(CoordType.BD09LL);

        IAutoNaviSettingManager.IProfessionalNaviSetting mSetting = BDAutoMapFactory.getProfessionalNaviSettingManager();
        int mVehicleType = BDAutoMapFactory.getNaviCommonSettingManager().getVehicleType();
        int dayNightMode = mSetting.getDayNightMode(mVehicleType);
        if (dayNightMode == IAutoNaviSettingParams.DayNightMode.DAY_NIGHT_MODE_AUTO){
            mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_AUTO, mVehicleType);
            BDAutoMapFactory.getAutoDayNightManager()
                    .setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_AUTO);
        }else if (dayNightMode == IAutoNaviSettingParams.DayNightMode.DAY_NIGHT_MODE_DAY){
            mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_DAY, mVehicleType);
            BDAutoMapFactory.getAutoDayNightManager()
                    .setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        }else if (dayNightMode == IAutoNaviSettingParams.DayNightMode.DAY_NIGHT_MODE_NIGHT){
            mSetting.setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_NIGHT, mVehicleType);
            BDAutoMapFactory.getAutoDayNightManager()
                    .setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_NIGHT);
        }
    }

    public class MyItem implements ClusterItem {
        LatLng mPosition;
        public MyItem(LatLng position) {
            mPosition = position;
        }
        @Override
        public LatLng getPosition() {
            return mPosition;
        }
        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            return BitmapDescriptorFactory
                    .fromResource(getContext(), R.drawable.icon_gcoding);
        }
    }

    AutoLocationChangeListener autoLocationChangeListener = new AutoLocationChangeListener() {
        @Override
        public void onLocationChange(AutoLocData locData) {
            Log.d("updateLocation", "updateLocation: " + locData.longitude + ","
                    + locData.latitude + "," + locData.locType + "," + locData.cityCode + ","
                    + locData.addr + "," + locData.satellitesNum);
            if ((SystemClock.elapsedRealtime() - lastTouchTime) > 3000) {
                Log.d("main", "onLocationChange: " + locData.toString());
                updateLocation(locData);
            }
        }

        @Override
        public CoordType onGetCoordType() {
            return CoordType.BD09LL;
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (autoLocationChangeListener != null) {
            BDAutoMapFactory.getAutoLocationManager().removeLocationChangeLister(autoLocationChangeListener);
        }
        autoCruiserControls.onPause();
        BDAutoMapFactory.getAutoLocationManager().onPause();
        isMainFragment = false;
        if (clusterManager != null) {
            clusterManager.onClusterClearListener();
        }
//        timer.cancel();
    }


    private AutoRoutePlanNode getMyLocNode() {
        AutoRoutePlanNode mLocNode = null;
        AutoLocData curLocData = new AutoLocData();
        Point point = CoordinateUtil.gcj02ToBD09mc(116.30142, 39.90882);
        curLocData.latitude = point.getDoubleY();
        curLocData.longitude = point.getDoubleX();

        if (curLocData != null) {
            mLocNode =
                    new AutoRoutePlanNode.Builder().longitude(curLocData.longitude)
                            .latitude(curLocData.latitude).name("百度大厦").description("百度大厦")
                            .districtID(131).build();
        }
        return mLocNode;
    }

    private AutoRoutePlanNode mStartNode = null;

    private void routePlanToNavi(final CoordType coType, final int from) {
        AutoRoutePlanNode sNode = new AutoRoutePlanNode.Builder()
                .latitude(40.05087)
                .longitude(116.30142)
                .name("百度大厦")
                .description("百度大厦")
                .build();
        AutoRoutePlanNode eNode = new AutoRoutePlanNode.Builder()
                .latitude(39.90882)
                .longitude(116.39750)
                .name("北京天安门")
                .description("北京天安门")
                .build();
        switch (coType) {
            case GCJ02: {
                sNode = new AutoRoutePlanNode.Builder()
                        .latitude(40.05087)
                        .longitude(116.30142)
                        .name("百度大厦")
                        .description("百度大厦")
                        .build();
                eNode = new AutoRoutePlanNode.Builder()
                        .latitude(39.90882)
                        .longitude(116.39750)
                        .name("北京天安门")
                        .description("北京天安门")
                        .build();
                break;
            }
            case BD09LL: {
                sNode = new AutoRoutePlanNode.Builder()
                        .latitude(40.057009624099436)
                        .longitude(116.30784537597782)
                        .name("百度大厦")
                        .description("百度大厦")
                        .build();
                eNode = new AutoRoutePlanNode.Builder()
                        .latitude(39.915160800132085)
                        .longitude(116.40386525193937)
                        .name("北京天安门")
                        .description("北京天安门")
                        .build();
                break;
            }
            default:
                break;
        }

        mStartNode = sNode;

        List<AutoRoutePlanNode> nodeList = new ArrayList<AutoRoutePlanNode>();
        nodeList.add(sNode);
        nodeList.add(eNode);

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("mainFragment", "onStop: ");
        BDAutoMapFactory.getCruiserManager().close();
        BDAutoMapFactory.getAutoMapManager().setMyLocationEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (autoCruiserControls != null) {
            autoCruiserControls.onDestroy();
        }
        if (mMarker1 != null) {
            mMarker1.remove();
        }
        if (mMarker2 != null) {
            mMarker2.remove();
        }
    }

    private int a = 0;
    private int b = 0;
    private int c = 0;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            switch (v.getId()) {
                case R.id.btn_route_plan:
                    BDAutoMapFactory.getAutoDayNightManager().cacheMapTheme();
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_mainFragment_to_routePlanFragment);
                    break;
                case R.id.btn_poi_search:
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_mainFragment_to_poiSearchFragment);
                    break;
                case R.id.btn_map_road_condition:
                    boolean isTraffic = BDAutoMapFactory.getAutoMapManager().isTraffic();
                    isTraffic = !isTraffic;
                    BDAutoMapFactory.getAutoMapManager().setTraffic(isTraffic);
                    break;
                case R.id.btn_map_tocenter: // 移动底图中心点
                    int animTime = 500;     // 动画持续时间（ms）
                    Point point = CoordinateUtil.gcj02ToBD09ll(116.30783, 40.056025);
                    if (point != null) {
                        BDAutoMapFactory.getAutoMapManager()
                                .animateToMapCenter(new LatLng(point.getDoubleY(), point.getDoubleX()),
                                        animTime);
                    }
                    break;
                case R.id.btn_marker:
                    if (mMarker0 == null) {
                        addMarker(40.070175, 116.400244);
                        mBtnMarker.setText("删除Marker");
                    } else {
                        removeLayer(mMarker0);
                        mMarker0 = null;
                        mBtnMarker.setText("添加Marker");
                    }
                    break;
                case R.id.btn_calc_distance:
                    Point pt1 = new Point(12947473.60, 4846326.10);
                    Point pt2 = new Point(12943803.25, 4842902.35);

                    int distance = (int) DistanceUtil.getDistanceByMc(pt1, pt2);
                    Toast.makeText(MainFragment.this.getActivity(),
                            "Distance : " + distance + "米",
                            Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_show_area:
                    showMapBoundArea();
                    break;
                case R.id.btn_day:
                    BDAutoMapFactory.getAutoDayNightManager()
                            .setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_DAY);
                    break;
                case R.id.btn_night:
                    BDAutoMapFactory.getAutoDayNightManager().setDayNightMode(
                            IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_NIGHT);
                    break;
                case R.id.btn_auto:
                    BDAutoMapFactory.getAutoDayNightManager()
                            .setDayNightMode(IAutoDayNightManager.DayNightMode.DAY_NIGHT_MODE_AUTO);
                    break;
                /*case R.id.btn_map_zoom_in:
                    BDAutoMapFactory.getAutoMapManager().zoomIn();
                    break;

                case R.id.btn_map_zoom_out:
                    BDAutoMapFactory.getAutoMapManager().zoomOut();
                    break;*/
                case R.id.btn_local_map_call:
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_mainFragment_to_offlineMapPage);
                    break;
                case R.id.btn_local_nav_map1:
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_mainFragment_to_offlineNavPage);
                    break;
                case R.id.rl_search:
                    EventBus.getDefault().postSticky(new MessageEvent(false));
//                    BDAutoMapFactory.getAutoDayNightManager().cacheMapTheme();
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_mainFragment_to_routePlanFragment);
                    break;
                case R.id.rl_prompt_tone:
                    a = 1;
                    b = 0;
                    c = 0;
                    imgBeep.setImageResource(R.mipmap.ic_beep_s_white);
                    tvBeep.setTextColor(Color.parseColor("#3366FF"));
                    imgMute.setImageResource(R.mipmap.ic_mute_n_white);
                    tvMute.setTextColor(Color.parseColor("#333333"));
                    llNavigationSound.setVisibility(View.GONE);
                    imgBroadcast.setImageResource(R.mipmap.ic_beep_n_white);
                    tvBroadcast.setText("仅提示音");
                    tvBroadcast.setTextColor(Color.parseColor("#333333"));
                    break;
                case R.id.rl_navigation_mute:
                    a = 0;
                    b = 1;
                    c = 0;
                    imgBeep.setImageResource(R.mipmap.ic_beep_n_white);
                    tvBeep.setTextColor(Color.parseColor("#333333"));
                    imgMute.setImageResource(R.mipmap.ic_mute_s_white);
                    tvMute.setTextColor(Color.parseColor("#3366FF"));
                    llNavigationSound.setVisibility(View.GONE);
                    imgBroadcast.setImageResource(R.mipmap.ic_mute_n_white);
                    tvBroadcast.setTextColor(Color.parseColor("#333333"));
                    tvBroadcast.setText("导航静音");
                    break;
                case R.id.rl_navigation_broadcast:
                    if (c == 0){
                        llNavigationSound.setVisibility(View.VISIBLE);
                        c = 1;
                    }else {
                        llNavigationSound.setVisibility(View.GONE);
                        c = 0;
                    }
                    imgBeep.setImageResource(R.mipmap.ic_beep_n_white);
                    tvBeep.setTextColor(Color.parseColor("#333333"));
                    imgMute.setImageResource(R.mipmap.ic_mute_n_white);
                    tvMute.setTextColor(Color.parseColor("#333333"));
                    imgBroadcast.setImageResource(R.mipmap.ic_broadcast_n_white);
                    tvBroadcast.setTextColor(Color.parseColor("#333333"));
                    tvBroadcast.setText("导航播报");
                    if (a == 1){
                        imgBeep.setImageResource(R.mipmap.ic_beep_s_white);
                        tvBeep.setTextColor(Color.parseColor("#3366FF"));
                    }
                    if (b == 1){
                        imgMute.setImageResource(R.mipmap.ic_mute_s_white);
                        tvMute.setTextColor(Color.parseColor("#333333"));
                    }
                    a = 0;
                    b = 0;
                    break;
                case R.id.rl_more:
                    rlAllUi.setVisibility(View.GONE);
                    showMore();
                    break;
                case R.id.rl_map_zoom_in:
                    BDAutoMapFactory.getAutoMapManager().zoomIn();
                    break;
                case R.id.rl_map_zoom_out:
                    BDAutoMapFactory.getAutoMapManager().zoomOut();
                    break;
                case R.id.img_head_portrait:
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_mainFragment_to_personalCenterFragment);
                    break;
                case R.id.main_rl_location:
                    AutoLocData locData =
                            BDAutoMapFactory.getAutoLocationManager().getCurLocation(CoordType.BD09LL);
                    updateLocation(locData);
                    /*AutoMapStatus st = BDAutoMapFactory.getAutoMapManager().getAutoMapStatus();
                    st.overlooking = -45;
                    st.animationTime = 1000;
                    BDAutoMapFactory.getAutoMapManager().setAutoMapStatus(st);*/
                    break;
                case R.id.rl_go_home:
                    EventBus.getDefault().postSticky(new MessageEvent(false));
                    bundle.putString("poiName" , "回家");
                    bundle.putDouble("latitude" , 39.91092);
                    bundle.putDouble("longitude" , 116.41338);
                    SharedPreferencesUtils.putBoolean(getActivity(), "isRoute", true);
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_recommendedRouteFragment, bundle);
                    break;
                case R.id.rl_go_company:
                    EventBus.getDefault().postSticky(new MessageEvent(false));
                    bundle.putString("poiName" , "去公司");
                    bundle.putDouble("latitude" , 40.04988);
                    bundle.putDouble("longitude" , 116.27985);
                    SharedPreferencesUtils.putBoolean(getActivity(), "isRoute", true);
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_recommendedRouteFragment, bundle);
                    break;
                default:
                    break;
            }
        }
    };

    private void initLocation() {
        AutoLocationOption locationOption = new AutoLocationOption();
        locationOption.setOpenGps(true);
        locationOption.setCoorType(CoordType.BD09LL);
        // 设置定位扫描间隔 setLocationChangeNotify为false会按照设置时间回调
        // setLocationChangeNotify为true会在接收到位置改变时回调
        locationOption.setScanSpan(2000);
        locationOption.setLocationChangeNotify(false);
        // 是否需要地址信息
        locationOption.setNeedAddrs(true);
        BDAutoMapFactory.getAutoLocationManager().init(getActivity(), locationOption);
        BDAutoMapFactory.getAutoLocationManager().addLocationChangeLister(autoLocationChangeListener);
        BDAutoMapFactory.getAutoLocationManager().startLoc();
        if (iconBitmap == null) {
            iconBitmap = BitmapUtil.drawableToBitmap(ContextCompat.getDrawable(getActivity(),
                    R.drawable.auto_carpoint));
        }
        BDAutoMapFactory.getAutoMapManager().setMapLocationIcon(iconBitmap);
    }

    private void updateLocation(AutoLocData curLocation) {

        if (BDAutoMapFactory.getCruiserManager().isStarted()
                || BDAutoMapFactory.getAutoNaviManager().isNaviBegin()) {
            return;
        }
        if (BDAutoMapFactory.getAutoLocationManager().isLocationValid()) {
            BDAutoMapFactory.getAutoMapManager().setMapLocationData(curLocation, false);
            BDAutoMapFactory.getAutoMapManager().setMyLocationEnabled(true);
            BDAutoMapFactory.getAutoMapManager().setZoomLevel(18);
            BDAutoMapFactory.getAutoMapManager().animateToMapCenter(new LatLng(curLocation.latitude,
                    curLocation.longitude), 1000);
        }
    }

    private int broadcast = 0;
    private int volume = 0;
    // 更多
    private void showMore() {

        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.more_popupwindow, null, false);
        PopupWindow morePopupWindow = new PopupWindow(inflate,ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT,true);
        morePopupWindow.setOutsideTouchable(true);
        morePopupWindow.setFocusable(true);
        morePopupWindow.setTouchable(true);
        darkenBackground(0.3f);
        morePopupWindow.showAtLocation(imgHeadPortrait, Gravity.LEFT, 0, 0);
        morePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                rlAllUi.setVisibility(View.VISIBLE);
                darkenBackground(1f);
            }
        });

        // 返回按钮
        ImageView imgBack = inflate.findViewById(R.id.more_popup_img_back);
        // 音量图标
        ImageView imgVolume = inflate.findViewById(R.id.img_volume);
        // 减声音
        ImageView imgVolumeReduce = inflate.findViewById(R.id.img_volume_reduce);
        // 加声音
        ImageView imgVolumeAdd = inflate.findViewById(R.id.img_volume_add);
        // 进度条
        ProgressBar pbVolume = inflate.findViewById(R.id.pb_volume);
        // 标准播报
        RelativeLayout rlStandardBroadcast = inflate.findViewById(R.id.rl_standard_broadcast);
        TextView tvStandardBroadcast = inflate.findViewById(R.id.tv_standard_broadcast);
        ImageView imgStandardBroadcast = inflate.findViewById(R.id.img_standard_broadcast);
        // 简洁播报
        RelativeLayout rlConciseBroadcast = inflate.findViewById(R.id.rl_concise_broadcast);
        TextView tvConciseBroadcast = inflate.findViewById(R.id.tv_concise_broadcast);
        ImageView imgConciseBroadcast = inflate.findViewById(R.id.img_concise_broadcast);
        // 路况
        RelativeLayout rlTraffic = inflate.findViewById(R.id.rl_traffic);
        TextView tvTraffic = inflate.findViewById(R.id.tv_traffic);
        ImageView imgTraffic = inflate.findViewById(R.id.img_traffic);
        // 播报简介
        TextView tvBroadcastBriefIntroduction = inflate.findViewById(R.id.tv_broadcast_brief_introduction);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morePopupWindow.dismiss();
            }
        });

        boolean isTraffic = BDAutoMapFactory.getAutoMapManager().isTraffic();
        Log.d("isTraffic", "showMore: " + isTraffic);
        if (isTraffic){
            tvTraffic.setText("路况开启");
            imgTraffic.setImageResource(R.mipmap.ic_traffic_s_white);
            imgTraffic.setTag(R.mipmap.ic_traffic_s_white);
        }else {
            tvTraffic.setText("路况关闭");
            imgTraffic.setImageResource(R.mipmap.ic_traffic_n_white);
            imgTraffic.setTag(R.mipmap.ic_traffic_n_white);
        }
        BDAutoMapFactory.getAutoMapManager().setTraffic(isTraffic);

        if (broadcast == 0){
            standardBroadcast(rlStandardBroadcast, tvStandardBroadcast, imgStandardBroadcast, rlConciseBroadcast,
                    tvConciseBroadcast, imgConciseBroadcast, tvBroadcastBriefIntroduction);
        }else if (broadcast == 1){
            standardBroadcast(rlStandardBroadcast, tvStandardBroadcast, imgStandardBroadcast, rlConciseBroadcast,
                    tvConciseBroadcast, imgConciseBroadcast, tvBroadcastBriefIntroduction);
        }else if (broadcast == 2){
            conciseBroadcast(rlStandardBroadcast, tvStandardBroadcast, imgStandardBroadcast, rlConciseBroadcast,
                    tvConciseBroadcast, imgConciseBroadcast, tvBroadcastBriefIntroduction);
        }

        pbVolume.setProgress(volume);
        if (volume == 0){
            imgVolume.setImageResource(R.mipmap.ic_low_volume_s);
        }
        imgVolumeReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volume--;
                pbVolume.setProgress(volume);
                if (volume == 0){
                    imgVolume.setImageResource(R.mipmap.ic_low_volume_s);
                }
            }
        });
        imgVolumeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volume++;
                pbVolume.setProgress(volume);
                imgVolume.setImageResource(R.mipmap.ic_low_volume_n_white);
            }
        });

        rlStandardBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcast = 1;
                standardBroadcast(rlStandardBroadcast, tvStandardBroadcast, imgStandardBroadcast, rlConciseBroadcast,
                        tvConciseBroadcast, imgConciseBroadcast, tvBroadcastBriefIntroduction);
            }
        });

        rlConciseBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcast = 2;
                conciseBroadcast(rlStandardBroadcast, tvStandardBroadcast, imgStandardBroadcast, rlConciseBroadcast,
                        tvConciseBroadcast, imgConciseBroadcast, tvBroadcastBriefIntroduction);
            }
        });

        rlTraffic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isTraffic = BDAutoMapFactory.getAutoMapManager().isTraffic();
                isTraffic = !isTraffic;
                BDAutoMapFactory.getAutoMapManager().setTraffic(isTraffic);
                if (isTraffic){
                    tvTraffic.setText("路况开启");
                    imgTraffic.setImageResource(R.mipmap.ic_traffic_s_white);
                }else {
                    tvTraffic.setText("路况关闭");
                    imgTraffic.setImageResource(R.mipmap.ic_traffic_n_white);
                }
            }
        });

    }

    private void upDateTraffic(boolean b, TextView tvTraffic, ImageView imgTraffic) {
        BDAutoMapFactory.getAutoMapManager().setTraffic(b);
        boolean traffic = BDAutoMapFactory.getAutoMapManager().isTraffic();
        if (traffic) {
            tvTraffic.setText("路况开启");
            imgTraffic.setImageResource(R.mipmap.ic_traffic_s_white);
            imgTraffic.setTag(R.mipmap.ic_traffic_s_white);
        }else {
            tvTraffic.setText("路况关闭");
            imgTraffic.setImageResource(R.mipmap.ic_traffic_n_white);
            imgTraffic.setTag(R.mipmap.ic_traffic_n_white);
        }
    }

    private void darkenBackground(float alpha){
        WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
        attributes.alpha = alpha;
        getActivity().getWindow().setAttributes(attributes);
    }

    private void standardBroadcast(RelativeLayout rlStandardBroadcast, TextView tvStandardBroadcast,
                                   ImageView imgStandardBroadcast, RelativeLayout rlConciseBroadcast,
                                   TextView tvConciseBroadcast, ImageView imgConciseBroadcast,
                                   TextView tvBroadcastBriefIntroduction){
        rlStandardBroadcast.setBackgroundResource(R.drawable.more_popup_broadcast_bg_blue);
        tvStandardBroadcast.setTextColor(Color.parseColor("#FFFFFF"));
        imgStandardBroadcast.setImageResource(R.mipmap.ic_standard_s_white);
        rlConciseBroadcast.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
        tvConciseBroadcast.setTextColor(Color.parseColor("#11131A"));
        imgConciseBroadcast.setImageResource(R.mipmap.ic_concise_n_white);
        tvBroadcastBriefIntroduction.setText("标准播报：默认模式，适合多数人使用");
    }

    private void conciseBroadcast(RelativeLayout rlStandardBroadcast, TextView tvStandardBroadcast,
                                  ImageView imgStandardBroadcast, RelativeLayout rlConciseBroadcast,
                                  TextView tvConciseBroadcast, ImageView imgConciseBroadcast,
                                  TextView tvBroadcastBriefIntroduction){
        rlStandardBroadcast.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
        tvStandardBroadcast.setTextColor(Color.parseColor("#11131A"));
        imgStandardBroadcast.setImageResource(R.mipmap.ic_broadcast_n_white);
        rlConciseBroadcast.setBackgroundResource(R.drawable.more_popup_broadcast_bg_blue);
        tvConciseBroadcast.setTextColor(Color.parseColor("#FFFFFF"));
        imgConciseBroadcast.setImageResource(R.mipmap.ic_concise_s_white);
        tvBroadcastBriefIntroduction.setText("简洁播报：减少直行路况播报，降低转弯播报频次等，熟练司机使用");
    }

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
        mMarker0 = BDAutoMapFactory.getAutoMapManager().addSDKOverlayItem(markerOption);

    }

    private void removeLayer(SDKOverlay layer) {
        if (layer != null) {
            layer.remove();
        }
    }

    private void showInfoWindow() {
        // 用来构造InfoWindow的Button
        Button button = new Button(this.getActivity());
        button.setBackgroundResource(R.drawable.popup);
        button.setText("InfoWin-show");

        LatLng point = new LatLng(39.870175, 116.420244);
        mInfoWindow = new InfoWindow(button, point, 0);

        BDAutoMapFactory.getAutoMapManager().showInfoWindow(mInfoWindow);
    }

    private void hideInfoWindow() {
        BDAutoMapFactory.getAutoMapManager().hideInfoWindow();
    }

    /**
     * 在指定的区域显示
     */
    private void showMapBoundArea() {

        LatLng leftTopPoint = new LatLng(41.070175, 115.400244);

        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(this.getActivity(),
                R.drawable.icon_gcoding);
        MarkerOptions markerOption = new MarkerOptions().position(leftTopPoint).icon(bitmap);
        mMarker1 = BDAutoMapFactory.getAutoMapManager().addSDKOverlayItem(markerOption);

        LatLng point = new LatLng(39.070175, 117.400244);
        markerOption = new MarkerOptions().position(point).icon(bitmap);
        mMarker2 = BDAutoMapFactory.getAutoMapManager().addSDKOverlayItem(markerOption);

        Point point1 = new Point(115.400244, 41.070175);
        Point point2 = new Point(117.400244, 39.070175);

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
        Rect screenRect = new Rect(screenWidth / 2, screenHeight / 2,
                screenWidth, screenHeight);

        /**
         * 注：得到的MapStatus的offset 不再是屏幕的中心点，而变成了Rect区域的中心点
         * 使用完此功能后，务必将MapStatus的offset再置为(0,0)点，即屏幕中心点
         */
        AutoMapStatus st = BDAutoMapFactory.getAutoMapManager().calcMapStatusByBounds(mapBound,
                screenRect);
        st.animationTime = 1000;

        BDAutoMapFactory.getAutoMapManager().setAutoMapStatus(st);
    }

    private Observer observer = new Observer() {
        @Override
        public void update(Observable o, final Object arg) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {

                }
            });

        }
    };
}
