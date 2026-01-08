package com.gempukku.swccgo.ai;

// Advanced AI: same heuristic engine as Beginner, with more aggressive weights.
public class AdvancedAi extends HeuristicAiBase {
    private static final KeywordWeight[] ACTION_WEIGHTS = new KeywordWeight[] {
            new KeywordWeight("force drain", 160),
            new KeywordWeight("initiate battle", 150),
            new KeywordWeight("battle", 100),
            new KeywordWeight("weapon", 50),
            new KeywordWeight("fire", 45),
            new KeywordWeight("deploy", 90),
            new KeywordWeight("play", 40),
            new KeywordWeight("move", 50),
            new KeywordWeight("activate", 70),
            new KeywordWeight("retrieve", 40),
            new KeywordWeight("draw", 35),
            new KeywordWeight("steal", 35),
            new KeywordWeight("capture", 35),
            new KeywordWeight("download", 45),
            new KeywordWeight("search", 30),
            new KeywordWeight("react", 30),
            new KeywordWeight("take into hand", 35)
    };

    private static final KeywordWeight[] ACTION_PENALTIES = new KeywordWeight[] {
            new KeywordWeight("pass", -160),
            new KeywordWeight("forfeit", -100),
            new KeywordWeight("lose", -60),
            new KeywordWeight("place in lost pile", -90),
            new KeywordWeight("place in used pile", -40),
            new KeywordWeight("return to hand", -25),
            new KeywordWeight("sacrifice", -120),
            new KeywordWeight("revert", -60)
    };

    private static final KeywordWeight[] CHOICE_WEIGHTS = new KeywordWeight[] {
            new KeywordWeight("draw", 60),
            new KeywordWeight("retrieve", 45),
            new KeywordWeight("deploy", 40),
            new KeywordWeight("battle destiny", 50),
            new KeywordWeight("weapon destiny", 50),
            new KeywordWeight("activate", 40),
            new KeywordWeight("force drain", 60),
            new KeywordWeight("initiate", 40),
            new KeywordWeight("capture", 30),
            new KeywordWeight("steal", 30),
            new KeywordWeight("download", 30),
            new KeywordWeight("use", 10),
            new KeywordWeight("yes", 10)
    };

    private static final KeywordWeight[] CHOICE_PENALTIES = new KeywordWeight[] {
            new KeywordWeight("lose", -55),
            new KeywordWeight("forfeit", -70),
            new KeywordWeight("lost pile", -60),
            new KeywordWeight("used pile", -35),
            new KeywordWeight("return to hand", -25),
            new KeywordWeight("neither", -30),
            new KeywordWeight("cancel", -30),
            new KeywordWeight("pass", -40)
    };

    private static final String[] CARD_HINTS = new String[] {
            "pilot", "weapon", "character", "starship", "vehicle", "droid", "alien",
            "jedi", "sith", "effect", "interrupt", "location", "site", "system",
            "ability", "destiny", "force"
    };

    @Override
    protected int getPassPenalty() {
        return 240;
    }

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
