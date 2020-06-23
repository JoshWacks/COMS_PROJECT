package com.example.petrolapp;

import java.util.ArrayList;

public class CarType {
    private String brand;
    private String model;
    private String type;
    private String year;
    private ArrayList<Double> entries=new ArrayList<Double>();

    public CarType(String b,String m,String y){
        brand=b;
        model=m;
        year=y;
        type=b+m+y;
    }

    public String getType(){
        return type;
    }
    public String getModel(){
        return model;
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
