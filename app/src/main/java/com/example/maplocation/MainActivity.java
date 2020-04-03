package com.example.maplocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    Geocoder geocoder;
    List< Address > addressList;
    Location mlocation;
    FusedLocationProviderClient fusedLocationProviderClient; //retrieve last loc of your device
    private static final int Request_Code=101;
    private Object PackageManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        GetLastLocation();
    }

    private void GetLastLocation() {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= android.content.pm.PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Request_Code);
        }
        Task<Location> task=fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener< Location >() {
            @Override
            public void onSuccess(Location location) {
                if(location != null)
                {
                    mlocation=location;
                    //Toast.makeText(getApplicationContext(),mlocation.getLatitude()+" "+mlocation.getLongitude(),
                          //  Toast.LENGTH_LONG).show();
                    SupportMapFragment supportMapFragment=(SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(MainActivity.this);
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        addressList = geocoder.getFromLocation(mlocation.getLatitude(), mlocation.getLongitude(), 1);

                        String address= addressList.get(0).getAddressLine(0);
                        String area= addressList.get(0).getLocality();
                        String city=addressList.get(0).getAdminArea();
                        String country=addressList.get(0).getCountryName();
                        String postalcode= addressList.get(0).getPostalCode();

                        String fullAddress=address+" "+area+" "+city+" "+country+" "+postalcode;
                        Toast.makeText(getApplicationContext(),fullAddress,Toast.LENGTH_LONG).show();
                    }
                    catch(IOException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng Latlng= new LatLng(mlocation.getLatitude(),mlocation.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions().position(Latlng).title("THIS IS MY LOCATION");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(Latlng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Latlng,6));
        googleMap.addMarker(markerOptions);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case Request_Code:
                if(grantResults.length>0 && grantResults[0]== android.content.pm.PackageManager.PERMISSION_GRANTED)
                {
                    GetLastLocation();
                }
        }

    }
}
