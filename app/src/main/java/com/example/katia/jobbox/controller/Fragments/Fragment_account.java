package com.example.katia.jobbox.controller.Fragments;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.katia.jobbox.BuildConfig;
import com.example.katia.jobbox.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import net.gotev.uploadservice.UploadService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static com.example.katia.jobbox.controller.Menu.oficios;
import static com.example.katia.jobbox.controller.Menu.trabajador;
//import static com.example.katia.jobbox.controller.Menu.usuario;


public class Fragment_account extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //Fragment_inicio fragment_inicio;
    //Fragment_chat fragment_chat;
    //Fragment_account fragment_account;
    private ExpandableLayout layout;
    private int counter = 0;
    TextView txtParent, txtChild;
    EditText edtData, edtAnios;
    ArrayAdapter<String> adapter;
    Button btnActualizar;
    ImageView imgPerfil, iconParent, iconChild;
    private FrameLayout mVista;
    private static String APP_DIRECTORY = "Jobbox/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "photoProfile";
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;
    private String mPath;
    private String imageName;
    private File newFile;

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabButton;

    public Fragment_account() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_account.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_account newInstance(String param1, String param2) {
        Fragment_account fragment = new Fragment_account();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //INICIALIZAMOS LA BIBLIOTECA PARA SUBIDA DE IMAGENES AL SERVIDOR
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        layout = (ExpandableLayout) rootView.findViewById(R.id.expandable_layout);
        mVista = (FrameLayout) rootView.findViewById(R.id.contenedor);
        imgPerfil = rootView.findViewById(R.id.img_perfil);
        bottomNavigationView = (BottomNavigationView) container.findViewById(R.id.navigationView);
        fabButton = (FloatingActionButton) container.findViewById(R.id.my_fab);

        /*if (!usuario.getImg().equalsIgnoreCase("false")) {

            Picasso.get().load(usuario.getImg())
                    .fit()
                    .centerInside()
                    .into(imgPerfil);
        }*/

        counter = 0;

        layout.setRenderer(new ExpandableLayout.Renderer() {
            @Override
            public void renderParent(View view, Object fieldName, boolean isExpanded, int parentPosition) {

                txtParent = view.findViewById(R.id.txt_parent);
                txtParent.setText(((String) fieldName));
                iconParent = view.findViewById(R.id.ic_parent);

                switch (counter) {

                    case 1:
                        iconParent.setBackgroundResource(R.drawable.account);
                        break;

                    case 2:
                        iconParent.setBackgroundResource(R.drawable.ic_security);
                        break;

                    case 3:
                        /*if (usuario.getRol() == 4) {
                            iconParent.setBackgroundResource(R.drawable.ic_work);
                        }*/
                        break;


                    case 4:
                        iconParent.setBackgroundResource(R.drawable.ic_help);

                        break;
                }
            }

            @Override
            public void renderChild(View view, Object value, int parentPosition, int childPosition) {

                final String cadena = ((String) value);
                final boolean[] flag = {false};
                txtChild = view.findViewById(R.id.txt_child);
                iconChild = view.findViewById(R.id.ic_child);
                txtChild.setText(cadena);
                edtData = view.findViewById(R.id.edtData);
                btnActualizar = view.findViewById(R.id.btnActualizar);

                //evento cuando el edittext recibe el foco para desaparecer la barra lateral de menu
                /*edtData.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if (hasFocus) {
                            if (bottomNavigationView != null && bottomNavigationView.getVisibility() == View.VISIBLE) {
                                bottomNavigationView.setVisibility(View.GONE);
                            }
                        } else {
                            if (bottomNavigationView != null && bottomNavigationView.getVisibility() == View.INVISIBLE) {
                                bottomNavigationView.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

                //evento cuando el edittext es typeado para introducit texyo para desaparecer la barra lateral de menu
                edtData.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN)) {

                            if (bottomNavigationView != null && bottomNavigationView.getVisibility() == View.VISIBLE) {
                                bottomNavigationView.setVisibility(View.GONE);
                            }

                            return false;
                        }
                        return true;
                    }
                });

                //evento cuando el edittext termina de ser editado para que la barra lateral de menu vuelva a aparecer
                edtData.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                        if (actionId == EditorInfo.IME_ACTION_DONE) {

                            bottomNavigationView.setVisibility(View.VISIBLE);
                            return false;
                        }

                        return true;
                    }
                });*/

                btnActualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String data = edtData.getText().toString();
                        String field = "";

                        if (validar(1)) {
                            if (edtData.getHint().toString().contains("telefono")) {
                                field = "telefono";
                                //usuario.updateData("users", field, data, usuario.getCorreo());
                            } else {
                                field = "password";
                                //DEBEMOS ACTUALIZAR TAMBIÉN LAS CREDENCIALES DE FIREBASE POR LA CUESTIÓN DEL CHAT
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final String finalField = field;
                                user.updatePassword(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            //usuario.updateData("users", finalField, data, usuario.getCorreo());
                                        } else {
                                            Toast.makeText(getActivity(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            edtData.setText("");
                        }
                    }
                });

                if (cadena.contains("Telefono") || cadena.contains("contraseña")) {

                    iconChild.setImageResource(R.drawable.edit);

                } else if (cadena.contains("oficio")) {

                    /*if (usuario.getRol() == 4) {
                        iconChild.setImageResource(R.drawable.ic_add);
                    }*/

                } else if (cadena.contains("comentario")) {
                    iconChild.setImageResource(R.drawable.ic_comment);
                }

                iconChild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (cadena.contains("Telefono") || cadena.contains("contraseña")) {

                            if (!flag[0]) {
                                flag[0] = true;
                                edtData.setVisibility(View.VISIBLE);
                                btnActualizar.setVisibility(View.VISIBLE);

                                if (cadena.contains("contraseña")) {
                                    edtData.setHint(R.string.hint_account_password);
                                    edtData.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                } else {
                                    edtData.setHint(R.string.hint_account_cellphone);
                                    edtData.setInputType(InputType.TYPE_CLASS_PHONE);
                                }

                            } else {
                                flag[0] = false;
                                edtData.setVisibility(View.GONE);
                                btnActualizar.setVisibility(View.GONE);
                            }


                        } else if (cadena.contains("oficio")) {

                            showDialog(1);

                        } else if (cadena.contains("comentario")) {

                            showDialog(2);
                        }
                    }
                });

            }

        });

        //PINTAMOS LOS CAMPOS DEL ACTIVITY
        layout.addSection(getSection());
        layout.addSection(getSection());
        //LA SECCIÓN TRES SOLO ESTA DESTINADA PARA TRABAJADORES
        /*if (usuario.getRol() == 4) {
            layout.addSection(getSection());
        } else {
            counter++;
        }*/
        layout.addSection(getSection());


        imgPerfil.setOnClickListener(this);
        return rootView;
    }

    //SECCIONAMOS LAS PESTAÑAS DEL LAYOUT CON SU CORRESPONDIENTE CONTENIDO
    private Section<String, String> getSection() {

        Section<String, String> section = new Section<>();
        List<String> list = new ArrayList<>();

        switch (counter) {

            case 0:

                section.parent = "Datos personales";
                //list.add("Correo: " + usuario.getCorreo());
                //list.add("Telefono: " + usuario.getTelefono());

                break;

            case 1:
                section.parent = "Seguridad y privacidad";
                list.add("Cambiar contraseña");
                break;

            case 2:

                /*if (usuario.getRol() == 4) {
                    section.parent = "Oficios";
                    list.add("Agregar nuevo oficio");
                }*/

                break;
            case 3:

                section.parent = "Ayuda";
                list.add("Enviar comentario");
                break;


        }
        if (!list.isEmpty()) {
            section.children.addAll(list);
        }
        counter++;
        return section;
    }


    private void showDialog(final int form) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView;
        final Spinner spn;

        if (form == 1) {
            mView = getLayoutInflater().inflate(R.layout.form_add_job, null);

            edtAnios = mView.findViewById(R.id.edtComentarios);

            spn = mView.findViewById(R.id.ratingBar);

            adapter = new ArrayAdapter<String>(getActivity(), R.layout.textview_spinner, oficios);
            ((ArrayAdapter) adapter).setDropDownViewResource(R.layout.textview_spinner);
            spn.setAdapter(adapter);

        } else {
            mView = getLayoutInflater().inflate(R.layout.form_send_comment, null);

            edtAnios = mView.findViewById(R.id.edtComment);

            spn = mView.findViewById(R.id.spnAffair);

            //GENERAMOS UN ARRAY CON LOS VALORES DE LOS ASUNTOS POSIBLES PARA UN COMENTARIO
            ArrayList<String> asuntos = new ArrayList<>();

            asuntos.add("Queja");
            asuntos.add("Felicitacion");
            asuntos.add("Comentario");

            adapter = new ArrayAdapter<String>(getActivity(), R.layout.textview_spinner, asuntos);
            ((ArrayAdapter) adapter).setDropDownViewResource(R.layout.textview_spinner);
            spn.setAdapter(adapter);
        }

        mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (validar(2)) {

                            if (form == 1) {

                                String anios = edtAnios.getText().toString();
                                String idOficio = (spn.getSelectedItemPosition() + 1) + "";

                                trabajador.setActivityActual(getActivity());
                                //trabajador.registerNewJob(anios, idOficio, usuario.getId() + "");

                            } else {

                                String asunto = spn.getSelectedItem().toString();
                                String comentario = edtAnios.getText().toString();

                                //usuario.registerNewComment(asunto, comentario, usuario.getId() + "");
                            }
                        } else {

                            Toast.makeText(getActivity(), "Por favor complete los campos", Toast.LENGTH_SHORT).show();
                        }
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
            cancelar.setTextColor(getResources().getColor(R.color.color_error));
            aceptar.setTextColor(getResources().getColor(R.color.color_success));
        }
    }

    //Evento click de la imagen
    @Override
    public void onClick(View v) {

        imgPerfil.setEnabled(true);
        //REVISAMOS LOS PERMISOS
        if (mayRequestStoragePermission()) {
            showOptions();
        }
    }

    //Función para mostrar una ventana modal en la cual se le permitira acceder a la galería del usuario y seleccionadar una imagen
    //Entradas: nada
    //Salidas: nada
    private void showOptions() {

        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Seleccione una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface loading, int which) {
                if (option[which] == "Tomar foto") {
                    //SOLICITAR PERMISOS PARA TOMAR FOTOGRAFÍAS Y ALMACENARAS EN EL DISPOSITIVO
                    openCamera();
                    //Toast.makeText(getActivity(), "Función en desarrollo", Toast.LENGTH_SHORT).show();
                } else if (option[which] == "Elegir de galeria") {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    //intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent.createChooser(intent, "Selecciona una imagen de perfil"), SELECT_PICTURE);
                } else {
                    loading.dismiss();
                }
            }
        });

        builder.show();
    }


    //Función para ejecutar la camara y permitir capturar una foto
    //Entradas: nada
    //Salidas: nada
    private void openCamera() {

        //CREAMOS UN NUEVO ARCHIVO PARA VERIFICAR LA UBICACIÓN DE LA CARPETA EN LA QUE VAMOS A ALMACENAR LA FOTOS EN LA MEMORIA EXTERNA
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();
        //VERIFICAMOS SI EL DIRECTORIO EN DONDE VAMOS A GUARDAR YA EXISTE, DE LO CONTRARIO LO CREAMOS
        if (!isDirectoryCreated)
            //SI NO ES ASI LO CREAMOS
            isDirectoryCreated = file.mkdirs();

        if (isDirectoryCreated) {

            //ASIGNAMOS PATH Y NOMBRE AL ARCHIVO
            imageName = setNombreFoto();
            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName;

            //CREAMOS EL ARCHIVO FINAL CON TODAS LAS CARACTERÍSTICAS YA ESTABLECIDAS COMO EL PATH Y EL NOMBRE
            newFile = new File(mPath);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                String authority = getActivity().getApplicationContext().getPackageName() + ".provider";
                Uri imageUri = FileProvider.getUriForFile(getActivity(), authority, newFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            }
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    //Función para crear un nombre único para una nueva fotografía
    //Entradas: nada
    //Salidas: cadena con nombre generado
    public String setNombreFoto() {

        String nameFile = "";
        // Producir nuevo int aleatorio entre 0 y 99
        Random aleatorio = new Random(System.currentTimeMillis());
        int num = aleatorio.nextInt(1000);
        //DAMOS FORMATO AL NOMBRE CON EL VALOR ALEATORIO AGREGADO
        nameFile = "JobboxPhoto" + String.valueOf(num) + ".jpg";
        // Refrescar datos aleatorios
        aleatorio.setSeed(System.currentTimeMillis());

        return nameFile;
    }

    //Función que recibe los resultados invocados desde funciones que utilizan el procedimiento StartActivityForResult para realizar acciones
    //Entradas: codigo que identifica el resultado que esta recibiendo, status de la actividad realizada y un objeto con la información recolectada
    //Salidas: nada
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Uri[] uriResult = {null};
        //super.onActivityResult(requestCode, resultCode, data);
        //imgPerfil.setRotation(0.0f);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(getActivity(),
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                    uriResult[0] = uri;
                                }
                            });


                    Picasso.get().load(newFile)
                            //fit() NOS PERMITE HACER UN RESIZE DE LA IMÁGEN A LO QUE ESPERA EL CONTENEDOR EN DONDE VA A ESTAR
                            .fit()
                            //CenterInside() NOS PERMITE MOSTRAR TODA LA IMAGEN DENTRO DEL CONTENEOR AUNQUE AVECES NO ABARCA TODO ESTE
                            //CenterCrop POR SU PARTE ABARCA TODO EL CONTENEDOR PERO PUEDE QUE NO SE MUESTRE TODA LA IMAGEN
                            .centerInside()
                            .into(imgPerfil);

                    break;

                case SELECT_PICTURE:
                    //Bitmap bitmap;
                    uriResult[0] = data.getData();
                    /*try {
                        //manera de recuperar la imagen de un Uri
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        mPath = getStringImagen(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    //mPath = getRealPathFromURI_API19(getActivity().getApplicationContext(), uriResult[0], "Image");
                    //newFile = new File(uriResult[0].getPath());
                    mPath = getImagePath(uriResult[0]);
                    imageName = setNombreFoto();
                    Picasso.get().load(uriResult[0])
                            .fit()
                            .centerInside()
                            .into(imgPerfil);

                    //String nameFoto = imageName.substring(0, 5);
                    //float rotacion = imgPerfil.getRotation();

                    //BUG EN LA ROTACIÓN DE LA FOTO
                    /*if (nameFoto.equalsIgnoreCase("UPayP")) {
                        if (rotacion == 0.0)
                            imgPerfil.setRotation(270.0f);
                    } else {
                        if (rotacion == 270.0)
                            imgPerfil.setRotation(90.0f);
                    }*/
                    //imgPerfil.setRotation(270.0f);
                    break;

                default:

                    break;

            }
            //usuario.setActivityActual(getActivity());
            //SUBIMOS LA IMAGEN AL SERVIDOR
            //usuario.registerNewPhoto(imageName, mPath, usuario.getId() + "");
        }
    }

    /*public String getStringImagen(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }*/


    public String getImagePath(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();


        return path;
    }


    //FUNCION PARA LOS PERMISOS CON BASE A LAS VERSIONES POSTERIORES A LA 6 DE ANDROID
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(getActivity(), "Permisos aceptados", Toast.LENGTH_SHORT).show();
                showOptions();
            }
        } else {
            //SI RECHAZO LOS PERMISOS, LA PRÓXIMA VEZ QUE INTENTE USAR LA APP SE MOSTRARÁ UNA EXPLICACIÓN DEL POR QUE SE PIDEN ESOS PERMISOS
            showExplanation();
        }
    }


    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface loading, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface loading, int which) {
                loading.dismiss();
                getActivity().finish();
            }
        });

        builder.show();
    }

    //SOLICITAMOS LOS PERMISOS PARA TOMAR FOTOS Y USAR MULTIMEDIA DEL DISPOSITIVO
    private boolean mayRequestStoragePermission() {

        //VERIFICAMOS LA VERSIÓN DEL SDK DE ANDROID SI ES MAYOR O NO A LA VERSIÓN 6
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        //EVALUAR SI ES NECESARIO DAR UNA EXPLICACIÓN DE LA SOLICITUD DE PERMISOS, ESTO SE DA CUANDO ANTES SE RECHAZARÓN
        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))) {

            Snackbar.make(mVista, "Los aplicación necesita permisos para acceder a su galería",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            }).show();
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }


    //FUNCIÓN PARA VALIDAR LOS CAMPOS DEL FORMULARIO DE REGISTRO, SALIDA: VERDERO O FALSO, SEGÚN SEA EL CASO
    public boolean validar(int form) {


        if (form == 1) {

            edtData.setError(null);

            String data = edtData.getText().toString().trim();

            if (TextUtils.isEmpty(data)) {
                edtData.setError("Campo Obligatorio");
                edtData.requestFocus();
                return false;
            }


            if (edtData.getHint().toString().contains("telefono")) {

                if (!verify("onlydigits", data)) {
                    edtData.setError("El telefono solo debe contener números");
                    edtData.requestFocus();
                    return false;
                }

                if (!verify("cel", data)) {

                    edtData.setError("El telefono debe contener 10 digitos");
                    edtData.requestFocus();
                    return false;
                }


            } else {

                if (TextUtils.isEmpty(data) || (data.length() < 8)) {
                    edtData.setError("La contraseña debe contener por lo menos 8 caractéres");
                    edtData.requestFocus();
                    return false;

                } else if (!data.matches(".*[0-9].*") || !data.matches(".*[a-zA-Z].*")) {
                    edtData.setError("Utilice números y letras para una mayor seguridad");
                    edtData.requestFocus();
                    return false;
                }

            }

        } else {
            //VALIDAR EL FORMULARIO EMERGENTE
            edtAnios.setError(null);

            String aniosExp = edtAnios.getText().toString();

            if (TextUtils.isEmpty(aniosExp)) {
                edtAnios.setError("Campo Obligatorio");
                edtAnios.requestFocus();
                return false;
            }

        }

        return true;
    }


    //FUNCIÓN PARA VERIICAR EL FORMATO DE LOS CAMPOS: ENTRADA = Tipo y Variable, SALIDA: TRUE O FALSE SEGÚN SEA EL CASO
    private boolean verify(String opc, String var) {

        switch (opc) {


            case "onlydigits":

                if (var.matches(".*[a-zA-Z].*")) {

                    return false;
                }

                break;

            case "cel":

                if ((var.length() != 10)) {
                    return false;
                }

                break;

        }

        return true;
    }

}
