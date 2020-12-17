package com.example.katia.jobbox.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.katia.jobbox.R;
import com.example.katia.jobbox.model.adapter.SlideAdapter;

public class TutorialActivity extends AppCompatActivity {


    private ViewPager viewPager;
    private LinearLayout mDotLayout;
    private SlideAdapter myAdapter;
    private TextView[] mDots;
    private Button btnP, btnN, btnQ;
    private int currentPage;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        myAdapter = new SlideAdapter(this);
        btnP = (Button) findViewById(R.id.btnPrev);
        btnN = (Button) findViewById(R.id.btnNext);
        btnQ = (Button) findViewById(R.id.btnQuit);

        verifyTutorial();

        viewPager.setAdapter(myAdapter);
        addDotsIndicators(0);
        viewPager.addOnPageChangeListener(viewListener);

        btnN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnN.getText().toString().equalsIgnoreCase("finish")) {
                    SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("bandera", true);
                    editor.commit();
                    launchMain();
                } else {
                    viewPager.setCurrentItem(currentPage + 1);
                }
            }
        });

        btnP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(currentPage - 1);
            }
        });

        btnQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("bandera", true);
                editor.commit();
                launchMain();
            }
        });
    }


    private void launchMain() {
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void addDotsIndicators(int position) {
        mDots = new TextView[7];
        //PARA EVITAR LA DUPLICIDAD DE VISTAS
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            mDotLayout.addView(mDots[i]);
        }
        //iluminamos el dot en el que actualmente nos encontramos del slide
        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.background_material_light));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDotsIndicators(position);
            currentPage = position;

            if (position == 0) {

                btnN.setEnabled(true);
                btnP.setEnabled(false);
                btnP.setVisibility(View.INVISIBLE);
                btnP.setText("");

            } else if (position == mDots.length - 1) {
                btnN.setEnabled(true);
                btnP.setEnabled(true);
                btnP.setVisibility(View.VISIBLE);
                btnP.setText("Regresar");
                btnN.setText("Finish");

            } else {
                btnN.setEnabled(true);
                btnP.setEnabled(true);
                btnP.setVisibility(View.VISIBLE);
                btnP.setText("Regresar");
                btnN.setText("Siguiente");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    private void verifyTutorial() {

        SharedPreferences prefs = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        Boolean bandActivity = prefs.getBoolean("bandera", false);
        //SI LA BANDERA ES FALSE NO SE HA MOSTRADO EL TUTORIAL
        if (bandActivity) {
            finish();
            launchMain();
        }
    }
}
