package dev.nathan22177.votegen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class VoteGenApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoteGenApplication.class, args);
	}

}
