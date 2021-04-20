package example.memorableplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.DialogTitle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.CaseMap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    Marker myMarker;
    String add = "";
    LocationManager locationManager;
    LocationListener locationListener;

    private GoogleMap mMap;
    public void centerMapOnLocation(Location location,String title){
        if(location != null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    centerMapOnLocation(lastLocation,"Your Location");
                }
        }

    }

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
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
        Intent intent = getIntent();
        if (intent.getIntExtra("position", 0) == 0) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    centerMapOnLocation(location, "Your Location");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastLocation, "Your Location");
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
            placeLocation.setLatitude(MainActivity.location.get(intent.getIntExtra("position", 0)).latitude);
            placeLocation.setLongitude(MainActivity.location.get(intent.getIntExtra("position", 0)).longitude);
            centerMapOnLocation(placeLocation, MainActivity.arrayList.get(intent.getIntExtra("position", 0)));
        }
    }
        // Add a marker in Sydney and move the camera

            @Override
             public void onMapLongClick(LatLng latLng){
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            String address = "";
            try {
                List<Address> listAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (listAddress != null && listAddress.size() > 0) {
                    if (listAddress.get(0).getThoroughfare() != null) {
                        if (listAddress.get(0).getSubThoroughfare() != null) {
                            address += listAddress.get(0).getSubThoroughfare() + " ";
                        }
                        address += listAddress.get(0).getThoroughfare();
                    }
                }

                }catch(Exception e){
                    e.printStackTrace();
                }

                if (address.equals("")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("HH: mm yyyy-MM-dd");
                    address += sdf.format(new Date());
                }// Marker was not set yet. Add marker:
                mMap.addMarker(new MarkerOptions().position(latLng).title(address).snippet("Your Marked Location"));
                MainActivity.arrayList.add(address);
                MainActivity.location.add(latLng);
                MainActivity.arrayAdapter.notifyDataSetChanged();
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("example.memorableplaces", Context.MODE_PRIVATE);
                try {
                    ArrayList<String> latitude = new ArrayList<>();
                    ArrayList<String> longitude = new ArrayList<>();
                    for (LatLng coord : MainActivity.location) {
                        latitude.add(Double.toString(coord.latitude));
                        longitude.add(Double.toString(coord.longitude));
                    }
                    sharedPreferences.edit().putString("places", ObjectSerializer.serialize(MainActivity.arrayList)).apply();
                    sharedPreferences.edit().putString("lats", ObjectSerializer.serialize(latitude)).apply();
                    sharedPreferences.edit().putString("lons", ObjectSerializer.serialize(longitude)).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "Location Saved!", Toast.LENGTH_SHORT).show();
            }
        }


