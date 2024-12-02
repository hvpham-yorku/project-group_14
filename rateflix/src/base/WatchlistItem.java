package base;

public class WatchlistItem {
    private int id; // Unique id for the watchlist item
    private String title; 
    private double rating; // Rating (1-5)
    private String status; // Watched or Not Watched

    public WatchlistItem() {
    }

    public WatchlistItem(String title, double rating, String status) {
        this.title = title;
        this.rating = rating;
        this.status = status;
    }

    public WatchlistItem(int id, String title, double rating, String status) {
        this.id = id;
        this.title = title;
        this.rating = rating;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "WatchlistItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", rating=" + rating +
                ", status='" + status + '\'' +
                '}';
    }
}
