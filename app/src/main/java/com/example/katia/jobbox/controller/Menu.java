package com.example.katia.jobbox.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.katia.jobbox.Presentador.UsuarioPresentador;
import com.example.katia.jobbox.activity.ContratacionActivity;
import com.example.katia.jobbox.controller.Fragments.Fragment_account;
import com.example.katia.jobbox.R;
import com.example.katia.jobbox.model.Auth.login;
import com.example.katia.jobbox.controller.Fragments.Fragment_chat;
import com.example.katia.jobbox.controller.Fragments.Fragment_inicio;
import com.example.katia.jobbox.model.Empleo;
import com.example.katia.jobbox.model.EmpleoModel;
import com.example.katia.jobbox.model.Interactor.UsuarioInteractor;
import com.example.katia.jobbox.model.Trabajador;
import com.example.katia.jobbox.model.Usuario;
import com.example.katia.jobbox.model.UsuarioModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Menu extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, login.OnFragmentChangedListener {

    private static Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    FloatingActionButton myFabButton;
    private String response;
    public static ArrayList<String> oficios = new ArrayList<>();
    public static ArrayList<Empleo> oficiosDetails = new ArrayList<>();
    public static ArrayList<Empleo> vacantes = new ArrayList<>();
    public Empleo empleo;
    public static Trabajador trabajador;


    public UsuarioModel usuario;
    public static int rol;
    UsuarioPresentador presentador;


    //ELEMENTOS DEL POST
    private Spinner spinner;
    private EditText title, desc, edtFecha;
    private ArrayAdapter<String> adapter;
    public static AlertDialog.Builder builder;

    //Fragmentos del menú
    FragmentManager fragmentManager;
    Fragment_inicio fragment_inicio;
    Fragment_chat fragment_chat;
    Fragment_account fragment_account;

    private String CURRENT_FRAGMENT = "";

    public UsuarioModel getUsuario() {
        return usuario;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        String lugar = prefs.getString("localidad", null);
        lugar += " " + prefs.getString("sublocalidad", null);
        //OBTENEMOS EL ROL DEL USUARIO: CLIENTE O TRABAJADOR
        //usuario = Usuario.getInstance();
        usuario = UsuarioModel.getUsuario();
        rol = usuario.getRol();
        fragment_inicio = Fragment_inicio.newInstance(String.valueOf(rol), "");
        manageFragments(fragment_inicio, "fragment_inicio", 1);
        initComponents();
        //Cuestion de componentes en pantallas dinámicas
        //usuario.setActivityActual(this);
        //usuario.setActivityMenu(this);
        //INICIALIZAMOS LOS COMPONENTES DE LA VENTANA EMERGENTE
        initFormPost();

        final String finalLugar = lugar;
        myFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (rol == 3) {

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(Menu.this);
                    View mView = getLayoutInflater().inflate(R.layout.form_add_vacancy, null);

                    spinner = mView.findViewById(R.id.ratingBar);
                    title = mView.findViewById(R.id.edtTitle);
                    desc = mView.findViewById(R.id.edtNewC);
                    edtFecha = mView.findViewById(R.id.edtFecha);

                    edtFecha.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final Calendar calendar = Calendar.getInstance();
                            DatePickerDialog datePickerDialog = new DatePickerDialog(Menu.this, new DatePickerDialog.OnDateSetListener() {
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

                    spinner.setAdapter((SpinnerAdapter) adapter);

                    mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    empleo.setIdCliente(usuario.getId());
                                    //En la descripción se envia el id del trabajador que esta solicitando para guardar la referencia
                                    empleo.setIdTrabajador(0);
                                    empleo.setIdOficio(spinner.getSelectedItemPosition() + 1);
                                    empleo.setTitulo(title.getText().toString());
                                    empleo.setDescripcion(desc.getText().toString());
                                    empleo.setLugar(finalLugar);
                                    empleo.setFecha(edtFecha.getText().toString());
                                    presentador.registerVacancy(empleo);
                                }
                            }
                    );

                    mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });


                    //AGREGAMOS LA VISTA QUE PREVIAMENTE ELABORAMOS
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();

                    //SETEAMOS COLOR A LOS BOTONES
                    Button cancelar = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    Button aceptar = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    if (cancelar != null && aceptar != null) {
                        cancelar.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        aceptar.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    }

                } else {
                    //ADMINISTRAR CONTRATACIONES
                    Intent intent = new Intent(Menu.this, ContratacionActivity.class);
                    intent.putExtra("manage_engagement", "true");
                    startActivity(intent);
                }
            }
        });

    }

    private void initComponents() {
        this.presentador = new UsuarioPresentador(this);
        toolbar = findViewById(R.id.toolbar);
        myFabButton = findViewById(R.id.my_fab);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        if (rol == 4)
            myFabButton.setImageResource(R.drawable.ic_tools);
        setSupportActionBar(toolbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(Menu.this);
    }


    public void initFormPost() {

        trabajador = new Trabajador();
        empleo = new Empleo(this);
        //Renovamos la lista de empleos
        if (!oficios.isEmpty()) {
            oficios.clear();
        }

        if (!oficiosDetails.isEmpty()) {
            oficiosDetails.clear();
        }

        oficios = empleo.getList();
        //Obtenemos notificaciones de las contrataciones
        //usuario.getEngagementUpdates();
        //Cliente
        if (rol == 3) {
            empleo.setListEmpleosDetalles(oficiosDetails);
            adapter = new ArrayAdapter<String>(this, R.layout.textview_spinner, oficios);
            ((ArrayAdapter) adapter).setDropDownViewResource(R.layout.textview_spinner);
            if (empleo.getAdapter() == null) {
                empleo.setAdapter(adapter);
            }
            //Trabajador
        } else {
            trabajador.setActivityActual(Menu.this);
            vacantes = trabajador.getVacancies(usuario.getId());
            //Obtenemos notificaciones de las contrataciones pendientes
            trabajador.getHiringRequest(String.valueOf(usuario.getId()));
        }
    }


    @Override
    public void onBackPressed() {

        //SI YA NO HAY FRAGMENTOS CARGADOS EN LA ACTIVITY
        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {

            confirmation();

        } else {

            super.onBackPressed();
        }
    }


    public void confirmation() {

        //DIALOGO DE CONFIRMACION PARA SALIR
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Menu.this);

        View mView = getLayoutInflater().inflate(R.layout.dialog_confirmation, null);

        mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        Usuario usuario = Usuario.getInstance();
                        usuario.logOut();
                        Menu.super.onBackPressed();
                    }
                }
        );

        mBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        //AGREGAMOS LA VISTA QUE PREVIAMENTE ELABORAMOS
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        //SETEAMOS COLOR A LOS BOTONES
        Button cancelar = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button aceptar = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (cancelar != null && aceptar != null) {
            cancelar.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            aceptar.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

    }

    //FUNCIÓN PARA ITERAR SOBRE LOS FRAGMENTOS DEL MODULO AUTH
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


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        MenuItem icon_notify = menu.findItem(R.id.item_notification);
        //usuario.setIconNotify(icon_notify);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.item_notification:

                Intent intent = new Intent(this, ContratacionActivity.class);
                intent.putExtra("manage_engagement", "false");
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //FUNCIÓN PARA CONTROLAR LOS EVENTOS CLICK DEL MENU: ENTRADAS = item seleccionado, SALIDAS = verdadero o falso
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        switch (id) {

            case R.id.item_home:

                if (fragment_inicio == null) {

                    fragment_inicio = new Fragment_inicio();
                }

                toolbar.setTitle(R.string.toolbar_inicio);
                manageFragments(fragment_inicio, "fragment_inicio", 2);

                break;

            case R.id.item_messages:

                if (fragment_chat == null) {

                    fragment_chat = new Fragment_chat();
                }

                toolbar.setTitle(R.string.toolbar_chat);
                manageFragments(fragment_chat, "fragment_chat", 3);


                break;

            case R.id.item_account:

                if (fragment_account == null) {

                    fragment_account = new Fragment_account();
                }

                toolbar.setTitle(R.string.toolbar_account);
                manageFragments(fragment_account, "fragment_account", 4);

                break;

        }
        return true;
    }


    //CLASE PARA ADMINISTRAR LA NAVEGACIÓN DE FRAGMENTOS DEL MENÚ: ENTRADAS = fragmento, etiqueta del fragmento
    public void manageFragments(Fragment fragment, String tag, int statusProcess) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //OBTENEMOS EL FRAGMENTO ACTUAL
        Fragment currentFragment = fragmentManager.findFragmentByTag(CURRENT_FRAGMENT);

        //ACTUALIZAMOS EL NOMBRE CON EL DEL FRAGMENTO ACTUAL
        CURRENT_FRAGMENT = tag;

        if (statusProcess == 1) {

            //PRIMERA CARGA DE FRAGMENTO
            fragmentTransaction.add(R.id.contenedor, fragment, tag);

        } else {

            //VERIFICAR SI NO SE TRATA DEL MISMO FRAGMENTO QUE YA SE ESTA MOSTRANDO
            if (!currentFragment.getTag().equalsIgnoreCase(CURRENT_FRAGMENT)) {
                //VERIFICAR QUE ESTE AÑADIDO EL FRAGMENTO A LA ACTIVIDAD
                if (fragment.isAdded()) {

                    fragmentTransaction
                            .hide(currentFragment)
                            .show(fragment);

                } else {
                    fragmentTransaction
                            .hide(currentFragment)
                            .add(R.id.contenedor, fragment, CURRENT_FRAGMENT);
                }

            }
        }

        fragmentTransaction.commit();
    }

    public static AlertDialog.Builder getDialogProgressBar(Context context) {

        final ProgressBar progressBar = new ProgressBar(context);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        progressBar.setLayoutParams(lp);

        if (builder != null) {

            builder = null;
        }

        builder = new AlertDialog.Builder(context);
        builder.setTitle("Cargando...");
        builder.setView(progressBar);

        return builder;
    }

    /*public static String startChat(final Context context, String filter, String contacto) {

        FirebaseDatabase database;
        DatabaseReference reference;


        final String[] key = new String[1];
        database = FirebaseDatabase.getInstance();
        reference = database.getReference(USERS_REFERENCE);

        //Obtenemos una referencia de la carpeta de usuario en la base de datos y filtramos usando el correo del trabajador para buscar su ID
        reference.orderByChild(filter).equalTo(contacto).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String key = null;
                UsuarioFirebase usuario = null;

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    key = child.getKey();
                    usuario = child.getValue(UsuarioFirebase.class);
                }
                //Pasamos la llave del trabajador para poder conectar un chat privado con el
                Intent intent = new Intent(context, MensajeriaActivity.class);
                intent.putExtra("keyReceptor", key);
                if (usuario != null) {
                    intent.putExtra("nameReceptor", usuario.getNombre());
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("errorFire", databaseError.toString());
            }
        });

        return key[0];
    }*/

}
