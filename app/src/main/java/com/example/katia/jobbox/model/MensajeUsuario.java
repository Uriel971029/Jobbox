package com.example.katia.jobbox.model;


import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.Locale;


public class MensajeUsuario {

    private String key;
    private Mensaje mensaje;
    //private UsuarioFirebaseAuthenticated usuarioAuthenticated;

    public MensajeUsuario(String key, Mensaje mensaje) {
        this.key = key;
        this.mensaje = mensaje;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Mensaje getMensaje() {
        return mensaje;
    }

    public void setMensaje(Mensaje mensaje) {
        this.mensaje = mensaje;
    }

    public long getCreatedTimestampLong(){
        return (long) mensaje.getCreatedTimestamp();
    }

    /*public UsuarioFirebaseAuthenticated getUsuarioAuthenticated() {
        return usuarioAuthenticated;
    }*/

    /*public void setUsuarioAuthenticated(UsuarioFirebaseAuthenticated usuarioAuthenticated) {
        this.usuarioAuthenticated = usuarioAuthenticated;
    }*/

    public String fechaDeCreacionDelMensaje(){
        Date date = new Date(getCreatedTimestampLong());
        PrettyTime prettyTime = new PrettyTime(new Date(),Locale.getDefault());
        return prettyTime.format(date);

    }

}