package com.gempukku.swccgo.ai.models.chosenone.evaluators;

import com.gempukku.swccgo.ai.common.AiCardHelper;
import com.gempukku.swccgo.ai.common.AiPriorityCards;
import com.gempukku.swccgo.ai.models.chosenone.strategy.DeployPhasePlanner;
import com.gempukku.swccgo.ai.models.chosenone.strategy.DeploymentInstruction;
import com.gempukku.swccgo.ai.models.chosenone.strategy.DeploymentPlan;
import com.gempukku.swccgo.ai.models.chosenone.strategy.ShieldStrategy;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.state.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Evaluates CARD_SELECTION and ARBITRARY_CARDS decisions.
 *
 * These are decisions where the player must select one or more cards
 * from a list (e.g., choosing where to deploy, which card to forfeit,
 * targeting for weapons, etc.).
 *
 * Decision types handled:
 * - "choose card to set sabacc value" -> Random selection
 * - "choose where to deploy" -> Pick best location
 * - "choose force to lose" -> Pick best card to lose
 * - "choose a card from battle to forfeit" -> Pick lowest forfeit value
 * - "choose a pilot" -> Pick best pilot
 * - "choose card to cancel" -> Cancel opponent's cards, not ours
 * - "choose target" -> Weapon/ability targeting
 *
 * Ported from Python card_selection_evaluator.py
 */
public class CardSelectionEvaluator extends ActionEvaluator {

    // Score constants
    private static final float VERY_GOOD_DELTA = 150.0f;
    private static final float GOOD_DELTA = 10.0f;
    private static final float BAD_DELTA = -10.0f;
    private static final float VERY_BAD_DELTA = -150.0f;
    private static final SwccgCardBlueprintLibrary FALLBACK_LIBRARY = new SwccgCardBlueprintLibrary();

    private final Random random = new Random();

    public CardSelectionEvaluator() {
        super("CardSelection");
    }

    /**
     * Look up card name from blueprintId using the blueprint library.
     * This is the CORRECT way to get card info - proves the bot can actually look up cards.
     */
    private String getCardNameFromBlueprint(DecisionContext context, String blueprintId) {
        if (blueprintId == null || blueprintId.isEmpty() || "inPlay".equals(blueprintId)) {
            return null;
        }

        SwccgCardBlueprintLibrary library = FALLBACK_LIBRARY;
        if (library == null) {
            logger.warn("⚠️ Cannot look up blueprint '{}' - library is null", blueprintId);
            return null;
        }

        try {
            SwccgCardBlueprint blueprint = library.getSwccgoCardBlueprint(blueprintId);
            if (blueprint != null) {
                String title = blueprint.getTitle();
                logger.info("✅ BLUEPRINT LOOKUP SUCCESS: '{}' -> '{}'", blueprintId, title);
                return title;
            } else {
                logger.warn("⚠️ Blueprint '{}' not found in library", blueprintId);
            }
        } catch (Exception e) {
            logger.warn("⚠️ Error looking up blueprint '{}': {}", blueprintId, e.getMessage());
        }
        return null;
    }

