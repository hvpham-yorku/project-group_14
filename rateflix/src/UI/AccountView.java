package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;

import javax.swing.*;

public class AccountView {
	private JTextField descriptionField;
	private JButton backButton, changePicButton, setPic;
	private JFrame frame;
	
	public AccountView() {
		initialize();
	}
	
	void initialize(){
		frame = new JFrame("Account View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 600);
		frame.setLayout(new BorderLayout());
		
		frame.add(createUserDetails(), BorderLayout.WEST);
		frame.add(createDescription(), BorderLayout.EAST);
		
		frame.setVisible(true);
	}
	
	private JPanel createDescription() {
		JPanel panel = new JPanel(new BorderLayout());
		descriptionField = new JTextField();
		descriptionField.setSize(400, 200);
		descriptionField.setSelectionColor(Color.BLUE);
		panel.add(descriptionField);
		
		return panel;
	}

	private JPanel createUserDetails() {
		JPanel panel = new JPanel(new BorderLayout());
		
		return panel;
	}

	public static void main(String[] args) {
		new AccountView();
	}
	
}
