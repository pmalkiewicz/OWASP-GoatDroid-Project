package org.owasp.goatdroid.webservice.fourgoats.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
