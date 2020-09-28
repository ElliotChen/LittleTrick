package tw.elliot.trick02.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class BaseConfig {

	@Bean
	public RetryTemplate buildRetryTemplate() {
		RetryTemplate retryTemplate = RetryTemplate.builder()
				.maxAttempts(2)
				.build();

		return retryTemplate;
	}
}
