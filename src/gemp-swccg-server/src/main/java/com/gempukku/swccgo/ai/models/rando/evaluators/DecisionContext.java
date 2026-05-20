package com.gempukku.swccgo.ai.models.rando.evaluators;

import com.gempukku.swccgo.ai.models.rando.strategy.DeployPhasePlanner;
import com.gempukku.swccgo.ai.models.rando.strategy.ObjectiveHandler;
import com.gempukku.swccgo.ai.models.rando.strategy.ShieldStrategy;
import com.gempukku.swccgo.ai.models.rando.strategy.StrategyController;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;

import java.util.*;

/**
 * Context information for evaluating a decision.
 *
 * Contains all information an evaluator needs to score actions:
 * - Current game state (board, resources, power)
 * - Available actions
 * - Decision type and text
 * - Phase information
 */
public class DecisionContext {
    // Game state
    private final GameState gameState;
    private final String playerId;
    private SwccgGame game;  // Full game reference for advanced analysis
    private Side side;  // Our side (DARK or LIGHT)

    // Decision info
    private final String decisionType;  // CARD_ACTION_CHOICE, CARD_SELECTION, INTEGER, etc.
    private final String decisionText;  // Human-readable prompt
    private final String decisionId;

    // Phase info
    private final Phase phase;
    private final int turnNumber;
    private final boolean isMyTurn;

    // Available actions (for ACTION_CHOICE decisions)
    private List<String> actionIds = new ArrayList<>();
    private List<String> actionTexts = new ArrayList<>();

    // For CARD_SELECTION decisions
    private List<String> cardIds = new ArrayList<>();
    private List<String> blueprints = new ArrayList<>();
    private List<Boolean> selectable = new ArrayList<>();
    private List<String> testingTexts = new ArrayList<>();  // Card titles from GEMP

    // Parameters from decision XML
    private boolean noPass = true;  // Can we pass/cancel?
    private int min = 0;  // Minimum selection required
    private int max = 1;  // Maximum selection allowed

    // Additional context
    private Map<String, Object> extra = new HashMap<>();

    // Blocked responses (for loop prevention)
    private Set<String> blockedResponses = new HashSet<>();

    // Strategy components (optional, set by AI)
    private StrategyController strategyController;
    private ObjectiveHandler objectiveHandler;
    private ShieldStrategy shieldStrategy;
    private DeployPhasePlanner deployPhasePlanner;

    public DecisionContext(GameState gameState, String playerId, String decisionType,
                          String decisionText, String decisionId, Phase phase) {
        this.gameState = gameState;
        this.playerId = playerId;
        this.decisionType = decisionType;
        this.decisionText = decisionText;
        this.decisionId = decisionId;
        this.phase = phase;
        this.turnNumber = gameState != null ? gameState.getPlayersLatestTurnNumber(playerId) : 1;
        this.isMyTurn = gameState != null && playerId.equals(gameState.getCurrentPlayerId());
    }

    // Getters
    public GameState getGameState() {
        return gameState;
    }

    public String getPlayerId() {
        return playerId;
    }

    public SwccgGame getGame() {
        return game;
    }

    public void setGame(SwccgGame game) {
        this.game = game;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public String getOpponentId() {
        if (gameState == null || playerId == null) return null;
        return gameState.getOpponent(playerId);
    }

    public String getDecisionType() {
        return decisionType;
    }

    public String getDecisionText() {
        return decisionText;
    }

    public String getDecisionId() {
        return decisionId;
    }

    public Phase getPhase() {
        return phase;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }

    public List<String> getActionIds() {
        return actionIds;
    }

    public void setActionIds(List<String> actionIds) {
        this.actionIds = actionIds;
    }

    public List<String> getActionTexts() {
        return actionTexts;
    }

    public void setActionTexts(List<String> actionTexts) {
        this.actionTexts = actionTexts;
    }

    public List<String> getCardIds() {
        return cardIds;
    }

    public void setCardIds(List<String> cardIds) {
        this.cardIds = cardIds;
    }

    public List<String> getBlueprints() {
        return blueprints;
    }

    public void setBlueprints(List<String> blueprints) {
        this.blueprints = blueprints;
    }

    public List<Boolean> getSelectable() {
        return selectable;
    }

    public void setSelectable(List<Boolean> selectable) {
        this.selectable = selectable;
    }

    public List<String> getTestingTexts() {
        return testingTexts;
    }

    public void setTestingTexts(List<String> testingTexts) {
        this.testingTexts = testingTexts;
    }

    /**
     * Get the card title for a given index from testingTexts.
     * This is the card name GEMP provides, which is more reliable than parsing action text.
     */
    public String getCardTitleAt(int index) {
        if (testingTexts == null || index < 0 || index >= testingTexts.size()) {
            return null;
        }
        String title = testingTexts.get(index);
        // testingText may have format like "•Card Name" - strip leading •
        if (title != null && title.startsWith("•")) {
            title = title.substring(1);
        }
        // May also have "(V)" virtual marker
        if (title != null && title.contains("(V)")) {
            title = title.replace("(V)", "").trim();
        }
        return title;
    }

    public boolean isNoPass() {
        return noPass;
    }

    public void setNoPass(boolean noPass) {
        this.noPass = noPass;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(String key, Object value) {
        this.extra.put(key, value);
    }

    public Object getExtra(String key) {
        return extra.get(key);
    }

    public Set<String> getBlockedResponses() {
        return blockedResponses;
    }

    public void addBlockedResponse(String response) {
        this.blockedResponses.add(response);
    }

    public void setBlockedResponses(Set<String> blocked) {
        this.blockedResponses.clear();
        if (blocked != null) {
            this.blockedResponses.addAll(blocked);
        }
    }

    // Convenience methods for game state queries
    public int getForceAvailable() {
        // Force pile size represents available force
        if (gameState == null) return 0;
        return gameState.getForcePileSize(playerId);
    }

    public int getReserveDeckSize() {
        if (gameState == null) return 0;
        return gameState.getReserveDeckSize(playerId);
    }

    public int getUsedPileSize() {
        if (gameState == null) return 0;
        return gameState.getUsedPile(playerId).size();
    }

    public int getForcePileSize() {
        if (gameState == null) return 0;
        return gameState.getForcePileSize(playerId);
    }

    public int getLifeForce() {
        return getReserveDeckSize() + getUsedPileSize() + getForcePileSize();
    }

    public List<PhysicalCard> getHand() {
        if (gameState == null) return Collections.emptyList();
        return gameState.getHand(playerId);
    }

    public int getHandSize() {
        if (gameState == null) return 0;
        return gameState.getHand(playerId).size();
    }

    // Strategy component getters and setters
    public StrategyController getStrategyController() {
        return strategyController;
    }

    public void setStrategyController(StrategyController strategyController) {
        this.strategyController = strategyController;
    }

    public ObjectiveHandler getObjectiveHandler() {
        return objectiveHandler;
    }

    public void setObjectiveHandler(ObjectiveHandler objectiveHandler) {
        this.objectiveHandler = objectiveHandler;
    }

    public ShieldStrategy getShieldStrategy() {
        return shieldStrategy;
    }

    public void setShieldStrategy(ShieldStrategy shieldStrategy) {
        this.shieldStrategy = shieldStrategy;
    }

    public DeployPhasePlanner getDeployPhasePlanner() {
        return deployPhasePlanner;
    }

    public void setDeployPhasePlanner(DeployPhasePlanner deployPhasePlanner) {
        this.deployPhasePlanner = deployPhasePlanner;
    }
}
