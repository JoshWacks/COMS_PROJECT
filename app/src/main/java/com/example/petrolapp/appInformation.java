package com.example.petrolapp;

public class appInformation {
    //Class to share all the information needed by each different activty

    private static String username;
    private static String pastActivity;
    private static String petrolPrice;
    private static String newStationName="";
    private static String liscence_plate="";

    public static String getUsername() {//when the username is needed
        return username;
    }

    public static void setUsername(String u) {
        username = u;
    }

    public static String getActivity() {//some classes need to know which activity it came from
        return pastActivity;
    }

    public static void setActivity(String activity) {
        appInformation.pastActivity = activity;
    }

    public static String getPetrolPrice() {//if the petrol price is needed
        return petrolPrice;
    }

    public static void setPetrolPrice(String petrolPrice) {
        appInformation.petrolPrice = petrolPrice;
    }

    public static String getNewStationName() {
        return newStationName;
    }

    public static void setNewStationName(String newStationName) {
        appInformation.newStationName = newStationName;
    }

    public static String getLiscence_plate() {
        return liscence_plate;
    }

    public static void setLiscence_plate(String liscence_plate) {
        appInformation.liscence_plate = liscence_plate;
    }
}
