import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*Maha Elabbadi, CEN-3024C-14835, 9/7/23
 * The Book class represents an individual book with attributes like ID, title, and author. 
 * Used to create book objects that store data about each book in the library.*/
class Book {
    private int id;
    private String title;
    private String author;

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
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
}

/* Maha Elabbadi, CEN-3024C-14835, 9/7/23
 * Manages the collection of books and provides methods for
 * adding, removing, listing, loading, and saving books. */
class Library {
    private List<Book> books = new ArrayList<>();
    private int nextId = 1; // To generate unique IDs

    public int getNextId() {
        return nextId;
    }
    
/* Method addBook adds a new book to the library's collection.
 * Arguments: title, author)  */
    public void addBook(String title, String author) {
        Book book = new Book(nextId, title, author);
        books.add(book);
        nextId++; // Increment for the next book
        saveBooksToFile("/Users/Maha/eclipse-workspace/Library/src/books.txt"); // Save changes to the text file
    }
/* Method removeBook removes books from collection based on ID
 * Arguments: ID	Return values: true, false  */
    public boolean removeBook(int id) {
        for (Book book : books) {
            if (book.getId() == id) {
                books.remove(book);
                saveBooksToFile("/Users/Maha/eclipse-workspace/Library/src/books.txt"); // Save changes to the text file
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

/* Method loadBookFromFIle Loads book data from a text file into the library's collection. 
 * Arguments: filename	Return value: void (none)*/
    public void loadBooksFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] bookInfo = line.split(",");
                if (bookInfo.length == 3) {
                    int id = Integer.parseInt(bookInfo[0].trim());
                    String title = bookInfo[1].trim();
                    String author = bookInfo[2].trim();
                    addBook(title, author);
                    if (id >= nextId) {
                        nextId = id + 1; // Ensure the next generated ID is greater than the specified ID
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/* Method saveBooksToFile saves changes/edits to the text file.
 * Arguments: filename	Return value: void (none)*/
    public void saveBooksToFile(String filename) {
        try (FileWriter fw = new FileWriter(filename)) {
            for (Book book : books) {
                fw.write(book.getId() + ", " + book.getTitle() + ", " + book.getAuthor() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/* Maha Elabbadi, CEN-3024C-14835, 9/7/23 
 * The LibraryApp class is what allows users to interact with the library management system,
 * Main objective is to help with book management tasks within the library.
 * Books are loaded from a text file when the program starts,
 * Any changes are saved back to the file*/
public class LibraryApp {
    private static Library library = new Library();

    public static void main(String[] args) {
        // Loads books from a text file when the program starts
        library.loadBooksFromFile("/Users/Maha/eclipse-workspace/Library/src/books.txt");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Add new book");
            System.out.println("2. Remove book");
            System.out.println("3. List all books");
            System.out.println("4. Exit");
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
                    // Saves the updated collection to the text file before exiting
                    library.saveBooksToFile("/Users/Maha/eclipse-workspace/Library/src/books.txt");
                    System.out.println("Exiting Program");
                    return;
                default:
                    System.out.println("Invalid. Try again");
            }
        }
    }

    private static void addBook() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter book title: ");
        String title = scanner.nextLine();

        System.out.print("Enter author name: ");
        String author = scanner.nextLine();

        int id = library.getNextId(); // Get the next available ID
        library.addBook(title, author);

        System.out.println("Book added successfully with ID: " + id);
    }

    private static void removeBook() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter book ID to remove: ");
        int id = scanner.nextInt();

        boolean removed = library.removeBook(id);

        if (removed) {
            System.out.println("Book removed successfully!");
        } else {
            System.out.println("Book with ID " + id + " not found.");
        }
    }

    private static void listBooks() {
        List<Book> books = library.getBooks();
        if (books.isEmpty()) {
            System.out.println("No books in the collection.");
        } else {
            System.out.println("Books in the collection:");
            for (Book book : books) {
                System.out.println(book.getId() + ", " + book.getTitle() + ", " + book.getAuthor());
            }
        }
    }
}
