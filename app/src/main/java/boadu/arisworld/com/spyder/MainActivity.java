package boadu.arisworld.com.spyder;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import boadu.arisworld.com.spyder.PopUp_Windows.ServiceProviderPWindow;
import boadu.arisworld.com.spyder.data.Ambulance;
import boadu.arisworld.com.spyder.data.AutoMechanic;
import boadu.arisworld.com.spyder.data.FireService;
import boadu.arisworld.com.spyder.data.Police;
import boadu.arisworld.com.spyder.data.TireService;
import boadu.arisworld.com.spyder.data.TowingService;

public class MainActivity
        extends
            AppCompatActivity
        implements
            OnMapReadyCallback,
            OnMarkerClickListener,
            NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;

    private Spinner mMapViewSpinner;
    private SpinnerAdapter mSpinnerAdapter;

    //map
    private static GoogleMap mMap;
    public static Locations mLocations;

    private LocationCallback mLocationCallback;

    //pop up windows for service provider info
    private PopupWindow spPopUpWindow = null;

    //pop up windows for emergency service provider info
    private ServiceProviderPWindow SPwindow = null;

    ClipboardManager clipboardManager;
    ClipData clipData;
    PackageManager packageManager;


    //Firebase database
    DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create a map fragment and place it in the fragment holder of the layout
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentHolder);
        mapFragment.getMapAsync(this);

        packageManager = this.getPackageManager();
        clipboardManager = (android.content.ClipboardManager) getSystemService(this.CLIPBOARD_SERVICE);

        //Add toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //Switch drawer layout on/off when you click menu icon or swipe the drawer layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mToggle.setDrawerIndicatorEnabled(true);
        mToggle.setHomeAsUpIndicator(R.mipmap.arrow_up_small);
        mToggle.setDrawerSlideAnimationEnabled(true);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        //navigation view
        mNavigationView = (NavigationView) findViewById(R.id.navView);
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(this);

        //Lists of types of mapviews
        List<String> mapViews = new ArrayList<String>();
        mapViews.add("Map view");
        mapViews.add("Satelite view");

        //Set map view spinner (Dropdown menu to prefered map view)
        mMapViewSpinner = findViewById(R.id.view_spinner);
        mSpinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mapViews);
        ((ArrayAdapter) mSpinnerAdapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMapViewSpinner.setAdapter(mSpinnerAdapter);

        //Set onclick listener to switch map views
        mMapViewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int viewId = parent.getSelectedItemPosition();

                if (mMap != null) {
                    switch (viewId) {
                        case 0:
                            //Set map view to normal street view
                            if(mMap.getMapType() != GoogleMap.MAP_TYPE_NORMAL){
                                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); }
                            break;
                        case 1:
                            //Set map view to satelite view
                            if(mMap.getMapType() != GoogleMap.MAP_TYPE_SATELLITE){
                                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); }
                            break;
                    }
                }else{Toast.makeText(getBaseContext(), "Map is Null", Toast.LENGTH_SHORT).show();}
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        // What happens when device location is detected
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }else{
                    //Just for debugging
                    Double lat = locationResult.getLastLocation().getLatitude();
                    Double lng = locationResult.getLastLocation().getLongitude();
                    String locatonAddress = String.format("Lat : %s\nLng : %s", String.valueOf(lat), String.valueOf(lng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
                    Toast.makeText(getApplicationContext(), locatonAddress, Toast.LENGTH_SHORT).show();
                }
            }
        };


        //Create a Location object
        mLocations = new Locations(this);

        //Check and request location permissions
        Task<LocationSettingsResponse> task = mLocations.getLocationSettings();
        mLocations.requestLocationPermission(task);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    @Override
    //Actions to perform when options menu item is selected
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.actBar_logout:
                AuthUI.getInstance().signOut(getBaseContext());
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    //Actions to perform when navigation  menu item is selected
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.nav_auto_mechanics:
                //Toast.makeText(getApplicationContext(), "Auto-mechanic clicked", Toast.LENGTH_SHORT).show();
                mMap.clear();
                mToolbar.setTitle(R.string.auto_mechanics);
                databaseHelper.mAutoMechanicRef.addValueEventListener(new ValueEventListener() {
                    MarkerOptions markerOptions;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for( DataSnapshot subData : dataSnapshot.getChildren()){
                                   AutoMechanic autoMechanic = subData.getValue(AutoMechanic.class);

                                    markerOptions = new MarkerOptions()
                                            .position(new LatLng(autoMechanic.getLatitude(), autoMechanic.getLongitude()))
                                            .title(autoMechanic.getShopName())
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.auto_icon));

                                    mMap.addMarker(markerOptions)
                                            .setTag(autoMechanic);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mDrawerLayout.closeDrawers();
                break;

            case R.id.nav_towing_service:
                mMap.clear();
                mToolbar.setTitle(R.string.towing_service);
                databaseHelper.mTowingRef.addValueEventListener(new ValueEventListener() {MarkerOptions markerOptions;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for( DataSnapshot subData : dataSnapshot.getChildren()){
                            TowingService towingService = subData.getValue(TowingService.class);

                            markerOptions = new MarkerOptions()
                                    .position(new LatLng(towingService.getLatitude(), towingService.getLongitude()))
                                    .title(towingService.getShopName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.towing_icon));
                            mMap.addMarker(markerOptions);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mDrawerLayout.closeDrawers();
                break;

            case R.id.nav_tire_service:
                mMap.clear();
                mToolbar.setTitle(R.string.tire_service);
                databaseHelper.mTireServiceRef.addValueEventListener(new ValueEventListener() {
                    // LatLng latLng;
                    MarkerOptions markerOptions;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for( DataSnapshot subData : dataSnapshot.getChildren()){
                            TireService tireService = subData.getValue(TireService.class);

                            markerOptions = new MarkerOptions()
                                    .position(new LatLng(tireService.getLatitude(), tireService.getLongitude()))
                                    .title(tireService.getShopName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.tire_icon));
                            mMap.addMarker(markerOptions);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mDrawerLayout.closeDrawers();
                break;

            case R.id.nav_police:
                mMap.clear();
                mToolbar.setTitle(R.string.police);
                databaseHelper.mPoliceRef.addValueEventListener(new ValueEventListener() {
                    // LatLng latLng;
                    MarkerOptions markerOptions;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for( DataSnapshot subData : dataSnapshot.getChildren()){
                            Police police = subData.getValue(Police.class);

                            markerOptions = new MarkerOptions()
                                    .position(new LatLng(police.getLatitude(), police.getLongitude()))
                                    .title(police.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.police_icon));
                            mMap.addMarker(markerOptions);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mDrawerLayout.closeDrawers();
                break;

            case R.id.nav_fire_service:
                mToolbar.setTitle(R.string.fire_service);
                databaseHelper.mPoliceRef.addValueEventListener(new ValueEventListener() {
                    MarkerOptions markerOptions;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for( DataSnapshot subData : dataSnapshot.getChildren()){
                            FireService fireService = subData.getValue(FireService.class);

                            markerOptions = new MarkerOptions()
                                    .position(new LatLng(fireService.getLatitude(), fireService.getLongitude()))
                                    .title(fireService.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.fire_icon));
                            mMap.addMarker(markerOptions);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mDrawerLayout.closeDrawers();
                break;

            case R.id.nav_ambulance:
                mToolbar.setTitle(R.string.ambulance);
                databaseHelper.mAmbulnceRef.addValueEventListener(new ValueEventListener() {
                    MarkerOptions markerOptions;
                    @Override                    // LatLng latLng;

                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for( DataSnapshot subData : dataSnapshot.getChildren()){
                            Ambulance ambulance = subData.getValue(Ambulance.class);

                            markerOptions = new MarkerOptions()
                                    .position(new LatLng(ambulance.getLatitude(), ambulance.getLongitude()))
                                    .title(ambulance.getName())
                                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance_icon));
                            mMap.addMarker(markerOptions);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mDrawerLayout.closeDrawers();
                break;

            case R.id.nav_about:
                mDrawerLayout.closeDrawers();
                break;
        }


        return false;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Add marker click listener
        mMap.setOnMarkerClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.zoomBy(10));
            }
        });
    }

    @Override
    //Callback for marker click
    public boolean onMarkerClick(Marker marker) {
        if(marker.getTag() instanceof AutoMechanic ){
            AutoMechanic markerObject = (AutoMechanic) marker.getTag();
            SPwindow = new ServiceProviderPWindow(this);
            SPwindow.getWidgets();
            SPwindow.setValues(markerObject);
            spPopUpWindow = SPwindow.getPopUpWindow();
            SPwindow.getTextView(R.id.txtDistance).setText(String.format("%.3f",mLocations.getDistance(marker)) + " Km");

            //Set onClick listener on widgete
            SPwindow.getTextView(R.id.txtPhone2).setOnClickListener(spPopUpListener);
            SPwindow.getTextView(R.id.txtPhone1).setOnClickListener(spPopUpListener);
            SPwindow.getTextView(R.id.txtEmail).setOnClickListener(spPopUpListener);
            SPwindow.getTextView(R.id.btnCopy).setOnClickListener(spPopUpListener);


            spPopUpWindow.showAtLocation((LinearLayout) findViewById(R.id.home), Gravity.CENTER, 0,0);
        }
        
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocations.startLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocations.stopLocationUpdates(mLocations.getmFusedLocationProvidedClient(), mLocationCallback);
    }


    private void placeCall(String phoneNumber){
        Intent dialer = new Intent(Intent.ACTION_DIAL);
        dialer.setData(Uri.parse("tel:"+ phoneNumber));
        if(dialer.resolveActivity(packageManager) != null){
            startActivity(dialer);
        }else{
            Toast.makeText(this, "Package manager empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage(String[] email, String subject, String text ){
        Intent sendMail = new Intent(Intent.ACTION_SENDTO);
        sendMail.setData(Uri.parse("mailto:"));
        sendMail.putExtra(Intent.EXTRA_EMAIL, email);
        sendMail.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendMail.putExtra(Intent.EXTRA_TEXT, text);

        if (sendMail.resolveActivity(packageManager) != null) {
            Toast.makeText(this, "Package manager not  empty", Toast.LENGTH_SHORT).show();
            startActivity(sendMail);
        }else{
            Toast.makeText(this, "Package manager empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyText(String text){
        clipData = ClipData.newPlainText("info",text);
        clipboardManager.setPrimaryClip(clipData);
    }


    private View.OnClickListener spPopUpListener = new View.OnClickListener(){
        @Override
        public  void  onClick(View view){
            int id = view.getId();

            if(SPwindow != null){
                if(spPopUpWindow != null){
            String title = SPwindow.getTextView(R.id.txtTitle).getText().toString();
            String technician = SPwindow.getTextView(R.id.txtTechnicianName).getText().toString();
            String town = SPwindow.getTextView(R.id.txtTown).getText().toString();
            String expertise = SPwindow.getTextView(R.id.txtExpertiseList).getText().toString();
            String phone1 = SPwindow.getTextView(R.id.txtPhone1).getText().toString().split("\\ ")[1].toString().trim();
            String phone2 = SPwindow.getTextView(R.id.txtPhone2).getText().toString().split("\\ ")[1].toString().trim();
            String email = SPwindow.getTextView(R.id.txtEmail).getText().toString().split("\\ ")[1].toString().trim();
            String distance = SPwindow.getTextView(R.id.txtDistance).getText().toString();
            String location = SPwindow.getTextView(R.id.txtGpsLocation).getText().toString();
            String info = "Title: " + title + "\n" +
                            "Technician name: " + technician + "\n" +
                            "Town: " + town +   "\n" +
                            "Expertise: " + expertise + "\n" +
                            "Phone1: " + phone1 + "\n" +
                            "Phone2: " + phone2 + "\n" +
                            "Email: " + email + "\n" +
                            "Distance: " + distance  + "\n" +
                            "GPS Location: " + location  + "\n" ;

            String subject = "Subject";
            String text = "Message body";

            switch (id){
                case R.id.btnCopy:
                    copyText(info);
                    break;
                case R.id.txtPhone1:
                    placeCall(phone1);
                    break;
                case R.id.txtPhone2:
                    placeCall(phone2);
                    break;
                case R.id.txtEmail:
                    String[] emailArray = new  String[1];
                    emailArray[0] = email;
                    sendMessage( emailArray, subject, text);
                    break;
            }// End of switch statement


                }
            }//End of if statement

        }
    };


}