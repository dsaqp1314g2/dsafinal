package edu.upc.eetac.dsa.dsaqt1314g2.videoshare.api.model;

import java.util.ArrayList;
import java.util.List;


public class ReviewCollection {
	List <Review> reviews;
	private long newestTimestamp;
	private long oldestTimestamp;

	public ReviewCollection() {
		super();
		reviews = new ArrayList<Review>();
	}
	public List<Review> getReviews() {
		return reviews;
	}
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	
	public void addUser(Review r) {
		reviews.add(r);
	}
	public long getNewestTimestamp() {
		return newestTimestamp;
	}
	public void setNewestTimestamp(long newestTimestamp) {
		this.newestTimestamp = newestTimestamp;
	}
	public long getOldestTimestamp() {
		return oldestTimestamp;
	}
	public void setOldestTimestamp(long oldestTimestamp) {
		this.oldestTimestamp = oldestTimestamp;
	}
}
