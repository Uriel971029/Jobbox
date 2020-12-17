package com.example.katia.jobbox.model;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;

import com.example.katia.jobbox.model.Auth.auth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.example.katia.jobbox.controller.Menu.getDialogProgressBar;
//import static com.example.katia.jobbox.controller.Menu.usuario;


//CLASE PARA ESTABLECER UNA CONEXIÓN DE TIPO HTTP EN SEGUNDO PLANO, ENTRADAS: URL, SALIDA: RESPUESTA DE SOLICITUD EN FORMA DE CADENA
public abstract class Connection extends AsyncTask<String, String, String> implements auth.onLoadScreenListener {

    private AlertDialog progressDialog = null;
    public static AlertDialog.Builder builder;
    private Activity activity;
    static CookieManager cookieManager = new CookieManager();
    static final String COOKIES_HEADER = "Set-Cookie";
    static String SESSION_ID = "";


    public Connection(final Activity activity) {

        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {

        if (!activity.isFinishing()) {
            progressDialog = getDialogProgressBar(activity).create();
            progressDialog.show();
        }
    }

    //Función asincrona que procesa y configura el cuerpo la petición HTTP
    //Entradas: arreglo de cadenas: [url, datos, metodo o tipo de petición]
    @Override
    protected String doInBackground(String... strings) {

        HttpURLConnection httpURLConnection = null;
        URL url = null;
        String bodyRequest = "";
        byte[] postData;
        int postDataLength = 0;
        ArrayList<String> dataUser = new ArrayList<>();

        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

            if (!SESSION_ID.equalsIgnoreCase("")) {
                httpURLConnection.setRequestProperty("Cookie",
                        TextUtils.join(";", cookieManager.getCookieStore().getCookies()));
            }

            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(50000);
            httpURLConnection.setReadTimeout(50000);

            if (!dataUser.isEmpty()) {
                dataUser.clear();
            }

            String opc = "";
            //PETICIÓN SIN CUERPO
            if (strings.length != 1) {
                if (strings.length < 3) {
                    opc = strings[1];
                } else {
                    opc = strings[2];
                    if (!opc.contains("add_")) {

                        if (!opc.contains("get_")) {

                            if (!opc.contains("register_")) {
                                //ELIMINAMOS ESPACIOS Y CORCHETES
                                strings[1] = strings[1].replaceAll("[\\s*\\[\\]]", "");
                            }
                        }
                    }
                }

                switch (opc) {

                    case "register":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));

                        if (dataUser.get(6).contains("&")) {
                            dataUser.set(6, dataUser.get(6).replace("&", " "));
                        }

                        bodyRequest = "nombre=" + URLEncoder.encode(dataUser.get(0), "UTF-8")
                                + "&apeP=" + URLEncoder.encode(dataUser.get(1), "UTF-8")
                                + "&apeM=" + URLEncoder.encode(dataUser.get(2), "UTF-8")
                                + "&password=" + URLEncoder.encode(dataUser.get(3), "UTF-8")
                                + "&correo=" + URLEncoder.encode(dataUser.get(4), "UTF-8")
                                + "&telefono=" + URLEncoder.encode(dataUser.get(5), "UTF-8")
                                + "&colonia=" + URLEncoder.encode(dataUser.get(6), "UTF-8")
                                + "&municipio=" + URLEncoder.encode(dataUser.get(7), "UTF-8")
                                + "&longitud=" + URLEncoder.encode(dataUser.get(8), "UTF-8")
                                + "&latitud=" + URLEncoder.encode(dataUser.get(9), "UTF-8")
                                + "&tokeN=" + URLEncoder.encode(dataUser.get(10), "UTF-8")
                                + "&tipoUsuario=" + URLEncoder.encode(dataUser.get(11), "UTF-8")
                                + "&method=" + URLEncoder.encode("1", "UTF-8");

                        if (dataUser.size() > 12) {
                            bodyRequest += "&idOficio=" + URLEncoder.encode(dataUser.get(12), "UTF-8") + "&aniosExp="
                                    + URLEncoder.encode(dataUser.get(13), "UTF-8");
                        }
                        break;

                    case "register_job":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));

                        bodyRequest = "aniosExp=" + URLEncoder.encode(dataUser.get(0), "UTF-8") + "&idOficio="
                                + URLEncoder.encode(dataUser.get(1), "UTF-8") + "&idUsuario="
                                + URLEncoder.encode(dataUser.get(2), "UTF-8") + "&method=" + URLEncoder.encode("1", "UTF-8");
                        break;

