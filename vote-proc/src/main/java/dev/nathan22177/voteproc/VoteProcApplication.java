package dev.nathan22177.voteproc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRetry
public class VoteProcApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoteProcApplication.class, args);
	}

}
