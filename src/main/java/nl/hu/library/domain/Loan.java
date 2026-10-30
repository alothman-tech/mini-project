package nl.hu.library.domain;

import java.time.LocalDate;

public class Loan {
    private final int id;
    private final int userId;
    private final String username;
    private final int itemId;
    private final String itemTitle;
    private final LocalDate borrowedAt;
    private final LocalDate returnedAt;

    public Loan(int id, int userId, String username, int itemId, String itemTitle,
                LocalDate borrowedAt, LocalDate returnedAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.borrowedAt = borrowedAt;
        this.returnedAt = returnedAt;
    }

    public boolean isReturned() { return returnedAt != null; }
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public int getItemId() { return itemId; }
    public String getItemTitle() { return itemTitle; }
    public LocalDate getBorrowedAt() { return borrowedAt; }
    public LocalDate getReturnedAt() { return returnedAt; }
}
