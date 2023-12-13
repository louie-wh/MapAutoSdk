package com.baidu.mapautosdk.ui.adapter;

import java.util.List;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapautosdk.api.search.poi.PoiDetailSearchOption;
import com.baidu.mapautosdk.api.search.poi.PoiDetailSearchResult;
import com.baidu.mapautosdk.api.search.poi.PoiResult;
import com.baidu.mapautosdk.api.search.sug.SugInfo;
import com.baidu.mapautosdk.interfaces.search.IAutoPoiSearchManager;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SuggestionsAdapter extends ArrayAdapter<String> implements View.OnClickListener{
    private Context mContext;
    private LayoutInflater mInflater;
    private int mResource;
    private List<SugInfo> sugInfoList;

    private InnerItemOnclickListener mListener;

    public SuggestionsAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mResource = resource;
    }

    public void setSugData(List<SugInfo> list){
        this.sugInfoList = list;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResource, parent, false);
            holder.imgLocationSearch = convertView.findViewById(R.id.img_location_search);
            holder.tvName = convertView.findViewById(R.id.item_suggestion_list_tv_name);
            holder.distance = convertView.findViewById(R.id.item_suggestion_list_tv_distance);
            holder.addrs = convertView.findViewById(R.id.item_suggestion_list_tv_addrs);

            holder.rlGoPoi = convertView.findViewById(R.id.item_suggestions_rl_go_poi);
            holder.imgGoHere = convertView.findViewById(R.id.item_suggestion_img_go_here);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        view = convertView;
        holder.tvName.setText(Html.fromHtml(getItem(position)));
        holder.distance.setText(sugInfoList.get(position).distance);
        holder.addrs.setText(sugInfoList.get(position).subTitle + sugInfoList.get(position).addrs);

        holder.rlGoPoi.setOnClickListener(this);
        holder.imgGoHere.setOnClickListener(this);
        holder.rlGoPoi.setTag(position);
        holder.imgGoHere.setTag(position);

        return view;
    }

    public interface InnerItemOnclickListener{
        void itemClick(View v);
    }

    public void setInnerItemOnclickListener(InnerItemOnclickListener listener){
        this.mListener = listener;
    }

    @Override
    public void onClick(View v) {
        mListener.itemClick(v);
    }

    public static class ViewHolder {
        public ImageView imgLocationSearch;
        public TextView tvName;
        public TextView distance;
        public TextView addrs;
        public RelativeLayout rlGoPoi;
        public ImageView imgGoHere;
    }
}
