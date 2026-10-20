package com.example.georegistro;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {

    private TextInputEditText etTitulo, etCategoria, etDescripcion, etFechaHora, etCoordenadas;
    private TextView tvSensorValores;
    private Button btnGuardar;

    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;

    private SensorManager sensorManager;
    private Sensor acelerometro;
    private long ultimoTiempoAct = 0;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    obtenerUbicacion();
                } else {
                    Toast.makeText(this, "Permiso de GPS denegado.", Toast.LENGTH_LONG).show();
                    etCoordenadas.setText("Ubicación no disponible");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etTitulo = findViewById(R.id.etTitulo);
        etCategoria = findViewById(R.id.etCategoria);
        etDescripcion = findViewById(R.id.etDescripcion);
        etFechaHora = findViewById(R.id.etFechaHora);
        etCoordenadas = findViewById(R.id.etCoordenadas);
        tvSensorValores = findViewById(R.id.tvSensorValores);
        btnGuardar = findViewById(R.id.btnGuardarIncidencia);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        cargarFechaYHoraActual();
        verificarPermisosYObtenerUbicacion();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        if (acelerometro == null) {
            tvSensorValores.setText("Acelerómetro no disponible en este dispositivo");
        }

        btnGuardar.setOnClickListener(v -> validarYGuardar());
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                tvSensorValores.setText(String.format(Locale.getDefault(), "Movimiento -> X: %.1f | Y: %.1f | Z: %.1f", x, y, z));

                double aceleracionTotal = Math.sqrt(x*x + y*y + z*z) - SensorManager.GRAVITY_EARTH;
                long tiempoActual = System.currentTimeMillis();

                if (aceleracionTotal > 12 && (tiempoActual - ultimoTiempoAct) > 1000) {
                    ultimoTiempoAct = tiempoActual;
                    etTitulo.setText("");
                    etCategoria.setText("");
                    etDescripcion.setText("");
                    Toast.makeText(RegistroActivity.this, "¡Campos borrados por agitación!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (acelerometro != null) {
            sensorManager.registerListener(sensorEventListener, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (acelerometro != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    private void cargarFechaYHoraActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        etFechaHora.setText(sdf.format(new Date()));
    }

    private void verificarPermisosYObtenerUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void obtenerUbicacion() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    etCoordenadas.setText("Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                } else {
                    etCoordenadas.setText("Buscando señal GPS...");
                }
            });
        } catch (SecurityException e) {
            etCoordenadas.setText("Error de seguridad con el GPS");
        }
    }

    private void validarYGuardar() {
        String titulo = etTitulo.getText().toString().trim();
        String categoria = etCategoria.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String fechaHora = etFechaHora.getText().toString().trim();
        String coordenadas = etCoordenadas.getText().toString().trim();

        if (titulo.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(this, "Completa el Título y la Categoría (*)", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        Incidencia nuevaIncidencia = new Incidencia(titulo, categoria, descripcion, fechaHora, coordenadas);

        db.collection("incidencias").add(nuevaIncidencia)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "¡Incidencia guardada!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error de red al guardar.", Toast.LENGTH_LONG).show();
                    btnGuardar.setEnabled(true);
                    btnGuardar.setText("Guardar Incidencia");
                });
    }
}