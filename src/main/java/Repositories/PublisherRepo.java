package Repositories;

import Entities.Publisher;
import jakarta.persistence.*;

import java.util.List;

/**
 * Handles database operations for Publisher entities.
 */
public class PublisherRepo {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("LMS-PU");

    /**
     * Adds a new publisher to the database.
     *
     * @param publisher the publisher to add.
     */
    public void createPublisher(Publisher publisher) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(publisher);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Finds a publisher by its ID.
     *
     * @param id the ID of the publisher to find.
     * @return the found publisher, or null if not found.
     */
    public Publisher findPublisherById(Integer id) {
        EntityManager em = emf.createEntityManager();
        Publisher publisher = em.find(Publisher.class, id);
        em.close();
        return publisher;
    }

    /**
     * Retrieves all publishers from the database.
     *
     * @return a list of all publishers.
     */
    public List<Publisher> getAllPublishers() {
        EntityManager em = emf.createEntityManager();
        List<Publisher> publishers = em.createQuery("SELECT p FROM Publisher p", Publisher.class).getResultList();
        em.close();
        return publishers;
    }

    /**
     * Updates an existing publisher in the database.
     *
     * @param publisher the publisher with updated details.
     */
    public void updatePublisher(Publisher publisher) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(publisher);
        em.getTransaction().commit();
        em.close();
    }

    /**
     * Finds a publisher by its name. If multiple publishers have the same name,
     * an exception is thrown.
     *
     * @param name the name of the publisher to find.
     * @return the found publisher, or null if not found.
     */
    public Publisher findPublisherByName(String name) {
        EntityManager em = emf.createEntityManager();
        try {
            List<Publisher> publishers = em.createQuery(
                            "SELECT p FROM Publisher p WHERE p.name = :name", Publisher.class)
                    .setParameter("name", name)
                    .getResultList();

            if (publishers.isEmpty()) {
                return null;
            } else if (publishers.size() > 1) {
                throw new NonUniqueResultException("Multiple publishers found with the name: " + name);
            }

            return publishers.get(0);
        } finally {
            em.close();
        }
    }

    /**
     * Deletes a publisher from the database. If the publisher has associated books,
     * it cannot be deleted.
     *
     * @param id the ID of the publisher to delete.
     * @throws PersistenceException if the publisher has associated books.
     */
    public void deletePublisher(Integer id) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Publisher publisher = em.find(Publisher.class, id);
        if (publisher != null && !publisher.getBooks().isEmpty()) {
            throw new PersistenceException("Cannot delete publisher with associated books.");
        }
        em.remove(publisher);
        em.getTransaction().commit();
        em.close();
    }
}