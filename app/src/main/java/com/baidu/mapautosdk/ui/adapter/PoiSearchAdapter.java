package com.baidu.mapautosdk.ui.adapter;


import com.baidu.mapautosdk.R;

import android.content.Context;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;

public class PoiSearchAdapter extends ArrayAdapter<String> {
    public PoiSearchAdapter(@NonNull Context context) {
        super(context, R.layout.item_suggestions_list, R.id.item_suggestion_list_tv_name);
    }
}
