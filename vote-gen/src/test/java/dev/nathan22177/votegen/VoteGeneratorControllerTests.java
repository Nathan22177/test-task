package dev.nathan22177.votegen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import dev.nathan22177.votegen.controllers.VoteGeneratorController;
import dev.nathan22177.votegen.exchange.Vote;
import dev.nathan22177.votegen.exchange.VoteBatch;
import dev.nathan22177.votegen.exchange.VotingResult;
import dev.nathan22177.votegen.voters.Voters;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class VoteGeneratorControllerTests {

	@Value("${local.root}")
	String localUrl;
	@Autowired
	private TestRestTemplate testRestTemplate;
	@Autowired
	private ObjectMapper objectMapper;

	@TestConfiguration
	static class Config {

		@Bean
		public RestTemplateBuilder restTemplateBuilder() {
			return new RestTemplateBuilder();
		}

	}

	@Test
	void getVotesRequestTest() throws JsonProcessingException {
		String url = UriComponentsBuilder
				.fromHttpUrl(localUrl + "/votes")
				.build()
				.toString();
		String response = testRestTemplate.getForObject(url, String.class);
		VoteBatch batch = objectMapper.readValue(response, new TypeReference<>() {});
		Assert.isTrue(!CollectionUtils.isEmpty(batch.getVotes()), "Should not get empty list of votes.");
		Assert.isTrue(batch.getVotes().stream().filter(vote -> vote.getParticipantId() == 0).count() == 2, "Two Casper participants expected on first call.");
		Assert.isTrue(batch.getVotes().stream().filter(vote -> vote.getParticipantId() == 1).count() == 1, "One Balthazar participant expected on first call.");
		Assert.isTrue(batch.getVotes().stream().noneMatch(vote -> vote.getParticipantId() == 2), "No Melchior participants expected on first call.");
	}

	@Test
	void endVotingRequestTest() {
		String endVotingUrl = UriComponentsBuilder
				.fromHttpUrl(localUrl + "/result/" + 0)
				.build()
				.toString();

		ResponseEntity<String> response = testRestTemplate.postForEntity(endVotingUrl, new VotingResult(0, true, 3, 0), String.class);
		Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Should be OK.");

		// Do it again
		response = testRestTemplate.postForEntity(endVotingUrl, new VotingResult(0, true, 3, 0), String.class);
		Assert.isTrue(response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY, "This voting has already been ended before.");

		// Now try end the vote that should be 5 votes from now
		response = testRestTemplate.postForEntity(endVotingUrl, new VotingResult(6, true, 3, 0), String.class);
		Assert.isTrue(response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY, "One or more ids has been skipped.");
	}

	@Test
	void notifyVoteProcessing() {
		String notifyUrl = UriComponentsBuilder
				.fromHttpUrl(localUrl + "/notify")
				.build()
				.toString();

		Vote vote = new Vote(0, true, 0, 0, Voters.CASPER.name());
		ResponseEntity<String> response = testRestTemplate.postForEntity(notifyUrl, vote , String.class);
		Assert.isTrue(response.getStatusCode() == HttpStatus.OK, "Should be OK.");

		// Do it again
		response = testRestTemplate.postForEntity(notifyUrl, vote , String.class);
		Assert.isTrue(response.getStatusCode() == HttpStatus.NOT_ACCEPTABLE, "Second identical vote processing should be denied.");
	}
}
