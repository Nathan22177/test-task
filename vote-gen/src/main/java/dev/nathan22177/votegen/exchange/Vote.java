package dev.nathan22177.votegen.exchange;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Vote {
    private int participantId;
    private boolean inFavor;
    private int roundNumber;
    private int votingId;

    /**
     * Makes it easier to read logs while testing.
     * */
    @JsonIgnore
    private String participantName;
}
