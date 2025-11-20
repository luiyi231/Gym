package com.example.gym;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym.adapters.EjercicioAdapter;
import com.example.gym.models.Ejercicio;
import com.example.gym.models.RutinaResponse;
import com.example.gym.repository.GymRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class RutinaDetailActivity extends AppCompatActivity {
    private TextView tvRutinaNombre, tvEmpty;
    private RecyclerView recyclerViewEjercicios;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddEjercicio; // Botón nuevo

    private GymRepository repository;
    private EjercicioAdapter ejercicioAdapter;

    private List<Ejercicio> ejerciciosEnRutina = new ArrayList<>();
    private List<Ejercicio> todosLosEjercicios = new ArrayList<>(); // Para el spinner
    private int rutinaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutina_detail);

        rutinaId = getIntent().getIntExtra("rutinaId", 0);
        String rutinaNombre = getIntent().getStringExtra("rutinaNombre");

        initViews();
        setupToolbar();

        if (rutinaNombre != null) {
            tvRutinaNombre.setText(rutinaNombre);
        }

        repository = new GymRepository(this);
        setupRecyclerView();
        setupFab(); // Configurar botón

        loadEjerciciosDeRutina(); // Cargar lo que ya tiene la rutina
        loadTodosLosEjercicios(); // Pre-cargar lista para cuando quiera agregar
    }

    private void initViews() {
        tvRutinaNombre = findViewById(R.id.tvRutinaNombre);
        recyclerViewEjercicios = findViewById(R.id.recyclerViewEjercicios);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAddEjercicio = findViewById(R.id.fabAddEjercicio);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        recyclerViewEjercicios.setLayoutManager(new LinearLayoutManager(this));
        // null en el listener porque aquí solo visualizamos (o puedes implementar borrar relación)
        ejercicioAdapter = new EjercicioAdapter(ejerciciosEnRutina, null);
        recyclerViewEjercicios.setAdapter(ejercicioAdapter);
    }

    private void setupFab() {
        fabAddEjercicio.setOnClickListener(v -> showAddEjercicioDialog());
    }

    // --- CARGA DE DATOS ---

    private void loadEjerciciosDeRutina() {
        showLoading(true);
        repository.getRutinaEjercicios(rutinaId, new GymRepository.DataCallback<RutinaResponse>() {
            @Override
            public void onSuccess(RutinaResponse data) {
                if (data.getEjercicios() != null) {
                    ejerciciosEnRutina = data.getEjercicios();
                    ejercicioAdapter.updateEjercicios(ejerciciosEnRutina);
                } else {
                    ejerciciosEnRutina = new ArrayList<>(); // Limpiar si viene null
                    ejercicioAdapter.updateEjercicios(ejerciciosEnRutina);
                }
                showLoading(false);
                updateEmptyState(ejerciciosEnRutina.isEmpty());
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                // Si da error 404 o similar, asumimos que está vacía
                updateEmptyState(true);
            }
        });
    }

    private void loadTodosLosEjercicios() {
        // Cargamos esto en segundo plano para tenerlo listo para el Spinner
        repository.getEjercicios(new GymRepository.DataCallback<List<Ejercicio>>() {
            @Override
            public void onSuccess(List<Ejercicio> data) {
                todosLosEjercicios = data;
            }
            @Override
            public void onError(String error) {
                Toast.makeText(RutinaDetailActivity.this, "Error cargando lista de ejercicios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- LÓGICA DE RELACIÓN (AGREGAR) ---

    private void showAddEjercicioDialog() {
        if (todosLosEjercicios.isEmpty()) {
            Toast.makeText(this, "No hay ejercicios disponibles para agregar", Toast.LENGTH_SHORT).show();
            // Intentamos recargar por si acaso falló antes
            loadTodosLosEjercicios();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_select_ejercicio, null);

        final Spinner spinner = view.findViewById(R.id.spinnerEjercicios);

        // Llenar Spinner
        List<String> nombres = new ArrayList<>();
        for (Ejercicio e : todosLosEjercicios) {
            nombres.add(e.getNombre() + " (" + e.getPeso() + "kg)");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setView(view)
                .setPositiveButton("Agregar", (dialog, which) -> {
                    int pos = spinner.getSelectedItemPosition();
                    Ejercicio seleccionado = todosLosEjercicios.get(pos);

                    agregarEjercicioARutina(seleccionado);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void agregarEjercicioARutina(Ejercicio ejercicio) {
        showLoading(true);
        // Llamada al método "mágico" del repositorio que llena la tabla intermedia
        repository.addEjercicioToRutina(rutinaId, ejercicio.getEjercicioId(), new GymRepository.DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(RutinaDetailActivity.this, "Ejercicio agregado!", Toast.LENGTH_SHORT).show();
                loadEjerciciosDeRutina(); // Recargamos la lista para ver el nuevo
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(RutinaDetailActivity.this, "Error al agregar: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewEjercicios.setVisibility(show ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(View.GONE); // Ocultar empty mientras carga
    }

    private void updateEmptyState(boolean isEmpty) {
        tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerViewEjercicios.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}