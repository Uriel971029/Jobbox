package com.example.katia.jobbox.Presentador;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.controller.Register;
import com.example.katia.jobbox.model.Empleo;
import com.example.katia.jobbox.model.Usuario;
import com.example.katia.jobbox.model.UsuarioModel;

import java.util.ArrayList;

public class AuthPresentador {

    ArrayAdapter adapter;
    Spinner spinner;
    ArrayList list;
    Activity activity;
    //Others
    Empleo empleo;

    public AuthPresentador(Activity activity, ArrayAdapter adapter, Spinner spinner, ArrayList list) {
        this.activity = activity;
        this.adapter = adapter;
        this.spinner = spinner;
        this.list = list;
    }

    public void initFormWorker() {
        this.getList("oficios");
        this.setList("oficios");
    }

    public void getList(String list) {

        switch (list) {
            case "oficios":
                empleo = new Empleo(activity);
                this.list = empleo.getList();
                break;
        }
    }

    public void setList(String method) {

        switch (method) {

            case "oficios":
                adapter = new ArrayAdapter<String>(activity, R.layout.textview_spinner, list);
                adapter.setDropDownViewResource(R.layout.textview_spinner);
                empleo.setAdapter(adapter);
                spinner.setAdapter(this.adapter);
                break;

            case "roles":
                //MUNICIPIOS
                adapter = new ArrayAdapter<String>(activity, R.layout.textview_spinner, list);
                adapter.setDropDownViewResource(R.layout.textview_spinner);
                //direccion.setAdapterM(adapterRol);
                spinner.setAdapter(adapter);
        }
    }

    public void register(Usuario usuario) {
        UsuarioModel usuarioModel = new UsuarioModel();
        usuarioModel.setNombre(usuario.getNombre());
        usuarioModel.setApePaterno(usuario.getApePaterno());
        usuarioModel.setApeMaterno(usuario.getApeMaterno());
        usuarioModel.setRol(usuario.getRol());
        usuarioModel.setTelefono(usuario.getTelefono());
        usuarioModel.setIdNotification(usuario.getIdNotification());
        usuarioModel.setCorreo(usuario.getCorreo());
        usuarioModel.setPassword(usuario.getPassword());
        usuarioModel.setRuta_foto("");
        usuarioModel.setOpc(1);
        usuario.register(usuarioModel);
    }
}
