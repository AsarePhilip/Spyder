package boadu.arisworld.com.spyder.data;


import java.util.HashMap;
import java.util.List;

public class FireService extends EmergencyService {
    public FireService(String id,String name, String town, double latitude, double longitude, List<Integer>rating,
                       HashMap<String,String> contact) {
        super(id,name, town, latitude, longitude, rating, contact);
    }
    public  FireService(){

    }
}
