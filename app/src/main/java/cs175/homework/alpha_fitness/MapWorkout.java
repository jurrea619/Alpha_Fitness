package cs175.homework.alpha_fitness;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.os.Handler;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.telecom.RemoteConnection;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/*
Fitness tracker application that tracks user movement using the phone's location
and tracks workout session information such as pace, distance, calories burned, distance traveled, and time
 */
public class MapWorkout extends FragmentActivity implements OnMapReadyCallback,  LocationListener {

    // initialize variables to be used
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 10;
    private String locationProvider;
    private Location location;
    private LocationManager locationManager;
    private int timer = 0;
    private Button startButton;
    private TextView timeDisplay;
    private Handler mHandler;
    //private WatchTime watchTime;
    private long timeInMillis = 0L;

    //remote service variables
    private RemoteConnection remoteConnection = null;
    IMyInterface remoteService;

    @Override
    public void onLocationChanged(Location location) {

    }

    class RemoteConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IMyInterface.Stub.asInterface((IBinder) service);
            Toast.makeText(MapWorkout.this, "Remote Service Connected", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteService = null;
            Toast.makeText(MapWorkout.this, "Remote Service Disconnected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_workout);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //reference UI components
        timeDisplay = (TextView) findViewById(R.id.timeDisplay);
        startButton = (Button) findViewById(R.id.startButton);
        //watchTime = new WatchTime();
        mHandler = new Handler();

        //initialize remote service
        remoteConnection = new RemoteConnection();
        Intent intent = new Intent();
        intent.setClassName("cs175.homework.alpha_fitness", cs175.homework.alpha_fitness.MyService.class.getName());
        if(!bindService(intent, remoteConnection, BIND_AUTO_CREATE)){
            Toast.makeText(MapWorkout.this, "Fail to bind to remote service", Toast.LENGTH_LONG).show();
        }
    }

    /*
    Open profile page
     */
    public void openProfile(View view) {
        Intent intent = new Intent(this, Profile.class);
        startActivity(intent);
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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        locationProvider = locationManager.getBestProvider(criteria, true);

        //check for location permissions. If not given, request
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);

            return;
        }

        //get last location
        location = locationManager.getLastKnownLocation(locationProvider);
        //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //initialize variables used for marker information
        String label = "Address:  ";
        List<Address> addresses;
        LatLng here;

        //check that location is not null, default to home address
        if (location != null) {
            here = new LatLng(location.getLatitude(), location.getLongitude());

            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null) {
                    Address address = addresses.get(0);
                    StringBuilder stringBuilder = new StringBuilder("");
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        stringBuilder.append(address.getAddressLine(i)).append("/");
                    }
                    label = label + stringBuilder.toString();
                }
            } catch (IOException e) {
            }
        } else {
            //set location as my home address back in San Diego
            here = new LatLng(32.649417, -117.074867);
        }

        //set zoom level for map, and place marker at current location
        float zoomLevel = 16;
        mMap.addMarker(new MarkerOptions().position(here).title(label));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(here));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, zoomLevel));

    }

    /*
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    */

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission Needed!", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSION_ACCESS_FINE_LOCATION);
                }
            }
        }
    }

    /*
    Start timer, replace "start" with "stop"
     */
    public void startTimer(View view) {

        //if timer was off, start clock
        if (timer == 0) {
            startButton.setText(getString(R.string.stop));
            timer = 1; //timer on

            try {
                remoteService.startTimer();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(updateTimerRunnable, 20);
        } else {   //timer was on and it getting stopped. Extract information and place into database
            //switch button label
            startButton.setText(getString(R.string.start));

            try {
                remoteService.addStoredTime(timeInMillis);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mHandler.removeCallbacks(updateTimerRunnable);

            resetTimer();
            //set timer to off
            timer = 0;
        }
    }

    /*
    Pulls all workout session info to store into database. Resets timer afterwards
     */
    public void resetTimer() {
        //before resetiing, pull values and send info to database
        //saveSession();

        try {
            remoteService.resetTimer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        timeInMillis = 0L;

        //reset values
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        timeDisplay.setText(String.format("%02d", hours) + ":" +
                String.format("%02d", minutes) + ":" +
                String.format("%02d", seconds));

    }

    /*
    Runnable object that updates timer display
     */
    private Runnable updateTimerRunnable = new Runnable() {
        public void run() {

            int time = 0;
            //compute time difference
            try {
                timeInMillis = SystemClock.uptimeMillis() - remoteService.getStartTime();
                remoteService.setTimeUpdate(timeInMillis);
                time = 0;
                time = (int) (remoteService.getTimeUpdate() / 1000);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            int hours = 0;
            int minutes = 0;
            int seconds = 0;

            //set values if greater than 1 hour
            if (time > 3600) {
                hours = time / 3600;
                time /= 3600;
                minutes = time;
            } else { //less than hour, set values
                hours = 0;
                minutes = time / 60;
            }
            seconds = time % 60;

            //change display
            timeDisplay.setText(String.format("%02d", hours) + ":" +
                    String.format("%02d", minutes) + ":" +
                    String.format("%02d", seconds));

            mHandler.postDelayed(this, 0);
        }

    };

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(remoteConnection);
        remoteConnection = null;
    }
}
