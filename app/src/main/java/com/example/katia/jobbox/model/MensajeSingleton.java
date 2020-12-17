package com.example.katia.jobbox.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.katia.jobbox.activity.MainActivity.CHAT_REFERENCE;

public class MensajeSingleton {

    private static MensajeSingleton mensajeriaDAO;

    private FirebaseDatabase database;
    private DatabaseReference referenceMensajeria;

    public static MensajeSingleton getInstancia() {
        if (mensajeriaDAO == null) mensajeriaDAO = new MensajeSingleton();
        return mensajeriaDAO;
    }

    private MensajeSingleton() {
        database = FirebaseDatabase.getInstance();
        referenceMensajeria = database.getReference(CHAT_REFERENCE);
    }

    public void crearMensaje(String keyEmisor, String keyReceptor, Mensaje mensaje) {
        //CREAMOS UNA COPIA DE LOS MENSAJES PARA AMBAS PARTES DEL CHAT
        DatabaseReference referenceEmisor = referenceMensajeria.child(keyEmisor).child(keyReceptor);
        DatabaseReference referenceReceptor = referenceMensajeria.child(keyReceptor).child(keyEmisor);
        referenceEmisor.push().setValue(mensaje);
        referenceReceptor.push().setValue(mensaje);
    }

}