import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Application {
  private Label receivedNumberLabel;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Server");

    // Creates GUI elements
    receivedNumberLabel = new Label("\n");

    VBox layout = new VBox(10);
    layout.getChildren().add(receivedNumberLabel);

    Scene scene = new Scene(layout, 350, 300);
    primaryStage.setScene(scene);

    primaryStage.show();

    new Thread(() -> startServer(49670)).start();
  }

  private void startServer(int port) {
    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Server listening on port " + port);

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Accepted connection from " + clientSocket.getInetAddress());

        // Handles client request
        new Thread(() -> handleClientRequest(clientSocket)).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleClientRequest(Socket clientSocket) {
    try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
    ) {
      // Reads the number from the client
      String clientRequest = reader.readLine();
      int numberToCheck = Integer.parseInt(clientRequest);

      System.out.println("Received request from " + clientSocket.getInetAddress() + ": " + numberToCheck);

      // Updates the GUI label
      Platform.runLater(() -> updateReceivedNumber(numberToCheck));

      // Determines if it's prime
      boolean isPrime = isPrime(numberToCheck);

      // Sends the result to the client
      writer.println(isPrime ? "Yes" : "No");

      System.out.println("Sent response to " + clientSocket.getInetAddress() + ": " + (isPrime ? "Yes" : "No"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void updateReceivedNumber(int number) {
    Platform.runLater(() -> receivedNumberLabel.setText(receivedNumberLabel.getText() + "The number received is: " + number + "\n"));
  }

  private boolean isPrime(int number) {
    if (number <= 1) {
      return false;
    }
    for (int i = 2; i <= Math.sqrt(number); i++) {
      if (number % i == 0) {
        return false;
      }
    }
    return true;
  }
}
