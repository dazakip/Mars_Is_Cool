package mic.view;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

//CLASS THAT RUNS BROWSER CLASS
public class browserEx extends Application {
    private Scene scene;
    @Override public void start(Stage stage) {
        // create the scene
        stage.setTitle("Web View");
        scene = new Scene(new Browser(),750,500, Color.web("#666970"));
        stage.setScene(scene);
        //scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}

// BROWSER CLASS
class Browser extends Region {

    final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();

    public Browser() {
        /*
        //wait till webengine loads before executing script
        webEngine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
                    public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                        if (newState == Worker.State.SUCCEEDED) {


                            Connection c = null;
                            Statement stmt = null;
                            try {
                                Class.forName("org.sqlite.JDBC");
                                c = DriverManager.getConnection("jdbc:sqlite:cur_loc.db");
                                c.setAutoCommit(false);
                                System.out.println("Opened database successfully");

                                stmt = c.createStatement();
                                ResultSet rs = stmt.executeQuery( "SELECT * FROM LOCATIONS;" );

                                int mark = 0;
                                while ( rs.next() ) {
                                    mark = mark+1;
                                    String so = "addMarker(" + rs.getFloat("lat") +"," + rs.getFloat("long")+");";
                                    webEngine.executeScript(so);
                                    System.out.println("Marker: " + mark);
                                }
                                System.out.println(mark + "- finished");
                                rs.close();
                                stmt.close();
                                c.close();
                            } catch ( Exception e ) {
                                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                                System.exit(0);
                            }
                        }
                    }
                });
                */

        //apply the styles
        getStyleClass().add("browser");

        URL url = getClass().getResource("openlayers.html");
        browser.getEngine().load(url.toExternalForm());

        //add the web view to the scene
        getChildren().add(browser);
        // load the web page
        webEngine.load(url.toExternalForm());



    }
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }

    @Override protected double computePrefWidth(double height) {
        return 1000;
    }

    @Override protected double computePrefHeight(double width) {
        return 500;
    }
}