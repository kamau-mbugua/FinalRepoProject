package technerd.com.googlemaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class DriverMapActivity extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    SupportMapFragment mapFragment;
    Marker mCurrentLocationMarker;

    private Switch mWorkingSwitch;
    private Button mHistory, mSettings, mLogout, mKabul, mRed,mRideStatus;
    private LinearLayout mCustomerInfo,mButtons;
    private ImageView mCustomerProfileImage;
    private TextView mCustomerDestination, mCustomerName, mCustomerPhone;

    private  int status = 0;
    private String customerId ="",destination, buton;
    private LatLng destinationLatLng, pickupLatLng;
    private  float rideDistance;

    boolean clicked;
    private  String durum;

    private  boolean isLoggingOut = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        /*// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        buton ="";

        polylines = new ArrayList<>();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mWorkingSwitch = findViewById(R.id.workingSwitch);
        mHistory = findViewById(R.id.history);
        mSettings = findViewById(R.id.settings);
        mLogout = findViewById(R.id.logout);
        mKabul = findViewById(R.id.kabul);
        mRed = findViewById(R.id.red);
        mRideStatus = findViewById(R.id.rideStatus);
        mCustomerInfo = findViewById(R.id.customerInfo);
        mCustomerProfileImage = findViewById(R.id.customerProfileImage);
        mCustomerDestination = findViewById(R.id.customerDestination);
        mCustomerName = findViewById(R.id.customerName);
        mCustomerPhone = findViewById(R.id.customerPhone);
        mButtons= findViewById(R.id.buttons);

        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    connectDriver();
                }else {
                    disconnectDriver();
                }
            }
        });
        mRideStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (status){
                    case 1:
                        status=2;
                        erasePolylines();
                        if (destinationLatLng.latitude!=0.0 && destinationLatLng.longitude !=0.0){
                            getRouteToMarker(destinationLatLng);
                        }
                        mRideStatus.setText("ride completed");
                        break;
                    case 2:
                        if (clicked == true){
                            recordRide();
                            endRide();
                        }else {
                            rideRed();
                        }
                        break;
                }
            }
        });
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLoggingOut= true;
                disconnectDriver();
                FirebaseAuth.getInstance().signOut();
                Intent intent= new Intent(DriverMapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1= new Intent(DriverMapActivity.this, DriverSettingsActivity.class);
                startActivity(intent1);
                return;
            }
        });
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(), HistoryActivity.class);
                intent.putExtra("customerOrDriver","Drivers");
                startActivity(intent);
                return;
            }
        });
        getAssignedCustomer();
        clicked=false;
        mKabul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked = true;
                buton = "acceptance";
                mKabul.setVisibility(View.GONE);
                mRed.setVisibility(View.GONE);
                mRideStatus.setVisibility(View.GONE);
            }
        });
        mRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicked =false;
                rideRed();
                buton = "rejection";
                DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId);
                HashMap map = new HashMap();
                map.put("rideStatus",buton);
                assignedCustomerRef.updateChildren(map);

                String driverUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference refAvailable= FirebaseDatabase.getInstance().getReference("driversAvailable");
                DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                GeoFire geoFireAvailable = new GeoFire(refAvailable);
                GeoFire geoFireWorking = new GeoFire(refWorking);
                geoFireWorking.removeLocation(driverUserId);
                geoFireAvailable.setLocation(driverUserId, new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

            }
        });
    }
    private void  getAssignedCustomer(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest").child("customerRideId");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    status = 1;
                    customerId = dataSnapshot.getValue().toString();
                    getAssignedCustomerPickUpLocation();
                    getAssignedCustomerDestination();
                    getAssignedCustomerInfo();
                }
                else {if (clicked == false){
                    rideRed();
                } else {
                    endRide();
                }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  Marker pickUpMarker;
    private DatabaseReference assignedCustomerPickUpLocationRef;
    private ValueEventListener assignedCustomerPickUpLocationRefListener;
    private  void  getAssignedCustomerPickUpLocation(){
        //We get the Assigned Pick Up Location of the customer
        assignedCustomerPickUpLocationRef= FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId).child("l");
        assignedCustomerPickUpLocationRefListener= assignedCustomerPickUpLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()&& !customerId.equals("")){
                    List<Object>map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    if (map.get(0)!= null){
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if (map.get(1)!=null){
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }
                    pickupLatLng = new LatLng(locationLat,locationLng);
                    pickUpMarker = mMap.addMarker(new MarkerOptions()
                    .position(pickupLatLng).title("Pick Up Location")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)));
                    getRouteToMarker(pickupLatLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
     public  void  getRouteToMarker(LatLng pickupLatLng){
        if (pickupLatLng != null && mLastLocation!= null){
            Routing routing = new Routing.Builder()
                    .key("AIzaSyDTWa76zTjoZHP7MG6eeZh9--YN73dS1ZY")
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),pickupLatLng)
                    .build();
            routing.execute();
        }

     }
     private  void getAssignedCustomerDestination(){
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("customerRequest");
        assignedCustomerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Map<String ,Object>map = (Map<String, Object>)dataSnapshot.getValue();
                    if (map.get("destination")!= null){
                        destination = map.get("destination").toString();
                        mCustomerDestination.setText("Destination:"+destination);
                    }
                    else {
                        mCustomerDestination.setText("Destination: --");
                    }
                    double destinationLat =0;
                    double destinationLng =0;
                    if (map.get("destinationLat")!= null){
                        destinationLat = Double.valueOf(map.get("destinationLat").toString());

                    }
                    if (map.get("destinationLng")!= null){
                        destinationLng = Double.valueOf(map.get("destinationLng").toString());
                        destinationLatLng =new LatLng(destinationLat,destinationLng);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
     }
     private  void getAssignedCustomerInfo(){
        mCustomerInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customer").child(customerId);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>)dataSnapshot.getValue();
                    if (map.get("name")!=null){
                        mCustomerName.setText("Name :"+map.get("name").toString());
                    }
                    if (map.get("phone")!=null){
                        mCustomerName.setText("Phone :"+map.get("phone").toString());
                    }
                    if (map.get("profileImageUrl")!=null){
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString())
                        .into(mCustomerProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
     }
     private void rideRed(){
        erasePolylines();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child("customerRequest");
        driverLocation.setValue(true);
        if (pickUpMarker!= null){pickUpMarker.remove();
        }
        if (assignedCustomerPickUpLocationRefListener!=null){
            assignedCustomerPickUpLocationRef.removeEventListener(assignedCustomerPickUpLocationRefListener);

        }
        mCustomerInfo.setVisibility(View.GONE);
        mCustomerName.setText("");
        mCustomerPhone.setText("");
        mCustomerDestination.setText("Destination:");
        mCustomerProfileImage.setImageResource(R.mipmap.ic_launcher);
        mRideStatus.setVisibility(View.GONE);
        mKabul.setVisibility(View.VISIBLE);
        mRed.setVisibility(View.VISIBLE);
     }
     private  void endRide(){
        erasePolylines();
        mRideStatus.setText("Picked customer");
        String userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child("customerRequest");
        driverRef.removeValue();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(customerId);
        customerId ="";
        rideDistance=0;
        if (pickUpMarker != null){
            pickUpMarker.remove();
        }
        if (assignedCustomerPickUpLocationRefListener != null){
            assignedCustomerPickUpLocationRef.removeEventListener(assignedCustomerPickUpLocationRefListener);
        }
         mCustomerInfo.setVisibility(View.GONE);
         mCustomerName.setText("");
         mCustomerPhone.setText("");
         mCustomerDestination.setText("Destination:");
         mCustomerProfileImage.setImageResource(R.mipmap.ic_launcher);
         mRideStatus.setVisibility(View.GONE);
         mKabul.setVisibility(View.VISIBLE);
         mRed.setVisibility(View.VISIBLE);

     }
     private  void  recordRide(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference drRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId).child("history");
         DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("history");
            DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("history");
            DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("historySingleForCustomer");
            String requestId = historyRef.push().getKey();
            customerRef.child(requestId).setValue(true);
            drRef.child(requestId).setValue(true);
            HashMap map = new HashMap();
            map.put("driver", userId);
            map.put("customer", customerId);
            map.put("rating",0);
            map.put("timestamp",getCurrentTimestamp());
            map.put("destination", destination);
            if (pickupLatLng!= null){
                map.put("location/from/lat",pickupLatLng.latitude);
                map.put("location/from/lng",pickupLatLng.longitude);
            }
         map.put("location/from/lat",destinationLatLng.latitude);
         map.put("location/from/lng",destinationLatLng.longitude);
         map.put("distance", rideDistance);
         historyRef.child(requestId).updateChildren(map);
         HashMap mapR = new HashMap();
         mapR.put("rideId",requestId);
         requestRef.child(customerId).updateChildren(mapR);
     }
     private  long getCurrentTimestamp(){
        Long timestamp = System.currentTimeMillis()/1000;
        return  timestamp;
     }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }
            else {
                checkLocationPermission();
            }
        }


       /* // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Am here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }
    boolean cameraSet = false;
    LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()){
                if (getApplicationContext()!=null){
                    if (!customerId.equals("")&&mLastLocation!=null){
                        rideDistance += mLastLocation.distanceTo(location)/1000;
                    }
                    mLastLocation = location;
                    if (mCurrentLocationMarker != null){
                        mCurrentLocationMarker.remove();
                    }
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                    float zoomLevel = 15.0f;
                    if (!cameraSet){
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
                        cameraSet =true;
                    }
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("your location");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    mCurrentLocationMarker = mMap.addMarker(markerOptions);

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference refAvailable=FirebaseDatabase.getInstance().getReference("driversAvailable");
                    DatabaseReference refWorking=FirebaseDatabase.getInstance().getReference("driversWorking");
                    GeoFire geoFireAvailable = new GeoFire(refAvailable);
                    GeoFire geoFireWorking = new GeoFire(refWorking);
                    switch (customerId){
                        case "":
                            geoFireWorking.removeLocation(userId);
                            geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(),location.getLongitude()));
                            break;
                        default:
                            if (clicked == true){
                                geoFireAvailable.removeLocation(userId);
                                geoFireWorking.setLocation(userId,new GeoLocation(location.getLatitude(),location.getLongitude()));

                            }
                            if (clicked!= true){
                                geoFireWorking.removeLocation(userId);
                                geoFireAvailable.setLocation(userId,new GeoLocation(location.getLatitude(),location.getLongitude()));


                            }
                            break;
                    }

                }
            }
        }
    };
    private  void  checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new  AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(
                                        DriverMapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1
                                );
                            }
                        })
                .create()
                .show();
            }
            else {
                ActivityCompat.requestPermissions(DriverMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length<0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"You should Allow",Toast.LENGTH_SHORT).show();

                }
        }
    }
    private  void  connectDriver(){
        checkLocationPermission();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,Looper.myLooper());
        mMap.setMyLocationEnabled(true);

    }
    private void disconnectDriver(){
        if (mFusedLocationClient != null){
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
        GeoFire geoFire = new GeoFire(refAvailable);
        geoFire.removeLocation(userId);
    }
    private  List<Polyline> polylines;
    private  static  final  int[] COLORS = new int[]{R.color.primary_dark_material_light};

    /*LocationCallback mLocationCallback = new LocationCallback(){

    }*/

    @Override
    public void onRoutingFailure(RouteException e) {
        if (e!=null){
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Something Went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {
        if (polylines.size()>0){
            for (Polyline poly : polylines){
                poly.remove();
            }
        }
        polylines = new ArrayList<>();
        for (int i1 = 0; i1< arrayList.size();i1++){
            int colorIndex = i1 % COLORS.length;
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(getResources().getColor(COLORS[colorIndex]));
            polylineOptions.width(10 + i1 *3);
            polylineOptions.addAll(arrayList.get(i1).getPoints());
            Polyline polyline = mMap.addPolyline(polylineOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route"+ (i1+1)+ ":distance -" + arrayList.get(i).getDistanceValue()+ ":duration -" + arrayList.get(i1).getDurationValue(), Toast.LENGTH_SHORT ).show();


        }

    }

    @Override
    public void onRoutingCancelled() {

    }
    private  void  erasePolylines(){
        for ( Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }
}
