package nests;

public enum NestType
{
    FrequentSpawnPoint,
    HotSpot,
    ClusterSpawn,
    FrequentSpawnArea;

    public static NestType fromString(final String s) {
        switch (s) {
            case "Frequent Spawn Point":
                return NestType.FrequentSpawnPoint;
            case "Cluster Spawn":
                return NestType.ClusterSpawn;
            case "Hot Spot":
                return NestType.HotSpot;
            case "Frequent Spawn Area":
                return NestType.FrequentSpawnArea;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
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
