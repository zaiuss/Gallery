package com.doctor.zaiuss.gallery;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class Zoomable_Activity extends Activity implements OnTouchListener {
    // Constantes que definen los estados posibles.
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    // Variable para el estado
    private int mode = NONE;
    // Variables para el Matrix
    private Matrix initialMatrix = new Matrix();
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    // Puntos para recordar las cordenadas
    private PointF start = new PointF();
    private PointF mid = new PointF();
    // Valores floats que necesitamos almacenar
    private float dist = 1f;
    // Para controlar el double tap.
    private static final long DOUBLE_TAP_TIME = 150; // Milisegundos
    long lastTap = 0;
    long tap;
    // Imagen
    private NetworkImageView viewDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoomable_activity_layout);
        /** Cargamos la imagen y se la seteamos a la vista **/
        ImageLoader imageLoader = VolleyImageSingleton.getInstance(getApplicationContext()).getImageLoader();
        viewDetail = findViewById(R.id.detail_imageView);
        viewDetail.setImageUrl(getIntent().getStringExtra("dirUrl"), imageLoader);

        /** Su listener on touch **/
        viewDetail.setOnTouchListener(this);

        /**
         * Con el observer listener esperamos a que la imagen esté cargada y asi poder obtener sus valores.
         * Con esto tenemos el matrix que tenga al principio definida por scaleType=fitCenter en el layout
         */
        viewDetail.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initialMatrix = viewDetail.getImageMatrix();
                matrix.set(initialMatrix);
            }
        });

        Toast.makeText(this, "Pulsa dos veces para recolocar la imagen", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        viewDetail = (NetworkImageView) view;

        /** En cuanto salte el listener la imagen pasa a tener scaleType=matrix **/
        viewDetail.setScaleType(ImageView.ScaleType.MATRIX);

        /** Seteamos tap con la hora actual **/
        tap = System.currentTimeMillis();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                /** Si ha hecho un double tap **/
                if (tap - lastTap < DOUBLE_TAP_TIME) {
                    Log.i("DOUBLE TAP", "Double tap");
                    viewDetail.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    lastTap = 0;
                }
                savedMatrix.set(matrix);
                start.set(motionEvent.getX(), motionEvent.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                dist = spacing(motionEvent);
                if (dist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, motionEvent);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    float dx = motionEvent.getX() - start.x;
                    float dy = motionEvent.getY() - start.y;
                    matrix.postTranslate(dx, dy);
                } else if (mode == ZOOM) {
                    float newDist = spacing(motionEvent);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float escala = (newDist / dist);
                        matrix.postScale(escala, escala, mid.x, mid.y);
                    }
                }
                break;
        }
        /** actualizamos lastTap para calcular si se hace double tap **/
        lastTap = tap;

        /** Seteamos nuestro matrix. **/
        viewDetail.setImageMatrix(matrix);
        return true;
    }

    /**
     * Metodo para determinar el punto medio entre los dos dedos
     */
    private void midPoint(PointF midPoint, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        midPoint.set(x / 2, y / 2);
    }

    /**
     * Metodo para determinar el espacio entre los dos dedos
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        float s = (x * x) + (y * y);
        return (float) Math.sqrt(s);
    }
}