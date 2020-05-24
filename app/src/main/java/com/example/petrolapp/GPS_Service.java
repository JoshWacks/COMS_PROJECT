package com.example.petrolapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import androidx.annotation.Nullable;

public class GPS_Service extends Service {
    private LocationListener listener;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    @SuppressLint("MissingPermission")
    public void onCreate(){
        listener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i=new Intent("location_update");
                Bundle extras=new Bundle();
                extras.putDouble("x_co",location.getLongitude());
                extras.putDouble("y_co",location.getLatitude());
                i.putExtras(extras);
                sendBroadcast(i);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

            }
        };
        locationManager=(LocationManager)getApplication().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,listener);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(locationManager!=null){
            locationManager.removeUpdates(listener);
        }
    }



}
