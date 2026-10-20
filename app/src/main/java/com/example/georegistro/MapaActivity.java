package com.example.georegistro;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        db = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapaFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        db.collection("incidencias").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String titulo = document.getString("titulo");
                String coordenadasStr = document.getString("coordenadas");

                if (coordenadasStr != null && coordenadasStr.contains("Lat:")) {
                    try {
                        String[] partes = coordenadasStr.replace("Lat: ", "").replace(" Lon: ", "").split(",");
                        double lat = Double.parseDouble(partes[0].trim());
                        double lon = Double.parseDouble(partes[1].trim());

                        LatLng ubicacion = new LatLng(lat, lon);
                        mMap.addMarker(new MarkerOptions().position(ubicacion).title(titulo));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 12));
                    } catch (Exception e) {
                    }
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar marcadores", Toast.LENGTH_SHORT).show();
        });
    }
}