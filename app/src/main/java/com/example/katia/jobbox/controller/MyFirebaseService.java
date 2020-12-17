package com.example.katia.jobbox.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import android.util.Log;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.katia.jobbox.activity.Prueba_activity;
import com.example.katia.jobbox.R;
import com.example.katia.jobbox.activity.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseService extends FirebaseMessagingService {

    String TAG = "Mensajes Firebase: ";
    String CHANNEL_ID = "0";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (true) {
                scheduleJob();
            } else {
                handleNow();
            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        } else {
            //ENVIAMOS LA NOTIFICACIÓN, COMO ES PERSONALIZADA DESDE UN BACKEND NO CONTIENE TODAS LAS CONFIGURACIONES QUE SE
            // OBTIENEN DESDE CONSOLA ASÍ QUE VAMOS A LLAMAR AL MÉTODO showNotification AUNQUE NO HAYA PASADO LA VALIDACIÓN
            String titulo = remoteMessage.getData().get("title");
            String mensaje = remoteMessage.getData().get("body");
            showNotification(titulo, mensaje);
        }

    }

    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void showNotification(String title, String body) {

        Intent intent = null;
        PendingIntent pendingIntent = null;
        NotificationCompat.Builder notificationBuilder = null;

        String url = "";
        int inicio, fin;

        //REGISTRAMOS EL CANAL DE NOTIFICACIONES
        createNotificationChannel();

        switch (title) {

            case "Prueba de registro":

                String fechaI = "";
                String fechaF = "";

                inicio = body.indexOf(":");
                fin = body.indexOf(",");
                url = body.substring(inicio + 2, fin);

                //inicio = fin;
                inicio = body.indexOf(":", fin);
                fin = body.indexOf("y", fin);
                fechaI = body.substring(inicio + 2, fin - 1);

                inicio = body.indexOf(" ", fin);
                fechaF = body.substring(inicio + 1, body.length());

                intent = new Intent(this, Prueba_activity.class);
                intent.putExtra("tipo_noty", 1);
                intent.putExtra("url_prueba", url);
                intent.putExtra("fecha_inicio", fechaI);
                intent.putExtra("fecha_fin", fechaF);

                break;

            case "Activacion de cuenta":

                intent = new Intent(this, MainActivity.class);
                intent.putExtra("message", body);

                break;

            case "Resultados de la prueba":

                inicio = 0;
                fin = 0;

                inicio = body.indexOf(":");
                fin = body.indexOf("y");
                String calif = body.substring(inicio + 2, fin - 1);
                inicio = fin;
                String status = body.substring(inicio + 2, body.length());
                intent = new Intent(this, Prueba_activity.class);
                intent.putExtra("tipo_noty", 2);
                intent.putExtra("aciertos", calif);
                intent.putExtra("status", status);

                break;

            default:

                intent = new Intent(this, MainActivity.class);

                break;

        }

        //LIMPIAR LA NOTIFICACIÓN EXISTENTE QUE ESTE EN MISMO CANAL
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //if (intent != null) {
        //EL PENDING INTENT ES LA FORMA DE EJECUTAR ACCIONES A TRAVÉS DE UNA NOTIFICACIÓN PUSH, COMO ESTE ES UN DERIVADO DE INTENT
        // UTILIZA UN OBJETO DE ESTE TIPO
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        //}
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //.setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (pendingIntent != null) {

            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    //.setDefaults(Notification.DEFAULT_SOUND)
                    .setSound(soundUri)
                    .setContentIntent(pendingIntent);
        } else {

            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    //.setDefaults(Notification.DEFAULT_SOUND)
                    .setSound(soundUri);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.GREEN);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        //ALMACENAMOS EL TOKEN EN LAS PREFERENCIAS DE USUARIO COMO ALTERNATIVA A MANTENER EL TOKEN
        SharedPreferences preferences = getSharedPreferences("MisPreferencias", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("tokenNotification", token);
        editor.commit();

    }
}
