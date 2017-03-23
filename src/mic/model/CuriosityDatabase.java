package mic.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dazak on 15/03/2017.
 */
public class CuriosityDatabase {

    private Connection c;
    private Statement stmt;
    private ResultSet rs;
    private ArrayList<DataEntry> dataEntries;
    private double nooll;

    public CuriosityDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:resources/CuriosityData.db");
        c.setAutoCommit(false);
        nooll = 11111;
        dataEntries = new ArrayList<>();

    }

    public void nextSol(int sol) throws SQLException {

        stmt = c.createStatement();
        rs = stmt.executeQuery( "SELECT * FROM CURIOSITYDATA WHERE SOL ="+sol+";");

        while (rs.next()) {
            DataEntry d = new DataEntry();
            d.setTimeStamp(rs.getInt("STAMP"));
            d.setSol(rs.getInt("SOL"));
            d.setTime(rs.getString("TIME"));

            d.setElevation(rs.getDouble("ELEVATION"));
            if (rs.wasNull()) d.setElevation(nooll);

            d.setAirTemp(rs.getDouble("AIRTEMP"));
            if (rs.wasNull()) d.setAirTemp(nooll);

            d.setUVA(rs.getDouble("UVA"));
            if (rs.wasNull()) d.setUVA(nooll);

            d.setHumidity(rs.getDouble("HUMIDITY"));
            if (rs.wasNull()) d.setHumidity(nooll);

            d.setPressure(rs.getDouble("PRESSURE"));
            if (rs.wasNull()) d.setPressure(nooll);

            dataEntries.add(d);
        }
    }

    public ArrayList<DataEntry> getDataEntries(){
        return dataEntries;
    }

    public void updateEntries(double lowestBound, double upperBound) {

        ArrayList<DataEntry> oldData = new ArrayList<>();
        for (DataEntry d : dataEntries) {
            if (d.getTimeStamp() < lowestBound || d.getTimeStamp() > upperBound+88765) {
                oldData.add(d);
            }
        }

        dataEntries.removeAll(oldData);
    }
}
