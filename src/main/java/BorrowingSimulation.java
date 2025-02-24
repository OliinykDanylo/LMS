import Entities.Borrowing;
import Entities.Copy;
import Entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Date;

/**
 * A simulation program for testing borrowing operations in the Library Management System (LMS).
 * This program demonstrates how to borrow a book copy by a user and persists the borrowing record.
 */
public class BorrowingSimulation {

    // used for trying borrowings

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("LMS-PU");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            User user = em.find(User.class, 477);
            Copy copy = em.find(Copy.class, 314);

            if (user == null || copy == null) {
                throw new IllegalStateException("User or Copy not found.");
            }

            Borrowing borrowing = new Borrowing();
            borrowing.setUser(user);
            borrowing.setCopy(copy);
            borrowing.setBorrowDate(new Date());

            em.persist(borrowing);
            em.flush();
            em.getTransaction().commit();

            System.out.println("Borrowing added successfully: " + borrowing.getId());
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}