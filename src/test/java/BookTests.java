import Entities.Book;
import Entities.Copy;
import Entities.Publisher;
import Repositories.BookRepo;
import Repositories.CopyRepo;
import Repositories.PublisherRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.PersistenceException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class validates the functionality of the Book repository.
 * It tests CRUD operations for books and ensures that business rules,
 * such as unique constraints and dependencies, are properly enforced.
 */
class BookTests {

    BookRepo bookRepo = new BookRepo();
    CopyRepo copyRepo = new CopyRepo();
    PublisherRepo publisherRepo = new PublisherRepo();

    /**
     * Clears the database before each test to ensure a clean state.
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
     * Test creating a book and verifies that it is persisted correctly.
     */
    @Test
    void testCreateBook() {
        Publisher publisher = new Publisher();
        publisher.setName("Sample Publisher");
        publisher.setAddress("123 Publisher St");
        publisher.setPhoneNumber("555-1234");
        publisherRepo.createPublisher(publisher);

        assertNotNull(publisher.getId(), "Publisher ID should not be null after persistence.");

        Book book = new Book();
        book.setTitle("1984");
        book.setAuthor("George Orwell");
        book.setPublisherEntity(publisher);
        book.setPublicationYear(1949);
        book.setIsbn("9780451524935");
        bookRepo.createBook(book);

        Book retrievedBook = bookRepo.findBookById(book.getId());
        assertNotNull(retrievedBook, "Book should be created and found in the database.");
        assertEquals("1984", retrievedBook.getTitle());
        assertEquals("George Orwell", retrievedBook.getAuthor());
        assertEquals(publisher.getId(), retrievedBook.getPublisherEntity().getId());
    }

    /**
     * Test retrieving all books from the database.
     */
    @Test
    void testReadBooks() {
        List<Book> books = bookRepo.getAllBooks();

        assertNotNull(books, "Books list should not be null.");
    }

    /**
     * Test updating an existing book's details.
     */
    @Test
    void testUpdateBook() {
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

        book.setTitle("Updated Title");
        bookRepo.updateBook(book);

        Book updatedBook = bookRepo.findBookById(book.getId());
        assertNotNull(updatedBook, "Updated book should be found.");
        assertEquals("Updated Title", updatedBook.getTitle());
        assertEquals("Sample Publisher", updatedBook.getPublisherEntity().getName());
    }

    /**
     * Test deleting a book that is not referenced by any other entity.
     */
    @Test
    void testDeleteBookNotReferenced() {
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
        book.setTitle("Updated Title");
        bookRepo.updateBook(book);

        bookRepo.deleteBook(book.getId());

        assertNull(bookRepo.findBookById(book.getId()), "Book should be deleted successfully.");
    }

    /**
     * Test deleting a book that is referenced by other entities (e.g., copies).
     * Ensures that a PersistenceException is thrown.
     */
    @Test
    void testDeleteBookReferenced() {
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

        Copy copy = new Copy();
        copy.setBook(book);
        copy.setCopyNumber(1);
        copy.setStatus("Available");
        copyRepo.createCopy(copy);

        PersistenceException exception = assertThrows(PersistenceException.class, () -> {
            bookRepo.deleteBook(book.getId());
        });

        assertNotNull(exception, "Deleting a referenced book should throw a PersistenceException.");
    }

    /**
     * Test that the ISBN field enforces uniqueness.
     * Attempts to create two books with the same ISBN and expects a PersistenceException.
     */
    @Test
    void testUniqueConstraintOnIsbn() {
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
        book2.setTitle("Book 2");
        book2.setAuthor("Author 2");
        book2.setPublicationYear(2020);
        book2.setPublisherEntity(publisher);
        book2.setIsbn("9780451524935"); // duplicate ISBN

        Exception exception = assertThrows(PersistenceException.class, () -> bookRepo.createBook(book2));
        assertNotNull(exception, "Creating a book with a duplicate ISBN should throw a PersistenceException.");
    }

    /**
     * Tests that attempting to create a book with null mandatory fields throws an exception.
     */
    @Test
    void testNullMandatoryFields() {
        Book book = new Book();
        book.setTitle(null);

        Exception exception = assertThrows(PersistenceException.class, () -> bookRepo.createBook(book));
        assertNotNull(exception, "Creating a book with null mandatory fields should throw an exception.");
    }
}