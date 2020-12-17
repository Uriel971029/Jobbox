package com.example.katia.jobbox.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.PostalAddress;
import com.example.katia.jobbox.BuildConfig;
import com.example.katia.jobbox.R;
import com.example.katia.jobbox.model.Contratacion;
import com.example.katia.jobbox.model.Empleo;
import com.example.katia.jobbox.model.adapter.ContratacionAdapter;
import com.example.katia.jobbox.model.adapter.EmpleoAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import net.gotev.uploadservice.UploadService;

import java.util.ArrayList;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

import static com.example.katia.jobbox.controller.Menu.rol;
import static com.example.katia.jobbox.controller.Menu.trabajador;
//import static com.example.katia.jobbox.controller.Menu.usuario;
import static com.example.katia.jobbox.model.adapter.ContratacionAdapter.URL_API;


public class ContratacionActivity extends AppCompatActivity implements PaymentMethodNonceCreatedListener, BraintreeCancelListener, BraintreeErrorListener {


    private RecyclerView rv;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    //Dialog dialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //INICIALIZAMOS LA BIBLIOTECA PARA SUBIDA DE IMAGENES AL SERVIDOR
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        setContentView(R.layout.activity_contratacion);
        rv = findViewById(R.id.rvContrataciones);
        rv.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(ContratacionActivity.this);
        rv.setLayoutManager(layoutManager);

