package com.example.duan1.model;

import java.io.Serializable;

public class Member implements Serializable {
    private int idMember;
    private String name;
    private String birthday;
    private String citizenIdentification;
    private String image;
    private String phone;
    private String hometown;
    private int idContract;

    public Member() {
    }

    public Member(int idMember, String name, String birthday, String citizenIdentification,
                  String image, String phone, String hometown, int idContract) {
        this.idMember = idMember;
        this.name = name;
        this.birthday = birthday;
        this.citizenIdentification = citizenIdentification;
        this.image = image;
        this.phone = phone;
        this.hometown = hometown;
        this.idContract = idContract;
    }

    public Member(String name, String birthday, String citizenIdentification, String image, String phone, String hometown, int idContract) {
        this.name = name;
        this.birthday = birthday;
        this.citizenIdentification = citizenIdentification;
        this.image = image;
        this.phone = phone;
        this.hometown = hometown;
        this.idContract = idContract;
    }

    public int getIdMember() {
        return idMember;
    }

    public void setIdMember(int idMember) {
        this.idMember = idMember;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getCitizenIdentification() {
        return citizenIdentification;
    }

    public void setCitizenIdentification(String citizenIdentification) {
        this.citizenIdentification = citizenIdentification;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public int getIdContract() {
        return idContract;
    }

    public void setIdContract(int idContract) {
        this.idContract = idContract;
    }
}