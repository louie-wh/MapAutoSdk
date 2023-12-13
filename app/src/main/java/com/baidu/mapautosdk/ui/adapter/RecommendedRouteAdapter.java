package com.baidu.mapautosdk.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.BDAutoMapFactory;
import com.baidu.mapautosdk.api.route.RouteLine;
import com.baidu.mapautosdk.api.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapautosdk.api.search.poi.PoiDetailSearchOption;
import com.baidu.mapautosdk.api.search.poi.PoiDetailSearchResult;
import com.baidu.mapautosdk.api.search.poi.PoiResult;
import com.baidu.mapautosdk.interfaces.search.IAutoPoiSearchManager;
import com.baidu.mapautosdk.util.StringFormatUtils;

import java.util.List;

public class RecommendedRouteAdapter extends ArrayAdapter<RouteLine> implements View.OnClickListener{
    private Context mContext;
    private LayoutInflater mInflater;
    private int mResource;
    private List<RouteLine> routeLine;
    private IAutoPoiSearchManager poiSearch;
    private int selectPosition;

    private ItemOnclickListener mListener;

    public RecommendedRouteAdapter(Context context, int resource, List<RouteLine> objects) {
        super(context, resource, objects);
        this.routeLine = objects;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mResource = resource;
        poiSearch = BDAutoMapFactory.getAutoPoiSearchManager();
    }

    public void setSugData(List<RouteLine> list){
        this.routeLine = list;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResource, parent, false);
            holder.rlScheme = convertView.findViewById(R.id.item_recommended_route_rl_scheme);
            holder.recommendedScheme = convertView.findViewById(R.id.item_recommended_route_tv_scheme);
            holder.recommendedSchemeNum = convertView.findViewById(R.id.item_recommended_route_tv_scheme_num);
            holder.mTime = convertView.findViewById(R.id.item_recommended_route_tv_time);
            holder.distance = convertView.findViewById(R.id.item_recommended_route_tv_distance);
            holder.img = convertView.findViewById(R.id.item_recommended_route_img);
            holder.trafficLightsNum = convertView.findViewById(R.id.item_recommended_route_traffic_lights_num);
            holder.totalPrices = convertView.findViewById(R.id.item_recommended_route_totalPrices);
            holder.imgSimulationNavi = convertView.findViewById(R.id.item_recommended_route_img_simulation_navi);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        view = convertView;
        holder.recommendedSchemeNum.setText(routeLine.get(position).planName);
        holder.mTime.setText(StringFormatUtils.carFormatTimeString(routeLine.get(position).duration));
        holder.distance.setText(StringFormatUtils.formatDistanceStringForRouteResult(routeLine.get(position).distance));
        holder.trafficLightsNum.setText(routeLine.get(position).trafficLight);
        if (routeLine.get(position).totalPrices == 0){
            holder.totalPrices.setVisibility(View.GONE);
        }else {
            holder.totalPrices.setVisibility(View.VISIBLE);
            holder.totalPrices.setText(routeLine.get(position).totalPrices + "");
        }
        if (position == 0){
            holder.recommendedScheme.setVisibility(View.VISIBLE);
            holder.recommendedSchemeNum.setPadding(10, 5, 25, 5);
        }else {
            holder.recommendedScheme.setVisibility(View.GONE);
            holder.recommendedSchemeNum.setPadding(0, 5, 0, 5);
        }
        if (selectPosition == position){
            if (position == 0){
                holder.rlScheme.setBackgroundResource(R.drawable.recommended_route_list_two_bg);
                holder.recommendedScheme.setVisibility(View.VISIBLE);
                holder.recommendedSchemeNum.setPadding(10, 5, 25, 5);
            }else {
                holder.rlScheme.setBackgroundResource(R.drawable.recommended_route_list_n_bg);
                holder.recommendedScheme.setVisibility(View.GONE);
                holder.recommendedSchemeNum.setPadding(0, 5, 0, 5);
            }
            holder.recommendedScheme.setSelected(true);
            holder.recommendedScheme.setBackgroundResource(R.drawable.recommended_route_list_one_bg);
            holder.recommendedSchemeNum.setSelected(true);
            holder.mTime.setSelected(true);
            holder.distance.setSelected(true);
            holder.img.setImageResource(R.mipmap.ic_traffic_lights_s);
            holder.trafficLightsNum.setSelected(true);
            holder.totalPrices.setSelected(true);
        }else {
            holder.rlScheme.setBackgroundResource(R.drawable.recommended_route_list_n_bg);
            holder.recommendedScheme.setSelected(false);
            holder.recommendedScheme.setBackgroundResource(R.drawable.recommended_route_list_n_bg);
            holder.recommendedSchemeNum.setSelected(false);
            holder.mTime.setSelected(false);
            holder.distance.setSelected(false);
            holder.img.setImageResource(R.mipmap.ic_traffic_lights_n);
            holder.trafficLightsNum.setSelected(false);
            holder.totalPrices.setSelected(false);
        }

        holder.imgSimulationNavi.setOnClickListener(this);
        holder.imgSimulationNavi.setTag(position);

        return view;
    }

    private void getPoiDetail(String uid) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        poiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(uid));
        poiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
                Log.i("guoqi", "onGetPoiDetailResult");
            }
        });
    }

    public interface ItemOnclickListener{
        void itemClick(View v);
    }

    public void setItemOnclickListener(ItemOnclickListener listener){
        this.mListener = listener;
    }

    @Override
    public void onClick(View v) {
        mListener.itemClick(v);
    }

    public static class ViewHolder {
        public RelativeLayout rlScheme;
        public TextView recommendedScheme;
        public TextView recommendedSchemeNum;
        public TextView mTime;
        public TextView distance;
        public ImageView img;
        public TextView trafficLightsNum;
        public TextView totalPrices;
        public ImageView imgSimulationNavi;
    }
}
