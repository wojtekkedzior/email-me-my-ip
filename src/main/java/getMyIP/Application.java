package getMyIP;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@EnableScheduling
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

  @Value("${c3p0.max_size:2}")
  private int maxSize;
  @Value("${c3p0.min_size:1}")
  private int minSize;
  @Value("${c3p0.numHelperThreads:3}")
  private int numHelperThreads;
  @Value("${c3p0.acquire_increment:1}")
  private int acquireIncrement;
  @Value("${c3p0.testPeriod:300}")
  private int testPeriod;
  @Value("${c3p0.testCheckin:true}")
  private boolean testCheckin;
  
  @Value("${spring.datasource.dbHostname}")
  private String dbHostname;
  
  @Value("${spring.datasource.dbName}")
  private String dbName;
  
  @Value("${spring.datasource.username}")
  private String dbUsername;
  
  @Value("${spring.datasource.password}")
  private String dbPassword;
  
  public static void main(final String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Bean
  public Runner getRunner() {
    return new Runner();
  }
  
  private String getDataSourceURL() throws ClassNotFoundException {
      Class.forName("com.mysql.jdbc.Driver");
      final StringBuilder builder = new StringBuilder("jdbc:mysql://");
      builder.append(dbHostname).append(":3306/").append(dbName);
      builder.append("?user=").append(dbUsername);
      builder.append("&password=").append(dbPassword);
      builder.append("&autoReconnect=true");
      return builder.toString();
  }

  @Bean
  public ComboPooledDataSource dataSource() throws ClassNotFoundException {
    final ComboPooledDataSource dataSource = new ComboPooledDataSource();
    dataSource.setMaxPoolSize(maxSize);
    dataSource.setInitialPoolSize(minSize);
    dataSource.setMinPoolSize(minSize);
    dataSource.setAcquireIncrement(acquireIncrement);
    dataSource.setTestConnectionOnCheckin(testCheckin);
    dataSource.setIdleConnectionTestPeriod(testPeriod);
    dataSource.setJdbcUrl(getDataSourceURL());
    return dataSource;
  }
}