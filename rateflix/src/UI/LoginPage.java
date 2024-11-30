package UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

import javax.swing.*;

import base.User;
import database.DatabaseSetup;

public class LoginPage extends JFrame implements ActionListener {
	private static JTextField usernameField;
	private static JPasswordField passwordField;
	private JButton loginBtn, RegBtn;
	
	public LoginPage() {
		initialize();
	}
	
	public static String getLoggedUser() {
		return usernameField.getText();
	}
	public static String getLoggedPassword() {
		return new String(passwordField.getPassword());
	}
	
	public void initialize() {
		setTitle("Account Login");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);
        
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginBtn.doClick();
                }
            }
        });
        
        loginBtn = new JButton("Login");
        loginBtn.addActionListener(this);
        
        RegBtn = new JButton("Don't have an Account?");
        RegBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new RegisterPage();
			}
        	
        });
        
        JPanel usernamePanel = new JPanel();
		usernamePanel.add(usernameLabel);
		usernamePanel.add(usernameField);
		
		JPanel passwordPanel = new JPanel();
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordField);
		
		panel.add(usernamePanel);
		panel.add(passwordPanel);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(RegBtn);
		buttonPanel.add(loginBtn);
		
		panel.add(buttonPanel);
        add(panel);
        setVisible(true);
        
	}
	
	@Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (authenticate(username, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!"); 
            openHome(username, password);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.");
        }
    }
    
    private boolean authenticate(String username, String password) {
    	// Establishing a database connection
    	try (Connection connection = DatabaseSetup.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM users WHERE username = ? AND password = ?")) {
               preparedStatement.setString(1, username);
               preparedStatement.setString(2, password);
               try (ResultSet resultSet = preparedStatement.executeQuery()) {
                   return resultSet.next();
               }
           } catch (SQLException ex) {
        	   // Print stack trace in case of SQL exception
               ex.printStackTrace();
           }
           return false;
    }
    
    private void openHome(String username, String password) {
    	User user = new User(username,password);
        new WatchlistFrontend().run(user);
    }
	
	public static void main(String[] args) {
		new LoginPage();
	}

	
}

