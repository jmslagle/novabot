package core;

public enum Region
{
    All,
    Regional, QueanbeyanRegion, TuggeranongRegion, BelconnenRegion, GungahlinRegion, InnerNorth, InnerSouth, WodenWeston;

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
            case "inner north":{
                return Region.InnerNorth;
            }
            case "inner south":{
                return Region.InnerSouth;
            }
            case "woden/weston":{
                return Region.WodenWeston;
            }
            default: {
                return fromString(regionStr);
            }
        }
    }

    private static Region fromString(final String regionStr) {
        if (regionStr.equals("all")) {
            return Region.All;
        }
        if (regionStr.equals("regional")) {
            return Region.Regional;
        }
        return null;
    }

    public String toWords() {
        switch (this) {
            case All:
                return "all";
            default:
                return "";
        }
    }
}
