import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {

    @Test
    public void testAddBook() {
        Library library = new Library();
        String title = "Test Title";
        String author = "Test Author";
        library.addBook(title, author);
        assertTrue(library.getBooks().stream().anyMatch(book -> book.getTitle().equals(title) && book.getAuthor().equals(author)));
    }

    @Test
    public void testRemoveBookByBarcode() {
        Library library = new Library();
        int barcode = library.getNextBarcode();
        library.addBook("Test Title", "Test Author");
        boolean removed = library.removeBook(String.valueOf(barcode));
        assertTrue(removed);
        assertFalse(library.getBooks().stream().anyMatch(book -> book.getBarcode() == barcode));
    }

    @Test
    public void testRemoveBookByTitle() {
        Library library = new Library();
        String title = "Test Title";
        library.addBook(title, "Test Author");
        boolean removed = library.removeBook(title);
        assertTrue(removed);
        assertFalse(library.getBooks().stream().anyMatch(book -> book.getTitle().equals(title)));
    }

    @Test
    public void testCheckOutBook() {
        Library library = new Library();
        String title = "Test Title";
        library.addBook(title, "Test Author");
        boolean checkedOut = library.checkOutBook(title);
        assertTrue(checkedOut);
        Book checkedOutBook = library.getBooks().stream().filter(book -> book.getTitle().equals(title)).findFirst().orElse(null);
        assertNotNull(checkedOutBook);
        assertNotNull(checkedOutBook.getDueDate());
    }

    @Test
    public void testCheckInBook() {
        Library library = new Library();
        String title = "Test Title";
        library.addBook(title, "Test Author");
        library.checkOutBook(title); // Check out the book first
        boolean checkedIn = library.checkInBook(title);
        assertTrue(checkedIn);
        Book checkedInBook = library.getBooks().stream().filter(book -> book.getTitle().equals(title)).findFirst().orElse(null);
        assertNotNull(checkedInBook);
        assertNull(checkedInBook.getDueDate());
    }
}
