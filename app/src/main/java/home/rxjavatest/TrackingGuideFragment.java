package home.rxjavatest;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrackingGuideFragment extends Fragment {

    private GoogleMap googleMap;

    private MainActivity activity;

    private Marker guideMarkerMap;
    private Marker myMarkerMap;

    public Context context;

    SharedPreferences sPref;

    private List<Polyline> polylines = new ArrayList<>();
    private static final int[] COLORS = new int[]{R.color.colorAccent,
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            android.R.color.holo_purple,
            android.R.color.holo_orange_light};

    float zoom = 20;
    @BindView(R2.id.et_longitude)
    EditText et_longitude;
    @BindView(R2.id.et_latitude)
    EditText et_latitude;
    @BindView(R2.id.add_button)
    Button add_button;
    @BindView(R2.id.show_button)
    Button show_button;
    @BindView(R2.id.remove_button)
    Button remove_button;

    @BindView(R2.id.mapView)
    MapView mapView;

    RealmMarkers realmReminder;
    List<LatLng> list;
    List<Marker> markers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mapFragmentView = inflater.inflate(R.layout.fragment_location_guide, container, false);
        ButterKnife.bind(this, mapFragmentView);

        mapView.onCreate(savedInstanceState);
        mapView.onLowMemory();
        MapsInitializer.initialize(context);

        sPref = getActivity().getSharedPreferences("coordinates", Context.MODE_PRIVATE);


        return mapFragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        activity = (MainActivity) getActivity();
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double latd = 0;
                double lond = 0;
                String lons = et_longitude.getText().toString();
                if (lons.length() == 0) {
                    Toast toast = Toast.makeText(context,
                            "Enter the Longitude", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    lond = Double.parseDouble(lons);
                    et_longitude.setText(null);

                }
                String lats = et_latitude.getText().toString();
                if (lats.length() == 0) {
                    Toast toast = Toast.makeText(context,
                            "Enter the Latitude", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    latd = Double.parseDouble(lats);
                    et_latitude.setText(null);

                }


                AddMarkerToBase(latd, lond);
                Log.d("MARKER", "Lat =" + String.valueOf(latd) + " " + "Long =" + String.valueOf(lond));

            }
        });

        show_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list = new ArrayList<>();
                for (MyMarker e : realmReminder.readeMarkers(context)) {
                    LatLng latLng = new LatLng(e.getLatitude(), e.getLongitude());
                    myMarkerPlot(latLng.latitude, latLng.longitude);


                }

            }

        });

        remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (MyMarker e : realmReminder.readeMarkers(context)) {
                    LatLng latLng = new LatLng(e.getLatitude(), e.getLongitude());
                    RemoveMarker(latLng.latitude, latLng.longitude);
                    googleMap.clear();





                 realmReminder.readeMarkers(context);





                }

            }
        });
    }


    private void initGoogleMap() {

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        UiSettings myUiSettings = googleMap.getUiSettings();
        myUiSettings.setZoomControlsEnabled(true);

        realmReminder = new RealmMarkers();

//        list = getAll();
//        if(list.size()>0) {
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(list.get(0), zoom));
//            for (LatLng ll: list)
//            myMarkerPlot(ll.latitude,ll.longitude);
//        } else googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.002, 48.004), zoom));
//
//        googleMap.setOnCameraMoveListener(() -> zoom = googleMap.getCameraPosition().zoom);

