package boadu.arisworld.com.spyder.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class Ambulance extends EmergencyService {

    public Ambulance(String id,String name, String town, double latitude, double longitude, List<Integer>rating,
                     HashMap<String,String> contact) {
        super(id,name, town, latitude, longitude, rating, contact);
    }


    public Ambulance(){

    }



}
