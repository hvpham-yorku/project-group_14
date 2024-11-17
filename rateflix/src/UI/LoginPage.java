package UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.*;

import base.User;

public class LoginPage extends JFrame implements ActionListener {
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JButton loginBtn;
	
	public LoginPage() {
		initialize();
	}
	
	public void initialize() {
		setTitle("Child Login");
        setSize(300, 200);
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
        
        JPanel usernamePanel = new JPanel();
		usernamePanel.add(usernameLabel);
		usernamePanel.add(usernameField);
		
		JPanel passwordPanel = new JPanel();
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordField);
		
		panel.add(usernamePanel);
		panel.add(passwordPanel);
        
        JPanel buttonPanel = new JPanel();
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
            openHome();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.");
        }
    }
    
    private boolean authenticate(String username, String password) {
    	// Establishing a database connection
    	try (Connection connection = DatabaseConnector.getConnection();
    			// Creating a PreparedStatement to execute a SQL query
                PreparedStatement preparedStatement = connection.prepareStatement(
                		// SQL query to select user account with provided username, password, and account type as 'Child'
                        "SELECT * FROM Accounts WHERE username = ? AND password = ? AND accountType = 'Child'")) {
    		   // Set parameters for the prepared statement
               preparedStatement.setString(1, username);
               preparedStatement.setString(2, password);
               // Execute the query and obtain a ResultSet
               try (ResultSet resultSet = preparedStatement.executeQuery()) {
            	   // If ResultSet contains any data, authentication is successful, return true
                   return resultSet.next();
               }
           } catch (SQLException ex) {
        	   // Print stack trace in case of SQL exception
               ex.printStackTrace();
           }
           return false;
    }
    
    private void openHome() {
    	User user = new User("userName", "passWord");
        new Home(user);
    }
	
	public static void main(String[] args) {
		new LoginPage();
	}

	
}

