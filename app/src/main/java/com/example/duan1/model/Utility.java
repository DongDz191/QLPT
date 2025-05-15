package com.example.duan1.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Utility {
    @PrimaryKey(autoGenerate = true)
    private int idUtility;
    private String name;
    private String image;

    public Utility() {
    }

    public Utility(int idUtility, String name, String image) {
        this.idUtility = idUtility;
        this.name = name;
        this.image = image;
    }

    public int getIdUtility() {
        return idUtility;
    }

    public void setIdUtility(int idUtility) {
        this.idUtility = idUtility;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
