package sqlite_examples;
/**
 * Created by Darren on 23/01/2017.
 */
import java.sql.*;

public class CREATE_db_and_table
{
    public static void main( String args[] )
    {
      /************************************************************************** CREATE A DATABASE
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:cur_loc.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");
*/


        //***************************************************************************** CREATE TABLE

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:elevationData.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE ELEVATION " +
                    "(STAMP         TIMESTAMP   NOT NULL," +
                    " SOL           INT         NOT NULL, " +
                    " TIME          TIME        NOT NULL, " +
                    " ELEVATION     DOUBLE"+
                    " AIRTEMP     DOUBLE"+
                    " UVA     DOUBLE"+
                    " PRESSURE     DOUBLE"+
                    " HUMIDITY     DOUBLE"+
                    ");";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");


    }
}
