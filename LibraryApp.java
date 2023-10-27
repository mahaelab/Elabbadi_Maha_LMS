import java.util.List;
import java.util.Scanner;

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
        library.saveBooksToFile("C:\\Users\\Maha\\Desktop\\LibraryApp\\src\\main\\java\\books.txt");
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
                    library.saveBooksToFile("C:\\Users\\Maha\\Desktop\\LibraryApp\\src\\main\\java\\books.txt"); // Save changes to the original file
                    System.out.println("Exiting Program");
                    return;
                default:
                    System.out.println("Invalid. Try again");
            }
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

        System.out.println("Book successfully added!");
        // save changes to the file after adding a book
        library.saveChanges();
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

        library.saveChanges(); // Save changes to the file after removing a book

        // Reprint the updated database from the LibraryApp class
        listBooks();
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
