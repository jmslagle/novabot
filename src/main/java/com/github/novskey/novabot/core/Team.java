package com.github.novskey.novabot.core;

import net.dv8tion.jda.core.entities.Emote;

import java.util.HashMap;

import static com.github.novskey.novabot.Util.StringLocalizer.getLocalString;

public enum Team {
    Uncontested,
    Valor,
    Instinct,
    Mystic;

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
                return getLocalString("Uncontested");
            case Valor:
                return getLocalString("Valor");
            case Mystic:
                return getLocalString("Mystic");
            case Instinct:
                return getLocalString("Instinct");
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
