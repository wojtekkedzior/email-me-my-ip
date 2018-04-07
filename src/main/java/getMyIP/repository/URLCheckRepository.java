package getMyIP.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import getMyIP.model.URLHealthCheck;

@Repository
public interface URLCheckRepository extends CrudRepository<URLHealthCheck, Long> {
	public URLHealthCheck findByUrl(String url);

}
