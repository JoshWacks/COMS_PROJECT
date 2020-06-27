package com.example.petrolapp;

public class appInformation {
    private static String username;
    private static String activity;
    private static String petrolPrice;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String u) {
        username = u;
    }

    public static String getActivity() {
        return activity;
    }

    public static void setActivity(String activity) {
        appInformation.activity = activity;
    }

    public static String getPetrolPrice() {
        return petrolPrice;
    }

    public static void setPetrolPrice(String petrolPrice) {
        appInformation.petrolPrice = petrolPrice;
    }
}
