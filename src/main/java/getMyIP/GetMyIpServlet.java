package getMyIP;

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
	private StatisticsStore store;

	@Override
	public void init(ServletConfig config) throws ServletException {
		 WebApplicationContextUtils.getWebApplicationContext(config.getServletContext())
	        .getAutowireCapableBeanFactory().autowireBean(this);
	}

	@Override
	public ServletConfig getServletConfig() {
		return null;
	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		res.getWriter().write("<p>Total checks: " + store.getTotalChecks() + "</p>\n");
		res.getWriter().write("<p>Total failures: " + store.getTotalFailures() + "</p>\n");
		res.getWriter().write("<p>Times the IP changed since startup: " + store.getTotalChanges() + "</p>\n");
		res.getWriter().write("<p>CurrentIP: " + store.getCurrentIp() + "</p>\n");
	}

	@Override
	public String getServletInfo() {
		return null;
	}

	@Override
	public void destroy() {

	}
	


}
