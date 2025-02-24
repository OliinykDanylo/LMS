package GUI;

import Entities.Book;
import Entities.Borrowing;
import Entities.Copy;
import Entities.User;
import Repositories.BorrowingRepo;
import Repositories.CopyRepo;
import Repositories.UserRepo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the user dashboard where users can view available books, all books in the library, and their borrowing history.
 * Provides functionality for borrowing and returning books.
 */
public class UserDashboard extends JFrame {
    private JPanel mainPanel;
    private JPanel allBooksPanel, availableBooksPanel, borrowingHistoryPanel;
    private JTable allBooksTable, availableBookTable, borrowingHistoryTable;
    private JButton borrowButton, returnButton;

    private final CopyRepo copyRepo = new CopyRepo();
    private final UserRepo userRepo = new UserRepo();
    private final BorrowingRepo borrowingRepo = new BorrowingRepo();

    private final int currentUserId;

    /**
     * Constructs a new UserDashboard for a specific user.
     *
     * @param userId The ID of the user logged into the dashboard.
     */
    public UserDashboard(int userId) {
        this.currentUserId = userId;

        setTitle("User Dashboard");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel navigationPanel = new JPanel();
        JButton allBooksButton = new JButton("All Books");
        JButton availableBooksButton = new JButton("Available Books");
        JButton borrowingHistoryButton = new JButton("Borrowing History");
        navigationPanel.add(allBooksButton);
        navigationPanel.add(availableBooksButton);
        navigationPanel.add(borrowingHistoryButton);
        add(navigationPanel, BorderLayout.NORTH);

        mainPanel = new JPanel(new CardLayout());

        allBooksPanel = createAllBooksPanel();
        mainPanel.add(allBooksPanel, "All Books");

        availableBooksPanel = createAvailableBooksPanel();
        mainPanel.add(availableBooksPanel, "Available Books");

        borrowingHistoryPanel = createBorrowingHistoryPanel();
        mainPanel.add(borrowingHistoryPanel, "Borrowing History");

        add(mainPanel, BorderLayout.CENTER);

        allBooksButton.addActionListener(e -> showPanel("All Books"));
        availableBooksButton.addActionListener(e -> showPanel("Available Books"));
        borrowingHistoryButton.addActionListener(e -> showPanel("Borrowing History"));
    }

