package parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

/**
 * Created by Dazak on 08/02/2017.
 */
public class jsonParser {

    /**
     * FHAZ_RIGHT_B     - Front Hazcam: Right B
     * FHAZ_RIGHT_A     - Front Hazcam: Right A
     * FHAZ_LEFT_B      - Front Hazcam: Left B
     * RHAZ_RIGHT_B     - Rear Hazcam: Right B
     * RHAZ_LEFT_B      - Rear Hazcam: Left B
     * NAV_LEFT_B       - Navcam: Left B
     * NAV_RIGHT_B      - Navcam: Right B
     * CHEMCAM_RMI      - ChemCam: Remote Micro-Imager
     * MARDI            - Mars Descent Imager
     * MAHLI            - Mars Hand Lens Imager
     * MAST_LEFT        - Mastcam: Left
     * MAST_RIGHT       - Mastcam: Right
     **/
    String CAMERA = "RHAZ_LEFT_A";

    /**
     * full - hazcams, chemcam, MARDI
     * subframe - some hazcams, mast, mahli
     * thumb - all
     */
    String SAMPLETYPE = "full";

    /**
     * -23 equals a decent shot at the horizon
     * -50 equals looking at the ground
     */
    int MAST_EL = 0;


    public void run() throws IOException {

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
        //get image_manifest json
        JSONArray solsArr = obj.getJSONArray("sols");

        //loop through each sol for images from that day
        //solsArr.length()
        for (int i = 0; i < 100; i++) {

            JSONObject sol = solsArr.getJSONObject(i);

            // go into each sol individually and scrape some images from
            readSolImgs(sol.getString("catalog_url"), i);
        }
    }

    // the method that reads the json file for each sol
    // grabs images based on criteria that can be changed in variables
    private void readSolImgs(String catlogurl, int solDay) throws IOException {



        URL url = new URL(catlogurl); //get link to json with images
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream("tempJSON.txt");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        Scanner scanner = new Scanner( new File("tempJSON.txt") );
        String text = scanner.useDelimiter("\\A").next();
        scanner.close(); // Put this call in a finally block



        // build a JSON object with Sol json
        JSONObject obj2 = new JSONObject(text);
        JSONArray solXIMGS = obj2.getJSONArray("images"); // get the array with all images


        //loop through images from sol
        for (int j = 0; j < solXIMGS.length(); j++) {
            //System.out.println("ImgNum: " + j);
            JSONObject imageX = solXIMGS.getJSONObject(j);

            //grab image from this sol if these values hold true
            // || imageX.getString("sampleType").equals("subframe")
            if ((imageX.getString("instrument").equals(CAMERA) ||
                    imageX.getString("instrument").equals("RHAZ_LEFT_B") ||
                    imageX.getString("instrument").equals("RHAZ_RIGHT_A") ||
                    imageX.getString("instrument").equals("RHAZ_RIGHT_B"))&&
                imageX.getString("sampleType").equals(SAMPLETYPE)   ) {

                //save the image to folder
                saveImage(imageX.getString("urlList"), Integer.toString(solDay));
                break;
            }


        }

    }

    private void saveImage(String imageUrl, String sol) throws IOException {
        URL url = new URL(imageUrl);
        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1!=(n=in.read(buf)))
        {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();


        System.out.println("Sol: " + sol);

        FileOutputStream fos = new FileOutputStream("E:\\mars_imgs\\"+ CAMERA +"\\" + sol +".jpg");
        fos.write(response);
        fos.close();
    }
}
