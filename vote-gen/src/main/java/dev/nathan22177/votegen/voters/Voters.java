package dev.nathan22177.votegen.voters;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Getter
public enum Voters {
    /**
    * Casts two votes in favor every round
    * */
    CASPER(0, Collections.singletonList(new Boolean[]{Boolean.TRUE, Boolean.TRUE})),

    /**
     * Casts one vote per round. Changes the favor every vote .
     * */
    BALTHAZAR(1, List.of(new Boolean[]{Boolean.TRUE}, new Boolean[]{Boolean.FALSE})),

    /**
     * Casts once every three rounds. Each time changes the favor.
     * */
    MELCHIOR(2, List.of(new Boolean[]{}, new Boolean[]{}, new Boolean[]{Boolean.TRUE},
                     new Boolean[]{}, new Boolean[]{}, new Boolean[]{Boolean.FALSE}));

    private int id;
    private List<Boolean[]> pattern;

    public Boolean[] getNextVotes(int totalRoundNumber) {

        return getPattern().get(totalRoundNumber % getPattern().size());
    }
}
