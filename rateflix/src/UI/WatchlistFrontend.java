package UI;

import base.User;
import base.WatchlistItem;
import database.DatabaseSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WatchlistFrontend {

	private User user;
	private JFrame frame;
	private JPanel movieGridPanel;
	private JComboBox<String> ratingDropdown, watchlistDropdown, filterDropdown;
	private JButton deleteButton, addWatchlistButton, removeWatchlistButton, viewReviewsButton;
	private String currentWatchlist;
	private WatchlistItem selectedItem;

	public static void main(String[] args) {
		User user = new User(LoginPage.getLoggedUser(), LoginPage.getLoggedPassword());
		new WatchlistFrontend().run(user);
	}

	void run(User user) {
		this.user = user;
		setupUI();
		loadWatchlists();
	}

	private void setupUI() {
		frame = new JFrame("RateFlix Watchlist");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 600);
		frame.setLayout(new BorderLayout());

		// Title and Watchlist Controls
		frame.add(createTitleAndWatchlistPanel(), BorderLayout.NORTH);

		// Add Movies/Shows Section
		frame.add(createAddSection(), BorderLayout.SOUTH);

		// Movie Grid
		movieGridPanel = new JPanel(new GridLayout(0, 4, 10, 10));
		frame.add(new JScrollPane(movieGridPanel), BorderLayout.CENTER);

		frame.setVisible(true);
	}

	private JPanel createTitleAndWatchlistPanel() {
	    JPanel panel = new JPanel(new BorderLayout());

	    // Title Label
	    JLabel title = new JLabel(user.getUserName() + " Watchlists", SwingConstants.CENTER);
	    title.setFont(new Font("Arial", Font.BOLD, 24));
	    panel.add(title, BorderLayout.WEST);
	    
	    JButton accountButton = new JButton("View Account");
	    accountButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				new AccountView(user);
				
			}
	    	
	    });
	    panel.add(accountButton);
	    // Watchlist Controls
	    JPanel watchlistControls = new JPanel(new FlowLayout(FlowLayout.LEFT));

	    // Select Watchlist Dropdown
	    watchlistDropdown = new JComboBox<>();
	    watchlistDropdown.addActionListener(e -> switchWatchlist((String) watchlistDropdown.getSelectedItem()));

	    addWatchlistButton = new JButton("Create Watchlist");
	    addWatchlistButton.addActionListener(e -> openCreateWatchlistDialog());

	    removeWatchlistButton = new JButton("Remove Watchlist");
	    removeWatchlistButton.addActionListener(e -> removeWatchlist());
	    
//	    viewReviewsButton = new JButton("View Reviews");
//	    viewReviewsButton.addActionListener(e -> new ReviewPage());
	    
	    watchlistControls.add(new JLabel("Select Watchlist:"));
	    watchlistControls.add(watchlistDropdown);
	    watchlistControls.add(addWatchlistButton);
	    watchlistControls.add(removeWatchlistButton);
