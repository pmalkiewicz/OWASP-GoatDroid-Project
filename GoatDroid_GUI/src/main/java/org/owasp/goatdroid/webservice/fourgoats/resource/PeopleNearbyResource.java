package org.owasp.goatdroid.webservice.fourgoats.resource;

import org.owasp.goatdroid.webservice.fourgoats.Constants;
import org.owasp.goatdroid.webservice.fourgoats.bean.PeopleNearbyBean;
import org.owasp.goatdroid.webservice.fourgoats.bean.PeopleNearbyListBean;
import org.owasp.goatdroid.webservice.fourgoats.impl.PeopleNearby;

import javax.ws.rs.*;

@Path("/fourgoats/api/v1/people_nearby")
public class PeopleNearbyResource {

    @Path("current_location")
    @POST
    @Produces("application/json")
    public PeopleNearbyBean setCurrentLocation(
            @CookieParam(Constants.SESSION_TOKEN_NAME) String sessionToken,
            @FormParam("latitude") String latitude,
            @FormParam("longitude") String longitude) {
        try {
            return PeopleNearby.setCurrentLocation(sessionToken, latitude, longitude);
        } catch (NullPointerException e) {
            PeopleNearbyBean bean = new PeopleNearbyBean();
            bean.setSuccess(false);
            return bean;
        }
    }

    @Path("list")
    @POST
    @Produces("application/json")
    public PeopleNearbyListBean getPeopleNearby(
            @CookieParam(Constants.SESSION_TOKEN_NAME) String sessionToken) {
        try {
            System.out.println("DEBUG: Resource called");
            PeopleNearbyListBean bean = PeopleNearby.getPeopleNearby(sessionToken);
            System.out.println(bean.getUsers().toString());
            return bean;
        } catch (NullPointerException e) {
            PeopleNearbyListBean bean = new PeopleNearbyListBean();
            bean.setSuccess(false);
            return bean;
        }
    }
}
