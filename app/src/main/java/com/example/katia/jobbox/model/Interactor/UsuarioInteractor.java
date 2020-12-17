package com.example.katia.jobbox.model.Interactor;

import android.util.Log;

import com.example.katia.jobbox.interfaces.UsuarioAPI;
import com.example.katia.jobbox.model.Empleo;
import com.example.katia.jobbox.model.RetrofitSingleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class UsuarioInteractor {

    //Función para registrar a un usuario
    //Entradas: objeto usuario
    //SALIDAS: array con resultados del servidor
    public void registerVacancy(Empleo empleo) {

        Retrofit retroFit = RetrofitSingleton.getInstance();
        UsuarioAPI usuarioAPI = retroFit.create(UsuarioAPI.class);
        Call<Integer> call = usuarioAPI.registerVacancy(empleo);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful()) {
                    Log.i("SUCCESS", "onResponse: " + response);
                }else{
                    Log.e("ERROR", "onFailure: " + response);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.e("ERROR", "onFailure: " + t.getMessage());
            }
        });
    }
}
