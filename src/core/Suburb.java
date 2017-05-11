package core;

import java.util.*;

public class Suburb
{
    private static final ArrayList<String> suburbs;

    public static boolean isSuburb(final String suburbStr) {
        return Suburb.suburbs.contains(suburbStr);
    }

    static {
        suburbs = new ArrayList<>(Arrays.asList("acton", "ainslie", "braddon", "campbell", "city", "dickson", "downer", "hackett", "lyneham", "o'connor", "reid", "turner", "watson", "barton", "deakin", "forrest", "griffith", "kingston", "narrabundah", "red hill", "yarralumla", "chifley", "curtin", "farrer", "garran", "hughes", "isaacs", "lyons", "mawson", "o'malley", "pearce", "phillip", "torrens", "aranda", "belconnen", "bruce", "charnwood", "cook", "dunlop", "evatt", "florey", "flynn", "fraser", "giralang", "hawker", "higgins", "holt", "kaleen", "latham", "lawson", "macgregor", "macquarie", "mckellar", "melba", "page", "scullin", "spence", "weetangera", "chapman", "duffy", "fisher", "holder", "rivett", "stirling", "waramanga", "weston", "banks", "bonython", "calwell", "chisholm", "conder", "fadden", "gilmore", "gordon", "gowrie", "greenway", "isabella plains", "kambah", "macarthur", "monash", "oxley", "richardson", "theodore", "tuggeranong", "wanniassa", "amaroo", "bonner", "casey", "crace", "forde", "franklin", "gungahlin", "hall", "harrison", "jacka", "mitchell", "moncrieff", "ngunnawal", "nicholls", "palmerston", "wright", "molonglo valley", "stromlo", "parkes", "googong", "jerrabomberra", "queanbeyan east", "queanbeyan west", "queanbeyan", "karabar", "hume", "crestwood", "stromlo", "coombs", "canberra"));
    }
}
