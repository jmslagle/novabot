package nests;

public enum NestStatus
{
    NotYetIdentified,
    Suspected,
    Confirmed;

    public static NestStatus fromString(final String s) {
        switch (s) {
            case "suspected": {
                return NestStatus.Suspected;
            }
            case "confirmed": {
                return NestStatus.Confirmed;
            }
            case "not yet identified": {
                return NestStatus.NotYetIdentified;
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case Suspected: {
                return "suspected";
            }
            case Confirmed: {
                return "confirmed";
            }
            case NotYetIdentified: {
                return "not yet identified";
            }
            default: {
                return "";
            }
        }
    }

    public static String listToString(final NestStatus[] statuses) {
        String str = "";
        if (statuses.length == 1) {
            return statuses[0].toString();
        }
        for (int i = 0; i < statuses.length; ++i) {
            if (i == statuses.length - 1) {
                str = str + "or " + statuses[i].toString();
            }
            else {
                str += ((i == statuses.length - 2) ? (statuses[i].toString() + " ") : (statuses[i].toString() + ", "));
            }
        }
        return str;
    }
}
