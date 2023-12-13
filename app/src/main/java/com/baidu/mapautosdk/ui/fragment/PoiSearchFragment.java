package com.baidu.mapautosdk.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.map.AutoMapBound;
import com.baidu.mapautosdk.api.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapautosdk.api.search.poi.PoiBoundSearchOption;
import com.baidu.mapautosdk.api.search.poi.PoiCitySearchOption;
import com.baidu.mapautosdk.api.search.poi.PoiDetailSearchResult;
import com.baidu.mapautosdk.api.search.poi.PoiNearbySearchOption;
import com.baidu.mapautosdk.api.search.poi.PoiResult;
import com.baidu.mapautosdk.bean.AutoPoiInfo;
import com.baidu.mapautosdk.interfaces.search.IAutoPoiSearchManager;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.ui.adapter.PoiSearchAdapter;
import com.baidu.platform.comapi.basestruct.Point;
import com.baidu.platform.comapi.sdk.mapapi.model.LatLng;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PoiSearchFragment extends BaseFragment {
    private EditText etPoiInput;
    private ListView mListView;
    private PoiSearchAdapter mAdapter;
    private Handler mHandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_poi_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etPoiInput = view.findViewById(R.id.et_poi);
        view.findViewById(R.id.btn_poi_city_search).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_poi_point_search).setOnClickListener(clickListener);
        view.findViewById(R.id.btn_poi_area_search).setOnClickListener(clickListener);
        mListView = view.findViewById(R.id.list_view);
        mAdapter = new PoiSearchAdapter(getActivity());
        mListView.setAdapter(mAdapter);
    }

    private void poiCitySearch(String keyword) {
        IAutoPoiSearchManager mPoiSearch = BDAutoMapFactory.getAutoPoiSearchManager();
        mPoiSearch.searchInCity(new PoiCitySearchOption().keyword(keyword).city(131));
        mPoiSearch.setOnGetPoiSearchResultListener(resultListener);
    }

    private void poiPointSearch(String keyword) {
        IAutoPoiSearchManager mPoiSearch = BDAutoMapFactory.getAutoPoiSearchManager();
        mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword(keyword).radius(1000)
                .location(new LatLng(39.915446, 116.403869)));
        mPoiSearch.setOnGetPoiSearchResultListener(resultListener);
    }

    private void poiAreaSearch(String keyword) {
        IAutoPoiSearchManager mPoiSearch = BDAutoMapFactory.getAutoPoiSearchManager();
        mPoiSearch.setOnGetPoiSearchResultListener(resultListener);
        AutoMapBound areaBound = new AutoMapBound();
        areaBound.leftBottomPt = new Point(116.380338, 39.92235);
        areaBound.rightTopPt = new Point(116.414977, 39.947246);
        mPoiSearch.searchInBound(new PoiBoundSearchOption().keyword(keyword).searchAreaBound(areaBound));
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_poi_city_search:
                    poiCitySearch(etPoiInput.getText().toString());
                    break;
                case R.id.btn_poi_point_search:
                    poiPointSearch(etPoiInput.getText().toString());
                    break;
                case R.id.btn_poi_area_search:
                    poiAreaSearch(etPoiInput.getText().toString());
                    break;
                default:
                    break;
            }
        }
    };

    private OnGetPoiSearchResultListener resultListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult.isSuccessful()) {
                List<String> mDataList = new ArrayList<>();
                for (AutoPoiInfo poiInfo : poiResult.getAllPoi()) {
                    mDataList.add(poiInfo.name);
                }
                mAdapter.clear();
                mAdapter.addAll(mDataList);
            } else {
                Toast.makeText(getActivity(), "Fail error = " + poiResult.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }
    };
}
