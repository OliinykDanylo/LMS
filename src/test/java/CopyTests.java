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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for validating copy functionalities in the library system.
 * This class tests various operations on book copies, such as creating, updating,
 * deleting, and enforcing constraints.
 */
public class CopyTests {

    CopyRepo copyRepo = new CopyRepo();
    BookRepo bookRepo = new BookRepo();
    PublisherRepo publisherRepo = new PublisherRepo();

    /**
     * Clears the database before each test.
     * Ensures a clean and consistent state for all test cases.
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
     * Tests the creation of a copy.
     * Verifies that the copy is created and can be retrieved from the database.
     */
    @Test
    void testCreateCopy() {
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

        Copy retrievedCopy = copyRepo.findCopyById(copy.getId());
        assertNotNull(retrievedCopy, "Copy should be created and found in the database.");
        assertEquals("Available", retrievedCopy.getStatus());
        assertEquals(book.getId(), retrievedCopy.getBook().getId());
    }

    /**
     * Tests the update functionality of a copy.
     * Verifies that the status of the copy can be updated successfully.
     */
    @Test
    void testUpdateCopy() {
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

        copy.setStatus("Borrowed");
        copyRepo.updateCopy(copy);

        Copy updatedCopy = copyRepo.findCopyById(copy.getId());
        assertNotNull(updatedCopy, "Updated copy should be found.");
        assertEquals("Borrowed", updatedCopy.getStatus());
    }

    /**
     * Tests the deletion of a copy.
     * Verifies that the copy is removed from the database.
     */
    @Test
    void testDeleteCopy() {
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

        copyRepo.deleteCopy(copy.getId());

        assertNull(copyRepo.findCopyById(copy.getId()), "Copy should be deleted successfully.");
    }

    /**
     * Tests the behavior when mandatory fields in a copy are null.
     * Verifies that an exception is thrown when trying to create a copy with null mandatory fields.
     */
    @Test
    void testNullMandatoryFields() {
        Copy copy = new Copy();
        copy.setBook(null);
        copy.setCopyNumber(1);
        copy.setStatus("Available");

        Exception exception = assertThrows(Exception.class, () -> copyRepo.createCopy(copy));
        assertNotNull(exception, "Creating a copy with null mandatory fields should throw an exception.");
    }

    /**
     * Tests the unique constraint on copyNumber for a book.
     * Verifies that duplicate copyNumbers for the same book are not allowed.
     */
    @Test
    void testUniqueCopyNumberPerBook() {
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
        copy2.setCopyNumber(1);
        copy2.setStatus("Available");

        Exception exception = assertThrows(Exception.class, () -> copyRepo.createCopy(copy2));
        assertNotNull(exception, "Creating a copy with duplicate copyNumber for the same book should throw an exception.");
    }
}