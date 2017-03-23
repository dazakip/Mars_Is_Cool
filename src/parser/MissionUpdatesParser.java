package parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dazak on 22/03/2017.
 */
public class MissionUpdatesParser {

    String startSol;
    String endSol;
    String title;
    String description;

    Connection c;
    Statement stmt;


    public MissionUpdatesParser() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:InfoBox.db");

        Statement stmt = null;
        stmt = c.createStatement();
        c.setAutoCommit(false);
        String sql = "CREATE TABLE INFOBOX " +
                "(STARTSOL      INT   NOT NULL," +
                " ENDSOL        INT, " +
                " TITLE         STRING        NOT NULL, " +
                " DESCRIPTION   TEXT"+
                ");";
        stmt.executeUpdate(sql);
        c.commit();

    }

    public void run() throws IOException, SQLException {
        File input = new File("sol_text.html");
        Document doc = Jsoup.parse(input,"UTF-8");
        Elements divs = doc.select("div");
        for (int i = 1; i < divs.size(); i++) {


            Element e = divs.get(i);
            getSols(e.select("h2").text());
            title = e.select("h2").text().replace("'", "").replace("’", "");
            if (startSol != null)
                description = e.getElementsByClass("description").text().replace("\n", " ").replace("'", "''").replace("’", "’’");

            if (startSol != null) {
                if (Integer.parseInt(startSol) <= 1500) {
                    System.out.println("Start Sol   : " + startSol);
                    System.out.println("End Sol     : " + endSol);
                    System.out.println("Title       : " + title);
                    System.out.println("Description : " + description);
                    System.out.println("");
                    addToDatabase(startSol, endSol, title, description);
                }
            }

            startSol = endSol = title = description = null;
        }

        String desc = "All three of the USGS scientists involved in MSL are in a large conference room in JPL building 321, along with over 400 other science team\n" +
                "members. The tension in the room is rising as MSL approaches Mars. Mars\n" +
                "Odyssey is in position and sending a good signal, ready to relay data from\n" +
                "MSL during descent. Each announcement (via video from the Mission Support\n" +
                "Area in a nearby building at JPL) is greeted with applause and cheers:\n" +
                "Good signal from Odyssey, cruise stage separation, turn to entry attitude,\n" +
                "etc. A few minutes ago, the Project Science Office led a brief all hands\n" +
                "meeting of the science team, encouraging us to work as a team, be patient,\n" +
                "and above all, have fun! With 5 minutes to entry, it is quiet both in the MSA and in Bldg.\n" +
                "321. Because the radio signals from Mars take over 14 minutes to reach\n" +
                "Earth, MSL has already landed successfully (or perhaps not), but we won't\n" +
                "know for a while. This is the most critical phase of the entire mission,\n" +
                "and must be executed perfectly by the spacecraft computer without any\n" +
                "control from Earth. I haven't been nervous all day, but my heart rate is\n" +
                "now quickening. The outcome of EDL will have a major effect on my career,\n" +
                "but there is nothing I can do about it except watch with everyone else.\n" +
                "What a crazy business! But I love it.\n" +
                "As MSL descends through the atmosphere, it will not be visible from\n" +
                "Earth, so its radio signals must be relayed by Odyssey. So a cheer goes up\n" +
                "when Odyssey data is first received. Another big cheer when the parachute\n" +
                "deployment is reported. Direct communication to Earth is lost as expected,\n" +
                "just before the lander separates from the parachute and retro-rockets\n" +
                "start. Another cheer as sky crane starts! When the signal is received\n" +
                "showing that MSL has landed successfully, everyone in the MSA and in 321\n" +
                "jump up at once, cheering and clapping loudly. Within a few minutes the\n" +
                "first pictures from MSL Hazard Avoidance Cameras are received via Odyssey\n" +
                "and even louder cheers erupt from both rooms.\n" +
                "Given the successful landings of Mars Pathfinder and both Mars\n" +
                "Exploration Rovers, I should have expected another success, but I wasnt.\n" +
                "MSL is so much more complex than previous missions, and the EDL so much\n" +
                "more difficult, that I was prepared for the worst. My heart is still\n" +
                "racing, many minutes after landing. I'm on second shift (uplink), so I\n" +
                "have to get some sleep before returning to JPL by 5:30 tomorrow morning. I\n" +
                "dont know how I will be able to sleep!";
        desc = desc.replace("\n", "").replace("'", "").replace("’", "");

        stmt = c.createStatement();
        String sql = "INSERT INTO INFOBOX (STARTSOL, ENDSOL, TITLE, DESCRIPTION) " +
                "VALUES (0,"+null+",'MSL Curiosity Rover lands on Mars!','"+desc+"');";
        stmt.executeUpdate(sql);
        c.commit();
    }

    private void getSols(String id) {

        String pattern = "Sol\40(\\d+)(?:-(\\d+))?";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(id);
        if (m.find( )) {
            startSol = m.group(1);
            endSol = m.group(2);
        }
    }

    private void addToDatabase(String startSol, String endSol, String title, String description) throws SQLException {
        stmt = c.createStatement();
        String sql = "INSERT INTO INFOBOX (STARTSOL, ENDSOL, TITLE, DESCRIPTION) " +
                "VALUES (" + Integer.parseInt(startSol) + "," + endSol + ",'" + title + "','" + description + "');";
        stmt.executeUpdate(sql);
        c.commit();
    }

}
