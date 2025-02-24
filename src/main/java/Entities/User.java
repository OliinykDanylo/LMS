package Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Represents a user in the library system.
 * A user can borrow books and, optionally, be a librarian with additional responsibilities.
 */
@Entity
@Table(name = "Users")
public class User {
    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The name of the user.
     */
    @Column(nullable = false)
    @NotNull
    private String name;

    /**
     * The email address of the user.
     * Must be unique and conform to a valid email format.
     */
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The phone number of the user.
     */
    @Column
    private String phoneNumber;

    /**
     * The address of the user.
     */
    @Column
    private String address;

    /**
     * The list of borrowings associated with the user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Borrowing> borrowings;

    /**
     * The librarian entity associated with the user, if the user is also a librarian.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Librarian librarian;

    /**
     * Gets the unique identifier for the user.
     *
     * @return the unique identifier for the user.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the user.
     *
     * @param id the unique identifier to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the name of the user.
     *
     * @return the name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email address of the user.
     *
     * @return the email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the phone number of the user.
     *
     * @return the phone number of the user.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the user.
     *
     * @param phoneNumber the phone number to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the address of the user.
     *
     * @return the address of the user.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the user.
     *
     * @param address the address to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the list of borrowings associated with the user.
     *
     * @return the list of borrowings.
     */
    public List<Borrowing> getBorrowings() {
        return borrowings;
    }

    /**
     * Sets the list of borrowings associated with the user.
     *
     * @param borrowings the list of borrowings to set.
     */
    public void setBorrowings(List<Borrowing> borrowings) {
        this.borrowings = borrowings;
    }

    /**
     * Gets the librarian entity associated with the user.
     *
     * @return the librarian entity, or null if the user is not a librarian.
     */
    public Librarian getLibrarian() {
        return librarian;
    }

    /**
     * Sets the librarian entity associated with the user.
     *
     * @param librarian the librarian entity to set.
     */
    public void setLibrarian(Librarian librarian) {
        this.librarian = librarian;
    }
}