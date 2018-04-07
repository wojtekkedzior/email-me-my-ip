package getMyIP.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Data
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
}
