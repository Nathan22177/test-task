package dev.nathan22177.votegen.services;

import dev.nathan22177.votegen.exchange.Vote;
import dev.nathan22177.votegen.exchange.VoteBatch;
import dev.nathan22177.votegen.voters.Voters;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
@Getter
public class VoteGeneratorService {

    private int totalRound;
    private int currentVotingRoundNumber;
    private int currentVotingId;

    public VoteBatch generateVotes() {
        List<Vote> tmp = new LinkedList<>();
        for (int i = 0; i < Voters.values().length; i++) {
            Voters voter = Voters.values()[i];
            Boolean[] castedVotes = voter.getNextVotes(totalRound);
            for (Boolean inFavor : castedVotes) {
                tmp.add(new Vote(
                        voter.getId(),
                        inFavor,
                        currentVotingRoundNumber,
                        currentVotingId,
                        voter.name()
                ));
            }
        }

        return new VoteBatch(totalRound++, currentVotingRoundNumber++, tmp);
    }


    public void endVote(int id) {
        if (currentVotingId == id) {
            currentVotingId++;
            currentVotingRoundNumber = 0;
        }
    }
}
