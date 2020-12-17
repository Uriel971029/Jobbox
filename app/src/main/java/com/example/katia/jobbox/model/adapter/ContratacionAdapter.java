package com.example.katia.jobbox.model.adapter;


import android.app.Activity;
import android.app.Dialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.cardemulation.HostNfcFService;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.PostalAddress;
import com.example.katia.jobbox.R;
import com.example.katia.jobbox.activity.ContratacionActivity;
import com.example.katia.jobbox.controller.Menu;
import com.example.katia.jobbox.model.Contratacion;
import com.example.katia.jobbox.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;


import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.view.View.GONE;
import static com.example.katia.jobbox.controller.Menu.getDialogProgressBar;
//import static com.example.katia.jobbox.controller.Menu.rol;
//import static com.example.katia.jobbox.controller.Menu.startChat;
import static com.example.katia.jobbox.controller.Menu.trabajador;
//import static com.example.katia.jobbox.controller.Menu.usuario;
import static com.example.katia.jobbox.model.Usuario.getSizeScreen;

public class ContratacionAdapter extends RecyclerView.Adapter<ContratacionAdapter.ContratacionViewHolder> {

    List<Contratacion> listContrataciones;
    String labelId, labelParticipante, labelDesc, labelCost, labelFech, labelServicio, labelStatus;
    String id, costo, servicio;
    Context context = null;
    Activity activity;
    RecyclerView.Adapter adapterContrataciones;
    int rolUser = 0;
    View cardview;
    String status = "";
    Button auxSubir, auxFinalizar;
    ImageButton auxPayPal;
    AlertDialog progressDialog = null;
    AlertDialog.Builder builder;
    //int type_card = 0;

    public static final String URL_API = "http://jobbox-com.stackstaging.com/App/API/braintree/";


    public ContratacionAdapter(List<Contratacion> listContrataciones, Activity activity) {

        this.listContrataciones = listContrataciones;
        this.activity = activity;
        //this.adapterContrataciones = adapter;
    }


