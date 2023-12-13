package com.baidu.mapautosdk.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapautosdk.R;

public class WaypointDeletePopup extends LinearLayout {
    private Context mContext;
    private TextView mPoiName;
    private ImageView msetWayPointImg;

    public WaypointDeletePopup(Context context) {
        this(context, null);
    }

    public WaypointDeletePopup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("NewApi")
    public WaypointDeletePopup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.waypoint_delete_popup, this);
        mPoiName = (TextView) findViewById(R.id.poi_name);
        msetWayPointImg = (ImageView) findViewById(R.id.delete_waypoint_img);
    }

    public void setRightBtnDrawable(int drawableId) {
        msetWayPointImg.setImageDrawable(getResources().getDrawable(drawableId));
    }

    public void setPoiName(String poiName) {
        mPoiName.setText(poiName);
    }
}
