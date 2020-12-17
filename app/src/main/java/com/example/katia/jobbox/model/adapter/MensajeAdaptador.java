
package com.example.katia.jobbox.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.model.MensajeUsuario;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MensajeAdaptador extends RecyclerView.Adapter<MensajeViewHolder> {

    private List<MensajeUsuario> listMensaje = new ArrayList<>();
    private Context c;

    public MensajeAdaptador(Context c) {
        this.c = c;
    }

    public int addMensaje(MensajeUsuario mensaje) {
        listMensaje.add(mensaje);
        //3 mensajes
        int posicion = listMensaje.size() - 1;//3
        notifyItemInserted(listMensaje.size());
        return posicion;
    }

    public void actualizarMensaje(int posicion, MensajeUsuario mensaje) {
        listMensaje.set(posicion, mensaje);//2
        notifyItemChanged(posicion);
    }

    @Override
    public MensajeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(c).inflate(R.layout.cardview_mensaje_emisor, parent, false);
        } else {
            view = LayoutInflater.from(c).inflate(R.layout.cardview_mensaje_receptor, parent, false);
        }
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MensajeViewHolder holder, int position) {

        MensajeUsuario mensaje = listMensaje.get(position);
        /*UsuarioFirebaseAuthenticated usuarioAuthenticated = mensaje.getUsuarioAuthenticated();
        if (usuarioAuthenticated != null) {
            holder.getNombre().setText(usuarioAuthenticated.getUsuario().getNombre());
            Picasso.get().load(usuarioAuthenticated.getUsuario().getUrl()).into(holder.getFotoMensajePerfil());
        }*/

        holder.getMensaje().setText(mensaje.getMensaje().getMensaje());
        if (mensaje.getMensaje().isContieneFoto()) {
            holder.getFotoMensaje().setVisibility(View.VISIBLE);
            holder.getMensaje().setVisibility(View.VISIBLE);
            Picasso.get().load(mensaje.getMensaje().getUrlFoto()).into(holder.getFotoMensaje());
        } else {
            holder.getFotoMensaje().setVisibility(View.GONE);
            holder.getMensaje().setVisibility(View.VISIBLE);
        }

        holder.getHora().setText(mensaje.fechaDeCreacionDelMensaje());
    }

    @Override
    public int getItemCount() {
        return listMensaje.size();
    }

    @Override
    public int getItemViewType(int position) {
        /*if (listMensaje.get(position).getUsuarioAuthenticated() != null) {
            if (listMensaje.get(position).getUsuarioAuthenticated().getKey().equals(UsuarioFirebaseSingleton.getInstancia().getKeyUsuario())) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }*/
        return 0;
    }
}