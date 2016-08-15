package getMyIP;

import getMyIP.model.Ip;
import getMyIP.repository.HistoryRepository;
import getMyIP.repository.IpRepository;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GetMyIpServlet {

  @Autowired
  private IpRepository repo;

  @Autowired
  private HistoryRepository historyRepo;

  @RequestMapping(method = RequestMethod.GET, value = "/stats")
  @ResponseBody
  public ResponseEntity<?> getstats() throws FileNotFoundException, UnknownHostException {

    String hostName = InetAddress.getLocalHost().getHostName();
    Ip lastIp = repo.findByHostname(hostName);

    StringBuffer res = new StringBuffer();
    res.append("<p>Current IP: " + lastIp.getIp() + "  changed on: " + lastIp.getChangeDate() + "</p>\n");
    res.append("<p>Total checks: " + lastIp.getChecks() + " failures: " + lastIp.getFailures() + " last checked at: " + lastIp.getLastChecked() + "</p>\n");

    return new ResponseEntity<String>(res.toString(), HttpStatus.OK);
  }
}
