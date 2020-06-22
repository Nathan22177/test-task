package dev.nathan22177.voteproc.controllers;

import dev.nathan22177.voteproc.common.Voting;
import dev.nathan22177.voteproc.services.VoteProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@Slf4j
public class VoteProcessingController {
    final
    VoteProcessingService voteProcessingService;

    public VoteProcessingController(VoteProcessingService voteProcessingService) {
        this.voteProcessingService = voteProcessingService;
    }

    @RequestMapping(value = "/state/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> votingStateCheck(@PathVariable int id) {
        Voting voting = voteProcessingService.getOne(id);
        if (voting == null) {
            return new ResponseEntity<>(String.format("No such voting #%s exists", id), HttpStatus.NOT_FOUND);
        }
        log.info("Sending state of voting #{}.", id);
        return new ResponseEntity<>(voting.getStatus().getMessage(), HttpStatus.OK);
    }
}
