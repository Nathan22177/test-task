package dev.nathan22177.voteproc;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class VoteProcessingControllerTests {

	@Value("${local.root}")
	String localUrl;
	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	void getVotesRequestTest(){
		String url = UriComponentsBuilder
				.fromHttpUrl(localUrl + "/state/" + 10)
				.build()
				.toString();
		ResponseEntity<String> response = testRestTemplate.getForEntity(url, String.class);
		Assert.isTrue(response.getStatusCode() == HttpStatus.NOT_FOUND, "This voting isn't supposed to exist.");
	}

}
