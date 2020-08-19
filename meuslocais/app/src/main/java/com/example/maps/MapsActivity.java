package com.example.maps;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.maps.controller.Controle;
import com.example.maps.db.PontosDB;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;


    public void onClickAdd(View view)
    {
        mMap.addMarker(new MarkerOptions().position(new LatLng(47.23135, 39.72328)).draggable(true).icon(
                BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }


    private void drawMarker(LatLng point){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);


        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);
    }

    private class LocationInsertTask extends android.os.AsyncTask<ContentValues, Void, Void>
    {
        @Override
        protected Void doInBackground(ContentValues... contentValues) {

            /** Setting up values to insert the clicked location into SQLite database */
            getContentResolver().insert(Controle.CONTENT_URI, contentValues[0]);
            return null;
        }
    }

    private class LocationDeleteTask extends android.os.AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {

            /** Deleting all the locations stored in SQLite database */
            getContentResolver().delete(Controle.CONTENT_URI, null, null);
            return null;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0,
                                         Bundle arg1)
    {

        // Uri to the content provider LocationsContentProvider
        Uri uri = Controle.CONTENT_URI;

        // Fetches all the rows from locations table
        return new CursorLoader(this, uri, null, null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0,
                               Cursor arg1)
    {
        int locationCount = 0;
        double lat=0;
        double lng=0;
        float zoom=0;

        // Number of locations available in the SQLite database table
        locationCount = arg1.getCount();

        // Move the current record pointer to the first row of the table
        arg1.moveToFirst();

        for(int i=0;i<locationCount;i++)
        {

            // Get the latitude
            lat = arg1.getDouble(arg1.getColumnIndex(PontosDB.FIELD_LAT));

            // Get the longitude
            lng = arg1.getDouble(arg1.getColumnIndex(PontosDB.FIELD_LNG));

            // Get the zoom level
            zoom = arg1.getFloat(arg1.getColumnIndex(PontosDB.FIELD_ZOOM));

            // Creating an instance of LatLng to plot the location in Google Maps
            LatLng location = new LatLng(lat, lng);

            // Drawing the marker in the Google Maps
            drawMarker(location);

            // Traverse the pointer to the next row
            arg1.moveToNext();
        }

        if(locationCount>0)
        {
            // Moving CameraPosition to last clicked position
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat,lng)));

            // Setting the zoom level in the map on last position  is clicked
            mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0)
    {
        // TODO Auto-generated method stub
    }




    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        getLocation();


        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                Intent intent = new Intent(MapsActivity.this, Campo.class);
                startActivity(intent);



                // Drawing marker on the map
                drawMarker(point);

                // Creating an instance of ContentValues
                ContentValues contentValues = new ContentValues();

                // Setting latitude in ContentValues
                contentValues.put(PontosDB.FIELD_LAT, point.latitude );

                // Setting longitude in ContentValues
                contentValues.put(PontosDB.FIELD_LNG, point.longitude);

                // Setting zoom in ContentValues
                contentValues.put(PontosDB.FIELD_ZOOM, mMap.getCameraPosition().zoom);

                // Creating an instance of LocationInsertTask
                LocationInsertTask insertTask = new LocationInsertTask();

                // Storing the latitude, longitude and zoom level to SQLite database
                insertTask.execute(contentValues);

                Toast.makeText(getBaseContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();


            }
        });

//        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener()
//        {
//            @Override
//            public void onMapLongClick(LatLng point)
//            {
//
//                // Removing all markers from the Google Map
//                mMap.clear();
//
//                // Creating an instance of LocationDeleteTask
//                LocationDeleteTask deleteTask = new LocationDeleteTask();
//
//                // Deleting all the rows from SQLite database table
//                deleteTask.execute();
//
//                Toast.makeText(getBaseContext(), "All markers are removed", Toast.LENGTH_LONG).show();
//
//            }
//        });
//
//        LatLng rostov = new LatLng(47.23135, 39.72328);
//        /*mMap.addMarker(new MarkerOptions().position(rostov).title("Rostov-on-Don").icon(
//                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)
//                )
//        );*/
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(rostov, 14.f));
    }




    public void getLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", location.toString());
                mMap.clear();
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
                String address = "";
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> listOfAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (listOfAddresses != null && listOfAddresses.size() > 0) {

                        Log.i("Address", listOfAddresses.get(0).toString());
                        if (listOfAddresses.get(0).getAddressLine(0) != null) {
                            address += listOfAddresses.get(0).getAddressLine(0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    address = "Unknown address";
                }
                mMap.addMarker(new MarkerOptions().position(userLocation).title(address));
            }


        };
        //Check's if location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //if permission is granted, request location updates using GPS
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                mMap.clear();
                LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
                Log.i("Last Known Location", userLocation.toString());
            }
        } else {
            //if permission is not granted, ask user for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) { //request code 1 is asking permission for ACCESS_FINE_LOCATION
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //if user permission is granted, check self permission
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, locationListener);
                }
            }
        }
    }


}