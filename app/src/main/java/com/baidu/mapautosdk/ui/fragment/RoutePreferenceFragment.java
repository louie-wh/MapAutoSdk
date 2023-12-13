package com.baidu.mapautosdk.ui.fragment;

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
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.ui.BaseFragment;

public class RoutePreferenceFragment extends BaseFragment {

    private ViewGroup mContentView;
    private RelativeLayout llOne;
    private ImageView imgTj;
    private TextView tvTj;
    private RelativeLayout rlOne;
    private RelativeLayout llTwo;
    private ImageView imgYd;
    private TextView tvYd;
    private RelativeLayout rlTwo;
    private RelativeLayout llThree;
    private ImageView imgSf;
    private TextView tvSf;
    private RelativeLayout rlThree;
    private RelativeLayout llFour;
    private ImageView imgGs;
    private TextView tvGs;
    private RelativeLayout rlFour;
    private RelativeLayout llFive;
    private ImageView imgTime;
    private TextView tvTime;
    private RelativeLayout rlFive;
    private RelativeLayout llSix;
    private ImageView imgGsyx;
    private TextView tvGsyx;
    private RelativeLayout rlSix;
    private TextView modifyDefault;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BDAutoMapFactory.getAutoRouteResultManager().onCreate(getContext());
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_route_preference, container, false);
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
        view.findViewById(R.id.route_preference_img_back).setOnClickListener(clickListener);
        // 智能推荐
        llOne = view.findViewById(R.id.route_preference_ll_one);
        imgTj = view.findViewById(R.id.route_preference_img_tj);
        tvTj = view.findViewById(R.id.route_preference_tv_tj);
        rlOne = view.findViewById(R.id.route_preference_rl_one);
        // 躲避拥堵
        llTwo = view.findViewById(R.id.route_preference_ll_two);
        imgYd = view.findViewById(R.id.route_preference_img_yd);
        tvYd = view.findViewById(R.id.route_preference_tv_yd);
        rlTwo = view.findViewById(R.id.route_preference_rl_two);
        // 少收费
        llThree = view.findViewById(R.id.route_preference_ll_three);
        imgSf = view.findViewById(R.id.route_preference_img_sf);
        tvSf = view.findViewById(R.id.route_preference_tv_sf);
        rlThree = view.findViewById(R.id.route_preference_rl_three);
        // 不走高速
        llFour = view.findViewById(R.id.route_preference_ll_four);
        imgGs = view.findViewById(R.id.route_preference_img_gs);
        tvGs = view.findViewById(R.id.route_preference_tv_gs);
        rlFour = view.findViewById(R.id.route_preference_rl_four);
        // 时间优先
        llFive = view.findViewById(R.id.route_preference_ll_five);
        imgTime = view.findViewById(R.id.route_preference_img_time);
        tvTime = view.findViewById(R.id.route_preference_tv_time);
        rlFive = view.findViewById(R.id.route_preference_rl_five);
        // 高速优先
        llSix = view.findViewById(R.id.route_preference_ll_six);
        imgGsyx = view.findViewById(R.id.route_preference_img_gsyx);
        tvGsyx = view.findViewById(R.id.route_preference_tv_gsyx);
        rlSix = view.findViewById(R.id.route_preference_rl_six);
        modifyDefault = view.findViewById(R.id.route_preference_tv_modify_default);

        llOne.setBackgroundResource(R.drawable.route_preference_default_bg);
        imgTj.setVisibility(View.GONE);
        tvTj.setTextColor(Color.parseColor("#FFFFFF"));
        rlOne.setVisibility(View.VISIBLE);
        llTwo.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
        imgYd.setVisibility(View.GONE);
        tvYd.setTextColor(Color.parseColor("#11131A"));
        rlTwo.setVisibility(View.GONE);
        llThree.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
        imgSf.setVisibility(View.GONE);
        tvSf.setTextColor(Color.parseColor("#11131A"));
        rlThree.setVisibility(View.GONE);
        llFour.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
        imgGs.setVisibility(View.GONE);
        tvGs.setTextColor(Color.parseColor("#11131A"));
        rlFour.setVisibility(View.GONE);
        llFive.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
        imgTime.setVisibility(View.GONE);
        tvTime.setTextColor(Color.parseColor("#11131A"));
        rlFive.setVisibility(View.GONE);
        llSix.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
        imgGsyx.setVisibility(View.GONE);
        tvGsyx.setTextColor(Color.parseColor("#11131A"));
        rlSix.setVisibility(View.GONE);

        modifyDefault.setOnClickListener(clickListener);
        llEnabled(false);
        llOne.setOnClickListener(clickListener);
        llTwo.setOnClickListener(clickListener);
        llThree.setOnClickListener(clickListener);
        llFour.setOnClickListener(clickListener);
        llFive.setOnClickListener(clickListener);
        llSix.setOnClickListener(clickListener);

    }

    private boolean firstModify = false;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.route_preference_img_back:
                    onBackPressed();
                    break;
                case R.id.route_preference_tv_modify_default:

                    llEnabled(true);

                    if (firstModify == false){
                        imgTj.setImageResource(R.mipmap.ic_route_preference_select);
                    }else {
                        imgTj.setImageResource(R.mipmap.ic_route_preference_unselected);
                    }

                    modifyDefault.setVisibility(View.GONE);
                    llOne.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgTj.setVisibility(View.VISIBLE);
                    tvTj.setTextColor(Color.parseColor("#11131A"));
                    rlOne.setVisibility(View.GONE);
                    llTwo.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgYd.setVisibility(View.VISIBLE);
                    tvYd.setTextColor(Color.parseColor("#11131A"));
                    rlTwo.setVisibility(View.GONE);
                    llThree.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgSf.setVisibility(View.VISIBLE);
                    tvSf.setTextColor(Color.parseColor("#11131A"));
                    rlThree.setVisibility(View.GONE);
                    llFour.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgGs.setVisibility(View.VISIBLE);
                    tvGs.setTextColor(Color.parseColor("#11131A"));
                    rlFour.setVisibility(View.GONE);
                    llFive.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgTime.setVisibility(View.VISIBLE);
                    tvTime.setTextColor(Color.parseColor("#11131A"));
                    rlFive.setVisibility(View.GONE);
                    llSix.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgGsyx.setVisibility(View.VISIBLE);
                    tvGsyx.setTextColor(Color.parseColor("#11131A"));
                    rlSix.setVisibility(View.GONE);

                    break;
                case R.id.route_preference_ll_one:

                    llEnabled(false);
                    firstModify = false;
                    modifyDefault.setVisibility(View.VISIBLE);
                    llOne.setBackgroundResource(R.drawable.route_preference_default_bg);
                    imgTj.setVisibility(View.GONE);
                    imgTj.setImageResource(R.mipmap.ic_route_preference_select);
                    tvTj.setTextColor(Color.parseColor("#FFFFFF"));
                    rlOne.setVisibility(View.VISIBLE);
                    llTwo.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgYd.setVisibility(View.GONE);
                    imgYd.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvYd.setTextColor(Color.parseColor("#11131A"));
                    rlTwo.setVisibility(View.GONE);
                    llThree.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgSf.setVisibility(View.GONE);
                    imgSf.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvSf.setTextColor(Color.parseColor("#11131A"));
                    rlThree.setVisibility(View.GONE);
                    llFour.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgGs.setVisibility(View.GONE);
                    imgGs.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvGs.setTextColor(Color.parseColor("#11131A"));
                    rlFour.setVisibility(View.GONE);
                    llFive.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgTime.setVisibility(View.GONE);
                    imgTime.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvTime.setTextColor(Color.parseColor("#11131A"));
                    rlFive.setVisibility(View.GONE);
                    llSix.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgGsyx.setVisibility(View.GONE);
                    imgGsyx.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvGsyx.setTextColor(Color.parseColor("#11131A"));
                    rlSix.setVisibility(View.GONE);

                    break;
                case R.id.route_preference_ll_two:

                    llEnabled(false);
                    firstModify = true;
                    modifyDefault.setVisibility(View.VISIBLE);
                    llOne.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgTj.setVisibility(View.GONE);
                    imgTj.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvTj.setTextColor(Color.parseColor("#11131A"));
                    rlOne.setVisibility(View.GONE);
                    llTwo.setBackgroundResource(R.drawable.route_preference_default_bg);
                    imgYd.setVisibility(View.GONE);
                    imgYd.setImageResource(R.mipmap.ic_route_preference_select);
                    tvYd.setTextColor(Color.parseColor("#FFFFFF"));
                    rlTwo.setVisibility(View.VISIBLE);
                    llThree.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgSf.setVisibility(View.GONE);
                    imgSf.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvSf.setTextColor(Color.parseColor("#11131A"));
                    rlThree.setVisibility(View.GONE);
                    llFour.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgGs.setVisibility(View.GONE);
                    imgGs.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvGs.setTextColor(Color.parseColor("#11131A"));
                    rlFour.setVisibility(View.GONE);
                    llFive.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgTime.setVisibility(View.GONE);
                    imgTime.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvTime.setTextColor(Color.parseColor("#11131A"));
                    rlFive.setVisibility(View.GONE);
                    llSix.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgGsyx.setVisibility(View.GONE);
                    imgGsyx.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvGsyx.setTextColor(Color.parseColor("#11131A"));
                    rlSix.setVisibility(View.GONE);

                    break;
                case R.id.route_preference_ll_three:

                    llEnabled(false);
                    firstModify = true;
                    modifyDefault.setVisibility(View.VISIBLE);
                    llOne.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgTj.setVisibility(View.GONE);
                    imgTj.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvTj.setTextColor(Color.parseColor("#11131A"));
                    rlOne.setVisibility(View.GONE);
                    llTwo.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgYd.setVisibility(View.GONE);
                    imgYd.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvYd.setTextColor(Color.parseColor("#11131A"));
                    rlTwo.setVisibility(View.GONE);
                    llThree.setBackgroundResource(R.drawable.route_preference_default_bg);
                    imgSf.setVisibility(View.GONE);
                    imgSf.setImageResource(R.mipmap.ic_route_preference_select);
                    tvSf.setTextColor(Color.parseColor("#FFFFFF"));
                    rlThree.setVisibility(View.VISIBLE);
                    llFour.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgGs.setVisibility(View.GONE);
                    imgGs.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvGs.setTextColor(Color.parseColor("#11131A"));
                    rlFour.setVisibility(View.GONE);
                    llFive.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgTime.setVisibility(View.GONE);
                    imgTime.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvTime.setTextColor(Color.parseColor("#11131A"));
                    rlFive.setVisibility(View.GONE);
                    llSix.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgGsyx.setVisibility(View.GONE);
                    imgGsyx.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvGsyx.setTextColor(Color.parseColor("#11131A"));
                    rlSix.setVisibility(View.GONE);

                    break;
                case R.id.route_preference_ll_four:

                    llEnabled(false);
                    firstModify = true;
                    modifyDefault.setVisibility(View.VISIBLE);
                    llOne.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgTj.setVisibility(View.GONE);
                    imgTj.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvTj.setTextColor(Color.parseColor("#11131A"));
                    rlOne.setVisibility(View.GONE);
                    llTwo.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgYd.setVisibility(View.GONE);
                    imgYd.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvYd.setTextColor(Color.parseColor("#11131A"));
                    rlTwo.setVisibility(View.GONE);
                    llThree.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgSf.setVisibility(View.GONE);
                    imgSf.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvSf.setTextColor(Color.parseColor("#11131A"));
                    rlThree.setVisibility(View.GONE);
                    llFour.setBackgroundResource(R.drawable.route_preference_default_bg);
                    imgGs.setVisibility(View.GONE);
                    imgGs.setImageResource(R.mipmap.ic_route_preference_select);
                    tvGs.setTextColor(Color.parseColor("#FFFFFF"));
                    rlFour.setVisibility(View.VISIBLE);
                    llFive.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgTime.setVisibility(View.GONE);
                    imgTime.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvTime.setTextColor(Color.parseColor("#11131A"));
                    rlFive.setVisibility(View.GONE);
                    llSix.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgGsyx.setVisibility(View.GONE);
                    imgGsyx.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvGsyx.setTextColor(Color.parseColor("#11131A"));
                    rlSix.setVisibility(View.GONE);

                    break;
                case R.id.route_preference_ll_five:

                    llEnabled(false);
                    firstModify = true;
                    modifyDefault.setVisibility(View.VISIBLE);
                    llOne.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgTj.setVisibility(View.GONE);
                    imgTj.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvTj.setTextColor(Color.parseColor("#11131A"));
                    rlOne.setVisibility(View.GONE);
                    llTwo.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgYd.setVisibility(View.GONE);
                    imgYd.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvYd.setTextColor(Color.parseColor("#11131A"));
                    rlTwo.setVisibility(View.GONE);
                    llThree.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgSf.setVisibility(View.GONE);
                    imgSf.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvSf.setTextColor(Color.parseColor("#11131A"));
                    rlThree.setVisibility(View.GONE);
                    llFour.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgGs.setVisibility(View.GONE);
                    imgGs.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvGs.setTextColor(Color.parseColor("#11131A"));
                    rlFour.setVisibility(View.GONE);
                    llFive.setBackgroundResource(R.drawable.route_preference_default_bg);
                    imgTime.setVisibility(View.GONE);
                    imgTime.setImageResource(R.mipmap.ic_route_preference_select);
                    tvTime.setTextColor(Color.parseColor("#FFFFFF"));
                    rlFive.setVisibility(View.VISIBLE);
                    llSix.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgGsyx.setVisibility(View.GONE);
                    imgGsyx.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvGsyx.setTextColor(Color.parseColor("#11131A"));
                    rlSix.setVisibility(View.GONE);

                    break;
                case R.id.route_preference_ll_six:

                    llEnabled(false);
                    firstModify = true;
                    modifyDefault.setVisibility(View.VISIBLE);
                    llOne.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgTj.setVisibility(View.GONE);
                    imgTj.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvTj.setTextColor(Color.parseColor("#11131A"));
                    rlOne.setVisibility(View.GONE);
                    llTwo.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgYd.setVisibility(View.GONE);
                    imgYd.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvYd.setTextColor(Color.parseColor("#11131A"));
                    rlTwo.setVisibility(View.GONE);
                    llThree.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgSf.setVisibility(View.GONE);
                    imgSf.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvSf.setTextColor(Color.parseColor("#11131A"));
                    rlThree.setVisibility(View.GONE);
                    llFour.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgGs.setVisibility(View.GONE);
                    imgGs.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvGs.setTextColor(Color.parseColor("#11131A"));
                    rlFour.setVisibility(View.GONE);
                    llFive.setBackgroundResource(R.drawable.more_popup_broadcast_bg);
                    imgTime.setVisibility(View.GONE);
                    imgTime.setImageResource(R.mipmap.ic_route_preference_unselected);
                    tvTime.setTextColor(Color.parseColor("#11131A"));
                    rlFive.setVisibility(View.GONE);
                    llSix.setBackgroundResource(R.drawable.route_preference_default_bg);
                    imgGsyx.setVisibility(View.GONE);
                    imgGsyx.setImageResource(R.mipmap.ic_route_preference_select);
                    tvGsyx.setTextColor(Color.parseColor("#FFFFFF"));
                    rlSix.setVisibility(View.VISIBLE);

                    break;
                default:
                    break;
            }
        }
    };


    // 默认不点击
    private void llEnabled(boolean isEnabled){
        llOne.setEnabled(isEnabled);
        llTwo.setEnabled(isEnabled);
        llThree.setEnabled(isEnabled);
        llFour.setEnabled(isEnabled);
        llFive.setEnabled(isEnabled);
        llSix.setEnabled(isEnabled);
    }

    @Override
    public boolean onBackPressed() {
        NavHostFragment.findNavController(RoutePreferenceFragment.this).popBackStack();
        return false;
    }
}