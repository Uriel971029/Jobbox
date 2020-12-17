package com.example.katia.jobbox.interfaces;

import com.example.katia.jobbox.model.Empleo;
import com.example.katia.jobbox.model.ResponseModel;
import com.example.katia.jobbox.model.Usuario;
import com.example.katia.jobbox.model.UsuarioModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UsuarioAPI {

    String API_ROUTE = "Controlador/controlador_usuarios.php";

    @POST(API_ROUTE)
    @FormUrlEncoded
    Call<UsuarioModel> getUsers(@Field("method") int opc, @Field("idUsuario") int id);

    @POST(API_ROUTE)
    Call<ResponseModel> registerUser(@Body UsuarioModel usuario);

    @POST(API_ROUTE)
    Call<Integer> registerVacancy(@Body Empleo empleo);
}
