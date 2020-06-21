package com.example.petrolapp;

import java.util.ArrayList;
//Class to organise all the statistics about each station
public class Station {
    private String name;
    private ArrayList<Double>entries=new ArrayList<Double>();


    public Station(String s){
        name=s;
    }

    public String getName(){
        return name;
    }

    public void addEntry(Double d){
        entries.add(d);
    }

    public Double getAverage(){
        Double sum=0.0;
        for(Double d:entries){
            sum=sum+d;
        }
        return (sum/entries.size());
    }


}
