package nl.hu.library.domain;

public interface Loanable {
    void borrow();
    void returnItem();
    boolean isAvailable();
}
