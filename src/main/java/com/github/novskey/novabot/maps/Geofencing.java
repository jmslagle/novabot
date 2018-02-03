package com.github.novskey.novabot.maps;

import com.github.novskey.novabot.Util.UtilityFunctions;
import com.github.novskey.novabot.core.NovaBot;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.TreeMap;


public class Geofencing
{

    static final TreeMap<GeofenceIdentifier,Polygon> geofencesMap = new TreeMap<>(Comparator.comparing(o -> o.name));

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

            if (identifier.hasAliases()) str += String.format(", aliases: %s", identifier.getAliasList());
            stringBuilder.append(String.format("`%s`%n", str));
        }

        return stringBuilder.toString();
    }

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

    public void loadGeofences(String geofences) {
        novaBot.novabotLog.info(String.format("Loading geofences from %s...", geofences));
        Path file = Paths.get(geofences);

        try (Scanner in = new Scanner(file)) {

            String name = null;
            ArrayList<String> aliases = new ArrayList<>();
            ArrayList<Coordinate> points = new ArrayList<>();

            Coordinate first, last;

            while(in.hasNext()){
                String line = in.nextLine().toLowerCase();

                if(line == null || line.length() == 0){
                    return;
                }

                if(line.charAt(0) == '['){
                    if(name != null && !points.isEmpty()) {
                        first = points.get(0);
                        last = points.get(points.size()-1);
                        if(first != last){
                            points.add(first);
                        }

                        geofencesMap.put(new GeofenceIdentifier(name,aliases),
                                gf.createPolygon(new CoordinateArraySequence(points.toArray(new Coordinate[points.size()]))));
                    }

                    ArrayList<String> names = UtilityFunctions.parseList(line);
                    name = names.get(0);
                    names.remove(0);

                    aliases = names;

                    points = new ArrayList<>();

                }else{
                    String[] split = line.split(",");

                    double lat = Double.parseDouble(split[0].trim());
                    double lon = Double.parseDouble(split[1].trim());

                    points.add(new Coordinate(lat,lon));
                }
            }
            if (points.size() > 0) {
                first = points.get(0);
                last = points.get(points.size() - 1);
                if (first != last) {
                    points.add(first);
                }
                geofencesMap.put(new GeofenceIdentifier(name, aliases),
                        gf.createPolygon(new CoordinateArraySequence(points.toArray(new Coordinate[points.size()]))));
            }
        } catch (NoSuchFileException e) {
            geofenceLog.info("Couldn't find geofence file " + geofences);
        } catch (IOException e) {
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
