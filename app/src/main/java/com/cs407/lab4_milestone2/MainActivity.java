package com.cs407.lab4_milestone2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager; //  gets the location data from the Android device
    LocationListener locationListener; // use the data from our LocationManager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager =  (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateLocationInfo(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s){

            }
        };
        if (Build.VERSION.SDK_INT < 23){
            startListening();
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                // if the user grants us permission, we request location updates and
                // use the location manager to get the last known location of the device
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    updateLocationInfo(location);
                }
            }
        }
    }
    /*
        * update the location info on our screen.
        * used for displaying the location data on the screen
     */
    public void updateLocationInfo(Location location){
        Log.i("LocationInfo", location.toString());

        TextView lat = (TextView) findViewById(R.id.lat);
        TextView longi =  (TextView) findViewById(R.id.longi);
        TextView alt = (TextView) findViewById(R.id.alti);
        TextView accu = (TextView) findViewById(R.id.accu);

        lat.setText("Latitude: \n" + String.format("%.4f", location.getLatitude()));
        longi.setText("Longitude: \n" + String.format("%.4f", location.getLongitude()));
        alt.setText("Altitude: \n" + String.format("%.4f", location.getAltitude()));
        accu.setText("Accuracy: \n"+ String.format("%.4f", location.getAccuracy()));
        //  Constructing our address and displaying in on the screen
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try{
            String address = "Couldn't find address";
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (listAddresses != null && listAddresses.size() > 0){
                Log.i("PlaceInfo", listAddresses.get(0).toString());
                address = "Address: \n";

                if (listAddresses.get(0).getSubThoroughfare() != null){
                    address += listAddresses.get(0).getSubThoroughfare() + " ";
                }
                if(listAddresses.get(0).getThoroughfare() != null){
                    address += listAddresses.get(0).getThoroughfare() + "\n";
                }
                if(listAddresses.get(0).getLocality() != null){
                    address += listAddresses.get(0).getLocality() + "\n";
                }
                if(listAddresses.get(0).getPostalCode() != null){
                    address += listAddresses.get(0).getPostalCode() + "\n";
                }
                if(listAddresses.get(0).getCountryName() != null){
                    address += listAddresses.get(0).getCountryName() + "\n";
                }
            }
            TextView addr = findViewById(R.id.address);
            addr.setText(address);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    /*
        * Use  for getting location details
     */
    private void startListening() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    /*
     * checks if the permission result is valid and is equal to PERMISSIONGRANTED and
     * then calls our startListening() method that we just defined.
     */
    @Override
    public void onRequestPermissionsResult(int requesCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requesCode, permissions, grantResults);

        if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }
}