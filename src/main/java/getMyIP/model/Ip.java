package getMyIP.model;


import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Ip {
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private long id;
  private String ip;
  @Temporal(TemporalType.TIMESTAMP)
  private Date date;
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
  public Date getDate() {
    return date;
  }
  public void setDate(Date date) {
    this.date = date;
  }
  

  
}
