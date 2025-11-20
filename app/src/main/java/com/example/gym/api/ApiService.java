package com.example.gym.api;

import com.example.gym.models.Usuario;
import com.example.gym.models.Ejercicio;
import com.example.gym.models.Rutina;
import com.example.gym.models.RutinaResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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

    // --- EJERCICIOS ---
    @GET("ejercicio")
    Call<List<Ejercicio>> getEjercicios();

    // Agrega esto en tu interfaz ApiService, debajo de los gets de ejercicio
    @POST("ejercicio")
    Call<Ejercicio> createEjercicio(@Body Ejercicio ejercicio);

    @PUT("ejercicio/{id}")
    Call<Void> updateEjercicio(@Path("id") int id, @Body Ejercicio ejercicio);

    @DELETE("ejercicio/{id}")
    Call<Void> deleteEjercicio(@Path("id") int id);

    @GET("RutinaEjercicio/Rutina/{rutinaId}")
    Call<RutinaResponse> getRutinaEjercicios(@Path("rutinaId") int rutinaId);


}