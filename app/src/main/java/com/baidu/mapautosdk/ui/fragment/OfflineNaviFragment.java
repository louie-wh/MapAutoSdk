package com.baidu.mapautosdk.ui.fragment;

import com.baidu.mapautosdk.api.offline.AutoOfflineNaviDownloadManager;
import com.baidu.mapautosdk.ui.BaseFragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 离线地图 接口测试fragment
 */
public class OfflineNaviFragment extends BaseFragment {
    private AutoOfflineNaviDownloadManager mDownloadUiManager;

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDownloadUiManager = new AutoOfflineNaviDownloadManager();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = mDownloadUiManager.onCreate(getActivity());
        mDownloadUiManager.setOnBackPressedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().onBackPressed();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDownloadUiManager.onResume();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDownloadUiManager.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDownloadUiManager.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloadUiManager.onDestroy();
    }
}
