package com.example.katia.jobbox.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.katia.jobbox.activity.MainActivity.URL_SERVER;

public class RetrofitSingleton<T> {

    public static Retrofit retrofit;
    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static Retrofit getInstance(){
        if(retrofit == null){
            retrofit = new Retrofit.Builder().baseUrl(URL_SERVER).addConverterFactory(GsonConverterFactory.create(gson)).build();
            Retrofit retrofit = new Retrofit.Builder().baseUrl(URL_SERVER).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
