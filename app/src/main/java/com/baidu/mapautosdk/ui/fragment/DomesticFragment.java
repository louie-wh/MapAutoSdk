package com.baidu.mapautosdk.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.offline.AutoLocalMap;
import com.baidu.mapautosdk.api.offline.model.AutoLocalMapSearchRecord;
import com.baidu.mapautosdk.platform.offline.localmap.LocalMapResource;
import com.baidu.mapautosdk.ui.BaseFragment;
import com.baidu.mapautosdk.ui.adapter.OfflineMapCityListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DomesticFragment extends BaseFragment implements AutoLocalMap.OnAutoDownloadListener {

    private ViewGroup mContentView;
    private RecyclerView rlv;
    private Button del;
    private Button tv;
    private AutoLocalMap mOfflineMap;
    private TextView mTvStatus;
    private ProgressBar mPb;
    private ImageView mImgDownload;
    private TextView mTvDownload;
    private TextView mTvDownloadOk;
    private RelativeLayout mRlDownload;
    private OfflineMapCityListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = (ViewGroup) inflater.inflate(R.layout.fragment_domestic, container, false);
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
        rlv = view.findViewById(R.id.domestic_rlv);
        del = view.findViewById(R.id.domestic_del);
        tv = view.findViewById(R.id.domestic_tv);

        mOfflineMap = AutoLocalMap.getInstance();
        mOfflineMap.init(this);
        ArrayList<LocalMapResource> data = (ArrayList<LocalMapResource>) mOfflineMap.getAllCity();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            data = (ArrayList<LocalMapResource>) data.stream()
                    .filter(localMapResource -> localMapResource.frc == 1).collect(Collectors.toList());
        }
        int downloadStatus = data.get(1).downloadStatus;
        tv.setText(downloadStatus + "");
        ArrayList<LocalMapResource> finalData = data;
        adapter = new OfflineMapCityListAdapter(getContext(), finalData);
        rlv.setLayoutManager(new LinearLayoutManager(getContext()));
        rlv.setAdapter(adapter);

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOfflineMap.remove(131);

                ArrayList<LocalMapResource> data2 = (ArrayList<LocalMapResource>) mOfflineMap.getAllCity();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    data2 = (ArrayList<LocalMapResource>) data2.stream()
                            .filter(localMapResource -> localMapResource.frc == 1).collect(Collectors.toList());
                }
                int downloadStatus = data2.get(1).downloadStatus;
                tv.setText(downloadStatus + "");
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<LocalMapResource> data2 = (ArrayList<LocalMapResource>) mOfflineMap.getAllCity();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    data2 = (ArrayList<LocalMapResource>) data2.stream()
                            .filter(localMapResource -> localMapResource.frc == 1).collect(Collectors.toList());
                }
                Log.d("DomesticFragemnt", "onClick: " + data2.toString());
            }
        });
    }

    public boolean onBackPressed() {
        NavHostFragment.findNavController(DomesticFragment.this).popBackStack();
        return false;
    }

    @Override
    public void onProgressUpdate(List<AutoLocalMapSearchRecord> waitingDownload,
                                 List<AutoLocalMapSearchRecord> downloading) {
        adapter.setData(downloading);
        StringBuffer stringBuffer = new StringBuffer();
        // 未下载的list
        for (AutoLocalMapSearchRecord mkolSearchRecord : waitingDownload) {
            String waitTask = "等待下载: " + mkolSearchRecord.name + "\n";
            stringBuffer.append(waitTask);
        }
        for (AutoLocalMapSearchRecord mkolSearchRecord : downloading) {
            String downloadingTask = "正在下载: " + mkolSearchRecord.name + " 已经下载进度: " +
                    mkolSearchRecord.downloadProgress + "\n";
            stringBuffer.append(downloadingTask);
        }
        // 正在下载的list
        Log.i("autosdk_offline", "onProgressUpdate : + \n" + stringBuffer.toString());
    }

    @Override
    public void onStart(String msg) {
        Log.i("autosdk_offline", msg);
    }

    @Override
    public void onFinish(List<AutoLocalMapSearchRecord> finshedDownload) {
        StringBuffer stringBuffer = new StringBuffer();
        for (AutoLocalMapSearchRecord mkolSearchRecord : finshedDownload) {
            String finshTask = "下载完成 [离线地图包]: ----  " + mkolSearchRecord.name + "\n";
            stringBuffer.append(finshTask);
        }
        Log.i("autosdk_offline", "onFinsh : + \n" + stringBuffer.toString());
    }

    @Override
    public void onPause(List<AutoLocalMapSearchRecord> stopDownload, String msg) {
        if (stopDownload != null) {
            StringBuffer stringBuffer = new StringBuffer();
            for (AutoLocalMapSearchRecord mkolSearchRecord : stopDownload) {
                String finshTask = "暂停下载 [离线地图包]: ----  " + mkolSearchRecord.name + "\n";
                stringBuffer.append(finshTask);
            }
            Log.i("autosdk_offline", "onPause : + \n" + stringBuffer.toString());
        } else {
            Log.i("autosdk_offline", msg);
        }
    }

    @Override
    public void onUpdate(String msg) {
        Log.i("autosdk_offline", msg);
    }

    @Override
    public void onDelete(String msg) {
        Log.i("autosdk_offline", msg);
    }
}