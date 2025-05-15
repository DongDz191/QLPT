package com.example.duan1.util;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class Utils {

    public static String formatMoney(int amountMoney) {

        Locale locale = new Locale("vi", "VN");
        Currency currency = Currency.getInstance(locale);

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        currencyFormatter.setCurrency(currency);

        String formatted = currencyFormatter.format(amountMoney);

        return formatted ;
    }
}
