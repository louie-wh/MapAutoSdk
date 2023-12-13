package com.baidu.mapautosdk.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.WaypointBean;
import com.baidu.tts.tools.SharedPreferencesUtils;

import org.greenrobot.eventbus.EventBus;

public class AddWaypointFragment extends BaseFragment {

    private ViewGroup mContentView;
    private Handler mHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BDAutoMapFactory.getAutoRouteResultManager().onCreate(getContext());
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_add_waypoint, container, false);
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
        view.findViewById(R.id.add_waypoint_img_back).setOnClickListener(clickListener);
        view.findViewById(R.id.add_waypoint_img_gas).setOnClickListener(clickListener);
        view.findViewById(R.id.add_waypoint_img_charging).setOnClickListener(clickListener);
        view.findViewById(R.id.add_waypoint_img_toilet).setOnClickListener(clickListener);
        view.findViewById(R.id.add_waypoint_img_point).setOnClickListener(clickListener);
        view.findViewById(R.id.add_waypoint_img_service_area).setOnClickListener(clickListener);
        view.findViewById(R.id.add_waypoint_img_food).setOnClickListener(clickListener);
        view.findViewById(R.id.add_waypoint_img_hotel).setOnClickListener(clickListener);
        view.findViewById(R.id.add_waypoint_img_serving).setOnClickListener(clickListener);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setScreenShow();
            }
        });
    }

    // 调整蚯蚓条显示区域
    public void setScreenShow() {
        int top = 10;     // 距离屏幕上边缘100dp
        int bottom = 10;  // 距离屏幕底部160dp
        int left = 400;
        int right = 20;
        BDAutoMapFactory.getAutoMapManager().setAutoScreenShow(left, top, right, bottom);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_waypoint_img_back:
                    onBackPressed();
                    break;
                case R.id.add_waypoint_img_gas:
                    EventBus.getDefault().postSticky(new WaypointBean("加油站"));
                    onBackPressed();
                    break;
                case R.id.add_waypoint_img_charging:
                    EventBus.getDefault().postSticky(new WaypointBean("充电站"));
                    onBackPressed();
                    break;
                case R.id.add_waypoint_img_toilet:
                    EventBus.getDefault().postSticky(new WaypointBean("厕所"));
                    onBackPressed();
                    break;
                case R.id.add_waypoint_img_point:
                    onBackPressed();
                    break;
                case R.id.add_waypoint_img_service_area:
                    EventBus.getDefault().postSticky(new WaypointBean("服务区"));
                    onBackPressed();
                    break;
                case R.id.add_waypoint_img_food:
                    EventBus.getDefault().postSticky(new WaypointBean("餐饮"));
                    onBackPressed();
                    break;
                case R.id.add_waypoint_img_hotel:
                    EventBus.getDefault().postSticky(new WaypointBean("酒店"));
                    onBackPressed();
                    break;
                case R.id.add_waypoint_img_serving:
                    EventBus.getDefault().postSticky(new WaypointBean("维修站"));
                    onBackPressed();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onBackPressed() {
        SharedPreferencesUtils.putBoolean(getActivity(), "isRoute", false);
        NavHostFragment.findNavController(AddWaypointFragment.this).popBackStack();
        return false;
    }

}