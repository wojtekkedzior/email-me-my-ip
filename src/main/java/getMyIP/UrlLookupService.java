package getMyIP;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class UrlLookupService {
	private final Logger log = LoggerFactory.getLogger(Runner.class);
	
    @Async
    public CompletableFuture<String> findUser(String urlToCheck) throws InterruptedException {
    	// log.info("Looking up " + user);//
    //    String url = String.format("https://api.github.com/users/%s", user);
        //User results = restTemplate.getForObject(url, User.class);
    	
    	Timer timer = new Timer();
    	
    	timer.schedule(new TimerTask() {
    		  @Override
    		  public void run() {
    		    // Your database code here
    		  }
    		}, 2*60*1000);
    	
    	
    	String state = new String();
    	
		try {
			log.info("Checking URL: " + urlToCheck);
//			URLHealthCheck findByUrl = urlCheckRepo.findByUrl(listedUrl);
			
//			if (findByUrl == null) {
//				findByUrl = new URLHealthCheck();
//				findByUrl.setUrl(listedUrl);
//			}
			
//			Date now = new Date(System.currentTimeMillis());
//			findByUrl.setLastChecked(now);
			
			URL url = new URL(urlToCheck);
			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setConnectTimeout(5000);
			log.info("URL: " + urlToCheck + " returned: " + http.getResponseCode());

			state = http.getResponseCode() == 200 ? "up" : "down";
			
//			findByUrl.setState(state);

			if(http.getResponseCode() != 200) {
//				findByUrl.setFailures(findByUrl.getFailures() +1);
//				findByUrl.setLastFailure(now);
				
				log.warn("Got state: " + state + " for URL: " + urlToCheck);
			}
			
//			findByUrl.setCount(findByUrl.getCount() + 1);

//			urlCheckRepo.save(findByUrl);

		} catch (IOException e) {
//			String state = "down";
//			findByUrl.setState(state);
//
//			findByUrl.setFailures(findByUrl.getFailures() +1);
//			findByUrl.setLastFailure(now);
//			findByUrl.setCount(findByUrl.getCount() + 1);
//
//			urlCheckRepo.save(findByUrl);
			
			log.error(urlToCheck + " " + e.toString());
			return CompletableFuture.completedFuture("down");
		}
    	
        return CompletableFuture.completedFuture(state);
    }
}