import Entities.Book;
import Entities.Publisher;
import Repositories.BookRepo;
import Repositories.PublisherRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.persistence.PersistenceException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for validating publisher-related functionalities in the library system.
 * This class tests operations such as creating, updating, deleting, and retrieving publishers.
 */
class PublisherTests {

    PublisherRepo publisherRepo = new PublisherRepo();
    BookRepo bookRepo = new BookRepo();

    /**
     * Clears the database before each test.
     * Ensures a clean state for testing by removing all publishers and associated books.
     */
    @BeforeEach
    void clearDatabase() {
        List<Book> books = bookRepo.getAllBooks();
        for (Book book : books) {
            bookRepo.deleteBook(book.getId());
        }

        List<Publisher> publishers = publisherRepo.getAllPublishers();
        for (Publisher publisher : publishers) {
            publisherRepo.deletePublisher(publisher.getId());
        }
    }

    /**
     * Tests the creation of a publisher.
     * Verifies that the publisher is created and can be retrieved from the database.
     */
    @Test
    void testCreatePublisher() {
        Publisher publisher = new Publisher();
        publisher.setName("Sample Publisher");
        publisher.setAddress("123 Publisher St");
        publisher.setPhoneNumber("555-1234");
        publisherRepo.createPublisher(publisher);

        Publisher retrievedPublisher = publisherRepo.findPublisherById(publisher.getId());
        assertNotNull(retrievedPublisher, "Publisher should be created and found in the database.");
        assertEquals("Sample Publisher", retrievedPublisher.getName());
        assertEquals("123 Publisher St", retrievedPublisher.getAddress());
    }

    /**
     * Tests reading all publishers from the database.
     * Verifies that the list of publishers is not null and can be retrieved successfully.
     */
    @Test
    void testReadPublishers() {
        List<Publisher> publishers = publisherRepo.getAllPublishers();

        assertNotNull(publishers, "Publishers list should not be null.");
    }

    /**
     * Tests updating a publisher's details.
     * Verifies that the publisher's information, such as phone number, can be updated successfully.
     */
    @Test
    void testUpdatePublisher() {
        Publisher publisher = new Publisher();
        publisher.setName("Sample Publisher");
        publisher.setAddress("123 Publisher St");
        publisher.setPhoneNumber("555-1234");
        publisherRepo.createPublisher(publisher);

        publisher.setPhoneNumber("555-555-5555");
        publisherRepo.updatePublisher(publisher);

        Publisher updatedPublisher = publisherRepo.findPublisherById(publisher.getId());
        assertNotNull(updatedPublisher, "Updated publisher should be found.");
        assertEquals("555-555-5555", updatedPublisher.getPhoneNumber());
    }

    /**
     * Tests deleting a publisher that is not referenced by any books.
     * Verifies that the publisher can be deleted successfully from the database.
     */
    @Test
    void testDeletePublisherNotReferenced() {
        Publisher publisher = new Publisher();
        publisher.setName("Sample Publisher");
        publisher.setAddress("123 Publisher St");
        publisher.setPhoneNumber("555-1234");
        publisherRepo.createPublisher(publisher);

        publisherRepo.deletePublisher(publisher.getId());

        assertNull(publisherRepo.findPublisherById(publisher.getId()), "Publisher should be deleted successfully.");
    }

    /**
     * Tests deleting a publisher that is referenced by one or more books.
     * Verifies that attempting to delete a referenced publisher throws a PersistenceException.
     */
    @Test
    void testDeletePublisherReferenced() {
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

        Exception exception = assertThrows(PersistenceException.class, () -> {
            publisherRepo.deletePublisher(publisher.getId());
        });

        assertNotNull(exception, "Deleting a referenced publisher should throw a PersistenceException.");
    }

    /**
     * Tests creating a publisher with null mandatory fields.
     * Verifies that attempting to create a publisher without mandatory fields throws a PersistenceException.
     */
    @Test
    void testNullMandatoryFields() {
        Publisher publisher = new Publisher();
        publisher.setName(null);

        Exception exception = assertThrows(PersistenceException.class, () -> publisherRepo.createPublisher(publisher));
        assertNotNull(exception, "Creating a publisher with null mandatory fields should throw an exception.");
    }
}
