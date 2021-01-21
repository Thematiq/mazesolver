package app;

import app.controllers.WindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/window.fxml"));
        Parent root = loader.load();
        WindowController wc = loader.getController();
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(wc::inputHandler);
        primaryStage.getIcons().add(new Image(getClass().getResource("../resources/king.png").toString()));
        primaryStage.setTitle("Maze Solver");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void run(String[] args) {
        launch(args);
    }
}
