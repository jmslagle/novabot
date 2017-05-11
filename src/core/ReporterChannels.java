package core;

import java.util.*;

class ReporterChannels extends ArrayList<ReporterChannel>
{
    public boolean containsChannel(final String channelName) {
        for (final ReporterChannel reporterChannel : this) {
            if (channelName.startsWith(reporterChannel.discordName)) {
                return true;
            }
        }
        return false;
    }

    public static Region getRegionByName(final String name) {
        System.out.println("Converting region name: " + name + " to core.Region");
        if (name.startsWith("inner-north")) {
            return Region.Innernorth;
        }
        if (name.startsWith("inner-south")) {
            return Region.Innersouth;
        }
        if (name.startsWith("gungahlin")) {
            return Region.GungahlinRegion;
        }
        if (name.startsWith("belconnen")) {
            return Region.BelconnenRegion;
        }
        if (name.startsWith("tuggeranong")) {
            return Region.TuggeranongRegion;
        }
        if (name.startsWith("woden-weston")) {
            return Region.Wodenweston;
        }
        if (name.startsWith("queanbeyan")) {
            return Region.QueanbeyanRegion;
        }
        if (name.startsWith("legacy-rare")) {
            return Region.Legacyrare;
        }
        if (name.startsWith("snorlax")) {
            return Region.SnorlaxCandy;
        }
        if (name.startsWith("mareep")) {
            return Region.MareepCandy;
        }
        if (name.startsWith("larvitar")) {
            return Region.LarvitarCandy;
        }
        if (name.startsWith("dratini")) {
            return Region.DratiniCandy;
        }
        if (name.startsWith("100-iv")) {
            return Region.Hundrediv;
        }
        if (name.startsWith("ultra-rare")) {
            return Region.Ultrarare;
        }
        if (name.startsWith("event")) {
            return Region.Event;
        }
        if (name.startsWith("0-iv")) {
            return Region.Zeroiv;
        }
        if (name.equals("dex-filler")) {
            return Region.Dexfiller;
        }
        return null;
    }
}
