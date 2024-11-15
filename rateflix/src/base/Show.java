package base;

import java.util.Arrays;
import java.util.Date;

public class Show {
	private String title;
	private String director;
	private String[] cast;
	private Date date;
	
	public Show(String title, String director, String[] cast, Date date) {
		this.title = title;
		this.director = director;
		this.cast = cast;
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String[] getCast() {
		return cast;
	}

	public void setCast(String[] cast) {
		this.cast = cast;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "Show [title=" + title + ", director=" + director + ", cast=" + Arrays.toString(cast) + ", date=" + date
				+ "]";
	}
	
}
