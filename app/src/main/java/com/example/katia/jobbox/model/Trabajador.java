package com.example.katia.jobbox.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.activity.MainActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


import static com.example.katia.jobbox.activity.MainActivity.URL_SERVER;
import static net.gotev.uploadservice.Placeholders.ELAPSED_TIME;
import static net.gotev.uploadservice.Placeholders.PROGRESS;
import static net.gotev.uploadservice.Placeholders.TOTAL_FILES;
import static net.gotev.uploadservice.Placeholders.UPLOADED_FILES;
import static net.gotev.uploadservice.Placeholders.UPLOAD_RATE;

public class Trabajador extends Usuario {

    private String oficio;
    private String aniosExp;
    ArrayList<Empleo> listEmpleos = new ArrayList<>();
    ArrayList<Empleo> listSolicitudes = new ArrayList<>();
    ArrayList<Direccion> coordenadas = new ArrayList<>();
    private TextView txtResultados, txtNombre, txtServicios, txtTelefono, txtCorreo;
    private ImageView imgWorker;
    Button btnFin, btnSubir, btnEnviar, btnCancelar;
    RatingBar ratingBar;
    EditText edtComentarios;
    View cardview;

    private RecyclerView.Adapter adapterRecycler;
    private RecyclerView rv;

    //ELEMENTOS PARA LISTA DE SOLICITUDES DE CONTRATACION
    private RecyclerView.Adapter adapterSolicitudes;
    private RecyclerView rvSolicitudes;

    private GoogleMap mMap;
    private ArrayList<String> data = new ArrayList<>();

    int idOficio;
    int idCliente;
    int idVacante;
    String emailCliente;
    Direccion direccion;


    public Trabajador() {
    }

    public Trabajador(String nombre, String apellidoP, String apellidoM, String correo, String telefono, String idNotification, String password, int rol, Activity activity, String oficio, String aniosExp) {
        super(nombre, apellidoP, apellidoM, correo, telefono, idNotification, password, rol, activity);
        this.oficio = oficio;
        this.aniosExp = aniosExp;
    }

    public String getOficio() {
        return oficio;
    }

    public String getAniosExp() {
        return aniosExp;
    }

    public void setRv(RecyclerView rv) {
        this.rv = rv;
    }

    public void setmMap(GoogleMap mMap) {
        this.mMap = mMap;
    }

    public ArrayList<Empleo> getListSolicitudes() {
        return listSolicitudes;
    }

    public void setBtnFin(Button btnFin) {
        this.btnFin = btnFin;
    }

    public void setBtnSubir(Button btnSubir) {
        this.btnSubir = btnSubir;
    }

    public void setBtnEnviar(Button btnEnviar) {
        this.btnEnviar = btnEnviar;
    }

    public void setBtnCancelar(Button btnCancelar) {
        this.btnCancelar = btnCancelar;
    }

    public void setAdapterRecycler(RecyclerView.Adapter adapterRecycler) {
        this.adapterRecycler = adapterRecycler;
    }

    public void setCardview(View cardview) {
        this.cardview = cardview;
    }

    public void setRatingBar(RatingBar ratingBar) {
        this.ratingBar = ratingBar;
    }

    public void setEdtComentarios(EditText edtComentarios) {
        this.edtComentarios = edtComentarios;
    }

    //FUNCION EN LA QUE SE CONSULTAN LAS VACANTES DE EMPLEO EXISTENTES Y SE LES DA FORMATO PARA DEVOLVER UNA LISTA DE EMPLEOS
    public ArrayList<Empleo> getVacancies(int idTrabajador) {

        if (!listEmpleos.isEmpty()) {
            listEmpleos.clear();
        }

        if (!data.isEmpty()) {
            data.clear();
        }

        data.add(idTrabajador + "");

        makeRequest("read_vacancies", data);
        return listEmpleos;
    }

