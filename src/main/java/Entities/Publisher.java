package Entities;

import jakarta.persistence.*;
import java.util.Set;

/**
 * Represents a publisher in the library system.
 * A publisher is responsible for producing books and has a unique name, address, and phone number.
 */
@Entity
@Table(name = "Publishers")
public class Publisher {
    /**
     * The unique identifier for the publisher.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The name of the publisher.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The address of the publisher.
     */
    @Column
    private String address;

    /**
     * The phone number of the publisher.
     */
    @Column
    private String phoneNumber;

    /**
     * The set of books published by this publisher.
     */
    @OneToMany(mappedBy = "publisherEntity", cascade = CascadeType.ALL)
    private Set<Book> books;

    /**
     * Gets the unique identifier for the publisher.
     *
     * @return the unique identifier for the publisher.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the publisher.
     *
     * @param id the unique identifier to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the name of the publisher.
     *
     * @return the name of the publisher.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the publisher.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the address of the publisher.
     *
     * @return the address of the publisher.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the publisher.
     *
     * @param address the address to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the phone number of the publisher.
     *
     * @return the phone number of the publisher.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the publisher.
     *
     * @param phoneNumber the phone number to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the set of books published by this publisher.
     *
     * @return the set of books published by this publisher.
     */
    public Set<Book> getBooks() {
        return books;
    }

    /**
     * Sets the set of books published by this publisher.
     *
     * @param books the set of books to set.
     */
    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}