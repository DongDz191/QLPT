package com.example.duan1.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Service {
    @PrimaryKey(autoGenerate = true)
    private int idService;
    private String name;
    private int price;
    private String image;

    public Service() {
    }

    public Service(int idService, String name, int price, String image) {
        this.idService = idService;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public Service(String name, int price, String image) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
