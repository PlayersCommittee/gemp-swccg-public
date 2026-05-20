package com.gempukku.swccgo.ai.models.rando.evaluators;

import com.gempukku.swccgo.ai.common.AiCardHelper;
import com.gempukku.swccgo.ai.common.AiPriorityCards;
import com.gempukku.swccgo.ai.models.rando.strategy.DeployPhasePlanner;
import com.gempukku.swccgo.ai.models.rando.strategy.DeploymentInstruction;
import com.gempukku.swccgo.ai.models.rando.strategy.DeploymentPlan;
import com.gempukku.swccgo.ai.models.rando.strategy.ShieldStrategy;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Icon;
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
        logger.warn("🚀🚀🚀 [CardSelectionEvaluator.evaluate] ENTRY POINT - JAR VERSION 2026-01-15-B 🚀🚀🚀");
        logger.warn("🔍 Decision type: {}", context.getDecisionType());
        logger.warn("🔍 Decision text (FULL): {}", text);

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
        } else if (textLower.contains("choose a pilot")) {
            return evaluatePilotSelection(context);
        } else if (textLower.contains("choose card to cancel")) {
            return evaluateCancelSelection(context);
        } else if (textLower.contains("choose target") ||
                   textLower.contains("click 'done' to cancel")) {
            return evaluateTargetSelection(context);
        } else if (textLower.contains("move") && textLower.contains("to")) {
            // Move destination selection
            return evaluateMoveDestination(context);
        } else if (textLower.contains("transit") || textLower.contains("transport")) {
            // Transit/transport destination selection
            return evaluateMoveDestination(context);
        } else if (textLower.contains("choose") && textLower.contains("location")) {
            return evaluateLocationSelection(context);
        } else if (textLower.contains("starting location")) {
            return evaluateStartingLocation(context);
        } else if (textLower.contains("card to take into hand")) {
            return evaluateTakeIntoHand(context);
        } else if (textLower.contains("card to put on lost pile")) {
            return evaluateLostPileSelection(context);
        } else if (textLower.contains("defensive shield") ||
                   isShieldSelectionByContent(context)) {
            return evaluateShieldSelection(context);
        } else {
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
            // Randomize scores to pick different cards each time
            float randomScore = VERY_BAD_DELTA + random.nextFloat() * 10;

            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.UNKNOWN,
                randomScore,
                "Set sabacc value (card " + cardId + ")"
            );
            action.addReasoning("Sabacc value (randomized)", randomScore);
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
                                // HEAVILY penalize deploying second weapon
                                action.addReasoning("⚠️ CHARACTER ALREADY HAS WEAPON: " + existingWeaponName, -200.0f);
                                logger.warn("⚠️ {} already has weapon '{}' - penalizing second weapon deployment!",
                                    title, existingWeaponName);
                            } else {
                                // Good target - character has no weapon
                                action.addReasoning("Character needs weapon", 20.0f);
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
                        // Battleground bonus (for all card types)
                        // =====================================================
                        if (blueprint != null && blueprint.getCardCategory() == CardCategory.LOCATION) {
                            if (titleLower.contains("battleground") || isLikelyBattleground(blueprint)) {
                                action.addReasoning("Battleground location", 30.0f);
                            }
                        }

                        // =====================================================
                        // CRITICAL: Check power at location
                        // Don't deploy characters to contested locations we're losing!
                        // =====================================================
                        if (isCharacter && game != null) {
                            try {
                                float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    game.getGameState(), location, playerId, false, false);
                                String opponent = game.getOpponent(playerId);
                                float theirPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    game.getGameState(), location, opponent, false, false);

                                if (theirPower > 0) {
                                    if (ourPower < theirPower) {
                                        // We're losing here - big penalty unless plan specifically targets this location
                                        if (plannedTargetId == null || !cardId.equals(plannedTargetId)) {
                                            action.addReasoning("CONTESTED & LOSING (" + (int)ourPower + " vs " + (int)theirPower + " power)", -80.0f);
                                            logger.info("⚠️ {} is contested and we're losing ({} vs {} power) - penalizing deploy",
                                                title, (int)ourPower, (int)theirPower);
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
                            } catch (Exception e) {
                                logger.debug("Could not get power at {}: {}", title, e.getMessage());
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

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.UNKNOWN,
                50.0f,
                "Lose force (card " + cardId + ")"
            );

            // Try to get card info to pick worst cards to lose
            if (gameState != null) {
                try {
                    PhysicalCard card = gameState.findCardById(Integer.parseInt(cardId));
                    if (card != null) {
                        SwccgCardBlueprint blueprint = card.getBlueprint();
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

                            // Don't want to lose priority cards
                            String title = card.getTitle();
                            if (title != null && AiPriorityCards.isPriorityCardByTitle(title)) {
                                action.addReasoning("Priority card - protect!", -100.0f);
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

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.UNKNOWN,
                50.0f,
                "Forfeit card " + cardId
            );

            // Optional forfeit - usually don't do it
            if (isOptional) {
                action.addReasoning("Optional forfeit - avoid", VERY_BAD_DELTA);
                actions.add(action);
                continue;
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
                                action.addReasoning("Ship has passengers - forfeit them first!", -100.0f);
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

        // Parse attrition and damage from decision text
        // Pattern: "Attrition remaining: X" or "Battle damage remaining: X"
        int attritionRemaining = extractNumberAfter(textLower, "attrition remaining:");
        int damageRemaining = extractNumberAfter(textLower, "damage remaining:");

        logger.info("🎯 Force loss OR forfeit: attrition={}, damage={}", attritionRemaining, damageRemaining);

        // Track if we have any hit cards or dead cards available for forfeit
        boolean hasHitCards = false;
        boolean hasDeadCards = false;
        PhysicalCard bestHitCard = null;
        float bestHitForfeit = Float.MAX_VALUE;
        SwccgGame game = context.getGame();
        String playerId = context.getPlayerId();

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

            // Check if this is a Force pile card (losing Force)
            if (cardId.startsWith("fp_") || textLower.contains("force pile")) {
                // Force loss option
                action.setDisplayText("Lose Force from pile");

                if (attritionRemaining > 0) {
                    // CRITICAL: Can't use Force loss to satisfy attrition!
                    action.addReasoning("CANNOT satisfy attrition with Force loss!", VERY_BAD_DELTA);
                } else if (damageRemaining > 0) {
                    // Can use Force loss for battle damage - usually preferred to save cards
                    if (hasHitCards) {
                        // But if we have hit cards, might as well forfeit them first
                        action.addReasoning("Have hit cards to forfeit first", -20.0f);
                    } else if (hasDeadCards) {
                        // We have dead cards (persona on table) - forfeit those instead!
                        action.addReasoning("Have dead cards (persona in play) to forfeit first", -30.0f);
                    } else {
                        // No hit or dead cards - prefer Force loss to save cards
                        action.addReasoning("Lose Force to save cards", 30.0f);
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

                            // If attrition remaining, forfeiting is REQUIRED
                            if (attritionRemaining > 0) {
                                SwccgCardBlueprint bp = card.getBlueprint();
                                // CRITICAL: Check hasForfeitAttribute() first - weapons throw exception!
                                Float forfeit = bp != null && bp.hasForfeitAttribute() ? bp.getForfeit() : null;
                                if (forfeit != null && forfeit >= attritionRemaining) {
                                    action.addReasoning("Forfeit satisfies attrition!", 50.0f);
                                } else {
                                    action.addReasoning("Must forfeit for attrition", 20.0f);
                                }
                            }

                            // Apply standard forfeit scoring
                            SwccgCardBlueprint blueprint = card.getBlueprint();
                            if (blueprint != null) {
                                // CRITICAL: Check hasForfeitAttribute() first - weapons throw exception!
                                Float forfeit = blueprint.hasForfeitAttribute() ? blueprint.getForfeit() : null;
                                if (forfeit != null) {
                                    if (forfeit <= 2) {
                                        action.addReasoning("Low forfeit value", 15.0f);
                                    } else if (forfeit >= 6) {
                                        action.addReasoning("High forfeit - protect", -30.0f);
                                    }
                                }

                                if (blueprint.getUniqueness() == Uniqueness.UNIQUE) {
                                    action.addReasoning("Unique card - protect", -15.0f);
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
                            Float deployCost = blueprint.getDeployCost();
                            if (deployCost != null) {
                                float costScore = Math.max(0, 30 - deployCost * 5);
                                action.addReasoning("Deploy cost " + deployCost.intValue(), costScore);
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

                            // Check if this is the planned pilot
                            if (plannedPilotBlueprintId != null && blueprintId != null &&
                                blueprintId.equals(plannedPilotBlueprintId)) {
                                action.addReasoning("PLANNED pilot for " + shipName, 200.0f);
                                logger.info("   ✅ {} is the PLANNED pilot (+200)", title);
                            } else {
                                // Score based on pilot quality

                                // Lower deploy cost is better (we're paying extra for this)
                                Float deployCost = blueprint.getDeployCost();
                                if (deployCost != null) {
                                    float costScore = Math.max(0, 30 - deployCost * 5);
                                    action.addReasoning("Deploy cost " + deployCost.intValue(), costScore);
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
                        }

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

                                // Prefer high-value targets
                                if (blueprint != null) {
                                    if (blueprint.hasPowerAttribute()) {
                                        Float power = blueprint.getPower();
                                        if (power != null && power >= 5) {
                                            action.addReasoning("High-power target", 30.0f);
                                        }
                                    }

                                    if (blueprint.getUniqueness() == Uniqueness.UNIQUE) {
                                        action.addReasoning("Unique target", 20.0f);
                                    }
                                }
                            } else {
                                action.addReasoning("Our card - don't target!", -200.0f);
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
     * Starting location selection.
     */
    private List<EvaluatedAction> evaluateStartingLocation(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();

        for (String cardId : context.getCardIds()) {
            EvaluatedAction action = new EvaluatedAction(
                cardId,
                ActionType.DEPLOY,
                50.0f,
                "Starting location " + cardId
            );

            // All starting locations are generally good
            action.addReasoning("Starting location", 50.0f);
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

            // Add randomization to avoid predictable patterns (like Python)
            float randomFactor = random.nextFloat() * 25.0f - 10.0f;  // -10 to +15
            action.addReasoning("Random factor", randomFactor);

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

            // Check for priority cards
            if (blueprintId != null && AiPriorityCards.isPriorityCard(blueprintId)) {
                int priorityScore = AiPriorityCards.getProtectionScore(blueprintId);
                action.addReasoning("Priority card", priorityScore * 0.3f);
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

        // Get deployment plan if available
        DeploymentPlan plan = null;
        if (planner != null && game != null && side != null && playerId != null) {
            plan = planner.createPlan(game, playerId, side);
            if (plan != null) {
                logger.info("[CardSelectionEvaluator] Using deployment plan: strategy={}, instructions={}",
                    plan.getStrategy(), plan.getInstructions().size());
            }
        }

        for (int i = 0; i < blueprints.size(); i++) {
            String blueprintId = blueprints.get(i);

            // Use index as action ID for blueprint-based selections
            EvaluatedAction action = new EvaluatedAction(
                String.valueOf(i),
                ActionType.DEPLOY,
                50.0f,
                "Deploy " + blueprintId
            );

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
}
