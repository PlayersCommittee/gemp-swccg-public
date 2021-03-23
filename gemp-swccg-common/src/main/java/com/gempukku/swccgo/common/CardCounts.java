package com.gempukku.swccgo.common;

/**
 * This interface defines constants for the number of cards in each set.
 */
public interface CardCounts {

    int[] FULL_SETS_CARD_COUNTS = {324, 162, 162, 180, 180, 180, 324, 180, 182, 54, 99, 189, 100, 129};

    int[] PREMIUM_SETS_CARD_COUNTS = {6, 11, 2, 7, 6, 18, 6, 6, 12, 12, 6, 20};

    int[] VIRTUAL_SETS_CARD_COUNTS = {146, 42, 15, 36, 58, 23, 14, 29, 59, 56, 47, 61, 6, 59, 22, 1};

    int[] VIRTUAL_PREMIUM_SETS_CARD_COUNTS = {8};

    int[] DREAM_CARD_SETS_CARD_COUNTS = {0};

    int[] PLAYTESTING_SETS_CARD_COUNTS = {100};

    int[] LEGACY_SETS_CARD_COUNTS = {999};
}
