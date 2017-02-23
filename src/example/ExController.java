package example;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView ;
import java.awt.*;
import javafx.scene.image.Image;

public class ExController {
    @FXML
    private ImageView FRONTHAZ;
    @FXML
    private ImageView REARHAZ;
    @FXML
    private ImageView LEFTHAZ;
    @FXML
    private ImageView RIGHTHAZ;


    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public ExController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.
        Image g = new Image("file:C:\\Users\\Dazak\\Desktop\\ssssssssasdsadasdasd.png");
        FRONTHAZ.setImage(g);
        LEFTHAZ.setImage(g);
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