    //FUNCION EN LA QUE SE CONSULTAN LAS SOLICITUDES DE CONTRATACIÓN QUE TIENE UN DETERMINADO TRABAJADOR
    public ArrayList<Empleo> getHiringRequest(String idTrabajador) {

        if (!listSolicitudes.isEmpty()) {
            listSolicitudes.clear();
        }

        if (!data.isEmpty()) {
            data.clear();
        }

        data.add(idTrabajador);
        makeRequest("read_hiringRequest", data);
        return listSolicitudes;
    }


    public void getLocationWorkers(String localidad, LatLng location, String idOficio, TextView txtResultados) {

        if (!coordenadas.isEmpty()) {
            coordenadas.clear();
        }

        if (!data.isEmpty()) {
            data.clear();
        }

        this.txtResultados = txtResultados;

        data.add(localidad);
        data.add(idOficio);
        data.add(String.valueOf(location.latitude));
        data.add(String.valueOf(location.longitude));

        makeRequest("read_workersLocation", data);
    }


    public void getDataWorker(String idUsuario, TextView nombre, TextView servicios, TextView telefono, TextView correo, ImageView imgWorker, RatingBar ratingBar) {

        if (!data.isEmpty()) {
            data.clear();
        }

        this.txtNombre = nombre;
        this.txtServicios = servicios;
        this.txtTelefono = telefono;
        this.txtCorreo = correo;
        this.imgWorker = imgWorker;
        this.ratingBar = ratingBar;

        data.add(idUsuario);

        makeRequest("read_workerInformation", data);

    }


    public void registerNewJob(String aniosExp, String idOficio, String idUsuario) {

        if (!data.isEmpty()) {
            data.clear();
        }

        if (!responseListener.isEmpty()) {

            responseListener.clear();
        }

        data.add(aniosExp);
        data.add(idOficio);
        data.add(idUsuario);

        makeRequest("register_job", data);

    }


    public void uploadEvidence(String name_evidence, String path_evidence, String idContrato) {

        if (!data.isEmpty()) {
            data.clear();
        }

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }

        data.add(name_evidence);
        data.add(path_evidence);
        data.add(idContrato);

