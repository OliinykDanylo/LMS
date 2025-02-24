import Entities.*;
import Repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for verifying the functionality of the {@link UserRepo} and its interactions with the database.
 * Includes tests for creating, reading, updating, and deleting users, as well as validating constraints and relationships.
 */
class UserTests {

    UserRepo userRepo = new UserRepo();
    BorrowingRepo borrowingRepo = new BorrowingRepo();
    BookRepo bookRepo = new BookRepo();
    CopyRepo copyRepo = new CopyRepo();
    PublisherRepo publisherRepo = new PublisherRepo();
    LibrarianRepo librarianRepo = new LibrarianRepo();

    /**
     * Clears the database before each test to ensure a clean state.
     * Removes all records from tables associated with users, borrowings, books, copies, publishers, and librarians.
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
     * Tests the creation of a user and verifies that the user is successfully stored and retrievable from the database.
     */
    @Test
    void testCreateUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("123456789");
        user.setAddress("123 Elm Street");
        userRepo.createUser(user);

        User retrievedUser = userRepo.findUserById(user.getId());
        assertNotNull(retrievedUser, "User should be created and found in the database.");
        assertEquals("John Doe", retrievedUser.getName());
        assertEquals("john.doe@example.com", retrievedUser.getEmail());
    }

    /**
     * Tests the retrieval of all users from the database.
     * Ensures the list of users is not null.
     */
    @Test
    void testReadUsers() {
        List<User> users = userRepo.getAllUsers();

        assertNotNull(users, "Users list should not be null.");
    }

    /**
     * Tests updating an existing user's details in the database.
     * Verifies that the changes are correctly reflected.
     */
    @Test
    void testUpdateUser() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("123456789");
        user.setAddress("123 Elm Street");
        userRepo.createUser(user);

        user.setPhoneNumber("555-9999");
        userRepo.updateUser(user);

        User updatedUser = userRepo.findUserById(user.getId());
        assertNotNull(updatedUser, "Updated user should be found.");
        assertEquals("555-9999", updatedUser.getPhoneNumber());
    }

    /**
     * Tests deleting a user with no references in the database.
     * Ensures the user is successfully removed.
     */
    @Test
    void testDeleteUserNotReferenced() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("123456789");
        user.setAddress("123 Elm Street");
        userRepo.createUser(user);

        userRepo.deleteUser(user.getId());

        assertNull(userRepo.findUserById(user.getId()), "User should be deleted successfully.");
    }

    /**
     * Tests attempting to delete a user who is referenced by other entities (e.g., borrowings).
     * Expects a PersistenceException to be thrown.
     */
    @Test
    void testDeleteUserReferenced() {
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

        Borrowing borrowing = new Borrowing();
        borrowing.setUser(user);
        borrowing.setCopy(copy);
        borrowing.setBorrowDate(new Date());
        borrowingRepo.createBorrowing(borrowing);

        Exception exception = assertThrows(PersistenceException.class, () -> {
            userRepo.deleteUser(user.getId());
        });

        assertNotNull(exception, "Deleting a referenced user should throw a PersistenceException.");
    }

    /**
     * Tests creating a user with null mandatory fields.
     * Expects a jakarta.validation.ConstraintViolationException to be thrown.
     */
    @Test
    void testNullMandatoryFields() {
        User user = new User();
        user.setName(null);

        Exception exception = assertThrows(jakarta.validation.ConstraintViolationException.class, () -> userRepo.createUser(user));
        assertNotNull(exception, "Creating a user with null mandatory fields should throw a ConstraintViolationException.");
    }

    /**
     * Tests the unique constraint on the email field.
     * Verifies that creating a user with a duplicate email throws a {@link PersistenceException}.
     */
    @Test
    void testUniqueConstraintOnEmail() {
        User user1 = new User();
        user1.setName("John Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPhoneNumber("123456789");
        user1.setAddress("123 Elm Street");
        userRepo.createUser(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("john.doe@example.com"); // Duplicate email

        Exception exception = assertThrows(PersistenceException.class, () -> userRepo.createUser(user2));
        assertNotNull(exception, "Creating a user with a duplicate email should throw an exception.");
    }
}