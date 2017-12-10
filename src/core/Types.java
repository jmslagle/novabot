package core;

import java.util.ArrayList;
import java.util.Arrays;

public class Types {

    public static ArrayList<String> getStrengths (String type){
        String types[] = new String[0];
        switch (type.toLowerCase()) {
            case "normal":
                break;
            case "fighting":
                types = new String[]{"normal","rock","steel","ice","dark"};
                break;
            case "flying":
                types = new String[]{"fighting", "bug", "grass"};
                break;
            case "poison":
                types = new String[]{"grass", "fairy"};
                break;
            case "ground":
                types = new String[]{"poison", "rock", "steel","fire","electric"};
                break;
            case "rock":
                types = new String[]{"flying", "bug", "fire", "ice"};
                break;
            case "bug":
                types = new String[]{"grass","psychic","dark"};
                break;
            case "ghost":
                types = new String[]{"ghost","psychic"};
                break;
            case "steel":
                types = new String[]{"rock","ice"};
                break;
            case "fire":
                types = new String[]{"bug","steel","grass","ice"};
                break;
            case "water":
                types = new String[]{"ground","rock","fire"};
                break;
            case "grass":
                types = new String[]{"ground","rock","water"};
                break;
            case "electric":
                types = new String[]{"flying","water"};
                break;
            case "psychic":
                types = new String[]{"fighting","poison"};
                break;
            case "ice":
                types = new String[]{"flying","ground","grass","dragon"};
                break;
            case "dragon":
                types = new String[]{"dragon"};
                break;
            case "dark":
                types = new String[]{"ghost","psychic"};
                break;
            case "fairy":
                types = new String[]{"fighting","dragon","dark"};
                break;
        }
        return new ArrayList<>(Arrays.asList(types));
    }

    public static ArrayList<String> getWeaknesses (String type){
        String types[] = new String[0];
        switch (type.toLowerCase()) {
            case "normal":
                types = new String[]{"fighting"};
                break;
            case "fighting":
                types = new String[]{"flying", "psychic", "fairy"};
                break;
            case "flying":
                types = new String[]{"rock", "electric", "ice"};
                break;
            case "poison":
                types = new String[]{"ground", "psychic"};
                break;
            case "ground":
                types = new String[]{"water", "grass", "ice"};
                break;
            case "rock":
                types = new String[]{"fighting", "ground", "steel", "water", "grass"};
                break;
            case "bug":
                types = new String[]{"flying", "rock", "fire"};
                break;
            case "ghost":
                types = new String[]{"ghost", "dark"};
                break;
            case "steel":
                types = new String[]{"fighting", "ground", "fire"};
                break;
            case "fire":
                types = new String[]{"ground", "rock", "water"};
                break;
            case "water":
                types = new String[]{"grass", "electric"};
                break;
            case "grass":
                types = new String[]{"flying", "poison", "bug", "fire", "ice"};
                break;
            case "electric":
                types = new String[]{"ground"};
                break;
            case "psychic":
                types = new String[]{"bug", "ghost", "dark"};
                break;
            case "ice":
                types = new String[]{"fighting", "rock", "steel", "fire"};
                break;
            case "dragon":
                types = new String[]{"ice", "dragon", "fairy"};
                break;
            case "dark":
                types = new String[]{"fighting", "bug", "fairy"};
                break;
            case "fairy":
                types = new String[]{"poison", "steel"};
                break;
        }
        return new ArrayList<>(Arrays.asList(types));
    }

}
