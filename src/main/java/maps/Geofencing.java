package maps;

import core.NovaBot;
import core.Util;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


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

    static final HashMap<GeofenceIdentifier,Polygon> geofencesMap = new HashMap<>();

    final static GeometryFactory gf = new GeometryFactory();

    private static final Logger geofenceLog = LoggerFactory.getLogger("Geofencing");
    private final NovaBot novaBot;
    public boolean loaded = false;

    public Geofencing(NovaBot novaBot) {
        this.novaBot = novaBot;
    }

    public static String getListMessage() {
        StringBuilder stringBuilder = new StringBuilder();

        for (GeofenceIdentifier identifier : geofencesMap.keySet()) {
            String str = String.format("  %s", identifier.name);

            if (identifier.hasAliases()) str += String.format("aliases: %s", identifier.getAliasList());
            stringBuilder.append(String.format("%s%n", str));
        }

        return stringBuilder.toString();
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

    public static ArrayList<GeofenceIdentifier> getGeofence(double lat, double lon) {
        Point point = gf.createPoint(new Coordinate(lat, lon));

        ArrayList<GeofenceIdentifier> geofenceIdentifiers = new ArrayList<>();

        geofencesMap.forEach((identifier, polygon) -> {
            if (polygon.contains(point)) {
                geofenceIdentifiers.add(identifier);
            }
        });

        return geofenceIdentifiers;
    }

    public void loadGeofences() {
        novaBot.novabotLog.info("Loading geofences from geofences.txt...");
        File file = new File("geofences.txt");

        try (Scanner in = new Scanner(file)) {

            String name = null;
            ArrayList<String> aliases = new ArrayList<>();
            ArrayList<Point> points = new ArrayList<>();

            while(in.hasNext()){
                String line = in.nextLine().toLowerCase();

                if(line == null || line.length() == 0){
                    return;
                }

                if(line.charAt(0) == '['){
                    if(name != null && !points.isEmpty()) {
                        geofencesMap.put(new GeofenceIdentifier(name,aliases),
                                gf.createPolygon(new LinearRing(new CoordinateArraySequence(points.toArray(new Coordinate[points.size()])),gf), null));
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

                    Point p = gf.createPoint(new Coordinate(lat,lon));
                    points.add(p);
                }
            }
            Polygon pgon = gf.createPolygon(new LinearRing(new CoordinateArraySequence(points.toArray(new Coordinate[points.size()])),gf), null);
            geofencesMap.put(new GeofenceIdentifier(name,aliases), pgon);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        loaded = true;
        geofenceLog.info(geofencesMap.toString());
        novaBot.novabotLog.info("Finished loading geofences");
    }

    public static boolean notEmpty() {
        return geofencesMap.size() > 0;
    }
}
