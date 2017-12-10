package maps;

import core.Location;
import core.Util;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.opengts.util.GeoPoint;
import org.opengts.util.GeoPolygon;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static net.dv8tion.jda.core.utils.SimpleLog.Level.INFO;

public class Geofencing
{
//    private static final GeoPolygon innernorth;
//    private static final GeoPolygon innersouth;
//    private static final GeoPolygon gungahlin;
//    private static final GeoPolygon belconnen;
//    private static final GeoPolygon woden;
//    private static final GeoPolygon weston;
//    private static final GeoPolygon queanbeyan;
//    private static final GeoPolygon tuggeranong;
//    private static final HashMap<GeoPolygon, Region> geofences;

    static final HashMap<GeofenceIdentifier,GeoPolygon> geofencesMap = new HashMap<>();

    private static final SimpleLog geofenceLog = SimpleLog.getLog("Geofencing");
    public static boolean loaded = false;

    public static void main(final String[] args) {
        loadGeofences();

        System.out.println(new Location(GeofenceIdentifier.fromString("woden-weston")).toDbString());

//        geofencesMap.keySet().forEach(System.out::println);

//        System.out.println(getGeofence(-35.214385, 149.0405493));

//        System.out.println(getFeedChannel(-35.214385, 149.0405493));
    }

