import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

/** Maha Elabbadi, CEN-3024C-14835, 11/14/23
 * The GUI2 class is what represents the main GUI application which interacts directly with the database
 * It contains buttons and unique methods meant for direct interaction with the database
  */
public class GUI2 {
    private static Library library = new Library();
    private static JButton clearButton;
    private static JPanel buttonPanel;
    private static Connection connection; // Define the connection variable at the class level


    /** Main method to start the GUI application
      */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI(); // Call the method to create and show the GUI
        });
    }


    /**  createAndShowGUI method creates and shows the GUI components that users will
        be able to interact with. It contains the GUI layout code, buttons, and action listeners */
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Library Management System");
        clearButton = new JButton("Clear");
        buttonPanel = new JPanel();

        JPanel welcomePanel = new JPanel();
        JLabel welcomeLabel = new JLabel("Welcome to the Library Management System");
        JLabel subCaptionLabel = new JLabel("Please select one of the following options");
        welcomeLabel.setForeground(new Color(123, 123, 123));
        subCaptionLabel.setForeground(new Color(142, 142, 142));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subCaptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 24));
        subCaptionLabel.setFont(new Font("Serif", Font.ITALIC, 18));
        welcomePanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Adjust the insets as needed
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(subCaptionLabel);

        buttonPanel.setLayout(new GridLayout(0, 1, 0, 10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        /** Connects to the database and error handling
          */
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/library_database";
            String username = "root";
            String password = "root";
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        /** Adds buttons to the button panel
          */
        JButton fileButton = new JButton("Load from File");
        JButton addButton = new JButton("Add Book");
        JButton removeButton = new JButton("Remove Book");
        JButton listButton = new JButton("List Books");
        JButton checkInButton = new JButton("Check In");
        JButton checkOutButton = new JButton("Check Out");

        buttonPanel.add(fileButton);
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(listButton);
        buttonPanel.add(checkOutButton);
        buttonPanel.add(checkInButton);
        buttonPanel.add(clearButton);

        JPanel textPanel = new JPanel(new BorderLayout());
        JTextArea outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        textPanel.add(scrollPane, BorderLayout.CENTER);

        frame.setLayout(new BorderLayout());
        frame.add(welcomePanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.WEST);
        frame.add(textPanel, BorderLayout.CENTER);

        int padding = 20;
        outputArea.setBorder(new EmptyBorder(padding, padding, padding, padding));

        textPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        frame.add(buttonPanel, BorderLayout.WEST);
        frame.add(textPanel, BorderLayout.CENTER);


        /** Action listener for the add book button. Adds book directly to DB
        * Distinct methods were created specifically for each listener
        * In order to interact with the database
        * Since previous methods were associated with interacting with a text file instead of a DB
         */
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /** Prompt the user for book details
                  */
                String title = JOptionPane.showInputDialog("Enter book title:");
                String author = JOptionPane.showInputDialog("Enter author name:");
                String genre = JOptionPane.showInputDialog("Enter genre:");

                /** Checks if any of the input fields are empty or null
                 */
                if (title == null || title.trim().isEmpty() ||
                        author == null || author.trim().isEmpty() ||
                        genre == null || genre.trim().isEmpty()) {
                    outputArea.setText("Error: Please provide values for title, author, and genre.");
                    return;
                }

                /** Database interaction code
                 */
                String query = "SELECT MIN(barcode) + 1 FROM books WHERE barcode + 1 NOT IN (SELECT barcode FROM books)";
                try (Connection connection = DatabaseConnection.getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(query)) {

                    int missingBarcode = 0;
                    if (resultSet.next()) {
                        missingBarcode = resultSet.getInt(1);
                    }

                    /** This ensures that there is no gap in barcode numbers.
                     * (Ex. If a book is removed, that previous barcode will be taken by another newly added book
                     * To make sure there aren't huge gaps in barcodes and keep the list sequential
                     * It also defaults new books to the "Checked-In" status and due date is null
                      */
                    String insertQuery = "INSERT INTO books (barcode, title, author, genre, status, due_date) VALUES (?, ?, ?, ?, ?, null)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                        preparedStatement.setInt(1, missingBarcode);
                        preparedStatement.setString(2, title);
                        preparedStatement.setString(3, author);
                        preparedStatement.setString(4, genre);
                        preparedStatement.setString(5, "Checked-In"); // All books are checked in automatically

                        preparedStatement.executeUpdate();
                        outputArea.setText("Book added successfully with barcode: " + missingBarcode);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        outputArea.setText("Error: Book not added.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    outputArea.setText("Error: Failed to retrieve missing barcode.");
                }
            }
        });

        /** Action listener for the load from file button. Allows users to open a text file from their computer
         * And add it directly to the DB
          */
        fileButton.addActionListener(e -> {
            /** Opens file chooser dialog to select a file
              */
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showDialog(null, "Select File");

            /** Handles the selected file
             */
            if (response == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();

                /** Gets the maximum existing barcode from the database so the barcodes remain sequential
                  */
                int maxBarcode = 0;
                String maxBarcodeQuery = "SELECT MAX(barcode) FROM books";
                try (Connection connection = DatabaseConnection.getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(maxBarcodeQuery)) {
                    if (resultSet.next()) {
                        maxBarcode = resultSet.getInt(1);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    outputArea.setText("Error: Unable to fetch maximum barcode from the database.");
                    return; // Exit the method if an error occurs
                }

                /** Read book information from the file and insert into the database with
                 * sequential and unique barcodes
                  */
                try (Scanner scanner = new Scanner(selectedFile)) {
                    while (scanner.hasNextLine()) {
                        String bookInfo = scanner.nextLine();
                        String[] bookData = bookInfo.split(", ");
                        if (bookData.length >= 4) {
                            String title = bookData[1];
                            String author = bookData[2];
                            String genre = bookData[3];

                            /** Generate a unique and sequential barcode for the book
                              */
                            int uniqueBarcode = ++maxBarcode;

                            /** Insert book information into the database with the unique and sequential barcode
                              */
                            String insertQuery = "INSERT INTO books (barcode, title, author, genre, status) VALUES (?, ?, ?, ?, 'Checked-In')";
                            try (Connection connection = DatabaseConnection.getConnection();
                                 PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                                preparedStatement.setInt(1, uniqueBarcode);
                                preparedStatement.setString(2, title);
                                preparedStatement.setString(3, author);
                                preparedStatement.setString(4, genre);
                                preparedStatement.executeUpdate();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                                outputArea.setText("Error: Unable to insert books from file into the database.");
                                return; // Exit the method if an error occurs
                            }
                        }
                    }
                    outputArea.setText("Books loaded successfully from file: " + filePath);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                    outputArea.setText("Error: File not found.");
                }
            }
        });

        /**Action listener for the remove book button
         * Removes book from the DB and updates list
          */
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String barcodeOrTitle = JOptionPane.showInputDialog("Enter book barcode or title:");
                String resultMessage;

                String query = "DELETE FROM books WHERE barcode = ? OR title = ?";
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    // Try to parse input as integer (barcode)
                    try {
                        int barcode = Integer.parseInt(barcodeOrTitle);
                        preparedStatement.setInt(1, barcode);  // Set barcode as an integer
                        preparedStatement.setObject(2, null);  // Set title as null
                    } catch (NumberFormatException ex) {
                        // If parsing as integer fails, assume it's a title
                        preparedStatement.setObject(1, null);  // Set barcode as null
                        preparedStatement.setString(2, barcodeOrTitle);  // Set title as a string
                    }

                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        resultMessage = "Book removed successfully from the database.";
                    } else {
                        resultMessage = "No book with the provided barcode or title found.";
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    resultMessage = "Error: Couldn't remove book from the database.";
                }

                /** Update the GUI output area with the result message
                  */
                outputArea.setText(resultMessage);
            }
        });


        /** Action listener for the list books button
         * Lists all books available in the DB
          */
        listButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = "SELECT * FROM books";
                StringBuilder bookList = new StringBuilder("Books in the database:\n\n");

                try (Connection connection = DatabaseConnection.getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(query)) {

                    bookList.append(String.format("%-10s %-40s %-25s %-20s %-15s %-15s\n", "Barcode", "Title", "Author", "Genre", "Status", "Due Date"));
                    bookList.append("\n");

                    while (resultSet.next()) {
                        int barcode = resultSet.getInt("barcode");
                        String title = truncate(resultSet.getString("title"), 40);
                        String author = truncate(resultSet.getString("author"), 25);
                        String genre = truncate(resultSet.getString("genre"), 20);
                        String status = truncate(resultSet.getString("status"), 15);

                        LocalDate dueDate = null;
                        Date sqlDueDate = resultSet.getDate("due_date");
                        if (sqlDueDate != null) {
                            dueDate = sqlDueDate.toLocalDate();
                        }

                        bookList.append(String.format("%-10d %-40s %-25s %-20s %-15s %-15s\n", barcode, title, author, genre, status, dueDate != null ? dueDate : "null"));
                    }

                    outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
                    outputArea.setText(bookList.toString());

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    outputArea.setText("Error: Failed to retrieve books from the database.");
                }
            }

            private String truncate(String str, int length) {
                if (str.length() > length) {
                    return str.substring(0, length);
                } else {
                    return str;
                }
            }
        });


        /** Action listener for the clear button (it just clears text from the screen)
          */
        clearButton.addActionListener(e -> {
            outputArea.setText("");
        });


        /** Action listener for the check in button
         * Checking in a book changes status to "Checked-In" and due date = null
          */
        checkInButton.addActionListener(e -> {
            String title = JOptionPane.showInputDialog("Enter the title of the book to check in:");
            String selectQuery = "SELECT * FROM books WHERE title = ? AND status = 'Checked-Out'";
            String updateQuery = "UPDATE books SET status = 'Checked-In', due_date = null WHERE title = ? AND status = 'Checked-Out'";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                 PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {

                /** Checks if the book is checked-out
                  */
                selectStatement.setString(1, title);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    /** If book is checked-out, this updates status to checked-In
                     */
                    updateStatement.setString(1, title);
                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        outputArea.setText("Book checked in successfully.\nDue Date: null");
                    } else {
                        outputArea.setText("Error: Book not found or already checked in.");
                    }
                } else {
                    outputArea.setText("Error: Book not found or already checked in.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                outputArea.setText("Error: Unable to check in the book.");
            }
        });

        /** Action listener for the check-out button
         * Once checked out, the status changes to "Checked-Out" and due date is 3 weeks from the current date
          */

        checkOutButton.addActionListener(e -> {
            String title = JOptionPane.showInputDialog("Enter the title of the book to check out:");
            String selectQuery = "SELECT * FROM books WHERE title = ? AND status = 'Checked-In'";
            String updateQuery = "UPDATE books SET status = 'Checked-Out', due_date = DATE_ADD(NOW(), INTERVAL 4 WEEK) WHERE title = ? AND status = 'Checked-In'";

            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                 PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {

                /** Checks if the book is checked-in
                  */

                selectStatement.setString(1, title);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    /** If book is Checked-In, update status to checked-out
                      */
                    updateStatement.setString(1, title);
                    int rowsAffected = updateStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        outputArea.setText("Book checked out successfully. \nDue Date: " + LocalDate.now().plusWeeks(4));
                    } else {
                        outputArea.setText("Error: Book not found.");
                    }
                } else {

                    outputArea.setText("Error: Book not found or already checked out.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                outputArea.setText("Error: Unable to check out the book.");
            }
        });

        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
