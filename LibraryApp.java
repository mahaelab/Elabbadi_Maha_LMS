import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

/* Maha Elabbadi, CEN-3024C-14835, 9/30/23
 * The Book class represents an individual book with attributes like ID, title, and author.
 * Used to create book objects that store data about each book in the library.*/

class Book {
    private int id;
    private String title;
    private String author;
    private String status;
    private LocalDate dueDate;

    /*cMaha Elabbadi, CEN-3024C-14835, 9/30/23
     * The Book class represents an individual book with attributes like ID, title, and author.
     * Used to create book objects that store data about each book in the library.*/
    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.status = "checked in"; // Initialize status as "checked in" by default
        this.dueDate = null;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}

/* Maha Elabbadi, CEN-3024C-14835, 9/30/23
 * Manages the collection of books and provides methods for
 * adding, removing, listing, loading, and saving books. */
class Library {
    private List<Book> books = new ArrayList<>();
    private int nextId = 1;

    public int getNextId() {
        return nextId;
    }

    /* Method addBook adds a new book to the library's collection.
     * Arguments: title, author
     * No Return Value */
    public void addBook(String title, String author) {
        // Generate the next available ID based on the maximum ID found in the current list
        int id = books.isEmpty() ? 1 : books.stream().mapToInt(Book::getId).max().getAsInt() + 1;

        Book book = new Book(id, title, author);
        books.add(book);

        // Save changes to the text file
        saveBooksToFile("C:\\Users\\Maha\\Desktop\\LibraryApp\\src\\books.txt"); // Update the file path as needed

    }

    /* Method removeBook removes books from collection based on ID
     * Arguments: ID    Return values: true, false */
    public boolean removeBook(String barcodeOrTitle) {
        Iterator<Book> iterator = books.iterator();

        while (iterator.hasNext()) {
            Book book = iterator.next();
            if (String.valueOf(book.getId()).equals(barcodeOrTitle) || book.getTitle().equalsIgnoreCase(barcodeOrTitle)) {
                iterator.remove(); // Remove the book
                saveBooksToFile("C:\\Users\\Maha\\Desktop\\LibraryApp\\src\\books.txt"); // Save changes to the database file
                return true;
            }
        }
        return false; // Book not found
    }

    /* Method List<Book> getBooks retrieves all books in the library's collection
     * No arguments. Return value are books. If there are none, empty list is returned*/
    public List<Book> getBooks() {
        return books;
    }

    /* loadDefaultBooks method loads in books from the original text file/database
     * No arguments or return value */
    public void loadDefaultBooks() {
        // Load the original LMS database text file
        loadBooksFromFile("C:\\Users\\Maha\\Desktop\\LibraryApp\\src\\books.txt");

        // Set nextId to 1 if there are no existing books
        if (books.isEmpty()) {
            nextId = 1;
        }
    }
    /* Method loadBookFromFIle Loads book data from a text file into the library's collection.
     * Arguments: filename
     * No return value */
    public void loadBooksFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int maxId = 0; // Track the maximum ID in the existing books
            while ((line = br.readLine()) != null) {
                String[] bookInfo = line.split(",");
                if (bookInfo.length == 3) {
                    int id = Integer.parseInt(bookInfo[0].trim());
                    String title = bookInfo[1].trim();
                    String author = bookInfo[2].trim();
                    maxId = Math.max(maxId, id); // Update the maximum ID
                    addBook(title, author);
                }
            }
            // Set nextId to 1 if there are no existing books
            if (books.isEmpty()) {
                nextId = 1;
            } else {
                // Update nextId to be the maximum ID + 1
                nextId = maxId + 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* saveBooksToFile method saves changes or any new books added to the original database text file
     * Arguments: filename
     * No return value */
    public void saveBooksToFile(String filename) {
        try (FileWriter fw = new FileWriter(filename)) {
            for (Book book : books) {
                fw.write(book.getId() + ", " + book.getTitle() + ", " + book.getAuthor() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Method checkOutBook is used to check out a book by title.
     * Arguments: title
     * Return value: true if the book is successfully checked out, false otherwise
     */
    public boolean checkOutBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                if ("checked in".equalsIgnoreCase(book.getStatus())) {
                    // Set the book as checked out and calculate the due date
                    book.setStatus("checked out");
                    book.setDueDate(LocalDate.now().plusWeeks(4));
                    return true;
                } else {
                    System.out.println("Error: Book is already checked out.");
                    return false;
                }
            }
        }
        System.out.println("Error: Book not found.");
        return false;
    }

    /* Method checkInBook checks in a book by title.
     * Arguments: title
     * Return value: true if the book is successfully checked in, false otherwise
     */
    public boolean checkInBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                if ("checked out".equalsIgnoreCase(book.getStatus())) {
                    // Set the book as checked in and clear the due date
                    book.setStatus("checked in");
                    book.setDueDate(null);
                    return true;
                } else {
                    System.out.println("Error: Book is already checked in.");
                    return false;
                }
            }
        }
        System.out.println("Error: Book not found.");
        return false;
    }
}

