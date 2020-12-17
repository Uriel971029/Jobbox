package com.example.katia.jobbox.controller;

import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import com.example.katia.jobbox.Presentador.AuthPresentador;
import com.example.katia.jobbox.interfaces.UsuarioAPI;
import com.example.katia.jobbox.model.Direccion;
import com.example.katia.jobbox.model.Empleo;
import com.example.katia.jobbox.model.RetrofitSingleton;
import com.example.katia.jobbox.model.Trabajador;
import com.example.katia.jobbox.model.Usuario;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.katia.jobbox.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Register extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    //ImageView photoP;
    BottomNavigationView bottomNavigationView;
    EditText edtName, edtCel, edtEmail, edtExp, edtPass;
    Spinner spnRol, spnO;
    private Group form_worker, form_address;
    ArrayAdapter<String> adapterRol, adapterO;
    public ArrayList<String> listaRol, listaO;
    private Empleo empleo;
    private Direccion direccion;
    private Trabajador trabajador;
    private Usuario usuario;
    //ArrayList<String> list = new ArrayList<>();

    private int rol;
    private String index;
    private boolean USE_LOCATION = false;
    AuthPresentador presentador;

    //protected ArrayList<String> responseListener = new ArrayList<>();
    //private FirebaseAuth mAuth;
    //private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registro);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        edtName = (EditText) findViewById(R.id.edtNombre);
        edtCel = (EditText) findViewById(R.id.edtTel);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtExp = (EditText) findViewById(R.id.edtExp);
        edtPass = (EditText) findViewById(R.id.edtPass);
        spnRol = (Spinner) findViewById(R.id.spnRol);
        spnO = (Spinner) findViewById(R.id.spnOficio);
        form_worker = (Group) findViewById(R.id.data_worker_group);
        form_address = (Group) findViewById(R.id.groupAddress);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        init();

        spnRol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //index = String.valueOf((position + 1));
                //getColonias(index);
                rol = position + 1;
                if (rol == 1) {
                    form_worker.setVisibility(View.GONE);
                } else {
                    presentador = new AuthPresentador(Register.this, adapterO, spnO, listaO);
                    presentador.initFormWorker();
                    form_worker.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    //Función que inicializa los componentes a utilizar en la interfaz en la que estamos según sea el rol seleccionado por el usuario
    public void init() {
        listaRol = new ArrayList<>(Arrays.asList("Cliente", "Trabajador"));
        presentador = new AuthPresentador(this, adapterRol, spnRol, listaRol);
        getSupportActionBar().setTitle("Registro");
        presentador.setList("roles");
    }

    /*public LatLng getLocation(String param) {

        //BUSCAR COORDENADAS GEO.(LATITUD Y LONGITUD) DE LA DIRECCION
        Geocoder geo = new Geocoder(this, Locale.getDefault());
        int maxResultados = 1;
        List<Address> adress = null;
        try {
            adress = geo.getFromLocationName(param, maxResultados);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng latLng = new LatLng(adress.get(0).getLatitude(), adress.get(0).getLongitude());

        return latLng;
    }*/


    //Definición: Función para enviar los datos recopilados del formulario del módulo de registro
    //Entradas: Ninguna
    //Salidas: Ninguna
    public void register() {

        final String nombre;
        final String apeP;
        final String apeM;
        final String password;
        final String correo;
        final String tel;
        final String municipio;
        //String colonia;
        final String oficio;
        final String exp;
        //ArrayList<String> data = new ArrayList<>();
        //LatLng latLng;
        StringTokenizer fullName = new StringTokenizer(edtName.getText().toString());

        if (validar()) {

            if (fullName.countTokens() > 2) {
                nombre = fullName.nextToken();
                apeP = fullName.nextToken();
                apeM = fullName.nextToken();
            } else {
                nombre = fullName.nextToken();
                apeP = fullName.nextToken();
                apeM = "";
            }

            password = edtPass.getText().toString();

            //recuperamos de las preferencias de usuario el token de FireBase para el envio de notificaciones
            SharedPreferences preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
            String tokeN = preferences.getString("tokenNotification", "");

            /*if (USE_LOCATION) {
                //USAMOS LA UBICACIÓN DEL USUARIO
                latLng = new LatLng(Double.parseDouble(preferences.getString("lat", null)),
                        Double.parseDouble(preferences.getString("long", null)));

            } else {
                String dir = "";
                dir = colonia + ", " + municipio + ", Mor.";
                //OBTENEMOS LAS COORDENADAS A PARTIR DE LA DIR
                latLng = getLocation(dir);
            }
            direccion.setLongitud(latLng.longitude);
            direccion.setLatitud(latLng.latitude);*/
            correo = edtEmail.getText().toString();
            tel = edtCel.getText().toString();
            //IDENTIFICAMOS EL TIPO DE USUARIO PARA SABER QUE OBJETO INSTANCIAR
            if (rol == 1) {
                usuario = new Usuario(nombre, apeP, apeM, correo, tel, tokeN, password, 3, this);
            } else {
                oficio = spnO.getSelectedItemPosition() + 1 + "";
                exp = edtExp.getText().toString();
                trabajador = new Trabajador(nombre, apeP, apeM, correo, tel, tokeN, password, 4, this, oficio, exp);
            }
            presentador.register(usuario != null ? usuario : trabajador);
        }
    }

    //FUNCIÓN PARA VALIDAR LOS CAMPOS DEL FORMULARIO DE REGISTRO, SALIDA: VERDERO O FALSO, SEGÚN SEA EL CASO
    public boolean validar() {


        edtName.setError(null);
        edtCel.setError(null);
        edtEmail.setError(null);
        //edtUser.setError(null);
        edtPass.setError(null);

        String name = edtName.getText().toString().trim();
        String cel = edtCel.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPass.getText().toString().trim();


        if (TextUtils.isEmpty(name)) {
            edtName.setError("Campo Obligatorio");
            edtName.requestFocus();
            return false;
        }

        if (!verify("onlyletters", name)) {
            edtName.setError("El nombre solo debe contener letras");
            edtName.requestFocus();
            return false;
        }


        if (!verify("full_name", name)) {
            edtName.setError("Escriba un nombre y un apellido por lo menos");
            edtName.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(cel)) {

            edtCel.setError("Campo Obligatorio");
            edtCel.requestFocus();
            return false;

        }

        if (!verify("onlydigits", cel)) {
            edtName.setError("El telefono solo debe contener números");
            edtName.requestFocus();
            return false;
        }

        if (!verify("cel", cel)) {

            edtCel.setError("El telefono debe contener 10 digitos");
            edtCel.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Campo Obligatorio");
            edtEmail.requestFocus();
            return false;
        }

        if (!verify("email", email)) {

            edtEmail.setError("Formato de correo inválido");
            edtEmail.requestFocus();
            return false;

        }

        if (TextUtils.isEmpty(password) || (password.length() < 8)) {
            edtPass.setError("La contraseña debe contener por lo menos 8 caractéres");
            edtPass.requestFocus();
            return false;

        } else if (!password.matches(".*[0-9].*") || !password.matches(".*[a-zA-Z].*")) {
            edtPass.setError("Utilice números y letras para una mayor seguridad");
            edtPass.requestFocus();
            return false;
        }

        if (form_worker.getVisibility() == View.VISIBLE) {
            edtExp.setError(null);
            String aniosExp = edtExp.getText().toString();

            if (TextUtils.isEmpty(aniosExp)) {
                edtExp.setError("Campo Obligatorio");
                edtExp.requestFocus();
                return false;
            }

            if (aniosExp.length() > 3) {
                edtExp.setError("Inserte una cantidad válida");
                edtExp.requestFocus();
                return false;
            }
        }

        return true;
    }

    //FUNCIÓN PARA VERIICAR EL FORMATO DE LOS CAMPOS: ENTRADA = Tipo y Variable, SALIDA: TRUE O FALSE SEGÚN SEA EL CASO
    private boolean verify(String opc, String var) {

        switch (opc) {

            case "onlyletters":

                if (var.matches(".*[0-9].*")) {

                    return false;
                }

                break;

            case "onlydigits":

                if (var.matches(".*[a-zA-Z].*")) {

                    return false;
                }

                break;

            case "full_name":

                if ((!var.contains(" "))) {
                    return false;
                }

                break;


            case "cel":

                if ((var.length() != 10)) {
                    return false;
                }

                break;


            case "email":

                if (!Patterns.EMAIL_ADDRESS.matcher(var).matches()) {

                    return false;
                }

                break;

        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.navigation_next:

                register();
                break;
        }

        return false;
    }


}
