package dev.nathan22177.votegen.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class VoteProcessingChecker {

    @Value("${vote.processing.server.root}")
    private String processingServerUrl;

    final VoteGeneratorService voteGeneratorService;
    final ObjectMapper objectMapper;
    final RestTemplate restTemplate;

    public VoteProcessingChecker(VoteGeneratorService voteGeneratorService, ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.voteGeneratorService = voteGeneratorService;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    public void checkVoteProcessing() {
        int currentVotingId = voteGeneratorService.getCurrentVotingId();
        String url = UriComponentsBuilder
                        .fromHttpUrl(processingServerUrl + "/state/" + currentVotingId)
                        .build()
                        .toString();
        String response = restTemplate.getForObject(url, String.class);
        log.info("Received state of voting #{}. Response: {}", currentVotingId, response);
    }
}
