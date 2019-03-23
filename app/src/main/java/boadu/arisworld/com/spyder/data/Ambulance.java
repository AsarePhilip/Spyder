package boadu.arisworld.com.spyder.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class Ambulance extends EmergencyService {


    public Ambulance(){
        super();
    }

    public Ambulance(String id, String name, String town, LatLng coordinate, List<Integer> rating,
                     HashMap<String, String> contact) {
        super(id, name, town, coordinate, rating, contact);
    }


}
