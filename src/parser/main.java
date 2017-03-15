package parser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Darren on 25/01/2017.
 */
public class main {

    public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException, SQLException, ClassNotFoundException {

        //jsonParser j = new jsonParser();
        //j.run();

        remsDownloader y = new remsDownloader();
        y.run();

        //remsParser r = new remsParser();
        //r.run();
        //locationParsing x = new locationParsing();
       // x.run();

    }
}

