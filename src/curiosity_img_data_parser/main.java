package curiosity_img_data_parser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

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

