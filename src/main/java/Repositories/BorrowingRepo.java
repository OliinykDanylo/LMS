package Repositories;

import Entities.Borrowing;
import Entities.Copy;
import Entities.User;
import jakarta.persistence.*;

import java.util.List;

/**
 * This class handles database operations related to borrowings.
 * It includes methods to create, update, find, and delete borrowings.
 */
public class BorrowingRepo {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("LMS-PU");

    /**
     * Creates a new borrowing record in the database.
     * Also updates the copy status to "Borrowed".
     *
     * @param borrowing the borrowing to be created.
     * @throws IllegalStateException if the user or copy is not valid or already borrowed.
     */
    public void createBorrowing(Borrowing borrowing) {
        if (borrowing.getBorrowDate() == null) {
            throw new IllegalArgumentException("Borrow date cannot be null.");
        }

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            System.out.println("Inside createBorrowing:");
            System.out.println("User ID: " + borrowing.getUser().getId());
            System.out.println("Copy ID: " + borrowing.getCopy().getId());

            User managedUser = em.find(User.class, borrowing.getUser().getId());
            Copy managedCopy = em.createQuery(
                            "SELECT c FROM Copy c WHERE c.id = :id", Copy.class)
                    .setParameter("id", borrowing.getCopy().getId())
                    .getSingleResult();

            if (managedUser == null || managedCopy == null) {
                throw new IllegalStateException("User or Copy is not managed by the EntityManager.");
            }

            if ("Borrowed".equals(managedCopy.getStatus())) {
                Long activeBorrowingCount = em.createQuery(
                                "SELECT COUNT(b) FROM Borrowing b WHERE b.copy.id = :copyId AND b.returnDate IS NULL", Long.class)
                        .setParameter("copyId", managedCopy.getId())
                        .getSingleResult();

                if (activeBorrowingCount > 0) {
                    throw new IllegalStateException("The book copy is already borrowed.");
                }
            }

            managedCopy.setStatus("Borrowed");
            borrowing.setUser(managedUser);
            borrowing.setCopy(managedCopy);

            em.persist(borrowing);
            em.merge(managedCopy);
            em.flush();
            em.getTransaction().commit();
            System.out.println("Borrowing created successfully!");
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Finds a borrowing by its ID.
     *
     * @param id the ID of the borrowing to find.
     * @return the borrowing if found, or null otherwise.
     */
    public Borrowing findBorrowingById(Integer id) {
        EntityManager em = emf.createEntityManager();
        Borrowing borrowing = em.find(Borrowing.class, id);
        em.close();
        return borrowing;
    }

    /**
     * Retrieves all borrowings from the database.
     *
     * @return a list of all borrowings.
     */
    public List<Borrowing> getAllBorrowings() {
        EntityManager em = emf.createEntityManager();
        List<Borrowing> borrowings = em.createQuery("SELECT b FROM Borrowing b", Borrowing.class).getResultList();
        em.close();
        return borrowings;
    }

    /**
     * Updates a borrowing record. This is mainly used for setting the return date.
     *
     * @param borrowing the borrowing to update.
     * @throws IllegalArgumentException if the borrowing is not found.
     * @throws IllegalStateException if the return date is before the borrow date.
     */
    public void updateBorrowing(Borrowing borrowing) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            Borrowing managedBorrowing = em.find(Borrowing.class, borrowing.getId());
            if (managedBorrowing == null) {
                throw new IllegalArgumentException("Borrowing record not found.");
            }

            if (borrowing.getReturnDate() != null &&
                    borrowing.getReturnDate().before(borrowing.getBorrowDate())) {
                throw new IllegalStateException("Return date cannot be earlier than borrow date.");
            }

            managedBorrowing.setReturnDate(borrowing.getReturnDate());
            em.merge(managedBorrowing);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    /**
     * Deletes a borrowing record from the database.
     *
     * @param id the ID of the borrowing to delete.
     */
    public void deleteBorrowing(Integer id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Borrowing borrowing = em.find(Borrowing.class, id);
        if (borrowing != null) {
            em.remove(borrowing);
        }
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Retrieves all borrowings by a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of borrowings for the user.
     */
    public List<Borrowing> getBorrowingsByUser(int userId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT b FROM Borrowing b WHERE b.user.id = :userId",
                            Borrowing.class
                    )
                    .setParameter("userId", userId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Finds an active borrowing for a user by the book's title.
     *
     * @param title the title of the book.
     * @param userId the ID of the user.
     * @return the active borrowing record.
     */
    public Borrowing findBorrowingByTitleAndUser(String title, int userId) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT b FROM Borrowing b WHERE b.copy.book.title = :title AND b.user.id = :userId AND b.returnDate IS NULL",
                            Borrowing.class)
                    .setParameter("title", title)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}