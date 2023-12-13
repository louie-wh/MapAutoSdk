package com.baidu.mapautosdk.ui.adapter;

import java.util.ArrayList;

import com.baidu.mapautosdk.platform.offline.localmap.LocalMapResource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

public class CityListAdapter
        extends Adapter<CityListAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<LocalMapResource> data;
    private CityListAdapter.OnClickListener itemClickListener;

    public CityListAdapter(Context context, ArrayList<LocalMapResource> data,
                           CityListAdapter.OnClickListener itemClickListener) {
        this.context = context;
        this.data = data;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(android.R.layout.activity_list_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.cityName.setText(data.get(position).name);
        holder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.data == null ? 0 : this.data.size();
    }

    public interface OnClickListener {
        void onItemClick(int var1);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private View itemView;
        private TextView cityName;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            cityName = itemView.findViewById(android.R.id.text1);
        }
    }
}

