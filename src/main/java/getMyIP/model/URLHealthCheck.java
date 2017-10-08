package getMyIP.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class URLHealthCheck {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String url;

	@Temporal(TemporalType.TIMESTAMP)
	private Date lastChecked;

	private String state; // should be an enum
	private int count;
	private int failures;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastFailure;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getLastChecked() {
		return lastChecked;
	}

	public void setLastChecked(Date lastChecked) {
		this.lastChecked = lastChecked;
	}

	public String getState() {
		return state; 
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getFailures() {
		return failures;
	}

	public void setFailures(int failures) {
		this.failures = failures;
	}

	public Date getLastFailure() {
		return lastFailure;
	}

	public void setLastFailure(Date lastFailure) {
		this.lastFailure = lastFailure;
	}
	

}
