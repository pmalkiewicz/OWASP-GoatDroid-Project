package org.owasp.goatdroid.fourgoats.rest.peoplenearby;

import android.content.Context;

import org.owasp.goatdroid.fourgoats.base.RequestBase;
import org.owasp.goatdroid.fourgoats.misc.Profile;
import org.owasp.goatdroid.fourgoats.misc.Utils;
import org.owasp.goatdroid.fourgoats.requestresponse.AuthenticatedRestClient;
import org.owasp.goatdroid.fourgoats.requestresponse.RequestMethod;

import java.util.ArrayList;

public class PeopleNearbyRequest extends RequestBase {

    private Context context;
    private String destinationInfo;

    public PeopleNearbyRequest(Context context) {
        this.context = context;
        destinationInfo = Utils.getDestinationInfo(context);
    }

    public boolean sendCurrentLocation(String sessionToken,
                                             String latitude, String longitude) throws Exception {

        AuthenticatedRestClient client = new AuthenticatedRestClient("https://"
                + destinationInfo + "/fourgoats/api/v1/people_nearby/current_location", sessionToken);

        client.AddParam("latitude", latitude);
        client.AddParam("longitude", longitude);
        client.Execute(RequestMethod.POST, context);

        return PeopleNearbyResponse.isSuccess(client.getResponse());
    }

    public ArrayList<Profile> getProfiles(String sessionToken)
            throws Exception {

        AuthenticatedRestClient client = new AuthenticatedRestClient("https://"
                + destinationInfo + "/fourgoats/api/v1/people_nearby/list",
                sessionToken);
        client.Execute(RequestMethod.POST, context);

        return PeopleNearbyResponse.parseList(client.getResponse());
    }
}
