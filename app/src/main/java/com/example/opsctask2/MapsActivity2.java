package com.example.opsctask2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.SphericalUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.opencensus.internal.Utils;

import static android.content.ContentValues.TAG;
import static io.opencensus.tags.TagKey.MAX_LENGTH;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback  {

    private GoogleMap mMap;
    String apikey = "AIzaSyBev14gFIXImwWda63HUN2exvobViR_MsQ";
    String TAG = "placeautocomplete";
    private FusedLocationProviderClient fusedLocationProviderClient;
    Location home,destination;
    ArrayList<LatLng> markerPoints;
    float[] distance;
    TextView text;
    String url;
    MarkerOptions start,stop;
    private Polyline currentPolyline;
    Button search;
    TextView info;
    FirebaseAuth fAuth;
    String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        text = (TextView) findViewById(R.id.infomrationText);
        search = (Button) findViewById(R.id.button2);
        fAuth = FirebaseAuth.getInstance();


        Places.initialize(getApplicationContext(), apikey);
        PlacesClient placesClient = Places.createClient(this);
        //mMap.setMyLocationEnabled(true);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){

                    onLocationChanged(location);
                    home = location;



                }
            }
        });

        // Initialize the AutocompleteSupportFragment.
        final AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


// Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS));

// Set up a PlaceSelectionListener to handle the response.
        markerPoints = new ArrayList<LatLng>();
        //mMap = mapFragment.getMapAsync(this);

        //mMap = mapFragment.getMapAsync();

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {


            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId()+", "+place.getLatLng()+", "+place.getAddress());
                if(mMap!=null) {
                    mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                    LatLng autoCompleteLatLng=place.getLatLng();
                    Location newLoc=new Location("New");
                    newLoc.setLatitude(autoCompleteLatLng.latitude);
                    newLoc.setLongitude(autoCompleteLatLng.longitude);
                    onLocationChanged(newLoc);
                    destination = newLoc;
                    //JSONObject jObject;
                    //SphericalUtil.computeDistanceBetween(home.getLatitude(), destination);
                    //Location.distanceBetween(home.getLatitude(),home.getLongitude(),destination.getLatitude(),destination.getLongitude(),distance);
                    text.setVisibility(View.VISIBLE);
                    float distance1 = home.distanceTo(destination)/1000;
                    double roundedFloat = Math.round(distance1 * 100.0) / 100.0;
                    double time = roundedFloat/40*60;
                    double time1 = Math.round(time*100)/100;
                    String distance2 = String.valueOf(roundedFloat);





                    text.setText("Distance:\n"+distance2+"Km\n"+"Estimated time:\n"+time1+" Minutes");
                    search.setVisibility(View.VISIBLE);

                    search.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try{
                                String hLat = String.valueOf(home.getLatitude());
                                String hLon = String.valueOf(home.getLongitude());
                                String dLat = String.valueOf(destination.getLatitude());
                                String dLon = String.valueOf(destination.getLongitude());
                                FirebaseFirestore fstore = FirebaseFirestore.getInstance();

                                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();



                                userID = fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fstore.collection("trips").document(random());
                                Map<String,Object> trip = new HashMap<>();


                                trip.put("userid", userID);
                                trip.put("Start Latitude", hLat);
                                trip.put("Start Longitude", hLon);
                                trip.put("End Latitude", dLat);
                                trip.put("End Longitude", dLon);

                                documentReference.set(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG","onSuccess: User profile is created for "+ userID);
                                        Toast.makeText(MapsActivity2.this, "Trip has been saved", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                documentReference.set(trip).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG","onFailure: "+ e.toString());
                                    }
                                });



                                start = new MarkerOptions().position(new LatLng(home.getLatitude(),home.getLongitude())).title("Origin");
                                stop = new MarkerOptions().position(new LatLng(destination.getLatitude(),destination.getLongitude())).title("Destination");

                                url = getUrl(start.getPosition(), stop.getPosition(),"driving");
                                new FetchURL(MapsActivity2.this).execute(url,"driving");
                                search.setVisibility(View.GONE);
                                handleNewLocation1(home);
                            }
                            catch (Exception e){
                                Toast.makeText(getApplicationContext(), "Error, try again" , Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                    //Toast.makeText(getApplicationContext(), distance2 , Toast.LENGTH_SHORT).show();


                    /*Polyline line = mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(home.getLatitude(),home.getLongitude()), new LatLng(destination.getLatitude(), destination.getLongitude()))
                            .width(10)
                            .color(Color.BLUE).geodesic(true));*/




                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


    }
    public  String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }







    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        LocationSettingsRequest.Builder builder1 = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


    }



    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    private void handleNewLocation(Location location){
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }
    private void handleNewLocation1(Location location){
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }



    public void onLocationChanged(Location location){
        handleNewLocation(location);
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
}
