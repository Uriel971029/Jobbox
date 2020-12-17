package com.example.katia.jobbox.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.katia.jobbox.R;

public class Prueba_activity extends AppCompatActivity {

    //WebView webView;
    TextView txtUrl, txtFechaI, txtFechaF, txtAciertos, txtStatus, txtInfo;
    AlertDialog.Builder mBuilder;
    View mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);

        /*webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String urlPrueba = getIntent().getExtras().getString("url_prueba");
        webView.loadUrl(urlPrueba);*/
        int type_notication = getIntent().getExtras().getInt("tipo_noty");

        switch (type_notication) {


            case 1:

                String urlPrueba = getIntent().getExtras().getString("url_prueba");
                String fechaI = getIntent().getExtras().getString("fecha_inicio");
                String fechaF = getIntent().getExtras().getString("fecha_fin");

                //CREAMOS VISTA
                mBuilder = new AlertDialog.Builder(Prueba_activity.this);
                mView = getLayoutInflater().inflate(R.layout.activity_detallesprueba, null);

                txtUrl = mView.findViewById(R.id.txtUrl);
                txtUrl.setText(urlPrueba);
                txtFechaI = mView.findViewById(R.id.txtFechaI);
                txtFechaI.setText(txtFechaI.getText().toString() + " " + fechaI);
                txtFechaF = mView.findViewById(R.id.txtFechaF);
                txtFechaF.setText(txtFechaF.getText().toString() + " " + fechaF);

                break;


            case 2:

                int aciertos = Integer.valueOf(getIntent().getExtras().getString("aciertos"));
                String status = getIntent().getExtras().getString("status");

                //CREAMOS VISTA
                mBuilder = new AlertDialog.Builder(Prueba_activity.this);
                mView = getLayoutInflater().inflate(R.layout.activity_resultadosprueba, null);

                txtAciertos = mView.findViewById(R.id.txtResultados);
                txtStatus = mView.findViewById(R.id.txtStatus);
                txtInfo = mView.findViewById(R.id.txtSumarry);

                txtAciertos.setText(txtAciertos.getText().toString() + " " + aciertos);

                if (aciertos >= 7) {

                    txtStatus.setTextColor(getResources().getColor(R.color.color_success));
                    txtInfo.setText(getResources().getString(R.string.text_congrats));

                } else {

                    txtStatus.setTextColor(getResources().getColor(R.color.color_error));
                    txtInfo.setText(getResources().getString(R.string.text_shame));

                }

                txtStatus.setText(status);

                mBuilder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                                finish();
                            }
                        }
                );

                break;

        }

        //AGREGAMOS LA VISTA QUE PREVIAMENTE ELABORAMOS
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        //dialog.setCancelable(false);
        dialog.show();

        //SETEAMOS COLOR A LOS BOTONES
        Button cancelar = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button aceptar = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (cancelar != null && aceptar != null) {
            cancelar.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            aceptar.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }
}
