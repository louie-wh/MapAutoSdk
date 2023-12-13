package com.baidu.mapautosdk.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapautosdk.MessageEvent;
import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.base.CoordType;
import com.baidu.mapautosdk.api.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapautosdk.api.search.poi.PoiDetailSearchOption;
import com.baidu.mapautosdk.api.search.poi.PoiDetailSearchResult;
import com.baidu.mapautosdk.api.search.poi.PoiResult;
import com.baidu.mapautosdk.api.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapautosdk.api.search.sug.SuggestionResult;
import com.baidu.mapautosdk.api.search.sug.SuggestionSearch;
import com.baidu.mapautosdk.api.search.sug.SuggestionSearchOption;
import com.baidu.mapautosdk.interfaces.search.IAutoPoiSearchManager;
import com.baidu.mapautosdk.model.AutoLocData;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.ui.adapter.SearchResultAdapter;
import com.baidu.mapautosdk.ui.presenter.SearchResultPresenter;
import com.baidu.platform.comapi.basestruct.Point;
import com.baidu.tts.tools.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SearchResultFragment extends BaseFragment implements SearchResultAdapter.InnerItemOnclickListener {
    private ViewGroup mContentView;
    private String etText;
    private TextView searchResultEt;
    private SearchResultPresenter searchResultPresenter;
    private IAutoPoiSearchManager poiSearch;
    private AutoLocData curLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null){
            etText = arguments.getString("etText");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        boolean registered = EventBus.getDefault().isRegistered(this);
        if (registered == false){
            EventBus.getDefault().register(this);
        }
        BDAutoMapFactory.getAutoRouteResultManager().onCreate(getContext());
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_search_result, container, false);
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

        searchResultPresenter = new SearchResultPresenter();
        searchResultPresenter.initListView(view);
        searchResultPresenter.setOnItemClickListener(itemClickListener);
        searchResultPresenter.setInnerItemOnclickListener(this);

        suggestionSearch(etText);
        view.findViewById(R.id.search_result_img_back).setOnClickListener(clickListener);
        searchResultEt = view.findViewById(R.id.search_result_et);
        searchResultEt.setText(etText);

        poiSearch = BDAutoMapFactory.getAutoPoiSearchManager();
    }

    private void suggestionSearch(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            searchResultPresenter.setData(null);
            return;
        }
        curLocation = BDAutoMapFactory.getAutoLocationManager().getCurLocation(CoordType.BD09LL);
        int cityId = SharedPreferencesUtils.getInt(getActivity(), "cityId", 131);
        SuggestionSearch suggestionSearch = SuggestionSearch.newInstance();
        suggestionSearch.setOnGetSuggestionResultListener(resultListener);
        suggestionSearch.requestSuggestion(new SuggestionSearchOption()
                .cityId(cityId)
                .keyword(keyword));
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
                        searchResultPresenter.setData(suggestionResult);
                    } else {
                        Toast.makeText(getActivity(), "fail" + suggestionResult.getErrorCode(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.search_result_img_back:
                    onBackPressed();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onBackPressed() {
        NavHostFragment.findNavController(SearchResultFragment.this).popBackStack();
        return false;
    }

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

    @Override
    public void itemClick(View v) {
        Bundle bundle = new Bundle();
        SuggestionResult resultData = searchResultPresenter.getResultData();
        int position;
        position = (int) v.getTag();
        switch (v.getId()){
            case R.id.item_search_result_rl_go_poi:

                String uid = resultData.getSugPoiList().get(position).uid;
                int cityId = resultData.getSugPoiList().get(position).cityId;
                String poiNamePoi = resultData.getSugPoiList().get(position).poiName;
                bundle.putString("uid", uid);
                bundle.putInt("cityId", cityId);
                bundle.putString("poiName", poiNamePoi);
                NavHostFragment.findNavController(SearchResultFragment.this)
                        .navigate(R.id.action_routePlanFragment_to_poiDetailsFragment, bundle);

                break;
            case R.id.item_search_result_img_go_here:

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
                SharedPreferencesUtils.putBoolean(getActivity(), "isRoute", true);
                NavHostFragment.findNavController(SearchResultFragment.this)
                        .navigate(R.id.action_routePlanFragment_to_recommendedRouteFragment, bundle);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void eventBusMessage(MessageEvent myMessage){

        if (myMessage.message == true){
            onBackPressed();
        }

    }
}