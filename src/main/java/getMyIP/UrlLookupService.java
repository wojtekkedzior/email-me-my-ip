package getMyIP;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import getMyIP.model.URLHealthCheck;
import getMyIP.repository.URLCheckRepository;

@Service
public class UrlLookupService {
	private final Logger log = LoggerFactory.getLogger(Runner.class);
	
	@Autowired
	private URLCheckRepository urlCheckRepo;
	
	private static final String UP = "UP";
	private static final String DOWN = "DOWN";
	
    @Async
    public CompletableFuture<String> findUser(String urlToCheck) throws InterruptedException {
    	URLHealthCheck findByUrl = urlCheckRepo.findByUrl(urlToCheck);
    	Date now = new Date(System.currentTimeMillis());
		try {
			log.info("Checking URL: " + urlToCheck);
			
			if (findByUrl == null) {
				findByUrl = new URLHealthCheck();
				findByUrl.setUrl(urlToCheck);
			}
			
			findByUrl.setState(new String(DOWN));
			findByUrl.setLastChecked(now);
			
			URL url = new URL(urlToCheck);
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setConnectTimeout(5000);
			log.info("URL: " + urlToCheck + " returned: " + http.getResponseCode());
			
			findByUrl.setState(http.getResponseCode() == HttpStatus.OK.value()  ? UP : DOWN);

			if(http.getResponseCode() != HttpStatus.OK.value() ) {
				findByUrl.setFailures(findByUrl.getFailures() +1);
				findByUrl.setLastFailure(now);
				
				log.warn(urlToCheck + " responded with " + http.getResponseCode());
			}
			
			findByUrl.setCount(findByUrl.getCount() + 1);

			urlCheckRepo.save(findByUrl);

		} catch (IOException e) {
			findByUrl.setFailures(findByUrl.getFailures() +1);
			findByUrl.setLastFailure(now);
			findByUrl.setCount(findByUrl.getCount() + 1);

			urlCheckRepo.save(findByUrl);
			
			log.error("Error while checking: " + urlToCheck + "  was  " + e.toString());
			return CompletableFuture.completedFuture(DOWN);
		}
    	
        return CompletableFuture.completedFuture(UP);
    }
}