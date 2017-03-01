package mic.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView ;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class ExController {
    @FXML
    private ImageView FRONTHAZ;
    @FXML
    private ImageView REARHAZ;
    @FXML
    private ImageView LEFTHAZ;
    @FXML
    private ImageView RIGHTHAZ;
    @FXML
    private ImageView MAST;
    @FXML
    private ImageView MAHLI;
    @FXML
    private javafx.scene.control.Label numSol;

    @FXML
    private WebView map;
    private WebEngine webEngine;

    @FXML
    private Slider solSlider;

    private int sol;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public ExController() {
        sol = 0;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        URL url = getClass().getResource("old_map_stuff/openlayers.html");
        map.getEngine().load(url.toExternalForm());
        webEngine = map.getEngine();

        // load the web page
        webEngine.load(url.toExternalForm());

        ScheduledExecutorService execService = Executors.newScheduledThreadPool(5);

        execService.scheduleAtFixedRate(()->{
            //The repetitive task
            if (sol<=100)
                loop();

        }, 0, 1000L, TimeUnit.MILLISECONDS);




    }

    private void loop() {

        updateCamera();

        solSlider.setValue(sol);
        //update sol label
        Platform.runLater(
                () -> {
                    numSol.setText("Sol: "+ Integer.toString(sol));
                }
        );
            sol++;

    }

    private void updateCamera() {

        if (new File("C:\\Users\\Dazak\\Desktop\\mars_imgs\\FHAZ_LEFT_A\\" + sol + ".jpg").isFile()) {
            FRONTHAZ.setOpacity(1);
            FRONTHAZ.setImage(new Image("file:C:\\Users\\Dazak\\Desktop\\mars_imgs\\FHAZ_LEFT_A\\" + sol + ".jpg"));
        }
        else
            FRONTHAZ.setOpacity(0.5);

        if (new File("C:\\Users\\Dazak\\Desktop\\mars_imgs\\RHAZ_LEFT_A\\" + sol + ".jpg").isFile()) {
            REARHAZ.setOpacity(1);
            REARHAZ.setImage(new Image("file:C:\\Users\\Dazak\\Desktop\\mars_imgs\\RHAZ_LEFT_A\\" + sol + ".jpg"));
        }
        else
            REARHAZ.setOpacity(0.5);

        if (new File("C:\\Users\\Dazak\\Desktop\\mars_imgs\\NAV_LEFT_A\\" + sol + ".jpg").isFile()){
            LEFTHAZ.setOpacity(1);
            LEFTHAZ.setImage(new Image("file:C:\\Users\\Dazak\\Desktop\\mars_imgs\\NAV_LEFT_A\\" + sol + ".jpg"));
        }
        else
            LEFTHAZ.setOpacity(0.5);


        if (new File("C:\\Users\\Dazak\\Desktop\\mars_imgs\\NAV_RIGHT_A\\" + sol + ".jpg").isFile()){
            RIGHTHAZ.setOpacity(1);
            RIGHTHAZ.setImage(new Image("file:C:\\Users\\Dazak\\Desktop\\mars_imgs\\NAV_RIGHT_A\\" + sol + ".jpg"));
        }
        else
            RIGHTHAZ.setOpacity(0.5);


        if (new File("C:\\Users\\Dazak\\Desktop\\mars_imgs\\MAHLI\\" + sol + ".jpg").isFile()){
            MAHLI.setOpacity(1);
            MAHLI.setImage(new Image("file:C:\\Users\\Dazak\\Desktop\\mars_imgs\\MAHLI\\" + sol + ".jpg"));
        }
        else
            MAHLI.setOpacity(0.5);


        if (new File("C:\\Users\\Dazak\\Desktop\\mars_imgs\\MAST_LEFT\\" + sol + ".jpg").isFile()){
            MAST.setOpacity(1);
            MAST.setImage(new Image("file:C:\\Users\\Dazak\\Desktop\\mars_imgs\\MAST_LEFT\\" + sol + ".jpg"));
        }
        else
            MAST.setOpacity(0.5);
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }
}