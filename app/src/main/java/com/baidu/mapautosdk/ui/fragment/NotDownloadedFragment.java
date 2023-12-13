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
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.ui.BaseFragment;

public class NotDownloadedFragment extends BaseFragment {

    private ViewGroup mContentView;
    private TextView tvDomestic;
    private TextView tvInternational;
    private ViewPager vp;
    private DomesticFragment domesticFragment;
    private InternationalFragment internationalFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_not_downloaded, container, false);
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
        // 输入框
        EditText et = view.findViewById(R.id.not_downloaded_et);
        // 国内
        tvDomestic = view.findViewById(R.id.not_downloaded_tv_domestic);
        tvDomestic.setOnClickListener(clickListener);
        // 国际
        tvInternational = view.findViewById(R.id.not_downloaded_tv_international);
        tvInternational.setOnClickListener(clickListener);
        vp = view.findViewById(R.id.not_downloaded_vp);

        initEvent();
        initViewPage(0);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.not_downloaded_tv_domestic:
                    initViewPage(0);
                    vp.setCurrentItem(0);
                    tvDomestic.setTextColor(Color.parseColor("#FFFFFF"));
                    tvDomestic.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvInternational.setTextColor(Color.parseColor("#11131A"));
                    tvInternational.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    break;
                case R.id.not_downloaded_tv_international:
                    initViewPage(1);
                    vp.setCurrentItem(1);
                    tvDomestic.setTextColor(Color.parseColor("#11131A"));
                    tvDomestic.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvInternational.setTextColor(Color.parseColor("#FFFFFF"));
                    tvInternational.setBackgroundResource(R.drawable.not_downloaded_city_bg);
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
                        tvDomestic.setTextColor(Color.parseColor("#FFFFFF"));
                        tvDomestic.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                        tvInternational.setTextColor(Color.parseColor("#11131A"));
                        tvInternational.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                        break;
                    case 1:
                        tvDomestic.setTextColor(Color.parseColor("#11131A"));
                        tvDomestic.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                        tvInternational.setTextColor(Color.parseColor("#FFFFFF"));
                        tvInternational.setBackgroundResource(R.drawable.not_downloaded_city_bg);
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
                if (domesticFragment == null){
                    domesticFragment = new DomesticFragment();
                    transaction.add(R.id.not_downloaded_fl, domesticFragment);
                }else {
                    transaction.show(domesticFragment);
                }
                break;
            case 1:
                if (internationalFragment == null){
                    internationalFragment = new InternationalFragment();
                    transaction.add(R.id.not_downloaded_fl, internationalFragment);
                }else {
                    transaction.show(internationalFragment);
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
        if (domesticFragment != null) {
            transaction.hide(domesticFragment);
        }
        if (internationalFragment != null) {
            transaction.hide(internationalFragment);
        }
    }

    public boolean onBackPressed() {
        NavHostFragment.findNavController(NotDownloadedFragment.this).popBackStack();
        return false;
    }
}