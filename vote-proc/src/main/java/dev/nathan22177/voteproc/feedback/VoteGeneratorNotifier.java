package dev.nathan22177.voteproc.feedback;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nathan22177.votegen.exchange.Vote;
import dev.nathan22177.votegen.exchange.VotingResult;
import dev.nathan22177.voteproc.common.Voting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class VoteGeneratorNotifier {

    @Value("${vote.generator.server.root}")
    private String generatorServerUrl;

    final ObjectMapper objectMapper;
    final RestTemplate restTemplate;
    final
    RetryTemplate retryTemplate;

    public VoteGeneratorNotifier(ObjectMapper objectMapper, RestTemplate restTemplate, RetryTemplate retryTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
    }

    @Retryable(
            value = ResponseStatusException.class,
            maxAttempts = 10,
            backoff = @Backoff(delay = 1000)
    )
    public void notifyGeneratorAboutProcessedVote(Vote vote) {
        String url = UriComponentsBuilder
                .fromHttpUrl(generatorServerUrl + "/notify")
                .build()
                .toString();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, vote, String.class);
            log.info(
                    "Successfully notified vote generator about processed vote. " +
                    "votingId: {}, roundNumber: {}, participantId:{}, inFavor: {}, response status: {}",
                    vote.getVotingId(), vote.getRoundNumber(), vote.getParticipantId(), vote.isInFavor(), response.getStatusCode()
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_ACCEPTABLE) {
                log.error(
                        "votingId: {}, roundNumber: {}, participantId:{}, inFavor: {}  " +
                        "Looks like generator has already been notified about this vote being processed.",
                        vote.getVotingId(), vote.getRoundNumber(), vote.getParticipantId(), vote.isInFavor()
                );
            } else {
                throw new ResponseStatusException(e.getStatusCode(), String.format(
                        "votingId: %s, roundNumber: %s, participantId: %s, inFavor: %s " +
                        "Not OK status code after notifying generator about processed vote: %s",
                        vote.getVotingId(), vote.getRoundNumber(), vote.getParticipantId(), vote.isInFavor(), e.getStatusCode()
                ));
            }
        }


    }

    @Retryable(
            value = ResponseStatusException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000)
    )
    public void notifyGeneratorAboutEndedVote(VotingResult result) {
        String url = UriComponentsBuilder
                .fromHttpUrl(generatorServerUrl + "/result/" + result.getId())
                .build()
                .toString();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, result, String.class);
            log.info(
                    "Successfully notified vote generator about voting conclusion." +
                    "votingId: {},  inFavor: {}, response status: {}",
                    response.getStatusCode(), result.getId(), result.isInFavor()
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_ACCEPTABLE) {
                log.error(
                        "votingId: {} does not correlate with active voting id on generator server",
                        result.getId()
                );
            }
            throw new ResponseStatusException(e.getStatusCode(), String.format(
                    "votingId: %s. Not OK status code after notifying generator about ended voting: %s",
                    result.getId(), e.getStatusCode()
            ));
        }
    }

}
