package old_map_stuff;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;

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

        //add the web old_map_stuff to the scene
        getChildren().add(browser);
        // load the web page
        webEngine.load(url.toExternalForm());



    }
}