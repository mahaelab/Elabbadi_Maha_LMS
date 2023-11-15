import java.time.LocalDate;

/**
 *  Maha Elabbadi, CEN-3024C-14835, 11/14/23
 * The Book class represents an individual book with the attributes barcode , title, author, status, and due date.
 * Used to create book objects that store data about each book in the library.
 * */
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
