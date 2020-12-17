package com.example.katia.jobbox.model;

import com.google.gson.annotations.SerializedName;

public class UsuarioModel {

    private int id;
    private String idNotification;
    private int rol;
    private String nombre;
    private String apePaterno;
    private String apeMaterno;
    private String telefono;
    private String ruta_foto;
    private String correo;
    private String password;
    @SerializedName("success")
    private int verified;
    private int opc;
    public static UsuarioModel usuario;


    public static UsuarioModel getUsuario() {
        return usuario;
    }

    public String getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(String idNotification) {
        this.idNotification = idNotification;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRuta_foto() {
        return ruta_foto;
    }

    public void setRuta_foto(String ruta_foto) {
        this.ruta_foto = ruta_foto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public static void setUsuario(UsuarioModel usuarioModel){
        usuario = usuarioModel;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static UsuarioModel getInstance(){
        if(usuario == null){
            usuario = new UsuarioModel();
        }
        return usuario;
    }

    public int getOpc() {
        return opc;
    }

    public void setOpc(int opc) {
        this.opc = opc;
    }

    @Override
    public String toString() {
        return "UsuarioModel{" +
                "id=" + id +
                ", idNotification='" + idNotification + '\'' +
                ", rol=" + rol +
                ", nombre='" + nombre + '\'' +
                ", apePaterno='" + apePaterno + '\'' +
                ", apeMaterno='" + apeMaterno + '\'' +
                ", telefono='" + telefono + '\'' +
                ", ruta_foto='" + ruta_foto + '\'' +
                ", verified=" + verified +
                '}';
    }
}
