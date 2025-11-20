package com.example.gym;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym.adapters.EjercicioAdapter;
import com.example.gym.adapters.RutinaAdapter;
import com.example.gym.adapters.UsuarioAdapter;
import com.example.gym.models.Ejercicio;
import com.example.gym.models.Rutina;
import com.example.gym.models.Usuario;
import com.example.gym.repository.GymRepository;
import com.example.gym.utils.NetworkUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty, tvNetworkStatus, tvDataSource;
    private LinearLayout llNetworkStatus;
    private TabLayout tabLayout;
    private FloatingActionButton fabAdd;

    private GymRepository repository;

    // Adapters
    private UsuarioAdapter usuarioAdapter;
    private RutinaAdapter rutinaAdapter;
    private EjercicioAdapter ejercicioAdapter;

    // Listas de datos
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Rutina> rutinas = new ArrayList<>();
    private List<Ejercicio> ejercicios = new ArrayList<>();

    // 0 = Usuarios, 1 = Rutinas, 2 = Ejercicios
    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        repository = new GymRepository(this);

        setupRecyclerView();
        setupTabLayout();
        setupFab();
        updateNetworkStatus();

        // Carga inicial (Usuarios)
        loadUsuarios();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvNetworkStatus = findViewById(R.id.tvNetworkStatus);
        tvDataSource = findViewById(R.id.tvDataSource);
        llNetworkStatus = findViewById(R.id.llNetworkStatus);
        tabLayout = findViewById(R.id.tabLayout);
        fabAdd = findViewById(R.id.fabAdd);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 1. ADAPTER USUARIOS (Click corto: Editar, Click largo: Eliminar)
        usuarioAdapter = new UsuarioAdapter(usuarios, new UsuarioAdapter.OnUsuarioClickListener() {
            @Override
            public void onUsuarioClick(Usuario usuario) { showUsuarioDialog(usuario); }
            @Override
            public void onUsuarioLongClick(Usuario usuario) { showDeleteUsuarioDialog(usuario); }
        });

        // 2. ADAPTER RUTINAS (Click corto: Ver Detalle, Click largo: Eliminar)
        rutinaAdapter = new RutinaAdapter(rutinas, new RutinaAdapter.OnRutinaClickListener() {
            @Override
            public void onRutinaClick(Rutina rutina) {
                Intent intent = new Intent(MainActivity.this, RutinaDetailActivity.class);
                intent.putExtra("rutinaId", rutina.getRutinaId());
                intent.putExtra("rutinaNombre", rutina.getRutinaNombre());
                startActivity(intent);
            }
            @Override
            public void onRutinaLongClick(Rutina rutina) { showDeleteRutinaDialog(rutina); }
        });

        // 3. ADAPTER EJERCICIOS (Click corto: Editar, Click largo: Eliminar)
        ejercicioAdapter = new EjercicioAdapter(ejercicios, new EjercicioAdapter.OnEjercicioClickListener() {
            @Override
            public void onEjercicioClick(Ejercicio ejercicio) { showEjercicioDialog(ejercicio); }
            @Override
            public void onEjercicioLongClick(Ejercicio ejercicio) { showDeleteEjercicioDialog(ejercicio); }
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            if (currentTab == 0) {
                showUsuarioDialog(null);
            } else if (currentTab == 1) {
                showRutinaDialog(); // Diálogo con Spinner
            } else if (currentTab == 2) {
                showEjercicioDialog(null);
            }
        });
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                fabAdd.show(); // Siempre mostramos el botón "+"

                if (currentTab == 0) {
                    loadUsuarios();
                } else if (currentTab == 1) {
                    loadRutinas();
                } else if (currentTab == 2) {
                    loadEjercicios();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // ==============================
    // MÉTODOS DE CARGA DE DATOS
    // ==============================

    private void loadUsuarios() {
        recyclerView.setAdapter(usuarioAdapter);
        showLoading(true);
        repository.getUsuarios(new GymRepository.DataCallback<List<Usuario>>() {
            @Override
            public void onSuccess(List<Usuario> data) {
                usuarios = data;
                usuarioAdapter.updateUsuarios(usuarios);
                showLoading(false);
                updateEmptyState(usuarios.isEmpty());
                updateNetworkStatus();
            }
            @Override
            public void onError(String error) {
                showLoading(false);
                updateEmptyState(true);
            }
        });
    }

    private void loadRutinas() {
        recyclerView.setAdapter(rutinaAdapter);
        showLoading(true);
        repository.getUsuarios(new GymRepository.DataCallback<List<Usuario>>() {
            @Override
            public void onSuccess(List<Usuario> data) { usuarios = data; }
            @Override
            public void onError(String error) {}
        });

        repository.getRutinas(new GymRepository.DataCallback<List<Rutina>>() {
            @Override
            public void onSuccess(List<Rutina> data) {
                rutinas = data;
                rutinaAdapter.updateRutinas(rutinas);
                showLoading(false);
                updateEmptyState(rutinas.isEmpty());
                updateNetworkStatus();
            }
            @Override
            public void onError(String error) {
                showLoading(false);
                updateEmptyState(true);
            }
        });
    }

    private void loadEjercicios() {
        recyclerView.setAdapter(ejercicioAdapter);
        showLoading(true);
        repository.getEjercicios(new GymRepository.DataCallback<List<Ejercicio>>() {
            @Override
            public void onSuccess(List<Ejercicio> data) {
                ejercicios = data;
                ejercicioAdapter.updateEjercicios(ejercicios);
                showLoading(false);
                updateEmptyState(ejercicios.isEmpty());
                updateNetworkStatus();
            }
            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                updateEmptyState(true);
            }
        });
    }
    private void showUsuarioDialog(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_usuario, null);
        final TextView tvTitle = view.findViewById(R.id.tvTitle);
        final EditText etNombre = view.findViewById(R.id.etNombre);
        final EditText etApellido = view.findViewById(R.id.etApellido);

        if (usuario != null) {
            tvTitle.setText("Editar Usuario");
            etNombre.setText(usuario.getNombre());
            etApellido.setText(usuario.getApellido());
        } else {
            tvTitle.setText("Nuevo Usuario");
        }

        builder.setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String apellido = etApellido.getText().toString().trim();
                    if (nombre.isEmpty()) return;

                    if (usuario == null) {
                        Usuario nuevo = new Usuario(0, nombre, apellido);
                        repository.createUsuario(nuevo, new GymRepository.DataCallback<Boolean>() {
                            @Override public void onSuccess(Boolean res) { loadUsuarios(); }
                            @Override public void onError(String err) { showToast(err); }
                        });
                    } else {
                        usuario.setNombre(nombre);
                        usuario.setApellido(apellido);
                        repository.updateUsuario(usuario.getId(), usuario, new GymRepository.DataCallback<Boolean>() {
                            @Override public void onSuccess(Boolean res) { loadUsuarios(); }
                            @Override public void onError(String err) { showToast(err); }
                        });
                    }
                })
                .setNegativeButton("Cancelar", null).show();
    }

    private void showDeleteUsuarioDialog(Usuario usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Eliminar a " + usuario.getNombre() + "?")
                .setPositiveButton("Si", (d, w) -> repository.deleteUsuario(usuario.getId(), new GymRepository.DataCallback<Boolean>() {
                    @Override public void onSuccess(Boolean res) { loadUsuarios(); }
                    @Override public void onError(String err) { showToast(err); }
                }))
                .setNegativeButton("No", null).show();
    }

    private void showRutinaDialog() {
        if (usuarios.isEmpty()) {
            showToast("Primero debes crear usuarios");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_rutina, null);

        final EditText etNombre = view.findViewById(R.id.etNombre);
        final Spinner spinner = view.findViewById(R.id.spinnerUsuario);

        List<String> nombresUsuarios = new ArrayList<>();
        for (Usuario u : usuarios) {
            nombresUsuarios.add(u.getNombre() + " " + u.getApellido());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, nombresUsuarios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setView(view)
                .setPositiveButton("Crear", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    if (nombre.isEmpty()) return;

                    // Obtener el ID del usuario seleccionado
                    int posicion = spinner.getSelectedItemPosition();
                    int usuarioId = usuarios.get(posicion).getId();

                    Rutina nuevaRutina = new Rutina();
                    nuevaRutina.setRutinaNombre(nombre);
                    nuevaRutina.setUsuarioId(usuarioId);

                    showLoading(true);
                    repository.createRutina(nuevaRutina, new GymRepository.DataCallback<Boolean>() {
                        @Override public void onSuccess(Boolean res) {
                            showToast("Rutina creada");
                            loadRutinas();
                        }
                        @Override public void onError(String err) {
                            showLoading(false);
                            showToast(err);
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showDeleteRutinaDialog(Rutina rutina) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Rutina")
                .setMessage("¿Eliminar " + rutina.getRutinaNombre() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    showLoading(true);
                    repository.deleteRutina(rutina.getRutinaId(), new GymRepository.DataCallback<Boolean>() {
                        @Override public void onSuccess(Boolean res) { loadRutinas(); }
                        @Override public void onError(String err) {
                            showLoading(false);
                            showToast(err);
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showEjercicioDialog(Ejercicio ejercicio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_ejercicio, null);

        final TextView tvTitle = view.findViewById(R.id.tvTitle);
        final EditText etNombre = view.findViewById(R.id.etNombre);
        final EditText etReps = view.findViewById(R.id.etReps);
        final EditText etPeso = view.findViewById(R.id.etPeso);

        if (ejercicio != null) {
            tvTitle.setText("Editar Ejercicio");
            etNombre.setText(ejercicio.getNombre());
            etReps.setText(String.valueOf(ejercicio.getReps()));
            etPeso.setText(String.valueOf(ejercicio.getPeso()));
        } else {
            tvTitle.setText("Nuevo Ejercicio");
        }

        builder.setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String repsStr = etReps.getText().toString().trim();
                    String pesoStr = etPeso.getText().toString().trim();

                    if (nombre.isEmpty() || repsStr.isEmpty() || pesoStr.isEmpty()) {
                        showToast("Complete todos los campos");
                        return;
                    }

                    int reps = Integer.parseInt(repsStr);
                    double peso = Double.parseDouble(pesoStr);

                    if (ejercicio == null) {
                        // CREAR
                        Ejercicio nuevo = new Ejercicio(0, nombre, reps, peso);
                        showLoading(true);
                        repository.createEjercicio(nuevo, new GymRepository.DataCallback<Boolean>() {
                            @Override public void onSuccess(Boolean res) {
                                showToast("Ejercicio creado");
                                loadEjercicios();
                            }
                            @Override public void onError(String err) {
                                showLoading(false);
                                showToast(err);
                            }
                        });
                    } else {
                        // ACTUALIZAR
                        ejercicio.setNombre(nombre);
                        ejercicio.setReps(reps);
                        ejercicio.setPeso(peso);
                        showLoading(true);
                        repository.updateEjercicio(ejercicio.getEjercicioId(), ejercicio, new GymRepository.DataCallback<Boolean>() {
                            @Override public void onSuccess(Boolean res) {
                                showToast("Ejercicio actualizado");
                                loadEjercicios();
                            }
                            @Override public void onError(String err) {
                                showLoading(false);
                                showToast(err);
                            }
                        });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showDeleteEjercicioDialog(Ejercicio ejercicio) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Ejercicio")
                .setMessage("¿Eliminar " + ejercicio.getNombre() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    showLoading(true);
                    repository.deleteEjercicio(ejercicio.getEjercicioId(), new GymRepository.DataCallback<Boolean>() {
                        @Override public void onSuccess(Boolean res) {
                            showToast("Eliminado");
                            loadEjercicios();
                        }
                        @Override public void onError(String err) {
                            showLoading(false);
                            showToast(err);
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateEmptyState(boolean isEmpty) {
        tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void updateNetworkStatus() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            tvNetworkStatus.setText("Estado: Online");
            llNetworkStatus.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            tvNetworkStatus.setText("Estado: Offline");
            llNetworkStatus.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNetworkStatus();
    }
}