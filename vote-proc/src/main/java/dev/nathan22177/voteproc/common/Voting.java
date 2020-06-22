package dev.nathan22177.voteproc.common;

import dev.nathan22177.votegen.exchange.Vote;
import dev.nathan22177.votegen.exchange.VotingResult;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Voting {
    private int id;
    private int yays;
    private int nays;
    private Set<Integer> participants;
    private int previousRoundNumber;
    private VotingStatus status;

    public Voting(int id) {
        this.id = id;
        this.status = VotingStatus.IN_PROGRESS;
        this.participants = new HashSet<>();
        this.previousRoundNumber = -1;
    }

    public VotingResult conclude() {
        if (!this.status.isFinalStatus()) {
            throw new IllegalStateException("Voting is still in progress.");
        }
        boolean inFavor = this.status == VotingStatus.ENDED_ACCEPTED;
        return new VotingResult(this.id, inFavor, this.yays, this.nays);
    }

    public boolean processVote(Vote vote) {
        if (this.status.isFinalStatus()) {
            throw new IllegalStateException("Voting already has enough votes to be ended. No more votes allowed");
        }

        if (this.participants.add(vote.getParticipantId())) {

            if (vote.isInFavor()) {
                yays++;
            } else {
                nays++;
            }

            if (yays == 3) {
                this.status = VotingStatus.ENDED_ACCEPTED;
            }

            if (nays == 2) {
                this.status = VotingStatus.ENDED_REJECTED;
            }

            return true;
        }

        return false;
    }
}
