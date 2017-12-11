package core;

import net.dv8tion.jda.core.utils.SimpleLog;

import static core.MessageListener.novabotLog;

/**
 * Created by miriam on 5/7/17.
 */
public class NotificationLimit {

    public final Integer pokemonLimit;
    public final Integer raidLimit;
    public final Integer presetLimit;

    private NotificationLimit(Integer pokemonLimit, Integer raidLimit, Integer presetLimit){
        this.pokemonLimit = pokemonLimit;
        this.raidLimit = raidLimit;
        this.presetLimit = presetLimit;
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

    public static void main(String[] args) {
        System.out.println(NotificationLimit.fromString("[5, 2, 1  ]"));
    }

    public static NotificationLimit fromString(String line) {
        String[] limitSplit = line.split(",");

        Integer pokeLimit = null, raidLimit = null, presetLimit = null;

        String pokeLimitStr = null;
        try {
            pokeLimitStr = limitSplit[0].substring(limitSplit[0].indexOf("[") + 1).trim();
            pokeLimit = pokeLimitStr.equals("n") ? null : Integer.parseInt(pokeLimitStr);
        } catch (NumberFormatException e) {
            novabotLog.log(SimpleLog.Level.FATAL, String.format("Error converting pokemon limit: \"%s\" to a number", pokeLimitStr));
        }

        String raidLimitStr = limitSplit[1].trim();
        try {
            raidLimit = raidLimitStr.equals("n") ? null : Integer.parseInt(raidLimitStr);
        } catch (NumberFormatException e) {
            novabotLog.log(SimpleLog.Level.FATAL, String.format("Error converting raid limit: \"%s\" to a number", raidLimitStr));
        }

        String presetLimitStr = limitSplit[2].substring(0, limitSplit[2].indexOf("]")).trim();
        try {
            presetLimit = presetLimitStr.equals("n") ? null : Integer.parseInt(presetLimitStr);
        } catch (NumberFormatException e) {
            novabotLog.log(SimpleLog.Level.FATAL, String.format("Error converting preset limit: \"%s\" to a number", presetLimitStr));
        }

        return new NotificationLimit(pokeLimit, raidLimit, presetLimit);
    }

}