    @NonNull
    @Override
    public ContratacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        rolUser = Usuario.getInstance().getRol();
        cardview = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_engagement, parent, false);
        return new ContratacionViewHolder(cardview);
    }

    @Override
    public void onBindViewHolder(@NonNull ContratacionViewHolder holder, int position) {

        if (listContrataciones != null) {

            if (listContrataciones.get(position) != null) {

                holder.img.setImageResource(listContrataciones.get(position).getImg());
                holder.idContratacion.setText(labelId + " " + listContrataciones.get(position).getIdcontratacion() + "");
                holder.txtParticipante.setText(labelParticipante + " " + listContrataciones.get(position).getNombre_participante());
                holder.txtServicio.setText(labelServicio + " " + listContrataciones.get(position).getEmpleo());
                holder.descripcion.setText(labelDesc + " " + listContrataciones.get(position).getDescripcion());
                holder.txtCosto.setText(labelCost + String.valueOf(listContrataciones.get(position).getCosto()) + "");

                status = translateStatus(listContrataciones.get(position).getStatus());
                holder.txtStatus.setText(labelStatus + status);

                //VERIFICAR QUE BOTONES SE LE VAN A MOSTRAR AL CLIENTE DE ACUERDO AL STATUS
                if (status.equalsIgnoreCase(" Presupuesto")) {

                    if (holder.btnFinalizar.getVisibility() == View.VISIBLE) {
                        //reagendar
                        holder.btnFinalizar.setVisibility(GONE);
                    }

                    if (holder.btnSubir.getVisibility() == View.VISIBLE) {
                        //mensaje
                        holder.btnSubir.setVisibility(GONE);
                    }
                    if (holder.btnPaypal.getVisibility() == GONE) {
                        //OCULTAMOS
                        holder.btnPaypal.setVisibility(View.VISIBLE);
                    }

                } else if (status.equalsIgnoreCase(" Pagado")) {
                    //reagendar
                    holder.btnFinalizar.setVisibility(View.VISIBLE);
                    //mensaje
                    holder.btnSubir.setVisibility(View.VISIBLE);
                    //OCULTAMOS
                    holder.btnPaypal.setVisibility(GONE);

                    if (rolUser == 3) {

                        holder.btnFinalizar.setText("REAGENDAR");
                        holder.btnSubir.setText("MENSAJE");

                    } else {
                        holder.btnFinalizar.setText("FINALIZAR");
                        holder.btnSubir.setText("SUBIR");
                    }

                } else if (status.contains("Evidencia")) {

                    //OCULTAMOS
                    holder.btnPaypal.setVisibility(GONE);
                    //mostramos ver evidencia
                    holder.btnFinalizar.setVisibility(View.VISIBLE);
                    //mostramos mensaje
                    holder.btnSubir.setVisibility(View.VISIBLE);

                    if (rolUser == 3) {
                        holder.btnFinalizar.setText("DESCARGAR");
                        holder.btnSubir.setText("MENSAJE");
                    }

                } else if (status.equalsIgnoreCase(" Terminado")) {

                    holder.btnPaypal.setVisibility(GONE);
                    //MOSTRAMOS
                    holder.btnFinalizar.setVisibility(View.VISIBLE);
                    holder.btnFinalizar.setText("Resumen");
                    holder.btnSubir.setVisibility(View.VISIBLE);
                    holder.btnSubir.setText("Calificar");
                }

                holder.fecha.setText(labelFech + " " + listContrataciones.get(position).getFecha());
            }

        }

    }

    public String translateStatus(int status) {

        String translate = "";

        switch (status) {

            case 0:

                translate = " Presupuesto";

                break;

            case 1:

                translate = " Pagado";

                break;


            case 2:

                translate = " Evidencia registrada";

                break;

            case 3:

                translate = " Terminado";

                break;
        }


        return translate;
    }

    @Override
    public int getItemCount() {

        if (listContrataciones != null) {
            return listContrataciones.size();

        } else {
            return listContrataciones.size();
        }

    }


    public class ContratacionViewHolder extends RecyclerView.ViewHolder {


        TextView idContratacion, descripcion, fecha, txtParticipante, txtServicio, txtCosto, txtStatus;
        Button btnEnviar, btnCancelar, btnFinalizar, btnSubir;
        ImageButton btnPaypal, btnBorrar;
        ImageView img;
        RatingBar ratingBar;
        EditText edtComentarios;


        public ContratacionViewHolder(@NonNull final View itemView) {
            super(itemView);


            idContratacion = itemView.findViewById(R.id.txtIdContratacion);
            descripcion = itemView.findViewById(R.id.txtDescripcion);
            txtCosto = itemView.findViewById(R.id.txtCostoC);
            fecha = itemView.findViewById(R.id.txtFecha);
            img = itemView.findViewById(R.id.imagen);

            txtParticipante = itemView.findViewById(R.id.txtParticipante);
            txtServicio = itemView.findViewById(R.id.txtServicioC);
            txtStatus = itemView.findViewById(R.id.txtStatus);

            btnFinalizar = itemView.findViewById(R.id.btnTerminar);
            btnSubir = itemView.findViewById(R.id.btnSubirE);
            btnPaypal = itemView.findViewById(R.id.btnPagar);
            btnBorrar = itemView.findViewById(R.id.btnDelete);

            labelId = itemView.getContext().getResources().getString(R.string.text_id_contratacion);

            if (rolUser == 3) {
                labelParticipante = itemView.getContext().getResources().getString(R.string.text_trabajador_contratacion);
            } else {
                labelParticipante = itemView.getContext().getResources().getString(R.string.text_cliente_contratacion);
            }
            labelDesc = itemView.getContext().getResources().getString(R.string.text_descripcion_contratacion);
            labelCost = itemView.getContext().getResources().getString(R.string.text_costo_contratacion);
            labelFech = itemView.getContext().getResources().getString(R.string.text_fecha_contratacion);
            labelServicio = itemView.getContext().getResources().getString(R.string.text_servicio_contratacion);
            labelStatus = itemView.getContext().getResources().getString(R.string.text_status_contratacion);
            context = itemView.getContext();

            final ArrayList<Integer> data = new ArrayList<>();

            btnPaypal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    String importe = txtCosto.getText().toString();
                    int index = importe.indexOf(":");
                    costo = importe.substring(index + 3, importe.length() - 2);
                    servicio = txtServicio.getText().toString();
                    String idC = idContratacion.getText().toString();
                    index = idC.indexOf(":");
                    id = idC.substring(index + 2, idC.length());

                    //usuario.setTxtStatus(txtStatus);
                    auxFinalizar = itemView.findViewById(R.id.btnTerminar);
                    auxSubir = itemView.findViewById(R.id.btnSubirE);
                    auxPayPal = itemView.findViewById(R.id.btnPagar);
                    confirmation(1, data);
                }
            });

            btnBorrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();
                    String tituloC = idContratacion.getText().toString();
                    int idContrato = Integer.parseInt(tituloC.substring(tituloC.indexOf(":") + 2, tituloC.length()));
                    if (!data.isEmpty())
                        data.clear();
                    //Cargamos el arreglo con la posición a eliminar y su id
                    data.add(position);
                    data.add(idContrato);
                    status = translateStatus(listContrataciones.get(position).getStatus());
                    if (status.equalsIgnoreCase(" Terminado")) {
                        confirmation(2, data);
                    } else {
                        Toast.makeText(activity, "No se puede eliminar esta contratación", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //Terminar contrato
            btnFinalizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (rolUser == 3) {

                        btnFinalizar = v.findViewById(R.id.btnTerminar);

                        if (btnFinalizar.getText().toString().equalsIgnoreCase("Reagendar")) {

                            //REAGENDAR TRABAJO
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());
                            View mView = ((Activity) v.getContext()).getLayoutInflater().inflate(R.layout.activity_detalles_reagendar, null);

                            Button btnReagendar = mView.findViewById(R.id.btnReagendar);
                            Button btnCancelar = mView.findViewById(R.id.btnCancelar);
                            final EditText edtFech = mView.findViewById(R.id.edtFecha);
                            final EditText edtNewC = mView.findViewById(R.id.edtNewC);

                            btnReagendar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    /*String fecha = edtFech.getText().toString();
                                    String newCosto = edtNewC.getText().toString();
                                    newCosto = newCosto.substring(newCosto.indexOf("$") + 1, newCosto.length());

                                    confirmation();

                                    usuario.delayEngagement("fecha&costo&status", fecha + newCosto);*/
                                    Toast.makeText(v.getContext(), "En desarrollo", Toast.LENGTH_SHORT).show();

                                    //DEBEMOS VERIFICAR EN EL METODO CONFIRMATION SI EL BOTON DE REAGENDAR ESTA VISIBLE
                                    // DE IGUAL MANERA EN EL METODO DE GET DATA QUE ES LLAMADO POR CONTRATACION ACTIVITY
                                    //CON EL FIN DE DEVOLVER ALGO QUE EN LUGAR DE LLAMAR AL MÉTODO payEngagement ahora llame
                                    // A delayEngagement

                                }
                            });

                            btnCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    progressDialog.dismiss();
                                }
                            });

                            //AGREGAMOS LA VISTA QUE PREVIAMENTE ELABORAMOS
                            mBuilder.setView(mView);
                            progressDialog = mBuilder.create();
                            progressDialog.show();

                        } else {
                            //DESCARGAR EVIDENCIA DEL TRABAJADOR
                            Toast.makeText(v.getContext(), "En desarrollo", Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        if (txtStatus.getText().toString().contains("Terminado")) {
                            //MOSTRAR RESUMEN DE LA CONTRATACIÓN
                            Toast.makeText(v.getContext(), "En desarrollo", Toast.LENGTH_SHORT).show();
                        } else {
                            String idContrato = idContratacion.getText().toString();
                            idContrato = idContrato.substring(idContrato.indexOf(":") + 2, idContrato.length());
                            trabajador.finish_engagement(idContrato, btnFinalizar, btnSubir, btnEnviar, btnCancelar, ratingBar, edtComentarios, itemView);
                        }
                    }
                }
            });

            //Subir evidencia
            btnSubir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //CALIFICAR USUARIO
                    if (txtStatus.getText().toString().contains("Terminado")) {

                        //LE DAMOS UN RESIZE A LA VISTA DE LA TARJETA PARA QUE QUEPAN BIEN LOS ELEMENTOS
                        //int newHeight = getSizeScreen((Activity) itemView.getContext(), 100, 1);
                        //itemView.getLayoutParams().height = newHeight;
                        //DESPLEGAR ELEMENTOS PARA ENVIAR UN COMENTARIO
                        //ratingBar.setVisibility(View.VISIBLE);
                        //edtComentarios.setVisibility(View.VISIBLE);
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());
                        View mView = ((Activity) v.getContext()).getLayoutInflater().inflate(R.layout.form_rate_user, null);

                        ratingBar = mView.findViewById(R.id.ratingBar);
                        edtComentarios = mView.findViewById(R.id.edtComentarios);
                        btnEnviar = mView.findViewById(R.id.btnEnviar);
                        btnCancelar = mView.findViewById(R.id.btnCancelar);

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();
                        //RECUPERAMOS LA REFERENCIA DE LOS BOTONES PARA LA CARDVIEW CORRECTA
                        //btnFinalizar = itemView.findViewById(R.id.btnTerminar);
                        //btnSubir = itemView.findViewById(R.id.btnSubirE);

                        //OCULTAMOS BOTONES
                        //btnFinalizar.setVisibility(GONE);
                        //btnSubir.setVisibility(GONE);

                        //btnEnviar.setVisibility(View.VISIBLE);
                        //btnCancelar.setVisibility(View.VISIBLE);

                        btnEnviar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String idContrato = idContratacion.getText().toString();
                                idContrato = idContrato.substring(idContrato.indexOf(":") + 2, idContrato.length());

                                String comentarios = edtComentarios.getText().toString();
                                if (ratingBar.getRating() != 0.0) {

                                    if (comentarios.isEmpty()) {
                                        comentarios = "";
                                    }

                                    //ENVIAMOS CALIFICACIÓN DEL USUARIO Y COMENTARIOS
                                    /*usuario.rateUser(ratingBar.getRating(), edtComentarios.getText().toString().trim(),
                                            usuario.getId() + "", idContrato);*/
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(v.getContext(), "Por favor elija una calificación", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                        btnCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                            }
                        });

                    } else {

                        if (rolUser == 4) {
                            //SUBIR EVIDENCIA
                            String idContrato = idContratacion.getText().toString();
                            idContrato = idContrato.substring(idContrato.indexOf(":") + 2, idContrato.length());
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/rar");
                            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                            trabajador.setTxtStatus(txtStatus);
                            activity.startActivityForResult(intent.createChooser(intent, "Selecciona una imagen de perfil"), Integer.parseInt(idContrato));

                        } else {
                            //ENVIAR MENSAJE A TRABAJADOR
                            String nombre_receptor = txtParticipante.getText().toString();
                            int inicio = nombre_receptor.indexOf(":") + 2;
                            nombre_receptor = nombre_receptor.substring(inicio, nombre_receptor.length());
                            //startChat(itemView.getContext(), "nombre", nombre_receptor);
                        }
                    }
                }
            });
        }
    }

    public Contratacion getDataPayment() {

        Contratacion contratacion = new Contratacion();
        contratacion.setIdcontratacion(Integer.valueOf(id));
        contratacion.setCosto(Double.parseDouble(costo));
        contratacion.setEmpleo(servicio);
        return contratacion;
    }

    public void confirmation(final int opc, final ArrayList<Integer> data) {

        //DIALOGO DE CONFIRMACION PARA SALIR
        final AlertDialog.Builder[] mBuilder = {new AlertDialog.Builder(activity)};
        View mView = activity.getLayoutInflater().inflate(R.layout.dialog_confirmation, null);

        ImageView imageView = mView.findViewById(R.id.img);
        TextView textView = mView.findViewById(R.id.txtConfirmation);

        if (opc == 1) {
            imageView.setImageResource(R.drawable.payment);
            textView.setText(R.string.text_payment_confirmation);
        } else {
            imageView.setImageResource(R.drawable.ic_trash);
            textView.setText(R.string.text_delete_confirmation);
        }
        mBuilder[0].setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (!activity.isFinishing()) {
                            progressDialog = getDialogProgressBar(activity).create();
                            progressDialog.show();
                        }

                        if (opc == 1) {

                            //PROCESO DEL SDK DE PAYPAL
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.get(URL_API + "main.php", new TextHttpResponseHandler() {

                                @Override
                                public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {

                                    progressDialog.dismiss();
                                    Toast.makeText(activity, "Ocurrió un problema con el token de autenticación de cliente", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String clientToken) {

                                    try {

                                        BraintreeFragment braintreeFragment = BraintreeFragment.newInstance(activity, clientToken);

                                        PayPalRequest request = new PayPalRequest(costo)
                                                .currencyCode("MXN")
                                                .landingPageType(PayPalRequest.LANDING_PAGE_TYPE_BILLING)
                                                //OBTENER LOS DATOS DE DIRECCION DE LA TARJETA
                                                .shippingAddressRequired(true)
                                                .intent(PayPalRequest.INTENT_AUTHORIZE);
                                        PayPal.requestOneTimePayment(braintreeFragment, request);

                                    } catch (InvalidArgumentException e) {

                                        progressDialog.dismiss();
                                        Log.d("exception", e.getMessage());
                                        Toast.makeText(activity, "Ocurrió un problema con el token de autenticación de cliente", Toast.LENGTH_LONG).show();
                                    }
                                }

                            });

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Eliminado", Toast.LENGTH_SHORT).show();
                            //ELIMINAMOS EL ITEM DE CONTRATACIÓN CORRESPONDIENTE DE LA LISTA Y LA ACTUALIZAMOS
                            listContrataciones.remove(data.get(0));
                            notifyItemRemoved(data.get(0));
                            //PASAMOS EL ID DE LA CONTRATACIÓN PARA REMOVERLA DE LA BD
                            //usuario.deleteEngagement(data.get(1));
                        }
                    }
                }
        );

        mBuilder[0].setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        //AGREGAMOS LA VISTA QUE PREVIAMENTE ELABORAMOS
        mBuilder[0].setView(mView);
        final AlertDialog alertDialog = mBuilder[0].create();
        alertDialog.show();

        //SETEAMOS COLOR A LOS BOTONES
        Button cancelar = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        Button aceptar = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (cancelar != null && aceptar != null) {
            cancelar.setTextColor(activity.getResources().getColor(R.color.color_error));
            aceptar.setTextColor(activity.getResources().getColor(R.color.color_success));
        }
    }


    public Button getBtnFinalizar() {
        return auxFinalizar;
    }

    public Button getBtnSubir() {
        return auxSubir;
    }

    public ImageButton getBtnPaypal() {
        return auxPayPal;
    }

    public AlertDialog getProgressDialog() {
        return progressDialog;
    }

}
