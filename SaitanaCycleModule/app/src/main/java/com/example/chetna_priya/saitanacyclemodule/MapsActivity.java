package com.example.chetna_priya.saitanacyclemodule;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.chetna_priya.saitanacyclemodule.backend.JSONService;
import com.example.chetna_priya.saitanacyclemodule.backend.NetClientGET;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NetClientGET.OnResultCallback,
         GoogleMap.OnInfoWindowClickListener {

    private static final String PLACES_RESULT = "places_result";
    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private static final String ACCESS_TOKEN_EXTRA = "access_token";
    private final int RESULT_CODE_SUCCESS = 200;
    private String accessToken;
    private String result;
    private ArrayList<PlaceObj> placeList = new ArrayList<>();
    private boolean isMapPopulated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(getIntent().hasExtra(ACCESS_TOKEN_EXTRA)) {
            accessToken = getIntent().getStringExtra(ACCESS_TOKEN_EXTRA);
            Log.d(TAG, "GETTTTTTTTTT ACCESSSSSSSSSS TOKENNNNNNNNNNNNNNN");
        }
        if(accessToken != null) {
            JSONService  jsonService = new JSONService();
            jsonService.getPlaces(accessToken, this);
            Log.d(TAG, "SERVICEEEEEEEEEEE STARTEDDDDDDDDDDDDDDDDDDDDDD\n"+accessToken);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey(ACCESS_TOKEN_EXTRA))
            accessToken = savedInstanceState.getString(ACCESS_TOKEN_EXTRA);
        if(savedInstanceState.containsKey(PLACES_RESULT))
            placeList = savedInstanceState.getParcelableArrayList(PLACES_RESULT);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ACCESS_TOKEN_EXTRA, accessToken);
        outState.putParcelableArrayList(PLACES_RESULT, placeList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mMap =  null;
        super.onDestroy();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        if(placeList.size()>0)
            populateMap();
        // Add a marker in Sydney and move the camera
        //  LatLng sydney = new LatLng(-34, 151);
        //  mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onResult(int resultCode, JSONObject output) {
        if(resultCode == RESULT_CODE_SUCCESS){
            parseJson(output);
            //   Log.d(TAG, result);
        }else
            makeToast(resultCode);
    }


    private void makeToast(int resultCode) {
        String message = "";
        switch (resultCode){
            case 401:
                message =  "User is unauthorized";
                break;
            default:
                message = "An internal error occured, please try again";
        }
        Snackbar.make(getCurrentFocus(), message, Snackbar.LENGTH_LONG).show();

    }

    private void parseJson(JSONObject output) {
        try {
            JSONArray results = output.getJSONArray("results");
            LocationObj location=null;
            String id="";
            String name="";
            for(int i =0; i<results.length();i++){
                JSONObject resultObject = results.getJSONObject(i);
                JSONObject locationObj = resultObject.getJSONObject("location");
                double lat = locationObj.getDouble("lat");
                double lng = locationObj.getDouble("lng");
                location = new LocationObj(lat,lng);
                id = resultObject.getString("id");
                name = resultObject.getString("name");
                PlaceObj placeObj = new PlaceObj(location, id, name);
                placeList.add(placeObj);
                Log.d(TAG, "LOCATION "+name);
            }
            Log.d(TAG, "PLACE LIST SIZEEEEEEEEEEEEE "+placeList.size());
            populateMap();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void populateMap() {
        if(mMap != null && !isMapPopulated){
            isMapPopulated = true;
            final Marker[] markers = new Marker[placeList.size()];

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postAtFrontOfQueue(new Runnable() {
                @Override
                public void run() {
                    for(int i=0; i<placeList.size();i++){
                        Log.d(TAG, "POPULATEEEEEEEE MAPPPPPPPPPPPPPP ");
                        final PlaceObj placeObj = placeList.get(i);
                        final LatLng latLng = new LatLng(placeObj.getLocation().getLatitude(),
                                placeObj.getLocation().getLongitude());
                        final int finalI = i;
                        markers[finalI] = mMap.addMarker(new MarkerOptions().position(latLng).title(placeObj.getName()));


                /**/
                    }

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                    }
                    final LatLngBounds bounds = builder.build();
                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            int padding = 0; // offset from edges of the map in pixels
                            if(placeList.size() == 1){//In case only 1 place is returned we don't want zoom to be unnatural
                                CameraUpdate center=
                                        CameraUpdateFactory.newLatLng(markers[0].getPosition());
                                CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
                            }else {
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                mMap.animateCamera(cu);
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(marker.getTitle());
        alertDialogBuilder.setMessage(getString(R.string.rent));
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(MapsActivity.this, PaymentActivity.class);
                intent.putExtra(ACCESS_TOKEN_EXTRA, accessToken);
                startActivity(intent);
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.setCancelable(true);
        dialog.show();
    }
}
