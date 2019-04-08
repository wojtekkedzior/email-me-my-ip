package getMyIP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import getMyIP.model.History;
import getMyIP.model.Ip;
import getMyIP.repository.HistoryRepository;
import getMyIP.repository.IpRepository;

@EnableScheduling
public class Runner {

	private final Logger log = LoggerFactory.getLogger(Runner.class);

	@Autowired
	private IpRepository repo;

	@Autowired
	private HistoryRepository historyRepo;

	@Value("${email}")
	private String email;
	@Value("${password}")
	private String password;

	@Value("${urls.to.check}")
	private String urlsToCheck;

	@Autowired
	private UrlLookupService urlLookupService;

	public Runner() {
	}

	@Scheduled(fixedDelay = 43_200_000) // every 12 hours
	private void checkUrls() throws InterruptedException, ExecutionException {
		List<CompletableFuture<String>> futures = new ArrayList<CompletableFuture<String>>();

		long start = System.currentTimeMillis();

		Arrays.asList(urlsToCheck.split(", ")).stream().forEach(url -> {
			CompletableFuture<String> page = urlLookupService.findUser(url);
			futures.add(page);
		});

		// Wait until they are all done
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

		// Print results, including elapsed time
		log.info("Elapsed time: " + (System.currentTimeMillis() - start));

	}

	@Scheduled(fixedDelay = 43_200_000)
	private void checkMyIP() throws UnknownHostException {
		String inputLine = "";

		String hostName = InetAddress.getLocalHost().getHostName();
		Date now = new Date(System.currentTimeMillis());

		boolean inError = false;

		try {
			URL oracle = new URL("http://checkip.dyndns.org/");
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
			inputLine = in.readLine();
			in.close();
		} catch (IOException e) {
			log.error("Check failed for host: " + hostName + " with: " + e);
			inError = true;
		}

		Ip lastValidEntry = repo.findByHostname(hostName);

		if (inError) {
			if (lastValidEntry == null) {
				lastValidEntry = new Ip();
				lastValidEntry.setChangeDate(now);
				lastValidEntry.setHostname(hostName);
				lastValidEntry.setLastChecked(now);
				lastValidEntry.setIp("");
			} else {
				lastValidEntry.setChecks(lastValidEntry.getChecks() + 1);
				lastValidEntry.setLastChecked(now);
			}
			lastValidEntry.setFailures(lastValidEntry.getFailures() + 1);
		} else {
			int startIndex = inputLine.indexOf("Current IP Address: ");
			int endIndex = inputLine.indexOf("</body></html>");

			String substring = inputLine.substring(startIndex, endIndex);

			int startIndex1 = substring.indexOf(": ");
			String myIp = substring.substring(startIndex1 + 2, substring.length());

			if (lastValidEntry == null) {
				lastValidEntry = new Ip();
				lastValidEntry.setChangeDate(now);
				lastValidEntry.setIp(myIp);
				lastValidEntry.setHostname(hostName);
				lastValidEntry.setLastChecked(now);
				log.info("No existing IP found.  Saving current IP: " + myIp);
			} else {
				if (!lastValidEntry.getIp().equals(myIp)) {
					lastValidEntry.setChecks(lastValidEntry.getChecks() + 1);
					lastValidEntry.setLastChecked(now);
					log.info("Old IP was: " + lastValidEntry.getIp() + " and now replacing with a new IP: " + myIp);
					lastValidEntry.setIp(myIp);
					sendEmail(myIp);
				} else {
					lastValidEntry.setChecks(lastValidEntry.getChecks() + 1);
					lastValidEntry.setLastChecked(now);
					log.info("IP has not changed, still: " + myIp);
				}
			}
		}

		repo.save(lastValidEntry);
		History findOne = historyRepo.findById(1L).get();
		int checks = findOne.getChecks();
		findOne.setChecks(checks + 1);
		historyRepo.save(findOne);
	}

	private void sendEmail(String ip) {
		try {
			Properties props = new Properties();
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(email, password);
				}
			});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(email));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("My IP changed to: " + ip);
			message.setText("My IP: " + ip);
			Transport.send(message);
		} catch (MessagingException e) {
			log.info("Failed to send email:  " + e.getMessage());
		}
	}
}