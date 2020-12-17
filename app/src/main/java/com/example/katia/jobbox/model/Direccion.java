package com.example.katia.jobbox.model;

import android.app.Activity;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static com.example.katia.jobbox.activity.MainActivity.URL_SERVER;

public class Direccion {

    private ArrayList<String> colonias = new ArrayList<>();
    private ArrayList<String> municipios = new ArrayList<>();
    private Activity activity;
    private ArrayAdapter<String> adapterM, adapterC;

    private String municipio;
    private String colonia;
    private double latitud;
    private double longitud;
    private int idTrabajador;

    public Direccion(Activity activity) {

        this.activity = activity;
    }

    //CONSTRUCTOR PARA USAR EN EL MODULO DE LOCALIZACIÓN EN EL MAPA
    public Direccion(int idTrabajador, Double latitud, Double longitud) {

        this.idTrabajador = idTrabajador;
        this.latitud = latitud;
        this.longitud = longitud;
    }


    public void setAdapterM(ArrayAdapter<String> adapterM) {
        this.adapterM = adapterM;
    }

    public void setAdapterC(ArrayAdapter<String> adapterC) {
        this.adapterC = adapterC;
    }

    public void setColonias(ArrayList<String> colonias) {
        this.colonias = colonias;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public int getIdTrabajador() {
        return idTrabajador;
    }

    public ArrayList<String> getList(int opc, String index) {

        if (opc == 1) {

            if (municipios.isEmpty()) {

                makeRequest(opc, index);
            }
            return municipios;
        } else {

            makeRequest(opc, index);
            return colonias;
        }
    }


    //FUNCION PARA REALIZAR LA PETICIÓN DE ELEMENTOS AL SERVIDOR PARA LLENAR LAS LISTAS DE DIRECCIONES EN EL FRMULARIO DE REGISTRO: ENTRADA = identificador y tipo de busqueda (por municipios o ciudades)
    public void makeRequest(final int opc, String index) {

        Connection connection;
        String url = "Controlador/controlador_direcciones.php";

        final ArrayList<String> lista = new ArrayList<>();
        //final String typeRequest = params.get(0);
        final String[] response = new String[1];

        connection = new Connection(activity) {
            @Override
            public void OnLoadScreen(String result) {

                //LIMPIAR CARACTERES ASCII PRESENTES EN ALGUNAS CADENAS JSON
                response[0] = result.replaceAll("[^\\x00-\\x7F]", "");
                try {

                    JSONArray jsonArray = new JSONArray(response[0]);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        lista.add(jsonArray.getString(i));
                    }

                    if (opc == 1) {

                        municipios.addAll(lista);
                        adapterM.notifyDataSetChanged();
                    } else {
                        if (!colonias.isEmpty()) {
                            colonias.clear();
                        }
                        colonias.addAll(lista);
                        adapterC.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        connection.execute(URL_SERVER + url, String.valueOf(opc), String.valueOf(index));
    }
}








