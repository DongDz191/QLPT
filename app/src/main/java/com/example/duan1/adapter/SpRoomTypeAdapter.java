package com.example.duan1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.duan1.R;
import com.example.duan1.model.RoomType;

import java.util.List;

public class SpRoomTypeAdapter extends ArrayAdapter {
    Context context;
    List<RoomType> list;
    public SpRoomTypeAdapter(@NonNull Context context, @NonNull List<RoomType> objects) {
        super(context, 0, objects);
        this.context = context;
        this.list = objects;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position,convertView);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position,convertView);
    }

    public View createView(int position, View convertView){
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_room_type,null);
            holder.tvRoomType = convertView.findViewById(R.id.tvRoomType);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        RoomType roomType = list.get(position);
        holder.tvRoomType.setText(roomType.getName());
        return convertView;
    }

    public class ViewHolder{
        TextView tvRoomType;
    }

}
