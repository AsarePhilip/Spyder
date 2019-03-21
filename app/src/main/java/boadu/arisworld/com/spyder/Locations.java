package boadu.arisworld.com.spyder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.concurrent.Executor;

public class Locations {
    LocationRequest mLocationReqeust;
    Context mContext;
    private FusedLocationProviderClient mFusedLocationProvidedClient;
    private boolean mLocationPermGranted = false;
    private LatLng mCurLatLng;

    public Locations(final Context context) {
        this.createLocationRequsets();
        this.mContext = context;
        this.mFusedLocationProvidedClient = LocationServices.getFusedLocationProviderClient(context);
    }


    //Create a location requests object
    private void createLocationRequsets() {
        mLocationReqeust = LocationRequest.create();
        mLocationReqeust.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationReqeust.setInterval(5000);
        mLocationReqeust.setFastestInterval(5000);
    }



    public FusedLocationProviderClient getmFusedLocationProvidedClient(){
        return mFusedLocationProvidedClient;
    }


    //Create a task of location permissions required
    public Task getLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().
                addLocationRequest(mLocationReqeust);
        SettingsClient client = LocationServices.getSettingsClient(mContext);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        return task;
    }

    // Ask thee user to grant location permissions if not already granted
    public void requestLocationPermission(Task task) {
        if (task != null) {
            task.addOnSuccessListener((Activity) mContext, new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    Toast.makeText(mContext, "Location permission granted", Toast.LENGTH_SHORT).show();
                    mLocationPermGranted = true;
                }
            });

            task.addOnFailureListener((Activity) mContext, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        int statusCode = ((ResolvableApiException) e).getStatusCode();
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult((Activity) mContext, statusCode);
                            mLocationPermGranted = true;
                        } catch (IntentSender.SendIntentException sendEx) {
                            }
                    }
                }
            });
        } else {
            Toast.makeText(mContext, "Task is empty", Toast.LENGTH_SHORT).show();
        }

    }


    //Request for current location coordinate(LatLng)
    @SuppressLint("MissingPermission")
    public LatLng getLastKnownLocation() {
        this.mFusedLocationProvidedClient.getLastLocation().
                addOnSuccessListener((Activity) mContext, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mCurLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
        return mCurLatLng;
    }

    //Start location updates
    @SuppressLint("MissingPermission")
    public void startLocationUpdates(LocationCallback locationCallback) {
        mFusedLocationProvidedClient.requestLocationUpdates(mLocationReqeust,
                locationCallback,
                null);
    }

    //Stop location updates
    public void stopLocationUpdates(FusedLocationProviderClient locationProviderClient, LocationCallback locationCallback) {
        locationProviderClient.removeLocationUpdates(locationCallback);
    }



    //Plot a single coordinate(LatLng) on map
    public void plotCoordinates(Coordinates coordinate, GoogleMap googleMap, int resourceId){
        googleMap.addMarker(new MarkerOptions()
                .position(coordinate.getLatLong())
                .title(coordinate.getmCoordTitle())
                .icon(BitmapDescriptorFactory.fromResource(resourceId))
        );
    }

    //Plot a list of coordinates(LatLng) on map
    public void plotCoordinates(List<Coordinates> coordinatesList , GoogleMap googleMap, int resourceId){
        for ( Coordinates coordinate : coordinatesList){
            googleMap.addMarker(new MarkerOptions()
                    .position(coordinate.getLatLong())
                    .title(coordinate.getmCoordTitle())
                    .icon(BitmapDescriptorFactory.fromResource(resourceId))
            );
        }
    }
}