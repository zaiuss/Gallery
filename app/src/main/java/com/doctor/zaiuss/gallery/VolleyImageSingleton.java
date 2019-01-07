package com.doctor.zaiuss.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Zaiuss on 27/07/17.
 */

public class VolleyImageSingleton  {
    private static VolleyImageSingleton singleton;
    private RequestQueue requestQueue;
    private static Context context;
    private ImageLoader imageLoader;

    private VolleyImageSingleton(Context contexto) {
        VolleyImageSingleton.context = contexto;
        requestQueue = getRequestQueue();
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> lruCache = new LruCache<>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return lruCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                lruCache.put(url, bitmap);
            }
        });
    }

    public static synchronized VolleyImageSingleton getInstance(Context context){
        if(singleton == null){
            singleton = new VolleyImageSingleton(context.getApplicationContext());
        }
        return singleton;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null){
            Cache cache = new DiskBasedCache(context.getCacheDir(), 10*1024*1024);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

    public void cancelRequestQueue(){
        requestQueue.cancelAll(getRequestQueue());
    }
}
