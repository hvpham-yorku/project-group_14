package UI;

import java.awt.BorderLayout;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import base.Review;
import database.DatabaseSetup;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import java.awt.event.ActionEvent;

import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class ReviewPage extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable reviewTable;
	private DefaultTableModel tableModel;
	private JTextField titleField;
	private static String title;
	private static Runnable refreshCallback;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReviewPage frame = new ReviewPage(title, refreshCallback);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ReviewPage(String title, Runnable refreshCallback) {
		this.title = title; // Assign the selected title
		this.refreshCallback = refreshCallback; // Assign the refresh callback
		setTitle("Review for: " + title);
		setBounds(100, 100, 652, 505);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Make a Review for: " + title);
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 25));
		lblNewLabel.setBounds(10, 11, 500, 41);
		contentPane.add(lblNewLabel);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 63, 618, 95);
		contentPane.add(scrollPane);

		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setFont(new Font("Arial", Font.PLAIN, 13));

		JComboBox<String> comboBox = new JComboBox<>();
		comboBox.setBackground(new Color(255, 255, 255));
		comboBox.setModel(new DefaultComboBoxModel<>(new String[] { "0", "1", "2", "3", "4", "5" }));
		comboBox.setBounds(141, 181, 66, 22);
		contentPane.add(comboBox);

		JButton btnNewButton = new JButton("Publish");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String customer_id = "TEST"; // Replace with actual user ID later
				int rating = Integer.parseInt((String) comboBox.getSelectedItem());
				String review = textArea.getText();
				if (review.isEmpty()) {
					JOptionPane.showMessageDialog(null, "No Review To Publish", "Try Again", JOptionPane.ERROR_MESSAGE);
					return;
				} else {
					Review obj = new Review();
					if (obj.addReview(new Review(customer_id, title, rating, review))) {
						JOptionPane.showMessageDialog(null, "Submitted", "Review Submitted",
								JOptionPane.INFORMATION_MESSAGE);
						textArea.setText("");
						loadReviews();
						if (refreshCallback != null) {
							refreshCallback.run(); // refresh callback for waatchlist UI
						}
					} else {
						JOptionPane.showMessageDialog(null, "Error", "Review Failed to Submit",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		btnNewButton.setFont(new Font("Arial", Font.PLAIN, 20));
		btnNewButton.setBounds(484, 169, 144, 41);
		contentPane.add(btnNewButton);

		JLabel lblNewLabel_1 = new JLabel("Reviews");
		lblNewLabel_1.setFont(new Font("Arial", Font.BOLD, 20));
		lblNewLabel_1.setBounds(10, 227, 87, 34);
		contentPane.add(lblNewLabel_1);

		JScrollPane tableScrollPane = new JScrollPane();
		tableScrollPane.setBounds(10, 272, 618, 185);
		contentPane.add(tableScrollPane);

		tableModel = new DefaultTableModel(new String[] { "Users", "", "Rating", "Review" }, 0);
		reviewTable = new JTable(tableModel);
		reviewTable.setFont(new Font("Yu Gothic UI Semilight", Font.PLAIN, 11));
		tableScrollPane.setViewportView(reviewTable);

		reviewTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		reviewTable.getColumnModel().getColumn(1).setPreferredWidth(185);
		reviewTable.getColumnModel().getColumn(2).setPreferredWidth(60);
		reviewTable.getColumnModel().getColumn(3).setPreferredWidth(600);

		loadReviews();
		setVisible(true);
	}

	private void loadReviews() {
		// TODO Auto-generated method stub
		try (Connection conn = DatabaseSetup.getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"SELECT customer_id, rating, timestamp, review FROM reviews WHERE title = ?")) {

			stmt.setString(1, title); // Filter reviews by the selected title
			ResultSet rs = stmt.executeQuery();

			tableModel.setRowCount(0);
			while (rs.next()) {
				String customerId = rs.getString("customer_id");
				int rating = rs.getInt("rating");
				Timestamp time = rs.getTimestamp("timestamp");
				String review = rs.getString("review");
				tableModel.addRow(new Object[] { customerId, time, rating, review });
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Error loading reviews: " + e.getMessage(), "Database Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
