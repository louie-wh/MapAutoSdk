package com.baidu.mapautosdk.ui.presenter;

import java.util.List;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.route.RouteLine;
import com.baidu.mapautosdk.util.StringFormatUtils;

import android.view.View;
import android.widget.TextView;

public class RoutePlanPresenter {
    private RouteSelectListener mRouteListener;
    private View[] tabsView = new View[3];
    private View mRouteTitleFirst;
    private View mRouteTitleSecond;
    private View mRouteTitleThird;
    private List<RouteLine> mRouteLines;

    public interface RouteSelectListener {
        void onSelectRoute(int index);
    }

    public void setRouteSelectListener(RouteSelectListener listener) {
        mRouteListener = listener;
    }

    public void initRouteCarPlanTabs(View view) {

        mRouteTitleFirst =  view.findViewById(R.id.route_car_tabs_first);
        mRouteTitleSecond = view.findViewById(R.id.route_car_tabs_second);
        mRouteTitleThird = view.findViewById(R.id.route_car_tabs_third);
        mRouteTitleFirst.setSelected(false);
        mRouteTitleSecond.setSelected(false);
        mRouteTitleThird.setSelected(false);
        tabsView[0] = mRouteTitleFirst;
        tabsView[1] = mRouteTitleSecond;
        tabsView[2] = mRouteTitleThird;
        mRouteTitleFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRoute(0);
            }
        });
        mRouteTitleSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRoute(1);
            }
        });
        mRouteTitleThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRoute(2);
            }
        });
    }

    public void updateRoute(List<RouteLine> routeLines) {
        mRouteLines = routeLines;
        int routeCount = routeLines.size();
        if (routeCount > 0) {
            mRouteTitleFirst.setSelected(true);
            updateFirstRoute();
        } else {
            hideFirstRoute();
        }
        if (routeCount > 1) {
            updateSecondRoute();
        } else {
            hideSecondRoute();
        }

        if (routeCount > 2) {
            updateThirdRoute();
        } else {
            hideThirdRoute();
        }

    }

    private void updateRoute(int index) {
        RouteLine routeLine = mRouteLines.get(index);
        View convertView = tabsView[index];
        convertView.setVisibility(View.VISIBLE);
        String thirdTime =
                StringFormatUtils.carFormatTimeString(routeLine.duration);
        TextView time = convertView.findViewById(R.id.tv_time);
        TextView distance = convertView.findViewById(R.id.tv_distance);
        TextView planName = convertView.findViewById(R.id.tv_plan);
        TextView trafficLight = convertView.findViewById(R.id.tv_traffic_light);
        TextView totalPrices = convertView.findViewById(R.id.tv_total_prices);
        time.setText(thirdTime);
        String thirdDis =
                StringFormatUtils.formatDistanceStringForRouteResult(routeLine.distance);
        distance.setText(thirdDis);
        planName.setText(routeLine.planName);
        trafficLight.setText(routeLine.trafficLight);
        if (routeLine.totalPrices == 0) {
            totalPrices.setVisibility(View.GONE);
        } else {
            totalPrices.setVisibility(View.VISIBLE);
            totalPrices.setText(String.valueOf(routeLine.totalPrices));
        }
    }

    public void selectRoute(int index) {
        if (index == 0) {
            mRouteTitleFirst.setSelected(true);
            mRouteTitleSecond.setSelected(false);
            mRouteTitleThird.setSelected(false);
        } else if (index == 1) {
            mRouteTitleFirst.setSelected(false);
            mRouteTitleSecond.setSelected(true);
            mRouteTitleThird.setSelected(false);
        } else if (index == 2){
            mRouteTitleFirst.setSelected(false);
            mRouteTitleSecond.setSelected(false);
            mRouteTitleThird.setSelected(true);
        }
        mRouteListener.onSelectRoute(index);
    }

    private void hideFirstRoute() {
        mRouteTitleFirst.setVisibility(View.INVISIBLE);
    }

    private void hideSecondRoute() {
        mRouteTitleSecond.setVisibility(View.INVISIBLE);
    }

    private void hideThirdRoute() {
        mRouteTitleThird.setVisibility(View.INVISIBLE);
    }

    private void updateFirstRoute() {
        updateRoute(0);
    }

    private void updateSecondRoute() {
        updateRoute(1);
    }

    private void updateThirdRoute() {
        updateRoute(2);
    }
}
