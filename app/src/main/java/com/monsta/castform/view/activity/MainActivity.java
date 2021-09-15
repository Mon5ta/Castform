package com.monsta.castform.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.monsta.castform.R;
import com.monsta.castform.model.WeatherSnapshot;
import com.monsta.castform.service.WeatherReportCall;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    BottomAppBar appBar;
    SearchView searchView;
    TextView locationTextView, descriptionTextView, temperatureTextView, minTemperatureTextView,
            maxTemperatureTextView, humidityTextView, pressureTextView, windSpeedTextView, windDirectionTextView,
            windSpeedDescriptionTextView;
    ImageView castformImageView;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.fab);
        appBar = findViewById(R.id.bottomBar);

        setSupportActionBar(appBar);

        locationTextView = findViewById(R.id.locationTitle);
        descriptionTextView = findViewById(R.id.descriptionValue);
        temperatureTextView = findViewById(R.id.temperatureValue);
        minTemperatureTextView = findViewById(R.id.minTemperatureValue);
        maxTemperatureTextView = findViewById(R.id.maxTemperatureValue);
        humidityTextView = findViewById(R.id.humidityValue);
        pressureTextView = findViewById(R.id.pressureValue);
        windSpeedTextView = findViewById(R.id.windSpeedValue);
        windDirectionTextView = findViewById(R.id.windDirectionValue);

        windSpeedDescriptionTextView = findViewById(R.id.windSpeed);

        castformImageView = findViewById(R.id.castformImageView);

        windSpeedDescriptionTextView.setOnLongClickListener(view -> {
            String description;
            switch (windDirectionTextView.getText().toString().trim()) {
                case "Β":
                    description = "Βόρειος";
                    break;
                case "ΒΑ":
                    description = "Μέσης";
                    break;
                case "Α":
                    description = "Απηλιώτης";
                    break;
                case "ΝΑ":
                    description = "Εύρος";
                    break;
                case "Ν":
                    description = "Νότιος";
                    break;
                case "ΝΔ":
                    description = "Λίβας";
                    break;
                case "Δ":
                    description = "Ζέφυρος";
                    break;
                case "ΒΔ":
                    description = "Σκίρων";
                    break;
                default:
                    description = "Λάθος τιμή ανέμου!";
            }
            Toast.makeText(MainActivity.this, description, Toast.LENGTH_SHORT).show();
            return true;
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();

                    if (location != null) {
                        initialFillViews(location.getLatitude(), location.getLongitude());
                    } else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location locationNewInstance = locationResult.getLastLocation();
                                initialFillViews(locationNewInstance.getLatitude(), locationNewInstance.getLongitude());
                            }
                        };
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void initialFillViews(double latitude, double longitude){
        WeatherReportCall.getWeatherReportByLongLat(latitude, longitude, new WeatherReportCall.IVolleyMessenger() {
            @Override
            public void onResponse(WeatherSnapshot weatherSnapshot) {
                defineCastform(weatherSnapshot.getMain());

                locationTextView.setText(weatherSnapshot.getLocationTitle());
                descriptionTextView.setText(weatherSnapshot.getDescription().trim().substring(0, 1).toUpperCase() + weatherSnapshot.getDescription().trim().substring(1).toLowerCase());
                temperatureTextView.setText(String.format("%s%s", weatherSnapshot.getTemperature(), getResources().getString(R.string.celsius)));
                minTemperatureTextView.setText(String.format("%s%s", weatherSnapshot.getMinTemperature(), getResources().getString(R.string.celsius)));
                maxTemperatureTextView.setText(String.format("%s%s", weatherSnapshot.getMaxTemperature(), getResources().getString(R.string.celsius)));
                humidityTextView.setText(String.format("%s%%", weatherSnapshot.getHumidity()));
                pressureTextView.setText(String.valueOf(weatherSnapshot.getPressure()));
                windSpeedTextView.setText(String.format("%s bft", windSpeedFromMsToBeaufort(weatherSnapshot.getWindSpeed())));
                windDirectionTextView.setText(defineWindDirection(weatherSnapshot.getWindDirection()));

            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }, MainActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            ((ActivityManager)MainActivity.this.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_bar_menu, menu);
        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                appBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                appBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                return true;
            }
        };
        menu.findItem(R.id.search).setOnActionExpandListener(onActionExpandListener);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setQueryHint(getResources().getString(R.string.search));

        searchView.setOnSearchClickListener(view -> appBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END));

        searchView.setOnCloseListener(() -> {
            appBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
            return false;
        });



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                WeatherReportCall.getWeatherReportByLocation(query, new WeatherReportCall.IVolleyMessenger() {
                    @Override
                    public void onResponse(WeatherSnapshot weatherSnapshot) {

                        defineCastform(weatherSnapshot.getMain());

                        locationTextView.setText(query.trim().substring(0, 1).toUpperCase() + query.trim().substring(1).toLowerCase());
                        descriptionTextView.setText(weatherSnapshot.getDescription().trim().substring(0, 1).toUpperCase() + weatherSnapshot.getDescription().trim().substring(1).toLowerCase());
                        temperatureTextView.setText(String.format("%s%s", weatherSnapshot.getTemperature(), getResources().getString(R.string.celsius)));
                        minTemperatureTextView.setText(String.format("%s%s", weatherSnapshot.getMinTemperature(), getResources().getString(R.string.celsius)));
                        maxTemperatureTextView.setText(String.format("%s%s", weatherSnapshot.getMaxTemperature(), getResources().getString(R.string.celsius)));
                        humidityTextView.setText(String.format("%s%%", weatherSnapshot.getHumidity()));
                        pressureTextView.setText(String.valueOf(weatherSnapshot.getPressure()));
                        windSpeedTextView.setText(String.format("%s bft", windSpeedFromMsToBeaufort(weatherSnapshot.getWindSpeed())));
                        windDirectionTextView.setText(defineWindDirection(weatherSnapshot.getWindDirection()));

                        searchView.onActionViewCollapsed();
                        appBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }, MainActivity.this);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void defineCastform(String mainWeatherDescription) {
        switch (mainWeatherDescription) {
            case "Thunderstorm":
            case "Rain":
            case "Drizzle":
                castformImageView.setImageResource(R.drawable.castform_rainny);
                break;
            case "Snow":
            case "Clouds":
                castformImageView.setImageResource(R.drawable.castform_snowy);
                break;
            case "Clear":
                boolean isDay;
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                isDay = hour > 6 && hour < 18;
                if (isDay) {
                    castformImageView.setImageResource(R.drawable.castform_sunny);
                    break;
                }
                castformImageView.setImageResource(R.drawable.castform_clear);
                break;
            default:
                castformImageView.setImageResource(R.drawable.castform_windy);
        }
    }

    private String defineWindDirection(double windDegrees) {
        String[] direction = {"Β", "ΒΑ", "Α", "ΝΑ", "Ν", "ΝΔ", "Δ", "ΒΔ", "Β"};
        return direction[(int) Math.round((windDegrees % 360) / 45)];
    }

    private double windSpeedFromMsToBeaufort(double windSpeed) {
        return Math.ceil(Math.cbrt(Math.pow(windSpeed / 0.836, 2)));
    }

    @Override
    public void onBackPressed() {
        searchView.onActionViewCollapsed();
        appBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
    }
}