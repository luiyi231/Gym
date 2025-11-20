package com.example.gym;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym.adapters.EjercicioAdapter;
import com.example.gym.models.Ejercicio;
import com.example.gym.models.RutinaResponse;
import com.example.gym.repository.GymRepository;

import java.util.ArrayList;
import java.util.List;

public class RutinaDetailActivity extends AppCompatActivity {
    private TextView tvRutinaNombre, tvEmpty;
    private RecyclerView recyclerViewEjercicios;
    private ProgressBar progressBar;
    
    private GymRepository repository;
    private EjercicioAdapter ejercicioAdapter;
    
    private List<Ejercicio> ejercicios = new ArrayList<>();
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
        loadEjercicios();
    }

    private void initViews() {
        tvRutinaNombre = findViewById(R.id.tvRutinaNombre);
        recyclerViewEjercicios = findViewById(R.id.recyclerViewEjercicios);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
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
        ejercicioAdapter = new EjercicioAdapter(ejercicios);
        recyclerViewEjercicios.setAdapter(ejercicioAdapter);
    }

    private void loadEjercicios() {
        showLoading(true);
        repository.getRutinaEjercicios(rutinaId, new GymRepository.DataCallback<RutinaResponse>() {
            @Override
            public void onSuccess(RutinaResponse data) {
                if (data.getEjercicios() != null) {
                    ejercicios = data.getEjercicios();
                    ejercicioAdapter.updateEjercicios(ejercicios);
                }
                showLoading(false);
                updateEmptyState(ejercicios.isEmpty());
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(RutinaDetailActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                updateEmptyState(true);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewEjercicios.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEmptyState(boolean isEmpty) {
        tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerViewEjercicios.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}