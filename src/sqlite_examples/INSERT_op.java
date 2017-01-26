package sqlite_examples;
import java.sql.*;

/**
 * Created by Darren on 23/01/2017.
 */
public class INSERT_op {
    public static void main( String args[] )
    {
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:cur_loc.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");


            stmt = c.createStatement();
            //String sql = "INSERT INTO LOCATIONS (ARRIV,SITE,LONG,LAT,STARTSOL,ENDSOL) " +
              //      "VALUES ('2008-11-11 13:23:44',1,1,1,1,1)";

            String sql = "DELETE from LOCATIONS where SITE=0;";
            stmt.executeUpdate(sql);
            stmt.executeUpdate(sql);
            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Records created successfully");
    }
}
