package dev.nathan22177.votegen;

import dev.nathan22177.votegen.services.VoteGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@Slf4j
@SpringBootTest
class VoteGeneratorServiceTest {

    @Autowired
    VoteGeneratorService voteGeneratorService;

    @Test
    void flowTest() {
        int currentVotingId = 0;
        for (int i = 0; i < 1000; i++) {
            Assert.isTrue(voteGeneratorService.getTotalRound() == i, "totalRound is not incremented correctly");
            voteGeneratorService.generateVotes();
            if (i % 3 == 0) {
                voteGeneratorService.endVote(currentVotingId++);
                Assert.isTrue(voteGeneratorService.getCurrentVotingId() == currentVotingId,
                        "votingId is not incremented correctly");
                Assert.isTrue(voteGeneratorService.getCurrentVotingRoundNumber() == 0,
                        "currentVotingRoundNumber is not refreshed");
            }
        }
    }

}

