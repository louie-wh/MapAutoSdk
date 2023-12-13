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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.ui.fragment.setting.BroadcastingFragment;
import com.baidu.mapautosdk.ui.fragment.setting.NavigationFragment;
import com.baidu.mapautosdk.ui.fragment.setting.OtherFragment;
import com.baidu.mapautosdk.ui.fragment.setting.PrivacyFragment;
import com.baidu.mapautosdk.ui.fragment.setting.RouteFragment;

public class SettingFragment extends BaseFragment {
    private ViewGroup mContentView;
    private LinearLayout llRoute;
    private ImageView imgRoute;
    private TextView tvRoute;
    private LinearLayout llNavigation;
    private ImageView imgNavigation;
    private TextView tvNavigation;
    private LinearLayout llBroadcasting;
    private ImageView imgBroadcasting;
    private TextView tvBroadcasting;
    private LinearLayout llPrivacy;
    private ImageView imgPrivacy;
    private TextView tvPrivacy;
    private LinearLayout llOther;
    private ImageView imgOther;
    private TextView tvOther;
    private ViewPager vp;
    private RouteFragment routeFragment;
    private NavigationFragment navigationFragment;
    private BroadcastingFragment broadcastingFragment;
    private PrivacyFragment privacyFragment;
    private OtherFragment otherFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_setting, container, false);
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
        view.findViewById(R.id.setting_img_back).setOnClickListener(clickListener);
        // 路线
        llRoute = view.findViewById(R.id.setting_ll_route);
        llRoute.setOnClickListener(clickListener);
        imgRoute = view.findViewById(R.id.setting_img_route);
        tvRoute = view.findViewById(R.id.setting_tv_route);
        // 导航
        llNavigation = view.findViewById(R.id.setting_ll_navigation);
        llNavigation.setOnClickListener(clickListener);
        imgNavigation = view.findViewById(R.id.setting_img_navigation);
        tvNavigation = view.findViewById(R.id.setting_tv_navigation);
        // 播报
        llBroadcasting = view.findViewById(R.id.setting_ll_broadcasting);
        llBroadcasting.setOnClickListener(clickListener);
        imgBroadcasting = view.findViewById(R.id.setting_img_broadcasting);
        tvBroadcasting = view.findViewById(R.id.setting_tv_broadcasting);
        // 隐私
        llPrivacy = view.findViewById(R.id.setting_ll_privacy);
        llPrivacy.setOnClickListener(clickListener);
        imgPrivacy = view.findViewById(R.id.setting_img_privacy);
        tvPrivacy = view.findViewById(R.id.setting_tv_privacy);
        // 其他
        llOther = view.findViewById(R.id.setting_ll_other);
        llOther.setOnClickListener(clickListener);
        imgOther = view.findViewById(R.id.setting_img_other);
        tvOther = view.findViewById(R.id.setting_tv_other);

        vp = view.findViewById(R.id.setting_vp);

        initEvent();
        initViewPage(0);
    }

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
                        llRoute.setBackgroundColor(Color.parseColor("#F2F4F7"));
                        imgRoute.setImageResource(R.mipmap.ic_route_s_white);
                        tvRoute.setTextColor(Color.parseColor("#3366FF"));
                        llNavigation.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgNavigation.setImageResource(R.mipmap.ic_navi_n_white);
                        tvNavigation.setTextColor(Color.parseColor("#11131A"));
                        llBroadcasting.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgBroadcasting.setImageResource(R.mipmap.ic_broadcast_setting_n_white);
                        tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
                        llPrivacy.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgPrivacy.setImageResource(R.mipmap.ic_privacy_n_white);
                        tvPrivacy.setTextColor(Color.parseColor("#11131A"));
                        llOther.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgOther.setImageResource(R.mipmap.ic_setting_n_white);
                        tvOther.setTextColor(Color.parseColor("#11131A"));
                        break;
                    case 1:
                        llRoute.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgRoute.setImageResource(R.mipmap.ic_route_n_white);
                        tvRoute.setTextColor(Color.parseColor("#11131A"));
                        llNavigation.setBackgroundColor(Color.parseColor("#F2F4F7"));
                        imgNavigation.setImageResource(R.mipmap.ic_navi_s_white);
                        tvNavigation.setTextColor(Color.parseColor("#3366FF"));
                        llBroadcasting.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgBroadcasting.setImageResource(R.mipmap.ic_broadcast_setting_n_white);
                        tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
                        llPrivacy.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgPrivacy.setImageResource(R.mipmap.ic_privacy_n_white);
                        tvPrivacy.setTextColor(Color.parseColor("#11131A"));
                        llOther.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgOther.setImageResource(R.mipmap.ic_setting_n_white);
                        tvOther.setTextColor(Color.parseColor("#11131A"));
                        break;
                    case 2:
                        llRoute.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgRoute.setImageResource(R.mipmap.ic_route_n_white);
                        tvRoute.setTextColor(Color.parseColor("#11131A"));
                        llNavigation.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgNavigation.setImageResource(R.mipmap.ic_navi_n_white);
                        tvNavigation.setTextColor(Color.parseColor("#11131A"));
                        llBroadcasting.setBackgroundColor(Color.parseColor("#F2F4F7"));
                        imgBroadcasting.setImageResource(R.mipmap.ic_broadcast_setting_s_white);
                        tvBroadcasting.setTextColor(Color.parseColor("#3366FF"));
                        llPrivacy.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgPrivacy.setImageResource(R.mipmap.ic_privacy_n_white);
                        tvPrivacy.setTextColor(Color.parseColor("#11131A"));
                        llOther.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgOther.setImageResource(R.mipmap.ic_setting_n_white);
                        tvOther.setTextColor(Color.parseColor("#11131A"));
                        break;
                    case 3:
                        llRoute.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgRoute.setImageResource(R.mipmap.ic_route_n_white);
                        tvRoute.setTextColor(Color.parseColor("#11131A"));
                        llNavigation.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgNavigation.setImageResource(R.mipmap.ic_navi_n_white);
                        tvNavigation.setTextColor(Color.parseColor("#11131A"));
                        llBroadcasting.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgBroadcasting.setImageResource(R.mipmap.ic_broadcast_setting_n_white);
                        tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
                        llPrivacy.setBackgroundColor(Color.parseColor("#F2F4F7"));
                        imgPrivacy.setImageResource(R.mipmap.ic_privacy_s_white);
                        tvPrivacy.setTextColor(Color.parseColor("#3366FF"));
                        llOther.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgOther.setImageResource(R.mipmap.ic_setting_n_white);
                        tvOther.setTextColor(Color.parseColor("#11131A"));
                        break;
                    case 4:
                        llRoute.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgRoute.setImageResource(R.mipmap.ic_route_n_white);
                        tvRoute.setTextColor(Color.parseColor("#11131A"));
                        llNavigation.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgNavigation.setImageResource(R.mipmap.ic_navi_n_white);
                        tvNavigation.setTextColor(Color.parseColor("#11131A"));
                        llBroadcasting.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgBroadcasting.setImageResource(R.mipmap.ic_broadcast_setting_n_white);
                        tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
                        llPrivacy.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        imgPrivacy.setImageResource(R.mipmap.ic_privacy_n_white);
                        tvPrivacy.setTextColor(Color.parseColor("#11131A"));
                        llOther.setBackgroundColor(Color.parseColor("#F2F4F7"));
                        imgOther.setImageResource(R.mipmap.ic_setting_s_white);
                        tvOther.setTextColor(Color.parseColor("#3366FF"));
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
                if (routeFragment == null){
                    routeFragment = new RouteFragment();
                    transaction.add(R.id.setting_fl, routeFragment);
                }else {
                    transaction.show(routeFragment);
                }
                break;
            case 1:
                if (navigationFragment == null){
                    navigationFragment = new NavigationFragment();
                    transaction.add(R.id.setting_fl, navigationFragment);
                }else {
                    transaction.show(navigationFragment);
                }
                break;
            case 2:
                if (broadcastingFragment == null){
                    broadcastingFragment = new BroadcastingFragment();
                    transaction.add(R.id.setting_fl, broadcastingFragment);
                }else {
                    transaction.show(broadcastingFragment);
                }
                break;
            case 3:
                if (privacyFragment == null){
                    privacyFragment = new PrivacyFragment();
                    transaction.add(R.id.setting_fl, privacyFragment);
                }else {
                    transaction.show(privacyFragment);
                }
                break;
            case 4:
                if (otherFragment == null){
                    otherFragment = new OtherFragment();
                    transaction.add(R.id.setting_fl, otherFragment);
                }else {
                    transaction.show(otherFragment);
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
        if (routeFragment != null) {
            transaction.hide(routeFragment);
        }
        if (navigationFragment != null) {
            transaction.hide(navigationFragment);
        }
        if (broadcastingFragment != null) {
            transaction.hide(broadcastingFragment);
        }
        if (privacyFragment != null) {
            transaction.hide(privacyFragment);
        }
        if (otherFragment != null) {
            transaction.hide(otherFragment);
        }
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.setting_img_back:
                    onBackPressed();
                    break;
                case R.id.setting_ll_route:
                    initViewPage(0);
                    vp.setCurrentItem(0);
                    llRoute.setBackgroundColor(Color.parseColor("#F2F4F7"));
                    imgRoute.setImageResource(R.mipmap.ic_route_s_white);
                    tvRoute.setTextColor(Color.parseColor("#3366FF"));
                    llNavigation.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgNavigation.setImageResource(R.mipmap.ic_navi_n_white);
                    tvNavigation.setTextColor(Color.parseColor("#11131A"));
                    llBroadcasting.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgBroadcasting.setImageResource(R.mipmap.ic_broadcast_setting_n_white);
                    tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
                    llPrivacy.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgPrivacy.setImageResource(R.mipmap.ic_privacy_n_white);
                    tvPrivacy.setTextColor(Color.parseColor("#11131A"));
                    llOther.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgOther.setImageResource(R.mipmap.ic_setting_n_white);
                    tvOther.setTextColor(Color.parseColor("#11131A"));
                    break;
                case R.id.setting_ll_navigation:
                    initViewPage(1);
                    vp.setCurrentItem(1);
                    llRoute.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgRoute.setImageResource(R.mipmap.ic_route_n_white);
                    tvRoute.setTextColor(Color.parseColor("#11131A"));
                    llNavigation.setBackgroundColor(Color.parseColor("#F2F4F7"));
                    imgNavigation.setImageResource(R.mipmap.ic_navi_s_white);
                    tvNavigation.setTextColor(Color.parseColor("#3366FF"));
                    llBroadcasting.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgBroadcasting.setImageResource(R.mipmap.ic_broadcast_setting_n_white);
                    tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
                    llPrivacy.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgPrivacy.setImageResource(R.mipmap.ic_privacy_n_white);
                    tvPrivacy.setTextColor(Color.parseColor("#11131A"));
                    llOther.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgOther.setImageResource(R.mipmap.ic_setting_n_white);
                    tvOther.setTextColor(Color.parseColor("#11131A"));
                    break;
                case R.id.setting_ll_broadcasting:
                    initViewPage(2);
                    vp.setCurrentItem(2);
                    llRoute.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgRoute.setImageResource(R.mipmap.ic_route_n_white);
                    tvRoute.setTextColor(Color.parseColor("#11131A"));
                    llNavigation.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgNavigation.setImageResource(R.mipmap.ic_navi_n_white);
                    tvNavigation.setTextColor(Color.parseColor("#11131A"));
                    llBroadcasting.setBackgroundColor(Color.parseColor("#F2F4F7"));
                    imgBroadcasting.setImageResource(R.mipmap.ic_broadcast_setting_s_white);
                    tvBroadcasting.setTextColor(Color.parseColor("#3366FF"));
                    llPrivacy.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgPrivacy.setImageResource(R.mipmap.ic_privacy_n_white);
                    tvPrivacy.setTextColor(Color.parseColor("#11131A"));
                    llOther.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgOther.setImageResource(R.mipmap.ic_setting_n_white);
                    tvOther.setTextColor(Color.parseColor("#11131A"));
                    break;
                case R.id.setting_ll_privacy:
                    initViewPage(3);
                    vp.setCurrentItem(3);
                    llRoute.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgRoute.setImageResource(R.mipmap.ic_route_n_white);
                    tvRoute.setTextColor(Color.parseColor("#11131A"));
                    llNavigation.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgNavigation.setImageResource(R.mipmap.ic_navi_n_white);
                    tvNavigation.setTextColor(Color.parseColor("#11131A"));
                    llBroadcasting.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgBroadcasting.setImageResource(R.mipmap.ic_broadcast_setting_n_white);
                    tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
                    llPrivacy.setBackgroundColor(Color.parseColor("#F2F4F7"));
                    imgPrivacy.setImageResource(R.mipmap.ic_privacy_s_white);
                    tvPrivacy.setTextColor(Color.parseColor("#3366FF"));
                    llOther.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgOther.setImageResource(R.mipmap.ic_setting_n_white);
                    tvOther.setTextColor(Color.parseColor("#11131A"));
                    break;
                case R.id.setting_ll_other:
                    initViewPage(4);
                    vp.setCurrentItem(4);
                    llRoute.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgRoute.setImageResource(R.mipmap.ic_route_n_white);
                    tvRoute.setTextColor(Color.parseColor("#11131A"));
                    llNavigation.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgNavigation.setImageResource(R.mipmap.ic_navi_n_white);
                    tvNavigation.setTextColor(Color.parseColor("#11131A"));
                    llBroadcasting.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgBroadcasting.setImageResource(R.mipmap.ic_broadcast_setting_n_white);
                    tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
                    llPrivacy.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    imgPrivacy.setImageResource(R.mipmap.ic_privacy_n_white);
                    tvPrivacy.setTextColor(Color.parseColor("#11131A"));
                    llOther.setBackgroundColor(Color.parseColor("#F2F4F7"));
                    imgOther.setImageResource(R.mipmap.ic_setting_s_white);
                    tvOther.setTextColor(Color.parseColor("#3366FF"));
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onBackPressed() {
        NavHostFragment.findNavController(SettingFragment.this).popBackStack();
        return false;
    }
}