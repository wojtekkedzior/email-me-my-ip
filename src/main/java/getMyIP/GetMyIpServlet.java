package getMyIP;

import getMyIP.model.History;
import getMyIP.model.Ip;
import getMyIP.repository.HistoryRepository;
import getMyIP.repository.IpRepository;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class GetMyIpServlet implements Servlet {

  public GetMyIpServlet() {

  }

  @Autowired
  private IpRepository repo;
  
  @Autowired
  private HistoryRepository historyRepo;

  @Override
  public void init(ServletConfig config) throws ServletException {
    WebApplicationContextUtils.getWebApplicationContext(config.getServletContext()).getAutowireCapableBeanFactory().autowireBean(this);
  }

  @Override
  public ServletConfig getServletConfig() {
    return null;
  }

  @Override
  public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
    History history = historyRepo.findOne(1L);
     res.getWriter().write("<p>Total checks: " + history.getChecks() + "</p>\n");
     Ip lastIp = repo.findFirst1ByOrderByIdDesc();
     res.getWriter().write("<p>CurrentIP: " + lastIp.getIp() + " changed on: " + lastIp.getDate() + "</p>\n");
  }

  @Override
  public String getServletInfo() {
    return null;
  }

  @Override
  public void destroy() {

  }

}
