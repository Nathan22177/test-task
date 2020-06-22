package dev.nathan22177.voteproc.services;

import dev.nathan22177.votegen.exchange.Vote;
import dev.nathan22177.votegen.exchange.VoteBatch;
import dev.nathan22177.voteproc.common.Voting;
import dev.nathan22177.voteproc.feedback.VoteGeneratorNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class VoteProcessingService {

    Map<Integer, Voting> votingMap;

    final
    VoteGeneratorNotifier voteGeneratorNotifier;

    public VoteProcessingService(VoteGeneratorNotifier voteGeneratorNotifier) {
        this.voteGeneratorNotifier = voteGeneratorNotifier;
    }

    @PostConstruct
    public void init() {
        this.votingMap = new HashMap<>();
        this.votingMap.put(0, new Voting(0));
    }

    public void countVotes(VoteBatch batch) {
       for (Vote vote : batch.getVotes()) {
           int votingId = vote.getVotingId();
           Voting voting = getOne(votingId);
           if (voting == null) {
               continue;
           }
           if (batch.getRoundNumber() - voting.getPreviousRoundNumber() < 1) {
               log.warn("Registered attempt to retrospectively vote in an ended round. " +
                               "votingId: {}, roundNumber: {}, participantId; {}, inFavor: {}. Skipping.",
                       vote.getVotingId(), vote.getRoundNumber(), vote.getParticipantId(), vote.isInFavor());
           }
           if (voting.getStatus().isFinalStatus()) {
               log.warn("Registered attempt to add vote to an ended voting. " +
                       "votingId: {}, roundNumber: {}, participantId; {}, inFavor: {}. Skipping.",
                       vote.getVotingId(), vote.getRoundNumber(), vote.getParticipantId(), vote.isInFavor());
               continue;
           } else {
               if (voting.processVote(vote)) {
                   log.info("Successfully processed a vote. Will now try to notify generator server. " +
                                   "votingId: {}, roundNumber: {}, participantId; {}, inFavor: {}. Skipping.",
                           vote.getVotingId(), vote.getRoundNumber(), vote.getParticipantId(), vote.isInFavor());
                   voteGeneratorNotifier.notifyGeneratorAboutProcessedVote(vote);
               }
           }
           if (voting.getStatus().isFinalStatus()) {
               votingMap.computeIfAbsent(votingId + 1, k -> new Voting(votingId + 1));
               log.warn("Voting has ended. Will now try to notify generator server. votingId: {}",
                       vote.getVotingId());
               voteGeneratorNotifier.notifyGeneratorAboutEndedVote(voting.conclude());
           }
       }
    }

    public Voting getOne(int id) {
        return votingMap.get(id);
    }
}
