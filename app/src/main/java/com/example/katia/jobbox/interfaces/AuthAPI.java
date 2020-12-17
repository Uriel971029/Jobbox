package com.example.katia.jobbox.interfaces;

import com.example.katia.jobbox.model.UsuarioModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthAPI {

    final String API_ROUTE = "Controlador/controlador_sesiones.php";
    @POST(API_ROUTE)
    @FormUrlEncoded
    Call<UsuarioModel> login(@Field("method") int opc, @Field("user") String user, @Field("pass") String pass);
}
