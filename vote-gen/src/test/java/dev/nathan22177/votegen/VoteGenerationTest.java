package dev.nathan22177.votegen;

import dev.nathan22177.votegen.exchange.Vote;
import dev.nathan22177.votegen.services.VoteGeneratorService;
import dev.nathan22177.votegen.voters.Voters;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
class VoteGenerationTest {

    @Autowired
	VoteGeneratorService voteGeneratorService;

	@Test
    void generateVotes() {
        List<Vote> allVotes = new LinkedList<>();
		List<Vote> currentVotes = new LinkedList<>();
        int votingIdEndTarget = 100000;
        while(true) {
            List<Vote> newVotes = voteGeneratorService.generateVotes().getVotes();

            Assert.isTrue(newVotes
							.stream()
							.allMatch(vote -> vote.getVotingId() == newVotes.get(0).getVotingId()),
                    "voteId should not change while generating votes");
			Assert.isTrue(newVotes
							.stream()
							.allMatch(vote -> vote.getRoundNumber() == newVotes.get(0).getRoundNumber()),
					"votingRoundId should not change while generating votes");

			currentVotes.addAll(newVotes);
			if (thisVoteShouldEnd(currentVotes)) {
				allVotes.addAll(currentVotes);
				int currentVotingId = currentVotes.get(0).getVotingId();
				voteGeneratorService.endVote(currentVotingId);
				currentVotes = new LinkedList<>();
				if (votingIdEndTarget == currentVotingId) {
					break;
				}
			}
        }
		checkCasperPattern(allVotes);
        checkBalthazarPattern(allVotes);
		checkMelchiorPattern(allVotes);
    }

	private boolean thisVoteShouldEnd(List<Vote> currentVotes) {
		return currentVotes
				.stream()
				.filter(Vote::isInFavor)
				.count() >= 3 ||
				currentVotes
				.stream()
				.filter(vote -> !vote.isInFavor())
				.count() >= 2;
	}

	void checkCasperPattern(List<Vote> votes) {
		Map<Double, List<Vote>> votesByRound = new HashMap<>();
		votes
				.stream()
				.filter(vote -> vote.getParticipantName().equals(Voters.CASPER.name()))
				.forEach(vote -> {
					double voteKey = vote.getRoundNumber() * 0.1 + vote.getVotingId();
					votesByRound.computeIfAbsent(voteKey, k -> new ArrayList<>(2)).add(vote);
				});
		for (Map.Entry<Double, List<Vote>> entry : votesByRound.entrySet()) {
			Assert.isTrue(entry.getValue().size() == 2, "Casper should cast two votes every round");
			Assert.isTrue(entry.getValue().stream().allMatch(Vote::isInFavor), "Casper should only vote in favor");
		}
	}

	void checkBalthazarPattern(List<Vote> votes){
		boolean previousVote = Boolean.FALSE;
		List<Vote> balthazarVotes = votes
				.stream()
				.filter(vote -> vote.getParticipantName().equals(Voters.BALTHAZAR.name()))
				.collect(Collectors.toList());

		for (Vote vote : balthazarVotes) {
			Assert.isTrue(vote.isInFavor() != previousVote, "Balthazar should change it's favor every round");
			previousVote = vote.isInFavor();
		}
	}

	void checkMelchiorPattern(List<Vote> votes){
		Map<Double, List<Vote>> votesByRound = new TreeMap<>();
		int skippedRounds = 0;
		votes.forEach(vote -> {
				double voteKey = vote.getRoundNumber() * 0.1 + vote.getVotingId();
					votesByRound.computeIfAbsent(voteKey, k -> new ArrayList<>(2)).add(vote);
				});
		for (Map.Entry<Double, List<Vote>> entry : votesByRound.entrySet()) {
			boolean melchiorHasVotedThisRound = entry
					.getValue()
					.stream()
					.anyMatch(vote -> vote.getParticipantName().equals(Voters.MELCHIOR.name()));
			if (!melchiorHasVotedThisRound) {
				skippedRounds++;
			} else {
				Assert.isTrue(skippedRounds == 2, "Melchior can only vote every third round");
				skippedRounds = 0;
			}
			Assert.isTrue(skippedRounds <= 2, "Melchior cannot skip more than two rounds");
		}
	}

}
