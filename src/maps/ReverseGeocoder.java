package maps;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import core.DBManager;

import java.util.ArrayList;
import java.util.Arrays;

public class ReverseGeocoder
{
    public static final ArrayList<String> GMAPS_KEYS;
    private static int lastKey;

    public static void main(final String[] args) {
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
        if (ReverseGeocoder.lastKey == ReverseGeocoder.GMAPS_KEYS.size() - 1) {
            ReverseGeocoder.lastKey = 0;
            return ReverseGeocoder.GMAPS_KEYS.get(ReverseGeocoder.lastKey);
        }
        ++ReverseGeocoder.lastKey;
        return ReverseGeocoder.GMAPS_KEYS.get(ReverseGeocoder.lastKey);
    }

    static {
        GMAPS_KEYS = new ArrayList<String>(Arrays.asList("AIzaSyCDM4XAeTqqWuZXcB_QHq9oV6elSEhjMIs", "AIzaSyC6Y28pz1MQoXiNohD8VB9njr4KngE6rY0", "AIzaSyAV6DA0AG7vGcihewT02ORIgZDzzVcqfbs", "AIzaSyAprRhsD1fAXtWDFpQERZ4tf403Ggh2S0g", "AIzaSyC-DL4ptlvJlrJR_44CBNWYLGRkMemC_Wo", "AIzaSyDS3uMuW1PW9fIORW79Vt_TB134F6GIV8o", "AIzaSyAevGAgFW_kRFOj1zZe2ZMiTDuCaqQIaug", "AIzaSyDQO3jdZLichs0z-dda2DqTmrb2wu1zefw", "AIzaSyCO32SeGLBFe5A42hjvHPqB1EMZgTMSGns", "AIzaSyBbg95L6HXWrbaAcBD2oyWNRb16m_eNAP4"));
        ReverseGeocoder.lastKey = 0;
    }
}
