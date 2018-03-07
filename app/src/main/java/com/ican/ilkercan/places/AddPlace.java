package com.ican.ilkercan.places;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.ican.ilkercan.utils.Constants;
import com.ican.ilkercan.utils.MyPlaces;
import com.ican.ilkercan.utils.PlaceItem;
import com.ican.ilkercan.utils.UtilsUI;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPlace extends AppCompatActivity {

    EditText txtName;
    EditText txtLatitude;
    EditText txtLongitude;
    LatLng point;

    private void InitControls()
    {
        txtName = (EditText)findViewById(R.id.txtName);
        txtLatitude = (EditText)findViewById(R.id.txtLatitude);
        txtLongitude = (EditText)findViewById(R.id.txtLongitude);

        point = (LatLng) getIntent().getExtras().get(Constants.EXTRA_CODE_FOR_LOCATION);

        if(point != null)
        {
            txtLatitude.setText(String.valueOf(point.latitude));
            //txtLatitude.setEnabled(false);

            txtLongitude.setText(String.valueOf(point.longitude));
            //txtLongitude.setEnabled(false);
        }

        Button btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(txtName.getText().toString().isEmpty())
                {
                    UtilsUI.ShowMessage(AddPlace.this, getString(R.string.entername));
                }
                else
                {
                    Save();

                    Intent intent = AddPlace.this.getIntent();
                    //intent.putExtra("SOMETHING", "EXTRAS");
                    AddPlace.this.setResult(RESULT_OK, intent);

                    finish();
                }

            }
        });
    }

    private void Save()
    {
        String name = txtName.getText().toString();
        LatLng place = new LatLng(Double.valueOf(txtLatitude.getText().toString()), Double.valueOf(txtLongitude.getText().toString()));

        Gson gson = new Gson();
        String myVal = UtilsUI.GetSharedPreferenceValue(AddPlace.this, Constants.SPMYPLACES);
        MyPlaces myPlaces = gson.fromJson(myVal, MyPlaces.class);

        if(myPlaces == null)
        {
            myPlaces = new MyPlaces();
        }
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");

        String key = s.format(new Date());
        myPlaces.AddPlace(new PlaceItem(name, place, key));

        String json = gson.toJson(myPlaces);

        UtilsUI.SetSharedPreferenceValue(AddPlace.this, Constants.SPMYPLACES, json);

        UtilsUI.ShowMessage(AddPlace.this, getString(R.string.placesaved));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        InitControls();
    }
}