        subirEvidencia(name_evidence, path_evidence);

    }


    public void finish_engagement(String idContrato, Button btnFin, Button btnSubir, Button btnEnviar, Button btnCancelar, RatingBar ratingBar,
                                  EditText edtComentarios, View cardview) {

        if (!data.isEmpty()) {
            data.clear();
        }

        if (!responseListener.isEmpty()) {
            responseListener.clear();
        }

        this.btnFin = btnFin;
        this.btnSubir = btnSubir;
        this.btnEnviar = btnEnviar;
        this.btnCancelar = btnCancelar;
        this.ratingBar = ratingBar;
        this.edtComentarios = edtComentarios;
        this.cardview = cardview;

        data.add(idContrato);
        makeRequest("finish_engagement", data);

    }


    //FUNCION PARA REALIZAR LA PETICIÓN DE ELEMENTOS AL SERVIDOR PARA LLENAR LAS LISTAS DE DIRECCIONES EN EL FRMULARIO DE REGISTRO: ENTRADA = identificador y tipo de busqueda (por municipios o ciudades)
    public void makeRequest(final String typeRequest, ArrayList<String> data) {

        Connection connection;
        final String url = "Controlador/controlador_trabajadores.php";
        String localidad;
        final String[] response = {""};
        final int[] opc = new int[1];
        final Double[] lat = new Double[1];
        final Double[] lon = new Double[1];

        final ArrayList<String> lista = new ArrayList<>();

        connection = new Connection(activityActual) {
            @Override
            public void OnLoadScreen(String result) {

                //LIMPIAR CARACTERES ASCII PRESENTES EN ALGUNAS CADENAS JSON
                response[0] = result.replaceAll("[^\\x00-\\x7F]", "");

                if (!response[0].contains("La_sesion_expiro")) {
                    try {

                        if (typeRequest.contains("register_") || typeRequest.contains("upload_") || typeRequest.contains("finish_")) {

                            JSONObject jsonObject = new JSONObject(response[0]);
                            responseListener.add(jsonObject.getString("success"));

                        } else if (typeRequest.contains("read_")) {

                            ArrayList<String> auxList = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(response[0]);

                            //ENTREGAMOS TODO EL ARREGLO DE ARREGLOS
                            for (int i = 0; i < jsonArray.length(); i++) {
                                lista.add(jsonArray.getString(i));
                            }

                            for (int i = 0; i < lista.size(); i++) {

                                if (!auxList.isEmpty()) {
                                    auxList.clear();
                                }
                                //pasamos paquete por paquete de datos
                                auxList.addAll(Arrays.asList(lista.get(i).replace("\"", "").split(",")));
                                //le damos formato para poder pasar la información a un objeto de empleo
                                formatearCadena(auxList);

                                if (typeRequest.equalsIgnoreCase("read_vacancies")) {

                                    idOficio = Integer.parseInt(auxList.get(4));
                                    emailCliente = auxList.get(5);
                                    idCliente = Integer.parseInt(auxList.get(6));
                                    idVacante = Integer.parseInt(auxList.get(7));
                                    float rating = Float.valueOf(auxList.get(8));

                                    Empleo empleo = new Empleo(auxList.get(0), auxList.get(1), auxList.get(3), idOficio, idCliente, emailCliente, idVacante);
                                    empleo.setImg();
                                    empleo.setFecha(auxList.get(2));
                                    empleo.setRating_cliente(rating);

                                    listEmpleos.add(empleo);

                                } else if (typeRequest.equalsIgnoreCase("read_hiringRequest")) {

                                    idOficio = Integer.parseInt(auxList.get(4));
                                    emailCliente = auxList.get(5);
                                    idCliente = Integer.parseInt(auxList.get(6));
                                    idVacante = Integer.parseInt(auxList.get(7));
                                    float rating = Float.valueOf(auxList.get(8));

                                    Empleo empleo = new Empleo(auxList.get(0) + "", auxList.get(1) + "", auxList.get(3), idOficio, idCliente, emailCliente, idVacante);
                                    empleo.setImg();
                                    empleo.setFecha(auxList.get(2));
                                    empleo.setRating_cliente(rating);

                                    listSolicitudes.add(empleo);

                                } else if (typeRequest.equalsIgnoreCase("read_workersLocation")) {

                                    lat[0] = Double.parseDouble(auxList.get(0));
                                    lon[0] = Double.parseDouble(auxList.get(1));

                                    direccion = new Direccion(Integer.parseInt(auxList.get(2)), lat[0], lon[0]);
                                    coordenadas.add(direccion);

                                } else if (typeRequest.equalsIgnoreCase("read_workerInformation")) {

                                    //EL PRIMER ARREGLO CONTIENE LOS DATOS PERSONALES Y EL SEGUNDOS LOS OFICIOS
                                    if (i == 0) {

                                        txtNombre.setText(auxList.get(0) + " " + auxList.get(1) + " " + auxList.get(2));
                                        txtTelefono.setText(auxList.get(3));
                                        txtCorreo.setText(auxList.get(4));

                                        //URL DE LA FOTO DE PERFIL
                                    } else if (i == 1) {

                                        String url_foto = auxList.get(0);
                                        if (!url_foto.equalsIgnoreCase("false")) {
                                            Picasso.get().load(url_foto)
                                                    .fit()
                                                    .centerInside()
                                                    .into(imgWorker);
                                        }

                                        //OFICIOS
                                    } else if (i == 2) {

                                        for (int j = 0; j < auxList.size(); j++) {

                                            if (!auxList.get(j).equalsIgnoreCase("[")) {
                                                if (j > 0) {
                                                    txtServicios.setText(txtServicios.getText().toString() + ", " + auxList.get(j));
                                                }else{
                                                    txtServicios.setText(txtServicios.getText().toString() + auxList.get(j));
                                                }
                                            }
                                        }
                                        //RATING EN ESTRELLAS
                                    } else if (i == 3) {
                                        ratingBar.setRating(Float.valueOf(auxList.get(0)));
                                    }
                                }
                            }
                        }
                        send(typeRequest);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    send("La_sesion_expiro");
                }
            }
        }

        ;

        switch (typeRequest) {

            case "register_job":

                connection.execute(URL_SERVER + url, data.toString(), "register_job");

                break;

            case "read_vacancies":

                connection.execute(URL_SERVER + url, data.get(0), "get_vacancies");

                break;

            case "read_hiringRequest":

                connection.execute(URL_SERVER + url, data.get(0), "get_hiringRequest");

                break;


            case "read_workersLocation":

                connection.execute(URL_SERVER + url, data.get(0) + "," + data.get(1), "get_locations");

                break;


            case "read_workerInformation":

                connection.execute(URL_SERVER + url, data.get(0), "get_workerInfo");

                break;

            case "upload_evidence":

                connection.execute(URL_SERVER + url, data.get(0) + "," + data.get(2), "upload_evidence");

                break;

            case "finish_engagement":

                connection.execute(URL_SERVER + url, data.get(0), "finish_engagement");

                break;

        }

    }

    private void send(String typeRequest) {

        switch (typeRequest) {


            case "register_job":

                if (responseListener.get(0).equalsIgnoreCase("1")) {

                    Toast.makeText(activityActual, "Empleo registrado correctamente", Toast.LENGTH_SHORT).show();

                } else if (responseListener.get(0).equalsIgnoreCase("0")) {

                    Toast.makeText(activityActual, "Ocurrió un error al tratar de registrar el empleo", Toast.LENGTH_SHORT).show();
                } else {
                    //TRABAJO REPETIDO
                    Toast.makeText(activityActual, "Empleo ya registrado", Toast.LENGTH_SHORT).show();
                }

                break;

            case "read_vacancies":

                rv.setAdapter(adapterRecycler);

                break;

            case "read_hiringRequest":

                if (rvSolicitudes != null && adapterSolicitudes != null) {
                    rvSolicitudes.setAdapter(adapterSolicitudes);
                }
                if (!listSolicitudes.isEmpty()) {
                    usuario.getIconNotify().setIcon(activityActual.getResources().getDrawable(R.drawable.ic_active_notification));
                }else{
                    usuario.getIconNotify().setIcon(activityActual.getResources().getDrawable(R.drawable.ic_normal_notification));
                }

                break;

            case "read_workersLocation":

                //AGREGAMOS LOS MARCADORES DE LOS TRABAJADORES DISPONIBLES EN EL MAPA QUE COINCIDEN CON LOS FILTROS ESTABLECIDOS
                LatLng location = new LatLng(Double.parseDouble(data.get(2)), Double.parseDouble(data.get(3)));
                txtResultados.setText(activityActual.getResources().getString(R.string.label_resultados_encontrados) + " " + coordenadas.size() + " resultado(s)");
                //COORDENADAS CONTIENE TODAS LAS UBICACIONES DE LOS TRABAJADORES QUE APLICAN CON LOS FILTROS SELECCIONADOS
                if (!coordenadas.isEmpty()) {
                    //DATA CONTIENE LA INFORMACIÓN DE UBICACION DEL CLIENTE Y ES AHÍ DONDE VAMOS A CENTRAR LA CAMARA EN EL MAPA
                    for (int i = 0; i < coordenadas.size(); i++) {
                        LatLng latLng = new LatLng(coordenadas.get(i).getLatitud(), coordenadas.get(i).getLongitud());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Trabajador disponible #" + coordenadas.get(i).getIdTrabajador()));
                    }
                } else {
                    mMap.clear();
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14.3f));

                break;


            case "upload_evidence":

                if (responseListener.get(0).equalsIgnoreCase("true")) {

                    Toast.makeText(activityActual, "Evidencia registrada correctamente", Toast.LENGTH_SHORT).show();
                    //ACTUALIZAR STATUS DE CONTRATO
                    txtStatus.setText("Estatus: " + "Evidencia registrada");
                    //subirEvidencia(data.get(0), data.get(1));

                } else if (responseListener.get(0).equalsIgnoreCase("false")) {

                    Toast.makeText(activityActual, "Ocurrió un error al tratar de registrar la evidencia", Toast.LENGTH_SHORT).show();

                } else if (responseListener.get(0).equalsIgnoreCase("repeated")) {
                    //REPETIDO
                    Toast.makeText(activityActual, "Esta contratación ya tiene evidencia registrada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activityActual, "Aún no puedes subir una evidencia, espera la fecha del contrato",
                            Toast.LENGTH_LONG).show();
                }

                break;


            case "finish_engagement":

                if (responseListener.get(0).equalsIgnoreCase("1")) {

                    Toast.makeText(activityActual, "Trabajo finalizado correctamente", Toast.LENGTH_SHORT).show();
                    txtStatus.setText("Estatus: Terminado");

                    btnSubir = cardview.findViewById(R.id.btnSubirE);
                    btnSubir.setText("Calificar");
                    btnFin = cardview.findViewById(R.id.btnTerminar);
                    btnFin.setText("Resumen");
                    //OCULTAMOS
                    /*btnSubir.setVisibility(View.GONE);
                    btnFin.setVisibility(View.GONE);*/

                    //Mostramos el formulario para calificar usuario
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(activityActual);
                    View mView = activityActual.getLayoutInflater().inflate(R.layout.form_rate_user, null);

                    ratingBar = mView.findViewById(R.id.ratingBar);
                    edtComentarios = mView.findViewById(R.id.edtComentarios);
                    btnEnviar = mView.findViewById(R.id.btnEnviar);
                    btnCancelar = mView.findViewById(R.id.btnCancelar);

                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();


                    btnEnviar.setVisibility(View.VISIBLE);
                    btnEnviar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String idContrato = data.get(0);
                            String comentarios = edtComentarios.getText().toString();
                            if (ratingBar.getRating() != 0.0) {

                                if (comentarios.isEmpty()) {
                                    comentarios = "";
                                }
                                //ENVIAMOS CALIFICACIÓN DEL CLIENTE Y COMENTARIOS
                                usuario.rateUser(ratingBar.getRating(), comentarios, usuario.getId() + "", idContrato);
                            } else {
                                Toast.makeText(activityActual, "Por favor elija una calificación", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    btnCancelar.setVisibility(View.VISIBLE);
                    btnCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();
                        }
                    });

                    //ratingBar.setVisibility(View.VISIBLE);
                    //edtComentarios.setVisibility(View.VISIBLE);
                    //LE DAMOS UN RESIZE A LA VISTA DE LA TARJETA PARA QUE QUEPAN BIEN LOS ELEMENTOS
                    //int newHeight = getSizeScreen(activityActual, 100, 1);
                    //cardview.getLayoutParams().height = newHeight;

                } else if (responseListener.get(0).equalsIgnoreCase("0")) {
                    Toast.makeText(activityActual, "Primero debe subir evidencia del trabajo", Toast.LENGTH_SHORT).show();
                } else if (responseListener.get(0).equalsIgnoreCase("-1")) {
                    Toast.makeText(activityActual, "Ocurrió un error al tratar de cerrar la contratación", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activityActual, "Aún no se cumple la fecha del contrato", Toast.LENGTH_SHORT).show();
                }

                break;


            case "La_sesion_expiro":

                //CERRAMOS SESIÓN EN FIREBASE
                FirebaseAuth.getInstance().signOut();
                activityActual.finish();
                usuario.getActivityMenu().finish();
                if (usuario != null) {
                    usuario.destroy();
                }
                break;
        }
    }


    private void formatearCadena(ArrayList<String> lista) {

        ArrayList<String> newList = new ArrayList<>();
        int first = 0;
        int last = 0;

        for (int i = 0; i < lista.size(); i++) {

            lista.set(i, lista.get(i).replaceAll("[\\}\\]]", ""));

            first = lista.get(i).indexOf(":");
            last = lista.get(i).length();
            newList.add(i, lista.get(i).substring(first + 1, last));
        }

        lista.clear();
        lista.addAll(newList);
    }

    //Función para subir un archivo de imagen al servidor
    //Entradas: nombre del archivo, ruta del arhivo
    //SALIDAS: array con resultados del servidor

    public void subirEvidencia(String file_name, String file_path) {

        String URL_SUBIRPICTURE = URL_SERVER + "uploadFile.php";

        try {
            //GENERAMOS UN ID ALEATORIO
            final String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(activityActual, uploadId, URL_SUBIRPICTURE)

                    .setMethod("POST")
                    .setUtf8Charset()
                    .addFileToUpload(file_path, "file")
                    //La ruta del servidor ya viene con el nombre del archivo incluido
                    .addParameter("filename", file_name)
                    .setMaxRetries(2)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            if (uploadInfo.getTotalBytes() > 5000000) {
                                UploadService.stopUpload(uploadId);
                            }
                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                            Toast.makeText(context, "Ocurrió un error al subir el archivo", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

                            //Toast.makeText(context, "Archivo subido exitosamente", Toast.LENGTH_SHORT).show();
                            //Obtenemos la respuesta del servidor
                            String body = serverResponse.getBodyAsString();
                            if (body.contains("ERROR_UNKNOWN_FILE")) {
                                Toast.makeText(context, "Su archivo no es reconocido", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Su archivo ha sido subido exitosamente", Toast.LENGTH_SHORT).show();
                                makeRequest("upload_evidence", data);
                            }
                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                            if (uploadInfo.getTotalBytes() > 5000000) {
                                Toast.makeText(context, "Su archivo es demasiado grande", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Cancelado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNotificationConfig(getNotificationConfig(uploadId, R.string.file_upload))
                    .startUpload();

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    protected UploadNotificationConfig getNotificationConfig(final String uploadId, @StringRes int title) {
        UploadNotificationConfig config = new UploadNotificationConfig();

        //PendingIntent clickIntent = PendingIntent.getActivity(
        //this, 1, new Intent(this, Intent.), PendingIntent.FLAG_CANCEL_CURRENT);

        config.setTitleForAllStatuses(activityActual.getString(title))
                .setRingToneEnabled(true)
                //.setClickIntentForAllStatuses(clickIntent)
                .setClearOnActionForAllStatuses(true);

        config.getProgress().message = "Subido " + UPLOADED_FILES + " de " + TOTAL_FILES
                + " at " + UPLOAD_RATE + " - " + PROGRESS;
        config.getProgress().iconResourceID = R.drawable.ic_upload;
        config.getProgress().iconColorResourceID = Color.BLUE;
        //config.getProgress().actions.add(new UploadNotificationAction(R.drawable.ic_cancelled, "Cancelado", ));

        config.getCompleted().message = "El archivo se ha subido exitosamente en " + ELAPSED_TIME;
        config.getCompleted().iconResourceID = R.drawable.ic_upload_success;
        config.getCompleted().iconColorResourceID = Color.GREEN;

        config.getError().message = "Error mientras se subía";
        config.getError().iconResourceID = R.drawable.ic_upload_error;
        config.getError().iconColorResourceID = Color.RED;

        config.getCancelled().message = "Cancelado";
        config.getCancelled().iconResourceID = R.drawable.ic_cancelled;
        config.getCancelled().iconColorResourceID = Color.YELLOW;


        return config;
    }


}
