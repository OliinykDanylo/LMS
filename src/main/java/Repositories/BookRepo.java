package Repositories;

import Entities.Book;
import jakarta.persistence.*;

import java.util.List;

/**
 * This class is used to handle all database actions for books.
 * It lets you add, update, find, or delete books in the system.
 */
public class BookRepo {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("LMS-PU");

    /**
     * Adds a new book to the database.
     *
     * @param book the book to be added.
     */
    public void createBook(Book book) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(book); // Save the book in the database
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Finds a book in the database using its ID.
     *
     * @param id the unique ID of the book.
     * @return the book if it exists, or null if it doesn't.
     */
    public Book findBookById(Integer id) {
        EntityManager em = emf.createEntityManager();
        Book book = em.find(Book.class, id); // Look for the book by its ID
        em.close();
        return book;
    }

    /**
     * Gets a list of all the books in the database.
     *
     * @return a list of all books.
     */
    public List<Book> getAllBooks() {
        EntityManager em = emf.createEntityManager();
        List<Book> books = em.createQuery("SELECT b FROM Book b", Book.class).getResultList(); // Fetch all books
        em.close();
        return books;
    }

    /**
     * Updates the details of an existing book in the database.
     *
     * @param book the book with updated details.
     */
    public void updateBook(Book book) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(book); // Update the book details
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Deletes a book from the database if it doesn't have any copies.
     *
     * @param id the unique ID of the book to delete.
     * @throws PersistenceException if the book has copies and cannot be deleted.
     */
    public void deleteBook(Integer id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Book book = em.find(Book.class, id); // Find the book
        if (book != null && !book.getCopies().isEmpty()) { // Check if it has copies
            em.close();
            throw new PersistenceException("Cannot delete book with associated copies.");
        }
        if (book != null) {
            em.remove(book); // Remove the book if it exists and has no copies
        }
        em.getTransaction().commit();
        em.close();
    }
}