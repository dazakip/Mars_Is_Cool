package example;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView ;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javafx.scene.image.Image;
import javafx.scene.text.Text;

import static java.util.concurrent.TimeUnit.SECONDS;

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

    private int sol = 0;



    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public ExController() {
        System.out.println("oioi");
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        //for (int sol = 0; sol <=100;sol++) {
        int sol = 0;

       // }

        ScheduledExecutorService execService = Executors.newScheduledThreadPool(5);

        execService.scheduleAtFixedRate(()->{
            //The repetitive task
            loop();

        }, 0, 650L, TimeUnit.MILLISECONDS);




    }

    private void loop() {

        FRONTHAZ.setImage(new Image("file:C:\\Users\\Dazak\\Desktop\\mars_imgs\\FHAZ_LEFT_A\\" + sol + ".jpg"));
        REARHAZ.setImage(new Image("file:C:\\Users\\Dazak\\Desktop\\mars_imgs\\RHAZ_LEFT_A\\" + sol + ".jpg"));
        LEFTHAZ.setImage(new Image("file:C:\\Users\\Dazak\\Desktop\\mars_imgs\\NAV_LEFT_A\\" + sol + ".jpg"));
        RIGHTHAZ.setImage(new Image("file:C:\\Users\\Dazak\\Desktop\\mars_imgs\\NAV_RIGHT_A\\" + sol + ".jpg"));
        MAHLI.setImage(new Image("file:C:\\Users\\Dazak\\Desktop\\mars_imgs\\MAHLI\\" + sol + ".jpg"));
        MAST.setImage(new Image("file:C:\\Users\\Dazak\\Desktop\\mars_imgs\\MAST_LEFT\\" + sol + ".jpg"));

        Platform.runLater(
                () -> {
                    numSol.setText("Sol: "+ Integer.toString(sol));
                }
        );
            sol++;

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