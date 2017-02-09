package curiosity_img_data_parser;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by Dazak on 08/02/2017.
 */
public class jsonParser {
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
        //get array of sols
        JSONArray solsArr = obj.getJSONArray("sols");

        //loop through each sol for images from that day
        //solsArr.length()
        for (int i = 0; i < 100; i++) {

            System.out.println("Sol: " + i);
            JSONObject sol = solsArr.getJSONObject(i);

            readSolImgs(sol.getString("catalog_url"), i);
        }
    }

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
            if (imageX.getString("instrument").equals("NAV_LEFT_A") &&
                    imageX.getString("sampleType").equals("full")) {

                System.out.println("-ImgNum: " + j);
                System.out.println("-Intrument: " + imageX.getString("instrument") + "Type: " + imageX.getString("sampleType"));

                saveImage(imageX.getString("urlList"), Integer.toString(j), Integer.toString(solDay));
            }


        }

    }

    private void saveImage(String imageUrl, String destinationFile, String sol) throws IOException {
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

        new File("C:\\Users\\Dazak\\Desktop\\imgs\\" + sol).mkdir();
        FileOutputStream fos = new FileOutputStream("C:\\Users\\Dazak\\Desktop\\imgs\\" + sol + "\\" + destinationFile + ".jpg");
        fos.write(response);
        fos.close();
    }
}
