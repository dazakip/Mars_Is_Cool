package mic.controller;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
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
import mic.model.CuriosityDatabase;
import mic.model.DataEntry;
import mic.model.ImageLocations;

import static javafx.application.Platform.runLater;
import static javafx.print.PrintColor.COLOR;

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
    private Label mainView, zeroth, first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth;
    @FXML
    private WebView map;
    private WebEngine webEngine;
    @FXML
    private Slider solSlider;
    @FXML
    private Button playpause, forwardStep, backwardStep, addMarker;
    @FXML
    private LineChart elevChart, tempChart, uvaChart, humidChart, pressChart;
    @FXML
    private NumberAxis elevX, elevY, tempX, tempY, uvaX, uvaY, humidX, humidY, pressX, pressY;
    @FXML
    private ToggleButton tempToggle, elevToggle, humidToggle, uvaToggle, pressToggle;
    private ArrayList<ToggleButton> toggleButtons;
    private ArrayList<LineChart> lineCharts;
    @FXML
    private ChoiceBox chooseAxisY;

    private ArrayList<NumberAxis> yAxises;

    private XYChart.Series elevSeries;
    private XYChart.Series tempSeries;
    private XYChart.Series uvaSeries;
    private XYChart.Series humidSeries;
    private XYChart.Series pressSeries;

    // fields critical to control flow
    private ImageLocations imgLocs;
    private CuriosityDatabase db;
    private int currentSol;
    private int solSeconds;
    private int solsOnGraph;
    private ScheduledExecutorService execService;
    private Future<?> future;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor is called before the initialize() method.
     */
    public ExController() throws SQLException, ClassNotFoundException {
        currentSol = -1;
        solSeconds = 88765;
        solsOnGraph = 10;
        execService = Executors.newScheduledThreadPool(1);
        cameras = new ArrayList<>();
        imgLocs = new ImageLocations();
        db = new CuriosityDatabase();
        yAxises = new ArrayList<>();
        toggleButtons = new ArrayList<>();
        lineCharts = new ArrayList<>();
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // grab mars map and laod into WebView
        URL url = getClass().getResource("/mic/view/map/openlayers_mic.html");
        map.getEngine().load(url.toExternalForm());
        webEngine = map.getEngine();
        webEngine.load(url.toExternalForm());

        playScheduler(false);
        establishListeners();
        createCharts();
    }

    /**
     * Method that is called every however often defined in the scheduler
     */
    private void loop() throws SQLException, ClassNotFoundException {

        currentSol++;
        runLater(() -> {
            updateCamera();
            try {
                updateLineChart();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
            try {
                loop();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        // button that move system 1 sol back
        backwardStep.setOnAction(event -> {
            pauseScheduler();
            currentSol = currentSol-2;
            try {
                loop();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
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

        chooseAxisY.valueProperty().addListener((ov, t, t1) -> {
                changeAxisY(t1.toString());
        });

        toggleButtons.add(tempToggle);
        toggleButtons.add(elevToggle);
        toggleButtons.add(pressToggle);
        toggleButtons.add(uvaToggle);
        toggleButtons.add(humidToggle);

        for (ToggleButton tb:toggleButtons) {
            tb.setOnAction(event -> {
                if (tb.isSelected()) {
                    changeLineVisibility(tb.getId(), true);
                }
                else
                    changeLineVisibility(tb.getId(), false);
            });
        }

        /**************************************************************************************************************
         * Button used for various tests ---------------- DELETE AT THE END
         */
        addMarker.setOnAction(event ->
                runLater(() -> webEngine.executeScript("panToCuriosity();")));
        /**************************************************************************************************************/
    }

    private void changeLineVisibility(String line, boolean visible){
        if (visible) {
            if (line.equals("tempToggle")) {
                tempChart.setOpacity(1);
            }
            if (line.equals("elevToggle")) {
                elevChart.setOpacity(1);
            }
            if (line.equals("uvaToggle")) {
                uvaChart.setOpacity(1);
            }
            if (line.equals("humidToggle")) {
                humidChart.setOpacity(1);
            }
            if (line.equals("pressToggle")) {
                pressChart.setOpacity(1);
            }
        }

        else {
            System.out.println(line);
            if (line.equals("tempToggle")) { tempChart.setOpacity(0); }
            if (line.equals("elevToggle")) { elevChart.setOpacity(0); }
            if (line.equals("uvaToggle")) { uvaChart.setOpacity(0); }
            if (line.equals("humidToggle")) { humidChart.setOpacity(0); }
            if (line.equals("pressToggle")) { pressChart.setOpacity(0); }
        }
    }

    private void changeAxisY(String newY) {

        for (NumberAxis y:yAxises)
            y.setOpacity(0);

        if (newY.equals("Temperature")) { tempY.setOpacity(1);}
        if (newY.equals("UVA")) { uvaY.setOpacity(1);}
        if (newY.equals("Pressure")) { pressY.setOpacity(1); }
        if (newY.equals("Humidity")) { humidY.setOpacity(1);}
        if (newY.equals("Elevation")) { elevY.setOpacity(1);}
    }

    private void changeChartLabels() {
        if (currentSol>=9) {tenth.setTextFill(Color.RED);}

        if (currentSol>=solsOnGraph) {
            zeroth.setText("Sol " + (currentSol - 10));
            first.setText("Sol " + (currentSol - 9));
            second.setText("Sol " + (currentSol - 8));
            third.setText("Sol " + (currentSol - 7));
            fourth.setText("Sol " + (currentSol - 6));
            fifth.setText("Sol " + (currentSol - 5));
            sixth.setText("Sol " + (currentSol - 4));
            seventh.setText("Sol " + (currentSol - 3));
            eighth.setText("Sol " + (currentSol - 2));
            ninth.setText("Sol " + (currentSol - 1));
            tenth.setText("Sol " + currentSol);
        }
    }

    private void createCharts(){
        //temperature
        tempY.setAutoRanging(false);
        tempY.setUpperBound(0);
        tempY.setLowerBound(-100.00);
        tempY.setTickUnit(10);
        tempX.setAutoRanging(false);
        tempX.setUpperBound(solSeconds*solsOnGraph);
        tempX.setLowerBound(0);
        tempX.setTickUnit(solSeconds);

        //elevation
        elevY.setAutoRanging(false);
        elevY.setUpperBound(175.00);
        elevY.setLowerBound(-25.00);
        elevY.setTickUnit(25);
        elevX.setAutoRanging(false);
        elevX.setUpperBound(solSeconds*solsOnGraph);
        elevX.setLowerBound(0);
        elevX.setTickUnit(solSeconds);

        //uva
        uvaY.setAutoRanging(false);
        uvaY.setUpperBound(11);
        uvaY.setLowerBound(0);
        uvaY.setTickUnit(1);
        uvaX.setAutoRanging(false);
        uvaX.setUpperBound(solSeconds*solsOnGraph);
        uvaX.setLowerBound(0);
        uvaX.setTickUnit(solSeconds);

        //pressure
        pressY.setAutoRanging(false);
        pressY.setUpperBound(4.9);
        pressY.setLowerBound(2.7);
        pressY.setTickUnit(0.1);
        pressX.setAutoRanging(false);
        pressX.setUpperBound(solSeconds*solsOnGraph);
        pressX.setLowerBound(0);
        pressX.setTickUnit(solSeconds);

        //humid
        humidY.setAutoRanging(false);
        humidY.setUpperBound(50);
        humidY.setLowerBound(0);
        humidY.setTickUnit(1);
        humidX.setAutoRanging(false);
        humidX.setUpperBound(solSeconds*solsOnGraph);
        humidX.setLowerBound(0);
        humidX.setTickUnit(solSeconds);

        elevChart.getYAxis().setOpacity(0);
        pressChart.getYAxis().setOpacity(0);
        humidChart.getYAxis().setOpacity(0);
        uvaChart.getYAxis().setOpacity(0);

        tempChart.getXAxis().setOpacity(0);
        elevChart.getXAxis().setOpacity(0);
        pressChart.getXAxis().setOpacity(0);
        humidChart.getXAxis().setOpacity(0);
        uvaChart.getXAxis().setOpacity(0);

        tempToggle.setSelected(true);
        elevToggle.setSelected(true);
        uvaToggle.setSelected(true);
        pressToggle.setSelected(true);
        humidToggle.setSelected(true);

        chooseAxisY.setItems(FXCollections.observableArrayList(
                "Temperature", "UVA", "Humidity", "Elevation", "Pressure")
        );
        chooseAxisY.setTooltip(new Tooltip("Change the Y Axis"));
        chooseAxisY.setValue("Temperature");

        yAxises.add(tempY);
        yAxises.add(elevY);
        yAxises.add(pressY);
        yAxises.add(uvaY);
        yAxises.add(humidY);

    }

    private void updateLineChart() throws SQLException {

        tempChart.getData().remove(tempSeries);
        elevChart.getData().remove(elevSeries);
        uvaChart.getData().remove(uvaSeries);
        pressChart.getData().remove(pressSeries);
        humidChart.getData().remove(humidSeries);
        tempSeries = new XYChart.Series();
        elevSeries = new XYChart.Series();
        uvaSeries = new XYChart.Series();
        pressSeries = new XYChart.Series();
        humidSeries = new XYChart.Series();

        db.nextSol(currentSol);
        changeChartLabels();

        if (currentSol >=solsOnGraph) {
            tempX.setUpperBound((currentSol * solSeconds));
            tempX.setLowerBound((currentSol * solSeconds) - (solSeconds*solsOnGraph));
            elevX.setUpperBound((currentSol * solSeconds));
            elevX.setLowerBound((currentSol * solSeconds) - (solSeconds*solsOnGraph));
            uvaX.setUpperBound((currentSol * solSeconds));
            uvaX.setLowerBound((currentSol * solSeconds) - (solSeconds*solsOnGraph));
            pressX.setUpperBound((currentSol * solSeconds));
            pressX.setLowerBound((currentSol * solSeconds) - (solSeconds*solsOnGraph));
            humidX.setUpperBound((currentSol * solSeconds));
            humidX.setLowerBound((currentSol * solSeconds) - (solSeconds*solsOnGraph));
            db.updateEntries(tempX.getLowerBound(), tempX.getUpperBound());
        }


        System.out.println(db.getDataEntries().size());
        for (DataEntry d : db.getDataEntries()) {
            if (d.getAirTemp()!=11111) {tempSeries.getData().add(new XYChart.Data(d.getTimeStamp(), d.getAirTemp())); }
            if (d.getElevation()!=11111) {elevSeries.getData().add(new XYChart.Data(d.getTimeStamp(), d.getElevation()));}
            if (d.getUVA()!=11111){uvaSeries.getData().add(new XYChart.Data(d.getTimeStamp(), d.getUVA()));}
            if (d.getPressure()!=11111){pressSeries.getData().add(new XYChart.Data(d.getTimeStamp(), d.getPressure()));}
            if (d.getHumidity()!=11111){humidSeries.getData().add(new XYChart.Data(d.getTimeStamp(), d.getHumidity()));}
        }


        elevChart.getData().add(elevSeries);
        tempChart.getData().add(tempSeries);
        uvaChart.getData().add(uvaSeries);
        pressChart.getData().add(pressSeries);
        humidChart.getData().add(humidSeries);
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
                try {
                    loop();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
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