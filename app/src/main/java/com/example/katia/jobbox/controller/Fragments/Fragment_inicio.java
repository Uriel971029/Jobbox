package com.example.katia.jobbox.controller.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.model.adapter.EmpleoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


//import static com.example.katia.jobbox.controller.Menu.empleo;
import static com.example.katia.jobbox.controller.Menu.oficiosDetails;
import static com.example.katia.jobbox.controller.Menu.trabajador;
import static com.example.katia.jobbox.controller.Menu.vacantes;


public class Fragment_inicio extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View rootView;
    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton myFabButton;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public Fragment_inicio() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fragment_inicio newInstance(String param1, String param2) {
        Fragment_inicio fragment = new Fragment_inicio();
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

        rootView = inflater.inflate(R.layout.fragment_inicio, container, false);

        //init();
        rv = rootView.findViewById(R.id.rvEmpleos);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);


        //VISTA DE TRABAJADOR
        if (getArguments().getString("param1", "").equalsIgnoreCase("4")) {

            adapter = new EmpleoAdapter(vacantes);
            trabajador.setAdapterRecycler(adapter);
            trabajador.setRv(rv);

        } else {
            //VISTA DE CLIENTE
            adapter = new EmpleoAdapter(oficiosDetails, 3);
            //empleo.setAdapterRecycler(adapter);
            //empleo.setRv(rv);
        }

        myFabButton = rootView.findViewById(R.id.my_fab);
        return rootView;
    }
}
