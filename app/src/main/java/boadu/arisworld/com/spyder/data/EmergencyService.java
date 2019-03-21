package boadu.arisworld.com.spyder.data;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class EmergencyService {
    protected String name;
    protected String town;
    protected LatLng coordinate;
    protected List<Integer> rating;
    protected HashMap<String, String> contact;
}
