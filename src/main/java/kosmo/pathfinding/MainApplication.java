package kosmo.pathfinding;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("root-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(false);
        stage.setTitle("Pathfinding");
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/kosmo/pathfinding/style.css")).toExternalForm());

        //stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/org/example/pau3/icon.png"))));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}