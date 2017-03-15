package mic.gui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView ;

import static javafx.application.Platform.runLater;

/**
 * ExController handles all the logic for MIC.
 */
public class ExController {
    @FXML
    private ImageView   MAINVIEW, FRONTHAZ_th, REARHAZ_th, LEFTHAZ_th, RIGHTHAZ_th,
                        MAST_th, MAHLI_th, MARDI_th, CHEM_th, CURATED_th;
    private ArrayList<ImageView> cameras;
    @FXML
    private Label solLabel;
    @FXML
    private TextField solTextField;
    @FXML
    private WebView map;
    private WebEngine webEngine;
    @FXML
    private Slider solSlider;
    @FXML
    private Button playpause, forwardStep, backwardStep, addMarker;

    private String imagePath;
    private int currentSol;
    private ScheduledExecutorService execService;
    private Future<?> future;

    private String selectedViewPath;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor is called before the initialize() method.
     */
    public ExController() {
        currentSol = -1;
        execService = Executors.newScheduledThreadPool(1);
        cameras = new ArrayList<ImageView>();
        imagePath = "C:\\Users\\Dazak\\Desktop\\mic_imgs\\";
        selectedViewPath = imagePath+"LEFTHAZ\\";
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        // grab mars map and laod into WebView
        URL url = getClass().getResource("map/openlayers_mic.html");

        map.getEngine().load(url.toExternalForm());
        webEngine = map.getEngine();
        webEngine.load(url.toExternalForm());


        // add cameras to arraylist
        cameras.add(FRONTHAZ_th);
        cameras.add(REARHAZ_th);
        cameras.add(LEFTHAZ_th);
        cameras.add(RIGHTHAZ_th);
        cameras.add(MAST_th);
        cameras.add(MAHLI_th);
        cameras.add(MARDI_th);
        cameras.add(CHEM_th);

        playScheduler();

        for (ImageView cam:cameras){
            cam.setOnMouseClicked(event -> {
                selectedViewPath = imagePath + cam.getId().substring(0, cam.getId().length() - 3)+"\\";
                updateCamera();
            });
        }

        //slider listener
        solSlider.valueProperty().addListener((arg0, arg1, arg2) -> {

            solTextField.setText(Integer.toString((int) solSlider.getValue()));
            currentSol = (int) solSlider.getValue();
            updateCamera();


        });

        solTextField.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue)
            {
                pauseScheduler();
            }
            else
            {
                currentSol = Integer.parseInt(solTextField.getText());
                playScheduler();
            }
        });

        addMarker.setOnAction(event -> {
            runLater(() -> {
                webEngine.executeScript("panToCuriosity();");
            });
        });

        playpause.setOnAction(event -> {
            if (playpause.getText().equals("Play")) {
                playScheduler();
            }
            else {
                pauseScheduler();
            }
        });

        forwardStep.setOnAction(event -> {
            pauseScheduler();
            loop();
        });

        backwardStep.setOnAction(event -> {
            pauseScheduler();
            currentSol = currentSol-2;
            loop();
        });
    }

    /**
     * Used whenever program is paused is to be resumed
     */
    private void playScheduler() {
        future = execService.scheduleAtFixedRate(()->{
            //The repetitive task
            if (currentSol<=1500)
                loop();
        }, 3000L, 1000L, TimeUnit.MILLISECONDS);
        playpause.setText("Pause");
    }

    /**
     * Used whenever program is to be paused
     * Even though is single line makes it easier to understand
     * code/more workable if any changes in future
     */
    private void pauseScheduler() {
        future.cancel(true);
        playpause.setText("Play");
    }

    /**
     * Method that is called every however often defined in the scheduler
     */
    private void loop() {

        currentSol++;
        updateCamera();
        runLater(() -> {
            solTextField.setText(Integer.toString(currentSol));
            solSlider.setValue(currentSol);
            webEngine.executeScript("highlightPath("+currentSol+");");
        });
        //webEngine.executeScript("highlightSolLine();");
        //addMarker.fire();
    }

    /**
     * Called each "round" to update image views with images relative to sol
     */
    private void updateCamera() {


        if (new File(selectedViewPath + currentSol + ".jpg").isFile()) {
            MAINVIEW.setOpacity(1);
            MAINVIEW.setImage(new Image("file:" + selectedViewPath + currentSol + ".jpg"));
        }
        else {
            MAINVIEW.setOpacity(0.75);
        }

        // for each cam view update view
        for (ImageView currCam : cameras) {
            if (new File(imagePath + currCam.getId() + "\\" + currentSol + ".jpg").isFile()) {
                currCam.setOpacity(1);
                currCam.setImage(new Image("file:"+imagePath+currCam.getId()+"\\" + currentSol + ".jpg"));
            }
            else {currCam.setOpacity(0.75);}
        }
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {this.mainApp = mainApp;}
}