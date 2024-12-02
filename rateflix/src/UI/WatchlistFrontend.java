package UI;

import base.User;
import base.WatchlistItem;
import database.DatabaseSetup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
	private JButton deleteButton, addWatchlistButton, removeWatchlistButton, viewReviewsButton, homeButton,
			accountButton;
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

		accountButton = new JButton("👤");
		accountButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				new AccountView(user);

			}

		});
		// Watchlist Controls
		JPanel watchlistControls = new JPanel(new FlowLayout(FlowLayout.LEFT));

		// Select Watchlist Dropdown
		watchlistDropdown = new JComboBox<>();
		watchlistDropdown.addActionListener(e -> switchWatchlist((String) watchlistDropdown.getSelectedItem()));

		addWatchlistButton = new JButton("Create Watchlist");
		addWatchlistButton.addActionListener(e -> openCreateWatchlistDialog());

		removeWatchlistButton = new JButton("Remove Watchlist");
		removeWatchlistButton.addActionListener(e -> removeWatchlist());

		search = new JTextField("");
		search.setFont(new Font("Arial", Font.PLAIN, 20));
		search.setColumns(13);
		search.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (search.getText().equals("")) {
					loadItems();
				} else {
					loadSearcheditems();
				}
			}
		});

		homeButton = new JButton("🎥");
		homeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				new HomePage();
			}
		});
		watchlistControls.add(accountButton);
		watchlistControls.add(new JLabel("Select Watchlist:"));
		watchlistControls.add(watchlistDropdown);
		watchlistControls.add(addWatchlistButton);
		watchlistControls.add(removeWatchlistButton);
		watchlistControls.add(search);
		watchlistControls.add(homeButton);
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
		movieGridPanel.removeAll(); // Clear the current movie grid
		try (Connection conn = DatabaseSetup.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT w.id, w.title, w.status, "
						+ "(SELECT AVG(r.rating) FROM reviews r WHERE r.title = w.title) AS avg_rating "
						+ "FROM watchlist_items w " + "WHERE w.watchlist_name = ? AND w.title LIKE ?")) {

			stmt.setString(1, currentWatchlist); // Set the current watchlist name
			stmt.setString(2, "%" + search.getText() + "%"); // Search query

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				String status = rs.getString("status");
				double avgRating = rs.getDouble("avg_rating");

				WatchlistItem item = new WatchlistItem(id, title, avgRating, status);
				addCard(item); // Add the card to the grid
			}
		} catch (SQLException e) {
			showError("Error loading items: " + e.getMessage());
		}

		refreshGrid(); // Refresh the UI
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

	private String fetchThumbnail(String title) {
		try {
			String apiKey = "API key"; // enter your TMDB api key here
			String apiUrl = "https://api.themoviedb.org/3/search/movie?query=" + URLEncoder.encode(title, "UTF-8")
					+ "&api_key=" + apiKey;

			// Open the connection
			HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
			conn.setRequestMethod("GET");

			// Read the response
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Parse JSON to extract the poster_path
			JSONObject jsonResponse = new JSONObject(response.toString());
			JSONArray results = jsonResponse.getJSONArray("results");
			if (results.length() > 0) {
				String posterPath = results.getJSONObject(0).getString("poster_path");
				return "https://image.tmdb.org/t/p/w500" + posterPath;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private JPanel selectedCard;

	private void addCard(WatchlistItem item) {
		JPanel card = new JPanel(new BorderLayout());
		card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Default border
		card.setBackground(Color.WHITE);
		card.setPreferredSize(new Dimension(220, 350)); // Fixed card size

		JLabel titleLabel = new JLabel(item.getTitle(), SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
		card.add(titleLabel, BorderLayout.NORTH);

		// Fetch and display the thumbnail
		new Thread(() -> {
			String thumbnailUrl = fetchThumbnail(item.getTitle());
			if (thumbnailUrl != null) {
				try {
					// Fetch the image from the URL
					ImageIcon originalIcon = new ImageIcon(new URL(thumbnailUrl));
					Image scaledImage = originalIcon.getImage().getScaledInstance(200, 300, Image.SCALE_SMOOTH);
					ImageIcon scaledIcon = new ImageIcon(scaledImage);

					JLabel thumbnailLabel = new JLabel(scaledIcon);
					SwingUtilities.invokeLater(() -> {
						card.add(thumbnailLabel, BorderLayout.CENTER);
						movieGridPanel.revalidate();
						movieGridPanel.repaint();
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

		JLabel ratingLabel = new JLabel("\u2605".repeat((int) Math.round(item.getRating())), SwingConstants.CENTER);
		ratingLabel.setFont(new Font("Serif", Font.BOLD, 16));
		ratingLabel.setForeground(Color.ORANGE);
		card.add(ratingLabel, BorderLayout.SOUTH);

		// handle selection and double-click
		card.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() == 1) { // Single click for selection
					selectedItem = item;
					deleteButton.setEnabled(true); // Enable delete button

					// Update the visual selection
					if (selectedCard != null) {
						selectedCard.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Reset previous card
						selectedCard.setBackground(Color.WHITE); // Reset previous card background
					}

					selectedCard = card;
					card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3)); // Highlight the selected card
					card.setBackground(new Color(230, 240, 255)); // Slightly shaded background
				} else if (e.getClickCount() == 2) { // Double click to open ReviewPage
					new ReviewPage(item.getTitle(), WatchlistFrontend.this::refreshWatchlist);
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
