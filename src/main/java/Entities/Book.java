package Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * Represents a book entity in the library system.
 * Each book has a title, author, publication year, ISBN, and is associated with a publisher.
 * A book can have multiple copies in the library.
 */
@Entity
@Table(name = "Books")
public class Book {

    /**
     * The unique identifier for the book.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The title of the book.
     * This field is mandatory.
     */
    @Column(nullable = false)
    private String title;

    /**
     * The author of the book.
     * This field is mandatory.
     */
    @Column(nullable = false)
    private String author;

    /**
     * The publication year of the book.
     * This field is mandatory.
     */
    @Column(nullable = false)
    private Integer publicationYear;

    /**
     * The ISBN of the book.
     * Must follow the standard ISBN format.
     * This field is mandatory and must be unique.
     */
    @Column(nullable = false, unique = true)
    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "Invalid ISBN format")
    private String isbn;

    /**
     * The name of the publisher for the book.
     * This field is mandatory.
     */
    @Column(nullable = false)
    private String publisher;

    /**
     * The list of copies associated with the book.
     * A book can have multiple copies available in the library.
     */
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<Copy> copies;

    /**
     * The publisher entity associated with the book.
     */
    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false, foreignKey = @ForeignKey(name = "fk_publisher_book"))
    private Publisher publisherEntity;

    /**
     * Gets the unique identifier of the book.
     * @return the ID of the book.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the book.
     * @param id the ID to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the title of the book.
     * @return the title of the book.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     * @param title the title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the author of the book.
     * @return the author of the book.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author of the book.
     * @param author the author to set.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the publication year of the book.
     * @return the publication year of the book.
     */
    public Integer getPublicationYear() {
        return publicationYear;
    }

    /**
     * Sets the publication year of the book.
     * @param publicationYear the publication year to set.
     */
    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    /**
     * Gets the ISBN of the book.
     * @return the ISBN of the book.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the book.
     * @param isbn the ISBN to set.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the list of copies associated with the book.
     * @return the list of copies.
     */
    public List<Copy> getCopies() {
        return copies;
    }

    /**
     * Sets the list of copies associated with the book.
     * @param copies the list of copies to set.
     */
    public void setCopies(List<Copy> copies) {
        this.copies = copies;
    }

    /**
     * Gets the publisher entity associated with the book.
     * @return the publisher entity.
     */
    public Publisher getPublisherEntity() {
        return publisherEntity;
    }

    /**
     * Sets the publisher entity associated with the book.
     * Automatically updates the publisher name.
     * @param publisherEntity the publisher entity to set.
     */
    public void setPublisherEntity(Publisher publisherEntity) {
        this.publisherEntity = publisherEntity;
        if (publisherEntity != null) {
            this.publisher = publisherEntity.getName();
        }
    }

    /**
     * Gets the name of the publisher for the book.
     * @return the publisher name.
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Sets the name of the publisher for the book.
     * @param publisher the publisher name to set.
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}