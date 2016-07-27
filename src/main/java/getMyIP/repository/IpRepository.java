package getMyIP.repository;

import getMyIP.model.Ip;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IpRepository extends CrudRepository<Ip, Long>{
  public Ip findFirst1ByOrderByIdDesc();
}
