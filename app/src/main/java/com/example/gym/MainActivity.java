package com.example.gym;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gym.adapters.RutinaAdapter;
import com.example.gym.adapters.UsuarioAdapter;
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
    private UsuarioAdapter usuarioAdapter;
    private RutinaAdapter rutinaAdapter;

    private List<Usuario> usuarios = new ArrayList<>();
    private List<Rutina> rutinas = new ArrayList<>();

    private boolean isUsuariosTab = true; // Para saber en qué pestaña estamos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        repository = new GymRepository(this);

        setupRecyclerView();
        setupTabLayout();
        setupFab(); // Configurar el botón flotante
        updateNetworkStatus();

        // Carga inicial
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
        fabAdd = findViewById(R.id.fabAdd); // Asegúrate de agregar esto en el XML
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializamos el adapter de usuarios con la interfaz de clicks
        usuarioAdapter = new UsuarioAdapter(usuarios, new UsuarioAdapter.OnUsuarioClickListener() {
            @Override
            public void onUsuarioClick(Usuario usuario) {
                // Clic corto: Editar Usuario
                showUsuarioDialog(usuario);
            }

            @Override
            public void onUsuarioLongClick(Usuario usuario) {
                // Clic largo: Eliminar Usuario
                showDeleteDialog(usuario);
            }
        });

        recyclerView.setAdapter(usuarioAdapter);
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            if (isUsuariosTab) {
                // Si estamos en la pestaña de usuarios, abrimos diálogo para crear nuevo
                showUsuarioDialog(null);
            } else {
                Toast.makeText(this, "Crear rutinas aún no implementado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    isUsuariosTab = true;
                    fabAdd.show(); // Mostrar botón +
                    loadUsuarios();
                } else if (position == 1) {
                    isUsuariosTab = false;
                    fabAdd.hide(); // Ocultar botón + (o cambiar lógica para rutinas)
                    loadRutinas();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void updateNetworkStatus() {
        boolean isOnline = NetworkUtils.isNetworkAvailable(this);
        if (isOnline) {
            tvNetworkStatus.setText("Estado: Online");
            tvDataSource.setText("Fuente: API");
            llNetworkStatus.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            tvNetworkStatus.setText("Estado: Offline");
            tvDataSource.setText("Fuente: SQLite");
            llNetworkStatus.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        }
    }

    // --- LÓGICA DE CARGA DE DATOS ---

    private void loadUsuarios() {
        // Asignamos el adapter de usuarios al Recycler
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
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                updateEmptyState(true);
            }
        });
    }

    private void loadRutinas() {
        if (rutinaAdapter == null) {
            rutinaAdapter = new RutinaAdapter(rutinas, rutina -> {
                Intent intent = new Intent(MainActivity.this, RutinaDetailActivity.class);
                intent.putExtra("rutinaId", rutina.getRutinaId());
                intent.putExtra("rutinaNombre", rutina.getRutinaNombre());
                startActivity(intent);
            });
        }
        // Cambiamos el adapter del Recycler al de Rutinas
        recyclerView.setAdapter(rutinaAdapter);

        showLoading(true);
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
                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                updateEmptyState(true);
            }
        });
    }

    // --- LÓGICA DE CRUD (Diálogos y llamadas al Repo) ---

    private void showUsuarioDialog(Usuario usuarioExistente) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_usuario, null);

        final TextView tvTitle = view.findViewById(R.id.tvTitle);
        final EditText etNombre = view.findViewById(R.id.etNombre);
        final EditText etApellido = view.findViewById(R.id.etApellido);

        if (usuarioExistente != null) {
            tvTitle.setText("Editar Usuario");
            etNombre.setText(usuarioExistente.getNombre());
            etApellido.setText(usuarioExistente.getApellido());
        } else {
            tvTitle.setText("Nuevo Usuario");
        }

        builder.setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String apellido = etApellido.getText().toString().trim();

                    if (nombre.isEmpty() || apellido.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Complete los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (usuarioExistente == null) {
                        // CREAR
                        Usuario nuevoUsuario = new Usuario(0, nombre, apellido);
                        createUsuarioEnApi(nuevoUsuario);
                    } else {
                        // ACTUALIZAR
                        usuarioExistente.setNombre(nombre);
                        usuarioExistente.setApellido(apellido);
                        updateUsuarioEnApi(usuarioExistente);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }

    private void showDeleteDialog(Usuario usuario) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Usuario")
                .setMessage("¿Estás seguro de eliminar a " + usuario.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteUsuarioEnApi(usuario))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // --- LLAMADAS AL REPOSITORIO PARA CRUD ---

    private void createUsuarioEnApi(Usuario usuario) {
        showLoading(true);
        repository.createUsuario(usuario, new GymRepository.DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(MainActivity.this, "Usuario creado", Toast.LENGTH_SHORT).show();
                loadUsuarios(); // Recargar lista
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(MainActivity.this, "Error al crear: " + error, Toast.LENGTH_SHORT).show();
                // Opcional: recargar de todas formas si quieres refrescar vista
                loadUsuarios();
            }
        });
    }

    private void updateUsuarioEnApi(Usuario usuario) {
        showLoading(true);
        repository.updateUsuario(usuario.getId(), usuario, new GymRepository.DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(MainActivity.this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                loadUsuarios();
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(MainActivity.this, "Error al actualizar: " + error, Toast.LENGTH_SHORT).show();
                loadUsuarios();
            }
        });
    }

    private void deleteUsuarioEnApi(Usuario usuario) {
        showLoading(true);
        repository.deleteUsuario(usuario.getId(), new GymRepository.DataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                Toast.makeText(MainActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                loadUsuarios();
            }

            @Override
            public void onError(String error) {
                showLoading(false);
                Toast.makeText(MainActivity.this, "Error al eliminar: " + error, Toast.LENGTH_SHORT).show();
                loadUsuarios();
            }
        });
    }

    // --- MÉTODOS DE UI (Loading y Empty) ---

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
        if (isEmpty) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNetworkStatus();
    }
}