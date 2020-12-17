package com.example.katia.jobbox.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.activity.MapaActivity;
import com.example.katia.jobbox.model.Contratacion;
import com.example.katia.jobbox.model.Empleo;
import com.example.katia.jobbox.model.Usuario;

import java.util.List;

//import static com.example.katia.jobbox.controller.Menu.startChat;

public class EmpleoAdapter extends RecyclerView.Adapter<EmpleoAdapter.EmpleoViewHolder> {

    List<Empleo> listVacantesEmpleo;
    List<Empleo> listOficios;
    int rol;
    EditText edtCosto, edtDetalles;
    Button btnEnviar;
    String costo, detalles;


    public EmpleoAdapter(List<Empleo> listVacantesEmpleo) {

        this.listVacantesEmpleo = listVacantesEmpleo;
        this.rol = 4;
    }

    public EmpleoAdapter(List<Empleo> listOficios, int rol) {

        this.listOficios = listOficios;
        this.rol = rol;
    }

    @NonNull
    @Override
    public EmpleoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_vacancy, parent, false);
        if (rol == 3) {
            v.findViewById(R.id.txtEmailClient).setVisibility(View.INVISIBLE);
        }
        return new EmpleoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpleoViewHolder holder, int position) {

        if (listVacantesEmpleo != null) {

            if (listVacantesEmpleo.get(position) != null) {

                holder.img.setImageResource(listVacantesEmpleo.get(position).getImg());
                holder.titulo.setText(listVacantesEmpleo.get(position).getTitulo());
                holder.descripcion.setText(listVacantesEmpleo.get(position).getDescripcion());
                holder.fecha.setText(listVacantesEmpleo.get(position).getFecha());
                holder.lugar.setText(listVacantesEmpleo.get(position).getLugar());
                holder.emailCliente.setText(listVacantesEmpleo.get(position).getEmailCliente());
                //rating del cliente en campo oculto
                holder.ratingClient.setText(listVacantesEmpleo.get(position).getRating_cliente() + "");
                holder.idOficio.setText(listVacantesEmpleo.get(position).getIdOficio() + "");
                holder.idCliente.setText(listVacantesEmpleo.get(position).getIdCliente() + "");
                holder.idVacante.setText(listVacantesEmpleo.get(position).getIdVacante() + "");
            }

        } else if (listOficios != null) {

            holder.img.setImageResource(listOficios.get(position).getImg());
            holder.titulo.setText(listOficios.get(position).getTitulo());
            holder.descripcion.setText(listOficios.get(position).getDescripcion());
            holder.fecha.setText(listOficios.get(position).getIdOficio() + "");
            holder.lugar.setText("");

        }

    }

    @Override
    public int getItemCount() {

        if (listVacantesEmpleo != null) {
            return listVacantesEmpleo.size();

        } else {
            return listOficios.size();
        }

    }

    public class EmpleoViewHolder extends RecyclerView.ViewHolder {


        ImageView img, icon;
        TextView titulo, descripcion, fecha, lugar, emailCliente, txtNombre, txtServicios, idOficio, idCliente, idVacante, ratingClient;
        RatingBar ratingBar;


        public EmpleoViewHolder(@NonNull final View itemView) {
            super(itemView);

            idOficio = itemView.findViewById(R.id.txtIdOficio);
            idCliente = itemView.findViewById(R.id.txtIdCliente);
            idVacante = itemView.findViewById(R.id.txtIdVacante);
            ratingClient = itemView.findViewById(R.id.txtRatingClient);
            img = itemView.findViewById(R.id.imagen);
            titulo = itemView.findViewById(R.id.txtIdContratacion);
            icon = itemView.findViewById(R.id.btnPagar);
            lugar = itemView.findViewById(R.id.txtCosto);
            fecha = itemView.findViewById(R.id.txtFecha);
            emailCliente = itemView.findViewById(R.id.txtEmailClient);
            descripcion = itemView.findViewById(R.id.txtDescripcion);
            final AlertDialog[] dialog = new AlertDialog[1];


            if (rol == 3) {
                icon.setImageResource(R.drawable.ic_search);
            } else {
                icon.setImageResource(R.drawable.ic_chat);
            }

            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (rol == 3) {
                        //HACE LO MISMO QUE EL EVENTO DE CLICK EN LA IMAGEN
                        Intent intent = new Intent(itemView.getContext(), MapaActivity.class);
                        intent.putExtra("idOficio", getAdapterPosition() + 1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        itemView.getContext().getApplicationContext().startActivity(intent);

                    } else {
                        //startChat(itemView.getContext(), "correo", emailCliente.getText().toString());
                    }
                }
            });

            //EVENTO CLICK DE LA IMAGEN DE LA CARDVIEW
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Evento Click del Cliente
                    //BUSCAMOS TRABAJADORES EN EL MAPA
                    if (rol == 3) {
                        Intent intent = new Intent(itemView.getContext(), MapaActivity.class);
                        intent.putExtra("idOficio", getAdapterPosition() + 1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        itemView.getContext().getApplicationContext().startActivity(intent);
                    } else {
                        //Evento Click del Trabajador
                        //Envío de presupuesto
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(itemView.getContext());
                        final LayoutInflater layoutInflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View mView = layoutInflater.inflate(R.layout.activity_detallescontratacion, null);

                        txtNombre = mView.findViewById(R.id.txtNombreC);
                        txtServicios = mView.findViewById(R.id.txtServicios);
                        edtCosto = mView.findViewById(R.id.edtCosto);
                        edtDetalles = mView.findViewById(R.id.edtDetalles);
                        btnEnviar = mView.findViewById(R.id.btnEnviar);
                        ratingBar = mView.findViewById(R.id.ratingBar);

                        txtNombre.setText(emailCliente.getText().toString());
                        txtServicios.setText(setEmpleo(Integer.valueOf(idOficio.getText().toString())));
                        ratingBar.setRating(Float.valueOf(ratingClient.getText().toString()));

                        btnEnviar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                if (validar()) {
                                    Usuario usuario = Usuario.getInstance();
                                    //CREAR UN OBJETO DE CONTRATACIÓN CON TODOS SUS DATOS A MANERA DE PRESUPUESTO
                                    Contratacion contratacion = new Contratacion();
                                    contratacion.setIdOficio(Integer.valueOf(idOficio.getText().toString()));
                                    contratacion.setCosto(Double.parseDouble(costo));
                                    contratacion.setDescripcion(detalles);
                                    contratacion.setIdTrabajador(usuario.getId());
                                    contratacion.setIdCliente(Integer.parseInt(idCliente.getText().toString()));
                                    contratacion.setIdVacante(Integer.parseInt(idVacante.getText().toString()));

                                    usuario.registerEngagement(contratacion);
                                    dialog[0].dismiss();

                                } else {

                                    Toast.makeText(itemView.getContext(), "Por favor complete los campos", Toast.LENGTH_SHORT).show();
                                    dialog[0].dismiss();

                                }
                            }

                        });

                        //AGREGAMOS LA VISTA QUE PREVIAMENTE ELABORAMOS
                        mBuilder.setView(mView);
                        dialog[0] = mBuilder.create();
                        dialog[0].show();

                    }

                }
            });

        }
    }

    public String setEmpleo(int id) {

        String empleo = "";

        switch (id) {

            case 1:

                empleo = "Herrería";

                break;


            case 2:

                empleo = "Fontanería";

                break;


            case 3:

                empleo = "Albañilería";


                break;


            case 4:

                empleo = "Electricista";

                break;

        }

        return empleo;
    }

    //FUNCIÓN PARA VALIDAR LOS CAMPOS DEL FORMULARIO DE REGISTRO, SALIDA: VERDERO O FALSO, SEGÚN SEA EL CASO
    public boolean validar() {

        edtCosto.setError(null);
        edtDetalles.setError(null);

        costo = edtCosto.getText().toString();
        detalles = edtDetalles.getText().toString().trim();

        if (TextUtils.isEmpty(costo)) {
            edtCosto.setError("Campo Obligatorio");
            edtCosto.requestFocus();
            return false;
        }

        return true;
    }
}
