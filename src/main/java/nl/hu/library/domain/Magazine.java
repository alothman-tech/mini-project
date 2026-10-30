package nl.hu.library.domain;

public class Magazine extends LibraryItem {
    private int issueNumber;

    public Magazine(int id, String title, int year, int issueNumber) {
        super(id, title, year);
        this.issueNumber = issueNumber;
    }

    public int getIssueNumber() { return issueNumber; }
    public void setIssueNumber(int issueNumber) { this.issueNumber = issueNumber; }
}
