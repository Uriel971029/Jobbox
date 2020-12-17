package com.example.katia.jobbox.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.katia.jobbox.R;

import java.util.ArrayList;
import java.util.List;

//import static com.example.katia.jobbox.controller.Menu.startChat;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    //private List<UsuarioFirebaseAuthenticated> listUsuarios = new ArrayList();
    private Context context;

    /*public UsuarioAdapter(List<UsuarioFirebaseAuthenticated> listUsuarios, Context context) {
        this.listUsuarios = listUsuarios;
        this.context = context;
    }*/

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_usuario, parent, false);
        return new UsuarioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        /*if (listUsuarios != null) {
            if (listUsuarios.get(position) != null) {
                //holder.img.setImageResource(listUsuarios.get(position).getImg());
                holder.nombre.setText(listUsuarios.get(position).getUsuario().getNombre());
                holder.correo.setText(listUsuarios.get(position).getUsuario().getCorreo());
            }
        }*/
    }

    @Override
    public int getItemCount() {
        /*if (listUsuarios != null) {
            return listUsuarios.size();

        } else {
            return listUsuarios.size();
        }*/
        return 0;
    }

    public class UsuarioViewHolder extends RecyclerView.ViewHolder {

        TextView nombre, correo;


        public UsuarioViewHolder(@NonNull final View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.txtNombreUsuario);
            correo = itemView.findViewById(R.id.txtCorreoUsuario);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //startChat(context,"correo", correo.getText().toString());
                }
            });
        }
    }
}
