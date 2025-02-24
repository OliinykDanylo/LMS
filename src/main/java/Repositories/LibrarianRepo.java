package Repositories;

import Entities.Librarian;
import Entities.User;
import jakarta.persistence.*;

import java.util.List;

/**
 * Handles database operations for Librarian entities.
 */
public class LibrarianRepo {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("LMS-PU");

    /**
     * Creates a new librarian in the database.
     *
     * @param librarian the librarian to be created.
     */
    public void createLibrarian(Librarian librarian) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(librarian);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Finds a librarian by their ID.
     *
     * @param id the ID of the librarian to find.
     * @return the found librarian or null if not found.
     */
    public Librarian findLibrarianById(Integer id) {
        EntityManager em = emf.createEntityManager();
        Librarian librarian = em.find(Librarian.class, id);
        em.close();
        return librarian;
    }

    /**
     * Retrieves all librarians from the database.
     *
     * @return a list of all librarians.
     */
    public List<Librarian> getAllLibrarians() {
        EntityManager em = emf.createEntityManager();
        List<Librarian> librarians = em.createQuery("SELECT l FROM Librarian l", Librarian.class).getResultList();
        em.close();
        return librarians;
    }

    /**
     * Updates an existing librarian's details in the database.
     *
     * @param librarian the librarian with updated details.
     */
    public void updateLibrarian(Librarian librarian) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(librarian);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Deletes a librarian from the database by their ID. Also removes the association
     * between the librarian and their user.
     *
     * @param id the ID of the librarian to delete.
     */
    public void deleteLibrarian(Integer id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Librarian librarian = em.find(Librarian.class, id);
        if (librarian != null) {
            User user = librarian.getUser();
            if (user != null) {
                user.setLibrarian(null); // Remove the association with the user
                em.merge(user); // Update the user in the database
            }

            em.remove(librarian); // Remove the librarian
        }

        em.getTransaction().commit();
        em.close();
    }
}