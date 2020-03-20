package technerd.com.googlemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mlocationRequest;
    private FusedLocationProviderClient mfusedLocationClient; //updates
    SupportMapFragment mapFragment;

    private Button mLogout , mRequest , mSettings , mHistory, mKapat;

    private  LatLng pickupLocation;
    private  Boolean requestBol = false;

    private  Marker pickupMarker;
    private  String destination;
    private  LatLng  destinationLatLng, clickMarkerLatLng;

    private LinearLayout mDriverInfo;

    private ImageView mDriverProfileImage;
    private TextView mDriverName, mDriverPhone, mDriverCar;
    private RatingBar mRatingBar;
   // private static  final  LatLng LURAMBI_KAKAMEGA= new LatLng(0.284,34.771);
   // private static  final LatLng MASINDE_MULIRO_UNIVERSITY = new LatLng(0.284,34.752);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        destinationLatLng = new LatLng(0.0,0.0);

        mDriverCar= findViewById(R.id.driverCar);
        mDriverInfo = findViewById(R.id.driverInfo);
        mDriverName = findViewById(R.id.driverName);
        mDriverPhone = findViewById(R.id.driverPhone);
        mDriverProfileImage = findViewById(R.id.driverProfileImage);
        mRatingBar = findViewById(R.id.ratingBar);
        mLogout = findViewById(R.id.logout);
        mRequest = findViewById(R.id.request);
        mSettings = findViewById(R.id.settings);
        mHistory = findViewById(R.id.history);
        mKapat = findViewById(R.id.history);
        mKapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDriverInfo.setVisibility(View.GONE);
            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(MapsActivity.this, "Sign Out Successfully!", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestBol){
                    endRide();
                }
                else {
                    requestBol = true;
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new GeoFire(ref);
                    geoFire.setLocation(userId , new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    pickupLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    pickupMarker = mMap.addMarker(new MarkerOptions().position(pickupLocation).title("Pick Up"));
                    mRequest.setText("We are Looking for available taxi driver");

                    sendRequestToDriver();
                }

            }
        });
            mSettings.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), CustomerSettingsActivity.class);
                            startActivity(intent);
                            return;
                        }
                    });
            mHistory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                            intent.putExtra("customerOrDriver","Customers");
                            startActivity(intent);
                            return;
                        }
                    });

            if (!Places.isInitialized()){
                        Places.initialize(getApplicationContext(),"AIzaSyDTWa76zTjoZHP7MG6eeZh9--YN73dS1ZY");
                    }
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.aoutocomplete_fragment);
         autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));

         autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
             @Override
             public void onPlaceSelected(@NonNull Place place) {
                 destination = place.getName();
                 destinationLatLng = place.getLatLng();
             }

             @Override
             public void onError(@NonNull Status status) {
                 Toast.makeText(getApplicationContext(),"error destintion",Toast.LENGTH_LONG).show();

             }
         });
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


    private int radius = 1;
    private  Boolean driverFound =false;
    private  String driverFoundID;
    private  String driverFoundID1;
    GeoQuery geoQuery;
    public void sendRequestToDriver(){
        //driverFoundID = key;
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID1).child("customerRequest");
        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap map = new HashMap(); //Hash table based implementation of the Map interface
        map.put("customerRideId", customerId);
        map.put("destination", destination);
        if(destinationLatLng!= null) {
            map.put("destinationLat", destinationLatLng.latitude);
            map.put("destinationLng", destinationLatLng.longitude);
        }
        driverRef.updateChildren(map);
        getDriverLocation();
        getHasRideEnded();
        mRequest.setText("Request sent");//Istek gönderildi

    }

    public void getClosestDriver(){
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("driversAvailable");
        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickupLocation.latitude, pickupLocation.longitude),radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!driverFound && requestBol){
                    driverFound = true;
                    driverFoundID = key;
                    DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap map = new HashMap();
                    map.put("customerRideId", customerId);
                    map.put("destination", destination);
                        if (destinationLatLng!=null){
                            map.put("destinationLat", destinationLatLng.latitude);
                            map.put("destinationLng", destinationLatLng.longitude);
                        }
                        driverRef.updateChildren(map);

                        getDriverLocation();
                        //getDriverInfo();
                    getHasRideEnded();
                    mRequest.setText("Location of the taxi driver");
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()&& dataSnapshot.getChildrenCount()>0){
                                Map<String, Object> driverMap = (Map<String, Object>)dataSnapshot.getValue();
                                if (driverFound){
                                    return;
                                }
                                driverFound=true;
                                driverFoundID= dataSnapshot.getKey();

                                DatabaseReference driverRef=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID).child("customerRequest");
                                String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                HashMap map= new HashMap();
                                map.put("customerRideId", customerId);
                                map.put("destination", destination);
                                map.put("destinationLat", destinationLatLng.latitude);
                                map.put("destinationLng", destinationLatLng.longitude);
                                driverRef.updateChildren(map);


                                getDriverLocation();
                                //getDriveInfo();
                                getHasRideEnded();

                                mRequest.setText("Location of the taxi driver");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!driverFound){
                    radius++;
                    getClosestDriver();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }
    private Marker mDriverMarker;
    private  DatabaseReference driverLocationRef;
    private  ValueEventListener driverLocationListener;
    private  void getDriverLocation(){
        driverLocationRef = FirebaseDatabase.getInstance().getReference().child("driversWorking").child(driverFoundID1).child("l");
        driverLocationListener = driverLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && requestBol){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat =0;
                    double locationLng =0;
                    mRequest.setText("Boda Found");
                    if (map.get(0)!=null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1)!=null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng driverLatLng = new LatLng(locationLat, locationLng);
                    if (mDriverMarker!= null){
                        mDriverMarker.remove();
                    }
                    Location loc1=  new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    Location loc2=  new Location("");
                    loc1.setLatitude(pickupLocation.latitude);
                    loc1.setLongitude(pickupLocation.longitude);

                    float distnce = loc1.distanceTo(loc2);
                    if (distnce<100){
                        mRequest.setText("The Boda has arived");
                    }
                    else {
                        mRequest.setText("Boda Rider:"+ String.valueOf(distnce)+"from");
                    }
                    mDriverMarker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Your Ride").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_taxi)));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void  getDriverInfo(){
        mDriverInfo.setVisibility(View.VISIBLE);
        final DatabaseReference mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(driverFoundID1);
        mDriverDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>)dataSnapshot.getValue();
                    if (dataSnapshot.child("name").getValue()!=null){
                        mDriverName.setText("Name: "+ dataSnapshot.child("name").getValue().toString());
                    }
                    if (dataSnapshot.child("phone").getValue()!=null){
                        mDriverPhone.setText("Phone: "+ dataSnapshot.child("phone").getValue().toString());
                    }
                    if (dataSnapshot.child("car").getValue()!=null){
                        mDriverCar.setText("Car: "+ dataSnapshot.child("car").getValue().toString());
                    }
                    if (dataSnapshot.child("profileImageUrl").getValue()!=null){
                       Glide.with(getApplication()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(mDriverProfileImage);
                    }
                    int ratingSum =0 ;
                    float ratingsTotal =0;
                    float ratingAvg =0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if (ratingsTotal !=0){
                        ratingAvg= ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingAvg);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private DatabaseReference driveHasEndedRef;
    private DatabaseReference customerRequestRef;
    private ValueEventListener driverHasEndedRefListener;
    private  void getHasRideEnded(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        customerRequestRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(userId);
        driveHasEndedRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Driver").child(driverFoundID1).child("customerRequest").child("customerRiderId");
        driverHasEndedRefListener = customerRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){Map<String,Object> map = (Map<String, Object>)dataSnapshot.getValue();
               if (dataSnapshot.child("rideStatus").getValue()!=null){
                   String status = dataSnapshot.child("rideStatus").getValue().toString();
                   if(status.equals("rejection")){
                       Toast.makeText(getApplicationContext(),"Denied Driver", Toast.LENGTH_SHORT).show();
                       mDriverInfo.setVisibility(View.GONE);
                       mRequest.setText("Search");
                       rideRed();
                   }
               }

               }
               else {
                   driveHasEndedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                           if (dataSnapshot.exists()){

                           }
                           else {
                               endRide();
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError databaseError) {

                       }
                   });
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public  void rideRed(){
        requestBol = false;
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("cudtomerRequest").child(userId);
        HashMap map = new HashMap();
        map.put("ridestatus",null);
        assignedCustomerRef.updateChildren(map);

        if (driverFoundID1 !=null){
            DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverFoundID1).child("customerRequest"); //removing customer id from drivers
            driverLocation.setValue(true); //it will rewrite child
            driverFoundID1=null;
        }
        if (mDriverMarker!= null){
            mDriverMarker.remove();
        }
        driverFound = false;
        mDriverInfo.setVisibility(View.GONE);
        mDriverName.setText("");
        mDriverPhone.setText("");
        mDriverCar.setText("");
        mDriverProfileImage.setImageResource(R.mipmap.ic_launcher);


    }
    String rideId = "";
    private  void endRide(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("historySingleForCustomer").child(userId);
        customerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String,Object>map = (Map<String, Object>)dataSnapshot.getValue();
                    if (dataSnapshot.child("rideId").getValue() !=null){
                        rideId = dataSnapshot.child("rideId").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        requestBol = false;
        if (geoQuery != null){
            geoQuery.removeAllListeners();
        }
        driverLocationRef.removeEventListener(driverLocationListener);
        if (driveHasEndedRef!= null){
            driveHasEndedRef.removeEventListener(driverHasEndedRefListener);
        }
        if (driverFoundID1 != null){
            DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("Users").child(driverFoundID1).child("customerRequest");
            driverLocation.setValue(true);
            driverFoundID1=null;
        }
        driverFound=false;
        radius =1;

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        if (pickupMarker!=null){
            pickupMarker.remove();
        }
        if (mDriverMarker != null){
            mDriverMarker.remove();
        }
        mRequest.setText("Search...");
        mDriverInfo.setVisibility(View.GONE);
        mDriverName.setText("");
        mDriverPhone.setText("");
        mDriverCar.setText("");
        mDriverProfileImage.setImageResource(R.mipmap.ic_launcher);
    }
    List<Marker>markerList1 = new ArrayList<Marker>();
    boolean doNotMoveCameraToCenterMarker = false;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                driverFoundID1 = marker.getTag().toString();
                getDriverInfo();
                return doNotMoveCameraToCenterMarker;
            }
        });
        mlocationRequest = new LocationRequest();
        mlocationRequest.setInterval(1000);
        mlocationRequest.setFastestInterval(1000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else {
                checkLocationPermission();
            }
        }
        mfusedLocationClient.requestLocationUpdates(mlocationRequest, mLocationCallback, Looper.myLooper());
        googleMap.setMyLocationEnabled(true);

    }
    boolean cameraSet = false;
    LocationCallback mLocationCallback = new LocationCallback(){

        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()){
                if (getApplicationContext()!= null){
                    mLastLocation=location;
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Your Location");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMap.addMarker(markerOptions);
                    float zoomLevel = 15.0f;
                    if (!cameraSet){
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                        cameraSet = true;
                    }
                    if (!getDriversAroundStarted)
                        gerDriversAround();
                }
            }
        }
    };
    private  void checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new  android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message ")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION},1);
                            }
                        })
                        .create()
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length<0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        mfusedLocationClient.requestLocationUpdates(mlocationRequest,mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "You Should Allow", Toast.LENGTH_SHORT).show();
                    return;
                }
        }



    }
    boolean getDriversAroundStarted = false;
    List<Marker> markerList = new ArrayList<Marker>();
    private void gerDriversAround() {
        getDriversAroundStarted = true;
        final  DatabaseReference driversLocation= FirebaseDatabase.getInstance().getReference().child("driversAvailable");
        GeoFire geoFire = new GeoFire(driversLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()), 1000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                for (Marker markerIt : markerList1){
                    if (markerIt.getTag().equals(key)){
                        return;
                    }
                }
                LatLng driverLatLng = new LatLng(location.latitude, location.longitude);
                Marker mDriverMarker= mMap.addMarker(new MarkerOptions()
                .position(driverLatLng).title(key).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mDriverMarker.setTag(key);
                markerList1.add(mDriverMarker);
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker markerIt : markerList1){
                    if (markerIt.getTag().equals(key)) {
                        markerList1.remove(markerIt);
                        markerIt.remove();
                        return;
                    }
                }

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for (Marker markerIt: markerList1){
                    if (markerIt.getTag().equals(key)){
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

}


//onclick





/*


    */
/*
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned  to the app.
     *//*

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //mMap.addMarker(new MarkerOptions()

        */
/*.icon(BitmapDescriptorFactory.fromResource(R.mipmap.hotel))
        .anchor(0.0f,1.0f)
        .title("Kakamega Guests")
        .position(MASINDE_MULIRO_UNIVERSITY));

        mMap.addMarker(new MarkerOptions()

                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.shopping))
                .anchor(0.0f,1.0f)
                .title("Shopping Center")
                .position(LURAMBI_KAKAMEGA));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(MASINDE_MULIRO_UNIVERSITY)
                .zoom(15)
                .bearing(0)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*//*




        // Add a marker in Sydney and move the camera
        LatLng MASINDE_MULIRO_UNIVERSITY */
/*sydney*//*
 = new LatLng( 0.284,34.752 */
/*-34, 151*//*
 );
        mMap.addMarker(new MarkerOptions().position(MASINDE_MULIRO_UNIVERSITY).title("Am here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(MASINDE_MULIRO_UNIVERSITY));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.action_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.action_satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.action_Terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.action_none:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;

        }


        return true;
    }
}
*/
