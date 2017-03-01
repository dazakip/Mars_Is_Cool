package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Darren on 26/01/2017.
 */
public class xmlParser {

    public void run(){
        Connection c = null;
        Statement stmt = null;
            try {
            // open connection to cur_loc database
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:cur_loc.db");
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

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    arrival = eElement.getElementsByTagName("arrivalTime").item(0).getTextContent();
                    site = eElement.getElementsByTagName("site").item(0).getTextContent();
                    lon = eElement.getElementsByTagName("lon").item(0).getTextContent();
                    lat = eElement.getElementsByTagName("lat").item(0).getTextContent();
                    start = eElement.getElementsByTagName("startSol").item(0).getTextContent();
                    end = eElement.getElementsByTagName("endSol").item(0).getTextContent();

                    System.out.println("BEFORE FUCKING IT: " +arrival);
                    arrival = convertToDateTime(arrival);

                }

                stmt = c.createStatement();
                String sql = "INSERT INTO LOCATIONS (ARRIV,SITE,LONG,LAT,STARTSOL,ENDSOL) " +
                        "VALUES ('" + arrival + "'," + site + "," + lon + "," + lat + "," + start + "," + end+ ");";
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
