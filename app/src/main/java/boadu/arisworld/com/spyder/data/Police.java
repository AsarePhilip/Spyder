package boadu.arisworld.com.spyder.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class Police extends EmergencyService {

    public Police(String id, String name, String town, LatLng coordinate, List<Integer>rating,
                  HashMap<String,String> contact)
            {
                super(id,name, town, coordinate, rating, contact);

            }

    public Police(){
        super();
    }

}