    /**
     * Creates the panel displaying all books in the library, without duplicates.
     *
     * @return A JPanel containing the all books table.
     */
    private JPanel createAllBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        allBooksTable = new JTable();
        populateAllBooksTable();
        panel.add(new JScrollPane(allBooksTable), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the panel displaying all available book copies for borrowing.
     *
     * @return A JPanel containing the available books table and a borrow button.
     */
    private JPanel createAvailableBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        availableBookTable = new JTable();
        populateAvailableBookTable();

        borrowButton = new JButton("Borrow");
        borrowButton.addActionListener(e -> handleBorrow());

        panel.add(new JScrollPane(availableBookTable), BorderLayout.CENTER);
        panel.add(borrowButton, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Creates the panel displaying the borrowing history of the current user.
     *
     * @return A JPanel containing the borrowing history table and a return button.
     */
    private JPanel createBorrowingHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        borrowingHistoryTable = new JTable();
        populateBorrowingHistoryTable();

        returnButton = new JButton("Return");
        returnButton.addActionListener(e -> handleReturn());

        panel.add(new JScrollPane(borrowingHistoryTable), BorderLayout.CENTER);
        panel.add(returnButton, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Switches the displayed panel in the dashboard.
     *
     * @param panelName The name of the panel to display.
     */
    private void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        cardLayout.show(mainPanel, panelName);
    }

    /**
     * Populates the table with all books in the library, ensuring no duplicates.
     */
    private void populateAllBooksTable() {
        String[] columns = {"Title", "Author", "ISBN", "Publisher", "Publication Year"};
        List<Book> books = copyRepo.getAllCopies()
                .stream()
                .map(Copy::getBook)
                .distinct()
                .collect(Collectors.toList());

        String[][] data = new String[books.size()][5];
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            data[i][0] = book.getTitle();
            data[i][1] = book.getAuthor();
            data[i][2] = book.getIsbn();
            data[i][3] = book.getPublisherEntity().getName();
            data[i][4] = String.valueOf(book.getPublicationYear());
        }

        allBooksTable.setModel(new DefaultTableModel(data, columns));
    }

    /**
     * Populates the table with all currently available book copies for borrowing.
     */
    private void populateAvailableBookTable() {
        String[] columns = {"Book ID", "Title", "Author", "ISBN", "Copy Number"};
        List<Copy> availableCopies = copyRepo.getAvailableCopies();

        String[][] data = new String[availableCopies.size()][5];
        for (int i = 0; i < availableCopies.size(); i++) {
            Book book = availableCopies.get(i).getBook();
            data[i][0] = String.valueOf(book.getId());
            data[i][1] = book.getTitle();
            data[i][2] = book.getAuthor();
            data[i][3] = book.getIsbn();
            data[i][4] = String.valueOf(availableCopies.get(i).getCopyNumber());
        }

        availableBookTable.setModel(new DefaultTableModel(data, columns));
    }

    /**
     * Populates the table with the borrowing history of the current user.
     */
    private void populateBorrowingHistoryTable() {
        String[] columns = {"Title", "Borrow Date", "Return Date"};
        List<Borrowing> borrowings = borrowingRepo.getBorrowingsByUser(currentUserId);

        String[][] data = new String[borrowings.size()][3];
        for (int i = 0; i < borrowings.size(); i++) {
            Borrowing borrowing = borrowings.get(i);
            data[i][0] = borrowing.getCopy().getBook().getTitle();
            data[i][1] = borrowing.getBorrowDate().toString();
            data[i][2] = borrowing.getReturnDate() == null ? "Not Returned" : borrowing.getReturnDate().toString();
        }

        borrowingHistoryTable.setModel(new DefaultTableModel(data, columns));
    }

    /**
     * Handles the borrowing of a selected book copy by the current user.
     * Validates the selected book's availability and updates its status to "Borrowed".
     * A new borrowing record is created and associated with the current user.
     * Updates the Available Books and Borrowing History tables upon successful operation.
     * If an error occurs, displays an appropriate error message.
     */
    private void handleBorrow() {
        int selectedRow = availableBookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to borrow.");
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                int copyNumber = Integer.parseInt((String) availableBookTable.getValueAt(selectedRow, 4));
                int bookId = Integer.parseInt((String) availableBookTable.getValueAt(selectedRow, 0));
                Copy selectedCopy = copyRepo.findCopyByNumberWithBorrowings(copyNumber, bookId);

                if (selectedCopy == null || !selectedCopy.getStatus().equals("Available")) {
                    throw new IllegalStateException("The book is not available.");
                }

                Borrowing borrowing = new Borrowing();
                borrowing.setUser(userRepo.findUserById(currentUserId));
                borrowing.setCopy(selectedCopy);
                borrowing.setBorrowDate(new Date());
                borrowingRepo.createBorrowing(borrowing);

                selectedCopy.setStatus("Borrowed");
                copyRepo.updateCopy(selectedCopy);

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(UserDashboard.this, "Book borrowed successfully!");
                    populateAvailableBookTable();
                    populateBorrowingHistoryTable();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(UserDashboard.this, "Error borrowing book: " + e.getMessage());
                }
            }
        }.execute();
    }

    /**
     * Handles the return of a borrowed book by the current user.
     * Validates the selected borrowing record and updates the book's status to "Available".
     * Sets the return date of the borrowing record to the current date.
     * Updates the Available Books and Borrowing History tables upon successful operation.
     * If an error occurs, displays an appropriate error message.
     */
    private void handleReturn() {
        int selectedRow = borrowingHistoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a borrowing record to return.");
            return;
        }

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                String title = (String) borrowingHistoryTable.getValueAt(selectedRow, 0);
                Borrowing borrowing = borrowingRepo.findBorrowingByTitleAndUser(title, currentUserId);
                if (borrowing == null || borrowing.getReturnDate() != null) {
                    throw new IllegalStateException("This book has already been returned.");
                }

                borrowing.setReturnDate(new Date());
                borrowingRepo.updateBorrowing(borrowing);

                Copy copy = borrowing.getCopy();
                copy.setStatus("Available");
                copyRepo.updateCopy(copy);

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(UserDashboard.this, "Book returned successfully!");
                    populateAvailableBookTable();
                    populateBorrowingHistoryTable();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(UserDashboard.this, "Error returning book: " + e.getMessage());
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserDashboard dashboard = new UserDashboard(1);
            dashboard.setVisible(true);
        });
    }
}