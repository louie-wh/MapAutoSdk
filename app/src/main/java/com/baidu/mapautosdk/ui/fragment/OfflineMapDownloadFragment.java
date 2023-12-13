package com.baidu.mapautosdk.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.ui.BaseFragment;

public class OfflineMapDownloadFragment extends BaseFragment {

    private ViewGroup mContentView;
    private ViewPager vp;
    private TextView tvNo;
    private View viewNo;
    private TextView tvYes;
    private View viewYes;
    private NotDownloadedFragment notDownloadedFragment;
    private DownloadedFragment downloadedFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_offline_map_download, container, false);
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
        // 返回按钮
        view.findViewById(R.id.offline_map_download_img_back).setOnClickListener(clickListener);
        // 未下载
        view.findViewById(R.id.offline_map_download_ll_no).setOnClickListener(clickListener);
        // 已下载
        view.findViewById(R.id.offline_map_download_ll_yes).setOnClickListener(clickListener);
        tvNo = view.findViewById(R.id.offline_map_download_tv_no);
        viewNo = view.findViewById(R.id.offline_map_download_view_no);
        tvYes = view.findViewById(R.id.offline_map_download_tv_yes);
        viewYes = view.findViewById(R.id.offline_map_download_view_yes);
        vp = view.findViewById(R.id.offline_map_download_vp);

        initEvent();
        initViewPage(0);

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.offline_map_download_img_back:
                    onBackPressed();
                    break;
                case R.id.offline_map_download_ll_no:
                    initViewPage(0);
                    vp.setCurrentItem(0);
                    tvNo.setTextColor(Color.parseColor("#11131A"));
                    viewNo.setVisibility(View.VISIBLE);
                    tvYes.setTextColor(Color.parseColor("#505773"));
                    viewYes.setVisibility(View.GONE);
                    break;
                case R.id.offline_map_download_ll_yes:
                    initViewPage(1);
                    vp.setCurrentItem(1);
                    tvNo.setTextColor(Color.parseColor("#505773"));
                    viewNo.setVisibility(View.GONE);
                    tvYes.setTextColor(Color.parseColor("#11131A"));
                    viewYes.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    private void initEvent(){
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int currentItem = vp.getCurrentItem();
                switch (currentItem){
                    case 0:
                        tvNo.setTextColor(Color.parseColor("#11131A"));
                        viewNo.setVisibility(View.VISIBLE);
                        tvYes.setTextColor(Color.parseColor("#505773"));
                        viewYes.setVisibility(View.GONE);
                        break;
                    case 1:
                        tvNo.setTextColor(Color.parseColor("#505773"));
                        viewNo.setVisibility(View.GONE);
                        tvYes.setTextColor(Color.parseColor("#11131A"));
                        viewYes.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initViewPage(int i){
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragment(transaction);
        switch (i){
            case 0:
                if (notDownloadedFragment == null){
                    notDownloadedFragment = new NotDownloadedFragment();
                    transaction.add(R.id.offline_map_download_fl, notDownloadedFragment);
                }else {
                    transaction.show(notDownloadedFragment);
                }
                break;
            case 1:
                if (downloadedFragment == null){
                    downloadedFragment = new DownloadedFragment();
                    transaction.add(R.id.offline_map_download_fl, downloadedFragment);
                }else {
                    transaction.show(downloadedFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    /*
     * 隐藏所有的Fragment
     */
    private void hideFragment(FragmentTransaction transaction) {
        if (notDownloadedFragment != null) {
            transaction.hide(notDownloadedFragment);
        }
        if (downloadedFragment != null) {
            transaction.hide(downloadedFragment);
        }
    }

    public boolean onBackPressed() {
        NavHostFragment.findNavController(OfflineMapDownloadFragment.this).popBackStack();
        return false;
    }
}