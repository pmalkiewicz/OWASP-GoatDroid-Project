package org.owasp.goatdroid.fourgoats.rest.peoplenearby;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.owasp.goatdroid.fourgoats.base.ResponseBase;
import org.owasp.goatdroid.fourgoats.misc.Profile;

import java.sql.Timestamp;
import java.util.ArrayList;

public class PeopleNearbyResponse extends ResponseBase {
    public static ArrayList<Profile> parseList(String response) {

        JSONObject json;

        try {
            json = new JSONObject(response);
            if (json.getString("success").equals("true")) {
                ArrayList<Profile> profiles = new ArrayList<>();

                try {
                    JSONArray usersArray = json.getJSONArray("users");

                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject jsonObject = usersArray.getJSONObject(i);
                        profiles.add(parseProfile(jsonObject));
                    }
                } catch (JSONException ex) {
                    // there is only single user or no users at all- not an array
                    if (json.has("users")) {
                        JSONObject user = json.getJSONObject("users");
                        profiles.add(parseProfile(user));
                    }
                }
                return profiles;
            } else {
                return null;
            }
        } catch (JSONException ex) {
            return null;
        }
    }

    private static Profile parseProfile(JSONObject jsonObject) throws JSONException {
        Profile profile = new Profile();

        if (jsonObject.has("userName"))
            profile.setUserName((String) jsonObject.get("userName"));
        if (jsonObject.has("firstName"))
            profile.setFirstName((String) jsonObject.get("firstName"));
        if (jsonObject.has("lastName"))
            profile.setLastName((String) jsonObject.get("lastName"));
        if (jsonObject.has("distance"))
            profile.setDistance(Double.parseDouble((String) jsonObject.get("distance")));
        if (jsonObject.has("lastSeenTimestamp"))
            profile.setTimestamp(Timestamp.valueOf((String) jsonObject.get("lastSeenTimestamp")));
        return profile;
    }
}
