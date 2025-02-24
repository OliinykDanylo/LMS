package Entities;

import jakarta.persistence.*;

import java.util.Date;

/**
 * Represents a librarian in the library system.
 * A librarian is associated with a user and has specific employment details, such as the employment date and position.
 */
@Entity
@Table(name = "Librarians")
public class Librarian {
    /**
     * The unique identifier for the librarian.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * The user associated with this librarian.
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_librarian"))
    private User user;

    /**
     * The date the librarian was employed.
     */
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date employmentDate;

    /**
     * The position held by the librarian.
     */
    @Column(nullable = false)
    private String position;

    /**
     * Gets the unique identifier for the librarian.
     * @return the unique identifier for the librarian.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the librarian.
     * @param id the unique identifier to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the user associated with this librarian.
     * @return the associated user.
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user associated with this librarian.
     * @param user the user to associate with this librarian.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the employment date of the librarian.
     * @return the employment date.
     */
    public Date getEmploymentDate() {
        return employmentDate;
    }

    /**
     * Sets the employment date of the librarian.
     * @param employmentDate the employment date to set.
     */
    public void setEmploymentDate(Date employmentDate) {
        this.employmentDate = employmentDate;
    }

    /**
     * Gets the position held by the librarian.
     * @return the position of the librarian.
     */
    public String getPosition() {
        return position;
    }

    /**
     * Sets the position held by the librarian.
     * @param position the position to set.
     */
    public void setPosition(String position) {
        this.position = position;
    }
}