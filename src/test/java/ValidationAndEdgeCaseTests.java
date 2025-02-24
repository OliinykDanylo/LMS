import Entities.*;
import Repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for validating edge cases and constraints in the Library Management System.
 * Covers scenarios involving invalid inputs, unique constraints, optional fields, and concurrency.
 */
class ValidationAndEdgeCaseTests {

    UserRepo userRepo = new UserRepo();
    BookRepo bookRepo = new BookRepo();
    BorrowingRepo borrowingRepo = new BorrowingRepo();
    CopyRepo copyRepo = new CopyRepo();
    PublisherRepo publisherRepo = new PublisherRepo();

    /**
     * Clears the database before each test to ensure a clean state.
     * Removes all records from related tables to avoid conflicts during testing.
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
     * Tests that creating a user with an invalid email format throws a {@link jakarta.validation.ConstraintViolationException}.
     */
    @Test
    void testInvalidEmail() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("invalid-email");
        user.setPhoneNumber("123456789");
        user.setAddress("123 Elm Street");

        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userRepo.createUser(user),
                "Creating a user with an invalid email should throw a ConstraintViolationException.");
    }

    /**
     * Tests the unique constraint on the email field.
     * Verifies that creating a user with a duplicate email logs the violation and throws an exception.
     */
    @Test
    void testUniqueConstraintOnEmailWithLogging() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("unique.email@example.com");
        user.setPhoneNumber("123456789");
        user.setAddress("123 Elm Street");
        userRepo.createUser(user);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("unique.email@example.com"); // Duplicate email
        user2.setPhoneNumber("987654321");
        user2.setAddress("456 Oak Street");

        try {
            userRepo.createUser(user2);
            fail("Expected ConstraintViolationException was not thrown.");
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            System.out.println("Constraint violated: " + e.getSQLException().getMessage());
            assertNotNull(e, "ConstraintViolationException should be thrown.");
        }
    }

    /**
     * Tests that creating a book with an invalid ISBN format throws a jakarta.validation.ConstraintViolationException.
     */
    @Test
    void testInvalidIsbn() {
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
        book.setIsbn("invalid-isbn");

        assertThrows(ConstraintViolationException.class, () -> bookRepo.createBook(book),
                "Creating a book with an invalid ISBN should throw a ConstraintViolationException.");
    }


    /**
     * Verifies that creating a borrowing record with null optional fields does not throw an exception.
     */
    @Test
    void testNullOptionalFields() {
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
        borrowing.setBorrowDate(new Date());
        borrowing.setReturnDate(null);
        borrowing.setUser(user);
        borrowing.setCopy(copy);

        assertDoesNotThrow(() -> borrowingRepo.createBorrowing(borrowing),
                "Creating a borrowing with null optional fields should not throw an exception.");
    }

    /**
     * Tests concurrent borrowing attempts to ensure proper handling of race conditions.
     * Verifies that the book copy is marked as borrowed correctly after both threads complete.
     *
     * @throws InterruptedException if a thread is interrupted while waiting.
     */
    @Test
    void testConcurrentBorrowing() throws InterruptedException {
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

        Runnable task1 = () -> {
            Borrowing borrowing = new Borrowing();
            borrowing.setBorrowDate(new Date());
            borrowing.setCopy(copy);
            borrowing.setUser(user);
            borrowingRepo.createBorrowing(borrowing);
            copy.setStatus("Borrowed");
            copyRepo.updateCopy(copy);
        };

        Runnable task2 = () -> {
            Borrowing borrowing = new Borrowing();
            borrowing.setBorrowDate(new Date());
            borrowing.setCopy(copy);
            borrowing.setUser(user);
            borrowingRepo.createBorrowing(borrowing);
            copy.setStatus("Borrowed");
            copyRepo.updateCopy(copy);
        };

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        Copy updatedCopy = copyRepo.findCopyById(copy.getId());
        assertEquals("Borrowed", updatedCopy.getStatus(),
                "The copy should have a status of 'Borrowed' after concurrency handling.");
    }

}