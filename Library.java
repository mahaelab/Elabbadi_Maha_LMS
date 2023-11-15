import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Maha Elabbadi, CEN-3024C-14835, 11/14/23
 * The Library class manages the collection of books and provides methods for
 * adding, removing, listing, loading, checkin in and out, and saving books.
 * This was the main component which allowed the older version of the program to run and interact
 * with a text file database.
 * */
class Library {
    private LocalDate dueDate;

    private List<Book> books = new ArrayList<>();
    private int nextBarcode = 1;
    public int getNextBarcode() {
        return nextBarcode;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }

    /** Method addBook adds a new book to the library's collection using user input.
     * @param title, author
     * @return none
     * */
    public void addBook(String title, String author) {
        int barcode = books.isEmpty() ? 1 : books.stream().mapToInt(Book::getBarcode).max().getAsInt() + 1;
        Book book = new Book(barcode, title, author);
        books.add(book);
    }

    /** Method removeBook removes books from collection based on barcode or title via user input
     * @param barcodeOrTitle
     * @return true, false
     * */
    public boolean removeBook(String barcodeOrTitle) {
        Iterator<Book> iterator = books.iterator();
        while (iterator.hasNext()) {
            Book book = iterator.next();
            if (String.valueOf(book.getBarcode()).equals(barcodeOrTitle) || book.getTitle().equalsIgnoreCase(barcodeOrTitle)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    /** Method List<Book> getBooks retrieves all books in the library's collection
     * @return none
     * */
    public List<Book> getBooks() {
        return books;
    }

    /** loadDefaultBooks method loads in books from the original text file database so that the
     * program is preloaded with a library of books
     * @return none
     * */
    public void loadDefaultBooks() {
        if (books.isEmpty()) {
            loadBooksFromFile("C:\\Users\\Maha\\Desktop\\LibraryApp\\src\\main\\java\\books.txt");
            if (books.isEmpty()) {
                nextBarcode = 1;
            } else {
                nextBarcode = books.stream().mapToInt(Book::getBarcode).max().getAsInt() + 1;
            }
        }
    }

    /** Method loadBookFromFIle Loads book data from a text file into the library's collection.
     * @param filename
     * @return none
     * */
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

    /** saveBooksToFile method saves changes or any new books added to the original database text file
     * @param filename
     * @return none
     * */
    public void saveBooksToFile(String filename) {
        try {
            File file = new File(filename);
            System.out.println("Saving books to file: " + file.getAbsolutePath());
            FileWriter fw = new FileWriter(file);
            for (Book book : books) {
                fw.write(book.getBarcode() + ", " + book.getTitle() + ", " + book.getAuthor() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Method saveChanges saves the books to the file and is called when needed
     * @return none
     * */
    public void saveChanges() {
        saveBooksToFile("C:\\Users\\Maha\\Desktop\\LibraryApp\\src\\main\\java\\books.txt");
    }

    /** Method checkOutBook is used to check out a book by title.
     * @param  title
     * @return true (if successfully checked out), false
     */
    public boolean checkOutBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                if ("checked in".equalsIgnoreCase(book.getStatus())) {
                    book.setStatus("checked out");
                    dueDate = LocalDate.now().plusWeeks(4); // Set due date to 4 weeks from now
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



    /** Method checkInBook checks in a book by title.
     * @param title
     * @return true (if successfully checked in), false
     */
    public boolean checkInBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                if ("checked out".equalsIgnoreCase(book.getStatus())) {
                    book.setStatus("checked in");
                    book.setDueDate(null); // Set due date to null when checking in
                    return true; // Book checked in successfully
                } else {
                    return false; // Book is already checked in
                }
            }
        }
        return false; // Book not found
    }
}
