package GUI;

import Entities.User;
import Repositories.UserRepo;

import javax.swing.*;
import java.awt.*;

/**
 * This class handles the login screen for the library system.
 * Users can log in as either a librarian or a regular user.
 */
public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private UserRepo userRepo = new UserRepo();

    /**
     * Creates a new login screen for the library system.
     */
    public LoginScreen() {
        setTitle("Library Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // username label and input field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        usernameField = new JTextField(15);
        add(usernameField, gbc);

        // password label and input field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        // login button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton = new JButton("Login");
        add(loginButton, gbc);

        loginButton.addActionListener(e -> handleLogin());
    }

    /**
     * Handles the login process when the login button is clicked.
     * Validates the username and password and determines if the user is a librarian or a regular user.
     */
    private void handleLogin() {
        String userIdInput = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            int userId = Integer.parseInt(userIdInput);

            // to find the user in the database
            User user = userRepo.findUserById(userId);

            if (user != null && password.equals(String.valueOf(userId))) {
                // to check if the user is a librarian or a regular user
                if (userRepo.isLibrarian(userId)) {
                    JOptionPane.showMessageDialog(this, "Librarian login successful!");
                    new LibrarianDashboard().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "User login successful!");
                    new UserDashboard(userId).setVisible(true);
                }
                this.dispose(); // to close the login screen
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid User ID. Please enter a number.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "An error occurred during login: " + e.getMessage());
        }
    }

    /**
     * The main method to start the login screen.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        });
    }
}