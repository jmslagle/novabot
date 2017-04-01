/**
 * Created by Paris on 18/03/2017.
 */
public enum Region {
    rares,
    ninetyfiveiv,
    hundrediv,
    simple_rares,
    mareepandevolutions,
    larvitarandevolutions,
    dratiniandevolutions,
    unown,
    All;

    public static Region fromString(String regionStr) {
        if (regionStr.equals("all"))
            return All;

        if (regionStr.equals("woden-weston") || regionStr.equals("wodenweston") || regionStr.equals("woden-weston-supporter")
                || regionStr.equals("woden-weston-region") || regionStr.equals("woden/weston"))
            return mareepandevolutions;

        if (regionStr.equals("belconnen") || regionStr.equals("belconnen-supporter") || regionStr.equals("belconnen-region"))
            return simple_rares;

        if (regionStr.equals("innernorth") || regionStr.equals("inner-north") || regionStr.equals("inner-north-supporter")
                || regionStr.equals("inner-north-region") || regionStr.equals("inner north"))
            return rares;

        if (regionStr.equals("innersouth") || regionStr.equals("inner-south") || regionStr.equals("inner-south-supporter")
                || regionStr.equals("inner-south-region") || regionStr.equals("inner south"))
            return ninetyfiveiv;

        if (regionStr.equals("gungahlin") || regionStr.equals("gungahlin-region") || regionStr.equals("gungahlin-supporter"))
            return hundrediv;

        if (regionStr.equals("tuggeranong") || regionStr.equals("tuggeranong-region") || regionStr.equals("tuggeranong-supporter"))
            return larvitarandevolutions;

        if (regionStr.equals("queanbeyan") || regionStr.equals("queanbeyan-region") || regionStr.equals("queanbeyan-supporter"))
            return dratiniandevolutions;

        if (regionStr.equals("100iv") || regionStr.equals("100%") || regionStr.equals("100-iv-supporter") || regionStr.equals("100-iv"))
            return unown;

        return null;
    }

    public String toWords() {
        switch (this) {
            case rares:
                return "inner north";
            case ninetyfiveiv:
                return "inner south";
            case hundrediv:
                return "gungahlin";
            case simple_rares:
                return "belconnen";
            case mareepandevolutions:
                return "woden-weston";
            case larvitarandevolutions:
                return "tuggeranong";
            case dratiniandevolutions:
                return "queanbeyan";
            case unown:
                return "Unown";
            case All:
                return "all";
        }
        return "";
    }
}
