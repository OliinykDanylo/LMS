package Entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a copy of a book in the library.
 * Each copy has a unique identifier, a reference to its book, a status, and a list of borrowings associated with it.
 */
@Entity
@Table(
        name = "Copies",
        uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "copyNumber"})
)
public class Copy {
    /**
     * The unique identifier for the copy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The book to which this copy belongs.
     */
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false, foreignKey = @ForeignKey(name = "fk_book_copy"))
    private Book book;

    /**
     * The unique number assigned to this copy of the book.
     */
    @Column(nullable = false)
    private Integer copyNumber;

    /**
     * The status of the copy (e.g., Available, Borrowed).
     */
    @Column(nullable = false)
    private String status;

    /**
     * The list of borrowings associated with this copy.
     */
    @OneToMany(mappedBy = "copy", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Borrowing> borrowings = new ArrayList<>();

    /**
     * Gets the list of borrowings associated with this copy.
     * @return the list of borrowings.
     */
    public List<Borrowing> getBorrowings() {
        return borrowings;
    }

    /**
     * Sets the list of borrowings associated with this copy.
     * @param borrowings the list of borrowings to set.
     */
    public void setBorrowings(List<Borrowing> borrowings) {
        this.borrowings = borrowings;
    }

    /**
     * Gets the unique identifier for the copy.
     * @return the unique identifier for the copy.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the copy.
     * @param id the unique identifier to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the book to which this copy belongs.
     * @return the book to which this copy belongs.
     */
    public Book getBook() {
        return book;
    }

    /**
     * Sets the book to which this copy belongs.
     * @param book the book to associate with this copy.
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Gets the unique number assigned to this copy.
     * @return the unique number of this copy.
     */
    public Integer getCopyNumber() {
        return copyNumber;
    }

    /**
     * Sets the unique number assigned to this copy.
     * @param copyNumber the unique number to assign to this copy.
     */
    public void setCopyNumber(Integer copyNumber) {
        this.copyNumber = copyNumber;
    }

    /**
     * Gets the status of the copy (e.g., Available, Borrowed).
     * @return the status of the copy.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status of the copy.
     * @param status the status to set (e.g., Available, Borrowed).
     */
    public void setStatus(String status) {
        this.status = status;
    }
}