package UI;

import javax.swing.*;

import base.User;
import database.DatabaseSetup;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class RegisterPage extends JFrame implements ActionListener {
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField confirmPasswordField;
	private JButton registerButton, LoginBtn;
	private JCheckBox showPasswordCheckbox;
	
	public RegisterPage() {
		initialize();
	}
	
	public void initialize() {
		setTitle("Account Registration Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 200);
        setLocationRelativeTo(null); 
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        

        JPanel panel = new JPanel(new GridLayout(5, 1));

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
   
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();
        
        confirmPasswordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	registerButton.doClick();
                }
            }
        });

        showPasswordCheckbox = new JCheckBox("Show Password");

        
  
        
        // Registration button
        registerButton = new JButton("Register");
        registerButton.addActionListener((ActionListener) this);
        
        LoginBtn = new JButton("Have an Account?");
        LoginBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				new LoginPage();
			}
        	
        });
        
        showPasswordCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (showPasswordCheckbox.isSelected()) {
					passwordField.setEchoChar((char) 0);
					confirmPasswordField.setEchoChar((char) 0);
				} else {
					passwordField.setEchoChar('*');
					confirmPasswordField.setEchoChar('*');
				}
			}
		});

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(confirmPasswordLabel);
        panel.add(confirmPasswordField);
		panel.add(new JLabel(""));
		panel.add(showPasswordCheckbox);
        panel.add(registerButton);
        panel.add(LoginBtn);

        mainPanel.add(panel, BorderLayout.CENTER);
        
        // Add main panel to the frame
        add(mainPanel);
        setVisible(true);
	}
	
	 @Override
	    public void actionPerformed(ActionEvent e) {
	        String username = usernameField.getText();
	        String password = new String(passwordField.getPassword());
	        String confirmPassword = new String(confirmPasswordField.getPassword());
	        
	        if(username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
		        JOptionPane.showMessageDialog(this, "Please enter all fields!");
		        return;
	        }
	        
	        if(DatabaseSetup.checkIfAccountExists(username)) {
		        JOptionPane.showMessageDialog(this, "Account already exists with this username!");
				return;
	        }
	        
	        if(password.equals(confirmPassword )) {
	        	DatabaseSetup.insertAccount(username, password);
	        	JOptionPane.showMessageDialog(this, "Account registered successfully!");
	        	openHome();
	        } else {
				JOptionPane.showMessageDialog(this, "Check that Password's are entered correctly");
			}

	    }
	        
	        private void openHome() {
	        	User user = new User(usernameField.getText(), new String(passwordField.getPassword()));
	            new WatchlistFrontend().run(user);
	        }
	        
	    	public static void main(String[] args) {
	    		new RegisterPage();
	    	}
	        
}
