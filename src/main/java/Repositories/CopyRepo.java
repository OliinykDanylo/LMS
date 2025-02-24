package Repositories;

import Entities.Book;
import Entities.Copy;
import jakarta.persistence.*;

import java.util.List;

/**
 * This class handles database operations for managing copies of books.
 */
public class CopyRepo {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("LMS-PU");

    /**
     * Creates a new copy of a book in the database.
     *
     * @param copy the copy to be created.
     */
    public void createCopy(Copy copy) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(copy);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Finds a copy by its ID.
     *
     * @param id the ID of the copy to find.
     * @return the found copy or null if not found.
     */
    public Copy findCopyById(Integer id) {
        EntityManager em = emf.createEntityManager();
        Copy copy = em.find(Copy.class, id);
        em.close();
        return copy;
    }

    /**
     * Retrieves all copies from the database.
     *
     * @return a list of all copies.
     */
    public List<Copy> getAllCopies() {
        EntityManager em = emf.createEntityManager();
        List<Copy> copies = em.createQuery("SELECT c FROM Copy c", Copy.class).getResultList();
        em.close();
        return copies;
    }

    /**
     * Updates an existing copy's details in the database.
     *
     * @param copy the copy with updated details.
     * @throws IllegalArgumentException if the copy is not found.
     */
    public void updateCopy(Copy copy) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        try {
            Copy managedCopy = em.createQuery(
                            "SELECT c FROM Copy c LEFT JOIN FETCH c.borrowings WHERE c.id = :id", Copy.class)
                    .setParameter("id", copy.getId())
                    .getSingleResult();

            if (managedCopy == null) {
                throw new IllegalArgumentException("Copy not found in the database.");
            }

            managedCopy.setStatus(copy.getStatus());
            managedCopy.setCopyNumber(copy.getCopyNumber());

            if (copy.getBook() != null) {
                Book managedBook = em.find(Book.class, copy.getBook().getId());
                managedCopy.setBook(managedBook);
            }

            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Deletes a copy from the database by its ID.
     *
     * @param id the ID of the copy to delete.
     */
    public void deleteCopy(Integer id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Copy copy = em.find(Copy.class, id);
        if (copy != null) {
            em.remove(copy);
        }

        em.getTransaction().commit();
        em.close();
    }

    /**
     * Retrieves all copies with the status "Available".
     *
     * @return a list of available copies.
     */
    public List<Copy> getAvailableCopies() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT c FROM Copy c WHERE c.status = 'Available'", Copy.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Finds a copy by its copy number and book ID, including borrowings.
     *
     * @param copyNumber the number of the copy.
     * @param bookId the ID of the book the copy belongs to.
     * @return the found copy or null if no copy matches.
     * @throws IllegalStateException if multiple copies match the given details.
     */
    public Copy findCopyByNumberWithBorrowings(int copyNumber, int bookId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT c FROM Copy c LEFT JOIN FETCH c.borrowings WHERE c.copyNumber = :copyNumber AND c.book.id = :bookId",
                            Copy.class
                    )
                    .setParameter("copyNumber", copyNumber)
                    .setParameter("bookId", bookId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException e) {
            throw new IllegalStateException("Multiple copies found for the same copyNumber and bookId.", e);
        } finally {
            em.close();
        }
    }
}