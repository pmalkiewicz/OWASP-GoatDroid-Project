package org.owasp.goatdroid.fourgoats.rest.location;

import android.content.Context;

import org.owasp.goatdroid.fourgoats.base.RequestBase;
import org.owasp.goatdroid.fourgoats.misc.Utils;
import org.owasp.goatdroid.fourgoats.requestresponse.AuthenticatedRestClient;
import org.owasp.goatdroid.fourgoats.requestresponse.RequestMethod;

public class LocationRequest extends RequestBase {

    Context context;
    String destinationInfo;

    public LocationRequest(Context context) {
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

        return LocationResponse.isSuccess(client.getResponse());
    }
}
