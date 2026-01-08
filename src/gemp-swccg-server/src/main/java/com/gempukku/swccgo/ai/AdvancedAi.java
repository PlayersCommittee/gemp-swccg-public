package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardType;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

// Advanced AI: heuristic engine with stronger weights and hand/board awareness.
public class AdvancedAi extends HeuristicAiBase {
    private static final int TITLE_MATCH_BONUS = 80;
    private static final int TYPE_MATCH_BONUS = 20;
    private static final int MAX_TYPE_MATCHES = 3;

    private AiContext _context;
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
            new KeywordWeight("cancel", 30),
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
    public String decide(String playerId, AwaitingDecision decision, GameState gameState) {
        _context = AiContext.build(playerId, gameState);
        try {
            return super.decide(playerId, decision, gameState);
        } finally {
            _context = null;
        }
    }

    @Override
    protected int getPassPenalty() {
        return 320;
    }

    @Override
    protected boolean shouldSkipOptionalResponses() {
        return false;
    }

    @Override
    protected int scoreActionContext(String playerId, GameState gameState, String decisionText, String actionText, Phase phase, Map<String, String[]> params) {
        AiContext context = _context;
        if (context == null || actionText == null || actionText.isEmpty()) {
            return 0;
        }

        int score = 0;
        if (context.matchesHandTitle(actionText)) {
            score += TITLE_MATCH_BONUS;
        }

        if (actionText.contains("deploy") || actionText.contains("play")) {
            int matches = context.countHandKeywordMatches(actionText);
            if (matches > 0) {
                score += Math.min(matches, MAX_TYPE_MATCHES) * TYPE_MATCH_BONUS;
            }
        }

        if (context.behindOnBoard()) {
            if (actionText.contains("initiate battle")) {
                score -= 40;
            }
            if (actionText.contains("deploy") || actionText.contains("activate") || actionText.contains("draw") || actionText.contains("retrieve")) {
                score += 25;
            }
        } else if (context.aheadOnBoard()) {
            if (actionText.contains("initiate battle")) {
                score += 30;
            }
            if (actionText.contains("force drain")) {
                score += 20;
            }
        }

        if (context.behindOnLifeForce()) {
            if (actionText.contains("force drain")) {
                score += 25;
            }
            if (actionText.contains("battle")) {
                score += 15;
            }
        }

        if (context.opponentObjectiveTitle != null
                && actionText.contains("cancel")
                && actionText.contains("objective")) {
            score += 30;
        }

        return score;
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

    private static final class AiContext {
        private final int selfLifeForce;
        private final int opponentLifeForce;
        private final int selfUnitsInPlay;
        private final int opponentUnitsInPlay;
        private final Set<String> handTitles;
        private final Set<String> handKeywords;
        private final String opponentObjectiveTitle;

        private AiContext(int selfLifeForce, int opponentLifeForce, int selfUnitsInPlay, int opponentUnitsInPlay,
                          Set<String> handTitles, Set<String> handKeywords, String opponentObjectiveTitle) {
            this.selfLifeForce = selfLifeForce;
            this.opponentLifeForce = opponentLifeForce;
            this.selfUnitsInPlay = selfUnitsInPlay;
            this.opponentUnitsInPlay = opponentUnitsInPlay;
            this.handTitles = handTitles;
            this.handKeywords = handKeywords;
            this.opponentObjectiveTitle = opponentObjectiveTitle;
        }

        private static AiContext build(String playerId, GameState gameState) {
            if (playerId == null || gameState == null) {
                return null;
            }

            String opponent = gameState.getOpponent(playerId);
            int selfLifeForce = safeLifeForce(gameState, playerId);
            int opponentLifeForce = opponent != null ? safeLifeForce(gameState, opponent) : selfLifeForce;

            int selfUnits = countUnitsInPlay(gameState, playerId);
            int opponentUnits = opponent != null ? countUnitsInPlay(gameState, opponent) : selfUnits;

            Set<String> handTitles = new HashSet<String>();
            Set<String> handKeywords = new HashSet<String>();
            buildHandSignals(gameState, playerId, handTitles, handKeywords);

            String opponentObjectiveTitle = null;
            if (opponent != null) {
                PhysicalCard objective = gameState.getObjectivePlayed(opponent);
                if (objective != null && objective.getTitle() != null) {
                    opponentObjectiveTitle = objective.getTitle().toLowerCase(Locale.ROOT);
                }
            }

            return new AiContext(selfLifeForce, opponentLifeForce, selfUnits, opponentUnits, handTitles, handKeywords, opponentObjectiveTitle);
        }

        private static int safeLifeForce(GameState gameState, String playerId) {
            try {
                return gameState.getPlayerLifeForce(playerId);
            } catch (RuntimeException e) {
                return 0;
            }
        }

        private static void buildHandSignals(GameState gameState, String playerId, Set<String> handTitles, Set<String> handKeywords) {
            Iterable<PhysicalCard> hand;
            try {
                hand = gameState.getHand(playerId);
            } catch (RuntimeException e) {
                return;
            }
            if (hand == null) {
                return;
            }
            for (PhysicalCard card : hand) {
                if (card == null) {
                    continue;
                }
                SwccgCardBlueprint blueprint = card.getBlueprint();
                if (blueprint != null) {
                    addCategoryKeyword(handKeywords, blueprint.getCardCategory());
                    addTypeKeywords(handKeywords, blueprint.getCardTypes());
                }
                for (String title : card.getTitles()) {
                    if (title == null) {
                        continue;
                    }
                    String normalized = title.toLowerCase(Locale.ROOT).trim();
                    if (normalized.length() >= 4) {
                        handTitles.add(normalized);
                    }
                }
            }
        }

        private static void addCategoryKeyword(Set<String> handKeywords, CardCategory category) {
            if (category == null) {
                return;
            }
            handKeywords.add(category.getHumanReadable().toLowerCase(Locale.ROOT));
        }

        private static void addTypeKeywords(Set<String> handKeywords, Set<CardType> cardTypes) {
            if (cardTypes == null) {
                return;
            }
            for (CardType type : cardTypes) {
                if (type == null) {
                    continue;
                }
                switch (type) {
                    case STARSHIP:
                    case VEHICLE:
                    case WEAPON:
                    case DEVICE:
                    case DROID:
                    case ALIEN:
                    case REBEL:
                    case IMPERIAL:
                    case EFFECT:
                    case INTERRUPT:
                    case LOCATION:
                    case OBJECTIVE:
                    case CREATURE:
                        handKeywords.add(type.getHumanReadable().toLowerCase(Locale.ROOT));
                        break;
                    case SITH:
                        handKeywords.add("sith");
                        break;
                    case JEDI_MASTER:
                    case DARK_JEDI_MASTER:
                        handKeywords.add("jedi");
                        break;
                    default:
                        break;
                }
            }
        }

        private static int countUnitsInPlay(GameState gameState, String playerId) {
            if (playerId == null || gameState == null) {
                return 0;
            }
            int count = 0;
            for (PhysicalCard card : gameState.getAllPermanentCards()) {
                if (card == null) {
                    continue;
                }
                Zone zone = card.getZone();
                if (zone == null || !zone.isInPlay()) {
                    continue;
                }
                if (!playerId.equals(card.getOwner())) {
                    continue;
                }
                SwccgCardBlueprint blueprint = card.getBlueprint();
                if (blueprint == null) {
                    continue;
                }
                CardCategory category = blueprint.getCardCategory();
                if (category == CardCategory.CHARACTER || category == CardCategory.STARSHIP || category == CardCategory.VEHICLE) {
                    count++;
                }
            }
            return count;
        }

        private boolean matchesHandTitle(String actionText) {
            if (handTitles.isEmpty()) {
                return false;
            }
            for (String title : handTitles) {
                if (actionText.contains(title)) {
                    return true;
                }
            }
            return false;
        }

        private int countHandKeywordMatches(String actionText) {
            if (handKeywords.isEmpty()) {
                return 0;
            }
            int matches = 0;
            for (String keyword : handKeywords) {
                if (actionText.contains(keyword)) {
                    matches++;
                }
            }
            return matches;
        }

        private boolean behindOnBoard() {
            return selfUnitsInPlay + 1 < opponentUnitsInPlay;
        }

        private boolean aheadOnBoard() {
            return selfUnitsInPlay > opponentUnitsInPlay + 1;
        }

        private boolean behindOnLifeForce() {
            return selfLifeForce + 5 < opponentLifeForce;
        }
    }
}
