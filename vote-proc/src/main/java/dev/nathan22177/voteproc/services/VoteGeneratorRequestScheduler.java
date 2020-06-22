package dev.nathan22177.voteproc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import dev.nathan22177.votegen.exchange.Vote;
import dev.nathan22177.votegen.exchange.VoteBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@Slf4j
public class VoteGeneratorRequestScheduler {

    @Value("${vote.generator.server.root}")
    private String generatorServerUrl;

    final VoteProcessingService voteProcessingService;
    final ObjectMapper objectMapper;
    final RestTemplate restTemplate;

    public VoteGeneratorRequestScheduler(VoteProcessingService voteProcessingService, ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.voteProcessingService = voteProcessingService;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    @Profile("!test")
    public void requestVotes() throws JsonProcessingException {
        String url = UriComponentsBuilder
                .fromHttpUrl(generatorServerUrl + "/votes")
                .build()
                .toString();
        String response = restTemplate.getForObject(url, String.class);
        if (response != null) {
            VoteBatch batch = objectMapper.readValue(response, new TypeReference<>() {});
            if (validateBatch(batch)) {
                voteProcessingService.countVotes(batch);
                log.info("Received new batch of votes for voting #{}, round#{}", batch.getVotingId(), batch.getRoundNumber());
            } else {
                log.warn("Received batch with incoherent round numbers. Skipping");
            }

        }
    }

    private boolean validateBatch(VoteBatch batch) {
        return batch.getVotes().stream().allMatch(vote -> vote.getRoundNumber() == batch.getRoundNumber());
    }
}
