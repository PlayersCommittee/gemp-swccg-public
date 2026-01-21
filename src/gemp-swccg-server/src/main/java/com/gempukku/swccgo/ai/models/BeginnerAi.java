package com.gempukku.swccgo.ai.models;

// Beginner AI with light heuristics (keyword/phase scoring).
public class BeginnerAi extends HeuristicAiBase {
    private static final KeywordWeight[] ACTION_WEIGHTS = new KeywordWeight[] {
            new KeywordWeight("force drain", 120),
            new KeywordWeight("initiate battle", 110),
            new KeywordWeight("battle", 70),
            new KeywordWeight("weapon", 40),
            new KeywordWeight("fire", 35),
            new KeywordWeight("deploy", 60),
            new KeywordWeight("play", 35),
            new KeywordWeight("move", 35),
            new KeywordWeight("activate", 50),
            new KeywordWeight("retrieve", 30),
            new KeywordWeight("draw", 25),
            new KeywordWeight("steal", 25),
            new KeywordWeight("capture", 25),
            new KeywordWeight("react", 20),
            new KeywordWeight("take into hand", 30)
    };

    private static final KeywordWeight[] ACTION_PENALTIES = new KeywordWeight[] {
            new KeywordWeight("pass", -120),
            new KeywordWeight("forfeit", -80),
            new KeywordWeight("lose", -45),
            new KeywordWeight("place in lost pile", -70),
            new KeywordWeight("place in used pile", -35),
            new KeywordWeight("return to hand", -30),
            new KeywordWeight("sacrifice", -80),
            new KeywordWeight("revert", -40)
    };

    private static final KeywordWeight[] CHOICE_WEIGHTS = new KeywordWeight[] {
            new KeywordWeight("draw", 40),
            new KeywordWeight("retrieve", 35),
            new KeywordWeight("deploy", 30),
            new KeywordWeight("battle destiny", 35),
            new KeywordWeight("weapon destiny", 35),
            new KeywordWeight("activate", 30),
            new KeywordWeight("force drain", 40),
            new KeywordWeight("initiate", 30),
            new KeywordWeight("capture", 20),
            new KeywordWeight("steal", 20),
            new KeywordWeight("use", 5),
            new KeywordWeight("yes", 5)
    };

    private static final KeywordWeight[] CHOICE_PENALTIES = new KeywordWeight[] {
            new KeywordWeight("lose", -45),
            new KeywordWeight("forfeit", -60),
            new KeywordWeight("lost pile", -50),
            new KeywordWeight("used pile", -30),
            new KeywordWeight("return to hand", -25),
            new KeywordWeight("neither", -25),
            new KeywordWeight("cancel", -20),
            new KeywordWeight("pass", -30)
    };

    private static final String[] CARD_HINTS = new String[] {
            "pilot", "weapon", "character", "starship", "vehicle", "droid", "alien",
            "jedi", "sith", "effect", "interrupt", "location", "site", "system"
    };

    @Override
    protected KeywordWeight[] getActionWeights() {
        return ACTION_WEIGHTS;
    }

    @Override
    protected KeywordWeight[] getActionPenalties() {
        return ACTION_PENALTIES;
    }

    @Override
    protected KeywordWeight[] getChoiceWeights() {
        return CHOICE_WEIGHTS;
    }

    @Override
    protected KeywordWeight[] getChoicePenalties() {
        return CHOICE_PENALTIES;
    }

    @Override
    protected String[] getCardHints() {
        return CARD_HINTS;
    }
}