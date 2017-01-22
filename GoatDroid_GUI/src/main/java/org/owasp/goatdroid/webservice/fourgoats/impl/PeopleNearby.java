package org.owasp.goatdroid.webservice.fourgoats.impl;

import org.owasp.goatdroid.webservice.fourgoats.Constants;
import org.owasp.goatdroid.webservice.fourgoats.DistanceCalculator;
import org.owasp.goatdroid.webservice.fourgoats.Validators;
import org.owasp.goatdroid.webservice.fourgoats.bean.PeopleNearbyBean;
import org.owasp.goatdroid.webservice.fourgoats.bean.PeopleNearbyListBean;
import org.owasp.goatdroid.webservice.fourgoats.dao.PeopleNearbyDAO;
import org.owasp.goatdroid.webservice.fourgoats.model.NearbyUserModel;

import java.util.ArrayList;

public class PeopleNearby {

    public static PeopleNearbyBean setCurrentLocation (String sessionToken, String latitude,
                                                       String longitude) {
        PeopleNearbyBean bean = new PeopleNearbyBean();
        bean.setSuccess(false);
        PeopleNearbyDAO dao = new PeopleNearbyDAO();
        ArrayList<String> errors = new ArrayList<String>();

        try {
            dao.openConnection();
            if (!dao.isSessionValid(sessionToken)
                    || !Validators.validateSessionTokenFormat(sessionToken))
                errors.add(Constants.INVALID_SESSION);
            else if (!Validators.validateCheckinFields(latitude, longitude))
                errors.add(Constants.LATITUDE_FORMAT_INVALID);

            if (errors.size() == 0) {
                String userID = dao.getUserID(sessionToken);
                boolean result;
                if (dao.checkUserRowExists(userID)) {
                    result = dao.updateUserLocation(userID, latitude, longitude);
                } else {
                    result = dao.insertUserLocation(userID, latitude, longitude);
                }
                bean.setSuccess(result);
                if (!result) {
                    errors.add(Constants.UNEXPECTED_ERROR);
                }
            }
        } catch (Exception ex) {
            errors.add(Constants.UNEXPECTED_ERROR);
        } finally {
            bean.setErrors(errors);
            try {
                dao.closeConnection();
            } catch (Exception e) {
            }
        }
        return bean;
    }

    public static PeopleNearbyListBean getPeopleNearby(String sessionToken, String latitude, String longitude) {
        PeopleNearbyListBean bean = new PeopleNearbyListBean();
        bean.setSuccess(false);
        PeopleNearbyDAO dao = new PeopleNearbyDAO();
        ArrayList<String> errors = new ArrayList<String>();

        try {
            dao.openConnection();
            if (!dao.isSessionValid(sessionToken)
                    || !Validators.validateSessionTokenFormat(sessionToken))
                errors.add(Constants.INVALID_SESSION);

            if (errors.size() == 0) {
                String userID = dao.getUserID(sessionToken);
                ArrayList<NearbyUserModel> users = dao.getActiveUsersNearby(userID);

                setDistance(users, latitude, longitude);

                bean.setUsers(users);
                bean.setSuccess(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            errors.add(Constants.UNEXPECTED_ERROR);
        } finally {
            bean.setErrors(errors);
            try {
                dao.closeConnection();
            } catch (Exception e) {
            }
        }
        return bean;
    }

    private static void setDistance(ArrayList<NearbyUserModel> users, String latitude, String longitude) {
        double dLatitude = Double.parseDouble(latitude);
        double dLongitude = Double.parseDouble(longitude);

        for (NearbyUserModel user : users) {
            double userLatitude = Double.parseDouble(user.getLatitude());
            double userLongitude = Double.parseDouble(user.getLongitude());
            double distance =
                    DistanceCalculator.getDistanceInMeters(userLatitude, userLongitude, dLatitude, dLongitude);
            String sDistance = String.format("%.1f", distance);
            user.setDistance(sDistance);
            user.setLatitude(null);
            user.setLongitude(null);
        }
    }
}
