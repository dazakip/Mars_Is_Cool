package curiosity_img_data_parser;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by Darren on 25/01/2017.
 */
public class main {

    public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException {

        /*
        // build a URL
        URL url = new URL("http://mars.jpl.nasa.gov/msl-raw-images/image/image_manifest.json");

        // read from the URL
        Scanner scan = new Scanner(url.openStream());
        String str = new String();
        while (scan.hasNext())
            str += scan.nextLine();
        scan.close();

        // build a JSON object
        JSONObject obj = new JSONObject(str);
        System.out.println(obj.getString("type"));

        */

        xmlParser x = new xmlParser();
        x.run();

    }
}

