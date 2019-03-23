package boadu.arisworld.com.spyder.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class TireService extends ServiceProvider {

    public TireService(String id, String shopName, String technicianName, String town, LatLng coordinate,
                       List<String> expertise, List<Integer> rating, HashMap<String, String> contact)
    {
        super(id, shopName, technicianName, town, coordinate, expertise, rating, contact);
    }


    public TireService(){
        super();
    }


}
