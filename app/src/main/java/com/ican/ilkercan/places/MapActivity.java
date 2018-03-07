package com.ican.ilkercan.places;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.ican.ilkercan.utils.Constants;

/*
* Goto https://github.com/googlemaps/android-samples
*
* for more information and samples.
*
* https://developers.google.com/maps/documentation/android-api/code-samples
* */


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private GoogleMap mMap;
    private Marker currLocationMarker;
    private GoogleApiClient mGoogleApiClient;
    int LOCATION_PERMISSION_REQUEST_CODE = 123;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //getSupportActionBar().hide();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.a_address_map);

        mapFragment.getMapAsync(this);

    }

    private void enableMyLocation() {

        if (mMap != null) {

            try {
                mMap.setMyLocationEnabled(true);
            } catch (SecurityException e) {
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {


        mGoogleApiClient = new GoogleApiClient.Builder(MapActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void SetCurrentLocation(Location location) {

        //place marker at current position :

        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = mMap.addMarker(markerOptions);

        //zoom to current position:
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(14).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        //If you only need one location, unregister the listener
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        mMap.getUiSettings().setZoomControlsEnabled(false);

        enableMyLocation();

        buildGoogleApiClient();

        mGoogleApiClient.connect();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                /*
                Intent intent = new Intent();

                intent.putExtra(Constants.EXTRA_CODE_FOR_LOCATION, latLng);

                setResult(RESULT_OK, intent);
                */

                Intent intent = new Intent(MapActivity.this, AddPlace.class);
                intent.putExtra(Constants.EXTRA_CODE_FOR_LOCATION, latLng);
                startActivityForResult(intent,Constants.SAVEPAGE_REQUEST_CODE);

                //finish();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode == Constants.SAVEPAGE_REQUEST_CODE) && (resultCode == RESULT_OK))
        {
            Intent intent = MapActivity.this.getIntent();
            //intent.putExtra("SOMETHING", "EXTRAS");
            MapActivity.this.setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            Location mLastLocation = null;

            if (ContextCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                PermissionUtils.requestPermission(MapActivity.this, LOCATION_PERMISSION_REQUEST_CODE, android.Manifest.permission.ACCESS_FINE_LOCATION, true);
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            } else {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }

            if (mLastLocation != null) {

                mMap.clear();

                SetCurrentLocation(mLastLocation);

            } else {
                Toast.makeText(MapActivity.this, getString(R.string.r_msg_loc_info), Toast.LENGTH_LONG).show();

            }

        } catch (SecurityException e) {

        } catch (Exception e) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
