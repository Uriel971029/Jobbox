package com.example.katia.jobbox.model;

import com.example.katia.jobbox.R;

public class Contratacion {

    private int idTrabajador;
    //idCliente
    private int users_idusuario;
    private int idcontratacion;
    //id de la vacante relacionada con la contratación
    private int idVacante;
    //Nombre del segundo participante de la contratación según corresponda
    private String nombre_participante;
    private int idOficio;
    private String empleo;
    private Double costo;
    private String descripcion;
    private String fecha;
    private int img;
    private int status;

    public Contratacion() {
    }

    public Contratacion(int users_idusuario, int idTrabajador, int idcontratacion, String fecha, Double costo, String descripcion, int idOficio, int status) {
        this.users_idusuario = users_idusuario;
        this.idTrabajador = idTrabajador;
        this.idcontratacion = idcontratacion;
        this.costo = costo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.idOficio = idOficio;
        this.status = status;
    }

    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public String getNombre_participante() {
        return nombre_participante;
    }

    public void setNombre_participante(String nombre_participante) {
        this.nombre_participante = nombre_participante;
    }

    public void setIdCliente(int users_idusuario) {
        this.users_idusuario = users_idusuario;
    }

    public void setIdVacante(int idVacante) {
        this.idVacante = idVacante;
    }

    public void setIdOficio(int idOficio) {
        this.idOficio = idOficio;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEmpleo() {
        return empleo;
    }

    public void setEmpleo(String empleo) {
        this.empleo = empleo;
    }

    public void setEmpleoName() {


        switch (this.idOficio) {

            case 1:

                this.empleo = "Herrería";

                break;


            case 2:

                this.empleo = "Fontanería";

                break;


            case 3:

                this.empleo = "Albañilería";


                break;


            case 4:

                this.empleo = "Electricista";

                break;

        }

    }

    public int getImg() {
        return img;
    }

    public void setImg() {

        switch (this.idOficio) {

            case 1:
                img = R.drawable.herrero;
                break;

            case 2:
                img = R.drawable.fontanero;
                break;

            case 3:
                img = R.drawable.albanil;
                break;

            case 4:
                img = R.drawable.electricista;
                break;

            default:

                break;
        }
    }

    public int getStatus() {
        return status;
    }

    public int getIdcontratacion() {
        return idcontratacion;
    }

    public void setIdcontratacion(int idcontratacion) {
        this.idcontratacion = idcontratacion;
    }

    @Override
    public String toString() {

        return costo + "," + descripcion + "," + users_idusuario + "," + idTrabajador + "," + idOficio + "," + idVacante;
    }
}
