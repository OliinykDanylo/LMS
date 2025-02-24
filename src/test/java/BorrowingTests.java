import Entities.*;
import Repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for validating borrowing functionalities in the library system.
 * This class tests various scenarios such as creating, updating, deleting borrowings,
 * and validating mandatory fields and business rules.
 */
public class BorrowingTests {

    BorrowingRepo borrowingRepo = new BorrowingRepo();
    UserRepo userRepo = new UserRepo();
    BookRepo bookRepo = new BookRepo();
    CopyRepo copyRepo = new CopyRepo();
    PublisherRepo publisherRepo = new PublisherRepo();

    /**
     * Clears the database before each test execution.
     * Ensures a clean state for consistent test results.
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
     * Tests the creation of a borrowing record.
     * Verifies that the borrowing record is created and can be retrieved from the database.
     */
    @Test
    void testCreateBorrowing() {
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

        Copy copy = new Copy();
        copy.setBook(book);
        copy.setCopyNumber(1);
        copy.setStatus("Available");
        copyRepo.createCopy(copy);

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setCopy(copy);
        borrowing.setBorrowDate(new Date());

        borrowingRepo.createBorrowing(borrowing);

        Borrowing retrievedBorrowing = borrowingRepo.findBorrowingById(borrowing.getId());
        assertNotNull(retrievedBorrowing, "Borrowing should be created and found in the database.");
        assertEquals(user.getId(), retrievedBorrowing.getUser().getId());
        assertEquals(copy.getId(), retrievedBorrowing.getCopy().getId());
    }

    /**
     * Tests the update functionality of a borrowing record.
     * Verifies that the return date can be updated successfully.
     */
    @Test
    void testUpdateBorrowing() {
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

        Copy copy = new Copy();
        copy.setBook(book);
        copy.setCopyNumber(2);
        copy.setStatus("Available");
        copyRepo.createCopy(copy);

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setCopy(copy);
        borrowing.setBorrowDate(new Date());
        borrowingRepo.createBorrowing(borrowing);

        borrowing.setReturnDate(new Date());
        borrowingRepo.updateBorrowing(borrowing);

        Borrowing updatedBorrowing = borrowingRepo.findBorrowingById(borrowing.getId());
        assertNotNull(updatedBorrowing, "Updated borrowing should be found.");
        assertNotNull(updatedBorrowing.getReturnDate(), "Return date should be updated.");
    }

    /**
     * Tests the deletion of a borrowing record.
     * Verifies that the borrowing record is removed from the database.
     */
    @Test
    void testDeleteBorrowing() {
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

        Copy copy = new Copy();
        copy.setBook(book);
        copy.setCopyNumber(3);
        copy.setStatus("Available");
        copyRepo.createCopy(copy);

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setCopy(copy);
        borrowing.setBorrowDate(new Date());
        borrowingRepo.createBorrowing(borrowing);

        borrowingRepo.deleteBorrowing(borrowing.getId());

        assertNull(borrowingRepo.findBorrowingById(borrowing.getId()), "Borrowing should be deleted successfully.");
    }

    /**
     * Tests the behavior when mandatory fields in a borrowing record are null.
     * Verifies that an exception is thrown when trying to create a borrowing with null fields.
     */
    @Test
    void testNullMandatoryFields() {
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

        Copy copy = new Copy();
        copy.setBook(book);
        copy.setCopyNumber(3);
        copy.setStatus("Available");
        copyRepo.createCopy(copy);

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setCopy(copy);
        borrowing.setBorrowDate(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> borrowingRepo.createBorrowing(borrowing),
                "Creating a borrowing with null mandatory fields should throw an IllegalArgumentException.");
        assertNotNull(exception, "Exception should not be null.");
    }

    /**
     * Tests the borrow and return date validation.
     * Ensures that the return date cannot be earlier than the borrow date
     * and that the return date is updated correctly.
     */
    @Test
    void testBorrowAndReturnDates() {
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

        Copy copy = new Copy();
        copy.setBook(book);
        copy.setCopyNumber(1);
        copy.setStatus("Available");
        copyRepo.createCopy(copy);

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setCopy(copy);
        borrowing.setBorrowDate(new Date());

        borrowingRepo.createBorrowing(borrowing);

        assertNotNull(borrowing.getBorrowDate(), "Borrow date should be set.");

        borrowing.setReturnDate(new Date(borrowing.getBorrowDate().getTime() - 86400000L));
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            borrowingRepo.updateBorrowing(borrowing);
        });

        assertEquals("Return date cannot be earlier than borrow date.", exception.getMessage());

        borrowing.setReturnDate(new Date(borrowing.getBorrowDate().getTime() + 86400000L));
        borrowingRepo.updateBorrowing(borrowing);

        Borrowing updatedBorrowing = borrowingRepo.findBorrowingById(borrowing.getId());
        assertNotNull(updatedBorrowing.getReturnDate(), "Return date should be set.");
        assertTrue(updatedBorrowing.getReturnDate().after(updatedBorrowing.getBorrowDate()), "Return date should be after borrow date.");
    }
}