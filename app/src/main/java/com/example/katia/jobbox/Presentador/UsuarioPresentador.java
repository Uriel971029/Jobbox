package com.example.katia.jobbox.Presentador;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.example.katia.jobbox.controller.Menu;
import com.example.katia.jobbox.model.Empleo;
import com.example.katia.jobbox.model.Interactor.UsuarioInteractor;
import com.example.katia.jobbox.model.Usuario;
import com.example.katia.jobbox.model.UsuarioModel;

public class UsuarioPresentador {
    UsuarioInteractor interactor;
    Activity activity;
    UsuarioModel usuario;

    public UsuarioPresentador(Activity activity) {
        interactor = new UsuarioInteractor();
        this.activity = activity;
        this.usuario = ((Menu) activity).getUsuario();
    }

    public boolean registerVacancy(Empleo empleo) {

        //sanemos los datos
        if (validar()) {
            //llamamos al modelo
            interactor.registerVacancy(empleo);
        } else {
            Toast.makeText(this.activity, "Por favor complete los campos", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    //FUNCIÓN PARA VALIDAR LOS CAMPOS DEL FORMULARIO DE REGISTRO, SALIDA: VERDERO O FALSO, SEGÚN SEA EL CASO
    public boolean validar() {

        EditText title = null, date;
        title.setError(null);

        String titulo = title.getText().toString().trim();

        if (TextUtils.isEmpty(titulo)) {
            title.setError("Campo Obligatorio");
            title.requestFocus();
            return false;
        }

        return true;
    }
}
