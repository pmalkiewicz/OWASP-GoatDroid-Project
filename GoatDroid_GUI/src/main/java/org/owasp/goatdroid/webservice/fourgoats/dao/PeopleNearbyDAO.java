package org.owasp.goatdroid.webservice.fourgoats.dao;

import org.owasp.goatdroid.webservice.fourgoats.Constants;
import org.owasp.goatdroid.webservice.fourgoats.model.NearbyUserModel;
import org.owasp.goatdroid.webservice.fourgoats.model.UserModel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PeopleNearbyDAO extends BaseDAO {

    public boolean updateUserLocation(String userID, String latitude, String longitude) throws SQLException {
        String sql = "update people_nearby set latitude = ?, longitude = ?, timestamp = CURRENT_TIMESTAMP " +
                "where userid = ?";
        PreparedStatement updateStatement = (PreparedStatement) conn.prepareCall(sql);
        updateStatement.setString(1, latitude);
        updateStatement.setString(2, longitude);
        updateStatement.setString(3, userID);
        int result = updateStatement.executeUpdate();
        return result == 1;
    }

    public boolean checkUserRowExists(String userID) throws SQLException {
        String sql = "select * from people_nearby where userid = ?";
        PreparedStatement selectStatement = (PreparedStatement) conn.prepareCall(sql);
        selectStatement.setString(1, userID);
        ResultSet rs = selectStatement.executeQuery();
        return rs.next();
    }

    public boolean insertUserLocation(String userID, String latitude, String longitude) throws SQLException {
        String sql = "insert into people_nearby (userid, latitude, longitude, timestamp) " +
                "values (?, ?, ?, CURRENT_TIMESTAMP)";
        PreparedStatement insertStatement = (PreparedStatement) conn.prepareCall(sql);
        insertStatement.setString(1, userID);
        insertStatement.setString(2, latitude);
        insertStatement.setString(3, longitude);
        int result = insertStatement.executeUpdate();
        return result == 1;
    }

    public ArrayList<NearbyUserModel> getActiveUsersNearby(String userID) throws SQLException {
        System.out.println("DAO called");
        String sql = "select users.userName, users.firstName, " +
                "users.lastName, people_nearby.latitude, people_nearby.longitude, " +
                "people_nearby.timestamp from people_nearby join users on users.userID = people_nearby.userID " +
                "where people_nearby.userID <> ? and {fn TIMESTAMPDIFF(SQL_TSI_HOUR, people_nearby.timestamp, CURRENT_TIMESTAMP)} < ?";

        PreparedStatement selectStatement = (PreparedStatement) conn.prepareCall(sql);
        selectStatement.setString(1, userID);
        selectStatement.setInt(2, Constants.MAX_HOURS_PEOPLE_NEARBY_PROFILE_ACTIVE);
        ResultSet rs = selectStatement.executeQuery();

        ArrayList<NearbyUserModel> users = new ArrayList<NearbyUserModel>();

        while (rs.next()) {
            NearbyUserModel user = new NearbyUserModel();
            user.setUserName(rs.getString("userName"));
            user.setFirstName(rs.getString("firstName"));
            user.setLastName(rs.getString("lastName"));
            user.setLatitude(rs.getString("latitude"));
            user.setLongitude(rs.getString("longitude"));
            user.setLastSeenTimestamp(rs.getString("timestamp"));
            users.add(user);
        }
        return users;
    }
}
