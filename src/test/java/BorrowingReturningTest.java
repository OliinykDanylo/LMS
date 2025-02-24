import Entities.*;
import Repositories.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for validating borrowing and returning functionalities.
 * This class tests different scenarios, including borrowing, returning,
 * and handling edge cases such as unavailable books.
 */
public class BorrowingReturningTest {
    private static UserRepo userRepo;
    private static CopyRepo copyRepo;
    private static BorrowingRepo borrowingRepo;
    private static PublisherRepo publisherRepo;
    private static BookRepo bookRepo;

    /**
     * Sets up the repositories before all tests are executed.
     * Ensures that repository instances are initialized.
     */
    @BeforeAll
    static void setup() {
        userRepo = new UserRepo();
        copyRepo = new CopyRepo();
        borrowingRepo = new BorrowingRepo();
        publisherRepo = new PublisherRepo();
        bookRepo = new BookRepo();
    }


    /**
     * Clears the database before each test.
     * Ensures a clean state for every test by removing all records from relevant tables.
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
     * Test the successful borrowing of a book.
     * Verifies that the borrowing record is created, and the book copy's status is updated to "Borrowed".
     */
    @Test
    void testBorrowBookSuccessfully() {
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

        assertNotNull(publisher.getId(), "Publisher ID should not be null after persistence.");

        Book book = new Book();
        book.setTitle("1984");
        book.setAuthor("George Orwell");
        book.setPublisherEntity(publisher);
        book.setPublicationYear(1949);
        book.setIsbn("9780451524935");
        bookRepo.createBook(book);

        assertNotNull(book.getId(), "Book ID should not be null after persistence.");

        Copy copy = new Copy();
        copy.setBook(book);
        copy.setCopyNumber(1);
        copy.setStatus("Available");
        copyRepo.createCopy(copy);

        assertNotNull(user, "User should exist for borrowing.");
        assertNotNull(copy, "Copy should exist for borrowing.");
        assertEquals("Available", copy.getStatus(), "Copy should be available for borrowing.");

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setCopy(copy);
        borrowing.setBorrowDate(new Date());

        borrowingRepo.createBorrowing(borrowing);

        Copy updatedCopy = copyRepo.findCopyById(copy.getId());
        assertNotNull(updatedCopy, "Updated copy should exist.");
        assertEquals("Borrowed", updatedCopy.getStatus(), "Copy status should be 'Borrowed' after borrowing.");

        Borrowing fetchedBorrowing = borrowingRepo.getAllBorrowings()
                .stream()
                .filter(b -> b.getUser().getId().equals(user.getId()) && b.getCopy().getId().equals(copy.getId()))
                .findFirst()
                .orElse(null);

        assertNotNull(fetchedBorrowing, "Borrowing record should exist.");
        assertEquals(user.getId(), fetchedBorrowing.getUser().getId(), "Borrowing should be associated with the correct user.");
        assertEquals(copy.getId(), fetchedBorrowing.getCopy().getId(), "Borrowing should be associated with the correct copy.");
    }

    /**
     * Test the successful returning of a borrowed book.
     * Verifies that the borrowing record is updated with a return date
     * and the book copy's status is updated to "Available".
     */
    @Test
    void testReturnBookSuccessfully() {
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

        assertNotNull(publisher.getId(), "Publisher ID should not be null after persistence.");

        Book book = new Book();
        book.setTitle("1984");
        book.setAuthor("George Orwell");
        book.setPublisherEntity(publisher);
        book.setPublicationYear(1949);
        book.setIsbn("9780451524935");
        bookRepo.createBook(book);

        assertNotNull(book.getId(), "Book ID should not be null after persistence.");

        Copy copy = new Copy();
        copy.setBook(book);
        copy.setCopyNumber(1);
        copy.setStatus("Available");
        copyRepo.createCopy(copy);

        assertNotNull(user, "User should exist for borrowing.");
        assertNotNull(copy, "Copy should exist for borrowing.");
        assertEquals("Available", copy.getStatus(), "Copy should be available for borrowing.");

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setCopy(copy);
        borrowing.setBorrowDate(new Date());

        borrowingRepo.createBorrowing(borrowing);

        borrowing.setReturnDate(new Date());
        borrowingRepo.updateBorrowing(borrowing);

        Copy returnedCopy = copyRepo.findCopyById(copy.getId());
        returnedCopy.setStatus("Available");
        copyRepo.updateCopy(returnedCopy);

        Borrowing updatedBorrowing = borrowingRepo.findBorrowingById(borrowing.getId());
        assertNotNull(updatedBorrowing.getReturnDate(), "Return date should be set.");
        assertEquals("Available", returnedCopy.getStatus(), "Copy status should be 'Available' after returning.");
    }

    /**
     * Test the borrowing of an unavailable book.
     * Verifies that an IllegalStateException is thrown when attempting to borrow a book
     * that is already marked as "Borrowed".
     */
    @Test
    void testBorrowUnavailableBook() {
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

        assertNotNull(publisher.getId(), "Publisher ID should not be null after persistence.");

        Book book = new Book();
        book.setTitle("1984");
        book.setAuthor("George Orwell");
        book.setPublisherEntity(publisher);
        book.setPublicationYear(1949);
        book.setIsbn("9780451524935");
        bookRepo.createBook(book);

        assertNotNull(book.getId(), "Book ID should not be null after persistence.");

        Copy copy = new Copy();
        copy.setBook(book);
        copy.setCopyNumber(1);
        copy.setStatus("Borrowed");
        copyRepo.createCopy(copy);

        assertNotNull(copy.getId(), "Copy should have been persisted.");
        assertEquals("Borrowed", copy.getStatus(), "Copy should be borrowed for this test.");

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setCopy(copy);
        borrowing.setBorrowDate(new Date());
        borrowingRepo.createBorrowing(borrowing);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            Borrowing newBorrowing = new Borrowing();
            newBorrowing.setUser(user);
            newBorrowing.setCopy(copy);
            newBorrowing.setBorrowDate(new Date());
            borrowingRepo.createBorrowing(newBorrowing);
        });

        assertEquals("The book copy is already borrowed.", exception.getMessage());
    }
}