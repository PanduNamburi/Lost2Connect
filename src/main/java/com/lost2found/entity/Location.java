package com.lost2found.entity;

import java.io.Serializable;

/**
 * Embedded Location Value Object.
 */
public class Location implements Serializable {

    private String venueName;
    private String city;
    private Double latitude;
    private Double longitude;

    public Location() {
    }

    public Location(String venueName, String city, Double latitude, Double longitude) {
        this.venueName = venueName;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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
}
