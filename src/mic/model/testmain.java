package mic.model;

import org.xml.sax.SAXException;
import parser.MissionUpdatesParser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

/**
 * Created by Dazak on 22/03/2017.
 */
public class testmain {

    public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException, SQLException, ClassNotFoundException {


        Random rand = new Random();
        for (int i = 0; i < 50; i++)
        System.out.println(rand.nextInt((10 - 0) + 1) + 0);

    }

}
