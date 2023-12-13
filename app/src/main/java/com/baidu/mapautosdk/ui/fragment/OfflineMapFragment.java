package com.baidu.mapautosdk.ui.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.offline.AutoLocalMap;
import com.baidu.mapautosdk.api.offline.model.AutoLocalMapSearchRecord;
import com.baidu.mapautosdk.platform.offline.localmap.LocalMapResource;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.ui.adapter.CityListAdapter;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 离线地图 接口测试fragment
 */
public class OfflineMapFragment extends BaseFragment implements
        View.OnClickListener, AutoLocalMap.OnAutoDownloadListener {

    private ViewGroup mContentView;
    private AutoLocalMap mOfflineMap;
    private int cityId;
    private EditText content;
    private RecyclerView mLocalMapListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_offline, container, false);
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
        content = view.findViewById(R.id.content);
        view.findViewById(R.id.start).setOnClickListener(this);
        mLocalMapListView = view.findViewById(R.id.rv_local_map_list);
        view.findViewById(R.id.pause).setOnClickListener(this);
        view.findViewById(R.id.del).setOnClickListener(this);
        view.findViewById(R.id.update).setOnClickListener(this);
        init();

        // 返回按钮
        view.findViewById(R.id.offline_img_back).setOnClickListener(clickListener);
        // 离线地图
        view.findViewById(R.id.offline_rl_offline_map).setOnClickListener(clickListener);
        // 离线包导航
        view.findViewById(R.id.offline_rl_offline_navigation).setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            switch (v.getId()) {
                case R.id.offline_img_back:
                    onBackPressed();
                    break;
                case R.id.offline_rl_offline_map:
                    NavHostFragment.findNavController(OfflineMapFragment.this)
                            .navigate(R.id.action_MainFragment_to_offlineMapDownloadFragment);
                    break;
                case R.id.offline_rl_offline_navigation:
                    // 离线包导航
                    NavHostFragment.findNavController(OfflineMapFragment.this)
                            .navigate(R.id.action_mainFragment_to_offlineNavPage);
                    break;
                default:
                    break;
            }
        }
    };

    private void init() {
        mOfflineMap = AutoLocalMap.getInstance();
        mOfflineMap.init(this);
        ArrayList<LocalMapResource> data = (ArrayList<LocalMapResource>) mOfflineMap.getAllCity();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            data = (ArrayList<LocalMapResource>) data.stream()
                    .filter(localMapResource -> localMapResource.frc == 1).collect(Collectors.toList());
        }
        ArrayList<LocalMapResource> finalData = data;
        CityListAdapter adapter = new CityListAdapter(getContext(),
                data, i -> content.setText(finalData.get(i).name));
        mLocalMapListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLocalMapListView.setAdapter(adapter);
    }

    @Override
    public boolean onBackPressed() {
        NavHostFragment.findNavController(OfflineMapFragment.this).popBackStack();
        return false;
    }

    @Override
    public void onClick(View v) {
        boolean isSearch = searchId();
        switch (v.getId()) {
            case R.id.start:
                if (isSearch) {
                    mOfflineMap.start(cityId);
                    Toast.makeText(getContext(), "开始下载", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.pause:
                if (isSearch) {
                    mOfflineMap.pause(cityId);
                    Toast.makeText(getContext(), "暂停下载", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.del:
                if (isSearch) {
                    mOfflineMap.remove(cityId);
                    Toast.makeText(getContext(), "删除下载", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.update:
                if (isSearch) {
                    mOfflineMap.update(cityId);
                    Toast.makeText(getContext(), "更新下载", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean searchId() {
        String cityName = content.getText().toString().trim();
        if (!TextUtils.isEmpty(cityName)) {
            List<AutoLocalMapSearchRecord> records = mOfflineMap.searchCity(cityName);
            if (records != null) {
                cityId = records.get(0).id;
                Log.i("hello", String.valueOf(records.get(0).frc));
                return true;
            } else {
                Toast.makeText(getContext(), "城市id查询失败", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getContext(), "请先输入城市名称进行查询", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onProgressUpdate(List<AutoLocalMapSearchRecord> list, List<AutoLocalMapSearchRecord> list1) {
        StringBuffer stringBuffer = new StringBuffer();
        // 未下载的list
        for (AutoLocalMapSearchRecord mkolSearchRecord : list) {
            String waitTask = "等待下载: " + mkolSearchRecord.name + "\n";
            stringBuffer.append(waitTask);
        }
        for (AutoLocalMapSearchRecord mkolSearchRecord : list1) {
            String downloadingTask = "正在下载: " + mkolSearchRecord.name + " 已经下载进度: " +
                    mkolSearchRecord.downloadProgress + "\n";
            stringBuffer.append(downloadingTask);
        }
        // 正在下载的list
        Log.i("autosdk_offline", "onProgressUpdate : + \n" + stringBuffer.toString());
    }

    @Override
    public void onStart(String s) {
        Log.i("autosdk_offline", s);
    }

    @Override
    public void onFinish(List<AutoLocalMapSearchRecord> list) {
        StringBuffer stringBuffer = new StringBuffer();
        for (AutoLocalMapSearchRecord mkolSearchRecord : list) {
            String finshTask = "下载完成 [离线地图包]: ----  " + mkolSearchRecord.name + "\n";
            stringBuffer.append(finshTask);
        }
        Log.i("autosdk_offline", "onFinsh : + \n" + stringBuffer.toString());
    }

    @Override
    public void onPause(List<AutoLocalMapSearchRecord> list, String msg) {
        if (list != null) {
            StringBuffer stringBuffer = new StringBuffer();
            for (AutoLocalMapSearchRecord mkolSearchRecord : list) {
                String finshTask = "暂停下载 [离线地图包]: ----  " + mkolSearchRecord.name + "\n";
                stringBuffer.append(finshTask);
            }
            Log.i("autosdk_offline", "onPause : + \n" + stringBuffer.toString());
        } else {
            Log.i("autosdk_offline", msg);
        }

    }

    @Override
    public void onUpdate(String s) {
        Log.i("autosdk_offline", s);
    }

    @Override
    public void onDelete(String s) {
        Log.i("autosdk_offline", s);
    }
}
