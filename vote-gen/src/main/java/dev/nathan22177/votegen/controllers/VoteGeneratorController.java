package dev.nathan22177.votegen.controllers;

import dev.nathan22177.votegen.exchange.Vote;
import dev.nathan22177.votegen.exchange.VoteBatch;
import dev.nathan22177.votegen.services.VoteGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@Slf4j
public class VoteGeneratorController {

    final VoteGeneratorService voteGeneratorService;

    public VoteGeneratorController(VoteGeneratorService voteGeneratorService) {
        this.voteGeneratorService = voteGeneratorService;
    }

    @RequestMapping(value = "/votes", method = RequestMethod.GET)
    public ResponseEntity<VoteBatch> getVotes() {
        VoteBatch batch = voteGeneratorService.generateVotes();
        log.info("Sending new batch of votes for voting #{}, round#{}", voteGeneratorService.getCurrentVotingId(), voteGeneratorService.getCurrentVotingRoundNumber());
        return new ResponseEntity<>(batch, HttpStatus.OK);
    }
}
