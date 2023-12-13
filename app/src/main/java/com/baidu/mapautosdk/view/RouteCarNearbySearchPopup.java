package com.baidu.mapautosdk.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapautosdk.R;

public class RouteCarNearbySearchPopup extends LinearLayout {
    private Context mContext;
    private TextView mPoiName;
    private TextView mPoiInfo;
    private TextView mSetWaypoint;
    private ImageView msetWayPointImg;
    private RelativeLayout mLeftBtn;
    private RelativeLayout mRightBtn;
    private View mPinPlaceholder;

    public RouteCarNearbySearchPopup(Context context) {
        this(context, null);
    }

    public RouteCarNearbySearchPopup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("NewApi")
    public RouteCarNearbySearchPopup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.route_nearby_search_car_popup, this);
        mPoiName = (TextView) findViewById(R.id.poi_name);
        mPoiInfo = (TextView) findViewById(R.id.route_cost);
        mSetWaypoint = (TextView) findViewById(R.id.set_waypoint_txt);
        msetWayPointImg = (ImageView) findViewById(R.id.set_waypoint_img);
        mLeftBtn = (RelativeLayout) findViewById(R.id.left_content);
        mRightBtn = (RelativeLayout) findViewById(R.id.right_content);
        mPinPlaceholder = findViewById(R.id.pin_placeholder);
    }

    public void setRightBtnText(String text, int textColor) {
        mSetWaypoint.setText(text);
        mSetWaypoint.setTextColor(textColor);
    }

    public void setRightBtnDrawable(int drawableId) {
        msetWayPointImg.setImageDrawable(getResources().getDrawable(drawableId));
    }

    public void setRightBtnBackgroundDrawable(int drawableId) {
        mRightBtn.setBackgroundDrawable(getResources().getDrawable(drawableId));
    }

    public void setPoiInfo(String poiInfo) {
        if (!TextUtils.isEmpty(poiInfo)) {
            mPoiInfo.setText(poiInfo);
            mPoiInfo.setVisibility(View.VISIBLE);
        } else {
            mPoiInfo.setVisibility(View.GONE);
        }
    }

    public void setPinPlaceholderHeight(int height) {
        ViewGroup.LayoutParams params = mPinPlaceholder.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        }
        params.height = height;
        mPinPlaceholder.setLayoutParams(params);
    }

    public void setPoiName(String poiName) {
        mPoiName.setText(poiName);
    }

    public int getLeftContentWidth() {
        return mLeftBtn.getMeasuredWidth();
    }

    public int getRightContentWidth() {
        return mRightBtn.getMeasuredWidth();
    }

    public int getLeftContentHeight() {
        return mLeftBtn.getMeasuredHeight();
    }

    public int getRightContentHeight() {
        return mRightBtn.getMeasuredHeight();
    }

    public int getPinPlaceHolderHeight() {
        return mPinPlaceholder.getMeasuredHeight();
    }

    public Bundle getLeftContentSizeBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("l", 0);
        bundle.putInt("r", getLeftContentWidth());
        bundle.putInt("t", getLeftContentHeight() + getPinPlaceHolderHeight());
        bundle.putInt("b", getPinPlaceHolderHeight());
        return bundle;
    }

    public Bundle getRightContentSizeBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt("l", getLeftContentWidth());
        bundle.putInt("r", getLeftContentWidth() + getRightContentWidth());
        bundle.putInt("t", getRightContentHeight() + getPinPlaceHolderHeight());
        bundle.putInt("b", getPinPlaceHolderHeight());
        return bundle;
    }
}
