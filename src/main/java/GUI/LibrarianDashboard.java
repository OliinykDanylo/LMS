package GUI;

import Entities.Book;
import Entities.Borrowing;
import Entities.Copy;
import Entities.User;
import Repositories.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Represents the dashboard for librarians.
 * Allows managing books, users, and borrowed books.
 */
public class LibrarianDashboard extends JFrame {
    private JPanel mainPanel;
    private JPanel bookPanel, userPanel, borrowedBookPanel;
    private JTable bookTable, userTable, borrowedBookTable;
    private JButton addBookButton, editBookButton, deleteBookButton;

    private final BookRepo bookRepo = new BookRepo();
    private final UserRepo userRepo = new UserRepo();
    private final BorrowingRepo borrowingRepo = new BorrowingRepo();
    private final PublisherRepo publisherRepo = new PublisherRepo();
    private final CopyRepo copyRepo = new CopyRepo();

    /**
     * Creates the librarian dashboard with panels for books, users, and borrowed books.
     */
    public LibrarianDashboard() {
        setTitle("Librarian Dashboard");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel navigationPanel = new JPanel();
        JButton booksButton = new JButton("Books");
        JButton usersButton = new JButton("Users");
        JButton borrowedBooksButton = new JButton("Borrowed Books");
        navigationPanel.add(booksButton);
        navigationPanel.add(usersButton);
        navigationPanel.add(borrowedBooksButton);
        add(navigationPanel, BorderLayout.NORTH);

        mainPanel = new JPanel(new CardLayout());

        bookPanel = createBookPanel();
        mainPanel.add(bookPanel, "Books");

        userPanel = createUserPanel();
        mainPanel.add(userPanel, "Users");

        borrowedBookPanel = createBorrowedBookPanel();
        mainPanel.add(borrowedBookPanel, "Borrowed Books");

        add(mainPanel, BorderLayout.CENTER);

        booksButton.addActionListener(e -> showPanel("Books"));
        usersButton.addActionListener(e -> showPanel("Users"));
        borrowedBooksButton.addActionListener(e -> showPanel("Borrowed Books"));
    }

    /**
     * Creates the panel for managing books.
     *
     * @return the book panel.
     */
    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        bookTable = new JTable();
        populateBookTable();
        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        addBookButton = new JButton("Add Book");
        editBookButton = new JButton("Edit Book");
        deleteBookButton = new JButton("Delete Book");
        buttonPanel.add(addBookButton);
        buttonPanel.add(editBookButton);
        buttonPanel.add(deleteBookButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        addBookButton.addActionListener(e -> handleAddBook());
        editBookButton.addActionListener(e -> handleEditBook());
        deleteBookButton.addActionListener(e -> handleDeleteBook());

        return panel;
    }

    /**
     * Creates the panel for managing users.
     *
     * @return the user panel.
     */
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        userTable = new JTable();
        populateUserTable();
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the panel for viewing borrowed books.
     *
     * @return the borrowed books panel.
     */
    private JPanel createBorrowedBookPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        borrowedBookTable = new JTable();
        populateBorrowedBookTable();
        panel.add(new JScrollPane(borrowedBookTable), BorderLayout.CENTER);

