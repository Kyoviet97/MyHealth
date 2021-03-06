package com.health.myhealth.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.health.myhealth.R;
import com.health.myhealth.model.UserModel;
import com.health.myhealth.utils.OnClickRecyclerView;

import java.util.List;

public class AdapterRecyclerHistory extends RecyclerView.Adapter<AdapterRecyclerHistory.ViewHolder>{
    public List<UserModel.DateHealth> dateHealthList;
    public OnClickRecyclerView onClickRecyclerView;

    public AdapterRecyclerHistory(List<UserModel.DateHealth> dateHealthList, OnClickRecyclerView onClickRecyclerView) {
        this.dateHealthList = dateHealthList;
        this.onClickRecyclerView = onClickRecyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_history_health, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtDateHistory.setText(dateHealthList.get(position).getDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRecyclerView.onClickRecyclerView(dateHealthList.get(position).getDate());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateHealthList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtDateHistory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDateHistory = itemView.findViewById(R.id.txt_date_history);
        }
    }

}
