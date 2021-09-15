package com.monsta.castform.model;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class WeatherHttpRequestSingleton {

        private static WeatherHttpRequestSingleton instance;
        private RequestQueue requestQueue;
        private static Context context;

        private WeatherHttpRequestSingleton(Context context) {
            WeatherHttpRequestSingleton.context = context;
            requestQueue = getRequestQueue();
        }

        public static synchronized WeatherHttpRequestSingleton getInstance(Context context) {
            if (instance == null) {
                instance = new WeatherHttpRequestSingleton(context);
            }
            return instance;
        }

        public RequestQueue getRequestQueue() {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(context.getApplicationContext());
            }
            return requestQueue;
        }

        public <T> void addToRequestQueue(Request<T> req) {
            getRequestQueue().add(req);
        }

}
