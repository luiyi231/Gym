package com.example.gym.repository;

import android.content.Context;
import android.util.Log;

import com.example.gym.api.ApiClient;
import com.example.gym.api.ApiService;
import com.example.gym.database.DatabaseHelper;
import com.example.gym.models.Ejercicio;
import com.example.gym.models.Rol;
import com.example.gym.models.Rutina;
import com.example.gym.models.RutinaEjercicio;
import com.example.gym.models.RutinaResponse;
import com.example.gym.models.Usuario;
import com.example.gym.utils.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GymRepository {
    private static final String TAG = "GymRepository";
    private Context context;
    private DatabaseHelper databaseHelper;
    private ApiService apiService;

    public GymRepository(Context context) {
        this.context = context;
        this.databaseHelper = new DatabaseHelper(context);
        this.apiService = ApiClient.getApiService();
    }

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }

    // Métodos para Usuario
    public void getUsuarios(DataCallback<List<Usuario>> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            // Online: obtener de API y guardar en SQLite
            apiService.getUsuarios().enqueue(new Callback<List<Usuario>>() {
                @Override
                public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Usuario> usuarios = response.body();
                        // Guardar en SQLite
                        databaseHelper.deleteAllUsuarios();
                        for (Usuario usuario : usuarios) {
                            databaseHelper.insertUsuario(usuario);
                        }
                        callback.onSuccess(usuarios);
                        Log.d(TAG, "Usuarios obtenidos de API y guardados en SQLite");
                    } else {
                        // Si falla la API, intentar desde SQLite
                        List<Usuario> usuarios = databaseHelper.getAllUsuarios();
                        if (!usuarios.isEmpty()) {
                            callback.onSuccess(usuarios);
                            Log.d(TAG, "API falló, usando datos de SQLite");
                        } else {
                            callback.onError("Error al obtener usuarios");
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Usuario>> call, Throwable t) {
                    // Si falla la conexión, usar SQLite
                    List<Usuario> usuarios = databaseHelper.getAllUsuarios();
                    if (!usuarios.isEmpty()) {
                        callback.onSuccess(usuarios);
                        Log.d(TAG, "Conexión falló, usando datos de SQLite");
                    } else {
                        callback.onError("Error de conexión: " + t.getMessage());
                    }
                }
            });
        } else {
            // Offline: obtener de SQLite
            List<Usuario> usuarios = databaseHelper.getAllUsuarios();
            callback.onSuccess(usuarios);
            Log.d(TAG, "Sin conexión, usando datos de SQLite");
        }
    }
    public void createUsuario(Usuario usuario, final DataCallback<Boolean> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.createUsuario(usuario).enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(true);
                    } else {
                        callback.onError("Error al crear: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    callback.onError("Fallo conexión: " + t.getMessage());
                }
            });
        } else {
            callback.onError("No hay conexión a internet");
        }
    }

    // Actualizar Usuario
    public void updateUsuario(int id, Usuario usuario, final DataCallback<Boolean> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.updateUsuario(id, usuario).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(true);
                    } else {
                        callback.onError("Error al actualizar: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    callback.onError("Fallo conexión: " + t.getMessage());
                }
            });
        } else {
            callback.onError("No hay conexión");
        }
    }

    // Eliminar Usuario
    public void deleteUsuario(int id, final DataCallback<Boolean> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.deleteUsuario(id).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        callback.onSuccess(true);
                    } else {
                        callback.onError("Error al eliminar: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    callback.onError("Fallo conexión: " + t.getMessage());
                }
            });
        } else {
            callback.onError("No hay conexión");
        }
    }

    // Métodos para Ejercicio
    public void getEjercicios(DataCallback<List<Ejercicio>> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.getEjercicios().enqueue(new Callback<List<Ejercicio>>() {
                @Override
                public void onResponse(Call<List<Ejercicio>> call, Response<List<Ejercicio>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Ejercicio> ejercicios = response.body();
                        databaseHelper.deleteAllEjercicios();
                        for (Ejercicio ejercicio : ejercicios) {
                            databaseHelper.insertEjercicio(ejercicio);
                        }
                        callback.onSuccess(ejercicios);
                        Log.d(TAG, "Ejercicios obtenidos de API y guardados en SQLite");
                    } else {
                        List<Ejercicio> ejercicios = databaseHelper.getAllEjercicios();
                        if (!ejercicios.isEmpty()) {
                            callback.onSuccess(ejercicios);
                        } else {
                            callback.onError("Error al obtener ejercicios");
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Ejercicio>> call, Throwable t) {
                    List<Ejercicio> ejercicios = databaseHelper.getAllEjercicios();
                    if (!ejercicios.isEmpty()) {
                        callback.onSuccess(ejercicios);
                    } else {
                        callback.onError("Error de conexión: " + t.getMessage());
                    }
                }
            });
        } else {
            List<Ejercicio> ejercicios = databaseHelper.getAllEjercicios();
            callback.onSuccess(ejercicios);
            Log.d(TAG, "Sin conexión, usando datos de SQLite para ejercicios");
        }
    }
    public void createEjercicio(Ejercicio ejercicio, final DataCallback<Boolean> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.createEjercicio(ejercicio).enqueue(new Callback<Ejercicio>() {
                @Override
                public void onResponse(Call<Ejercicio> call, Response<Ejercicio> response) {
                    if (response.isSuccessful()) callback.onSuccess(true);
                    else callback.onError("Error al crear: " + response.code());
                }
                @Override
                public void onFailure(Call<Ejercicio> call, Throwable t) {
                    callback.onError("Fallo conexión: " + t.getMessage());
                }
            });
        } else {
            callback.onError("Se requiere internet para crear");
        }
    }

    public void updateEjercicio(int id, Ejercicio ejercicio, final DataCallback<Boolean> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.updateEjercicio(id, ejercicio).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) callback.onSuccess(true);
                    else callback.onError("Error al actualizar");
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    callback.onError("Fallo conexión");
                }
            });
        } else {
            callback.onError("Se requiere internet para editar");
        }
    }

    public void deleteEjercicio(int id, final DataCallback<Boolean> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.deleteEjercicio(id).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) callback.onSuccess(true);
                    else callback.onError("Error al eliminar");
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    callback.onError("Fallo conexión");
                }
            });
        } else {
            callback.onError("Se requiere internet para eliminar");
        }
    }

    // Métodos para Rutina
    public void getRutinas(DataCallback<List<Rutina>> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.getRutinas().enqueue(new Callback<List<Rutina>>() {
                @Override
                public void onResponse(Call<List<Rutina>> call, Response<List<Rutina>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Rutina> rutinas = response.body();
                        databaseHelper.deleteAllRutinas();
                        databaseHelper.deleteAllRutinaEjercicios();
                        for (Rutina rutina : rutinas) {
                            databaseHelper.insertRutina(rutina);
                        }
                        callback.onSuccess(rutinas);
                        Log.d(TAG, "Rutinas obtenidas de API y guardadas en SQLite");
                    } else {
                        List<Rutina> rutinas = databaseHelper.getAllRutinas();
                        if (!rutinas.isEmpty()) {
                            callback.onSuccess(rutinas);
                        } else {
                            callback.onError("Error al obtener rutinas");
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<Rutina>> call, Throwable t) {
                    List<Rutina> rutinas = databaseHelper.getAllRutinas();
                    if (!rutinas.isEmpty()) {
                        callback.onSuccess(rutinas);
                    } else {
                        callback.onError("Error de conexión: " + t.getMessage());
                    }
                }
            });
        } else {
            List<Rutina> rutinas = databaseHelper.getAllRutinas();
            callback.onSuccess(rutinas);
            Log.d(TAG, "Sin conexión, usando datos de SQLite para rutinas");
        }
    }

    // Método para obtener ejercicios de una rutina específica
    public void getRutinaEjercicios(int rutinaId, DataCallback<RutinaResponse> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.getRutinaEjercicios(rutinaId).enqueue(new Callback<RutinaResponse>() {
                @Override
                public void onResponse(Call<RutinaResponse> call, Response<RutinaResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        RutinaResponse rutinaResponse = response.body();
                        // Guardar ejercicios en SQLite
                        if (rutinaResponse.getEjercicios() != null) {
                            for (Ejercicio ejercicio : rutinaResponse.getEjercicios()) {
                                databaseHelper.insertEjercicio(ejercicio);
                                RutinaEjercicio rutinaEjercicio = new RutinaEjercicio(
                                        rutinaResponse.getRutinaId(),
                                        ejercicio.getEjercicioId()
                                );
                                databaseHelper.insertRutinaEjercicio(rutinaEjercicio);
                            }
                        }
                        callback.onSuccess(rutinaResponse);
                        Log.d(TAG, "RutinaEjercicios obtenidos de API");
                    } else {
                        // Intentar desde SQLite
                        List<Rutina> rutinas = databaseHelper.getAllRutinas();
                        Rutina rutina = null;
                        for (Rutina r : rutinas) {
                            if (r.getRutinaId() == rutinaId) {
                                rutina = r;
                                break;
                            }
                        }
                        if (rutina != null) {
                            RutinaResponse rutinaResponse = new RutinaResponse();
                            rutinaResponse.setRutinaId(rutina.getRutinaId());
                            rutinaResponse.setRutinaNombre(rutina.getRutinaNombre());
                            rutinaResponse.setEjercicios(rutina.getEjercicios());
                            callback.onSuccess(rutinaResponse);
                        } else {
                            callback.onError("Error al obtener ejercicios de la rutina");
                        }
                    }
                }

                @Override
                public void onFailure(Call<RutinaResponse> call, Throwable t) {
                    List<Rutina> rutinas = databaseHelper.getAllRutinas();
                    Rutina rutina = null;
                    for (Rutina r : rutinas) {
                        if (r.getRutinaId() == rutinaId) {
                            rutina = r;
                            break;
                        }
                    }
                    if (rutina != null) {
                        RutinaResponse rutinaResponse = new RutinaResponse();
                        rutinaResponse.setRutinaId(rutina.getRutinaId());
                        rutinaResponse.setRutinaNombre(rutina.getRutinaNombre());
                        rutinaResponse.setEjercicios(rutina.getEjercicios());
                        callback.onSuccess(rutinaResponse);
                    } else {
                        callback.onError("Error de conexión: " + t.getMessage());
                    }
                }
            });
        } else {
            // Offline: obtener de SQLite
            List<Rutina> rutinas = databaseHelper.getAllRutinas();
            Rutina rutina = null;
            for (Rutina r : rutinas) {
                if (r.getRutinaId() == rutinaId) {
                    rutina = r;
                    break;
                }
            }
            if (rutina != null) {
                RutinaResponse rutinaResponse = new RutinaResponse();
                rutinaResponse.setRutinaId(rutina.getRutinaId());
                rutinaResponse.setRutinaNombre(rutina.getRutinaNombre());
                rutinaResponse.setEjercicios(rutina.getEjercicios());
                callback.onSuccess(rutinaResponse);
            } else {
                callback.onError("Rutina no encontrada");
            }
            Log.d(TAG, "Sin conexión, usando datos de SQLite para RutinaEjercicios");
        }
    }
}

