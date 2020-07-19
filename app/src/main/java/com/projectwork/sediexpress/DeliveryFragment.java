package com.projectwork.sediexpress;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class DeliveryFragment extends Fragment implements OnMapReadyCallback {
    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    private static final int AUTO_COMPLETE_PICK = 1;
    private static final int AUTO_COMPLETE_DROP = 2;
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 5f;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private EditText tvPick, tvDrop;
    private String currentLocation = "";
    private MarkerOptions markerOptions;
    private ProgressBar progress;
    private ImageView imageView;
    private CardView search_badge;
    private NavController navController;
    private SupportMapFragment mapFragment;
    private ArrayList<LatLng> mMarkerPoints;
    private LatLng mOrigin;
    private LatLng mDestination;
    private PrefManager prefManager;
    private BottomNavigationView bottomNavigationView;
    private Context context;
    //vars
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    public static Boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
// This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
// This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);

        }
    }

    /**
     * Move the compass button to the right side, centered vertically.
     */
    private void moveCompassButton(View mapView) {
        try {
            assert mapView != null; // skip this if the mapView has not been set yet

            Log.d(TAG, "moveCompassButton()");

            // View view = mapView.findViewWithTag("GoogleMapCompass");
            View view = mapView.findViewWithTag("GoogleMapMyLocationButton");

            // move the compass button to the right side, centered
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.setMarginEnd(18);

            view.setLayoutParams(layoutParams);
        } catch (Exception ex) {
            Log.e(TAG, "moveCompassButton() - failed: " + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(getActivity(), "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (Utils.isLocationEnabled(getActivity())) {
                // getDeviceLocation();

                //  MarkerOptions markerOptions = new MarkerOptions();

                //  markerOptions.position(new LatLng(5.862707, -0.672011));
                //   markerOptions.title(currentLocation);
                //   markerOptions.snippet("Current Location");

                //   Marker location = mMap.addMarker(markerOptions);
                //    location.setDraggable(true);
                //     location.showInfoWindow();

                moveCamera(new LatLng(5.862707, -0.672011),
                        DEFAULT_ZOOM);
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                Log.d(TAG, "onMapClick: " + mMarkerPoints.size());

                View mapView = mapFragment.getView();
                moveCompassButton(mapView);
            }


            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Toast.makeText(getActivity(), "onInfoWindowClick: " + marker.getTitle(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onInfoWindowClick: " + marker.getTitle());
                }
            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    //Toast.makeText(getContext(), "maker clicked", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            mMap.setMinZoomPreference(10);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    //

                    // Adding new item to the ArrayList

//                    markerOptions.position(latLng);
//                    markerOptions.title(currentLocation);
//                    markerOptions.snippet("Current Location");
//
//                    Marker location = mMap.addMarker(markerOptions);
//                    location.setDraggable(true);
//                    location.showInfoWindow();
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                    // markerOptions.title(currentLocation);
                    // Already two locations
                    if (mMarkerPoints.size() > 1) {
                        mMarkerPoints.clear();
                        mMap.clear();
                    }

                    mMarkerPoints.add(latLng);

                    // Setting the position of the marker
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);

                    /**
                     * For the start location, the color of marker is GREEN and
                     * for the end location, the color of marker is RED.
                     */
                    Log.d(TAG, "onMapClick: " + mMarkerPoints.size());


                    if (mMarkerPoints.size() < 1) {
                        currentLocation = getLocationAddress(latLng.latitude, latLng.longitude);
                        tvPick.setText(currentLocation);
                        markerOptions.snippet("Pickup");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                        markerOptions.title(currentLocation);
                        Marker location = mMap.addMarker(markerOptions);
                        location.setDraggable(true);
                        location.showInfoWindow();
                        mMap.addMarker(markerOptions);
                    } else if (mMarkerPoints.size() > 1) {
                        // currentLocation = getLocationAddress(latLng.latitude,latLng.longitude);
                        tvDrop.setText(currentLocation);
                        markerOptions.title(currentLocation);
                        markerOptions.snippet("Drop Off");
                        Marker location = mMap.addMarker(markerOptions);
                        location.setDraggable(true);
                        location.showInfoWindow();
                        //  Add new marker to the Google Map Android API V2markerOptions.snippet("Drop Location");
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        mMap.addMarker(markerOptions);
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(1));

                    mMap.addMarker(markerOptions);

                    // Checks, whether start and end locations are captured
                    if (mMarkerPoints.size() > 1) {
                        mOrigin = mMarkerPoints.get(0);
                        mDestination = mMarkerPoints.get(1);
                    }
                    SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                    prefEditor.putString("location", currentLocation);
                    prefEditor.apply();

                    //  Toast.makeText(getActivity(), latLng.latitude+"----" +latLng.longitude+"", Toast.LENGTH_SHORT).show();
                    //  Toast.makeText(getContext(), "map clicked", Toast.LENGTH_SHORT).show();
                }
            });


        }


        search_badge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: " + tvPick.getText());
                String pickLocation = tvPick.getText().toString();
                String dropLocation = tvDrop.getText().toString();
                if (TextUtils.isEmpty(pickLocation)) {
                    Toast.makeText(getActivity(), "Pickup Location cant be empty", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(dropLocation)) {
                    Toast.makeText(getActivity(), "Drop Location cant be empty", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utils.haveNetworkConnection(getActivity())) {
                        prefManager.setDropLocation(dropLocation);
                        prefManager.setPickLocation(pickLocation);

//                        ProceedDialog cdd = new ProceedDialog(getActivity());
//                        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                        cdd.show();

                        new getPickLocationAsync().execute(pickLocation, dropLocation);
                    } else {
                        Toast.makeText(getActivity(), "Check internet connection", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Sedi Express Mobile");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_maps, container, false);
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Places.initialize(getActivity(), getString(R.string.google_maps_key));
        PlacesClient placesClient = Places.createClient(getActivity());

        context = getActivity();
        mMarkerPoints = new ArrayList<>();
        prefManager = new PrefManager(getActivity());

        bottomNavigationView = getActivity().findViewById(R.id.navigation);
        // bottomNavigationView.getMenu().getItem(1).setChecked(true);
        bottomNavigationView.getMenu().findItem(R.id.nav_home).setChecked(true);


        navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        search_badge = view.findViewById(R.id.search_badge);
        tvPick = view.findViewById(R.id.tvPick);
        tvDrop = view.findViewById(R.id.tvDrop);
        progress = view.findViewById(R.id.progress);
        imageView = view.findViewById(R.id.imageView);


        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.setRetainInstance(true);

        mapFragment.getMapAsync(this);


//        view.findViewById(R.id.pkuplocation).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Autocomplete.IntentBuilder(
//                        AutocompleteActivityMode.OVERLAY,Arrays.asList(Place.Field.ID,
//                                Place.Field.NAME,
//                                Place.Field.LAT_LNG,
//                                Place.Field.ADDRESS))
//                        .setTypeFilter(TypeFilter.ADDRESS)
////                       .setLocationBias(RectangularBounds.newInstance(
////                               new LatLng(-33.880490,151.184363),
////                               new LatLng(-33.858754,151.229596)
////                       ))
//                        .setCountry("GH")
//                        .build(getActivity());
//                startActivityForResult(intent,AUTO_COMPLETE_PICK);
//            }
//        });     view.findViewById(R.id.droplocation).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Autocomplete.IntentBuilder(
//                        AutocompleteActivityMode.OVERLAY,Arrays.asList(Place.Field.ID,
//                        Place.Field.NAME,
//                        Place.Field.LAT_LNG,
//                        Place.Field.ADDRESS))
//                        .setTypeFilter(TypeFilter.ADDRESS)
////                       .setLocationBias(RectangularBounds.newInstance(
////                               new LatLng(-33.880490,151.184363),
////                               new LatLng(-33.858754,151.229596)
////                       ))
//                        .setCountry("GH")
//                        .build(getActivity());
//                startActivityForResult(intent,AUTO_COMPLETE_DROP);
//            }
//        });


        getLocationPermission();
        init();
        if (!isLocationEnabled(getActivity())) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("GPS Location Service is OFF")  // GPS not found
                    .setMessage("Enable GPS Location to let Sedi access your location services") // Want to enable?
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getActivity());

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getActivity(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isLocationEnabled(getActivity());

    }

    //    private void initGooglePlacesApi() {
//        // Initialize Places.
//        Places.initialize(getActivity(), getResources().getString(R.string.google_maps_key));
//        // Create a new Places client instance.
//        PlacesClient placesClient = Places.createClient(getActivity());
//
//        // Initialize the AutocompleteSupportFragment.
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getActivity().getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
////        autocompleteFragment.setHint(getString(R.string.previous));
////        autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(
////                new LatLng(34.7006096, 19.2477876),
////                new LatLng(41.7488862, 29.7296986))); //Greece bounds
//        autocompleteFragment.setCountry("gh");
//
//
//        // Specify the types of place data to return.
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS));
//        autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
//
//
//        // Set up a PlaceSelectionListener to handle the response.
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                if(place.getAddressComponents().asList().get(0).getTypes().get(0).equalsIgnoreCase("route")){
//                    //binding.textViewLocation.setText(place.getAddress()); //Works well
//                    //location = place.getAddress();
//
//                }else{ //If user does not choose a specific place.
////                    AndroidUtils.vibratePhone(getApplication(), 200);
////                    TastyToast.makeText(getApplicationContext(),
////                            getString(R.string.choose_an_address), TastyToast.DEFAULT, TastyToast.CONFUSING);
//                }
//
//                Log.i(TAG, "Place: " + place.getAddressComponents().asList().get(0).getTypes().get(0) + ", " + place.getId() + ", " + place.getAddress());
//            }
//
//            @Override
//            public void onError(Status status) {
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });
//    }
    private void init() {


//        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
//                if(actionId == EditorInfo.IME_ACTION_SEARCH
//                        || actionId == EditorInfo.IME_ACTION_DONE
//                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
//                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
//
//                    //execute our method for searching
//                    geoLocate();
//                }
//
//                return false;
//            }
//        });


//        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                Log.d(TAG, "onMapClick: " );
//                Toast.makeText(
//                        getActivity(),
//                        "Lat : " + latLng.latitude + " , "
//                                + "Long : " + latLng.longitude,
//                        Toast.LENGTH_LONG).show();
//
//
//            }
//        });
//
//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//
//            @Override
//            public void onInfoWindowClick(Marker arg0) {
////                Intent intent = new Intent(getBaseContext(), Activity.class);
////                String reference = mMarkerPlaceLink.get(arg0.getId());
////                intent.putExtra("reference", reference);
////
////                // Starting the  Activity
////                startActivity(intent);
//                Log.d("mGoogleMap1", "Activity_Calling");
//                Toast.makeText(getActivity(), arg0.getTitle().toString(), Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                Toast.makeText(
//                        getActivity(),
//                        "Title : " + marker.getTitle()  ,
//                        Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");

        //String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName("searchString todo", 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());
            Toast.makeText(getActivity(), address.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            if (null != currentLocation) {
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        DEFAULT_ZOOM);
                            }


                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getActivity(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        if (isServicesOK()) {
//            SupportMapFragment mapFragment = (SupportMapFragment)getActivity(). getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(getActivity(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTO_COMPLETE_DROP) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                LatLng latLng = place.getLatLng();
                String mStringLatitude = String.valueOf(latLng.latitude);
                String mStringLongitude = String.valueOf(latLng.longitude);

                Toast.makeText(getActivity(), "lat : " + mStringLatitude + " long " + mStringLongitude, Toast.LENGTH_SHORT).show();
                tvDrop.setText(place.getAddress());

                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Drop Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getActivity(), "status:  " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "status:  " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "cancelled ", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == AUTO_COMPLETE_PICK) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                tvPick.setText("place: " + place.getName() + ", " + place.getId());
                Log.i(TAG, "place: " + place.getName() + ", " + place.getId());
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Pickup Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getActivity(), "status:  " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                Log.i(TAG, "status:  " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "cancelled ", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    Address getLocationFromAddress(String name) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocationName(name, 5); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.size() > 0)
                return addresses.get(0);
        } catch (ArrayIndexOutOfBoundsException | IOException e) {
            Log.i(TAG, "getLocationFromAddress: " + e.getMessage());
            e.printStackTrace();
        }

        return null;

    }

    public Address getLocationFromCoordinateAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public String getLocationAddress(double lat, double Long) {

        String mLocation = "";
//markerOptions.title("Waiting for location");
        //   tvDrop.setText("getting location...");
        Address locationAddress = getLocationFromCoordinateAddress(lat, Long);

        if (locationAddress != null) {

            String address = locationAddress.getAddressLine(0);
            String address1 = locationAddress.getAddressLine(1);
            String city = locationAddress.getLocality();
            String state = locationAddress.getAdminArea();
            String district = locationAddress.getSubAdminArea();
            String country = locationAddress.getCountryName();
            String postalCode = locationAddress.getPostalCode();


            if (!TextUtils.isEmpty(address)) {
                mLocation = address;
                if (!TextUtils.isEmpty(address1))
                    mLocation += "\n" + address1;
                else {
                    if (!TextUtils.isEmpty(postalCode))
                        mLocation += "\n" + postalCode;
                }

            }
        }
        return mLocation;
    }

    public int calculateDistanceInKilometer(double userLat, double userLng,
                                            double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
    }

    @SuppressLint("StaticFieldLeak")
    class getPickLocationAsync extends AsyncTask<String, String, ArrayList<Address>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(ArrayList<Address> address) {
            super.onPostExecute(address);
            Log.i(TAG, "onPostExecute: ");
            if (address.get(0) != null) {
                if (address.get(1) != null) {
                    Address pkAddress = address.get(0);
                    Address drAddress = address.get(1);
                    LatLng source = new LatLng(pkAddress.getLatitude(), pkAddress.getLongitude());
                    //  Toast.makeText(getContext(), ""+pkAddress.getLatitude()+"---"+pkAddress.getLongitude(), Toast.LENGTH_SHORT).show();
                    LatLng destination = new LatLng(drAddress.getLatitude(), drAddress.getLongitude());
                    //  Toast.makeText(getContext(), ""+drAddress.getLatitude()+"---"+drAddress.getLongitude(), Toast.LENGTH_SHORT).show();

                    prefManager.setDistance(calculateDistanceInKilometer(pkAddress.getLatitude(), pkAddress.getLongitude(), drAddress.getLatitude(), drAddress.getLongitude()));

                    final AlertDialog dialog;

                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View content = inflater.inflate(R.layout.mycustom_dialog, null);
                    builder.setView(content);

                    TextView pk = (TextView) content.findViewById(R.id.pk);
                    TextView dr = (TextView) content.findViewById(R.id.dr);
                    TextView appdistance = (TextView) content.findViewById(R.id.appdistance);


                    TextView cancel = content.findViewById(R.id.cancel);


                    navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

                    pk.setText(prefManager.getPickLocation());
                    dr.setText(prefManager.getDropLocation());
                    appdistance.setText(prefManager.getDistance() + " Km");

                    builder.setView(content);//view is your inflate xml layout

                    dialog = builder.create();

                    dialog.show();


                    TextView yes = content.findViewById(R.id.yes);
                    yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            navController.navigate(R.id.requestActivity);
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


//                    ProceedDialog cdd = new ProceedDialog(getActivity());
//                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    cdd.show();

                    //   ProceedDialog cdd = new ProceedDialog(getActivity());
                    //   cdd.show();


                    //  navController.navigate(R.id.startFragment);


//                    new GetPathFromLocation(source, destination, new DirectionPointListener() {
//                        @Override
//                        public void onPath(PolylineOptions polyLine) {
//                            mMap.addPolyline(polyLine);
//                        }
//                    }).execute();
//
//                Log.i(TAG, "onPostExecute: "+address.toString());
//                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//
//                if (null!=address.getSubAdminArea())
//                markerOptions.title(tvPick.getText().toString() + ", " + address.getSubAdminArea());
//                else
//                markerOptions.title(tvPick.getText().toString());
//
//                markerOptions.snippet("Pickup");
//                Marker location = mMap.addMarker(markerOptions);
//                location.setDraggable(true);
//                location.showInfoWindow();
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//

                } else {
                    Toast.makeText(getActivity(), "Drop Off location not found, Retry", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(getActivity(), "Pickup location not found, Retry", Toast.LENGTH_SHORT).show();

            }

            progress.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Address> doInBackground(String... strings) {
//            Log.i(TAG, "doInBackground: "+strings[0]);
            Address n = getLocationFromAddress(strings[0]);
            Address m = getLocationFromAddress(strings[1]);
            ArrayList<Address> routes = new ArrayList<>();
            routes.add(n);
            routes.add(m);
            return routes;
        }
    }


//
// @SuppressLint("StaticFieldLeak")
//    class getDropLocationAsync extends AsyncTask<String, String, Address> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progress.setVisibility(View.VISIBLE);
//            imageView.setVisibility(View.GONE);
//            Log.i(TAG, "onPreExecute: ");
//
//
//        }
//
//        @Override
//        protected void onPostExecute(Address address) {
//            super.onPostExecute(address);
//            Log.i(TAG, "onPostExecute: ");
//            if (address != null) {
//                Log.i(TAG, "onPostExecute: "+address.toString());
//                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(latLng);
//
//                if (null!=address.getSubAdminArea())
//                markerOptions.title(tvDrop.getText().toString() + ", " + address.getSubAdminArea());
//                else
//                markerOptions.title(tvDrop.getText().toString());
//
//                markerOptions.snippet("Drop");
//                Marker location = mMap.addMarker(markerOptions);
//                location.setDraggable(true);
//                location.showInfoWindow();
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
//
//            }else {
//                Toast.makeText(getActivity(), "Location not found, Retry", Toast.LENGTH_SHORT).show();
//
//            }
//            progress.setVisibility(View.GONE);
//            imageView.setVisibility(View.VISIBLE);
//        }
//
//        @Override
//        protected Address doInBackground(String... strings) {
//            Log.i(TAG, "doInBackground: "+strings[0]);
//            Address n = getLocationFromAddress(strings[0]);
//           // Log.i(TAG, "doInBackground: "+n.toString());
//            return n;
//        }
//    }


}