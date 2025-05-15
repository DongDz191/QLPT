package com.example.duan1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.Interface.IClickItemRoom;
import com.example.duan1.databinding.ItemFloorBinding;
import com.example.duan1.model.Floor;
import com.example.duan1.model.Room;

import java.util.List;

public class FloorAdapter extends RecyclerView.Adapter<FloorAdapter.MyViewHolder> {
    Context context;
    List<Floor> list;
    private IClickItemRoom iClickItemListener;
    public FloorAdapter(Context context, List<Floor> list, IClickItemRoom listener) {
        this.context = context;
        this.list = list;
        this.iClickItemListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ItemFloorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        List<Room> listRoom = list.get(position).getList();
        if(listRoom.size() > 0){
            holder.binding.tvFloor.setText("Tầng " + listRoom.get(0).getFloor());
            LinearLayoutManager manager = new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false);
            holder.binding.rvRoom.setLayoutManager(manager);
            holder.binding.rvRoom.setAdapter(new RoomAdapter(context,listRoom,iClickItemListener));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ItemFloorBinding binding;
        public MyViewHolder(ItemFloorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
