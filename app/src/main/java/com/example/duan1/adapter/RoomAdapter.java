package com.example.duan1.adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duan1.Interface.IClickItemRoom;
import com.example.duan1.R;
import com.example.duan1.api.ApiClient;
import com.example.duan1.api.ApiService;
import com.example.duan1.databinding.ItemRoomBinding;
import com.example.duan1.model.Room;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.MyViewHolder> {
    Context context;
    List<Room> list;
    private IClickItemRoom iClickItemListener;
    private ApiService apiService;

    public RoomAdapter(Context context, List<Room> list, IClickItemRoom listener) {
        this.context = context;
        this.list = list;
        this.iClickItemListener = listener;
        this.apiService = ApiClient.getApiService();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ItemRoomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Room room = list.get(position);
        holder.binding.tvRoomCode.setText(room.getRoomCode());

        // Gọi API để kiểm tra trạng thái phòng
        apiService.checkRoom(room.getRoomCode()).enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Phòng tồn tại (occupied)
                    Log.d(TAG, "Room " + room.getRoomCode() + " is occupied");
                    holder.binding.img.setImageResource(R.drawable.door_close);
                    holder.itemView.setOnClickListener(view -> {
                        Log.d(TAG, "Room " + room.getRoomCode() + " clicked (occupied)");
                        iClickItemListener.onClickItemRoom(room, true);
                    });
                } else {
                    // Phòng trống (empty) or not found
                    Log.d(TAG, "Room " + room.getRoomCode() + " is empty or not found: HTTP Code: " + response.code() + ", Message: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            Log.d(TAG, "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading errorBody: " + e.getMessage(), e);
                        }
                    }
                    Log.d(TAG, "Raw response: " + response.raw().toString());
                    holder.binding.img.setImageResource(R.drawable.door_open);
                    holder.itemView.setOnClickListener(view -> {
                        Log.d(TAG, "Room " + room.getRoomCode() + " clicked (empty)");
                        iClickItemListener.onClickItemRoom(room, false);
                    });
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                Log.e(TAG, "Failed to check room " + room.getRoomCode() + ": " + t.getMessage(), t);
                holder.binding.img.setImageResource(R.drawable.room_open3);
                holder.itemView.setOnClickListener(view -> {
                    Log.d(TAG, "Room " + room.getRoomCode() + " clicked (assumed empty due to failure)");
                    iClickItemListener.onClickItemRoom(room, false);
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemRoomBinding binding;

        public MyViewHolder(ItemRoomBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}