package com.github.novskey.novabot.core;

import net.dv8tion.jda.core.entities.Emote;

import java.util.HashMap;

import static com.github.novskey.novabot.Util.StringLocalizer.getLocalString;

public enum Weather {
    None,
    Clear,
    Rain,
    PartlyCloudy,
    Cloudy,
    Windy,
    Snow,
    Fog;

    public static final HashMap<Weather, Emote> emotes = new HashMap<>();

    public String toEmoteName() {
        switch(this){
            case None:
                return "None";
            case Clear:
                return "Clear";
            case Rain:
                return "Rain";
            case PartlyCloudy:
                return "PartlyCloudy";
            case Cloudy:
                return "Cloudy";
            case Windy:
                return "Windy";
            case Snow:
                return "Snow";
            case Fog:
                return "fogweather";
            default:
                return "unkn";
        }
    }

    @Override
    public String toString () {
        switch(this){
            case None:
                return getLocalString("None");
            case Clear:
                return getLocalString("Clear");
            case Rain:
                return getLocalString("Rain");
            case PartlyCloudy:
                return getLocalString("PartlyCloudy");
            case Cloudy:
                return getLocalString("Cloudy");
            case Windy:
                return getLocalString("Windy");
            case Snow:
                return getLocalString("Snow");
            case Fog:
                return getLocalString("Fog");
            default:
                return "unkn";
        }
    }

    public static Weather fromId(int i){
        switch(i){
            case 0:
                return None;
            case 1:
                return Clear;
            case 2:
                return Rain;
            case 3:
                return PartlyCloudy;
            case 4:
                return Cloudy;
            case 5:
                return Windy;
            case 6:
                return Snow;
            case 7:
                return Fog;
            default:
                return null;
        }
    }

    public String getEmote() {
        if(emotes.get(this) != null) {
            return emotes.get(this).getAsMention();
        }else{
            return "";
        }
    }

}
