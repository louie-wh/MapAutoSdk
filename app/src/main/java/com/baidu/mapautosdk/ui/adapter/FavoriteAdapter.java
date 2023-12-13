package com.baidu.mapautosdk.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.baidu.mapautosdk.R;
import com.baidu.mapautosdk.model.AutoFavoriteInfo;

import java.util.ArrayList;

public class FavoriteAdapter extends Adapter<FavoriteAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<AutoFavoriteInfo> data;

    public FavoriteAdapter(Context context, ArrayList<AutoFavoriteInfo> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_favorite_list, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AutoFavoriteInfo dataInfos = data.get(position);
        String rename = dataInfos.getRename();
        if (TextUtils.isEmpty(rename)){
            holder.name.setText(dataInfos.getName());
        }else {
            holder.name.setText(rename);
        }
        holder.addrs.setText(dataInfos.getAddress());

        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null){
                    onItemClickListener.onGoPoiClick(position);
                }
            }
        });

        holder.goHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null){
                    onItemClickListener.onGoHereClick(position);
                }
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
        private RelativeLayout rl;
        private TextView name;
        private TextView addrs;
        private ImageView goHere;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            rl = itemView.findViewById(R.id.item_favorite_rl_go_poi);
            name = itemView.findViewById(R.id.item_favorite_list_tv_name);
            addrs = itemView.findViewById(R.id.item_favorite_list_tv_addrs);
            goHere = itemView.findViewById(R.id.item_favorite_img_go_here);
        }
    }

    public interface OnItemClickListener{
        void onGoPoiClick(int position);
        void onGoHereClick(int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

