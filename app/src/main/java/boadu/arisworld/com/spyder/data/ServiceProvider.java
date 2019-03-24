package boadu.arisworld.com.spyder.data;




/*
This class represent a details on a service provider
*/

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class ServiceProvider {
    private String id;
    private String shopName;
    private String technicianName;
    private String town;
    private List<String> expertise;
    private List<Integer> rating;
    private HashMap<String, String> contact;
    private double latitude;
    private double longitude;

    public ServiceProvider() {
    }

    public ServiceProvider(String id, String shopName, String technicianName, String town,
                           double latitude, double longitude, List<String> expertise, List<Integer> rating,
                           HashMap<String, String> contact)
            {
                this.id = id;
                this.shopName = shopName;
                this.technicianName = technicianName;
                this.town = town;
                this.latitude = latitude;
                this.longitude = longitude;
                this.expertise = expertise;
                this.rating = rating;
                this.contact = contact;
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


    public List getExpertise() {
        return expertise;
    }

    public List getRating(){
        return rating;
    }

    public HashMap<String, String> getContact() {
        return contact;
    }

    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
