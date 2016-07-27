package getMyIP;

import getMyIP.model.History;
import getMyIP.model.Ip;
import getMyIP.repository.HistoryRepository;
import getMyIP.repository.IpRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class Runner {

  private static final Logger log = Logger.getLogger(Runner.class);

  @Autowired
  private IpRepository repo;

  @Autowired
  private HistoryRepository historyRepo;

  @Value("${username}")
  private String username;
  @Value("${password}")
  private String password;

  public Runner() {
  }

  @Scheduled(fixedDelay = 300_000) //15 mins
  // @Scheduled(fixedDelay = 1_800_000)
  // 30 mins
  public void doSomething() {
    checkMyIP();
  }

  private void checkMyIP() {
    try {
      URL oracle = new URL("http://checkip.dyndns.org/");
      BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
      String inputLine = in.readLine();
      in.close();

      int startIndex = inputLine.indexOf("Current IP Address: ");
      int endIndex = inputLine.indexOf("</body></html>");

      String substring = inputLine.substring(startIndex, endIndex);

      int startIndex1 = substring.indexOf(": ");
      String myIp = substring.substring(startIndex1 + 2, substring.length());

      Ip lastValidEntry = repo.findFirst1ByOrderByIdDesc();

      if (lastValidEntry == null) {
        Ip myNewIp = new Ip();
        myNewIp.setDate(new Date(System.currentTimeMillis()));
        myNewIp.setIp(myIp);
        repo.save(myNewIp);
        log.info("No existing IP found.  Saving current IP: " + myIp);
      } else {
        if (!lastValidEntry.getIp().equals(myIp)) {
          Ip myNewIp = new Ip();
          myNewIp.setDate(new Date(System.currentTimeMillis()));
          myNewIp.setIp(myIp);
          repo.save(myNewIp);

          log.info("Old IP was: " + lastValidEntry.getIp() + " and now replacing with a new IP: " + myNewIp.getIp());
          sendEmail(myIp);
        } else {
          log.info("IP has not changed, still: " + myIp);
        }
      }
    } catch (IOException e) {
      log.info("Ip check failed: " + e.getMessage());
    } finally {
      History findOne = historyRepo.findOne(1L);
      int checks = findOne.getChecks();
      findOne.setChecks(checks + 1);
      historyRepo.save(findOne);
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
    } catch (MessagingException e) {
      log.info("Failed to send email:  " + e.getMessage());
    }
  }
}
