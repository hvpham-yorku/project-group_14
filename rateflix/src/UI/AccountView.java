package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.*;

import javax.swing.*;

import base.User;
import database.DatabaseSetup;

public class AccountView {
	private User user;
    private JTextField descriptionField;
    private JButton backButton, changePicButton, setPic, changeName;
    private JFrame frame;

    public AccountView(User user) {
    	this.user = user;
        initialize();
    }

    void initialize() {
        frame = new JFrame("Account View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout());

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
        backButton = new JButton("Back");
        JLabel currUser = new JLabel(user.getUserName());
        changeName = new JButton("Change");
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userDetailsLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(currUser, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(changeName, gbc);
        gbc.gridx = -1;
        gbc.gridy = 2;
        panel.add(backButton, gbc);
        return panel;
    }
    
    public String fetchUsername() {
    	String name = "TestName";
    	try (Connection conn = DatabaseSetup.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT username FROM users WHERE")){
        	
        } catch(SQLException e) {
        	System.out.println("Error Fetching username: " + e.getMessage());
        }
    	return name;
    }

    public static void main(String[] args) {
    	User user = new User("userName","password");
        new AccountView(user);
    }
}