    /**
     * Get the blueprint object from blueprintId for accessing card properties.
     */
    private SwccgCardBlueprint getBlueprintFromId(DecisionContext context, String blueprintId) {
        if (blueprintId == null || blueprintId.isEmpty() || "inPlay".equals(blueprintId)) {
            return null;
        }

        SwccgCardBlueprintLibrary library = FALLBACK_LIBRARY;
        if (library == null) return null;

        try {
            return library.getSwccgoCardBlueprint(blueprintId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean canEvaluate(DecisionContext context) {
        String decisionType = context.getDecisionType();
        return "CARD_SELECTION".equals(decisionType) || "ARBITRARY_CARDS".equals(decisionType);
    }

    @Override
    public List<EvaluatedAction> evaluate(DecisionContext context) {
        String text = context.getDecisionText();
        String textLower = text != null ? text.toLowerCase(Locale.ROOT) : "";

        // ========== CRITICAL DEBUG LOGGING ==========
        logger.warn("🚀🚀🚀 [CardSelectionEvaluator.evaluate] ENTRY POINT - JAR VERSION 2026-02-23-V21 🚀🚀🚀");
        logger.warn("🔍 Decision type: {}", context.getDecisionType());
        logger.warn("🔍 Decision text (FULL): {}", text);

        // === V21: BAN CERTAIN EFFECTS AS STARTING EFFECTS ===
        // These should never be deployed via starting interrupt (turn 0)
        // They CAN still be deployed from hand during turn 1+ deploy phase
        if (context.getTurnNumber() <= 0) {
            java.util.Set<String> BANNED_STARTING_EFFECTS = new java.util.HashSet<>(java.util.Arrays.asList(
                "no escape", "no escape (v)",
                "coarse and rough and irritating",
                // V67p (Steve): Tentacle is not a useful starting interrupt — it's a
                // counter to Dianoga/garbage compactor scenarios, not a turn-0 setup card.
                // Picking it as starting effect wastes the turn-0 slot.
                "tentacle"
            ));

            // Check if any card in this selection is banned
            GameState startGameState = context.getGameState();
            List<String> startCardIds = context.getCardIds();
            boolean hasBannedCard = false;
            java.util.Map<String, Boolean> cardBanStatus = new java.util.HashMap<>();

            if (startGameState != null && startCardIds != null) {
                for (String cid : startCardIds) {
                    try {
                        PhysicalCard pc = startGameState.findCardById(Integer.parseInt(cid));
                        if (pc != null) {
                            String cardTitle = pc.getTitle();
                            if (cardTitle != null) {
                                String titleLower = cardTitle.toLowerCase(java.util.Locale.ROOT);
                                boolean banned = false;
                                for (String b : BANNED_STARTING_EFFECTS) {
                                    if (titleLower.contains(b)) {
                                        banned = true;
                                        break;
                                    }
                                }
                                cardBanStatus.put(cid, banned);
                                if (banned) {
                                    hasBannedCard = true;
                                    logger.warn("V21 STARTING BAN: '{}' is banned as starting effect", cardTitle);
                                }
                            }
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }

            if (hasBannedCard) {
                List<EvaluatedAction> startBanActions = new ArrayList<>();
                for (String cid : startCardIds) {
                    Boolean banned = cardBanStatus.get(cid);
                    boolean isBanned = banned != null && banned;
                    EvaluatedAction action = new EvaluatedAction(
                        cid,
                        ActionType.UNKNOWN,
                        isBanned ? -500.0f : 100.0f,
                        isBanned ? "BANNED as starting effect" : "OK as starting effect"
                    );
                    startBanActions.add(action);
                }
                logger.warn("V21 STARTING BAN: Returning {} scored actions", startBanActions.size());
                return startBanActions;
            }
        }

        List<String> cardIds = context.getCardIds();
        List<String> blueprints = context.getBlueprints();
        List<Boolean> selectable = context.getSelectable();

        logger.warn("🔍 cardIds: {} items", cardIds != null ? cardIds.size() : "null");
        logger.warn("🔍 blueprints: {} items", blueprints != null ? blueprints.size() : "null");
        logger.warn("🔍 selectable array: {} items -> {}",
            selectable != null ? selectable.size() : "null",
            selectable != null && selectable.size() <= 10 ? selectable : (selectable != null ? selectable.subList(0, Math.min(10, selectable.size())) + "..." : "null"));

        // Log min/max for selection
        int min = context.getMin();
        int max = context.getMax();
        boolean noPass = context.isNoPass();
        logger.warn("🔍 Selection min={}, max={}, noPass={}", min, max, noPass);

        // Log first few card IDs and blueprints for debugging
        if (cardIds != null && !cardIds.isEmpty()) {
            logger.warn("🔍 First 5 cardIds: {}", cardIds.subList(0, Math.min(5, cardIds.size())));
        }
        if (blueprints != null && !blueprints.isEmpty()) {
            logger.warn("🔍 First 5 blueprints: {}", blueprints.subList(0, Math.min(5, blueprints.size())));
        }
        // Log testingTexts (CARD TITLES from GEMP - most reliable!)
        List<String> testingTexts = context.getTestingTexts();
        if (testingTexts != null && !testingTexts.isEmpty()) {
            logger.warn("🔍 testingTexts (CARD TITLES!): {} items", testingTexts.size());
            logger.warn("🔍 First 5 testingTexts: {}", testingTexts.subList(0, Math.min(5, testingTexts.size())));
        } else {
            logger.warn("🔍 testingTexts: null or empty - card titles unavailable!");
        }
        // ========== END CRITICAL DEBUG LOGGING ==========

        logger.info("[CardSelectionEvaluator] Evaluating: {}",
            text != null && text.length() > 60 ? text.substring(0, 60) + "..." : text);

        // Log selectable info - CRITICAL for debugging GEMP rejection issues
        if (selectable != null && !selectable.isEmpty()) {
            int selectableCount = 0;
            for (Boolean s : selectable) {
                if (s != null && s) selectableCount++;
            }
            logger.info("[CardSelectionEvaluator] {} cards total, {} selectable",
                       cardIds != null ? cardIds.size() : 0, selectableCount);

            // If NOTHING is selectable, this is likely a "verify" decision or a bug
            if (selectableCount == 0 && cardIds != null && !cardIds.isEmpty()) {
                logger.warn("⚠️⚠️⚠️ ALL {} CARDS ARE NON-SELECTABLE! This may be a 'verify' decision or GEMP bug.", cardIds.size());
                logger.warn("    Decision contains 'verify': {}", textLower.contains("verify"));
                logger.warn("    Decision contains 'unsuccessful': {}", textLower.contains("unsuccessful"));

                // === V24.7: OPPONENT DECK INTEL — SCAN DESTINY VALUES ===
                // When verifying opponent's deck, scan all visible cards for destiny values.
                // This gives us real data for BattlePredictor instead of random 0-6 guesses.
                GameState peekGameState = context.getGameState();
                com.gempukku.swccgo.ai.models.chosenone.strategy.OpponentDeckTracker tracker =
                    context.getOpponentDeckTracker();
                if (peekGameState != null && tracker != null) {
                    try {
                        float[] destinyValues = new float[cardIds.size()];
                        int idx = 0;
                        for (String peekCardId : cardIds) {
                            try {
                                PhysicalCard peekCard = peekGameState.findCardById(Integer.parseInt(peekCardId));
                                if (peekCard != null && peekCard.getBlueprint() != null) {
                                    Float destiny = peekCard.getBlueprint().getDestiny();
                                    destinyValues[idx] = (destiny != null) ? destiny : -1.0f;
                                    if (destiny != null) {
                                        logger.info("V24.7 PEEK: {} — destiny {}", peekCard.getTitle(), destiny);
                                    }
                                } else {
                                    destinyValues[idx] = -1.0f;
                                }
                            } catch (NumberFormatException nfe) {
                                destinyValues[idx] = -1.0f;
                            }
                            idx++;
                        }
                        tracker.recordPeek(destinyValues, cardIds.size());
                        logger.warn("V24.7 OPPONENT INTEL: Scanned {} cards — average destiny: {}",
                            cardIds.size(), tracker.getOpponentDestinyAverage());
                    } catch (Exception e) {
                        logger.debug("V24.7: Error scanning opponent deck: {}", e.getMessage());
                    }
                }
            }
        }

        // For reserve deck selections, we may have blueprints but no cardIds
        if ((cardIds == null || cardIds.isEmpty()) &&
            (blueprints == null || blueprints.isEmpty())) {
            logger.warn("[CardSelectionEvaluator] No card IDs or blueprints in {} decision", context.getDecisionType());
            return new ArrayList<>();
        }

        // If we have blueprints but no cardIds, handle reserve deck selection
        if ((cardIds == null || cardIds.isEmpty()) && blueprints != null && !blueprints.isEmpty()) {
            logger.info("[CardSelectionEvaluator] Reserve deck selection with {} blueprints", blueprints.size());
            return evaluateReserveDeckSelection(context, textLower);
        }

        logger.debug("[CardSelectionEvaluator] {} cards to evaluate", cardIds.size());

        // Route to specific handlers based on decision text
        if (textLower.contains("choose card to set sabacc value")) {
            return evaluateSabaccSetValue(context);
        } else if (textLower.contains("choose") && textLower.contains("clone")) {
            return evaluateSabaccClone(context);
        } else if (textLower.contains("choose where to deploy")) {
            // V67v (Steve, 2026-05-03): Routing precedence bug — this branch caught
            // turn-0 starting-location decisions BEFORE V67r could route them. Result:
            // all V67o/p/q/r + V29.14 Funeral Pyre + V24.10 CC Exterior + V67q Sith
            // logic was bypassed. Steve's symptom for ChosenOne playing Luke Saga:
            // picked a Tatooine site instead of Endor: Funeral Pyre (V29.14 should
            // give +1000).
            if (context.getTurnNumber() <= 0) {
                logger.warn("V67v STARTING DEPLOY: turn 0 'where to deploy' → evaluateStartingLocation (was missed by precedence bug)");
                return evaluateStartingLocation(context);
            }
            return evaluateDeployLocation(context);
        } else if (textLower.contains("force to lose or") && textLower.contains("forfeit")) {
            // COMBINED decision: lose force OR forfeit card - MUST check before individual handlers!
            // Critical: Attrition MUST be satisfied by forfeiting, battle damage can be either
            return evaluateForceLossOrForfeit(context);
        } else if (textLower.contains("choose force to lose")) {
            return evaluateForceLoss(context);
        } else if (textLower.contains("choose a card from battle to forfeit") ||
                   textLower.contains("forfeit")) {
            return evaluateForfeit(context);
        } else if (textLower.contains("simultaneously deploy aboard")) {
            // Simultaneous pilot deployment - special handling
            return evaluateSimultaneousPilotSelection(context);
        } else if (textLower.contains("choose a pilot") ||
                   (textLower.contains("pilot") && (textLower.contains("choose") || textLower.contains("select"))) ||
                   (textLower.contains("matching") && textLower.contains("starship"))) {
            // V22.7: Broadened to catch AMSD pilot selection — GEMP text may say
            // "Choose a unique pilot character" which doesn't match "choose a pilot"
            return evaluatePilotSelection(context);
        } else if (textLower.contains("choose card to cancel")) {
            return evaluateCancelSelection(context);
        } else if (textLower.contains("move to,")
                   || textLower.contains("where to move")
                   || (textLower.contains("move") && textLower.contains("to")
                       && !textLower.contains("choose target")
                       && !textLower.contains("cardhint"))) {
            // V63 ROUTING FIX: Route destination-selection decisions to
            // evaluateMoveDestination BEFORE the "click 'done' to cancel" branch.
            // V67d: "Choose where to move <X>" is destination selection (cardHint
            // is the character, not the destination).
            return evaluateMoveDestination(context);
        } else if (textLower.contains("choose target") ||
                   textLower.contains("click 'done' to cancel")) {
            // === V42: SHIELD CHECK — must come before other routing in this branch ===
            // K&D shield selection uses "Choose card, or click 'Done' to cancel" which
            // matches this branch. Check if all choices are shields FIRST.
            if (isShieldSelectionByContent(context)) {
                logger.warn("V42 SHIELD ROUTING FIX: 'click done to cancel' text but content is shields → evaluateShieldSelection");
                return evaluateShieldSelection(context);
            }
            // === V24.11: AMSD ROUTING — CHECK BEFORE evaluateTargetSelection ===
            // "Choose card from hand, or click 'Done' to cancel" matches this branch,
            // but when AMSD is active and we're picking characters in deploy phase,
            // this is actually an AMSD pilot selection. Route to evaluatePilotSelection
            // so Piett-only enforcement fires. Without this, Vader gets picked and
            // the AMSD action fails because Executor isn't his matching ship.
            if (context.getPhase() == Phase.DEPLOY) {
                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle amsdOracle = context.getDeckOracle();
                if (amsdOracle != null && amsdOracle.isAnalyzed()) {
                    boolean amsdOnTable = amsdOracle.isCardInPlay("Alert My Star Destroyer")
                        || amsdOracle.isCardInPlay("Alert My Star Destroyer!")
                        || amsdOracle.isCardInPlay("Alert My Star Destroyer! (V)");
                    if (amsdOnTable) {
                        boolean hasCharacterChoices = false;
                        GameState amsdGs = context.getGameState();
                        if (amsdGs != null && context.getCardIds() != null) {
                            for (String cid : context.getCardIds()) {
                                try {
                                    PhysicalCard rc = amsdGs.findCardById(Integer.parseInt(cid));
                                    if (rc != null && rc.getBlueprint() != null &&
                                        rc.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                        hasCharacterChoices = true;
                                        break;
                                    }
                                } catch (Exception e) { /* skip */ }
                            }
                        }
                        if (hasCharacterChoices) {
                            logger.warn("V24.11 AMSD ROUTING FIX: 'click done to cancel' branch but AMSD active + deploy phase + characters → routing to evaluatePilotSelection!");
                            return evaluatePilotSelection(context);
                        }
                    }
                }
            }
            return evaluateTargetSelection(context);
        } else if (textLower.contains("move") && textLower.contains("to")) {
            // Move destination selection
            return evaluateMoveDestination(context);
        } else if (textLower.contains("transit") || textLower.contains("transport")) {
            // Transit/transport destination selection
            return evaluateMoveDestination(context);
        } else if (textLower.contains("starting interrupt")) {
            // V43: Route starting interrupt selection
            return evaluateStartingInterrupt(context);
        } else if (textLower.contains("starting location")) {
            return evaluateStartingLocation(context);
        } else if (context.getTurnNumber() <= 0
                   && textLower.contains("where to deploy")) {
            // V67r: At turn 0 (PLAY_STARTING_CARDS), the starting interrupt asks
            // "Choose where to deploy <card>" — NOT "starting location". Without
            // this routing, V67o/p/q never fire.
            logger.warn("V67r STARTING DEPLOY: routing 'where to deploy' on turn 0 to evaluateStartingLocation");
            return evaluateStartingLocation(context);
        } else if (textLower.contains("choose") && textLower.contains("location")) {
            return evaluateLocationSelection(context);
        } else if (textLower.contains("card to take into hand")) {
            return evaluateTakeIntoHand(context);
        } else if (textLower.contains("card to put on lost pile")) {
            return evaluateLostPileSelection(context);
        } else if (textLower.contains("defensive shield") ||
                   isShieldSelectionByContent(context)) {
            return evaluateShieldSelection(context);
        } else {
            // === V24.10: AMSD ROUTING CATCH ===
            // If AMSD is in play and we're choosing characters during deploy phase,
            // this is almost certainly an AMSD pilot selection that wasn't caught by
            // the regular pilot routing (decision text didn't contain "pilot").
            // Route to evaluatePilotSelection to get full Piett-only enforcement.
            if (context.getPhase() == Phase.DEPLOY) {
                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle routeOracle = context.getDeckOracle();
                if (routeOracle != null && routeOracle.isAnalyzed()) {
                    boolean amsdOnTable = routeOracle.isCardInPlay("Alert My Star Destroyer")
                        || routeOracle.isCardInPlay("Alert My Star Destroyer!")
                        || routeOracle.isCardInPlay("Alert My Star Destroyer! (V)");
                    if (amsdOnTable) {
                        // Check if the choices include characters (i.e., pilot candidates)
                        boolean hasCharacterChoices = false;
                        GameState routeGs = context.getGameState();
                        if (routeGs != null && context.getCardIds() != null) {
                            for (String cid : context.getCardIds()) {
                                try {
                                    PhysicalCard rc = routeGs.findCardById(Integer.parseInt(cid));
                                    if (rc != null && rc.getBlueprint() != null &&
                                        rc.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                        hasCharacterChoices = true;
                                        break;
                                    }
                                } catch (Exception e) { /* skip */ }
                            }
                        }
                        if (hasCharacterChoices) {
                            logger.warn("V24.10 AMSD ROUTING CATCH: AMSD in play + deploy phase + character choices → routing to evaluatePilotSelection!");
                            return evaluatePilotSelection(context);
                        }
                    }
                }
            }
            // Unknown - create neutral scored actions
            return evaluateUnknown(context);
        }
    }

    /**
     * Sabacc value setting - random selection to break loops.
     */
    private List<EvaluatedAction> evaluateSabaccSetValue(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();

        for (String cardId : context.getCardIds()) {
            // V24.5: No randomness — use deterministic score
            float sabaccScore = 0.0f;

            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.UNKNOWN,
                sabaccScore,
                "Set sabacc value (card " + cardId + ")"
            );
            action.addReasoning("Sabacc value (deterministic)", sabaccScore);
            actions.add(action);
        }

        return actions;
    }

    /**
     * Sabacc clone - avoid cloning.
     */
    private List<EvaluatedAction> evaluateSabaccClone(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.UNKNOWN,
                VERY_BAD_DELTA,
                "Clone sabacc value"
            );
            action.addReasoning("Avoid cloning sabacc cards", VERY_BAD_DELTA);
            actions.add(action);
        }

        return actions;
    }

    /**
     * Choose where to deploy - evaluate locations.
     *
     * CRITICAL RULES (ported from Python card_selection_evaluator.py lines 185-400):
     * 1. Starships should NEVER deploy to docking bays (0 power!)
     * 2. Starships without pilots (and no permanent pilot icon) are weak
     * 3. Always prefer space systems over docking bays for starships
     * 4. Vehicles need EXTERIOR ground locations
     * 5. Follow the deploy plan when available
     */
    private List<EvaluatedAction> evaluateDeployLocation(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        SwccgGame game = context.getGame();
        String playerId = context.getPlayerId();

        // =====================================================
        // Detect what type of card we're deploying
        // =====================================================
        boolean isStarship = false;
        boolean isVehicle = false;
        boolean isCharacter = false;
        boolean isWeapon = false;  // Weapon deployment (deploys ON a character)
        String deployingCardName = "card";

        // Try to determine from decision text what we're deploying
        String decisionText = context.getDecisionText() != null ? context.getDecisionText().toLowerCase() : "";

        // Check for weapon deployment first - "as attached" indicates weapon/device
        if (decisionText.contains("as attached")) {
            isWeapon = true;
            deployingCardName = "weapon";
            logger.info("🔫 Detected WEAPON deployment (as attached)");
        } else if (decisionText.contains("starship") || decisionText.contains("capital ship")) {
            isStarship = true;
            deployingCardName = "starship";
        } else if (decisionText.contains("vehicle")) {
            isVehicle = true;
            deployingCardName = "vehicle";
        } else if (decisionText.contains("character") || decisionText.contains("alien") ||
                   decisionText.contains("droid") || decisionText.contains("jedi") ||
                   decisionText.contains("imperial") || decisionText.contains("rebel")) {
            isCharacter = true;
            deployingCardName = "character";
        }

        // =====================================================
        // Check deploy planner for target location
        // Extract the card being deployed from decision text HTML
        // Format: <div class='cardHint' value='8_35'>
        // =====================================================
        String plannedTargetId = null;
        String plannedTargetName = null;
        String deployingBlueprintId = extractBlueprintFromDecisionText(context.getDecisionText());

        DeployPhasePlanner deployPhasePlanner = context.getDeployPhasePlanner();
        if (deployPhasePlanner != null) {
            DeploymentPlan currentPlan = deployPhasePlanner.getCurrentPlan();
            if (currentPlan != null && !currentPlan.getInstructions().isEmpty()) {
                // FIXED: Look up the instruction for the SPECIFIC card being deployed
                if (deployingBlueprintId != null) {
                    DeploymentInstruction matchingInstruction = currentPlan.getInstructionForCard(deployingBlueprintId);
                    if (matchingInstruction != null && matchingInstruction.getTargetLocationId() != null) {
                        plannedTargetId = matchingInstruction.getTargetLocationId();
                        plannedTargetName = matchingInstruction.getTargetLocationName();
                        logger.info("📋 Deploy plan says: {} ({}) -> {}",
                            matchingInstruction.getCardName(), deployingBlueprintId, plannedTargetName);
                    } else {
                        logger.info("📋 No matching instruction for blueprint {}", deployingBlueprintId);
                    }
                } else {
                    // Fallback: use first instruction if we can't determine the card
                    logger.warn("⚠️ Could not extract blueprint from decision text, using first instruction");
                    for (DeploymentInstruction instruction : currentPlan.getInstructions()) {
                        if (instruction.getTargetLocationId() != null) {
                            plannedTargetId = instruction.getTargetLocationId();
                            plannedTargetName = instruction.getTargetLocationName();
                            logger.info("📋 Deploy plan fallback: {} -> {}", deployingCardName, plannedTargetName);
                            break;
                        }
                    }
                }
            }
        }

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.DEPLOY,
                50.0f,
                "Deploy to location " + cardId
            );

            // Try to get location info
            if (gameState != null) {
                try {
                    PhysicalCard location = gameState.findCardById(Integer.parseInt(cardId));
                    if (location != null) {
                        SwccgCardBlueprint blueprint = location.getBlueprint();
                        String title = location.getTitle();
                        String titleLower = title != null ? title.toLowerCase() : "";
                        action.setDisplayText("Deploy to " + (title != null ? title : "location"));

                        // === V64 MAPUZO JEDI-ONLY RULE ===
                        // On Hidden Path, only Jedi Survivors can transit off Mapuzo via the
                        // Underground Corridor game text. Non-Jedi characters deployed to any
                        // Mapuzo location get STUCK there — they can't follow the Jedi out to
                        // support them at battleground sites. Block non-Jedi character deploys
                        // to Mapuzo UNLESS the opponent is actively threatening Mapuzo with a
                        // drain or presence (in which case we need defenders).
                        // Steve's feedback: "The jedi are the only ones that can move off of
                        // Mapuzo, so deploying any other character except the fallen order
                        // jedi will result in trapping those characters on Mapuzo."
                        if (isCharacter && titleLower.contains("mapuzo")
                            && game != null && playerId != null) {
                            // Check if opponent is present at Mapuzo — defenders needed
                            String v64Opp = gameState.getOpponent(playerId);
                            float oppPowerAtMapuzo = 0;
                            try {
                                oppPowerAtMapuzo = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, location, v64Opp, false, false);
                            } catch (Exception e) { /* ignore */ }

                            // V67b: Authoritative Jedi Survivor test — game text contains
                            // literal "Jedi Survivor" keyword. Drops the persona-name fallback
                            // that misclassified Ahsoka Tano With Lightsabers, Obi-Wan With
                            // Lightsaber, etc. — they're Jedi but NOT Jedi Survivors and can't
                            // transit off Mapuzo via Underground Corridor.
                            boolean isJediSurvivor = false;
                            if (deployingBlueprintId != null) {
                                try {
                                    SwccgCardBlueprint deployBp = getBlueprintFromId(context, deployingBlueprintId);
                                    if (deployBp != null) {
                                        String gt = deployBp.getGameText();
                                        if (gt != null && gt.toLowerCase(java.util.Locale.ROOT).contains("jedi survivor")) {
                                            isJediSurvivor = true;
                                        }
                                    }
                                } catch (Exception e) { /* ignore */ }
                            }

                            if (!isJediSurvivor) {
                                if (oppPowerAtMapuzo > 0) {
                                    // Opponent is attacking/draining Mapuzo — defenders welcome
                                    action.addReasoning(
                                        "V64 MAPUZO DEFENSE: Opponent at " + title
                                            + " (power " + (int)oppPowerAtMapuzo
                                            + ") — non-Jedi defender OK here",
                                        30.0f);
                                    logger.info("V64 MAPUZO DEFENSE: {} needs defender vs opponent power {} (+30)",
                                        title, (int)oppPowerAtMapuzo);
                                } else {
                                    // Non-Jedi to empty Mapuzo = trapped forever. Hard block.
                                    action.addReasoning(
                                        "V64 MAPUZO TRAP: Non-Jedi character at " + title
                                            + " will be STUCK — only Jedi Survivors transit off Mapuzo!",
                                        -1500.0f);
                                    logger.warn("V64 MAPUZO TRAP: Non-Jedi deploy to empty {} BLOCKED (-1500)", title);
                                }
                            }
                        }

                        // =====================================================
                        // FOLLOW THE DEPLOY PLAN!
                        // =====================================================
                        if (plannedTargetId != null) {
                            if (cardId.equals(plannedTargetId)) {
                                action.addReasoning("PLANNED TARGET: " + plannedTargetName, 200.0f);
                                logger.info("✅ {} is the PLANNED target (+200)", title);
                            } else {
                                action.addReasoning("Not planned target (want " + plannedTargetName + ")", -100.0f);
                            }
                        }

                        // =====================================================
                        // V24.14B: EARLY SPY DETECTION (UNIVERSAL)
                        // Check the deploying card's blueprint game text for "undercover".
                        // This catches ALL undercover spy cards, not just hardcoded names.
                        // Sets earlySpyDetected flag — deeper spy scoring handles location logic
                        // (including allowing spy at CC sites where OPPONENT has presence).
                        // =====================================================
                        boolean earlySpyDetected = false;
                        // Primary: Check deploying card's blueprint game text for "undercover"
                        if (deployingBlueprintId != null) {
                            try {
                                SwccgCardBlueprint deployingBp = getBlueprintFromId(context, deployingBlueprintId);
                                if (deployingBp != null) {
                                    String gameTextCheck = deployingBp.getGameText();
                                    if (gameTextCheck != null && gameTextCheck.toLowerCase(java.util.Locale.ROOT).contains("undercover")) {
                                        earlySpyDetected = true;
                                        logger.warn("V24.14B SPY DETECT: Blueprint game text contains 'undercover' — spy deploy!");
                                    }
                                }
                            } catch (Exception e) {
                                logger.debug("V24.14B: Error checking deploying blueprint: {}", e.getMessage());
                            }
                        }
                        // Fallback: Check decision text for spy-related keywords
                        if (!earlySpyDetected) {
                            if (decisionText.contains("undercover") || decisionText.contains("as a spy")) {
                                earlySpyDetected = true;
                                logger.warn("V24.14B SPY DETECT: Decision text contains spy keyword — spy deploy!");
                            }
                        }

                        // =====================================================
                        // CRITICAL: Check if target is a STARSHIP (cargo bay)
                        // Deploying ships INTO other ships is almost always terrible!
                        // Ships in cargo bays contribute 0 power!
                        // =====================================================
                        if (blueprint != null && blueprint.getCardCategory() == CardCategory.STARSHIP) {
                            // Target is a capital ship - we'd be deploying INTO its cargo bay
                            action.addReasoning("⚠️ DEPLOY TO CARGO BAY = 0 POWER!", -300.0f);
                            logger.warn("⚠️ BLOCKING deploy of {} into cargo bay of {} - ships in cargo contribute 0 power!",
                                deployingCardName, title);
                            // Don't evaluate further - this is almost never correct
                            actions.add(action);
                            continue;
                        }

                        // =====================================================
                        // CRITICAL: WEAPON DEPLOYMENT - check if target already has weapon
                        // Don't deploy a second weapon on a character that already has one!
                        // =====================================================
                        if (isWeapon && blueprint != null && blueprint.getCardCategory() == CardCategory.CHARACTER) {
                            PhysicalCard targetCharacter = location;  // 'location' is actually the target character
                            boolean alreadyHasWeapon = false;
                            String existingWeaponName = null;

                            // Check cards attached to this character
                            List<PhysicalCard> attachedCards = gameState.getAttachedCards(targetCharacter);
                            if (attachedCards != null) {
                                for (PhysicalCard attached : attachedCards) {
                                    if (attached != null && attached.getBlueprint() != null) {
                                        CardCategory attachedCategory = attached.getBlueprint().getCardCategory();
                                        if (attachedCategory == CardCategory.WEAPON) {
                                            alreadyHasWeapon = true;
                                            existingWeaponName = attached.getTitle();
                                            break;
                                        }
                                    }
                                }
                            }

                            if (alreadyHasWeapon) {
                                // V25: HARD BLOCK deploying second weapon — characters can only use one!
                                // Previous -200 was too weak and got overridden by lightsaber priority.
                                action.addReasoning("⚠️ CHARACTER ALREADY HAS WEAPON: " + existingWeaponName + " — CANNOT USE TWO!", -9999.0f);
                                logger.warn("⚠️ V25 HARD BLOCK: {} already has weapon '{}' - NEVER deploy second weapon!",
                                    title, existingWeaponName);
                            } else {
                                // Good target - character has no weapon
                                action.addReasoning("Character needs weapon", 20.0f);
                            }
                        }

                        // =====================================================
                        // V25: HUNT DOWN V — LIGHTSABER DEPLOY PRIORITY
                        // Lightsabers are critical for the Hunt Down deck engine.
                        // Boost any card with "lightsaber" in the title when deploying.
                        // BUT: Never deploy a second lightsaber on same character!
                        // =====================================================
                        if (deployingBlueprintId != null) {
                            try {
                                SwccgCardBlueprint lsDeployBp = getBlueprintFromId(context, deployingBlueprintId);
                                if (lsDeployBp != null && lsDeployBp.getTitle() != null) {
                                    String lsDeployTitle = lsDeployBp.getTitle().toLowerCase(java.util.Locale.ROOT);
                                    if (lsDeployTitle.contains("lightsaber")) {
                                        // V25: Check if target character already has a lightsaber/weapon
                                        boolean targetHasLightsaber = false;
                                        if (blueprint != null && blueprint.getCardCategory() == CardCategory.CHARACTER) {
                                            PhysicalCard targetChar = location;
                                            List<PhysicalCard> targetAttached = gameState.getAttachedCards(targetChar);
                                            if (targetAttached != null) {
                                                for (PhysicalCard att : targetAttached) {
                                                    if (att != null && att.getBlueprint() != null) {
                                                        CardCategory attCat = att.getBlueprint().getCardCategory();
                                                        String attTitle = att.getTitle();
                                                        if (attCat == CardCategory.WEAPON ||
                                                            (attTitle != null && attTitle.toLowerCase(java.util.Locale.ROOT).contains("lightsaber"))) {
                                                            targetHasLightsaber = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (targetHasLightsaber) {
                                            action.addReasoning("V25 HUNT DOWN: Target ALREADY HAS lightsaber — NEVER deploy second!", -9999.0f);
                                            logger.warn("V25 HUNT DOWN: BLOCKED second lightsaber on {} — can only use one!", title);
                                        } else {
                                            com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer lsDeployOA =
                                                context.getObjectiveAnalyzer();
                                            if (lsDeployOA != null && lsDeployOA.isAnalyzed() && lsDeployOA.isHuntDownV()) {
                                                action.addReasoning("V25 HUNT DOWN: DEPLOYING LIGHTSABER — deck engine critical!", 150.0f);
                                                logger.warn("V25 HUNT DOWN: Lightsaber '{}' deploying — PRIORITY (+150)", lsDeployBp.getTitle());
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                logger.debug("V25 HUNT DOWN: Error checking lightsaber deploy: {}", e.getMessage());
                            }
                        }

                        // =====================================================
                        // CRITICAL: Detect location type
                        // =====================================================
                        boolean isDockingBay = titleLower.contains("docking bay") || titleLower.contains("landing platform");
                        boolean isSpaceSystem = false;
                        boolean isGroundSite = false;

                        if (blueprint != null) {
                            com.gempukku.swccgo.common.CardSubtype subtype = blueprint.getCardSubtype();
                            isSpaceSystem = (subtype == com.gempukku.swccgo.common.CardSubtype.SYSTEM);
                            isGroundSite = (subtype == com.gempukku.swccgo.common.CardSubtype.SITE) && !isDockingBay;
                        }

                        // =====================================================
                        // CRITICAL: Starships at docking bays have 0 power!
                        // Ported from Python card_selection_evaluator.py lines 267-283
                        // =====================================================
                        if (isStarship) {
                            if (isDockingBay) {
                                // NEVER deploy starships to docking bays!
                                action.addReasoning("⚠️ STARSHIP TO DOCKING BAY = 0 POWER!", VERY_BAD_DELTA);
                                logger.warn("⚠️ {} would have 0 power at docking bay {}!", deployingCardName, title);
                            } else if (isSpaceSystem) {
                                // Space system - starship has power here (if piloted)
                                // BUT check if we'd be at a power disadvantage!
                                if (game != null) {
                                    try {
                                        float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                            game.getGameState(), location, playerId, false, false);
                                        String opponent = game.getOpponent(playerId);
                                        float theirPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                            game.getGameState(), location, opponent, false, false);

                                        if (theirPower > 0) {
                                            // Contested space location - check power differential
                                            // Get ship's power from blueprint
                                            int shipPower = 0;
                                            SwccgCardBlueprint deployingBlueprint = getBlueprintFromId(context, deployingBlueprintId);
                                            if (deployingBlueprint != null && deployingBlueprint.hasPowerAttribute()) {
                                                Float power = deployingBlueprint.getPower();
                                                shipPower = power != null ? power.intValue() : 0;
                                            }

                                            float projectedPower = ourPower + shipPower;
                                            if (projectedPower < theirPower) {
                                                // We'd still be losing after deployment!
                                                action.addReasoning(String.format(
                                                    "⚠️ SPACE POWER DISADVANTAGE: %.0f vs %.0f after deploy",
                                                    projectedPower, theirPower), -80.0f);
                                                logger.warn("⚠️ Deploying {} to {} would leave us at power disadvantage ({} vs {})",
                                                    deployingCardName, title, (int)projectedPower, (int)theirPower);
                                            } else if (projectedPower >= theirPower + 3) {
                                                // Good advantage
                                                action.addReasoning(String.format(
                                                    "Good space position: %.0f vs %.0f after deploy",
                                                    projectedPower, theirPower), 30.0f);
                                            } else {
                                                // Close fight
                                                action.addReasoning(String.format(
                                                    "Close space fight: %.0f vs %.0f after deploy",
                                                    projectedPower, theirPower), 10.0f);
                                            }
                                        } else {
                                            // Uncontested - good target
                                            action.addReasoning("Uncontested space system", 30.0f);
                                        }
                                    } catch (Exception e) {
                                        // Fallback to basic bonus if we can't check power
                                        action.addReasoning("Starship to space system", GOOD_DELTA * 2);
                                        logger.debug("Could not check power at {}: {}", title, e.getMessage());
                                    }
                                } else {
                                    action.addReasoning("Starship to space system", GOOD_DELTA * 2);
                                }
                            } else if (isGroundSite) {
                                // Ground location - starship can't deploy here normally
                                action.addReasoning("STARSHIP TO GROUND - unusual!", BAD_DELTA);
                            }
                        }

                        // =====================================================
                        // CRITICAL: Vehicles need EXTERIOR ground locations
                        // Ported from Python card_selection_evaluator.py lines 287-302
                        // =====================================================
                        if (isVehicle) {
                            if (isSpaceSystem) {
                                // Space location - vehicles can't deploy here
                                action.addReasoning("VEHICLE TO SPACE - invalid!", VERY_BAD_DELTA);
                            } else if (isGroundSite || isDockingBay) {
                                // Check if location has exterior icon
                                boolean hasExterior = true;  // Default to true if unknown
                                boolean hasInteriorOnly = false;

                                if (blueprint != null) {
                                    // Use hasIcon() method instead of getIcons()
                                    boolean foundExterior = blueprint.hasIcon(com.gempukku.swccgo.common.Icon.EXTERIOR_SITE);
                                    boolean foundInterior = blueprint.hasIcon(com.gempukku.swccgo.common.Icon.INTERIOR_SITE);
                                    hasExterior = foundExterior;
                                    hasInteriorOnly = foundInterior && !foundExterior;
                                }

                                if (hasInteriorOnly) {
                                    action.addReasoning("VEHICLE TO INTERIOR-ONLY - can't deploy!", VERY_BAD_DELTA);
                                    logger.warn("⚠️ Vehicle cannot deploy to interior site {}", title);
                                } else if (hasExterior) {
                                    action.addReasoning("Vehicle to exterior ground - good", GOOD_DELTA);
                                }
                            }
                        }

                        // =====================================================
                        // V24.14B: WEAPON CHARACTERS/VEHICLES TO SPACE — PENALIZE
                        // Characters with weapons (lightsabers, blasters) can't fire them at
                        // system locations (space). They're mostly useless there.
                        // Also penalize vehicles going to space (already handled above with
                        // VERY_BAD_DELTA, but this adds reasoning for character+weapon combos).
                        // =====================================================
                        if (isCharacter && isSpaceSystem) {
                            // Check if this character has a permanent weapon.
                            // Characters with "permanent weapon" in game text have built-in weapons
                            // that can't fire at system (space) locations — they're useless there.
                            boolean hasPermanentWeapon = false;
                            // Primary: Check deploying card's blueprint game text for "permanent weapon"
                            if (deployingBlueprintId != null) {
                                try {
                                    SwccgCardBlueprint weaponCheckBp = getBlueprintFromId(context, deployingBlueprintId);
                                    if (weaponCheckBp != null) {
                                        String weaponGameText = weaponCheckBp.getGameText();
                                        if (weaponGameText != null) {
                                            String weaponTextLower = weaponGameText.toLowerCase(java.util.Locale.ROOT);
                                            if (weaponTextLower.contains("permanent weapon")) {
                                                hasPermanentWeapon = true;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.debug("V24.14B WEAPON CHECK: Error: {}", e.getMessage());
                                }
                            }
                            // Fallback: Check decision text for weapon keywords in card name
                            if (!hasPermanentWeapon) {
                                if (decisionText.contains("lightsaber") || decisionText.contains("blaster")
                                    || decisionText.contains("with rifle") || decisionText.contains("with cannon")) {
                                    hasPermanentWeapon = true;
                                }
                            }
                            if (hasPermanentWeapon) {
                                action.addReasoning("V24.14B WEAPON CHAR TO SPACE: Permanent weapon can't fire at system locations — useless in space!", -300.0f);
                                logger.warn("V24.14B WEAPON TO SPACE: Character with permanent weapon deploying to {} — penalized (-300)", title);
                            }
                        }
                        // V24.14B: Weapon characters are GREAT at ground sites — they win battles!
                        // Bonus for deploying to sites, especially objective locations or contested ones.
                        if (isCharacter && (isGroundSite || isDockingBay)) {
                            boolean hasPermanentWeaponGround = false;
                            if (deployingBlueprintId != null) {
                                try {
                                    SwccgCardBlueprint wgBp = getBlueprintFromId(context, deployingBlueprintId);
                                    if (wgBp != null) {
                                        String wgText = wgBp.getGameText();
                                        if (wgText != null && wgText.toLowerCase(java.util.Locale.ROOT).contains("permanent weapon")) {
                                            hasPermanentWeaponGround = true;
                                        }
                                    }
                                } catch (Exception e) { /* ignore */ }
                            }
                            if (!hasPermanentWeaponGround) {
                                if (decisionText.contains("lightsaber") || decisionText.contains("blaster")
                                    || decisionText.contains("with rifle") || decisionText.contains("with cannon")) {
                                    hasPermanentWeaponGround = true;
                                }
                            }
                            if (hasPermanentWeaponGround) {
                                action.addReasoning("V24.14B WEAPON CHAR ON GROUND: Strong battle presence — weapon fires here!", 100.0f);
                                logger.info("V24.14B WEAPON GROUND: Character with permanent weapon at site {} — bonus (+100)", title);
                            }
                        }

                        // =====================================================
                        // Battleground bonus (for all card types)
                        // =====================================================
                        if (blueprint != null && blueprint.getCardCategory() == CardCategory.LOCATION) {
                            if (titleLower.contains("battleground") || isLikelyBattleground(blueprint)) {
                                action.addReasoning("Battleground location", 30.0f);
                            }
                        }

                        // V22: OBJECTIVE LOCATION BONUS (boosted from +50 to +150)
                        // Deploy to locations relevant to our objective - critical for flipping
                        // V24.15: SKIP for spies — they don't contribute presence to objectives while undercover!
                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer locObjAnalyzer =
                            context.getObjectiveAnalyzer();
                        if (!earlySpyDetected && locObjAnalyzer != null && locObjAnalyzer.isAnalyzed() && title != null) {
                            if (locObjAnalyzer.isObjectiveRelevantLocation(title)) {
                                float objLocBonus = locObjAnalyzer.getLocationObjectiveBonus(title);
                                action.addReasoning("OBJECTIVE LOCATION - deploy here helps flip!", objLocBonus);
                                logger.warn("V22 OBJECTIVE DEPLOY: {} is objective-relevant (+{})", title, objLocBonus);
                            }
                        }

                        // === V88 (Steve, 2026-05-19): MY LORD — SENATOR → GALACTIC SENATE BONUS ===
                        // Mirror of Rando V88. See Rando CardSelectionEvaluator V88 comment.
                        if (locObjAnalyzer != null && locObjAnalyzer.isAnalyzed()
                                && locObjAnalyzer.getObjectiveTitle() != null
                                && title != null
                                && deployingBlueprintId != null) {
                            String v88ObjLower = locObjAnalyzer.getObjectiveTitle().toLowerCase(java.util.Locale.ROOT);
                            boolean v88IsMyLord = v88ObjLower.contains("my lord")
                                || v88ObjLower.contains("make it legal");
                            if (v88IsMyLord) {
                                try {
                                    SwccgCardBlueprint v88DepBp = getBlueprintFromId(context, deployingBlueprintId);
                                    boolean isSenator = v88DepBp != null
                                        && v88DepBp.hasKeyword(com.gempukku.swccgo.common.Keyword.SENATOR);
                                    if (isSenator) {
                                        String v88TitleLower = title.toLowerCase(java.util.Locale.ROOT);
                                        if (v88TitleLower.contains("galactic senate")) {
                                            action.addReasoning(
                                                "V88 MY LORD: senator → Galactic Senate (flip target + weapon destiny -6 protection)",
                                                1500.0f);
                                            logger.warn("V88 MY LORD: senator location bonus +1500 for {}", title);
                                        } else {
                                            action.addReasoning(
                                                "V88 MY LORD: senator not at Galactic Senate — wrong site!",
                                                -2000.0f);
                                            logger.warn("V88 MY LORD: senator BLOCK -2000 for non-Senate target {}", title);
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.debug("V88 MY LORD CardSelection error: {}", e.getMessage());
                                }
                            }
                        }

                        // === V24.10: EXECUTOR MUST DEPLOY TO BESPIN ===
                        // When deploying Executor/Flagship, Bespin system is the ONLY correct target
                        // for TDIGWATT. Deploying to any other system is catastrophic — the entire
                        // deck engine depends on Executor occupying Bespin for Dark Deal + CC Occupation.
                        if (isStarship && isSpaceSystem && deployingCardName != null) {
                            String deployingNameLower = deployingCardName.toLowerCase(java.util.Locale.ROOT);
                            if (deployingNameLower.contains("executor") || deployingNameLower.contains("flagship")) {
                                String locTitleLower = title != null ? title.toLowerCase(java.util.Locale.ROOT) : "";
                                if (locTitleLower.contains("bespin")) {
                                    action.addReasoning("V24.10 EXECUTOR TO BESPIN: This is THE correct system — entire TDIGWATT engine depends on it!", 500.0f);
                                    logger.warn("V24.10 EXECUTOR LOCATION: Bespin system selected — MASSIVE bonus (+500)!");
                                } else {
                                    // Any non-Bespin system is WRONG for Executor
                                    action.addReasoning("V24.10 EXECUTOR WRONG SYSTEM: Executor MUST go to Bespin, not " + title + "!", -9999.0f);
                                    logger.warn("V24.10 EXECUTOR LOCATION: {} is NOT Bespin — HARD BLOCK! Executor must deploy to Bespin!", title);
                                }
                            }
                        }

                        // === V22.7: OBJECTIVE-CRITICAL LOCATION CONTESTATION ===
                        // If the opponent occupies a location our objective NEEDS us to control,
                        // we MUST contest it — even if we're currently losing there.
                        // The objective bonus alone may not override the contest penalty,
                        // so add an explicit "MUST CONTEST" bonus for ships at critical systems.
                        if (locObjAnalyzer != null && locObjAnalyzer.isAnalyzed() && title != null
                            && locObjAnalyzer.isObjectiveRelevantLocation(title)
                            && isSpaceSystem && isStarship && game != null) {
                            try {
                                float ourP = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    game.getGameState(), location, playerId, false, false);
                                String opp = game.getOpponent(playerId);
                                float theirP = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    game.getGameState(), location, opp, false, false);
                                if (theirP > 0 && ourP < theirP) {
                                    // Opponent controls our objective-critical system!
                                    // Strong override to ensure we deploy here despite contest penalty.
                                    action.addReasoning("V22.7 MUST CONTEST: Opponent controls objective-critical " +
                                        title + "! Deploy ship to contest!", 300.0f);
                                    logger.warn("V22.7 MUST CONTEST: {} — opponent has {} power, we have {} — MUST deploy ship here!",
                                        title, (int)theirP, (int)ourP);
                                }
                            } catch (Exception e) {
                                logger.debug("V22.7: Could not check objective-critical contest: {}", e.getMessage());
                            }
                        }

                        // === V23: OPPONENT FORCE ICON PREFERENCE (ALL OBJECTIVES) ===
                        // Locations with opponent force icons are better force drain targets.
                        // For Dark Side: Light Side force icons = more drain damage.
                        if (blueprint != null) {
                            Side mySide = context.getSide();
                            int opponentIcons = 0;
                            if (mySide == Side.DARK) {
                                opponentIcons = blueprint.getIconCount(Icon.LIGHT_FORCE);
                            } else {
                                opponentIcons = blueprint.getIconCount(Icon.DARK_FORCE);
                            }
                            if (opponentIcons > 0) {
                                float iconBonus = opponentIcons * 30.0f;
                                action.addReasoning("V23 FORCE DRAIN: " + opponentIcons +
                                    " opponent force icon(s) — better drain target!", iconBonus);
                                logger.info("V23 FORCE ICONS: {} has {} opponent icons (+{})", title, opponentIcons, (int)iconBonus);
                            }
                        }

                        // === V24.15: AVOID DEPLOYING CHARACTERS TO 0-DRAIN LOCATIONS ===
                        // Characters at 0-drain locations contribute nothing to force drain pressure.
                        // They're wasted resources and vulnerable to Surprise Assault traps.
                        // Penalty scales with character power — don't waste your best characters!
                        if (isCharacter && !earlySpyDetected && game != null && location != null) {
                            try {
                                float deployDrainAmount = game.getModifiersQuerying().getForceDrainAmount(
                                    game.getGameState(), location, playerId);
                                if (deployDrainAmount <= 0) {
                                    // 0-drain location — penalize character deployment
                                    Float charPower = (blueprint != null && blueprint.hasPowerAttribute()) ? blueprint.getPower() : null;
                                    float powerVal = (charPower != null) ? charPower : 3.0f;
                                    // Higher power characters get bigger penalty — don't waste Palpatine on a 0-drain site!
                                    float zeroDrainPenalty = -50.0f - (powerVal * 10.0f);
                                    action.addReasoning("V24.15 ZERO DRAIN: Location has 0 drain — character wasted here!", zeroDrainPenalty);
                                    logger.warn("V24.15 ZERO DRAIN: {} has 0 drain — penalizing {} (power {}) by {}",
                                        title, decisionText, powerVal, zeroDrainPenalty);
                                }
                            } catch (Exception e) {
                                logger.debug("V24.15: Error checking drain amount for deploy: {}", e.getMessage());
                            }
                        }

                        // === V25: ISB OPERATIONS DEPLOYMENT STRATEGY ===
                        // When running ISB Operations, prioritize deploying ISB agents (chars with
                        // ISB/Rebel/Rebellion in lore) to get 4 on table for the flip.
                        // After flip, ISB agents at battleground sites give +1/-1 drain bonuses.
                        // Non-ISB characters are deprioritized pre-flip to save Force for agents.
                        if (isCharacter && !earlySpyDetected && locObjAnalyzer != null
                            && locObjAnalyzer.isAnalyzed() && locObjAnalyzer.isISBOperations()) {
                            try {
                                // Check if the deploying card is an ISB agent
                                boolean deployingIsISBAgent = false;
                                if (deployingBlueprintId != null) {
                                    SwccgCardBlueprint deployBp = getBlueprintFromId(context, deployingBlueprintId);
                                    if (deployBp != null) {
                                        String lore = deployBp.getLore();
                                        if (lore != null) {
                                            String loreLower = lore.toLowerCase(Locale.ROOT);
                                            deployingIsISBAgent = loreLower.contains("isb")
                                                || loreLower.contains("rebel") || loreLower.contains("rebellion");
                                        }
                                    }
                                }

                                int isbOnTable = locObjAnalyzer.countISBAgentsOnTable(gameState, playerId);
                                int isbNeeded = locObjAnalyzer.getISBFlipAgentCount();
                                boolean preFlip = !locObjAnalyzer.isFlipped();
                                boolean needMoreAgents = preFlip && isbOnTable < isbNeeded;

                                // Check if this location is a battleground site
                                boolean isBattleground = false;
                                if (blueprint != null) {
                                    String locGameText = blueprint.getGameText();
                                    if (locGameText != null && locGameText.toLowerCase(Locale.ROOT).contains("battleground")) {
                                        isBattleground = true;
                                    }
                                    // Also check via icon — many battleground sites have the icon
                                    if (blueprint.hasIcon(com.gempukku.swccgo.common.Icon.DARK_FORCE)
                                        || blueprint.hasIcon(com.gempukku.swccgo.common.Icon.LIGHT_FORCE)) {
                                        // Sites with force icons are typically battleground sites
                                        isBattleground = true;
                                    }
                                }

                                if (deployingIsISBAgent) {
                                    // ISB agent — bonus to deploy, bigger bonus at battlegrounds
                                    float isbBonus = 80.0f;
                                    if (needMoreAgents) {
                                        isbBonus = 120.0f;  // Extra urgency pre-flip
                                    }
                                    if (isBattleground) {
                                        isbBonus += 50.0f;  // Battleground = drain bonus after flip
                                    }
                                    action.addReasoning("V25 ISB AGENT: Deploy ISB agent (" + isbOnTable +
                                        "/" + isbNeeded + " on table)" +
                                        (isBattleground ? " to BATTLEGROUND" : "") +
                                        (needMoreAgents ? " — NEED MORE FOR FLIP!" : ""), isbBonus);
                                    logger.warn("V25 ISB: {} is ISB agent at {} ({}/{} on table, bg={}, +{})",
                                        decisionText, title, isbOnTable, isbNeeded, isBattleground, (int)isbBonus);
                                } else if (needMoreAgents) {
                                    // Non-ISB character when we need more agents — deprioritize
                                    float nonIsbPenalty = -80.0f;
                                    action.addReasoning("V25 ISB: Non-ISB character — save Force for ISB agents! (" +
                                        isbOnTable + "/" + isbNeeded + " agents on table)", nonIsbPenalty);
                                    logger.warn("V25 ISB: {} is NOT ISB agent — penalizing ({}/{} agents on table)",
                                        decisionText, isbOnTable, isbNeeded);
                                }
                            } catch (Exception e) {
                                logger.debug("V25 ISB: Error in ISB Operations scoring: {}", e.getMessage());
                            }
                        }

                        // === V25: HUNT DOWN V — VADER PRIORITY DEPLOYMENT ===
                        // When running Hunt Down V, Vader MUST be deployed to flip the objective.
                        // Without Vader: deck bleeds 1 Force/turn from Visage, can't flip, can't cancel drains.
                        // If Vader is not on table:
                        //   - Vader gets massive deploy bonus (+300) to any battleground site
                        //   - Non-Vader characters get heavy penalty (-200) to save Force for Vader
                        //   - Exception: Inquisitors still get a small allowance since they help battle destiny
                        if (isCharacter && !earlySpyDetected && locObjAnalyzer != null
                            && locObjAnalyzer.isAnalyzed() && locObjAnalyzer.isHuntDownV()) {
                            try {
                                boolean vaderOnTable = locObjAnalyzer.isVaderOnTable(gameState, playerId);
                                boolean preFlip = !locObjAnalyzer.isFlipped();

                                // Check if the deploying card IS Vader
                                boolean deployingIsVader = false;
                                boolean deployingIsInquisitor = false;
                                if (deployingBlueprintId != null) {
                                    SwccgCardBlueprint deployBp = getBlueprintFromId(context, deployingBlueprintId);
                                    if (deployBp != null) {
                                        deployingIsVader = com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer.isVaderCard(deployBp);
                                        // Check if Inquisitor — they have "inquisitor" in title or characteristics
                                        String depTitle = deployBp.getTitle();
                                        String depGameText = deployBp.getGameText();
                                        if (depTitle != null) {
                                            String depTitleLower = depTitle.toLowerCase(Locale.ROOT);
                                            deployingIsInquisitor = depTitleLower.contains("inquisitor")
                                                || depTitleLower.contains("fifth brother")
                                                || depTitleLower.contains("seventh sister")
                                                || depTitleLower.contains("eighth brother")
                                                || depTitleLower.contains("second sister")
                                                || depTitleLower.contains("grand inquisitor");
                                        }
                                    }
                                }

                                if (deployingIsVader) {
                                    // VADER — massive bonus, especially to battleground sites
                                    float vaderBonus = 300.0f;
                                    // Check if this is a battleground site (needed for flip)
                                    boolean isBattleground = false;
                                    if (blueprint != null) {
                                        isBattleground = blueprint.hasIcon(com.gempukku.swccgo.common.Icon.DARK_FORCE)
                                            && blueprint.hasIcon(com.gempukku.swccgo.common.Icon.LIGHT_FORCE);
                                    }
                                    if (isBattleground) {
                                        vaderBonus += 100.0f;  // Extra bonus for battleground — enables flip!
                                    }
                                    action.addReasoning("V25 HUNT DOWN: DEPLOY VADER! Critical for flip!" +
                                        (isBattleground ? " BATTLEGROUND = CAN FLIP!" : ""), vaderBonus);
                                    logger.warn("V25 HUNT DOWN: Vader deploy to {} — MASSIVE PRIORITY (+{})",
                                        title, (int)vaderBonus);
                                } else if (!vaderOnTable && preFlip) {
                                    // Non-Vader character when Vader isn't on table yet
                                    // Inquisitors get a lighter penalty since they help with battle destiny
                                    if (deployingIsInquisitor) {
                                        float inqPenalty = -80.0f;
                                        action.addReasoning("V25 HUNT DOWN: Inquisitor OK but save Force for Vader first!", inqPenalty);
                                        logger.warn("V25 HUNT DOWN: {} is Inquisitor — mild penalty while Vader not on table",
                                            decisionText);
                                    } else {
                                        float nonVaderPenalty = -200.0f;
                                        action.addReasoning("V25 HUNT DOWN: SAVE FORCE FOR VADER! He must be deployed first!", nonVaderPenalty);
                                        logger.warn("V25 HUNT DOWN: {} is NOT Vader — heavy penalty (-200) to save Force for Vader",
                                            decisionText);
                                    }
                                }
                            } catch (Exception e) {
                                logger.debug("V25 HUNT DOWN: Error in Hunt Down scoring: {}", e.getMessage());
                            }
                        }

                        // === V25: CLOUD CITY ABILITY-BASED SPREAD STRATEGY (TDIGWATT) ===
                        // When TDIGWATT is active, spreading across Cloud City locations maximizes:
                        //   - Cloud City Occupation: +1 damage per CC location occupied
                        //   - Dark Deal: +1 to each force drain at CC locations
                        //   - Force drains at each occupied location
                        // V25: Use ABILITY (not character count) to decide when a site is secure.
                        // ~6 ability = can draw battle destiny and hold the site.
                        // Vader alone (ability 6-7) can hold a site. Lando alone (ability 2) cannot.
                        // V24.15: Skip CC spread scoring for spies — they don't contribute while undercover
                        if (isCharacter && !earlySpyDetected && locObjAnalyzer != null && locObjAnalyzer.isAnalyzed()
                            && locObjAnalyzer.needsBespinSystemPresence()
                            && locObjAnalyzer.isObjectiveRelevantLocation(title)) {
                            try {
                                final float ABILITY_SECURE_THRESHOLD = 6.0f;

                                // Get our ability at THIS location
                                float ourAbilityHere = game.getModifiersQuerying().getTotalAbilityAtLocation(
                                    gameState, playerId, location);

                                // V24.13: Check if Lando is alone at this location — he's a high-value target!
                                boolean landoAloneHere = false;
                                int ourCharsAtThisLoc = 0;
                                java.util.List<PhysicalCard> cardsAtLoc = gameState.getCardsAtLocation(location);
                                if (cardsAtLoc != null) {
                                    for (PhysicalCard c : cardsAtLoc) {
                                        if (c != null && playerId.equals(c.getOwner()) &&
                                            c.getBlueprint() != null &&
                                            c.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                            ourCharsAtThisLoc++;
                                            String charTitle = c.getTitle();
                                            if (ourCharsAtThisLoc == 1 && charTitle != null &&
                                                charTitle.toLowerCase(java.util.Locale.ROOT).contains("lando")) {
                                                landoAloneHere = true;
                                            }
                                        }
                                    }
                                }
                                // Reset lando flag if more than 1 char
                                if (ourCharsAtThisLoc > 1) landoAloneHere = false;

                                // Check ALL objective-relevant locations for ability status
                                int locsEmpty = 0;
                                int locsInsecure = 0;  // Have presence but ability < threshold
                                int locsSecure = 0;    // Ability >= threshold
                                java.util.List<PhysicalCard> allLocs = gameState.getLocationsInOrder();
                                for (PhysicalCard checkLoc : allLocs) {
                                    if (checkLoc == null || checkLoc.getTitle() == null) continue;
                                    if (!locObjAnalyzer.isObjectiveRelevantLocation(checkLoc.getTitle())) continue;
                                    float abilityThere = game.getModifiersQuerying().getTotalAbilityAtLocation(
                                        gameState, playerId, checkLoc);
                                    if (abilityThere <= 0) {
                                        locsEmpty++;
                                    } else if (abilityThere < ABILITY_SECURE_THRESHOLD) {
                                        locsInsecure++;
                                    } else {
                                        locsSecure++;
                                    }
                                }

                                // Apply ability-based spread scoring
                                if (landoAloneHere) {
                                    // V24.13: LANDO IS ALONE — critical priority to reinforce!
                                    action.addReasoning("V24.13 LANDO SUPPORT: Lando is ALONE here — MUST reinforce!", 250.0f);
                                    logger.warn("V24.13 LANDO ALONE: {} — Lando needs backup! (+250)", title);
                                } else if (ourAbilityHere > 0 && ourAbilityHere < ABILITY_SECURE_THRESHOLD) {
                                    // REINFORCE: This location has presence but isn't secure yet
                                    float deficit = ABILITY_SECURE_THRESHOLD - ourAbilityHere;
                                    float reinforceBonus = 100.0f + (deficit * 15.0f);
                                    action.addReasoning("V25 REINFORCE: Site has ability " + String.format("%.0f", ourAbilityHere)
                                        + " — need " + String.format("%.0f", ABILITY_SECURE_THRESHOLD) + " to hold!", reinforceBonus);
                                    logger.warn("V25 ABILITY: {} has ability {} (need {}) — REINFORCE (+{})",
                                        title, String.format("%.0f", ourAbilityHere), String.format("%.0f", ABILITY_SECURE_THRESHOLD), (int)reinforceBonus);
                                } else if (ourAbilityHere <= 0) {
                                    // EMPTY: New unoccupied CC location — only spread if other locations are secure
                                    if (locsInsecure > 0) {
                                        // Other locations need ability reinforcement first
                                        action.addReasoning("V25 SPREAD: New CC location but " + locsInsecure + " site(s) need more ability first", 40.0f);
                                        logger.info("V25 ABILITY: {} unoccupied but {} sites insecure — moderate priority", title, locsInsecure);
                                    } else {
                                        // All occupied locations are secure — spread to new!
                                        action.addReasoning("V25 SPREAD: All held sites have 6+ ability — spread for more occupation damage!", 120.0f);
                                        logger.warn("V25 ABILITY: {} unoccupied, all {} sites secure — SPREAD (+120)", title, locsSecure);
                                    }
                                } else {
                                    // SECURE: This location already has 6+ ability
                                    if (locsInsecure > 0 || locsEmpty > 0) {
                                        // Other locations need attention — mild penalty for over-stacking
                                        action.addReasoning("V25 SECURE: Site already has " + String.format("%.0f", ourAbilityHere)
                                            + " ability — other sites need help", -40.0f);
                                        logger.info("V25 ABILITY: {} has ability {} (secure), {} sites need attention", title,
                                            String.format("%.0f", ourAbilityHere), (locsInsecure + locsEmpty));
                                    } else {
                                        // ALL locations are secure — extra stacking OK
                                        action.addReasoning("V25 SECURE: All CC sites have 6+ ability — extra defense OK", 20.0f);
                                    }
                                }
                            } catch (Exception e) {
                                logger.debug("V25: Could not evaluate CC ability spread: {}", e.getMessage());
                            }
                        }

                        // =====================================================
                        // V59 UNIVERSAL SPY SCORING — runs regardless of ObjectiveAnalyzer state.
                        // FIXES Issue #1 from peaceful-pike replay: Jyn Erso deployed to empty
                        // Upper Chamber (+165) instead of Entrance where opponent drains 2/turn,
                        // because the spy-aware scoring at line ~2201 was trapped inside
                        // `if (deployObjAnalyzer.isAnalyzed())`. When ChosenOne's deck doesn't have
                        // an analyzed objective (e.g., "Like My Father Before Me" variants),
                        // spy placement fell back to generic icon-count scoring which ties every BG.
                        // This block scores spies BEFORE the objective-gated block and sets a flag
                        // to prevent double-counting downstream.
                        // =====================================================
                        boolean spyScoringApplied = false;
                        if (earlySpyDetected && game != null && location != null) {
                            try {
                                float ourPwr = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    game.getGameState(), location, playerId, false, false);
                                String opp = game.getOpponent(playerId);
                                float oppPwr = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    game.getGameState(), location, opp, false, false);

                                if (oppPwr > 0 && ourPwr == 0) {
                                    // BEST: Opponent actively draining/occupying — block them!
                                    action.addReasoning("V59 SPY UNIVERSAL: Opp has power " + (int)oppPwr
                                        + ", we have 0 — IDEAL spy site, blocks their drain!", 600.0f);
                                    logger.warn("V59 SPY UNIVERSAL: {} — opp {}, us 0 — IDEAL! (+600)", title, (int)oppPwr);
                                } else if (oppPwr > 0 && ourPwr > 0) {
                                    // Both sides — spy would block our own drain while undercover
                                    action.addReasoning("V59 SPY UNIVERSAL: Both sides present at " + title
                                        + " — spy blocks OWN drain while undercover", -200.0f);
                                    logger.warn("V59 SPY UNIVERSAL: {} — opp {}, us {} — hurts us (-200)",
                                        title, (int)oppPwr, (int)ourPwr);
                                } else if (oppPwr == 0 && ourPwr > 0) {
                                    // Only us — spy blocks OUR drain, catastrophic
                                    action.addReasoning("V59 SPY UNIVERSAL: Only we have presence at " + title
                                        + " — spy would block OWN drain!", -2000.0f);
                                    logger.warn("V59 SPY UNIVERSAL: {} — only us {} — BLOCKED (-2000)",
                                        title, (int)ourPwr);
                                } else {
                                    // Empty — no drain to block
                                    action.addReasoning("V59 SPY UNIVERSAL: " + title
                                        + " is empty — no drain to block", -300.0f);
                                    logger.warn("V59 SPY UNIVERSAL: {} — empty, wasted spy (-300)", title);
                                }
                                spyScoringApplied = true;
                            } catch (Exception e) {
                                logger.debug("V59 SPY UNIVERSAL: Error: {}", e.getMessage());
                            }
                        }

                        // =====================================================
                        // CRITICAL: Check power at location
                        // Don't deploy characters to contested locations we're losing!
                        // V22: Prefer own objective locations over opponent locations
                        // V24.15: EXEMPT SPIES from contest penalties!
                        // Spies deploy undercover — they don't fight battles.
                        // Contest penalty is meaningless for them. Their scoring is
                        // handled by the V24.14B spy scoring block below.
                        // =====================================================
                        if (isCharacter && game != null && !earlySpyDetected) {
                            try {
                                float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    game.getGameState(), location, playerId, false, false);
                                String opponent = game.getOpponent(playerId);
                                float theirPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    game.getGameState(), location, opponent, false, false);

                                if (theirPower > 0) {
                                    float powerDiff = theirPower - ourPower;
                                    if (ourPower < theirPower) {
                                        // V22.3: Contested penalty SCALES with how badly we're losing
                                        // Must be strong enough to override objective bonus (+200)
                                        // when deploying would just feed the opponent cards
                                        float contestPenalty = -80.0f;
                                        if (powerDiff > 5) contestPenalty = -150.0f;   // Significantly outgunned
                                        if (powerDiff > 10) contestPenalty = -250.0f;  // Massively outgunned — overrides obj bonus
                                        if (powerDiff > 15) contestPenalty = -350.0f;  // Suicide — hard no

                                        // Check if deploying THIS card would actually close the gap meaningfully
                                        Float deployPower = (blueprint != null && blueprint.hasPowerAttribute()) ? blueprint.getPower() : null;
                                        float addedPower = (deployPower != null) ? deployPower : 0;
                                        if (addedPower > 0 && (ourPower + addedPower) >= theirPower) {
                                            // This character would tip the balance — reduce penalty
                                            contestPenalty = Math.min(contestPenalty + 100.0f, -20.0f);
                                            action.addReasoning("V22.3: Would tip balance at contested location (" +
                                                (int)(ourPower + addedPower) + " vs " + (int)theirPower + ")", 0.0f);
                                        }

                                        // V22.7: If this is an objective-critical location, reduce the
                                        // contest penalty — we NEED to fight here even at a disadvantage
                                        if (locObjAnalyzer != null && locObjAnalyzer.isAnalyzed()
                                            && locObjAnalyzer.isObjectiveRelevantLocation(title)) {
                                            float objOverride = Math.min(200.0f, Math.abs(contestPenalty) * 0.6f);
                                            action.addReasoning("V22.7: Objective-critical location — must contest!", objOverride);
                                            logger.warn("V22.7 OBJ CONTEST: {} is objective-critical — reducing contest penalty by {}",
                                                title, (int)objOverride);
                                        }

                                        if (plannedTargetId == null || !cardId.equals(plannedTargetId)) {
                                            action.addReasoning("CONTESTED & LOSING (" + (int)ourPower + " vs " + (int)theirPower +
                                                " power, gap=" + (int)powerDiff + ")", contestPenalty);
                                            logger.warn("V22.3 CONTEST: {} at {} losing {}-vs-{} penalty={}",
                                                title, (int)ourPower, (int)theirPower, contestPenalty);
                                        }
                                    } else if (ourPower > theirPower + 4) {
                                        // We're already winning big - don't need more here
                                        action.addReasoning("Already winning big here", -20.0f);
                                    } else if (ourPower >= theirPower) {
                                        // We're winning or tied - reinforce is reasonable
                                        action.addReasoning("Can reinforce winning position", 10.0f);
                                    }
                                } else {
                                    // No opponent power - uncontested, good target
                                    if (ourPower == 0) {
                                        action.addReasoning("Establish at empty location", 20.0f);
                                    }
                                }

                                // V22.4 + V67bn: LONELY CHARACTER REINFORCEMENT.
                                // V67bn extends the rule beyond ourPower<=5 to catch
                                // STRONG-but-outgunned solo chars (Vader, Sidious, etc.).
                                // See Rando V67bn comment for full rationale.
                                if (ourPower > 0) {
                                    int ourCharsHere = 0;
                                    try {
                                        java.util.List<PhysicalCard> cardsHere = gameState.getCardsAtLocation(location);
                                        if (cardsHere != null) {
                                            for (PhysicalCard c : cardsHere) {
                                                if (c != null && playerId.equals(c.getOwner())) {
                                                    SwccgCardBlueprint cBp = c.getBlueprint();
                                                    if (cBp != null && cBp.getCardCategory() == CardCategory.CHARACTER) {
                                                        ourCharsHere++;
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        // Fallback
                                    }
                                    boolean v67bnOutgunned = (theirPower - ourPower) >= 4f;
                                    // V67bu (mirror of Rando, 2026-05-11): extended to ourCharsHere >= 1
                                    // with escape-route check. See Rando V67bu comment for rationale.
                                    boolean v67buCanEscape = false;
                                    if (ourCharsHere >= 1 && v67bnOutgunned) {
                                        try {
                                            String locTitleLower = location.getTitle() != null
                                                ? location.getTitle().toLowerCase(java.util.Locale.ROOT) : "";
                                            String planetPrefix = locTitleLower.contains(":")
                                                ? locTitleLower.substring(0, locTitleLower.indexOf(":")).trim()
                                                : locTitleLower;
                                            for (PhysicalCard pc : gameState.getAllPermanentCards()) {
                                                if (pc == null || pc.getBlueprint() == null) continue;
                                                if (!playerId.equals(pc.getOwner())) continue;
                                                if (pc.getZone() == null || !pc.getZone().isInPlay()) continue;
                                                CardCategory pcCat = pc.getBlueprint().getCardCategory();
                                                if (pcCat == CardCategory.CHARACTER || pcCat == CardCategory.STARSHIP) {
                                                    PhysicalCard pcLoc = pc.getAtLocation();
                                                    if (pcLoc == null || pcLoc == location) continue;
                                                    String pcLocTitle = pcLoc.getTitle();
                                                    if (pcLocTitle == null) continue;
                                                    String pcLocLower = pcLocTitle.toLowerCase(java.util.Locale.ROOT);
                                                    if (!planetPrefix.isEmpty() && pcLocLower.startsWith(planetPrefix)) {
                                                        v67buCanEscape = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        } catch (Exception eEsc) {
                                            logger.debug("V67bu escape-check error: {}", eEsc.getMessage());
                                        }
                                    }
                                    if (ourCharsHere >= 1 && v67bnOutgunned && !v67buCanEscape) {
                                        action.addReasoning(String.format(
                                            "V67bn REINFORCE OUTGUNNED (Braveheart): %d friendly char(s) at %s (our %d vs opp %d, deficit %d) — NO ESCAPE, DEPLOY HERE!",
                                            ourCharsHere, title, (int)ourPower, (int)theirPower, (int)(theirPower-ourPower)),
                                            800.0f);
                                        logger.warn("V67bn REINFORCE OUTGUNNED: dest={} chars={} our={} opp={} deficit={} no-escape → +800",
                                            title, ourCharsHere, (int)ourPower, (int)theirPower, (int)(theirPower-ourPower));
                                    } else if (ourCharsHere >= 1 && v67bnOutgunned && v67buCanEscape) {
                                        logger.info("V67bu ESCAPE AVAILABLE at {} — skip reinforce", title);
                                    } else if (ourPower <= 5f && ourCharsHere == 1) {
                                        float reinforceBonus = 80.0f;
                                        if (theirPower > 0) reinforceBonus = 120.0f;
                                        action.addReasoning("V22.4: REINFORCE SOLO CHARACTER (power " +
                                            (int)ourPower + ") - don't leave them alone!", reinforceBonus);
                                        logger.info("V22.4 REINFORCE: Solo char at {} (power {}), opponent power {}, bonus={}",
                                            title, (int)ourPower, (int)theirPower, reinforceBonus);
                                    } else if (ourCharsHere == 2 && theirPower > ourPower * 1.5f) {
                                        action.addReasoning("V22.4: Reinforce outnumbered pair at " + title, 50.0f);
                                    }
                                }

                                // === V24.3B: DR. EVAZAN WEAPON COMBO — DEPLOY LOCATION PREFERENCE ===
                                // Deploy Evazan to sites with weapon chars, and weapon chars to sites with Evazan.
                                // Evazan converts weapon hits into immediate character loss — devastating combo.
                                if (isCharacter && decisionText != null) {
                                    boolean deployingEvazan = decisionText.contains("evazan");
                                    boolean deployingWeaponChar = (decisionText.contains("maul") && decisionText.contains("lightsaber"))
                                        || (decisionText.contains("vader") && decisionText.contains("lightsaber"))
                                        || (decisionText.contains("mara") && decisionText.contains("lightsaber"))
                                        || (decisionText.contains("jade") && decisionText.contains("lightsaber"))
                                        || (decisionText.contains("aurra") && decisionText.contains("blaster"))
                                        || (decisionText.contains("sing") && decisionText.contains("blaster"));

                                    if (deployingEvazan || deployingWeaponChar) {
                                        // Scan cards at this location for combo partner
                                        boolean comboPartnerHere = false;
                                        try {
                                            java.util.List<PhysicalCard> cardsAtLoc = gameState.getCardsAtLocation(location);
                                            if (cardsAtLoc != null) {
                                                for (PhysicalCard c : cardsAtLoc) {
                                                    if (c == null || !playerId.equals(c.getOwner())) continue;
                                                    String cTitle = c.getTitle();
                                                    if (cTitle == null) continue;
                                                    String cTitleLower = cTitle.toLowerCase();

                                                    if (deployingEvazan) {
                                                        // Looking for weapon characters
                                                        if ((cTitleLower.contains("maul") && cTitleLower.contains("lightsaber"))
                                                            || (cTitleLower.contains("vader") && cTitleLower.contains("lightsaber"))
                                                            || (cTitleLower.contains("mara") && cTitleLower.contains("lightsaber"))
                                                            || (cTitleLower.contains("jade") && cTitleLower.contains("lightsaber"))
                                                            || (cTitleLower.contains("aurra") && cTitleLower.contains("blaster"))
                                                            || (cTitleLower.contains("sing") && cTitleLower.contains("blaster"))) {
                                                            comboPartnerHere = true;
                                                            break;
                                                        }
                                                    } else {
                                                        // Deploying weapon char — looking for Evazan
                                                        if (cTitleLower.contains("evazan")) {
                                                            comboPartnerHere = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (Exception e) { /* ignore */ }

                                        if (comboPartnerHere) {
                                            action.addReasoning("V24.3 EVAZAN COMBO: Deploy here — combo partner at this site for weapon kill combo!", 200.0f);
                                            logger.warn("V24.3 EVAZAN COMBO: {} — combo partner found at {} (+200)", decisionText, title);
                                        }
                                    }
                                }

                                // === V24.10: LANDO DEPLOY LOCATION — PREFER DINING ROOM ===
                                // Dining Room is Lando's optimal deploy site:
                                // - I'm Sorry pulls Dining Room which chain-pulls Lando
                                // - Lando at Dining Room establishes occupation for TDIGWATT engine
                                // - Lando can then MOVE to other CC sites at start of control phase
                                if (isCharacter && decisionText != null && decisionText.contains("lando")) {
                                    String locTitleLower = title != null ? title.toLowerCase(java.util.Locale.ROOT) : "";
                                    if (locTitleLower.contains("dining room")) {
                                        action.addReasoning("V24.10 LANDO TO DINING ROOM: Optimal deploy — establishes occupation, can move to other sites!", 300.0f);
                                        logger.warn("V24.10 LANDO: Dining Room +300 — ideal deploy location for Lando!");
                                    } else if (locTitleLower.contains("cloud city") || locTitleLower.contains("upper walkway")
                                               || locTitleLower.contains("carbonite") || locTitleLower.contains("security tower")
                                               || locTitleLower.contains("platform") || locTitleLower.contains("lower corridor")) {
                                        // Other CC sites are OK but not ideal — Lando can move here later
                                        action.addReasoning("V24.10 LANDO: CC site but not Dining Room — Lando can move here later, deploy to Dining Room first!", -50.0f);
                                        logger.warn("V24.10 LANDO: {} is CC but not Dining Room — mild penalty (-50)", title);
                                    }

                                    // === V25: LANDO ALONE PROTECTION ===
                                    // NEVER deploy Lando to a CC site where he'd be alone and unprotected.
                                    // Lando alone (ability 2, power 3) is an easy kill for any Jedi.
                                    // Rey killed Lando alone EVERY TURN in testing — catastrophic Force losses.
                                    // Only deploy Lando if:
                                    //   (a) friendlies already at the site, OR
                                    //   (b) we have characters in hand we can deploy alongside him, OR
                                    //   (c) Turn 1 and opponent has no CC presence yet (early establish OK)
                                    if (game != null && gameState != null) {
                                        try {
                                            int friendlyCharsAtSite = 0;
                                            int opponentCharsAtAnyCCSite = 0;
                                            int charsInHand = 0;
                                            String opponentId = gameState.getOpponent(playerId);

                                            // Count friendlies AND opponents at THIS location
                                            int opponentCharsAtThisSite = 0;
                                            java.util.List<PhysicalCard> siteCards = gameState.getCardsAtLocation(location);
                                            if (siteCards != null) {
                                                for (PhysicalCard sc : siteCards) {
                                                    if (sc != null && sc.getBlueprint() != null &&
                                                        sc.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                                        if (playerId.equals(sc.getOwner())) {
                                                            friendlyCharsAtSite++;
                                                        } else if (opponentId != null && opponentId.equals(sc.getOwner())) {
                                                            opponentCharsAtThisSite++;
                                                        }
                                                    }
                                                }
                                            }

                                            // Count characters in hand (potential protectors)
                                            java.util.List<PhysicalCard> hand = gameState.getHand(playerId);
                                            if (hand != null) {
                                                for (PhysicalCard hc : hand) {
                                                    if (hc != null && hc.getBlueprint() != null &&
                                                        hc.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                                        charsInHand++;
                                                    }
                                                }
                                            }

                                            // Check opponent presence at CC sites
                                            for (PhysicalCard checkLoc : gameState.getLocationsInOrder()) {
                                                if (checkLoc == null || checkLoc.getTitle() == null) continue;
                                                if (checkLoc == location) continue; // skip this site
                                                String checkLocLower = checkLoc.getTitle().toLowerCase(java.util.Locale.ROOT);
                                                boolean isCCsite = checkLocLower.contains("cloud city") || checkLocLower.contains("upper walkway")
                                                    || checkLocLower.contains("carbonite") || checkLocLower.contains("security tower")
                                                    || checkLocLower.contains("dining room") || checkLocLower.contains("platform")
                                                    || checkLocLower.contains("lower corridor");
                                                if (!isCCsite) continue;
                                                java.util.List<PhysicalCard> ccCards = gameState.getCardsAtLocation(checkLoc);
                                                if (ccCards != null) {
                                                    for (PhysicalCard cc : ccCards) {
                                                        if (cc != null && cc.getOwner() != null) {
                                                            if (opponentId != null && opponentId.equals(cc.getOwner()) &&
                                                                cc.getBlueprint() != null &&
                                                                cc.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                                                opponentCharsAtAnyCCSite++;
                                                            }
                                                            // (could track friendly chars at other CC sites here if needed)
                                                        }
                                                    }
                                                }
                                            }

                                            boolean hasProtection = friendlyCharsAtSite > 0;
                                            boolean canDeployProtector = charsInHand >= 1;
                                            boolean opponentThreatens = (opponentCharsAtAnyCCSite + opponentCharsAtThisSite) > 0;

                                            if (hasProtection) {
                                                // Friendlies at site — Lando is safe
                                                logger.info("V25 LANDO: {} — {} friendlies here — safe to deploy", title, friendlyCharsAtSite);
                                            } else if (!canDeployProtector) {
                                                // Lando would be ALONE and we have NO characters in hand to protect him.
                                                // Even on Turn 1 this is dangerous — opponent deploys right after us.
                                                action.addReasoning("V47 LANDO ALONE BLOCK: No protection at " + title
                                                    + " and no characters in hand — Lando dies alone!", -9999.0f);
                                                logger.warn("V47 LANDO ALONE: {} — no friendlies, no hand chars — HARD BLOCK!", title);
                                            } else if (opponentThreatens) {
                                                // Lando alone but we DO have chars in hand AND opponent at CC
                                                // Mild penalty — we can deploy protectors but it's still risky
                                                action.addReasoning("V25 LANDO CAUTION: Alone at " + title
                                                    + " but " + charsInHand + " chars in hand — deploy protector ASAP!", -100.0f);
                                                logger.warn("V25 LANDO: {} — alone + opponent at CC, but {} chars in hand (-100)",
                                                    title, charsInHand);
                                            } else {
                                                // Lando alone, no opponent at CC, but we have chars in hand
                                                // OK to deploy — we can protect him and opponent hasn't arrived yet
                                                logger.info("V25 LANDO: {} — alone but {} chars in hand and no CC threats — OK", title, charsInHand);
                                            }
                                        } catch (Exception e) {
                                            logger.debug("V25 LANDO ALONE CHECK: Error: {}", e.getMessage());
                                        }
                                    }
                                }

                                // V22/V22.2: Strongly prefer deploying to objective locations
                                // Post-flip: scale required power based on opponent threat
                                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer deployObjAnalyzer =
                                    context.getObjectiveAnalyzer();
                                if (deployObjAnalyzer != null && deployObjAnalyzer.isAnalyzed() && title != null) {
                                    boolean isObjLocation = deployObjAnalyzer.isObjectiveRelevantLocation(title);
                                    boolean isFlipBackLocation = deployObjAnalyzer.isFlipBackProtectionLocation(title);
                                    boolean objectiveIsFlipped = deployObjAnalyzer.isFlipped();

                                    // V24.2E (V24.9/V24.14B fix): Detect undercover spy deployment.
                                    // UNIVERSAL: Check deploying card's blueprint game text for "undercover".
                                    // This works for ALL spy cards without hardcoding names.
                                    // Also uses early detection result from V24.14B if available.
                                    boolean isUndercoverSpy = earlySpyDetected;  // Reuse V24.14B early detection
                                    // Method 1 (UNIVERSAL): Check deploying card's blueprint game text
                                    if (!isUndercoverSpy && deployingBlueprintId != null) {
                                        try {
                                            SwccgCardBlueprint spyCheckBp = getBlueprintFromId(context, deployingBlueprintId);
                                            if (spyCheckBp != null) {
                                                String spyCheckText = spyCheckBp.getGameText();
                                                if (spyCheckText != null && spyCheckText.toLowerCase(java.util.Locale.ROOT).contains("undercover")) {
                                                    isUndercoverSpy = true;
                                                    logger.warn("V24.14B SPY DETECT Method 1: Blueprint game text contains 'undercover'!");
                                                }
                                            }
                                        } catch (Exception e) {
                                            logger.debug("V24.14B SPY DETECT Method 1: Error: {}", e.getMessage());
                                        }
                                    }
                                    // Method 1b (fallback): Decision text keywords
                                    if (!isUndercoverSpy) {
                                        if (decisionText.contains("undercover") || decisionText.contains("as a spy")) {
                                            isUndercoverSpy = true;
                                            logger.warn("V24.14B SPY DETECT Method 1b: Decision text spy keyword!");
                                        }
                                    }
                                    // V67bt (mirror of Rando, 2026-05-11): METHOD 2 REMOVED.
                                    // Heuristic "both sides offered → spy" caused false positives
                                    // on non-spy chars. Methods 1 + 3 (game text "undercover")
                                    // are the correct typed checks. See Rando V67bt comment.
                                    // Method 3: Check deploying card's blueprint game text for "undercover" keyword.
                                    // User confirmed: spy cards have "undercover" in their game text.
                                    // deployingBlueprintId is extracted from the decision text HTML earlier in this method.
                                    if (!isUndercoverSpy && deployingBlueprintId != null) {
                                        try {
                                            SwccgCardBlueprint deployingBp = getBlueprintFromId(context, deployingBlueprintId);
                                            if (deployingBp != null) {
                                                String spyGameText = deployingBp.getGameText();
                                                if (spyGameText != null && spyGameText.toLowerCase(java.util.Locale.ROOT).contains("undercover")) {
                                                    isUndercoverSpy = true;
                                                    logger.info("V24.9 SPY DETECT Method 3: Blueprint game text contains 'undercover' — this is a spy deploy!");
                                                }
                                            }
                                        } catch (Exception e) {
                                            logger.debug("V24.9 SPY DETECT Method 3: Error checking blueprint: {}", e.getMessage());
                                        }
                                    }

                                    if (isUndercoverSpy && !spyScoringApplied) {
                                        // V24.14B: SPY LOCATION SCORING — check WHO has presence.
                                        // Spy blocks force drains for BOTH sides at a location.
                                        // GOOD: Deploy spy where opponent has presence and we DON'T → blocks their drain.
                                        // BAD: Deploy spy where only WE have presence → blocks OUR drain.
                                        // CC/objective locations need same logic — opponent CAN deploy to our CC sites!
                                        // V59: Skipped when spyScoringApplied=true (universal scoring already ran).
                                        float oppPowerHere = 0;
                                        try {
                                            oppPowerHere = game.getModifiersQuerying().getTotalPowerAtLocation(
                                                game.getGameState(), location, opponent, false, false);
                                        } catch (Exception e) { /* ignore */ }

                                        if (oppPowerHere > 0 && ourPower == 0) {
                                            // BEST: Opponent controls, we don't — spy stays undercover, blocks THEIR drain!
                                            // Works at ANY site including CC if opponent moved in.
                                            action.addReasoning("V24.14B SPY: Opponent controls here, we don't — block their force drain!", 300.0f);
                                            logger.warn("V24.14B SPY: {} — opp power {}, our power 0 — IDEAL spy location! (+300)", title, oppPowerHere);
                                        } else if (oppPowerHere > 0 && ourPower > 0) {
                                            // LAST RESORT: Both sides present. Spy would need to break cover
                                            // to contribute as a regular character. Spy is better staying
                                            // undercover blocking drains elsewhere.
                                            if (isObjLocation || isFlipBackLocation) {
                                                // CC site — we already drain here, breaking cover wastes spy AND blocks our drain while undercover
                                                action.addReasoning("V24.14B SPY: Both sides at CC site — spy blocks OUR drain while undercover!", -500.0f);
                                                logger.warn("V24.14B SPY: {} — both sides present at CC — bad for us (-500)", title);
                                            } else {
                                                // Non-CC — spy must break cover to help. Last resort only.
                                                action.addReasoning("V24.14B SPY: Both sides present — would need to break cover (last resort)", -50.0f);
                                                logger.info("V24.14B SPY: {} — both sides, spy would need to break cover (-50)", title);
                                            }
                                        } else if (oppPowerHere == 0 && ourPower > 0) {
                                            // BAD: Only WE have presence — spy blocks OUR drain!
                                            action.addReasoning("V24.14B SPY: Only we have presence — spy blocks OUR drain!", -2000.0f);
                                            logger.warn("V24.14B SPY: {} — only our power {} — spy HURTS us! (-2000)", title, ourPower);
                                        } else {
                                            // Empty location — spy doesn't help either side
                                            if (isObjLocation || isFlipBackLocation) {
                                                // Empty CC site — we want to drain here, not block it with a spy
                                                action.addReasoning("V24.14B SPY: Empty CC site — don't waste spy here!", -300.0f);
                                                logger.warn("V24.14B SPY: {} — empty CC site, spy wastes potential drain (-300)", title);
                                            } else {
                                                action.addReasoning("V24.14B SPY: Empty non-CC location — no drain to block", -100.0f);
                                            }
                                        }
                                    } else if (!isObjLocation && !isFlipBackLocation) {
                                        if (isCharacter && deployObjAnalyzer.needsBespinSystemPresence()) {
                                            // V24: TDIGWATT-specific hard block for non-objective character deploys (non-spies only)
                                            action.addReasoning("V24 TDIGWATT: Do NOT deploy characters to non-Cloud City locations!", -250.0f);
                                            logger.warn("V24 TDIGWATT: Blocking character deploy to non-objective location {} (-250)", title);
                                        }
                                        // Non-objective location: penalize, scale by urgency
                                        boolean objLocationNeedsHelp = false;
                                        float worstDeficit = 0;
                                        java.util.List<PhysicalCard> allLocs = game.getGameState().getLocationsInOrder();
                                        // opponent already declared above in this scope
                                        for (PhysicalCard checkLoc : allLocs) {
                                            if (checkLoc == null) continue;
                                            String checkTitle = checkLoc.getTitle();
                                            if (checkTitle == null) continue;
                                            boolean needsProtection = objectiveIsFlipped
                                                ? deployObjAnalyzer.isFlipBackProtectionLocation(checkTitle)
                                                : deployObjAnalyzer.isObjectiveRelevantLocation(checkTitle);
                                            if (!needsProtection) continue;

                                            float ourPowerThere = game.getModifiersQuerying().getTotalPowerAtLocation(
                                                game.getGameState(), checkLoc, playerId, false, false);
                                            float theirPowerThere = game.getModifiersQuerying().getTotalPowerAtLocation(
                                                game.getGameState(), checkLoc, opponent, false, false);

                                            // V22.2: Dynamic threshold — need MORE power when opponent is strong
                                            // Base threshold 8, plus match opponent power with a margin
                                            float requiredPower = Math.max(8.0f, theirPowerThere + 4.0f);
                                            if (ourPowerThere < requiredPower) {
                                                objLocationNeedsHelp = true;
                                                float deficit = requiredPower - ourPowerThere;
                                                if (deficit > worstDeficit) worstDeficit = deficit;
                                            }
                                        }
                                        if (objLocationNeedsHelp) {
                                            // V22.2: Penalty scales with how badly we need reinforcements
                                            // Post-flip penalty is MUCH stronger — losing locations = losing objective
                                            float penalty = objectiveIsFlipped ? -180.0f : -120.0f;
                                            if (worstDeficit > 6) penalty -= 40.0f;  // Extra urgency if severely outgunned
                                            action.addReasoning("V22.2: Objective locations need fortifying" +
                                                (objectiveIsFlipped ? " (POST-FLIP CRITICAL)" : "") +
                                                " - don't deploy elsewhere", penalty);
                                            logger.warn("V22.2 DEPLOY: Penalizing {} ({}), obj locs need +{} power{}",
                                                title, penalty, (int)worstDeficit,
                                                objectiveIsFlipped ? " [FLIPPED - PROTECT!]" : "");
                                        } else {
                                            float mildPenalty = objectiveIsFlipped ? -60.0f : -40.0f;
                                            action.addReasoning("V22: Non-objective location - prefer own locations", mildPenalty);
                                        }
                                    } else if (objectiveIsFlipped && isFlipBackLocation) {
                                        // V22.2: BONUS for deploying to flip-back protection locations post-flip
                                        action.addReasoning("V22.2 POST-FLIP: Deploying to protect flipped objective!", 60.0f);
                                        logger.warn("V22.2 PROTECT: {} is flip-back protection location - bonus for deploying here", title);
                                    }
                                }
                            } catch (Exception e) {
                                logger.debug("Could not get power at {}: {}", title, e.getMessage());
                            }
                        }

                        // === V67as (Steve, 2026-05-08): SPREAD-AWARE DEPLOY DESTINATION ===
                        //
                        // Mirrors V67aj+V67al (which live in DeployEvaluator) for the
                        // CardSelectionEvaluator path — needed because hand-deploy actions
                        // say "Deploy <character>" with NO location in the action text;
                        // the destination is picked here in evaluateDeployLocation via a
                        // sub-decision. V67aj/V67al never fired for hand-deploys because
                        // their actionText.contains(loc.getTitle()) check always failed.
                        //
                        // Steve's report: 59 of 59 character deploys went to one site
                        // (Hoth: Defensive Perimeter, 3rd Marker). Now scoring per
                        // candidate destination here:
                        //
                        //   stack count: friendly characters at this destination
                        //   power total: sum of friendly character power at destination
                        //
                        // Tiered spread bonus / anti-stack penalty:
                        //   Empty obj-req BG:                 +500
                        //   Empty BG (not obj-req):           +300
                        //   1-2 friendlies + BG:              +100
                        //   3+ friendlies + BG (not obj-req): -300  ← anti-stack
                        //   20-24 friendly power, non-obj:    -200  ← V67al-style
                        //   25-34 friendly power, non-obj:    -400
                        //   35+ friendly power, non-obj:      -700
                        if (game != null && playerId != null) {
                            try {
                                boolean v67asIsBg = game.getModifiersQuerying()
                                    .isBattleground(gameState, location, null);
                                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer asObj =
                                    context.getObjectiveAnalyzer();
                                boolean v67asIsObjReq = asObj != null && asObj.isAnalyzed()
                                    && !asObj.isFlipped()
                                    && asObj.isObjectiveRelevantLocation(title);

                                int v67asStack = 0;
                                float v67asPower = 0f;
                                java.util.List<PhysicalCard> v67asCards =
                                    gameState.getCardsAtLocation(location);
                                if (v67asCards != null) {
                                    for (PhysicalCard pc : v67asCards) {
                                        if (pc == null || pc.getBlueprint() == null) continue;
                                        if (!playerId.equals(pc.getOwner())) continue;
                                        if (pc.getBlueprint().getCardCategory()
                                                != CardCategory.CHARACTER) continue;
                                        v67asStack++;
                                        if (pc.getBlueprint().hasPowerAttribute()) {
                                            Float p = pc.getBlueprint().getPower();
                                            if (p != null) v67asPower += p;
                                        }
                                    }
                                }

                                String v67asLabel = null;
                                float v67asBonus = 0f;
                                if (v67asIsBg && v67asIsObjReq && v67asStack == 0) {
                                    v67asLabel = "OBJ-REQ BG, EMPTY";
                                    v67asBonus = 500f;
                                } else if (v67asIsBg && v67asIsObjReq && v67asStack <= 2) {
                                    v67asLabel = "OBJ-REQ BG, REINFORCE";
                                    v67asBonus = 250f;
                                } else if (v67asIsBg && v67asStack == 0) {
                                    v67asLabel = "BG, OPEN-FRONT";
                                    v67asBonus = 300f;
                                } else if (v67asIsBg && v67asStack <= 2) {
                                    v67asLabel = "BG, REINFORCE";
                                    v67asBonus = 100f;
                                } else if (v67asIsBg) {
                                    v67asLabel = "BG, OVER-STACK";
                                    v67asBonus = -300f;
                                }
                                if (v67asLabel != null) {
                                    action.addReasoning(String.format(
                                        "V67as DEPLOY DEST [%s, stack=%d]: %s",
                                        v67asLabel, v67asStack, title), v67asBonus);
                                    logger.warn("V67as [{}]: dest={} stack={} → {}{}",
                                        v67asLabel, title, v67asStack,
                                        v67asBonus > 0 ? "+" : "", (int) v67asBonus);
                                }

                                // V67as POWER-STACK PENALTY (mirrors V67al, non-objective only)
                                if (!v67asIsObjReq) {
                                    float v67asPwrPenalty = 0f;
                                    String v67asPwrLabel = null;
                                    if (v67asPower >= 35f) {
                                        v67asPwrPenalty = -700f;
                                        v67asPwrLabel = "POWER-STACK CATASTROPHIC";
                                    } else if (v67asPower >= 25f) {
                                        v67asPwrPenalty = -400f;
                                        v67asPwrLabel = "POWER-STACK HEAVY";
                                    } else if (v67asPower >= 20f) {
                                        v67asPwrPenalty = -200f;
                                        v67asPwrLabel = "POWER-STACK MILD";
                                    }
                                    if (v67asPwrLabel != null) {
                                        action.addReasoning(String.format(
                                            "V67as %s: %s already has %.0f friendly power — spread to threaten elsewhere!",
                                            v67asPwrLabel, title, v67asPower), v67asPwrPenalty);
                                        logger.warn("V67as {}: dest={} friendlyPower={} → {}",
                                            v67asPwrLabel, title, (int) v67asPower, (int) v67asPwrPenalty);
                                    }
                                }

                                // V67br (mirror of Rando, 2026-05-11): TURN-BASED SPREAD DISCIPLINE.
                                // Ground/aboard-ship safety distinction:
                                //   - Friendlies at SITES (ground) anchor V67br
                                //   - Friendlies at SYSTEMS (aboard ships) are safe, don't anchor
                                //   - SYSTEM destinations don't get V67br penalty
                                try {
                                    int v67brTurn = gameState.getPlayersLatestTurnNumber(playerId);
                                    boolean v67brDestIsSite = location != null
                                        && location.getBlueprint() != null
                                        && location.getBlueprint().getCardSubtype() == com.gempukku.swccgo.common.CardSubtype.SITE;
                                    if (v67brTurn <= 2 && v67brDestIsSite) {
                                        PhysicalCard v67brConcSite = null;
                                        int v67brMaxCount = 0;
                                        for (PhysicalCard loc : gameState.getLocationsInOrder()) {
                                            if (loc == null || loc.getBlueprint() == null) continue;
                                            if (loc.getBlueprint().getCardSubtype() != com.gempukku.swccgo.common.CardSubtype.SITE) continue;
                                            int v67brCount = 0;
                                            java.util.List<PhysicalCard> cardsAtLoc = gameState.getCardsAtLocation(loc);
                                            if (cardsAtLoc != null) {
                                                for (PhysicalCard pc : cardsAtLoc) {
                                                    if (pc == null || pc.getBlueprint() == null) continue;
                                                    if (!playerId.equals(pc.getOwner())) continue;
                                                    if (pc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                                    v67brCount++;
                                                }
                                            }
                                            if (v67brCount > v67brMaxCount) {
                                                v67brMaxCount = v67brCount;
                                                v67brConcSite = loc;
                                            }
                                        }
                                        if (v67brConcSite != null && !v67brConcSite.equals(location)) {
                                            // V75 (Steve, 2026-05-15): KILL-BOX CHECK. Mirror of Rando V75.
                                            float v75OurPower = 0f;
                                            float v75OppPower = 0f;
                                            try {
                                                java.util.List<PhysicalCard> ccAt = gameState.getCardsAtLocation(v67brConcSite);
                                                if (ccAt != null) {
                                                    for (PhysicalCard cc : ccAt) {
                                                        if (cc == null || cc.getBlueprint() == null) continue;
                                                        if (cc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                                        Float pw = cc.getBlueprint().getPower();
                                                        if (pw == null) continue;
                                                        if (playerId.equals(cc.getOwner())) v75OurPower += pw;
                                                        else v75OppPower += pw;
                                                    }
                                                }
                                            } catch (Exception ex) { /* ignore */ }
                                            boolean v75KillBox = v75OppPower > (v75OurPower + 4f) && v75OppPower > 0;
                                            if (v75KillBox) {
                                                action.addReasoning(String.format(
                                                    "V75 KILL-BOX OVERRIDE: concentration site %s overwhelmed (opp %d > our %d + 4) — spread to fresh site!",
                                                    v67brConcSite.getTitle(), (int) v75OppPower, (int) v75OurPower), 200.0f);
                                                logger.warn("V75 KILL-BOX OVERRIDE: concSite={} opp={} our={} → spread bonus +200",
                                                    v67brConcSite.getTitle(), (int) v75OppPower, (int) v75OurPower);
                                            } else {
                                                float v67brPenalty = (v67brTurn == 1) ? -800.0f : -300.0f;
                                                String v67brLabel = (v67brTurn == 1) ? "TURN 1 NO-SPREAD" : "TURN 2 CAUTIOUS-SPREAD";
                                                action.addReasoning(String.format(
                                                    "V67br %s: concentration site is %s (%d friendly chars there). Deploy with them, not here!",
                                                    v67brLabel, v67brConcSite.getTitle(), v67brMaxCount), v67brPenalty);
                                                logger.warn("V67br {}: dest={} concSite={} ({} chars) → {}",
                                                    v67brLabel, title, v67brConcSite.getTitle(), v67brMaxCount, (int)v67brPenalty);
                                            }
                                        }
                                    }
                                } catch (Exception v67brEx) {
                                    logger.debug("V67br turn-spread check error: {}", v67brEx.getMessage());
                                }

                                // V67bj (mirror of Rando, 2026-05-11): THREAT-AWARE DESTINATION.
                                // See Rando CardSelectionEvaluator V67bj comment for full rationale.
                                try {
                                    String opponentForBj = gameState.getOpponent(playerId);
                                    float oppPowerAtSite = game.getModifiersQuerying()
                                        .getTotalPowerAtLocation(gameState, location, opponentForBj, false, false);
                                    float myPowerAtSite = game.getModifiersQuerying()
                                        .getTotalPowerAtLocation(gameState, location, playerId, false, false);

                                    float deployingPower = 0f;
                                    int deployingCost = 0;
                                    if (deployingBlueprintId != null) {
                                        for (PhysicalCard hc : gameState.getHand(playerId)) {
                                            if (hc != null && hc.getBlueprint() != null
                                                    && deployingBlueprintId.equals(hc.getBlueprintId(true))) {
                                                SwccgCardBlueprint dbp = hc.getBlueprint();
                                                if (dbp.hasPowerAttribute() && dbp.getPower() != null) {
                                                    deployingPower = dbp.getPower();
                                                }
                                                Float dCost = dbp.getDeployCost();
                                                if (dCost != null) deployingCost = dCost.intValue();
                                                break;
                                            }
                                        }
                                    }

                                    final int BATTLE_RESERVE = 2;
                                    int forcePileNow = 0;
                                    try { forcePileNow = gameState.getForcePileSize(playerId); } catch (Exception ignored) { }
                                    int forceLeftForOtherDeploys = Math.max(0, forcePileNow - deployingCost - BATTLE_RESERVE);

                                    float handDeployablePower = 0f;
                                    java.util.List<PhysicalCard> bjHand = gameState.getHand(playerId);
                                    if (bjHand != null) {
                                        // V67bj v3 (mirror of Rando): filter to CHARACTERs first;
                                        // getDeployCost() throws on Effects/Interrupts.
                                        java.util.List<PhysicalCard> bjCharHand = new java.util.ArrayList<>();
                                        for (PhysicalCard hc : bjHand) {
                                            if (hc == null || hc.getBlueprint() == null) continue;
                                            if (deployingBlueprintId != null
                                                    && deployingBlueprintId.equals(hc.getBlueprintId(true))) continue;
                                            if (hc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                            bjCharHand.add(hc);
                                        }
                                        bjCharHand.sort((a, b) -> {
                                            float ca, cb;
                                            try { Float v = a.getBlueprint().getDeployCost(); ca = v != null ? v : 99f; }
                                            catch (Exception ex) { ca = 99f; }
                                            try { Float v = b.getBlueprint().getDeployCost(); cb = v != null ? v : 99f; }
                                            catch (Exception ex) { cb = 99f; }
                                            return Float.compare(ca, cb);
                                        });
                                        for (PhysicalCard hc : bjCharHand) {
                                            SwccgCardBlueprint hBp = hc.getBlueprint();
                                            int c;
                                            try {
                                                Float hCost = hBp.getDeployCost();
                                                c = (hCost != null) ? hCost.intValue() : 0;
                                            } catch (Exception ex) { continue; }
                                            if (c <= forceLeftForOtherDeploys) {
                                                if (hBp.hasPowerAttribute() && hBp.getPower() != null) {
                                                    handDeployablePower += hBp.getPower();
                                                }
                                                forceLeftForOtherDeploys -= c;
                                            }
                                        }
                                    }

                                    float totalMyAvailable = myPowerAtSite + deployingPower + handDeployablePower;
                                    float deficit = oppPowerAtSite - totalMyAvailable;
                                    // V67bu (mirror): V67bj only fires for UNCOMMITTED destinations.
                                    int v67buFriendliesHere = 0;
                                    try {
                                        java.util.List<PhysicalCard> bjCardsHere = gameState.getCardsAtLocation(location);
                                        if (bjCardsHere != null) {
                                            for (PhysicalCard pc : bjCardsHere) {
                                                if (pc == null || pc.getBlueprint() == null) continue;
                                                if (!playerId.equals(pc.getOwner())) continue;
                                                if (pc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                                v67buFriendliesHere++;
                                            }
                                        }
                                    } catch (Exception ignored) { }

                                    if (deficit >= 4f && v67buFriendliesHere == 0) {
                                        action.addReasoning(String.format(
                                            "V67bj DON'T BAIT (uncommitted): %s deficit %.0f (opp %.0f vs my available %.0f). Pick safer site!",
                                            title, deficit, oppPowerAtSite, totalMyAvailable),
                                            -400.0f);
                                        logger.warn("V67bj THREAT BLOCK (uncommitted): dest={} opp={} myAvail={} deficit={} → -400",
                                            title, (int) oppPowerAtSite, (int) totalMyAvailable, (int) deficit);
                                    } else if (deficit >= 4f && v67buFriendliesHere > 0) {
                                        logger.info("V67bj SKIPPED (committed): {} friendlies at {} — V67bn handles",
                                            v67buFriendliesHere, title);
                                    }
                                } catch (Exception bjEx) {
                                    logger.debug("V67bj threat check error: {}", bjEx.getMessage());
                                }
                            } catch (Exception e) {
                                logger.debug("V67as: error scoring deploy destination: {}", e.getMessage());
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    logger.debug("Could not parse cardId: {}", cardId);
                }
            }

            actions.add(action);
        }

        return actions;
    }

    /**
     * Choose force to lose - pick cards we want to lose least.
     */
    private List<EvaluatedAction> evaluateForceLoss(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        String playerId = context.getPlayerId();

        // V21: Count hand size and reserves for hand protection
        int handSize = 0;
        int totalReserves = 0;
        if (gameState != null && playerId != null) {
            try {
                handSize = gameState.getHand(playerId).size();
                totalReserves = gameState.getReserveDeckSize(playerId)
                    + gameState.getUsedPile(playerId).size()
                    + gameState.getForcePileSize(playerId);
            } catch (Exception e) {
                // Fallback
            }
        }

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.UNKNOWN,
                50.0f,
                "Lose force (card " + cardId + ")"
            );

            if (gameState != null) {
                try {
                    PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                    if (card != null) {
                        SwccgCardBlueprint blueprint = card.getBlueprint();
                        String title = card.getTitle();
                        if (title != null) {
                            action.setDisplayText("Lose " + title);
                        }

                        // =======================================================
                        // V25: SIMPLIFIED ZONE-AWARE FORCE LOSS
                        // Simple rule based on life force threshold:
                        //   Life force (reserve + used + force pile) > 10:
                        //     → Lose from RESERVE/FORCE PILE (protect hand for deploying)
                        //   Life force <= 10:
                        //     → Lose from HAND (protect life force from depletion)
                        // =======================================================
                        com.gempukku.swccgo.common.Zone zone = card.getZone();
                        boolean isFromHand = (zone != null && zone.name().contains("HAND"));
                        boolean isFromReserve = (zone != null && zone.name().contains("RESERVE"));
                        boolean isFromForcePile = (zone != null && zone.name().contains("FORCE_PILE"));

                        boolean lifeForceHealthy = (totalReserves > 10);

                        if (isFromReserve) {
                            if (lifeForceHealthy) {
                                // Life force is healthy — reserve losses are OK
                                action.addReasoning("V25 LIFE FORCE HEALTHY (" + totalReserves + "): Reserve loss OK — protect hand for deploying", 30.0f);
                            } else {
                                // Life force is critical — protect reserve!
                                action.addReasoning("V25 LIFE FORCE LOW (" + totalReserves + "): PROTECT reserve — lose from hand!", -300.0f);
                                logger.warn("V25 RESERVE PROTECT: {} in reserve, lifeForce={} — PROTECT (-300)", title, totalReserves);
                            }
                        } else if (isFromForcePile) {
                            if (lifeForceHealthy) {
                                // Healthy — force pile losses acceptable but slightly worse than reserve
                                action.addReasoning("V25 LIFE FORCE HEALTHY: Force pile loss OK", 10.0f);
                            } else {
                                // Low — protect force pile too, it's life force
                                action.addReasoning("V25 LIFE FORCE LOW (" + totalReserves + "): PROTECT force pile!", -200.0f);
                            }
                        } else if (isFromHand) {
                            if (lifeForceHealthy) {
                                // Life force is healthy — protect hand for deploying!
                                action.addReasoning("V25 LIFE FORCE HEALTHY: PROTECT hand — cards needed for deploying!", -100.0f);
                            } else {
                                // Life force is low — hand losses are OK to preserve life force
                                action.addReasoning("V25 LIFE FORCE LOW (" + totalReserves + "): Hand loss OK — preserve life force!", 80.0f);
                                logger.warn("V25 HAND EXPENDABLE: {} from hand, lifeForce={} — prefer hand loss", title, totalReserves);
                            }

                            // V25: CHARACTER PROTECTION IN HAND
                            // Even when losing from hand is preferred (low life force),
                            // characters should be protected over effects/interrupts.
                            // Characters need to get deployed; interrupts/effects are expendable.
                            if (blueprint != null) {
                                CardCategory handCardCategory = blueprint.getCardCategory();
                                if (handCardCategory == CardCategory.CHARACTER) {
                                    action.addReasoning("V25 HAND PROTECT: CHARACTER — needs to be deployed! Lose effects/interrupts first!", -150.0f);
                                    logger.warn("V25 HAND CHAR PROTECT: {} is a CHARACTER — protect from hand loss (-150)", title);
                                } else if (handCardCategory == CardCategory.STARSHIP || handCardCategory == CardCategory.VEHICLE) {
                                    action.addReasoning("V25 HAND PROTECT: Ship/vehicle needs deploying", -80.0f);
                                } else if (handCardCategory == CardCategory.WEAPON || handCardCategory == CardCategory.DEVICE) {
                                    action.addReasoning("V25 HAND: Weapon/device — moderate protection", -40.0f);
                                } else if (handCardCategory == CardCategory.EFFECT) {
                                    action.addReasoning("V25 HAND: Effect — acceptable loss from hand", 30.0f);
                                } else if (handCardCategory == CardCategory.INTERRUPT) {
                                    action.addReasoning("V25 HAND: Interrupt — most expendable from hand", 50.0f);
                                }
                            }
                        }

                        if (blueprint != null) {
                            // Prefer losing low-destiny cards
                            Float destiny = blueprint.getDestiny();
                            if (destiny != null) {
                                if (destiny <= 2) {
                                    action.addReasoning("Low destiny - good to lose", 30.0f);
                                } else if (destiny >= 5) {
                                    action.addReasoning("High destiny - keep for draws", -40.0f);
                                }
                            }

                            // Protect unique cards
                            if (blueprint.getUniqueness() == Uniqueness.UNIQUE) {
                                action.addReasoning("Unique card - protect", -15.0f);
                            }

                            // Don't want to lose priority cards
                            if (title != null && AiPriorityCards.isPriorityCardByTitle(title)) {
                                action.addReasoning("Priority card - protect!", -100.0f);
                            }

                            // =======================================================
                            // V21: HARD BAN on objective-critical cards
                            // These should NEVER be voluntarily lost
                            // =======================================================
                            com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer objAnalyzer = context.getObjectiveAnalyzer();
                            if (objAnalyzer != null && objAnalyzer.isAnalyzed() && title != null) {
                                if (objAnalyzer.isRequiredCardForFlip(title)) {
                                    action.addReasoning("OBJECTIVE CRITICAL - NEVER LOSE!", -9999.0f);
                                    logger.warn("V21 HARD BAN: {} is REQUIRED for flip - score crushed!", title);
                                } else if (objAnalyzer.isPullableCard(title)) {
                                    action.addReasoning("OBJECTIVE PULLABLE - NEVER LOSE!", -9999.0f);
                                    logger.warn("V21 HARD BAN: {} is objective pullable - score crushed!", title);
                                }

                                // V25: HUNT DOWN V — Protect lightsabers!
                                // Vader + lightsaber is the core engine of Hunt Down.
                                // Without lightsabers, Vader can't cancel drain bonuses (back side)
                                // and the Hatred engine can't function. Lightsabers are irreplaceable.
                                if (objAnalyzer.isHuntDownV()) {
                                    String titleLower = title.toLowerCase(java.util.Locale.ROOT);
                                    if (titleLower.contains("lightsaber")) {
                                        action.addReasoning("V25 HUNT DOWN: PROTECT LIGHTSABER — critical for deck engine!", -500.0f);
                                        logger.warn("V25 HUNT DOWN LOSS PROTECT: {} is a lightsaber — HARD PROTECT (-500)", title);
                                    }
                                }
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }

            actions.add(action);
        }

        return actions;
    }

    /**
     * Choose card to forfeit - smart forfeit selection.
     *
     * Priority order (highest first):
     * 1. Hit cards - MUST be forfeited anyway, might as well do it first
     * 2. Pilots on ships - forfeit pilot before ship (ship dying loses pilots too)
     * 3. Low forfeit value cards - satisfy damage efficiently
     * 4. Low power cards - keep high-power cards fighting
     *
     * Avoid:
     * - Ships with pilots aboard (forfeit pilots first!)
     * - High forfeit/power unique characters
     * - Cards with attrition immunity
     *
     * Ported from Python card_selection_evaluator.py _evaluate_forfeit()
     */
    private List<EvaluatedAction> evaluateForfeit(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        String textLower = context.getDecisionText().toLowerCase(java.util.Locale.ROOT);
        boolean isOptional = textLower.contains("if desired");

        // V22.4 FIX: Get remaining damage directly from game state.
        // The decision text is always just "Choose a card from battle to forfeit (if desired)"
        // with NO damage numbers embedded, so text-parsing always returned 0 and optional
        // forfeits were always skipped. Query the battle state directly instead.
        int optionalDamageRemaining = 0;
        int optionalAttritionRemaining = 0;
        if (isOptional) {
            SwccgGame forfeitGame = context.getGame();
            String forfeitPlayerId = context.getPlayerId();
            if (forfeitGame != null && forfeitPlayerId != null) {
                try {
                    optionalDamageRemaining = (int) com.gempukku.swccgo.logic.timing.GuiUtils
                        .getBattleDamageRemaining(forfeitGame, forfeitPlayerId);
                    optionalAttritionRemaining = (int) com.gempukku.swccgo.logic.timing.GuiUtils
                        .getBattleAttritionRemaining(forfeitGame, forfeitPlayerId);
                } catch (Exception e) {
                    logger.debug("Could not read battle damage from game state: {}", e.getMessage());
                }
            }
            logger.info("V22.4 OPTIONAL FORFEIT (game state): isOptional={}, damageRemaining={}, attritionRemaining={}",
                isOptional, optionalDamageRemaining, optionalAttritionRemaining);
        }

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.UNKNOWN,
                50.0f,
                "Forfeit card " + cardId
            );

            // V22.4: Optional forfeit handling — COMPLETELY REWORKED
            // Old bug: ALL optional forfeits were avoided (-150). This meant Rando would
            // NEVER voluntarily forfeit characters to satisfy battle damage, leading to
            // massive hand/reserve losses (Emperor Palpatine not forfeited, losing 16 cards instead)
            //
            // NEW LOGIC: If there's battle damage remaining, optional forfeits are GOOD!
            // A character with forfeit=6 satisfies 6 damage in 1 action vs 6 cards from reserve.
            // Only avoid optional forfeits when there's NO damage to satisfy.
            if (isOptional && optionalDamageRemaining <= 0) {
                // No battle damage remaining — truly optional forfeit, avoid it
                action.addReasoning("Optional forfeit with no damage remaining - avoid", VERY_BAD_DELTA);
                actions.add(action);
                continue;
            } else if (isOptional && optionalDamageRemaining > 0) {
                // V22.4: Battle damage still remaining! Forfeiting is MUCH better than losing from reserve!
                // This character's forfeit value will satisfy multiple points of damage
                if (gameState != null) {
                    try {
                        PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                        if (card != null) {
                            SwccgCardBlueprint bp = card.getBlueprint();
                            Float forfeitVal = (bp != null && bp.hasForfeitAttribute()) ? bp.getForfeit() : null;
                            float fv = (forfeitVal != null) ? forfeitVal : 0;

                            if (fv > 0) {
                                // V67t WASTE-AWARE FORFEIT SCORING:
                                // Steve's rule: "if only need to lose 2 or less force from a battle,
                                // keep characters on location and just lose force from reserves."
                                //
                                // Old formula: efficiencyBonus = fv * 20  (gave Sidious fv=7 a +140
                                // bonus for satisfying 1 damage — wasted 6 forfeit. Lost Sidious to
                                // pay 1 damage instead of losing 1 reserve card.)
                                //
                                // New formula: net = savings*20 - waste*50
                                //   savings = min(fv, damage_remaining)  — efficient damage covered
                                //   waste   = max(0, fv - damage_remaining) — over-payment
                                // Heavy waste penalty (-50/pt) outweighs savings (+20/pt) so high-fv
                                // characters never forfeit for tiny damage.
                                int savings = (int) Math.min(fv, optionalDamageRemaining);
                                int waste = (int) Math.max(0f, fv - optionalDamageRemaining);
                                float efficiencyBonus = savings * 20.0f - waste * 50.0f;
                                if (optionalDamageRemaining > 8 && waste == 0) efficiencyBonus += 50.0f;

                                // V67bh (mirror of Rando, 2026-05-10): SMALL-DAMAGE PROTECTION
                                // FOR VALUABLE UN-HIT CHARACTERS. damage ≤ 3 + fv ≥ 4 + not
                                // hit → -400 to forfeit (prefer reserve/hand loss). V67t
                                // remains as a secondary backstop for low-value chars.
                                boolean v67bhSmallDmg = optionalDamageRemaining <= 3;
                                boolean v67bhValuable = fv >= 4;
                                boolean v67bhNotHit   = !card.isHit();
                                if (v67bhSmallDmg && v67bhValuable && v67bhNotHit) {
                                    efficiencyBonus -= 400.0f;
                                    action.addReasoning(String.format(
                                        "V67bh PROTECT VALUABLE: %s (fv=%d, not hit) — only %d damage, lose from reserve/hand!",
                                        card.getTitle(), (int)fv, optionalDamageRemaining), 0.0f);
                                    logger.warn("V67bh PROTECT VALUABLE: {} fv={} damage={} not-hit → -400",
                                        card.getTitle(), (int)fv, optionalDamageRemaining);
                                } else if (optionalDamageRemaining <= 2 && fv >= 2) {
                                    efficiencyBonus -= 250.0f;
                                    action.addReasoning("V67t SMALL DAMAGE: ≤2 damage — keep character on table, lose from reserve!", 0.0f);
                                    logger.warn("V67t SMALL DAMAGE: {} fv={} damage={} → -250 (prefer reserve loss)",
                                        card.getTitle(), (int)fv, optionalDamageRemaining);
                                }

                                action.addReasoning("V22.4/V67t OPTIONAL FORFEIT (savings=" + savings
                                    + " waste=" + waste + " of " + optionalDamageRemaining + " damage)",
                                    efficiencyBonus);
                                logger.info("V67t OPTIONAL FORFEIT: {} fv={} damage={} → savings={} waste={} bonus={}",
                                    card.getTitle(), (int)fv, optionalDamageRemaining, savings, waste, efficiencyBonus);

                                // V67t: Apply V37 PROTECT here too (was only in non-optional path)
                                Float charPower = bp != null && bp.hasPowerAttribute() ? bp.getPower() : null;
                                Float charAbility = bp != null && bp.hasAbilityAttribute() ? bp.getAbility() : null;
                                if (charPower != null && charPower >= 6 && charAbility != null && charAbility >= 4) {
                                    action.addReasoning(String.format(
                                        "V37/V67t PROTECT: %s (power %.0f, ability %.0f) — keep alive!",
                                        card.getTitle(), charPower, charAbility), -150.0f);
                                }
                            } else {
                                // Zero forfeit value — not worth it
                                action.addReasoning("Optional forfeit but zero forfeit value", -80.0f);
                            }

                            // V22.4: Check objective-critical protection even for optional forfeits
                            String fTitle = card.getTitle();
                            com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer optObjAnalyzer =
                                context.getObjectiveAnalyzer();
                            if (optObjAnalyzer != null && optObjAnalyzer.isAnalyzed() && fTitle != null) {
                                if (optObjAnalyzer.isRequiredCardForFlip(fTitle)) {
                                    action.addReasoning("OBJECTIVE CRITICAL - don't voluntarily forfeit", -9999.0f);
                                } else if (optObjAnalyzer.isPullableCard(fTitle)) {
                                    action.addReasoning("OBJECTIVE PULLABLE - don't voluntarily forfeit", -9999.0f);
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
                actions.add(action);
                continue;  // Skip normal scoring — optional forfeit has its own scoring above
            }

            if (gameState != null) {
                try {
                    PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                    if (card != null) {
                        SwccgCardBlueprint blueprint = card.getBlueprint();
                        String title = card.getTitle();
                        if (title != null) {
                            action.setDisplayText("Forfeit " + title);
                        }

                        // =======================================================
                        // CRITICAL: Hit cards should ALWAYS be forfeited first!
                        // They're already damaged - no reason to keep them around
                        // =======================================================
                        if (card.isHit()) {
                            action.addReasoning("ALREADY HIT - forfeit first!", 150.0f);
                            logger.info("🎯 {} is HIT - prioritizing for forfeit", title);
                        }

                        // =======================================================
                        // CRITICAL: Dead cards (persona already deployed) should
                        // be forfeited - they can never be played anyway!
                        // =======================================================
                        SwccgGame game = context.getGame();
                        String playerId = context.getPlayerId();
                        if (game != null && playerId != null &&
                            AiCardHelper.isDeadCard(card, game, playerId)) {
                            action.addReasoning("☠️ DEAD CARD (persona on table) - forfeit!", 140.0f);
                            logger.info("☠️ {} is a DEAD CARD - prioritizing for forfeit", title);
                        }

                        // =======================================================
                        // Check if this is a pilot attached to a ship
                        // Pilots on ships should be forfeited BEFORE the ship!
                        // =======================================================
                        PhysicalCard attachedTo = card.getAttachedTo();
                        if (attachedTo != null) {
                            SwccgCardBlueprint attachedBlueprint = attachedTo.getBlueprint();
                            if (attachedBlueprint != null) {
                                CardCategory attachedCat = attachedBlueprint.getCardCategory();
                                if (attachedCat == CardCategory.STARSHIP || attachedCat == CardCategory.VEHICLE) {
                                    action.addReasoning("PILOT ON SHIP - forfeit first!", 50.0f);
                                }
                            }
                        }

                        // =======================================================
                        // Check if this is a ship/vehicle with cards aboard
                        // Should NOT be forfeited until pilots are gone!
                        // =======================================================
                        List<PhysicalCard> attachedCards = gameState.getAttachedCards(card);
                        if (attachedCards != null && !attachedCards.isEmpty()) {
                            boolean hasCharacterAboard = false;
                            for (PhysicalCard attached : attachedCards) {
                                if (attached.getBlueprint() != null &&
                                    attached.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                    hasCharacterAboard = true;
                                    break;
                                }
                            }
                            if (hasCharacterAboard) {
                                // V48: NEVER forfeit a ship with crew aboard — you lose the ship
                                // AND all its pilots/passengers. Forfeit individual crew instead.
                                int crewCount = 0;
                                for (PhysicalCard att : attachedCards) {
                                    if (att.getBlueprint() != null &&
                                        att.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                        crewCount++;
                                    }
                                }
                                action.addReasoning(String.format(
                                    "V48 SHIP WITH CREW: %s has %d crew aboard — forfeit crew first, not the ship!",
                                    title, crewCount), -9999.0f);
                                logger.warn("V48 SHIP FORFEIT BLOCK: {} has {} crew — NEVER forfeit ship with crew aboard!",
                                    title, crewCount);
                            }
                        }

                        if (blueprint != null) {
                            // Forfeit value scoring - lower is better to forfeit
                            // CRITICAL: Must check hasForfeitAttribute() first - weapons throw exception!
                            Float forfeit = blueprint.hasForfeitAttribute() ? blueprint.getForfeit() : null;
                            if (forfeit != null) {
                                // Base score: higher for low forfeit, lower for high forfeit
                                // forfeit=0 -> +100, forfeit=7 -> +30, forfeit=10 -> 0
                                float forfeitScore = Math.max(0, 100 - (forfeit * 10));
                                action.addReasoning(
                                    String.format("Forfeit value %.0f", forfeit),
                                    forfeitScore
                                );
                            }

                            // Power scoring - prefer keeping high power
                            if (blueprint.hasPowerAttribute()) {
                                Float power = blueprint.getPower();
                                if (power != null) {
                                    if (power <= 2) {
                                        action.addReasoning("Low power - less valuable", 15.0f);
                                    } else if (power >= 5) {
                                        action.addReasoning("High power - keep fighting", -20.0f);
                                    }
                                }
                            }

                            // Protect unique high-value characters
                            if (blueprint.getUniqueness() == Uniqueness.UNIQUE) {
                                Float ability = blueprint.hasAbilityAttribute() ? blueprint.getAbility() : null;
                                Float power = blueprint.hasPowerAttribute() ? blueprint.getPower() : null;
                                if ((ability != null && ability >= 5) || (power != null && power >= 5)) {
                                    action.addReasoning("Valuable unique character", -25.0f);
                                } else {
                                    action.addReasoning("Unique card", -10.0f);
                                }
                            }

                            // Characters with extra destiny draws are valuable
                            // TODO: Check for destiny draw bonuses when API available
                        }

                        // V21: OBJECTIVE-CRITICAL CARD PROTECTION (forfeit)
                        String fTitle = card.getTitle();
                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer fObjAnalyzer = context.getObjectiveAnalyzer();
                        if (fObjAnalyzer != null && fObjAnalyzer.isAnalyzed() && fTitle != null) {
                            if (fObjAnalyzer.isRequiredCardForFlip(fTitle)) {
                                action.addReasoning("OBJECTIVE CRITICAL - NEVER FORFEIT!", -9999.0f);
                                logger.warn("V21 HARD BAN: {} is REQUIRED for flip - never forfeit!", fTitle);
                            } else if (fObjAnalyzer.isPullableCard(fTitle)) {
                                action.addReasoning("OBJECTIVE PULLABLE - NEVER FORFEIT!", -9999.0f);
                                logger.warn("V21 HARD BAN: {} is objective pullable - never forfeit!", fTitle);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }

            actions.add(action);
        }

        return actions;
    }

    /**
     * Handle combined "Force to lose OR forfeit" decisions.
     *
     * CRITICAL SWCCG RULES:
     * - ATTRITION can ONLY be satisfied by forfeiting cards (not Force loss)
     * - Battle damage can be satisfied by EITHER Force loss OR forfeiting
     *
     * Strategy:
     * - If attrition is remaining, MUST forfeit (prioritize hit cards)
     * - If only battle damage, prefer losing Force (saves cards)
     * - Exception: if we have hit cards, forfeit them first anyway
     *
     * Ported from Python card_selection_evaluator.py _evaluate_force_loss_or_forfeit()
     */
    private List<EvaluatedAction> evaluateForceLossOrForfeit(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        String text = context.getDecisionText();
        String textLower = text.toLowerCase(java.util.Locale.ROOT);

        // Get attrition and damage remaining directly from game state.
        // Text-parsing is unreliable — the decision text does not embed damage counts.
        SwccgGame game = context.getGame();
        String playerId = context.getPlayerId();
        int attritionRemaining = 0;
        int damageRemaining = 0;
        if (game != null && playerId != null) {
            try {
                attritionRemaining = (int) com.gempukku.swccgo.logic.timing.GuiUtils
                    .getBattleAttritionRemaining(game, playerId);
                damageRemaining = (int) com.gempukku.swccgo.logic.timing.GuiUtils
                    .getBattleDamageRemaining(game, playerId);
            } catch (Exception e) {
                logger.debug("Could not read battle damage from game state: {}", e.getMessage());
            }
        }

        logger.info("🎯 Force loss OR forfeit (game state): attrition={}, damage={}", attritionRemaining, damageRemaining);

        // Track if we have any hit cards or dead cards available for forfeit
        boolean hasHitCards = false;
        boolean hasDeadCards = false;
        PhysicalCard bestHitCard = null;
        float bestHitForfeit = Float.MAX_VALUE;
        // Note: game and playerId already declared above for battle state queries

        // First pass: identify hit cards and dead cards
        for (String cardId : context.getCardIds()) {
            if (gameState != null) {
                try {
                    PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                    if (card != null) {
                        if (card.isHit()) {
                            hasHitCards = true;
                            SwccgCardBlueprint bp = card.getBlueprint();
                            // CRITICAL: Check hasForfeitAttribute() first - weapons throw exception!
                            float forfeit = bp != null && bp.hasForfeitAttribute() && bp.getForfeit() != null ? bp.getForfeit() : 0;
                            if (forfeit < bestHitForfeit) {
                                bestHitForfeit = forfeit;
                                bestHitCard = card;
                            }
                        }
                        // Check for dead cards (persona already deployed)
                        if (game != null && playerId != null &&
                            AiCardHelper.isDeadCard(card, game, playerId)) {
                            hasDeadCards = true;
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.UNKNOWN,
                50.0f,
                "Choose " + cardId
            );

            // V22.4: Determine if this is a Force loss option or a Forfeit option
            // OLD BUG: Used cardId.startsWith("fp_") which NEVER matches GEMP's numeric IDs!
            // All cards were treated as forfeit options, and force loss penalty never applied.
            // NEW: Check the card's actual zone — hand/reserve/force pile = force loss, table = forfeit
            boolean isForceLosSOption = false;
            if (gameState != null) {
                try {
                    PhysicalCard zoneCheckCard = gameState.findCardById(Integer.parseInt(cardId));
                    if (zoneCheckCard != null) {
                        com.gempukku.swccgo.common.Zone cardZone = zoneCheckCard.getZone();
                        if (cardZone != null) {
                            String zoneName = cardZone.name();
                            isForceLosSOption = zoneName.contains("HAND") ||
                                zoneName.contains("RESERVE") ||
                                zoneName.contains("FORCE_PILE") ||
                                zoneName.contains("USED_PILE");
                        }
                    }
                } catch (NumberFormatException e) {
                    // Fallback — assume forfeit option
                }
            }

            if (isForceLosSOption) {
                // Force loss option — card from hand/reserve/force pile
                action.setDisplayText("Lose Force from pile");

                // V67be (mirrors Rando, 2026-05-09): V67y REMOVED from combined
                // prompt. V67y was scoped to standalone force-loss prompts; in the
                // battle prompt forfeit-from-site is also offered, and V67y's +500
                // pile bonus was silently dominating V22.3's forfeit-first penalty.
                // V22.3 + V67bd (attrition forfeit bonus) now drive this decision.

                // V25: HUNT DOWN V — Protect lightsabers from Force loss
                if (gameState != null) {
                    try {
                        PhysicalCard lossCard = gameState.findCardById(Integer.parseInt(cardId));
                        if (lossCard != null && lossCard.getTitle() != null) {
                            String lossTitle = lossCard.getTitle().toLowerCase(java.util.Locale.ROOT);
                            com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer lossOA = context.getObjectiveAnalyzer();
                            if (lossOA != null && lossOA.isAnalyzed() && lossOA.isHuntDownV()
                                && lossTitle.contains("lightsaber")) {
                                action.addReasoning("V25 HUNT DOWN: PROTECT LIGHTSABER from loss!", -400.0f);
                                logger.warn("V25 HUNT DOWN COMBINED-LOSS: {} is a lightsaber — PROTECT (-400)", lossCard.getTitle());
                            }
                        }
                    } catch (NumberFormatException e) { /* ignore */ }
                }

                if (attritionRemaining > 0) {
                    // CRITICAL: Can't use Force loss to satisfy attrition!
                    action.addReasoning("CANNOT satisfy attrition with Force loss!", VERY_BAD_DELTA);
                } else if (damageRemaining > 0) {
                    // V22.3: ALWAYS prefer forfeiting characters over losing from hand/reserve!
                    // Forfeiting a character with forfeit=5 satisfies 5 damage with 1 card.
                    // Losing from hand/reserve satisfies only 1 damage per card.
                    // Example: 15 damage, forfeit 2 chars (forfeit 5 each) = 10 satisfied + 5 from hand = 7 cards total
                    // vs losing 15 from hand = 15 cards total. Forfeiting saves 8 cards!
                    if (hasHitCards) {
                        action.addReasoning("V22.3: Have hit cards to forfeit first - much more efficient!", -80.0f);
                    } else if (hasDeadCards) {
                        action.addReasoning("V22.3: Have dead cards to forfeit - they satisfy multiple damage!", -80.0f);
                    } else {
                        // V22.3: PENALIZE force loss — characters satisfy more damage per card
                        // The higher the remaining damage, the worse force loss is
                        float forceLossPenalty = -40.0f;
                        if (damageRemaining > 5) forceLossPenalty = -80.0f;
                        if (damageRemaining > 10) forceLossPenalty = -120.0f;
                        action.addReasoning("V22.3: FORFEIT CHARACTERS FIRST - they cover " +
                            "multiple damage points per card! (" + damageRemaining + " damage left)", forceLossPenalty);
                    }
                }
            } else {
                // Forfeit card option
                if (gameState != null) {
                    try {
                        PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                        if (card != null) {
                            String title = card.getTitle();
                            action.setDisplayText("Forfeit " + (title != null ? title : cardId));

                            // HIT cards get massive priority
                            if (card.isHit()) {
                                action.addReasoning("ALREADY HIT - forfeit immediately!", 200.0f);
                                logger.info("🎯 Prioritizing HIT card for forfeit: {}", title);
                            }

                            // Dead cards (persona already deployed) - high priority to forfeit!
                            // Note: 'game' and 'playerId' are already defined at the start of the method
                            if (game != null && playerId != null &&
                                AiCardHelper.isDeadCard(card, game, playerId)) {
                                action.addReasoning("☠️ DEAD CARD - persona on table, forfeit!", 180.0f);
                                logger.info("☠️ Prioritizing DEAD CARD for forfeit: {}", title);
                            }

                            // FORFEIT EFFICIENCY: A character that covers attrition AND/OR battle damage
                            // in a single forfeit is far more efficient than losing one reserve card
                            // per point of damage. Always forfeit before burning reserve.
                            SwccgCardBlueprint bp = card.getBlueprint();
                            Float forfeitVal = bp != null && bp.hasForfeitAttribute() ? bp.getForfeit() : null;
                            float fv = forfeitVal != null ? forfeitVal : 0;
                            int totalRemaining = attritionRemaining + damageRemaining;

                            if (attritionRemaining > 0 && fv > 0) {
                                // V67bd (mirrors Rando): attrition can ONLY be paid by
                                // forfeit — reserve force can't. So forfeit is unavoidable;
                                // doing it FIRST also absorbs damage that would otherwise
                                // burn reserve. Coverage = min(fv, total). +200 floor + 80/coverage.
                                int coverage = (int) Math.min(fv, totalRemaining);
                                float attritionBonus = 200.0f + (coverage * 80.0f);
                                if (fv >= totalRemaining) {
                                    attritionBonus += 200.0f;
                                    action.addReasoning("V67bd FORFEIT COVERS ALL: attrition+" +
                                        "damage in one shot — forfeit before reserve! (fv=" + (int)fv
                                        + ", total=" + totalRemaining + ")", attritionBonus);
                                    logger.warn("🎯 V67bd FORFEIT WIPES ALL: {} fv={} covers {}/{} total damage → +{}",
                                        title, (int)fv, (int)fv, totalRemaining, attritionBonus);
                                } else {
                                    action.addReasoning("V67bd FORFEIT FOR ATTRITION: fv=" + (int)fv
                                        + " covers " + coverage + " of " + totalRemaining
                                        + " owed (attrition+damage) — must forfeit before losing force!",
                                        attritionBonus);
                                    logger.warn("🎯 V67bd FORFEIT-FIRST: {} fv={} coverage={}/{} → +{}",
                                        title, (int)fv, coverage, totalRemaining, attritionBonus);
                                }
                            } else if (damageRemaining > 0 && fv > 0) {
                                // V67t WASTE-AWARE: same formula as evaluateForfeit.
                                int savings = (int) Math.min(fv, damageRemaining);
                                int waste = (int) Math.max(0f, fv - damageRemaining);
                                float efficiencyBonus = savings * 20.0f - waste * 50.0f;
                                if (damageRemaining > 5 && waste == 0) efficiencyBonus += 50.0f;

                                // V67bh (mirror of Rando): SMALL-DAMAGE PROTECTION.
                                boolean v67bhSmallDmg = damageRemaining <= 3;
                                boolean v67bhValuable = fv >= 4;
                                boolean v67bhNotHit   = !card.isHit();
                                if (v67bhSmallDmg && v67bhValuable && v67bhNotHit) {
                                    efficiencyBonus -= 400.0f;
                                    action.addReasoning(String.format(
                                        "V67bh PROTECT VALUABLE: %s (fv=%d, not hit) — only %d damage, lose from reserve/hand!",
                                        title, (int)fv, damageRemaining), 0.0f);
                                    logger.warn("V67bh PROTECT VALUABLE: {} fv={} damage={} not-hit → -400",
                                        title, (int)fv, damageRemaining);
                                } else if (damageRemaining <= 2 && fv >= 2) {
                                    efficiencyBonus -= 250.0f;
                                    action.addReasoning("V67t SMALL DAMAGE: ≤2 damage — keep character, lose from reserve!", 0.0f);
                                    logger.warn("V67t SMALL DAMAGE: {} fv={} damage={} → -250 (prefer reserve loss)",
                                        title, (int)fv, damageRemaining);
                                }

                                action.addReasoning("V67t FORFEIT (savings=" + savings + " waste=" + waste
                                    + " of " + damageRemaining + " damage)", efficiencyBonus);
                                logger.info("V67t FORFEIT: {} fv={} damage={} → savings={} waste={} bonus={}",
                                    title, (int)fv, damageRemaining, savings, waste, efficiencyBonus);
                            }

                            // Apply standard forfeit scoring
                            SwccgCardBlueprint blueprint = card.getBlueprint();
                            if (blueprint != null) {
                                // CRITICAL: Check hasForfeitAttribute() first - weapons throw exception!
                                Float forfeit = blueprint.hasForfeitAttribute() ? blueprint.getForfeit() : null;
                                if (forfeit != null) {
                                    // V22.3: Low forfeit = less damage covered = less efficient
                                    // High forfeit = more damage covered = more efficient to forfeit
                                    // But we still slightly prefer keeping high-power characters
                                    if (forfeit <= 2) {
                                        action.addReasoning("Low forfeit - less efficient but acceptable", 10.0f);
                                    } else if (forfeit >= 6) {
                                        // High forfeit covers lots of damage, but these are usually strong characters
                                        // Only mild protection — the efficiency bonus above will override when damage is high
                                        action.addReasoning("High forfeit character - efficient but valuable", -10.0f);
                                    }
                                }

                                if (blueprint.getUniqueness() == Uniqueness.UNIQUE) {
                                    action.addReasoning("Unique card - mild protection", -10.0f);
                                }

                            // V21: OBJECTIVE-CRITICAL CARD PROTECTION
                            com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer objAnalyzer =
                                context.getObjectiveAnalyzer();
                            if (objAnalyzer != null && objAnalyzer.isAnalyzed() && title != null) {
                                if (objAnalyzer.isRequiredCardForFlip(title)) {
                                    action.addReasoning("OBJECTIVE CRITICAL - NEVER LOSE!", -9999.0f);
                                    logger.warn("V21 HARD BAN: {} is REQUIRED for flip!", title);
                                } else if (objAnalyzer.isPullableCard(title)) {
                                    action.addReasoning("OBJECTIVE PULLABLE - NEVER LOSE!", -9999.0f);
                                    logger.warn("V21 HARD BAN: {} is objective pullable!", title);
                                }
                            }
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
            }

            actions.add(action);
        }

        return actions;
    }

    /**
     * Extract a number following a pattern in text.
     * E.g., "attrition remaining: 5" -> returns 5
     */
    private int extractNumberAfter(String text, String pattern) {
        int idx = text.indexOf(pattern);
        if (idx >= 0) {
            String afterPattern = text.substring(idx + pattern.length()).trim();
            // Extract first number
            StringBuilder num = new StringBuilder();
            for (char c : afterPattern.toCharArray()) {
                if (Character.isDigit(c)) {
                    num.append(c);
                } else if (num.length() > 0) {
                    break;
                }
            }
            if (num.length() > 0) {
                try {
                    return Integer.parseInt(num.toString());
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        return 0;
    }

    /**
     * Choose a pilot - pick best pilot by ability.
     * Enhanced with deploy cost consideration and matching pilot detection.
     * Ported from Python deploy_evaluator.py _evaluate_simultaneous_pilot_selection
     */
    private List<EvaluatedAction> evaluatePilotSelection(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();

        // Detect if this pilot selection is for Alert My Star Destroyer (AMSD).
        // AMSD deploys a Star Destroyer — only Imperial/First Order pilots belong
        // on capital ships. Non-Imperial pilots like Jango Fett have matching ships
        // that are NOT Star Destroyers (Slave I, etc.), so they will always fail the
        // reserve deck search and waste the action entirely.
        String decisionText = context.getDecisionText() != null
            ? context.getDecisionText().toLowerCase(java.util.Locale.ROOT) : "";
        // V22.7: Broadened AMSD detection. GEMP may present pilot selection with text
        // like "Choose a unique pilot character" without mentioning "star destroyer".
        // If we're in Deploy phase choosing a unique pilot, it's likely AMSD.
        // V24.12: Also detect AMSD by checking if the card is actually on the table,
        // because the decision text for "Choose card from hand" doesn't mention AMSD at all.
        boolean isAmsdPilotChoice = decisionText.contains("alert my star destroyer")
            || decisionText.contains("star destroyer")
            || decisionText.contains("matching starship")
            || decisionText.contains("matching star destroyer")
            || (context.getPhase() == Phase.DEPLOY && decisionText.contains("unique")
                && decisionText.contains("pilot"));

        // V24.12: AMSD-on-table detection — if AMSD is deployed and we're choosing
        // characters during deploy phase, this IS an AMSD pilot pick even if the
        // decision text is generic ("Choose card from hand, or click 'Done' to cancel").
        if (!isAmsdPilotChoice && context.getPhase() == Phase.DEPLOY) {
            com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle pilotOracle = context.getDeckOracle();
            if (pilotOracle != null && pilotOracle.isAnalyzed()) {
                boolean amsdDeployed = pilotOracle.isCardInPlay("Alert My Star Destroyer")
                    || pilotOracle.isCardInPlay("Alert My Star Destroyer!")
                    || pilotOracle.isCardInPlay("Alert My Star Destroyer! (V)");
                if (amsdDeployed) {
                    // Verify at least one choice is a character (not a location/effect)
                    GameState pilotGs = context.getGameState();
                    if (pilotGs != null && context.getCardIds() != null) {
                        for (String cid : context.getCardIds()) {
                            try {
                                PhysicalCard pc = pilotGs.findCardById(Integer.parseInt(cid));
                                if (pc != null && pc.getBlueprint() != null &&
                                    pc.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                    isAmsdPilotChoice = true;
                                    logger.warn("V24.12 AMSD DETECTED: AMSD on table + deploy phase + character choices — forcing AMSD pilot mode!");
                                    break;
                                }
                            } catch (Exception e) { /* skip */ }
                        }
                    }
                }
            }
        }

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.DEPLOY,
                50.0f,
                "Select pilot " + cardId
            );

            if (gameState != null) {
                try {
                    PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                    if (card != null) {
                        SwccgCardBlueprint blueprint = card.getBlueprint();
                        String title = card.getTitle();
                        if (blueprint != null) {
                            action.setDisplayText("Select pilot " + (title != null ? title : cardId));

                            // === V24.10: AMSD PILOT GUARD — PIETT ONLY ===
                            // AMSD should ONLY be used with Piett + Executor.
                            // Block ALL other pilots regardless of matching ships.
                            if (isAmsdPilotChoice) {
                                String pilotLower = (title != null) ? title.toLowerCase(java.util.Locale.ROOT) : "";

                                if (!pilotLower.contains("piett")) {
                                    // NOT Piett — hard block, no exceptions
                                    action.setScore(-9999.0f);
                                    action.addReasoning("V24.10 AMSD BLOCKED: Only Piett may use AMSD — " +
                                        title + " is not allowed!", -9999.0f);
                                    logger.warn("V24.10 AMSD HARD BLOCK: {} is NOT Piett — only Piett + Executor for AMSD!", title);
                                    actions.add(action);
                                    continue;
                                }

                                // It's Piett — verify Executor is in reserve
                                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle oracle = context.getDeckOracle();
                                if (oracle != null && oracle.isAnalyzed()) {
                                    boolean executorInReserve = oracle.isCardInReserve("Executor") ||
                                        oracle.isCardInReserve("Flagship Executor");
                                    if (!executorInReserve) {
                                        action.setScore(-9999.0f);
                                        action.addReasoning("V24.10 AMSD: Piett selected but Executor NOT in reserve!", -9999.0f);
                                        logger.warn("V24.10 AMSD: Piett but Executor not in reserve — HARD BLOCK");
                                        actions.add(action);
                                        continue;
                                    }
                                    // Piett + Executor in reserve — approved!
                                    action.addReasoning("V24.10 AMSD: Piett + Executor in reserve — APPROVED!", 300.0f);
                                    logger.warn("V24.10 AMSD: Piett + Executor in reserve — APPROVED (+300)");
                                } else {
                                    // Oracle unavailable but it's Piett — allow (best guess)
                                    action.addReasoning("V24.10 AMSD: Piett selected (oracle unavailable — allowing)", 200.0f);
                                    logger.warn("V24.10 AMSD: Piett selected, oracle unavailable — allowing (+200)");
                                }
                            }

                            // Prefer high-ability pilots
                            if (blueprint.hasAbilityAttribute()) {
                                Float ability = blueprint.getAbility();
                                if (ability != null) {
                                    float abilityScore = ability * 10.0f;
                                    action.addReasoning("Ability " + ability.intValue(), abilityScore);
                                }
                            }

                            // Prefer pilots that add power
                            if (blueprint.hasPowerAttribute()) {
                                Float power = blueprint.getPower();
                                if (power != null && power >= 3) {
                                    action.addReasoning("Good power bonus (" + power.intValue() + ")", 20.0f);
                                }
                            }

                            // Lower deploy cost is better
                            // V43: Wrap in try-catch — some cards (Interrupts, Effects like
                            // "Hidden Weapons") don't support getDeployCost() and throw
                            // UnsupportedOperationException, crashing the cleanup thread.
                            try {
                                Float deployCost = blueprint.getDeployCost();
                                if (deployCost != null) {
                                    float costScore = Math.max(0, 30 - deployCost * 5);
                                    action.addReasoning("Deploy cost " + deployCost.intValue(), costScore);
                                }
                            } catch (UnsupportedOperationException e) {
                                // Card type doesn't support deployCost — skip
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }

            actions.add(action);
        }

        return actions;
    }

    /**
     * Simultaneous pilot selection - when deploying a ship and choosing which pilot to put aboard.
     * The card_ids are pilot cards in hand, NOT locations.
     * Ported from Python deploy_evaluator.py _evaluate_simultaneous_pilot_selection lines 1193-1273
     */
    private List<EvaluatedAction> evaluateSimultaneousPilotSelection(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        String decisionText = context.getDecisionText();

        // Extract the ship name from decision text
        // Format: "Choose a pilot from hand to simultaneously deploy aboard •Ship Name"
        String shipName = extractShipNameFromText(decisionText);
        logger.info("🚀 Simultaneous pilot selection for {}", shipName != null ? shipName : "unknown ship");

        // Detect if the ship being piloted is a Star Destroyer (capital ship).
        // Only Imperial/First Order characters should pilot Star Destroyers.
        boolean isStarDestroyerDeploy = decisionText != null &&
            decisionText.toLowerCase(java.util.Locale.ROOT).contains("star destroyer");

        // Check deploy plan for a planned pilot for this ship
        String plannedPilotBlueprintId = null;
        DeployPhasePlanner planner = context.getDeployPhasePlanner();
        if (planner != null) {
            DeploymentPlan plan = planner.getCurrentPlan();
            if (plan != null) {
                for (DeploymentInstruction instruction : plan.getInstructions()) {
                    // Check if this instruction is for a pilot boarding a ship
                    String aboardShipName = instruction.getAboardShipName();
                    if (aboardShipName != null && shipName != null &&
                        aboardShipName.toLowerCase().contains(shipName.toLowerCase())) {
                        plannedPilotBlueprintId = instruction.getCardBlueprintId();
                        logger.info("   📋 Plan says pilot: {} (blueprint={})",
                            instruction.getCardName(), plannedPilotBlueprintId);
                        break;
                    }
                }
            }
        }

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.DEPLOY,
                50.0f,
                "Deploy pilot (card " + cardId + ")"
            );

            if (gameState != null) {
                try {
                    PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                    if (card != null) {
                        SwccgCardBlueprint blueprint = card.getBlueprint();
                        String title = card.getTitle();
                        String blueprintId = card.getBlueprintId(true);

                        if (blueprint != null) {
                            action.setDisplayText("Deploy pilot " + (title != null ? title : cardId));

                            // STAR DESTROYER GUARD: Only Imperial/First Order pilots belong
                            // on Star Destroyers. Block others to prevent failed searches.
                            if (isStarDestroyerDeploy) {
                                boolean isImperial = blueprint.hasIcon(com.gempukku.swccgo.common.Icon.IMPERIAL);
                                boolean isFirstOrder = blueprint.hasIcon(com.gempukku.swccgo.common.Icon.FIRST_ORDER);
                                if (!isImperial && !isFirstOrder) {
                                    action.setScore(-500.0f);
                                    action.addReasoning("SD BLOCKED: non-Imperial/FO can't pilot Star Destroyers!", -500.0f);
                                    logger.warn("🚫 SD GUARD: Blocking {} for Star Destroyer — not Imperial or FO", title);
                                    actions.add(action);
                                    continue;
                                } else {
                                    action.addReasoning("SD: Imperial/First Order pilot — valid!", 100.0f);
                                }
                            }

                            // Check if this is the planned pilot
                            if (plannedPilotBlueprintId != null && blueprintId != null &&
                                blueprintId.equals(plannedPilotBlueprintId)) {
                                action.addReasoning("PLANNED pilot for " + shipName, 200.0f);
                                logger.info("   ✅ {} is the PLANNED pilot (+200)", title);
                            } else {
                                // Score based on pilot quality

                                // Lower deploy cost is better (we're paying extra for this)
                                // V43: try-catch for cards that don't support getDeployCost()
                                try {
                                    Float deployCost = blueprint.getDeployCost();
                                    if (deployCost != null) {
                                        float costScore = Math.max(0, 30 - deployCost * 5);
                                        action.addReasoning("Deploy cost " + deployCost.intValue(), costScore);
                                    }
                                } catch (UnsupportedOperationException e) {
                                    // Card type doesn't support deployCost — skip
                                }

                                // Higher ability is better for piloting
                                if (blueprint.hasAbilityAttribute()) {
                                    Float ability = blueprint.getAbility();
                                    if (ability != null) {
                                        float abilityScore = ability * 10.0f;
                                        action.addReasoning("Ability " + ability.intValue(), abilityScore);
                                    }
                                }

                                // Check for matching pilot (pilot name contains ship name)
                                if (title != null && shipName != null) {
                                    String titleLower = title.toLowerCase();
                                    String shipNameLower = shipName.toLowerCase().replace("•", "").trim();
                                    if (titleLower.contains(shipNameLower) ||
                                        shipNameLower.contains(titleLower.replace(" ", ""))) {
                                        action.addReasoning("Matching pilot for " + shipName + "!", 50.0f);
                                        logger.info("   🎯 {} appears to be matching pilot for {}", title, shipName);
                                    }
                                }
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    logger.debug("Could not parse cardId: {}", cardId);
                }
            }

            actions.add(action);
        }

        return actions;
    }

    /**
     * Extract ship name from simultaneous deploy decision text.
     * Format: "...simultaneously deploy aboard •Ship Name" or "...aboard Ship Name"
     */
    private String extractShipNameFromText(String text) {
        if (text == null) return null;

        // Look for "aboard" followed by ship name
        int aboardIdx = text.toLowerCase().indexOf("aboard");
        if (aboardIdx >= 0) {
            String afterAboard = text.substring(aboardIdx + 6).trim();
            // Remove HTML tags if present
            afterAboard = afterAboard.replaceAll("<[^>]+>", " ").trim();
            // Take the first few words (ship names are usually 2-4 words)
            String[] words = afterAboard.split("\\s+");
            StringBuilder shipName = new StringBuilder();
            for (int i = 0; i < Math.min(words.length, 5); i++) {
                if (words[i].isEmpty()) continue;
                if (shipName.length() > 0) shipName.append(" ");
                shipName.append(words[i]);
            }
            return shipName.toString().trim();
        }
        return null;
    }

    /**
     * Evaluate move destination selection.
     * Ported from Python card_selection_evaluator.py _evaluate_move_destination
     *
     * Prefer:
     * - Locations with opponent icons (force drain potential!)
     * - Locations where we have power advantage
     * - Locations with our icons (force generation)
     * Avoid:
     * - Locations where enemy is much stronger
     * - Locations with fewer total icons than alternatives
     */
    private List<EvaluatedAction> evaluateMoveDestination(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        SwccgGame game = context.getGame();
        String playerId = context.getPlayerId();
        Side mySide = context.getSide();

        // Icon bonus constant (same as MoveEvaluator/Python)
        final float ICON_BONUS = 15.0f;

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.MOVE,
                0.0f,  // Start at 0 for move decisions
                "Move to location " + cardId
            );

            if (gameState != null) {
                try {
                    PhysicalCard location = gameState.findCardById(Integer.parseInt(cardId));
                    if (location != null) {
                        String title = location.getTitle();
                        action.setDisplayText("Move to " + (title != null ? title : "location"));

                        // Get power at destination
                        float ourPower = 0;
                        float theirPower = 0;

                        if (game != null && playerId != null) {
                            String opponentId = gameState.getOpponent(playerId);

                            // Calculate our power at destination
                            for (PhysicalCard card : gameState.getCardsAtLocation(location)) {
                                if (card == null) continue;
                                String owner = card.getOwner();
                                SwccgCardBlueprint bp = card.getBlueprint();
                                if (bp == null || !bp.hasPowerAttribute()) continue;

                                Float power = bp.getPower();
                                if (power == null) continue;

                                if (playerId.equals(owner)) {
                                    ourPower += power;
                                } else if (opponentId != null && opponentId.equals(owner)) {
                                    theirPower += power;
                                }
                            }
                        }

                        // === ICON-BASED SCORING ===
                        SwccgCardBlueprint bp = location.getBlueprint();
                        int myIcons = 0;
                        int theirIcons = 0;

                        if (bp != null) {
                            // Get force icons based on our side
                            int lightIcons = bp.getIconCount(Icon.LIGHT_FORCE);
                            int darkIcons = bp.getIconCount(Icon.DARK_FORCE);

                            if (mySide == Side.LIGHT) {
                                myIcons = lightIcons;
                                theirIcons = darkIcons;
                            } else {
                                myIcons = darkIcons;
                                theirIcons = lightIcons;
                            }

                            // Bonus for opponent icons (force drain potential!)
                            if (theirIcons > 0) {
                                float iconScore = theirIcons * ICON_BONUS;
                                action.addReasoning(theirIcons + " opponent icons = force drain potential!", iconScore);
                                logger.debug("Move dest {}: +{} for {} opponent icons",
                                    title, iconScore, theirIcons);
                            }

                            // Smaller bonus for our icons (force generation)
                            if (myIcons > 0) {
                                float iconScore = myIcons * (ICON_BONUS / 2);
                                action.addReasoning(myIcons + " of our icons = force generation", iconScore);
                            }

                            // Penalty for no icons at all
                            int totalIcons = myIcons + theirIcons;
                            if (totalIcons == 0) {
                                action.addReasoning("No icons at location - low value", -10.0f);
                            }

                            // === V67e/V67g EXPECTED FORCE LOSS — TIE-BREAKER + DRAIN-AWARE PENALTY ===
                            // Steve's rule: "When there is a tie for points the default scoring
                            // should re-look at whether the decision will make opponent lose more
                            // or less force. Less force drain should be considered a bad move."
                            // V67g STRENGTHENED: −25 zero-drain wasn't enough to dominate tactical
                            // bonuses — Luke + Leia moved Guest Quarters (drain) → Upper Plaza
                            // Corridor (no drain) then back. Now penalty is much stronger AND a
                            // new MOVE-FROM-DRAIN penalty fires when we're abandoning a draining
                            // site for a non-draining one.
                            float v67eExpectedDrain = theirIcons;
                            try {
                                if (game.getModifiersQuerying().isBattleground(gameState, location, null)) {
                                    v67eExpectedDrain *= 1.25f;
                                }
                            } catch (Exception e) { /* ignore */ }
                            // V67k: exempt transit-staging sites (Underground Corridor)
                            // from V67g drain penalties — Jedi MUST go there to fire transit.
                            String v67kTitleLower = title != null
                                ? title.toLowerCase(java.util.Locale.ROOT) : "";
                            boolean v67kIsTransitStagingSite =
                                v67kTitleLower.contains("underground corridor");

                            if (v67eExpectedDrain > 0) {
                                float v67eBonus = v67eExpectedDrain * 12.0f;
                                action.addReasoning(String.format(
                                    "V67e DRAIN POTENTIAL: drain %.1f at %s = +%.0f opponent force loss",
                                    v67eExpectedDrain, title, v67eBonus), v67eBonus);
                                logger.info("V67e DRAIN POTENTIAL: {} drain={} → +{} (tiebreaker: prefer max drain)",
                                    title, v67eExpectedDrain, (int)v67eBonus);
                            } else if (v67kIsTransitStagingSite) {
                                // V67n: Corridor needs to OUTSCORE other Mapuzo destinations,
                                // not just be exempt from penalty. Other Mapuzo sites have
                                // Dark icons (drain potential), giving them V67e + ICON_BONUS
                                // (~+30) — Corridor with 0 score loses to them. Then Rando
                                // ping-pongs Mining Village ↔ Safehouse and never reaches
                                // Corridor to flip Hidden Path / Fallen Order.
                                // +1500 dominates V67e/ICON_BONUS on other Mapuzo sites and
                                // matches V67l location-pull priority. Only fires when destination
                                // matches "underground corridor" — narrowly scoped.
                                action.addReasoning("V67n TRANSIT STAGING DEST: " + title
                                    + " is the Hidden Path transit hub — Jedi MUST channel through here!",
                                    1500.0f);
                                logger.warn("V67n TRANSIT STAGING DEST: {} → +1500 (dominates other Mapuzo destinations)", title);
                            } else {
                                action.addReasoning("V67g ZERO DRAIN: " + title
                                    + " has no opponent force icons — wasted move!", -200.0f);
                                logger.warn("V67g ZERO DRAIN: {} no drain — strong penalty (-200)", title);
                            }

                            // V67g MOVE-FROM-DRAIN — additional penalty when this is a MOVE
                            // (not deploy) and we're leaving a draining site for a worse one.
                            // V67k EXEMPTION: skip when destination is a transit staging site.
                            try {
                                if (v67kIsTransitStagingSite) {
                                    logger.info("V67k MOVE-FROM-DRAIN exempt: {} is transit staging site", title);
                                } else {
                                String dt = context.getDecisionText() != null
                                    ? context.getDecisionText().toLowerCase(java.util.Locale.ROOT) : "";
                                boolean isMoveDecision = dt.contains("where to move") || dt.contains("move to,");
                                if (isMoveDecision && playerId != null) {
                                    // Find the moving character's current location and that
                                    // location's drain potential — if higher than the destination,
                                    // penalize abandoning it.
                                    String dtForName = context.getDecisionText() != null
                                        ? context.getDecisionText() : "";
                                    java.util.regex.Matcher mvNameMatch = java.util.regex.Pattern.compile(
                                        "value='([^']+)'>").matcher(dtForName);
                                    if (mvNameMatch.find()) {
                                        String mvBp = mvNameMatch.group(1);
                                        // Find this character on the table
                                        for (PhysicalCard cur : gameState.getAllPermanentCards()) {
                                            if (cur == null || cur.getBlueprintId(true) == null) continue;
                                            if (!playerId.equals(cur.getOwner())) continue;
                                            if (!mvBp.equals(cur.getBlueprintId(true))) continue;
                                            PhysicalCard fromLoc = cur.getAtLocation();
                                            if (fromLoc == null || fromLoc == location) break;
                                            SwccgCardBlueprint fromBp = fromLoc.getBlueprint();
                                            if (fromBp == null) break;
                                            int fromTheirIcons = (mySide == Side.LIGHT)
                                                ? fromBp.getIconCount(Icon.DARK_FORCE)
                                                : fromBp.getIconCount(Icon.LIGHT_FORCE);
                                            if (fromTheirIcons > theirIcons) {
                                                int dropAmt = fromTheirIcons - theirIcons;
                                                float v67gPenalty = -250.0f * dropAmt;
                                                action.addReasoning(String.format(
                                                    "V67g MOVE-FROM-DRAIN: leaving %s (drain %d) for %s (drain %d) — losing %d drain!",
                                                    fromLoc.getTitle(), fromTheirIcons, title, theirIcons, dropAmt),
                                                    v67gPenalty);
                                                logger.warn("V67g MOVE-FROM-DRAIN: leaving {} drain {} for {} drain {} → {}",
                                                    fromLoc.getTitle(), fromTheirIcons, title, theirIcons, (int)v67gPenalty);
                                            }
                                            break;
                                        }
                                    }
                                }
                                }  // close else (v67kIsTransitStagingSite exemption)
                            } catch (Exception e) { /* ignore */ }
                        }

                        // === V67au (Steve, 2026-05-08): RETREAT-TO-DRAIN STRATEGY ===
                        //
                        // When Rando is at an over-contested battleground (enemy power
                        // exceeds Rando's), and the candidate move destination is a
                        // SAFE adjacent non-BG with friendly drain icons and no
                        // opponents, this is a 'deploy-then-move-to-drain' play:
                        // Rando deploys characters to a contested BG (because that's
                        // where his deck wants them), then moves them out next turn to
                        // an empty drainable adjacent site. Net effect: avoids battle
                        // suicide AND drains uncontested AND spreads pressure.
                        //
                        // Strict version (Steve's choice): only fire when there's a
                        // CONFIRMED escape route — destination has zero opponents AND
                        // friendly drain icons. Otherwise no bonus (don't reward
                        // arbitrary retreats).
                        try {
                            String dtForRetreat = context.getDecisionText() != null
                                ? context.getDecisionText() : "";
                            java.util.regex.Matcher mvForRetreatMatch = java.util.regex.Pattern.compile(
                                "value='([^']+)'>").matcher(dtForRetreat);
                            if (mvForRetreatMatch.find()) {
                                String retBp = mvForRetreatMatch.group(1);
                                PhysicalCard retFromLoc = null;
                                for (PhysicalCard cur : gameState.getAllPermanentCards()) {
                                    if (cur == null || cur.getBlueprintId(true) == null) continue;
                                    if (!playerId.equals(cur.getOwner())) continue;
                                    if (!retBp.equals(cur.getBlueprintId(true))) continue;
                                    retFromLoc = cur.getAtLocation();
                                    break;
                                }
                                if (retFromLoc != null && retFromLoc != location && game != null) {
                                    String oppId = gameState.getOpponent(playerId);
                                    float fromOppPower = game.getModifiersQuerying()
                                        .getTotalPowerAtLocation(gameState, retFromLoc, oppId, false, false);
                                    float fromOurPower = game.getModifiersQuerying()
                                        .getTotalPowerAtLocation(gameState, retFromLoc, playerId, false, false);
                                    boolean fromIsBg = game.getModifiersQuerying()
                                        .isBattleground(gameState, retFromLoc, null);
                                    boolean fromOverContested = fromIsBg
                                        && fromOppPower > 0
                                        && fromOppPower > fromOurPower;

                                    boolean destIsBg = game.getModifiersQuerying()
                                        .isBattleground(gameState, location, null);
                                    float destOppPower = game.getModifiersQuerying()
                                        .getTotalPowerAtLocation(gameState, location, oppId, false, false);
                                    int destFriendlyDrainIcons = 0;
                                    SwccgCardBlueprint destBp = location.getBlueprint();
                                    if (destBp != null) {
                                        // Friendly drain icons = MY side icons at destination
                                        if (mySide == Side.LIGHT) {
                                            destFriendlyDrainIcons = destBp.getIconCount(Icon.LIGHT_FORCE);
                                        } else {
                                            destFriendlyDrainIcons = destBp.getIconCount(Icon.DARK_FORCE);
                                        }
                                    }

                                    if (fromOverContested && !destIsBg && destOppPower == 0
                                            && destFriendlyDrainIcons > 0) {
                                        action.addReasoning(String.format(
                                            "V67au RETREAT-TO-DRAIN: %s is over-contested (their %0.f vs our %0.f) — move to safe adjacent %s (no opp, %d friendly icons) and drain there!",
                                            retFromLoc.getTitle(), fromOppPower, fromOurPower,
                                            title, destFriendlyDrainIcons), 400.0f);
                                        logger.warn("V67au RETREAT-TO-DRAIN: from={} (opp {}, ours {}) → to {} (non-BG, empty, {} icons) → +400",
                                            retFromLoc.getTitle(), (int) fromOppPower, (int) fromOurPower,
                                            title, destFriendlyDrainIcons);
                                    }
                                }
                            }
                        } catch (Exception e) { logger.debug("V67au error: {}", e.getMessage()); }

                        // === POWER-BASED SCORING ===
                        if (ourPower >= theirPower && theirPower > 0) {
                            action.addReasoning("We have power advantage here", GOOD_DELTA);
                        } else if (theirPower - ourPower <= 2 && theirPower > 0) {
                            action.addReasoning("Can help reinforce here", GOOD_DELTA);
                        } else if (theirPower == 0) {
                            // Unoccupied - good if it has icons
                            if (theirIcons > 0) {
                                action.addReasoning("Unoccupied with opponent icons - force drain!", GOOD_DELTA * 2);
                            } else if (myIcons > 0) {
                                action.addReasoning("Unoccupied with our icons - control", GOOD_DELTA);
                            } else {
                                action.addReasoning("Unoccupied but no icons - low priority", 0.0f);
                            }
                        } else {
                            // Enemy is much stronger - penalty scales with their power
                            float penalty = BAD_DELTA * (theirPower / 2);
                            action.addReasoning("Enemy too strong (" + (int)theirPower + " power)", penalty);
                        }

                        // Bonus for battleground locations
                        if (bp != null) {
                            String titleLower = title != null ? title.toLowerCase() : "";
                            if (titleLower.contains("battleground") || isLikelyBattleground(bp)) {
                                action.addReasoning("Battleground location", 15.0f);
                            }
                        }

                        // === V24.9: PREFER UNOCCUPIED CC SITES (ESCAPE SPY-BLOCKED LOCATIONS) ===
                        // If the destination is an objective-relevant CC site with no opponent presence,
                        // moving here means we can force drain uncontested. Big bonus.
                        {
                            com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer moveObjCheck =
                                context.getObjectiveAnalyzer();
                            if (moveObjCheck != null && moveObjCheck.needsBespinSystemPresence()) {
                                String destTitle = title != null ? title : "";
                                boolean isObjLoc = moveObjCheck.isObjectiveRelevantLocation(destTitle);
                                if (isObjLoc && theirPower == 0 && ourPower == 0) {
                                    // Unoccupied CC site — moving here creates a new drain site!
                                    action.addReasoning("V24.9: Unoccupied CC site — free force drain if we move here!", 200.0f);
                                    logger.info("V24.9: Move dest {} is unoccupied CC — big bonus (+200)", title);
                                } else if (isObjLoc && theirPower == 0 && ourPower > 0) {
                                    // We already have presence — reinforcement is less critical
                                    action.addReasoning("V24.9: CC site with only our presence — already draining", 20.0f);
                                }
                            }
                        }

                        // === V64 POWER-AWARE MOVE DESTINATION — don't send Jedi to their death ===
                        // When transiting Jedi off Mapuzo, avoid sites where the opponent's
                        // total power exceeds what our available Jedi can match. ChosenOne
                        // previously sent Kelleran (power 5) to Jabiim: Starship Hangar
                        // where Grand Inquisitor + Emperor Palpatine sat (combined 13+
                        // power) — instant kill. Hidden Path Jedi are ~6-7 power flipped,
                        // so destinations with opponent power ≥ 8 without our own support
                        // are suicide moves.
                        // FIXES z7qk4ap0b72e4uvm replay (msg 324): Kelleran moved into
                        // Grand Inquisitor + Emperor → Steve won battle at msg 451.
                        // Steve's preferred strategy: drain pressure via split-sites, not
                        // battle initiation into stronger enemies.
                        {
                            com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer v64Obj =
                                context.getObjectiveAnalyzer();
                            boolean v64HiddenPath = v64Obj != null && v64Obj.isAnalyzed()
                                && v64Obj.getObjectiveTitle() != null
                                && v64Obj.getObjectiveTitle().toLowerCase(java.util.Locale.ROOT).contains("hidden path");
                            if (v64HiddenPath && game != null && gameState != null
                                && title != null
                                && !title.toLowerCase(java.util.Locale.ROOT).contains("mapuzo")) {
                                // V65: Tightened threshold from 8 to 7 — Lord Vader at printed
                                // power 7 with DVL slipped through. A lone Jedi vs Vader on
                                // opponent's next-turn deploy+battle phase is a guaranteed loss.
                                float assumedJediPower = 6.0f;
                                float projectedOurPower = ourPower + assumedJediPower;
                                if (theirPower >= 7 && projectedOurPower < theirPower + 2) {
                                    float deathPenalty = -1500.0f;
                                    if (theirPower >= 9) deathPenalty = -1800.0f;
                                    if (theirPower >= 12) deathPenalty = -2500.0f;
                                    action.addReasoning(
                                        "V64 SUICIDE MOVE: " + title + " has enemy power "
                                            + (int)theirPower + " — solo Jedi will DIE on their next turn!",
                                        deathPenalty);
                                    logger.warn("V64 SUICIDE MOVE: {} enemy={} our projected={} — HARD BLOCKED ({})",
                                        title, (int)theirPower, (int)projectedOurPower, (int)deathPenalty);
                                } else if (theirPower == 0) {
                                    // Empty site — excellent drain target
                                    action.addReasoning(
                                        "V64 SAFE DRAIN: " + title + " is empty — Jedi can drain without opposition!",
                                        150.0f);
                                    logger.info("V64 SAFE DRAIN: {} empty — ideal drain destination (+150)", title);
                                } else if (projectedOurPower >= theirPower + 3) {
                                    // We'll have clear power advantage
                                    action.addReasoning(
                                        "V64 FAVORABLE: " + title + " — Jedi arrival gives us power advantage",
                                        80.0f);
                                }
                            }
                        }

                        // === V62 HIDDEN PATH SPLIT-SITE ===
                        // Hidden Path flips when we have 2 Jedi Survivors at 2 DIFFERENT
                        // battleground/opponent sites outside Mapuzo.
                        {
                            com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer v62Obj =
                                context.getObjectiveAnalyzer();
                            boolean onHiddenPath = v62Obj != null && v62Obj.isAnalyzed()
                                && v62Obj.getObjectiveTitle() != null
                                && v62Obj.getObjectiveTitle().toLowerCase(java.util.Locale.ROOT).contains("hidden path")
                                && !v62Obj.isFlipped();
                            if (onHiddenPath && game != null && gameState != null && title != null
                                && !title.toLowerCase(java.util.Locale.ROOT).contains("mapuzo")) {
                                try {
                                    boolean isBGDest = game.getModifiersQuerying().isBattleground(gameState, location, null);
                                    if (isBGDest) {
                                        int ourJediHere = 0;
                                        java.util.List<PhysicalCard> hereCards = gameState.getCardsAtLocation(location);
                                        if (hereCards != null) {
                                            for (PhysicalCard hc : hereCards) {
                                                if (hc != null && playerId.equals(hc.getOwner())
                                                    && hc.getBlueprint() != null
                                                    && hc.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                                    String hcText = hc.getBlueprint().getGameText();
                                                    String hcTitle = hc.getTitle() != null
                                                        ? hc.getTitle().toLowerCase(java.util.Locale.ROOT) : "";
                                                    boolean isJediSurv = (hcText != null
                                                            && hcText.toLowerCase(java.util.Locale.ROOT).contains("jedi survivor"))
                                                        || hcTitle.contains("obi-wan") || hcTitle.contains("kelleran")
                                                        || hcTitle.contains("quinlan") || hcTitle.contains("ahsoka")
                                                        || hcTitle.contains("cal kestis") || hcTitle.contains("cere");
                                                    if (isJediSurv) ourJediHere++;
                                                }
                                            }
                                        }
                                        if (ourJediHere >= 1) {
                                            action.addReasoning(
                                                "V62 SPLIT SITE: Already have " + ourJediHere
                                                    + " Jedi at " + title
                                                    + " — move 2nd Jedi to a DIFFERENT battleground to flip Hidden Path!",
                                                -500.0f);
                                            logger.warn("V62 SPLIT SITE: {} has {} friendly Jedi — penalize duplicate dest (-500)",
                                                title, ourJediHere);
                                        } else {
                                            action.addReasoning(
                                                "V62 SPLIT SITE: No friendly Jedi at " + title
                                                    + " yet — great split-site target for Hidden Path flip!",
                                                200.0f);
                                        }
                                    }
                                } catch (Exception e) { /* ignore */ }
                            }
                        }

                        // === V62 DON'T DILUTE OUR OWN UNDERCOVER SPY ===
                        if (game != null && gameState != null && location != null) {
                            try {
                                java.util.List<PhysicalCard> siteCards = gameState.getCardsAtLocation(location);
                                boolean ourSpyHere = false;
                                if (siteCards != null) {
                                    for (PhysicalCard sc : siteCards) {
                                        if (sc != null && playerId.equals(sc.getOwner())
                                            && sc.isUndercover()) {
                                            ourSpyHere = true;
                                            break;
                                        }
                                    }
                                }
                                boolean movingCardIsSpy = false;
                                String dt = context.getDecisionText() != null
                                    ? context.getDecisionText().toLowerCase(java.util.Locale.ROOT) : "";
                                if (dt.contains("jyn erso") || dt.contains("boushh")
                                    || dt.contains("orrimaarko")) {
                                    movingCardIsSpy = true;
                                }
                                if (ourSpyHere && !movingCardIsSpy) {
                                    // V65: Strengthened from -400 to -1500. Previous -400 was
                                    // getting overridden by +300 V41 CONTEST DEST + +300 contest
                                    // bonus. -1500 ensures spy dilution is a near-hard block when
                                    // safer alternatives exist.
                                    action.addReasoning(
                                        "V62 SPY DILUTION: Our undercover spy is at " + title
                                            + " — moving a non-spy here wastes the spy's drain-blocking!",
                                        -1500.0f);
                                    logger.warn("V62 SPY DILUTION: {} has our spy — don't dilute (-1500)", title);
                                }
                            } catch (Exception e) { /* ignore */ }
                        }

                        // === V24.13: LANDO ALONE DETECTION — MOVE TO SUPPORT ===
                        // If Lando is the only friendly character at this CC site, big bonus
                        // to move here and protect him. Lando alone = easy kill for opponent.
                        if (game != null && playerId != null) {
                            try {
                                java.util.List<PhysicalCard> destCards = gameState.getCardsAtLocation(location);
                                if (destCards != null) {
                                    boolean landoAlone = false;
                                    int ourCharCount = 0;
                                    for (PhysicalCard c : destCards) {
                                        if (c == null || !playerId.equals(c.getOwner())) continue;
                                        if (c.getBlueprint() == null) continue;
                                        if (c.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                        ourCharCount++;
                                        String cTitle = c.getTitle();
                                        if (cTitle != null && cTitle.toLowerCase(java.util.Locale.ROOT).contains("lando")) {
                                            landoAlone = true;
                                        }
                                    }
                                    if (landoAlone && ourCharCount == 1) {
                                        action.addReasoning(
                                            "V24.13 LANDO SUPPORT: Lando is ALONE here — move to protect him!", 250.0f);
                                        logger.warn("V24.13 LANDO ALONE AT {}: Moving here to support (+250)", title);
                                    }
                                }
                            } catch (Exception e) { /* ignore */ }
                        }

                        // === V47: LANDO MOVEMENT — STAY AT DINING ROOM ===
                        // Lando should NOT move from Dining Room. He establishes occupation there
                        // and moving wastes force / loses presence. Only move if we have 3+ friendlies
                        // at his current location (he's redundant) and destination is unoccupied CC site.
                        String moveDecisionText = context.getDecisionText() != null
                            ? context.getDecisionText().toLowerCase() : "";
                        if (moveDecisionText.contains("lando")) {
                            com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer moveObjAnalyzer =
                                context.getObjectiveAnalyzer();
                            if (moveObjAnalyzer != null && moveObjAnalyzer.needsBespinSystemPresence()) {
                                // V47: Block most Lando moves — he stays where he is
                                action.addReasoning("V47 LANDO STAY: Lando should stay put — moving wastes force and loses occupation!", -9999.0f);
                                logger.warn("V47 LANDO STAY: Blocking Lando move to {} — stay at current location!", title);
                            }
                        }

                        // === V24.14B: WEAPON CHARACTERS TO SPACE — MOVEMENT PENALTY ===
                        // Characters with "permanent weapon" in game text shouldn't shuttle/move
                        // to system locations (space) — their weapons can't fire there.
                        {
                            boolean destIsSpace = false;
                            if (bp != null) {
                                com.gempukku.swccgo.common.CardSubtype destSubtype = bp.getCardSubtype();
                                destIsSpace = (destSubtype == com.gempukku.swccgo.common.CardSubtype.SYSTEM);
                            }
                            if (destIsSpace) {
                                // Check if the character being moved has a permanent weapon
                                boolean movingCharHasWeapon = false;
                                // Check decision text for weapon keywords in card name (fallback)
                                if (moveDecisionText.contains("lightsaber") || moveDecisionText.contains("blaster")
                                    || moveDecisionText.contains("with rifle") || moveDecisionText.contains("with cannon")) {
                                    movingCharHasWeapon = true;
                                }
                                // Check blueprint game text for "permanent weapon" (universal)
                                if (!movingCharHasWeapon) {
                                    // Try to extract the moving card's blueprint from decision text
                                    String moveBpId = extractBlueprintFromDecisionText(context.getDecisionText());
                                    if (moveBpId != null) {
                                        try {
                                            SwccgCardBlueprint moveBp = getBlueprintFromId(context, moveBpId);
                                            if (moveBp != null) {
                                                String moveGameText = moveBp.getGameText();
                                                if (moveGameText != null &&
                                                    moveGameText.toLowerCase(java.util.Locale.ROOT).contains("permanent weapon")) {
                                                    movingCharHasWeapon = true;
                                                }
                                            }
                                        } catch (Exception e) {
                                            logger.debug("V24.14B: Error checking move blueprint: {}", e.getMessage());
                                        }
                                    }
                                }
                                if (movingCharHasWeapon) {
                                    action.addReasoning("V24.14B WEAPON CHAR TO SPACE: Permanent weapon can't fire at system locations — don't shuttle here!", -300.0f);
                                    logger.warn("V24.14B WEAPON MOVE: Char with permanent weapon moving to space {} — penalized (-300)", title);
                                }
                                // Also penalize vehicles moving to space
                                if (moveDecisionText.contains("vehicle")) {
                                    action.addReasoning("V24.14B VEHICLE TO SPACE: Vehicles don't belong in space!", -300.0f);
                                    logger.warn("V24.14B VEHICLE MOVE: Vehicle moving to space {} — penalized (-300)", title);
                                }
                            }
                        }

                        // === V41: HUNT DOWN — MOVE DESTINATION AWARENESS ===
                        // V67f2: Exclude UNDERCOVER SPIES from "go fight" bonus — a spy
                        // doesn't actively threaten us; moving Jedi to an opp-spy site
                        // wastes drain potential. FIXES uarc0hmiai1i594y replay: Ezra
                        // and Young Skywalker piled into Tatooine: Mos Eisley because
                        // V41 saw "+300 go fight" on Steve's U-3PO spy (power 1).
                        if (game != null && playerId != null) {
                            try {
                                String opponentId = gameState.getOpponent(playerId);

                                // V67f2: Recompute opponent power EXCLUDING undercover spies.
                                float v67fNonSpyOpponentPower = 0;
                                int v67fSpiesHere = 0;
                                try {
                                    java.util.List<PhysicalCard> hereCards =
                                        gameState.getCardsAtLocation(location);
                                    if (hereCards != null) {
                                        for (PhysicalCard hc : hereCards) {
                                            if (hc == null) continue;
                                            if (!opponentId.equals(hc.getOwner())) continue;
                                            if (hc.isUndercover()) {
                                                v67fSpiesHere++;
                                                continue;
                                            }
                                            SwccgCardBlueprint hcBp = hc.getBlueprint();
                                            if (hcBp != null && hcBp.hasPowerAttribute()) {
                                                Float p = hcBp.getPower();
                                                if (p != null) v67fNonSpyOpponentPower += p;
                                            }
                                        }
                                    }
                                } catch (Exception e) { /* ignore */ }

                                // V67aa: HIDDEN PATH JEDI SUICIDE BLOCK (mirror of rando)
                                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer v67aaObj =
                                    context.getObjectiveAnalyzer();
                                boolean v67aaOnHiddenPath = v67aaObj != null && v67aaObj.isAnalyzed()
                                    && v67aaObj.getObjectiveTitle() != null
                                    && v67aaObj.getObjectiveTitle().toLowerCase(java.util.Locale.ROOT).contains("hidden path")
                                    && !v67aaObj.isFlipped();
                                if (v67aaOnHiddenPath && v67fNonSpyOpponentPower >= 5 && ourPower == 0) {
                                    action.addReasoning(String.format(
                                        "V67aa HIDDEN PATH SUICIDE BLOCK: %s opp power %.0f — pre-flip Jedi survivors are power 3, suicide!",
                                        title, v67fNonSpyOpponentPower), -9999.0f);
                                    logger.warn("V67aa SUICIDE BLOCK: {} opp={} our=0 on Hidden Path pre-flip — BLOCK (-9999)",
                                        title, v67fNonSpyOpponentPower);
                                    actions.add(action);
                                    continue;
                                }

                                if (v67fNonSpyOpponentPower > 0) {
                                    float contestBonus = 300.0f;
                                    if (ourPower == 0) {
                                        contestBonus += 200.0f;
                                        logger.warn("V41 MOVE DEST CONTEST: {} is UNCONTESTED — urgent! (+500)", title);
                                    }
                                    boolean jediAtDest = false;
                                    for (PhysicalCard c : gameState.getCardsAtLocation(location)) {
                                        if (c == null || playerId.equals(c.getOwner())) continue;
                                        String cTitle = c.getTitle() != null ? c.getTitle().toLowerCase(java.util.Locale.ROOT) : "";
                                        if (ActionEvaluator.isJediOrPadawan(cTitle)) { jediAtDest = true; break; }
                                    }
                                    if (jediAtDest) {
                                        contestBonus += 200.0f;
                                        logger.warn("V41 HUNT JEDI DEST: Jedi at {} — must go here! (+{})", title, (int)contestBonus);
                                    }
                                    action.addReasoning(String.format(
                                        "V41 CONTEST DEST: Opponents (power %.0f) at %s%s — go fight!",
                                        v67fNonSpyOpponentPower, title, jediAtDest ? " [JEDI!]" : ""), contestBonus);
                                } else if (v67fSpiesHere > 0) {
                                    // V67f2: Opponent's spy here but no real characters.
                                    // Don't dilute drain potential by piling characters into a spy site.
                                    action.addReasoning(
                                        "V67f SPY-ONLY: " + title + " has only opponent spy ("
                                            + v67fSpiesHere + ") — drain blocked, prefer draining elsewhere",
                                        -100.0f);
                                    logger.warn("V67f SPY-ONLY: {} has only opp spies (no real characters) — penalize move-in (-100)", title);
                                } else {
                                    // V65 SMART WRONG-DIRECTION: Skip the hard-block when:
                                    //   (a) Our own undercover spy is at the "draining" site
                                    //       (spy neutralizes their drain — it's not actually a threat)
                                    //   (b) The "draining" site is suicide to enter
                                    //       (opponent power too high for our Jedi)
                                    boolean opponentsElsewhere = false;
                                    String worstDrainLoc = null;
                                    float worstDrainPower = 0;
                                    for (PhysicalCard otherLoc : gameState.getTopLocations()) {
                                        if (otherLoc == null || otherLoc == location) continue;
                                        float oppPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                            gameState, otherLoc, opponentId, false, false);
                                        float ourPowerThere = game.getModifiersQuerying().getTotalPowerAtLocation(
                                            gameState, otherLoc, playerId, false, false);
                                        if (oppPower > 0 && ourPowerThere == 0) {
                                            // V65a: Our spy at the drain location blocks it. Skip.
                                            boolean ourSpyBlocksIt = false;
                                            try {
                                                java.util.List<PhysicalCard> cardsAtOther = gameState.getCardsAtLocation(otherLoc);
                                                if (cardsAtOther != null) {
                                                    for (PhysicalCard osc : cardsAtOther) {
                                                        if (osc != null && playerId.equals(osc.getOwner())
                                                            && osc.isUndercover()) {
                                                            ourSpyBlocksIt = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                            } catch (Exception e) { /* ignore */ }
                                            if (ourSpyBlocksIt) {
                                                logger.info("V65a SPY-NEUTRALIZED: Not marking {} as wrong-direction — our spy blocks {} drain",
                                                    title, otherLoc.getTitle());
                                                continue;  // don't count this as a drain threat
                                            }
                                            // V65b: Suicide destination — opponent too strong for single Jedi.
                                            if (oppPower >= 7) {
                                                logger.info("V65b SUICIDE-WRONG-DIR: Not marking {} as wrong-direction — {} has enemy power {} (suicide for Jedi)",
                                                    title, otherLoc.getTitle(), (int)oppPower);
                                                continue;  // don't count this as a drain threat
                                            }
                                            opponentsElsewhere = true;
                                            if (oppPower > worstDrainPower) { worstDrainPower = oppPower; worstDrainLoc = otherLoc.getTitle(); }
                                        }
                                    }
                                    if (opponentsElsewhere) {
                                        // V67z: EXEMPT Hidden Path split-sites (see rando-side note)
                                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer v67zObj =
                                            context.getObjectiveAnalyzer();
                                        boolean v67zOnHiddenPath = v67zObj != null && v67zObj.isAnalyzed()
                                            && v67zObj.getObjectiveTitle() != null
                                            && v67zObj.getObjectiveTitle().toLowerCase(java.util.Locale.ROOT).contains("hidden path")
                                            && !v67zObj.isFlipped();
                                        boolean v67zNonMapuzoBG = false;
                                        if (v67zOnHiddenPath && title != null
                                                && !title.toLowerCase(java.util.Locale.ROOT).contains("mapuzo")) {
                                            try {
                                                v67zNonMapuzoBG = game.getModifiersQuerying()
                                                    .isBattleground(gameState, location, null);
                                            } catch (Exception e) { /* ignore */ }
                                        }
                                        if (v67zNonMapuzoBG) {
                                            logger.info("V67z HIDDEN PATH SPLIT EXEMPT: {} non-Mapuzo BG, V41 skipped", title);
                                        } else {
                                            action.addReasoning(String.format(
                                                "V41 WRONG DIRECTION: %s is empty — opponents draining at %s!",
                                                title, worstDrainLoc), -9999.0f);
                                            logger.warn("V41 WRONG DIRECTION: {} empty, opponents at {} — BLOCKED", title, worstDrainLoc);
                                        }
                                    }
                                }
                                String destTitleLower = title != null ? title.toLowerCase(java.util.Locale.ROOT) : "";
                                if (destTitleLower.contains("mustafar") && destTitleLower.contains("castle")) {
                                    boolean anyOpponents = false;
                                    for (PhysicalCard otherLoc : gameState.getTopLocations()) {
                                        if (otherLoc == null) continue;
                                        float op = game.getModifiersQuerying().getTotalPowerAtLocation(
                                            gameState, otherLoc, opponentId, false, false);
                                        if (op > 0) { anyOpponents = true; break; }
                                    }
                                    if (anyOpponents) {
                                        action.addReasoning("V41 CASTLE RETREAT: NEVER retreat to Castle!", -9999.0f);
                                        logger.warn("V41 CASTLE RETREAT BLOCKED in move destination selection");
                                    }
                                }
                            } catch (Exception e) { logger.debug("V41 MOVE DEST error: {}", e.getMessage()); }
                        }

                        // === V24.3C: DR. EVAZAN WEAPON COMBO — MOVEMENT PREFERENCE ===
                        // Move Evazan toward weapon characters, and weapon chars toward Evazan.
                        boolean movingEvazan = moveDecisionText.contains("evazan");
                        boolean movingWeaponChar = (moveDecisionText.contains("maul") && moveDecisionText.contains("lightsaber"))
                            || (moveDecisionText.contains("vader") && moveDecisionText.contains("lightsaber"))
                            || (moveDecisionText.contains("mara") && moveDecisionText.contains("lightsaber"))
                            || (moveDecisionText.contains("jade") && moveDecisionText.contains("lightsaber"))
                            || (moveDecisionText.contains("aurra") && moveDecisionText.contains("blaster"))
                            || (moveDecisionText.contains("sing") && moveDecisionText.contains("blaster"));

                        if (movingEvazan || movingWeaponChar) {
                            boolean comboPartnerAtDest = false;
                            try {
                                java.util.List<PhysicalCard> destCards = gameState.getCardsAtLocation(location);
                                if (destCards != null) {
                                    for (PhysicalCard c : destCards) {
                                        if (c == null || !playerId.equals(c.getOwner())) continue;
                                        String cTitle = c.getTitle();
                                        if (cTitle == null) continue;
                                        String cLower = cTitle.toLowerCase();

                                        if (movingEvazan) {
                                            if ((cLower.contains("maul") && cLower.contains("lightsaber"))
                                                || (cLower.contains("vader") && cLower.contains("lightsaber"))
                                                || (cLower.contains("mara") && cLower.contains("lightsaber"))
                                                || (cLower.contains("jade") && cLower.contains("lightsaber"))
                                                || (cLower.contains("aurra") && cLower.contains("blaster"))
                                                || (cLower.contains("sing") && cLower.contains("blaster"))) {
                                                comboPartnerAtDest = true;
                                                break;
                                            }
                                        } else {
                                            if (cLower.contains("evazan")) {
                                                comboPartnerAtDest = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) { /* ignore */ }

                            if (comboPartnerAtDest) {
                                action.addReasoning(
                                    "V24.3 EVAZAN COMBO: Move here — combo partner at this site for weapon kill combo!",
                                    200.0f);
                                logger.warn("V24.3 EVAZAN COMBO MOVE: Partner found at {} (+200)", title);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    logger.debug("Could not parse cardId for move destination: {}", cardId);
                }
            }

            actions.add(action);
        }

        return actions;
    }

    /**
     * Choose card to cancel - cancel opponent's cards.
     */
    private List<EvaluatedAction> evaluateCancelSelection(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        String playerId = context.getPlayerId();

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.UNKNOWN,
                50.0f,
                "Cancel card " + cardId
            );

            if (gameState != null) {
                try {
                    PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                    if (card != null) {
                        String owner = card.getOwner();

                        // Cancel opponent's cards, not ours!
                        if (!playerId.equals(owner)) {
                            action.addReasoning("Opponent's card - cancel!", 100.0f);
                        } else {
                            action.addReasoning("Our card - don't cancel!", -200.0f);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }

            actions.add(action);
        }

        return actions;
    }

    /**
     * Check if the decision text indicates we're playing a beneficial card
     * that should target our OWN cards (not opponent's).
     *
     * Examples:
     * - A Few Maneuvers (adds +2 hyperspeed and maneuver to your starship)
     * - Hyper Escape (allows your ship to escape)
     * - Various buff/enhancement cards
     */
    private boolean isBeneficialTargetingCard(String decisionText) {
        if (decisionText == null) return false;
        String textLower = decisionText.toLowerCase();

        // Cards that buff your own cards
        String[] beneficialCards = {
            "a few maneuvers",      // +2 hyperspeed and maneuver
            "hyper escape",         // Escape action
            "evasive action",       // Escape/dodge
            "rebel barrier",        // Defense
            "narrow escape",        // Escape
            "darklighter spin",     // Combat bonus
            "hear me baby",         // Buff
            "all power to weapons", // Attack buff
            "full throttle",        // Speed buff
            "punch it",             // Speed/escape
            "alert my star destroyer", // Defense buff
            "i have you now"        // Attack buff (targets your TIE)
        };

        for (String card : beneficialCards) {
            if (textLower.contains(card)) {
                logger.info("🎯 Detected beneficial card '{}' - targeting own cards", card);
                return true;
            }
        }

        return false;
    }

    /**
     * Target selection for weapons/abilities - must select, don't cancel.
     *
     * IMPORTANT: Some cards target your OWN cards (beneficial buffs like A Few Maneuvers)
     * while others target OPPONENT cards (weapons, disruptions). We detect this from
     * the decision text which shows what card is being played.
     */
    private List<EvaluatedAction> evaluateTargetSelection(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        String playerId = context.getPlayerId();
        String decisionText = context.getDecisionText();

        // Check if we're playing a beneficial card that targets our own cards
        boolean targetOwnCards = isBeneficialTargetingCard(decisionText);

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.UNKNOWN,
                50.0f,
                "Target " + cardId
            );

            if (gameState != null) {
                try {
                    PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                    if (card != null) {
                        String owner = card.getOwner();
                        SwccgCardBlueprint blueprint = card.getBlueprint();
                        boolean isOurCard = playerId.equals(owner);

                        if (targetOwnCards) {
                            // Beneficial card - target OUR cards, not opponent's
                            if (isOurCard) {
                                action.addReasoning("Beneficial effect on our card", 50.0f);

                                // Prefer high-value targets for buffs
                                if (blueprint != null) {
                                    if (blueprint.hasPowerAttribute()) {
                                        Float power = blueprint.getPower();
                                        if (power != null && power >= 5) {
                                            action.addReasoning("High-power target for buff", 30.0f);
                                        }
                                    }

                                    if (blueprint.getUniqueness() == Uniqueness.UNIQUE) {
                                        action.addReasoning("Unique target for buff", 20.0f);
                                    }
                                }
                            } else {
                                action.addReasoning("Don't buff opponent's card!", -200.0f);
                            }
                        } else {
                            // Harmful card (weapon, etc.) - target OPPONENT cards
                            if (!isOurCard) {
                                action.addReasoning("Target opponent's card", 50.0f);

                                // V51: Don't waste weapons on already-hit characters
                                if (card.isHit()) {
                                    action.addReasoning("V51 ALREADY HIT: Target already hit — don't waste weapon!", -500.0f);
                                    logger.warn("V51 ALREADY HIT: Weapon targeting {} but already hit — -500", card.getTitle());
                                }

                                // V51: Force Lightning / Trample — prioritize opponent spies
                                if (card.isUndercover()) {
                                    action.addReasoning("V51 KILL SPY: Target is an undercover spy — eliminate it!", 500.0f);
                                    logger.warn("V51 KILL SPY: Targeting spy {} — +500!", card.getTitle());
                                }

                                if (blueprint != null) {
                                    // === V36: DESTINY-BASED WEAPON TARGETING ===
                                    // Calculate hit probability: avgDestiny * numDraws vs defense value
                                    // Lightsaber draws 2 destiny. Other weapons draw 1-2.
                                    // Only fire at targets we can actually hit!
                                    SwccgGame targetGame = context.getGame();
                                    if (targetGame != null && gameState != null && context.getPhase() == Phase.BATTLE) {
                                        try {
                                            // Get target's defense value (ability for characters)
                                            float defenseValue = targetGame.getModifiersQuerying().getDefenseValue(gameState, card);

                                            // Get average destiny in reserve deck
                                            com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle destOracle = context.getDeckOracle();
                                            double avgDestiny = 3.0; // fallback
                                            if (destOracle != null && destOracle.isAnalyzed()) {
                                                avgDestiny = destOracle.getAverageDestinyInReserve();
                                            }

                                            // Lightsaber draws 2 destiny, most other weapons draw 1
                                            int numDraws = 2; // assume lightsaber
                                            float expectedTotal = (float)(avgDestiny * numDraws);
                                            float hitMargin = expectedTotal - defenseValue;

                                            String targetTitle = card.getTitle() != null ? card.getTitle() : "?";
                                            String targetLower = targetTitle.toLowerCase(java.util.Locale.ROOT);

                                            if (hitMargin >= 3.0f) {
                                                // Easy hit — very likely to succeed
                                                action.addReasoning(String.format(
                                                    "V36 EASY HIT: %s defense %.0f, expected destiny %.1f — HIGH hit chance!",
                                                    targetTitle, defenseValue, expectedTotal), 200.0f);
                                            } else if (hitMargin >= 0.0f) {
                                                // Marginal hit — coin flip
                                                action.addReasoning(String.format(
                                                    "V36 MARGINAL HIT: %s defense %.0f, expected destiny %.1f — might hit",
                                                    targetTitle, defenseValue, expectedTotal), 50.0f);
                                            } else {
                                                // Likely miss — defense too high
                                                action.addReasoning(String.format(
                                                    "V36 LIKELY MISS: %s defense %.0f, expected destiny %.1f — probably won't hit!",
                                                    targetTitle, defenseValue, expectedTotal), -150.0f);
                                                logger.warn("V36 WEAPON TARGET: {} defense {} vs expected {} — LIKELY MISS",
                                                    targetTitle, (int)defenseValue, String.format("%.1f", expectedTotal));
                                            }

                                            // === V36: PRIORITY TARGETS ===
                                            // 1. Game-text cancelers (Padme cancels Vader!) — MUST REMOVE
                                            if (targetLower.contains("padme") || targetLower.contains("naberrie")) {
                                                action.addReasoning("V36 PRIORITY: Padme cancels Vader's game text — REMOVE HER!", 300.0f);
                                            }

                                            // 2. Characters that add battle destiny (Lando Scoundrel, etc.)
                                            // These draw extra destiny = extra attrition damage
                                            if (targetLower.contains("lando") || targetLower.contains("boba fett")
                                                || targetLower.contains("wedge") || targetLower.contains("chewie")) {
                                                action.addReasoning("V36 PRIORITY: Battle destiny adder — dangerous!", 100.0f);
                                            }

                                            // 3. Jedi/Padawan — Hunt Down bonus for killing them
                                            if (isJediOrPadawan(targetLower)) {
                                                action.addReasoning("V36 HUNT: Jedi/Padawan target — Hunt Down bonus!", 80.0f);
                                            }

                                        } catch (Exception e) {
                                            logger.debug("V36 WEAPON TARGET: Error calculating hit probability: {}", e.getMessage());
                                        }
                                    } else {
                                        // Not in battle — basic targeting
                                        if (blueprint.hasPowerAttribute()) {
                                            Float power = blueprint.getPower();
                                            if (power != null && power >= 5) {
                                                action.addReasoning("High-power target", 30.0f);
                                            }
                                        }
                                    }

                                    if (blueprint.getUniqueness() == Uniqueness.UNIQUE) {
                                        action.addReasoning("Unique target", 20.0f);
                                    }
                                }
                            } else {
                                action.addReasoning("V38.3 SELF-TARGET: NEVER target own card with harmful effect!", -9999.0f);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }

            actions.add(action);
        }

        return actions;
    }

    /**
     * Location selection - pick battlegrounds and force icon locations.
     */
    private List<EvaluatedAction> evaluateLocationSelection(DecisionContext context) {
        return evaluateDeployLocation(context);  // Same logic
    }

    /**
     * V43: Starting interrupt selection.
     * Prefer interrupts that deploy the Epic Event ("Force Is Strong In My Family")
     * over generic starting interrupts like "The Signal".
     */
    private List<EvaluatedAction> evaluateStartingInterrupt(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        List<String> cardIds = context.getCardIds();
        List<String> blueprintIds = context.getBlueprints();
        boolean isArbitrary = "ARBITRARY_CARDS".equals(context.getDecisionType());
        GameState gameState = context.getGameState();

        logger.warn("V43 STARTING INTERRUPT: Evaluating {} choices", cardIds != null ? cardIds.size() : 0);

        if (cardIds == null) return actions;

        for (int idx = 0; idx < cardIds.size(); idx++) {
            String cardId = cardIds.get(idx);
            EvaluatedAction action = new EvaluatedAction(cardId, ActionType.UNKNOWN, 50.0f, "Starting interrupt candidate");

            try {
                SwccgCardBlueprint blueprint = null;
                String title = "?";

                if (isArbitrary && blueprintIds != null && idx < blueprintIds.size()) {
                    blueprint = getBlueprintFromId(context, blueprintIds.get(idx));
                } else if (gameState != null) {
                    PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                    if (card != null) blueprint = card.getBlueprint();
                }

                if (blueprint != null) {
                    title = blueprint.getTitle() != null ? blueprint.getTitle() : "?";
                    String gameText = blueprint.getGameText() != null ? blueprint.getGameText().toLowerCase(java.util.Locale.ROOT) : "";

                    // HARD PREFER: interrupts that deploy or reference the Epic Event
                    if (gameText.contains("force is strong in my family")
                        || gameText.contains("force is strong")
                        || gameText.contains("epic")) {
                        action.addReasoning("V43 EPIC EVENT: Deploys saga Epic Event — MUST USE THIS!", 1500.0f);
                        logger.warn("V43 STARTING INTERRUPT: {} references Epic Event — HARD PREFER (+1500)", title);
                    } else {
                        action.addReasoning("V43: Generic starting interrupt — no Epic Event", 0.0f);
                        logger.warn("V43 STARTING INTERRUPT: {} is generic (no Epic Event reference)", title);
                    }
                }
            } catch (Exception e) {
                logger.debug("V43 STARTING INTERRUPT: Error evaluating card {}: {}", cardId, e.getMessage());
            }

            actions.add(action);
        }

        return actions;
    }

    /**
     * Starting location selection.
     * V22: Objective-aware + bonus for locations that pull from reserve deck.
     * Base +50 only if the location is mentioned in the starting interrupt's text.
     */
    private List<EvaluatedAction> evaluateStartingLocation(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer startLocObjAnalyzer =
            context.getObjectiveAnalyzer();

        // V22: Get the decision text which should reference the starting interrupt
        String decisionText = context.getDecisionText();
        String decisionTextLower = decisionText != null ? decisionText.toLowerCase(java.util.Locale.ROOT) : "";

        // V29.14: Get blueprint IDs for ARBITRARY_CARDS decisions (temp IDs can't be parsed as ints)
        List<String> blueprintIds = context.getBlueprints();
        boolean isArbitrary = "ARBITRARY_CARDS".equals(context.getDecisionType());

        List<String> cardIds = context.getCardIds();
        for (int idx = 0; idx < cardIds.size(); idx++) {
            String cardId = cardIds.get(idx);
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.DEPLOY,
                10.0f,
                "Starting location " + cardId
            );

            // V22/V29.14: Look up the card to check game text and title
            // V29.14: For ARBITRARY_CARDS, card IDs are "temp0" etc. — use blueprint lookup instead.
            try {
                String locTitle = null;
                String locTitleLower = "";
                SwccgCardBlueprint locBp = null;

                if (isArbitrary && blueprintIds != null && idx < blueprintIds.size()) {
                    // V29.14: ARBITRARY_CARDS path — look up by blueprint ID
                    String bpId = blueprintIds.get(idx);
                    locBp = getBlueprintFromId(context, bpId);
                    if (locBp != null) {
                        locTitle = locBp.getTitle();
                        locTitleLower = locTitle != null ? locTitle.toLowerCase(java.util.Locale.ROOT) : "";
                        logger.warn("V29.14 ARBITRARY_CARDS: Resolved card '{}' via blueprint '{}' → '{}'", cardId, bpId, locTitle);
                    } else {
                        logger.warn("V29.14 ARBITRARY_CARDS: Could not resolve blueprint '{}' for card '{}'", bpId, cardId);
                    }
                } else if (gameState != null) {
                    // Standard path — look up by integer card ID
                    PhysicalCard locCard = gameState.findCardById(Integer.parseInt(cardId));
                    if (locCard != null) {
                        locTitle = locCard.getTitle();
                        locTitleLower = locTitle != null ? locTitle.toLowerCase(java.util.Locale.ROOT) : "";
                        locBp = locCard.getBlueprint();
                    }
                }

                if (locTitle != null && locBp != null) {
                    // V22: +50 base ONLY if this location is mentioned in the starting interrupt
                    if (decisionTextLower.contains(locTitleLower)) {
                        action.addReasoning("V22 MENTIONED IN STARTING INTERRUPT", 50.0f);
                        logger.warn("V22 STARTING LOC: {} is mentioned in interrupt text (+50)", locTitle);
                    }

                    // V22: Objective-relevant starting location gets big boost
                    if (startLocObjAnalyzer != null && startLocObjAnalyzer.isAnalyzed()) {
                        if (startLocObjAnalyzer.isObjectiveRelevantLocation(locTitle)) {
                            float objBonus = startLocObjAnalyzer.getLocationObjectiveBonus(locTitle);
                            action.addReasoning("V22 OBJECTIVE STARTING LOCATION: " + locTitle, objBonus);
                            logger.warn("V22 STARTING LOC: {} is objective-relevant (+{})", locTitle, objBonus);
                        }
                    }

                    // === V24.10: EXTERIOR CC SITE MUST BE STARTING LOCATION ===
                    if (locTitleLower.contains("cloud city")) {
                        boolean isExterior = locBp.hasIcon(com.gempukku.swccgo.common.Icon.EXTERIOR_SITE);
                        boolean isInterior = locBp.hasIcon(com.gempukku.swccgo.common.Icon.INTERIOR_SITE);
                        if (isExterior && !isInterior) {
                            action.addReasoning("V24.10 EXTERIOR CC STARTING LOCATION: Only way to deploy — I'm Sorry can't pull this!", 500.0f);
                            logger.warn("V24.10 STARTING LOC: {} is EXTERIOR — HARD PREFER as starting location (+500)", locTitle);
                        } else if (isInterior) {
                            action.addReasoning("V24.10 INTERIOR CC: Slip Sliding or I'm Sorry will pull this — save starting slot for exterior!", -500.0f);
                            logger.warn("V24.10 STARTING LOC: {} is INTERIOR — HARD BLOCK as starting location (-500)", locTitle);
                        }
                    }

                    // V22: Starting locations that pull cards from Reserve Deck
                    // V71 (Steve, 2026-05-15): Mirror of Rando V71. Locations store text
                    // in getLocationLightSideGameText()/getLocationDarkSideGameText(),
                    // not getGameText(). Concat all three so keyword checks work.
                    String locBaseText = locBp.getGameText();
                    String locLightText = null;
                    String locDarkText = null;
                    try { locLightText = locBp.getLocationLightSideGameText(); } catch (Exception ignored) { }
                    try { locDarkText = locBp.getLocationDarkSideGameText(); } catch (Exception ignored) { }
                    StringBuilder locAllSb = new StringBuilder();
                    if (locBaseText != null) locAllSb.append(locBaseText).append(' ');
                    if (locLightText != null) locAllSb.append(locLightText).append(' ');
                    if (locDarkText != null) locAllSb.append(locDarkText).append(' ');
                    String locTextLower = locAllSb.toString().toLowerCase(java.util.Locale.ROOT);
                    if (!locTextLower.isEmpty()) {
                        if (locTextLower.contains("reserve")) {
                            action.addReasoning("V22 RESERVE PULL: starting location pulls from reserve deck", 75.0f);
                            logger.warn("V22 STARTING LOC: {} pulls from Reserve Deck (+75)", locTitle);
                        }
                        if (locTextLower.contains("force generation") || locTextLower.contains("force icon")
                            || locTextLower.contains("adds one to")) {
                            action.addReasoning("V22 FORCE GEN: starting location boosts force", 25.0f);
                            logger.warn("V22 STARTING LOC: {} boosts force generation (+25)", locTitle);
                        }

                        // === V29.14: EPIC EVENT STARTING LOCATION ===
                        // V71: now scans Light/Dark side texts (fixes Ajan Kloss miss).
                        if (locTextLower.contains("epic")) {
                            action.addReasoning("V29.14 EPIC EVENT: game text mentions 'epic' — critical starting location!", 1000.0f);
                            logger.warn("V29.14 STARTING LOC: {} mentions 'epic' in game text — HARD PREFER (+1000)", locTitle);
                        }
                    }

                    // === V29.14: FUNERAL PYRE TITLE CHECK ===
                    // Belt-and-suspenders: also check card title for "Funeral Pyre"
                    // This is a key starting location for Luke Saga decks.
                    if (locTitleLower.contains("funeral pyre")) {
                        action.addReasoning("V29.14 FUNERAL PYRE: critical starting location for Luke Saga!", 1000.0f);
                        logger.warn("V29.14 STARTING LOC: {} title contains 'Funeral Pyre' — HARD PREFER (+1000)", locTitle);
                    }

                    // === V67o BATTLEGROUND STARTING LOCATION ===
                    // Steve's rule: starting location should be a BATTLEGROUND so force
                    // drains and battles can happen there from turn 1. Without this rule
                    // Rando picks non-battleground sites (e.g., Dooku deck starts at a
                    // non-BG site) and loses tempo from turn 1.
                    //
                    // Detection heuristic (matches V29.6 in the deploy path):
                    //   1. Game text contains "battleground"
                    //   2. Title contains "battleground"
                    //   3. Site has BOTH Light Force AND Dark Force icons
                    //      (most battlegrounds have both — drainable + drainable-against)
                    //
                    // Score: +300 for battleground, -150 for non-battleground.
                    // Below Funeral Pyre/Epic Event (+1000) and CC Exterior (+500) so
                    // those specific overrides still win; above Force Gen (+25), Reserve
                    // Pull (+75), and Mention-in-Interrupt (+50) so battleground wins
                    // when no specific override applies.
                    boolean v67oIsBg = false;
                    String v67oReason = null;
                    String v67oGt = locBp.getGameText();
                    if (v67oGt != null && v67oGt.toLowerCase(java.util.Locale.ROOT).contains("battleground")) {
                        v67oIsBg = true;
                        v67oReason = "game text contains 'battleground'";
                    } else if (locTitleLower.contains("battleground")) {
                        v67oIsBg = true;
                        v67oReason = "title contains 'battleground'";
                    } else {
                        try {
                            if (locBp.hasIcon(com.gempukku.swccgo.common.Icon.LIGHT_FORCE)
                                && locBp.hasIcon(com.gempukku.swccgo.common.Icon.DARK_FORCE)) {
                                v67oIsBg = true;
                                v67oReason = "site has both LIGHT and DARK force icons";
                            }
                        } catch (Exception e) { /* ignore */ }
                    }
                    if (v67oIsBg) {
                        action.addReasoning("V67o BATTLEGROUND STARTING LOC: " + locTitle
                            + " is a battleground (" + v67oReason
                            + ") — drains and battles from turn 1!", 300.0f);
                        logger.warn("V67o STARTING LOC: {} is BATTLEGROUND ({}) → +300", locTitle, v67oReason);
                    } else {
                        action.addReasoning("V67o NON-BATTLEGROUND STARTING LOC: " + locTitle
                            + " — no force drains/battles possible here, prefer battleground!",
                            -150.0f);
                        logger.warn("V67o STARTING LOC: {} is NON-BATTLEGROUND → -150", locTitle);
                    }

                    // === V67q SITH DECK SPECIFIC TIGHTENING ===
                    // Steve's Dooku deck uses Rise Of The Sith / Revenge Of The Sith.
                    // Those starting Effects only function at a NON-PALACE battleground.
                    // If the deck has either of those cards anywhere in the player's
                    // pool (hand, reserve, used/lost/force pile, in-play, stacked, etc.),
                    // tighten the starting-location preference:
                    //   - Non-Palace battleground: +600 ADDITIONAL (net ~+900 with V67o)
                    //   - Palace battleground:     -350 ADDITIONAL (net ~-50 — discouraged)
                    //   - Non-battleground:        -300 ADDITIONAL (net ~-450)
                    // This mirrors a previous K-2 session's design (lost when their session
                    // ended without committing).
                    boolean v67qHasSithStart = false;
                    try {
                        String gsPlayerId = context.getPlayerId();
                        if (gameState != null && gsPlayerId != null) {
                            String[] sithMarkers = new String[] {
                                "rise of the sith", "revenge of the sith"
                            };
                            // Scan every zone the card could be in
                            java.util.List<PhysicalCard> v67qScanCards = new java.util.ArrayList<>();
                            try { v67qScanCards.addAll(gameState.getHand(gsPlayerId)); } catch (Exception e) { /* ignore */ }
                            try { v67qScanCards.addAll(gameState.getReserveDeck(gsPlayerId)); } catch (Exception e) { /* ignore */ }
                            try { v67qScanCards.addAll(gameState.getForcePile(gsPlayerId)); } catch (Exception e) { /* ignore */ }
                            try { v67qScanCards.addAll(gameState.getUsedPile(gsPlayerId)); } catch (Exception e) { /* ignore */ }
                            try { v67qScanCards.addAll(gameState.getLostPile(gsPlayerId)); } catch (Exception e) { /* ignore */ }
                            try { v67qScanCards.addAll(gameState.getOutOfPlayPile(gsPlayerId)); } catch (Exception e) { /* ignore */ }
                            try { v67qScanCards.addAll(gameState.getOutsideOfDeck(gsPlayerId)); } catch (Exception e) { /* ignore */ }
                            try { v67qScanCards.addAll(gameState.getSideOfTableFaceDown(gsPlayerId)); } catch (Exception e) { /* ignore */ }
                            try { v67qScanCards.addAll(gameState.getVoid(gsPlayerId)); } catch (Exception e) { /* ignore */ }
                            try { v67qScanCards.addAll(gameState.getCardsRevealedAfterStartingEffect(gsPlayerId)); } catch (Exception e) { /* ignore */ }
                            // V67x (Steve, 2026-05-03): getAllPermanentCards() returns BOTH players'
                            // cards. Filter to only my cards so opponent's Rise/Revenge Of The
                            // Sith doesn't trigger V67q for me.
                            try {
                                for (PhysicalCard pc : gameState.getAllPermanentCards()) {
                                    if (pc != null && gsPlayerId.equals(pc.getOwner())) {
                                        v67qScanCards.add(pc);
                                    }
                                }
                            } catch (Exception e) { /* ignore */ }
                            for (PhysicalCard sc : v67qScanCards) {
                                if (sc == null) continue;
                                if (sc.getOwner() != null && !gsPlayerId.equals(sc.getOwner())) continue;
                                String t = sc.getTitle();
                                if (t == null) continue;
                                String tl = t.toLowerCase(java.util.Locale.ROOT);
                                for (String marker : sithMarkers) {
                                    if (tl.contains(marker)) {
                                        v67qHasSithStart = true;
                                        break;
                                    }
                                }
                                if (v67qHasSithStart) break;
                            }
                        }
                    } catch (Exception e) { logger.debug("V67q scan error: {}", e.getMessage()); }

                    if (v67qHasSithStart) {
                        boolean v67qIsPalace = locTitleLower.contains("palace");
                        if (v67oIsBg && !v67qIsPalace) {
                            action.addReasoning("V67q SITH START (RotS/RevotS): " + locTitle
                                + " is non-Palace battleground — starting Effect WILL trigger here!",
                                600.0f);
                            logger.warn("V67q SITH START: {} non-Palace BG → +600 (RotS/RevotS triggers)", locTitle);
                        } else if (v67qIsPalace) {
                            action.addReasoning("V67q SITH START PALACE: " + locTitle
                                + " is a Palace — RotS/RevotS Effect WON'T trigger here, avoid!",
                                -350.0f);
                            logger.warn("V67q SITH START: {} is PALACE → -350 (Effect won't trigger)", locTitle);
                        } else {
                            action.addReasoning("V67q SITH START NON-BG: " + locTitle
                                + " is not a battleground — RotS/RevotS Effect cannot trigger!",
                                -300.0f);
                            logger.warn("V67q SITH START: {} non-BG → -300 (Effect cannot trigger)", locTitle);
                        }
                    }
                }
            } catch (Exception e) {
                logger.warn("V29.14 STARTING LOC: Error looking up card {}: {}", cardId, e.getMessage());
            }

            actions.add(action);
        }

        return actions;
    }

    /**
     * Take into hand - prefer high-value cards.
     * Handles both in-play cards (by card ID) and reserve deck cards (by blueprint).
     * CRITICAL: Only score selectable cards - GEMP rejects non-selectable selections!
     */
    private List<EvaluatedAction> evaluateTakeIntoHand(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        SwccgGame game = context.getGame();
        List<String> cardIds = context.getCardIds();
        List<String> blueprints = context.getBlueprints();
        List<String> testingTexts = context.getTestingTexts();  // CARD TITLES from GEMP!

        logger.info("🔍 evaluateTakeIntoHand: {} cards, {} blueprints, {} testingTexts",
                   cardIds != null ? cardIds.size() : 0,
                   blueprints != null ? blueprints.size() : 0,
                   testingTexts != null ? testingTexts.size() : 0);

        for (int i = 0; i < cardIds.size(); i++) {
            String cardId = cardIds.get(i);
            String blueprintId = (blueprints != null && i < blueprints.size()) ? blueprints.get(i) : null;

            // LOOK UP CARD NAME FROM BLUEPRINT LIBRARY - this PROVES we can identify cards!
            String cardTitle = null;
            SwccgCardBlueprint blueprint = null;

            // Method 1: For regular cardIds, look up the card in game state
            if (gameState != null && cardId != null && !cardId.startsWith("temp")) {
                try {
                    PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                    if (card != null) {
                        cardTitle = card.getTitle();
                        blueprint = card.getBlueprint();
                        logger.info("✅ CARD LOOKUP[{}]: cardId={} -> '{}'", i, cardId, cardTitle);
                    }
                } catch (NumberFormatException e) {
                    // Card ID is not a number - expected for temp IDs
                }
            }

            // Method 2: For temp IDs or if Method 1 failed, look up from blueprintId in library
            if (cardTitle == null && blueprintId != null && !blueprintId.isEmpty() && !"inPlay".equals(blueprintId)) {
                cardTitle = getCardNameFromBlueprint(context, blueprintId);
                if (cardTitle != null) {
                    blueprint = getBlueprintFromId(context, blueprintId);
                }
            }

            // Fallback: use blueprintId as display name if we still don't have a title
            if (cardTitle == null) {
                cardTitle = (blueprintId != null && !blueprintId.isEmpty()) ? "bp=" + blueprintId : cardId;
                logger.warn("⚠️ Could not look up card name for [{}]: cardId={}, bp={}", i, cardId, blueprintId);
            }

            // CRITICAL: Skip non-selectable cards! But still log the REAL card name
            if (!isCardSelectable(context, i)) {
                logger.info("⚠️ Skipping non-selectable[{}]: '{}' (cardId={}, bp={})", i, cardTitle, cardId, blueprintId);
                continue;
            }

            // Now we have card info
            Float destiny = null;
            Float power = null;
            Float ability = null;
            CardCategory category = null;

            // Log the final card title we determined
            logger.info("📋 evaluateTakeIntoHand[{}]: cardId='{}', blueprintId='{}', TITLE='{}'",
                i, cardId, blueprintId, cardTitle);

            // Extract card properties from blueprint (if we have one)
            if (blueprint != null) {
                try {
                    destiny = blueprint.getDestiny();
                } catch (UnsupportedOperationException e) {
                    // Card type doesn't support destiny
                }
                if (blueprint.hasPowerAttribute()) {
                    power = blueprint.getPower();
                }
                if (blueprint.hasAbilityAttribute()) {
                    ability = blueprint.getAbility();
                }
                category = blueprint.getCardCategory();
            }

            // Check for priority cards by blueprintId if we have one but couldn't get card info
            boolean isPriorityByBlueprint = false;
            int priorityScoreByBlueprint = 0;
            if (blueprintId != null) {
                isPriorityByBlueprint = AiPriorityCards.isPriorityCard(blueprintId);
                priorityScoreByBlueprint = AiPriorityCards.getProtectionScore(blueprintId);
            }

            // Create action with proper display text
            float baseScore = 50.0f;
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.SELECT_CARD,
                baseScore,
                "Take " + cardTitle + " into hand"
            );
            action.setCardName(cardTitle);
            if (blueprintId != null) {
                action.setBlueprintId(blueprintId);
            }

            // === SCORING LOGIC ===

            // High destiny cards are VERY valuable - they're used for destiny draws
            if (destiny != null) {
                if (destiny >= 6) {
                    action.addReasoning("Excellent destiny (" + destiny + ")", 60.0f);
                } else if (destiny >= 5) {
                    action.addReasoning("High destiny (" + destiny + ")", 40.0f);
                } else if (destiny >= 4) {
                    action.addReasoning("Good destiny (" + destiny + ")", 20.0f);
                } else if (destiny >= 3) {
                    action.addReasoning("Decent destiny (" + destiny + ")", 5.0f);
                } else if (destiny <= 1) {
                    action.addReasoning("Low destiny (" + destiny + ")", -20.0f);
                }
            }

            // Priority cards (Houjix, Sense, etc.) are always good
            // Check by title first (when we have the actual card)
            if (!cardTitle.equals("Unknown") && !cardTitle.startsWith("Card ") &&
                AiPriorityCards.isPriorityCardByTitle(cardTitle)) {
                int priorityScore = AiPriorityCards.getProtectionScoreByTitle(cardTitle);
                action.addReasoning("Priority card: " + cardTitle, priorityScore * 0.5f);
            } else if (isPriorityByBlueprint) {
                // Check by blueprint ID (when we only have the blueprintId)
                action.addReasoning("Priority card (by ID: " + blueprintId + ")", priorityScoreByBlueprint * 0.5f);
            }

            // Prefer characters with high power
            if (category == CardCategory.CHARACTER && power != null) {
                if (power >= 6) {
                    action.addReasoning("High power character (" + power + ")", 30.0f);
                } else if (power >= 4) {
                    action.addReasoning("Strong character (" + power + ")", 15.0f);
                }
            }

            // Prefer characters with high ability (can draw battle destiny)
            if (category == CardCategory.CHARACTER && ability != null && ability >= 4) {
                action.addReasoning("High ability (" + ability + ") - draws battle destiny", 25.0f);
            }

            // Locations are often good early game
            if (category == CardCategory.LOCATION) {
                int turnNumber = context.getTurnNumber();
                if (turnNumber <= 3) {
                    action.addReasoning("Location (good early game)", 20.0f);
                } else {
                    action.addReasoning("Location", 5.0f);
                }
            }

            // === V22.6: UNIVERSAL LOCATION PRIORITY FOR OBJECTIVE PULLS ===
            // When an objective offers multiple cards to pull from the reserve deck,
            // locations (systems, sites, sectors) should ALWAYS be pulled first.
            // Locations are prerequisites — effects and characters deploy ON locations,
            // so without the location on table first, those other pulls are wasted.
            // Example: Bespin system must be pulled before Alert My Star Destroyer,
            // because AMSD deploys on Bespin system.
            // This is universal for ALL objectives, not just Bespin-related ones.
            // If the location is NOT among the reserve deck options, the game engine
            // already filtered it out (it's in hand, on table, or not in deck), so
            // pulling other cards is fine — no extra hand/table checks needed here.
            if (category == CardCategory.LOCATION) {
                action.addReasoning("V22.6 LOCATION PRIORITY: locations are prerequisites — pull before effects/characters", 500.0f);
                logger.warn("🌍 V22.6 LOCATION PRIORITY: {} gets +500 (always pull locations first from objective)", cardTitle);
            }

            // === V22.6: FAILED PULL AVOIDANCE (DeckOracle) ===
            // If we've tried to pull this card 2+ times and failed, it's likely not in the
            // reserve deck. Stop wasting actions trying to pull it.
            com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle oracle = context.getDeckOracle();
            if (oracle != null && blueprintId != null && oracle.shouldAvoidPulling(blueprintId)) {
                action.addReasoning("V22.6 FAILED PULL: tried 2+ times, card likely unavailable — skipping", -500.0f);
                logger.warn("📚 V22.6 FAILED PULL BLOCK: {} has failed 2+ pull attempts — score crushed (-500)", cardTitle);
            } else if (oracle != null && cardTitle != null && oracle.shouldAvoidPullingByTitle(cardTitle)) {
                action.addReasoning("V22.6 FAILED PULL (by title): tried 2+ times, card likely unavailable", -500.0f);
                logger.warn("📚 V22.6 FAILED PULL BLOCK: '{}' has failed 2+ pull attempts by title — score crushed (-500)", cardTitle);
            }

            // === V24.1: CARD-SPECIFIC PULL PREFERENCES (TDIGWATT) ===
            // These priorities ensure the correct TDIGWATT setup sequence:
            // Endor Shield → Piett first (matches Executor for AMSD)
            // Piett → Gherant first (deploys an Executor site = free location)
            String pullDecisionText = context.getDecisionText() != null ?
                context.getDecisionText().toLowerCase(java.util.Locale.ROOT) : "";
            String cardTitleLower = cardTitle != null ? cardTitle.toLowerCase(java.util.Locale.ROOT) : "";

            // V24.1A: Endor Shield admiral pull — Piett first, Chiraneau backup
            // V24.12: GEMP decision text is just "Choose card to take into hand" — no "admiral"
            // in it. So also detect admiral pulls by checking if this card IS an admiral.
            // The Endor Shield action restricts choices to admirals, so if Piett/Chiraneau
            // are among the options, we know it's an admiral pull.
            boolean isAdmiralPull = (pullDecisionText.contains("admiral") && pullDecisionText.contains("reserve"));
            // V24.12: GEMP text is generic — detect admiral pulls by known admiral names in card title
            if (!isAdmiralPull) {
                if (cardTitleLower.contains("admiral") || cardTitleLower.contains("piett")
                    || cardTitleLower.contains("chiraneau") || cardTitleLower.contains("ozzel")
                    || cardTitleLower.contains("motti") || cardTitleLower.contains("firmus")) {
                    isAdmiralPull = true;
                }
            }
            if (isAdmiralPull) {
                if (cardTitleLower.contains("piett")) {
                    action.addReasoning("V24.12 ADMIRAL PULL: Piett is #1 pick — matches Executor for AMSD!", 300.0f);
                    logger.warn("V24.12 ADMIRAL PULL: Piett gets +300 — best AMSD pilot for Executor!");
                } else if (cardTitleLower.contains("chiraneau")) {
                    action.addReasoning("V24.12 ADMIRAL PULL: Chiraneau is backup — can pilot Executor manually", 150.0f);
                    logger.warn("V24.12 ADMIRAL PULL: Chiraneau gets +150 — backup pilot");
                } else if (cardTitleLower.contains("ozzel")) {
                    action.addReasoning("V24.12 ADMIRAL PULL: Ozzel matches Executor — decent AMSD option", 100.0f);
                    logger.warn("V24.12 ADMIRAL PULL: Ozzel gets +100");
                }
            }

            // V24.1B: Piett's commander pull — Gherant first (pulls Executor site = free location)
            if ((pullDecisionText.contains("commander") || pullDecisionText.contains("admiral's order")) &&
                pullDecisionText.contains("reserve")) {
                if (cardTitleLower.contains("gherant")) {
                    action.addReasoning("V24.1 PIETT PULL: Gherant deploys an Executor site — free location + force generation!", 400.0f);
                    logger.warn("V24.1 COMMANDER PULL: Gherant gets +400 — pulls Executor site on deploy!");
                }
            }

            // === V24.2B: LANDO/LOBOT PULL PRIORITY (TDIGWATT) ===
            // Lando and Lobot are key to flipping the TDIGWATT objective.
            // Lando can move to unoccupied CC sites at start of control phase = 3-site drains.
            // Both deploy cheap. Prioritize pulling them from reserve when available.
            // V47: BUT don't pull Lando if he'd be alone at CC — he gets clobbered!
            com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer pullObjAnalyzer =
                context.getObjectiveAnalyzer();
            if (pullObjAnalyzer != null && pullObjAnalyzer.isAnalyzed()
                && pullObjAnalyzer.needsBespinSystemPresence()) {
                if (cardTitleLower.contains("lando")) {
                    // V47: Check if we have ANY friendly characters at Cloud City sites
                    boolean friendlyAtCC = false;
                    String pullPlayerId = context.getPlayerId();
                    if (game != null && gameState != null && pullPlayerId != null) {
                        try {
                            for (PhysicalCard checkLoc : gameState.getLocationsInOrder()) {
                                if (checkLoc == null || checkLoc.getTitle() == null) continue;
                                String checkLocLower = checkLoc.getTitle().toLowerCase(java.util.Locale.ROOT);
                                boolean isCCsite = checkLocLower.contains("cloud city") || checkLocLower.contains("upper walkway")
                                    || checkLocLower.contains("carbonite") || checkLocLower.contains("security tower")
                                    || checkLocLower.contains("dining room") || checkLocLower.contains("platform")
                                    || checkLocLower.contains("lower corridor");
                                if (!isCCsite) continue;
                                java.util.List<PhysicalCard> siteCards = gameState.getCardsAtLocation(checkLoc);
                                if (siteCards != null) {
                                    for (PhysicalCard sc : siteCards) {
                                        if (sc != null && pullPlayerId.equals(sc.getOwner()) && sc.getBlueprint() != null
                                            && sc.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                            friendlyAtCC = true;
                                            break;
                                        }
                                    }
                                }
                                if (friendlyAtCC) break;
                            }
                        } catch (Exception e) {
                            logger.debug("V47: Error checking CC friendlies: {}", e.getMessage());
                        }
                    }

                    // V47: Also check if we have chars in hand + enough force to deploy both
                    boolean hasHandBuddy = false;
                    int forceAvailable = 0;
                    if (!friendlyAtCC && game != null && gameState != null) {
                        try {
                            forceAvailable = context.getForcePileSize();
                            java.util.List<PhysicalCard> hand = gameState.getHand(pullPlayerId);
                            if (hand != null) {
                                for (PhysicalCard hc : hand) {
                                    if (hc != null && hc.getBlueprint() != null
                                        && hc.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                        hasHandBuddy = true;
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.debug("V47: Error checking hand chars: {}", e.getMessage());
                        }
                    }

                    if (friendlyAtCC) {
                        action.addReasoning("V24.2 TDIGWATT: Lando is KEY — moves to 3rd CC site for extra drains + occupation!", 250.0f);
                        logger.warn("V24.2 PULL: Lando gets +250 — friendlies at CC, safe to pull!");
                    } else if (hasHandBuddy && forceAvailable >= 5) {
                        action.addReasoning("V47 LANDO PULL OK: No CC friendlies but have char in hand + force to deploy both!", 250.0f);
                        logger.warn("V47 LANDO PULL: {} force available + char in hand — OK to pull Lando with buddy!", forceAvailable);
                    } else {
                        action.addReasoning("V47 LANDO PULL BLOCK: No friendlies at CC, no buddy in hand or not enough force — Lando would die alone!", -9999.0f);
                        logger.warn("V47 LANDO PULL BLOCK: No CC friendlies, handBuddy={}, force={} — don't pull Lando!", hasHandBuddy, forceAvailable);
                    }
                } else if (cardTitleLower.contains("lobot")) {
                    action.addReasoning("V24.2 TDIGWATT: Lobot deploys cheap — helps flip objective!", 200.0f);
                    logger.warn("V24.2 PULL: Lobot gets +200 — cheap deploy, helps flip!");
                }
            }

            // Log the decision
            logger.debug("🎯 {} ({}): score={}, destiny={}, power={}",
                        cardTitle, blueprintId != null ? blueprintId : cardId,
                        action.getScore(),
                        destiny != null ? destiny : "?",
                        power != null ? power : "?");

            actions.add(action);
        }

        // Sort by score descending for logging
        actions.sort((a, b) -> Float.compare(b.getScore(), a.getScore()));
        if (!actions.isEmpty()) {
            logger.info("✅ Best take into hand: {} (score: {})",
                       actions.get(0).getCardName(), actions.get(0).getScore());
        }

        return actions;
    }

    /**
     * Lost pile selection - prefer low-value cards.
     */
    private List<EvaluatedAction> evaluateLostPileSelection(DecisionContext context) {
        return evaluateForceLoss(context);  // Same logic
    }

    /**
     * Unknown decision type - neutral scoring with card name lookup.
     * CRITICAL: Only score selectable cards - GEMP rejects non-selectable selections!
     *
     * Ported from Python _evaluate_unknown() which scores based on card type.
     */
    private List<EvaluatedAction> evaluateUnknown(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        SwccgGame game = context.getGame();
        List<String> cardIds = context.getCardIds();
        List<String> blueprints = context.getBlueprints();
        List<String> testingTexts = context.getTestingTexts();  // CARD TITLES from GEMP!

        // Determine base score - higher for gain/select decisions
        String textLower = context.getDecisionText() != null ? context.getDecisionText().toLowerCase(Locale.ROOT) : "";
        boolean isLossDecision = textLower.contains("lose") || textLower.contains("lost") ||
                                 textLower.contains("place in") || textLower.contains("put on");

        logger.info("🔍 evaluateUnknown: {} cards, {} blueprints, {} testingTexts for '{}' (loss={})",
                   cardIds != null ? cardIds.size() : 0,
                   blueprints != null ? blueprints.size() : 0,
                   testingTexts != null ? testingTexts.size() : 0,
                   context.getDecisionText(),
                   isLossDecision);

        int selectableCount = 0;
        int skippedCount = 0;

        for (int i = 0; i < cardIds.size(); i++) {
            String cardId = cardIds.get(i);
            String blueprintId = (blueprints != null && i < blueprints.size()) ? blueprints.get(i) : null;

            // LOOK UP CARD NAME FROM BLUEPRINT LIBRARY - this PROVES we can identify cards!
            String cardTitle = null;
            SwccgCardBlueprint blueprint = null;

            // Method 1: For regular cardIds, look up the card in game state
            if (gameState != null && cardId != null && !cardId.startsWith("temp")) {
                try {
                    PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                    if (card != null) {
                        cardTitle = card.getTitle();
                        blueprint = card.getBlueprint();
                        logger.info("✅ CARD LOOKUP[{}]: cardId={} -> '{}'", i, cardId, cardTitle);
                    }
                } catch (NumberFormatException e) {
                    // Card ID is not a number - expected for temp IDs
                }
            }

            // Method 2: For temp IDs or if Method 1 failed, look up from blueprintId in library
            if (cardTitle == null && blueprintId != null && !blueprintId.isEmpty() && !"inPlay".equals(blueprintId)) {
                cardTitle = getCardNameFromBlueprint(context, blueprintId);
                if (cardTitle != null) {
                    blueprint = getBlueprintFromId(context, blueprintId);
                }
            }

            // Fallback: use blueprintId as display name if we still don't have a title
            if (cardTitle == null) {
                cardTitle = (blueprintId != null && !blueprintId.isEmpty()) ? "bp=" + blueprintId : cardId;
                logger.warn("⚠️ Could not look up card name for [{}]: cardId={}, bp={}", i, cardId, blueprintId);
            }

            // CRITICAL: Skip non-selectable cards! But still log the REAL card name
            if (!isCardSelectable(context, i)) {
                skippedCount++;
                logger.info("⚠️ Skipping non-selectable[{}]: '{}' (cardId={}, bp={})", i, cardTitle, cardId, blueprintId);
                continue;
            }
            selectableCount++;

            // Now we have card info
            Float destiny = null;
            Float power = null;
            CardCategory category = null;

            // Log the final card title we determined
            logger.info("📋 evaluateUnknown[{}]: cardId='{}', blueprintId='{}', TITLE='{}'",
                i, cardId, blueprintId, cardTitle);

            // Extract card properties from blueprint (if we have one)
            if (blueprint != null) {
                try {
                    destiny = blueprint.getDestiny();
                } catch (UnsupportedOperationException e) {
                    // Card type doesn't support destiny
                }
                if (blueprint.hasPowerAttribute()) {
                    power = blueprint.getPower();
                }
                category = blueprint.getCardCategory();
            }

            // Base score beats PassEvaluator (~5-20)
            float baseScore = 30.0f;
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.UNKNOWN,
                baseScore,
                "Select " + cardTitle
            );
            action.setCardName(cardTitle);
            if (blueprintId != null) {
                action.setBlueprintId(blueprintId);
            }

            // V70 (Steve, 2026-05-12): ONE-WEAPON-PER-CHARACTER rule.
            // Mirror of rando/CardSelectionEvaluator.java. See helper docs.
            String v70UnkReason = v70CheckWeaponDeviceBlock(
                game, context.getPlayerId(), category, blueprint);
            if (v70UnkReason != null) {
                action.addReasoning(
                    "V70 NO 2ND WEAPON: " + v70UnkReason + " — '" + cardTitle + "'",
                    -9999.0f);
                logger.warn("V70 BLOCK (evaluateUnknown, {}): '{}' (bp {}) — {}",
                    category, cardTitle, blueprintId, v70UnkReason);
                actions.add(action);
                continue;
            }

            // === V22 STARTING EFFECTS: BAN + OBJECTIVE-AWARE PREFERENCE ===
            if (context.getTurnNumber() <= 0 && cardTitle != null) {
                String titleCheck = cardTitle.toLowerCase(java.util.Locale.ROOT);
                if (titleCheck.contains("no escape") || titleCheck.contains("coarse and rough")) {
                    action.addReasoning("V22 STARTING BAN: " + cardTitle + " banned!", -600.0f);
                    logger.warn("V22 STARTING BAN: Blocking {} (-600)", cardTitle);
                    actions.add(action);
                    continue;
                }
                if (titleCheck.contains("endor shield") || titleCheck.contains("alert my star destroyer")) {
                    action.addReasoning("V22 PREFERRED STARTING EFFECT: " + cardTitle, 200.0f);
                    logger.warn("V22 PREFERRED START: {} (+200)", cardTitle);
                }

                // V80 (Steve, 2026-05-15): SKYWALKER EPIC EVENT REQUIRED EFFECTS.
                // Mirror of Rando V80. A Cunning Warrior + A Good Friend are
                // must-picks when Rise Of Skywalker offers free Effect deploys.
                if (titleCheck.contains("cunning warrior") || titleCheck.contains("good friend")) {
                    action.addReasoning("V80 SKYWALKER STARTING EFFECT: " + cardTitle + " — required for Rey/Luke Saga deck!", 1000.0f);
                    logger.warn("V80 SKYWALKER STARTING: {} → +1000", cardTitle);
                }

                // V25: HUNT DOWN V — Specific starting effects
                // The three effects that make this deck work:
                // 1. "There Are Many Hunting You Now" — hatred card engine
                // 2. "I Am Your Father" — key interrupt/effect for Vader synergy
                // 3. "Crush The Rebellion" — force drain enhancement
                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer startHDAnalyzer =
                    context.getObjectiveAnalyzer();
                if (startHDAnalyzer != null && startHDAnalyzer.isAnalyzed() && startHDAnalyzer.isHuntDownV()) {
                    if (titleCheck.contains("there are many hunting you now")
                        || titleCheck.contains("i am your father")
                        || titleCheck.contains("crush the rebellion")) {
                        action.addReasoning("V25 HUNT DOWN STARTING EFFECT: " + cardTitle + " — REQUIRED!", 500.0f);
                        logger.warn("V25 HUNT DOWN START: {} is a REQUIRED starting effect (+500)", cardTitle);
                    } else {
                        // Penalize other effects so the three required ones always win
                        action.addReasoning("V25 HUNT DOWN: Not a required starting effect for Hunt Down", -300.0f);
                        logger.warn("V25 HUNT DOWN START: {} is NOT a required starting effect (-300)", cardTitle);
                    }
                }

                // V22: Check if this starting effect's game text references
                // objective-relevant locations. Effects that pull locations needed
                // for our objective should be strongly preferred.
                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer startObjAnalyzer =
                    context.getObjectiveAnalyzer();
                if (startObjAnalyzer != null && startObjAnalyzer.isAnalyzed() && blueprint != null) {
                    String effectGameText = blueprint.getGameText();
                    if (effectGameText != null) {
                        String effectTextLower = effectGameText.toLowerCase(java.util.Locale.ROOT);
                        // Check if the effect's game text mentions objective-relevant location fragments
                        for (String fragment : startObjAnalyzer.getFlipConditionLocationFragments()) {
                            if (effectTextLower.contains(fragment)) {
                                action.addReasoning("V22 OBJECTIVE-SYNERGY STARTING EFFECT: references '" + fragment + "'", 250.0f);
                                logger.warn("V22 OBJECTIVE START: {} references objective location '{}' (+250)", cardTitle, fragment);
                                break;
                            }
                        }
                        // Check if it can pull cards needed for objective
                        for (String required : startObjAnalyzer.getRequiredCardsOnTable()) {
                            if (effectTextLower.contains(required)) {
                                action.addReasoning("V22 OBJECTIVE-SYNERGY: pulls required card '" + required + "'", 200.0f);
                                logger.warn("V22 OBJECTIVE START: {} can pull required card '{}' (+200)", cardTitle, required);
                                break;
                            }
                        }
                        // Check if it mentions "deploy" + "location", "site", or "system" (location-pulling effect)
                        if (effectTextLower.contains("deploy") && (effectTextLower.contains("location") || effectTextLower.contains("site") || effectTextLower.contains("system"))) {
                            action.addReasoning("V22 LOCATION-PULLING EFFECT: deploys locations", 100.0f);
                            logger.warn("V22 OBJECTIVE START: {} appears to deploy locations (+100)", cardTitle);
                        }
                        // V22: Any effect that interacts with Reserve Deck gets a bonus
                        if (effectTextLower.contains("reserve")) {
                            action.addReasoning("V22 RESERVE DECK ACCESS: can pull from reserve", 50.0f);
                            logger.warn("V22 OBJECTIVE START: {} references Reserve Deck (+50)", cardTitle);
                        }
                        // V22: Effects that boost force generation are valuable early game
                        if (effectTextLower.contains("force generation") || effectTextLower.contains("activate")
                            || effectTextLower.contains("force icon") || effectTextLower.contains("adds one to")) {
                            action.addReasoning("V22 FORCE GENERATION: boosts early force economy", 25.0f);
                            logger.warn("V22 OBJECTIVE START: {} boosts force generation (+25)", cardTitle);
                        }

                        // === V29.15: EPIC EVENT STARTING EFFECT/INTERRUPT ===
                        // Starting effects or interrupts whose game text mentions "epic"
                        // or deploys a key card like "Force Is Strong In My Family"
                        // are critical for decks built around Epic Events.
                        if (effectTextLower.contains("epic")
                            || effectTextLower.contains("force is strong in my family")
                            || effectTextLower.contains("force is strong")) {
                            action.addReasoning("V43 EPIC: starting card deploys Epic Event — critical for deck strategy!", 1500.0f);
                            logger.warn("V43 EPIC START: {} references Epic Event in game text — HARD PREFER (+1500)", cardTitle);
                        }
                    }
                }
            }
            // V24.5: No randomness — deterministic decisions only

            // Score based on card type (like Python)
            if (isLossDecision) {
                // For loss decisions: prefer effects/interrupts
                if (category == CardCategory.EFFECT || category == CardCategory.INTERRUPT) {
                    action.addReasoning("Effect/Interrupt - OK to lose", 25.0f);
                } else if (category == CardCategory.CHARACTER) {
                    action.addReasoning("Character - avoid losing", -15.0f);
                } else if (category == CardCategory.STARSHIP) {
                    action.addReasoning("Starship - avoid losing", -15.0f);
                } else if (category == CardCategory.VEHICLE) {
                    action.addReasoning("Vehicle - avoid losing", -10.0f);
                } else if (category == CardCategory.LOCATION) {
                    action.addReasoning("Location - avoid losing", -20.0f);
                }
            } else {
                // For gain/select decisions: prefer deployables
                if (category == CardCategory.CHARACTER) {
                    action.addReasoning("Character - valuable", 10.0f);
                } else if (category == CardCategory.STARSHIP) {
                    action.addReasoning("Starship - valuable", 8.0f);
                } else if (category == CardCategory.LOCATION) {
                    action.addReasoning("Location - valuable", 10.0f);
                }
            }

            // === V25: HUNT DOWN V — LIGHTSABER PRIORITY (evaluateUnknown path) ===
            // For Hunt Down V, lightsabers are critical for the deck engine:
            // - Vader + lightsaber cancels drain bonuses (back side)
            // - Hatred engine needs lightsabers stacked
            // - "I Am Your Father" pulls Vader's Lightsaber
            if (cardTitle != null) {
                String lsTitleLower = cardTitle.toLowerCase(java.util.Locale.ROOT);
                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer lsObjAnalyzer =
                    context.getObjectiveAnalyzer();
                if (lsObjAnalyzer != null && lsObjAnalyzer.isAnalyzed() && lsObjAnalyzer.isHuntDownV()
                    && lsTitleLower.contains("lightsaber")) {
                    if (isLossDecision) {
                        action.addReasoning("V25 HUNT DOWN: PROTECT LIGHTSABER from loss!", -300.0f);
                        logger.warn("V25 HUNT DOWN UNKNOWN-LOSS: {} is a lightsaber — PROTECT (-300)", cardTitle);
                    } else {
                        action.addReasoning("V25 HUNT DOWN: LIGHTSABER — critical for deck engine!", 200.0f);
                        logger.warn("V25 HUNT DOWN UNKNOWN-GAIN: {} is a lightsaber — PRIORITY (+200)", cardTitle);
                    }
                }
            }

            // === V24.10: CC SITE SELECTION — CONTEXT-AWARE (evaluateUnknown path) ===
            // Slip Sliding GRABS Dining Room — guarantees Upper Walkway + Dining Room as starting sites.
            // I'm Sorry then pulls other interior CC sites in-game.
            if (cardTitle != null) {
                String ctLower = cardTitle.toLowerCase(java.util.Locale.ROOT);
                if (ctLower.contains("cloud city") &&
                    (textLower.contains("sorry") || textLower.contains("interior") ||
                     textLower.contains("cloud city") || textLower.contains("battleground"))) {

                    boolean isSlipSliding = textLower.contains("slip") || textLower.contains("battleground") ||
                        context.getTurnNumber() <= 0;
                    boolean isImSorry = textLower.contains("sorry") || textLower.contains("interior");

                    if (isImSorry && !isSlipSliding) {
                        // I'm Sorry pulls other interior CC sites (Dining Room already on table from Slip Sliding)
                        if (ctLower.contains("dining room")) {
                            // Dining Room should already be on table — low priority for I'm Sorry
                            action.addReasoning("V24.10 I'M SORRY: Dining Room likely already on table", -50.0f);
                        } else if (ctLower.contains("security tower")) {
                            // V24.13: Security Tower = force-gen only, deploy LAST for far-end placement
                            action.addReasoning("V24.13 I'M SORRY: Security Tower — force-gen only, deploy LAST!", -30.0f);
                        } else if (ctLower.contains("carbonite chamber")) {
                            // V24.13: Carbonite Chamber = key battleground, pull FIRST!
                            action.addReasoning("V24.13 I'M SORRY: Carbonite Chamber — priority battleground!", 150.0f);
                        } else {
                            action.addReasoning("V24.10 I'M SORRY: Pull interior CC site — expand drain sites!", 100.0f);
                        }
                    } else if (isSlipSliding) {
                        if (ctLower.contains("dining room")) {
                            // Slip Sliding GRABS Dining Room — guarantees best starting pair!
                            action.addReasoning("V24.10 SLIP SLIDING: Dining Room — guarantees best starting CC site pair!", 300.0f);
                            logger.warn("V24.10 SLIP SLIDING: Dining Room +300 — grab it as starting location!");
                        } else {
                            action.addReasoning("V24.10 SLIP SLIDING: Other CC site — Dining Room is better", -50.0f);
                        }
                    }
                }
            }

            // Check for priority cards
            if (blueprintId != null && AiPriorityCards.isPriorityCard(blueprintId)) {
                int priorityScore = AiPriorityCards.getProtectionScore(blueprintId);
                action.addReasoning("Priority card", priorityScore * 0.3f);
            }

            // === V24.10: AMSD PIETT-ONLY SAFETY NET (replaces V24.8) ===
            // If AMSD is in play and we're choosing characters during deploy phase,
            // enforce Piett-only regardless of decision text. This catches cases where
            // the AMSD routing catch above didn't fire (e.g., we're already in evaluateUnknown).
            if (context.getPhase() == Phase.DEPLOY && blueprint != null && category == CardCategory.CHARACTER) {
                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle safetyOracle = context.getDeckOracle();
                if (safetyOracle != null && safetyOracle.isAnalyzed()) {
                    boolean amsdActive = safetyOracle.isCardInPlay("Alert My Star Destroyer")
                        || safetyOracle.isCardInPlay("Alert My Star Destroyer!");
                    if (amsdActive) {
                        String pilotNameLower = (cardTitle != null) ? cardTitle.toLowerCase(java.util.Locale.ROOT) : "";
                        if (pilotNameLower.contains("piett")) {
                            action.addReasoning("V24.10 AMSD SAFETY NET: PIETT — approved for AMSD!", 500.0f);
                            logger.warn("V24.10 AMSD SAFETY NET: Piett detected — APPROVED (+500)");
                        } else {
                            action.addReasoning("V24.10 AMSD SAFETY NET: " + cardTitle + " is NOT Piett — AMSD requires Piett only!", -9999.0f);
                            logger.warn("V24.10 AMSD SAFETY NET: {} is NOT Piett — HARD BLOCK (-9999)", cardTitle);
                        }
                    }
                }
            }

            actions.add(action);
        }

        logger.info("🔍 evaluateUnknown: {} selectable, {} skipped (non-selectable)",
                   selectableCount, skippedCount);

        if (actions.isEmpty()) {
            logger.warn("⚠️ evaluateUnknown: No selectable cards! Decision may fail.");
        }

        return actions;
    }

    /**
     * Check if a location is likely a battleground.
     */
    private boolean isLikelyBattleground(SwccgCardBlueprint blueprint) {
        // Sites and systems are often battlegrounds
        // This is a heuristic - actual battleground status comes from card data
        return blueprint != null && blueprint.getCardCategory() == CardCategory.LOCATION;
    }

    /**
     * Check if a card at given index is selectable.
     * CRITICAL: GEMP rejects selection of non-selectable cards!
     */
    private boolean isCardSelectable(DecisionContext context, int index) {
        List<Boolean> selectable = context.getSelectable();
        if (selectable == null || selectable.isEmpty()) {
            // No selectable info - assume all are selectable
            return true;
        }
        if (index >= selectable.size()) {
            // Index out of bounds - assume selectable
            return true;
        }
        Boolean isSelectable = selectable.get(index);
        return isSelectable == null || isSelectable;
    }

    /**
     * Reserve deck selection - evaluate cards by blueprint.
     * Used when selecting from Reserve Deck (e.g., deploying shields via starting effect).
     * Uses DeployPhasePlanner when available to select cards that fit the deployment plan.
     */
    private List<EvaluatedAction> evaluateReserveDeckSelection(DecisionContext context, String textLower) {
        List<EvaluatedAction> actions = new ArrayList<>();
        List<String> blueprints = context.getBlueprints();
        ShieldStrategy shieldStrategy = context.getShieldStrategy();
        DeployPhasePlanner planner = context.getDeployPhasePlanner();
        SwccgGame game = context.getGame();
        Side side = context.getSide();
        String playerId = context.getPlayerId();
        int turnNumber = context.getTurnNumber();

        logger.info("[CardSelectionEvaluator] Evaluating Reserve Deck selection: {}", textLower);

        // V67ay (Steve, 2026-05-08): UNIVERSAL ONE-WEAPON RULE for reserve-deck pick.
        // Mirrors Rando's V67ay. See its CardSelectionEvaluator for full rationale.
        boolean v67ayAllArmed = false;
        int v67ayUnarmed = 0;
        int v67ayArmed = 0;
        if (game != null && playerId != null) {
            try {
                GameState gs = game.getGameState();
                if (gs != null) {
                    for (PhysicalCard pc : gs.getAllPermanentCards()) {
                        if (pc == null || pc.getBlueprint() == null) continue;
                        if (!playerId.equals(pc.getOwner())) continue;
                        com.gempukku.swccgo.common.Zone z = pc.getZone();
                        if (z == null || !z.isInPlay()) continue;
                        if (pc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                        boolean armed = false;
                        java.util.List<PhysicalCard> atts = gs.getAttachedCards(pc);
                        if (atts != null) {
                            for (PhysicalCard a : atts) {
                                if (a != null && a.getBlueprint() != null
                                        && a.getBlueprint().getCardCategory() == CardCategory.WEAPON) {
                                    armed = true;
                                    break;
                                }
                            }
                        }
                        if (armed) v67ayArmed++; else v67ayUnarmed++;
                    }
                    v67ayAllArmed = (v67ayUnarmed == 0 && v67ayArmed > 0);
                }
            } catch (Exception e) {
                logger.debug("V67ay weapon-armed scan failed: {}", e.getMessage());
            }
        }
        if (v67ayAllArmed) {
            logger.warn("V67ay GUARD: every Chosen One character armed (armed={}, unarmed=0)",
                v67ayArmed);
        }

        // Get deployment plan if available
        DeploymentPlan plan = null;
        if (planner != null && game != null && side != null && playerId != null) {
            plan = planner.createPlan(game, playerId, side);
            if (plan != null) {
                logger.info("[CardSelectionEvaluator] Using deployment plan: strategy={}, instructions={}",
                    plan.getStrategy(), plan.getInstructions().size());
            }
        }

        // V24.10: Get card titles for smarter reserve deck selection
        List<String> reserveTestingTexts = context.getTestingTexts();

        for (int i = 0; i < blueprints.size(); i++) {
            String blueprintId = blueprints.get(i);

            // Get card title if available
            String cardTitle = null;
            if (reserveTestingTexts != null && i < reserveTestingTexts.size()) {
                cardTitle = reserveTestingTexts.get(i);
            }
            String cardTitleLower = cardTitle != null ? cardTitle.toLowerCase(java.util.Locale.ROOT) : "";

            // Use index as action ID for blueprint-based selections
            EvaluatedAction action = new EvaluatedAction(
                String.valueOf(i),
                ActionType.DEPLOY,
                50.0f,
                "Deploy " + (cardTitle != null ? cardTitle : blueprintId)
            );

            // V70 (Steve, 2026-05-12): ONE-WEAPON-PER-CHARACTER. Uses comprehensive
            // criteria search via v70CheckWeaponDeviceBlock helper.
            SwccgCardBlueprint v70Bp = null;
            CardCategory v70Cat = null;
            try {
                v70Bp = getBlueprintFromId(context, blueprintId);
                if (v70Bp != null) v70Cat = v70Bp.getCardCategory();
            } catch (Exception e) { /* ignore */ }
            String v70Reason = v70CheckWeaponDeviceBlock(game, playerId, v70Cat, v70Bp);
            if (v70Reason != null) {
                action.addReasoning(
                    "V70 NO 2ND WEAPON: " + v70Reason + " — '" + (cardTitle != null ? cardTitle : blueprintId) + "'",
                    -9999.0f);
                logger.warn("V70 BLOCK (reserve-pick, {}): '{}' (bp {}) — {}",
                    v70Cat, cardTitle, blueprintId, v70Reason);
                actions.add(action);
                continue;
            }

            // V80 (Steve, 2026-05-15): SKYWALKER EPIC EVENT REQUIRED EFFECTS.
            // Mirror of Rando V80 in evaluateReserveDeckSelection.
            if (cardTitleLower.contains("cunning warrior") || cardTitleLower.contains("good friend")) {
                action.addReasoning(
                    "V80 SKYWALKER STARTING EFFECT: " + cardTitle + " — required for Rey/Luke Saga deck!",
                    1000.0f);
                logger.warn("V80 SKYWALKER STARTING (reserve-pick): {} → +1000", cardTitle);
            }

            // === V24.10: CC SITE SELECTION — CONTEXT-AWARE ===
            // Two different effects pull CC sites from reserve:
            //   1. Slip Sliding Away (starting interrupt, turn 0): picks a CC battleground site
            //   2. I'm Sorry (V) (during game): pulls interior CC sites
            // Strategy: Slip Sliding GRABS Dining Room — guarantees Upper Walkway + Dining Room
            // as the starting CC site pair. I'm Sorry then pulls other interior sites in-game.
            if (cardTitleLower.contains("cloud city") &&
                (textLower.contains("sorry") || textLower.contains("interior") ||
                 textLower.contains("cloud city") || textLower.contains("battleground"))) {

                boolean isSlipSlidingPick = textLower.contains("slip") || textLower.contains("battleground") ||
                    context.getTurnNumber() <= 0;
                boolean isImSorryPick = textLower.contains("sorry") || textLower.contains("interior");

                if (isImSorryPick && !isSlipSlidingPick) {
                    // I'm Sorry pulls other interior CC sites (Dining Room already on table from Slip Sliding)
                    if (cardTitleLower.contains("dining room")) {
                        action.addReasoning("V24.10 I'M SORRY: Dining Room likely already on table from Slip Sliding", -50.0f);
                        logger.info("V24.10 I'M SORRY PULL: Dining Room -50 — should already be deployed");
                    } else if (cardTitleLower.contains("security tower")) {
                        // V24.13: Security Tower is purely for force generation — Rando can't
                        // typically force drain there (0 base drain). Deploy it LAST so it ends
                        // up at the far end of the site chain, not clogging movement lanes.
                        // Pull battleground sites (Carbonite Chamber, etc.) FIRST.
                        action.addReasoning("V24.13 I'M SORRY: Security Tower is force-gen only — deploy LAST!", -30.0f);
                        logger.info("V24.13 I'M SORRY: Security Tower deprioritized (-30) — pull battleground sites first");
                    } else if (cardTitleLower.contains("carbonite chamber")) {
                        // V24.13: Carbonite Chamber is a key battleground — pull FIRST!
                        // Characters deployed here can force drain and fight.
                        action.addReasoning("V24.13 I'M SORRY: Carbonite Chamber — key battleground, pull FIRST!", 150.0f);
                        logger.warn("V24.13 I'M SORRY PULL: Carbonite Chamber +150 — priority battleground!");
                    } else {
                        action.addReasoning("V24.10 I'M SORRY: Pull interior CC site — expand drain sites!", 100.0f);
                        logger.warn("V24.10 I'M SORRY PULL: {} +100 — new drain site!", cardTitle);
                    }
                } else if (isSlipSlidingPick) {
                    // Slip Sliding Away GRABS Dining Room — best starting pair!
                    if (cardTitleLower.contains("dining room")) {
                        action.addReasoning("V24.10 SLIP SLIDING: Dining Room — guarantees best starting CC site pair!", 300.0f);
                        logger.warn("V24.10 SLIP SLIDING: Dining Room +300 — grab as starting location!");
                    } else {
                        action.addReasoning("V24.10 SLIP SLIDING: Other CC site — Dining Room is better", -50.0f);
                        logger.info("V24.10 SLIP SLIDING: {} — not Dining Room (-50)", cardTitle);
                    }
                }
            }

            // === Check deployment plan first ===
            if (plan != null && !plan.getInstructions().isEmpty()) {
                DeploymentInstruction instruction = plan.getInstructionForCard(blueprintId);
                if (instruction != null) {
                    // Card is in deployment plan - high priority
                    action.addReasoning("IN DEPLOYMENT PLAN: " + plan.getStrategy(), 100.0f);
                    logger.info("[ReserveDeck] {} IN PLAN - high priority", blueprintId);
                } else if (plan.getHoldBackCards().contains(blueprintId)) {
                    // Card should be held back
                    action.addReasoning("HOLD BACK: save for later", -50.0f);
                    logger.debug("[ReserveDeck] {} should be held back", blueprintId);
                }
            }

            // === Shield scoring ===
            if (shieldStrategy != null) {
                // Check if this is a defensive shield by blueprint pattern
                float shieldScore = shieldStrategy.scoreShield(blueprintId, blueprintId, turnNumber);

                if (shieldScore > -50) {
                    // Likely a shield - use shield scoring
                    // Add to existing score rather than replacing
                    action.addReasoning("Shield scoring", shieldScore);
                    String description = shieldStrategy.getShieldDescription(blueprintId, blueprintId);
                    logger.info("[ReserveDeck] Shield {}: score={} ({})", blueprintId, shieldScore, description);
                }

                // === V51: BATTLE ORDER GATE ===
                // Battle Order requires occupying BOTH a battleground site AND a battleground system.
                // If we don't occupy both, deploying Battle Order is a waste — it does nothing.
                if (cardTitleLower.contains("battle order")) {
                    boolean hasBGSite = false;
                    boolean hasBGSystem = false;
                    try {
                        GameState gsBO = (game != null) ? game.getGameState() : null;
                        if (game != null && gsBO != null && playerId != null) {
                            for (PhysicalCard loc : gsBO.getAllPermanentCards()) {
                                if (loc == null || loc.getBlueprint() == null) continue;
                                com.gempukku.swccgo.common.Zone locZone = loc.getZone();
                                if (locZone == null || locZone != com.gempukku.swccgo.common.Zone.LOCATIONS) continue;
                                SwccgCardBlueprint locBp = loc.getBlueprint();
                                boolean isBattleground = false;
                                try {
                                    com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying mq = game.getModifiersQuerying();
                                    if (mq != null) isBattleground = mq.isBattleground(gsBO, loc, null);
                                } catch (Exception bgEx) { /* fallback: not battleground */ }
                                if (!isBattleground) continue;
                                // Check if we occupy it (have a character/starship present)
                                boolean weOccupy = false;
                                for (PhysicalCard atLoc : gsBO.getCardsAtLocation(loc)) {
                                    if (atLoc != null && playerId.equals(atLoc.getOwner())) {
                                        weOccupy = true;
                                        break;
                                    }
                                }
                                if (weOccupy) {
                                    if (locBp.getCardSubtype() == com.gempukku.swccgo.common.CardSubtype.SYSTEM) {
                                        hasBGSystem = true;
                                    } else if (locBp.getCardSubtype() == com.gempukku.swccgo.common.CardSubtype.SITE) {
                                        hasBGSite = true;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.debug("V51 BATTLE ORDER: Error checking occupation: {}", e.getMessage());
                    }
                    if (!hasBGSite || !hasBGSystem) {
                        action.addReasoning("V51 BATTLE ORDER GATE: Need BOTH a BG site AND BG system occupied!", -9999.0f);
                        logger.warn("V51 BATTLE ORDER GATE: hasBGSite={}, hasBGSystem={} — BLOCKED!", hasBGSite, hasBGSystem);
                    } else {
                        action.addReasoning("V51 BATTLE ORDER: Occupy BG site + BG system — ready!", 50.0f);
                        logger.warn("V51 BATTLE ORDER: Requirements met — deploying!");
                    }
                }
            }

            actions.add(action);
        }

        return actions;
    }

    /**
     * Check if this is a shield selection by examining the available cards.
     * Similar to Python's approach of checking if majority of options are shields.
     */
    private boolean isShieldSelectionByContent(DecisionContext context) {
        GameState gameState = context.getGameState();
        List<String> cardIds = context.getCardIds();

        if (gameState == null || cardIds == null || cardIds.isEmpty()) {
            return false;
        }

        int shieldCount = 0;
        for (String cardId : cardIds) {
            try {
                PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                if (card != null) {
                    SwccgCardBlueprint blueprint = card.getBlueprint();
                    if (blueprint != null &&
                        blueprint.getCardCategory() == CardCategory.DEFENSIVE_SHIELD) {
                        shieldCount++;
                    }
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        // If majority are shields, treat as shield selection
        return shieldCount > 0 && shieldCount >= cardIds.size() * 0.5;
    }

    /**
     * Defensive shield selection - use ShieldStrategy scoring.
     */
    private List<EvaluatedAction> evaluateShieldSelection(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        ShieldStrategy shieldStrategy = context.getShieldStrategy();
        int turnNumber = context.getTurnNumber();

        logger.info("[CardSelectionEvaluator] Evaluating DEFENSIVE SHIELD selection");

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.DEPLOY,
                50.0f,  // Base score
                "Deploy shield"
            );

            if (gameState != null) {
                try {
                    PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                    if (card != null) {
                        String title = card.getTitle();
                        String blueprintId = card.getBlueprintId(true);
                        SwccgCardBlueprint blueprint = card.getBlueprint();

                        action.setDisplayText("Shield: " + (title != null ? title : cardId));

                        // Verify it's actually a defensive shield
                        if (blueprint != null &&
                            blueprint.getCardCategory() == CardCategory.DEFENSIVE_SHIELD) {

                            // Use ShieldStrategy for scoring
                            if (shieldStrategy != null && blueprintId != null && title != null) {
                                float shieldScore = shieldStrategy.scoreShield(
                                    blueprintId, title, turnNumber);

                                // Set score directly (ShieldStrategy fully controls priority)
                                action.setScore(shieldScore);
                                String description = shieldStrategy.getShieldDescription(blueprintId, title);
                                action.addReasoning("Shield: " + description, 0.0f);

                                logger.info("[Shield] {}: score={} ({})", title, shieldScore, description);

                                // === V51: BATTLE ORDER GATE (shield selection path) ===
                                if (title.toLowerCase(java.util.Locale.ROOT).contains("battle order")) {
                                    boolean hasBGSite = false;
                                    boolean hasBGSystem = false;
                                    try {
                                        GameState gs = context.getGameState();
                                        SwccgGame g = context.getGame();
                                        String pid = context.getPlayerId();
                                        if (g != null && gs != null && pid != null) {
                                            for (PhysicalCard loc : gs.getAllPermanentCards()) {
                                                if (loc == null || loc.getBlueprint() == null) continue;
                                                com.gempukku.swccgo.common.Zone locZone = loc.getZone();
                                                if (locZone == null || locZone != com.gempukku.swccgo.common.Zone.LOCATIONS) continue;
                                                SwccgCardBlueprint locBp = loc.getBlueprint();
                                                boolean isBattleground = false;
                                                try {
                                                    com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying mq = g.getModifiersQuerying();
                                                    if (mq != null) isBattleground = mq.isBattleground(gs, loc, null);
                                                } catch (Exception bgEx) { /* fallback */ }
                                                if (!isBattleground) continue;
                                                boolean weOccupy = false;
                                                for (PhysicalCard atLoc : gs.getCardsAtLocation(loc)) {
                                                    if (atLoc != null && pid.equals(atLoc.getOwner())) {
                                                        weOccupy = true;
                                                        break;
                                                    }
                                                }
                                                if (weOccupy) {
                                                    if (locBp.getCardSubtype() == com.gempukku.swccgo.common.CardSubtype.SYSTEM) {
                                                        hasBGSystem = true;
                                                    } else if (locBp.getCardSubtype() == com.gempukku.swccgo.common.CardSubtype.SITE) {
                                                        hasBGSite = true;
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        logger.debug("V51 BATTLE ORDER: Error checking occupation: {}", e.getMessage());
                                    }
                                    if (!hasBGSite || !hasBGSystem) {
                                        action.addReasoning("V51 BATTLE ORDER GATE: Need BOTH a BG site AND BG system occupied!", -9999.0f);
                                        logger.warn("V51 BATTLE ORDER GATE (shield): hasBGSite={}, hasBGSystem={} — BLOCKED!", hasBGSite, hasBGSystem);
                                    }
                                }
                            } else {
                                // Fallback if no shield strategy
                                action.addReasoning("Defensive shield (no strategy)", 50.0f);
                            }
                        } else {
                            // Not a shield - low priority
                            action.addReasoning("Not a defensive shield", -50.0f);
                        }
                    }
                } catch (NumberFormatException e) {
                    action.addReasoning("Invalid card ID", -100.0f);
                }
            }

            actions.add(action);
        }

        return actions;
    }

    /**
     * Extract blueprint ID from decision text HTML.
     * GEMP decision text for deploy location includes the card being deployed
     * in format: <div class='cardHint' value='8_35'>CardName</div>
     *
     * @param decisionText the decision text which may contain HTML
     * @return the blueprint ID, or null if not found
     */
    private String extractBlueprintFromDecisionText(String decisionText) {
        if (decisionText == null || decisionText.isEmpty()) {
            return null;
        }

        // Pattern: value='8_35' or value="8_35"
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
            "value=['\"]([0-9]+_[0-9]+)['\"]",
            java.util.regex.Pattern.CASE_INSENSITIVE
        );
        java.util.regex.Matcher matcher = pattern.matcher(decisionText);
        if (matcher.find()) {
            String blueprintId = matcher.group(1);
            logger.debug("Extracted blueprint {} from decision text", blueprintId);
            return blueprintId;
        }

        return null;
    }

    // ====================================================================
    // V70 helpers (Steve, 2026-05-12) — Universal one-weapon-per-character.
    // Mirror of rando/CardSelectionEvaluator.java helpers.
    // ====================================================================

    private static String v70ExtractDeployCriteria(String gameText) {
        if (gameText == null) return null;
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(
            "(?i)deploys?\\s+(?:only\\s+)?on\\s+(?:a|an|the|your)?\\s*([a-z][a-z\\s'-]{2,30}?)\\s*[.,;]"
        ).matcher(gameText);
        if (m.find()) {
            String c = m.group(1).trim();
            if (!c.isEmpty()) return c.toLowerCase(java.util.Locale.ROOT);
        }
        return null;
    }

    private static boolean v70CharacterMatchesCriteria(SwccgGame game, GameState gs, PhysicalCard pc, String criteria) {
        if (pc == null || pc.getBlueprint() == null || criteria == null || criteria.isEmpty()) return false;
        SwccgCardBlueprint bp = pc.getBlueprint();
        String c = criteria.toLowerCase(java.util.Locale.ROOT);

        if (bp.getTitle() != null && bp.getTitle().toLowerCase(java.util.Locale.ROOT).contains(c)) return true;
        if (bp.getLore() != null && bp.getLore().toLowerCase(java.util.Locale.ROOT).contains(c)) return true;
        if (bp.getGameText() != null && bp.getGameText().toLowerCase(java.util.Locale.ROOT).contains(c)) return true;

        java.util.Set<com.gempukku.swccgo.common.CardType> types = null;
        try {
            if (game != null && gs != null) {
                types = game.getModifiersQuerying().getCardTypes(gs, pc);
            }
        } catch (Exception ignored) { }
        if (types == null) types = bp.getCardTypes();
        if (types != null) {
            for (com.gempukku.swccgo.common.CardType ct : types) {
                String n = (ct.getHumanReadable() != null) ? ct.getHumanReadable() : ct.name();
                if (n.toLowerCase(java.util.Locale.ROOT).replace('_', ' ').contains(c)) return true;
            }
        }
        com.gempukku.swccgo.common.CardSubtype st = bp.getCardSubtype();
        if (st != null) {
            String n = (st.getHumanReadable() != null) ? st.getHumanReadable() : st.name();
            if (n.toLowerCase(java.util.Locale.ROOT).replace('_', ' ').contains(c)) return true;
        }
        java.util.Set<com.gempukku.swccgo.common.Persona> personas = bp.getPersonas();
        if (personas != null) {
            for (com.gempukku.swccgo.common.Persona p : personas) {
                if (p.name().toLowerCase(java.util.Locale.ROOT).contains(c)) return true;
            }
        }
        for (com.gempukku.swccgo.common.Icon icon : com.gempukku.swccgo.common.Icon.values()) {
            if (icon.name().toLowerCase(java.util.Locale.ROOT).contains(c)) {
                if (bp.hasIcon(icon)) return true;
            }
        }
        for (com.gempukku.swccgo.common.Keyword kw : com.gempukku.swccgo.common.Keyword.values()) {
            if (kw.name().toLowerCase(java.util.Locale.ROOT).contains(c)) {
                if (bp.hasKeyword(kw)) return true;
            }
        }
        return false;
    }

    private static int[] v70CountFriendlies(SwccgGame game, String playerId, String criteria) {
        int matchingArmed = 0, matchingUnarmed = 0, totalArmed = 0, totalUnarmed = 0;
        if (game == null || playerId == null) return new int[]{matchingArmed, matchingUnarmed, totalArmed, totalUnarmed};
        GameState gs = game.getGameState();
        if (gs == null) return new int[]{matchingArmed, matchingUnarmed, totalArmed, totalUnarmed};
        try {
            for (PhysicalCard pc : gs.getAllPermanentCards()) {
                if (pc == null || pc.getBlueprint() == null) continue;
                if (!playerId.equals(pc.getOwner())) continue;
                com.gempukku.swccgo.common.Zone z = pc.getZone();
                if (z == null || !z.isInPlay()) continue;
                if (pc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                boolean armed = false;
                java.util.List<PhysicalCard> atts = gs.getAttachedCards(pc);
                if (atts != null) {
                    for (PhysicalCard a : atts) {
                        if (a != null && a.getBlueprint() != null
                                && a.getBlueprint().getCardCategory() == CardCategory.WEAPON) {
                            armed = true;
                            break;
                        }
                    }
                }
                if (armed) totalArmed++; else totalUnarmed++;
                if (criteria == null || v70CharacterMatchesCriteria(game, gs, pc, criteria)) {
                    if (armed) matchingArmed++; else matchingUnarmed++;
                }
            }
        } catch (Exception ignored) { }
        return new int[]{matchingArmed, matchingUnarmed, totalArmed, totalUnarmed};
    }

    private static String v70CheckWeaponDeviceBlock(SwccgGame game, String playerId,
                                                     CardCategory candidateCategory,
                                                     SwccgCardBlueprint candidateBp) {
        if (candidateCategory != CardCategory.WEAPON && candidateCategory != CardCategory.DEVICE) return null;
        if (candidateBp == null || game == null || playerId == null) return null;

        String criteria = v70ExtractDeployCriteria(candidateBp.getGameText());
        int[] counts = v70CountFriendlies(game, playerId, criteria);
        int matchingArmed = counts[0], matchingUnarmed = counts[1], totalArmed = counts[2], totalUnarmed = counts[3];

        if (criteria != null && matchingArmed > 0 && matchingUnarmed == 0) {
            return String.format("every applicable '%s' friendly (%d) already armed", criteria, matchingArmed);
        }
        if (criteria != null && matchingArmed == 0 && matchingUnarmed == 0 && totalArmed > 0) {
            return String.format("no '%s' friendly matched our comprehensive search but %d friendly char(s) armed (defensive)", criteria, totalArmed);
        }
        // V72 (Steve, 2026-05-15): relaxed criteria==null fallback — only block if ALL friendlies armed.
        if (criteria == null && totalUnarmed == 0 && totalArmed > 0) {
            return String.format("no parseable deploy criteria and ALL %d friendly char(s) armed — no unarmed target", totalArmed);
        }
        return null;
    }
}