package com.example.duan1.base;

public class BaseCheckValid {
    public static boolean checkEmptyString(String ...arr){
        for (String s:arr)
            if(s.isEmpty())
                return false;
        return true;
    }
}
