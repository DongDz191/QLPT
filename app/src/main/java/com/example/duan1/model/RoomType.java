package com.example.duan1.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RoomType {
    @PrimaryKey(autoGenerate = true)
    private int idRoomType;
    private String name;
    private int rentCode;
    private int area;
    private int electronicFee;
    private int waterFee;

    public RoomType() {
    }

    public RoomType(int idRoomType, String name, int rentCode, int area, int electronicFee, int waterFee) {
        this.idRoomType = idRoomType;
        this.name = name;
        this.rentCode = rentCode;
        this.area = area;
        this.electronicFee = electronicFee;
        this.waterFee = waterFee;
    }

    public RoomType(String name, int rentCode, int area, int electronicFee, int waterFee) {
        this.name = name;
        this.rentCode = rentCode;
        this.area = area;
        this.electronicFee = electronicFee;
        this.waterFee = waterFee;
    }

    public int getIdRoomType() {
        return idRoomType;
    }

    public void setIdRoomType(int idRoomType) {
        this.idRoomType = idRoomType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRentCode() {
        return rentCode;
    }

    public void setRentCode(int rentCode) {
        this.rentCode = rentCode;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getElectronicFee() {
        return electronicFee;
    }

    public void setElectronicFee(int electronicFee) {
        this.electronicFee = electronicFee;
    }

    public int getWaterFee() {
        return waterFee;
    }

    public void setWaterFee(int waterFee) {
        this.waterFee = waterFee;
    }


}
