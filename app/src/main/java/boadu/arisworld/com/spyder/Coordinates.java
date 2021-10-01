package boadu.arisworld.com.spyder;

import com.google.android.gms.maps.model.LatLng;

public class Coordinates {
    private  double mLongitude;
    private double mLatitude;
    private String mCoordTitle;


    public Coordinates(double latitude, double longitude){
        this.mLongitude = longitude;
        this.mLatitude = latitude;
    }


    public double getmLatitude() {
        return mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setLatLong(double latitude, double longitude){
        this.mLongitude = longitude;
        this.mLatitude = latitude;
    }

    public LatLng getLatLong(){
        return new LatLng(mLatitude, mLongitude);
    }

    public String getmCoordTitle(){
        return mCoordTitle;
    }
}
