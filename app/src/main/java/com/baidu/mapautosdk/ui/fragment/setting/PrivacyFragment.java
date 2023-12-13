package com.baidu.mapautosdk.ui.fragment.setting;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.ui.BaseFragment;

public class PrivacyFragment extends BaseFragment {
    private ViewGroup mContentView;
    private ImageView imgSearchRecords;
    private ImageView imgNavigationTrack;
    private ImageView imgLocalFootprint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_privacy, container, false);
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
        // 搜索记录同步
        imgSearchRecords = view.findViewById(R.id.route_img_search_records);
        imgSearchRecords.setOnClickListener(clickListener);
        // 导航轨迹同步
        imgNavigationTrack = view.findViewById(R.id.route_img_navigation_track);
        imgNavigationTrack.setOnClickListener(clickListener);
        // 本地足迹同步
        imgLocalFootprint = view.findViewById(R.id.route_img_local_footprint);
        imgLocalFootprint.setOnClickListener(clickListener);
    }

    private boolean isSearchRecords = false;
    private boolean isNavigationTrack = false;
    private boolean isLocalFootprint = false;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.route_img_search_records:
                    if (isSearchRecords == false){
                        imgSearchRecords.setImageResource(R.mipmap.ic_switch_open_white);
                        isSearchRecords = true;
                    }else {
                        imgSearchRecords.setImageResource(R.mipmap.ic_switch_close_white);
                        isSearchRecords = false;
                    }
                    break;
                case R.id.route_img_navigation_track:
                    if (isNavigationTrack == false){
                        imgNavigationTrack.setImageResource(R.mipmap.ic_switch_open_white);
                        isNavigationTrack = true;
                    }else {
                        imgNavigationTrack.setImageResource(R.mipmap.ic_switch_close_white);
                        isNavigationTrack = false;
                    }
                    break;
                case R.id.route_img_local_footprint:
                    if (isLocalFootprint == false){
                        imgLocalFootprint.setImageResource(R.mipmap.ic_switch_open_white);
                        isLocalFootprint = true;
                    }else {
                        imgLocalFootprint.setImageResource(R.mipmap.ic_switch_close_white);
                        isLocalFootprint = false;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onBackPressed() {
        NavHostFragment.findNavController(PrivacyFragment.this).popBackStack();
        return false;
    }
}