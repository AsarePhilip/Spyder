package boadu.arisworld.com.spyder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        OnMarkerClickListener, NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;

    private Spinner mMapViewSpinner;
    private SpinnerAdapter mSpinnerAdapter;

    //map
    private static GoogleMap mMap;
    private LatLngBounds.Builder mBuilder;
    private LatLngBounds mMapBounds;
    private CameraUpdate mCameraUpdate;

    public Locations mLocations;

    private LocationCallback mLocationCallback;

    //Coordinate(Longitude , Latitude) at the four corners of Ghana map
    private LatLng northEast = new LatLng(11.11, 1.14);
    private LatLng northWest = new LatLng(11.11,-3.17);
    private LatLng southEast = new LatLng(4.45, 1.14);
    private LatLng southWest = new LatLng(4.45,-3.17);

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
                            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            break;
                        case 1:
                            //Set map view to satelite view
                            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
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
                }
                for (Location location : locationResult.getLocations()) {
                    //Just for debugging
                    Double lat = location.getLatitude();
                    Double lng = location.getLongitude();
                    String locatonAddress = String.format("Lat : %s\nLng : %s", String.valueOf(lat), String.valueOf(lng));
                    Toast.makeText(getApplicationContext(), locatonAddress, Toast.LENGTH_SHORT).show();
                }
            }
            ;
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


        /**
         Just 4 Testing (Show four corners of Ghana map)
         */
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


        //Plot markers on map(Icon like images on map)
        mMap.addMarker(northEastMarker);
        mMap.addMarker(northWestMarker);
        mMap.addMarker(southEastMarker);
        mMap.addMarker(southWestMarker);


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
                mCameraUpdate = CameraUpdateFactory.newLatLngBounds(mMapBounds, 5);
                mMap.animateCamera(mCameraUpdate);

                /*Trying to focus camera on current location but its not working at the moment
                LatLng curPosistion = locations.getLastKnownLocation();
                if (curPosistion!= null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(curPosistion)
                            .zoom(17)
                            .bearing(90)
                            .tilt(40)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }*/
            }
        });
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