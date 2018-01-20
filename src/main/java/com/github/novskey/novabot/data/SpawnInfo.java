package com.github.novskey.novabot.data;

import com.github.novskey.novabot.maps.GeocodedLocation;

import java.time.ZoneId;

/**
 * Created by Paris on 17/01/2018.
 */
public class SpawnInfo {

    public GeocodedLocation geocodedLocation = null;
    public SpawnPoint spawnPoint;
    public ZoneId zoneId = null;

    public SpawnInfo(SpawnPoint point) {
        this.spawnPoint = point;
    }
}
