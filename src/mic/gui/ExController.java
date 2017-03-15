package mic.gui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
    private TextField solTextField;
    @FXML
    private Label mainView;
    @FXML
    private WebView map;
    private WebEngine webEngine;
    @FXML
    private Slider solSlider;
    @FXML
    private Button playpause, forwardStep, backwardStep, addMarker;
    @FXML
    private LineChart lineChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    // fields critical to control flow
    private ImageLocations imgLocs;
    private int currentSol;
    private ScheduledExecutorService execService;
    private Future<?> future;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor is called before the initialize() method.
     */
    public ExController() {
        currentSol = -1;
        execService = Executors.newScheduledThreadPool(1);
        cameras = new ArrayList<>();
        imgLocs = new ImageLocations();
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

        playScheduler(false);
        establishListeners();
        createLineChart();
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
    }

    /**
     * Method contain all the various listeners used in the application, called from initilize()
     * grouped together to easily find/hide away
     */
    private void establishListeners() {
        //sol slider listener
        solSlider.valueProperty().addListener((arg0, arg1, arg2) -> {
            solTextField.setText(Integer.toString((int) solSlider.getValue()));
            currentSol = (int) solSlider.getValue();
            updateCamera();
        });

        // text field showing the current sol listener
        solTextField.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (newPropertyValue){
                pauseScheduler();
            } else {
                currentSol = Integer.parseInt(solTextField.getText());
                playScheduler(true);
            }
        });

        // button that play/pauses the system
        playpause.setOnAction(event -> {
            if (playpause.getText().equals("Play")) {
                playScheduler(true);
            } else {
                pauseScheduler();
            }
        });

        // button that move system 1 sol forward
        forwardStep.setOnAction(event -> {
            pauseScheduler();
            loop();
        });

        // button that move system 1 sol back
        backwardStep.setOnAction(event -> {
            pauseScheduler();
            currentSol = currentSol-2;
            loop();
        });

        // add cameras to arraylist
        cameras.add(FRONTHAZ_th);
        cameras.add(REARHAZ_th);
        cameras.add(LEFTHAZ_th);
        cameras.add(RIGHTHAZ_th);
        cameras.add(MAST_th);
        cameras.add(MAHLI_th);
        cameras.add(MARDI_th);
        cameras.add(CHEM_th);
        cameras.add(CURATED_th);

        // add listener to all cameras that updates the main view with the thumbnail view clicked
        for (ImageView cam:cameras){
            cam.setOnMouseClicked(event -> {
                String camalam = cam.getId().substring(0, cam.getId().length() - 3);
                imgLocs.setSelectedView(camalam);
                mainView.setText(imgLocs.getCamLabel(camalam));
                updateCamera();
            });
        }

        /**************************************************************************************************************
         * Button used for various tests ---------------- DELETE AT THE END
         */
        addMarker.setOnAction(event ->
                runLater(() -> webEngine.executeScript("panToCuriosity();")));
        /**************************************************************************************************************/
    }

    private void createLineChart(){
        lineChart.setTitle("Stock Monitoring, 2010");
        xAxis.setLabel("TIMEEEEEEEEEE");
        yAxis.setLabel("DATA REEEEEEE");
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("My portfolio");
        xAxis.setLabel("DATA WEEEEWWWWW");
        yAxis.setLabel("TIMEEEE");

        //populating the series with data

        series.getData().add(new XYChart.Data(1, 23));
        series.getData().add(new XYChart.Data(2, 14));
        series.getData().add(new XYChart.Data(3, 15));
        series.getData().add(new XYChart.Data(4, 24));
        series.getData().add(new XYChart.Data(5, 34));
        series.getData().add(new XYChart.Data(6, 36));
        series.getData().add(new XYChart.Data(7, 22));
        series.getData().add(new XYChart.Data(8, 45));
        series.getData().add(new XYChart.Data(9, 43));
        series.getData().add(new XYChart.Data(10, 17));
        series.getData().add(new XYChart.Data(11, 29));
        series.getData().add(new XYChart.Data(12, 25));
        lineChart.getData().add(series);
    }
    /**
     * Used whenever program is paused is to be resumed
     */
    private void playScheduler(boolean immediately) {
        long delay = 3000L;
        // for restarting scheduler quickly when changing sol/pressing play
        if (immediately)
            delay = 500L;

        future = execService.scheduleAtFixedRate(()->{
            //The repetitive task
            if (currentSol<=1500)
                loop();
        }, delay, 1000L, TimeUnit.MILLISECONDS);
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
     * Called each "round" to update image views with images relative to sol
     */
    private void updateCamera() {

        if (new File(imgLocs.getSelectedView() + currentSol + ".jpg").isFile()) {
            MAINVIEW.setOpacity(1);
            MAINVIEW.setImage(new Image("file:" + imgLocs.getSelectedView() + currentSol + ".jpg"));
        }
        else {
            MAINVIEW.setOpacity(0.75);
        }

        // for each thumbnail view update
        for (ImageView currCam : cameras) {
            if (new File(imgLocs.getCamera(currCam.getId()) + "\\" + currentSol + ".jpg").isFile()) {
                currCam.setOpacity(1);
                currCam.setImage(new Image("file:"+imgLocs.getCamera(currCam.getId())+"\\" + currentSol + ".jpg"));
            }
            else {currCam.setOpacity(0.75);}
        }
    }

    /**
     * Is called by the main application to give a reference back to itself.
     */
    public void setMainApp(MainApp mainApp) {this.mainApp = mainApp;}
}