package Entities;

import jakarta.persistence.*;

import java.util.Date;

/**
 * Represents a borrowing record in the library management system.
 * A borrowing record tracks when a user borrows a specific book copy
 * and optionally when the book copy is returned.
 */
@Entity
@Table(name = "Borrowings")
public class Borrowing {
    /**
     * The unique identifier for the borrowing record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The user associated with this borrowing record.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_borrowing"))
    private User user;

    /**
     * The book copy associated with this borrowing record.
     */
    @ManyToOne
    @JoinColumn(name = "copy_id", nullable = false, foreignKey = @ForeignKey(name = "fk_copy_borrowing"))
    private Copy copy;

    /**
     * The date when the book copy was borrowed.
     */
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date borrowDate;

    /**
     * The date when the book copy was returned (optional).
     * If null, the book copy has not been returned yet.
     */
    @Column
    @Temporal(TemporalType.DATE)
    private Date returnDate;

    /**
     * Gets the unique identifier of the borrowing record.
     * @return the unique identifier of the borrowing record.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the borrowing record.
     * @param id the unique identifier to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the user associated with this borrowing record.
     * @return the user associated with this borrowing record.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user associated with this borrowing record.
     * @param user the user to associate with this borrowing record.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the book copy associated with this borrowing record.
     * @return the book copy associated with this borrowing record.
     */
    public Copy getCopy() {
        return copy;
    }

    /**
     * Sets the book copy associated with this borrowing record.
     * @param copy the book copy to associate with this borrowing record.
     */
    public void setCopy(Copy copy) {
        this.copy = copy;
    }

    /**
     * Gets the date when the book copy was borrowed.
     * @return the borrow date.
     */
    public Date getBorrowDate() {
        return borrowDate;
    }

    /**
     * Sets the date when the book copy was borrowed.
     * @param borrowDate the borrow date to set.
     */
    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    /**
     * Gets the date when the book copy was returned, or null if not yet returned.
     * @return the return date, or null if not returned.
     */
    public Date getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the date when the book copy was returned.
     * @param returnDate the return date to set.
     */
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
}