package com.example.gym.repository;

import android.content.Context;
import android.util.Log;

import com.example.gym.api.ApiClient;
import com.example.gym.api.ApiService;
import com.example.gym.database.DatabaseHelper;
import com.example.gym.models.Ejercicio;
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

    public void getUsuarios(DataCallback<List<Usuario>> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.getUsuarios().enqueue(new Callback<List<Usuario>>() {
                @Override
                public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Usuario> usuarios = response.body();
                        databaseHelper.deleteAllUsuarios();
                        for (Usuario usuario : usuarios) {
                            databaseHelper.insertUsuario(usuario);
                        }
                        callback.onSuccess(usuarios);
                    } else {
                        useLocalUsuarios(callback, "Error API: " + response.code());
                    }
                }
                @Override
                public void onFailure(Call<List<Usuario>> call, Throwable t) {
                    useLocalUsuarios(callback, "Fallo conexión: " + t.getMessage());
                }
            });
        } else {
            useLocalUsuarios(callback, "Modo Offline");
        }
    }

    private void useLocalUsuarios(DataCallback<List<Usuario>> callback, String errorMsg) {
        List<Usuario> localData = databaseHelper.getAllUsuarios();
        if (!localData.isEmpty()) {
            callback.onSuccess(localData);
            Log.d(TAG, "Usando SQLite Usuarios. Razón: " + errorMsg);
        } else {
            callback.onError(errorMsg);
        }
    }

    public void createUsuario(Usuario usuario, final DataCallback<Boolean> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.createUsuario(usuario).enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                    if (response.isSuccessful()) callback.onSuccess(true);
                    else callback.onError("Error al crear: " + response.code());
                }
                @Override
                public void onFailure(Call<Usuario> call, Throwable t) {
                    callback.onError("Fallo conexión: " + t.getMessage());
                }
            });
        } else {
            callback.onError("Se requiere internet");
        }
    }

    public void updateUsuario(int id, Usuario usuario, final DataCallback<Boolean> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.updateUsuario(id, usuario).enqueue(new Callback<Void>() {
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
            callback.onError("Se requiere internet");
        }
    }

    public void deleteUsuario(int id, final DataCallback<Boolean> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.deleteUsuario(id).enqueue(new Callback<Void>() {
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
            callback.onError("Se requiere internet");
        }
    }

    public void getEjercicios(DataCallback<List<Ejercicio>> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.getEjercicios().enqueue(new Callback<List<Ejercicio>>() {
                @Override
                public void onResponse(Call<List<Ejercicio>> call, Response<List<Ejercicio>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Ejercicio> ejercicios = response.body();
                        databaseHelper.deleteAllEjercicios();
                        for (Ejercicio e : ejercicios) {
                            databaseHelper.insertEjercicio(e);
                        }
                        callback.onSuccess(ejercicios);
                    } else {
                        useLocalEjercicios(callback, "Error API Ejercicios");
                    }
                }
                @Override
                public void onFailure(Call<List<Ejercicio>> call, Throwable t) {
                    useLocalEjercicios(callback, "Fallo conexión");
                }
            });
        } else {
            useLocalEjercicios(callback, "Modo Offline");
        }
    }

    private void useLocalEjercicios(DataCallback<List<Ejercicio>> callback, String errorMsg) {
        List<Ejercicio> localData = databaseHelper.getAllEjercicios();
        if (!localData.isEmpty()) callback.onSuccess(localData);
        else callback.onError(errorMsg);
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
            callback.onError("Se requiere internet");
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
            callback.onError("Se requiere internet");
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
            callback.onError("Se requiere internet");
        }
    }

    public void getRutinas(DataCallback<List<Rutina>> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.getRutinas().enqueue(new Callback<List<Rutina>>() {
                @Override
                public void onResponse(Call<List<Rutina>> call, Response<List<Rutina>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Rutina> rutinas = response.body();
                        databaseHelper.deleteAllRutinas();
                        for (Rutina r : rutinas) {
                            databaseHelper.insertRutina(r);
                        }
                        callback.onSuccess(rutinas);
                    } else {
                        useLocalRutinas(callback, "Error API Rutinas");
                    }
                }
                @Override
                public void onFailure(Call<List<Rutina>> call, Throwable t) {
                    useLocalRutinas(callback, "Fallo conexión");
                }
            });
        } else {
            useLocalRutinas(callback, "Modo Offline");
        }
    }

    private void useLocalRutinas(DataCallback<List<Rutina>> callback, String errorMsg) {
        List<Rutina> localData = databaseHelper.getAllRutinas();
        if (!localData.isEmpty()) callback.onSuccess(localData);
        else callback.onError(errorMsg);
    }

    public void createRutina(Rutina rutina, final DataCallback<Boolean> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.createRutina(rutina).enqueue(new Callback<Rutina>() {
                @Override
                public void onResponse(Call<Rutina> call, Response<Rutina> response) {
                    if (response.isSuccessful()) callback.onSuccess(true);
                    else callback.onError("Error al crear rutina: " + response.code());
                }
                @Override
                public void onFailure(Call<Rutina> call, Throwable t) {
                    callback.onError("Fallo conexión");
                }
            });
        } else {
            callback.onError("Requiere internet");
        }
    }

    public void updateRutina(int id, Rutina rutina, final DataCallback<Boolean> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.updateRutina(id, rutina).enqueue(new Callback<Void>() {
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
            callback.onError("Requiere internet");
        }
    }

    public void deleteRutina(int id, final DataCallback<Boolean> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.deleteRutina(id).enqueue(new Callback<Void>() {
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
            callback.onError("Requiere internet");
        }
    }

    public void getRutinaEjercicios(int rutinaId, DataCallback<RutinaResponse> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.getRutinaEjercicios(rutinaId).enqueue(new Callback<RutinaResponse>() {
                @Override
                public void onResponse(Call<RutinaResponse> call, Response<RutinaResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        RutinaResponse data = response.body();

                        if (data.getEjercicios() != null) {
                            for (Ejercicio e : data.getEjercicios()) {
                                RutinaEjercicio re = new RutinaEjercicio(data.getRutinaId(), e.getEjercicioId());
                                databaseHelper.insertRutinaEjercicio(re);
                            }
                        }
                        callback.onSuccess(data);
                    } else {
                        Rutina r = getLocalRutinaById(rutinaId);
                        if (r != null) {
                            RutinaResponse rr = new RutinaResponse();
                            rr.setRutinaId(r.getRutinaId());
                            rr.setRutinaNombre(r.getRutinaNombre());
                            rr.setEjercicios(r.getEjercicios());
                            callback.onSuccess(rr);
                        } else {
                            callback.onError("Error API y sin datos locales");
                        }
                    }
                }
                @Override
                public void onFailure(Call<RutinaResponse> call, Throwable t) {
                    // Fallback local
                    Rutina r = getLocalRutinaById(rutinaId);
                    if (r != null) {
                        RutinaResponse rr = new RutinaResponse();
                        rr.setRutinaId(r.getRutinaId());
                        rr.setRutinaNombre(r.getRutinaNombre());
                        rr.setEjercicios(r.getEjercicios());
                        callback.onSuccess(rr);
                    } else {
                        callback.onError("Fallo conexión: " + t.getMessage());
                    }
                }
            });
        } else {
            Rutina r = getLocalRutinaById(rutinaId);
            if (r != null) {
                RutinaResponse rr = new RutinaResponse();
                rr.setRutinaId(r.getRutinaId());
                rr.setRutinaNombre(r.getRutinaNombre());
                rr.setEjercicios(r.getEjercicios());
                callback.onSuccess(rr);
            } else {
                callback.onError("Sin conexión y sin datos locales");
            }
        }
    }

    private Rutina getLocalRutinaById(int rutinaId) {
        List<Rutina> rutinas = databaseHelper.getAllRutinas();
        for (Rutina r : rutinas) {
            if (r.getRutinaId() == rutinaId) return r;
        }
        return null;
    }

    public void addEjercicioToRutina(int rutinaId, int ejercicioId, final DataCallback<Boolean> callback) {
        RutinaEjercicio relacion = new RutinaEjercicio(rutinaId, ejercicioId);

        if (NetworkUtils.isNetworkAvailable(context)) {
            apiService.addRutinaEjercicio(relacion).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) callback.onSuccess(true);
                    else callback.onError("Error al vincular: " + response.code());
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    callback.onError("Fallo conexión: " + t.getMessage());
                }
            });
        } else {
            callback.onError("Requiere internet");
        }
    }

    // Eliminar ejercicio de rutina (Borrar relación)
    public void removeEjercicioFromRutina(int rutinaId, int ejercicioId, final DataCallback<Boolean> callback) {
        if (NetworkUtils.isNetworkAvailable(context)) {
            // Usamos Query params (Delete con URL)
            apiService.deleteRutinaEjercicio(rutinaId, ejercicioId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) callback.onSuccess(true);
                    else callback.onError("Error al eliminar vínculo");
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    callback.onError("Fallo conexión");
                }
            });
        } else {
            callback.onError("Requiere internet");
        }
    }
}