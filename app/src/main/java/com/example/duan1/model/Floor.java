package com.example.duan1.model;

import java.util.List;

public class Floor {
    private int floor;
    private List<Room> list;

    public Floor() {
    }

    public Floor(int floor, List<Room> list) {
        this.floor = floor;
        this.list = list;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public List<Room> getList() {
        return list;
    }

    public void setList(List<Room> list) {
        this.list = list;
    }
}
