package com.example.katia.jobbox.model.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.katia.jobbox.R;
import com.squareup.picasso.Picasso;

public class SlideAdapter extends PagerAdapter {

    private Context context;
    LayoutInflater inflater;

    public SlideAdapter(Context context) {
        this.context = context;
    }

    //listado de imagenes
    public int[] listImg = {
            R.drawable.logo,
            R.drawable.registro_2,
            R.drawable.roles,
            R.drawable.admin_3,
            R.drawable.notify,
            R.drawable.aprobado_2,
            R.drawable.work_2
    };

    //listado de titulos
    public String[] listTitles = {
            "¡Bienvenido a Job-box!",
            "Paso 1",
            "Paso 2",
            "Paso 3",
            "¿Qué sigue después?",
            "Activación de cuenta",
            "Ya estás registrado"
    };


    //listado de descripciones
    public String[] listDescrip = {
            "Para crear una cuenta solo tienes que seguir estos sencillos pasos",
            "Pulsa en la opción registrarse, ¡es totalmente gratis!",
            "Ahora selecciona el tipo de rol que encaja con tus necesidades y a continuación, llena el formulario con tus datos",
            "Finalmente, un administrador atenderá tu solicitud y recibirás una notificación",
            "Un compromiso en nuestra plataforma es contar con gente preparada, por ello la notificación que recibirás tendrá un link y" +
                    " una fecha en la que serás evaluado a través de 2 breves pruebas acorde a tu oficio, nada del otro mundo",
            "Una vez que hayas realizado las pruebas recibirás una notificación",
            "¡Ahora ya puedes publicar tus servicios de trabajador en la plataforma y comenzar a conseguir más clientes!",
    };

    //listado de colores de fondo
    public int[] listColores = {
            //naranja
            Color.rgb(230,117,12),
            //azul medio
            Color.rgb(22,155,215),
            //azul aqua
            Color.rgb(1,188,212),
            //rosa
            Color.rgb(255,187,187),
            //azul fuerte
            Color.rgb(0, 48, 135),
            //morado
            Color.rgb(124, 14, 205),
            //verde
            Color.rgb(163,204,0),
    };


    @Override
    public int getCount() {
        return listTitles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (LinearLayout)object);
    }

    //Función para cargar los elementos correspondientes al tutorial de introducción de la App, entradas: un objeto de conjunto de vistas y una
    //variable llamada position que alude a la posición en la que se encuentra la vista visualizada por el usuario, salida: un objeto de tipo vista
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        //INICIALIZAMOS LOS RECURSOS
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide, container, false);
        LinearLayout layoutslide = (LinearLayout) view.findViewById(R.id.slidelayout);
        ImageView imgslide = (ImageView) view.findViewById(R.id.imgslide);
        TextView titleslide = (TextView) view.findViewById(R.id.titleslide);
        TextView descslide = (TextView) view.findViewById(R.id.subtitleslide);

        layoutslide.setBackgroundColor(listColores[position]);

        Picasso.get().load(listImg[position])
                //fit() NOS PERMITE HACER UN RESIZE DE LA IMÁGEN A LO QUE ESPERA EL CONTENEDOR EN DONDE VA A ESTAR
                .fit()
                .centerInside()
                .into(imgslide);

        titleslide.setText(listTitles[position]);
        descslide.setText(listDescrip[position]);

        //AGREGAMOS LA VISTA PARA SER MOSTRADA
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
