package dev.nathan22177.votegen.exchange;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VotingResult {
    private int id;
    private boolean inFavor;
    private int yays;
    private int nays;
}
