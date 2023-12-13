package com.baidu.mapautosdk.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.baidu.mapautosdk.MessageEvent;
import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.navi.AutoNaviCommonParams;
import com.baidu.mapautosdk.api.route.AutoRoutePlanNode;
import com.baidu.mapautosdk.api.route.DrivingRoutePlanOption;
import com.baidu.mapautosdk.api.route.DrivingRoutePlanPoiOption;
import com.baidu.mapautosdk.api.route.DrivingRouteResult;
import com.baidu.mapautosdk.api.route.OnGetRoutePlanResultListener;
import com.baidu.mapautosdk.api.route.OnRouteClickListener;
import com.baidu.mapautosdk.api.route.RouteLine;
import com.baidu.mapautosdk.api.route.RouteSortManager;
import com.baidu.mapautosdk.api.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapautosdk.api.search.poi.PoiDetailSearchOption;
import com.baidu.mapautosdk.api.search.poi.PoiDetailSearchResult;
import com.baidu.mapautosdk.api.search.poi.PoiResult;
import com.baidu.mapautosdk.api.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapautosdk.api.search.sug.SuggestionResult;
import com.baidu.mapautosdk.api.search.sug.SuggestionSearch;
import com.baidu.mapautosdk.api.search.sug.SuggestionSearchOption;
import com.baidu.mapautosdk.interfaces.IAutoCommonConfig;
import com.baidu.mapautosdk.interfaces.route.IAutoRoutPlanManager;
import com.baidu.mapautosdk.interfaces.route.IAutoRouteResultManager;
import com.baidu.mapautosdk.interfaces.search.IAutoPoiSearchManager;
import com.baidu.mapautosdk.model.AutoLocData;
import com.baidu.mapautosdk.model.CityWeather;
import com.baidu.mapautosdk.platform.route.model.CarPassCityInfo;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.ui.adapter.SuggestionsAdapter;
import com.baidu.mapautosdk.ui.presenter.RoutePlanPresenter;
import com.baidu.mapautosdk.ui.presenter.SuggestionsPresenter;
import com.baidu.platform.comapi.basestruct.Point;
import com.baidu.tts.tools.SharedPreferencesUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 1。路线检索可以直接传名称例如"百度大厦"进行路线检索
 */
public class RoutePlanFragment extends BaseFragment implements SuggestionsAdapter.InnerItemOnclickListener {
    private static final int FLAG_START_INPUT = DrivingRouteResult.NODE_TYPE_START;
    private static final int FLAG_END_INPUT = DrivingRouteResult.NODE_TYPE_END;
    private View routeResultBottom;
    private TextView tvRouteSort;

    private RoutePlanPresenter routePlanPresenter;

    private DrivingRoutePlanOption routePlanOption;

    private TextView tvStartNode;
    private TextView tvEndNode;

    private ViewGroup mContentView;

    private TextView tvMapLevel;
    private TextView mLevelBgTextView;
    private TextView mLevelDrawable;

    private Button btnRoutePoiSearch;
    private Button btnRouteRefresh;
    private Button btnExchange;

    private Handler mHandler;

    private IAutoRoutPlanManager mSearch;

    private boolean isBackFromNavi = false;

    AutoRoutePlanNode stNode = null;
    AutoRoutePlanNode enNode = null;
    private Button mBtnRouteLongWay;
    private Button mBtnRouteLongWayWeather;

    private EditText navigationPopupEt;
    private SuggestionsPresenter suggestionsPresenter;
    private RelativeLayout rlRoutePlanAll;
    private RelativeLayout rlRoutePlanLv;
    private TextView tvRoutePlanSearch;
    private View navigationPopupView;

