package com.baidu.mapautosdk.ui.presenter;

import android.view.View;
import android.widget.ListView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.bean.AutoPoiInfo;
import com.baidu.mapautosdk.ui.adapter.ParkingAdapter;

import java.util.List;

public class ParkingPresenter {
    private ListView mListView;
    private ParkingAdapter mAdapter;

    public void initListView(View view, List<AutoPoiInfo> result) {
        mListView = view.findViewById(R.id.parking_listview);
        mAdapter = new ParkingAdapter(view.getContext(), R.layout.item_parking_list, result);
        mListView.setAdapter(mAdapter);
    }

    public void setItemOnclickListener(ParkingAdapter.ItemOnclickListener listener) {
        mAdapter.setItemOnclickListener(listener);
    }

}
