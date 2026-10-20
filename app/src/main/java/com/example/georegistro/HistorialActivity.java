package com.example.georegistro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private RecyclerView recyclerHistorial;
    private FirebaseFirestore db;
    private List<Incidencia> listaIncidencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        recyclerHistorial = findViewById(R.id.recyclerHistorial);
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        listaIncidencias = new ArrayList<>();

        cargarDatosDeFirebase();
    }

    private void cargarDatosDeFirebase() {
        db.collection("incidencias")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Incidencia incidencia = document.toObject(Incidencia.class);
                        listaIncidencias.add(incidencia);
                    }
                    AdaptadorIncidencias adaptador = new AdaptadorIncidencias(listaIncidencias);
                    recyclerHistorial.setAdapter(adaptador);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error de red al cargar historial", Toast.LENGTH_SHORT).show();
                });
    }
    private class AdaptadorIncidencias extends RecyclerView.Adapter<AdaptadorIncidencias.ViewHolder> {
        private List<Incidencia> incidencias;

        public AdaptadorIncidencias(List<Incidencia> incidencias) {
            this.incidencias = incidencias;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incidencia, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Incidencia inc = incidencias.get(position);
            holder.tvTitulo.setText(inc.getTitulo());
            holder.tvCategoria.setText(inc.getCategoria());
            holder.tvFecha.setText("Fecha: " + inc.getFechaHora());
            holder.tvCoordenadas.setText(inc.getCoordenadas());
        }

        @Override
        public int getItemCount() {
            return incidencias.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitulo, tvCategoria, tvFecha, tvCoordenadas;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitulo = itemView.findViewById(R.id.tvItemTitulo);
                tvCategoria = itemView.findViewById(R.id.tvItemCategoria);
                tvFecha = itemView.findViewById(R.id.tvItemFecha);
                tvCoordenadas = itemView.findViewById(R.id.tvItemCoordenadas);
            }
        }
    }
}