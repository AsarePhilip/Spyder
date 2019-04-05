package boadu.arisworld.com.spyder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Gravity;
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
    private LinearLayout home;

    private Spinner mMapViewSpinner;
    private SpinnerAdapter mSpinnerAdapter;

    //map
    private static GoogleMap mMap;
    private LatLngBounds.Builder mBuilder;
    private LatLngBounds mMapBounds;
    private CameraUpdate mCameraUpdate;

    public static Locations mLocations;

    private LocationCallback mLocationCallback;

    //Coordinate(Longitude , Latitude) at the four corners of Ghana map
    private LatLng northEast = new LatLng(11.11, 1.14);
    private LatLng northWest = new LatLng(11.11,-3.17);
    private LatLng southEast = new LatLng(4.45, 1.14);
    private LatLng southWest = new LatLng(4.45,-3.17);

    //pop up windows for service provider info
    private PopupWindow spPopUpWindow;

    //pop up windows for emergency service provider info
    private ServiceProviderPWindow SPwindow;

    /*
    //Click listener for Markers
    private OnMarkerClickListener policeMarkerListener;
    private OnMarkerClickListener fireMarkerListener;
    private OnMarkerClickListener ambulanceMarkeListener;
    private OnMarkerClickListener autoMarkerListener;
    private OnMarkerClickListener tireMarkerListener;
    private OnMarkerClickListener towingMarkerListener;
        */

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

        //


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



        /**
         Just 4 Testing (Show four corners of Ghana map)

        MarkerOptions northEastMarker = new MarkerOptions()
                .position(northEast)//northEast
                .title("Ambulance")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance_icon));

        MarkerOptions northWestMarker = new MarkerOptions()
                .position(northWest) //northWest
                .title("Tire service")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.tire_icon));

        MarkerOptions southWestMarker = new MarkerOptions()
                .position(southWest) //southWest
                .title("Police station")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.police_icon));


        MarkerOptions southEastMarker = new MarkerOptions()
                .position(southEast) //southEast
                .title("Fire service")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.fire_icon));

         mMap.addMarker(northEastMarker);
        mMap.addMarker(northWestMarker);
        mMap.addMarker(southEastMarker);
        mMap.addMarker(southWestMarker);
        */

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mBuilder = new LatLngBounds.Builder();
                mBuilder.include(southEast);
                mBuilder.include(southWest);
                mBuilder.include(northEast);
                mBuilder.include(northWest);

                mMapBounds = mBuilder.build();
                //mCameraUpdate = CameraUpdateFactory.newLatLngBounds(mMapBounds, 5);
                //mMap.animateCamera(mCameraUpdate);
                mMap.moveCamera(CameraUpdateFactory.zoomBy(10));

            }
        });
    }

    @Override
    //Callback for marker click
    public boolean onMarkerClick(Marker marker) {
        if(marker.getTag() instanceof AutoMechanic ){
            AutoMechanic markerObject = (AutoMechanic) marker.getTag();
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
           // Toast.makeText(this,"helooooooooo",Toast.LENGTH_SHORT).show();
            SPwindow = new ServiceProviderPWindow(this);
            SPwindow.getWidgets();
            SPwindow.setValues(markerObject);
            spPopUpWindow = SPwindow.getPopUpWindow();
            /*
            TextView tv = spPopUpWindow.getContentView().findViewById(R.id.txtTechnicianName);
            tv.setText(markerObject.getTechnicianName());
            */
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





}