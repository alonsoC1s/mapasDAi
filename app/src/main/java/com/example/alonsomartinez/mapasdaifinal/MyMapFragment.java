package com.example.alonsomartinez.mapasdaifinal;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.app.Fragment;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;


public class MyMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private static final String TAG = "MAPFragment";
    private GoogleMap mMap;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference eventRef = database.getReference("Eventos");
    FloatingActionButton fabNewEvent;

    // Handles de bottom sheet
    BottomSheetBehavior bottomSheet;
    TextView sheetTitle;
    TextView sheetContent;
    LinearLayout sheetHeader;

    // Instancias estáticas de referencias
    private static String currentMarkerid;

    BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View view, int newState) {
            switch (newState){
                case (BottomSheetBehavior.STATE_DRAGGING):

                    sheetHeader.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

                    getDataOnMarkerClick(currentMarkerid);
                    break;
            }
        }

        @Override
        public void onSlide(@NonNull View view, float v) { }
    };

    FloatingActionButton.OnClickListener fabListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View v) {
            Snackbar.make(Objects.requireNonNull(getView()), "Abriendo localizador ...", Snackbar.LENGTH_LONG).show();

            //Open the google place picker
            final int PLACE_PICKER_REQUEST = 1;
            try {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent = builder.build(getActivity());
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                Log.e(TAG,"Google play services repairable exception");
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.e(TAG,"Google play services not available exception");
            }

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Obteniendo referencias a views
        bottomSheet = BottomSheetBehavior.from(view.findViewById(R.id.bottomSheetLayout));
        sheetTitle = view.findViewById(R.id.tv_modalsheet_title);
        sheetContent = view.findViewById(R.id.tv_modalsheet_content);
        sheetHeader = view.findViewById(R.id.sheet_header);
        fabNewEvent = view.findViewById(R.id.fab_new_event);

        // Bottom Sheet behaviour
        bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheet.setBottomSheetCallback(bottomSheetCallback);

        // Floating Action Button
        fabNewEvent.setOnClickListener(fabListener);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        MapFragment myGoogleMap = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        myGoogleMap.getMapAsync(this);

        drawMarkersFromDB();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Preferencias del mapa
        mMap.setMinZoomPreference(11);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        // mostrar datos de la base de datos

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 11));
            }
        });
    }

    /*
    Eventos que se conectan a la BD
     */

    /**
     * Método que llama a la base de datos y dibuja los marcadores que se encuentran en ella.
     */
    private void drawMarkersFromDB(){
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataShot : dataSnapshot.getChildren()){
                    MyEvents event = dataShot.getValue(MyEvents.class);

                    LatLng coords = new LatLng(event.getEventLatitude(), event.getEventLongitude());

                    MarkerOptions options = new MarkerOptions().position(coords)
                            .title(event.getEventName());

                    mMap.addMarker(options).setTag(event.getEventID());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    /**
     * Mètodo que recupera datos del marcador de la bd con el tag
     * @param eventId: Id único del evento, que está contenido en el tag del marcador
     */
    private void getDataOnMarkerClick(String eventId){
        eventRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                MyEvents evento = dataSnapshot.getValue(MyEvents.class);

                sheetContent.setText(evento.getEventContent());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    /*
    Métodos del callback listener mapa
     */

    @Override
    public boolean onMarkerClick(Marker marker) {

        sheetTitle.setText(marker.getTitle());
        currentMarkerid = String.valueOf(marker.getTag());

        bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);

        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        bottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    //Google place picker callback. When location selected, open newPostActivity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){

            Place place = PlacePicker.getPlace(data, getActivity());
            LatLng placePickerLatLng = place.getLatLng();

            Intent intent = new Intent(getActivity(),NewEvent.class);

            Bundle infoBundle = new Bundle();
            infoBundle.putDouble("eventLatitude",placePickerLatLng.latitude);
            infoBundle.putDouble("eventLongitude",placePickerLatLng.longitude);

            intent.putExtras(infoBundle);
            startActivity(intent);
        }
    }
}
