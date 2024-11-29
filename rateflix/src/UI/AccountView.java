package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;

import javax.swing.*;

import base.User;
import database.DatabaseSetup;

public class AccountView {
	private User user;
    private JTextField descriptionField;
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
        descriptionField.setPreferredSize(new Dimension(400,200));
        gbc.gridy = 0;
        panel.add(desc, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(descriptionField,gbc);
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

        JLabel currUser = new JLabel(user.getUserName());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 10, 50, 15);
        panel.add(currUser, gbc);

        changeName = new JButton("Change");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 10, 50, 0);
        panel.add(changeName, gbc);

        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.dispose();
            new WatchlistFrontend().run(user);
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(backButton, gbc);

        return panel;
    }

    
    private void pictureSetter(ActionEvent e) {
    	JFileChooser chooser = new JFileChooser();
    	chooser.setMultiSelectionEnabled(false);
    	if(chooser.showOpenDialog(null) == chooser.APPROVE_OPTION) {
    		this.file = chooser.getSelectedFile();
    	}
    }
    
    
    public static void main(String[] args) {
    	User user = new User(LoginPage.getLoggedUser(),LoginPage.getLoggedPassword());
        new AccountView(user);
    }
}
