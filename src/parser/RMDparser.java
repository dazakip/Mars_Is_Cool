package parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

/**
 * Created by Dazak on 15/03/2017.
 */
public class RMDparser {

    int currentTime;
    Connection newC = null;
    Connection elevationC = null;
    Statement elevationSTMT = null;
    double lastElevation = 0.00;

    double nooll;

    public RMDparser() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        newC = DriverManager.getConnection("jdbc:sqlite:CuriosityData.db");
        Statement stmt = null;
        stmt = newC.createStatement();
        newC.setAutoCommit(false);
        String sql = "CREATE TABLE CURIOSITYDATA " +
                "(STAMP         TIMESTAMP   NOT NULL," +
                " SOL           INT         NOT NULL, " +
                " TIME          TIME        NOT NULL, " +
                " ELEVATION     DOUBLE,"+
                " AIRTEMP     DOUBLE,"+
                " UVA     DOUBLE,"+
                " PRESSURE     DOUBLE,"+
                " HUMIDITY     DOUBLE"+
                ");";
        stmt.executeUpdate(sql);
        newC.commit();

        elevationC = DriverManager.getConnection("jdbc:sqlite:elevationData2.db");
        elevationC.setAutoCommit(false);

        nooll = 11111;
    }


    public void run() throws IOException, SQLException {

        for (int i = 0; i <= 1500; i++) {
            System.out.println("Sol: " +i);
            if (new File("C:\\Users\\Dazak\\Desktop\\instr_data\\RMD\\"+i+".txt").exists()) {
                BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Dazak\\Desktop\\instr_data\\RMD\\"+i+".txt"));
                String firstLine[] = br.readLine().split(",");
                currentTime = Integer.parseInt(firstLine[0].substring(1, firstLine[0].length()));
                String line;
                while ((line = br.readLine()) != null) {
                    String segments[] = line.split(",");
                    dealWithLine(segments);
                }
            }
        }

        newC.commit();
        newC.close();
        //elevationSTMT.close();
        //elevationC.close();
    }

    private void dealWithLine(String[] line) throws SQLException {
        int timestamp = Integer.parseInt(line[0].substring(1, line[0].length()));

        if (timestamp >= (currentTime+3600)) {

            int sol = Integer.parseInt(line[2].substring(1,6).replaceFirst("^0+(?!$)", ""));
            String time = line[2].substring(7,15);
            double elevation = getElevation(timestamp, sol);
            double airTemp = getValue(line[15]);
            double UVA = getValue(line[17]);
            double humidity = getValue(line[30]);
            double pressure = getValue(line[38]);
            currentTime = timestamp;
            finalValues(timestamp, sol, time, elevation, airTemp, UVA, humidity, pressure);
        }

    }

    private double getValue(String valueB4) {
        String value = valueB4.replaceAll("[\\W]", "");
        if (value.equals("NULL")||value.equals("UNK"))
            return nooll;
        else
            return Double.parseDouble(value)/100;
    }

    private double getElevation(int timestamp, int sol) throws SQLException {
        int lol = 0;
        elevationSTMT = elevationC.createStatement();
        ResultSet rs = elevationSTMT.executeQuery( "SELECT * FROM ELEVATION WHERE SOL = "+sol+";");
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                double ele = rs.getDouble("ELEVATION");
                lastElevation = ele;
                return ele;
            }
        }

        else {
            return lastElevation;
            /*
            rs = elevationSTMT.executeQuery( "SELECT * FROM ELEVATION " +
                    "WHERE SOL = "+sol+" AND STAMP BETWEEN "+(timestamp-60000)+" AND " +(timestamp+60000)+";");
            if (rs.isBeforeFirst()) {
                while (rs.next()) {
                    System.out.println(lol);
                    return rs.getFloat("ELEVATION");
                }
            }
            else
                return nooll;*/
        }

        rs.close();

        return nooll;
    }

    private void finalValues(int timestamp, int sol, String time, double elevation, double airtemp,
                             double UVA, double humidity, double pressure) throws SQLException {

        System.out.println(timestamp + " - " + sol + " - " + time + " |Elevation: " +elevation+ " |AirTemp: " + airtemp + " |UVA: " + UVA + " |Humidity: " + humidity + " Pressure: " + pressure);
        java.sql.Time myTime = java.sql.Time.valueOf(time);

        String sql = "INSERT INTO CURIOSITYDATA (STAMP, SOL, TIME, ELEVATION, AIRTEMP, UVA, HUMIDITY, PRESSURE) " +
                    " VALUES(?,?,?,?,?,?,?,?)";

        PreparedStatement pstmt = newC.prepareStatement(sql);
        pstmt.setInt(1, timestamp);
        pstmt.setInt(2, sol);
        pstmt.setTime(3, myTime);
        if (elevation!=nooll) pstmt.setDouble(4, elevation);
        else pstmt.setNull(4, java.sql.Types.DOUBLE);

        if (airtemp!=nooll) pstmt.setDouble(5, airtemp);
        else pstmt.setNull(5, java.sql.Types.DOUBLE);

        if (UVA!=nooll) pstmt.setDouble(6, UVA);
        else pstmt.setNull(6, java.sql.Types.DOUBLE);

        if (humidity!=nooll) pstmt.setDouble(7, humidity);
        else pstmt.setNull(7, java.sql.Types.DOUBLE);

        if (pressure!=nooll) pstmt.setDouble(8, pressure);
        else pstmt.setNull(8, java.sql.Types.DOUBLE);

        //System.out.println(pstmt);
        pstmt.executeUpdate();
        newC.commit();

    }
}
