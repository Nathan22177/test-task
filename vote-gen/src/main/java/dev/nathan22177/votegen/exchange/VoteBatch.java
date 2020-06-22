package dev.nathan22177.votegen.exchange;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class VoteBatch {
    int votingId;
    int roundNumber;
    List<Vote> votes;
}
