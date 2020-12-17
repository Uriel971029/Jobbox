package com.example.katia.jobbox.interfaces;

import com.example.katia.jobbox.model.EmpleoModel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TrabajadorAPI {

    final String API_ROUTE = "Controlador/controlador_trabajadores.php";
    final String API_ROUTE_OFICIOS = "Controlador/controlador_oficios.php";

    @GET(API_ROUTE_OFICIOS)
    Call<ArrayList<EmpleoModel>> getOficios();
}
