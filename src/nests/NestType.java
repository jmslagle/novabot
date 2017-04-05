package nests;

/**
 * Created by Paris on 28/03/2017.
 */
public enum NestType {
    FrequentSpawnPoint,
    HotSpot,
    ClusterSpawn,
    FrequentSpawnArea;

    public static NestType fromString(String s) {

        switch(s){
            case "Frequent Spawn Point":
                return FrequentSpawnPoint;
            case "Cluster Spawn":
                return ClusterSpawn;
            case "Hot Spot":
                return HotSpot;
            case "Frequent Spawn Area":
                return FrequentSpawnArea;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch(this){
            case ClusterSpawn:
                return "Cluster spawn";
            case FrequentSpawnArea:
                return "Frequent spawn area";
            case FrequentSpawnPoint:
                return "Frequent spawn point";
            case HotSpot:
                return "Hot spot";
            default:
                return null;
        }
    }
}
