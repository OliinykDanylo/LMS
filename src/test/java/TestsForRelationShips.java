import Entities.*;
import Repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to validate the relationships between various entities in the library system.
 * This class includes tests for user borrowings, book copies, user-librarian relationships, and publisher-books relationships.
 */
class TestForRelationships {
    UserRepo userRepo = new UserRepo();
    BorrowingRepo borrowingRepo = new BorrowingRepo();
    BookRepo bookRepo = new BookRepo();
    CopyRepo copyRepo = new CopyRepo();
    PublisherRepo publisherRepo = new PublisherRepo();
    LibrarianRepo librarianRepo = new LibrarianRepo();

    /**
     * Clears the database before each test.
     * Ensures a clean state for testing by deleting all records from the relevant tables.
     */
    @BeforeEach
    void clearDatabase() {
        EntityManager em = Persistence.createEntityManagerFactory("LMS-PU").createEntityManager();
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Borrowing").executeUpdate();
        em.createQuery("DELETE FROM Librarian").executeUpdate();
        em.createQuery("DELETE FROM Copy").executeUpdate();
        em.createQuery("DELETE FROM Book").executeUpdate();
        em.createQuery("DELETE FROM Publisher").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Tests the relationship between a user and their borrowings.
     * Verifies that borrowings reference the correct user and the user has the expected number of borrowings.
     */
    @Test
    void testUserBorrowingsRelationship() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("123456789");
        user.setAddress("123 Elm Street");
        userRepo.createUser(user);

        Publisher publisher = new Publisher();
        publisher.setName("Sample Publisher");
        publisher.setAddress("123 Publisher St");
        publisher.setPhoneNumber("555-1234");
        publisherRepo.createPublisher(publisher);

        Book book = new Book();
        book.setTitle("1984");
        book.setAuthor("George Orwell");
        book.setPublisherEntity(publisher);
        book.setPublicationYear(1949);
        book.setIsbn("9780451524935");
        bookRepo.createBook(book);

        Copy copy1 = new Copy();
        copy1.setBook(book);
        copy1.setCopyNumber(1);
        copy1.setStatus("Available");
        copyRepo.createCopy(copy1);

        Copy copy2 = new Copy();
        copy2.setBook(book);
        copy2.setCopyNumber(2);
        copy2.setStatus("Available");
        copyRepo.createCopy(copy2);

        Borrowing borrowing1 = new Borrowing();
        borrowing1.setUser(user);
        borrowing1.setCopy(copy1);
        borrowing1.setBorrowDate(new Date());
        borrowingRepo.createBorrowing(borrowing1);

        Borrowing borrowing2 = new Borrowing();
        borrowing2.setUser(user);
        borrowing2.setCopy(copy2);
        borrowing2.setBorrowDate(new Date());
        borrowingRepo.createBorrowing(borrowing2);

        List<Borrowing> borrowings = borrowingRepo.getAllBorrowings();

        assertEquals(2, borrowings.size(), "User should have two borrowings.");
        assertEquals(user.getId(), borrowings.get(0).getUser().getId(), "Borrowing should reference the correct user.");
    }

    /**
     * Tests the relationship between a book and its copies.
     * Verifies that a book has the correct number of copies and that each copy references the correct book.
     */
    @Test
    void testBookCopiesRelationship() {
        Publisher publisher = new Publisher();
        publisher.setName("Sample Publisher");
        publisher.setAddress("123 Publisher St");
        publisher.setPhoneNumber("555-1234");
        publisherRepo.createPublisher(publisher);

        Book book = new Book();
        book.setTitle("1984");
        book.setAuthor("George Orwell");
        book.setPublisherEntity(publisher);
        book.setPublicationYear(1949);
        book.setIsbn("9780451524935");
        bookRepo.createBook(book);

        Copy copy1 = new Copy();
        copy1.setBook(book);
        copy1.setCopyNumber(1);
        copy1.setStatus("Available");
        copyRepo.createCopy(copy1);

        Copy copy2 = new Copy();
        copy2.setBook(book);
        copy2.setCopyNumber(2);
        copy2.setStatus("Borrowed");
        copyRepo.createCopy(copy2);

        List<Copy> copies = copyRepo.getAllCopies();

        assertEquals(2, copies.size(), "Book should have two copies.");
        assertEquals(book.getId(), copies.get(0).getBook().getId(), "Copy should reference the correct book.");
    }

    /**
     * Tests the relationship between a user and their librarian role.
     * Verifies that a librarian references the correct user and has the expected attributes.
     */
    @Test
    void testUserLibrarianRelationship() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("123456789");
        user.setAddress("123 Elm Street");
        userRepo.createUser(user);

        Librarian librarian = new Librarian();
        librarian.setUser(user);
        librarian.setEmploymentDate(java.sql.Date.valueOf("2021-01-01"));
        librarian.setPosition("Chief Librarian");
        librarianRepo.createLibrarian(librarian);

        Librarian retrievedLibrarian = librarianRepo.findLibrarianById(librarian.getId());

        assertNotNull(retrievedLibrarian, "Librarian should exist.");
        assertEquals(user.getId(), retrievedLibrarian.getUser().getId(), "Librarian should reference the correct user.");
    }

    /**
     * Tests the relationship between a publisher and the books published by them.
     * Verifies that a publisher has the correct number of books and that each book references the correct publisher.
     */
    @Test
    void testPublisherBooksRelationship() {
        Publisher publisher = new Publisher();
        publisher.setName("Sample Publisher");
        publisher.setAddress("123 Publisher St");
        publisher.setPhoneNumber("555-1234");
        publisherRepo.createPublisher(publisher);

        Book book = new Book();
        book.setTitle("1984");
        book.setAuthor("George Orwell");
        book.setPublisherEntity(publisher);
        book.setPublicationYear(1949);
        book.setIsbn("9780451524935");
        bookRepo.createBook(book);

        Book book2 = new Book();
        book2.setTitle("Book Two");
        book2.setAuthor("Author Two");
        book2.setPublicationYear(2002);
        book2.setIsbn("0987654321");
        book2.setPublisherEntity(publisher);
        bookRepo.createBook(book2);

        List<Book> books = bookRepo.getAllBooks();

        assertEquals(2, books.size(), "Publisher should have two books.");
        assertEquals(publisher.getId(), books.get(0).getPublisherEntity().getId(), "Book should reference the correct publisher.");
    }
}