package com.baidu.mapautosdk.ui.fragment;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.view.FlowRadioGroup;

public class MyCarFragment extends BaseFragment {

    private ViewGroup mContentView;
    private ImageView imgSwitch;
    private FlowRadioGroup rgPowerType;
    private RadioButton rbFuel;
    private RadioButton rbElectric;
    private RadioButton rbOilElectricMixing;
    private RadioButton rbDieselOil;
    private RadioButton rbPlugIn;
    private RadioButton rbAdder;
    private FlowRadioGroup rgTruckType;
    private RadioButton rbMiniature;
    private RadioButton rbLight;
    private RadioButton rbMidsize;
    private RadioButton rbHeavy;
    private RadioButton rbTrailer;
    private RadioButton rbSpecialPurpose;
    private RadioButton rbCrossCountry;
    private TextView tvWeight;
    private TextView tvVerification;
    private TextView tvCarLength;
    private TextView tvCarWidth;
    private TextView tvCarHeight;
    private RadioGroup rgAxes;
    private RadioButton rbAxesTwo;
    private RadioButton rbAxesThree;
    private RadioButton rbAxesFour;
    private RadioButton rbAxesFive;
    private RadioButton rbAxesSix;
    private RadioButton rbAxesSixAbove;
    private RadioGroup rgDischarge;
    private RadioButton rbDischargeOne;
    private RadioButton rbDischargeTwo;
    private RadioButton rbDischargeThree;
    private RadioButton rbDischargeFour;
    private RadioButton rbDischargeFive;
    private RadioButton rbDischargeSix;
    private RadioButton rbDischargeNo;
    private RadioGroup rgLicensePlateColor;
    private RadioButton rbLicensePlateColorYellow;
    private RadioButton rbLicensePlateColorBlue;
    private RadioButton rbLicensePlateColorGreen;
    private RadioButton rbLicensePlateColorBlack;
    private RadioButton rbLicensePlateColorWhite;
    private FlowRadioGroup rgPurpose;
    private RadioButton rbPurposeNo;
    private RadioButton rbPurposeDangerous;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_my_car, container, false);
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
        view.findViewById(R.id.my_car_popup_img_back).setOnClickListener(clickListener);
        // 车牌归属地
        view.findViewById(R.id.my_car_popup_rl_place_of_ownership).setOnClickListener(clickListener);
        // 车牌号
        view.findViewById(R.id.my_car_popup_tv_car_number).setOnClickListener(clickListener);
        // 避开限行
        imgSwitch = view.findViewById(R.id.my_car_popup_img_switch);
        imgSwitch.setOnClickListener(clickListener);

        // 动力类型选择
        rgPowerType = view.findViewById(R.id.my_car_popup_rg_power_type);
        rgPowerType.setOnCheckedChangeListener(powerTypeCheckedChange);
        // 燃油车
        rbFuel = view.findViewById(R.id.my_car_popup_rb_fuel);
        // 纯电动
        rbElectric = view.findViewById(R.id.my_car_popup_rb_electric);
        // 油电混合动力
        rbOilElectricMixing = view.findViewById(R.id.my_car_popup_rb_oil_electric_mixing);
        // 柴油
        rbDieselOil = view.findViewById(R.id.my_car_popup_rb_diesel_oil);
        // 插电式混合动力
        rbPlugIn = view.findViewById(R.id.my_car_popup_rb_plug_in);
        // 增程式动力
        rbAdder = view.findViewById(R.id.my_car_popup_rb_adder);

        // 货车类型
        rgTruckType = view.findViewById(R.id.my_car_popup_rg_truck_type);
        rgTruckType.setOnCheckedChangeListener(truckTypeCheckedChange);
        // 微型货车
        rbMiniature = view.findViewById(R.id.my_car_popup_rg_miniature);
        // 轻型货车
        rbLight = view.findViewById(R.id.my_car_popup_rg_light);
        // 中型货车
        rbMidsize = view.findViewById(R.id.my_car_popup_rg_midsize);
        // 重型货车
        rbHeavy = view.findViewById(R.id.my_car_popup_rg_heavy);
        // 挂车
        rbTrailer = view.findViewById(R.id.my_car_popup_rg_trailer);
        // 专用货车
        rbSpecialPurpose = view.findViewById(R.id.my_car_popup_rg_special_purpose);
        // 越野货车
        rbCrossCountry = view.findViewById(R.id.my_car_popup_rg_cross_country);

        // 总重量减
        view.findViewById(R.id.my_car_popup_img_weight_decrease).setOnClickListener(clickListener);
        // 总重量
        tvWeight = view.findViewById(R.id.my_car_popup_tv_weight);
        // 总重量加
        view.findViewById(R.id.my_car_popup_img_weight_add).setOnClickListener(clickListener);
        // 总重量 查看示例
        view.findViewById(R.id.my_car_popup_tv_weight_example);

        // 核定载量减
        view.findViewById(R.id.my_car_popup_img_verification_decrease).setOnClickListener(clickListener);
        // 核定载量
        tvVerification = view.findViewById(R.id.my_car_popup_tv_verification);
        // 核定载量加
        view.findViewById(R.id.my_car_popup_img_verification_add).setOnClickListener(clickListener);
        // 核定载量 查看示例
        view.findViewById(R.id.my_car_popup_tv_verification_example);
        // 超重提示
        TextView tvWeightTips = view.findViewById(R.id.my_car_popup_tv_weight_tips);

        // 车长减
        view.findViewById(R.id.my_car_popup_img_car_length_decrease).setOnClickListener(clickListener);
        // 车长
        tvCarLength = view.findViewById(R.id.my_car_popup_tv_car_length);
        // 车长加
        view.findViewById(R.id.my_car_popup_img_car_length_add).setOnClickListener(clickListener);

        // 车宽减
        view.findViewById(R.id.my_car_popup_img_car_width_decrease).setOnClickListener(clickListener);
        // 车宽
        tvCarWidth = view.findViewById(R.id.my_car_popup_tv_car_width);
        // 车宽加
        view.findViewById(R.id.my_car_popup_img_car_width_add).setOnClickListener(clickListener);
        // 超宽提示
        TextView tvWidthTips = view.findViewById(R.id.my_car_popup_tv_width_tips);

        // 车高减
        view.findViewById(R.id.my_car_popup_img_car_height_decrease).setOnClickListener(clickListener);
        // 车高
        tvCarHeight = view.findViewById(R.id.my_car_popup_tv_car_height);
        // 车高加
        view.findViewById(R.id.my_car_popup_img_car_height_add).setOnClickListener(clickListener);
        // 超高提示
        TextView tvHeightTips = view.findViewById(R.id.my_car_popup_tv_height_tips);

        // 轴数
        rgAxes = view.findViewById(R.id.my_car_popup_rg_axes);
        rgAxes.setOnCheckedChangeListener(axesNumberCheckedChange);
        // 2轴
        rbAxesTwo = view.findViewById(R.id.my_car_popup_rb_axes_two);
        // 3轴
        rbAxesThree = view.findViewById(R.id.my_car_popup_rb_axes_three);
        // 4轴
        rbAxesFour = view.findViewById(R.id.my_car_popup_rb_axes_four);
        // 5轴
        rbAxesFive = view.findViewById(R.id.my_car_popup_rb_axes_five);
        // 6轴
        rbAxesSix = view.findViewById(R.id.my_car_popup_rb_axes_six);
        // 6轴以上
        rbAxesSixAbove = view.findViewById(R.id.my_car_popup_rb_axes_six_above);

        // 排放标准
        rgDischarge = view.findViewById(R.id.my_car_popup_rg_discharge);
        rgDischarge.setOnCheckedChangeListener(dischargeCheckedChange);
        // 国1
        rbDischargeOne = view.findViewById(R.id.my_car_popup_rb_discharge_one);
        // 国2
        rbDischargeTwo = view.findViewById(R.id.my_car_popup_rb_discharge_two);
        // 国3
        rbDischargeThree = view.findViewById(R.id.my_car_popup_rb_discharge_three);
        // 国4
        rbDischargeFour = view.findViewById(R.id.my_car_popup_rb_discharge_four);
        // 国5
        rbDischargeFive = view.findViewById(R.id.my_car_popup_rb_discharge_five);
        // 国6
        rbDischargeSix = view.findViewById(R.id.my_car_popup_rb_discharge_six);
        // 无
        rbDischargeNo = view.findViewById(R.id.my_car_popup_rb_discharge_no);

        // 车牌颜色
        rgLicensePlateColor = view.findViewById(R.id.my_car_popup_rg_license_plate_color);
        rgLicensePlateColor.setOnCheckedChangeListener(licensePlateColorCheckedChange);
        // 黄牌
        rbLicensePlateColorYellow = view.findViewById(R.id.my_car_popup_rb_license_plate_color_yellow);
        // 蓝牌
        rbLicensePlateColorBlue = view.findViewById(R.id.my_car_popup_rb_license_plate_color_blue);
        // 绿牌
        rbLicensePlateColorGreen = view.findViewById(R.id.my_car_popup_rb_license_plate_color_green);
        // 黑牌
        rbLicensePlateColorBlack = view.findViewById(R.id.my_car_popup_rb_license_plate_color_black);
        // 白牌
        rbLicensePlateColorWhite = view.findViewById(R.id.my_car_popup_rb_license_plate_color_white);

        // 货车用途
        rgPurpose = view.findViewById(R.id.my_car_popup_rg_purpose);
        rgPurpose.setOnCheckedChangeListener(purposeCheckedChange);
        // 暂不选择
        rbPurposeNo = view.findViewById(R.id.my_car_popup_rb_purpose_no);
        // 危化车辆
        rbPurposeDangerous = view.findViewById(R.id.my_car_popup_rb_purpose_dangerous_vehicles);
    }

    private boolean isSwitch = false;
    private int truckWeight = 0;
    private int truckVerification = 0;
    private int carLength = 0;
    private int carWidth = 0;
    private int carHeight = 0;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            switch (v.getId()) {
                case R.id.my_car_popup_img_back:
                    onBackPressed();
                    break;
                case R.id.my_car_popup_img_switch:
                    if (isSwitch == false){
                        imgSwitch.setImageResource(R.mipmap.ic_switch_open_white);
                        isSwitch = true;
                    }else {
                        imgSwitch.setImageResource(R.mipmap.ic_switch_close_white);
                        isSwitch = false;
                    }
                    break;
                case R.id.my_car_popup_rl_place_of_ownership:
                case R.id.my_car_popup_tv_car_number:
                    NavHostFragment.findNavController(MyCarFragment.this)
                            .navigate(R.id.action_mainFragment_to_modifyLicensePlateNumberFragment);
                    break;
                case R.id.my_car_popup_img_weight_decrease:
                    if (truckWeight > 0){
                        truckWeight--;
                        tvWeight.setText(truckWeight + "");
                    }else {
                        tvWeight.setText(truckWeight + "");
                    }
                    break;
                case R.id.my_car_popup_img_weight_add:
                    truckWeight++;
                    tvWeight.setText(truckWeight + "");
                    break;
                case R.id.my_car_popup_img_verification_decrease:
                    if (truckVerification > 0){
                        truckVerification--;
                        tvVerification.setText(truckVerification + "");
                    }else {
                        tvVerification.setText(truckVerification + "");
                    }
                    break;
                case R.id.my_car_popup_img_verification_add:
                    truckVerification++;
                    tvVerification.setText(truckVerification + "");
                    break;
                case R.id.my_car_popup_img_car_length_decrease:
                    if (carLength > 0){
                        carLength--;
                        tvCarLength.setText(carLength + "");
                    }else {
                        tvCarLength.setText(carLength + "");
                    }
                    break;
                case R.id.my_car_popup_img_car_length_add:
                    carLength++;
                    tvCarLength.setText(carLength + "");
                    break;
                case R.id.my_car_popup_img_car_width_decrease:
                    if (carWidth > 0){
                        carWidth--;
                        tvCarWidth.setText(carWidth + "");
                    }else {
                        tvCarWidth.setText(carWidth + "");
                    }
                    break;
                case R.id.my_car_popup_img_car_width_add:
                    carWidth++;
                    tvCarWidth.setText(carWidth + "");
                    break;
                case R.id.my_car_popup_img_car_height_decrease:
                    if (carHeight > 0){
                        carHeight--;
                        tvCarHeight.setText(carHeight + "");
                    }else {
                        tvCarHeight.setText(carHeight + "");
                    }
                    break;
                case R.id.my_car_popup_img_car_height_add:
                    carHeight++;
                    tvCarHeight.setText(carHeight + "");
                    break;
                default:
                    break;
            }
        }
    };

    private FlowRadioGroup.OnCheckedChangeListener powerTypeCheckedChange = new
            FlowRadioGroup.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.my_car_popup_rb_fuel:
