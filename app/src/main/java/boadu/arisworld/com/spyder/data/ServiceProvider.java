package boadu.arisworld.com.spyder.data;




/*
This class represent a details on a service provider
*/

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class ServiceProvider {
    protected String shopName;
    protected String technicianName;
    protected String town;
    protected LatLng coordinate;
    protected List<String> expertise;
    protected List<Integer> rating;
    protected HashMap<String, String> contact;

    public ServiceProvider() {
    }


    public String getShopName() {
        return shopName;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public String getTown() {
        return town;
    }

    public LatLng getCoordinate(){
        return coordinate;
    }

    public List getExpertise() {
        return expertise;
    }

    public List getRating(){
        return rating;
    }

    public HashMap<String, String> getContact() {
        return contact;
    }
}
