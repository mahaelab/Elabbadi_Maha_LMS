import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.time.LocalDate;

//
// * Maha Elabbadi, CEN-3024C-14835, 10/27/23
// * The LibraryGUI class is what represents the main GUI application
// * It contains buttons for the methods from the Library and Book classes,
// * making it easier for users to use and navigate*/
public class LibraryGUI {
    private static Library library = new Library();
    private static JButton clearButton;
    private static JPanel buttonPanel;

    // Main method to start the GUI application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGUI(); // Call the method to create and show the GUI
        });
    }

    /*  createAndShowGUI method creates and shows the GUI components that users will
        be able to interact with */
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Library Management System");

        JButton clearButton = new JButton("Clear");

        // Sets close action and layout manager for the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 0, 10)); // 1 column, any number of rows, vertical gap of 10 pixels

        // Adds buttons to the button panel
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

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());

        JTextArea outputArea = new JTextArea();
        outputArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        outputArea.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Create an empty border to add some padding around the text area
        int padding = 20;
        outputArea.setBorder(new EmptyBorder(padding, padding, padding, padding));

        textPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        frame.add(buttonPanel, BorderLayout.WEST);
        frame.add(textPanel, BorderLayout.CENTER);

        // Action listener for the Add button
        addButton.addActionListener(e -> {
            // Prompt the user for book title and author
            String title = JOptionPane.showInputDialog("Enter book title:");
            String author = JOptionPane.showInputDialog("Enter author name:");

            // Check if valid input, add the book to the library, and update output area
            if (title != null && !title.trim().isEmpty() && author != null && !author.trim().isEmpty()) {
                library.addBook(title, author);
                library.saveChanges();
                outputArea.setText("Book added successfully!");
            } else {
                outputArea.setText("Invalid input. Book not added.");
            }
        });

        // Action listener for the Load from File button
        fileButton.addActionListener(e -> {
            // File chooser dialog to select a file
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showDialog(null, "Select File");

            // Handle the selected file
            if (response == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();

                // Load or save books based on file existence
                if (selectedFile.exists() && selectedFile.isFile()) {
                    library.loadBooksFromFile(filePath);
                    outputArea.setText("Books loaded successfully from file: " + filePath);
                } else {
                    library.saveBooksToFile(filePath);
                    outputArea.setText("Books saved successfully to file: " + filePath);
                }

                library.saveChanges();
            }
        });

        // Action listener for the Remove Book button
        removeButton.addActionListener(e -> {
            String barcodeOrTitle = JOptionPane.showInputDialog("Enter the barcode number or title of the book to remove:");
            boolean removed = library.removeBook(barcodeOrTitle);
            if (removed) {
                library.saveChanges();
                outputArea.setText("Book(s) removed successfully!");

                // Logic to display the updated book list after removing a book
                library.loadDefaultBooks();
                List<Book> books = library.getBooks();
                if (books.isEmpty()) {
                    outputArea.append("\nNo books in the collection.");
                } else {
                    outputArea.append("\nBooks in the database:");
                    for (Book book : books) {
                        outputArea.append("\n" + book.getBarcode() + ", " + book.getTitle() + ", " + book.getAuthor());
                    }
                }
            } else {
                outputArea.setText("No book with the provided barcode or title found.");
            }
        });

        // Action listener for the List Books button
        listButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                outputArea.setText("");
                library.loadDefaultBooks();
                List<Book> books = library.getBooks();
                if (books.isEmpty()) {
                    outputArea.setText("No books in the collection.");
                } else {
                    StringBuilder bookList = new StringBuilder("Books in the database:\n");
                    for (Book book : books) {
                        bookList.append(book.getBarcode()).append(", ").append(book.getTitle()).append(", ").append(book.getAuthor()).append("\n");
                    }
                    outputArea.setText(bookList.toString());
                }
            });
        });

        // Action listener for the Clear button
        clearButton.addActionListener(e -> {
            outputArea.setText(""); // Clears the output area
        });

        // Action listener for the Check In button
        checkInButton.addActionListener(e -> {
            String title = JOptionPane.showInputDialog("Enter the title of the book to check in:");
            boolean checkedIn = library.checkInBook(title);
            if (checkedIn) {
                outputArea.setText("Book checked in successfully.\nDue Date: null");
            } else {
                outputArea.setText("Error: Book not found or already checked in.");
            }
        });

        // Action listener for the Check Out button
        checkOutButton.addActionListener(e -> {
            String title = JOptionPane.showInputDialog("Enter the title of the book to check out:");
            boolean checkedOut = library.checkOutBook(title);
            if (checkedOut) {
                library.saveChanges();
                LocalDate dueDate = library.getDueDate();
                outputArea.setText("Book checked out successfully. \nDue Date: " + dueDate);
            } else {
                outputArea.setText("Error: Book not found or already checked out.");
            }
        });

        frame.setSize(700, 550);
        frame.setVisible(true);
    }
}