        return panel;
    }

    /**
     * Switches to a specified panel.
     *
     * @param panelName the name of the panel to display.
     */
    private void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        cardLayout.show(mainPanel, panelName);
    }

    /**
     * Populates the table with a list of books.
     */
    private void populateBookTable() {
        String[] columns = {"ID", "Title", "Author", "Publisher", "Publication Year", "ISBN"};
        List<Book> books = bookRepo.getAllBooks();
        String[][] data = new String[books.size()][6];

        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            data[i][0] = String.valueOf(book.getId());
            data[i][1] = book.getTitle();
            data[i][2] = book.getAuthor();
            data[i][3] = book.getPublisherEntity().getName();
            data[i][4] = String.valueOf(book.getPublicationYear());
            data[i][5] = book.getIsbn();
        }

        bookTable.setModel(new DefaultTableModel(data, columns));
    }

    private void populateUserTable() {
        String[] columns = {"ID", "Name", "Email", "Phone", "Address"};
        List<User> users = userRepo.getAllUsers();
        String[][] data = new String[users.size()][5];

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            data[i][0] = String.valueOf(user.getId());
            data[i][1] = user.getName();
            data[i][2] = user.getEmail();
            data[i][3] = user.getPhoneNumber();
            data[i][4] = user.getAddress();
        }

        userTable.setModel(new DefaultTableModel(data, columns));
    }

    /**
     * Populates the table with a list of users.
     */
    private void populateBorrowedBookTable() {
        String[] columns = {"User Name", "Book Title", "Borrow Date", "Return Date"};
        List<Borrowing> borrowings = borrowingRepo.getAllBorrowings();
        String[][] data = new String[borrowings.size()][4];

        for (int i = 0; i < borrowings.size(); i++) {
            Borrowing borrowing = borrowings.get(i);
            data[i][0] = borrowing.getUser().getName();
            data[i][1] = borrowing.getCopy().getBook().getTitle();
            data[i][2] = borrowing.getBorrowDate().toString();
            data[i][3] = borrowing.getReturnDate() != null ? borrowing.getReturnDate().toString() : "Not Returned";
        }

        borrowedBookTable.setModel(new DefaultTableModel(data, columns));
    }

    /**
     * Handles the process of adding a new book along with its copies.
     * Prompts the user for book details and the number of copies to create.
     */
    private void handleAddBook() {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField publisherField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField copiesField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Publisher:"));
        panel.add(publisherField);
        panel.add(new JLabel("Publication Year:"));
        panel.add(yearField);
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);
        panel.add(new JLabel("Number of Copies:"));
        panel.add(copiesField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Book book = new Book();
                book.setTitle(titleField.getText());
                book.setAuthor(authorField.getText());
                book.setPublisherEntity(publisherRepo.findPublisherByName(publisherField.getText()));
                book.setPublicationYear(Integer.parseInt(yearField.getText()));
                book.setIsbn(isbnField.getText());

                bookRepo.createBook(book);
                JOptionPane.showMessageDialog(this, "Book added successfully.");

                int numberOfCopies = Integer.parseInt(copiesField.getText());
                for (int i = 1; i <= numberOfCopies; i++) {
                    Copy copy = new Copy();
                    copy.setBook(book);
                    copy.setCopyNumber(i);
                    copy.setStatus("Available");
                    copyRepo.createCopy(copy);
                }
                JOptionPane.showMessageDialog(this, numberOfCopies + " copies created successfully.");

                populateBookTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding book or copies: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles the process of editing an existing book's details.
     * Prompts the user to select a book and updates its details based on the input.
     */
    private void handleEditBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit.");
            return;
        }

        int bookId = Integer.parseInt((String) bookTable.getValueAt(selectedRow, 0));
        Book book = bookRepo.findBookById(bookId);

        JTextField titleField = new JTextField(book.getTitle());
        JTextField authorField = new JTextField(book.getAuthor());
        JTextField publisherField = new JTextField(book.getPublisherEntity().getName());
        JTextField yearField = new JTextField(String.valueOf(book.getPublicationYear()));
        JTextField isbnField = new JTextField(book.getIsbn());

        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Publisher:"));
        panel.add(publisherField);
        panel.add(new JLabel("Publication Year:"));
        panel.add(yearField);
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                book.setTitle(titleField.getText());
                book.setAuthor(authorField.getText());
                book.setPublisherEntity(publisherRepo.findPublisherByName(publisherField.getText()));
                book.setPublicationYear(Integer.parseInt(yearField.getText()));
                book.setIsbn(isbnField.getText());

                bookRepo.updateBook(book);
                JOptionPane.showMessageDialog(this, "Book updated successfully.");
                populateBookTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating book: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles the process of deleting a book from the system.
     * Prompts the user to select a book and deletes it if there are no associated copies.
     * Displays a success or error message based on the operation result.
     */
    private void handleDeleteBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");
            return;
        }

        int bookId = Integer.parseInt((String) bookTable.getValueAt(selectedRow, 0));
        bookRepo.deleteBook(bookId);
        JOptionPane.showMessageDialog(this, "Book deleted successfully.");
        populateBookTable();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibrarianDashboard librarianDashboard = new LibrarianDashboard();
            librarianDashboard.setVisible(true);
        });
    }
}