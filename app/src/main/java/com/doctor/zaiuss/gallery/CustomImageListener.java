package com.doctor.zaiuss.gallery;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by mrj on 7/31/17.
 */

public class CustomImageListener implements ImageLoader.ImageListener {

    Context context;

    public static CustomImageListener getImageListener(final CustomOnResponseListener customOnResponseListener, final ImageView view,
                                                       final int defaultImageResId,
                                                       final int errorImageResId) {

        return new CustomImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customOnResponseListener.onError(error);
                if (errorImageResId != 0) {
                    view.setImageResource(errorImageResId);
                }
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                //TODO: aqui deberías poder controlar cuando llega cada foto?? Prueba
                customOnResponseListener.onResponse(response, isImmediate);

                if (response.getBitmap() != null) {
                    view.setImageBitmap(response.getBitmap());
                } else if (defaultImageResId != 0) {
                    view.setImageResource(defaultImageResId);
                }
            }
        };
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    public interface CustomOnResponseListener {
        void onResponse(ImageLoader.ImageContainer response, boolean isImmediate);
        void onError(VolleyError error);
    }
}
