package dev.nathan22177.votegen.feedback;

import dev.nathan22177.votegen.exchange.Vote;
import dev.nathan22177.votegen.exchange.VotingResult;
import dev.nathan22177.votegen.services.VoteGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Controller
@Slf4j
public class VoteGeneratorFeedbackController {

    final
    VoteGeneratorService voteGeneratorService;
    private Set<Vote> processedVotes;

    public VoteGeneratorFeedbackController(VoteGeneratorService voteGeneratorService) {
        this.voteGeneratorService = voteGeneratorService;
    }

    @PostConstruct
    public void init(){
        processedVotes = new HashSet<>();
    }

    @RequestMapping(value = "/notify", method = RequestMethod.POST)
    public ResponseEntity<Object> notifyAboutProcessedVote(@RequestBody Vote processedVote) {
        if (!processedVotes.add(processedVote)) {

            log.warn("Received notification about vote that has already been processed. " +
                            "votingId: {}, round #{}, participantId: {}, voted: {}",
                    processedVote.getVotingId(), processedVote.getRoundNumber(),
                    processedVote.getParticipantId(), processedVote.isInFavor());

            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Server has already been notified about this vote being processed.");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/result/{id}", method = RequestMethod.POST)
    public ResponseEntity<Object> notifyVoteEndingAndStartNewVote(@PathVariable int id,
                                                                  @RequestBody VotingResult result){
        int currentVotingId = voteGeneratorService.getCurrentVotingId();
        if (id < currentVotingId) {
            log.warn("Received results of an already ended voting. votingId: {}, currentVotingId: {}", id, currentVotingId);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Wrong votingId. This voting has already been ended.");
        }

        if (id > currentVotingId) {
            log.warn("Received results of yet to be started voting. votingId: {}, currentVotingId: {}", id, currentVotingId);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Wrong votingId one or more ids has been skipped.");
        }

        String resultStr = result.isInFavor() ? "accepted" : "rejected";
        log.info("Vote #{} has ended, the motion was {}. Votes in favor: {}. Votes against: {}",
                id, resultStr, result.getYays(), result.getNays());
        voteGeneratorService.endVote(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
