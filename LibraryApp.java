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
    private int barcode;
    private String title;
    private String author;
    private String status;
    private LocalDate dueDate;

    public Book(int barcode, String title, String author) {
        this.barcode = barcode;
        this.title = title;
        this.author = author;
        this.status = "checked in";
        this.dueDate = null;
    }

    public int getBarcode() {
        return barcode;
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
    private int nextBarcode = 1;

    public int getNextBarcode() {
        return nextBarcode;
    }

    /* Method addBook adds a new book to the library's collection.
     * Arguments: title, author
     * No Return Value */
    public void addBook(String title, String author) {
        int barcode = books.isEmpty() ? 1 : books.stream().mapToInt(Book::getBarcode).max().getAsInt() + 1;
        Book book = new Book(barcode, title, author);
        books.add(book);
        saveBooksToFile("C:\\Users\\Maha\\Desktop\\LibraryApp\\src\\books.txt");
    }

    /* Method removeBook removes books from collection based on ID
     * Arguments: barcodeOrTitle
     * Return values: true, false */
    public boolean removeBook(String barcodeOrTitle) {
        Iterator<Book> iterator = books.iterator();
        while (iterator.hasNext()) {
            Book book = iterator.next();
            if (String.valueOf(book.getBarcode()).equals(barcodeOrTitle) || book.getTitle().equalsIgnoreCase(barcodeOrTitle)) {
                iterator.remove();
                saveBooksToFile("C:\\Users\\Maha\\Desktop\\LibraryApp\\src\\books.txt");
                return true;
            }
        }
        return false;
    }

    /* Method List<Book> getBooks retrieves all books in the library's collection
     * No arguments. Return value are books. If there are none, empty list is returned*/
    public List<Book> getBooks() {
        return books;
    }

    /* loadDefaultBooks method loads in books from the original text file/database
     * No arguments or return value */
    public void loadDefaultBooks() {
        loadBooksFromFile("C:\\Users\\Maha\\Desktop\\LibraryApp\\src\\books.txt");
        if (books.isEmpty()) {
            nextBarcode = 1;
        }
    }

    /* Method loadBookFromFIle Loads book data from a text file into the library's collection.
     * Arguments: filename
     * No return value */
    public void loadBooksFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int maxBarcode = 0;
            while ((line = br.readLine()) != null) {
                String[] bookInfo = line.split(",");
                if (bookInfo.length == 3) {
                    int barcode = Integer.parseInt(bookInfo[0].trim());
                    String title = bookInfo[1].trim();
                    String author = bookInfo[2].trim();
                    maxBarcode = Math.max(maxBarcode, barcode);
                    addBook(title, author);
                }
            }
            if (books.isEmpty()) {
                nextBarcode = 1;
            } else {
                nextBarcode = maxBarcode + 1;
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
                fw.write(book.getBarcode() + ", " + book.getTitle() + ", " + book.getAuthor() + "\n");
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
                    book.setStatus("checked out");
                    LocalDate dueDate = LocalDate.now().plusWeeks(4); // Set due date to 4 weeks from now
                    book.setDueDate(dueDate);
                    System.out.println("Book checked out successfully. Due Date: " + dueDate);
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
                    book.setStatus("checked in");
                    book.setDueDate(null); // Set due date to null when checking in
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

    //calls loadBooksFromFile method where user inputs a file path. Added books are saved to database file
    private static void loadBooksFromFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to the text file with books: ");
        String filePath = scanner.nextLine();
        library.loadBooksFromFile(filePath);
        library.saveBooksToFile("C:\\Users\\Maha\\Desktop\\LibraryApp\\src\\books.txt");
        System.out.println("Books loaded successfully from the file and saved to the database.");
    }

    public static void main(String[] args) {
        library.loadDefaultBooks();

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
            scanner.nextLine();

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
                    loadBooksFromFile();
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

    //calls checkOutBook method to check out a book and status changes to "checked out"
    private static void checkOutBook() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the title of the book to check out: ");
        String title = scanner.nextLine();

        boolean checkedOut = library.checkOutBook(title);

    }
    //calls checkInBook method and returns book status to checked in, due date is null if not checked out
    private static void checkInBook() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the title of the book to check in: ");
        String title = scanner.nextLine();

        boolean checkedIn = library.checkInBook(title);

        if (checkedIn) {
            System.out.println("Book checked in successfully.");
        }
    }
    //calls addBook method to add books using Author and Title
    private static void addBook() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter book title: ");
        String title = scanner.nextLine();

        System.out.print("Enter author name: ");
        String author = scanner.nextLine();

        int barcode = library.getNextBarcode();
        library.addBook(title, author);

        System.out.println("Book successfully added! New Barcode: " + barcode);
    }
    //calls removeBook method to add books using Author and Title
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
    //calls listBooks method to list all books currently in the database file collection
    private static void listBooks() {
        List<Book> books = library.getBooks();
        if (books.isEmpty()) {
            System.out.println("No books in the collection.");
        } else {
            System.out.println("Printing is taking place. Books in the database:");
            for (Book book : books) {
                System.out.println(book.getBarcode() + ", " + book.getTitle() + ", " + book.getAuthor());
            }
        }
        System.out.println();
    }
}
