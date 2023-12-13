package com.baidu.mapautosdk.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.ui.BaseFragment;

public class ModifyLicensePlateNumberFragment extends BaseFragment {

    private ViewGroup mContentView;
    private ImageView imgDelete;
    private EditText etNumber;
    private TextView tvOk;
    private RelativeLayout rlPlaceOfOwnership;
    private TextView tvCity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_modify_license_plate_number,
                    container, false);
        } else {
            ViewGroup parentView = (ViewGroup) mContentView.getParent();
            if (parentView != null) {
                parentView.removeView(mContentView);
            }
        }
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 返回按钮
        view.findViewById(R.id.modify_license_plate_number_img_back).setOnClickListener(clickListener);
        // 车牌归属地
        rlPlaceOfOwnership = view.
                findViewById(R.id.modify_license_plate_number_rl_place_of_ownership);
        // 城市缩写
        tvCity = view.findViewById(R.id.modify_license_plate_number_tv_city);
        // 输入车牌号
        etNumber = view.findViewById(R.id.modify_license_plate_number_et);
        // 删除已输入的车牌号
        imgDelete = view.findViewById(R.id.modify_license_plate_number_img_delete);
        imgDelete.setOnClickListener(clickListener);
        // 车牌号输入完成
        tvOk = view.findViewById(R.id.modify_license_plate_number_tv_ok);
        tvOk.setOnClickListener(clickListener);
        tvOk.setEnabled(false);
        etNumber.addTextChangedListener(etTextChanged);

    }

    private TextWatcher etTextChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0){
                imgDelete.setVisibility(View.VISIBLE);
                if (s.length() == 5){
                    tvOk.setEnabled(true);
                    tvOk.setBackgroundResource(R.mipmap.ic_btn_complete_s_white);
                }else {
                    tvOk.setEnabled(false);
                    tvOk.setBackgroundResource(R.mipmap.ic_btn_complete_n_white);
                }
            }else {
                imgDelete.setVisibility(View.GONE);
                tvOk.setEnabled(false);
                tvOk.setBackgroundResource(R.mipmap.ic_btn_complete_n_white);
            }
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            switch (v.getId()) {
                case R.id.modify_license_plate_number_img_back:
                    onBackPressed();
                    break;
                case R.id.modify_license_plate_number_img_delete:
                    etNumber.setText("");
                    break;
                case R.id.modify_license_plate_number_tv_ok:
                    // 是否保存车牌号
                    showPreservation();
                    break;
                default:
                    break;
            }
        }
    };

    private void showPreservation() {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.preservation_popup,
                null, false);
        PopupWindow preservationPopup = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);
        preservationPopup.setOutsideTouchable(true);
        preservationPopup.setFocusable(true);
        preservationPopup.setTouchable(true);
        preservationPopup.showAtLocation(rlPlaceOfOwnership, Gravity.CENTER, 0, 0);
        // 保存
        TextView preservationTvOk = inflate.findViewById(R.id.preservation_popup_tv_ok);
        // 取消
        TextView preservationTvCancel = inflate.findViewById(R.id.preservation_popup_tv_cancel);
        preservationTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preservationPopup.dismiss();
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        NavHostFragment.findNavController(ModifyLicensePlateNumberFragment.this).popBackStack();
        return false;
    }
}