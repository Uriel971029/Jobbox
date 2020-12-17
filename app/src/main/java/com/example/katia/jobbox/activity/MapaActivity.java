package com.example.katia.jobbox.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.FragmentActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.model.Direccion;
import com.example.katia.jobbox.model.Empleo;
import com.example.katia.jobbox.model.Usuario;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.katia.jobbox.activity.MainActivity.USERS_REFERENCE;
import static com.example.katia.jobbox.controller.Menu.oficios;
import static com.example.katia.jobbox.controller.Menu.trabajador;

public class MapaActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private EditText edtDesc, edtFecha;
    private Button btnBuscar, btnMensaje, btnContratar, btnEnviar, btnCancelar;
    private TextView txtResultados, txtBuscado, txtNombre, txtServicios, txtTelefono, txtCorreo;
    private ImageView imgUser;
    RatingBar ratingBar;
    private String localidad, municipio, idTrabajador;
    private LatLng location;
    private String idOficio, oficio = "";
    private int index;
    Group addressGroup, jobGroup;
    RadioGroup radioGroup;
    RadioButton radioButton;
    ArrayAdapter<String> adapterM, adapterC, adapterO;
    public ArrayList<String> listaM, listaC = new ArrayList<>(), listaO = new ArrayList<>();
    Spinner spnM, spnC, spnO;
    Direccion direccion;
    Empleo empleo;
    private String opc = "Oficio";
    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet;
    FirebaseDatabase database;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        btnBuscar = findViewById(R.id.btnBuscar);
        txtResultados = findViewById(R.id.txtResults);
        txtBuscado = findViewById(R.id.txtBuscado);
        spnM = (Spinner) findViewById(R.id.spnRol);
        spnC = (Spinner) findViewById(R.id.spnColonia);
        spnO = (Spinner) findViewById(R.id.spnOficio);
        addressGroup = findViewById(R.id.groupAddress);
        jobGroup = findViewById(R.id.groupJob);
        radioGroup = findViewById(R.id.radioGroup);
        constraintLayout = findViewById(R.id.constraintLayout);

        direccion = new Direccion(this);
        empleo = new Empleo(this);
        send("oficios");

        spnO.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (mMap != null) {
                    mMap.clear();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spnM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String index = String.valueOf((position + 1));
                getColonias(index);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int idChecked = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(idChecked);

                opc = radioButton.getText().toString();

                switch (opc) {

                    case "Oficio":

                        mMap.clear();
                        constraintSet = new ConstraintSet();

                        localidad = spnC.getSelectedItem().toString();
                        municipio = spnM.getSelectedItem().toString();
                        txtBuscado.setText(getResources().getString(R.string.label_zona_buscada) + " " + localidad);

                        if (addressGroup.isShown()) {

                            addressGroup.setVisibility(View.GONE);

                        }

                        if (!jobGroup.isShown()) {

                            jobGroup.setVisibility(View.VISIBLE);
                        }

                        constraintSet.clone(constraintLayout);
                        constraintSet.connect(txtResultados.getId(), ConstraintSet.TOP, spnO.getId(), ConstraintSet.BOTTOM, 24);
                        constraintSet.applyTo(constraintLayout);

                        send("oficios");

                        break;

                    case "Zona":

                        mMap.clear();

                        constraintSet = new ConstraintSet();
                        idOficio = String.valueOf(spnO.getSelectedItemPosition() + 1);
                        oficio = spnO.getSelectedItem().toString();

                        txtResultados.setText(getResources().getString(R.string.label_resultados_encontrados));
                        txtBuscado.setText(getResources().getString(R.string.label_oficio_buscado) + " " + oficio);

                        listaM = direccion.getList(1, "1");

                        if (jobGroup.isShown()) {

                            jobGroup.setVisibility(View.GONE);
                        }

                        if (!addressGroup.isShown()) {

                            addressGroup.setVisibility(View.VISIBLE);
                        }

                        constraintSet.clone(constraintLayout);
                        constraintSet.connect(txtResultados.getId(), ConstraintSet.TOP, spnC.getId(), ConstraintSet.BOTTOM, 24);
                        constraintSet.applyTo(constraintLayout);


                        send("municipios");


                        break;
                }

            }
        });

        trabajador.setActivityActual(this);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (opc.equalsIgnoreCase("Oficio")) {

                    idOficio = spnO.getSelectedItemPosition() + 1 + "";

                } else if (opc.equalsIgnoreCase("Zona")) {

                    index = 0;
                    txtBuscado.setText(getResources().getString(R.string.label_oficio_buscado) + oficio);
                    localidad = spnC.getSelectedItem().toString() + ", " + spnM.getSelectedItem().toString() + ", Mor.";
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {

                        if (!listaC.isEmpty()) {

                            List<Address> list = geocoder.getFromLocationName(localidad, 1);
                            Address address = list.get(0);
                            location = new LatLng(address.getLatitude(), address.getLongitude());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    index = localidad.indexOf(",");
                    localidad = localidad.substring(0, index);
                }

                trabajador.getLocationWorkers(localidad, location, idOficio, txtResultados);
            }
        });

        mapFragment.getMapAsync(this);
    }

    public void getColonias(String index) {

        direccion.setColonias(listaC);
        listaC.addAll(direccion.getList(2, index));
    }


    public void send(String method) {

        switch (method) {

            case "oficios":

                //declaramos empleo global y llamamos de nuevo aqui
                if (adapterO == null) {
                    adapterO = new ArrayAdapter<String>(this, R.layout.textview_spinner, oficios);
                    adapterO.setDropDownViewResource(R.layout.textview_spinner);
                    empleo.setAdapter(adapterO);
                    spnO.setAdapter(adapterO);
                }

                break;

            case "municipios":

                if (adapterM == null && adapterC == null) {
                    //MUNICIPIOS
                    adapterM = new ArrayAdapter<String>(this, R.layout.textview_spinner, listaM);
                    adapterM.setDropDownViewResource(R.layout.textview_spinner);
                    direccion.setAdapterM(adapterM);
                    spnM.setAdapter(adapterM);

                    //LA LISTA DE CIUDADES SE LLENA UNA VEZ QUE SE HA SELECCIONADO UN MUNICIPIO
                    adapterC = new ArrayAdapter<String>(this, R.layout.textview_spinner, listaC);
                    adapterC.setDropDownViewResource(R.layout.textview_spinner);
                    direccion.setAdapterC(adapterC);
                    spnC.setAdapter(adapterC);
                }

                break;

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        Double latitud;
        Double longitud;

        SharedPreferences preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        latitud = Double.parseDouble(preferences.getString("lat", null));
        longitud = Double.parseDouble(preferences.getString("long", null));
        localidad = preferences.getString("localidad", null);
        //municipio = preferences.getString("municipio", null);

        txtBuscado.setText(getResources().getString(R.string.label_zona_buscada) + " " + localidad);
        location = new LatLng(latitud, longitud);
        idOficio = String.valueOf(getIntent().getExtras().getInt("idOficio"));
        //PETICIÓN AL SERVIDOR PARA BUSCAR TODOS AQUELLOS TRABAJADORES PERTENECIENTES A LA LOCALIDAD MAS CERCANA
        trabajador.getLocationWorkers(localidad, location, idOficio, txtResultados);
        trabajador.setmMap(mMap);

        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        marker.showInfoWindow();

        index = marker.getTitle().indexOf("#");
        idTrabajador = marker.getTitle().substring(index + 1, marker.getTitle().length());

        final AlertDialog[] dialog = {null};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapaActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.activity_detallesusuario, null);

        txtNombre = mView.findViewById(R.id.txtNombre);
        txtServicios = mView.findViewById(R.id.txtServicios);
        txtTelefono = mView.findViewById(R.id.txtTelefono);
        txtCorreo = mView.findViewById(R.id.txtCorreo);
        imgUser = mView.findViewById(R.id.imgUser);
        ratingBar = mView.findViewById(R.id.ratingBar);

        //MOSTRAMOS LA INFORMACIÓN DEL TRABAJADOR EN LA VENATANA FLOTANTE
        index = marker.getTitle().indexOf("#");
        String idUsuario = marker.getTitle().substring(index + 1, marker.getTitle().length());
        trabajador.getDataWorker(idUsuario, txtNombre, txtServicios, txtTelefono, txtCorreo, imgUser, ratingBar);

        btnMensaje = mView.findViewById(R.id.btnMensaje);
        btnMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startChatWithWorker();
            }
        });

        btnContratar = mView.findViewById(R.id.btnEnviar);
        btnContratar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //EL USUARIO AGREGA LA FECHA EN LA QUE REQUIERE EL SERVICIO Y LA DESCRIPCIÓN EN OTRO DIALOG
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MapaActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.activity_detalles_solicitud, null);

                edtDesc = mView.findViewById(R.id.edtNewC);
                edtFecha = mView.findViewById(R.id.edtFecha);
                btnEnviar = mView.findViewById(R.id.btnEnviar);
                btnCancelar = mView.findViewById(R.id.btnCancelar);


                edtFecha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Calendar calendar = Calendar.getInstance();
                        DatePickerDialog datePickerDialog = new DatePickerDialog(MapaActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int mes, int dia) {
                                Calendar calendarResultado = Calendar.getInstance();
                                calendarResultado.set(Calendar.YEAR, year);
                                calendarResultado.set(Calendar.MONTH, mes);
                                calendarResultado.set(Calendar.DAY_OF_MONTH, dia);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                Date date = calendarResultado.getTime();
                                String fechaDeNacimientoTexto = simpleDateFormat.format(date);
                                //fechaDeNacimiento = date.getTime();
                                edtFecha.setText(fechaDeNacimientoTexto);
                            }
                        }, calendar.get(Calendar.YEAR) - 18, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                    }
                });

                btnEnviar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (validar()) {
                            Usuario usuario = Usuario.getInstance();
                            usuario.setActivityActual(MapaActivity.this);
                            //SE ENVIA UNA SOLICITUD DE CONTRATACIÓN AL TRABAJADOR EN FORMA DE VACANTE
                            if (oficio.isEmpty()) {
                                oficio = spnO.getItemAtPosition(Integer.valueOf(idOficio) - 1).toString();
                            }

                            empleo.setIdOficio(Integer.valueOf(idOficio));
                            empleo.setIdCliente(usuario.getId());
                            //En la descripción se envia el id del trabajador que esta solicitando para guardar la referencia
                            empleo.setIdTrabajador(Integer.valueOf(idTrabajador));
                            empleo.setTitulo(getResources().getString(R.string.text_solicitud_contratacion) + " " + oficio);
                            empleo.setDescripcion(edtDesc.getText().toString());
                            empleo.setLugar(localidad);
                            empleo.setFecha(edtFecha.getText().toString());
                            usuario.setBtnSolicitar(btnContratar);
                            usuario.registerVacancy(empleo);
                            dialog[0].dismiss();
                        }else{
                            Toast.makeText(MapaActivity.this, "Complete los campos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog[0].dismiss();
                    }
                });

                //AGREGAMOS LA VISTA QUE PREVIAMENTE ELABORAMOS
                mBuilder.setView(mView);
                dialog[0] = mBuilder.create();
                dialog[0].show();

            }
        });

        //AGREGAMOS LA VISTA QUE PREVIAMENTE ELABORAMOS
        mBuilder.setView(mView);
        dialog[0] = mBuilder.create();
        dialog[0].show();


        return false;
    }

    //FUNCIÓN PARA VALIDAR LOS CAMPOS DEL FORMULARIO DE REGISTRO, SALIDA: VERDERO O FALSO, SEGÚN SEA EL CASO
    public boolean validar() {

        edtFecha.setError(null);
        edtDesc.setError(null);

        String fecha = edtFecha.getText().toString();
        String desc = edtDesc.getText().toString();

        if (TextUtils.isEmpty(fecha)) {
            edtFecha.setError("Campo Obligatorio");
            edtFecha.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(desc)) {
            edtDesc.setError("Campo Obligatorio");
            edtDesc.requestFocus();
            return false;
        }

        return true;
    }

    private String startChatWithWorker() {

        final String[] key = new String[1];
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(USERS_REFERENCE);
        String correoTrabajador = txtCorreo.getText().toString();
        //Obtenemos una referencia de la carpeta de usuario en la base de datos y filtramos usando el correo del trabajador para buscar su ID
        reference.orderByChild("correo").equalTo(correoTrabajador).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key = null;
                //UsuarioFirebase usuario = null;

                /*for (DataSnapshot child : dataSnapshot.getChildren()) {
                    key = child.getKey();
                    usuario = child.getValue(UsuarioFirebase.class);
                }*/
                //Obtenemos la llave del trabajador
                Intent intent = new Intent(getApplicationContext(), MensajeriaActivity.class);
                intent.putExtra("keyReceptor", key);
                /*if (usuario != null) {
                    intent.putExtra("nameReceptor", usuario.getNombre());
                }*/
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("errorFire", databaseError.toString());
            }
        });

        return key[0];
    }

}
