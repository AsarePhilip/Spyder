package boadu.arisworld.com.spyder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import boadu.arisworld.com.spyder.Fragments.MapFragment;
import boadu.arisworld.com.spyder.Fragments.SignUpFragment;
import boadu.arisworld.com.spyder.data.RegData;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        OnMarkerClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static int RC_SIGN_IN = 9999;


    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView navigationView;

    private Spinner mapViewSpinner;
    private SpinnerAdapter spinnerAdapter;

    private static GoogleMap mMap;

    public Locations locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create a map fragment and place it in the fragment holder of the layout
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentHolder);
        mapFragment.getMapAsync(this);

        //Add toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //Switch drawer layout on and off
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mToggle.setDrawerIndicatorEnabled(true);
        mToggle.setHomeAsUpIndicator(R.mipmap.arrow_up_small);
        mToggle.setDrawerSlideAnimationEnabled(true);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        //navigation view
        navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        //Lists for mapviews
        List<String> mapViews = new ArrayList<String>();
        mapViews.add("Map view");
        mapViews.add("Satelite view");

        //Set map view spinner
        mapViewSpinner = findViewById(R.id.view_spinner);
        spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, mapViews);
        ((ArrayAdapter) spinnerAdapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapViewSpinner.setAdapter(spinnerAdapter);

        //Set onclick listener to switch map views
        mapViewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int viewId = parent.getSelectedItemPosition();

                if (mMap != null) {
                    switch (viewId) {
                        case 0:
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            break;
                        case 1:
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            break;
                    }
                }else{Toast.makeText(getBaseContext(), "Map is Null", Toast.LENGTH_SHORT).show();}
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Check and request location permissions
        locations = new Locations(this);
        Task<LocationSettingsResponse> task = locations.getLocationSettings();
        locations.requestLocationPermission(task);
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
                Toast.makeText(getApplicationContext(), "Auto-mechanic clicked", Toast.LENGTH_SHORT).show();
                mDrawerLayout.closeDrawers();
                break;
            case R.id.nav_towing_service:
                mDrawerLayout.closeDrawers();
                break;

            case R.id.nav_tire_service:
                mDrawerLayout.closeDrawers();
                break;

            case R.id.nav_police:
                mDrawerLayout.closeDrawers();
                break;

            case R.id.nav_fire_service:
                mDrawerLayout.closeDrawers();
                break;

            case R.id.nav_ambulance:
                mDrawerLayout.closeDrawers();
                break;
            case R.id.nav_about:
                mDrawerLayout.closeDrawers();
                break;
        }


        return false;
    }

    @Override
    //Callback for marker click
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(true);


        LatLng latLng1 = new LatLng(10,10);
        MarkerOptions markerOptions1 = new MarkerOptions()
                .position(latLng1)
                .title("Ambulance")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ambulance_icon));

        MarkerOptions markerOptions2 = new MarkerOptions()
                .position(new LatLng(10,10.1))
                .title("Police station")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.police_icon));

        MarkerOptions markerOptions3 = new MarkerOptions()
                .position(new LatLng(9.9,10.1))
                .title("Fire service")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.fire_icon));

        MarkerOptions markerOptions4 = new MarkerOptions()
                .position(new LatLng(9.9,9.9))
                .title("Tire service")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.tire_icon));

        MarkerOptions markerOptions5 = new MarkerOptions()
                .position(new LatLng(9.8,10.1))
                .title("Auto-mechanic")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.auto_icon));

        MarkerOptions markerOptions6 = new MarkerOptions()
                .position(new LatLng(10.2,10.1))
                .title("Towing service")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.towing_icon));

        mMap.addMarker(markerOptions1);
        mMap.addMarker(markerOptions2);
        mMap.addMarker(markerOptions3);
        mMap.addMarker(markerOptions4);
        mMap.addMarker(markerOptions5);
        mMap.addMarker(markerOptions6);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 10));

    }

    @Override
    protected void onResume() {
        super.onResume();
        locations.startLocationUpdates(locations.mLocationCallback);
    }


    @Override
    protected void onPause() {
        super.onPause();
        locations.stopLocationUpdates(locations.mFusedLocationProvidedClient, locations.mLocationCallback);
    }


}