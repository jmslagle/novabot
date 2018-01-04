package com.github.novskey.novabot.maps;

import java.util.HashMap;

/**
 * Created by Owner on 19/05/2017.
 */
public class GeocodedLocation {

    private final HashMap<String,String> locationProperties;

    public GeocodedLocation(){
        locationProperties = new HashMap<>();
    }

    public void set(String key, String value) {
        locationProperties.put(key,value);
    }

    public HashMap<String, String> getProperties() {
        return locationProperties;
    }
}
