package getMyIP;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import getMyIP.model.History;
import getMyIP.model.Ip;
import getMyIP.model.URLHealthCheck;
import getMyIP.repository.HistoryRepository;
import getMyIP.repository.IpRepository;
import getMyIP.repository.URLCheckRepository;

@Controller
public class GreetingController {

  @Autowired
  private IpRepository repo;

  @Autowired
  private HistoryRepository historyRepo;
  
  @Autowired
  private URLCheckRepository urlCheckRepo;

  @RequestMapping(method = RequestMethod.GET, value = "/stats")
  @ResponseBody
  public ResponseEntity<?> getstats() throws FileNotFoundException, UnknownHostException {

    String hostName = InetAddress.getLocalHost().getHostName();
    Ip lastIp = repo.findByHostname(hostName);

    StringBuffer res = new StringBuffer();
    res.append("<p>Current IP: " + lastIp.getIp() + "  changed on: " + lastIp.getChangeDate() + "</p>\n");
    res.append("<p>Total checks: " + lastIp.getChecks() + ".  Failures: " + lastIp.getFailures() + ".  Last  checked at: " + lastIp.getLastChecked() + "</p>\n");
    
    return new ResponseEntity<String>(res.toString(), HttpStatus.OK);
  }
  
  @RequestMapping("/greeting")
  public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) throws UnknownHostException {
      String hostName = InetAddress.getLocalHost().getHostName();
      
      Ip myIp = repo.findByHostname(hostName);
      model.addAttribute("myIp", myIp.getIp());
      
      Iterable<Ip> allCheckedIPs = repo.findAll();
      model.addAttribute("allCheckedIPs", allCheckedIPs);
      
      History history = historyRepo.findOne(1l);
      model.addAttribute("totalChecks", history.getChecks());
      
      long numberOfUrls = urlCheckRepo.count();
      model.addAttribute("numberOfUrls", numberOfUrls);
      
      Iterable<URLHealthCheck> allUrls = urlCheckRepo.findAll();
      model.addAttribute("urls", allUrls);
      
      return "greeting";
  }
  
  @RequestMapping(method = RequestMethod.GET, value = "/wojtek")
	@ResponseBody
	public ResponseEntity<?> getOriginalImage() {
//		final HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.IMAGE_PNG);
//
//		if (originalFile == null) {
//			return new ResponseEntity<byte[]>(new byte[0], headers, HttpStatus.OK);
//		}
//		int[] data = read(originalFile);
//		BMPFormat imageFormat = new BMPFormat(data);
//		byte[] myBytes = imageFormat.getCompleteImageAsByteArray();
//
//		visitCounterService.incrementVisitCounter();

//		return new ResponseEntity<byte[]>(myBytes, headers, HttpStatus.CREATED);
	  return new ResponseEntity<>(HttpStatus.OK);
	  
	}
}