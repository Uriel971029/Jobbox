package com.example.katia.jobbox.model;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.activity.MainActivity;
import com.example.katia.jobbox.controller.Fragments.Fragment_account;
import com.example.katia.jobbox.controller.Menu;
import com.example.katia.jobbox.interfaces.UsuarioAPI;
import com.example.katia.jobbox.model.Auth.auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.katia.jobbox.activity.MainActivity.URL_SERVER;
//variables para la carga de imagenes al servidor
import static net.gotev.uploadservice.Placeholders.ELAPSED_TIME;
import static net.gotev.uploadservice.Placeholders.PROGRESS;
import static net.gotev.uploadservice.Placeholders.TOTAL_FILES;
import static net.gotev.uploadservice.Placeholders.UPLOADED_FILES;
import static net.gotev.uploadservice.Placeholders.UPLOAD_RATE;

public class Usuario {

    protected int id;
    protected String nombre;
    protected String apePaterno;
    protected String apeMaterno;
    protected String correo;
    protected Direccion direccion;
    protected String telefono;
    protected String idNotification;
    protected String password;
    protected int rol;
    protected String img;
    protected ArrayList<String> responseListener = new ArrayList<>();
    protected ArrayList<Contratacion> listUpdates = new ArrayList<>();
    protected MenuItem iconNotify;
    RecyclerView.Adapter adapterContrataciones;
    Button btnSolicitar;

    protected Activity activityActual;
    protected Activity activityMenu;


    protected static Usuario usuario;
    protected TextView txtStatus;

    ArrayList<String> params = new ArrayList<>();

    public Usuario(Activity activity) {

        this.activityActual = activity;
    }

    //CONSTRUCTOR PARA REGISTRO
    public Usuario(String nombre, String apePaterno, String apeMaterno, String correo, String telefono, String idNotification, String password, int rol, Activity activity) {
        this.nombre = nombre;
        this.apePaterno = apePaterno;
        this.apeMaterno = apeMaterno;
        this.correo = correo;
        //this.direccion = direccion;
        this.telefono = telefono;
        this.idNotification = idNotification;
        this.password = password;
        this.rol = rol;
        this.activityActual = activity;
    }

    //CONSTRUCTOR PARA INICIO DE SESIÓN
    private Usuario(int id, String nombre, String apePaterno, String apeMaterno, String correo, String telefono, int rol) {

        this.id = id;
        this.nombre = nombre;
        this.apePaterno = apePaterno;
        this.apeMaterno = apeMaterno;
        this.correo = correo;
        this.telefono = telefono;
        this.rol = rol;
    }

    public static Usuario getInstance(int id, String nombre, String apePaterno, String apeMaterno, String correo, String telefono, int rol) {

        if (usuario == null) {
            usuario = new Usuario(id, nombre, apePaterno, apeMaterno, correo, telefono, rol);
        }
        return usuario;
    }

    //CONSTRUCTOR DE MENU PARA RECUPERAR DATOS DEL USUARIO CORRESPONDIENTE A LA SESIÓN ACTIVA
    protected Usuario() {

    }

    public static Usuario getInstance() {

        if (usuario == null) {
            usuario = new Usuario();
        }
        return usuario;
    }

