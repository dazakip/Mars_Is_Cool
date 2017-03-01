package parser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by Darren on 25/01/2017.
 */
public class main {

    public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException {

        jsonParser j = new jsonParser();
        j.run();


/*
        xmlParser x = new xmlParser();
        x.run();
*/
    }
}

