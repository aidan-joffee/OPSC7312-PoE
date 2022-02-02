package com.example.opsc7312task2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.PolyUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import Directions.DirectionsApiRequest;
import Directions.Model.DirectionResults;
import Places.Model.PlacesResult;
import Places.Model.SearchResult;
import Places.PlacesApiRequest;
import Places.SearchApiRequest;
import User.UserFavouriteLandmarks;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//-----------------------------------------------------------------------------------------------------------------------
//Google maps fragment
public class MapsFragment extends Fragment implements OnMapReadyCallback {
    //buttons and textviews
    private Button getDirectionBtn, saveLocationBtn, clearRouteBtn;
    private TextView durationInfo, distanceInfo, destinationInfo, currentDisplay;
    private ImageButton searchLocationBtn;
    private EditText searchLocationInfo;
    //firebase
    DatabaseReference mDatabase;
    FirebaseUser user;
    //location variables
    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private Marker selectedMarker, polylineMarker, searchMarker;
    private Polyline routePolyline;
    private UserFavouriteLandmarks favLandmark;
    //settings
    private SharedPreferences sharedPreferences;
    private String measureSystem, favLandmarkType, routeDistance, routeDuration;
    //statics
    private static final int PROXIMITY_RADIUS = 10000;
    private static final double KM_TO_MILES = 1.609;
    private static final String BASE_URL = "https://maps.googleapis.com/";

    //blank constructor
    public MapsFragment() {
    }

    //parameterised constructor
    public MapsFragment(UserFavouriteLandmarks favLandmarkDirection) {
        this.favLandmark = favLandmarkDirection;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //onCreate method
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity activity = (MainActivity) getActivity();
        //firebase
        mDatabase = FirebaseDatabase.getInstance("https://opsc7312-poe-8e798-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        user = activity.getUser();

        //map
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            client = LocationServices.getFusedLocationProviderClient(activity);
            setSettings(); //set the settings from the user

            //checking permissions
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //denied permissions, request permission
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , 44);
            } else {
                //accepted permission
                getLocationRequest(activity, mapFragment);
            }
            //button and onClick to start directions
            getDirectionBtn = (Button) view.findViewById(R.id.btGetDirections);
            saveLocationBtn = (Button) view.findViewById(R.id.btSaveFavouriteLocation);
            clearRouteBtn = (Button) view.findViewById(R.id.btClearRoute);
            searchLocationBtn = (ImageButton) view.findViewById(R.id.btSearchLocation);
            //textviews
            destinationInfo = (TextView) view.findViewById(R.id.tvSelectedDestination);
            durationInfo = (TextView) view.findViewById(R.id.tvDuration);
            distanceInfo = (TextView) view.findViewById(R.id.tvDistance);
            currentDisplay = (TextView) view.findViewById(R.id.tvCurrentDisplay);
            searchLocationInfo = (EditText) view.findViewById(R.id.etSearchLocationTxt);
            currentDisplay.setText(favLandmarkType);

            saveLocationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveLocation();
                }
            });

            clearRouteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearRoute();
                }
            });

            searchLocationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchLocation();
                }
            });
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //onMapReady callback for when the map is ready to be used
    @SuppressLint({"MissingPermission", "PotentialBehaviorOverride"})
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //getting latittude and longitude
        mMap = googleMap;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        //map zoom
        MoveCamera(googleMap, latLng, 13);

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        //Displaying nearby locations
        getNearbyPlaces(googleMap);

        //click event for a marker
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @SuppressLint("PotentialBehaviorOverride")
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (marker.getTag() == null) {
                    selectedMarker = marker;
                    enableButtons();
                    Toast toast = Toast.makeText(getActivity(), selectedMarker.getTitle() + " selected!", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 200);
                    toast.show();
                    //setting text
                    destinationInfo.setText(selectedMarker.getTitle());
                    return false;
                } else {
                    return true;
                }
            }
        });

        //click event for polyline, to show the info window
        googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {
                if(routePolyline != null && polylineMarker != null){
                    polylineMarker.showInfoWindow();
                }
            }
        });

        //directions
        setUpDirections();
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to get issue location request, to get the current location of the user
    @SuppressLint("MissingPermission")
    public void getLocationRequest(Activity activity, SupportMapFragment map) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if(locationResult == null){
                    Toast toast = Toast.makeText(getActivity(),"Current Location is null",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 200);
                    toast.show();
                } else {
                    super.onLocationResult(locationResult);
                    for (Location location : locationResult.getLocations()) {
                        currentLocation = location;
                        //execute the onMapReady callback
                        map.getMapAsync(MapsFragment.this);
                    }
                }
            }
        };

        //fire the callback with the request
        client.requestLocationUpdates(mLocationRequest, locationCallback, Looper.myLooper());
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Request permission to access user location
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //when permission granted
                //call method
                Activity activity = this.getActivity();
                SupportMapFragment mapFragment =
                        (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                getLocationRequest(activity, mapFragment);
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //method to move the camera
    public void MoveCamera(GoogleMap map, LatLng latLng, float zoom) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Getting the nearby places
    public void getNearbyPlaces(GoogleMap mMap) {
        mMap.clear();
        //values
        String location = currentLocation.getLatitude() + ", " + currentLocation.getLongitude();
        //retrofit
        Retrofit placesRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        //api request
        PlacesApiRequest placesApiRequest = placesRetrofit.create(PlacesApiRequest.class);
        placesApiRequest.getPlaces(favLandmarkType, location, PROXIMITY_RADIUS, getString(R.string.google_maps_key))
                .enqueue(new Callback<PlacesResult>() {
                    @Override
                    public void onResponse(Call<PlacesResult> call, Response<PlacesResult> response) {
                        try {
                            int size = response.body().getResults().size();
                            if (size > 0) {
                                //for each location, add a marker
                                for (int i = 0; i < size; i++) {
                                    //lattitude, longitude, name and vicinity of each location
                                    Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
                                    Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
                                    String placeName = response.body().getResults().get(i).getName();
                                    String vicinity = response.body().getResults().get(i).getVicinity();

                                    //add marker
                                    MarkerOptions marker = new MarkerOptions();
                                    LatLng position = new LatLng(lat, lng);
                                    marker.position(position);
                                    marker.title(placeName);
                                    marker.snippet(vicinity);
                                    mMap.addMarker(marker);
                                }
                            } else {
                                String currentdisplay = currentDisplay.getText().toString();
                                Toast toast = Toast.makeText(getActivity(), "No nearby " + currentdisplay + " could be found, try a different landmark type.", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 200);
                                toast.show();
                            }

                        } catch (Exception e) {
                            Toast toast = Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 200);
                            toast.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PlacesResult> call, Throwable t) {
                        Toast toast = Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 200);
                        toast.show();
                    }
                });

    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to setup the ability to request directional information
    public void setUpDirections() {
        //this will check if the fragment has been opened using a favourite landmark, then directions will be shown
        if (favLandmark != null) {
            double lat = favLandmark.getLatitude();
            double lng = favLandmark.getLongitude();
            destinationInfo.setText(favLandmark.getTitle());
            //adding marker and routing
            mMap.addMarker(new MarkerOptions()
                    .title(favLandmark.getTitle())
                    .position(new LatLng(lat, lng)));
            getDirections(mMap, lat, lng);

        }

        //onclick for the getDirectionsButton
        getDirectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedMarker != null) {
                    getDirections(mMap,
                            selectedMarker.getPosition().latitude,
                            selectedMarker.getPosition().longitude);
                }
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to get the directions
    public void getDirections(GoogleMap mMap, double destinationLat, double destinationLng) {
        //remove current polyline if it exists
        if (routePolyline != null) {
            routePolyline.remove();
        }
        //origin and destination
        String origin = currentLocation.getLatitude() + ", " + currentLocation.getLongitude();
        String destination = destinationLat + ", " + destinationLng;

        //the retrofit
        Retrofit directionsRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //the class
        DirectionsApiRequest directionsApiRequest = directionsRetrofit.create(DirectionsApiRequest.class);
        directionsApiRequest.getJson(origin, destination, measureSystem, getString(R.string.google_maps_key)).enqueue(new Callback<DirectionResults>() {
            @Override
            public void onResponse(Call<DirectionResults> call, Response<DirectionResults> response) {
                //looping through the routes
                try {
                    List<DirectionResults.Route> routes = response.body().getRoutes();
                    getRouteDetails(mMap, routes);
                    addMapPolyline(mMap, routes);
                } catch (Exception e) {
                    Toast toast = Toast.makeText(getActivity(), e.getMessage(),
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 200);
                    toast.show();
                }
                //--
            }

            @Override
            public void onFailure(Call<DirectionResults> call, Throwable t) {
                Toast toast = Toast.makeText(getActivity(), t.getMessage(),
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 200);
                toast.show();
            }
        });
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to get add the polyline from the directions API
    public void addMapPolyline(GoogleMap mMap, List<DirectionResults.Route> routes) {
        //decoding polyline to display on the map
        List<LatLng> decoded = null;
        int routeLength = routes.size();
        for (int i = 0; i < routeLength; i++) {
            DirectionResults.OverviewPolyLine polyLine = routes.get(i).getOverviewPolyLine();
            decoded = PolyUtil.decode(polyLine.getPoints());
        }
        routePolyline = mMap.addPolyline(new PolylineOptions()
                .color(Color.RED)
                .clickable(true)
                .addAll(decoded));
        addPolylineMarker();
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to add an invisible marker to the polyline that will display the distance and time
    public void addPolylineMarker() {
        //polylineMarker.remove();
        int markerPoints = routePolyline.getPoints().size();
        int middleMarker = markerPoints / 2;
        LatLng middleOfLine = routePolyline.getPoints().get(middleMarker);

        //bitmap
        Bitmap markerIcon = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        BitmapDescriptor invisibleMarker = BitmapDescriptorFactory.fromBitmap(markerIcon);
        MoveCamera(mMap, middleOfLine, 12);

        //adding marker
        polylineMarker = mMap.addMarker(new MarkerOptions()
                .position(middleOfLine)
                .title(routeDistance)
                .snippet(routeDuration)
                .alpha(0f)
                .icon(invisibleMarker)
                .anchor(0f, 0f));
        polylineMarker.setTag(true);
        polylineMarker.showInfoWindow();
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to get add the polyline from the directions API
    public void getRouteDetails(GoogleMap mMap, List<DirectionResults.Route> routes) {
        int totalDistanceM = 0;
        int totalDurationS = 0;
        int routeLength = routes.size();
        for (int i = 0; i < routeLength; i++) {
            List<DirectionResults.Legs> legs = routes.get(i).getLegs();
            //getting each leg of the route
            int legLength = legs.size();
            for (int n = 0; n < legLength; n++) {
                //totaling duration and distance of each leg (segment)
                totalDistanceM = totalDistanceM + legs.get(n).getDistance().getValue(); //this value will always be in metres
                totalDurationS = totalDurationS + legs.get(n).getDuration().getValue(); //this value will always be in seconds
            }
        }
        //converting
        String distance = convertDistance(totalDistanceM);
        String duration = convertDuration(totalDurationS);

        //displaying
        routeDistance = distance;
        routeDuration = duration;
        distanceInfo.setText(distance);
        durationInfo.setText(duration);
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to convert the distance based on the measure system selected
    public String convertDistance(double distance) {
        String stringDistance = ""; //string to return
        String unit = "km";
        double convertDistance = (double) distance / 1000; //making KM

        if (measureSystem.equals("imperial")) {
            convertDistance = convertDistance / KM_TO_MILES;
            unit = "mi.";
        }

        //rounding to 2 decimal places
        double totalDistance = Math.round(convertDistance * 100) / 100d;
        stringDistance = totalDistance + "" + unit;
        return stringDistance;
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to convert the seconds into minutes
    public String convertDuration(long duration) {
        String stringDuration = "";
        long time = duration;
        String unit = "sec";
        if (duration > 60) {
            time = TimeUnit.SECONDS.toMinutes(duration);
            unit = "min";
        }
        stringDuration = time + "" + unit;
        return stringDuration;
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to search for a location near the user based on user input
    public void searchLocation() {
        if(currentLocation!=null || searchLocationInfo.getText().length() == 0) {
            if(searchMarker!=null) {searchMarker.remove();} //remove marker to replace old one
            String input = searchLocationInfo.getText().toString();
            String inputtype = "textquery";
            String fields = "formatted_address,name,geometry";
            String locationbias = "circle:2000@"+currentLocation.getLatitude() + "," + currentLocation.getLongitude();

            //retrofit
            Retrofit searchBuilder = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            SearchApiRequest searchApiRequest = searchBuilder.create(SearchApiRequest.class);
            searchApiRequest.getPlace(input, inputtype, locationbias, fields, getString(R.string.google_maps_key))
                    .enqueue(new Callback<SearchResult>() {
                        @Override
                        public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                            int size = response.body().getCandidates().size();
                            if(size > 0) {
                                for (int i = 0; i < size; i++) {
                                    Double lat = response.body().getCandidates().get(i).getGeometry().getLocation().getLat();
                                    Double lng = response.body().getCandidates().get(i).getGeometry().getLocation().getLng();
                                    String name = response.body().getCandidates().get(i).getName();
                                    String vicinity = response.body().getCandidates().get(i).getFormatted_address();

                                    //marker
                                    LatLng latLng = new LatLng(lat, lng); //marker posistion
                                    searchMarker = mMap.addMarker(new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)) //blue
                                            .position(latLng)
                                            .title(name)
                                            .snippet(vicinity));
                                    //moving and setting as the selected marker, enabling buttons to navigate
                                    selectedMarker = searchMarker;
                                    enableButtons();
                                    searchMarker.showInfoWindow();
                                    destinationInfo.setText(selectedMarker.getTitle());
                                    MoveCamera(mMap, latLng, 13);
                                }
                            } else{
                                Toast toast = Toast.makeText(getActivity(), "That location could not be found.",
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 200);
                                toast.show();
                            }
                        }

                        @Override
                        public void onFailure(Call<SearchResult> call, Throwable t) {
                            Toast toast = Toast.makeText(getActivity(), t.getMessage(),
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 200);
                            toast.show();
                        }
                    });
        } else{
            Toast toast = Toast.makeText(getActivity(), "Please enter a location.",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 200);
            toast.show();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to save the currently selected location as a favourite
    public void saveLocation() {
        if (selectedMarker != null) {
            //setting new landmark location
            UserFavouriteLandmarks favLandmark = new UserFavouriteLandmarks(
                    selectedMarker.getTitle(),
                    selectedMarker.getPosition().latitude,
                    selectedMarker.getPosition().longitude
            );

            //uploading to firebase
            mDatabase.child("UserFavouriteLandmarks").child(user.getUid()).child(selectedMarker.getTitle()).setValue(favLandmark)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast toast = Toast.makeText(getActivity(), selectedMarker.getTitle() + " has been added to favourites",
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 200);
                                toast.show();
                            } else {
                                Toast toast = Toast.makeText(getActivity(), selectedMarker.getTitle() + "FIREBASE error, location could not be added.",
                                        Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 200);
                                toast.show();
                            }
                        }
                    });
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to enable the save location button when a marker is selected.
    public void enableButtons() {
        if (selectedMarker != null) {
            saveLocationBtn.setEnabled(true);
            getDirectionBtn.setEnabled(true);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to get the settings from the shared preferences
    public void setSettings() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        measureSystem = sharedPreferences.getString("measureSystem", "Metric").toLowerCase();
        favLandmarkType = sharedPreferences.getString("favouriteLandmark", "Statues").toLowerCase();
    }

    //-----------------------------------------------------------------------------------------------------------------------
    //Method to clear the polyline and route information and selected destination
    public void clearRoute() {
        destinationInfo.setText("No destination selected");
        saveLocationBtn.setEnabled(false);
        getDirectionBtn.setEnabled(false);
        selectedMarker = null;
        distanceInfo.setText("0");
        durationInfo.setText("0");

        if (routePolyline != null) {
            routePolyline.remove();
        }
        if (polylineMarker != null) {
            polylineMarker.remove();
        }
    }
    //--
}
//-------------------------------------------------End Of File----------------------------------------------------------