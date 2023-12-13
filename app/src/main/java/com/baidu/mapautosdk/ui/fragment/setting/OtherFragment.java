package com.baidu.mapautosdk.ui.fragment.setting;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.ui.BaseFragment;

public class OtherFragment extends BaseFragment {
    private ViewGroup mContentView;
    private ImageView imgSwitch;
    private TextView tvReduce;
    private TextView tvSuspend;
    private RelativeLayout rlClear;
    private RelativeLayout rlAbout;
    private TextView tvLoginOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_other, container, false);
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
        // 通话时收听语音播报
        imgSwitch = view.findViewById(R.id.other_img_switch);
        imgSwitch.setOnClickListener(clickListener);
        // 降低音乐
        tvReduce = view.findViewById(R.id.other_tv_reduce);
        tvReduce.setOnClickListener(clickListener);
        // 暂停音乐
        tvSuspend = view.findViewById(R.id.other_tv_suspend);
        tvSuspend.setOnClickListener(clickListener);
        // 清除缓存
        rlClear = view.findViewById(R.id.other_rl_clear);
        // 关于百度地图
        rlAbout = view.findViewById(R.id.other_rl_about);
        // 退出登录
        tvLoginOut = view.findViewById(R.id.other_tv_login_out);
    }

    private boolean isSwitch = false;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.other_img_switch:
                    if (isSwitch == false){
                        imgSwitch.setImageResource(R.mipmap.ic_switch_open_white);
                        isSwitch = true;
                    }else {
                        imgSwitch.setImageResource(R.mipmap.ic_switch_close_white);
                        isSwitch = false;
                    }
                    break;
                case R.id.other_tv_reduce:
                    tvReduce.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvReduce.setTextColor(Color.parseColor("#FFFFFF"));
                    tvSuspend.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvSuspend.setTextColor(Color.parseColor("#11131A"));
                    break;
                case R.id.other_tv_suspend:
                    tvReduce.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvReduce.setTextColor(Color.parseColor("#11131A"));
                    tvSuspend.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvSuspend.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onBackPressed() {
        NavHostFragment.findNavController(OtherFragment.this).popBackStack();
        return false;
    }
}