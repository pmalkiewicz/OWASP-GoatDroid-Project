package org.owasp.goatdroid.webservice.fourgoats.model;

public class NearbyUserModel extends UserModel{
    String latitude;
    String longitude;
    String distance;
    String lastSeenTimestamp;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLastSeenTimestamp() {
        return lastSeenTimestamp;
    }

    public void setLastSeenTimestamp(String lastSeenTimestamp) {
        this.lastSeenTimestamp = lastSeenTimestamp;
    }
}