        init();

    }

    public void init() {

        String mode = getIntent().getExtras().getString("manage_engagement", null);
        if (mode != null) {

            if (mode.equalsIgnoreCase("true")) {

                getSupportActionBar().setTitle("Contrataciones activas");
                Toast.makeText(ContratacionActivity.this, "Lista de contrataciones", Toast.LENGTH_SHORT).show();
                //MOSTRAR TARJETA DE CONTRATACIÓN CON DATOS GENERALES Y LA OPCION DE SUBIR EVIDENCIA Y FINALIZARLA
                //adapter = new ContratacionAdapter(usuario.getListUpdates(), this);
                rv.setAdapter(adapter);

            } else {

                if (rol == 3) {

                    getSupportActionBar().setTitle("Registro de contrataciones");
                    //adapter = new ContratacionAdapter(usuario.getListUpdates(), this);
                    rv.setAdapter(adapter);

                } else {

                    //EL TRABAJADOR LISTA LAS SOLICITUDES DE TRABAJO ENVIADAS A EL EN ESPECÍFICO
                    getSupportActionBar().setTitle("Solicitudes de contratación");
                    ArrayList<Empleo> solicitudes = new ArrayList<>();
                    //trabajador.setAdapterSolicitudes(adapter);
                    //trabajador.setRvSolicitudes(rv);
                    //DENTRO DE LAS VACANTES DEBEMOS OBTENER AQUELLAS QUE SE DIRIJAN AL TRABAJADOR Y ESTAN EN UN STATUS DE PENDIENTE
                    solicitudes = trabajador.getListSolicitudes();
                    adapter = new EmpleoAdapter(solicitudes);
                    rv.setAdapter(adapter);

                }

            }

        }

    }

    @Override
    public void onCancel(int requestCode) {

        //Log.d("error_paypal", requestCode + "");
        Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show();
        ((ContratacionAdapter) adapter).getProgressDialog().dismiss();
    }

    @Override
    public void onError(Exception error) {

        //Log.d("error_paypal", error.getMessage().toString());
        Toast.makeText(this, "Ocurrió un error durante la transacción: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        ((ContratacionAdapter) adapter).getProgressDialog().dismiss();
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {

        postNonceToServer(paymentMethodNonce);
    }


    //METODO UTILIZADO CUANDO SE TIENE SU PROPIO BACKEND EN EL SERVIDOR Y SE TRABAJA CON BREINTREE Y NO EL SDK DE PAYPAL
    public void postNonceToServer(PaymentMethodNonce paymentMethodNonce) {

        final Contratacion contratacion = ((ContratacionAdapter) adapter).getDataPayment();

        String nonce = paymentMethodNonce.getNonce();
        if (paymentMethodNonce instanceof PayPalAccountNonce) {
            PayPalAccountNonce payPalAccountNonce = (PayPalAccountNonce) paymentMethodNonce;

            //PREPARAMOS LOS VALORES A ENVIAR AL SERVIDOR
            PostalAddress shippingAddress = payPalAccountNonce.getShippingAddress();
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("payment_method_nonce", nonce);
            params.put("amount", contratacion.getCosto() + "");
            params.put("first_name", payPalAccountNonce.getFirstName());
            params.put("lastname", payPalAccountNonce.getLastName());
            params.put("company", "Universidad Politécnica del Estado de Morelos");
            params.put("street", shippingAddress.getStreetAddress());
            params.put("extended_address", "");
            params.put("locality", shippingAddress.getLocality());
            params.put("region", shippingAddress.getRegion());
            params.put("postal_code", shippingAddress.getPostalCode());
            params.put("description", "Pago por " + contratacion.getEmpleo() + " desde la aplicación UPay");

            client.post(URL_API + "checkout.php", params, new TextHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                    //Log.d("***", "checkout response: " + responseString);
                    ((ContratacionAdapter) adapter).getProgressDialog().dismiss();
                    Toast.makeText(ContratacionActivity.this, "¡Transacción realizada exitosamente!", Toast.LENGTH_LONG).show();
                    //usuario.setAdapterContrataciones(adapter);
                    ((ContratacionAdapter) adapter).getBtnPaypal().setVisibility(View.GONE);
                    ((ContratacionAdapter) adapter).getBtnFinalizar().setVisibility(View.VISIBLE);
                    ((ContratacionAdapter) adapter).getBtnFinalizar().setText("REAGENDAR");
                    ((ContratacionAdapter) adapter).getBtnSubir().setVisibility(View.VISIBLE);
                    ((ContratacionAdapter) adapter).getBtnSubir().setText("MENSAJE");
                    //ACTUALIZAMOS EL STATUS DE LA CONTRATACION YA QUE ESTA PAGADA
                    //usuario.payEngagement(String.valueOf(contratacion.getIdcontratacion()));
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    Toast.makeText(ContratacionActivity.this, "¡Ocurrió un error con la transacción!", Toast.LENGTH_LONG).show();
                    ((ContratacionAdapter) adapter).getProgressDialog().dismiss();
                }

            });

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            //subir archivo de evidencia al servidor y registrar su ruta en su contratacion correspondiente
            Uri uri = data.getData();
            String nombre_archivo = setNombreArchivo();
            String ruta_archivo = getFilePath(uri);

            trabajador.uploadEvidence(nombre_archivo, ruta_archivo, requestCode + "");

        }
    }

    //Función para crear un nombre único para una nueva fotografía
    //Entradas: nada
    //Salidas: cadena con nombre generado
    public String setNombreArchivo() {

        String nameFile = "";
        // Producir nuevo int aleatorio entre 0 y 99
        Random aleatorio = new Random(System.currentTimeMillis());
        int num = aleatorio.nextInt(10000);
        //DAMOS FORMATO AL NOMBRE CON EL VALOR ALEATORIO AGREGADO
        nameFile = "JobboxEvidence" + String.valueOf(num) + ".rar";
        // Refrescar datos aleatorios
        aleatorio.setSeed(System.currentTimeMillis());

        return nameFile;
    }


    public String getFilePath(Uri uri) {

        Cursor cursor;
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);
        String id = wholeID.split(":")[1];
        //ACCEDEMOS A LA MEMORIA EXTERNA
        uri = MediaStore.Files.getContentUri("external");
        String[] column = new String[]{BaseColumns._ID};
        String sortOrder = null; // unordered
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("rar");
        String[] selectionArgsPdf = new String[]{mimeType};
        cursor = this.getContentResolver().query(uri, column, selectionMimeType, selectionArgsPdf, sortOrder);

        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        } else {
            filePath = id;
        }
        cursor.close();
        return filePath;
    }

}
