package com.istudio.weatherapp;

public class City {

    private String name;
    private String weatherDescription;
    private String temperature;
    private String humidity;
    private String windSpeed;


    public City(String pName, String pDescription, String pTemperature, String pHumidity, String pWind) {

        this.setName(pName);
        this.setWeatherDescription(pDescription);
        this.setTemperature(pTemperature);
        this.setHumidity(pHumidity);
        this.setWindSpeed(pWind);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}