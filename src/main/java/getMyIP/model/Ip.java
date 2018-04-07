package getMyIP.model;


import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;

@Entity
@Cacheable(false)
@Data
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
}
