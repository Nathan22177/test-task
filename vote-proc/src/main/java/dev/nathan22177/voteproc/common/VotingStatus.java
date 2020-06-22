package dev.nathan22177.voteproc.common;

import lombok.Getter;

public enum VotingStatus {
    IN_PROGRESS(false,"Some votes has been processed, but not enough to conclude result."),
    ENDED_REJECTED(true,"Enough votes has been processed to end the voting. The the motion was rejected."),
    ENDED_ACCEPTED(true,"Enough votes has been processed to end the voting. The the motion was accepted.");

    @Getter
    private boolean finalStatus;
    @Getter
    private String message;


    VotingStatus(boolean finalStatus, String message) {
        this.finalStatus = finalStatus;
        this.message = message;
    }
}
