package mic.controller;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import parser.CuratedController;


/**
 * MainApp is the class that contains the main method.
 * It initializes the  root layout that the chosen
 * view for MIC will be shown in.
 */
public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("mars is wicked cool");

		// add listener to close stage upon close request
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

		initRootLayout();
		showView();
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/mic/view/fxml/RootLayout.fxml"));
			rootLayout = loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			//primaryStage.setFullScreen(true);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the chosen view view inside the root layout.
	 */
	public void showView() {
		try {
			// Load view view.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/mic/view/fxml/laptop.fxml"));
			AnchorPane personOverview = loader.load();

			// Set view view into the center of root layout.
			rootLayout.setCenter(personOverview);

			// Give the controller access to the main app.
			Controller controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showCuratedView() {
		try {
			// Load view view.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("/parser/curated.fxml"));
			AnchorPane personOverview = loader.load();

			// Set view view into the center of root layout.
			rootLayout.setCenter(personOverview);

			// Give the controller access to the main app.
			CuratedController controller = loader.getController();
			controller.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}