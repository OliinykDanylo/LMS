import Entities.Librarian;
import Entities.User;
import Repositories.LibrarianRepo;
import Repositories.UserRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for validating librarian functionalities in the library system.
 * This class tests various operations on librarians, such as creating, updating,
 * deleting, and ensuring constraints.
 */
public class LibrarianTests {

    LibrarianRepo librarianRepo = new LibrarianRepo();
    UserRepo userRepo = new UserRepo();

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
     * Tests the creation of a librarian.
     * Verifies that the librarian is created and can be retrieved from the database.
     */
    @Test
    void testCreateLibrarian() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("123456789");
        user.setAddress("123 Elm Street");
        userRepo.createUser(user);

        Librarian librarian = new Librarian();
        librarian.setUser(user);
        librarian.setEmploymentDate(java.sql.Date.valueOf("2020-01-01"));
        librarian.setPosition("Head Librarian");

        librarianRepo.createLibrarian(librarian);

        Librarian retrievedLibrarian = librarianRepo.findLibrarianById(librarian.getId());
        assertNotNull(retrievedLibrarian, "Librarian should be created and found in the database.");
        assertEquals("Head Librarian", retrievedLibrarian.getPosition());
        assertEquals(user.getId(), retrievedLibrarian.getUser().getId());
    }

    /**
     * Tests the update functionality of a librarian.
     * Verifies that the librarian's details, such as position, can be updated successfully.
     */
    @Test
    void testUpdateLibrarian() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("123456789");
        user.setAddress("123 Elm Street");
        userRepo.createUser(user);

        Librarian librarian = new Librarian();
        librarian.setUser(user);
        librarian.setEmploymentDate(java.sql.Date.valueOf("2021-01-01"));
        librarian.setPosition("Assistant Librarian");
        librarianRepo.createLibrarian(librarian);

        librarian.setPosition("Head Librarian");
        librarianRepo.updateLibrarian(librarian);

        Librarian updatedLibrarian = librarianRepo.findLibrarianById(librarian.getId());
        assertNotNull(updatedLibrarian, "Updated librarian should be found.");
        assertEquals("Head Librarian", updatedLibrarian.getPosition());
    }

    /**
     * Tests the deletion of a librarian.
     * Verifies that the librarian is removed from the database.
     */
    @Test
    void testDeleteLibrarian() {
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("123456789");
        user.setAddress("123 Elm Street");
        userRepo.createUser(user);

        Librarian librarian = new Librarian();
        librarian.setUser(user);
        librarian.setEmploymentDate(java.sql.Date.valueOf("2021-01-01"));
        librarian.setPosition("Assistant Librarian");
        librarianRepo.createLibrarian(librarian);

        librarianRepo.deleteLibrarian(librarian.getId());

        assertNull(librarianRepo.findLibrarianById(librarian.getId()), "Librarian should be deleted successfully.");
    }

    /**
     * Tests the behavior when mandatory fields in a librarian are null.
     * Verifies that an exception is thrown when trying to create a librarian with null mandatory fields.
     */
    @Test
    void testNullMandatoryFields() {
        Librarian librarian = new Librarian();
        librarian.setEmploymentDate(null);
        librarian.setPosition("Temporary Librarian");

        Exception exception = assertThrows(PersistenceException.class, () -> librarianRepo.createLibrarian(librarian));
        assertNotNull(exception, "Creating a librarian with null mandatory fields should throw an exception.");
    }

    /**
     * Tests reading all librarians from the database.
     * Verifies that the list of librarians is not null and can be retrieved successfully.
     */
    @Test
    void testReadLibrarians() {
        List<Librarian> librarians = librarianRepo.getAllLibrarians();

        assertNotNull(librarians, "Librarians list should not be null.");
    }
}