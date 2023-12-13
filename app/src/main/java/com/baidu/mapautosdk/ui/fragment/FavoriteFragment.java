package com.baidu.mapautosdk.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baidu.mapautosdk.MessageEvent;
import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.search.ugc.UgcFavoriteOption;
import com.baidu.mapautosdk.interfaces.ugc.IAutoUgcManager;
import com.baidu.mapautosdk.model.AutoFavoriteInfo;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.ui.adapter.FavoriteAdapter;
import com.baidu.mapautosdk.util.SpUtil;
import com.baidu.tts.tools.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class FavoriteFragment extends BaseFragment {
    private ViewGroup mContentView;
    private RelativeLayout rlNotData;
    private RecyclerView rlv;
    private ImageView imgRefresh;
    private FavoriteAdapter favoriteAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_favorite, container, false);
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

        view.findViewById(R.id.favorite_img_back).setOnClickListener(clickListener);
        // 没有收藏点
        rlNotData = view.findViewById(R.id.favorite_rl_not_data);
        rlv = view.findViewById(R.id.favorite_rlv);
        // 刷新
        imgRefresh = view.findViewById(R.id.favorite_img_refresh);
        String openid = SpUtil.getInStace().getString("openid", "");
        initData(openid);
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData(openid);
            }
        });
    }

    private void initData(String openid) {
        BDAutoMapFactory.getAutoUgcManager().requestFavoritesInfo(new UgcFavoriteOption().setOpenid(openid).setPage(1).setSize(10));
        BDAutoMapFactory.getAutoUgcManager().setOnFavoritesResultListener(new IAutoUgcManager.OnFavoritesResultListener() {
            @Override
            public void onGetFavoritesResult(ArrayList<AutoFavoriteInfo> info) {
                if (info != null && info.size() > 0) {
                    rlNotData.setVisibility(View.GONE);
                    favoriteAdapter = new FavoriteAdapter(getContext(), info);
                    rlv.setLayoutManager(new LinearLayoutManager(getContext()));
                    rlv.setAdapter(favoriteAdapter);

                    favoriteAdapter.setOnItemClickListener(new FavoriteAdapter.OnItemClickListener() {
                        @Override
                        public void onGoPoiClick(int position) {
                            Bundle bundle = new Bundle();
                            AutoFavoriteInfo dataInfos = info.get(position);
                            EventBus.getDefault().postSticky(new MessageEvent(false));
                            if (!TextUtils.isEmpty(dataInfos.getRename())){
                                bundle.putString("uid", dataInfos.getUid());
                                bundle.putString("poiName", dataInfos.getRename());
                                bundle.putDouble("latitude", dataInfos.getLatLng().latitude);
                                bundle.putDouble("longitude", dataInfos.getLatLng().longitude);
                                NavHostFragment.findNavController(FavoriteFragment.this)
                                        .navigate(R.id.action_routePlanFragment_to_poiDetailsFragment, bundle);
                            }else {
                                bundle.putString("uid", dataInfos.getUid());
                                bundle.putString("poiName", dataInfos.getName());
                                NavHostFragment.findNavController(FavoriteFragment.this)
                                        .navigate(R.id.action_routePlanFragment_to_poiDetailsFragment, bundle);
                            }
                        }

                        @Override
                        public void onGoHereClick(int position) {
                            Bundle bundle = new Bundle();
                            AutoFavoriteInfo dataInfos = info.get(position);
                            EventBus.getDefault().postSticky(new MessageEvent(false));
                            if (!TextUtils.isEmpty(dataInfos.getRename())) {
                                bundle.putString("poiName", dataInfos.getRename());
                                bundle.putDouble("latitude", dataInfos.getLatLng().latitude);
                                bundle.putDouble("longitude", dataInfos.getLatLng().longitude);
                                SharedPreferencesUtils.putBoolean(getActivity(), "isRoute", true);
                                NavHostFragment.findNavController(FavoriteFragment.this)
                                        .navigate(R.id.action_routePlanFragment_to_recommendedRouteFragment, bundle);
                            }else {
                                bundle.putString("poiName", dataInfos.getName());
                                bundle.putDouble("latitude", dataInfos.getLatLng().latitude);
                                bundle.putDouble("longitude", dataInfos.getLatLng().longitude);
                                SharedPreferencesUtils.putBoolean(getActivity(), "isRoute", true);
                                NavHostFragment.findNavController(FavoriteFragment.this)
                                        .navigate(R.id.action_routePlanFragment_to_recommendedRouteFragment, bundle);
                            }
                        }
                    });
                }else {
                    rlNotData.setVisibility(View.VISIBLE);
                }
            }
        });
//        BDAutoMapFactory.getAutoUgcManager().getFavoritesInfo(openid, 1, 10,
//                new IAutoUgcManager.OnFavoritesResultListener() {
//                    @Override
//                    public void onGetFavoritesResult(ArrayList<AutoFavoriteInfo> info) {
//                        if (info != null && info.size() > 0) {
//                            rlNotData.setVisibility(View.GONE);
//                            favoriteAdapter = new FavoriteAdapter(getContext(), info);
//                            rlv.setLayoutManager(new LinearLayoutManager(getContext()));
//                            rlv.setAdapter(favoriteAdapter);
//
//                            favoriteAdapter.setOnItemClickListener(new FavoriteAdapter.OnItemClickListener() {
//                                @Override
//                                public void onGoPoiClick(int position) {
//                                    Bundle bundle = new Bundle();
//                                    AutoFavoriteInfo dataInfos = info.get(position);
//                                    EventBus.getDefault().postSticky(new MessageEvent(false));
//                                    if (!TextUtils.isEmpty(dataInfos.getRename())){
//                                        bundle.putString("uid", dataInfos.getUid());
//                                        bundle.putString("poiName", dataInfos.getRename());
//                                        bundle.putDouble("latitude", dataInfos.getLatLng().latitude);
//                                        bundle.putDouble("longitude", dataInfos.getLatLng().longitude);
//                                        NavHostFragment.findNavController(FavoriteFragment.this)
//                                                .navigate(R.id.action_routePlanFragment_to_poiDetailsFragment, bundle);
//                                    }else {
//                                        bundle.putString("uid", dataInfos.getUid());
//                                        bundle.putString("poiName", dataInfos.getName());
//                                        NavHostFragment.findNavController(FavoriteFragment.this)
//                                                .navigate(R.id.action_routePlanFragment_to_poiDetailsFragment, bundle);
//                                    }
//                                }
//
//                                @Override
//                                public void onGoHereClick(int position) {
//                                    Bundle bundle = new Bundle();
//                                    AutoFavoriteInfo dataInfos = info.get(position);
//                                    EventBus.getDefault().postSticky(new MessageEvent(false));
//                                    if (!TextUtils.isEmpty(dataInfos.getRename())) {
//                                        bundle.putString("poiName", dataInfos.getRename());
//                                        bundle.putDouble("latitude", dataInfos.getLatLng().latitude);
//                                        bundle.putDouble("longitude", dataInfos.getLatLng().longitude);
//                                        SharedPreferencesUtils.putBoolean(getActivity(), "isRoute", true);
//                                        NavHostFragment.findNavController(FavoriteFragment.this)
//                                                .navigate(R.id.action_routePlanFragment_to_recommendedRouteFragment, bundle);
//                                    }else {
//                                        bundle.putString("poiName", dataInfos.getName());
//                                        bundle.putDouble("latitude", dataInfos.getLatLng().latitude);
//                                        bundle.putDouble("longitude", dataInfos.getLatLng().longitude);
//                                        SharedPreferencesUtils.putBoolean(getActivity(), "isRoute", true);
//                                        NavHostFragment.findNavController(FavoriteFragment.this)
//                                                .navigate(R.id.action_routePlanFragment_to_recommendedRouteFragment, bundle);
//                                    }
//                                }
//                            });
//                        }else {
//                            rlNotData.setVisibility(View.VISIBLE);
//                        }
//                    }
//                });
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.favorite_img_back:
                    onBackPressed();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onBackPressed() {
        NavHostFragment.findNavController(FavoriteFragment.this).popBackStack();
        return false;
    }
}