//        googleMap.setOnMapClickListener(latLng -> {
//            myMarkerPlot(latLng.latitude,latLng.longitude);
//            dbsave(latLng);
//        });
//
        googleMap.setOnMarkerClickListener(marker ->{
            LatLng markerLL = marker.getPosition();
            realmReminder.readeMarkers(context).stream()
                    .filter(e -> markerLL.latitude == e.getLatitude() && markerLL.longitude == e.getLongitude())
                    .forEach(e -> realmReminder.removeMarker(getActivity(), e.getLatitude(),e.getLongitude()));
            marker.remove();
            return true;});
    }


    public void dbsave(LatLng latLng){
//        RealmReminder realmReminder = new RealmReminder();
        MyMarker myMarker = new MyMarker();
        myMarker.setLatitude(latLng.latitude);
        myMarker.setLongitude(latLng.longitude);
        myMarker.setId(latLng.latitude,latLng.longitude);
        realmReminder.saveMarker(context, myMarker);
    }

    public List<LatLng> getAll(){
//        RealmReminder realmReminder = new RealmReminder();
        list = new ArrayList<>();
        for (MyMarker e:realmReminder.readeMarkers(context)) {
            LatLng latLng = new LatLng(e.getLatitude(), e.getLongitude());
            list.add(latLng);
        }
        return list;
    }

    public void myMarkerPlot(double lat, double lon) {

            if (lat != 0 && lon != 0) {

//                if (myMarkerMap != null)
//                    myMarkerMap.remove();

                MarkerOptions usMarker = new MarkerOptions().position(
                        new LatLng(lat, lon)).title("I").anchor(Float.parseFloat("0.5"), Float.parseFloat("0.5"));
                usMarker.icon(activity.getBitmapDescriptor(R.drawable.ic_marker_tourist));
                myMarkerMap = googleMap.addMarker(usMarker);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,
                        lon), zoom));
//                meetingPointMarkerPlot(lat,lon);
            }
    }

    public void meetingPointMarkerPlot(double lat, double lon) {

        LatLng latLng = new LatLng(48.4506, 32.0505);
        if (latLng.latitude != 0 && latLng.longitude != 0) {
            MarkerOptions markerOption = new MarkerOptions().position(latLng).title("name");
            markerOption.icon(activity.getBitmapDescriptor(R.drawable.ic_meeting_point));
            googleMap.addMarker(markerOption);
            trackToGuide(latLng,lat,lon);
        }

    }

    private void trackToGuide(LatLng end, double lat, double lon) {

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(new RoutingListener() {
                    @Override
                    public void onRoutingFailure(RouteException e) {
                    }

                    @Override
                    public void onRoutingStart() {

                    }

                    @Override
                    public void onRoutingSuccess(ArrayList<Route> route, int i) {

                        try {

                            if (polylines.size() > 0) {
                                for (Polyline poly : polylines) {
                                    poly.remove();
                                }
                            }

                            polylines = new ArrayList<>();
                            //add route(s) to the map.
                            for (i = 0; i < route.size(); i++) {

                                //In case of more than 5 alternative routes
                                int colorIndex = i % COLORS.length;

                                PolylineOptions polyOptions = new PolylineOptions();
                                polyOptions.color(ContextCompat.getColor(getActivity(), COLORS[colorIndex]));
                                polyOptions.width(10 + i * 3);
                                polyOptions.addAll(route.get(i).getPoints());
                                Polyline polyline = googleMap.addPolyline(polyOptions);
                                polylines.add(polyline);

                                //                               Toast.makeText(activity, "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("TAG", "route " + e.toString());
                        }

                    }

                    @Override
                    public void onRoutingCancelled() {

                    }
                })
                .alternativeRoutes(true)
                .waypoints(new LatLng(lat,
                        lon), end)
                .build();
        routing.execute();

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mapView.onResume();
            mapView.getMapAsync(gMap -> {
                googleMap = gMap;
                initGoogleMap();
            });
        } catch (NullPointerException e) {
            Log.e("TAG", "Map " + e.toString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            mapView.onPause();
        } catch (Exception e) {
            Log.e("TAG", "Map " + e.toString());
        }
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
        System.gc();
    }
    public void AddMarkerToBase (double latitude, double longitude){
        MyMarker marker = new MyMarker();
        marker.setLatitude(latitude);
        marker.setLongitude(longitude);
        marker.setId(latitude,longitude);
        realmReminder.saveMarker(context,marker);
    }
    public void RemoveMarker (double latitude, double longitude){
        realmReminder.removeMarker(context,latitude,longitude);
    }


    }