                    case "register_rate":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));

                        bodyRequest = "calif=" + URLEncoder.encode(dataUser.get(0), "UTF-8")
                                + "&coment=" + URLEncoder.encode(dataUser.get(1), "UTF-8")
                                + "&idEmisor=" + URLEncoder.encode(dataUser.get(2), "UTF-8")
                                + "&idContrato=" + URLEncoder.encode(dataUser.get(3), "UTF-8")
                                + "&method=" + URLEncoder.encode("5", "UTF-8");
                        break;


                    case "register_comment":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));

                        bodyRequest = "asunto=" + URLEncoder.encode(dataUser.get(0), "UTF-8")
                                + "&coment=" + URLEncoder.encode(dataUser.get(1), "UTF-8")
                                + "&idUser=" + URLEncoder.encode(dataUser.get(2), "UTF-8")
                                + "&method=" + URLEncoder.encode("6", "UTF-8");
                        break;

                    case "login":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));

                        bodyRequest = "user=" + URLEncoder.encode(dataUser.get(0), "UTF-8")
                                + "&pass=" + URLEncoder.encode(dataUser.get(1), "UTF-8")
                                //+ "&opc=" + URLEncoder.encode("1", "UTF-8")
                                + "&method=" + URLEncoder.encode("1", "UTF-8");
                        break;

                    case "logout":
                        bodyRequest = "method=" + URLEncoder.encode("2", "UTF-8");
                        break;

                    case "sendMail":
                        bodyRequest = "email=" + URLEncoder.encode(strings[1], "UTF-8") + "&method=" + URLEncoder.encode("3", "UTF-8");
                        break;

                    case "resetPassword":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));
                        bodyRequest = "tabla=" + URLEncoder.encode(dataUser.get(0), "UTF-8") + "&campos=" + URLEncoder.encode(dataUser.get(1), "UTF-8")
                                + "&pwd=" + URLEncoder.encode(dataUser.get(2), "UTF-8") + "&codigo=" + URLEncoder.encode(dataUser.get(3), "UTF-8")
                                + "&correo=" + URLEncoder.encode(dataUser.get(4), "UTF-8") + "&method=" + URLEncoder.encode("3", "UTF-8");
                        break;

                    case "form_add_vacancy":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));
                        bodyRequest = "titulo=" + URLEncoder.encode(dataUser.get(0), "UTF-8") + "&descripcion=" + URLEncoder.encode(dataUser.get(1),
                                "UTF-8") + "&lugar=" + URLEncoder.encode(dataUser.get(2), "UTF-8") + "&idOficio=" + URLEncoder.encode(dataUser.get(3),
                                "UTF-8") + "&idCliente=" + URLEncoder.encode(dataUser.get(4), "UTF-8")
                                + "&idTrabajador=" + URLEncoder.encode(dataUser.get(5), "UTF-8")
                                + "&fecha=" + URLEncoder.encode(dataUser.get(6), "UTF-8")
                                + "&method=" + URLEncoder.encode("1", "UTF-8");

                        break;

                    case "add_engagement":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));
                        bodyRequest = "costo=" + URLEncoder.encode(dataUser.get(0), "UTF-8") + "&descripcion=" + URLEncoder.encode(dataUser.get(1), "UTF-8")
                                + "&idCliente=" + URLEncoder.encode(dataUser.get(2), "UTF-8") + "&idTrabajador=" + URLEncoder.encode(dataUser.get(3), "UTF-8")
                                + "&idOficio=" + URLEncoder.encode(dataUser.get(4), "UTF-8") + "&idVacante=" + URLEncoder.encode(dataUser.get(5), "UTF-8")
                                + "&method=" + URLEncoder.encode("1", "UTF-8");

                        break;

                    case "get_engagementUpdates":


                        dataUser.addAll(Arrays.asList(strings[1].split(",")));

                        bodyRequest = "idUsuario=" + URLEncoder.encode(dataUser.get(0), "UTF-8") +
                                "&rol=" + URLEncoder.encode(dataUser.get(1), "UTF-8")
                                + "&method=" + URLEncoder.encode("2", "UTF-8");

                        break;


                    case "get_vacancies":

                        bodyRequest = "idTrabajador=" + URLEncoder.encode(strings[1], "UTF-8") + "&method=" + URLEncoder.encode("3", "UTF-8");

                        break;

                    case "get_hiringRequest":

                        bodyRequest = "idTrabajador=" + URLEncoder.encode(strings[1], "UTF-8") + "&method=" + URLEncoder.encode("2", "UTF-8");

                        break;


                    case "get_locations":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));
                        bodyRequest = "colonia=" + URLEncoder.encode(dataUser.get(0), "UTF-8") + "&idOficio=" + URLEncoder.encode(dataUser.get(1), "UTF-8")
                                + "&method=" + URLEncoder.encode("4", "UTF-8");

                        break;

                    case "get_workerInfo":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));
                        bodyRequest = "idusuario=" + URLEncoder.encode(dataUser.get(0), "UTF-8") + "&method=" + URLEncoder.encode("5", "UTF-8");

                        break;

                    case "pay_engagement":

                        bodyRequest = "idContratacion=" + URLEncoder.encode(strings[1], "UTF-8") + "&method=" + URLEncoder.encode("3", "UTF-8");

                        break;

                    case "delay_engagement":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));
                        bodyRequest = "fields=" + URLEncoder.encode(dataUser.get(0), "UTF-8")
                                + "data=" + URLEncoder.encode(dataUser.get(1), "UTF-8")
                                + "&method=" + URLEncoder.encode("4", "UTF-8");
                        break;

                    case "delete_engagement":

                        bodyRequest = "idContratacion=" + URLEncoder.encode(strings[1], "UTF-8") + "&method=" + URLEncoder.encode("5", "UTF-8");

                        break;

                    case "update_user":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));
                        bodyRequest = "tabla=" + URLEncoder.encode(dataUser.get(0), "UTF-8") + "&campos=" + URLEncoder.encode(dataUser.get(1), "UTF-8")
                                + "&data=" + URLEncoder.encode(dataUser.get(2), "UTF-8")
                                + "&correo=" + URLEncoder.encode(dataUser.get(3), "UTF-8") + "&method=" + URLEncoder.encode("3", "UTF-8");
                        break;

                    case "upload_image":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));
                        bodyRequest = "filename=" + URLEncoder.encode(dataUser.get(0), "UTF-8") +
                                "&idUser=" + URLEncoder.encode(dataUser.get(1), "UTF-8") +
                                "&method=" + URLEncoder.encode("4", "UTF-8");

                        break;

                    case "upload_evidence":

                        dataUser.addAll(Arrays.asList(strings[1].split(",")));
                        bodyRequest = "evidencia=" + URLEncoder.encode(dataUser.get(0), "UTF-8")
                                + "&idcontrato=" + URLEncoder.encode(dataUser.get(1), "UTF-8")
                                + "&method=" + URLEncoder.encode("6", "UTF-8");

                        break;


                    case "finish_engagement":

                        bodyRequest = "idcontrato=" + URLEncoder.encode(strings[1], "UTF-8") + "&method=" + URLEncoder.encode("7", "UTF-8");

                        break;

                    //obtener direcciones
                    default:
                        bodyRequest = "param1=" + URLEncoder.encode(strings[1], "UTF-8") + "&param2="
                                + URLEncoder.encode(strings[2], "UTF-8");

                        break;
                }


                //CONFIGURAMOS EL CUERPO DE LA PETICIÓN Y SUS CABECERAS
                postData = bodyRequest.getBytes(Charset.forName("UTF-8"));
                postDataLength = postData.length;
                httpURLConnection.setRequestProperty("Content-Length", Integer.toString(postDataLength));

                OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
                outputStream.write(bodyRequest.getBytes());
                outputStream.flush();
                outputStream.close();

                //OBTENER COOKIES
                Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                }

                if (cookieManager.getCookieStore().getCookies().size() > 0) {
                    SESSION_ID = cookieManager.getCookieStore().getCookies().get(0).getValue();
                }

                httpURLConnection.connect();

            }

            int code = httpURLConnection.getResponseCode();

            if (code == HttpURLConnection.HTTP_OK) {

                //OBTENEMOS LA INFORMACIÓN
                InputStream in = new BufferedInputStream(httpURLConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                StringBuffer buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {

                    buffer.append(line);
                }
                reader.close();
                return buffer.toString();

            } else {
                Log.d("error_con", httpURLConnection.getErrorStream().toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }

        return null;
    }


    @Override
    protected void onPostExecute(String response) {

        if (activity != null && activity.isFinishing()) {
            OnLoadScreen(response);
            return;
        }
        progressDialog.dismiss();
        OnLoadScreen(response);
    }


    @Override
    public abstract void OnLoadScreen(String result);
}
