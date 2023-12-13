package com.baidu.mapautosdk.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.bean.AutoPoiInfo;

import java.util.List;

public class ParkingAdapter extends ArrayAdapter<AutoPoiInfo> implements View.OnClickListener {
    private Context mContext;
    private LayoutInflater mInflater;
    private int mResource;
    private List<AutoPoiInfo> autoInfoList;
    private ItemOnclickListener mListener;

    public ParkingAdapter(Context context, int resource, List<AutoPoiInfo> result) {
        super(context, resource, result);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mResource = resource;
        autoInfoList = result;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mResource, parent, false);
            holder.imgSign = convertView.findViewById(R.id.item_parking_img_sign);
            holder.tvSign = convertView.findViewById(R.id.item_parking_tv_sign);
            holder.tvName = convertView.findViewById(R.id.item_parking_list_tv_name);
//            holder.distance = convertView.findViewById(R.id.item_parking_list_tv_distance);
            holder.addrs = convertView.findViewById(R.id.item_parking_list_tv_addrs);

            holder.rlGoPoi = convertView.findViewById(R.id.item_parking_rl_go_poi);
            holder.imgGoHere = convertView.findViewById(R.id.item_parking_img_go_here);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        view = convertView;
        if (position == 0){
            holder.imgSign.setImageResource(R.mipmap.sign_selcted_white);
            holder.tvSign.setText("1");
        }else if (position > 0){
            holder.imgSign.setImageResource(R.mipmap.sign_unselcted_white);
            holder.tvSign.setText(position + 1 + "");
        }
        holder.tvName.setText(Html.fromHtml(getItem(position).name));
//        holder.distance.setText(autoInfoList.get(position).distance);
        holder.addrs.setText(autoInfoList.get(position).address);

        holder.rlGoPoi.setOnClickListener(this);
        holder.imgGoHere.setOnClickListener(this);
        holder.rlGoPoi.setTag(position);
        holder.imgGoHere.setTag(position);

        return view;
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
        public ImageView imgSign;
        public TextView tvSign;
        public TextView tvName;
        public TextView distance;
        public TextView addrs;
        public RelativeLayout rlGoPoi;
        public ImageView imgGoHere;
    }
}