//                    AutoToast.show("燃油车");
                    break;
                case R.id.my_car_popup_rb_electric:
//                    AutoToast.show("纯电动");
                    break;
                case R.id.my_car_popup_rb_oil_electric_mixing:
//                    AutoToast.show("油电混合动力");
                    break;
                case R.id.my_car_popup_rb_diesel_oil:
//                    AutoToast.show("柴油");
                    break;
                case R.id.my_car_popup_rb_plug_in:
//                    AutoToast.show("插电式混合动力");
                    break;
                case R.id.my_car_popup_rb_adder:
//                    AutoToast.show("增程式动力");
                    break;
            }
        }
    };

    private FlowRadioGroup.OnCheckedChangeListener truckTypeCheckedChange =
            new FlowRadioGroup.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.my_car_popup_rg_miniature:
//                    AutoToast.show("微型货车");
                    break;
                case R.id.my_car_popup_rg_light:
//                    AutoToast.show("轻型货车");
                    break;
                case R.id.my_car_popup_rg_midsize:
//                    AutoToast.show("中型货车");
                    break;
                case R.id.my_car_popup_rg_heavy:
//                    AutoToast.show("重型货车");
                    break;
                case R.id.my_car_popup_rg_trailer:
//                    AutoToast.show("挂车");
                    break;
                case R.id.my_car_popup_rg_special_purpose:
