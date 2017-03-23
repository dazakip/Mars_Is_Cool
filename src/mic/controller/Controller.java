package mic.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.*;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.image.ImageView ;
import mic.model.CuriosityDatabase;
import mic.model.DataEntry;
import mic.model.ImageLocations;
import mic.model.InfoBoxRetrieval;

import static javafx.application.Platform.runLater;

/**
 * Controller handles all the logic for MIC.
 */
public class Controller {
    // Camera related fields
    @FXML
    private Label mainView, zeroth, first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth;
    @FXML
    private ImageView   MAINVIEW, FRONTHAZ_th, REARHAZ_th, LEFTHAZ_th, RIGHTHAZ_th,
                        MAST_th, MAHLI_th, MARDI_th, CHEM_th, CURATED_th;
    private ArrayList<ImageView> cameras;
    @FXML
    private Label outdatedLabel;

    // Map realted fields
    @FXML
    private WebView mapGale;
    private WebEngine webEngineGale;
    @FXML
    private WebView mapGlobal;
    private WebEngine webEngineGlobal;
    @FXML
    private ImageView mapSwitch;

    // Line chart related fields
    @FXML
    private LineChart elevChart, tempChart, uvaChart, humidChart, pressChart;
    private ArrayList<LineChart> lineCharts;
    private XYChart.Series elevSeries;
    private XYChart.Series tempSeries;
    private XYChart.Series uvaSeries;
    private XYChart.Series humidSeries;
    private XYChart.Series pressSeries;
    @FXML
    private NumberAxis elevX, elevY, tempX, tempY, uvaX, uvaY, humidX, humidY, pressX, pressY;
    private ArrayList<NumberAxis> yAxises;
    @FXML
    private Label yAxisLabel;
    @FXML
    private ChoiceBox chooseAxisY;
    @FXML
    private ToggleButton tempToggle, elevToggle, humidToggle, uvaToggle, pressToggle;
    private ArrayList<ToggleButton> toggleButtons;

    // Sol Controlling related fields
    @FXML
    private Label solNumber;
    @FXML
    private TextArea infoboxDesc;
    @FXML
    private Slider solSlider;
    @FXML
    private Button playpause, forwardStep, backwardStep;
    @FXML
    private ImageView curiositySpeaks;

    // fields critical to control flow
    private ImageLocations imgLocs;
    private CuriosityDatabase db;
    private InfoBoxRetrieval infobox;
    private int currentSol;
    private int solSeconds;
    private int solsOnGraph;
    private ScheduledExecutorService execService;
    private Future<?> future;
    private MainApp mainApp;
    private boolean firstTime;

    /**
     * The constructor is called before the initialize() method.
     */
    public Controller() throws SQLException, ClassNotFoundException {
        currentSol = -1;
        solSeconds = 88765;
        solsOnGraph = 10;
        execService = Executors.newScheduledThreadPool(1);
        cameras = new ArrayList<>();
        imgLocs = new ImageLocations();
        db = new CuriosityDatabase();
        infobox = new InfoBoxRetrieval();
        yAxises = new ArrayList<>();
        toggleButtons = new ArrayList<>();
        lineCharts = new ArrayList<>();
        firstTime = true;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        URL url = getClass().getResource("/mic/view/map/openlayers_global_mic.html");
        mapGlobal.getEngine().load(url.toExternalForm());
        webEngineGlobal = mapGlobal.getEngine();
        webEngineGlobal.load(url.toExternalForm());
        // grab mars map and laod into WebView
        URL url2 = getClass().getResource("/mic/view/map/openlayers_mic.html");
        mapGale.getEngine().load(url2.toExternalForm());
        webEngineGale = mapGale.getEngine();
        webEngineGale.load(url2.toExternalForm());
        outdatedLabel.setVisible(false);
        infoboxDesc.setEditable(false);
        playScheduler(false);
        establishListeners();
        createCharts();
    }


