package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Dazak on 14/03/2017.
 */
public class ADRparser {

    int currentTime;
    Connection c = null;
    Statement stmt = null;

    public ADRparser() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:elevationData2.db");

        Statement stmt = null;
        stmt = c.createStatement();
        c.setAutoCommit(false);
        String sql = "CREATE TABLE ELEVATION " +
                "(STAMP         TIMESTAMP   NOT NULL," +
                " SOL           INT         NOT NULL, " +
                " TIME          TIME        NOT NULL, " +
                " ELEVATION     DOUBLE"+
                ");";
        stmt.executeUpdate(sql);
        c.commit();


    }


    public void run() throws IOException, SQLException {

        for (int i = 0; i <= 1500; i++) {
            System.out.println("Sol: " +i);
            if (new File("C:\\Users\\Dazak\\Desktop\\instr_data\\ADR\\"+i+".txt").exists()) {
                BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Dazak\\Desktop\\instr_data\\ADR\\"+i+".txt"));
                String firstLine[] = br.readLine().split(",");
                currentTime = Integer.parseInt(firstLine[0].substring(1, firstLine[0].length()));
                System.out.println(currentTime);
                String line;
                line = br.readLine();
                    String segments[] = line.split(",");
                    dealWithLine(segments);

            }
        }


        stmt.close();
        c.commit();
        c.close();
    }

    private void dealWithLine(String[] line) {

        String timeVal = line[0].substring(1, line[0].length());
        int timeInt = Integer.parseInt(timeVal);

        int sol = Integer.parseInt(line[2].substring(1,6).replaceFirst("^0+(?!$)", ""));
        String time = line[2].substring(7,15);
        System.out.println(time);
        String roverZVal = line[7];
        System.out.println(timeVal+" - " + roverZVal);
        currentTime = timeInt;
        finalValues(timeInt, roverZVal, sol, time);
    }

    private void finalValues(int timestamp,  String elevation, int sol, String time){
        try {
            System.out.println("String - "+elevation);
            System.out.println("Double - "+Double.parseDouble(elevation));
            stmt = c.createStatement();
                String sql = "INSERT INTO ELEVATION (STAMP, SOL, TIME, ELEVATION) " +
                        "VALUES ('" + timestamp + "'," + sol + ",'" + time + "'," + Double.parseDouble(elevation) + ");";
                stmt.executeUpdate(sql);


        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

    }

}
