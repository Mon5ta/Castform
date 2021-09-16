package com.monsta.castform.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.monsta.castform.model.WeatherHttpRequestSingleton;
import com.monsta.castform.model.WeatherSnapshot;

import org.json.JSONException;

public class WeatherReportCall {

    private static final String DEMO_API_TOKEN = "abcdefghijklmnopqrstuvwxyz";
    private static final String URL_FOR_WEATHER_REPORT_BY_LOCATION_PT1 = "https://api.openweathermap.org/data/2.5/weather?q=";
    private static final String URL_FOR_WEATHER_REPORT_BY_LOCATION_PT2 = "&units=metric&lang=el&appid=" + DEMO_API_TOKEN;

    private static final String URL_FOR_WEATHER_REPORT_BY_LONG_LAT_PT1 = "https://api.openweathermap.org/data/2.5/weather?lat=";
    private static final String URL_FOR_WEATHER_REPORT_BY_LONG_LAT_PT2 = "&lon=";
    private static final String URL_FOR_WEATHER_REPORT_BY_LONG_LAT_PT3 = "&units=metric&lang=el&appid=";
    private static Context context;

    public static void getWeatherReportByLocation(String location, IVolleyMessenger listener, Context context) {

        WeatherReportCall.context = context;

        String url = URL_FOR_WEATHER_REPORT_BY_LOCATION_PT1 + location.trim() + URL_FOR_WEATHER_REPORT_BY_LOCATION_PT2;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                WeatherSnapshot weatherSnapshot = new WeatherSnapshot(
                        response.getJSONObject("sys").getString("country"),
                        response.getString("name"),
                        response.getJSONArray("weather").getJSONObject(0).getString("main"),
                        response.getJSONArray("weather").getJSONObject(0).getString("description"),
                        response.getJSONObject("main").getDouble("temp"),
                        response.getJSONObject("main").getDouble("temp_min"),
                        response.getJSONObject("main").getDouble("temp_max"),
                        response.getJSONObject("main").getDouble("humidity"),
                        response.getJSONObject("main").getDouble("pressure"),
                        response.getJSONObject("wind").getDouble("speed"),
                        response.getJSONObject("wind").getDouble("deg")
                );

                listener.onResponse(weatherSnapshot);
            } catch (JSONException e) {
                listener.onError("Το conversion είναι.. σαν τη μάπα σου!");
            }
        }, error -> listener.onError("Δεν έχεις σήμα! / Εσφαλμένη τοποθεσία!"));

        WeatherHttpRequestSingleton.getInstance(WeatherReportCall.context).addToRequestQueue(jsonObjectRequest);

    }

    public static void getWeatherReportByLongLat(double latitude, double longitude, IVolleyMessenger listener, Context context) {

        WeatherReportCall.context = context;

        String url = URL_FOR_WEATHER_REPORT_BY_LONG_LAT_PT1 + latitude + URL_FOR_WEATHER_REPORT_BY_LONG_LAT_PT2 + longitude + URL_FOR_WEATHER_REPORT_BY_LONG_LAT_PT3 + DEMO_API_TOKEN;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                WeatherSnapshot weatherSnapshot = new WeatherSnapshot(
                        response.getJSONObject("sys").getString("country"),
                        response.getString("name"),
                        response.getJSONArray("weather").getJSONObject(0).getString("main"),
                        response.getJSONArray("weather").getJSONObject(0).getString("description"),
                        response.getJSONObject("main").getDouble("temp"),
                        response.getJSONObject("main").getDouble("temp_min"),
                        response.getJSONObject("main").getDouble("temp_max"),
                        response.getJSONObject("main").getDouble("humidity"),
                        response.getJSONObject("main").getDouble("pressure"),
                        response.getJSONObject("wind").getDouble("speed"),
                        response.getJSONObject("wind").getDouble("deg")
                );

                listener.onResponse(weatherSnapshot);
            } catch (JSONException e) {
                listener.onError("Το conversion είναι.. σαν τη μάπα σου!");
            }
        }, error -> listener.onError("Δεν έχεις σήμα! / Εσφαλμένη τοποθεσία!"));

        WeatherHttpRequestSingleton.getInstance(WeatherReportCall.context).addToRequestQueue(jsonObjectRequest);

    }

    public interface IVolleyMessenger {
        void onResponse(WeatherSnapshot weatherSnapshot);

        void onError(String message);
    }

}
