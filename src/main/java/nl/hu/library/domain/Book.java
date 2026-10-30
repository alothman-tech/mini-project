package nl.hu.library.domain;

public class Book extends LibraryItem {
    private String author;

    public Book(int id, String title, int year, String author) {
        super(id, title, year);
        this.author = author;
    }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
}
