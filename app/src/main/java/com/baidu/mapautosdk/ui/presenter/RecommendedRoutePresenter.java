package com.baidu.mapautosdk.ui.presenter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.route.RouteLine;
import com.baidu.mapautosdk.ui.adapter.RecommendedRouteAdapter;

import java.util.List;

public class RecommendedRoutePresenter {
    private RouteSelectListener mRouteListener;

    private List<RouteLine> mRouteLines;
    private ListView mListView;
    private RecommendedRouteAdapter mAdapter;
    private int selectPosition;

    public interface RouteSelectListener {
        void onSelectRoute(int index);
    }

    public void setRouteSelectListener(RouteSelectListener listener) {
        mRouteListener = listener;
    }

    public void initRouteCarPlanView(View view, List<RouteLine> routeLines) {

        mListView = view.findViewById(R.id.recommended_route_listview);
        mAdapter = new RecommendedRouteAdapter(view.getContext(), R.layout.item_recommended_route_list, routeLines);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                mAdapter.setSelectPosition(selectPosition);
                mAdapter.notifyDataSetChanged();
                mRouteListener.onSelectRoute(position);
            }
        });
    }

    public List<RouteLine> getmRouteLines() {
        return mRouteLines;
    }

    public void selectRoute(int index) {
        mAdapter.setSelectPosition(index);
        mAdapter.notifyDataSetChanged();
        mRouteListener.onSelectRoute(index);
    }

    public void setItemOnclickListener(RecommendedRouteAdapter.ItemOnclickListener listener) {
        mAdapter.setItemOnclickListener(listener);
    }

}
