package com.example.gym.api;

import com.example.gym.models.Usuario;
import com.example.gym.models.Ejercicio;
import com.example.gym.models.Rutina;
import com.example.gym.models.RutinaResponse;
import com.example.gym.models.RutinaEjercicio;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query; // Importante importar Query

public interface ApiService {
    String BASE_URL = "http://demomoviluiyi.somee.com/api/v1/";

    // --- USUARIOS ---
    @GET("usuario")
    Call<List<Usuario>> getUsuarios();

    @POST("usuario")
    Call<Usuario> createUsuario(@Body Usuario usuario);

    @PUT("usuario/{id}")
    Call<Void> updateUsuario(@Path("id") int id, @Body Usuario usuario);

    @DELETE("usuario/{id}")
    Call<Void> deleteUsuario(@Path("id") int id);

    // --- RUTINAS ---
    @GET("rutina")
    Call<List<Rutina>> getRutinas();

    @POST("rutina")
    Call<Rutina> createRutina(@Body Rutina rutina);

    @PUT("rutina/{id}")
    Call<Void> updateRutina(@Path("id") int id, @Body Rutina rutina);

    @DELETE("rutina/{id}")
    Call<Void> deleteRutina(@Path("id") int id);

    // --- EJERCICIOS ---
    @GET("ejercicio")
    Call<List<Ejercicio>> getEjercicios();

    @POST("ejercicio")
    Call<Ejercicio> createEjercicio(@Body Ejercicio ejercicio);

    @PUT("ejercicio/{id}")
    Call<Void> updateEjercicio(@Path("id") int id, @Body Ejercicio ejercicio);

    @DELETE("ejercicio/{id}")
    Call<Void> deleteEjercicio(@Path("id") int id);

    @GET("RutinaEjercicio/Rutina/{rutinaId}")
    Call<RutinaResponse> getRutinaEjercicios(@Path("rutinaId") int rutinaId);

    @POST("RutinaEjercicio")
    Call<Void> addRutinaEjercicio(@Body RutinaEjercicio relacion);

    @DELETE("RutinaEjercicio")
    Call<Void> deleteRutinaEjercicio(@Query("rutinaId") int rutinaId, @Query("ejercicioId") int ejercicioId);
}