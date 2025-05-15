package com.example.duan1.model;

import java.io.Serializable;

public class UtilityDetails implements Serializable {
    private int idRoomType;
    private int idUtility;

    public UtilityDetails() {
    }

    public UtilityDetails(int idRoomType, int idUtility) {
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