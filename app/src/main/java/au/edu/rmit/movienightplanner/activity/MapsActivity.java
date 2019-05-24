package au.edu.rmit.movienightplanner.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import au.edu.rmit.movienightplanner.R;
import au.edu.rmit.movienightplanner.model.DAO;
import au.edu.rmit.movienightplanner.model.Event;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Event> futureEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        futureEvents = DAO.getFutureEvents();
    }


    /**
     * Manipulates the map once available. This callback is triggered when the map is ready to be
     * used. This is where we can add markers or lines, add listeners or move the camera. In this
     * case, we just add a marker near Sydney, Australia. If Google Play services is not installed
     * on the device, the user will be prompted to install it inside the SupportMapFragment. This
     * method will only be triggered once the user has installed Google Play services and returned
     * to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        int count = 3;
        if(futureEvents.size() < 3) count = futureEvents.size();
        LatLng latLng;
        for (int i = 0; i < count; i++) {
            latLng = new LatLng(Double.parseDouble(futureEvents.get(i).getLocation()[0]),
                                       Double.parseDouble(futureEvents.get(i).getLocation()[1]));
            System.out.println(futureEvents.get(i).getTitle() + " --> add marker");;
            mMap.addMarker(new MarkerOptions().position(latLng).title(futureEvents.get(i).getTitle()));
        }
        LatLng melbourne = new LatLng(-37.8136, 144.9631);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(melbourne));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(melbourne, 10));
        //mMap.addMarker(new MarkerOptions().position(melbourne).title("MEL"));
    }
}
