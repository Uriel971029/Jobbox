package com.example.katia.jobbox.model.Auth;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.katia.jobbox.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;


public class recuperarContrasenia extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText edtEmail, edtCode, edtPass, edtConfirm;
    private TextView txtIns, txtEnv;
    private String email, verifyCode, pass, confirmP;
    private BottomNavigationView bottomNavigationView;
    private View rootView;
    private Group passGroup;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnCodeSentListener mCodeListener;
    private OnPasswordResetListener mPasswordListener;

    public recuperarContrasenia() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment recuperarContrasenia.
     */
    // TODO: Rename and change types and number of parameters
    public static recuperarContrasenia newInstance(String param1, String param2) {
        recuperarContrasenia fragment = new recuperarContrasenia();
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
        rootView = inflater.inflate(R.layout.fragment_recuperar_contrasenia, container, false);

        edtEmail = (EditText) rootView.findViewById(R.id.edtRecoverEmail);
        edtCode = (EditText) rootView.findViewById(R.id.edtCode);
        edtPass = (EditText) rootView.findViewById(R.id.edtPassword);
        edtConfirm = (EditText) rootView.findViewById(R.id.edtConfirmation);
        txtIns = (TextView) rootView.findViewById(R.id.txtInstructions);
        txtEnv = (TextView) rootView.findViewById(R.id.txtEnviar);
        bottomNavigationView = (BottomNavigationView) rootView.findViewById(R.id.navigationView);
        passGroup = (Group) rootView.findViewById(R.id.passwordGroup);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        passGroup.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Restablecer la contraseña");
        builder.setMessage("Podemos enviar un código de verificación al correo asociado a su cuenta para que restablezca su contraseña");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Reestablecer contraseña");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return rootView;
    }

    //FUNCIÓN QUE RECIBE LOS EVENTOS CLICK DEL MENU, ENTRADAS: item seleccionado del menu, SALIDA: true o false
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        HashMap<String, String> args = new HashMap<>();

        switch (menuItem.getItemId()) {

            case R.id.navigation_next:


                if (edtEmail.getVisibility() == View.VISIBLE) {

                    //ENVIAR CORREO PARA REESTABLECER LA CONTRASEÑA
                    if (validar(1)) {
                        email = edtEmail.getText().toString();
                        mCodeListener.onCodeSent(email);
                    }

                } else {
                    //RESTABLECER LA CONTRASEÑA
                    if (validar(2)) {
                        mPasswordListener.onPasswordReset(email, pass, verifyCode);
                    }
                }
                break;
        }
        return false;
    }


    public interface OnCodeSentListener {

        public void onCodeSent(String email);

    }


    public interface OnPasswordResetListener {

        public void onPasswordReset(String email, String password, String code);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCodeSentListener && context instanceof OnPasswordResetListener) {
            mCodeListener = (OnCodeSentListener) context;
            mPasswordListener = (OnPasswordResetListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement the correspondent interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCodeListener = null;
    }

    public boolean validar(int method) {

        if (method == 1) {

            email = edtEmail.getText().toString();


            if (email.isEmpty()) {
                edtEmail.setError("Complete el campo por favor");
                edtEmail.requestFocus();
                return false;

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                edtEmail.setError("Formato inválido de correo");
                edtEmail.requestFocus();
                return false;

            }

        } else {

            verifyCode = edtCode.getText().toString().trim();
            pass = edtPass.getText().toString().trim();
            confirmP = edtConfirm.getText().toString().trim();

            if (verifyCode.isEmpty()) {

                edtCode.setError("Por favor complete el campo");
                edtCode.requestFocus();
                return false;

            } else if (verifyCode.length() != 8) {
                edtCode.setError("Código incorrecto");
                edtCode.requestFocus();
                return false;
            }

            if (pass.isEmpty()) {
                edtPass.setError("Por favor complete el campo");
                edtPass.requestFocus();
                return false;
            } else if (pass.length() < 8) {
                edtPass.setError("La contraseña debe contener por lo menos 8 caractéres");
                edtPass.requestFocus();
                return false;
            } else if (!pass.matches(".*[0-9].*") || !pass.matches(".*[a-zA-Z].*")) {
                edtPass.setError("Utilice números y letras para una mayor seguridad");
                edtPass.requestFocus();
                return false;
            }

            if (confirmP.isEmpty()) {

                edtConfirm.setError("Por favor complete el campo");
                edtConfirm.requestFocus();
                return false;

            } else if (confirmP.length() != pass.length() || !pass.equals(confirmP)) {
                edtConfirm.setError("Las contraseñas no coinciden");
                edtConfirm.requestFocus();
                return false;
            }
        }
        return true;
    }


    public void limpiar(int opc) {

        if (opc == 1) {

            edtEmail.setText("");

        } else {
            edtEmail.setVisibility(View.GONE);
            passGroup.setVisibility(View.VISIBLE);
            txtIns.setText(R.string.txt_instructions2);
            txtEnv.setText(R.string.txt_send2);
            bottomNavigationView.getMenu().getItem(0).setTitle(R.string.send);
        }
    }
}
