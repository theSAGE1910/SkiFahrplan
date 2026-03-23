package edu.kit.kastel;

/**
 * Represents the distinct skiing proficiency tiers of a skier.
 * The skill level directly influences the mathematical calculation
 * of how fast a skier can traverse different pistes.
 *
 * @author uxuwg
 * @version 0.1
 */
public enum SkillLevel {

    /**
     * A novice skier who traverses slopes at a significantly slower pace.
     */
    BEGINNER,

    /**
     * A skier with average skills, representing the baseline moderate pace.
     */
    INTERMEDIATE,

    /**
     * A highly skilled skier who traverses slopes at an accelerated pace.
     */
    EXPERT
}