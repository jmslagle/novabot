package com.github.novskey.novabot.data;

/**
 * Created by Paris on 17/01/2018.
 */
public class SpawnPoint {

    double lat;
    double lng;

    public SpawnPoint(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public int hashCode() {
        return (int) (lat * lng);
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().equals(this.getClass())) return false;
        SpawnPoint spawnPoint = (SpawnPoint) obj;
        return spawnPoint.lat == this.lat && spawnPoint.lng == this.lng;
    }
}
