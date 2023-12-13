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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviSettingManager;
import com.baidu.mapautosdk.interfaces.navi.IAutoNaviSettingParams;
import com.baidu.mapautosdk.ui.BaseFragment;

public class BroadcastingFragment extends BaseFragment {
    private ViewGroup mContentView;
    private ImageView imgSwitch;
    private TextView tvBroadcasting;
    private TextView tvMute;
    private TextView tvTips;
    private TextView tvStandard;
    private TextView tvConcise;
    private TextView tvContentTips;
    private ImageView imgVolume;
    private ImageView imgVolumeReduce;
    private ProgressBar pbVolume;
    private ImageView imgVolumeAdd;
    private int volume = 0;
    private IAutoNaviSettingManager.IProfessionalNaviSetting mSetting;
    private int mVehicleType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_broadcasting, container, false);
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
        // 获取车辆类型
        mVehicleType = BDAutoMapFactory.getNaviCommonSettingManager().getVehicleType();

        mSetting = BDAutoMapFactory.getProfessionalNaviSettingManager();

        // 在线算路优先
        imgSwitch = view.findViewById(R.id.broadcasting_img_online_route_calculation);
        imgSwitch.setOnClickListener(clickListener);
        // 导航播报
        tvBroadcasting = view.findViewById(R.id.broadcasting_tv_broadcasting);
        tvBroadcasting.setOnClickListener(clickListener);
        // 导航静音
        tvMute = view.findViewById(R.id.broadcasting_tv_mute);
        tvMute.setOnClickListener(clickListener);
        // 仅提示音
        tvTips = view.findViewById(R.id.broadcasting_tv_tips);
        tvTips.setOnClickListener(clickListener);
        int voiceMode = mSetting.getVoiceMode(mVehicleType);
        if (voiceMode == IAutoNaviSettingParams.VoiceMode.PLAY){
            mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.PLAY, mVehicleType);
            tvBroadcasting.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvBroadcasting.setTextColor(Color.parseColor("#FFFFFF"));
            tvMute.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvMute.setTextColor(Color.parseColor("#11131A"));
            tvTips.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvTips.setTextColor(Color.parseColor("#11131A"));
        }else if (voiceMode == IAutoNaviSettingParams.VoiceMode.QUITE){
            mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.QUITE, mVehicleType);
            tvBroadcasting.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
            tvMute.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvMute.setTextColor(Color.parseColor("#FFFFFF"));
            tvTips.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvTips.setTextColor(Color.parseColor("#11131A"));
        }else if (voiceMode == IAutoNaviSettingParams.VoiceMode.JustPlayWarning){
            mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.JustPlayWarning, mVehicleType);
            tvBroadcasting.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
            tvMute.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvMute.setTextColor(Color.parseColor("#11131A"));
            tvTips.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvTips.setTextColor(Color.parseColor("#FFFFFF"));
        }

        // 标准播报
        tvStandard = view.findViewById(R.id.broadcasting_tv_standard);
        tvStandard.setOnClickListener(clickListener);
        // 简洁播报
        tvConcise = view.findViewById(R.id.broadcasting_tv_concise);
        tvConcise.setOnClickListener(clickListener);
        int diyVoiceMode = mSetting.getDiyVoiceMode(mVehicleType);
        // 标准播报,简洁播报简介
        tvContentTips = view.findViewById(R.id.broadcasting_tv_content_tips);
        if (diyVoiceMode == IAutoNaviSettingParams.VoiceDiyMode.STANDARD){
            mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceDiyMode.STANDARD, mVehicleType);
            tvStandard.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvStandard.setTextColor(Color.parseColor("#FFFFFF"));
            tvConcise.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvConcise.setTextColor(Color.parseColor("#11131A"));
            tvContentTips.setText("标准播报：默认模式，适合多数人使用");
        }else if (diyVoiceMode == IAutoNaviSettingParams.VoiceDiyMode.SIMPLE){
            mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceDiyMode.SIMPLE, mVehicleType);
            tvStandard.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
            tvStandard.setTextColor(Color.parseColor("#11131A"));
            tvConcise.setBackgroundResource(R.drawable.not_downloaded_city_bg);
            tvConcise.setTextColor(Color.parseColor("#FFFFFF"));
            tvContentTips.setText("简洁播报：减少直行路况播报，降低转弯播报频次等，熟练司机使用");
        }

        // 导航是否静音
        imgVolume = view.findViewById(R.id.broadcasting_img_volume);
        // 音量减
        imgVolumeReduce = view.findViewById(R.id.broadcasting_img_volume_reduce);
        imgVolumeReduce.setOnClickListener(clickListener);
        // 音量进度条
        pbVolume = view.findViewById(R.id.broadcasting_pb_volume);
        // 音量加
        imgVolumeAdd = view.findViewById(R.id.broadcasting_img_volume_add);
        imgVolumeAdd.setOnClickListener(clickListener);

        pbVolume.setProgress(volume);
        if (volume == 0){
            imgVolume.setImageResource(R.mipmap.ic_low_volume_s);
        }
    }

    private boolean isSwitch = false;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.broadcasting_img_online_route_calculation:
                    if (isSwitch == false){
                        imgSwitch.setImageResource(R.mipmap.ic_switch_open_white);
                        isSwitch = true;
                    }else {
                        imgSwitch.setImageResource(R.mipmap.ic_switch_close_white);
                        isSwitch = false;
                    }
                    break;
                case R.id.broadcasting_tv_broadcasting:
                    mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.PLAY, mVehicleType);
                    tvBroadcasting.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvBroadcasting.setTextColor(Color.parseColor("#FFFFFF"));
                    tvMute.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvMute.setTextColor(Color.parseColor("#11131A"));
                    tvTips.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvTips.setTextColor(Color.parseColor("#11131A"));
                    break;
                case R.id.broadcasting_tv_mute:
                    mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.QUITE, mVehicleType);
                    tvBroadcasting.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
                    tvMute.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvMute.setTextColor(Color.parseColor("#FFFFFF"));
                    tvTips.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvTips.setTextColor(Color.parseColor("#11131A"));
                    break;
                case R.id.broadcasting_tv_tips:
                    mSetting.setVoiceMode(IAutoNaviSettingParams.VoiceMode.JustPlayWarning, mVehicleType);
                    tvBroadcasting.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvBroadcasting.setTextColor(Color.parseColor("#11131A"));
                    tvMute.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvMute.setTextColor(Color.parseColor("#11131A"));
                    tvTips.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvTips.setTextColor(Color.parseColor("#FFFFFF"));
                    break;
                case R.id.broadcasting_tv_standard:
                    mSetting.setDiyVoiceMode(IAutoNaviSettingParams.VoiceDiyMode.STANDARD, mVehicleType);
                    tvStandard.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvStandard.setTextColor(Color.parseColor("#FFFFFF"));
                    tvConcise.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvConcise.setTextColor(Color.parseColor("#11131A"));
                    tvContentTips.setText("标准播报：默认模式，适合多数人使用");
                    break;
                case R.id.broadcasting_tv_concise:
                    mSetting.setDiyVoiceMode(IAutoNaviSettingParams.VoiceDiyMode.SIMPLE, mVehicleType);
                    tvStandard.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    tvStandard.setTextColor(Color.parseColor("#11131A"));
                    tvConcise.setBackgroundResource(R.drawable.not_downloaded_city_bg);
                    tvConcise.setTextColor(Color.parseColor("#FFFFFF"));
                    tvContentTips.setText("简洁播报：减少直行路况播报，降低转弯播报频次等，熟练司机使用");
                    break;
                case R.id.broadcasting_img_volume_reduce:
                    volume--;
                    pbVolume.setProgress(volume);
                    if (volume == 0){
                        imgVolume.setImageResource(R.mipmap.ic_low_volume_s);
                    }
                    break;
                case R.id.broadcasting_img_volume_add:
                    volume++;
                    pbVolume.setProgress(volume);
                    imgVolume.setImageResource(R.mipmap.ic_low_volume_n_white);
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onBackPressed() {
        NavHostFragment.findNavController(BroadcastingFragment.this).popBackStack();
        return false;
    }
}