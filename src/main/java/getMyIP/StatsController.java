package getMyIP;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import getMyIP.model.History;
import getMyIP.model.Ip;
import getMyIP.model.URLHealthCheck;
import getMyIP.repository.HistoryRepository;
import getMyIP.repository.IpRepository;
import getMyIP.repository.URLCheckRepository;

@Controller
public class StatsController {

	@Autowired
	private IpRepository repo;

	@Autowired
	private HistoryRepository historyRepo;

	@Autowired
	private URLCheckRepository urlCheckRepo;

	@RequestMapping("/")
	public String getStats(Model model) throws UnknownHostException {
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

		return "stats";
	}
}