package com.gempukku.swccgo.ai.models.chosenone.evaluators;

import com.gempukku.swccgo.ai.common.AiCardHelper;
import com.gempukku.swccgo.ai.models.chosenone.ChosenOneConfig;
import com.gempukku.swccgo.ai.models.chosenone.ChosenOneLogger;
import com.gempukku.swccgo.ai.models.chosenone.strategy.DeployPhasePlanner;
import com.gempukku.swccgo.ai.models.chosenone.strategy.DeploymentInstruction;
import com.gempukku.swccgo.ai.models.chosenone.strategy.DeploymentPlan;
import com.gempukku.swccgo.ai.models.chosenone.strategy.DeployStrategy;
import com.gempukku.swccgo.ai.models.chosenone.strategy.CardKnowledge;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evaluates deployment decisions during Deploy phase.
 *
 * Handles:
 * - CARD_ACTION_CHOICE decisions with "Deploy" actions
 * - Scoring based on card value (power + ability) vs deploy cost
 * - Strategic deployment prioritization (locations first, reinforce losing)
 * - Affordability checking
 *
 * Ported from Python deploy_evaluator.py (simplified)
 */
public class DeployEvaluator extends ActionEvaluator {
    private static final Logger LOG = ChosenOneLogger.getEvaluatorLogger();

    // Track cards we've already tried deploying this turn to avoid retry loops
    private Set<String> pendingDeployCardIds = new HashSet<>();
    private int lastTurnNumber = -1;

    public DeployEvaluator() {
        super("Deploy");
    }

    /**
     * Reset pending deploy tracking (call at turn start)
     */
    public void resetPendingDeploys() {
        pendingDeployCardIds.clear();
    }

    @Override
    public boolean canEvaluate(DecisionContext context) {
        // Only evaluate CARD_ACTION_CHOICE during Deploy phase
        if (!"CARD_ACTION_CHOICE".equals(context.getDecisionType())) {
            LOG.debug("[DeployEvaluator] canEvaluate=false: not CARD_ACTION_CHOICE (got {})", context.getDecisionType());
            return false;
        }

        // Must be our turn
        if (context.getGameState() != null && !context.isMyTurn()) {
            LOG.debug("[DeployEvaluator] canEvaluate=false: not our turn");
            return false;
        }

        // Must be Deploy phase
        Phase phase = context.getPhase();
        if (phase != Phase.DEPLOY) {
            LOG.debug("[DeployEvaluator] canEvaluate=false: not Deploy phase (got {})", phase);
            return false;
        }

        // Must have at least one deploy action
        List<String> actionTexts = context.getActionTexts();
        if (actionTexts == null || actionTexts.isEmpty()) {
            LOG.debug("[DeployEvaluator] canEvaluate=false: no action texts");
            return false;
        }

        boolean hasDeployAction = false;
        for (String actionText : actionTexts) {
            if (actionText != null && actionText.toLowerCase(Locale.ROOT).contains("deploy")) {
                hasDeployAction = true;
                break;
            }
        }

        if (hasDeployAction) {
            LOG.info("[DeployEvaluator] canEvaluate=TRUE - will evaluate deploy decision");
        } else {
            LOG.warn("[DeployEvaluator] canEvaluate=false: no deploy actions found in {} action texts", actionTexts.size());

            // V59 DIAGNOSTIC for Issue #2: when Deploy phase presents 0 deploy-from-hand
            // actions despite having hand cards + force, dump state so we can diagnose.
            // FIXES visibility of Turn 5 peaceful-pike bug where ChosenOne had Obi-Wan, YS,
            // Luke's Lightsaber in hand, 13F in pile, but only 'Break cover' was offered.
            try {
                int force = context.getForcePileSize();
                List<PhysicalCard> h = context.getHand();
                int handSize = h != null ? h.size() : 0;
                // Count potentially deployable cards in hand
                int potentialDeploys = 0;
                StringBuilder handDetail = new StringBuilder();
                if (h != null) {
                    for (PhysicalCard hc : h) {
                        if (hc == null || hc.getBlueprint() == null) continue;
                        CardCategory cat = hc.getBlueprint().getCardCategory();
                        if (cat == null) continue;
                        int cost = 0;
                        try { Float c = hc.getBlueprint().getDeployCost(); if (c != null) cost = c.intValue(); }
                        catch (UnsupportedOperationException uoe) { /* not deployable */ }
                        boolean deployable = (cat == CardCategory.CHARACTER || cat == CardCategory.STARSHIP
                            || cat == CardCategory.VEHICLE || cat == CardCategory.WEAPON
                            || cat == CardCategory.DEVICE || cat == CardCategory.LOCATION
                            || cat == CardCategory.EFFECT);
                        if (deployable && cost <= force) potentialDeploys++;
                        handDetail.append(hc.getTitle()).append("(").append(cat).append(",c=").append(cost).append(") ");
                    }
                }
                LOG.warn("V59 DIAGNOSTIC NO-DEPLOYS: phase={} force={} handSize={} affordable={} | offered: {} | hand: [{}]",
                    context.getPhase(), force, handSize, potentialDeploys,
                    actionTexts, handDetail.toString().trim());
                if (potentialDeploys > 0) {
                    LOG.warn("V59 DIAGNOSTIC: {} affordable deploys in hand but GEMP offered NONE — engine-side restriction?",
                        potentialDeploys);
                }
            } catch (Exception e) {
                LOG.debug("V59 DIAGNOSTIC: Error dumping state: {}", e.getMessage());
            }
        }

        return hasDeployAction;
    }

