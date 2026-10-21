/*
 * Scanner Core - A Modular Framework for Probe Definition, Execution, and Result Analysis.
 *
 * Copyright 2017-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.scanner.core.report.rating;

/**
 * Categorizes the impact of a property result on the overall rating. Each category defines the
 * influence that is added to the score if the result is observed. There are no strict rules on when
 * to use which value. When applying the modifiers, implementers should reflect on the impact but
 * also on the prevalence of the issue. Penalties starting from {@link #PENALTY_HIGH} additionally
 * cap the score.
 */
public enum RatingImpact {
    /** A significant improvement equivalent to supporting a new generation of a protocol. */
    BONUS_EPOCHAL(500),
    BONUS_VERY_HIGH(300),
    BONUS_HIGH(200),
    BONUS_MEDIUM(100),
    /** Following a mere best practice. */
    BONUS_LOW(50),
    // ----------------------------------
    /** Missing a mere best practice. */
    PENALTY_LOW(-50),
    PENALTY_MEDIUM(-100),
    PENALTY_HIGH(-200, 1000),
    PENALTY_VERY_HIGH(-500, 750),
    /** A significant issue that would also apply in common peer configurations. */
    PENALTY_EPOCHAL(-800, 500);

    private final int influence;

    private final Integer scoreCap;

    RatingImpact(int influence) {
        this(influence, null);
    }

    RatingImpact(int influence, Integer scoreCap) {
        this.influence = influence;
        this.scoreCap = scoreCap;
    }

    /**
     * Returns the influence on the score.
     *
     * @return the influence that is added to the score
     */
    public int getInfluence() {
        return influence;
    }

    /**
     * Returns the maximum score that can be reached if the result is observed.
     *
     * @return the score cap, or null if the score is not capped
     */
    public Integer getScoreCap() {
        return scoreCap;
    }

    /**
     * Returns the impact of the same level with the opposite sign, e.g., {@link #PENALTY_MEDIUM}
     * for {@link #BONUS_MEDIUM}.
     *
     * @return the opposite impact
     */
    public RatingImpact getOpposite() {
        return switch (this) {
            case BONUS_EPOCHAL -> PENALTY_EPOCHAL;
            case BONUS_VERY_HIGH -> PENALTY_VERY_HIGH;
            case BONUS_HIGH -> PENALTY_HIGH;
            case BONUS_MEDIUM -> PENALTY_MEDIUM;
            case BONUS_LOW -> PENALTY_LOW;
            case PENALTY_LOW -> BONUS_LOW;
            case PENALTY_MEDIUM -> BONUS_MEDIUM;
            case PENALTY_HIGH -> BONUS_HIGH;
            case PENALTY_VERY_HIGH -> BONUS_VERY_HIGH;
            case PENALTY_EPOCHAL -> BONUS_EPOCHAL;
        };
    }
}
