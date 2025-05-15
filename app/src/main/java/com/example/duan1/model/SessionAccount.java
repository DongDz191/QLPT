package com.example.duan1.model;

import android.content.Context;
import android.content.SharedPreferences;


public class SessionAccount {
    public static String USERNAME = "username" ;
    public static String PASSWORD = "password";
    public static String NAME = "name" ;
    public static String PHONE = "phone";
    public static String TITLE = "title" ;
    public static String IMAGE = "image";

    private SharedPreferences pref ;
    public SessionAccount(Context context) {
        pref = context.getSharedPreferences("account", Context.MODE_PRIVATE);
    }
    public void saveAccount(Account account){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SessionAccount.USERNAME, account.getUsername());
        editor.putString(SessionAccount.PASSWORD, account.getPassword());
        editor.putString(SessionAccount.NAME, account.getName());
        editor.putString(SessionAccount.PHONE, account.getPhone());
        editor.putString(SessionAccount.TITLE, account.getTitle());
        editor.putString(SessionAccount.IMAGE, account.getImage());
        editor.commit();
    }

    public Account fetchAccount(){
        Account account = new Account();
        account.setUsername(pref.getString(SessionAccount.USERNAME,""));
        account.setPassword(pref.getString(SessionAccount.PASSWORD,""));
        account.setName(pref.getString(SessionAccount.NAME,""));
        account.setTitle(pref.getString(SessionAccount.TITLE,""));
        account.setPhone(pref.getString(SessionAccount.PHONE,""));
        account.setImage(pref.getString(SessionAccount.IMAGE,""));
        return account;
    }

    public void dropAccount(){
        pref.edit().clear().commit();
    }
}