    private AutoLocData curLocation;
    private IAutoPoiSearchManager poiSearch;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        boolean registered = EventBus.getDefault().isRegistered(this);
        if (registered == false){
            EventBus.getDefault().register(this);
        }
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_route_plan, container, false);
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
        routeResultBottom = view.findViewById(R.id.route_result_bottom);
        tvRouteSort = view.findViewById(R.id.btn_route_sort);
        mBtnRouteLongWay = view.findViewById(R.id.btn_route_search_long_way);
        mBtnRouteLongWayWeather = view.findViewById(R.id.btn_route_search_long_way_weather);
        tvStartNode = view.findViewById(R.id.tv_start_name);
        tvEndNode = view.findViewById(R.id.tv_end_name);
        tvMapLevel = view.findViewById(R.id.tv_map_level);
        mLevelBgTextView = view.findViewById(R.id.tv_level_bg);
        mLevelDrawable = view.findViewById(R.id.level_drawable);
        btnExchange = view.findViewById(R.id.btn_exchange);
        tvStartNode.setOnClickListener(clickListener);
        tvEndNode.setOnClickListener(clickListener);
        btnExchange.setOnClickListener(clickListener);
        view.findViewById(R.id.btn_to_nav).setOnClickListener(clickListener);
        btnRouteRefresh = view.findViewById(R.id.btn_route_refresh);
        btnRouteRefresh.setOnClickListener(clickListener);
        btnRoutePoiSearch = view.findViewById(R.id.btn_route_poi_search);
        btnRoutePoiSearch.setOnClickListener(clickListener);
        tvRouteSort.setOnClickListener(clickListener);
        mBtnRouteLongWay.setOnClickListener(clickListener);
        mBtnRouteLongWayWeather.setOnClickListener(clickListener);
        routePlanPresenter = new RoutePlanPresenter();

        // 收藏点
        view.findViewById(R.id.route_plan_ll_favorite).setOnClickListener(clickListener);
        // 停车场
        view.findViewById(R.id.route_plan_ll_paking).setOnClickListener(clickListener);
        // 加油站
        view.findViewById(R.id.route_plan_ll_gas).setOnClickListener(clickListener);
        // 洗车
        view.findViewById(R.id.route_plan_ll_washcar).setOnClickListener(clickListener);
        // 更多
        view.findViewById(R.id.route_plan_ll_more).setOnClickListener(clickListener);

        stNode =
                new AutoRoutePlanNode.Builder().name("百度大厦").latitude(40.05087).longitude(116.30142)
                        .build();
        enNode = new AutoRoutePlanNode.Builder().name("百度国际大厦").latitude(22.53042).longitude(113.950292)
                .build();
        if (routePlanOption == null) {
            routePlanOption = new DrivingRoutePlanOption();
        }

        // 返回上个页面
        view.findViewById(R.id.navigation_popup_img_back).setOnClickListener(clickListener);
        // 搜索框
        navigationPopupEt = view.findViewById(R.id.navigation_popup_et);
        // 搜索按钮
        tvRoutePlanSearch = view.findViewById(R.id.tv_route_plan_search);
        tvRoutePlanSearch.setOnClickListener(clickListener);
        // 搜索按钮前边的分界线
        navigationPopupView = view.findViewById(R.id.navigation_popup_view);
        // 初始ui
        rlRoutePlanAll = view.findViewById(R.id.rl_route_plan_all);
        // 搜索结果
        rlRoutePlanLv = view.findViewById(R.id.rl_route_plan_lv);
        navigationPopupEt.addTextChangedListener(startWatcher);
        suggestionsPresenter = new SuggestionsPresenter();
        suggestionsPresenter.initListView(view);
        suggestionsPresenter.setOnItemClickListener(itemClickListener);
        suggestionsPresenter.setInnerItemOnclickListener(this);
        poiSearch = BDAutoMapFactory.getAutoPoiSearchManager();
    }

    private TextWatcher startWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0){
                rlRoutePlanAll.setVisibility(View.GONE);
                rlRoutePlanLv.setVisibility(View.VISIBLE);
                suggestionSearch(s.toString());
            }else {
                rlRoutePlanAll.setVisibility(View.VISIBLE);
                rlRoutePlanLv.setVisibility(View.GONE);
            }

        }
    };

    private void suggestionSearch(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            suggestionsPresenter.setData(null);
            return;
        }
        /*curLocation = BDAutoMapFactory.getAutoLocationManager().getCurLocation(CoordType.BD09LL);
        if (!TextUtils.isEmpty(curLocation.cityCode)){
            SharedPreferencesUtils.putInt(getActivity(), "cityId", Integer.valueOf(curLocation.cityCode));
        }*/
        SuggestionSearch suggestionSearch = SuggestionSearch.newInstance();
        suggestionSearch.setOnGetSuggestionResultListener(resultListener);
        suggestionSearch. requestSuggestion(new SuggestionSearchOption()
//                .cityId(cityId)
                .keyword(keyword));