/* Maha Elabbadi, CEN-3024C-14835, 9/30/23
 * The LibraryApp class is what allows users to interact with the library management system,
 * Main objective is to help with book management tasks within the library.
 * Books are loaded from a text file when the program starts,
 * Any changes are saved back to the file*/
public class LibraryApp {
    private static Library library = new Library();
    private static void loadBooksFromFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to the text file with books: ");
        String filePath = scanner.nextLine();
        library.loadBooksFromFile(filePath);
        library.saveBooksToFile("C:\\Users\\Maha\\Desktop\\LibraryApp\\src\\books.txt"); // Save changes to the original file
        System.out.println("Books loaded successfully from the file and saved to the database.");
    }

    public static void main(String[] args) {
        library.loadDefaultBooks(); // Load the original LMS database

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Add new book");
            System.out.println("2. Remove book");
            System.out.println("3. List all books");
            System.out.println("4. Load books in from file");
            System.out.println("5. Check out book");
            System.out.println("6. Check in book");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    removeBook();
                    break;
                case 3:
                    listBooks();
                    break;
                case 4:
                    loadBooksFromFile(); // Load books from a file and save to the database
                    break;
                case 5:
                    checkOutBook();
                    break;
                case 6:
                    checkInBook();
                    break;
                case 7:
                    library.saveBooksToFile("C:\\Users\\Maha\\Desktop\\LibraryApp\\src\\books.txt"); // Save changes to the original file
                    System.out.println("Exiting Program");
                    return;
                default:
                    System.out.println("Invalid. Try again");
            }
        }
    }

    private static void checkOutBook() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the title of the book to check out: ");
        String title = scanner.nextLine(); // Use nextLine() to read the entire line of text

        boolean checkedOut = library.checkOutBook(title);

        if (checkedOut) {
            System.out.println("Book checked out successfully.");
        }
    }


    private static void checkInBook() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the title of the book to check in: ");
        String title = scanner.nextLine();

        boolean checkedIn = library.checkInBook(title);

        if (checkedIn) {
            System.out.println("Book checked in successfully.");
        }
    }

    private static void addBook() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter book title: ");
        String title = scanner.nextLine();

        System.out.print("Enter author name: ");
        String author = scanner.nextLine();

        int id = library.getNextId();
        library.addBook(title, author);

        System.out.println("Book successfully added! New Barcode: " + id);
    }

    private static void removeBook() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the barcode number or title of the book to remove: ");
        String barcodeOrTitle = scanner.nextLine();

        boolean removed = library.removeBook(barcodeOrTitle);

        if (removed) {
            System.out.println("Book(s) removed successfully!");
        } else {
            System.out.println("No book with the provided barcode or title found.");
        }

        // Reprint the updated database from the LibraryApp class
        listBooks();
    }

    private static void listBooks() {
        List<Book> books = library.getBooks();
        if (books.isEmpty()) {
            System.out.println("No books in the collection.");
        } else {
            System.out.println("Printing is taking place. Books in the database:");
            for (Book book : books) {
                System.out.println(book.getId() + ", " + book.getTitle() + ", " + book.getAuthor());
            }
        }
        System.out.println();
    }
}
