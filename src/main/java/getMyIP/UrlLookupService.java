package getMyIP;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import getMyIP.model.URLHealthCheck;
import getMyIP.repository.URLCheckRepository;

@Service
public class UrlLookupService {
	private final Logger log = LoggerFactory.getLogger(Runner.class);
	
	@Autowired
	private URLCheckRepository urlCheckRepo;
	
    @Async
    public CompletableFuture<String> findUser(String urlToCheck) throws InterruptedException {
    	String state = new String();
    	URLHealthCheck findByUrl = urlCheckRepo.findByUrl(urlToCheck);
    	Date now = new Date(System.currentTimeMillis());
    	
		try {
			log.info("Checking URL: " + urlToCheck);
			
			if (findByUrl == null) {
				findByUrl = new URLHealthCheck();
				findByUrl.setUrl(urlToCheck);
			}
			
			findByUrl.setLastChecked(now);
			
			URL url = new URL(urlToCheck);
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setConnectTimeout(5000);
			log.info("URL: " + urlToCheck + " returned: " + http.getResponseCode());

			state = http.getResponseCode() == 200 ? "up" : "down";
			
			findByUrl.setState(state);

			if(http.getResponseCode() != 200) {
				findByUrl.setFailures(findByUrl.getFailures() +1);
				findByUrl.setLastFailure(now);
				
				log.warn("Got state: " + state + " for URL: " + urlToCheck);
			}
			
			findByUrl.setCount(findByUrl.getCount() + 1);

			urlCheckRepo.save(findByUrl);

		} catch (IOException e) {
//			String state = "down";
			findByUrl.setState(state);

			findByUrl.setFailures(findByUrl.getFailures() +1);
			findByUrl.setLastFailure(now);
			findByUrl.setCount(findByUrl.getCount() + 1);

			urlCheckRepo.save(findByUrl);
			
			log.error(urlToCheck + " " + e.toString());
			return CompletableFuture.completedFuture("down");
		}
    	
        return CompletableFuture.completedFuture(state);
    }
}