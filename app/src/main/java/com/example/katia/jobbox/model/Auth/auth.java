package com.example.katia.jobbox.model.Auth;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.katia.jobbox.activity.activity_fingerPrint;
import com.example.katia.jobbox.controller.Menu;
import com.example.katia.jobbox.interfaces.AuthAPI;
import com.example.katia.jobbox.model.RetrofitSingleton;
import com.example.katia.jobbox.model.Usuario;
import com.example.katia.jobbox.model.UsuarioModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.model.Connection;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.katia.jobbox.activity.MainActivity.URL_SERVER;

public class auth extends AppCompatActivity implements login.OnFragmentChangedListener, login.OnLoginRequiredListener,
        recuperarContrasenia.OnCodeSentListener, recuperarContrasenia.OnPasswordResetListener {

    //DIALOGO DE ESPERA
    private String response = "";
    FragmentManager fragmentManager;
    Connection connection;
    ArrayList<String> args = new ArrayList<>();
    private login fragment_login = null;
    private JSONObject jsonObject;
    private String correo;
    private FirebaseAuth mAuth;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        fragment_login = login.newInstance(null);
        mAuth = FirebaseAuth.getInstance();
        //ENVIAR A LA VISTA DEL LOGIN
        onFragmentChanged(fragment_login, "login");

    }


    public interface onLoadScreenListener {

        public void OnLoadScreen(String result);
    }


    //FUNCIÓN QUE HACE PETICIONES AL SERVIDOR, ENTRADA: lista de parámetros, SALIDA: respuesta del servidor en cadena
    private void makeRequest(ArrayList<String> params) {

        final String method = params.get(0);
        String FILE = "";
        if (!response.isEmpty()) {
            response = "";
        }

        if (isNetDisponible()) {

            if (isOnlineNet()) {

                connection = new Connection(this) {
                    @Override
                    public void OnLoadScreen(String result) {
                        try {

                            jsonObject = new JSONObject(result);
                            response = jsonObject.getString("success");
                            //LIMPIAR CARACTERES ASCII PRESENTES EN ALGUNAS CADENAS JSON
                            response = response.replaceAll("[^\\x00-\\x7F]", "");

                            if (response.equalsIgnoreCase("1")) {

                                if (method.equalsIgnoreCase("login")) {
                                    //LIMPIAMOS LOS CAMPOS DEL FORMULARIO
                                    login loginFragment = (login) getSupportFragmentManager().findFragmentByTag("login");
                                    loginFragment.limpiar();
                                    send(method, 1);
                                } else {
                                    send(method, 1);
                                }
                            } else if (response.equalsIgnoreCase("0")) {
                                send(method, 0);
                            } else {
                                send(method, -1);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                ArrayList<String> data = new ArrayList<>();

                if (method.equalsIgnoreCase("resetPassword")) {
                    FILE = params.get(1);
                    //LOS DATOS YA VIENEN ENCAPSULADOS EN param(2) CON UN toString
                    data.add(params.get(2));

                } else {
                    FILE = "Controlador/controlador_sesiones.php";
                    //PASAMOS TODOS LOS DATOS MENOS EL PRIMERO YA QUE SOLO NOS SIRVE PARA UBICAR EL TIPO DE METODO A USAR
                    if (params.size() > 1) {
                        for (int i = 1; i < params.size(); i++) {
                            data.add(params.get(i));
                        }
                    }
                }
                connection.execute(URL_SERVER + FILE, data.toString(), method);
            } else {
                Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error no red, revise su conexión", Toast.LENGTH_SHORT).show();

        }
    }


    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return false;
    }

    @Override
    public void onFragmentChanged(Fragment fragment, String tag) {

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (tag.equalsIgnoreCase("login")) {

            String key = fragment.getArguments().getString("message", null);
            if (key == null) {
                fragmentTransaction.add(R.id.contenedorAuth, fragment, tag);
            } else {
                fragmentManager.popBackStackImmediate();
                fragmentTransaction.replace(R.id.contenedorAuth, fragment, tag);
            }
        } else {
            fragmentTransaction.replace(R.id.contenedorAuth, fragment, tag).addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    //FUNCION PARA HACER EL ENVIO DEL CODIGO DE VERIFICACIÓN PARA RESSTABLECIMIENTO DE CONTRASEÑA
    @Override
    public void onCodeSent(String email) {

        if (!args.isEmpty()) {
            args.clear();
        }

        args.add("sendMail");
        args.add(email);
        makeRequest(args);
    }

    //FUNCIÓN PARA INICIAR SESIÓN, ENTRADA: username y password
    @Override
    public void onLoginRequired(String user, String pass) {

        this.correo = user;
        //no se debería guardar la contraseña pero se requiere por el fingerPrint
        this.password = pass;

        if (!args.isEmpty()) {
            args.clear();
        }

        args.add(user);
        args.add(pass);

        //Retrofit
        Retrofit retrofit = RetrofitSingleton.getInstance();
        AuthAPI auth = retrofit.create(AuthAPI.class);

        Call<UsuarioModel> call = auth.login(1, args.get(0), args.get(1));
        call.enqueue(new Callback<UsuarioModel>() {
            @Override
            public void onResponse(Call<UsuarioModel> call, Response<UsuarioModel> response) {
                if(response.isSuccessful()){
                    UsuarioModel usuario = (UsuarioModel) response.body();
                    //guardamos una copia local del usuario
                    UsuarioModel.setUsuario(usuario);
                    send("login", usuario.getVerified());
                }else{
                    Log.e("error", response.message());
                }
            }
            @Override
            public void onFailure(Call<UsuarioModel> call, Throwable t) {
                Log.e("error", t.getMessage());
            }
        });
    }

    @Override
    public void onPasswordReset(String email, String password, String code) {

        ArrayList<String> data = new ArrayList<>();

        if (!args.isEmpty()) {
            args.clear();
        }

        //NOMBRE DE LA TABLA
        data.add("users");
        //NOMBRE DE LOS CAMPOS
        data.add("password");
        //CONTRASENIA
        data.add(password);
        //CODIGO DE VERIFICACIÓN
        data.add(code);
        //EMAIL
        data.add(email);

        args.add("resetPassword");
        args.add("Controlador/controlador_usuarios.php");
        args.add(data.toString());

        //DEBEMOS ACTUALIZAR EN FIREBASE ANTES QUE EN LA PLATAFORMA
        updateFirebaseUser(password);

    }

    public void updateFirebaseUser(final String password) {

        //OBTENEMOS LOS DATOS DE ACCESO DEL USUARIO DE LAS SHAREDPREFERENCES ALMACENADAS
        SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        final String correo = prefs.getString("correo", null);
        final String contrasenia = prefs.getString("contrasenia", null);

        //INICIAMOS UNA SESIÓN EN FIREBASE PARA PODER USAR EL OBJETO USER
        mAuth.signInWithEmailAndPassword(correo, contrasenia)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            //Obtener las credenciales del usuario confirmando con los datos de acceso que van a ser reeemplazados
                            AuthCredential credential = EmailAuthProvider.getCredential(correo, contrasenia);
                            // Prompt the user to re-provide their sign-in credentials
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //HERE MUST BE THE BELOW CODE
                                        user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("success", "Password updated");
                                                    makeRequest(args);
                                                } else {
                                                    Log.d("error", "Error password not updated");
                                                }
                                            }
                                        });

                                    } else {
                                        Log.d("error", "Error auth failed");
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(auth.this, "Revisa tus datos", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void send(String method, int opc) {

        switch (method) {

            case "login":

                if (opc == 1) {

                    //DEBEMOS RESGUARDAR EL CORREO DEL USUARIO PARA FINES RELACIONADOS CON LA AUTENTICACIÓN CON HUELLA DESPUÉS
                    SharedPreferences preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("correo", correo);
                    //PENSAR EN COMO PROTEGER ESTE CAMPO
                    editor.putString("contrasenia", password);
                    editor.commit();
                    /*if (rol != 0) {
                        //Obtenemos un copia del objeto global de usuario que se inicializa con todos los datos enviados en un singleton
                        Usuario usuario = Usuario.getInstance(idUser, nombre, apePaterno, apeMaterno, correo, telefono, rol);
                        usuario.setImg(ruta_foto);
                    }*/
                    Intent intent = new Intent(this, Menu.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }

                break;


            case "sendMail":

                if (opc == 1) {

                    Toast.makeText(this, "Por favor revise su correo, se ha enviado un codigo de verificación para restablecer su contraseña", Toast.LENGTH_LONG).show();
                    recuperarContrasenia recuperarFragment = (recuperarContrasenia) getSupportFragmentManager().findFragmentByTag("recuperar_contrasenia");
                    recuperarFragment.limpiar(2);
                } else if (opc == 0) {
                    Toast.makeText(this, "No existe una cuenta asociada al correo ingresado", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "No existe una cuenta de trabajador o cliente asociada a este correo", Toast.LENGTH_LONG).show();
                }

                break;

            case "resetPassword":

                if (opc == 1) {

                    //ACTUALIZAR LA CONTRASEÑA EN FIREBASE
                    fragment_login = login.newInstance("success");

                } else {
                    fragment_login = login.newInstance("failure");
                }
                onFragmentChanged(fragment_login, "login");
                break;
        }
    }


    //Función para comprobar la conexión a internet, SALIDA: TRUE O FALSE
    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }

    //Función para comprobar la conexión a una red, SALIDA: TRUE O FALSE
    public Boolean isOnlineNet() {
        try {

            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");
            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

}
