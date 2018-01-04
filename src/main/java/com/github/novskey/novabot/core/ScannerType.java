package com.github.novskey.novabot.core;

/**
 * Created by Paris on 16/12/2017.
 */
public enum ScannerType {
    RocketMap,
    Monocle,
    Hydro74000Monocle,
    PhilMap;

    public static ScannerType fromString(String str){
        switch (str.toLowerCase().trim()){
            case "rocketmap":
                return RocketMap;
            case "monocle":
                return Monocle;
            case "hydro74000monocle":
                return Hydro74000Monocle;
            case "philmap":
                return PhilMap;
        }

        return null;
    }
}
