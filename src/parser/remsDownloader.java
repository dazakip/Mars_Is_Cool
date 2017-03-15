package parser;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Dazak on 14/03/2017.
 */
public class remsDownloader {

    String baseURL ="http://pds-atmospheres.nmsu.edu/PDS/data/mslrem_1001/DATA/";

    public void run() throws IOException {

        Document doc = Jsoup.connect(baseURL).get();
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            if (link.text().equals("Name") ||
                    link.text().equals("Last modified") ||
                    link.text().equals("Size") ||
                    link.text().equals("Description") ||
                    link.text().equals("Parent Directory")) {

            } else {
                solChunk(link.attr("href"));
            }
        }
    }

    private void solChunk(String solFolders) throws IOException {
        String url = baseURL + solFolders;
        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
            for (Element link : links) {

                if (link.text().equals("Name") ||
                    link.text().equals("Last modified") ||
                    link.text().equals("Size") ||
                    link.text().equals("Description") ||
                    link.text().equals("Parent Directory")) {

                } else {
                solFolder(url, link.attr("href"));
                }
            }

    }


    private void solFolder(String urlsofar, String solFolder) throws IOException {
        String url = urlsofar + solFolder;
        String sol = solFolder.substring(4,8);
        sol = sol.replaceFirst("^0+(?!$)", "");

        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String currFile = link.attr("href");
            if (link.text().equals("Name") ||
                    link.text().equals("Last modified") ||
                    link.text().equals("Size") ||
                    link.text().equals("Description") ||
                    link.text().equals("Parent Directory")) {

            } else {
                ifFile(currFile, url+link.attr("href"), sol);
                //SAVE TO A SOL FOLDER
               //System.out.println(url + link.attr("href"));
            }
        }
    }

    private void ifFile(String fullName, String fullURL, String sol) throws IOException {
        //check if tab file
        String type = fullName.substring(fullName.length()-3, fullName.length());
        String threeChars = fullName.substring(13,16);

        if (type.equals("TAB")) {
            //if (threeChars.equals("ADR"))
                //saveADR(fullURL, sol);

            if (threeChars.equals("RMD"))
                saveRMD(fullURL, sol);
        }
    }

    private void saveADR(String url, String sol) throws IOException {
        File f = new File("C:\\Users\\Dazak\\Desktop\\instr_data\\ADR\\"+sol+".txt");
        URL u = new URL(url);
        FileUtils.copyURLToFile(u, f);
    }

    private void saveRMD(String url, String sol) throws IOException {
        File f = new File("C:\\Users\\Dazak\\Desktop\\instr_data\\RMD\\"+sol+".txt");
        URL u = new URL(url);
        FileUtils.copyURLToFile(u, f);
    }
}