    public static void loadGeofences(){
        File file = new File("geofences.txt");

        try (Scanner in = new Scanner(file)) {

            String name = null;
            ArrayList<String> aliases = new ArrayList<>();
            ArrayList<GeoPoint> points = new ArrayList<>();

            while(in.hasNext()){
                String line = in.nextLine().toLowerCase();

                if(line == null || line.length() == 0){
                    return;
                }

                if(line.charAt(0) == '['){
                    if(name != null && !points.isEmpty()) {
                        geofencesMap.put(new GeofenceIdentifier(name,aliases),new GeoPolygon(points));
                    }

                    ArrayList<String> names = Util.parseList(line);
                    name = names.get(0);
                    names.remove(0);

                    aliases = names;

                    points = new ArrayList<>();

                }else{
                    String[] split = line.split(",");

                    double lat = Double.parseDouble(split[0].trim());
                    double lon = Double.parseDouble(split[1].trim());

                    points.add(new GeoPoint(lat,lon));
                }
            }
            geofencesMap.put(new GeofenceIdentifier(name,aliases),new GeoPolygon(points));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        loaded = true;
        geofenceLog.log(INFO,geofencesMap);
    }

//    public static Region getRegion(final double lat, final double lon) {
//        final GeoPoint point = new GeoPoint(lat, lon);
//        for (final GeoPolygon geoPolygon : Geofencing.geofences.keySet()) {
//            if (geoPolygon.containsPoint(point)) {
//                return Geofencing.geofences.get(geoPolygon);
//            }
//        }
//        return null;
//    }

    public static ArrayList<GeofenceIdentifier> getGeofence(double lat, double lon){
        GeoPoint point = new GeoPoint(lat,lon);

        ArrayList<GeofenceIdentifier> geofenceIdentifiers = new ArrayList<>();

        geofencesMap.forEach((identifier, geoPolygon) -> {
            if(geoPolygon.containsPoint(point)){
                geofenceIdentifiers.add(identifier);
            }
        });

        return geofenceIdentifiers;
    }

    static {
//        innernorth = new GeoPolygon("Innernorth", new GeoPoint(-35.2318089, 149.1235256), new GeoPoint(-35.2321594, 149.1186333), new GeoPoint(-35.2362256, 149.1166592), new GeoPoint(-35.2406421, 149.112196), new GeoPoint(-35.2495444, 149.1098785), new GeoPoint(-35.2520677, 149.106617), new GeoPoint(-35.2528387, 149.0975189), new GeoPoint(-35.2583056, 149.0897942), new GeoPoint(-35.2651036, 149.0865326), new GeoPoint(-35.2715507, 149.0891075), new GeoPoint(-35.2781025, 149.0870261), new GeoPoint(-35.2809051, 149.0862321), new GeoPoint(-35.2858096, 149.0882063), new GeoPoint(-35.2878256, 149.0892145), new GeoPoint(-35.2868255, 149.0907383), new GeoPoint(-35.2857746, 149.0985489), new GeoPoint(-35.2915194, 149.0976906), new GeoPoint(-35.2942165, 149.0976906), new GeoPoint(-35.2982534, 149.0978086), new GeoPoint(-35.2988751, 149.1019821), new GeoPoint(-35.2961431, 149.1071749), new GeoPoint(-35.2976842, 149.114213), new GeoPoint(-35.2969136, 149.1223669), new GeoPoint(-35.2943217, 149.1246843), new GeoPoint(-35.2946369, 149.1310787), new GeoPoint(-35.2986299, 149.1382027), new GeoPoint(-35.3037785, 149.1409492), new GeoPoint(-35.3083314, 149.1418505), new GeoPoint(-35.3096271, 149.1492748), new GeoPoint(-35.3050743, 149.1524076), new GeoPoint(-35.3034633, 149.1548109), new GeoPoint(-35.301537, 149.159317), new GeoPoint(-35.3047591, 149.1622782), new GeoPoint(-35.3049693, 149.1643381), new GeoPoint(-35.3063701, 149.1666985), new GeoPoint(-35.304514, 149.1695309), new GeoPoint(-35.3028679, 149.1706038), new GeoPoint(-35.3020186, 149.1735757), new GeoPoint(-35.3040237, 149.1761827), new GeoPoint(-35.3091719, 149.1745949), new GeoPoint(-35.312954, 149.1784143), new GeoPoint(-35.3140746, 149.1780281), new GeoPoint(-35.3152653, 149.1759682), new GeoPoint(-35.3191872, 149.1778564), new GeoPoint(-35.3196774, 149.1847229), new GeoPoint(-35.3217083, 149.1927052), new GeoPoint(-35.3217171, 149.1959024), new GeoPoint(-35.3204653, 149.201417), new GeoPoint(-35.3178566, 149.2063952), new GeoPoint(-35.3088917, 149.2251706), new GeoPoint(-35.2968697, 149.2125203), new GeoPoint(-35.2934283, 149.2070591), new GeoPoint(-35.2855644, 149.1962242), new GeoPoint(-35.282166, 149.1954947), new GeoPoint(-35.2770334, 149.1964824), new GeoPoint(-35.2658393, 149.198456), new GeoPoint(-35.254661, 149.1908598), new GeoPoint(-35.239871, 149.1971255), new GeoPoint(-35.2363658, 149.1989708), new GeoPoint(-35.231423, 149.1981769), new GeoPoint(-35.2261996, 149.1972113), new GeoPoint(-35.2177158, 149.1955376), new GeoPoint(-35.2137188, 149.1869545), new GeoPoint(-35.2196792, 149.1777706), new GeoPoint(-35.2240966, 149.164381), new GeoPoint(-35.2222033, 149.1451549), new GeoPoint(-35.225569, 149.14361), new GeoPoint(-35.2283033, 149.144125), new GeoPoint(-35.2287941, 149.1394043), new GeoPoint(-35.2357349, 149.1306496), new GeoPoint(-35.2318089, 149.1235256));
//        innersouth = new GeoPolygon("Innersouth", new GeoPoint(-35.2863175, 149.0949226), new GeoPoint(-35.2868255, 149.0907383), new GeoPoint(-35.2878256, 149.0892145), new GeoPoint(-35.2906345, 149.0868063), new GeoPoint(-35.2929118, 149.0856207), new GeoPoint(-35.294847, 149.0849662), new GeoPoint(-35.2968962, 149.0835071), new GeoPoint(-35.2974749, 149.0827), new GeoPoint(-35.2976492, 149.0811682), new GeoPoint(-35.2974805, 149.0790753), new GeoPoint(-35.2954737, 149.0780213), new GeoPoint(-35.2938171, 149.0779973), new GeoPoint(-35.2965866, 149.075497), new GeoPoint(-35.2948821, 149.0712976), new GeoPoint(-35.3025877, 149.064045), new GeoPoint(-35.3138295, 149.0626287), new GeoPoint(-35.3070005, 149.0746236), new GeoPoint(-35.3127965, 149.082563), new GeoPoint(-35.3141097, 149.0972829), new GeoPoint(-35.314775, 149.093914), new GeoPoint(-35.316631, 149.0910816), new GeoPoint(-35.3196074, 149.0894508), new GeoPoint(-35.3266804, 149.0873909), new GeoPoint(-35.3269605, 149.1090631), new GeoPoint(-35.3317922, 149.1147709), new GeoPoint(-35.3462417, 149.1167879), new GeoPoint(-35.3487358, 149.1223454), new GeoPoint(-35.3493133, 149.1215515), new GeoPoint(-35.3499784, 149.1215945), new GeoPoint(-35.3496458, 149.1178608), new GeoPoint(-35.3581163, 149.1210577), new GeoPoint(-35.3827523, 149.1269159), new GeoPoint(-35.3663055, 149.1747885), new GeoPoint(-35.3421373, 149.1642737), new GeoPoint(-35.3399493, 149.1760111), new GeoPoint(-35.3423998, 149.184165), new GeoPoint(-35.3300416, 149.1930485), new GeoPoint(-35.3192572, 149.1868687), new GeoPoint(-35.3176465, 149.1778135), new GeoPoint(-35.312954, 149.1784143), new GeoPoint(-35.3091719, 149.1745949), new GeoPoint(-35.3040237, 149.1761827), new GeoPoint(-35.3020186, 149.1735757), new GeoPoint(-35.3026227, 149.1708183), new GeoPoint(-35.3048992, 149.1691446), new GeoPoint(-35.3063701, 149.1666985), new GeoPoint(-35.3049693, 149.1643381), new GeoPoint(-35.3047591, 149.1622782), new GeoPoint(-35.301537, 149.159317), new GeoPoint(-35.3024126, 149.1564846), new GeoPoint(-35.3037785, 149.1540813), new GeoPoint(-35.3044789, 149.1528797), new GeoPoint(-35.3096271, 149.1492748), new GeoPoint(-35.3083314, 149.1418505), new GeoPoint(-35.304514, 149.1412926), new GeoPoint(-35.2989802, 149.1387606), new GeoPoint(-35.2944968, 149.1313362), new GeoPoint(-35.2940414, 149.1245127), new GeoPoint(-35.2964583, 149.1218519), new GeoPoint(-35.2972989, 149.11829), new GeoPoint(-35.2956176, 149.1069603), new GeoPoint(-35.2981746, 149.0981197), new GeoPoint(-35.2881391, 149.098227), new GeoPoint(-35.2857746, 149.0985489), new GeoPoint(-35.2863175, 149.0949226));
//        gungahlin = new GeoPolygon("GungahlinRegion", new GeoPoint(-35.240572, 149.1364431), new GeoPoint(-35.2272517, 149.1563988), new GeoPoint(-35.2205579, 149.1779481), new GeoPoint(-35.2202341, 149.2014234), new GeoPoint(-35.2077647, 149.2032039), new GeoPoint(-35.2053933, 149.2011647), new GeoPoint(-35.2016009, 149.1968122), new GeoPoint(-35.2016685, 149.1906879), new GeoPoint(-35.1983695, 149.1924601), new GeoPoint(-35.1935274, 149.1926875), new GeoPoint(-35.1917715, 149.1980646), new GeoPoint(-35.1851734, 149.1992055), new GeoPoint(-35.1750679, 149.1910768), new GeoPoint(-35.1632785, 149.1898159), new GeoPoint(-35.1569636, 149.1796299), new GeoPoint(-35.1577364, 149.1699032), new GeoPoint(-35.1400165, 149.1658991), new GeoPoint(-35.1424575, 149.1519649), new GeoPoint(-35.1381328, 149.1514351), new GeoPoint(-35.1327769, 149.1462356), new GeoPoint(-35.1336676, 149.1418934), new GeoPoint(-35.126671, 149.1391033), new GeoPoint(-35.125009, 149.13228), new GeoPoint(-35.1233681, 149.1179482), new GeoPoint(-35.1390521, 149.0904826), new GeoPoint(-35.1366157, 149.0467071), new GeoPoint(-35.1467225, 149.0201855), new GeoPoint(-35.1791402, 149.0601825), new GeoPoint(-35.188479, 149.0717255), new GeoPoint(-35.1993421, 149.0829277), new GeoPoint(-35.2009311, 149.0927016), new GeoPoint(-35.2054613, 149.1001153), new GeoPoint(-35.2101073, 149.1085052), new GeoPoint(-35.213789, 149.1129684), new GeoPoint(-35.2195039, 149.1184831), new GeoPoint(-35.2318089, 149.1235256), new GeoPoint(-35.240572, 149.1364431));
//        belconnen = new GeoPolygon("BelconnenRegion", new GeoPoint(-35.213228, 148.9708328), new GeoPoint(-35.2330708, 148.9880848), new GeoPoint(-35.2339124, 148.9937926), new GeoPoint(-35.2335619, 148.9991571), new GeoPoint(-35.2306871, 149.0074825), new GeoPoint(-35.2339822, 149.018383), new GeoPoint(-35.2435862, 149.0250349), new GeoPoint(-35.2544508, 149.024992), new GeoPoint(-35.2589012, 149.0267084), new GeoPoint(-35.2663651, 149.031601), new GeoPoint(-35.2710602, 149.0362358), new GeoPoint(-35.2701492, 149.0535736), new GeoPoint(-35.2773667, 149.0720272), new GeoPoint(-35.2809402, 149.0863609), new GeoPoint(-35.2715507, 149.0891075), new GeoPoint(-35.2651036, 149.0865326), new GeoPoint(-35.2583056, 149.0897942), new GeoPoint(-35.2528387, 149.0975189), new GeoPoint(-35.2520677, 149.106617), new GeoPoint(-35.2495444, 149.1098785), new GeoPoint(-35.2406421, 149.112196), new GeoPoint(-35.2362256, 149.1166592), new GeoPoint(-35.2321594, 149.1186333), new GeoPoint(-35.2318089, 149.1235256), new GeoPoint(-35.223746, 149.1232681), new GeoPoint(-35.2161205, 149.1157794), new GeoPoint(-35.2101073, 149.1085052), new GeoPoint(-35.2066535, 149.1023899), new GeoPoint(-35.2009311, 149.0927016), new GeoPoint(-35.1993421, 149.0829277), new GeoPoint(-35.1882247, 149.0718555), new GeoPoint(-35.1544422, 149.0296269), new GeoPoint(-35.1492491, 149.017868), new GeoPoint(-35.1651743, 149.007573), new GeoPoint(-35.1858707, 149.0178773), new GeoPoint(-35.213228, 148.9708328));
//        woden = new GeoPolygon("Woden", new GeoPoint(-35.3586062, 149.0689373), new GeoPoint(-35.3637947, 149.0661369), new GeoPoint(-35.3644946, 149.065386), new GeoPoint(-35.3659907, 149.0636158), new GeoPoint(-35.3676792, 149.0616952), new GeoPoint(-35.3732259, 149.0689695), new GeoPoint(-35.3752642, 149.0706538), new GeoPoint(-35.3770309, 149.0725958), new GeoPoint(-35.3776961, 149.0746342), new GeoPoint(-35.378798, 149.0780997), new GeoPoint(-35.3790433, 149.0807819), new GeoPoint(-35.3788333, 149.0932274), new GeoPoint(-35.3848598, 149.1019391), new GeoPoint(-35.387099, 149.1069174), new GeoPoint(-35.3853497, 149.113462), new GeoPoint(-35.3850614, 149.1163158), new GeoPoint(-35.385223, 149.1186548), new GeoPoint(-35.3827523, 149.1269159), new GeoPoint(-35.363296, 149.1231823), new GeoPoint(-35.3470556, 149.1170025), new GeoPoint(-35.3432398, 149.1168522), new GeoPoint(-35.3395639, 149.1160153), new GeoPoint(-35.3317922, 149.1147709), new GeoPoint(-35.3269605, 149.1090631), new GeoPoint(-35.3259101, 149.0885067), new GeoPoint(-35.3165259, 149.0921974), new GeoPoint(-35.3139346, 149.0980339), new GeoPoint(-35.3118334, 149.0861893), new GeoPoint(-35.3070005, 149.0746236), new GeoPoint(-35.3138295, 149.0626287), new GeoPoint(-35.3182768, 149.0636373), new GeoPoint(-35.3203778, 149.0631866), new GeoPoint(-35.3240894, 149.0649033), new GeoPoint(-35.3318622, 149.0629292), new GeoPoint(-35.338444, 149.0663624), new GeoPoint(-35.3478256, 149.0710831), new GeoPoint(-35.3532861, 149.0705681), new GeoPoint(-35.3586062, 149.0689373));
//        weston = new GeoPolygon("Weston", new GeoPoint(-35.3138294, 149.0626286), new GeoPoint(-35.3066603, 149.063558), new GeoPoint(-35.3019077, 149.06659), new GeoPoint(-35.2953754, 149.0724165), new GeoPoint(-35.2965866, 149.075497), new GeoPoint(-35.2939405, 149.0779552), new GeoPoint(-35.295367, 149.0780041), new GeoPoint(-35.2967585, 149.0786754), new GeoPoint(-35.2974805, 149.0790753), new GeoPoint(-35.2977472, 149.0802263), new GeoPoint(-35.2974749, 149.0827), new GeoPoint(-35.2959479, 149.0842262), new GeoPoint(-35.2941758, 149.0851945), new GeoPoint(-35.2923526, 149.0859146), new GeoPoint(-35.2906345, 149.0868063), new GeoPoint(-35.2894227, 149.0879031), new GeoPoint(-35.2878256, 149.0892145), new GeoPoint(-35.283864, 149.0878223), new GeoPoint(-35.2809402, 149.0863609), new GeoPoint(-35.279819, 149.0846872), new GeoPoint(-35.2762065, 149.0714483), new GeoPoint(-35.2688177, 149.0509986), new GeoPoint(-35.2755078, 149.0439811), new GeoPoint(-35.2835325, 149.039154), new GeoPoint(-35.2953193, 149.0334682), new GeoPoint(-35.3046539, 149.0310429), new GeoPoint(-35.3113779, 149.0241764), new GeoPoint(-35.3136355, 149.017368), new GeoPoint(-35.3131965, 149.0115035), new GeoPoint(-35.3150771, 149.0086678), new GeoPoint(-35.3197511, 149.0047795), new GeoPoint(-35.3228965, 149.0005757), new GeoPoint(-35.3265029, 149.0004788), new GeoPoint(-35.329118, 149.001683), new GeoPoint(-35.3293254, 149.0025742), new GeoPoint(-35.3291126, 149.0034225), new GeoPoint(-35.3272367, 149.0046785), new GeoPoint(-35.3276717, 149.0072433), new GeoPoint(-35.326038, 149.0101201), new GeoPoint(-35.3260265, 149.0114834), new GeoPoint(-35.326686, 149.0129804), new GeoPoint(-35.328816, 149.0157221), new GeoPoint(-35.3262424, 149.0251417), new GeoPoint(-35.343975, 149.0211295), new GeoPoint(-35.3513959, 149.0193271), new GeoPoint(-35.363121, 149.0267933), new GeoPoint(-35.3717382, 149.0361929), new GeoPoint(-35.3707154, 149.0510846), new GeoPoint(-35.3676792, 149.0616952), new GeoPoint(-35.3638647, 149.066212), new GeoPoint(-35.3583654, 149.0691841), new GeoPoint(-35.353286, 149.070568), new GeoPoint(-35.3478255, 149.071083), new GeoPoint(-35.3384439, 149.0663623), new GeoPoint(-35.3300415, 149.064474), new GeoPoint(-35.3240893, 149.0649032), new GeoPoint(-35.3138294, 149.0626286));
//        queanbeyan = new GeoPolygon("QueanbeyanRegion", new GeoPoint(-35.3371137, 149.1878557), new GeoPoint(-35.3438613, 149.1818474), new GeoPoint(-35.3455679, 149.1846366), new GeoPoint(-35.3446052, 149.2004725), new GeoPoint(-35.3462766, 149.2057834), new GeoPoint(-35.351886, 149.2018246), new GeoPoint(-35.3571362, 149.1979408), new GeoPoint(-35.3777747, 149.1412923), new GeoPoint(-35.4076863, 149.1459702), new GeoPoint(-35.4338199, 149.1433525), new GeoPoint(-35.4441738, 149.1472151), new GeoPoint(-35.4511616, 149.1972541), new GeoPoint(-35.4605255, 149.2043773), new GeoPoint(-35.4712956, 149.2190552), new GeoPoint(-35.4704568, 149.245491), new GeoPoint(-35.4634663, 149.2506408), new GeoPoint(-35.4422115, 149.2478943), new GeoPoint(-35.4075206, 149.265747), new GeoPoint(-35.371135, 149.2674637), new GeoPoint(-35.3350742, 149.2695206), new GeoPoint(-35.3113431, 149.2465211), new GeoPoint(-35.3088917, 149.2251706), new GeoPoint(-35.3202377, 149.2010737), new GeoPoint(-35.3279844, 149.1935642), new GeoPoint(-35.3371137, 149.1878557));
//        tuggeranong = new GeoPolygon("TuggeranongRegion", new GeoPoint(-35.3704435, 149.0496683), new GeoPoint(-35.3717382, 149.0361929), new GeoPoint(-35.377652, 149.0365791), new GeoPoint(-35.3810111, 149.0413856), new GeoPoint(-35.3868191, 149.0320301), new GeoPoint(-35.3912274, 149.0284252), new GeoPoint(-35.396475, 149.031515), new GeoPoint(-35.3983641, 149.0383815), new GeoPoint(-35.4034014, 149.0555048), new GeoPoint(-35.4071791, 149.0542172), new GeoPoint(-35.4127055, 149.0541743), new GeoPoint(-35.4195605, 149.0549468), new GeoPoint(-35.4220086, 149.052329), new GeoPoint(-35.4262926, 149.0520285), new GeoPoint(-35.4277088, 149.0545177), new GeoPoint(-35.4281112, 149.0617701), new GeoPoint(-35.4313545, 149.0642806), new GeoPoint(-35.4342698, 149.0681538), new GeoPoint(-35.4338982, 149.0704822), new GeoPoint(-35.4361186, 149.0757822), new GeoPoint(-35.4349122, 149.0793657), new GeoPoint(-35.4385836, 149.084258), new GeoPoint(-35.4405066, 149.0805244), new GeoPoint(-35.4466251, 149.0778636), new GeoPoint(-35.450519, 149.0777564), new GeoPoint(-35.4489591, 149.071641), new GeoPoint(-35.4535388, 149.0731859), new GeoPoint(-35.456759, 149.0751815), new GeoPoint(-35.4591402, 149.0742588), new GeoPoint(-35.4635096, 149.0749883), new GeoPoint(-35.4748339, 149.0845155), new GeoPoint(-35.4784336, 149.0888929), new GeoPoint(-35.481011, 149.0961776), new GeoPoint(-35.481072, 149.101746), new GeoPoint(-35.4797528, 149.1048467), new GeoPoint(-35.4778744, 149.1058873), new GeoPoint(-35.4736805, 149.1079472), new GeoPoint(-35.469836, 149.1177319), new GeoPoint(-35.4641654, 149.1158868), new GeoPoint(-35.4612467, 149.1231394), new GeoPoint(-35.4633964, 149.1231824), new GeoPoint(-35.4626357, 149.1251134), new GeoPoint(-35.4588522, 149.1250705), new GeoPoint(-35.4570601, 149.1297484), new GeoPoint(-35.4517292, 149.1311644), new GeoPoint(-35.4477787, 149.1324305), new GeoPoint(-35.4442477, 149.1314649), new GeoPoint(-35.4394401, 149.1311), new GeoPoint(-35.4344926, 149.1290187), new GeoPoint(-35.4321498, 149.132452), new GeoPoint(-35.4278662, 149.1385463), new GeoPoint(-35.4255844, 149.1416791), new GeoPoint(-35.422743, 149.14361), new GeoPoint(-35.4187386, 149.1451549), new GeoPoint(-35.4147341, 149.1453265), new GeoPoint(-35.4076863, 149.1459702), new GeoPoint(-35.4016873, 149.1457557), new GeoPoint(-35.3868892, 149.1438246), new GeoPoint(-35.3828743, 149.1429876), new GeoPoint(-35.3777747, 149.1412923), new GeoPoint(-35.379708, 149.1345335), new GeoPoint(-35.3827523, 149.1269159), new GeoPoint(-35.383242, 149.1228819), new GeoPoint(-35.3835852, 149.1211546), new GeoPoint(-35.385223, 149.1186548), new GeoPoint(-35.3850614, 149.1163158), new GeoPoint(-35.3853497, 149.113462), new GeoPoint(-35.387099, 149.1069174), new GeoPoint(-35.3848598, 149.1019391), new GeoPoint(-35.3833553, 149.0998041), new GeoPoint(-35.3800668, 149.0950513), new GeoPoint(-35.3793274, 149.0940642), new GeoPoint(-35.3788333, 149.0932274), new GeoPoint(-35.3788417, 149.0919935), new GeoPoint(-35.3789116, 149.0881955), new GeoPoint(-35.3789816, 149.0821874), new GeoPoint(-35.3783956, 149.0796286), new GeoPoint(-35.378798, 149.0780997), new GeoPoint(-35.3770309, 149.0725958), new GeoPoint(-35.3735666, 149.0694736), new GeoPoint(-35.3712833, 149.0668344), new GeoPoint(-35.3676792, 149.0616952), new GeoPoint(-35.3704435, 149.0496683));
//        (geofences = new HashMap<GeoPolygon, Region>()).put(Geofencing.innernorth, Region.Innernorth);
//        Geofencing.geofences.put(Geofencing.innersouth, Region.Innersouth);
//        Geofencing.geofences.put(Geofencing.gungahlin, Region.GungahlinRegion);
//        Geofencing.geofences.put(Geofencing.belconnen, Region.BelconnenRegion);
//        Geofencing.geofences.put(Geofencing.innersouth, Region.Innersouth);
//        Geofencing.geofences.put(Geofencing.queanbeyan, Region.QueanbeyanRegion);
//        Geofencing.geofences.put(Geofencing.tuggeranong, Region.TuggeranongRegion);
//        Geofencing.geofences.put(Geofencing.weston, Region.Wodenweston);
//        Geofencing.geofences.put(Geofencing.woden, Region.Wodenweston);
    }

    public static String getListMessage() {
        String str = "";

        for (GeofenceIdentifier identifier : geofencesMap.keySet()) {
            str += String.format("  %s, aliases: %s%n",identifier.name,identifier.getAliasList());
        }

        return str;
    }
}
