package com.example.katia.jobbox.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.model.Mensaje;
import com.example.katia.jobbox.model.MensajeSingleton;
import com.example.katia.jobbox.model.MensajeUsuario;
import com.example.katia.jobbox.model.adapter.MensajeAdaptador;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.katia.jobbox.activity.MainActivity.CHAT_REFERENCE;
import static com.example.katia.jobbox.activity.MainActivity.IMG_REFERENCE;


public class MensajeriaActivity extends AppCompatActivity {

    private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private TextView txtNombre;
    private EditText txtMensaje;
    private Button btnEnviar;
    private MensajeAdaptador adaptadorM;
    private ImageButton btnEnviarFoto;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private UploadTask uploadTask;
    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_CHANGED_PERFIL = 2;
    private String fotoPerfilCadena;

    private FirebaseAuth mAuth;
    private String NOMBRE_USUARIO;

    private String KEY_RECEPTOR;
    private String NAME_RECEPTOR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensaje);


        KEY_RECEPTOR = getIntent().getExtras().getString("keyReceptor", "empty");
        NAME_RECEPTOR = getIntent().getExtras().getString("nameReceptor", "");

        if (KEY_RECEPTOR == null || NAME_RECEPTOR == null) {

            Toast.makeText(MensajeriaActivity.this, "Error de credenciales Firebase", Toast.LENGTH_SHORT);
            finish();
        }

        fotoPerfil = (CircleImageView) findViewById(R.id.fotoPerfil);
        nombre = (TextView) findViewById(R.id.nombre);
        rvMensajes = (RecyclerView) findViewById(R.id.rvMensajes);
        txtMensaje = (EditText) findViewById(R.id.txtMensaje);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        btnEnviarFoto = (ImageButton) findViewById(R.id.btnEnviarFoto);
        fotoPerfilCadena = "";

        nombre.setText(NAME_RECEPTOR);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference(IMG_REFERENCE);
        mAuth = FirebaseAuth.getInstance();


        adaptadorM = new MensajeAdaptador(this);
        LinearLayoutManager l = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(l);
        rvMensajes.setAdapter(adaptadorM);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensajeEnviar = txtMensaje.getText().toString();
                if (!mensajeEnviar.isEmpty()) {
                    Mensaje mensaje = new Mensaje();
                    mensaje.setMensaje(mensajeEnviar);
                    mensaje.setContieneFoto(false);
                    //mensaje.setKeyEmisor(UsuarioFirebaseSingleton.getInstancia().getKeyUsuario());
                    //MensajeSingleton.getInstancia().crearMensaje(UsuarioFirebaseSingleton.getInstancia().getKeyUsuario(), KEY_RECEPTOR, mensaje);
                    txtMensaje.setText("");
                }
            }
        });

        btnEnviarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(i, "Selecciona una foto"), PHOTO_SEND);
            }
        });

        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("image/jpeg");
                i.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(i, "Selecciona una foto"), PHOTO_CHANGED_PERFIL);
            }
        });

        adaptadorM.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

            //Obtenemos una referencia a la carpeta del chat en la bd y entramos a la cerpeta con nuestro UID y después a la nuestras conversaciones
            /*FirebaseDatabase.getInstance().getReference(CHAT_REFERENCE).child(UsuarioFirebaseSingleton.getInstancia().getKeyUsuario()).
                    child(KEY_RECEPTOR).addChildEventListener(new ChildEventListener() {

                Map<String, UsuarioFirebaseAuthenticated> mapUsuariosTemporales = new HashMap<>();

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    final Mensaje mensaje = dataSnapshot.getValue(Mensaje.class);
                    final MensajeUsuario mensajeUsuario = new MensajeUsuario(dataSnapshot.getKey(), mensaje);
                    final int posicion = adaptadorM.addMensaje(mensajeUsuario);

                    //SI EXISTE EL USUARIO EN LA LISTA SOLO SE REFERENCIA EL OBJETO MENSAJE CON SU EMISOR CORRESPONDIENTE
                    //SE BUSCA AL USUARIO POR LA LLAVE OBTENIDA DEL OBJETO MENSAJE RECIEN CREADO, EN EL MAPA DE USUARIOS TEMPORALES
                    if (mapUsuariosTemporales.get(mensaje.getKeyEmisor()) != null) {
                        //SE RELACIONA EL MENSAJE RECUPERADO CON SU USUARIO EMISOR Y SE ACTUALIZA LA LISTA YA QUE EL OBJETO HA CAMBIADO
                        mensajeUsuario.setUsuarioAuthenticated(mapUsuariosTemporales.get(mensaje.getKeyEmisor()));
                        adaptadorM.actualizarMensaje(posicion, mensajeUsuario);
                        //SI NO EXISTE, SE RECUPERA AL USUARIO DE LA BD  MEDIANTE SU LLAVE Y SE AGREGAGA A LA LISTA DE USUARIOS
                        // FINALMENTE SE RELACIONA CON SU MSG CORRESPONDIENTE
                    } else {

                        //UNA VEZ OBTENIDA LA INFORMACIÓN DEL USUARIO SE AGREGA AL MAPA TEMPORAL DE USUARIOS CON SU PAREJA DE CLAVE: KEY - USUARIO
                        UsuarioFirebaseSingleton.getInstancia().getDataByKey(mensaje.getKeyEmisor(), new UsuarioFirebaseSingleton.GetUsuarioInterface() {
                            @Override
                            public void getUsuario(UsuarioFirebaseAuthenticated usuarioAuthenticated) {
                                mapUsuariosTemporales.put(mensaje.getKeyEmisor(), usuarioAuthenticated);
                                mensajeUsuario.setUsuarioAuthenticated(usuarioAuthenticated);
                                adaptadorM.actualizarMensaje(posicion, mensajeUsuario);
                            }

                            @Override
                            public void getError(String error) {
                                Toast.makeText(MensajeriaActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });*/

        verifyStoragePermissions(this);
    }

    private void setScrollbar() {
        rvMensajes.scrollToPosition(adaptadorM.getItemCount() - 1);
    }

    public static boolean verifyStoragePermissions(Activity activity) {
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int REQUEST_EXTERNAL_STORAGE = 1;
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_SEND && resultCode == RESULT_OK) {

            String path = data.getData().getPath();
            Uri file = Uri.fromFile(new File(path));

            final StorageReference fotoReferencia = storageReference.child(file.getLastPathSegment());

            uploadTask = fotoReferencia.putFile(file);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("error_media", e.toString());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri uri = taskSnapshot.getUploadSessionUri();
                    Mensaje mensaje = new Mensaje();
                    mensaje.setMensaje("El uuario ha enviado una foto");
                    mensaje.setUrlFoto(uri.toString());
                    mensaje.setContieneFoto(true);
                    //mensaje.setKeyEmisor(UsuarioFirebaseSingleton.getInstancia().getKeyUsuario());
                    //MensajeSingleton.getInstancia().crearMensaje(UsuarioFirebaseSingleton.getInstancia().getKeyUsuario(), KEY_RECEPTOR, mensaje);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                }
            });


        }/*else if(requestCode == PHOTO_PERFIL && resultCode == RESULT_OK){
            Uri file = data.getData();
            storageReference = storage.getReference("foto_perfil");//imagenes_chat
            final StorageReference fotoReferencia = storageReference.child(file.getLastPathSegment());
            fotoReferencia.putFile(file).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fotoReferencia.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        fotoPerfilCadena = uri.toString();
                        MensajeEnviar m = new MensajeEnviar(NOMBRE_USUARIO+" ha actualizado su foto de perfil",uri.toString(),NOMBRE_USUARIO,fotoPerfilCadena,"2",ServerValue.TIMESTAMP);
                        databaseReference.push().setValue(m);
                        Glide.with(MensajeriaActivity.this).load(uri.toString()).into(fotoPerfil);
                    }
                }
            });
        }*/
    }
}