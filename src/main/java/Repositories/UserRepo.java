package Repositories;

import Entities.User;
import jakarta.persistence.*;

import java.util.List;

/**
 * Handles database operations for User entities.
 */
public class UserRepo {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("LMS-PU");

    /**
     * Adds a new user to the database.
     *
     * @param user the user to add.
     */
    public void createUser(User user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user to find.
     * @return the found user, or null if not found.
     */
    public User findUserById(Integer id) {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, id);
        em.close();
        return user;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all users.
     */
    public List<User> getAllUsers() {
        EntityManager em = emf.createEntityManager();
        List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();
        em.close();
        return users;
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user the user with updated details.
     */
    public void updateUser(User user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(user);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Deletes a user from the database. A user cannot be deleted if they are associated with
     * borrowings or if they are a librarian.
     *
     * @param id the ID of the user to delete.
     * @throws PersistenceException if the user has borrowings or is a librarian.
     */
    public void deleteUser(Integer id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        User user = em.find(User.class, id);
        if (user != null) {
            boolean hasBorrowings = !user.getBorrowings().isEmpty();
            boolean isLibrarian = user.getLibrarian() != null;

            if (hasBorrowings || isLibrarian) {
                em.close();
                throw new PersistenceException("Cannot delete user associated with borrowings or librarian.");
            }

            em.remove(user);
        }

        em.getTransaction().commit();
        em.close();
    }

    /**
     * Checks if a user is a librarian.
     *
     * @param userId the ID of the user to check.
     * @return true if the user is a librarian, false otherwise.
     */
    public boolean isLibrarian(int userId) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery(
                            "SELECT COUNT(l) FROM Librarian l WHERE l.user.id = :userId", Long.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }
}