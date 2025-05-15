package com.example.duan1.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(primaryKeys = {"idRoomType","idUtility"},
        foreignKeys = {
        @ForeignKey(entity = Utility.class, childColumns = "idUtility", parentColumns = "idUtility", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = RoomType.class, childColumns = "idRoomType", parentColumns = "idRoomType", onDelete = ForeignKey.CASCADE)})
public class UtilityDetail {
    @NonNull
    private int idRoomType;
    @NonNull
    private int idUtility;

    public UtilityDetail() {
    }

    public UtilityDetail(int idRoomType, int idUtility) {
        this.idRoomType = idRoomType;
        this.idUtility = idUtility;
    }

    public int getIdRoomType() {
        return idRoomType;
    }

    public void setIdRoomType(int idRoomType) {
        this.idRoomType = idRoomType;
    }

    public int getIdUtility() {
        return idUtility;
    }

    public void setIdUtility(int idUtility) {
        this.idUtility = idUtility;
    }
}
