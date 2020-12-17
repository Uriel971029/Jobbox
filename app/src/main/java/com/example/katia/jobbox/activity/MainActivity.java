package com.example.katia.jobbox.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.controller.Register;
import com.example.katia.jobbox.interfaces.UsuarioAPI;
import com.example.katia.jobbox.model.Auth.auth;
import com.example.katia.jobbox.controller.SelectionRol;
import com.example.katia.jobbox.model.RetrofitSingleton;
import com.example.katia.jobbox.model.UsuarioModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView txtNewAccount;
    ImageView imgLogo;

    //http://192.168.1.76/Platform/App/Servicios_Rest/
    //http://192.168.0.9//App/Servicios_Rest/
    //http://jobbox-com.stackstaging.com/App/Servicios_Rest/
    public static final String URL_SERVER = "http://192.168.0.12/App/Servicios_Rest/";
    public static final String USERS_REFERENCE = "Usuarios";
    public static final String CHAT_REFERENCE = "Chat";
    public static final String IMG_REFERENCE = "img_chat";
    public static boolean FLAG_FINGERPRINT = true;
    public LocationManager handler;
    private String provider;
    private final int MY_PERMISSIONS = 100;
    private Double lat;
    private Double lon;

    private int isGPSEnable = 0;
    Intent intent;
    SharedPreferences prefs;
    private final Activity activity = this;

    public Activity getActivity(){

        return MainActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String message = getIntent().getStringExtra("message");
        if (message != null) {
            Toast.makeText(this, getIntent().getExtras().getString("message"), Toast.LENGTH_LONG).show();
        }

        txtNewAccount = (TextView) findViewById(R.id.txtCreate);
        imgLogo = (ImageView) findViewById(R.id.imgSelect);

        verifyTutorial();
        mayRequestStoragePermission();
        //MOSTRAMOS LA OPCIÓN PARA INICIAR SESIÓN CON HUELLA
        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        //Crear el objeto retroFit para usuar parseo de Json a Java
        Retrofit retrofit = RetrofitSingleton.getInstance();
        //Se instancia la intyerfaz con los end-points
        UsuarioAPI usuarioAPI = retrofit.create(UsuarioAPI.class);
        //se instancia un objeto Call a partir de llamar al end-point
        Call<UsuarioModel> call = usuarioAPI.getUsers(7, 49);
        call.enqueue(new Callback<UsuarioModel>() {
            @Override
            public void onResponse(Call<UsuarioModel> call, Response<UsuarioModel> response) {
                    if(response.isSuccessful()) {
                        UsuarioModel usuario = response.body();
                        Log.i("usuarioRetro: ", usuario.toString());
                    }else{
                        Log.e("retrofit", response.message());
                    }
                }

            @Override
            public void onFailure(Call<UsuarioModel> call, Throwable t) {
                Log.e("Retrofitex", t.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkIfLocationOpened()) {
            activarGPS();
        }
    }

    //Verificar si ya fue presentado en la primera ejecución el tutorial de introducción de la App, ningúna entrada, ningúna salida.
    private void verifyTutorial() {

        prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        Boolean bandActivity = prefs.getBoolean("bandera", false);

        if (!bandActivity) {
            finish();
            intent = new Intent(this, TutorialActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //Función que redirecciona al formulario de registro, ENTRADA: Objeto de tipo vista
    public void createAccount(View view) {
        if (isGPSEnable == 1) {
            intent = new Intent(this, Register.class);
            startActivity(intent);
        } else {
            activarGPS();
        }
    }


    //FUNCION QUE REDIRIGE A LA INTERFAZ DE INICIO DE SESIÓN, ENTRADA: Objeto de vista, SALIDA:nada
    public void login(View view) {

        if (isGPSEnable == 1) {
            if (isNetDisponible()) {
                //SI HAY CONEXIÓN A INTERNET
                if (isOnlineNet()) {

                    //MOSTRAR OPCIÓN DE INICIAR SESIÓN CON HUELLA
                    intent = new Intent(this, activity_fingerPrint.class);
                    intent.putExtra("isFingerprintUsed", new ResultReceiver(null) {

                        //SI RESPONDE ES QUE SE VA A INICIAR SESIÓN CLÁSICAMENTE
                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {

                            if (resultCode == 2) {

                                if (!FLAG_FINGERPRINT) {

                                    intent = new Intent(MainActivity.this, auth.class);

                                    /*intent.putExtra("isFinished", new ResultReceiver(null) {

                                        @Override
                                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                                            MainActivity.this.finish();
                                        }
                                    });
                                    startActivityForResult(intent, 1);*/
                                    startActivity(intent);
                                }

                            }
                        }
                    });
                    startActivityForResult(intent, 2);


                } else {
                    Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Error no red, revise su conexión", Toast.LENGTH_SHORT).show();

            }
        } else {
            activarGPS();
        }
    }


    //SOLICITAMOS LOS PERMISOS PARA PERMISOS DE UBICACIÓN
    private boolean mayRequestStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //VERIFICAMOS LA VERSIÓN DEL SDK DE ANDROID SI ES MAYOR O NO A LA VERSIÓN 6
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    return true;
                }

                //EVALUAR SI ES NECESARIO DAR UNA EXPLICACIÓN DE LA SOLICITUD DE PERMISOS, ESTO SE DA CUANDO ANTES SE RECHAZARÓN
                if ((shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) && (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION))) {
                    showExplanation();
                    return false;
                } else {
                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, MY_PERMISSIONS);
                }

            } else {
                if (!checkIfLocationOpened()) {
                    activarGPS();
                } else {
                    isGPSEnable = 1;
                    habilitarServicio();
                }
            }
        }

        return false;
    }


    private void showExplanation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface loading, int which) {

                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface loading, int which) {

                loading.dismiss();
            }
        });

        builder.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length == 2 && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                //Toast.makeText(getActivityActual(), "Permisos aceptados", Toast.LENGTH_SHORT).show();
                if (!checkIfLocationOpened()) {
                    activarGPS();
                }
                habilitarServicio();
            }
        } else {
            //SI RECHAZO LOS PERMISOS, LA PRÓXIMA VEZ QUE INTENTE USAR LA APP SE MOSTRARÁ UNA EXPLICACIÓN DEL POR QUE SE PIDEN ESOS PERMISOS
            showExplanation();
        }
    }


    @SuppressLint("MissingPermission")
    public void habilitarServicio() {

        handler = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        provider = handler.getBestProvider(c, true);
        handler.requestLocationUpdates(provider, 1000, 1, this);
        obtenerUbicacion();
    }

    public void detenerServicio() {

        handler.removeUpdates(this);
    }

    @SuppressLint("MissingPermission")
    public void obtenerUbicacion() {

        Location location = null;

        if (handler.getAllProviders().contains(provider) && handler.isProviderEnabled(provider)) {
            location = handler.getLastKnownLocation(provider);
        }
        if (location != null) {
            isGPSEnable = 1;
            lon = location.getLongitude();
            lat = location.getLatitude();

            if (lon != 0.0 && lat != 0.0) {

                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                try {
                    List<Address> list = geocoder.getFromLocation(lat, lon, 1);
                    if (!list.isEmpty()) {

                        Address address = list.get(0);
                        //ALMACENAMOS LA DIRECCIÓN EN LAS PREFERENCIAS
                        SharedPreferences preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        //editor.putString("cp", address.getPostalCode());
                        editor.putString("localidad", address.getLocality());
                        //editor.putString("municipio", address.getCountryName());
                        editor.putString("sublocalidad", address.getSubLocality());
                        editor.putString("lat", String.valueOf(address.getLatitude()));
                        editor.putString("long", String.valueOf(address.getLongitude()));
                        editor.commit();

                        detenerServicio();

                    }


                } catch (IOException e) {
                    Log.d("error", e.toString());
                }
            }

        }
    }


    private boolean checkIfLocationOpened() {

        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        System.out.println("Provider contains=> " + provider);
        if (provider.contains("gps") || provider.contains("network")) {
            return true;
        }
        return false;
    }


    public void activarGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Activar GPS");
        builder.setMessage("La App utiliza el servicio de GPS para funcionar normalmente, por favor encienda su ubicación");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface loading, int which) {
                isGPSEnable = 1;
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface loading, int which) {

                isGPSEnable = 0;
                loading.dismiss();
            }
        });

        builder.show();

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

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
