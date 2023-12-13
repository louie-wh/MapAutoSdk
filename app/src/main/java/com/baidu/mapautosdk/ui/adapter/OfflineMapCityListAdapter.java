package com.baidu.mapautosdk.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.api.offline.AutoLocalMap;
import com.baidu.mapautosdk.api.offline.model.AutoLocalMapSearchRecord;
import com.baidu.mapautosdk.platform.offline.localmap.LocalMapResource;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OfflineMapCityListAdapter
        extends Adapter<OfflineMapCityListAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<LocalMapResource> data;
    private List<AutoLocalMapSearchRecord> downloading;
    private AutoLocalMap mOfflineMap = AutoLocalMap.getInstance();

    public OfflineMapCityListAdapter(Context context, ArrayList<LocalMapResource> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.offline_map_list_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        LocalMapResource bean = data.get(position);
        holder.cityName.setText(bean.name);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        holder.tvSize.setText(decimalFormat.format(bean.mapsize / 1024f / 1024f) + "M");
        LocalMapResource cityById = AutoLocalMap.getInstance().getCityById(bean.id);

        if (cityById != null) {

            if (cityById.downloadStatus == 0) {
                holder.pb.setProgress(100);
                holder.pb.setVisibility(View.VISIBLE);
                holder.rlDownload.setVisibility(View.VISIBLE);
                holder.tvDownloadOk.setVisibility(View.GONE);
            } else if (cityById.downloadStatus == 1) {
                holder.pb.setVisibility(View.VISIBLE);
                holder.rlDownload.setVisibility(View.VISIBLE);
                holder.tvDownloadOk.setVisibility(View.GONE);
                holder.tvStatus.setVisibility(View.VISIBLE);
                holder.tvStatus.setText("（正在下载）");
                holder.tvStatus.setTextColor(Color.parseColor("#3366FF"));
                holder.pb.setProgress(cityById.downloadProgress);
                holder.imgDownload.setImageResource(R.mipmap.ic_pause);
                holder.tvDownload.setText(bean.downloadProgress + "%");
            } else if (cityById.downloadStatus == 3) {
                holder.pb.setVisibility(View.VISIBLE);
                holder.rlDownload.setVisibility(View.VISIBLE);
                holder.tvDownloadOk.setVisibility(View.GONE);
                holder.tvStatus.setVisibility(View.VISIBLE);
                holder.tvStatus.setText("（" + "已暂停" + cityById.downloadProgress + "%）");
                holder.tvStatus.setTextColor(Color.parseColor("#F76454"));
                holder.pb.setProgress(cityById.downloadProgress);
                holder.imgDownload.setImageResource(R.mipmap.ic_continue);
                holder.tvDownload.setText("继续");
            } else if (cityById.downloadStatus == 4) {
                holder.pb.setVisibility(View.GONE);
                holder.rlDownload.setVisibility(View.GONE);
                holder.tvDownloadOk.setVisibility(View.VISIBLE);
            }
        }

        holder.rlDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.downloadStatus == 0){
                    mOfflineMap.start(bean.id);
                    bean.downloadStatus = 1;
                }else if (bean.downloadStatus == 1){
                    mOfflineMap.pause(bean.id);
                    bean.downloadStatus = 3;
                }else if (bean.downloadStatus == 3){
                    mOfflineMap.start(bean.id);
                    bean.downloadStatus = 1;
                }else if (bean.downloadStatus == 4){
                    holder.pb.setVisibility(View.GONE);
                    holder.rlDownload.setVisibility(View.GONE);
                    holder.tvDownloadOk.setVisibility(View.VISIBLE);
                }
                mOfflineMap.init(new AutoLocalMap.OnAutoDownloadListener() {
                    @Override
                    public void onProgressUpdate(List<AutoLocalMapSearchRecord> waitingDownload,
                                                 List<AutoLocalMapSearchRecord> downloading) {
                        StringBuffer stringBuffer = new StringBuffer();
                        holder.pb.setVisibility(View.VISIBLE);
                        holder.rlDownload.setVisibility(View.VISIBLE);
                        holder.tvDownloadOk.setVisibility(View.GONE);
                        holder.tvStatus.setVisibility(View.VISIBLE);
                        holder.tvStatus.setText("（正在下载）");
                        holder.tvStatus.setTextColor(Color.parseColor("#3366FF"));
                        holder.imgDownload.setImageResource(R.mipmap.ic_pause);
                        for (AutoLocalMapSearchRecord mkolSearchRecord : downloading) {
                            holder.pb.setProgress(mkolSearchRecord.downloadProgress);
                            holder.tvDownload.setText(mkolSearchRecord.downloadProgress + "%");
                            String downloadingTask = "正在下载: " + mkolSearchRecord.name + " 已经下载进度: " +
                                    mkolSearchRecord.downloadProgress + "\n" + " 下载状态: " +
                                    mkolSearchRecord.downloadStatus + "\n";
                            stringBuffer.append(downloadingTask);
                        }
                        for (AutoLocalMapSearchRecord mkolSearchRecord : waitingDownload) {
                            String downloadingTask = "等待下载: " + mkolSearchRecord.name  + "\n" + " 下载状态: " +
                                    mkolSearchRecord.downloadStatus + "\n";
                            stringBuffer.append(downloadingTask);
                        }
                        Log.i("autosdk_offline", "onProgressUpdate : + \n" + stringBuffer.toString());
                    }

                    @Override
                    public void onStart(String msg) {
                        Log.i("autosdk_offline", "onStart" + msg);
                    }

                    @Override
                    public void onFinish(List<AutoLocalMapSearchRecord> finshedDownload) {
                        StringBuffer stringBuffer = new StringBuffer();
                        holder.pb.setVisibility(View.GONE);
                        holder.tvStatus.setVisibility(View.GONE);
                        holder.rlDownload.setVisibility(View.GONE);
                        holder.tvDownloadOk.setVisibility(View.VISIBLE);
                        for (AutoLocalMapSearchRecord mkolSearchRecord : finshedDownload) {
                            String finshTask = "下载完成 [离线地图包]: ----  " + mkolSearchRecord.name +
                                    mkolSearchRecord.downloadStatus + "\n";
                            stringBuffer.append(finshTask);
                        }
                        Log.i("autosdk_offline", "onFinish" + stringBuffer);
                    }

                    @Override
                    public void onPause(List<AutoLocalMapSearchRecord> stopDownload, String msg) {
                        if (stopDownload != null) {
                            StringBuffer stringBuffer = new StringBuffer();
                            holder.pb.setVisibility(View.VISIBLE);
                            holder.rlDownload.setVisibility(View.VISIBLE);
                            holder.tvDownloadOk.setVisibility(View.GONE);
                            holder.tvStatus.setVisibility(View.VISIBLE);
                            holder.tvStatus.setText("（" + "已暂停" + stopDownload.get(0).downloadProgress + "%）");
                            holder.tvStatus.setTextColor(Color.parseColor("#F76454"));
                            holder.pb.setProgress(stopDownload.get(0).downloadProgress);
                            holder.imgDownload.setImageResource(R.mipmap.ic_continue);
                            holder.tvDownload.setText("继续");
                            for (AutoLocalMapSearchRecord mkolSearchRecord : stopDownload) {
                                String finshTask = "暂停下载 [离线地图包]: ----  " + mkolSearchRecord.name + "\n";
                                stringBuffer.append(finshTask);
                            }
                            Log.i("autosdk_offline", "onPause : + \n" + stringBuffer);
                        }
                    }

                    @Override
                    public void onUpdate(String msg) {
                        Log.i("autosdk_offline", "onUpdate : + \n" + msg);
                    }

                    @Override
                    public void onDelete(String msg) {
                    }
                });
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data == null ? 0 : this.data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView cityName;
        private TextView tvStatus;
        private TextView tvSize;
        private ProgressBar pb;
        private RelativeLayout rlDownload;
        private ImageView imgDownload;
        private TextView tvDownload;
        private TextView tvDownloadOk;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            cityName = itemView.findViewById(R.id.offline_map_item_tv_city);
            tvStatus = itemView.findViewById(R.id.offline_map_item_tv_status);
            tvSize = itemView.findViewById(R.id.offline_map_item_tv_size);
            pb = itemView.findViewById(R.id.offline_map_item_pb);
            rlDownload = itemView.findViewById(R.id.offline_map_item_rl_download);
            imgDownload = itemView.findViewById(R.id.offline_map_item_img_download);
            tvDownload = itemView.findViewById(R.id.offline_map_item_tv_download);
            tvDownloadOk = itemView.findViewById(R.id.offline_map_item_tv_download_ok);
        }
    }

    public interface OnClickListener {
        void onItemClick(int position, TextView tvStatus, ProgressBar pb, ImageView imgDownload,
                         TextView tvDownload, TextView tvDownloadOk, RelativeLayout rl);
    }

    private OfflineMapCityListAdapter.OnClickListener itemClickListener;

    public void setItemClickListener(OnClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setData(List<AutoLocalMapSearchRecord> downloading){
        this.downloading = downloading;
    }
}

