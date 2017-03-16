package mic.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Dazak on 15/03/2017.
 */
public class CuriosityDatabase {

    private Connection c = null;
    private Statement stmt = null;

    public CuriosityDatabase() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:CuriosityData.db");
        c.setAutoCommit(false);
    }
}
