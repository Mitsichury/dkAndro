package com.example.mitsichury.firsttestlocation;

/**
 * Created by MITSICHURY on 03/08/2015.
 */
public class LocPosition {
    private double longitude;
    private double lattitude;

    public double getLongitude() {
        return longitude;
    }

    public double getLattitude() {
        return lattitude;
    }

    public LocPosition(double longitude, double lattitude) {

        this.longitude = longitude;
        this.lattitude = lattitude;
    }

    public double distance(LocPosition lp){
        return Math.sqrt(Math.pow(this.longitude-lp.getLongitude(),2)+Math.pow(this.lattitude-lp.getLattitude(),2));
    }
}
