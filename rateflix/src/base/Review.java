package base;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import database.DatabaseSetup;

public class Review{

    private String id;
    private String title;
    private String customer_id;
    private int rating;
    private LocalDateTime timestamp;
    private String review;

    public Review(String customer_id, String title, int rating, String review) {
    	super();
        this.customer_id = customer_id;
        this.title = title; 
        this.rating = rating;
        this.review = review;
    }

    public Review() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
	private String getTitle() {
		return title;
	}

    @Override
    public int hashCode() {
        return Objects.hash(customer_id, id, rating, review, timestamp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Review other = (Review) obj;
        return Objects.equals(customer_id, other.customer_id) && Objects.equals(id, other.id) && rating == other.rating
                && Objects.equals(review, other.review) && Objects.equals(timestamp, other.timestamp);
    }

    @Override
    public String toString() {
        return "Review [id=" + id + ", customer_id=" + customer_id + ", rating=" + rating + ", timestamp=" + timestamp
                + ", review=" + review + "]";
    }
    
    public boolean addReview(Review obj) {
        String sql = "INSERT INTO reviews (id, customer_id, title, rating, timestamp, review) " +
                     "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?);";
        try (Connection con = DatabaseSetup.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, UUID.randomUUID().toString());
            ps.setString(2, obj.getCustomer_id());
            ps.setString(3, obj.getTitle()); // NEW FIELD
            ps.setInt(4, obj.getRating());
            ps.setString(5, obj.getReview());

            return (ps.executeUpdate() > 0);
        } catch (SQLException e1) {
            throw new RuntimeException(e1);
        }
    }

}