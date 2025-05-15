package com.example.duan1.model;

import java.io.Serializable;

public class Users  implements Serializable {
    private String name,thepassword,phone_number,password;

    public Users(String name, String thepassword, String phone_number, String password) {
        this.name = name;
        this.thepassword = thepassword;
        this.phone_number = phone_number;
        this.password = password;
    }

    public Users() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThepassword() {
        return thepassword;
    }

    public void setThepassword(String thepassword) {
        this.thepassword = thepassword;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
