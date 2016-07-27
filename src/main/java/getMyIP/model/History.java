package getMyIP.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class History {
  
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private long id;
  
  private int checks;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getChecks() {
    return checks;
  }

  public void setChecks(int checks) {
    this.checks = checks;
  }
  
  
  

}