    /**
     * Method that is called every however often defined in the scheduler
     */
    private void loop() throws SQLException, ClassNotFoundException {

        currentSol++;
        imgLocs.nextSol();
        runLater(() -> {
            try {
                updateCamera();
                updateLineChart();
                infoboxUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            setSolLabel(currentSol);
            solSlider.setValue(currentSol);
            webEngineGale.executeScript("highlightPath("+currentSol+");");
        });
    }

    private void infoboxUpdate() throws SQLException {
        String desc = infobox.getInfoBox(currentSol);
        infoboxDesc.setText(desc);
        if (desc.charAt(0) == '\"') {
            infoboxDesc.setStyle("-fx-font-size: 16px;");
            curiositySpeaks.setStyle("-fx-opacity: 1");
        }
        else {
            infoboxDesc.setStyle("-fx-font-size: 14px;");
            curiositySpeaks.setStyle("-fx-opacity: 0");
        }
    }

    private void setSolLabel(int sol){
        if (currentSol <10)
            solNumber.setText("000"+Integer.toString(sol));
        else if (currentSol<100)
            solNumber.setText("00"+Integer.toString(sol));
        else if (currentSol<1000)
            solNumber.setText("0"+Integer.toString(sol));
        else
            solNumber.setText(Integer.toString(sol));

    }

    /**
     * Method contain all the various listeners used in the application, called from initilize()
     * grouped together to easily find/hide away
     */
    private void establishListeners() {
        //sol slider listener
        solSlider.valueProperty().addListener((arg0, arg1, arg2) -> {
            setSolLabel((int) solSlider.getValue());
            currentSol = (int) solSlider.getValue();

            try {
                updateCamera();
                infoboxUpdate();
            } catch (SQLException e) { e.printStackTrace();} catch (IOException e) {
                e.printStackTrace();
            }
        });

        // button that play/pauses the system
        playpause.setOnAction(event -> {
            if (playpause.getText().equals("Taking a break.")) {
                playScheduler(true);
            } else {
                pauseScheduler();
            }
        });

        // button that move system 1 sol forward
        forwardStep.setOnAction(event -> {
            try {loop();}
            catch (SQLException e) {e.printStackTrace();}
            catch (ClassNotFoundException e) {e.printStackTrace();}
        });

        // button that move system 1 sol back
        backwardStep.setOnAction(event -> {
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
                try {updateCamera();} catch (SQLException e) {e.printStackTrace();} catch (IOException e) {
                    e.printStackTrace();
                }
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

        mapSwitch.setOnMouseClicked(event -> {
            if (mapGlobal.isVisible()) {
                mapGlobal.setVisible(false);
                mapSwitch.setImage(new Image("/mic/view/fxml/css/glob.png"));
            }
            else {
                mapGlobal.setVisible(true);
                mapSwitch.setImage(new Image("/mic/view/fxml/css/mr.png"));
            }
        });

        infoboxDesc.addEventFilter(ScrollEvent.ANY, (x) -> {
            pauseScheduler();
        });

        infoboxDesc.setOnMouseClicked(event ->
            pauseScheduler()
        );
    }

    private void changeLineVisibility(String line, boolean visible){
        if (visible) {
            if (line.equals("tempToggle")) {
                tempChart.setOpacity(1);
                tempToggle.setStyle("-fx-background-color:#bc2e29;");
            }
            if (line.equals("elevToggle")) {
                elevChart.setOpacity(1);
                elevToggle.setStyle("-fx-background-color:#5279c7;");
            }
            if (line.equals("uvaToggle")) {
                uvaChart.setOpacity(1);
                uvaToggle.setStyle("-fx-background-color:#8cb464;");
            }
            if (line.equals("humidToggle")) {
                humidChart.setOpacity(1);
                humidToggle.setStyle("-fx-background-color:#d48621;");
            }
            if (line.equals("pressToggle")) {
                pressChart.setOpacity(1);
                pressToggle.setStyle("-fx-background-color:#9e4c9e;");
            }
        }

        else {
            if (line.equals("tempToggle")) {
                tempChart.setOpacity(0);
                tempToggle.setStyle("-fx-background-color:grey;");
            }
            if (line.equals("elevToggle")) {
                elevChart.setOpacity(0);
                elevToggle.setStyle("-fx-background-color:grey;");
            }
            if (line.equals("uvaToggle")) {
                uvaChart.setOpacity(0);
                uvaToggle.setStyle("-fx-background-color:grey;");
            }
            if (line.equals("humidToggle")) {
                humidChart.setOpacity(0);
                humidToggle.setStyle("-fx-background-color:grey;");
            }
            if (line.equals("pressToggle")) {
                pressChart.setOpacity(0);
                pressToggle.setStyle("-fx-background-color:grey;"); }
        }
    }

    private void changeAxisY(String newY) {

        for (NumberAxis y:yAxises)
            y.setOpacity(0);

        if (newY.equals("Temperature")) {
            tempY.setOpacity(1);
            yAxisLabel.setText("Celsius (°C)");
            chooseAxisY.setStyle("-fx-background-color:#bc2e29;");
        }
        else if (newY.equals("UVA")) {
            uvaY.setOpacity(1);
            yAxisLabel.setText("Irradiance (W/m²)");
            chooseAxisY.setStyle("-fx-background-color:#8cb464;");
        }
        else if (newY.equals("Pressure")) {
            pressY.setOpacity(1);
            yAxisLabel.setText("Pascal (Pa)");
            chooseAxisY.setStyle("-fx-background-color:#9e4c9e;");
        }
        else if (newY.equals("Humidity")) {
            humidY.setOpacity(1);
            yAxisLabel.setText("Relative Humidity (%)");
            chooseAxisY.setStyle("-fx-background-color:#d48621;");
        }
        else if (newY.equals("Elevation")) {
            elevY.setOpacity(1);
            yAxisLabel.setText("Metres (relative to landing site)");
            chooseAxisY.setStyle("-fx-background-color:#5279c7;");
        }
    }

    private void createCharts(){
        //temperature
        tempY.setAutoRanging(false);
        tempY.setUpperBound(0);
        tempY.setLowerBound(-100.00);
        tempY.setTickUnit(20);
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
        uvaY.setTickUnit(2);
        uvaX.setAutoRanging(false);
        uvaX.setUpperBound(solSeconds*solsOnGraph);
        uvaX.setLowerBound(0);
        uvaX.setTickUnit(solSeconds);

        //pressure
        pressY.setAutoRanging(false);
        pressY.setUpperBound(4.9);
        pressY.setLowerBound(2.7);
        pressY.setTickUnit(0.3);
        pressX.setAutoRanging(false);
        pressX.setUpperBound(solSeconds*solsOnGraph);
        pressX.setLowerBound(0);
        pressX.setTickUnit(solSeconds);

        //humid
        humidY.setAutoRanging(false);
        humidY.setUpperBound(50);
        humidY.setLowerBound(0);
        humidY.setTickUnit(10);
        humidX.setAutoRanging(false);
        humidX.setUpperBound(solSeconds*solsOnGraph);
        humidX.setLowerBound(0);
        humidX.setTickUnit(solSeconds);

        tempChart.getYAxis().setOpacity(0);
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
        chooseAxisY.setValue("Elevation");

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
     * Resumes application from paused
     */
    private void playScheduler(boolean immediately) {
        int delay = 5000;

        // for restarting scheduler quickly when changing sol/pressing play
        if (immediately)
            delay = 500;

        future = execService.scheduleAtFixedRate(()->{
            if (currentSol<=1500) {
                if (firstTime) {
                    mapGlobal.setVisible(false);
                    mapSwitch.setImage(new Image("/mic/view/fxml/css/glob.png"));
                    firstTime = false;
                }
                try { loop();}
                catch (SQLException e) {e.printStackTrace();}
                catch (ClassNotFoundException e) {e.printStackTrace();}}
        }, delay, 2000, TimeUnit.MILLISECONDS);
        playpause.setText("Currently Exploring...");
        playpause.setStyle("-fx-background-color:#2e7d32;");

    }

    /**
     * Pauses whenever application is to be paused by this controller or by the user
     */
    private void pauseScheduler() {
        future.cancel(true);
        playpause.setText("Taking a break.");
        playpause.setStyle("-fx-background-color:red;");
    }

    /**
     * Called each "round" to update image views with images relative to sol
     */
    private void updateCamera() throws SQLException, IOException {

        File main = new File(imgLocs.getSelectedView() + currentSol + ".jpg");

        // update main view with whatever is selected
        if (new File(main.getAbsolutePath()).isFile()) {
            MAINVIEW.setOpacity(1);
            MAINVIEW.setImage(new Image("file:"+main.getAbsolutePath()));
            outdatedLabel.setVisible(false);
        }
        else {
            outdatedLabel.setVisible(true);
            MAINVIEW.setOpacity(0.75);
        }

        // update each thumbnail view
        for (ImageView currCam : cameras) {
            File curr = new File(imgLocs.getCamera(currCam.getId()) + currentSol + ".jpg");
            //System.out.println(imgLocs.getCamera(currCam.getId())+ currentSol + ".jpg");
            if (new File(curr.getAbsolutePath()).isFile()) {
                currCam.setOpacity(1);
                currCam.setImage(new Image("file:" + curr.getAbsolutePath()));
            }
            else {currCam.setOpacity(0.75);}
        }

        // update curated thumbnail
        File curated = new File (imgLocs.getCurated()+currentSol+".jpg");
        if (new File(curated.getAbsolutePath()).isFile()) {
            CURATED_th.setOpacity(1);
            CURATED_th.setImage(new Image ("file:"+curated.getAbsolutePath()));
        }
        else
            CURATED_th.setOpacity(0.75);
    }

    /**
     * Is called by the main application to give a reference back to itself.
     */
    public void setMainApp(MainApp mainApp) {this.mainApp = mainApp;}
}