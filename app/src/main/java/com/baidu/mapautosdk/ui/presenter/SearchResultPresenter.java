package com.baidu.mapautosdk.ui.presenter;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.search.sug.SuggestionResult;
import com.baidu.mapautosdk.ui.adapter.SearchResultAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResultPresenter {
    private ListView mListView;
    private SearchResultAdapter mAdapter;
    private SuggestionResult mResult;

    public void initListView(View view) {
        mListView = view.findViewById(R.id.search_result_listview);
        List<String> dataList = new ArrayList<>();
        mAdapter = new SearchResultAdapter(view.getContext(), R.layout.item_search_result_list, dataList);
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

    public void setInnerItemOnclickListener(SearchResultAdapter.InnerItemOnclickListener listener) {
        mAdapter.setInnerItemOnclickListener(listener);
    }

    public SuggestionResult getResultData() {
        return mResult;
    }
}
