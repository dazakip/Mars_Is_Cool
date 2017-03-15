package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Darren on 26/01/2017.
 */
public class locationParsing {

    public void run(){
        //toGeoJson();

        Connection c = null;
        Statement stmt = null;
            try {
            // open connection to cur_loc database
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:cur_locV2.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            // set up xml file to parse into doc builder
            File fXmlFile = new File("locations.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("location");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);
                String arrival = null;
                String site = null;
                String lon = null;
                String lat = null;
                String start = null;
                String end = null;
                String drive = null;

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    arrival = eElement.getElementsByTagName("arrivalTime").item(0).getTextContent();
                    site = eElement.getElementsByTagName("site").item(0).getTextContent();
                    lon = eElement.getElementsByTagName("lon").item(0).getTextContent();
                    lat = eElement.getElementsByTagName("lat").item(0).getTextContent();
                    start = eElement.getElementsByTagName("startSol").item(0).getTextContent();
                    end = eElement.getElementsByTagName("endSol").item(0).getTextContent();
                    drive = eElement.getElementsByTagName("drive").item(0).getTextContent();

                    System.out.println("BEFORE FUCKING IT: " +arrival);
                    arrival = convertToDateTime(arrival);

                }

                stmt = c.createStatement();
                String sql = "INSERT INTO LOCATIONS (ARRIV,SITE,LONG,LAT,STARTSOL,ENDSOL,DRIVE) " +
                        "VALUES ('" + arrival + "'," + site + "," + lon + "," + lat + "," + start + "," + end+ "," + drive + ");";
                stmt.executeUpdate(sql);
            }

            stmt.close();
            c.commit();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
            System.out.println("Records created successfully");


    }

    private void toGeoJson() {
        Connection c;
        Statement stmt;
        ResultSet rs = null;
        PrintWriter writer = null;
        String currCoord = null;

        //////////////////////////////////////////////////////////////////////////////////
        try{
            writer = new PrintWriter("curiositypath.js");
            writer.println("var data = {\"type\":\"FeatureCollection\",\"features\":[");

        } catch (IOException e) {
            System.err.println(e);
        }
        /////////////////////////////////////////////////////////////////////////////////

        try {
            //open database with coordinates
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:cur_loc.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();

            for (int i = 0; i <= 1500; i++) {
                rs = stmt.executeQuery("SELECT * FROM LOCATIONS WHERE ENDSOL ="+i+" OR STARTSOL ="+i+";");


                while (rs.next()) {
                    double longi = rs.getDouble("LONG");
                    double lat = rs.getDouble("LAT");
                    currCoord = currCoord + "["+longi+","+lat+"],";
                }

                if (currCoord != null) {
                    writer.println("{\"type\":\"Feature\",\"properties\": {\"name\":\""+i+"\"},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[");
                    writer.print(currCoord.substring(0, currCoord.length() - 1));
                    writer.print("]}},");
                    writer.println();
                    System.out.println(currCoord);
                    currCoord = null;
                }
            }
            writer.print("]}");
            rs.close();
            stmt.close();
            c.close();
            writer.close();

        } catch ( Exception e ) {
            System.err.println( e );
            System.exit(0);
        }
        System.out.println("Operation done successfully");

    }

    private String convertToDateTime(String dt) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        Calendar cal = Calendar.getInstance(tz);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss'Z'");
        sdf.setCalendar(cal);
        try {
            cal.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date = cal.getTime();  //2008-11-11 13:23:44
        int year = date.getYear() - 100+2000;
        String month = Integer.toString(date.getMonth() + 1);
        if(date.getMonth() <10) {
            month = "0"+month;
        }
        String dateNum = Integer.toString(date.getDate());
        if(date.getDate() <10) {
            dateNum = "0"+dateNum;
        }
        String finalDate = year+"-"+month+"-"+dateNum+" "+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds();
        System.out.println(date.toString());
        System.out.println(date.getHours());
        System.out.println(finalDate);
        //System.exit(0);
        return finalDate;
    }
}
