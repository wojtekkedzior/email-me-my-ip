package getMyIP;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableScheduling
@EnableAsync
@SpringBootApplication
@EnableMBeanExport(registration=RegistrationPolicy.IGNORE_EXISTING)
public class Application extends org.springframework.boot.web.servlet.support.SpringBootServletInitializer {

	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public Runner getRunner() {
		return new Runner();
	}
	
	
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("urlLookUp-");
        executor.initialize();
        return executor;
    }
}