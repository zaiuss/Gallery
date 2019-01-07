package com.doctor.zaiuss.gallery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CustomImageListener.CustomOnResponseListener{
    ImageLoader imageLoader;
    List<Cuadro> listaCuadros = new ArrayList<>();
    Context contexto;
    String test;
    String urlJson = "http://35.204.134.175/f1l3s/jsonArray.txt";
    RecyclerView recyclerView;
    StaggeredAdapter adapter;

    int imagesCount = 0;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contexto = getApplicationContext();
        setTitle("School");

        // Adapter
        adapter = new StaggeredAdapter();

        // Layout Manager
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        // Recycler
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading pictures...");
        progressDialog.setCancelable(false);
        //TODO:gestion
        progressDialog.show();

        // Volley que pide JsonArray, cuando lo reciba setteará el adapter el la vista
        llamadaJsonArrayVolley(urlJson);
//        recyclerView.setAdapter(adapter);
    }

    //Volley
    public void llamadaJsonArrayVolley(String URL){
        final JsonArrayRequest jsonArray = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Type listaTypeServer = new TypeToken<List<Cuadro>>(){}.getType();
                listaCuadros = new Gson().fromJson(response.toString(), listaTypeServer);
                Log.i("FINtest", "Lista llena");
//                StaggeredAdapter adapter = new StaggeredAdapter();
//                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//                gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
//                recyclerView.setLayoutManager(gridLayoutManager);
                recyclerView.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("errorTest", "Error recibido" + error.toString());
            }
        });
        VolleyImageSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArray);
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
        if (imagesCount >= listaCuadros.size()) progressDialog.hide();
        else imagesCount += 1;
        Log.i("ImageLoaderResponse", response.toString() + " imageCount: " + imagesCount + " isImmediate: " + isImmediate);
    }

    @Override
    public void onError(VolleyError error) {
        if (imagesCount >= listaCuadros.size()) progressDialog.hide();
        else imagesCount += 1;
        Log.i("ImageLoaderResponse", error.toString() + " imageCount: " + imagesCount);
    }

    //Adapter
    public class StaggeredAdapter extends RecyclerView.Adapter<StaggeredAdapter.StaggeredHolder> {
        @Override
        public StaggeredAdapter.StaggeredHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View contactView = inflater.inflate(R.layout.view_item, parent, false);

            contactView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int posicion = recyclerView.getChildLayoutPosition(view);
                    //Toast.makeText(contexto, "Posicion" + posicion, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(contexto, ScrollingActivity.class);
                    intent.putExtra("Titulo", listaCuadros.get(posicion).getTitulo());
                    intent.putExtra("dirUrl", listaCuadros.get(posicion).getDirUrl());
                    startActivity(intent);
                }
            });
            return new StaggeredAdapter.StaggeredHolder(contactView);
        }

        @Override
        public void onBindViewHolder(StaggeredAdapter.StaggeredHolder holder, int position) {
            imageLoader = VolleyImageSingleton.getInstance(contexto).getImageLoader();
            CustomImageListener imageListener = CustomImageListener.getImageListener(MainActivity.this,
                    holder.imageview, R.mipmap.loading, android.R.drawable.ic_dialog_alert);
            imageLoader.get(listaCuadros.get(position).getDirUrl(), imageListener);
            holder.imageview.setImageUrl(listaCuadros.get(position).getDirUrl(), imageLoader);
        }

        @Override
        public int getItemCount() {
            if(listaCuadros != null){
                return listaCuadros.size();
            }else {
                return 0;
            }
        }

        class StaggeredHolder extends RecyclerView.ViewHolder {
            NetworkImageView imageview;

            public StaggeredHolder(View itemView) {
                super(itemView);
                imageview = itemView.findViewById(R.id.iv_item);
            }
        }
    }
}
