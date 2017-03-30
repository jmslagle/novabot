import java.util.ArrayList;

/**
 * Created by Paris on 18/03/2017.
 */
public class ReporterChannels extends ArrayList<ReporterChannel>{


    public boolean containsChannel(String channelName) {

        for (ReporterChannel reporterChannel : this) {
            if(channelName.startsWith(reporterChannel.discordName)) return true;
        }
        return false;
    }

    public static Region getRegionByName(String name) {

        System.out.println("Converting region name: " + name + " to Region");

        if(name.startsWith("inner-north")) return Region.Innernorth;

        if(name.startsWith("inner-south")) return Region.Innersouth;

        if(name.startsWith("gungahlin")) return Region.Gungahlin;

        if(name.startsWith("belconnen")) return Region.Belconnen;

        if(name.startsWith("tuggeranong")) return Region.Tuggeranong;

        if(name.startsWith("woden-weston")) return Region.Wodenweston;

        if(name.startsWith("queanbeyan")) return Region.Queanbeyan;

        if(name.startsWith("legacy-rare")) return Region.Legacyrare;

        if(name.startsWith("snorlax")) return Region.Snorlax;

        if(name.startsWith("mareep")) return Region.Mareep;

        if(name.startsWith("larvitar")) return Region.Larvitar;

        if(name.startsWith("dratini")) return Region.Dratini;

        if(name.startsWith("100-iv")) return Region.Hundrediv;

        if(name.startsWith("event")) return Region.Event;

        return null;
    }
}
