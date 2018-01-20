package com.github.novskey.novabot.core;

import net.dv8tion.jda.core.entities.Emote;

import java.util.HashMap;
import java.util.ResourceBundle;

public enum Team {
    Uncontested,
    Valor,
    Instinct,
    Mystic;

    private static ResourceBundle bundle;

    public static void setBundle(ResourceBundle bundle){
        Team.bundle = bundle;
    }

    public static final HashMap<Team, Emote> emotes = new HashMap<>();

    public static Team fromId(int i) {
        switch (i) {
            case 0:
                return Uncontested;
            case 1:
                return Mystic;
            case 2:
                return Valor;
            case 3:
                return Instinct;
        }
        return null;
    }

    public Team fromString(String s) {
        switch (s.toLowerCase()) {
            case "valor":
                return Valor;
            case "instinct":
                return Instinct;
            case "mystic":
                return Mystic;
        }
        return null;
    }

    @Override
    public String toString() {
        switch (this) {
            case Uncontested:
                return bundle.getString("Uncontested");
            case Valor:
                return bundle.getString("Valor");
            case Mystic:
                return bundle.getString("Mystic");
            case Instinct:
                return bundle.getString("Instinct");
        }
        return "";
    }

    public String getEmote() {
        if(emotes.get(this) != null) {
            return emotes.get(this).getAsMention();
        }else{
            return "";
        }
    }
}
