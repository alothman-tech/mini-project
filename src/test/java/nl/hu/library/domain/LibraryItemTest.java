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
}
