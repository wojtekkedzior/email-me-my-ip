package getMyIP;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableAsync
@EnableScheduling
public class GetMyIPApp extends SpringBootServletInitializer {

	private StatisticsStore store;

	private String username;
	private String password;
	
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(GetMyIPApp.class);
	}

	public static void main(final String[] args) {
		SpringApplication.run(GetMyIPApp.class, args);
	}

	@Bean
	public ServletRegistrationBean atmosphereServlet() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new GetMyIpServlet(), "/stats");
		registration.setLoadOnStartup(1);
		
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			Properties prop = new Properties();
			prop.load(classLoader.getResourceAsStream("application.properties"));
			
			username = prop.getProperty("email");
			password = prop.getProperty("password");
			
			logger.info("username: " + username);
			logger.info("password: " + password);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return registration;
	}

	@Bean
	public StatisticsStore getStatisticsStore() {
		if (store == null) {
			store = new StatisticsStore();
		}
		return store;
	}
	
	@Scheduled(fixedDelay = 1_800_000)
	// 30 mins
	public void doSomething() {
		checkMyIP();
	}

	private void checkMyIP() {
		String myIp = "";
		File myIpFile = new File("myIp.txt");

		if (!myIpFile.exists()) {
			try {
				myIpFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			URL oracle = new URL("http://checkip.dyndns.org/");
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
			String inputLine = in.readLine();
			in.close();

			int startIndex = inputLine.indexOf("Current IP Address: ");
			int endIndex = inputLine.indexOf("</body></html>");

			String substring = inputLine.substring(startIndex, endIndex);

			int startIndex1 = substring.indexOf(": ");
			myIp = substring.substring(startIndex1 + 2, substring.length());

			logger.info("Current IP is: " + myIp);

			BufferedReader fileReader = new BufferedReader(new FileReader(myIpFile));

			String line = "";
			LinkedList<String> lines = new LinkedList<String>();
			while ((line = fileReader.readLine()) != null) {
				lines.add(line);
			}
			fileReader.close();

			if (lines.isEmpty()) {
				Writer output = new BufferedWriter(new FileWriter(myIpFile, true)); // clears file every time
				output.append("\n" + myIp);
				output.close();

				sendEmail(myIp);
			} else {
				String lastIp = lines.getLast();

				if (!lastIp.equals(myIp)) {
					logger.info("IP changed!!  was: " + lastIp + " now: " + myIp);

					Writer output = new BufferedWriter(new FileWriter(myIpFile, true));
					output.append("\n" + myIp);
					output.close();
					logger.info("Wrote new IP to file");

					sendEmail(myIp);

					store.incrementChange();
				}
			}

			store.incrementCheck();
			
			store.setCurrentIp(myIp);

		} catch (MalformedURLException e1) {
			store.incrementFailure();
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			store.incrementFailure();
		}
	}

	private void sendEmail(String ip) {
		try {
			
			Properties props = new Properties();
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");

			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(username));
			message.setSubject("My IP changed to: " + ip);
			message.setText("My IP: " + ip);
			Transport.send(message);
			logger.info("Email sent for IP change: " + ip);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}