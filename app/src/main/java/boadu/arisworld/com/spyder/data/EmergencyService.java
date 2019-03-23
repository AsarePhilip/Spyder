package boadu.arisworld.com.spyder.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class EmergencyService {
    private String id;
    private String name;
    private String town;
    private LatLng coordinate;
    private List<Integer> rating;
    private HashMap<String, String> contact;


    public EmergencyService(){};

    public EmergencyService(String id,String name, String town, LatLng coordinate, List<Integer>rating,
                            HashMap<String,String> contact)
    {
        this.id = id;
        this.name = name;
        this.town = town;
        this.coordinate = coordinate;
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

    public LatLng getCoordinate() {
        return coordinate;
    }

    public List<Integer> getRating() {
        return rating;
    }

    public HashMap<String, String> getContact() {
        return contact;
    }


}
