package com.yunussen.myapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng userLantLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                userLantLng=new LatLng(location.getLatitude(),location.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLantLng).title("your location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLantLng,15));
                Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses=null;
                    try
                    {
                        addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                        if(addresses.get(0)!=null&&addresses.size()>0)
                        {
                            System.out.println("address"+addresses.get(0).toString());
                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
            }
        };

        //izinler icin yazdım.
        askLocationPermission();
        mMap.setOnMapLongClickListener(this);
    }

    private void askLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            //izin verilmemişse izin istedim ve onRequestPermission overide methoduna yönlendirdim.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{
            //izin verilmişse kullanıcının lokasyonunu aldım.
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,5000,50,locationListener);

            //Kullanıcın bilinen son lokasyonunu aldım.

            Location lastLocation=locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            if(lastLocation.getAltitude()!=0){
                LatLng userLastLocation=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLastLocation).title("your  last location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation,15));
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults.length>0){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,5000,5,locationListener);
                }
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        String address="";

        try {
            List<Address>addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if(addressList.get(0)!=null&&addressList.size()>0){
                if(addressList.get(0).getThoroughfare()!=null){
                    address+=addressList.get(0).getThoroughfare();
                }
                if(addressList.get(0).getSubThoroughfare()!=null){
                    address+=addressList.get(0).getSubThoroughfare();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(address.matches("")){
            address="empty address";
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(address));

    }
}