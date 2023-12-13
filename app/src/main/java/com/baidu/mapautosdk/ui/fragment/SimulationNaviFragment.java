package com.baidu.mapautosdk.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapautosdk.MessageEvent;
import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.ui.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SimulationNaviFragment extends BaseFragment {
    private ViewGroup mContentView;
    private String poiName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        poiName = arguments.getString("poiName");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boolean registered = EventBus.getDefault().isRegistered(this);
        if (registered == false){
            EventBus.getDefault().register(this);
        }

        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_simulation_navi, container, false);
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
        // 关闭本页面
        view.findViewById(R.id.simulation_navi_img_back).setOnClickListener(clickListener);
        // 增加途经点
        view.findViewById(R.id.simulation_navi_tv_add_passing_point).setOnClickListener(clickListener);
        TextView recommendedRouteEt = view.findViewById(R.id.simulation_navi_tv);
        recommendedRouteEt.setText(Html.fromHtml(poiName));
        // 模拟导航
        view.findViewById(R.id.simulation_navi_ll).setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            switch (v.getId()) {
                case R.id.simulation_navi_img_back:
                    onBackPressed();
                    break;
                case R.id.simulation_navi_tv_add_passing_point:
                    NavHostFragment.findNavController(SimulationNaviFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_addWaypointFragment);
                    break;
                case R.id.simulation_navi_ll:
                    EventBus.getDefault().postSticky(new MessageEvent(false));
                    bundle.putString("endNode", Html.fromHtml(poiName).toString());
                    bundle.putInt("naviType", 2);
                    NavHostFragment.findNavController(SimulationNaviFragment.this)
                            .navigate(R.id.action_routePlanFragment_to_demoGuideFragment, bundle);

//                    NavHostFragment.findNavController(SimulationNaviFragment.this)
//                            .navigate(R.id.action_routePlanFragment_to_demoCustomUiFragment, bundle);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onBackPressed() {
        NavHostFragment.findNavController(SimulationNaviFragment.this).popBackStack();
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void eventBusMessage(MessageEvent myMessage){

        if (myMessage.message == true){
            onBackPressed();
        }

    }
}