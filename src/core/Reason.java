package core;

/**
 * Created by Owner on 27/06/2017.
 */
public enum Reason {
    SupporterAttemptedPublic,
    PublicAttemptedGeofence;

    @Override
    public String toString() {
        switch (this){
            case SupporterAttemptedPublic:
                return "Trying to use a public channel as a supporter";
            case PublicAttemptedGeofence:
                return "Trying to use a geofence region as a public user";
        }
        return "";
    }
}
