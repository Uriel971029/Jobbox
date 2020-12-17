package com.example.katia.jobbox.controller;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.katia.jobbox.R;

public class SelectionRol extends AppCompatActivity implements View.OnClickListener {
    
    ImageView imgClient;
    ImageView imgWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_rol);
        
        imgClient = (ImageView) findViewById(R.id.imgC);
        imgWorker = (ImageView) findViewById(R.id.imgW);

        imgClient.setOnClickListener(this);
        imgWorker.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(this, Register.class);

        //OBTENEMOS EL ID DE LA IMAGEN QUE FUE PULSADA PARA DETERMINAR EL TIPO DE REGISTRO AL QUE IRÁ
        switch (v.getId()){

            case R.id.imgC:

                intent.putExtra("indicator", 1);
                break;

            case R.id.imgW:

                intent.putExtra("indicator", 2);
                break;

            default:
                intent.putExtra("indicator", 0);
                break;
        }

        startActivity(intent);
    }

}
