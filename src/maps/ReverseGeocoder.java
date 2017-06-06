package maps;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import core.DBManager;
import core.MessageListener;

import static core.MessageListener.config;
import static core.MessageListener.loadConfig;

public class ReverseGeocoder {

    private static int lastKey;

    public static void main(final String[] args) {
//        DBManager.novabotdbConnect();
        MessageListener.testing = true;
        loadConfig();

        DBManager.novabotdbConnect();

        DBManager.setGeocodedLocation(-35.405055,149.1270075,geocodedLocation(-35.405055, 149.1270075));
    }

    public static GeocodedLocation geocodedLocation(double lat, double lon){

        GeocodedLocation location = DBManager.getGeocodedLocation(lat,lon);

        if(location != null)  return location;

        location = new GeocodedLocation();
        location.set("street_num","unkn");
        location.set("street","unkn");
        location.set("city","unkn");
        location.set("state","unkn");
        location.set("postal","unkn");
        location.set("neighbourhood","unkn");
        location.set("sublocality","unkn");
        location.set("country","unkn");

        final GeoApiContext context = new GeoApiContext();
        try {
            context.setApiKey(getNextKey());
            final GeocodingResult[] results = (GeocodingApi.reverseGeocode(context, new LatLng(lat, lon))).await();
            for (final AddressComponent addressComponent : results[0].addressComponents) {
                final AddressComponentType[] types = addressComponent.types;

                for (AddressComponentType type : types) {

                    switch(type){
                        case STREET_NUMBER:
                            location.set("street_num",addressComponent.longName);
                            break;
                        case ROUTE:
                            location.set("street",addressComponent.longName);
                            break;
                        case LOCALITY:
                            location.set("city",addressComponent.longName);
                            break;
                        case ADMINISTRATIVE_AREA_LEVEL_1:
                            location.set("state",addressComponent.shortName);
                        case POSTAL_CODE:
                            location.set("postal",addressComponent.longName);
                            break;
                        case NEIGHBORHOOD:
                            location.set("neighbourhood",addressComponent.longName);
                            break;
                        case SUBLOCALITY:
                            location.set("sublocality",addressComponent.longName);
                            break;
                        case COUNTRY:
                            location.set("country",addressComponent.longName);
                            break;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        DBManager.setGeocodedLocation(lat,lon,location);

        return location;
    }

    public static String getSuburb(final double lat, final double lon) {

        String suburb = DBManager.getSuburb(lat, lon);
        if (suburb == null) {
            final GeoApiContext context = new GeoApiContext();
            try {
                context.setApiKey(getNextKey());
                final GeocodingResult[] results = (GeocodingApi.reverseGeocode(context, new LatLng(lat, lon))).await();
                for (final AddressComponent addressComponent : results[0].addressComponents) {
                    final AddressComponentType[] types = addressComponent.types;
                    final int length2 = types.length;
                    int j = 0;
                    while (j < length2) {
                        final AddressComponentType type = types[j];
                        if (type == AddressComponentType.LOCALITY) {
                            if (addressComponent.longName.equals("MacArthur")) {
                                suburb = "Macarthur";
                                break;
                            }
                            suburb = addressComponent.longName;
                            break;
                        }
                        else {
                            ++j;
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (suburb == null) {
                suburb = "Unknown";
            }
            DBManager.setSuburb(lat, lon, suburb);
        }
        return suburb;
    }

    private static String getNextKey() {
        if (ReverseGeocoder.lastKey == config.getKeys().size() - 1) {
            ReverseGeocoder.lastKey = 0;
            return config.getKeys().get(ReverseGeocoder.lastKey);
        }
        ++ReverseGeocoder.lastKey;
        return config.getKeys().get(ReverseGeocoder.lastKey);
    }
}
