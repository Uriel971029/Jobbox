package com.example.katia.jobbox.model.Auth;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.katia.jobbox.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class login extends Fragment implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String message = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    //private String mParam2;
    private View rootView;
    private TextView txtRecoverP;
    private EditText edtUser, edtPass;
    private BottomNavigationView bottomNavigationView;

    private OnFragmentChangedListener mFragmentListener;
    private OnLoginRequiredListener mLoginListener;
    //private OnProgressDisplayedListener mProgressListener;

    public login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment login.
     */
    // TODO: Rename and change types and number of parameters
    public static login newInstance(String param1) {

        login fragment = new login();
        Bundle args = new Bundle();
        args.putString("message", param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("message");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_login, container, false);

        txtRecoverP = (TextView) rootView.findViewById(R.id.txtRecover);
        edtUser = (EditText) rootView.findViewById(R.id.edtUsername);
        edtPass = (EditText) rootView.findViewById(R.id.edtPassword);
        bottomNavigationView = (BottomNavigationView) rootView.findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        txtRecoverP.setOnClickListener(this);

        //RESPUESTA DE EVENTO DE RECUPERACIÓN DE CONTRASEÑA
        if (mParam1 != null) {
            if (mParam1.equalsIgnoreCase("success")) {
                Toast.makeText(getActivity(), "Su contraseña se ha actualizado correctamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "Ocurrió un error tratando de actualizar la contraseña", Toast.LENGTH_LONG).show();
            }
        }

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Iniciar sesión");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return rootView;
    }


    //FUNCIÓN DE NAVEGACIÓN DEL MENU DE LAS INTERFACES, ENTRADA: COMPONENTE DE TIPO Item de Menu, SALIDA: TRUE O FALSE;
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.navigation_next:

                if (validar()) {
                    mLoginListener.onLoginRequired(edtUser.getText().toString(), edtPass.getText().toString());
                }

                break;

            default:

                break;

        }
        return false;
    }


    public interface OnFragmentChangedListener {

        public void onFragmentChanged(Fragment fragment, String tag);

    }

    public interface OnLoginRequiredListener {

        public void onLoginRequired(String username, String password);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentChangedListener && context instanceof OnLoginRequiredListener) {
            mFragmentListener = (OnFragmentChangedListener) context;
            mLoginListener = (OnLoginRequiredListener) context;
            //mProgressListener = (OnProgressDisplayedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }

    @Override
    public void onClick(View v) {
        recuperarContrasenia recuperarContrasenia = new recuperarContrasenia();
        mFragmentListener.onFragmentChanged(recuperarContrasenia, "recuperar_contrasenia");

    }

    //FUNCIÓN PARA VALIDAR LOS CAMPOS DEL FORMULARIO DE REGISTRO, SALIDA: VERDERO O FALSO, SEGÚN SEA EL CASO
    public boolean validar() {


        edtUser.setError(null);
        edtPass.setError(null);

        String user = edtUser.getText().toString().trim();
        String password = edtPass.getText().toString().trim();


        if (TextUtils.isEmpty(user)) {
            edtUser.setError("Campo vacio");
            edtUser.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            edtPass.setError("Campo vacio");
            edtPass.requestFocus();
            return false;

        }

        return true;
    }

    public void limpiar() {

        edtUser.setText("");
        edtPass.setText("");
    }

}
