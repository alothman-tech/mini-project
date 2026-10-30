package nl.hu.library.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LibraryItemTest {
    @Test
    void borrowMakesItemUnavailable() {
        LibraryItem book = new Book(1, "Clean Code", 2008, "Robert C. Martin");
        book.borrow();
        assertFalse(book.isAvailable());
    }

    @Test
    void borrowingTwiceThrows() {
        LibraryItem mag = new Magazine(2, "Wired", 2024, 7);
        mag.borrow();
        assertThrows(IllegalStateException.class, mag::borrow);
    }

    @Test
    void returnMakesItemAvailableAgain() {
        LibraryItem book = new Book(3, "Refactoring", 1999, "Martin Fowler");
        book.borrow();
        book.returnItem();
        assertTrue(book.isAvailable());
    }

    @Test
    void returningItemThatIsNotBorrowedThrows() {
        LibraryItem book = new Book(4, "DDD", 2003, "Eric Evans");
        assertThrows(IllegalStateException.class, book::returnItem);
    }

    @Test
    void bookAndMagazineAreLoanable() {
        Loanable book = new Book(5, "A", 2000, "B");
        Loanable mag = new Magazine(6, "C", 2001, 1);
        assertTrue(book.isAvailable());
        assertTrue(mag.isAvailable());
    }

    @Test
    void subclassesKeepTheirOwnProperties() {
        Book book = new Book(7, "T", 2010, "Auteur");
        Magazine mag = new Magazine(8, "M", 2011, 12);
        assertEquals("Auteur", book.getAuthor());
        assertEquals(12, mag.getIssueNumber());
    }
}
