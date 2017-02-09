package mic.view.example;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.View;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static javafx.scene.input.KeyCode.R;

public class MainApp extends Application {

    int picNo = 0;
    public static void main(String[] args) {
        Application.launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        ArrayList<String> lol = new ArrayList<String>();
        final FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("N/A", "jpg");//, whatever other extensions you want);
        File file = new File("C:\\Users\\Dazak\\Desktop\\imgs");
        for(final File child : file.listFiles()) {
            File file2 = new File(child.getAbsolutePath());
            for(final File child2 : file2.listFiles()) {
                if (extensionFilter.accept(child2)) {
                    lol.add("file:" + child2.getAbsolutePath());
                        /*
                        System.out.println(child2.getAbsolutePath());
                        Image g = new Image("file:" + child2.getAbsolutePath());
                        imgView.setImage(g);
                        */
                }
            }
        }


        primaryStage.setTitle("Load Image");

        StackPane sp = new StackPane();
        Image img = new Image("file:C:\\Users\\Dazak\\Desktop\\imgs\\1\\14.jpg");
        ImageView imgView = new ImageView(img);

        imgView.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent t) {
                Image g = new Image(lol.get(picNo));
                imgView.setImage(g);
                picNo++;
            }
        });


        sp.getChildren().add(imgView);

        //Adding HBox to the scene
        Scene scene = new Scene(sp);
        primaryStage.setScene(scene);
        primaryStage.show();

        /*
        Image g = new Image("file:C:\\Users\\Dazak\\Desktop\\imgs\\1\\10.jpg");
        imgView.setImage(g);
        */




    }

}