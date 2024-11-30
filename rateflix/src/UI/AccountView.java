package UI;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import javax.swing.*;

import base.User;
import database.DatabaseSetup;

public class AccountView {
    private User user;
    private JTextField descriptionField, newNameField;
    private JButton backButton, setPic, changeName, saveButton;
    private JFrame frame;

    private File file;

    public AccountView(User user) {
        this.user = user;
        initialize();
    }

    void initialize() {
        frame = new JFrame("Account View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());

        saveButton = new JButton("Save");

        frame.add(saveButton, BorderLayout.SOUTH);
        frame.add(createUserDetails(), BorderLayout.WEST);
        frame.add(createDescription(), BorderLayout.EAST);

        frame.setVisible(true);
    }

    private JPanel createDescription() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        descriptionField = new JTextField();
        JLabel desc = new JLabel("Your Description:");
        descriptionField.setPreferredSize(new Dimension(400, 200));
        gbc.gridy = 0;
        panel.add(desc, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(descriptionField, gbc);
        return panel;
    }

    private JPanel createUserDetails() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel userDetailsLabel = new JLabel("User Details Section");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 50, 0);
        panel.add(userDetailsLabel, gbc);

        setPic = new JButton("Set Picture");
        setPic.addActionListener(this::pictureSetter);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 50, 0);
        panel.add(setPic, gbc);

        JLabel currUser = new JLabel("Current Username: " + user.getUserName());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 10, 50, 0);
        panel.add(currUser, gbc);

        changeName = new JButton("Change Username");
        changeName.addActionListener(e -> openChangeUsernameDialog(currUser));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 10, 50, 0);
        panel.add(changeName, gbc);

        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.dispose();
            new WatchlistFrontend().run(user);
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(backButton, gbc);

        return panel;
    }

    private void openChangeUsernameDialog(JLabel currUser) {
        JDialog dialog = new JDialog(frame, "Change Username", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel label = new JLabel("Enter new username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(label, gbc);

        JTextField newUsernameField = new JTextField(15);
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(newUsernameField, gbc);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String newUsername = newUsernameField.getText();
            if (!newUsername.isBlank()) {
                updateName(newUsername);
                currUser.setText("Current Username: " + newUsername);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Username cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(saveButton, gbc);

        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    public void updateName(String name) {
        try (Connection conn = DatabaseSetup.getConnection();
             PreparedStatement stmnt = conn.prepareStatement("UPDATE users SET username = ? WHERE username = ?")) {
            stmnt.setString(1, name);
            stmnt.setString(2, user.getUserName());
            int rowsUpdated = stmnt.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(frame, "Username updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                user.setUserName(name);
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to update username.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error updating username in Database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pictureSetter(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            this.file = chooser.getSelectedFile();
        }
    }

    public static void main(String[] args) {
        User user = new User("defaultUser", "defaultPassword");
        new AccountView(user);
    }
}
