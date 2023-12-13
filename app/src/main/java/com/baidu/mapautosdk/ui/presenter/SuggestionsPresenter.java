package com.baidu.mapautosdk.ui.presenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.search.sug.SuggestionResult;
import com.baidu.mapautosdk.ui.adapter.SuggestionsAdapter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SuggestionsPresenter {
    private ListView mListView;
    private SuggestionsAdapter mAdapter;
    private SuggestionResult mResult;

    public void initListView(View view) {
        mListView = view.findViewById(R.id.route_plan_search_listview);
        List<String> dataList = new ArrayList<>();
        mAdapter = new SuggestionsAdapter(view.getContext(), R.layout.item_suggestions_list, dataList);
        mListView.setAdapter(mAdapter);

    }

    public void setData(SuggestionResult result) {
        if (result != null) {
            mResult = result;
            String[] poiName = mResult.getPoiname();
            mAdapter.setSugData(mResult.getSugPoiList());
            if (poiName != null) {
                mAdapter.clear();
                mAdapter.addAll(Arrays.asList(poiName));
            } else {
                mAdapter.clear();
            }
        } else {
            mAdapter.clear();
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        mListView.setOnItemClickListener(itemClickListener);
    }

    public void setInnerItemOnclickListener(SuggestionsAdapter.InnerItemOnclickListener listener) {
        mAdapter.setInnerItemOnclickListener(listener);
    }

    public SuggestionResult getResultData() {
        return mResult;
    }
}
