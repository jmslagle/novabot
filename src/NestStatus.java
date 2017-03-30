/**
 * Created by Paris on 28/03/2017.
 */
public enum NestStatus {
    NotYetIdentified,
    Suspected,
    Confirmed;

    public static NestStatus fromString(String s) {

        switch(s){
            case "suspected":
                return Suspected;
            case "confirmed":
                return Confirmed;
            case "not yet identified":
                return NotYetIdentified;
            default:
                return null;
        }

    }

    @Override
    public String toString() {
        switch (this){
            case Suspected:
                return "suspected";
            case Confirmed:
                return "confirmed";
            case NotYetIdentified:
                return "not yet identified";
            default:
                return "";
        }
    }

    public static String listToString(NestStatus[] statuses) {
        String str = "";

        if(statuses.length == 1){
            return statuses[0].toString();
        }

        for (int i = 0; i < statuses.length; i++) {
            if(i == statuses.length-1)
                str += "or " + statuses[i].toString();
            else
                str += i == statuses.length - 2 ? statuses[i].toString() + " " : statuses[i].toString() +", ";
        }

        return str;
    }
}
