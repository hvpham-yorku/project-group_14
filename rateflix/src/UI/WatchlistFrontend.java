package UI;

import base.User;
import base.WatchlistItem;
import database.DatabaseSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WatchlistFrontend {

	private User user;
	private JFrame frame;
	private JPanel movieGridPanel;
	private JComboBox<String> watchlistDropdown;
	private JButton deleteButton, addWatchlistButton, removeWatchlistButton;
	private JTextField search;
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

		search = new JTextField("Search");
		search.setFont(new Font("Arial", Font.PLAIN, 20));
		search.setColumns(13);
		search.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (search.getText().equals("Search")) {

				} else if (search.getText().equals("")) {
					loadItems();
				} else {
					loadSearcheditems();
				}
			}
		});

		watchlistControls.add(new JLabel("Select Watchlist:"));
		watchlistControls.add(watchlistDropdown);
		watchlistControls.add(addWatchlistButton);
		watchlistControls.add(removeWatchlistButton);
		watchlistControls.add(search);

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

		// Add and Delete Buttons
		gbc.gridx = 0;
		gbc.gridy = 1;
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

	private void loadSearcheditems() {
		movieGridPanel.removeAll();
		try (Connection conn = DatabaseSetup.getConnection();
				PreparedStatement stmt = conn
						.prepareStatement("SELECT * FROM watchlist_items WHERE watchlist_name = ? AND title LIKE ?")) {

			stmt.setString(1, currentWatchlist);
			stmt.setString(2, "%" + search.getText() + "%");
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

	private void loadItems() {
		movieGridPanel.removeAll();

		try (Connection conn = DatabaseSetup.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT id, title, status, "
						+ "(SELECT AVG(r.rating) FROM reviews r WHERE r.title = w.title) AS avg_rating "
						+ "FROM watchlist_items w WHERE w.watchlist_name = ?")) {

			stmt.setString(1, currentWatchlist);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				String status = rs.getString("status");
				double avgRating = rs.getDouble("avg_rating");

				WatchlistItem item = new WatchlistItem(id, title, avgRating, status); // Include ID
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
						"INSERT INTO watchlist_items (watchlist_name, title, status) VALUES (?, ?, ?)")) {

			stmt.setString(1, currentWatchlist);
			stmt.setString(2, title);
			stmt.setString(3, "Not Watched");

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

			stmt.setInt(1, selectedItem.getId()); // Use the ID of the selected item
			stmt.executeUpdate();

			showMessage("Item '" + selectedItem.getTitle() + "' deleted successfully.");
			loadItems(); // Reload the items after deletion
			movieGridPanel.revalidate(); // Refresh the panel
			movieGridPanel.repaint(); // Redraw the panel
			selectedItem = null; // Reset selectedItem
			deleteButton.setEnabled(false); // Disable the delete button

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

		JLabel ratingLabel = new JLabel("\u2605".repeat((int) Math.round(item.getRating())), SwingConstants.CENTER);
		ratingLabel.setFont(new Font("Serif", Font.BOLD, 16));
		ratingLabel.setForeground(Color.ORANGE);
		card.add(ratingLabel, BorderLayout.SOUTH);

		card.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				selectedItem = item; // select item to be deleted
				deleteButton.setEnabled(true); // Enable delete button when selected
				if (e.getClickCount() == 2) { // Open ReviewPage on double-click
					new ReviewPage(item.getTitle(), WatchlistFrontend.this::refreshWatchlist); // Use instance reference
				}
			}
		});

		movieGridPanel.add(card);
	}

	private void refreshWatchlist() {
		loadItems(); // Reload items to reflect updated ratings
		movieGridPanel.revalidate();
		movieGridPanel.repaint();
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