//                .mapLoc(CoordinateUtil.gcj02Tobd09mc(curLocation.longitude, curLocation.latitude))
//                .mapLevel(14)
//                .newSearchType(0)
    }

    private final OnGetSuggestionResultListener resultListener =
            new OnGetSuggestionResultListener() {
                @Override
                public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                    if (suggestionResult == null) {
                        return;
                    }
                    boolean successful = suggestionResult.isSuccessful();
                    if (successful) {
                        Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                        suggestionsPresenter.setData(suggestionResult);
                    } else {
                        Toast.makeText(getActivity(), "fail" + suggestionResult.getErrorCode(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Intent intent = new Intent(BroadcastConstants.ACTION.SELECT_NODE);
//            SuggestionResult result = presenter.getResultData();
//            String cityId = result.getCityid(position);
//            intent.putExtra("keyword", result.getPoiname(position));
//            intent.putExtra("cityId", TextUtils.isEmpty(cityId) ? 0 : Integer.valueOf(cityId));
//            intent.putExtra("subTitle", result.getSubtitle(position));
//            intent.putExtra("flag", mFlag);
//            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
//            NavHostFragment.findNavController(SearchInputFragment.this).popBackStack();
        }
    };

    private void planRoute() {

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
        mSearch.setOnGetRoutePlanResultListener(routePlanResultListener);
        mSearch.routePlan(routePlanOption);

        mSearch.setRouteSelectListener(new OnRouteClickListener() {
            @Override
            public void onRouteClick(int i) {
                mSearch.selectRoute(i);
                routePlanPresenter.selectRoute(i);
            }
        });
    }

    private void researchRoute(int entry) {
        if (mSearch == null) {
            mSearch = BDAutoMapFactory.getAutoRoutePlanManager();
        }
        mSearch.setOnGetRoutePlanResultListener(routePlanResultListener);
        mSearch.routePlan(routePlanOption);
    }

    private void routePoi(String keyword) {
        DrivingRoutePlanPoiOption option = new DrivingRoutePlanPoiOption();
        option.keyword(keyword);
        if (mSearch == null) {
            mSearch = BDAutoMapFactory.getAutoRoutePlanManager();
        }
        mSearch.setOnGetRoutePlanResultListener(routePlanResultListener);
        mSearch.alongPoiSearch(option);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isBackFromNavi) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setScreenShow();
                }
            }, 0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        isBackFromNavi = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BDAutoMapFactory.getAutoDayNightManager().restoreMapTheme();
        EventBus.getDefault().unregister(this);
    }

    private void showRoute(List<RouteLine> routeLines) {
        updateBottomBar(routeLines, true);
        updateRouteSort();
    }

    private void updateBottomBar(List<RouteLine> routeLines, boolean isFromPlan) {
        tvRouteSort.setVisibility(View.VISIBLE);
        btnRouteRefresh.setVisibility(View.VISIBLE);
        btnRoutePoiSearch.setVisibility(View.VISIBLE);
        routeResultBottom.setVisibility(View.VISIBLE);
        mBtnRouteLongWay.setVisibility(View.VISIBLE);
        mBtnRouteLongWayWeather.setVisibility(View.VISIBLE);
        routePlanPresenter.initRouteCarPlanTabs(routeResultBottom);
        routePlanPresenter.updateRoute(routeLines);
        routePlanPresenter.setRouteSelectListener(new RoutePlanPresenter.RouteSelectListener() {
            @Override
            public void onSelectRoute(int index) {
                if (mSearch == null) {
                    mSearch = BDAutoMapFactory.getAutoRoutePlanManager();
                }
                mSearch.selectRoute(index);

            }
        });
    }

    private void updateRouteSort() {
        String str = RouteSortManager.getInstance().getCurrentRouteSortName();
        if (TextUtils.isEmpty(str)) {
            tvRouteSort.setText("智能推荐");
        } else {
            tvRouteSort.setText(str);
        }
    }

    private OnGetRoutePlanResultListener routePlanResultListener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            if (drivingRouteResult.isSuccessful()) {
                if (drivingRouteResult.isRouteLine()) {
                    if (drivingRouteResult.getRouteLines().size() > 0) {
                        final List<RouteLine> routeLines = drivingRouteResult.getRouteLines();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setScreenShow();
                                showRoute(routeLines);
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

    @Override
    public boolean onBackPressed() {
        NavHostFragment.findNavController(RoutePlanFragment.this).popBackStack();
        return false;
    }

    // 调整蚯蚓条显示区域
    public void setScreenShow() {
        int top = 100;     // 距离屏幕上边缘100dp
        int bottom = 160;  // 距离屏幕底部160dp
        int left = 0;
        int right = 0;
//        NavMapManager.getInstance().setScreenShow(top, bottom, left, right);
//        BNMapController.getInstance().resetRouteDetailIndex();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            switch (v.getId()) {
                case R.id.tv_start_name:
                    bundle.putInt("flag", FLAG_START_INPUT);
                    NavHostFragment.findNavController(RoutePlanFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_searchInputFragment, bundle);
                    break;
                case R.id.tv_end_name:
                    bundle.putInt("flag", FLAG_END_INPUT);
                    NavHostFragment.findNavController(RoutePlanFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_searchInputFragment, bundle);
                    break;
                case R.id.btn_route_sort:

                    break;
                case R.id.btn_route_refresh:
                    researchRoute(IAutoRoutPlanManager.ROUTE_PLAN_ENTRY_REFRESH_ICON);
                    break;
                case R.id.btn_route_poi_search:
                    routePoi("加油站");
                    break;
                case R.id.btn_to_nav:
                    NavHostFragment.findNavController(RoutePlanFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_demoGuideFragment, bundle);
                    break;
                case R.id.btn_exchange:
                    AutoRoutePlanNode tmpNode = stNode;
                    stNode = enNode;
                    enNode = tmpNode;
                    tvStartNode.setText("");
                    tvEndNode.setText("");
                    planRoute();
                    break;
                case R.id.btn_route_search_long_way:
                    longDistanceSearch();
                    break;
                case R.id.btn_route_search_long_way_weather:
                    longDistanceWeatherSearch();
                    break;
                case R.id.navigation_popup_img_back:
                    onBackPressed();
                    break;
                case R.id.tv_route_plan_search:
                    EventBus.getDefault().postSticky(new MessageEvent(false));
                    String etText = navigationPopupEt.getText().toString();
                    if (etText.length() > 0){
                        bundle.putString("etText", etText);
                        NavHostFragment.findNavController(RoutePlanFragment.this)
                                .navigate(R.id.action_routePlanFragment_to_searchResultFragment, bundle);
                    }
                    break;
                case R.id.route_plan_ll_favorite:
                    NavHostFragment.findNavController(RoutePlanFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_favoriteFragment);
                    break;
                case R.id.route_plan_ll_paking:
                    bundle.putInt("poiType", 1);
                    NavHostFragment.findNavController(RoutePlanFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_parkingFragment, bundle);
                    break;
                case R.id.route_plan_ll_gas:
                    bundle.putInt("poiType", 2);
                    NavHostFragment.findNavController(RoutePlanFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_parkingFragment, bundle);
                    break;
                case R.id.route_plan_ll_washcar:
                    bundle.putInt("poiType", 3);
                    NavHostFragment.findNavController(RoutePlanFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_parkingFragment, bundle);
                    break;
                default:
                    break;
            }
        }
    };

    private void longDistanceWeatherSearch() {
        String cityIdStr = "119|340"; // 城市id 从longDistanceSearch返回的CarPassCityInfo mCityCode | 拼接
        String arrStr = "3411|3427"; // 到达时间字符串 CarPassCityInfo mArriveTime
        BDAutoMapFactory.getAutoRouteResultManager().fetchWeather(cityIdStr, arrStr,
                new IAutoRouteResultManager.IOnWeatherResult() {
                    @Override
                    public void onResult(List<CityWeather> list) {

                    }
                });
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

    private String replaceHtml(String htmlStr) {
        Pattern phtml = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);
        Matcher mhtml = phtml.matcher(htmlStr);
        htmlStr = mhtml.replaceAll("");
        htmlStr = htmlStr.replaceAll(" ", "");
        return htmlStr.trim();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void itemClick(View v) {
        Bundle bundle = new Bundle();
        SuggestionResult resultData = suggestionsPresenter.getResultData();
        int position;
        position = (int) v.getTag();
        switch (v.getId()){
            case R.id.item_suggestions_rl_go_poi:

                EventBus.getDefault().postSticky(new MessageEvent(false));
                String uid = resultData.getSugPoiList().get(position).uid;
                int cityId = resultData.getSugPoiList().get(position).cityId;
                String poiNamePoi = resultData.getSugPoiList().get(position).poiName;
                bundle.putString("uid", uid);
                bundle.putInt("cityId", cityId);
                bundle.putString("poiName", poiNamePoi);
                NavHostFragment.findNavController(RoutePlanFragment.this)
                        .navigate(R.id.action_routePlanFragment_to_poiDetailsFragment, bundle);

                break;
            case R.id.item_suggestion_img_go_here:

                EventBus.getDefault().postSticky(new MessageEvent(false));

                String poiName = resultData.getSugPoiList().get(position).poiName;
                String subTitle = resultData.getSugPoiList().get(position).subTitle;
                String uid1 = resultData.getSugPoiList().get(position).uid;
                getPoiDetail(uid1, poiName, subTitle, bundle);

                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void eventBusMessage(MessageEvent myMessage){

        if (myMessage.message == true){
            onBackPressed();
        }

    }

    private void getPoiDetail(String uid, String poiName, String subTitle, Bundle bundle) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        poiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(uid));
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
                Point geo = poiDetailSearchResult.getPoiDetailInfoList().get(0).geo;
                bundle.putString("poiName", poiName);
                bundle.putString("subTitle", subTitle);
                bundle.putDouble("latitude" , geo.getDoubleY());
                bundle.putDouble("longitude" , geo.getDoubleX());
                bundle.putString("poiUid" , poiDetailSearchResult.getPoiDetailInfoList().get(0).uid);
                SharedPreferencesUtils.putBoolean(getActivity(), "isRoute", true);
                NavHostFragment.findNavController(RoutePlanFragment.this)
                        .navigate(R.id.action_routePlanFragment_to_recommendedRouteFragment, bundle);
            }
        });
    }
}