    public void destroy() {

        usuario = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public int getRol() {
        return rol;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public ArrayList<String> getStatus() {
        return responseListener;
    }

    public void setStatus(ArrayList<String> responseListener) {
        this.responseListener = responseListener;
    }

    public MenuItem getIconNotify() {
        return iconNotify;
    }

    public void setIconNotify(MenuItem iconNotify) {
        this.iconNotify = iconNotify;
    }

    public void setActivityActual(Activity activityActual) {
        this.activityActual = activityActual;
    }

    public Activity getActivityActual() {
        return activityActual;
    }
    public Activity getActivityMenu() {
        return activityMenu;
    }

    public void setActivityMenu(Activity activityMenu) {
        this.activityMenu = activityMenu;
    }

    public ArrayList<Contratacion> getListUpdates() {
        return listUpdates;
    }

    public void setTxtStatus(TextView txtStatus) {
        this.txtStatus = txtStatus;
    }

    public void setAdapterContrataciones(RecyclerView.Adapter adapterContrataciones) {
        this.adapterContrataciones = adapterContrataciones;
    }

    public String getApePaterno() {
        return apePaterno;
    }

    public void setApePaterno(String apePaterno) {
        this.apePaterno = apePaterno;
    }

    public String getApeMaterno() {
        return apeMaterno;
    }

    public void setApeMaterno(String apeMaterno) {
        this.apeMaterno = apeMaterno;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(String idNotification) {
        this.idNotification = idNotification;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBtnSolicitar(Button btnSolicitar) {
        this.btnSolicitar = btnSolicitar;
    }

    //Función para registrar a un usuario
    //Entradas: objeto usuario
    //SALIDAS: array con resultados del servidor
    public void register(UsuarioModel usuario) {

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }
        Retrofit retrofit = RetrofitSingleton.getInstance();
        UsuarioAPI usuarioAPI = retrofit.create(UsuarioAPI.class);
        Call<ResponseModel> call = usuarioAPI.registerUser(usuario);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if (response.isSuccessful()) {
                    Log.i("result", response.body().toString());
                    ResponseModel responseModel = response.body();
                    if(responseModel.isSuccess()){
                        String message = "Se enviará un correo de activación a su cuenta, por favor confirme su registro";
                        Intent intent =  new Intent(activityActual, MainActivity.class);
                        intent.putExtra("message", message);
                        activityActual.startActivity(intent);
                    }else{
                        Toast.makeText(activityActual, "Ocurrió un error al agregar el usuario", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("Error", response.body().toString());
                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });
    }

    //Función para registrar a un usuario
    //Entradas: objeto usuario
    //SALIDAS: array con resultados del servidor
    public ArrayList<String> registerVacancy(Empleo empleo) {

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }

        makeRequest("form_add_vacancy", empleo.toString());
        return responseListener;
    }

    //Función para registrar una contratación
    //Entradas: objeto usuario
    //SALIDAS: array con resultados del servidor
    public ArrayList<String> registerEngagement(Contratacion contratacion) {

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }
        makeRequest("add_engagement", contratacion.toString());
        return responseListener;
    }


    //Función para obtener actualizaciones de una contratacion
    //Entradas: nada
    //SALIDAS: array con resultados del servidor
    public ArrayList<String> getEngagementUpdates() {

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }


        makeRequest("get_engagementUpdates", this.id + "," + this.rol);
        return responseListener;
    }


    //Función para obtener actualizaciones de una contratacion
    //Entradas: nada
    //SALIDAS: array con resultados del servidor
    public ArrayList<String> payEngagement(String idContratacion) {

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }

        makeRequest("pay_engagement", idContratacion);
        return responseListener;
    }


    //Función para registrar una calificación a un usuario
    //Entradas: puntuación, comentarios, idEmirsor, idReceptor
    //SALIDAS: array con resultados del servidor
    public ArrayList<String> delayEngagement(String fields, String data) {

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }

