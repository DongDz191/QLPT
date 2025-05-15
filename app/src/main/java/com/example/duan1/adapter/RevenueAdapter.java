package com.example.duan1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.R;
import com.example.duan1.model.Revenue;
import com.example.duan1.util.Utils;

import java.util.List;

public class RevenueAdapter extends RecyclerView.Adapter<RevenueAdapter.MyViewHolder> {

    Context context;
    List<Revenue> list;

    public RevenueAdapter(Context context, List<Revenue> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_revenue, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Revenue revenue = list.get(position);
        holder.paidAmount.setText(Utils.formatMoney(revenue.getPaidAmount()));
        holder.unPaidAmount.setText(Utils.formatMoney(revenue.getUnpaidAmount()));
        holder.contract.setText(revenue.getIdContract() + "");


    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView paidAmount, unPaidAmount , contract;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            paidAmount = itemView.findViewById(R.id.paid);
            unPaidAmount = itemView.findViewById(R.id.unpaid);
            contract = itemView.findViewById(R.id.contract);
        }
    }

}
