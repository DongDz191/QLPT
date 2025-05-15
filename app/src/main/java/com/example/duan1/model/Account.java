package com.example.duan1.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Account implements Serializable {
    @PrimaryKey
    @NonNull
    private String username;
    private String password;
    private String name;
    private String phone;
    private String title;
    private String image;

    public Account() {
    }

    public Account(@NonNull String username, String password, String name, String phone, String title, String image) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.title = title;
        this.image = image;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
