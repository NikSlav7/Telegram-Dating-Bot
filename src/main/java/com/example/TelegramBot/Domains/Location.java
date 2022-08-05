package com.example.TelegramBot.Domains;


import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class Location implements Serializable {

    Double latitude;

    Double longitude;

    String countryName;

    String cityName;

    public Location(Double latitude, Double longitude, String countryName, String cityName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryName = countryName;
        this.cityName = cityName;
    }

    public Location() {

    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
