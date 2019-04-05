package boadu.arisworld.com.spyder.data;

import java.util.HashMap;
import java.util.List;

public class EmergencyService {
    private String id;
    private String name;
    private String town;
    private double latitude;
    private double longitude;
    private List<Integer> rating;
    private HashMap<String, String> contact;


    public EmergencyService(){};

    public EmergencyService(String id,String name, String town, double latitude, double longitude, List<Integer>rating,
                            HashMap<String,String> contact)
    {
        this.id = id;
        this.name = name;
        this.town = town;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.contact = contact;

    }


    public String getTown() {
        return town;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    public List<Integer> getRating() {
        return rating;
    }

    public HashMap<String, String> getContact() {
        return contact;
    }


}
