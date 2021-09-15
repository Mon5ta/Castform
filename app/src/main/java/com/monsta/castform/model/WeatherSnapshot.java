package com.monsta.castform.model;

public class WeatherSnapshot {
    private final String country;
    private final String locationTitle;
    private final String main;
    private final String description;
    private final double temperature;
    private final double minTemperature;
    private final double maxTemperature;
    private final double humidity;
    private final double pressure;
    private final double windSpeed;
    private final double windDirection;

    public WeatherSnapshot(String country, String locationTitle, String main, String description, double temperature, double minTemperature, double maxTemperature, double humidity, double pressure, double windSpeed, double windDirection) {
        this.country = country;
        this.locationTitle = locationTitle;
        this.main = main;
        this.description = description;
        this.temperature = temperature;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
    }

    public String getCountry() {
        return country;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

}
