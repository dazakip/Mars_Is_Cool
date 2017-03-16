package parser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Darren on 25/01/2017.
 */
public class main2 {

    public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException, SQLException, ClassNotFoundException {

        //jsonParser r = new jsonParser();
        //locationParsing r = new locationParsing();
        //remsDownloader r = new remsDownloader();
        ADRparser r = new ADRparser();
        //RMDparser r = new RMDparser();
        r.run();

    }
}

