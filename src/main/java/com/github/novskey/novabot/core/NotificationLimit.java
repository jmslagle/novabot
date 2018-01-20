package com.github.novskey.novabot.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by miriam on 5/7/17.
 */
public class NotificationLimit {

    public static final Logger novabotLog = LoggerFactory.getLogger("novabot");
    public final Integer pokemonLimit;
    public final Integer raidLimit;
    public final Integer presetLimit;
    public int sumSize;

    public NotificationLimit(Integer pokemonLimit, Integer raidLimit, Integer presetLimit) {
        this.pokemonLimit = pokemonLimit;
        this.raidLimit = raidLimit;
        this.presetLimit = presetLimit;
        sumSize =  (pokemonLimit == null ? Integer.MAX_VALUE : pokemonLimit) + (raidLimit == null ? Integer.MAX_VALUE : raidLimit) + (presetLimit == null ? Integer.MAX_VALUE : presetLimit);
    }

    public static NotificationLimit fromString(String line) {
        String[] limitSplit = line.split(",");

        Integer pokeLimit = null, raidLimit = null, presetLimit = null;

        String pokeLimitStr = null;
        try {
            pokeLimitStr = limitSplit[0].substring(limitSplit[0].indexOf("[") + 1).trim();
            pokeLimit = pokeLimitStr.equals("n") ? null : Integer.parseInt(pokeLimitStr);
        } catch (NumberFormatException e) {
            novabotLog.error(String.format("Error converting pokemon limit: \"%s\" to a number", pokeLimitStr));
        }

        String raidLimitStr = limitSplit[1].trim();
        try {
            raidLimit = raidLimitStr.equals("n") ? null : Integer.parseInt(raidLimitStr);
        } catch (NumberFormatException e) {
            novabotLog.error(String.format("Error converting raid limit: \"%s\" to a number", raidLimitStr));
        }

        String presetLimitStr = limitSplit[2].substring(0, limitSplit[2].indexOf("]")).trim();
        try {
            presetLimit = presetLimitStr.equals("n") ? null : Integer.parseInt(presetLimitStr);
        } catch (NumberFormatException e) {
            novabotLog.error(String.format("Error converting preset limit: \"%s\" to a number", presetLimitStr));
        }

        return new NotificationLimit(pokeLimit, raidLimit, presetLimit);
    }

    public static void main(String[] args) {
        System.out.println(NotificationLimit.fromString("[5, 2, 1  ]"));
    }

    @Override
    public String toString() {
        return String.format("%s pokemon, %s raid%s, %s preset%s",
                             (pokemonLimit == null ? "unlimited" : pokemonLimit),
                             (raidLimit == null ? "unlimited" : raidLimit),
                             (raidLimit == null || raidLimit != 1) ? "s" : "",
                             presetLimit == null ? "unlimited" : presetLimit,
                             (presetLimit == null || presetLimit != 1) ? "s" : "");
    }

    public String toWords() {
        return String.format("%s pokemon, %s raid%s, and %s preset%s",
                             (pokemonLimit == null ? "unlimited" : pokemonLimit),
                             (raidLimit == null ? "unlimited" : raidLimit),
                             (raidLimit == null || raidLimit != 1) ? "s" : "",
                             presetLimit == null ? "unlimited" : presetLimit,
                             (presetLimit == null || presetLimit != 1) ? "s" : "");
    }

}