//	    watchlistControls.add(viewReviewsButton);

	    panel.add(watchlistControls, BorderLayout.EAST);
	    return panel;
	}


	private JPanel createAddSection() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Title Input
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(new JLabel("Movie/Show Title:"), gbc);
		gbc.gridx = 1;
		JTextField movieInputField = new JTextField(15);
		panel.add(movieInputField, gbc);

		// Rating Dropdown
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(new JLabel("Rating:"), gbc);
		gbc.gridx = 1;
		ratingDropdown = new JComboBox<>(new String[] { "1", "2", "3", "4", "5" });
		panel.add(ratingDropdown, gbc);

		// Add and Delete Buttons
		gbc.gridx = 0;
		gbc.gridy = 2;
		JButton addButton = new JButton("Add Movie/Show");
		addButton.addActionListener(e -> addItem(movieInputField.getText()));
		panel.add(addButton, gbc);

		gbc.gridx = 1;
		deleteButton = new JButton("Delete Movie/Show");
		deleteButton.setEnabled(false);
		deleteButton.addActionListener(e -> deleteItem());
		panel.add(deleteButton, gbc);

		return panel;
	}

	private void openCreateWatchlistDialog() {
		// Open a dialog to create a new watchlist
		String watchlistName = JOptionPane.showInputDialog(frame, "Enter a name for the new watchlist:",
				"Create Watchlist", JOptionPane.PLAIN_MESSAGE);

		if (watchlistName != null && !watchlistName.isBlank()) {
			createWatchlist(watchlistName.trim());
		}
	}

	private void loadWatchlists() {
		try (Connection conn = DatabaseSetup.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT name FROM watchlist_names")) {

			ResultSet rs = stmt.executeQuery();
			watchlistDropdown.removeAllItems(); // Clear existing items

			while (rs.next()) {
				String name = rs.getString("name");
				watchlistDropdown.addItem(name);
			}

		} catch (SQLException e) {
			showError("Error loading watchlists: " + e.getMessage());
		}

		if (watchlistDropdown.getItemCount() > 0) {
			currentWatchlist = (String) watchlistDropdown.getItemAt(0);
			loadItems();
		}
	}

	private void switchWatchlist(String name) {
		currentWatchlist = name;
		loadItems();
	}

	private void createWatchlist(String name) {
		if (watchlistDropdown.getItemCount() > 0 && currentWatchlist.equals(name)) {
			showError("Watchlist already exists.");
			return;
		}

		try (Connection conn = DatabaseSetup.getConnection();
				PreparedStatement stmt = conn.prepareStatement("INSERT INTO watchlist_names (name) VALUES (?)")) {

			stmt.setString(1, name);
			stmt.executeUpdate();

			watchlistDropdown.addItem(name);
			showMessage("Watchlist '" + name + "' created successfully.");

		} catch (SQLException e) {
			showError("Error creating watchlist: " + e.getMessage());
		}
	}

	private void removeWatchlist() {
		String name = (String) watchlistDropdown.getSelectedItem();

		if (name == null) {
			showError("No watchlist selected to remove.");
			return;
		}

		try (Connection conn = DatabaseSetup.getConnection();
				PreparedStatement stmt = conn.prepareStatement("DELETE FROM watchlist_names WHERE name = ?")) {

			stmt.setString(1, name);
			stmt.executeUpdate();

			watchlistDropdown.removeItem(name);
			showMessage("Watchlist '" + name + "' removed successfully.");

			if (watchlistDropdown.getItemCount() > 0) {
				currentWatchlist = (String) watchlistDropdown.getItemAt(0);
				loadItems();
			} else {
				currentWatchlist = null;
				movieGridPanel.removeAll();
				refreshGrid();
			}

		} catch (SQLException e) {
			showError("Error removing watchlist: " + e.getMessage());
		}
	}

	private void loadItems() {
		movieGridPanel.removeAll();

		try (Connection conn = DatabaseSetup.getConnection();
				PreparedStatement stmt = conn
						.prepareStatement("SELECT * FROM watchlist_items WHERE watchlist_name = ?")) {

			stmt.setString(1, currentWatchlist);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				WatchlistItem item = new WatchlistItem(rs.getInt("id"), rs.getString("title"), rs.getInt("rating"),
						rs.getString("status"));
				addCard(item);
			}

		} catch (SQLException e) {
			showError("Error loading items: " + e.getMessage());
		}

		refreshGrid();
	}

	private void addItem(String title) {
		if (title == null || title.isBlank()) {
			showError("Please enter a title.");
			return;
		}

		try (Connection conn = DatabaseSetup.getConnection();
				PreparedStatement stmt = conn.prepareStatement(
						"INSERT INTO watchlist_items (watchlist_name, title, rating, status) VALUES (?, ?, ?, ?)")) {

			stmt.setString(1, currentWatchlist);
			stmt.setString(2, title);
			stmt.setInt(3, Integer.parseInt((String) ratingDropdown.getSelectedItem()));
			stmt.setString(4, "Not Watched");

			stmt.executeUpdate();
			loadItems();

		} catch (SQLException e) {
			showError("Error adding item: " + e.getMessage());
		}
	}

	private void deleteItem() {
		if (selectedItem == null) {
			showError("No item selected.");
			return;
		}

		try (Connection conn = DatabaseSetup.getConnection();
				PreparedStatement stmt = conn.prepareStatement("DELETE FROM watchlist_items WHERE id = ?")) {

			stmt.setInt(1, selectedItem.getId());
			stmt.executeUpdate();

			loadItems();
			selectedItem = null;
			deleteButton.setEnabled(false);

		} catch (SQLException e) {
			showError("Error deleting item: " + e.getMessage());
		}
	}

	private void addCard(WatchlistItem item) {
		JPanel card = new JPanel(new BorderLayout());
		card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		card.setBackground(Color.WHITE);

		JLabel titleLabel = new JLabel(item.getTitle(), SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
		card.add(titleLabel, BorderLayout.NORTH);

		JLabel ratingLabel = new JLabel("\u2605".repeat(item.getRating()), SwingConstants.CENTER);
		ratingLabel.setFont(new Font("Serif", Font.BOLD, 16));

		ratingLabel.setForeground(Color.ORANGE);
		card.add(ratingLabel, BorderLayout.SOUTH);

		// Select the Item
		card.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				selectedItem = item;
				deleteButton.setEnabled(true); // Enable delete button when an item is selected
				new ReviewPage();
			}
		});

		// Add the card to the grid panel
		movieGridPanel.add(card);
	}

	private void refreshGrid() {
		movieGridPanel.revalidate();
		movieGridPanel.repaint();
	}

	private void showError(String message) {
		JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private void showMessage(String message) {
		JOptionPane.showMessageDialog(frame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
	}
}
