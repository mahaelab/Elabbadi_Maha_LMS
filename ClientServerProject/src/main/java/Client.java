import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Application {
  private TextField numberField;
  private Label resultLabel;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Client");

    // Creates UI elements
    numberField = new TextField();
    Button checkButton = new Button("Check Prime");
    resultLabel = new Label("");

    checkButton.setOnAction(e -> checkPrime());

    VBox layout = new VBox(10);
    layout.getChildren().addAll(numberField, checkButton, resultLabel);

    Scene scene = new Scene(layout, 350, 300);
    primaryStage.setScene(scene);

    primaryStage.show();
  }

  private void checkPrime() {
    String number = numberField.getText();
    try (
            Socket socket = new Socket("localhost", 49670);
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))
    ) {
      // Send the number to the server
      writer.println(number);

      // Receives and displays the user input
      String userInput = "The number is: " + number;

      // Receives and displays the result from the server
      String result = reader.readLine();
      String primeStatus = "The number " + number + " is " + (result.equals("Yes") ? "prime" : "not prime");

      // Updates the result label
      updateResultLabel(userInput + "\n" + primeStatus);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private void updateResultLabel(String result) {
    resultLabel.setText(resultLabel.getText() + result + "\n");
  }

}