//                    AutoToast.show("专用货车");
                    break;
                case R.id.my_car_popup_rg_cross_country:
//                    AutoToast.show("越野货车");
                    break;
            }
        }
    };

    private FlowRadioGroup.OnCheckedChangeListener axesNumberCheckedChange = new
            FlowRadioGroup.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.my_car_popup_rb_axes_two:
//                    AutoToast.show("2轴");
                    break;
                case R.id.my_car_popup_rb_axes_three:
//                    AutoToast.show("3轴");
                    break;
                case R.id.my_car_popup_rb_axes_four:
//                    AutoToast.show("4轴");
                    break;
                case R.id.my_car_popup_rb_axes_five:
//                    AutoToast.show("5轴");
                    break;
                case R.id.my_car_popup_rb_axes_six:
//                    AutoToast.show("6轴");
                    break;
                case R.id.my_car_popup_rb_axes_six_above:
//                    AutoToast.show("6轴以上");
                    break;
            }
        }
    };

    private FlowRadioGroup.OnCheckedChangeListener dischargeCheckedChange = new
            FlowRadioGroup.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.my_car_popup_rb_discharge_one:
//                    AutoToast.show("国1");
                    break;
                case R.id.my_car_popup_rb_discharge_two:
//                    AutoToast.show("国2");
                    break;
                case R.id.my_car_popup_rb_discharge_three:
