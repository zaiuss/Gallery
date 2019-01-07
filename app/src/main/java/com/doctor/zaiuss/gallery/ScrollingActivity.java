package com.doctor.zaiuss.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class ScrollingActivity extends AppCompatActivity {
    Context contexto;
    private String imagenURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intentFromMain = getIntent();
        contexto = getApplicationContext();

        //TODO: Hay que hacer un objeto cuadro, donde pillamos titulo, descripcion, etc
        toolbar.setTitle(intentFromMain.getStringExtra("Titulo"));
        setSupportActionBar(toolbar);

        ImageLoader imageLoader = VolleyImageSingleton.getInstance(getApplicationContext()).getImageLoader();
        NetworkImageView imagen = (NetworkImageView) findViewById(R.id.networkImageView);
        imagenURL = intentFromMain.getStringExtra("dirUrl");

        imagen.setImageUrl(intentFromMain.getStringExtra("dirUrl"), imageLoader);

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(contexto, Zoomable_Activity.class);
                intent.putExtra("dirUrl", imagenURL);
                startActivity(intent);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
