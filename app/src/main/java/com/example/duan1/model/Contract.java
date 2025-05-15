package com.example.duan1.model;

import java.io.Serializable;

public class Contract implements Serializable {
    private int idContract;
    private String statingDate;
    private String endingDate;
    private int status;
    private String roomCode;

    public Contract() {
    }

    public Contract(int idContract, String statingDate, String endingDate, int status, String roomCode) {
        this.idContract = idContract;
        this.statingDate = statingDate;
        this.endingDate = endingDate;
        this.status = status;
        this.roomCode = roomCode;
    }

    public Contract(String statingDate, String endingDate, int status, String roomCode) {
        this.statingDate = statingDate;
        this.endingDate = endingDate;
        this.status = status;
        this.roomCode = roomCode;
    }

    public int getIdContract() {
        return idContract;
    }

    public void setIdContract(int idContract) {
        this.idContract = idContract;
    }

    public String getStatingDate() {
        return statingDate;
    }

    public void setStatingDate(String statingDate) {
        this.statingDate = statingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
}