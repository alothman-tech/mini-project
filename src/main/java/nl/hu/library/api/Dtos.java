package nl.hu.library.api;

import nl.hu.library.domain.Book;
import nl.hu.library.domain.LibraryItem;
import nl.hu.library.domain.Loan;
import nl.hu.library.domain.Magazine;

/** Data Transfer Objects: wat er als JSON over de lijn gaat. */
public final class Dtos {
    private Dtos() { }

    public record LoginRequest(String username, String password) { }

    public record LoginResponse(String token, String username, String role) { }

    public record LoanRequest(Integer itemId) { }

    public record ItemDto(Integer id, String type, String title, Integer year,
                          String author, Integer issueNumber, Boolean available) {

        public static String typeOf(LibraryItem item) {
            return item instanceof Book ? "BOOK" : "MAGAZINE";
        }

        public static ItemDto from(LibraryItem item) {
            String author = item instanceof Book b ? b.getAuthor() : null;
            Integer issue = item instanceof Magazine m ? m.getIssueNumber() : null;
            return new ItemDto(item.getId(), typeOf(item), item.getTitle(), item.getYear(),
                    author, issue, item.isAvailable());
        }
    }

    public record LoanDto(int id, int itemId, String itemTitle, String username,
                          String borrowedAt, String returnedAt) {

        public static LoanDto from(Loan loan) {
            return new LoanDto(loan.getId(), loan.getItemId(), loan.getItemTitle(), loan.getUsername(),
                    loan.getBorrowedAt().toString(),
                    loan.getReturnedAt() == null ? null : loan.getReturnedAt().toString());
        }
    }
}
