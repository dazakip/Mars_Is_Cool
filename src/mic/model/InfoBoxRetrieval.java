package mic.model;


import java.sql.*;
import java.util.Random;

public class InfoBoxRetrieval {

    private Connection c;
    private Statement stmt;
    private ResultSet rs;

    private int startSol;
    private int endSol;
    private String description;

    public InfoBoxRetrieval() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:resources/InfoBox.db");
        c.setAutoCommit(false);
        endSol = -6;
        startSol = -6;
        description = "Nothing to say.";
    }

    public String getInfoBox(int sol) throws SQLException {
        if (sol > startSol && sol < endSol ) {

        }
        else {
            endSol = -1;
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM INFOBOX WHERE STARTSOL =" + sol + ";");

            if (rs.next()) {
                String testNull = rs.getString("ENDSOL");
                if (testNull == null) {
                    endSol = sol;
                } else {
                    endSol = Integer.parseInt(testNull);
                }
                startSol = rs.getInt("STARTSOL");
                description = rs.getString("DESCRIPTION");
            } else {
                description = randomLine();
            }
        }
        return description;
    }

    private String randomLine() {
        Random rand = new Random();
        int randomNum = rand.nextInt((10 - 0) + 1);

        if (randomNum==0)
            return "\"I'm not feeling very chatty tosol. No that was not a typo.\"";
        else if (randomNum==1)
            return "\"Sometimes it gets lonely up here...\"";
        else if (randomNum==2)
            return "\"It doesn't look so red down here.\"";
        else if (randomNum==3)
            return "\"I wish my parents gave me a normal name.\"";
        else if (randomNum==4)
            return "\"Sometimes i worry Martians will run past me and I'll be too slow to take a picture.\"";
        else if (randomNum==5)
            return "\"Take a look at the laaaawman, beating up the wrong guy!\"";
        else if (randomNum==6)
            return "\"I wonder what Pathfiner is up to.\"";
        else if (randomNum==7)
            return "\"I hope the data i'm gathering gets home okay.\"";
        else if (randomNum==8)
            return "\"I hope i see my creators again\"";
        else if (randomNum==9)
            return "\"Need to stay vigilant, a dust devil could strike at any time.\"";
        else if (randomNum==10)
            return "\"Beep boop doing serious work beep boop.\"";
        else
            return "\"I wish I could drive faster.\"";
    }
}
