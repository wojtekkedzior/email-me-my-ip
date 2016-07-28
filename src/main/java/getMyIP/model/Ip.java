package getMyIP.model;


import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Cacheable(false)
public class Ip {
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private long id;
  
  private String ip;
  
  @Temporal(TemporalType.TIMESTAMP)
  private Date changeDate;
  
  private int checks;
  
  private int failures;
  
  private String hostname;
 
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastChecked;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public Date getChangeDate() {
    return changeDate;
  }

  public void setChangeDate(Date changeDate) {
    this.changeDate = changeDate;
  }

  public int getChecks() {
    return checks;
  }

  public void setChecks(int checks) {
    this.checks = checks;
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public Date getLastChecked() {
    return lastChecked;
  }

  public void setLastChecked(Date lastChecked) {
    this.lastChecked = lastChecked;
  }

  public int getFailures() {
    return failures;
  }

  public void setFailures(int failures) {
    this.failures = failures;
  }
  
  

  
}
