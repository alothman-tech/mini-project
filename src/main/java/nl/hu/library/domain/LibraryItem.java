package nl.hu.library.domain;

public abstract class LibraryItem implements Loanable {
    private final int id;
    private String title;
    private int year;
    private boolean available = true;

    protected LibraryItem(int id, String title, int year) {
        this.id = id;
        this.title = title;
        this.year = year;
    }

    @Override
    public void borrow() {
        if (!available) throw new IllegalStateException("Item is al uitgeleend");
        available = false;
    }

    @Override
    public void returnItem() {
        if (available) throw new IllegalStateException("Item is niet uitgeleend");
        available = true;
    }

    @Override public boolean isAvailable() { return available; }
    public int getId() { return id; }
    public String getTitle() { return title; }
    public int getYear() { return year; }
    public void setTitle(String title) { this.title = title; }
    public void setYear(int year) { this.year = year; }
}
