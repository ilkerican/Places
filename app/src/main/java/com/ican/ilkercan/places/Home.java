package com.ican.ilkercan.places;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.ican.ilkercan.utils.Constants;
import com.ican.ilkercan.utils.HttpManager;
import com.ican.ilkercan.utils.MyPlaces;
import com.ican.ilkercan.utils.PlaceItem;
import com.ican.ilkercan.utils.ShowPlace;
import com.ican.ilkercan.utils.UtilsUI;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Home extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    ProgressDialog progressDialog;
    GetTravelTime mGetTravelTimeTask;
    private GoogleApiClient mGoogleApiClient;
    int LOCATION_PERMISSION_REQUEST_CODE = 123;
    Location mLastLocation = null;
    ListView listView;
    TextView noDataTv;

    private void InitAddButton()
    {
        FloatingActionButton btnAdd = (FloatingActionButton)findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Home.this, MapActivity.class);
                startActivityForResult(intent, Constants.MAPPAGE_REQUEST_CODE);
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {


        mGoogleApiClient = new GoogleApiClient.Builder(Home.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((requestCode == Constants.MAPPAGE_REQUEST_CODE) && (resultCode == RESULT_OK))
        {
            attemptGetTravelTime();
        }
    }


    private void attemptGetTravelTime() {

        if (mGetTravelTimeTask != null) {
            return;
        }

        String spMyPlaces = UtilsUI.GetSharedPreferenceValue(Home.this, Constants.SPMYPLACES);

        if((spMyPlaces != null) && !(spMyPlaces.isEmpty())) {

            Gson gson = new Gson();

            MyPlaces myPlaces = gson.fromJson(spMyPlaces, MyPlaces.class);

            UtilsUI.ShowProgressDialog(progressDialog, true);

            mGetTravelTimeTask = new GetTravelTime(myPlaces);
            mGetTravelTimeTask.execute((Void) null);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ContextCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            PermissionUtils.requestPermission(Home.this, LOCATION_PERMISSION_REQUEST_CODE, android.Manifest.permission.ACCESS_FINE_LOCATION, true);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            attemptGetTravelTime();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                attemptGetTravelTime();

            } else {
                Toast.makeText(this, R.string.location_not_permitted, Toast.LENGTH_LONG).show();

            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        noDataTv = (TextView) findViewById(R.id.no_data_tv);
        noDataTv.setText(getString(R.string.no_data));

        listView = (ListView) findViewById(R.id.place_list);

        progressDialog = UtilsUI.GetProgressDialog(Home.this);

        InitAddButton();

        buildGoogleApiClient();

        mGoogleApiClient.connect();
    }

    public static String getMetadata(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            // if we can’t find it in the manifest, just return null
        }

        return null;
    }

    private void InitList(List<ShowPlace> data) {
        if(data.size() > 0) {
            ArrayAdapter<ShowPlace> placeArrayAdapter = new PlaceArrayAdapter(this, 0, data);

            listView.setAdapter(placeArrayAdapter);

            noDataTv.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        else
        {
            noDataTv.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }


    }

    private void Delete(String key)
    {
        String spMyPlaces = UtilsUI.GetSharedPreferenceValue(Home.this, Constants.SPMYPLACES);

        if((spMyPlaces != null) && !(spMyPlaces.isEmpty())) {

            Gson gson = new Gson();

            MyPlaces myPlaces = gson.fromJson(spMyPlaces, MyPlaces.class);

            myPlaces.DeletePlace(key);

            String json = gson.toJson(myPlaces);

            UtilsUI.SetSharedPreferenceValue(Home.this, Constants.SPMYPLACES, json);

            UtilsUI.ShowMessage(Home.this, getString(R.string.placedeleted));

            UtilsUI.ShowProgressDialog(progressDialog, true);

            mGetTravelTimeTask = new GetTravelTime(myPlaces);
            mGetTravelTimeTask.execute((Void) null);
        }
    }

    class PlaceArrayAdapter extends ArrayAdapter<ShowPlace> {

        Context context;
        List<ShowPlace> objects;

        public PlaceArrayAdapter(Context context, int resource, List<ShowPlace> objects) {
            super(context, resource, objects);

            this.context = context;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ShowPlace showPlace = objects.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.place_item, null);

            TextView txtDistance = (TextView) view.findViewById(R.id.txtDistance);
            txtDistance.setText(showPlace.getDistance());

            TextView txtName = (TextView) view.findViewById(R.id.txtPlacename);
            txtName.setText(showPlace.getName());

            Button btnDuration = (Button) view.findViewById(R.id.btnDuration);
            btnDuration.setText(showPlace.getDuration());

            btnDuration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mLastLocation != null) {

                        String source = String.valueOf(mLastLocation.getLatitude()) + "," + String.valueOf(mLastLocation.getLongitude());
                        //String source = "41.111979,28.842578";
                        String destination = String.valueOf(showPlace.getLatLng().latitude) + "," + String.valueOf(showPlace.getLatLng().longitude);

                        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr=" + source + "&daddr=" + destination + "&directionsmode=driving");

                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                        mapIntent.setPackage("com.google.android.apps.maps");

                        startActivity(mapIntent);
                    }
                }
            });

            Button btnDelete = (Button)view.findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Delete(showPlace.getKey());
                }
            });


            return view;
        }
    }

    public class GetTravelTime extends AsyncTask<Void, Void, Boolean> {

        String response;
        MyPlaces mPlaces;

        List<ShowPlace> results;

        GetTravelTime(MyPlaces myPlaces) {

            mPlaces = myPlaces;

        }

        private void PrepareResults()
        {
            if(response != null)
            {
                if(!response.isEmpty())
                {
                    try {

                        results = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray destination_addresses = jsonObject.getJSONArray("destination_addresses");
                        JSONArray rows = jsonObject.getJSONArray("rows");
                        JSONObject elements = (JSONObject) rows.get(0);

                        for(int i = 0; i<elements.getJSONArray("elements").length(); i++) {

                            JSONObject element = (JSONObject) elements.getJSONArray("elements").get(i);

                            String time = element.getJSONObject("duration").getString("text");
                            String distance = element.getJSONObject("distance").getString("text");
                            String address = destination_addresses.get(i).toString();

                            ShowPlace showPlace = new ShowPlace();
                            showPlace.setAddress(address);
                            showPlace.setDistance(distance);
                            showPlace.setDuration(time);

                            showPlace.setKey(mPlaces.getPlaces().get(i).getKey());
                            showPlace.setName(mPlaces.getPlaces().get(i).getName());
                            showPlace.setLatLng(mPlaces.getPlaces().get(i).getLatLng());

                            results.add(showPlace);

                        }
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                if(mPlaces != null)
                {
                    int size = mPlaces.getPlaces().size();

                    if(size > 0)
                    {
                        String origins = "origins=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() ;
                        //String origins = "origins=" + "41.111979,28.842578";
                        String destinations = "destinations=";

                        for(int i = 0; i < size; i++)
                        {
                            if(i > 0)
                            {
                                destinations += "|";
                            }
                            destinations += String.valueOf(((PlaceItem)mPlaces.getPlaces().get(i)).getLatLng().latitude) + ","
                                    + String.valueOf(((PlaceItem)mPlaces.getPlaces().get(i)).getLatLng().longitude);
                        }

                        String APIkey = getMetadata(Home.this, "com.google.android.maps.v2.API_KEY");

                        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?language=tr&units=imperial&" + origins + "&" + destinations + "&key=" + APIkey;

                        /*
                        *
                        * ALTERNATIVE URL : try this  :
                        * https://maps.googleapis.com/maps/api/directions/json?language=tr&origin=41.068245,29.006761&destination=41.062922,28.901070&key=AIzaSyAscCIlaGZJkQesemSfO3_Lz_7-5XFRENg
                        *
                        * Need to run this for each destination
                        *
                        * So the algorithm needs to change...
                        *
                        * See how : https://stackoverflow.com/questions/30004793/running-same-asynctask-multiple-times-sequentially
                        *
                        * See how : https://stackoverflow.com/questions/29394875/android-asynctask-called-multiple-times
                        *
                        * http://jabahan.com/android-avoid-asynctask-running-multiple-times/
                        *
                        * http://android-delight.blogspot.com.tr/2015/12/how-to-execute-multiple-async-task-at.html
                        *
                        * */

                        response = HttpManager.getData(url);
                    }
                }


                /*
                MyPlaces places = new MyPlaces();

                LatLng l1 = new LatLng(41.111979,28.842578);
                LatLng l2 = new LatLng(41.111979,28.842578);

                SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");

                String key1 = s.format(new Date());

                Thread.sleep(2000);

                String key2 = s.format(new Date());

                PlaceItem p1 = new PlaceItem("p1", l1, key1);
                PlaceItem p2 = new PlaceItem("p2", l2, key2);

                places.AddPlace(p1);
                places.AddPlace(p2);

                places.DeletePlace(key1);


                Gson gson = new Gson();
                String json = gson.toJson(places);

                UtilsUI.SetSharedPreferenceValue(Home.this, Constants.SPMYPLACES, json);

                String myVal = UtilsUI.GetSharedPreferenceValue(Home.this, Constants.SPMYPLACES);

                MyPlaces obj = gson.fromJson(myVal, MyPlaces.class);

                */

                /*


                {
   "destination_addresses" : [
      "Karadolap Mahallesi, Şengüzel Sk. No:8, 34065 Eyüp/İstanbul, Türkiye",
      "Karadolap Mahallesi, Şengüzel Sk. No:8, 34065 Eyüp/İstanbul, Türkiye"
   ],
   "origin_addresses" : [
      "Oruçreis Mahallesi, Unnamed Road, 34235 Esenler/İstanbul, Türkiye"
   ],
   "rows" : [
      {
         "elements" : [
            {
               "distance" : {
                  "text" : "9,2 mil",
                  "value" : 14866
               },
               "duration" : {
                  "text" : "25 dakika",
                  "value" : 1503
               },
               "status" : "OK"
            },
            {
               "distance" : {
                  "text" : "9,2 mil",
                  "value" : 14866
               },
               "duration" : {
                  "text" : "25 dakika",
                  "value" : 1503
               },
               "status" : "OK"
            }
         ]
      }
   ],
   "status" : "OK"
}
                * */



                // See : https://developers.google.com/maps/documentation/distance-matrix/intro#DistanceMatrixRequests

                // See : https://developers.google.com/maps/documentation/distance-matrix/start

                //String APIkey = getMetadata(Home.this, "com.google.android.maps.v2.API_KEY");
                // Sample : origins=41.43206,-81.38992|-33.86748,151.20699
                //String origins = "origins=" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() ;

                //String origins = "origins=" + "41.111979,28.842578"; // comment this later
                //RC : String destinations = "destinations=" + nearestStation.getLatitude() + "," + nearestStation.getLongitude();
                //String destinations = "destinations=" + "41.111979,28.842578";// comment this later
                //String url = "https://maps.googleapis.com/maps/api/distancematrix/json?language=tr&units=imperial&" + origins + "&" + destinations + "&key=" + APIkey;



                //response = HttpManager.getData(url);

            } catch (Exception e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            mGetTravelTimeTask= null;

            UtilsUI.ShowProgressDialog(progressDialog, false);

            if (success) {

                progressDialog.dismiss();

                try {
                    /*
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray rows = jsonObject.getJSONArray("rows");
                    JSONObject elements = (JSONObject) rows.get(0);
                    JSONObject duration = (JSONObject)elements.getJSONArray("elements").get(0);
                    String time = duration.getJSONObject("duration").getString("text");
                    Button button = (Button)findViewById(R.id.a_homescreen_button_duration);
                    button.setText(time);
                    */
                    PrepareResults();

                    InitList(results);
                }
                catch (Exception e)
                {

                }

            } else {

                UtilsUI.ShowMessage(Home.this, getString(R.string.error_generic_description));
            }
        }

        @Override
        protected void onCancelled() {

            mGetTravelTimeTask = null;

            UtilsUI.ShowProgressDialog(progressDialog, false);

        }
    }
}
