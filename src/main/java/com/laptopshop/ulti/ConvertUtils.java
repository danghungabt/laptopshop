package com.laptopshop.ulti;

import org.springframework.stereotype.Component;


public class ConvertUtils {

    public static String convertStateOrder(String state){
        switch (state){
            case "dang-cho-giao":
                return "Đang chờ giao";
            case "dang-giao":
                return "Đang giao";
            case "cho-duyet":
                return "Chờ duyệt";
            case "hoan-thanh":
                return "Hoàn thành";
            case "da-bi-huy":
                return "Đã bị hủy";
            default:
                return null;
        }
    }
}
