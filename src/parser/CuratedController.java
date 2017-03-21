package parser;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.image.ImageView ;
import mic.controller.MainApp;
import mic.model.CuriosityDatabase;
import mic.model.DataEntry;
import mic.model.ImageLocations;

import static javafx.application.Platform.runLater;
import static javafx.print.PrintColor.COLOR;

/**
 * Controller handles all the logic for MIC.
 */
public class CuratedController {
    @FXML
    private ImageView  LEFTHAZ, RIGHTHAZ, REARHAZ, FRONTHAZ, CHEM, MAHLI, MAST, MARDI;
    private ArrayList<ImageView> cameras;
    private int currentSol;
    private String imgLocs;
    @FXML
    private Label currSol;

    Connection c;
    Statement stmt;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor is called before the initialize() method.
     */
    public CuratedController() throws SQLException, ClassNotFoundException {
        currentSol = 87;
        imgLocs = "C:\\Users\\Dazak\\Desktop\\mic_imgs\\";
        cameras = new ArrayList<>();
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() throws ClassNotFoundException, SQLException {

        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:CuratedView.db");
        c.setAutoCommit(false);
        System.out.println("Opened database successfully");

        // add cameras to arraylist
        cameras.add(FRONTHAZ);
        cameras.add(REARHAZ);
        cameras.add(LEFTHAZ);
        cameras.add(RIGHTHAZ);
        cameras.add(MAST);
        cameras.add(MAHLI);
        cameras.add(MARDI);
        cameras.add(CHEM);

        updateCameras();

        // add listener to all cameras that updates the main view with the thumbnail view clicked
        for (ImageView cam:cameras){
            cam.setOnMouseClicked(event -> {
                try {
                    addEntry(cam.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                currentSol = currentSol+1;
                updateCameras();
                currSol.setText("Sol: " +currentSol);
            });
        }
    }

    public void updateCameras() {
        // for each thumbnail view update
        for (ImageView currCam : cameras) {
            if (new File((imgLocs +"\\"+currCam.getId()) + "\\" + currentSol + ".jpg").isFile()) {
                currCam.setOpacity(1);
                currCam.setImage(new Image("file:"+imgLocs +"\\"+currCam.getId()+"\\" + currentSol + ".jpg"));
            }
            else {currCam.setOpacity(0);}
        }
    }

    public void addEntry(String camera) throws SQLException {

        stmt = c.createStatement();
        //String sql = "INSERT INTO LOCATIONS (ARRIV,SITE,LONG,LAT,STARTSOL,ENDSOL) " +
        //      "VALUES ('2008-11-11 13:23:44',1,1,1,1,1)";

        String sql = "INSERT INTO CURATED (SOL, CAMERA) " +
                " VALUES("+currentSol+",'"+camera+"')";
        stmt.executeUpdate(sql);
        stmt.close();
        c.commit();
    }


    /**
     * Is called by the main application to give a reference back to itself.
     */
    public void setMainApp(MainApp mainApp) {this.mainApp = mainApp;}
}