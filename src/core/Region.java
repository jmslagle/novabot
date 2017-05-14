package core;

public enum Region
{
    Innernorth,
    Innersouth,
    GungahlinRegion,
    BelconnenRegion,
    Wodenweston,
    TuggeranongRegion,
    QueanbeyanRegion,
    Hundrediv,
    DratiniCandy,
    MareepCandy,
    SnorlaxCandy,
    LarvitarCandy,
    Legacyrare,
    Ultrarare,
    All,
    Event,
    Regional,
    Dexfiller,
    Zeroiv,
    BigFishLittleRat, UnownAlphabet;

    public static Region fromDbString(final String regionStr) {
        switch (regionStr) {
            case "dratini": {
                return Region.DratiniCandy;
            }
            case "larvitar": {
                return Region.LarvitarCandy;
            }
            case "snorlax": {
                return Region.SnorlaxCandy;
            }
            case "mareep": {
                return Region.MareepCandy;
            }
            case "hundrediv": {
                return Region.Hundrediv;
            }
            case "zeroiv": {
                return Region.Zeroiv;
            }
            default: {
                return fromString(regionStr);
            }
        }
    }

    public static Region fromNestString(final String regionStr) {
        switch (regionStr) {
            case "gungahlin": {
                return Region.GungahlinRegion;
            }
            case "belconnen": {
                return Region.BelconnenRegion;
            }
            case "tuggeranong": {
                return Region.TuggeranongRegion;
            }
            case "queanbeyan": {
                return Region.QueanbeyanRegion;
            }
            default: {
                return fromString(regionStr);
            }
        }
    }

    public static Region fromString(final String regionStr) {
        if (regionStr.equals("all")) {
            return Region.All;
        }
        if (regionStr.equals("regional")) {
            return Region.Regional;
        }
        if (regionStr.equals("woden-weston") || regionStr.equals("wodenweston") || regionStr.equals("woden-weston-supporter") || regionStr.equals("woden-weston-region") || regionStr.equals("woden/weston")) {
            return Region.Wodenweston;
        }
        if (regionStr.equals("belconnenregion") || regionStr.equals("belconnen-supporter") || regionStr.equals("belconnen-region")) {
            return Region.BelconnenRegion;
        }
        if (regionStr.equals("innernorth") || regionStr.equals("inner-north") || regionStr.equals("inner-north-supporter") || regionStr.equals("inner-north-region") || regionStr.equals("inner north")) {
            return Region.Innernorth;
        }
        if (regionStr.equals("innersouth") || regionStr.equals("inner-south") || regionStr.equals("inner-south-supporter") || regionStr.equals("inner-south-region") || regionStr.equals("inner south")) {
            return Region.Innersouth;
        }
        if (regionStr.equals("gungahlinregion") || regionStr.equals("gungahlin-region") || regionStr.equals("gungahlin-supporter")) {
            return Region.GungahlinRegion;
        }
        if (regionStr.equals("tuggeranongregion") || regionStr.equals("tuggeranong-region") || regionStr.equals("tuggeranong-supporter")) {
            return Region.TuggeranongRegion;
        }
        if (regionStr.equals("queanbeyanregion") || regionStr.equals("queanbeyan-region") || regionStr.equals("queanbeyan-supporter")) {
            return Region.QueanbeyanRegion;
        }
        if (regionStr.equals("legacy") || regionStr.equals("legacyrare") || regionStr.equals("legacy-rare") || regionStr.equals("legacy-rare-supporters")) {
            return Region.Legacyrare;
        }
        if (regionStr.equals("larvitarcandy") || regionStr.equals("larvitar-candy") || regionStr.equals("larvitar-candy-supporter")) {
            return Region.LarvitarCandy;
        }
        if (regionStr.equals("dratinicandy") || regionStr.equals("dratini-candy") || regionStr.equals("dratini-candy-supporter")) {
            return Region.DratiniCandy;
        }
        if (regionStr.equals("mareepcandy") || regionStr.equals("mareep-candy") || regionStr.equals("mareep-candy-supporter")) {
            return Region.MareepCandy;
        }
        if (regionStr.equals("ultrarare") || regionStr.equals("ultra-rare") || regionStr.equals("ultra-rare-supporter")) {
            return Region.Ultrarare;
        }
        if (regionStr.equals("100iv") || regionStr.equals("100%") || regionStr.equals("100-iv-supporter") || regionStr.equals("100-iv")) {
            return Region.Hundrediv;
        }
        if (regionStr.equals("snorlaxcandy") || regionStr.equals("snorlax-supporter") || regionStr.equals("snorlax-candy")) {
            return Region.SnorlaxCandy;
        }
        if (regionStr.equals("event")) {
            return Region.Event;
        }
        if (regionStr.equals("0iv") || regionStr.equals("0%") || regionStr.equals("0-iv") || regionStr.equals("0-iv-supporter")) {
            return Region.Zeroiv;
        }
        if (regionStr.equals("dexfiller") || regionStr.equals("dex-filler")) {
            return Region.Dexfiller;
        }
        if (regionStr.equals("bigfishlittlerat") || regionStr.equals("big-fish-little-rat") || regionStr.equals("big-fish-little-rat-cardboard-box")) {
            return Region.BigFishLittleRat;
        }
        if (regionStr.equals("unownalphabet") || regionStr.equals("unown-alphabet"))
            return UnownAlphabet;
        return null;
    }

    public String toWords() {
        switch (this) {
            case Innernorth:
                return "inner north";
            case Innersouth:
                return "inner south";
            case GungahlinRegion:
                return "gungahlin region";
            case BelconnenRegion:
                return "belconnen region";
            case Wodenweston:
                return "woden-weston";
            case TuggeranongRegion:
                return "tuggeranong region";
            case QueanbeyanRegion:
                return "queanbeyan region";
            case Hundrediv:
                return "100% iv";
            case DratiniCandy:
                return "dratini candy";
            case MareepCandy:
                return "mareep candy";
            case SnorlaxCandy:
                return "snorlax";
            case LarvitarCandy:
                return "larvitar candy";
            case Legacyrare:
                return "legacy rare";
            case Ultrarare:
                return "ultra rare";
            case Event:
                return "event";
            case All:
                return "all";
            case Zeroiv:
                return "0% iv";
            case Dexfiller:
                return "dex filler";
            case BigFishLittleRat:
                return "big fish little rat";
            case UnownAlphabet:
                return "unown alphabet";
            default:
                return "";
        }
    }
}