    @Override
    public List<EvaluatedAction> evaluate(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();

        // ========== CRITICAL DEBUG LOGGING ==========
        // This MUST show to verify JAR is deployed correctly
        LOG.warn("🚀🚀🚀 [DeployEvaluator.evaluate] ENTRY POINT - JAR VERSION 2026-02-23-V21 🚀🚀🚀");
        LOG.warn("🔍 Decision type: {}", context.getDecisionType());
        LOG.warn("🔍 Decision text: {}", context.getDecisionText());

        // Log ALL context data we have access to
        List<String> ctxCardIds = context.getCardIds();
        List<String> ctxBlueprints = context.getBlueprints();
        List<String> ctxActionIds = context.getActionIds();
        List<String> ctxActionTexts = context.getActionTexts();
        List<Boolean> ctxSelectable = context.getSelectable();

        LOG.warn("🔍 Context cardIds: {} items -> {}",
            ctxCardIds != null ? ctxCardIds.size() : "null",
            ctxCardIds != null ? ctxCardIds : "null");
        LOG.warn("🔍 Context blueprints: {} items -> {}",
            ctxBlueprints != null ? ctxBlueprints.size() : "null",
            ctxBlueprints != null ? ctxBlueprints : "null");
        LOG.warn("🔍 Context actionIds: {} items -> {}",
            ctxActionIds != null ? ctxActionIds.size() : "null",
            ctxActionIds != null ? ctxActionIds : "null");
        LOG.warn("🔍 Context actionTexts: {} items -> {}",
            ctxActionTexts != null ? ctxActionTexts.size() : "null",
            ctxActionTexts != null ? ctxActionTexts : "null");
        LOG.warn("🔍 Context selectable: {} items -> {}",
            ctxSelectable != null ? ctxSelectable.size() : "null",
            ctxSelectable != null ? ctxSelectable : "null");
        List<String> ctxTestingTexts = context.getTestingTexts();
        LOG.warn("🔍 Context testingTexts (CARD TITLES): {} items -> {}",
            ctxTestingTexts != null ? ctxTestingTexts.size() : "null",
            ctxTestingTexts != null ? ctxTestingTexts : "null");

        // Log hand cards
        List<PhysicalCard> debugHand = context.getHand();
        LOG.warn("🔍 Hand size: {}", debugHand != null ? debugHand.size() : "null");
        if (debugHand != null) {
            StringBuilder handStr = new StringBuilder();
            for (PhysicalCard card : debugHand) {
                if (card != null) {
                    handStr.append(card.getTitle()).append(" (id=").append(card.getCardId())
                           .append(", bp=").append(card.getBlueprintId(true)).append("), ");
                }
            }
            LOG.warn("🔍 Hand cards: {}", handStr);
        }
        // ========== END CRITICAL DEBUG LOGGING ==========

        LOG.info("[DeployEvaluator] Evaluating deploy phase decision");

        // Reset pending deploy tracking at the start of each turn
        if (context.getTurnNumber() != lastTurnNumber) {
            resetPendingDeploys();
            lastTurnNumber = context.getTurnNumber();
            LOG.debug("[DeployEvaluator] Reset pending deploys for turn {}", lastTurnNumber);
        }

        List<String> actionIds = context.getActionIds();
        List<String> actionTexts = context.getActionTexts();

        if (actionIds == null || actionTexts == null) {
            LOG.warn("[DeployEvaluator] No action IDs or texts available");
            return actions;
        }

        // Get available force
        int availableForce = context.getForcePileSize();
        int lifeForce = context.getLifeForce();
        List<PhysicalCard> hand = context.getHand();

        // V48: Check if Vader needs force reserved for movement
        int vaderMoveReserve = 0;
        // V79 (Steve, 2026-05-15): Verge of Greatness Death-Star move reserve
        int v79VergeMoveReserve = 0;
        GameState vaderCheckGs = context.getGameState();
        SwccgGame vaderCheckGame = context.getGame();
        if (vaderCheckGs != null && vaderCheckGame != null) {
            try {
                String vPlayerId = context.getPlayerId();
                boolean v79VergeActive = false;
                PhysicalCard v79DeathStar = null;
                boolean v79DeathStarAtScarif = false;
                for (PhysicalCard pCard : vaderCheckGs.getAllPermanentCards()) {
                    if (pCard == null || !vPlayerId.equals(pCard.getOwner())) continue;
                    com.gempukku.swccgo.common.Zone pZone = pCard.getZone();
                    if (pZone == null || !pZone.isInPlay()) continue;
                    if (pCard.getBlueprint() == null || pCard.getTitle() == null) continue;
                    String pTitle = pCard.getTitle().toLowerCase(java.util.Locale.ROOT);
                    if (pTitle.contains("on the verge of greatness")
                            || pTitle.contains("taking control of the weapon")) {
                        v79VergeActive = true;
                    }
                    if (pTitle.contains("death star")
                            && pCard.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                        v79DeathStar = pCard;
                        PhysicalCard dsLoc = pCard.getAtLocation();
                        if (dsLoc != null && dsLoc.getTitle() != null
                                && dsLoc.getTitle().toLowerCase(java.util.Locale.ROOT).contains("scarif")) {
                            v79DeathStarAtScarif = true;
                        }
                    }
                    if (pTitle.contains("vader") && pCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                        PhysicalCard vaderLoc = pCard.getAtLocation();
                        if (vaderLoc != null) {
                            String vOppId = vaderCheckGame.getOpponent(vPlayerId);
                            boolean opponentsHere = false;
                            try {
                                float oppPower = vaderCheckGame.getModifiersQuerying().getTotalPowerAtLocation(
                                    vaderCheckGs, vaderLoc, vOppId, false, false);
                                opponentsHere = (oppPower > 0);
                            } catch (Exception e) { /* ignore */ }

                            if (!opponentsHere) {
                                vaderMoveReserve = 2;
                                LOG.warn("V48 VADER MOVE RESERVE: Vader at {} with no opponents — reserving {} force for move!",
                                    vaderLoc.getTitle(), vaderMoveReserve);
                            }
                        }
                        // Don't break — V79 scan continues
                    }
                }
                if (v79VergeActive && v79DeathStar != null && !v79DeathStarAtScarif) {
                    v79VergeMoveReserve = 1;
                    LOG.warn("V79 VERGE MOVE RESERVE: Verge of Greatness active + Death Star not at Scarif — reserve 1 Force for Move phase");
                }
            } catch (Exception e) {
                LOG.debug("V48 VADER MOVE RESERVE: Error: {}", e.getMessage());
            }
        }

        // V53: Reserve 1 force per undercover spy for movement next turn.
        // If opponent moves away from our spy, we need force to follow them.
        int spyMoveReserve = 0;
        if (vaderCheckGs != null) {
            try {
                String spyRsvPid = context.getPlayerId();
                for (PhysicalCard spyRsvCard : vaderCheckGs.getAllPermanentCards()) {
                    if (spyRsvCard == null || !spyRsvPid.equals(spyRsvCard.getOwner())) continue;
                    if (spyRsvCard.isUndercover()) {
                        spyMoveReserve++;
                    }
                }
                if (spyMoveReserve > 0) {
                    LOG.info("V53 SPY RESERVE: Reserving {} force for {} undercover spy movement(s)",
                        spyMoveReserve, spyMoveReserve);
                }
            } catch (Exception e) { /* ignore */ }
        }
        // Reduce available force by spy reserve (same pattern as Vader reserve)
        availableForce = Math.max(0, availableForce - spyMoveReserve);

        LOG.debug("[DeployEvaluator] Resources: force={}, lifeForce={}, handSize={}, actions={}",
            availableForce, lifeForce, hand != null ? hand.size() : 0, actionIds.size());

        // === USE DEPLOY PHASE PLANNER ===
        DeploymentPlan plan = null;
        DeployPhasePlanner planner = context.getDeployPhasePlanner();
        SwccgGame game = context.getGame();
        Side side = context.getSide();
        String playerId = context.getPlayerId();

        // DEBUG: Log which context values are available
        LOG.info("[DeployEvaluator] Context check: planner={}, game={}, side={}, playerId={}",
            planner != null ? "SET" : "NULL",
            game != null ? "SET" : "NULL",
            side != null ? side : "NULL",
            playerId);

        if (planner != null && game != null && side != null) {
            LOG.info("[DeployEvaluator] Calling planner.createPlan()...");
            plan = planner.createPlan(game, playerId, side);
            if (plan != null) {
                LOG.info("[DeployEvaluator] ✅ Got deployment plan: strategy={}, instructions={}",
                    plan.getStrategy(), plan.getInstructions().size());

                // === AUTO-CLEANUP: Detect deployed cards ===
                // Check if any planned cards are no longer in hand - they were deployed!
                // This fixes the STALE PLAN bug where recordDeployment() was never called
                if (!plan.getInstructions().isEmpty() && hand != null) {
                    Set<String> handBlueprintIds = new HashSet<>();
                    for (PhysicalCard card : hand) {
                        if (card != null) {
                            String bpId = card.getBlueprintId(true);
                            if (bpId != null) {
                                handBlueprintIds.add(bpId);
                            }
                        }
                    }

                    // Find instructions for cards no longer in hand
                    List<DeploymentInstruction> deployedCards = new ArrayList<>();
                    for (DeploymentInstruction instruction : plan.getInstructions()) {
                        if (!handBlueprintIds.contains(instruction.getCardBlueprintId())) {
                            deployedCards.add(instruction);
                        }
                    }

                    // Record deployments for cards that left hand
                    for (DeploymentInstruction instruction : deployedCards) {
                        LOG.info("📋 Auto-detected deployment: {} left hand", instruction.getCardName());
                        planner.recordDeployment(instruction.getCardBlueprintId());
                    }
                }

                // Log plan status
                if (plan.isPlanComplete()) {
                    LOG.info("📋 Deploy plan: COMPLETE ({} deployed)", plan.getDeploymentsMade());
                } else {
                    int remaining = plan.getInstructions().size();
                    int done = plan.getDeploymentsMade();
                    LOG.info("📋 Deploy plan: {} - {} ({} remaining, {} done)",
                        plan.getStrategy().getValue(), plan.getReason(), remaining, done);
                }
            } else {
                LOG.warn("[DeployEvaluator] ⚠️ Planner returned null plan");
            }
        } else {
            LOG.warn("[DeployEvaluator] ⚠️ Cannot call planner - missing: {}{}{}",
                planner == null ? "planner " : "",
                game == null ? "game " : "",
                side == null ? "side " : "");
        }

        // === STALE PLAN DETECTION ===
        // Check if available deploy actions match the plan
        // If none match and we have deploy actions, check WHY before marking stale
        if (plan != null && !plan.getInstructions().isEmpty() && !plan.isPlanComplete()) {
            boolean planCardsAvailable = false;
            boolean planCardsStillInHand = false;
            List<String> cardIdList = context.getCardIds();
            List<String> blueprintList = context.getBlueprints();

            // First, check if any planned cards are still in hand
            Set<String> handBlueprintIds = new HashSet<>();
            if (hand != null) {
                for (PhysicalCard handCard : hand) {
                    if (handCard != null) {
                        String bpId = handCard.getBlueprintId(true);
                        if (bpId != null) {
                            handBlueprintIds.add(bpId);
                        }
                    }
                }
            }

            StringBuilder planInHandCards = new StringBuilder();
            StringBuilder planNotInHandCards = new StringBuilder();
            for (DeploymentInstruction inst : plan.getInstructions()) {
                if (handBlueprintIds.contains(inst.getCardBlueprintId())) {
                    planCardsStillInHand = true;
                    planInHandCards.append(inst.getCardName()).append(" (").append(inst.getCardBlueprintId()).append("), ");
                } else {
                    planNotInHandCards.append(inst.getCardName()).append(" (").append(inst.getCardBlueprintId()).append("), ");
                }
            }
            if (planCardsStillInHand) {
                LOG.warn("📋 Plan cards IN HAND but not deployable: {}", planInHandCards);
            }
            if (planNotInHandCards.length() > 0) {
                LOG.warn("📋 Plan cards NOT in hand (already deployed?): {}", planNotInHandCards);
            }

            for (int i = 0; i < actionTexts.size(); i++) {
                String actionText = actionTexts.get(i);
                if (actionText == null || !actionText.toLowerCase(Locale.ROOT).contains("deploy")) {
                    continue;
                }

                // Get blueprint ID using cardId lookup (most reliable)
                String bpId = null;
                String cardIdStr = (cardIdList != null && i < cardIdList.size()) ? cardIdList.get(i) : null;

                // Method 1: Look up card by cardId in game state to get its blueprint
                if (cardIdStr != null && !cardIdStr.isEmpty() && gameState != null) {
                    try {
                        int cardIdNum = Integer.parseInt(cardIdStr);
                        PhysicalCard card = gameState.findCardById(cardIdNum);
                        if (card != null) {
                            bpId = card.getBlueprintId(true);
                        }
                    } catch (NumberFormatException e) {
                        // Not a number - ignore
                    }
                }

                // Method 2: Use blueprint from params (for virtual/off-table actions)
                if (bpId == null && blueprintList != null && i < blueprintList.size()) {
                    String paramBp = blueprintList.get(i);
                    if (paramBp != null && !paramBp.isEmpty() && !"inPlay".equals(paramBp)) {
                        bpId = paramBp;
                    }
                }

                // Check if this blueprint is in the plan
                if (bpId != null && plan.getInstructionForCard(bpId) != null) {
                    planCardsAvailable = true;
                    LOG.debug("   Found plan card {} in action: {}", bpId, actionText.substring(0, Math.min(60, actionText.length())));
                    break;
                }
            }

            if (!planCardsAvailable) {
                if (planCardsStillInHand) {
                    // Plan cards are in hand but not deployable - probably can't afford them
                    // Set flag so we apply HUGE penalty to non-plan deploys
                    LOG.warn("📋 Plan cards in hand but not affordable - will heavily penalize off-plan deploys");
                    plan.setWaitingForPlannedCards(true);
                } else {
                    // Plan cards are NOT in hand at all - plan is truly stale
                    LOG.warn("⚠️ STALE PLAN: Plan has {} cards but NONE are in hand or deploy actions!",
                        plan.getInstructions().size());
                    StringBuilder planCards = new StringBuilder();
                    for (DeploymentInstruction inst : plan.getInstructions()) {
                        planCards.append(inst.getCardName()).append(", ");
                    }
                    LOG.warn("   Plan cards: {}", planCards);
                    // Mark plan as allowing extra actions since planned cards are truly gone
                    plan.setForceAllowExtras(true);
                }
            }
        }

        // Check if we're behind on board (need to deploy more aggressively)
        boolean needsReinforcement = false;
        if (gameState != null) {
            String opponentId = gameState.getOpponent(playerId);
            // Simple check: compare card counts
            int ourCards = countCardsInPlay(gameState, playerId);
            int theirCards = opponentId != null ? countCardsInPlay(gameState, opponentId) : 0;
            needsReinforcement = ourCards < theirCards;
        }

        // DEBUG: Log ALL available actions to understand the format
        LOG.info("[DeployEvaluator] === Available actions ({} total) ===", actionIds.size());
        for (int idx = 0; idx < actionIds.size(); idx++) {
            String id = actionIds.get(idx);
            String txt = idx < actionTexts.size() ? actionTexts.get(idx) : "(no text)";
            LOG.info("   Action[{}]: id='{}', text='{}'", idx, id, txt);
        }

        for (int i = 0; i < actionIds.size(); i++) {
            String actionId = actionIds.get(i);
            String actionText = i < actionTexts.size() ? actionTexts.get(i) : "";
            String actionLower = actionText.toLowerCase(Locale.ROOT);

            // Only handle deploy-related actions (including persona replace)
            if (!actionLower.contains("deploy") && !actionLower.contains("persona replace")) {
                continue;
            }

            // V38.4: PERSONA REPLACE — usually BAD
            if (actionLower.contains("persona replace")) {
                EvaluatedAction prAction = new EvaluatedAction(actionId, ActionType.DEPLOY, -500.0f, actionText);
                prAction.addReasoning("V38.4 PERSONA REPLACE: Loses armed character — blocked!", -500.0f);
                actions.add(prAction);
                continue;
            }

            EvaluatedAction action = new EvaluatedAction(
                actionId,
                ActionType.DEPLOY,
                50.0f,  // Base score
                actionText
            );

            // === V60 RESERVE DECK PULL GUARDS ===
            // FIXES Issue #B from peaceful-pike replay: Rando invoked "Deploy Tala Durith
            // from Reserve Deck" and "Deploy a Padawan" at force=0, search failed, opponent
            // saw Rando's entire Reserve Deck. NEVER invoke a Reserve pull unless:
            //   1. We can afford the deploy cost (tricky: unknown cost for "a Padawan")
            //   2. DeckOracle confirms a valid target exists (prevents reveal)
            //   3. This specific action hasn't failed 2x this game (shouldAvoidPulling)
            // For generic-target actions ("Deploy a Padawan"), guards #1-2 are best-effort.
            String v60ActionLower = actionText != null ? actionText.toLowerCase(Locale.ROOT) : "";
            boolean v60IsReservePull = v60ActionLower.contains("from reserve deck")
                || v60ActionLower.contains("[download]");
            if (v60IsReservePull) {
                // Guard: failed 2x — stop trying
                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle v60Oracle = context.getDeckOracle();
                if (v60Oracle != null) {
                    String failKey = "action:" + actionText;
                    if (v60Oracle.shouldAvoidPulling(failKey)) {
                        action.addReasoning("V60 RESERVE FAIL-STOP: '" + actionText
                            + "' failed 2x — stop trying!", -9999.0f);
                        LOG.warn("V60 RESERVE FAIL-STOP: {} hard-blocked after 2+ failures", actionText);
                        actions.add(action);
                        continue;
                    }
                }
                // Guard: reserve deck critically small (reveal risk)
                GameState v60Gs = context.getGameState();
                if (v60Gs != null) {
                    try {
                        int v60ReserveSize = v60Gs.getReserveDeckSize(context.getPlayerId());
                        if (v60ReserveSize <= 2) {
                            action.addReasoning("V60 RESERVE RISK: " + v60ReserveSize
                                + " cards in Reserve — reveal almost the whole deck!", -9999.0f);
                            LOG.warn("V60 RESERVE RISK: {} blocked — only {} cards in reserve",
                                actionText, v60ReserveSize);
                            actions.add(action);
                            continue;
                        }
                    } catch (Exception e) { /* ignore */ }
                }
                // Guard: named target check — "Deploy [Name] from Reserve Deck"
                // Only blocks MULTI-WORD proper-noun targets (case-sensitive match).
                // Generic placeholders like "card", "a farm", "a Padawan" are NOT blocked —
                // the engine picks the actual target from the source card's filter list.
                // FIXES Yarna "Deploy card from Reserve Deck" false positive.
                if (v60Oracle != null) {
                    java.util.regex.Matcher namedMatch = java.util.regex.Pattern.compile(
                        "Deploy ([A-Z][A-Za-z']+ [A-Z][A-Za-z' -]+?) from Reserve Deck")
                        .matcher(actionText);
                    if (namedMatch.find()) {
                        String v60Target = namedMatch.group(1).trim();
                        if (!v60Oracle.hasTargetInReserve(v60Target.split(" "))) {
                            action.addReasoning("V60 RESERVE MISS: '" + v60Target
                                + "' not in Reserve — pull fails + reveals deck!", -9999.0f);
                            LOG.warn("V60 RESERVE MISS: {} not in reserve — hard-blocked", v60Target);
                            actions.add(action);
                            continue;
                        }
                    }
                }

                // Guard: generic category pull — "Deploy a farm from Reserve Deck"
                // If DeckOracle shows 0 cards match the keyword in Reserve, block.
                // FIXES IMBATS pulling farm when the only farm is already in hand.
                if (v60Oracle != null) {
                    java.util.regex.Matcher genMatch = java.util.regex.Pattern.compile(
                        "Deploy an? ([a-z][a-z ]*?) from Reserve Deck").matcher(actionText);
                    if (genMatch.find()) {
                        String v60Kw = genMatch.group(1).trim();
                        // V67bg (mirror of Rando, 2026-05-10): TYPE-AWARE pull validation.
                        // See Rando DeployEvaluator V67bg comment for full rationale.
                        com.gempukku.swccgo.filters.Filter v67bgTypedFilter =
                            com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle
                                .resolveCommonNounToFilter(v60Kw);
                        if (v67bgTypedFilter != null) {
                            boolean v67bgMatch = v60Oracle.hasFilterMatchInReserve(
                                context.getGame(), context.getPlayerId(), v67bgTypedFilter);
                            if (!v67bgMatch) {
                                action.addReasoning("V67bg RESERVE MISS (typed '" + v60Kw
                                    + "'): no card matching Filter in Reserve — pull will fail!",
                                    -9999.0f);
                                LOG.warn("V67bg RESERVE MISS: typed filter for '{}' has no match in reserve — hard-blocked",
                                    v60Kw);
                                actions.add(action);
                                continue;
                            } else {
                                LOG.warn("V67bg RESERVE OK: typed filter for '{}' has matches in reserve — pull valid",
                                    v60Kw);
                            }
                        } else if (v60Kw.length() >= 3 && !v60Oracle.hasTargetInReserve(v60Kw)) {
                            action.addReasoning("V60 RESERVE MISS (generic, untyped): no '" + v60Kw
                                + "' in Reserve — pull fails + reveals deck!", -9999.0f);
                            LOG.warn("V60 RESERVE MISS (untyped): keyword '{}' not in reserve — hard-blocked (CONSIDER adding to resolveCommonNounToFilter)", v60Kw);
                            actions.add(action);
                            continue;
                        }
                    }
                }

                // V66 MEMORY AUDIT: Unified pull validation via DeckOracle.
                // Catches pulls that the named-target/generic regexes miss,
                // AND catches "WASTEFUL" pulls (target already in hand/play).
                // Steve's feedback: "Rando doesn't seem to remember what's in
                // his hand, force pile, reserve, used or lost pile."
                // This runs AFTER the older named/generic guards so those more
                // specific penalties still fire first.
                if (v60Oracle != null && v60Oracle.isAnalyzed()) {
                    com.gempukku.swccgo.common.Zone v66Zone =
                        com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.parseSourceZone(actionText);
                    if (v66Zone != null) {
                        // Extract candidate target keyword(s) from action text.
                        // Prefer multi-word proper noun, fall back to generic "a X".
                        String[] v66Keywords = null;
                        java.util.regex.Matcher v66Named = java.util.regex.Pattern.compile(
                            "(?:Deploy|Take) ([A-Z][A-Za-z']+ [A-Z][A-Za-z' -]+?) "
                                + "(?:from Reserve|from Lost|from Used|from Force|into hand from)")
                            .matcher(actionText);
                        if (v66Named.find()) {
                            v66Keywords = v66Named.group(1).trim().split(" ");
                        } else {
                            java.util.regex.Matcher v66Gen = java.util.regex.Pattern.compile(
                                "(?:Deploy|Take) an? ([a-z]+) (?:from|into hand from)")
                                .matcher(actionText);
                            if (v66Gen.find()) {
                                String kw = v66Gen.group(1).trim();
                                if (kw.length() >= 3) v66Keywords = new String[] { kw };
                            }
                        }
                        if (v66Keywords != null && v66Keywords.length > 0) {
                            com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.PullValidation v66Result =
                                v60Oracle.validatePull(v66Zone, v66Keywords);
                            if (v66Result.outcome ==
                                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.PullOutcome.WILL_FAIL) {
                                action.addReasoning("V66 MEMORY: " + v66Result.reason, -9999.0f);
                                LOG.warn("V66 MEMORY WILL_FAIL: '{}' — {}", actionText, v66Result.reason);
                                actions.add(action);
                                continue;
                            } else if (v66Result.outcome ==
                                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.PullOutcome.WASTEFUL) {
                                action.addReasoning("V66 MEMORY: " + v66Result.reason, -800.0f);
                                LOG.warn("V66 MEMORY WASTEFUL: '{}' — {} (-800)", actionText, v66Result.reason);
                            } else if (v66Result.outcome ==
                                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.PullOutcome.WILL_SUCCEED) {
                                LOG.info("V66 MEMORY OK: {} — {}", actionText, v66Result.reason);
                            }
                        }

                        // V67h: When the action text is generic ("Choose card to deploy from
                        // Reserve Deck", "[Download] a matching weapon"), use the SOURCE CARD's
                        // game text to identify what filter the action targets. This catches
                        // the failures the regex-based V66 misses — e.g., Yarna's "[download]
                        // Arleil, Doallyn, Tessek, Wild Karrde, or a Tatooine battleground"
                        // when none of those is in Reserve.
                        // Steve's expectation: "Rando is already aware of what's in his deck
                        // at the start of game and would know when he would have a successful
                        // search."
                        try {
                            List<String> v67hCardIds = context.getCardIds();
                            String v67hCardIdStr = (v67hCardIds != null && i < v67hCardIds.size())
                                ? v67hCardIds.get(i) : null;
                            if (v67hCardIdStr != null && !v67hCardIdStr.isEmpty() && gameState != null) {
                                PhysicalCard v67hSrcCard =
                                    gameState.findCardById(Integer.parseInt(v67hCardIdStr));
                                if (v67hSrcCard != null && v67hSrcCard.getBlueprint() != null) {
                                    String v67hGT = v67hSrcCard.getBlueprint().getGameText();
                                    if (v67hGT != null) {
                                        com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.PullValidation v67hResult =
                                            v60Oracle.validatePullFromSourceCard(v66Zone, v67hGT);
                                        if (v67hResult.outcome ==
                                            com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.PullOutcome.WILL_FAIL) {
                                            action.addReasoning("V67h MEMORY (game-text): " + v67hResult.reason, -9999.0f);
                                            LOG.warn("V67h MEMORY WILL_FAIL: source={} — {}",
                                                v67hSrcCard.getTitle(), v67hResult.reason);
                                            actions.add(action);
                                            continue;
                                        } else if (v67hResult.outcome ==
                                            com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.PullOutcome.WILL_SUCCEED) {
                                            LOG.info("V67h MEMORY OK: source={} — {}",
                                                v67hSrcCard.getTitle(), v67hResult.reason);
                                        }
                                    }
                                }
                            }
                        } catch (NumberFormatException nfe) { /* ignore */ }
                        catch (Exception e) { LOG.debug("V67h: error: {}", e.getMessage()); }
                    }
                }

                // Passed all guards — positive signal (final score combines with plan bonuses)
                action.addReasoning("V60 RESERVE PULL: try every turn — free value", 100.0f);
                LOG.warn("V60 RESERVE PULL: '{}' passed guards — +100 baseline", actionText);
            }

            // V38.4 + V56 FIX 18: DEPLOY URGENCY — hand size + Force pile
            // V56: closed the mid/late-game urgency gap (handSize < 9 used to give 0).
            {
                int handSize = hand != null ? hand.size() : 0;
                float urgencyBonus = 0;
                if (handSize >= 12) {
                    urgencyBonus = 200.0f + (handSize - 12) * 50.0f;
                } else if (handSize >= 9) {
                    urgencyBonus = 100.0f + (handSize - 9) * 30.0f;
                } else if (handSize >= 5) {
                    urgencyBonus = 80.0f;   // V56: mid-hand baseline
                } else if (handSize >= 1) {
                    urgencyBonus = 50.0f;   // V56: small-hand baseline
                }
                if (availableForce >= 10 && handSize >= 8) {
                    urgencyBonus += 100.0f;
                }
                if (availableForce >= 6 && handSize >= 1 && handSize < 8) {
                    urgencyBonus += 80.0f;  // V56: unused force with any hand
                }
                if (urgencyBonus > 0) {
                    action.addReasoning(String.format(
                        "V38.4 DEPLOY URGENCY: hand=%d, force=%d (+%.0f)",
                        handSize, availableForce, urgencyBonus), urgencyBonus);
                }
            }

            // === APPLY PHASE-LEVEL PLAN ===
            // V24.10: NEVER hold back on turns 1-2. The engine MUST be built ASAP:
            //   Locations → AMSD (Piett + Executor) → Lando/Lobot → everything else.
            // Holding back early wastes critical setup turns.
            // After turn 2, HOLD_BACK can apply to non-location cards only.
            // Locations are ALWAYS exempt from HOLD_BACK regardless of turn.
            if (plan != null && plan.getStrategy() == DeployStrategy.HOLD_BACK) {
                int holdBackTurn = context.getTurnNumber();
                if (holdBackTurn <= 2) {
                    // Turns 1-2: IGNORE hold-back entirely — build the engine!
                    LOG.warn("V24.10 NO HOLD_BACK TURNS 1-2: Turn {} — ignoring hold-back, must build engine! Action: '{}'",
                        holdBackTurn, actionText);
                    // Fall through to normal scoring
                } else {
                    // V46: Turn 3+: HOLD_BACK only at start, not end of game!
                    // Once past setup turns, deploy aggressively like any other deck.
                    LOG.warn("V46 HOLD_BACK EXPIRED: Turn {} — past setup phase, deploy freely!", holdBackTurn);
                    // Fall through to normal scoring
                }
            }

            // === Get card ID from decision parameters ===
            // For CARD_ACTION_CHOICE, each action has an associated cardId at the same index
            List<String> cardIdList = context.getCardIds();
            List<String> blueprintList = context.getBlueprints();
            String cardIdStr = (cardIdList != null && i < cardIdList.size()) ? cardIdList.get(i) : null;
            String blueprintIdFromParam = (blueprintList != null && i < blueprintList.size()) ? blueprintList.get(i) : null;

            // Get card title from testingText (MOST RELIABLE - directly from GEMP)
            String cardTitleFromGemp = context.getCardTitleAt(i);
            LOG.info("[DeployEvaluator] Action[{}]: cardId='{}', blueprintId='{}', CARD_TITLE='{}', actionText='{}'",
                i, cardIdStr, blueprintIdFromParam, cardTitleFromGemp, actionText);

            // NOTE: We used to check pendingDeployCardIds here to avoid loops,
            // but the tracking was broken (added during evaluation, not after selection).
            // Loop detection is now handled by DecisionTracker at a higher level.
            // If loops become an issue, we need to track selected actions in CombinedEvaluator.

            // === EARLY-GAME DEPLOYMENT RESTRICTION ===
            // Block certain Effects from being deployed on turn 1.
            // Uses title-based matching from CardKnowledge.shouldBlockDeployment().
            // The title can come from GEMP's testingText or from the action text itself.
            int currentTurn = context.getTurnNumber();
            String titleForRestrictionCheck = cardTitleFromGemp;
            if (titleForRestrictionCheck == null) {
                // Fallback: try to extract title from action text (strip HTML, bullets, etc.)
                titleForRestrictionCheck = actionText.replaceAll("<[^>]*>", "").replace("•", "").trim();
            }
            if (CardKnowledge.shouldBlockDeployment(titleForRestrictionCheck, currentTurn)) {
                LOG.warn("🚫 BLOCKING turn-1 deploy of Effect '{}' (turn {})", titleForRestrictionCheck, currentTurn);
                action.addReasoning("BLOCKED: Do not deploy this Effect on turn 1", -9999.0f);
                actions.add(action);
                continue;
            }

            // === LOCATION DEPLOYMENT - Highest Priority ===
            // Deploying locations opens up deployment options
            if (actionLower.contains("location") || actionLower.contains("site") || actionLower.contains("system")) {
                action.addReasoning("LOCATION - deploy first!", 200.0f);

                // === V24.10: EXTRA LOCATION PRIORITY WHEN PIETT NEEDS FINDING ===
                // If Piett is stuck in the force pile, deploying more locations means
                // more force generation → bigger force pile → draw through faster to find him.
                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle locOracle = context.getDeckOracle();
                if (locOracle != null && locOracle.isAnalyzed()) {
                    boolean piettAccessible = locOracle.isCardInHand("Admiral Piett") || locOracle.isCardInHand("Piett")
                        || locOracle.isCardInReserve("Admiral Piett") || locOracle.isCardInReserve("Piett")
                        || locOracle.isCardInPlay("Admiral Piett") || locOracle.isCardInPlay("Piett");
                    boolean piettLost = locOracle.isCardLost("Admiral Piett") || locOracle.isCardLost("Piett");
                    if (!piettAccessible && !piettLost && context.getTurnNumber() <= 4) {
                        action.addReasoning("V24.10 PIETT MISSING: Deploy locations to generate force — need to draw for Piett!", 150.0f);
                        LOG.warn("V24.10 PIETT DIG: Piett not accessible — extra location deploy priority (+150) to power force pile draws!");
                    }
                }

                // === V23: BESPIN SYSTEM EARLY DEPLOY PRIORITY ===
                // For TDIGWATT, Bespin system is the FOUNDATION of the entire objective.
                // Without Bespin on table, nothing works: no Dark Deal, no CC Occupation,
                // no AMSD deploy target. Deploy it IMMEDIATELY on turns 1-3.
                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer bespinObjAnalyzer =
                    context.getObjectiveAnalyzer();
                int turnNum = context.getTurnNumber();
                if (bespinObjAnalyzer != null && bespinObjAnalyzer.isAnalyzed()
                    && bespinObjAnalyzer.needsBespinSystemPresence() && turnNum <= 3) {
                    // Check if this action deploys Bespin system specifically
                    boolean isBespinDeploy = actionLower.contains("bespin");
                    if (!isBespinDeploy && cardTitleFromGemp != null) {
                        isBespinDeploy = cardTitleFromGemp.toLowerCase(Locale.ROOT).contains("bespin");
                    }
                    if (isBespinDeploy) {
                        // Check Bespin isn't already on table
                        boolean bespinOnTable = false;
                        if (gameState != null) {
                            for (PhysicalCard loc : gameState.getLocationsInOrder()) {
                                if (loc != null && loc.getTitle() != null &&
                                    loc.getTitle().toLowerCase(Locale.ROOT).contains("bespin") &&
                                    loc.getBlueprint() != null && loc.getBlueprint().getCardSubtype() != null &&
                                    loc.getBlueprint().getCardSubtype() == com.gempukku.swccgo.common.CardSubtype.SYSTEM) {
                                    bespinOnTable = true;
                                    break;
                                }
                            }
                        }
                        if (!bespinOnTable) {
                            // V24.15: Mega-boost on Turn 1 — Bespin MUST be absolute first deploy!
                            float bespinBoost = (turnNum <= 1) ? 800.0f : 400.0f;
                            action.addReasoning("V24.15 BESPIN PRIORITY: Deploy Bespin system FIRST — objective foundation!", bespinBoost);
                            LOG.warn("V24.15 BESPIN PRIORITY: Bespin system deploy gets +{} on turn {} — MUST deploy ASAP!", bespinBoost, turnNum);
                        }
                    }
                }

                actions.add(action);
                continue;
            }

            // === Look up the card using multiple methods (like Python) ===
            PhysicalCard card = null;
            String blueprintIdFromHtml = null;

            LOG.warn("🔎 [Method 1] Trying extractBlueprintFromActionHtml for: '{}'", actionText);

            // Method 1: Extract blueprint from action text HTML (most reliable)
            // GEMP includes card hints like: <div class='cardHint' value='7_305'>•Card Name</div>
            blueprintIdFromHtml = extractBlueprintFromActionHtml(actionText);
            LOG.warn("🔎 [Method 1] Result: blueprintIdFromHtml = '{}'", blueprintIdFromHtml);

            if (blueprintIdFromHtml != null && hand != null) {
                LOG.warn("🔎 [Method 1] Searching hand ({} cards) for blueprint '{}'", hand.size(), blueprintIdFromHtml);
                // Find card in hand by blueprint ID
                for (PhysicalCard handCard : hand) {
                    if (handCard != null && blueprintIdFromHtml.equals(handCard.getBlueprintId(true))) {
                        card = handCard;
                        LOG.warn("🔎 [Method 1] ✅ Found card by HTML blueprint {}: {}", blueprintIdFromHtml, card.getTitle());
                        break;
                    }
                }
                if (card == null) {
                    LOG.warn("🔎 [Method 1] ❌ No card in hand matches blueprint '{}'", blueprintIdFromHtml);
                }
            } else {
                LOG.warn("🔎 [Method 1] Skipped: blueprintIdFromHtml={}, hand={}", blueprintIdFromHtml, hand != null ? "exists" : "null");
            }

            // Method 2: Try to find card by cardId in game state
            LOG.warn("🔎 [Method 2] Trying gameState.findCardById for cardIdStr='{}' (card still null={})", cardIdStr, card == null);
            if (card == null && cardIdStr != null && !cardIdStr.isEmpty() && gameState != null) {
                try {
                    int cardIdNum = Integer.parseInt(cardIdStr);
                    LOG.warn("🔎 [Method 2] Parsed cardId as int: {}", cardIdNum);
                    card = gameState.findCardById(cardIdNum);
                    if (card != null) {
                        LOG.warn("🔎 [Method 2] ✅ Found card by ID {}: {}", cardIdNum, card.getTitle());
                    } else {
                        LOG.warn("🔎 [Method 2] ❌ gameState.findCardById({}) returned null", cardIdNum);
                    }
                } catch (NumberFormatException e) {
                    LOG.warn("🔎 [Method 2] ❌ Could not parse cardId '{}' as integer", cardIdStr);
                }
            } else {
                LOG.warn("🔎 [Method 2] Skipped: card={}, cardIdStr='{}', gameState={}",
                    card != null ? "found" : "null", cardIdStr, gameState != null ? "exists" : "null");
            }

            // Method 3: Try to use blueprintId from decision params
            LOG.warn("🔎 [Method 3] Trying blueprintIdFromParam='{}' (card still null={})", blueprintIdFromParam, card == null);
            if (card == null && blueprintIdFromParam != null && !blueprintIdFromParam.isEmpty() &&
                !"inPlay".equals(blueprintIdFromParam) && hand != null) {
                LOG.warn("🔎 [Method 3] Searching hand for blueprint '{}'", blueprintIdFromParam);
                // Find card in hand by blueprint ID from params
                for (PhysicalCard handCard : hand) {
                    if (handCard != null && blueprintIdFromParam.equals(handCard.getBlueprintId(true))) {
                        card = handCard;
                        LOG.warn("🔎 [Method 3] ✅ Found card by param blueprint {}: {}", blueprintIdFromParam, card.getTitle());
                        break;
                    }
                }
                if (card == null) {
                    LOG.warn("🔎 [Method 3] ❌ No card in hand matches blueprint '{}'", blueprintIdFromParam);
                }
            } else {
                LOG.warn("🔎 [Method 3] Skipped: card={}, blueprintIdFromParam='{}', hand={}",
                    card != null ? "found" : "null", blueprintIdFromParam, hand != null ? "exists" : "null");
            }

            // Method 4: Fallback - try to match by title in action text (rarely needed)
            if (card == null) {
                card = findCardInHand(hand, actionText);
                if (card != null) {
                    LOG.info("🔎 [Method 4] ✅ Found card by title match: {}", card.getTitle());
                }
                // Don't log failure - findCardInHand already logs at debug level
            }

            // Final result
            if (card == null) {
                LOG.warn("❌❌❌ CARD LOOKUP FAILED for action '{}' - ALL 4 METHODS FAILED ❌❌❌",
                    actionText.length() > 80 ? actionText.substring(0, 80) + "..." : actionText);
                LOG.warn("    cardIdStr='{}', blueprintFromHtml='{}', blueprintFromParam='{}'",
                    cardIdStr, blueprintIdFromHtml, blueprintIdFromParam);
            } else {
                LOG.warn("✅✅✅ CARD LOOKUP SUCCESS: {} (bp={}) ✅✅✅", card.getTitle(), card.getBlueprintId(true));
            }
            if (card != null) {
                SwccgCardBlueprint blueprint = card.getBlueprint();
                if (blueprint != null) {
                    action.setCardName(card.getTitle());

                    // === V83 (Steve, 2026-05-16): MY LORD — SENATORS ONLY AT GALACTIC SENATE ===
                    // Mirror of Rando V83. See rando DeployEvaluator V83 comment
                    // for full rationale. Senators at non-Senate sites die to
                    // weapon fire (My Lord's -6 weapon destiny only applies at
                    // Galactic Senate). Objective also requires senators at the
                    // Senate to stay flipped.
                    {
                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer mlObj =
                            context.getObjectiveAnalyzer();
                        if (mlObj != null && mlObj.isAnalyzed() && mlObj.getObjectiveTitle() != null
                                && gameState != null && game != null) {
                            String mlObjLower = mlObj.getObjectiveTitle().toLowerCase(Locale.ROOT);
                            boolean isMyLord = mlObjLower.contains("my lord")
                                || mlObjLower.contains("make it legal");
                            if (isMyLord
                                    && com.gempukku.swccgo.filters.Filters.senator.accepts(
                                        gameState, game.getModifiersQuerying(), card)) {
                                PhysicalCard mlTargetLoc = null;
                                String mlActionLower = actionText.toLowerCase(Locale.ROOT);
                                for (PhysicalCard loc : gameState.getTopLocations()) {
                                    if (loc == null || loc.getTitle() == null) continue;
                                    if (mlActionLower.contains(loc.getTitle().toLowerCase(Locale.ROOT))) {
                                        mlTargetLoc = loc;
                                        break;
                                    }
                                }
                                // V83.1 mirror — only penalize when target identifiable.
                                if (mlTargetLoc != null) {
                                    boolean atSenate = com.gempukku.swccgo.filters.Filters.Galactic_Senate.accepts(
                                        gameState, game.getModifiersQuerying(), mlTargetLoc);
                                    if (!atSenate) {
                                        action.addReasoning(
                                            "V83 MY LORD: senator '" + card.getTitle()
                                                + "' → '" + mlTargetLoc.getTitle()
                                                + "' — must deploy to Galactic Senate (dies elsewhere)",
                                            -2000.0f);
                                        LOG.warn("V83 MY LORD: blocking senator {} → {} (only Galactic Senate is safe)",
                                            card.getTitle(), mlTargetLoc.getTitle());
                                    }
                                }
                            }
                        }
                    }

                    // === V86 (Steve, 2026-05-16): INVASION — NEIMOIDIAN PILOTS ABOARD CAPITAL SHIP ===
                    // Mirror of Rando V86. See rando DeployEvaluator V86 comment for full rationale.
                    {
                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer invObj =
                            context.getObjectiveAnalyzer();
                        if (invObj != null && invObj.isAnalyzed() && invObj.getObjectiveTitle() != null
                                && gameState != null && game != null) {
                            String invObjLower = invObj.getObjectiveTitle().toLowerCase(Locale.ROOT);
                            boolean isInvasion = invObjLower.contains("invasion");
                            boolean isNeimoidianPilot =
                                com.gempukku.swccgo.filters.Filters.Neimoidian.accepts(
                                    gameState, game.getModifiersQuerying(), card)
                                && com.gempukku.swccgo.filters.Filters.pilot.accepts(
                                    gameState, game.getModifiersQuerying(), card);
                            if (isInvasion && isNeimoidianPilot) {
                                PhysicalCard friendlyCapital = null;
                                for (PhysicalCard pCard : gameState.getAllPermanentCards()) {
                                    if (pCard == null) continue;
                                    if (!playerId.equals(pCard.getOwner())) continue;
                                    if (com.gempukku.swccgo.filters.Filters.capital_starship.accepts(
                                            gameState, game.getModifiersQuerying(), pCard)) {
                                        friendlyCapital = pCard;
                                        break;
                                    }
                                }
                                if (friendlyCapital != null) {
                                    // V86.1 mirror — only act when action text is target-explicit.
                                    String v86ActionLower = actionText.toLowerCase(Locale.ROOT);
                                    String capitalTitleLower = friendlyCapital.getTitle() != null
                                        ? friendlyCapital.getTitle().toLowerCase(Locale.ROOT) : "";
                                    boolean targetExplicit =
                                        v86ActionLower.contains("aboard")
                                        || v86ActionLower.contains(" to ")
                                        || v86ActionLower.contains(" on ");
                                    if (targetExplicit) {
                                        boolean aboardCapital = !capitalTitleLower.isEmpty()
                                            && v86ActionLower.contains(capitalTitleLower);
                                        if (!aboardCapital) {
                                            action.addReasoning(
                                                "V86 INVASION: Neimoidian pilot '" + card.getTitle()
                                                    + "' must deploy aboard friendly capital ship '"
                                                    + friendlyCapital.getTitle()
                                                    + "' (vulnerable on ground sites)",
                                                -1500.0f);
                                            LOG.warn("V86 INVASION: blocking Neimoidian pilot {} (target not aboard {}) → -1500",
                                                card.getTitle(), friendlyCapital.getTitle());
                                        } else {
                                            action.addReasoning(
                                                "V86 INVASION: Neimoidian pilot '" + card.getTitle()
                                                    + "' deploying aboard capital ship — correct placement!",
                                                300.0f);
                                            LOG.info("V86 INVASION: Neimoidian pilot {} aboard {} → +300",
                                                card.getTitle(), friendlyCapital.getTitle());
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // === V88 (Steve, 2026-05-18): MY LORD — SENATOR → GALACTIC SENATE BONUS ===
                    // Mirror of Rando V88. See Rando DeployEvaluator V88 comment for rationale.
                    {
                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer mlObj88 =
                            context.getObjectiveAnalyzer();
                        if (mlObj88 != null && mlObj88.isAnalyzed() && mlObj88.getObjectiveTitle() != null
                                && gameState != null && game != null) {
                            String mlObj88Lower = mlObj88.getObjectiveTitle().toLowerCase(Locale.ROOT);
                            boolean isMyLord88 = mlObj88Lower.contains("my lord")
                                || mlObj88Lower.contains("make it legal");
                            if (isMyLord88
                                    && com.gempukku.swccgo.filters.Filters.senator.accepts(
                                        gameState, game.getModifiersQuerying(), card)) {
                                String v88ActionLower = actionText.toLowerCase(Locale.ROOT);
                                if (v88ActionLower.contains("galactic senate")) {
                                    action.addReasoning(
                                        "V88 MY LORD: senator '" + card.getTitle()
                                            + "' → Galactic Senate (flip condition + weapon destiny -6 protection)",
                                        1500.0f);
                                    LOG.warn("V88 MY LORD: BOOST senator {} → Galactic Senate → +1500",
                                        card.getTitle());
                                }
                            }
                        }
                    }

                    // === V89 (Steve, 2026-05-18): DR. EVAZAN — NEEDS ARMED PARTNER ===
                    // Mirror of Rando V89. See Rando DeployEvaluator V89 comment for rationale.
                    {
                        String cardTitleForEvazan = card.getTitle();
                        if (cardTitleForEvazan != null
                                && cardTitleForEvazan.startsWith("Dr. Evazan")
                                && gameState != null && game != null) {
                            PhysicalCard evazanTargetLoc = null;
                            String evazanActionLower = actionText.toLowerCase(Locale.ROOT);
                            for (PhysicalCard loc : gameState.getTopLocations()) {
                                if (loc == null || loc.getTitle() == null) continue;
                                if (evazanActionLower.contains(loc.getTitle().toLowerCase(Locale.ROOT))) {
                                    evazanTargetLoc = loc;
                                    break;
                                }
                            }
                            if (evazanTargetLoc != null) {
                                boolean armedFriendAtTarget = false;
                                for (PhysicalCard pCard : gameState.getAllPermanentCards()) {
                                    if (pCard == null) continue;
                                    if (!playerId.equals(pCard.getOwner())) continue;
                                    if (pCard == card) continue;
                                    PhysicalCard pCardLoc = null;
                                    try {
                                        pCardLoc = game.getModifiersQuerying().getLocationThatCardIsAt(gameState, pCard);
                                    } catch (Exception ignore) { /* */ }
                                    if (pCardLoc != evazanTargetLoc) continue;
                                    if (com.gempukku.swccgo.filters.Filters.character_with_a_weapon.accepts(
                                            gameState, game.getModifiersQuerying(), pCard)) {
                                        armedFriendAtTarget = true;
                                        break;
                                    }
                                }
                                if (!armedFriendAtTarget) {
                                    action.addReasoning(
                                        "V89 DR. EVAZAN: '" + cardTitleForEvazan
                                            + "' deploying to '" + evazanTargetLoc.getTitle()
                                            + "' with no armed friend — block (will get sniped)",
                                        -1500.0f);
                                    LOG.warn("V89 DR. EVAZAN: blocking {} → {} (no armed friend present)",
                                        cardTitleForEvazan, evazanTargetLoc.getTitle());
                                }
                            }
                        }
                    }

                    // === V90 (Steve, 2026-05-19): NO SOLO DEPLOY TO SITE WITH ENEMY WEAPON ===
                    // Mirror of Rando V90. See Rando DeployEvaluator V90 comment for rationale.
                    if (card != null && blueprint != null
                            && blueprint.getCardCategory() == CardCategory.CHARACTER
                            && gameState != null && game != null) {
                        PhysicalCard v90TargetLoc = null;
                        String v90ActionLower = actionText.toLowerCase(Locale.ROOT);
                        for (PhysicalCard loc : gameState.getTopLocations()) {
                            if (loc == null || loc.getTitle() == null) continue;
                            if (v90ActionLower.contains(loc.getTitle().toLowerCase(Locale.ROOT))) {
                                v90TargetLoc = loc;
                                break;
                            }
                        }
                        if (v90TargetLoc != null) {
                            boolean enemyArmedAtTarget = false;
                            boolean friendlyArmedAtTarget = false;
                            String opponentId = game.getOpponent(playerId);
                            for (PhysicalCard pCard : gameState.getAllPermanentCards()) {
                                if (pCard == null || pCard == card) continue;
                                PhysicalCard pCardLoc = null;
                                try {
                                    pCardLoc = game.getModifiersQuerying().getLocationThatCardIsAt(gameState, pCard);
                                } catch (Exception ignore) { /* */ }
                                if (pCardLoc != v90TargetLoc) continue;
                                boolean armed = false;
                                try {
                                    armed = com.gempukku.swccgo.filters.Filters.character_with_a_weapon.accepts(
                                        gameState, game.getModifiersQuerying(), pCard);
                                } catch (Exception ignore) { /* */ }
                                if (!armed) continue;
                                if (opponentId != null && opponentId.equals(pCard.getOwner())) {
                                    enemyArmedAtTarget = true;
                                } else if (playerId.equals(pCard.getOwner())) {
                                    friendlyArmedAtTarget = true;
                                }
                            }
                            if (enemyArmedAtTarget && !friendlyArmedAtTarget) {
                                action.addReasoning(
                                    "V90 NO SUICIDE DEPLOY: '" + card.getTitle()
                                        + "' → '" + v90TargetLoc.getTitle()
                                        + "' has enemy armed char + no friendly weapon — will be sniped on weapons segment",
                                    -1500.0f);
                                LOG.warn("V90 NO SUICIDE DEPLOY: blocking {} → {} (enemy weapon present, no friendly weapon)",
                                    card.getTitle(), v90TargetLoc.getTitle());
                            }
                        }
                    }

                    // === DEPLOYMENT PLAN SCORING ===
                    // If we have a plan, score based on whether this card is in the plan
                    String blueprintId = card.getBlueprintId(true);

                    if (plan != null) {
                        if (!plan.getInstructions().isEmpty()) {
                            // Plan has pending instructions - check if this card is in plan
                            DeploymentInstruction instruction = plan.getInstructionForCard(blueprintId);

                            if (instruction != null) {
                                // Card is in plan - high priority!
                                action.addReasoning("IN DEPLOYMENT PLAN: " + plan.getStrategy().getValue(), 100.0f);

                                // Extra bonus based on instruction priority
                                int priority = instruction.getPriority();
                                if (priority <= 1) {
                                    action.addReasoning("Highest priority deployment", 50.0f);
                                } else if (priority <= 3) {
                                    action.addReasoning("High priority deployment", 25.0f);
                                }
                            } else if (!plan.isForceAllowExtras()) {
                                // Card is NOT in plan and we're not allowing extras
                                // CRITICAL: If plan is DEPLOY_LOCATIONS, block NON-LOCATION cards.
                                // But actual LOCATION cards (like Bespin from hand) should still be allowed!
                                if (plan.getStrategy() == DeployStrategy.DEPLOY_LOCATIONS) {
                                    // V24.10 FIX: Check if card IS a location before blocking!
                                    // Bespin can be in hand (from objective pull) but not in the plan.
                                    CardCategory planCheckCategory = blueprint.getCardCategory();
                                    if (planCheckCategory == CardCategory.LOCATION) {
                                        LOG.warn("📋 V24.10: {} is a LOCATION not in plan — ALLOWING during DEPLOY_LOCATIONS (locations always welcome!)", card.getTitle());
                                        action.addReasoning("V24.10: Location not in plan but DEPLOY_LOCATIONS allows all locations!", 100.0f);
                                    } else {
                                        LOG.warn("🚫 BLOCKING non-location deploy during DEPLOY_LOCATIONS plan: {}", card.getTitle());
                                        action.addReasoning("BLOCKED: Plan is DEPLOY_LOCATIONS ONLY - no characters/ships!", -1000.0f);
                                        actions.add(action);
                                        continue;  // Skip all other scoring - this action is blocked
                                    }
                                } else if (plan.isWaitingForPlannedCards()) {
                                    if (availableForce < 8) {
                                        LOG.warn("📋 Low force — saving for planned cards: {}", card.getTitle());
                                        action.addReasoning("Saving force for planned cards", -200.0f);
                                        actions.add(action);
                                        continue;
                                    } else {
                                        action.addReasoning("V38.4: Plenty of Force — deploy off-plan!", -20.0f);
                                    }
                                } else {
                                    action.addReasoning("NOT in deployment plan", -50.0f);
                                }
                            } else {
                                // Plan allows extras (stale plan) - but still respect DEPLOY_LOCATIONS
                                if (plan.getStrategy() == DeployStrategy.DEPLOY_LOCATIONS) {
                                    LOG.warn("🚫 BLOCKING non-location deploy - stale plan but DEPLOY_LOCATIONS means locations only!");
                                    action.addReasoning("BLOCKED: Stale plan but DEPLOY_LOCATIONS - no character/ship deploys!", -1000.0f);
                                    actions.add(action);
                                    continue;  // Skip all other scoring
                                }
                                action.addReasoning("Extra deploy (plan stale)", 0.0f);
                            }
                        } else if (plan.isPlanComplete()) {
                            // Plan is complete! All planned deployments are done.
                            // CRITICAL FIX: DEPLOY_LOCATIONS means "deploy locations FIRST", not "ONLY locations"
                            // Once locations are deployed, we should allow character/ship deploys normally.
                            // The strategy was just to ensure locations came first to open deployment options.
                            if (plan.getStrategy() == DeployStrategy.DEPLOY_LOCATIONS) {
                                LOG.info("✅ DEPLOY_LOCATIONS plan complete - now allowing character/ship deploys");
                                action.addReasoning("DEPLOY_LOCATIONS complete - extra deploy allowed", 25.0f);
                            }

                            // For all strategies (DEPLOY_LOCATIONS, ESTABLISH, REINFORCE), allow extra deploys
                            int extraBudget = plan.getExtraForceBudget(availableForce);
                            if (extraBudget > 0) {
                                action.addReasoning("Plan COMPLETE - extra deploy allowed", 25.0f);
                            } else {
                                // Saving force for battle
                                action.addReasoning("Plan complete but reserving force for battle", -30.0f);
                            }
                        }

                        // Check if this card is in hold-back list
                        if (blueprintId != null && plan.getHoldBackCards().contains(blueprintId)) {
                            action.addReasoning("HOLD BACK - waiting for better opportunity", -80.0f);
                        }
                    }

                    // Get card stats (with type checks for safety)
                    // DeployCost exists on most deployable cards
                    int cost = 0;
                    try {
                        Float deployCost = blueprint.getDeployCost();
                        cost = deployCost != null ? deployCost.intValue() : 0;
                        // V22.3: Maintenance card check - need enough Force for upkeep AFTER deploying
                        // Maintenance cost = card's deploy cost. Must have that much Force
                        // remaining in Force Pile after paying deploy cost, or card dies at end of turn.
                        if (blueprint.hasIcon(com.gempukku.swccgo.common.Icon.MAINTENANCE)) {
                            int totalForce = context.getGameState() != null ?
                                context.getGameState().getForcePileSize(context.getPlayerId()) : 0;
                            int forceAfterDeploy = totalForce - cost;
                            // Maintenance cost = deploy cost (SWCCG rule)
                            int maintenanceCost = cost;

                            // V59 HOLISTIC MAINTENANCE: Account for other planned deploys AND
                            // battle reserve. FIXES Issue #4 from peaceful-pike replay: Lando
                            // deployed with 8F "post-deploy", but ChosenOne then spent 4F on Jyn +
                            // 1F on battle = only 3F left for 5F maintenance → Lando sacrificed.
                            // Look at all pending deploys this turn from the plan and subtract
                            // their cost. Also reserve 2F for battle interrupts/draws.
                            int pendingDeployCost = 0;
                            int battleReserve = 2;
                            try {
                                DeployPhasePlanner maintPlanner = context.getDeployPhasePlanner();
                                if (maintPlanner != null) {
                                    DeploymentPlan maintPlan = maintPlanner.getCurrentPlan();
                                    if (maintPlan != null && blueprintId != null) {
                                        for (DeploymentInstruction ins : maintPlan.getInstructions()) {
                                            if (ins == null) continue;
                                            // Skip the card we're currently evaluating (its cost already subtracted)
                                            if (blueprintId.equals(ins.getCardBlueprintId())) continue;
                                            pendingDeployCost += ins.getDeployCost();
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                LOG.debug("V59 MAINTENANCE: Error reading plan: {}", e.getMessage());
                            }
                            int forceAfterAllDeploys = forceAfterDeploy - pendingDeployCost - battleReserve;

                            // V64 TIGHTER MAINTENANCE: drains by opponent, Visage losses, and
                            // force losses to effects will further reduce our pile between deploy
                            // and end-of-turn. Require a DRAIN BUFFER on top of maintenance.
                            // Steve's feedback: "ChosenOne deployed Lando (maintenance card) and did
                            // not save enough force for him. Lost at the end of his turn."
                            // Previous -500/-600 weren't enough to override +300 V52 SPEND FORCE.
                            // Now -2000 hard block guarantees maintenance cards only deploy with
                            // comfortable headroom.
                            int drainBuffer = 2;  // opponent likely drains ~2/turn
                            int safeBuffer = maintenanceCost + drainBuffer;

                            if (forceAfterDeploy < maintenanceCost) {
                                // CANNOT pay maintenance even as first deploy — HARD BLOCK
                                action.addReasoning("V59 MAINTENANCE HARD: " + blueprint.getTitle() +
                                    " needs " + maintenanceCost + "F upkeep but only " +
                                    forceAfterDeploy + "F left — WILL die at end of turn!", -2000.0f);
                                LOG.warn("V59 MAINTENANCE HARD: {} costs {}, {}F available, {}F after deploy but needs {} for upkeep — HARD BLOCKED!",
                                    blueprint.getTitle(), cost, totalForce, forceAfterDeploy, maintenanceCost);
                            } else if (forceAfterAllDeploys < maintenanceCost) {
                                // Can pay if alone, but planned deploys + battle will consume too much
                                action.addReasoning("V59 MAINTENANCE HOLISTIC: " + blueprint.getTitle() +
                                    " needs " + maintenanceCost + "F but only " + forceAfterAllDeploys +
                                    "F after all planned deploys + battle reserve — WILL be sacrificed!", -1500.0f);
                                LOG.warn("V59 MAINTENANCE HOLISTIC: {} needs {}, only {}F after deploys({}) + reserve({}) = {}F — HARD BLOCKING!",
                                    blueprint.getTitle(), maintenanceCost, forceAfterAllDeploys,
                                    pendingDeployCost, battleReserve, forceAfterAllDeploys);
                            } else if (forceAfterAllDeploys < safeBuffer) {
                                // V64: Tight — opponent drain could push us below maintenance.
                                // Raised from -80 to -400 to clearly beat +300 V52 SPEND FORCE.
                                action.addReasoning("V64 MAINTENANCE TIGHT: " + blueprint.getTitle() +
                                    " — " + forceAfterAllDeploys + "F post-deploys, need "
                                    + maintenanceCost + "+" + drainBuffer + " drain buffer — likely sacrifice!",
                                    -400.0f);
                                LOG.warn("V64 MAINTENANCE TIGHT: {} — {}F post-deploys, need {} (maint) + {} (drain buffer)",
                                    blueprint.getTitle(), forceAfterAllDeploys, maintenanceCost, drainBuffer);
                            } else {
                                LOG.info("V59 MAINTENANCE OK: {} has {}F post-all-deploys, upkeep needs {} (+{}F drain buffer)",
                                    blueprint.getTitle(), forceAfterAllDeploys, maintenanceCost, drainBuffer);
                            }
                        }
                    } catch (UnsupportedOperationException e) {
                        // Card type doesn't support deployCost (e.g., Interrupt)
                    }

                    // === V24.5: RESERVE FORCE FOR EXISTING MAINTENANCE CARDS ===
                    // If cards with maintenance costs are already in play, deploying this card
                    // must leave enough Force to pay their upkeep. Otherwise they get sacrificed.
                    if (cost > 0 && gameState != null) {
                        try {
                            int existingMaintenanceCost = 0;
                            java.util.List<PhysicalCard> allInPlay = gameState.getAllPermanentCards();
                            if (allInPlay != null) {
                                for (PhysicalCard mCard : allInPlay) {
                                    if (mCard == null) continue;
                                    if (!context.getPlayerId().equals(mCard.getOwner())) continue;
                                    com.gempukku.swccgo.common.Zone mZone = mCard.getZone();
                                    if (mZone == null || !mZone.isInPlay()) continue;
                                    SwccgCardBlueprint mBp = mCard.getBlueprint();
                                    if (mBp != null && mBp.hasIcon(com.gempukku.swccgo.common.Icon.MAINTENANCE)) {
                                        Float mCost = mBp.getDeployCost();
                                        int cardMaint = (mCost != null) ? mCost.intValue() : 1;
                                        existingMaintenanceCost += cardMaint;
                                    }
                                }
                            }
                            if (existingMaintenanceCost > 0) {
                                int totalForceNow = gameState.getForcePileSize(context.getPlayerId());
                                int forceAfterThisDeploy = totalForceNow - cost;
                                if (forceAfterThisDeploy < existingMaintenanceCost) {
                                    action.addReasoning("V24.5 MAINTENANCE RESERVE: Deploying this leaves only " +
                                        forceAfterThisDeploy + " Force but need " + existingMaintenanceCost +
                                        " for existing maintenance cards — they'll be sacrificed!", -400.0f);
                                    LOG.warn("V24.5 MAINTENANCE RESERVE: {} costs {}, {} Force available, " +
                                        "only {} left but existing maintenance needs {} — BLOCKING!",
                                        blueprint.getTitle(), cost, totalForceNow, forceAfterThisDeploy, existingMaintenanceCost);
                                } else if (forceAfterThisDeploy < existingMaintenanceCost + 2) {
                                    action.addReasoning("V24.5 MAINTENANCE RESERVE: Tight on Force for existing maintenance (" +
                                        forceAfterThisDeploy + " left, need " + existingMaintenanceCost + ")", -100.0f);
                                    LOG.warn("V24.5 MAINTENANCE WARNING: {} — {} Force left after deploy, maintenance needs {}",
                                        blueprint.getTitle(), forceAfterThisDeploy, existingMaintenanceCost);
                                }
                            }
                        } catch (Exception e) {
                            LOG.debug("V24.5: Error checking maintenance reserve: {}", e.getMessage());
                        }
                    }

                    // Power only exists on Character, Starship, Vehicle
                    int powerVal = 0;
                    if (blueprint.hasPowerAttribute()) {
                        Float power = blueprint.getPower();
                        powerVal = power != null ? power.intValue() : 0;
                    }

                    // Ability only exists on Character, some Vehicles
                    int abilityVal = 0;
                    if (blueprint.hasAbilityAttribute()) {
                        Float ability = blueprint.getAbility();
                        abilityVal = ability != null ? ability.intValue() : 0;
                    }

                    // Destiny exists on most cards
                    float destinyVal = 0;
                    try {
                        Float destiny = blueprint.getDestiny();
                        destinyVal = destiny != null ? destiny : 0;
                    } catch (UnsupportedOperationException e) {
                        // Card type doesn't support destiny
                    }

                    action.setDeployCost(cost);

                    // === AFFORDABILITY CHECK ===
                    if (cost > availableForce) {
                        action.addReasoning(
                            String.format("Can't afford! Need %d, have %d", cost, availableForce),
                            -1000.0f
                        );
                        actions.add(action);
                        continue;
                    }

                    // === V48: VADER MOVEMENT FORCE RESERVE ===
                    if (vaderMoveReserve > 0 && cost > 0) {
                        int forceAfterDeploy = availableForce - cost;
                        if (forceAfterDeploy < vaderMoveReserve) {
                            action.addReasoning(String.format(
                                "V48 VADER MOVE RESERVE: Deploy costs %d, leaves %d — need %d for Vader to move!",
                                cost, forceAfterDeploy, vaderMoveReserve), -500.0f);
                            LOG.warn("V48 VADER MOVE RESERVE: {} costs {} force, leaves {} — Vader needs {} to move!",
                                card != null ? card.getTitle() : actionText, cost, forceAfterDeploy, vaderMoveReserve);
                        }
                    }

                    // === V79 (Steve, 2026-05-15): VERGE DEATH-STAR MOVE RESERVE ===
                    if (v79VergeMoveReserve > 0 && cost > 0) {
                        int forceAfterDeployV79 = availableForce - cost;
                        if (forceAfterDeployV79 < v79VergeMoveReserve) {
                            action.addReasoning(String.format(
                                "V79 VERGE MOVE RESERVE: Deploy costs %d, leaves %d — need %d for Death Star move!",
                                cost, forceAfterDeployV79, v79VergeMoveReserve), -500.0f);
                            LOG.warn("V79 VERGE MOVE RESERVE: {} costs {}, leaves {} — Death Star needs {}!",
                                card != null ? card.getTitle() : actionText, cost, forceAfterDeployV79, v79VergeMoveReserve);
                        }
                    }

                    // === V51: CONTEST OPPONENT DRAIN LOCATIONS — DRAIN 2+ IS AN EMERGENCY ===
                    // Opponent drains are the #1 damage source. Drain 2+ sites are THE decisive
                    // battleground — both players will stack there, whoever wins that fight wins the game.
                    // Deploy aggressively to contest: flood the location with multiple characters.
                    // V51: Massively increased bonuses for drain 2+ sites. Every character sent
                    // to contest a high-drain site gets a large bonus, not just the first one.
                    if (blueprint.getCardCategory() == CardCategory.CHARACTER && gameState != null) {
                        try {
                            String v36Pid = context.getPlayerId();
                            String v36Oid = game.getOpponent(v36Pid);
                            String v36ActionLower = actionText.toLowerCase(Locale.ROOT);

                            // Find which location this deploy targets
                            for (PhysicalCard v36Loc : gameState.getTopLocations()) {
                                if (v36Loc == null || v36Loc.getTitle() == null) continue;
                                String v36LocLower = v36Loc.getTitle().toLowerCase(Locale.ROOT);
                                if (v36LocLower.isEmpty() || !v36ActionLower.contains(v36LocLower)) continue;

                                float v36OppPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, v36Loc, v36Oid, false, false);
                                float v36OurPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, v36Loc, v36Pid, false, false);

                                // V53: Check for OUR undercover spies at this location.
                                // Spies aren't counted as "present" by the game engine, so our power
                                // reads as 0 even when we have a spy there. Count spy power as
                                // POTENTIAL power — we could flip the spy to our side.
                                float v53SpyPower = 0;
                                try {
                                    for (PhysicalCard v53c : gameState.getCardsAtLocation(v36Loc)) {
                                        if (v53c != null && v36Pid.equals(v53c.getOwner()) && v53c.isUndercover()) {
                                            float spPow = 0;
                                            if (v53c.getBlueprint() != null && v53c.getBlueprint().hasPowerAttribute()) {
                                                Float sp = v53c.getBlueprint().getPower();
                                                spPow = (sp != null ? sp : 0);
                                            }
                                            v53SpyPower += spPow;
                                            LOG.info("V53 SPY ASSET: Our spy {} (power {}) at {} — counting as potential power",
                                                v53c.getTitle(), (int)spPow, v36Loc.getTitle());
                                        }
                                    }
                                    if (v53SpyPower > 0) {
                                        // Add spy power bonus — deploying here means we can flip the spy
                                        action.addReasoning(String.format(
                                            "V53 SPY ALLY: Our spy at %s has power %.0f — deploy here to flip and fight together!",
                                            v36Loc.getTitle(), v53SpyPower), 200.0f);
                                        LOG.warn("V53 SPY ALLY: Spy power {} at {} — +200 deploy bonus",
                                            (int)v53SpyPower, v36Loc.getTitle());
                                        // Count spy as our power for deploy decisions
                                        v36OurPower += v53SpyPower;
                                    }
                                } catch (Exception e) { /* ignore */ }

                                if (v36OppPower > 0) {
                                    // Opponent has presence — check drain amount
                                    float drainAmount = 1.0f;
                                    try {
                                        drainAmount = game.getModifiersQuerying().getForceDrainAmount(
                                            gameState, v36Loc, v36Oid);
                                    } catch (Exception e) { /* default 1 */ }

                                    if (drainAmount >= 3.0f && v36OurPower == 0) {
                                        // V51: EMERGENCY — drain 3+ uncontested, flood with everything
                                        action.addReasoning(String.format(
                                            "V51 DRAIN EMERGENCY: %s drains %.0f at %s — FLOOD this location!",
                                            v36Oid, drainAmount, v36Loc.getTitle()), 600.0f);
                                        LOG.warn("V51 DRAIN EMERGENCY: {} to {} — opponent drains {} uncontested (+600)",
                                            card.getTitle(), v36Loc.getTitle(), (int)drainAmount);
                                    } else if (drainAmount >= 3.0f && v36OurPower > 0) {
                                        // V51: Drain 3+ and we already sent someone — keep piling on
                                        action.addReasoning(String.format(
                                            "V51 DRAIN REINFORCE: %s drains %.0f at %s — keep piling on!",
                                            v36Oid, drainAmount, v36Loc.getTitle()), 500.0f);
                                        LOG.warn("V51 DRAIN REINFORCE: {} to {} — opponent drains {} we have presence (+500)",
                                            card.getTitle(), v36Loc.getTitle(), (int)drainAmount);
                                    } else if (drainAmount >= 2.0f && v36OurPower == 0) {
                                        // V51: Drain 2+ uncontested — this will be THE battle site
                                        action.addReasoning(String.format(
                                            "V51 CONTEST BATTLEGROUND: %s drains %.0f at %s — this is THE decisive fight!",
                                            v36Oid, drainAmount, v36Loc.getTitle()), 500.0f);
                                        LOG.warn("V51 CONTEST BATTLEGROUND: {} to {} — opponent drains {} uncontested (+500)",
                                            card.getTitle(), v36Loc.getTitle(), (int)drainAmount);
                                    } else if (drainAmount >= 2.0f && v36OurPower > 0) {
                                        // V51: Drain 2+ and we have presence — reinforce for the big fight
                                        action.addReasoning(String.format(
                                            "V51 REINFORCE BATTLEGROUND: %s drains %.0f at %s — reinforce for battle!",
                                            v36Oid, drainAmount, v36Loc.getTitle()), 500.0f);
                                        LOG.warn("V51 REINFORCE BATTLEGROUND: {} to {} — opponent drains {} we have presence (+500)",
                                            card.getTitle(), v36Loc.getTitle(), (int)drainAmount);
                                    } else if (v36OurPower == 0) {
                                        // Drain 1 uncontested — still worth contesting
                                        float contestDrainBonus = 200.0f + (drainAmount * 100.0f);
                                        action.addReasoning(String.format(
                                            "V36 CONTEST DRAIN: %s drains %.0f at %s UNCONTESTED — deploy to stop the bleeding!",
                                            v36Oid, drainAmount, v36Loc.getTitle()), contestDrainBonus);
                                        LOG.warn("V36 CONTEST DRAIN: {} to {} — opponent drains {} uncontested (+{})",
                                            card.getTitle(), v36Loc.getTitle(), (int)drainAmount, (int)contestDrainBonus);
                                    }
                                }
                                break; // Found target location
                            }
                        } catch (Exception e) {
                            LOG.debug("V51 CONTEST DRAIN: Error: {}", e.getMessage());
                        }
                    }

                    // === V51: VADER AGGRESSIVE FLIP — Deploy Vader from hand to opponent battleground ===
                    // If Hunt Down V objective is NOT flipped AND Vader is in hand, deploying
                    // Vader to ANY opponent's battleground site immediately flips the objective.
                    // This is THE highest priority play — Steve does this Turn 1 every game.
                    if (blueprint.getCardCategory() == CardCategory.CHARACTER && actionLower.contains("vader")
                        && !actionLower.contains("bounty") && !actionLower.contains("lightsaber")
                        && gameState != null && game != null) {
                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer vaderFlipAnalyzer =
                            context.getObjectiveAnalyzer();
                        if (vaderFlipAnalyzer != null && vaderFlipAnalyzer.isAnalyzed()
                            && vaderFlipAnalyzer.isHuntDownV() && !vaderFlipAnalyzer.isFlipped()) {
                            // Hunt Down not flipped — check if deploying to opponent's battleground
                            String vfPid = context.getPlayerId();
                            String vfOid = game.getOpponent(vfPid);
                            for (PhysicalCard vfLoc : gameState.getTopLocations()) {
                                if (vfLoc == null || vfLoc.getTitle() == null) continue;
                                String vfLocLower = vfLoc.getTitle().toLowerCase(Locale.ROOT);
                                if (!actionLower.contains(vfLocLower)) continue;
                                // Check if it's an opponent's location with their presence or their icons
                                try {
                                    float vfOppPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, vfLoc, vfOid, false, false);
                                    com.gempukku.swccgo.game.SwccgCardBlueprint vfLocBp = vfLoc.getBlueprint();
                                    boolean isOpponentSite = false;
                                    if (vfLocBp != null) {
                                        com.gempukku.swccgo.common.Side oppSide = (context.getSide() == com.gempukku.swccgo.common.Side.DARK)
                                            ? com.gempukku.swccgo.common.Side.LIGHT : com.gempukku.swccgo.common.Side.DARK;
                                        int oppIcons = (oppSide == com.gempukku.swccgo.common.Side.DARK)
                                            ? vfLocBp.getIconCount(com.gempukku.swccgo.common.Icon.DARK_FORCE)
                                            : vfLocBp.getIconCount(com.gempukku.swccgo.common.Icon.LIGHT_FORCE);
                                        if (oppIcons > 0 || vfOppPower > 0) isOpponentSite = true;
                                    }
                                    if (isOpponentSite) {
                                        action.addReasoning(String.format(
                                            "V51 VADER FLIP: Deploy Vader to %s — FLIPS OBJECTIVE IMMEDIATELY!",
                                            vfLoc.getTitle()), 900.0f);
                                        LOG.warn("V51 VADER FLIP: Vader to {} — Hunt Down flips! +900", vfLoc.getTitle());
                                    }
                                } catch (Exception e) { /* ignore */ }
                                break;
                            }
                        }
                    }

                    // === V34: DEPLOY DIRECTLY TO OPPONENTS — CONTEST THEIR LOCATIONS ===
                    // Deploy to locations where opponents have presence instead of empty locations.
                    // This prevents the "deploy to empty site, then waste Force moving" pattern.
                    if (blueprint.getCardCategory() == CardCategory.CHARACTER && gameState != null) {
                        try {
                            String opponentIdDeploy = game.getOpponent(playerId);
                            String actionTextLowerDeploy = actionText.toLowerCase(Locale.ROOT);

                            for (PhysicalCard locCard : gameState.getAllPermanentCards()) {
                                if (locCard == null || locCard.getBlueprint() == null) continue;
                                if (locCard.getBlueprint().getCardCategory() != CardCategory.LOCATION) continue;
                                com.gempukku.swccgo.common.Zone locZone = locCard.getZone();
                                if (locZone == null || !locZone.isInPlay()) continue;
                                String locTitleDeploy = locCard.getTitle() != null
                                    ? locCard.getTitle().toLowerCase(Locale.ROOT) : "";
                                if (locTitleDeploy.isEmpty()) continue;
                                if (!actionTextLowerDeploy.contains(locTitleDeploy)) continue;

                                float oppPowerHere = 0;
                                try {
                                    oppPowerHere = game.getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, locCard, opponentIdDeploy, false, false);
                                } catch (Exception e) { /* ignore */ }

                                if (oppPowerHere > 0) {
                                    // V49: Check our existing power + deploying card's power vs opponent
                                    float ourPowerHere = 0;
                                    try {
                                        ourPowerHere = game.getModifiersQuerying().getTotalPowerAtLocation(
                                            gameState, locCard, playerId, false, false);
                                    } catch (Exception e2) { /* ignore */ }
                                    float deployingPower = card.getBlueprint() != null ? card.getBlueprint().getPower() : 0;
                                    float totalOurPowerAfterDeploy = ourPowerHere + deployingPower;

                                    // V50: Deploy power-disadvantage penalty — turns 1-3 only, even-power threshold.
                                    // After turn 3, deploy everywhere no matter what — can't afford to sit idle.
                                    int v50Turn = context.getTurnNumber();
                                    if (v50Turn <= 3 && totalOurPowerAfterDeploy < oppPowerHere) {
                                        float disadvantagePenalty = -200.0f;
                                        action.addReasoning(String.format(
                                            "V50 EARLY DANGER: Turn %d — deploying %s to %s would leave us at power %.0f vs opponent %.0f — wait for backup!",
                                            v50Turn, card.getTitle(), locCard.getTitle(), totalOurPowerAfterDeploy, oppPowerHere), disadvantagePenalty);
                                        LOG.warn("V50 DEPLOY DANGER T{}: {} to {} — our power {}, opponent power {} — PENALIZED (turns 1-3 only)",
                                            v50Turn, card.getTitle(), locCard.getTitle(), (int)totalOurPowerAfterDeploy, (int)oppPowerHere);
                                        continue;
                                    } else if (v50Turn > 3 && totalOurPowerAfterDeploy < oppPowerHere) {
                                        action.addReasoning(String.format(
                                            "V50 LATE DEPLOY: Turn %d — deploying %s to %s despite power %.0f vs %.0f — must stay active!",
                                            v50Turn, card.getTitle(), locCard.getTitle(), totalOurPowerAfterDeploy, oppPowerHere), 0.0f);
                                        LOG.warn("V50 LATE DEPLOY T{}: {} to {} — our power {}, opponent power {} — deploying anyway (past turn 3)",
                                            v50Turn, card.getTitle(), locCard.getTitle(), (int)totalOurPowerAfterDeploy, (int)oppPowerHere);
                                    }

                                    float engageBonus = 250.0f;
                                    if (oppPowerHere >= 6) engageBonus += 100.0f;

                                    // V35: Check for Jedi at this location — Vader/Inquisitor bonuses
                                    boolean v35JediHere = false;
                                    boolean v35HatredHere = false;
                                    try {
                                        for (PhysicalCard lc : gameState.getCardsAtLocation(locCard)) {
                                            if (lc == null) continue;
                                            String lcTitle = lc.getTitle() != null ? lc.getTitle().toLowerCase(Locale.ROOT) : "";
                                            if (opponentIdDeploy.equals(lc.getOwner())) {
                                                if (isJediOrPadawan(lcTitle)) v35JediHere = true;
                                                java.util.List<PhysicalCard> stacked = gameState.getStackedCards(lc);
                                                if (stacked != null && !stacked.isEmpty()) v35HatredHere = true;
                                            }
                                        }
                                    } catch (Exception e) { /* ignore */ }

                                    String deployCardLower = card.getTitle() != null ? card.getTitle().toLowerCase(Locale.ROOT) : "";
                                    if (v35JediHere && deployCardLower.contains("vader")) {
                                        engageBonus += (float) ChosenOneConfig.SCORE_VADER_SEEK_JEDI; // +350 Vader hunts Jedi
                                        LOG.warn("V35 HUNT JEDI DEPLOY: Vader to {} with Jedi! (+{})",
                                            locCard.getTitle(), ChosenOneConfig.SCORE_VADER_SEEK_JEDI);
                                    }
                                    if (v35JediHere && isInquisitor(deployCardLower)) {
                                        engageBonus += 250.0f; // Inquisitor vs Jedi = power bonuses + destiny
                                        LOG.warn("V35 INQUISITOR vs JEDI: {} to {} (+250)", card.getTitle(), locCard.getTitle());
                                    }
                                    if (v35HatredHere && isInquisitor(deployCardLower)) {
                                        engageBonus += (float) ChosenOneConfig.SCORE_INQUISITOR_HATRED_SYNERGY; // +300
                                        LOG.warn("V35 INQUISITOR+HATRED: {} to {} with hatred (+{})",
                                            card.getTitle(), locCard.getTitle(), ChosenOneConfig.SCORE_INQUISITOR_HATRED_SYNERGY);
                                    }

                                    action.addReasoning(String.format(
                                        "V34 DIRECT ENGAGE: Deploy %s to %s (opp power %.0f%s%s) — contest!",
                                        card.getTitle(), locCard.getTitle(), oppPowerHere,
                                        v35JediHere ? " JEDI" : "", v35HatredHere ? " HATRED" : ""), engageBonus);
                                    LOG.warn("V34 DIRECT ENGAGE: {} to {} — opponents power={} (+{})",
                                        card.getTitle(), locCard.getTitle(), (int)oppPowerHere, (int)engageBonus);
                                } else {
                                    boolean opponentsElsewhere = false;
                                    for (PhysicalCard otherLoc : gameState.getAllPermanentCards()) {
                                        if (otherLoc == null || otherLoc.getBlueprint() == null) continue;
                                        if (otherLoc.getBlueprint().getCardCategory() != CardCategory.LOCATION) continue;
                                        if (otherLoc == locCard) continue;
                                        com.gempukku.swccgo.common.Zone oz = otherLoc.getZone();
                                        if (oz == null || !oz.isInPlay()) continue;
                                        try {
                                            float otherOppPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                                gameState, otherLoc, opponentIdDeploy, false, false);
                                            if (otherOppPower > 0) {
                                                opponentsElsewhere = true;
                                                break;
                                            }
                                        } catch (Exception e) { /* ignore */ }
                                    }
                                    if (opponentsElsewhere) {
                                        // V36: SMART EMPTY DEPLOY — penalty depends on context.
                                        // If we have enough Force AND characters to challenge opponents,
                                        // heavy penalty for empty site. But if we CAN'T challenge
                                        // (low Force, no characters in hand to pair up), deploying to
                                        // an empty drain site is acceptable for force economy.
                                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer emptyDeployAnalyzer =
                                            context.getObjectiveAnalyzer();
                                        boolean isHuntDown = emptyDeployAnalyzer != null
                                            && emptyDeployAnalyzer.isAnalyzed() && emptyDeployAnalyzer.isHuntDownV();

                                        // Check if this empty site has force drain icons (useful for our drains)
                                        boolean hasDrainValue = false;
                                        try {
                                            com.gempukku.swccgo.common.Side mySide36 = context.getSide();
                                            com.gempukku.swccgo.game.SwccgCardBlueprint locBp = locCard.getBlueprint();
                                            if (locBp != null) {
                                                int myIcons = (mySide36 == com.gempukku.swccgo.common.Side.DARK)
                                                    ? locBp.getIconCount(com.gempukku.swccgo.common.Icon.DARK_FORCE)
                                                    : locBp.getIconCount(com.gempukku.swccgo.common.Icon.LIGHT_FORCE);
                                                if (myIcons > 0) hasDrainValue = true;
                                            }
                                        } catch (Exception e) { /* ignore */ }

                                        // Count characters in hand that could deploy to opponent locations
                                        int charsInHand = 0;
                                        try {
                                            java.util.List<PhysicalCard> v36Hand = gameState.getHand(playerId);
                                            if (v36Hand != null) {
                                                for (PhysicalCard hc : v36Hand) {
                                                    if (hc != null && hc.getBlueprint() != null
                                                        && hc.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                                        charsInHand++;
                                                    }
                                                }
                                            }
                                        } catch (Exception e) { /* ignore */ }

                                        float emptyPenalty;
                                        if (isHuntDown && charsInHand >= 2) {
                                            // V37.4: Reduced from -600 — was blocking ALL deploys
                                            emptyPenalty = -300.0f;
                                        } else if (isHuntDown) {
                                            emptyPenalty = hasDrainValue ? -50.0f : -150.0f;
                                        } else {
                                            emptyPenalty = -150.0f;
                                        }

                                        action.addReasoning(String.format(
                                            "V36 EMPTY DEPLOY: %s to %s — no opponents here%s (penalty %.0f)",
                                            card.getTitle(), locCard.getTitle(),
                                            hasDrainValue ? " but has drain icons" : "", emptyPenalty),
                                            emptyPenalty);
                                        LOG.warn("V36 EMPTY DEPLOY: {} to {} (hunt={}, charsInHand={}, drainIcons={}, penalty={})",
                                            card.getTitle(), locCard.getTitle(), isHuntDown, charsInHand, hasDrainValue, (int)emptyPenalty);
                                    }
                                }
                                break;
                            }
                        } catch (Exception e) {
                            LOG.debug("V34 DIRECT ENGAGE: Error: {}", e.getMessage());
                        }
                    }

                    // === CARD VALUE SCORING ===
                    // Score based on power + ability vs cost
                    int cardValue = powerVal + abilityVal;
                    float valueRatio = cost > 0 ? (float) cardValue / cost : cardValue;

                    if (valueRatio >= 2.0f) {
                        action.addReasoning(String.format("Excellent value (%.1f)", valueRatio), 40.0f);
                    } else if (valueRatio >= 1.5f) {
                        action.addReasoning(String.format("Good value (%.1f)", valueRatio), 20.0f);
                    } else if (valueRatio >= 1.0f) {
                        action.addReasoning(String.format("Average value (%.1f)", valueRatio), 0.0f);
                    } else {
                        action.addReasoning(String.format("Below average value (%.1f)", valueRatio), -20.0f);
                    }

                    // === HIGH DESTINY BONUS ===
                    if (destinyVal >= 5.0f) {
                        action.addReasoning(String.format("High destiny (%.0f)", destinyVal), 15.0f);
                    }

                    // === CARD TYPE BONUSES ===
                    CardCategory category = blueprint.getCardCategory();

                    // === V24: MEGA LOCATION PRIORITY ===
                    // Locations are the foundation of EVERYTHING — force generation, deploy targets, drain sites.
                    // In the first 3 turns, deploying locations should dominate all other actions.
                    // V60 FIX: Only apply when the ACTION is actually deploying the location
                    // (source is a LOCATION card and actionText is a bare "Deploy" / "Deploy [location]"
                    // — NOT when the action invokes a location's game-text to pull a character
                    // like "Deploy a Padawan" or "Deploy Tala Durith from Reserve Deck".
                    // FIXES Issue #A from peaceful-pike replay: Rando invoked Malachor STE's
                    // "Deploy a Padawan" at force=0 because V24 thought it was a location deploy.
                    String v24ActionLower = actionText != null ? actionText.toLowerCase(Locale.ROOT) : "";
                    boolean isActualLocationDeploy = category == CardCategory.LOCATION
                        && (v24ActionLower.equals("deploy")
                            || v24ActionLower.startsWith("deploy ") && !v24ActionLower.contains("from reserve")
                            && !v24ActionLower.contains("padawan")
                            && !v24ActionLower.contains("jedi survivor")
                            && !v24ActionLower.contains("tala durith"));
                    if (isActualLocationDeploy) {
                        // V67ai Tier 4 (Steve, 2026-05-07): Hand-deploy of a location is the
                        // LAST resort in the location-deploy order: Objective (+2000) → Effect
                        // (+1800) → Interrupt (+1600) → Hand (+1400). Hand deploys are still
                        // critical — Rando must NEVER avoid deploying locations — but other
                        // sources should fire first to keep flexibility for next turn's deploys.
                        action.addReasoning("V67ai LOCATION DEPLOY ORDER [Tier 4 HAND]: deploy location from hand — force generation foundation!",
                            1400.0f);
                        LOG.warn("V67ai LOCATION TIER 4 HAND: {} (turn {}) → +1400",
                            card.getTitle(), context.getTurnNumber());
                    } else if (category == CardCategory.LOCATION) {
                        // Source is a location but action is a game-text pull — don't give +200
                        LOG.info("V60 V24 SKIP: '{}' on {} is a game-text pull, not a location deploy — no +200 bonus",
                            actionText, card.getTitle());
                    }

                    // === V67i GLOBAL LOCATION-FIRST PRIORITY ===
                    // Boost any action that adds a location to the table (including
                    // [download]/Reserve-pull actions) over character deploys. Each new
                    // location expands future deploy options + force generation.
                    boolean v67iAddsLocation = false;
                    String v67iReason = null;
                    try {
                        String v67iLower = v24ActionLower;
                        if (v67iLower.contains("from reserve deck") || v67iLower.contains("[download]")) {
                            String[] v67iLocationKeywords = new String[] {
                                "site", "battleground", "location", "system", "farm",
                                "cantina", "mos eisley", "tatooine", "endor", "hoth",
                                "dagobah", "naboo", "yavin", "bespin", "cloud city",
                                "mustafar", "malachor", "mapuzo", "jabiim", "coruscant",
                                "kashyyyk", "kessel", "kamino", "geonosis", "alderaan",
                                "docking bay", "spaceport", "city", "palace", "temple",
                                "safehouse", "corridor", "village", "outpost"
                            };
                            for (String kw : v67iLocationKeywords) {
                                if (v67iLower.contains(kw)) {
                                    v67iAddsLocation = true;
                                    v67iReason = "actionText contains location keyword '" + kw + "'";
                                    break;
                                }
                            }
                            if (!v67iAddsLocation) {
                                List<String> v67iCardIds2 = context.getCardIds();
                                String v67iCardIdStr = (v67iCardIds2 != null && i < v67iCardIds2.size())
                                    ? v67iCardIds2.get(i) : null;
                                if (v67iCardIdStr != null && !v67iCardIdStr.isEmpty() && gameState != null) {
                                    PhysicalCard v67iSrc =
                                        gameState.findCardById(Integer.parseInt(v67iCardIdStr));
                                    if (v67iSrc != null && v67iSrc.getBlueprint() != null) {
                                        String gt = v67iSrc.getBlueprint().getGameText();
                                        if (gt != null) {
                                            List<String> targets = com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle
                                                .parseSourceCardPullTargets(gt);
                                            for (String t : targets) {
                                                for (String kw : v67iLocationKeywords) {
                                                    if (t.contains(kw)) {
                                                        v67iAddsLocation = true;
                                                        v67iReason = "source card '" + v67iSrc.getTitle()
                                                            + "' game text targets location-like '" + t + "'";
                                                        break;
                                                    }
                                                }
                                                if (v67iAddsLocation) break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) { LOG.debug("V67i error: {}", e.getMessage()); }

                    if (v67iAddsLocation) {
                        // V67ai Tier 1-3 (Steve, 2026-05-07): When DeployEvaluator scores
                        // a card-action pull, classify by source-card category for the
                        // tiered location-deploy order: Objective → Effect → Interrupt → Hand.
                        int v67aiTier = 0;
                        String v67aiTierName = "unclassified";
                        if (card != null && card.getBlueprint() != null) {
                            CardCategory srcCat = card.getBlueprint().getCardCategory();
                            if (srcCat == CardCategory.OBJECTIVE) {
                                v67aiTier = 1; v67aiTierName = "OBJECTIVE";
                            } else if (srcCat == CardCategory.EFFECT) {
                                v67aiTier = 2; v67aiTierName = "EFFECT";
                            } else if (srcCat == CardCategory.INTERRUPT) {
                                v67aiTier = 3; v67aiTierName = "INTERRUPT";
                            } else if (srcCat == CardCategory.LOCATION) {
                                v67aiTier = 2; v67aiTierName = "LOCATION-EFFECT";
                            }
                        }
                        float v67aiBonus;
                        switch (v67aiTier) {
                            case 1: v67aiBonus = 2000.0f; break;
                            case 2: v67aiBonus = 1800.0f; break;
                            case 3: v67aiBonus = 1600.0f; break;
                            default: v67aiBonus = 1500.0f; break;
                        }
                        action.addReasoning(
                            String.format("V67ai LOCATION DEPLOY ORDER [Tier %d %s]: %s — Objective → Effect → Interrupt → Hand!",
                                v67aiTier, v67aiTierName, v67iReason), v67aiBonus);
                        LOG.warn("V67ai LOCATION TIER {} [{}]: '{}' → +{} ({})",
                            v67aiTier, v67aiTierName, actionText, (int) v67aiBonus, v67iReason);
                    }

                    // === V67m UNIVERSAL WEAPON-PULL PRIORITY ===
                    // Steve's rule: "There are other cards that pull weapons from reserve,
                    // after location pulls and character deploys, we should use those
                    // effects to deploy weapons from reserve with positive points."
                    //
                    // Score +200 — positive enough to fire over passing/idle, but well
                    // below character deploy peaks (+300-500) so chars deploy first.
                    // Mirrors V67l's dual-source detection (action text + game text fallback).
                    boolean v67mAddsWeapon = false;
                    String v67mReason = null;
                    try {
                        String v67mLower = v24ActionLower;
                        if (v67mLower.contains("from reserve deck") || v67mLower.contains("[download]")) {
                            String[] v67mWeaponKeywords = new String[] {
                                "weapon", "lightsaber", "saber", "blaster",
                                "rifle", "pistol", "cannon", "bowcaster",
                                "thermal detonator", "vibroblade", "vibro-",
                                "force pike", "electrostaff"
                            };
                            for (String kw : v67mWeaponKeywords) {
                                if (v67mLower.contains(kw)) {
                                    v67mAddsWeapon = true;
                                    v67mReason = "actionText contains weapon keyword '" + kw + "'";
                                    break;
                                }
                            }
                            // Fallback: source card game text
                            if (!v67mAddsWeapon) {
                                List<String> v67mCardIds = context.getCardIds();
                                String v67mCardIdStr = (v67mCardIds != null && i < v67mCardIds.size())
                                    ? v67mCardIds.get(i) : null;
                                if (v67mCardIdStr != null && !v67mCardIdStr.isEmpty() && gameState != null) {
                                    PhysicalCard v67mSrc =
                                        gameState.findCardById(Integer.parseInt(v67mCardIdStr));
                                    if (v67mSrc != null && v67mSrc.getBlueprint() != null) {
                                        String gt = v67mSrc.getBlueprint().getGameText();
                                        if (gt != null) {
                                            List<String> targets = com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle
                                                .parseSourceCardPullTargets(gt);
                                            for (String t : targets) {
                                                for (String kw : v67mWeaponKeywords) {
                                                    if (t.contains(kw)) {
                                                        v67mAddsWeapon = true;
                                                        v67mReason = "source card '" + v67mSrc.getTitle()
                                                            + "' game text targets weapon-like '" + t + "'";
                                                        break;
                                                    }
                                                }
                                                if (v67mAddsWeapon) break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) { LOG.debug("V67m error: {}", e.getMessage()); }

                    // Don't double-bonus location pulls (V67l already gave +1500)
                    if (v67mAddsWeapon && !v67iAddsLocation) {
                        // V67ar (Steve, 2026-05-08): UNIVERSAL ONE-WEAPON RULE — DeployEvaluator path.
                        // Mirrors V67aq's logic. Count UNARMED Rando characters; if zero
                        // unarmed (every char already armed), hard-block. No hardcoded names.
                        int v67arUnarmed = 0;
                        int v67arArmed = 0;
                        if (gameState != null && context.getPlayerId() != null) {
                            try {
                                for (PhysicalCard pc : gameState.getAllPermanentCards()) {
                                    if (pc == null || pc.getBlueprint() == null) continue;
                                    if (!context.getPlayerId().equals(pc.getOwner())) continue;
                                    com.gempukku.swccgo.common.Zone z = pc.getZone();
                                    if (z == null || !z.isInPlay()) continue;
                                    if (pc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                    boolean armed = false;
                                    java.util.List<PhysicalCard> atts = gameState.getAttachedCards(pc);
                                    if (atts != null) {
                                        for (PhysicalCard a : atts) {
                                            if (a != null && a.getBlueprint() != null
                                                    && a.getBlueprint().getCardCategory() == CardCategory.WEAPON) {
                                                armed = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (armed) v67arArmed++;
                                    else v67arUnarmed++;
                                }
                            } catch (Exception e) { /* ignore */ }
                        }
                        if (v67arUnarmed == 0 && v67arArmed > 0) {
                            action.addReasoning(String.format(
                                "V67ar UNIVERSAL BLOCK: every Rando character (%d) already armed — pulled weapon would stack a 2nd weapon (forbidden)!",
                                v67arArmed), -9999.0f);
                            LOG.warn("V67ar UNIVERSAL BLOCK (DeployEvaluator pull): '{}' — all {} chars armed",
                                actionText, v67arArmed);
                        } else if (v67arUnarmed == 0) {
                            action.addReasoning(
                                "V67ao ORDER GATE: weapon pull blocked — no Rando character on table to hold the weapon. Deploy a character first!",
                                -9999.0f);
                            LOG.warn("V67ao ORDER GATE (DeployEvaluator): weapon pull '{}' blocked (no chars on table)",
                                actionText);
                        } else {
                            action.addReasoning(String.format(
                                "V67am WEAPON PULL (universal, tier 1): %d unarmed character(s) — pull weapon from reserve! %s",
                                v67arUnarmed, v67mReason), 600.0f);
                            LOG.warn("V67am WEAPON PULL (DeployEvaluator): '{}' adds weapon ({}) → +600 ({} unarmed)",
                                actionText, v67mReason, v67arUnarmed);
                        }
                    }

                    // === V51: CLOUD CITY ARMY PRE-FLIP — Stack characters at CC sites ===
                    // For TDIGWATT/Dark Deal: before objective flips, build your Cloud City army.
                    // +500 for deploying characters to Cloud City sites pre-flip.
                    if (blueprint.getCardCategory() == CardCategory.CHARACTER && gameState != null) {
                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer ccAnalyzer =
                            context.getObjectiveAnalyzer();
                        if (ccAnalyzer != null && ccAnalyzer.isAnalyzed()
                            && ccAnalyzer.needsBespinSystemPresence() && !ccAnalyzer.isFlipped()) {
                            // TDIGWATT pre-flip — bonus for Cloud City character deploys
                            for (PhysicalCard ccLoc : gameState.getTopLocations()) {
                                if (ccLoc == null || ccLoc.getTitle() == null) continue;
                                String ccLocLower = ccLoc.getTitle().toLowerCase(Locale.ROOT);
                                if (!actionLower.contains(ccLocLower)) continue;
                                if (ccLocLower.contains("cloud city")) {
                                    action.addReasoning(String.format(
                                        "V51 CC ARMY: Deploy to %s pre-flip — build Cloud City army!",
                                        ccLoc.getTitle()), 500.0f);
                                    LOG.warn("V51 CC ARMY: {} to {} pre-flip — +500", card.getTitle(), ccLoc.getTitle());
                                }
                                break;
                            }
                        }
                    }

                    // === V51: OBJECTIVE-FIRST DEPLOYMENT — Bonus for objective locations pre-flip ===
                    // Before objective flips, deploying to objective-relevant locations gets a bonus.
                    // This applies to ALL objective decks.
                    if (blueprint.getCardCategory() == CardCategory.CHARACTER && gameState != null) {
                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer objFirstAnalyzer =
                            context.getObjectiveAnalyzer();
                        if (objFirstAnalyzer != null && objFirstAnalyzer.isAnalyzed()
                            && !objFirstAnalyzer.isFlipped()) {
                            for (PhysicalCard ofLoc : gameState.getTopLocations()) {
                                if (ofLoc == null || ofLoc.getTitle() == null) continue;
                                String ofLocLower = ofLoc.getTitle().toLowerCase(Locale.ROOT);
                                if (!actionLower.contains(ofLocLower)) continue;
                                if (objFirstAnalyzer.isObjectiveRelevantLocation(ofLoc.getTitle())) {
                                    action.addReasoning(String.format(
                                        "V51 OBJ FIRST: Deploy to %s — objective-relevant location pre-flip!",
                                        ofLoc.getTitle()), 300.0f);
                                    LOG.warn("V51 OBJ FIRST: {} to {} — objective location pre-flip +300",
                                        card.getTitle(), ofLoc.getTitle());
                                }
                                break;
                            }
                        }
                    }

                    // V67ao (removed): per Steve, no soft penalties for character deploys
                    // when locations are still in hand. V67ai location tier bonuses
                    // (+1400 to +2000) already outscore character deploys; Combined
                    // Evaluator picks locations first naturally. The hard-block order
                    // gates only apply where the action would actually FAIL (weapon/device
                    // pull with no character on table).

                    // === V67ak (Steve, 2026-05-07): KEY-CHARACTER DEPLOY PRIORITY ===
                    //
                    // Steve's rule: 'If the objective or epic event states a specific
                    // character or character type, Rando should favor deploying those
                    // characters first. Hunt Down V mentions Vader being deployed to flip
                    // the objective, so Vader must come out first. Universal mechanism —
                    // no hardcoded character lists per deck.'
                    //
                    // Implementation: ObjectiveAnalyzer.getStrategyCharacterTokens scans
                    // objective text + Epic Event game text + Effect game text on Rando's
                    // side, extracts capitalized persona-name tokens (filtered for generic
                    // words). Any character whose title contains a token gets +800.
                    //
                    // Skip if this character (or persona) is ALREADY on table — once Vader
                    // is out, additional Vader-named cards (which would be unique-blocked
                    // anyway) don't need the priority.
                    if (category == CardCategory.CHARACTER && card != null && card.getTitle() != null
                            && context.getObjectiveAnalyzer() != null
                            && context.getObjectiveAnalyzer().isAnalyzed()) {
                        try {
                            com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer akObj =
                                context.getObjectiveAnalyzer();
                            if (akObj.isStrategyKeyCharacter(game, context.getPlayerId(), card.getTitle())) {
                                // Check the matched token is NOT already on table as a card
                                // that satisfies the same key-character role.
                                String akCardTitleLower = card.getTitle().toLowerCase(Locale.ROOT);
                                boolean alreadyOnTable = false;
                                for (PhysicalCard exist : gameState.getAllPermanentCards()) {
                                    if (exist == null || exist.getBlueprint() == null) continue;
                                    if (!context.getPlayerId().equals(exist.getOwner())) continue;
                                    com.gempukku.swccgo.common.Zone ez = exist.getZone();
                                    if (ez == null || !ez.isInPlay()) continue;
                                    if (exist.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                    String et = exist.getTitle();
                                    if (et == null) continue;
                                    String etLower = et.toLowerCase(Locale.ROOT);
                                    // Persona-style match: any strategy token that appears in
                                    // BOTH the candidate card's title AND an existing-on-table
                                    // card's title means the role is already filled.
                                    for (String tok : akObj.getStrategyCharacterTokens(game, context.getPlayerId())) {
                                        if (akCardTitleLower.contains(tok) && etLower.contains(tok)) {
                                            alreadyOnTable = true;
                                            break;
                                        }
                                    }
                                    if (alreadyOnTable) break;
                                }
                                if (!alreadyOnTable) {
                                    action.addReasoning(String.format(
                                        "V67ak KEY CHARACTER: %s is named in objective/epic-event text — deploy first to enable flip!",
                                        card.getTitle()), 800.0f);
                                    LOG.warn("V67ak KEY CHARACTER: {} matches strategy token — +800 deploy priority",
                                        card.getTitle());
                                } else {
                                    LOG.info("V67ak KEY CHARACTER skip: {} role already filled by an on-table card",
                                        card.getTitle());
                                }
                            }
                        } catch (Exception e) { LOG.debug("V67ak error: {}", e.getMessage()); }
                    }

                    // === V67aj (Steve, 2026-05-07): SPREAD-AWARE CHARACTER DEPLOY DESTINATION ===
                    //
                    // Steve's rules:
                    //   1. Buddy system was over-firing: Rando stacked all characters on
                    //      one location all game.
                    //   2. Where Rando deploys is critical — must check objective for
                    //      flip-required locations (Endor Operations, Dark Deal, etc.).
                    //   3. ALWAYS favor battlegrounds (battles + drains).
                    //
                    // Tiered destination scoring layered on top of V51 OBJ FIRST:
                    //   Objective-required + BG, empty:        +500 (urgent — occupy now)
                    //   Objective-required + BG, stack 1-2:    +250 (reinforce)
                    //   Objective-required, stack 3+:          0    (sufficient — spread instead)
                    //   BG (not obj-required), empty:          +300 (open new front)
                    //   BG (not obj-required), stack 1-2:      +100 (mild reinforce)
                    //   BG (not obj-required), stack 3+:       -300 (V67aj OVER-STACK)
                    //   Non-BG: handled by V67ah (already in CardSelectionEvaluator)
                    //
                    // The OVER-STACK penalty fights the over-buddy clustering Steve called out.
                    // Combined with V51 OBJ FIRST (+300) and V29.7 (+80 for BG), an empty
                    // objective-required BG can score +880 across rules.
                    if (category == CardCategory.CHARACTER && card != null && gameState != null
                            && game != null && context.getPlayerId() != null) {
                        try {
                            String v67ajPid = context.getPlayerId();
                            for (PhysicalCard ajLoc : gameState.getTopLocations()) {
                                if (ajLoc == null || ajLoc.getTitle() == null) continue;
                                String ajLocLower = ajLoc.getTitle().toLowerCase(Locale.ROOT);
                                if (!actionLower.contains(ajLocLower)) continue;

                                // Count current friendly characters at this site
                                int v67ajStack = 0;
                                for (PhysicalCard c : gameState.getCardsAtLocation(ajLoc)) {
                                    if (c == null || c.getBlueprint() == null) continue;
                                    if (!v67ajPid.equals(c.getOwner())) continue;
                                    if (c.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                    v67ajStack++;
                                }

                                boolean v67ajIsBg;
                                try {
                                    v67ajIsBg = game.getModifiersQuerying().isBattleground(gameState, ajLoc, null);
                                } catch (Exception e) { v67ajIsBg = false; }

                                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer ajObj =
                                    context.getObjectiveAnalyzer();
                                boolean v67ajIsObjReq = ajObj != null && ajObj.isAnalyzed()
                                    && !ajObj.isFlipped()
                                    && ajObj.isObjectiveRelevantLocation(ajLoc.getTitle());

                                String v67ajLabel;
                                float v67ajBonus;
                                if (v67ajIsObjReq && v67ajIsBg) {
                                    if (v67ajStack == 0) {
                                        v67ajLabel = "OBJ-REQ + BG, EMPTY"; v67ajBonus = 500f;
                                    } else if (v67ajStack <= 2) {
                                        v67ajLabel = "OBJ-REQ + BG, REINFORCE"; v67ajBonus = 250f;
                                    } else {
                                        v67ajLabel = "OBJ-REQ + BG, SUFFICIENT"; v67ajBonus = 0f;
                                    }
                                } else if (v67ajIsObjReq) {
                                    if (v67ajStack == 0) {
                                        v67ajLabel = "OBJ-REQ, EMPTY"; v67ajBonus = 400f;
                                    } else if (v67ajStack <= 2) {
                                        v67ajLabel = "OBJ-REQ, REINFORCE"; v67ajBonus = 200f;
                                    } else {
                                        v67ajLabel = "OBJ-REQ, SUFFICIENT"; v67ajBonus = 0f;
                                    }
                                } else if (v67ajIsBg) {
                                    if (v67ajStack == 0) {
                                        v67ajLabel = "BG, OPEN-FRONT"; v67ajBonus = 300f;
                                    } else if (v67ajStack <= 2) {
                                        v67ajLabel = "BG, REINFORCE"; v67ajBonus = 100f;
                                    } else {
                                        v67ajLabel = "BG, OVER-STACK"; v67ajBonus = -300f;
                                    }
                                } else {
                                    // Non-BG: leave V67ah / V67ag in CardSelectionEvaluator to handle.
                                    v67ajLabel = null; v67ajBonus = 0f;
                                }

                                if (v67ajLabel != null && v67ajBonus != 0f) {
                                    action.addReasoning(String.format(
                                        "V67aj DEPLOY DEST [%s, stack=%d]: %s",
                                        v67ajLabel, v67ajStack, ajLoc.getTitle()), v67ajBonus);
                                    LOG.warn("V67aj [{}]: {} → {} stack={} → {}{}",
                                        v67ajLabel, card.getTitle(), ajLoc.getTitle(), v67ajStack,
                                        v67ajBonus > 0 ? "+" : "", (int) v67ajBonus);
                                }

                                // === V67al (Steve, 2026-05-07): POWER-STACK SPREAD PENALTY ===
                                // Steve's rule: 'In the last games he had a site with like
                                // 25-40 or so power, way more than enough to protect and spread.'
                                //
                                // Beyond ability/character-count stacking, RAW POWER stacking
                                // is the clearer signal: once a site has 20+ power of friendly
                                // characters, you have plenty for any battle there. Adding
                                // more to that site is sub-optimal — those characters could
                                // be opening new fronts elsewhere.
                                //
                                // Tiered power-stack penalty:
                                //    20-24 friendly power: -200 (already strong, prefer spread)
                                //    25-34 friendly power: -400 (heavily over-stacked)
                                //    35+   friendly power: -700 (catastrophically over-stacked)
                                //
                                // Skipped if location is objective-required (V67aj already
                                // tapers obj-req at stack 3+ to 0 — power doesn't override
                                // flip requirement).
                                try {
                                    if (!v67ajIsObjReq) {
                                        float v67alFriendlyPower = 0f;
                                        for (PhysicalCard pc : gameState.getCardsAtLocation(ajLoc)) {
                                            if (pc == null || pc.getBlueprint() == null) continue;
                                            if (!v67ajPid.equals(pc.getOwner())) continue;
                                            if (pc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                            if (pc.getBlueprint().hasPowerAttribute()) {
                                                Float p = pc.getBlueprint().getPower();
                                                if (p != null) v67alFriendlyPower += p;
                                            }
                                        }
                                        float v67alPenalty = 0f;
                                        String v67alLabel = null;
                                        if (v67alFriendlyPower >= 35f) {
                                            v67alPenalty = -700f; v67alLabel = "POWER-STACK CATASTROPHIC";
                                        } else if (v67alFriendlyPower >= 25f) {
                                            v67alPenalty = -400f; v67alLabel = "POWER-STACK HEAVY";
                                        } else if (v67alFriendlyPower >= 20f) {
                                            v67alPenalty = -200f; v67alLabel = "POWER-STACK MILD";
                                        }
                                        if (v67alLabel != null) {
                                            action.addReasoning(String.format(
                                                "V67al %s: %s already has %.0f friendly power — spread to threaten elsewhere!",
                                                v67alLabel, ajLoc.getTitle(), v67alFriendlyPower), v67alPenalty);
                                            LOG.warn("V67al {}: site={} friendlyPower={} → {}",
                                                v67alLabel, ajLoc.getTitle(), (int) v67alFriendlyPower, (int) v67alPenalty);
                                        }
                                    }
                                } catch (Exception e) { /* ignore */ }
                                break;
                            }
                        } catch (Exception e) {
                            LOG.debug("V67aj DEPLOY DEST: error: {}", e.getMessage());
                        }
                    }

                    // === V22.7: CLOUD CITY OCCUPATION GUARD ===
                    // Cloud City Occupation self-cancels if we don't occupy Bespin system.
                    // Don't waste the deploy — block it until we actually occupy Bespin.
                    // Also check Dark Deal (V) which has similar Bespin requirements.
                    String cardTitleLower = card.getTitle() != null ? card.getTitle().toLowerCase(Locale.ROOT) : "";
                    if (cardTitleLower.contains("cloud city occupation") || cardTitleLower.contains("dark deal")) {
                        boolean weOccupyBespin = false;
                        try {
                            String pid = context.getPlayerId();
                            String opponentId = gameState.getOpponent(pid);
                            for (PhysicalCard loc : gameState.getLocationsInOrder()) {
                                if (loc != null && loc.getTitle() != null &&
                                    loc.getTitle().toLowerCase(Locale.ROOT).contains("bespin") &&
                                    loc.getBlueprint() != null && loc.getBlueprint().getCardSubtype() != null &&
                                    loc.getBlueprint().getCardSubtype() == com.gempukku.swccgo.common.CardSubtype.SYSTEM) {
                                    float ourPower = context.getGame().getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, loc, pid, false, false);
                                    float theirPower = opponentId != null ?
                                        context.getGame().getModifiersQuerying().getTotalPowerAtLocation(
                                            gameState, loc, opponentId, false, false) : 0;
                                    // "Occupy" = we have presence and opponent does NOT
                                    weOccupyBespin = (ourPower > 0 && theirPower == 0);
                                    LOG.info("V22.7 BESPIN CHECK: our power={}, their power={}, occupy={}",
                                        ourPower, theirPower, weOccupyBespin);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            LOG.debug("V22.7: Could not check Bespin occupation: {}", e.getMessage());
                        }
                        if (!weOccupyBespin) {
                            action.addReasoning("V22.7 BLOCKED: " + card.getTitle() +
                                " will SELF-CANCEL — we don't occupy Bespin system!", -800.0f);
                            LOG.warn("🚫 V22.7: BLOCKING {} — we don't occupy Bespin, it will self-cancel!",
                                card.getTitle());
                            actions.add(action);
                            continue;
                        } else {
                            // V24: TDIGWATT ENGINE BOOST — Dark Deal and CC Occupation are the core damage engine
                            // When we occupy Bespin, deploying these is TOP PRIORITY
                            boolean effectAlreadyOnTable = false;
                            try {
                                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle effectOracle = context.getDeckOracle();
                                if (effectOracle != null) {
                                    effectAlreadyOnTable = effectOracle.isCardInPlay(card.getTitle());
                                }
                            } catch (Exception e) {
                                LOG.debug("V24: Error checking if effect is on table: {}", e.getMessage());
                            }
                            if (!effectAlreadyOnTable) {
                                action.addReasoning("V24 TDIGWATT ENGINE: Deploy " + card.getTitle() +
                                    " NOW — enables objective damage engine!", 300.0f);
                                LOG.warn("V24 TDIGWATT ENGINE: {} gets +300 — CRITICAL engine piece, deploy ASAP!", card.getTitle());
                            } else {
                                action.addReasoning("V22.7: We occupy Bespin — safe to deploy " + card.getTitle(), 50.0f);
                            }
                        }
                    }

                    // === V33: ONE WEAPON PER CHARACTER (HARD BLOCK) ===
                    // A character should only ever have one weapon. If the target character
                    // already has ANY weapon attached, hard-block this deploy (-9999).
                    if (category == CardCategory.WEAPON && gameState != null) {
                        try {
                            String v33PlayerId = context.getPlayerId();
                            for (PhysicalCard tableCard : gameState.getAllPermanentCards()) {
                                if (tableCard == null || !v33PlayerId.equals(tableCard.getOwner())) continue;
                                com.gempukku.swccgo.common.Zone v33Zone = tableCard.getZone();
                                if (v33Zone == null || !v33Zone.isInPlay()) continue;
                                if (tableCard.getBlueprint() == null || tableCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                String v33CharTitle = tableCard.getTitle() != null ? tableCard.getTitle().toLowerCase(Locale.ROOT) : "";
                                if (v33CharTitle.isEmpty() || !actionLower.contains(v33CharTitle)) continue;

                                java.util.List<PhysicalCard> v33Attachments = gameState.getAttachedCards(tableCard);
                                if (v33Attachments != null) {
                                    for (PhysicalCard att : v33Attachments) {
                                        if (att != null && att.getBlueprint() != null
                                            && att.getBlueprint().getCardCategory() == CardCategory.WEAPON) {
                                            action.addReasoning(String.format(
                                                "V33 ONE WEAPON: %s already has a weapon — BLOCKED!",
                                                tableCard.getTitle()), -9999.0f);
                                            LOG.warn("V33 ONE WEAPON: {} on {} BLOCKED — character already armed!",
                                                card.getTitle(), tableCard.getTitle());
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        } catch (Exception e) {
                            LOG.debug("V33 ONE WEAPON: Error: {}", e.getMessage());
                        }
                    }

                    // === V67aq (Steve, 2026-05-08): UNIVERSAL ONE-WEAPON RULE ===
                    //
                    // Replaces the entire V29.11/V29.9/V67ad/V67ap stack of hardcoded
                    // character-name detection. Steve's rule, full stop:
                    //   "No second weapon should deploy on ANY character. Period."
                    //
                    // Universal logic, no hardcoded names, no faction filters, no persona
                    // matching for the BLOCK side:
                    //   1. Iterate every Rando character in play.
                    //   2. Count how many are unarmed (no WEAPON attached).
                    //   3. If at least one unarmed Rando character exists → allow the
                    //      weapon deploy and give +300 (someone good can take it).
                    //   4. If ZERO unarmed characters AND at least one armed character →
                    //      hard-block (-9999): every character would be a 2nd-weapon
                    //      stack or the weapon would orphan.
                    //   5. If zero characters at all → V67ao gate elsewhere blocks.
                    //
                    // CardSelectionEvaluator handles which specific character to attach
                    // to (target-pick layer, V67an handles persona-matching for swaps).
                    if (category == CardCategory.WEAPON && gameState != null) {
                        try {
                            String wepPlayerId = context.getPlayerId();
                            int v67aqUnarmed = 0;
                            int v67aqArmed = 0;
                            for (PhysicalCard tc : gameState.getAllPermanentCards()) {
                                if (tc == null || !wepPlayerId.equals(tc.getOwner())) continue;
                                if (tc.getBlueprint() == null
                                        || tc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                com.gempukku.swccgo.common.Zone z = tc.getZone();
                                if (z == null || !z.isInPlay()) continue;
                                boolean armed = false;
                                java.util.List<PhysicalCard> atts = gameState.getAttachedCards(tc);
                                if (atts != null) {
                                    for (PhysicalCard a : atts) {
                                        if (a != null && a.getBlueprint() != null
                                                && a.getBlueprint().getCardCategory() == CardCategory.WEAPON) {
                                            armed = true;
                                            break;
                                        }
                                    }
                                }
                                if (armed) v67aqArmed++; else v67aqUnarmed++;
                            }

                            if (v67aqUnarmed > 0) {
                                // At least one unarmed character — weapon deploy is useful.
                                action.addReasoning(String.format(
                                    "V67aq WEAPON DEPLOY: %d unarmed character(s) on table — deploy weapon to arm them!",
                                    v67aqUnarmed), 300.0f);
                                LOG.info("V67aq WEAPON DEPLOY: {} → unarmed={}, armed={} → +300",
                                    card.getTitle(), v67aqUnarmed, v67aqArmed);
                            } else if (v67aqArmed > 0) {
                                // All characters armed — second weapon would stack (illegal/wasteful).
                                action.addReasoning(String.format(
                                    "V67aq UNIVERSAL BLOCK: every Rando character (%d) is already armed — second weapon on ANY character is forbidden!",
                                    v67aqArmed), -9999.0f);
                                LOG.warn("V67aq UNIVERSAL BLOCK: {} — all {} chars armed, no 2nd weapon allowed (HARD BLOCK)",
                                    card.getTitle(), v67aqArmed);
                            } else {
                                // No characters at all — V67ao ORDER GATE handles this elsewhere.
                                LOG.info("V67aq WEAPON DEPLOY: {} — no chars on table, V67ao order gate applies",
                                    card.getTitle());
                            }
                        } catch (Exception e) {
                            LOG.debug("V67aq error: {}", e.getMessage());
                        }
                    }

                    // === V33: NAMED WEAPON PRIORITY ===
                    if (category == CardCategory.WEAPON && gameState != null) {
                        try {
                            boolean isNamedWeapon = cardTitleLower.contains("vader") || cardTitleLower.contains("mara")
                                || cardTitleLower.contains("maul") || cardTitleLower.contains("palpatine")
                                || cardTitleLower.contains("emperor") || cardTitleLower.contains("luke")
                                || cardTitleLower.contains("obi-wan") || cardTitleLower.contains("ahsoka")
                                || cardTitleLower.contains("sabine") || cardTitleLower.contains("inquisitor")
                                || cardTitleLower.contains("tarkin") || cardTitleLower.contains("piett");

                            if (isNamedWeapon) {
                                action.addReasoning("V33 NAMED WEAPON: Character-specific weapon — deploy priority!", 200.0f);
                                LOG.warn("V33 NAMED WEAPON: {} is character-specific — boosted (+200)", card.getTitle());
                            } else {
                                String v33wPlayerId = context.getPlayerId();
                                String targetCharName = null;
                                for (PhysicalCard tableCard : gameState.getAllPermanentCards()) {
                                    if (tableCard == null || !v33wPlayerId.equals(tableCard.getOwner())) continue;
                                    com.gempukku.swccgo.common.Zone v33wZone = tableCard.getZone();
                                    if (v33wZone == null || !v33wZone.isInPlay()) continue;
                                    if (tableCard.getBlueprint() == null || tableCard.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                    String v33wCharTitle = tableCard.getTitle() != null ? tableCard.getTitle().toLowerCase(Locale.ROOT) : "";
                                    if (!v33wCharTitle.isEmpty() && actionLower.contains(v33wCharTitle)) {
                                        targetCharName = v33wCharTitle;
                                        break;
                                    }
                                }

                                if (targetCharName != null) {
                                    java.util.List<PhysicalCard> v33Hand = gameState.getHand(v33wPlayerId);
                                    if (v33Hand != null) {
                                        for (PhysicalCard hc : v33Hand) {
                                            if (hc == null || hc == card || hc.getBlueprint() == null) continue;
                                            if (hc.getBlueprint().getCardCategory() != CardCategory.WEAPON) continue;
                                            String hcTitle = hc.getTitle() != null ? hc.getTitle().toLowerCase(Locale.ROOT) : "";
                                            if (hcTitle.contains(targetCharName.split(",")[0].split(" ")[0])) {
                                                action.addReasoning(String.format(
                                                    "V33 NAMED WEAPON WAIT: %s has named weapon %s in hand — save the slot!",
                                                    targetCharName, hc.getTitle()), -400.0f);
                                                LOG.warn("V33 NAMED WEAPON WAIT: Generic {} blocked on {} — named {} in hand!",
                                                    card.getTitle(), targetCharName, hc.getTitle());
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LOG.debug("V33 NAMED WEAPON: Error: {}", e.getMessage());
                        }
                    }

                    // Characters with high ability are valuable
                    if (category == CardCategory.CHARACTER && abilityVal >= 4) {
                        action.addReasoning("High-ability character", 25.0f);
                    }

                    // === V24.1C: GHERANT DEPLOY BONUS ===
                    // Commander Gherant pulls an Executor site when deployed.
                    // That's a FREE location = force generation. Treat him almost like deploying a location.
                    if (category == CardCategory.CHARACTER && cardTitleLower.contains("gherant")) {
                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer gherantObjAnalyzer =
                            context.getObjectiveAnalyzer();
                        if (gherantObjAnalyzer != null && gherantObjAnalyzer.isAnalyzed()
                            && gherantObjAnalyzer.needsBespinSystemPresence()) {
                            action.addReasoning("V24.1 GHERANT: Deploys an Executor site — free location + force generation!", 150.0f);
                            LOG.warn("V24.1 GHERANT: {} gets +150 — pulls Executor site on deploy!", card.getTitle());
                        }
                    }

                    // === V24.2C: LANDO/LOBOT DEPLOY PRIORITY (TDIGWATT) ===
                    // Lando and Lobot are cheap to deploy and critical for flipping TDIGWATT.
                    // Lando's movement ability enables 3-site drain strategy.
                    if (category == CardCategory.CHARACTER) {
                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer landoObjAnalyzer =
                            context.getObjectiveAnalyzer();
                        if (landoObjAnalyzer != null && landoObjAnalyzer.isAnalyzed()
                            && landoObjAnalyzer.needsBespinSystemPresence()) {
                            if (cardTitleLower.contains("lando")) {
                                action.addReasoning("V24.2 LANDO: Key piece — enables 3-site drain with movement ability!", 200.0f);
                                LOG.warn("V24.2 LANDO: {} gets +200 — critical for 3-site drain strategy!", card.getTitle());
                            } else if (cardTitleLower.contains("lobot")) {
                                action.addReasoning("V24.2 LOBOT: Cheap deploy — helps flip TDIGWATT objective!", 150.0f);
                                LOG.warn("V24.2 LOBOT: {} gets +150 — cheap, helps flip!", card.getTitle());
                            }
                        }
                    }

                    // === V47: LANDO/LOBOT SOLO BLOCK (RESERVE DEPLOY) ===
                    // "Deploy Lando from Reserve Deck" comes from Dining Room (a LOCATION card).
                    // The resolved card is Dining Room, not Lando. Check action text for "lando"/"lobot".
                    // NEVER deploy them alone to CC — they get clobbered every time.
                    {
                        String actionTextLower = actionText != null ? actionText.toLowerCase(java.util.Locale.ROOT) : "";
                        boolean isLandoDeploy = cardTitleLower.contains("lando") || actionTextLower.contains("lando");
                        boolean isLobotDeploy = cardTitleLower.contains("lobot") || actionTextLower.contains("lobot");

                        if (isLandoDeploy || isLobotDeploy) {
                            com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer landoObjAnalyzer =
                                context.getObjectiveAnalyzer();
                            if (landoObjAnalyzer != null && landoObjAnalyzer.isAnalyzed()
                                && landoObjAnalyzer.needsBespinSystemPresence()) {
                                boolean haveCharAtCCSite = false;
                                GameState landoGs = context.getGameState();
                                SwccgGame landoGame = context.getGame();
                                if (landoGs != null && landoGame != null) {
                                    try {
                                        String landoPlayerId = context.getPlayerId();
                                        for (PhysicalCard loc : landoGs.getTopLocations()) {
                                            if (loc == null || loc.getTitle() == null) continue;
                                            String locT = loc.getTitle().toLowerCase(java.util.Locale.ROOT);
                                            if (!locT.contains("cloud city")) continue;
                                            java.util.List<PhysicalCard> cardsHere = landoGs.getCardsAtLocation(loc);
                                            if (cardsHere == null) continue;
                                            for (PhysicalCard c : cardsHere) {
                                                if (c != null && landoPlayerId.equals(c.getOwner())
                                                    && c.getBlueprint() != null
                                                    && c.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                                    haveCharAtCCSite = true;
                                                    break;
                                                }
                                            }
                                            if (haveCharAtCCSite) break;
                                        }
                                    } catch (Exception e) {
                                        LOG.debug("V47 LANDO SOLO CHECK: Error: {}", e.getMessage());
                                    }
                                }

                                String whoName = isLandoDeploy ? "Lando" : "Lobot";
                                if (haveCharAtCCSite) {
                                    action.addReasoning("V29.2 " + whoName + ": Key piece + backup present — safe to deploy!", 200.0f);
                                    LOG.warn("V29.2 {}: +200 — has backup at CC site!", whoName);
                                } else {
                                    action.addReasoning("V47 " + whoName + " SOLO BLOCK: No friendlies at CC — dies alone!", -9999.0f);
                                    LOG.warn("V47 {} SOLO BLOCK: No friendly chars at CC — blocking reserve deploy!", whoName);
                                }
                            }
                        }
                    }

                    // === V31: PRE-FLIP vs POST-FLIP OBJECTIVE DEPLOYMENT STRATEGY ===
                    // PRE-FLIP: Spread characters across objective locations to meet flip condition.
                    //   - Solo deploys to objective locations are OK pre-flip — we need presence fast.
                    //   - Bonus for deploying to unoccupied objective locations.
                    // POST-FLIP: Consolidate to fewer locations to hold.
                    //   - Only need 2 locations to prevent flip-back (1 CC site + Bespin system).
                    //   - Bonus for reinforcing the 2 strongest held objective locations.
                    if (category == CardCategory.CHARACTER && card != null) {
                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer flipObjAnalyzer =
                            context.getObjectiveAnalyzer();
                        if (flipObjAnalyzer != null && flipObjAnalyzer.isAnalyzed()
                            && gameState != null && game != null) {
                            try {
                                String flipPlayerId = context.getPlayerId();
                                java.util.Set<String> objLocFragments = flipObjAnalyzer.getFlipConditionLocationFragments();

                                if (!flipObjAnalyzer.isFlipped()) {
                                    // === PRE-FLIP: Spread to meet flip condition ===
                                    int occupiedObjLocs = 0;
                                    int unoccupiedObjLocs = 0;
                                    java.util.List<String> unoccupiedLocNames = new java.util.ArrayList<>();
                                    for (PhysicalCard loc : gameState.getTopLocations()) {
                                        if (loc == null || loc.getTitle() == null) continue;
                                        String locLower = loc.getTitle().toLowerCase(Locale.ROOT);
                                        boolean isObjLoc = false;
                                        for (String frag : objLocFragments) {
                                            if (locLower.contains(frag.toLowerCase(Locale.ROOT))) {
                                                isObjLoc = true;
                                                break;
                                            }
                                        }
                                        if (!isObjLoc) continue;
                                        float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                            gameState, loc, flipPlayerId, false, false);
                                        if (ourPower > 0) {
                                            occupiedObjLocs++;
                                        } else {
                                            unoccupiedObjLocs++;
                                            unoccupiedLocNames.add(loc.getTitle());
                                        }
                                    }

                                    boolean deploysToUnoccupiedObjLoc = false;
                                    for (String unoccName : unoccupiedLocNames) {
                                        if (actionLower.contains(unoccName.toLowerCase(Locale.ROOT))) {
                                            deploysToUnoccupiedObjLoc = true;
                                            break;
                                        }
                                    }

                                    if (unoccupiedObjLocs > 0 && deploysToUnoccupiedObjLoc) {
                                        // V36: DEFEND YOUR TERRITORY — objective sites left empty get
                                        // occupied by opponent Jedi who drain 2-3 per turn. Malachor sites
                                        // must have presence BEFORE the opponent gets there.
                                        // On turns 1-3, this is the #1 priority for Inquisitors.
                                        float defendBonus = 250.0f;
                                        int turnNum = context.getTurnNumber();
                                        boolean isHuntDown36 = flipObjAnalyzer.isHuntDownV();
                                        String deployCardLower36 = card.getTitle() != null
                                            ? card.getTitle().toLowerCase(Locale.ROOT) : "";
                                        boolean isInquisitor36 = isInquisitor(deployCardLower36);

                                        if (isHuntDown36 && turnNum <= 3) {
                                            // Early game Hunt Down — CRITICAL to defend Malachor
                                            defendBonus = 800.0f; // V36: Overrides Hunt Block -2000
                                            if (isInquisitor36) defendBonus = 1000.0f; // Inquisitors are ideal defenders
                                            LOG.warn("V36 DEFEND MALACHOR: {} to empty obj site EARLY (turn {}) — must defend! (+{})",
                                                card.getTitle(), turnNum, (int)defendBonus);
                                        } else if (isHuntDown36) {
                                            // Later turns — still important but less urgent
                                            defendBonus = 500.0f;
                                            LOG.warn("V36 DEFEND TERRITORY: {} to empty obj site (turn {}) — +{}",
                                                card.getTitle(), turnNum, (int)defendBonus);
                                        }

                                        action.addReasoning(String.format(
                                            "V36 DEFEND TERRITORY: Deploy to unoccupied obj location! (%d/%d occupied%s)",
                                            occupiedObjLocs, occupiedObjLocs + unoccupiedObjLocs,
                                            isHuntDown36 && turnNum <= 3 ? " — EARLY DEFENSE CRITICAL" : ""), defendBonus);
                                        LOG.warn("V36 PRE-FLIP: {} to unoccupied obj loc (+{}) — {}/{} occupied",
                                            card.getTitle(), (int)defendBonus, occupiedObjLocs, occupiedObjLocs + unoccupiedObjLocs);
                                    } else if (unoccupiedObjLocs > 0) {
                                        action.addReasoning(String.format(
                                            "V31 PRE-FLIP: %d obj locations still unoccupied — spread out!",
                                            unoccupiedObjLocs), -50.0f);
                                    }
                                } else {
                                    // === POST-FLIP: Consolidate to fewer locations ===
                                    java.util.Map<String, Float> objLocPower = new java.util.LinkedHashMap<>();
                                    java.util.List<PhysicalCard> occupiedObjLocCards = new java.util.ArrayList<>();
                                    for (PhysicalCard loc : gameState.getTopLocations()) {
                                        if (loc == null || loc.getTitle() == null) continue;
                                        String locLower = loc.getTitle().toLowerCase(Locale.ROOT);
                                        boolean isObjLoc = false;
                                        for (String frag : objLocFragments) {
                                            if (locLower.contains(frag.toLowerCase(Locale.ROOT))) {
                                                isObjLoc = true;
                                                break;
                                            }
                                        }
                                        if (!isObjLoc) continue;
                                        float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                            gameState, loc, flipPlayerId, false, false);
                                        if (ourPower > 0) {
                                            occupiedObjLocCards.add(loc);
                                            objLocPower.put(loc.getTitle(), ourPower);
                                        }
                                    }

                                    java.util.Set<String> holdLocations = new java.util.HashSet<>();
                                    for (int holdIdx = 0; holdIdx < 2 && !objLocPower.isEmpty(); holdIdx++) {
                                        String bestLoc = null;
                                        float bestPwr = -1;
                                        for (java.util.Map.Entry<String, Float> e : objLocPower.entrySet()) {
                                            if (e.getValue() > bestPwr) {
                                                bestPwr = e.getValue();
                                                bestLoc = e.getKey();
                                            }
                                        }
                                        if (bestLoc != null) {
                                            holdLocations.add(bestLoc);
                                            objLocPower.remove(bestLoc);
                                        }
                                    }

                                    boolean deploysToHoldLoc = false;
                                    for (String holdLoc : holdLocations) {
                                        if (actionLower.contains(holdLoc.toLowerCase(Locale.ROOT))) {
                                            deploysToHoldLoc = true;
                                            break;
                                        }
                                    }

                                    if (deploysToHoldLoc) {
                                        action.addReasoning("V31 POST-FLIP: Reinforce key hold location!", 200.0f);
                                        LOG.warn("V31 POST-FLIP: {} reinforcing hold location (+200)", card.getTitle());
                                    } else {
                                        boolean deploysToAnyObjLoc = false;
                                        for (String frag : objLocFragments) {
                                            if (actionLower.contains(frag.toLowerCase(Locale.ROOT))) {
                                                deploysToAnyObjLoc = true;
                                                break;
                                            }
                                        }
                                        if (deploysToAnyObjLoc && occupiedObjLocCards.size() > 2) {
                                            action.addReasoning("V31 POST-FLIP: Don't spread to 3+ obj locs — consolidate!", -100.0f);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                LOG.debug("V31 PRE/POST-FLIP: Error: {}", e.getMessage());
                            }
                        }
                    }

                    // === V32: ABILITY >= 4 DEPLOYMENT RULE ===
                    // SWCCG requires total ability >= 4 at a site to draw battle destiny.
                    // NEVER leave total friendly ability < 4 at a site after deploying.
                    if (category == CardCategory.CHARACTER && card != null && card.getBlueprint() != null
                        && gameState != null && game != null) {
                        try {
                            float cardAbility = 0;
                            if (card.getBlueprint().hasAbilityAttribute()) {
                                Float ab = card.getBlueprint().getAbility();
                                cardAbility = ab != null ? ab : 0;
                            }

                            String v32PlayerId = context.getPlayerId();
                            for (PhysicalCard loc : gameState.getTopLocations()) {
                                if (loc == null || loc.getTitle() == null) continue;
                                if (loc.getBlueprint() == null || loc.getBlueprint().getCardSubtype() == null) continue;
                                if (loc.getBlueprint().getCardSubtype() != com.gempukku.swccgo.common.CardSubtype.SITE) continue;

                                String siteTitle = loc.getTitle().toLowerCase(Locale.ROOT);
                                if (!actionLower.contains(siteTitle)) continue;

                                float currentAbilityAtSite = 0;
                                int friendlyCharCount = 0;
                                for (PhysicalCard c : gameState.getCardsAtLocation(loc)) {
                                    if (c == null || !v32PlayerId.equals(c.getOwner())) continue;
                                    if (c.getBlueprint() == null) continue;
                                    if (c.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                    friendlyCharCount++;
                                    if (c.getBlueprint().hasAbilityAttribute()) {
                                        Float cAb = c.getBlueprint().getAbility();
                                        currentAbilityAtSite += (cAb != null ? cAb : 0);
                                    }
                                }

                                float totalAfterDeploy = currentAbilityAtSite + cardAbility;

                                if (totalAfterDeploy >= 4.0f) {
                                    if (friendlyCharCount > 0 && currentAbilityAtSite < 4.0f) {
                                        action.addReasoning(String.format(
                                            "V32 ABILITY FIX: Deploy brings ability from %.0f to %.0f (>= 4) at %s!",
                                            currentAbilityAtSite, totalAfterDeploy, loc.getTitle()), 150.0f);
                                        LOG.warn("V32 ABILITY FIX: {} fixes deficit at {} (was {}, now {})",
                                            card.getTitle(), loc.getTitle(), currentAbilityAtSite, totalAfterDeploy);
                                    }
                                } else if (friendlyCharCount == 0) {
                                    boolean canFollowUp = false;
                                    java.util.List<PhysicalCard> handCards = gameState.getHand(v32PlayerId);
                                    if (handCards != null) {
                                        for (PhysicalCard hc : handCards) {
                                            if (hc == null || hc == card) continue;
                                            if (hc.getBlueprint() == null) continue;
                                            if (hc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                            float hcAbility = 0;
                                            if (hc.getBlueprint().hasAbilityAttribute()) {
                                                Float hcAb = hc.getBlueprint().getAbility();
                                                hcAbility = hcAb != null ? hcAb : 0;
                                            }
                                            if (cardAbility + hcAbility >= 4.0f) {
                                                canFollowUp = true;
                                                break;
                                            }
                                        }
                                    }

                                    if (!canFollowUp) {
                                        action.addReasoning(String.format(
                                            "V32 ABILITY RISK: Solo deploy with ability %.0f < 4 at %s — NO battle destiny!",
                                            cardAbility, loc.getTitle()), -200.0f);
                                        LOG.warn("V32 ABILITY RISK: {} (ability {}) solo at {} — no follow-up",
                                            card.getTitle(), cardAbility, loc.getTitle());
                                    } else {
                                        action.addReasoning(String.format(
                                            "V32 ABILITY CAUTION: Solo ability %.0f < 4 at %s but follow-up in hand",
                                            cardAbility, loc.getTitle()), -30.0f);
                                    }
                                } else {
                                    action.addReasoning(String.format(
                                        "V32 ABILITY WARNING: Total ability %.0f still < 4 at %s after deploy!",
                                        totalAfterDeploy, loc.getTitle()), -100.0f);
                                    LOG.warn("V32 ABILITY WARNING: {} to {} — total ability {} still < 4!",
                                        card.getTitle(), loc.getTitle(), totalAfterDeploy);
                                }
                                break;
                            }
                        } catch (Exception e) {
                            LOG.debug("V32 ABILITY CHECK: Error: {}", e.getMessage());
                        }
                    }

                    // === V33: ABILITY 7 BUDDY SYSTEM ===
                    // Encourage stacking ability at sites to reach 7+.
                    if (category == CardCategory.CHARACTER && card != null && card.getBlueprint() != null
                        && gameState != null && game != null) {
                        try {
                            float v33CardAbility = 0;
                            if (card.getBlueprint().hasAbilityAttribute()) {
                                Float v33Ab = card.getBlueprint().getAbility();
                                v33CardAbility = v33Ab != null ? v33Ab : 0;
                            }

                            String v33PlayerId = context.getPlayerId();
                            for (PhysicalCard loc : gameState.getTopLocations()) {
                                if (loc == null || loc.getTitle() == null) continue;
                                if (loc.getBlueprint() == null || loc.getBlueprint().getCardSubtype() == null) continue;
                                if (loc.getBlueprint().getCardSubtype() != com.gempukku.swccgo.common.CardSubtype.SITE) continue;

                                String v33SiteTitle = loc.getTitle().toLowerCase(Locale.ROOT);
                                if (!actionLower.contains(v33SiteTitle)) continue;

                                // V67ab (Steve, 2026-05-03): Only stack ability at BATTLEGROUNDS.
                                // V33 BUDDY FIX/BONUS was firing for non-battleground sites where
                                // battles can't happen — wasting characters on places they can't
                                // contribute. Symptom: Mira deployed to Coruscant: The Works
                                // (non-BG) to "buddy" with Sidious — but Sidius doesn't need
                                // protection there (no battles), and Mira got trapped.
                                // The buddy ability >= 7 threshold exists for BATTLE destiny;
                                // non-BG sites don't have battles, so don't reward stacking there.
                                boolean v67abIsBg = false;
                                try {
                                    v67abIsBg = game.getModifiersQuerying().isBattleground(gameState, loc, null);
                                } catch (Exception e) { /* ignore */ }
                                if (!v67abIsBg) {
                                    // V67ag (Steve, 2026-05-04): NON-BG STACKING PENALTY.
                                    // V67ab skipped the buddy BONUS at non-BG, but didn't penalize
                                    // STACKING. Steve's report: 'Rando deployed Sidious to The Works
                                    // (good — drains for 1) but then loaded extra characters there
                                    // (useless — they can't battle anywhere they're stacked).'
                                    // Rule: if non-BG already has any of our characters, additional
                                    // characters wasted there.
                                    boolean v67agHasFriendly = false;
                                    String v67agExistingTitle = null;
                                    try {
                                        for (PhysicalCard c : gameState.getCardsAtLocation(loc)) {
                                            if (c == null || c.getBlueprint() == null) continue;
                                            if (!v33PlayerId.equals(c.getOwner())) continue;
                                            if (c.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                            v67agHasFriendly = true;
                                            v67agExistingTitle = c.getTitle();
                                            break;
                                        }
                                    } catch (Exception e) { /* ignore */ }
                                    if (v67agHasFriendly) {
                                        action.addReasoning(String.format(
                                            "V67ag NON-BG STACK PENALTY: %s already has %s — additional character at non-BG can't battle, deploys to a battleground instead!",
                                            loc.getTitle(), v67agExistingTitle), -300.0f);
                                        LOG.warn("V67ag NON-BG STACK PENALTY: {} already has friendly {} at non-BG {} — penalize additional deploy (-300)",
                                            card.getTitle(), v67agExistingTitle, loc.getTitle());
                                    } else {
                                        LOG.info("V67ab BUDDY SKIP: {} is non-battleground — V33 buddy bonus not applied (no battles here)",
                                            loc.getTitle());
                                    }
                                    break;  // Don't apply V33 to non-BG sites
                                }

                                // Count current friendly ability at this site
                                float v33CurrentAbility = 0;
                                for (PhysicalCard c : gameState.getCardsAtLocation(loc)) {
                                    if (c == null || !v33PlayerId.equals(c.getOwner())) continue;
                                    if (c.getBlueprint() == null) continue;
                                    if (c.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                    if (c.getBlueprint().hasAbilityAttribute()) {
                                        Float cAb = c.getBlueprint().getAbility();
                                        v33CurrentAbility += (cAb != null ? cAb : 0);
                                    }
                                }

                                float v33TotalAfter = v33CurrentAbility + v33CardAbility;

                                if (v33CurrentAbility < ChosenOneConfig.ABILITY_BUDDY_THRESHOLD) {
                                    if (v33TotalAfter >= ChosenOneConfig.ABILITY_BUDDY_THRESHOLD) {
                                        action.addReasoning(String.format(
                                            "V33 BUDDY FIX: Deploy brings ability from %.0f to %.0f (>= %d) at %s!",
                                            v33CurrentAbility, v33TotalAfter, ChosenOneConfig.ABILITY_BUDDY_THRESHOLD,
                                            loc.getTitle()), 150.0f);
                                        LOG.warn("V33 BUDDY FIX: {} (ability {}) at {} — brings total from {} to {} (>= {})",
                                            card.getTitle(), v33CardAbility, loc.getTitle(),
                                            v33CurrentAbility, v33TotalAfter, ChosenOneConfig.ABILITY_BUDDY_THRESHOLD);
                                    } else if (v33CurrentAbility > 0) {
                                        action.addReasoning(String.format(
                                            "V33 BUDDY BONUS: Reinforcing ability at %s (%.0f → %.0f, target %d)",
                                            loc.getTitle(), v33CurrentAbility, v33TotalAfter,
                                            ChosenOneConfig.ABILITY_BUDDY_THRESHOLD), 100.0f);
                                        LOG.warn("V33 BUDDY BONUS: {} reinforcing {} — ability {} → {}",
                                            card.getTitle(), loc.getTitle(), v33CurrentAbility, v33TotalAfter);
                                    }
                                }
                                break;
                            }
                        } catch (Exception e) {
                            LOG.debug("V33 BUDDY SYSTEM: Error: {}", e.getMessage());
                        }
                    }

                    // === V51: DRAIN 2+ SITE STACKING + BUDDY SYSTEM ===
                    // Drain 2+ sites are THE battleground. Stack characters there.
                    // Buddy system: ability >= 4 enables battle destiny, ability >= 7 is ideal.
                    if (category == CardCategory.CHARACTER && card != null && card.getBlueprint() != null
                        && gameState != null && game != null) {
                        try {
                            String v51Pid = context.getPlayerId();
                            String v51ActionLower = actionText.toLowerCase(Locale.ROOT);

                            for (PhysicalCard v51Loc : gameState.getTopLocations()) {
                                if (v51Loc == null || v51Loc.getTitle() == null) continue;
                                String v51LocLower = v51Loc.getTitle().toLowerCase(Locale.ROOT);
                                if (!v51ActionLower.contains(v51LocLower)) continue;

                                // Count friendly characters and ability at the target location
                                int v51FriendlyCount = 0;
                                float v51FriendlyAbility = 0;
                                for (PhysicalCard v51c : gameState.getCardsAtLocation(v51Loc)) {
                                    if (v51c == null || !v51Pid.equals(v51c.getOwner())) continue;
                                    if (v51c.getBlueprint() == null || v51c.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                    v51FriendlyCount++;
                                    if (v51c.getBlueprint().hasAbilityAttribute()) {
                                        Float v51ab = v51c.getBlueprint().getAbility();
                                        v51FriendlyAbility += (v51ab != null ? v51ab : 0);
                                    }
                                }

                                // Check if this is a drain 2+ site (our drain potential)
                                float v51OurDrain = 0;
                                try {
                                    v51OurDrain = game.getModifiersQuerying().getForceDrainAmount(
                                        gameState, v51Loc, v51Pid);
                                } catch (Exception e) { /* default 0 */ }

                                // V51: Reinforcement bonus scales with drain value
                                if (v51FriendlyCount > 0 && v51OurDrain >= 2.0f) {
                                    // Drain 2+ site with friendlies — THIS is the battleground
                                    action.addReasoning(String.format(
                                        "V51 FORTIFY BATTLEGROUND: Joining %d friendlies at %s (our drain %.0f) — this is THE fight!",
                                        v51FriendlyCount, v51Loc.getTitle(), v51OurDrain), 500.0f);
                                    LOG.warn("V51 FORTIFY BATTLEGROUND: {} joins {} friendlies at {} (drain {}) — +500",
                                        card.getTitle(), v51FriendlyCount, v51Loc.getTitle(), (int)v51OurDrain);
                                } else if (v51FriendlyCount == 0 && v51OurDrain >= 2.0f) {
                                    // Drain 2+ site, establishing first presence
                                    action.addReasoning(String.format(
                                        "V51 ESTABLISH BATTLEGROUND: First deploy to %s (our drain %.0f) — start the army!",
                                        v51Loc.getTitle(), v51OurDrain), 400.0f);
                                    LOG.warn("V51 ESTABLISH BATTLEGROUND: {} first to {} (drain {}) — +400",
                                        card.getTitle(), v51Loc.getTitle(), (int)v51OurDrain);
                                } else if (v51FriendlyCount > 0) {
                                    // Non-drain-2 site but has friendlies — still good to reinforce
                                    action.addReasoning(String.format(
                                        "V51 REINFORCE: Joining %d friendlies at %s!",
                                        v51FriendlyCount, v51Loc.getTitle()), 300.0f);
                                    LOG.warn("V51 REINFORCE: {} joins {} friendlies at {} — +300",
                                        card.getTitle(), v51FriendlyCount, v51Loc.getTitle());
                                }

                                // V51: Buddy system — ability thresholds with higher bonuses
                                float v51CardAbility = 0;
                                if (card.getBlueprint().hasAbilityAttribute()) {
                                    Float v51ab2 = card.getBlueprint().getAbility();
                                    v51CardAbility = (v51ab2 != null ? v51ab2 : 0);
                                }
                                float totalAbilityAfter = v51FriendlyAbility + v51CardAbility;

                                if (v51FriendlyAbility < 4.0f && totalAbilityAfter >= 4.0f && v51FriendlyCount > 0) {
                                    // V51: Deploy enables battle destiny at this site!
                                    action.addReasoning(String.format(
                                        "V51 BUDDY DESTINY: Ability %.0f → %.0f (>= 4) at %s — battle destiny ENABLED!",
                                        v51FriendlyAbility, totalAbilityAfter, v51Loc.getTitle()), 400.0f);
                                    LOG.warn("V51 BUDDY DESTINY: {} enables battle destiny at {} (ability {} → {}) — +400",
                                        card.getTitle(), v51Loc.getTitle(), (int)v51FriendlyAbility, (int)totalAbilityAfter);
                                } else if (totalAbilityAfter >= 7.0f && v51FriendlyCount > 0) {
                                    // V51: Full buddy system — ideal ability threshold
                                    action.addReasoning(String.format(
                                        "V51 BUDDY FULL: Ability total %.0f >= 7 at %s — full buddy system!",
                                        totalAbilityAfter, v51Loc.getTitle()), 500.0f);
                                    LOG.warn("V51 BUDDY FULL: {} — total ability {} at {} — +500",
                                        card.getTitle(), (int)totalAbilityAfter, v51Loc.getTitle());
                                } else if (totalAbilityAfter >= 4.0f && v51FriendlyCount > 0) {
                                    // V51: Ability already >= 4, reinforcing toward 7
                                    action.addReasoning(String.format(
                                        "V51 BUDDY REINFORCE: Ability %.0f → %.0f at %s — building toward 7!",
                                        v51FriendlyAbility, totalAbilityAfter, v51Loc.getTitle()), 200.0f);
                                    LOG.warn("V51 BUDDY REINFORCE: {} ability {} → {} at {} — +200",
                                        card.getTitle(), (int)v51FriendlyAbility, (int)totalAbilityAfter, v51Loc.getTitle());
                                }

                                // V51: Armed character bonus at drain 2+ sites
                                if (v51OurDrain >= 2.0f || (v51FriendlyCount > 0)) {
                                    String v51CardLower = card.getTitle() != null ? card.getTitle().toLowerCase(Locale.ROOT) : "";
                                    if (v51CardLower.contains("lightsaber") || v51CardLower.contains("blaster")
                                        || v51CardLower.contains("with lightsaber") || v51CardLower.contains("with blaster")) {
                                        action.addReasoning(String.format(
                                            "V51 ARMED: %s brings a weapon to %s — ready for battle!",
                                            card.getTitle(), v51Loc.getTitle()), 150.0f);
                                        LOG.warn("V51 ARMED: {} to {} — weapon bonus +150", card.getTitle(), v51Loc.getTitle());
                                    }
                                }

                                break; // Only check first matching location
                            }
                        } catch (Exception e) {
                            LOG.debug("V51 DRAIN STACKING: Error: {}", e.getMessage());
                        }
                    }

                    // === V30: UNIVERSAL MATCHING PILOT + STARSHIP DEPLOY RULE ===
                    // If a pilot character and its matching starship are BOTH in hand,
                    // deploy them together NOW with maximum priority (+1000).
                    // Also: deploy them to the system mentioned in the objective (+1000).
                    // If only the pilot is in hand and matching ship is in reserve with
                    // AMSD on table, soft-prefer AMSD (-500) but allow manual fallback.
                    // If matching ship is already in play, boost deploying pilot to it (+300).
                    if (category == CardCategory.CHARACTER && card != null && card.getBlueprint() != null) {
                        Filter matchingShipFilter = card.getBlueprint().getMatchingStarshipFilter();
                        if (matchingShipFilter != null && gameState != null && game != null) {
                            try {
                                boolean matchingShipInHand = false;
                                boolean matchingShipInReserve = false;
                                boolean matchingShipInPlay = false;
                                String matchingShipName = null;

                                java.util.List<PhysicalCard> handCards = gameState.getHand(context.getPlayerId());
                                if (handCards != null) {
                                    for (PhysicalCard handCard : handCards) {
                                        if (handCard != null && matchingShipFilter.accepts(game.getGameState(),
                                                game.getModifiersQuerying(), handCard)) {
                                            matchingShipInHand = true;
                                            matchingShipName = handCard.getTitle();
                                            break;
                                        }
                                    }
                                }

                                if (!matchingShipInHand) {
                                    for (PhysicalCard inPlayCard : gameState.getAllPermanentCards()) {
                                        if (inPlayCard != null && context.getPlayerId().equals(inPlayCard.getOwner())
                                                && matchingShipFilter.accepts(game.getGameState(),
                                                    game.getModifiersQuerying(), inPlayCard)) {
                                            matchingShipInPlay = true;
                                            matchingShipName = inPlayCard.getTitle();
                                            break;
                                        }
                                    }
                                }

                                if (!matchingShipInHand && !matchingShipInPlay) {
                                    java.util.List<PhysicalCard> reserveCards = gameState.getReserveDeck(context.getPlayerId());
                                    if (reserveCards != null) {
                                        for (PhysicalCard resCard : reserveCards) {
                                            if (resCard != null && matchingShipFilter.accepts(game.getGameState(),
                                                    game.getModifiersQuerying(), resCard)) {
                                                matchingShipInReserve = true;
                                                matchingShipName = resCard.getTitle();
                                                break;
                                            }
                                        }
                                    }
                                }

                                if (matchingShipInHand) {
                                    action.addReasoning(String.format(
                                        "V30 MATCHING COMBO: %s + %s both in hand — deploy together NOW!",
                                        card.getTitle(), matchingShipName), 1000.0f);
                                    LOG.warn("V30 MATCHING COMBO: {} + {} BOTH IN HAND — maximum priority (+1000)!",
                                        card.getTitle(), matchingShipName);

                                    com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer matchObjAnalyzer =
                                        context.getObjectiveAnalyzer();
                                    if (matchObjAnalyzer != null && matchObjAnalyzer.isAnalyzed()) {
                                        java.util.Set<String> objLocations = matchObjAnalyzer.getFlipConditionLocationFragments();
                                        if (objLocations != null && actionText != null) {
                                            String actionLwr = actionText.toLowerCase(java.util.Locale.ROOT);
                                            for (String objLoc : objLocations) {
                                                if (actionLwr.contains(objLoc.toLowerCase(java.util.Locale.ROOT))) {
                                                    action.addReasoning(String.format(
                                                        "V30 OBJECTIVE SYSTEM: Deploy to %s — matches objective location!",
                                                        objLoc), 1000.0f);
                                                    LOG.warn("V30 OBJECTIVE SYSTEM: {} deploying to objective location '{}' — +1000!",
                                                        card.getTitle(), objLoc);
                                                    break;
                                                }
                                            }
                                        }
                                    }

                                } else if (matchingShipInPlay) {
                                    action.addReasoning(String.format(
                                        "V30 MATCHING SHIP IN PLAY: %s is deployed — get %s aboard!",
                                        matchingShipName, card.getTitle()), 300.0f);
                                    LOG.warn("V30 MATCHING SHIP: {} in play — deploy {} as pilot (+300)!",
                                        matchingShipName, card.getTitle());

                                } else if (matchingShipInReserve) {
                                    com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle matchOracle = context.getDeckOracle();
                                    boolean amsdInPlay = false;
                                    if (matchOracle != null && matchOracle.isAnalyzed()) {
                                        amsdInPlay = matchOracle.isCardInPlay("Alert My Star Destroyer")
                                            || matchOracle.isCardInPlay("Alert My Star Destroyer!");
                                    }
                                    if (amsdInPlay) {
                                        action.addReasoning(String.format(
                                            "V30 AMSD AVAILABLE: %s in reserve + AMSD on table — prefer AMSD pull, manual OK as fallback",
                                            matchingShipName), -500.0f);
                                        LOG.warn("V30 AMSD: {} in reserve — soft penalty (-500), prefer AMSD but not hard-blocked",
                                            matchingShipName);
                                    }
                                }
                            } catch (Exception e) {
                                LOG.debug("V30 MATCHING PILOT CHECK: Error: {}", e.getMessage());
                            }
                        }
                    }

                    // === V30: UNIVERSAL MATCHING STARSHIP + PILOT DEPLOY RULE (reverse) ===
                    if ((category == CardCategory.STARSHIP || category == CardCategory.VEHICLE)
                            && card != null && card.getBlueprint() != null && gameState != null && game != null) {
                        try {
                            boolean matchingPilotInHand = false;
                            String matchingPilotName = null;
                            java.util.List<PhysicalCard> shipHandCards = gameState.getHand(context.getPlayerId());
                            if (shipHandCards != null) {
                                for (PhysicalCard handCard : shipHandCards) {
                                    if (handCard != null && handCard.getBlueprint() != null
                                            && handCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                        Filter pilotMatchFilter = handCard.getBlueprint().getMatchingStarshipFilter();
                                        if (pilotMatchFilter != null && pilotMatchFilter.accepts(game.getGameState(),
                                                game.getModifiersQuerying(), card)) {
                                            matchingPilotInHand = true;
                                            matchingPilotName = handCard.getTitle();
                                            break;
                                        }
                                    }
                                }
                            }

                            if (matchingPilotInHand) {
                                action.addReasoning(String.format(
                                    "V30 MATCHING COMBO: %s + pilot %s both in hand — deploy together NOW!",
                                    card.getTitle(), matchingPilotName), 1000.0f);
                                LOG.warn("V30 MATCHING COMBO: {} + {} BOTH IN HAND — maximum priority (+1000)!",
                                    card.getTitle(), matchingPilotName);

                                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer shipObjAnalyzer =
                                    context.getObjectiveAnalyzer();
                                if (shipObjAnalyzer != null && shipObjAnalyzer.isAnalyzed()) {
                                    java.util.Set<String> objLocations = shipObjAnalyzer.getFlipConditionLocationFragments();
                                    if (objLocations != null && actionText != null) {
                                        String actionLwr = actionText.toLowerCase(java.util.Locale.ROOT);
                                        for (String objLoc : objLocations) {
                                            if (actionLwr.contains(objLoc.toLowerCase(java.util.Locale.ROOT))) {
                                                action.addReasoning(String.format(
                                                    "V30 OBJECTIVE SYSTEM: Deploy to %s — matches objective!",
                                                    objLoc), 1000.0f);
                                                LOG.warn("V30 OBJECTIVE SYSTEM: {} to objective location '{}' — +1000!",
                                                    card.getTitle(), objLoc);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LOG.debug("V30 MATCHING SHIP CHECK: Error: {}", e.getMessage());
                        }
                    }

                    // === V35.6: SHIP ABILITY CHECK — NEED >= 4 ABILITY AT SYSTEM ===
                    if ((category == CardCategory.STARSHIP || category == CardCategory.VEHICLE)
                        && card != null && card.getBlueprint() != null && gameState != null) {
                        try {
                            float shipAbility = 0;
                            if (card.getBlueprint().hasAbilityAttribute()) {
                                Float sa = card.getBlueprint().getAbility();
                                shipAbility = sa != null ? sa : 0;
                            }
                            String v36Pid = context.getPlayerId();
                            boolean matchingPilotAffordable = false;
                            float matchingPilotAbility = 0;
                            String matchingPilotTitle = null;
                            int shipCost = card.getBlueprint().getDeployCost() != null
                                ? card.getBlueprint().getDeployCost().intValue() : 0;
                            Filter matchPilotFilter = card.getBlueprint().getMatchingPilotFilter();
                            java.util.List<PhysicalCard> v36Hand = gameState.getHand(v36Pid);
                            if (v36Hand != null && matchPilotFilter != null) {
                                for (PhysicalCard hc : v36Hand) {
                                    if (hc == null || hc.getBlueprint() == null) continue;
                                    if (hc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                    if (matchPilotFilter.accepts(game.getGameState(), game.getModifiersQuerying(), hc)) {
                                        matchingPilotTitle = hc.getTitle();
                                        Float mpAb = hc.getBlueprint().getAbility();
                                        matchingPilotAbility = mpAb != null ? mpAb : 0;
                                        int pilotCost = hc.getBlueprint().getDeployCost() != null
                                            ? hc.getBlueprint().getDeployCost().intValue() : 0;
                                        int totalCost = shipCost + Math.max(0, pilotCost / 2);
                                        if (context.getForcePileSize() >= totalCost) matchingPilotAffordable = true;
                                        break;
                                    }
                                }
                            }
                            float totalAbilityWithPilot = shipAbility + (matchingPilotAffordable ? matchingPilotAbility : 0);
                            if (matchingPilotAffordable && matchingPilotTitle != null) {
                                action.addReasoning(String.format(
                                    "V35.6 NAMED PILOT: %s + %s (ability %.0f+%.0f=%.0f) — deploy together!",
                                    card.getTitle(), matchingPilotTitle, shipAbility, matchingPilotAbility, totalAbilityWithPilot),
                                    300.0f);
                            }
                            // V35.7: ALL ships with ability < 4 need a pilot
                            if (shipAbility < 4.0f) {
                                boolean anyPilotHelps = false;
                                if (v36Hand != null) {
                                    for (PhysicalCard hc : v36Hand) {
                                        if (hc == null || hc.getBlueprint() == null) continue;
                                        if (hc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                        if (!hc.getBlueprint().hasAbilityAttribute()) continue;
                                        Float hcAb = hc.getBlueprint().getAbility();
                                        if (hcAb != null && (shipAbility + hcAb) >= 4.0f) {
                                            anyPilotHelps = true;
                                            break;
                                        }
                                    }
                                }
                                if (!anyPilotHelps) {
                                    action.addReasoning(String.format(
                                        "V35.7 SHIP ABILITY: %s ability %.0f — BLOCKED!",
                                        card.getTitle(), shipAbility), -800.0f);
                                } else if (!matchingPilotAffordable) {
                                    action.addReasoning(String.format(
                                        "V35.7 SHIP: %s needs pilot but can't afford both!",
                                        card.getTitle()), -400.0f);
                                } else {
                                    action.addReasoning(String.format(
                                        "V35.7 SHIP: %s needs pilot aboard for ability 4",
                                        card.getTitle()), -100.0f);
                                }
                            }
                        } catch (Exception e) {
                            LOG.debug("V35.6 SHIP ABILITY: Error: {}", e.getMessage());
                        }
                    }

                    // === V35.5: DON'T DEPLOY WEAK STARSHIPS AGAINST STRONG OPPONENTS ===
                    if ((category == CardCategory.STARSHIP || category == CardCategory.VEHICLE)
                        && gameState != null && game != null) {
                        try {
                            String v35ShipPid = context.getPlayerId();
                            String v35ShipOid = gameState.getOpponent(v35ShipPid);
                            String v35ShipActionLower = actionText.toLowerCase(Locale.ROOT);
                            float ourShipPower = 0;
                            if (card.getBlueprint().hasPowerAttribute()) {
                                Float sp = card.getBlueprint().getPower();
                                ourShipPower = sp != null ? sp : 0;
                            }
                            for (PhysicalCard sysLoc : gameState.getLocationsInOrder()) {
                                if (sysLoc == null || sysLoc.getTitle() == null) continue;
                                if (sysLoc.getBlueprint() == null || sysLoc.getBlueprint().getCardSubtype() == null) continue;
                                if (sysLoc.getBlueprint().getCardSubtype() != com.gempukku.swccgo.common.CardSubtype.SYSTEM
                                    && sysLoc.getBlueprint().getCardSubtype() != com.gempukku.swccgo.common.CardSubtype.SECTOR) continue;
                                String sysTitle = sysLoc.getTitle().toLowerCase(Locale.ROOT);
                                if (sysTitle.isEmpty() || !v35ShipActionLower.contains(sysTitle)) continue;
                                float oppShipPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, sysLoc, v35ShipOid, false, false);
                                if (oppShipPower > 0 && oppShipPower > ourShipPower * 1.5f) {
                                    float shipPenalty = -600.0f;
                                    if (oppShipPower > ourShipPower * 3) shipPenalty = -1000.0f;
                                    action.addReasoning(String.format(
                                        "V35.5 SHIP SUICIDE: %s (power %.0f) vs opponent (power %.0f) — OUTGUNNED!",
                                        card.getTitle(), ourShipPower, oppShipPower), shipPenalty);
                                    LOG.warn("V35.5 SHIP SUICIDE: {} power {} vs opponent {} — BLOCKED ({})",
                                        card.getTitle(), (int)ourShipPower, (int)oppShipPower, (int)shipPenalty);
                                }
                                break;
                            }
                        } catch (Exception e) {
                            LOG.debug("V35.5 SHIP CHECK: Error: {}", e.getMessage());
                        }
                    }

                    // === V51: UNDERCOVER SPY DEPLOY — HIGHEST PRIORITY AT DRAIN 2+ SITES ===
                    // Spies cost almost nothing and cripple the opponent's entire drain investment.
                    // Opponent spends 15-20 force deploying 3-4 characters to a drain 2 site.
                    // One spy for 1-2 force cuts that drain in half. Best ROI in the game.
                    // +1000 to deploy spy where opponent threatens drain >= 2.
                    // -300 if opponent has NO locations threatening drain >= 2 (spy is wasted).
                    if (actionLower.contains("undercover spy") || actionLower.contains("as a spy")
                        || actionLower.contains("undercover")) {
                        try {
                            String spyPlayerId = context.getPlayerId();
                            String spyOppId = game.getOpponent(spyPlayerId);

                            // First: scan ALL opponent locations for drain >= 2 threats
                            boolean opponentHasDrain2Plus = false;
                            for (PhysicalCard scanLoc : gameState.getTopLocations()) {
                                if (scanLoc == null || scanLoc.getTitle() == null) continue;
                                float scanOppPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, scanLoc, spyOppId, false, false);
                                if (scanOppPower > 0) {
                                    float scanDrain = 1.0f;
                                    try {
                                        scanDrain = game.getModifiersQuerying().getForceDrainAmount(
                                            gameState, scanLoc, spyOppId);
                                    } catch (Exception e) { /* default 1 */ }
                                    if (scanDrain >= 2.0f) {
                                        opponentHasDrain2Plus = true;
                                        break;
                                    }
                                }
                            }

                            // Now check which location this spy deploys to
                            boolean deploysToHighDrainSite = false;
                            boolean deploysToOpponentLoc = false;
                            boolean deploysToFriendlyLoc = false;
                            for (PhysicalCard loc : gameState.getTopLocations()) {
                                if (loc == null || loc.getTitle() == null) continue;
                                String locTitle = loc.getTitle().toLowerCase(java.util.Locale.ROOT);
                                if (!actionLower.contains(locTitle)) continue;

                                float oppPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, loc, spyOppId, false, false);
                                float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, loc, spyPlayerId, false, false);

                                if (oppPower > 0) {
                                    deploysToOpponentLoc = true;
                                    float spyDrain = 1.0f;
                                    try {
                                        spyDrain = game.getModifiersQuerying().getForceDrainAmount(
                                            gameState, loc, spyOppId);
                                    } catch (Exception e) { /* default 1 */ }
                                    if (spyDrain >= 2.0f) {
                                        deploysToHighDrainSite = true;
                                        // V51: SPY AT DRAIN 2+ = BEST PLAY IN THE GAME
                                        action.addReasoning(String.format(
                                            "V51 SPY CRIPPLE: Spy at %s cuts drain from %.0f — opponent's army is WASTED!",
                                            loc.getTitle(), spyDrain), 1000.0f);
                                        LOG.warn("V51 SPY CRIPPLE: {} to {} (drain {}) — +1000! Best ROI in the game!",
                                            card.getTitle(), loc.getTitle(), (int)spyDrain);
                                    }
                                } else if (ourPower > 0) {
                                    deploysToFriendlyLoc = true;
                                }
                            }

                            if (deploysToOpponentLoc && !deploysToHighDrainSite) {
                                // Opponent location but drain < 2 — still useful
                                action.addReasoning("V43 SPY TO ENEMY: Deploy spy to opponent location — blocks their drain!", 200.0f);
                                LOG.warn("V43 SPY: {} to opponent location — +200", card.getTitle());
                            } else if (deploysToFriendlyLoc) {
                                action.addReasoning("V43 SPY WASTED: Spy at friendly location does NOTHING — send to opponent!", -500.0f);
                                LOG.warn("V43 SPY WASTED: {} to friendly location — -500", card.getTitle());
                            }

                            // V51: If opponent has NO drain 2+ sites, spy is low priority
                            if (!opponentHasDrain2Plus && !deploysToOpponentLoc) {
                                action.addReasoning("V51 SPY NO TARGET: Opponent has no drain 2+ sites — deploy a fighter instead!", -300.0f);
                                LOG.warn("V51 SPY NO TARGET: {} — no drain 2+ sites to cripple — -300", card.getTitle());
                            }
                        } catch (Exception e) { /* ignore */ }
                    }

                    // === V24.3A: DR. EVAZAN WEAPON COMBO DEPLOY PRIORITY ===
                    // Dr. Evazan converts weapon "hits" into immediate "lost" — devastating combo.
                    // Boost Evazan deploy when weapon characters are in play, and vice versa.
                    if (category == CardCategory.CHARACTER) {
                        com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle comboOracle = context.getDeckOracle();
                        if (comboOracle != null) {
                            boolean isEvazan = cardTitleLower.contains("evazan");
                            boolean isWeaponChar = (cardTitleLower.contains("maul") && cardTitleLower.contains("lightsaber"))
                                || (cardTitleLower.contains("vader") && cardTitleLower.contains("lightsaber"))
                                || (cardTitleLower.contains("mara") && cardTitleLower.contains("lightsaber"))
                                || (cardTitleLower.contains("jade") && cardTitleLower.contains("lightsaber"))
                                || (cardTitleLower.contains("aurra") && cardTitleLower.contains("blaster"))
                                || (cardTitleLower.contains("sing") && cardTitleLower.contains("blaster"));

                            if (isEvazan) {
                                // Check if ANY weapon character is already in play
                                boolean weaponPartnerInPlay = comboOracle.isCardInPlay("Maul With Lightsaber")
                                    || comboOracle.isCardInPlay("Vader With Lightsaber")
                                    || comboOracle.isCardInPlay("Mara Jade With Lightsaber")
                                    || comboOracle.isCardInPlay("Jade With Lightsaber")
                                    || comboOracle.isCardInPlay("Aurra Sing With Blaster")
                                    || comboOracle.isCardInPlay("Sing With Blaster");
                                if (weaponPartnerInPlay) {
                                    action.addReasoning("V24.3 EVAZAN COMBO: Weapon character on table — deploy Evazan for kill combo!", 150.0f);
                                    LOG.warn("V24.3 EVAZAN: Weapon partner in play — +150 deploy priority!");
                                }
                            } else if (isWeaponChar) {
                                // Check if Evazan is already in play
                                if (comboOracle.isCardInPlay("Evazan")) {
                                    action.addReasoning("V24.3 EVAZAN COMBO: Dr. Evazan on table — deploy weapon character for kill combo!", 100.0f);
                                    LOG.warn("V24.3 WEAPON CHAR: Evazan in play — +100 deploy priority for {}!", card.getTitle());
                                }
                            }
                        }
                    }

                    // Starships and vehicles for board presence
                    if (category == CardCategory.STARSHIP || category == CardCategory.VEHICLE) {
                        action.addReasoning("Starship/Vehicle deployment", 15.0f);

                        // === V24.6A+V24.9: EXECUTOR DEPLOY PRIORITY ===
                        // Executor is THE key ship for TDIGWATT — it force drains at Bespin,
                        // enables Dark Deal + CC Occupation. If it's in hand, deploy it NOW.
                        // V24.9: MUST come out turn 1 or 2 at the latest. If AMSD didn't pull it
                        // from reserve, deploy it manually from hand — no excuses.
                        if (cardTitleLower.contains("executor") || cardTitleLower.contains("flagship")) {
                            com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer execObjAnalyzer =
                                context.getObjectiveAnalyzer();
                            if (execObjAnalyzer != null && execObjAnalyzer.isAnalyzed()
                                && execObjAnalyzer.needsBespinSystemPresence()) {

                                // V24.10: BESPIN MUST BE ON TABLE BEFORE EXECUTOR
                                // Executor needs to deploy TO Bespin system. If Bespin isn't on the
                                // table yet, deploying Executor sends it to Tatooine or another system
                                // where it's completely useless for TDIGWATT. HARD BLOCK until Bespin is out.
                                boolean bespinOnTable = false;
                                try {
                                    for (com.gempukku.swccgo.game.PhysicalCard loc : gameState.getLocationsInOrder()) {
                                        if (loc != null && loc.getTitle() != null &&
                                            loc.getTitle().toLowerCase(java.util.Locale.ROOT).contains("bespin") &&
                                            loc.getBlueprint() != null && loc.getBlueprint().getCardSubtype() != null &&
                                            loc.getBlueprint().getCardSubtype() == com.gempukku.swccgo.common.CardSubtype.SYSTEM) {
                                            bespinOnTable = true;
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    LOG.debug("V24.10 Executor gate: Error checking Bespin: {}", e.getMessage());
                                }

                                if (!bespinOnTable) {
                                    // HARD BLOCK: Executor without Bespin is useless — deploy Bespin first!
                                    action.addReasoning("V24.10 EXECUTOR BLOCKED: Bespin system NOT on table — deploy Bespin FIRST!", -9999.0f);
                                    LOG.warn("V24.10 EXECUTOR BLOCKED: {} in hand but Bespin not on table — CANNOT deploy to wrong system!", card.getTitle());
                                } else {
                                    int execTurn = context.getTurnNumber();
                                    if (execTurn <= 2) {
                                        // Turns 1-2: MAXIMUM priority — Executor MUST come out now
                                        action.addReasoning("V24.9 EXECUTOR CRITICAL: Bespin on table — MUST deploy NOW!", 800.0f);
                                        LOG.warn("V24.9 EXECUTOR CRITICAL: {} on turn {} + Bespin on table — MAXIMUM priority (+800)!", card.getTitle(), execTurn);
                                    } else {
                                        action.addReasoning("V24.6 EXECUTOR: Key ship for TDIGWATT — deploy to Bespin!", 800.0f);
                                        LOG.warn("V24.6 EXECUTOR: {} in hand + Bespin on table — deploy priority (+800)!", card.getTitle());
                                    }
                                }
                            }
                        }

                        // V22.5: BESPIN SYSTEM SHIP PRIORITY
                        // For objectives that reference Bespin/Cloud City (like TDIGWATT),
                        // having a ship at Bespin system is critical for enabling Dark Deal
                        // and Cloud City Occupation. Prioritize ship deployment if no ship there yet.
                        com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer shipObjAnalyzer =
                            context.getObjectiveAnalyzer();
                        if (shipObjAnalyzer != null && shipObjAnalyzer.isAnalyzed() && shipObjAnalyzer.needsBespinSystemPresence()) {
                            boolean hasBespinPresence = false;
                            try {
                                for (PhysicalCard loc : gameState.getLocationsInOrder()) {
                                    if (loc != null && loc.getTitle() != null &&
                                        loc.getTitle().toLowerCase(Locale.ROOT).contains("bespin") &&
                                        loc.getBlueprint() != null && loc.getBlueprint().getCardSubtype() != null &&
                                        loc.getBlueprint().getCardSubtype() == com.gempukku.swccgo.common.CardSubtype.SYSTEM) {
                                        String pid = context.getPlayerId();
                                        float ourSpacePower = context.getGame().getModifiersQuerying().getTotalPowerAtLocation(
                                            gameState, loc, pid, false, false);
                                        if (ourSpacePower > 0) hasBespinPresence = true;
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                LOG.debug("Could not check Bespin presence: {}", e.getMessage());
                            }
                            if (!hasBespinPresence) {
                                // V23: Check if opponent has presence at Bespin — contestation is even more urgent
                                boolean opponentAtBespin = false;
                                try {
                                    for (PhysicalCard loc : gameState.getLocationsInOrder()) {
                                        if (loc != null && loc.getTitle() != null &&
                                            loc.getTitle().toLowerCase(Locale.ROOT).contains("bespin") &&
                                            loc.getBlueprint() != null && loc.getBlueprint().getCardSubtype() != null &&
                                            loc.getBlueprint().getCardSubtype() == com.gempukku.swccgo.common.CardSubtype.SYSTEM) {
                                            String oppId = context.getOpponentId();
                                            if (oppId != null) {
                                                float oppPower = context.getGame().getModifiersQuerying().getTotalPowerAtLocation(
                                                    gameState, loc, oppId, false, false);
                                                opponentAtBespin = (oppPower > 0);
                                            }
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    LOG.debug("V23: Could not check opponent Bespin presence: {}", e.getMessage());
                                }

                                if (opponentAtBespin) {
                                    // Opponent controls Bespin — URGENT contestation needed
                                    action.addReasoning("V23 BESPIN CONTEST: Opponent controls Bespin — deploy ship to contest IMMEDIATELY!", 300.0f);
                                    LOG.warn("V23 BESPIN CONTEST: {} gets +300 — opponent has presence at Bespin!", card.getTitle());
                                } else {
                                    // No opponent but we still need ship presence for objective
                                    action.addReasoning("V23 BESPIN CRITICAL: Deploy ship to enable Dark Deal + CC Occupation!", 250.0f);
                                    LOG.warn("V23 BESPIN SHIP: {} gets +250 — no ship at Bespin system yet!", card.getTitle());
                                }
                            }
                        }
                    }

                    // === PILOT BONUS ===
                    if (AiCardHelper.isPilot(card)) {
                        action.addReasoning("Pilot character", 10.0f);
                    }

                    // === V47: EXECUTOR PILOT GROUND BLOCK ===
                    // Piett and Gherant should NEVER deploy to ground solo — they're too weak
                    // alone and too valuable as Executor pilots. Only deploy aboard ships.
                    if (cardTitleLower.contains("piett") || cardTitleLower.contains("gherant")) {
                        boolean deployingAboardShip = actionLower.contains("aboard") || actionLower.contains("pilot")
                            || actionLower.contains("executor") || actionLower.contains("simultaneously");
                        if (deployingAboardShip) {
                            action.addReasoning("V40.1 PILOT ABOARD: Deploy aboard ship!", 300.0f);
                        } else {
                            action.addReasoning("V47 EXECUTOR PILOT GROUND BLOCK: " + card.getTitle()
                                + " must deploy aboard a ship, not to ground!", -9999.0f);
                            LOG.warn("V47 EXECUTOR PILOT GROUND BLOCK: {} — blocking ground deploy, pilots belong on ships!",
                                card.getTitle());
                        }
                    }

                    // === MATCHING PILOT CHECK ===
                    if (actionLower.contains("matching")) {
                        action.addReasoning("Matching pilot/ship synergy", 30.0f);
                    }

                    // === STRATEGIC BONUSES ===
                    if (needsReinforcement) {
                        action.addReasoning("Need to reinforce board", 20.0f);
                    }

                    // Low life force - be more aggressive
                    if (lifeForce <= ChosenOneConfig.CRITICAL_LIFE_FORCE) {
                        action.addReasoning("Critical life force - must deploy!", 30.0f);
                    }
                }
            } else {
                // Unknown card - check if we should block it
                if (plan != null && plan.getStrategy() == DeployStrategy.DEPLOY_LOCATIONS && !plan.isForceAllowExtras()) {
                    // During DEPLOY_LOCATIONS, block unknown non-location actions
                    // (locations were already handled above with +200 bonus)
                    LOG.warn("🚫 BLOCKING unknown card deploy during DEPLOY_LOCATIONS plan");
                    action.addReasoning("BLOCKED: Unknown card during DEPLOY_LOCATIONS plan", -1000.0f);
                    actions.add(action);
                    continue;
                }
                action.addReasoning("Unknown card", -10.0f);
            }

            // === V67bk (Steve, 2026-05-11): V52 SPEND FORCE +300 REMOVED ===
            // See Rando DeployEvaluator V67bk comment for full rationale.
            // Save force for interrupts, next turn, opponent's-turn responses.

            // === V52 FIX 11: DEPLOY MOMENTUM — Bonus for deploying multiple cards same turn ===
            {
                if (plan != null && plan.getDeploymentsMade() >= 1) {
                    float momentumBonus = 100.0f;
                    if (plan.getDeploymentsMade() >= 2) momentumBonus = 150.0f;
                    if (plan.getDeploymentsMade() >= 3) momentumBonus = 200.0f;
                    action.addReasoning(String.format(
                        "V52 MOMENTUM: Already deployed %d cards this turn — keep deploying! (+%.0f)",
                        plan.getDeploymentsMade(), momentumBonus), momentumBonus);
                    LOG.warn("V52 MOMENTUM: {} gets +{} — {} cards already deployed this turn",
                        card != null ? card.getTitle() : actionText, (int)momentumBonus, plan.getDeploymentsMade());
                }
            }

            // === V52 FIX 12: TDIGWATT TURN 1 SCRIPT ===
            {
                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer tdigObjAnalyzer =
                    context.getObjectiveAnalyzer();
                int tdigTurn = context.getTurnNumber();
                if (tdigObjAnalyzer != null && tdigObjAnalyzer.isAnalyzed()
                    && tdigObjAnalyzer.needsBespinSystemPresence()
                    && !tdigObjAnalyzer.isHuntDownV() && tdigTurn <= 1) {
                    String tdigTitle = card != null && card.getTitle() != null
                        ? card.getTitle().toLowerCase(Locale.ROOT) : "";
                    if (tdigTitle.isEmpty() && cardTitleFromGemp != null) {
                        tdigTitle = cardTitleFromGemp.toLowerCase(Locale.ROOT);
                    }
                    if (!tdigTitle.isEmpty()) {
                        if (tdigTitle.contains("bespin") && actionLower.contains("system")) {
                            action.addReasoning("V52 TDIGWATT T1: Bespin system — FOUNDATION!", 1500.0f);
                            LOG.warn("V52 TDIGWATT T1: Bespin system +1500");
                        } else if (tdigTitle.contains("cloud city") || actionLower.contains("i'm sorry")
                                   || actionLower.contains("i am sorry")) {
                            action.addReasoning("V52 TDIGWATT T1: Cloud City site via I'm Sorry!", 1200.0f);
                            LOG.warn("V52 TDIGWATT T1: Cloud City site +1200");
                        } else if (tdigTitle.contains("lando") && tdigTitle.contains("broker")) {
                            action.addReasoning("V52 TDIGWATT T1: Lando as Broker — key engine piece!", 1000.0f);
                            LOG.warn("V52 TDIGWATT T1: Lando as Broker +1000");
                        } else if (tdigTitle.contains("executor") || tdigTitle.contains("flagship")) {
                            action.addReasoning("V52 TDIGWATT T1: Executor/Flagship — Bespin control!", 900.0f);
                            LOG.warn("V52 TDIGWATT T1: Executor/Flagship +900");
                        } else if (tdigTitle.contains("chiraneau")) {
                            action.addReasoning("V52 TDIGWATT T1: Chiraneau — pilot for Executor!", 850.0f);
                            LOG.warn("V52 TDIGWATT T1: Chiraneau +850");
                        }
                    }
                }
            }

            // === V54 FIX 16: SKYWALKER SAGA EPIC EVENT T1-3 SCRIPT ===
            // Mirror of Rando V54. Detect Skywalker Saga by its unique starting
            // location (Endor: Anakin's Funeral Pyre 217_34) because its
            // objective-slot card is Anger/Fear/Aggression (type EFFECT), which
            // the ObjectiveAnalyzer does not detect.
            {
                int lsTurn = context.getTurnNumber();
                GameState lsGs = context.getGameState();
                boolean isLukeSaga = false;
                if (lsGs != null && lsTurn <= 3) {
                    try {
                        for (PhysicalCard loc : lsGs.getLocationsInOrder()) {
                            if (loc == null) continue;
                            String locTitle = loc.getTitle();
                            if (locTitle != null
                                && locTitle.toLowerCase(Locale.ROOT).contains("anakin's funeral pyre")) {
                                isLukeSaga = true;
                                break;
                            }
                        }
                    } catch (Exception ignored) {}
                }
                if (isLukeSaga) {
                    String lsCardTitle = (card != null && card.getTitle() != null)
                        ? card.getTitle().toLowerCase(Locale.ROOT) : "";
                    if (lsCardTitle.isEmpty() && cardTitleFromGemp != null) {
                        lsCardTitle = cardTitleFromGemp.toLowerCase(Locale.ROOT);
                    }
                    String lsActionLower = actionText.toLowerCase(Locale.ROOT);
                    float turnMult = lsTurn == 1 ? 1.0f : (lsTurn == 2 ? 0.85f : 0.7f);

                    if (lsCardTitle.contains("tatooine: cantina") || lsCardTitle.equals("cantina")) {
                        float s = 1500.0f * turnMult;
                        action.addReasoning("V54 SKYWALKER SAGA T" + lsTurn + ": Tatooine: Cantina — drain engine!", s);
                        LOG.warn("V54 SKYWALKER SAGA T{}: Tatooine: Cantina +{}", lsTurn, (int)s);
                    } else if (lsCardTitle.contains("mos eisley")) {
                        float s = 1500.0f * turnMult;
                        action.addReasoning("V54 SKYWALKER SAGA T" + lsTurn + ": Tatooine: Mos Eisley — Cantina shuttle!", s);
                        LOG.warn("V54 SKYWALKER SAGA T{}: Tatooine: Mos Eisley +{}", lsTurn, (int)s);
                    } else if (lsCardTitle.contains("lars") && lsCardTitle.contains("moisture")) {
                        float s = 1500.0f * turnMult;
                        action.addReasoning("V54 SKYWALKER SAGA T" + lsTurn + ": Lars' Moisture Farm — Tatooine site!", s);
                        LOG.warn("V54 SKYWALKER SAGA T{}: Lars' Moisture Farm +{}", lsTurn, (int)s);
                    } else if (lsCardTitle.startsWith("tatooine:") && !lsCardTitle.contains("jabba")) {
                        float s = 1300.0f * turnMult;
                        action.addReasoning("V54 SKYWALKER SAGA T" + lsTurn + ": Tatooine battleground site!", s);
                        LOG.warn("V54 SKYWALKER SAGA T{}: {} (Tatooine site) +{}", lsTurn, lsCardTitle, (int)s);
                    } else if (lsCardTitle.equals("tatooine") && lsActionLower.contains("system")) {
                        float s = 900.0f * turnMult;
                        action.addReasoning("V54 SKYWALKER SAGA T" + lsTurn + ": Tatooine system — ship presence!", s);
                        LOG.warn("V54 SKYWALKER SAGA T{}: Tatooine system +{}", lsTurn, (int)s);
                    } else if (lsCardTitle.contains("young skywalker")) {
                        float s = 1200.0f * turnMult;
                        action.addReasoning("V54 SKYWALKER SAGA T" + lsTurn + ": Young Skywalker — Luke persona (I have it)!", s);
                        LOG.warn("V54 SKYWALKER SAGA T{}: Young Skywalker +{}", lsTurn, (int)s);
                    } else if (lsCardTitle.contains("luke") && card != null && card.getBlueprint() != null
                             && card.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                        float s = 1100.0f * turnMult;
                        action.addReasoning("V54 SKYWALKER SAGA T" + lsTurn + ": Luke persona — deploy for drain power!", s);
                        LOG.warn("V54 SKYWALKER SAGA T{}: {} (Luke persona) +{}", lsTurn, lsCardTitle, (int)s);
                    } else if (lsCardTitle.contains("luke's lightsaber")) {
                        float s = 1100.0f * turnMult;
                        action.addReasoning("V54 SKYWALKER SAGA T" + lsTurn + ": Luke's Lightsaber — arm Luke NOW!", s);
                        LOG.warn("V54 SKYWALKER SAGA T{}: Luke's Lightsaber +{}", lsTurn, (int)s);
                    }
                    // NOTE: Lightsaber-from-Reserve pullers (Gift Of The Mentor) are a
                    // BATTLE combo (Obi-Wan/Yoda buddy → +2 destiny), not deploy tempo.
                    else if ((lsCardTitle.contains("obi-wan") || lsCardTitle.contains("yoda"))
                             && card != null && card.getBlueprint() != null
                             && card.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                        float s = 800.0f * turnMult;
                        action.addReasoning("V54 SKYWALKER SAGA T" + lsTurn + ": Jedi buddy for Luke!", s);
                        LOG.warn("V54 SKYWALKER SAGA T{}: {} (Jedi buddy) +{}", lsTurn, lsCardTitle, (int)s);
                    }
                }
            }

            // === V55 FIX 17: HIGH-ABILITY CHARACTER DEPLOY URGENCY ===
            // Generalized: ability >= 6 character in hand gets a deploy urgency bonus
            // every turn, scaled higher early. Side-agnostic, deck-agnostic.
            {
                if (card != null && card.getBlueprint() != null
                    && card.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                    Float abl = null;
                    try { abl = card.getBlueprint().getAbility(); } catch (Exception e) {}
                    if (abl != null && abl >= 6.0f) {
                        int v55Turn = context.getTurnNumber();
                        float v55Bonus;
                        if (v55Turn <= 3)      v55Bonus = 500.0f;
                        else if (v55Turn <= 6) v55Bonus = 350.0f;
                        else                   v55Bonus = 200.0f;
                        action.addReasoning(
                            "V55 HIGH-ABILITY: " + card.getTitle() + " (ability " + abl.intValue()
                                + ") in hand — deploy, don't hoard!", v55Bonus);
                        LOG.warn("V55 HIGH-ABILITY: {} (ability {}) T{} +{}",
                            card.getTitle(), abl.intValue(), v55Turn, (int)v55Bonus);
                    }
                }
            }

            // === V52b FIX 13: HIDDEN PATH JEDI FLOOD (turns 1-2) ===
            // Deploy Jedi FIRST and FAST. Check both card title AND action text,
            // because Fallen Order deploys Jedi via "Deploy a Jedi Survivor stacked here"
            // where the card is Fallen Order, not the Jedi itself.
            {
                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer hpObjAnalyzer =
                    context.getObjectiveAnalyzer();
                int hpTurn = context.getTurnNumber();
                if (hpObjAnalyzer != null && hpObjAnalyzer.isAnalyzed()
                    && hpTurn <= 2) {
                    String hpObjTitle = hpObjAnalyzer.getObjectiveTitle();
                    boolean isHiddenPath = hpObjTitle != null
                        && hpObjTitle.toLowerCase(Locale.ROOT).contains("hidden path");
                    if (isHiddenPath) {
                        String hpCardTitle = (card != null && card.getTitle() != null)
                            ? card.getTitle().toLowerCase(Locale.ROOT) : "";
                        if (hpCardTitle.isEmpty() && cardTitleFromGemp != null) {
                            hpCardTitle = cardTitleFromGemp.toLowerCase(Locale.ROOT);
                        }
                        // Also check the ACTION TEXT — Fallen Order says "Deploy a Jedi Survivor"
                        String hpActionLower = actionText.toLowerCase(Locale.ROOT);

                        // Detect Jedi deploy via card OR action text
                        boolean isJediDeploy = false;
                        boolean isJediChar = false;
                        if (card != null && card.getBlueprint() != null
                            && card.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                            Float hpAbility = null;
                            try { hpAbility = card.getBlueprint().getAbility(); } catch (Exception e) {}
                            // Only count ability >= 6 as true Jedi (excludes Padawans like Sabine ability 4)
                            if (hpAbility != null && hpAbility >= 6) isJediChar = true;
                            // Named Jedi always qualify
                            if (hpCardTitle.contains("obi-wan") || hpCardTitle.contains("quinlan")
                                || hpCardTitle.contains("kelleran") || hpCardTitle.contains("cal kestis")
                                || hpCardTitle.contains("ezra") || hpCardTitle.contains("kanan")
                                || hpCardTitle.contains("ahsoka tano") || hpCardTitle.contains("cere")
                                || hpCardTitle.contains("luke") || hpCardTitle.contains("yoda")) {
                                isJediChar = true;
                            }
                        }
                        // Fallen Order "Deploy a Jedi Survivor" action
                        if (hpActionLower.contains("jedi survivor") || hpActionLower.contains("fallen order")) {
                            isJediDeploy = true;
                        }

                        if (isJediChar) {
                            action.addReasoning("V52b HIDDEN PATH: Jedi character — deploy FIRST!", 800.0f);
                            LOG.warn("V52b HIDDEN PATH: {} (Jedi char) +800 on turn {}", card.getTitle(), hpTurn);
                        } else if (isJediDeploy) {
                            action.addReasoning("V52b HIDDEN PATH: Fallen Order Jedi deploy — deploy FIRST!", 800.0f);
                            LOG.warn("V52b HIDDEN PATH: Fallen Order Jedi deploy +800 on turn {}", hpTurn);
                        } else if (hpCardTitle.contains("lightsaber") || hpCardTitle.contains("shoto")) {
                            action.addReasoning("V52b HIDDEN PATH: Lightsaber — arm the Jedi!", 700.0f);
                            LOG.warn("V52b HIDDEN PATH: {} (lightsaber) +700", hpCardTitle);
                        } else if (hpCardTitle.contains("holocron") || hpActionLower.contains("holocron")) {
                            action.addReasoning("V52b HIDDEN PATH: Jedi Holocron!", 600.0f);
                            LOG.warn("V52b HIDDEN PATH: {} (holocron) +600", hpCardTitle);
                        }
                    }
                }
            }

            // NOTE: Don't add cardIds to pendingDeployCardIds here during evaluation!
            // We used to do: pendingDeployCardIds.add(cardIdStr);
            // But that caused ALL evaluated cards to be marked as "already tried"
            // which broke deployment plans. We now only track when the action is actually chosen.
            // See line ~630 where we track the selected action's cardId.

            LOG.debug("[DeployEvaluator] Scored '{}' -> {} ({})",
                actionText.length() > 50 ? actionText.substring(0, 50) + "..." : actionText,
                String.format("%.1f", action.getScore()),
                action.getReasoningString());

            actions.add(action);
        }

        LOG.info("[DeployEvaluator] Evaluated {} deploy actions", actions.size());
        return actions;
    }

    /**
     * Extract card ID from action text (if present).
     */
    private String extractCardIdFromAction(String actionText) {
        if (actionText == null) return null;

        // Look for cardId pattern like "cardId='123'"
        Pattern pattern = Pattern.compile("cardId=['\"]?(\\d+)['\"]?");
        Matcher matcher = pattern.matcher(actionText);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    /**
     * Extract blueprint ID from action text HTML.
     * GEMP includes card hints in HTML like: <div class='cardHint' value='7_305'>•Card Name</div>
     *
     * Ported from Python deploy_evaluator.py _extract_blueprint_from_action
     */
    private String extractBlueprintFromActionHtml(String actionText) {
        if (actionText == null) return null;

        // Look for value='blueprint_id' pattern in HTML
        // Example: <div class='cardHint' value='7_305'>•OS-72-1</div>
        Pattern pattern = Pattern.compile("value=['\"]([^'\"]+)['\"]");
        Matcher matcher = pattern.matcher(actionText);
        if (matcher.find()) {
            String blueprintId = matcher.group(1);
            LOG.debug("[extractBlueprintFromActionHtml] Found blueprint '{}' in action text", blueprintId);
            return blueprintId;
        }

        return null;
    }

    /**
     * Try to find a card in hand that matches the action text.
     * NOTE: This is a fallback method - prefer using cardId lookup via gameState.findCardById()
     */
    private PhysicalCard findCardInHand(List<PhysicalCard> hand, String actionText) {
        if (hand == null || actionText == null) {
            return null;
        }

        String actionLower = actionText.toLowerCase(Locale.ROOT);

        for (PhysicalCard card : hand) {
            if (card == null) continue;

            // Match by title
            String title = card.getTitle();
            if (title != null) {
                String titleLower = title.toLowerCase(Locale.ROOT);
                if (actionLower.contains(titleLower)) {
                    LOG.debug("[findCardInHand] ✅ Found match: '{}' in action text", title);
                    return card;
                }
            }
        }

        // Only log failure at debug level - this method is a fallback and often won't find anything
        LOG.debug("[findCardInHand] No title match for action: '{}'",
            actionText.length() > 50 ? actionText.substring(0, 50) + "..." : actionText);

        return null;
    }

    /**
     * Count cards in play for a player.
     */
    private int countCardsInPlay(GameState gameState, String playerId) {
        if (gameState == null || playerId == null) return 0;

        int count = 0;
        for (PhysicalCard card : gameState.getAllPermanentCards()) {
            if (card == null) continue;
            if (!playerId.equals(card.getOwner())) continue;
            if (card.getZone() == null || !card.getZone().isInPlay()) continue;

            SwccgCardBlueprint blueprint = card.getBlueprint();
            if (blueprint == null) continue;

            CardCategory category = blueprint.getCardCategory();
            if (category == CardCategory.CHARACTER ||
                category == CardCategory.STARSHIP ||
                category == CardCategory.VEHICLE ||
                category == CardCategory.LOCATION) {
                count++;
            }
        }

        return count;
    }
}
