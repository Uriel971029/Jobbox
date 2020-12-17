package com.example.katia.jobbox.model;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.interfaces.TrabajadorAPI;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.katia.jobbox.activity.MainActivity.URL_SERVER;

public class Empleo {

    private int idOficio;
    private int idCliente;
    private float rating_cliente;
    private int idTrabajador;
    private int idVacante;
    private String emailCliente;
    private String titulo;
    private String descripcion;
    private String fecha;
    private String lugar;
    private int img;
    private Activity activity;
    private ArrayAdapter<String> adapter;
    private RecyclerView.Adapter adapterRecycler;
    private RecyclerView rv;
    ArrayList<String> listEmpleos = new ArrayList<>();
    ArrayList<Empleo> listEmpleosDetalles = new ArrayList<>();
    ArrayList<EmpleoModel> listEmpleosD = new ArrayList<>();


    public Empleo(String titulo, String descripcion, String lugar, int idOficio, int idCliente, String emailCliente, int idVacante) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.lugar = lugar;
        //this.activityActual = activityActual;
        this.emailCliente = emailCliente;
        this.idOficio = idOficio;
        this.idCliente = idCliente;
        this.idVacante = idVacante;

    }

    public Empleo(int idOficio, String titulo, String descripcion) {
        this.idOficio = idOficio;
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public Empleo(Activity activity) {
        this.activity = activity;
    }

    public int getIdOficio() {
        return idOficio;
    }

    public void setIdOficio(int idOficio) {
        this.idOficio = idOficio;
    }

    public int getIdVacante() {
        return idVacante;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public float getRating_cliente() {
        return rating_cliente;
    }

    public void setRating_cliente(float rating_cliente) {
        this.rating_cliente = rating_cliente;
    }

    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setListEmpleosDetalles(ArrayList<Empleo> listEmpleosDetalles) {
        this.listEmpleosDetalles = listEmpleosDetalles;
    }

    public void setAdapterRecycler(RecyclerView.Adapter adapterRecycler) {
        this.adapterRecycler = adapterRecycler;
    }

    public void setRv(RecyclerView rv) {
        this.rv = rv;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

    public ArrayList<String> getList() {

        if (!listEmpleos.isEmpty()) {

            listEmpleos.clear();
        }

        if (!listEmpleosDetalles.isEmpty()) {

            listEmpleosDetalles.clear();
        }

        Retrofit retrofit = RetrofitSingleton.getInstance();
        TrabajadorAPI trabajadorAPI = retrofit.create(TrabajadorAPI.class);

        Call<ArrayList<EmpleoModel>> call = trabajadorAPI.getOficios();
        call.enqueue(new Callback<ArrayList<EmpleoModel>>() {
            @Override
            public void onResponse(Call<ArrayList<EmpleoModel>> call, Response<ArrayList<EmpleoModel>> response) {
                if (response.isSuccessful()) {
                    Log.i("result", response.body().toString());
                    listEmpleosD = response.body();
                    listEmpleos.addAll(getListTitles(listEmpleosD));
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("Error", response.body().toString());
                }
            }
            @Override
            public void onFailure(Call<ArrayList<EmpleoModel>> call, Throwable t) {
                Log.e("Error", t.getMessage());
            }
        });

        return listEmpleos;
    }


    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }

    public void setAdapter(ArrayAdapter<String> adapter) {
        this.adapter = adapter;
    }

    //FUNCION PARA REALIZAR LA PETICIÓN DE ELEMENTOS AL SERVIDOR PARA LLENAR LAS LISTAS DE DIRECCIONES EN EL FRMULARIO DE REGISTRO: ENTRADA = identificador y tipo de busqueda (por municipios o ciudades)
    public void makeRequest(final String typeRequest) {

        Connection connection;
        String url = "Controlador/controlador_oficios.php";

        final ArrayList<String> lista = new ArrayList<>();
        //final String typeRequest = params.get(0);
        final String[] response = new String[1];

        connection = new Connection(activity) {
            @Override
            public void OnLoadScreen(String result) {

                //LIMPIAR CARACTERES ASCII PRESENTES EN ALGUNAS CADENAS JSON
                response[0] = result.replaceAll("[^\\x00-\\x7F]", "");
                try {

                    ArrayList<String> auxList = new ArrayList<>();

                    JSONArray jsonArray = new JSONArray(response[0]);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        lista.add(jsonArray.getString(i));
                    }

                    for (int i = 0; i < lista.size(); i++) {

                        if (!auxList.isEmpty()) {
                            auxList.clear();
                        }
                        //pasamos paquete por paquete de datos
                        auxList.addAll(Arrays.asList(lista.get(i).split(",")));
                        //le damos formato para poder pasar la información a un objeto de empleo
                        formatearCadena(auxList);

                        if (typeRequest.equalsIgnoreCase("read_jobs")) {
                            //idOficio = Integer.parseInt(auxList.get(4).substring(1, auxList.get(4).length()));
                            Empleo empleo = new Empleo(Integer.parseInt(auxList.get(0)), auxList.get(1), auxList.get(2));
                            empleo.setImg();
                            //Se guardan todos los empleos disponibles con sus detalles
                            //listEmpleosDetalles.add(empleo);

                        }
                    }
                    //Obtenemos solo la lista de nombres de los empleos
                    //listEmpleos.addAll(getListTitles(listEmpleosDetalles));
                    send(typeRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        connection.execute(URL_SERVER + url);

    }

    private void send(String typeRequest) {

        switch (typeRequest) {


            case "add":

                break;

            case "read_jobs":

                //Actualizamos la lista de nombres de empleos
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

                //Actualizamos la lista de empleos con detalles
                if (adapterRecycler != null) {
                    rv.setAdapter(adapterRecycler);
                    adapterRecycler.notifyDataSetChanged();
                }
                break;
        }
    }


    private void formatearCadena(ArrayList<String> lista) {

        ArrayList<String> newList = new ArrayList<>();
        int first = 0;
        int last = 0;

        for (int i = 0; i < lista.size(); i++) {

            first = lista.get(i).indexOf(":");
            last = lista.get(i).length() - 1;

            if (lista.get(i).contains("{") || lista.get(i).contains("}")) {

                if (lista.get(i).contains("&")) {

                    lista.set(i, lista.get(i).replaceAll("[\\&]", ","));
                }

                newList.add(i, lista.get(i).replaceAll("[\\{\\}]", "").substring(first + 1, last - 1));

            } else {

                newList.add(i, lista.get(i).substring(first + 2, last));

            }
        }

        lista.clear();
        lista.addAll(newList);
    }


    public ArrayList<String> getListTitles(ArrayList<EmpleoModel> oficios) {

        ArrayList<String> listJobs = new ArrayList<>();

        for (int i = 0; i < oficios.size(); i++) {

            listJobs.add(oficios.get(i).getNombre());
        }

        return listJobs;
    }

    @Override
    public String toString() {
        return titulo + "," + descripcion + "," + lugar + "," + idOficio + "," + idCliente + "," + idTrabajador + "," + fecha;
    }
}
