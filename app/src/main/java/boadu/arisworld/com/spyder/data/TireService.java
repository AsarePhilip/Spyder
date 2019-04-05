package boadu.arisworld.com.spyder.data;


import java.util.HashMap;
import java.util.List;

public class TireService extends ServiceProvider {

    public TireService(String id, String shopName, String technicianName, String town,
                       double latitude, double longitude, List<String> expertise, List<Integer> rating,
                       HashMap<String, String> contact)
    {
        super(id, shopName, technicianName, town, latitude,longitude, expertise, rating, contact);
    }


    public TireService(){
        super();
    }


}
