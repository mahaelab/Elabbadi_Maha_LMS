import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Starts the server
        Stage serverStage = new Stage();
        Server server = new Server();
        server.start(serverStage);

        // Starts the client
        Stage clientStage = new Stage();
        Client client = new Client();
        client.start(clientStage);
    }
}
