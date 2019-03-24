package boadu.arisworld.com.spyder.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class AutoMechanic extends ServiceProvider {

    public AutoMechanic() {

    }



  public AutoMechanic(String id, String shopName, String technicianName, String town,
                      double latitude, double longitude, List<String> expertise, List<Integer> rating,
                      HashMap<String, String> contact)
  {
        super(id, shopName, technicianName, town,latitude, longitude, expertise, rating, contact);
  }

}
