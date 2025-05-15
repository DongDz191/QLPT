package com.example.duan1.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Room implements Serializable {
    private String roomCode;
    private int floor;
    private String describe;
    private String image;
    private int idRoomType;

    public Room() {
    }

    public Room(String roomCode, int floor, String describe, String image, int idRoomType) {
        this.roomCode = roomCode;
        this.floor = floor;
        this.describe = describe;
        this.image = image;
        this.idRoomType = idRoomType;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getIdRoomType() {
        return idRoomType;
    }

    public void setIdRoomType(int idRoomType) {
        this.idRoomType = idRoomType;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomCode='" + roomCode + '\'' +
                ", floor=" + floor +
                ", describe='" + describe + '\'' +
                ", image='" + image + '\'' +
                ", idRoomType=" + idRoomType +
                '}';
    }

    // Custom serialization
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    // Custom deserialization
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
}