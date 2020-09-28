package tw.elliot.trick02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class Trick02Application {

	public static void main(String[] args) {
		SpringApplication.run(Trick02Application.class, args);
	}

}