        makeRequest("delay_engagement", fields + "," + data);
        return responseListener;
    }


    //Función para eliminar una contratación
    //Entradas: id de la contratación
    //SALIDAS: array con resultados del servidor
    public ArrayList<String> deleteEngagement(int idContratacion) {

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }

        makeRequest("delete_engagement", idContratacion + "");
        return responseListener;
    }


    //Función para actualizar la información del usuario
    //Entradas: nombre de la tabla, campos a modificar de la tabla, la información nueva y el correo del usuario a modificar
    //SALIDAS: array con resultados del servidor
    public ArrayList<String> updateData(String tabla, String campos, String newData, String correo) {

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }

        if (!params.isEmpty()) {
            params.clear();
        }

        params.add(tabla);
        params.add(campos);
        params.add(newData);
        params.add(correo);

        makeRequest("update_user", params.toString());
        return responseListener;
    }

    //Función para registrar los datos de una nueva foto de perfil
    //Entradas: nombre del archivo, ruta del arhivo y correo del usuario
    //SALIDAS: array con resultados del servidor
    public ArrayList<String> registerNewPhoto(String file_name, String file_path, String idUser) {

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }

        if (!params.isEmpty()) {
            params.clear();
        }

        params.add(file_name);
        params.add(file_path);
        params.add(idUser);

        makeRequest("registerNewPhoto", "");
        return responseListener;
    }


    //Función para registrar una calificación a un usuario
    //Entradas: puntuación, comentarios, idEmirsor, idReceptor
    //SALIDAS: array con resultados del servidor
    public ArrayList<String> rateUser(float estrellas, String comentarios, String idEmisor, String idContratacion) {

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }

        if (!params.isEmpty()) {
            params.clear();
        }

        params.add(estrellas + "");
        params.add(comentarios);
        params.add(idEmisor);
        params.add(idContratacion);

        makeRequest("registerNewRate", params.toString());
        return responseListener;
    }

    //Función para registrar una calificación a un usuario
    //Entradas: puntuación, comentarios, idEmirsor, idReceptor
    //SALIDAS: array con resultados del servidor
    public ArrayList<String> registerNewComment(String asunto, String coment, String idUsuario) {

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }


        if (!params.isEmpty()) {
            params.clear();
        }

        params.add(asunto);
        params.add(coment);
        params.add(idUsuario);

        makeRequest("registerNewComment", params.toString());
        return responseListener;
    }

    //Función para Iniciar sesión con la huella dactilar
    //Entradas: nada
    //SALIDAS: array con resultados del servidor
    public ArrayList<String> logInWithFingerprint() {

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }

        //OBTENEMOS LOS DATOS DE ACCESO DEL USUARIO DE LAS SHAREDPREFERENCES ALMACENADAS
        SharedPreferences prefs = activityActual.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        final String correo = prefs.getString("correo", null);
        final String contrasenia = prefs.getString("contrasenia", null);

        if (correo != null && contrasenia != null) {

            this.correo = correo;
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            //INICIAMOS SESIÓN EN FIREBASE PARA PERMITIRLE AL USUARIO UTILIZAR EL CHAT DESPUÉS
            mAuth.signInWithEmailAndPassword(correo, contrasenia)
                    .addOnCompleteListener(activityActual, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                makeRequest("login_fingerprint", correo + "," + contrasenia);
                            } else {
                                Toast.makeText(activityActual, "Revisa tus datos", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        } else {
            Toast.makeText(activityActual, "Inicie sesión con sus datos por lo menos un vez", Toast.LENGTH_LONG).show();
        }
        return responseListener;
    }


    //Función para cerrar la sesión y setear en null al objeto usuario actual
    //Entradas: nada
    //SALIDAS: array con resultados del servidor
    public ArrayList<String> logOut() {

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }
        makeRequest("logout", "");
        return responseListener;

    }


    //Función que realiza la petición HTTP y obtiene respuesta del servidor
    // ENTRADA : identificador y tipo de busqueda (por municipios o ciudades)
    //SALIDA : arreglo con respuesta del servidor
    public void makeRequest(final String typeRequest, final String data) {

        Connection connection;
        String url = "Controlador/controlador_usuarios.php";
        final String[] response = {""};
        final int[] opc = new int[1];
        final ArrayList<String> lista = new ArrayList<>();

        connection = new Connection(activityActual) {
            @Override
            public void OnLoadScreen(String result) {
                //verificar que la cedena no venga vacia o nula
                if (result != null && !result.isEmpty()) {

                //LIMPIAR CARACTERES ASCII PRESENTES EN ALGUNAS CADENAS JSON
                response[0] = result.replaceAll("[^\\x00-\\x7F]", "");

                if (!response[0].contains("La_sesion_expiro")) {

                    try {

                        if (typeRequest.equalsIgnoreCase("add")) {

                            JSONObject jsonObject = new JSONObject(response[0]);
                            lista.add(jsonObject.getString("success"));
                            lista.add(jsonObject.getString("user_status"));
                            lista.add(jsonObject.getString("address_status"));
                            opc[0] = 1;

                            if (rol == 4) {

                                lista.add(jsonObject.getString("job_status"));
                            }

                        } else if (typeRequest.equalsIgnoreCase("form_add_vacancy")) {
                            JSONObject jsonObject = new JSONObject(response[0]);
                            lista.add(jsonObject.getString("success"));
                            opc[0] = 2;
                        } else if (typeRequest.equalsIgnoreCase("add_engagement")) {

                            JSONObject jsonObject = new JSONObject(response[0]);
                            lista.add(jsonObject.getString("success"));
                            opc[0] = 3;

                        } else if (typeRequest.equalsIgnoreCase("get_engagementUpdates")) {

                            //Verificar si la respuesta no viene vacía
                            //if (!response[0].contains("{datos_contratacion:[]}")) {
                            JSONObject jsonObject = new JSONObject(response[0]);
                            if (jsonObject.getJSONArray("datos_contratacion").length() > 0) {
                                JSONArray contrataciones = jsonObject.getJSONArray("datos_contratacion");
                                //El nombre de los trabajadores o clientes (según el caso) que deben aparecer en la tarjetas de contrataciones
                                JSONArray nombres_participante = jsonObject.getJSONArray("nombres_participante");
                                //new JSONArray(result.replaceAll("[^\\x00-\\x7F]", ""));
                                //ArrayList<Contratacion> auxList = new ArrayList<>();
                                readFromJson(contrataciones, nombres_participante);
                            } else {
                                System.out.println("");
                            }
                            opc[0] = 4;
                            //}

                        } else if (typeRequest.equalsIgnoreCase("pay_engagement")) {

                            JSONObject jsonObject = new JSONObject(response[0]);
                            lista.add(jsonObject.getString("success"));
                            opc[0] = 5;

                        } else if (typeRequest.equalsIgnoreCase("update_user")) {

                            JSONObject jsonObject = new JSONObject(response[0]);
                            lista.add(jsonObject.getString("success"));
                            opc[0] = 6;

                        } else if (typeRequest.equalsIgnoreCase("registerNewPhoto")) {

                            JSONObject jsonObject = new JSONObject(response[0]);
                            lista.add(jsonObject.getString("INSERT_STATUS"));
                            opc[0] = 7;

                        } else if (typeRequest.equalsIgnoreCase("registerNewRate")) {

                            JSONObject jsonObject = new JSONObject(response[0]);
                            lista.add(jsonObject.getString("success"));
                            opc[0] = 8;

                        } else if (typeRequest.equalsIgnoreCase("registerNewComment")) {

                            JSONObject jsonObject = new JSONObject(response[0]);
                            lista.add(jsonObject.getString("success"));
                            opc[0] = 9;

                        } else if (typeRequest.equalsIgnoreCase("delay_engagement")) {

                            JSONObject jsonObject = new JSONObject(response[0]);
                            lista.add(jsonObject.getString("success"));
                            opc[0] = 10;

                        } else if (typeRequest.equalsIgnoreCase("delete_engagement")) {

                            JSONObject jsonObject = new JSONObject(response[0]);
                            lista.add(jsonObject.getString("success"));
                            opc[0] = 11;

                        } else if (typeRequest.equalsIgnoreCase("login_fingerprint")) {

                            JSONObject jsonObject = new JSONObject(response[0]);
                            //Eliminamos el objeto usuario que usamos como auxiliar
                            usuario.destroy();
                            //Leer los datos del GSON para inicializar el objeto usuario
                            boolean flag = readDataUser(jsonObject);
                            lista.add(flag + "");
                            opc[0] = 12;

                        } else if (typeRequest.equalsIgnoreCase("logout")) {

                            JSONObject jsonObject = new JSONObject(response[0]);
                            lista.add(jsonObject.getString("success"));
                            opc[0] = 13;
                        }

                        responseListener.addAll(lista);
                        if (sendMessage(opc[0], response[0])) {
                            if (opc[0] == 1) {
                                comeBack();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //Destruimos el objeto usuario y enviamos a la vista correspondiente
                    opc[0] = 13;
                    sendMessage(opc[0], response[0]);
                }
            }
            }
        };

        switch (typeRequest) {

            case "add":
                connection.execute(URL_SERVER + url, toString(), "register");
                break;

            case "form_add_vacancy":

                url = "Controlador/controlador_clientes.php";
                connection.execute(URL_SERVER + url, data, "form_add_vacancy");
                break;

            case "add_engagement":

                url = "Controlador/controlador_contrataciones.php";
                connection.execute(URL_SERVER + url, data, "add_engagement");
                break;

            case "get_engagementUpdates":

                url = "Controlador/controlador_contrataciones.php";
                connection.execute(URL_SERVER + url, data, "get_engagementUpdates");
                break;

            case "pay_engagement":

                url = "Controlador/controlador_contrataciones.php";
                connection.execute(URL_SERVER + url, data, "pay_engagement");
                break;

            case "delay_engagement":

                url = "Controlador/controlador_contrataciones.php";
                connection.execute(URL_SERVER + url, data, "delay_engagement");
                break;

            case "delete_engagement":

                url = "Controlador/controlador_contrataciones.php";
                connection.execute(URL_SERVER + url, data, "delete_engagement");
                break;

            case "update_user":
                connection.execute(URL_SERVER + url, data, "update_user");
                break;

            case "registerNewPhoto":

                String datos = params.get(0) + "," + params.get(2);
                connection.execute(URL_SERVER + url, datos, "upload_image");
                break;

            case "registerNewRate":

                datos = params.get(0) + "," + params.get(1) + "," + params.get(2) + "," + params.get(3);
                connection.execute(URL_SERVER + url, datos, "register_rate");
                break;


            case "registerNewComment":

                datos = params.get(0) + "," + params.get(1) + "," + params.get(2);
                connection.execute(URL_SERVER + url, datos, "register_comment");
                break;


            case "login_fingerprint":

                url = "Controlador/controlador_sesiones.php";
                connection.execute(URL_SERVER + url, data, "login");
                break;

            case "logout":

                url = "Controlador/controlador_sesiones.php";
                connection.execute(URL_SERVER + url, "logout");
                break;
        }
    }

    private boolean readDataUser(JSONObject jsonObject) {

        boolean flag = false;
        int rol = 0;
        int idUser = 0;
        String nombre = "";
        String apePaterno = "";
        String apeMaterno = "";
        String telefono = "";
        String ruta_foto = "";

        if (jsonObject != null) {
            try {

                rol = jsonObject.getInt("rol");
                idUser = jsonObject.getInt("id");
                nombre = jsonObject.getString("nombre");
                apePaterno = jsonObject.getString("apePaterno");
                apeMaterno = jsonObject.getString("apeMaterno");
                telefono = jsonObject.getString("telefono");
                ruta_foto = jsonObject.getString("ruta_foto");

                flag = true;

                Usuario usuario = Usuario.getInstance(idUser, nombre, apePaterno, apeMaterno, correo, telefono, rol);
                usuario.setImg(ruta_foto);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return flag;
    }

    //Función para leer los datos devueltos por el servidor en formato JSON
    //Entradas: JsonArray
    //Salida: Arreglo de tipo contratación
    public ArrayList<Contratacion> readFromJson(JSONArray contrataciones, JSONArray nombres_participante) {

        Contratacion contratacion;
        Gson gson = new Gson();
        ArrayList<String> list = new ArrayList<>();
        ArrayList<String> listNom = new ArrayList<>();

        for (int i = 0; i < contrataciones.length(); i++) {

            try {
                list.add(contrataciones.get(i).toString());
                listNom.add(nombres_participante.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!list.isEmpty()) {
            int cont = 0;
            for (String obj : list) {

                contratacion = gson.fromJson(obj, Contratacion.class);
                contratacion.setImg();
                contratacion.setEmpleoName();
                contratacion.setNombre_participante(listNom.get(cont));
                listUpdates.add(contratacion);

                cont++;
            }

        }

        return listUpdates;
    }


    public boolean sendMessage(int opc, String status) {

        int address_status = 0;
        int job_status = 0;
        boolean flag = true;

        switch (opc) {

            case 1:

                if (responseListener.get(0).equalsIgnoreCase("true") && responseListener.get(1).equalsIgnoreCase("1")) {

                    if (rol == 4) {
                        if (responseListener.get(2).equalsIgnoreCase("1")) {
                            address_status = 1;
                        } else {
                            Toast.makeText(activityActual, "Ocurrió un error al tratar de regitrar la dirección", Toast.LENGTH_LONG).show();
                        }
                        //oficio registrado
                        if (responseListener.get(3).equalsIgnoreCase("1")) {
                            job_status = 1;
                        } else {

                            Toast.makeText(activityActual, "Ocurrió un error al tratar de regitrar el oficio", Toast.LENGTH_LONG).show();
                        }

                        if (address_status == 1 && job_status == 1) {
                            Toast.makeText(activityActual, "Usuario registrado correctamente, pronto recibirá una notificación", Toast.LENGTH_LONG).show();
                            flag = true;
                        } else {
                            flag = false;
                        }
                    } else {
                        if (responseListener.get(2).equalsIgnoreCase("1")) {
                            Toast.makeText(activityActual, "Usuario registrado correctamente", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(activityActual, "Ocurrió un error al tratar de regitrar la dirección", Toast.LENGTH_LONG).show();
                        }
                    }

                    destroy();
                } else {
                    if (responseListener.get(1).equalsIgnoreCase("-1")) {

                        Toast.makeText(activityActual, "Usuario o correo repetido", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(activityActual, "Ocurrió un error al registrar", Toast.LENGTH_LONG).show();
                    }
                    flag = false;
                }

                break;

            case 2:

                if (responseListener.get(0).equalsIgnoreCase("true")) {

                    flag = true;
                    if (btnSolicitar != null) {
                        btnSolicitar.setEnabled(false);
                    }
                    Toast.makeText(activityActual, "Vacante registrada correctamente", Toast.LENGTH_SHORT).show();

                } else if (responseListener.get(0).equalsIgnoreCase("false")) {

                    flag = false;
                    Toast.makeText(activityActual, "Ocurrió un error al tratar de registrar la vacante", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(activityActual, "No puedes agendar en fechas pasadas",
                            Toast.LENGTH_LONG).show();
                }

                break;

            case 3:

                if (responseListener.get(0).equalsIgnoreCase("true")) {

                    flag = true;
                    Toast.makeText(activityActual, "Presupuesto enviado correctamente", Toast.LENGTH_SHORT).show();
                } else {

                    flag = false;
                    Toast.makeText(activityActual, "Ocurrió un error al tratar de registrar el presupuesto", Toast.LENGTH_SHORT).show();
                }

                break;

            case 4:
                if (!listUpdates.isEmpty()) {

                    iconNotify.setIcon(activityActual.getResources().getDrawable(R.drawable.ic_active_notification));
                    if (adapterContrataciones != null) {
                        adapterContrataciones.notifyDataSetChanged();
                    }
                }

                break;

            case 5:

                if (responseListener.get(0).equalsIgnoreCase("true")) {

                    flag = true;
                    Toast.makeText(activityActual, "Contratación registrada correctamente", Toast.LENGTH_SHORT).show();
                    txtStatus.setText("Estatus: " + "Pagado");

                } else {
                    flag = false;
                    Toast.makeText(activityActual, "Ocurrió un error al tratar de registrar la contratación", Toast.LENGTH_SHORT).show();
                }

                break;

            case 6:
                if (responseListener.get(0).equalsIgnoreCase("1")) {

                    flag = true;
                    Toast.makeText(activityActual, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();

                } else {

                    flag = false;
                    Toast.makeText(activityActual, "Ocurrió un error al tratar de actualizar los datos", Toast.LENGTH_SHORT).show();

                }
                break;

            case 7:

                //RESULTADO DE REGISTRO DE NUEVA FOTO DE PERFIL DE USUARIO
                if (responseListener.get(0).equalsIgnoreCase("true")) {

                    //Subimos la foto al servidor
                    subirFoto(params.get(0), params.get(1));

                } else {
                    Toast.makeText(activityActual, "Ocurrión un error al registrar la nueva foto", Toast.LENGTH_SHORT).show();
                }

                break;

            case 8:
                if (responseListener.get(0).equalsIgnoreCase("true")) {
                    Toast.makeText(activityActual, "Calificación enviada correctamente", Toast.LENGTH_SHORT).show();
                    //OCULTAR
                    /*trabajador.ratingBar.setRating(0);
                    trabajador.ratingBar.setVisibility(View.GONE);
                    trabajador.edtComentarios.setText("");
                    trabajador.edtComentarios.setVisibility(View.GONE);
                    trabajador.btnEnviar.setVisibility(View.GONE);
                    trabajador.btnCancelar.setVisibility(View.GONE);
                    //MOSTRAR
                    trabajador.btnFin.setVisibility(View.VISIBLE);
                    trabajador.btnFin.setText("RESUMEN");
                    trabajador.btnSubir.setVisibility(View.VISIBLE);
                    trabajador.btnSubir.setText("COMENTARIO");*/
                    //CORREGIMOS LA ALTURA DE LA TARJETA
                    /*int newHeight = getSizeScreen(activityActual, 0, 2);
                    trabajador.cardview.getLayoutParams().height = newHeight;*/

                } else if (responseListener.get(0).equalsIgnoreCase("false")) {
                    Toast.makeText(activityActual, "Ocurrión un error al registrar la calificación", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activityActual, "Ya ha registrado una calificación de este trabajo", Toast.LENGTH_SHORT).show();
                }
                break;

            case 9:

                if (responseListener.get(0).equalsIgnoreCase("true")) {
                    Toast.makeText(activityActual, "Comentario enviado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activityActual, "Ocurrión un error al enviar el mensaje", Toast.LENGTH_SHORT).show();
                }

                break;

            case 10:

                if (responseListener.get(0).equalsIgnoreCase("true")) {
                    Toast.makeText(activityActual, "Contratación reagendada correctamente", Toast.LENGTH_SHORT).show();
                } else if (responseListener.get(0).equalsIgnoreCase("false")) {
                    Toast.makeText(activityActual, "Ocurrión un error al intentar reagendar", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activityActual, "Para reagendar una contratación, debe hacerlo con 3 días de" +
                            "antelación", Toast.LENGTH_LONG).show();
                }

                break;


            case 11:

                if (responseListener.get(0).equalsIgnoreCase("true")) {
                    Toast.makeText(activityActual, "Contratación eliminada correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activityActual, "Ocurrión un error al intentar eliminar", Toast.LENGTH_SHORT).show();
                }

                break;

            case 12:

                if (responseListener.get(0).equalsIgnoreCase("true")) {

                    //PASAMOS A MENU  Y TERMINAMOS LA ACTIVIDAD DEL FINGERPRINT
                    Intent intent = new Intent(activityActual, Menu.class);
                    activityActual.startActivity(intent);
                    activityActual.finish();

                } else {
                    Toast.makeText(activityActual, "Error al intentar iniciar sesión con huella", Toast.LENGTH_SHORT).show();
                }

                break;

            case 13:

                FirebaseAuth.getInstance().signOut();

                if (status.contains("La_sesion_expiro")) {

                    flag = false;
                    activityActual.finish();
                    if (usuario != null) {
                        usuario.destroy();
                    }

                } else {

                    if (responseListener.get(0).equalsIgnoreCase("1")) {

                        flag = true;
                        activityActual.finish();
                        destroy();

                    } else {
                        Toast.makeText(activityActual, "Ocurrión un error al cerrar sesión", Toast.LENGTH_SHORT);
                    }
                }

                break;
        }

        return flag;
    }

    //FUNCIÓN PARA REGRESAR A LA INTERFAZ PRINCIPAL UNA VEZ REGISTRADO
    private void comeBack() {
        activityActual.finish();
        Intent intent = new Intent(activityActual, MainActivity.class);
        activityActual.startActivity(intent);
    }

    //Función para subir un archivo de imagen al servidor
    //Entradas: nombre del archivo, ruta del arhivo
    //SALIDAS: array con resultados del servidor

    public void subirFoto(String file_name, String file_path) {

        String URL_SUBIRPICTURE = URL_SERVER + "uploadPicture.php";

        try {
            //GENERAMOS UN ID ALEATORIO
            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(activityActual, uploadId, URL_SUBIRPICTURE)

                    .setMethod("POST")
                    .setUtf8Charset()
                    .addFileToUpload(file_path, "picture")
                    .addParameter("filename", file_name)
                    //Número de intentos a llevar a cabo en caso de un error durante la subida del archivo
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                            Toast.makeText(context, "Ocurrió un error al subir la imagen: " + exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

                            //String body = serverResponse.getBodyAsString();
                            Toast.makeText(context, "Imagen subida exitosamente", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                            Toast.makeText(context, "Cancelado", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .setNotificationConfig(getNotificationConfig(uploadId, R.string.image_upload))
                    .startUpload();

        } catch (Exception exc) {
            exc.printStackTrace();
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }

    }


    protected UploadNotificationConfig getNotificationConfig(final String uploadId, @StringRes int title) {
        UploadNotificationConfig config = new UploadNotificationConfig();

        PendingIntent clickIntent = PendingIntent.getActivity(
                activityActual, 1, new Intent(activityActual, Fragment_account.class), PendingIntent.FLAG_UPDATE_CURRENT);

        config.setTitleForAllStatuses(activityActual.getString(title))
                .setRingToneEnabled(true)
                .setClickIntentForAllStatuses(clickIntent)
                .setClearOnActionForAllStatuses(true);

        config.getProgress().message = "Subido " + UPLOADED_FILES + " de " + TOTAL_FILES
                + " at " + UPLOAD_RATE + " - " + PROGRESS;
        config.getProgress().iconResourceID = R.drawable.ic_upload;
        config.getProgress().iconColorResourceID = Color.BLUE;

        config.getCompleted().message = "La imagen se ha subido exitosamente en " + ELAPSED_TIME;
        config.getCompleted().iconResourceID = R.drawable.ic_upload_success;
        config.getCompleted().iconColorResourceID = Color.GREEN;

        config.getError().message = "Error mientras se subía";
        config.getError().iconResourceID = R.drawable.ic_upload_error;
        config.getError().iconColorResourceID = Color.RED;

        config.getCancelled().message = "La subida ha sido cancela";
        config.getCancelled().iconResourceID = R.drawable.ic_cancelled;
        config.getCancelled().iconColorResourceID = Color.YELLOW;

        return config;
    }

    public static int getSizeScreen(Activity activity, int cant, int opc) {

        float alturaDp;
        // calculo con altura natural de la vista en dp + lo que deseo agregar
        if (opc == 1) {
            alturaDp = 300 + cant;
        } else {
            alturaDp = 300 - cant;
        }
        // Obtener la densidad de pantalla
        final float escala = activity.getResources().getDisplayMetrics().density;
        // Convertir los dps a pixels, basado en la escala de densidad
        final int alturaPx = (int) (alturaDp * escala + 0.5f);
        return alturaPx;
    }

    @NonNull
    @Override
    public String toString() {

        if (this.rol == 3) {

            return nombre + "," + apePaterno + "," + apeMaterno + "," + password + "," + correo + "," + telefono + ","
                    + direccion.getColonia() + "," + direccion.getMunicipio() + "," + direccion.getLongitud()
                    + "," + direccion.getLatitud() + "," + idNotification + "," + rol;
        } else {
            return nombre + "," + apePaterno + "," + apeMaterno + "," + password + "," + correo + "," + telefono + ","
                    + direccion.getColonia() + "," + direccion.getMunicipio() + "," + direccion.getLongitud()
                    + "," + direccion.getLatitud() + "," + idNotification + "," + rol + "," + ((Trabajador) usuario).getOficio()
                    + "," + ((Trabajador) usuario).getAniosExp();
        }
    }
}
