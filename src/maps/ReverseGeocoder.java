package maps;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import core.DBManager;

import static core.MessageListener.config;

public class ReverseGeocoder {

    private static int lastKey;

    public static void main(final String[] args) {
        DBManager.novabotdbConnect();
        System.out.println(getSuburb(-35.405055, 149.1270075));
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
