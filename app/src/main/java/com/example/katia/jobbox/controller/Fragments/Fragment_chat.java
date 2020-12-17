package com.example.katia.jobbox.controller.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.model.adapter.UsuarioAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.katia.jobbox.activity.MainActivity.CHAT_REFERENCE;
import static com.example.katia.jobbox.controller.Menu.getDialogProgressBar;

public class Fragment_chat extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView rvChats;
    private RecyclerView.Adapter adapterC;
    private RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference reference;
    AlertDialog progressDialog;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<String> keys = new ArrayList<>();


    public Fragment_chat() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_chat.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_chat newInstance(String param1, String param2) {
        Fragment_chat fragment = new Fragment_chat();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        rvChats = rootView.findViewById(R.id.rvChats);
        //adapterC = new MensajeAdaptador(getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvChats.setLayoutManager(linearLayoutManager);
        //rvChats.setAdapter(adapterC);

        //initList();
        return rootView;

    }


    /*public void initList() {

        database = FirebaseDatabase.getInstance();
        reference = database.getReference(CHAT_REFERENCE);
        String UID = UsuarioFirebaseSingleton.getInstancia().getKeyUsuario();

        progressDialog = getDialogProgressBar(getActivity()).create();
        progressDialog.show();
        //Obtenemos una referencia a la carpeta del chat en la bd y entramos a la cerpeta con nuestro UID y después a la
        reference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //ACCEDEMOS AL CHAT
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot chat : dataSnapshot.getChildren()) {
                        keys.add(chat.getKey());
                    }
                    //Obtenemos los contactos referentes a sus claves
                    adapterC = new UsuarioAdapter(getContactList(keys), getActivity());
                    rvChats.setAdapter(adapterC);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Aún no tiene conversaciones ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getActivity(), "error: " + databaseError.toString(), Toast.LENGTH_SHORT).show();
            }

        });
    }


    public ArrayList<UsuarioFirebaseAuthenticated> getContactList(List<String> keys) {

        final ArrayList<UsuarioFirebaseAuthenticated> listContacts = new ArrayList<>();

        for (String key : keys) {
            //OBETENEMOS LA INFORMACIÓN DEL USUARIO A TRAVÉS DE LA LLAVE OBTENIDA
            UsuarioFirebaseSingleton.getInstancia().getDataByKey(key, new UsuarioFirebaseSingleton.GetUsuarioInterface() {
                @Override
                public void getUsuario(UsuarioFirebaseAuthenticated usuarioAuthenticated) {

                    listContacts.add(usuarioAuthenticated);
                    if (listContacts.isEmpty()) {
                        adapterC.notifyItemChanged(listContacts.size());
                    } else {
                        adapterC.notifyItemChanged(listContacts.size() - 1);
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void getError(String error) {
                    Toast.makeText(getActivity(), "Error: " + error, Toast.LENGTH_SHORT).show();
                }

            });

        }
        return listContacts;
    }*/
}