//                    AutoToast.show("国3");
                    break;
                case R.id.my_car_popup_rb_discharge_four:
//                    AutoToast.show("国4");
                    break;
                case R.id.my_car_popup_rb_discharge_five:
//                    AutoToast.show("国5");
                    break;
                case R.id.my_car_popup_rb_discharge_six:
//                    AutoToast.show("国6");
                    break;
                case R.id.my_car_popup_rb_discharge_no:
//                    AutoToast.show("暂无排放标准");
                    break;
            }
        }
    };

    private FlowRadioGroup.OnCheckedChangeListener licensePlateColorCheckedChange = new
            FlowRadioGroup.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.my_car_popup_rb_license_plate_color_yellow:
//                    AutoToast.show("黄色车牌");
                    break;
                case R.id.my_car_popup_rb_license_plate_color_blue:
//                    AutoToast.show("蓝色车牌");
                    break;
                case R.id.my_car_popup_rb_license_plate_color_green:
//                    AutoToast.show("绿色车牌");
                    break;
                case R.id.my_car_popup_rb_license_plate_color_black:
//                    AutoToast.show("黑色车牌");
                    break;
                case R.id.my_car_popup_rb_license_plate_color_white:
//                    AutoToast.show("白色车牌");
                    break;
            }
        }
    };

    private FlowRadioGroup.OnCheckedChangeListener purposeCheckedChange = new
            FlowRadioGroup.OnCheckedChangeListener(){
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.my_car_popup_rb_purpose_no:
//                    AutoToast.show("暂不选择");
                    break;
                case R.id.my_car_popup_rb_purpose_dangerous_vehicles:
//                    AutoToast.show("危化车辆");
                    break;
            }
        }
    };

    @Override
    public boolean onBackPressed() {
        NavHostFragment.findNavController(MyCarFragment.this).popBackStack();
        return false;
    }
}