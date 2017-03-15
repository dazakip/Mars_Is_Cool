package parser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
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

    /**
     * full - hazcams, chemcam, MARDI
     * subframe - some hazcams, mast, mahli
     * thumb - all

    /**
     * -23 equals a decent shot at the horizon
     * -50 equals looking at the ground
     */
    double MAST_EL = -23.0;
    private ArrayList<Integer> usefullArrayList = new ArrayList<Integer>();

    private double currBestEl;
    private String currBestURL;

    /*
    *  This method reads the overall JSON and splits into each
    *  Sol's image feed via readSolImgs();
    * */
    public void run() throws IOException {

        // build a URL & read from
        URL url = new URL("http://mars.jpl.nasa.gov/msl-raw-images/image/image_manifest.json");
        Scanner scan = new Scanner(url.openStream());
        String str = new String();
        while (scan.hasNext())
            str += scan.nextLine();
        scan.close();

        //System.out.println(str);
        //System.exit(0);

        // build a JSON object
        JSONObject obj = new JSONObject(str);
        //get image_manifest json
        JSONArray solsArr = obj.getJSONArray("sols");

        String cat_url;
        //loop through each sol for images from that day
        //solsArr.length()

        for (int i = 0; i <= 1462; i++) {
            //cat_url = "http://mars.jpl.nasa.gov/msl-raw-images/image/images_sol"+i+".json";
            //readSolImgs(cat_url, i);
            JSONObject sol = solsArr.getJSONObject(i);
            readSolImgs(sol.getString("catalog_url"), i);
        }

        for (int i = 1462; i <= 1500; i++) {
            cat_url = "http://mars.jpl.nasa.gov/msl-raw-images/image/images_sol"+i+".json";
            readSolImgs(cat_url, i);
            //JSONObject sol = solsArr.getJSONObject(i);
            //readSolImgs(sol.getString("catalog_url"), i);
        }

       // readSolImgs2();
    }

    // the method that reads the image feed for each sol
    // grabs images based on criteria that can be changed in variables
    private void readSolImgs(String catlogurl, int solDay) throws IOException {

        URL url = new URL(catlogurl); //get link to json with images
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream("tempJSON.txt");
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        Scanner scanner = new Scanner(new File("tempJSON.txt"));
        String text = scanner.useDelimiter("\\A").next();
        scanner.close();

        // build a JSON object with Sol json
        JSONObject obj2 = new JSONObject(text);
        JSONArray solXIMGS = obj2.getJSONArray("images"); // get the array with all images

        currBestEl = -100; // reset best elevation
        //loop through images from sol
        for (int j = 0; j < solXIMGS.length(); j++) {
            //System.out.println("ImgNum: " + j);
            JSONObject imageX = solXIMGS.getJSONObject(j);

            //grab image from this sol if these values hold true
            // || imageX.getString("sampleType").equals("subframe")
            if ((
                    imageX.getString("instrument").equals("MAST_LEFT") ||
                    imageX.getString("instrument").equals("MAST_RIGHT")
            ) &&
                    (
                            imageX.getString("sampleType").equals("subframe")
                    )
                    ) {
                //save the image to folder
                saveImage(imageX.getString("urlList"),Integer.toString(solDay));
                break;
                /*
                if (!imageX.getString("mastEl").equals("UNK")) {
                    double tempEl = Double.parseDouble(imageX.getString("mastEl"));
                    if (tempEl >= currBestEl && tempEl <= 25)
                            {
                        currBestEl = Double.parseDouble(imageX.getString("mastEl"));
                        currBestURL = imageX.getString("urlList");
                        System.out.println(currBestURL);

                    }
                }
                */
            }
        }
/*
        if (currBestURL == null) {
            for (int j = 0; j < solXIMGS.length(); j++) {
                JSONObject imageX = solXIMGS.getJSONObject(j);
                if ((
                        imageX.getString("instrument").equals("MAHLI") ||
                                imageX.getString("instrument").equals("MAHLI")
                ) &&
                        (
                                imageX.getString("sampleType").equals("subframe")
                        )
                        ) {
                    saveImage(imageX.getString("urlList"),Integer.toString(solDay));
                }
            }

        }
        else {
            saveImage(currBestURL, Integer.toString(solDay));

        }
        */

        //System.out.println(currBestURL);
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
        String CAMERA = "MAST";
        FileOutputStream fos = new FileOutputStream("C:\\Users\\Dazak\\Desktop\\mic_imgs\\"+ CAMERA +"\\" + sol +".jpg");
        fos.write(response);
        fos.close();
        currBestURL = null;
    }

    private void readSolImgs2() throws IOException {

        for (int sol : usefullArrayList) {

            URL url = new URL("http://mars.jpl.nasa.gov/msl-raw-images/image/images_sol"+sol+".json"); //get link to json with images
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream("tempJSON.txt");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            Scanner scanner = new Scanner(new File("tempJSON.txt"));
            String text = scanner.useDelimiter("\\A").next();
            scanner.close();

            // build a JSON object with Sol json
            JSONObject obj2 = new JSONObject(text);
            JSONArray solXIMGS = obj2.getJSONArray("images"); // get the array with all images


            //loop through images from sol
            for (int j = 0; j < solXIMGS.length(); j++) {
                //System.out.println("ImgNum: " + j);
                JSONObject imageX = solXIMGS.getJSONObject(j);

                //grab image from this sol if these values hold true
                // || imageX.getString("sampleType").equals("subframe")
                if ((
                        imageX.getString("instrument").equals("CHEMCAM_RMI")
                ) &&
                        (
                                imageX.getString("sampleType").equals("full")
                        )
                        ) { //now do things down here

                    //save the image to folder
                    saveImage(imageX.getString("urlList"), Integer.toString(sol));
                    break;
                }


            }

        }
    }
}
