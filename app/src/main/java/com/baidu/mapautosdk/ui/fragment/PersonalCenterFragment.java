package com.baidu.mapautosdk.ui.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.ui.BaseFragment;

public class PersonalCenterFragment extends BaseFragment {

    private ViewGroup mContentView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_personal_center, container, false);
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
        view.findViewById(R.id.personal_center_popup_img_back).setOnClickListener(clickListener); // 返回按钮
        view.findViewById(R.id.personal_center_popup_tv_car).setOnClickListener(clickListener); // 添加爱车
        view.findViewById(R.id.personal_center_popup_ll_setting).setOnClickListener(clickListener); // 设置
        view.findViewById(R.id.personal_center_popup_ll_map).setOnClickListener(clickListener); // 离线地图
        view.findViewById(R.id.personal_center_popup_ll_team).setOnClickListener(clickListener); // 组队出行
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            switch (v.getId()) {
                case R.id.personal_center_popup_img_back:
                    onBackPressed();
                    break;
                case R.id.personal_center_popup_tv_car:
                    NavHostFragment.findNavController(PersonalCenterFragment.this)
                            .navigate(R.id.action_mainFragment_to_myCarFragment);
                    break;
                case R.id.personal_center_popup_ll_setting:
                    NavHostFragment.findNavController(PersonalCenterFragment.this)
                            .navigate(R.id.action_mainFragment_to_settingFragment);
                    break;
                case R.id.personal_center_popup_ll_map:
                    NavHostFragment.findNavController(PersonalCenterFragment.this)
                            .navigate(R.id.action_mainFragment_to_offlineMapPage);
                    break;
                case R.id.personal_center_popup_ll_team:
                    NavHostFragment.findNavController(PersonalCenterFragment.this)
                            .navigate(R.id.action_mainFragment_to_teamUpFragment);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onBackPressed() {
        NavHostFragment.findNavController(PersonalCenterFragment.this).popBackStack();
        return false;
    }
}