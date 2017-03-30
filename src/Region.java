/**
 * Created by Paris on 18/03/2017.
 */
public enum Region {
    Innernorth,
    Innersouth,
    Gungahlin,
    Belconnen,
    Wodenweston,
    Tuggeranong,
    Queanbeyan,
    Hundrediv,
    Dratini,
    Mareep,
    Snorlax,
    Larvitar,
    Legacyrare,
    Ultrarare,
    All,
    Event,
    Regional;

    public static Region fromString(String regionStr) {
        if (regionStr.equals("all"))
            return All;

        if(regionStr.equals("regional"))
            return Regional;

        if (regionStr.equals("woden-weston") || regionStr.equals("wodenweston") || regionStr.equals("woden-weston-supporter")
                || regionStr.equals("woden-weston-region") || regionStr.equals("woden/weston"))
            return Wodenweston;

        if (regionStr.equals("belconnen") || regionStr.equals("belconnen-supporter") || regionStr.equals("belconnen-region"))
            return Belconnen;

        if (regionStr.equals("innernorth") || regionStr.equals("inner-north") || regionStr.equals("inner-north-supporter")
                || regionStr.equals("inner-north-region") || regionStr.equals("inner north"))
            return Innernorth;

        if (regionStr.equals("innersouth") || regionStr.equals("inner-south") || regionStr.equals("inner-south-supporter")
                || regionStr.equals("inner-south-region") || regionStr.equals("inner south"))
            return Innersouth;

        if (regionStr.equals("gungahlin") || regionStr.equals("gungahlin-region") || regionStr.equals("gungahlin-supporter"))
            return Gungahlin;

        if (regionStr.equals("tuggeranong") || regionStr.equals("tuggeranong-region") || regionStr.equals("tuggeranong-supporter"))
            return Tuggeranong;

        if (regionStr.equals("queanbeyan") || regionStr.equals("queanbeyan-region") || regionStr.equals("queanbeyan-supporter"))
            return Queanbeyan;

        if (regionStr.equals("legacy") || regionStr.equals("legacyrare") || regionStr.equals("legacy-rare")
                || regionStr.equals("legacy-rare-supporters"))
            return Legacyrare;

        if (regionStr.equals("larvitar") || regionStr.equals("larvitarcandy") || regionStr.equals("larvitar-candy")
                || regionStr.equals("larvitar-candy-supporter"))
            return Larvitar;

        if (regionStr.equals("dratini") || regionStr.equals("dratinicandy") || regionStr.equals("dratini-candy")
                || regionStr.equals("dratini-candy-supporter"))
            return Dratini;

        if (regionStr.equals("mareep") || regionStr.equals("mareepcandy") || regionStr.equals("mareep-candy")
                || regionStr.equals("mareep-candy-supporter"))
            return Mareep;

        if (regionStr.equals("ultrarare") || regionStr.equals("ultra-rare") || regionStr.equals("ultra-rare-supporter"))
            return Ultrarare;

        if (regionStr.equals("100iv") || regionStr.equals("100%") || regionStr.equals("100-iv-supporter") || regionStr.equals("100-iv"))
            return Hundrediv;

        if (regionStr.equals("snorlax") || regionStr.equals("snorlax-supporter"))
            return Snorlax;

        if (regionStr.equals("event"))
            return Event;

        return null;
    }

    public String toWords() {
        switch (this) {
            case Innernorth:
                return "inner north";
            case Innersouth:
                return "inner south";
            case Gungahlin:
                return "gungahlin";
            case Belconnen:
                return "belconnen";
            case Wodenweston:
                return "woden-weston";
            case Tuggeranong:
                return "tuggeranong";
            case Queanbeyan:
                return "queanbeyan";
            case Hundrediv:
                return "100% iv";
            case Dratini:
                return "dratini candy";
            case Mareep:
                return "mareep candy";
            case Snorlax:
                return "snorlax";
            case Larvitar:
                return "larvitar candy";
            case Legacyrare:
                return "legacy rare";
            case Ultrarare:
                return "ultra rare";
            case Event:
                return "event";
            case All:
                return "all";
        }
        return "";
    }
}
