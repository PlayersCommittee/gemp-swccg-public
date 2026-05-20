package com.gempukku.swccgo.ai.models.chosenone.evaluators;

import com.gempukku.swccgo.ai.common.AiPriorityCards;
import com.gempukku.swccgo.ai.models.chosenone.ChosenOneConfig;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Action Text Evaluator
 *
 * Handles text-based action ranking by pattern matching action text.
 * Ported from Python action_text_evaluator.py (~1350 lines)
 *
 * This evaluator provides baseline rankings for common SWCCG actions
 * based on analyzing the action text.
 */
public class ActionTextEvaluator extends ActionEvaluator {

    // Rank deltas (from Python)
    private static final float VERY_GOOD_DELTA = 50.0f;
    private static final float GOOD_DELTA = 30.0f;
    private static final float BAD_DELTA = -30.0f;
    private static final float VERY_BAD_DELTA = -50.0f;

    // Pattern for extracting blueprint ID from action text HTML
    private static final Pattern BLUEPRINT_PATTERN = Pattern.compile("value='([^']+)'");

    // Track barriered targets to avoid playing multiple barriers on same card
    private Set<String> barrieredTargets = new HashSet<>();
    private int barrierTurn = 0;

    public ActionTextEvaluator() {
        super("ActionText");
    }

    @Override
    public boolean canEvaluate(DecisionContext context) {
        String decisionType = context.getDecisionType();

        // Handle CARD_ACTION_CHOICE and ACTION_CHOICE
        if ("CARD_ACTION_CHOICE".equals(decisionType) || "ACTION_CHOICE".equals(decisionType)) {
            return true;
        }

        // Also handle MULTIPLE_CHOICE for capacity slot, Epic Event, and activation confirmation
        if ("MULTIPLE_CHOICE".equals(decisionType)) {
            String decisionText = context.getDecisionText();
            if (decisionText != null) {
                String dtLower = decisionText.toLowerCase();
                if (dtLower.contains("capacity slot") || dtLower.contains("choose an option")
                    || dtLower.contains("not activated force") || dtLower.contains("have not activated")) {
                    return true;
                }
                // V79 (Steve, 2026-05-15): Death Star hyperspace destinations.
                if (dtLower.contains("choose parsec to move to")
                    || (dtLower.contains("choose destination for") && dtLower.contains("parsec"))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<EvaluatedAction> evaluate(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();
        SwccgGame game = context.getGame();
        List<String> actionIds = context.getActionIds();
        List<String> actionTexts = context.getActionTexts();
        List<String> cardIds = context.getCardIds();
        Set<String> blocked = context.getBlockedResponses();

        for (int i = 0; i < actionIds.size(); i++) {
            String actionId = actionIds.get(i);
            String actionText = i < actionTexts.size() ? actionTexts.get(i) : "";
            String cardId = i < cardIds.size() ? cardIds.get(i) : null;
            String textLower = actionText.toLowerCase();

            EvaluatedAction action = new EvaluatedAction(actionId, ActionType.UNKNOWN, 0.0f, actionText);

            // Check if this response is blocked (loop prevention)
            if (blocked.contains(actionId) || blocked.contains(actionText)) {
                action.addReasoning("BLOCKED (loop prevention)", -200.0f);
                logger.debug("Blocked action: {}", actionText);
            }

            // === V87 (Steve, 2026-05-16): HARD-BLOCK pilot/passenger capacity slot swaps ===
            // Mirror of Rando V87. See Rando ActionTextEvaluator V87 comment for
            // full rationale. Swap loop in replay tem28wtufcy7d08j.
            if (textLower.contains("move to passenger capacity slot")
                    || textLower.contains("move to pilot capacity slot")) {
                action.addReasoning(
                    "V87 NO SWAP: pilot↔passenger capacity slot rearrangement is pointless — hard block",
                    -3000.0f);
                logger.warn("V87 NO SWAP blocking: '{}' → -3000", actionText);
                actions.add(action);
                continue;
            }

            // V79 (Steve, 2026-05-15): VERGE — DEATH STAR PARSEC / ORBIT MULTIPLE_CHOICE
            // Mirror of Rando V79 ActionTextEvaluator parsec/orbit handler.
            {
                String v79DtLower = context.getDecisionText() != null
                    ? context.getDecisionText().toLowerCase() : "";
                boolean v79IsParsecChoice = v79DtLower.contains("choose parsec to move to");
                boolean v79IsDestChoice = v79DtLower.contains("choose destination for")
                    && v79DtLower.contains("parsec");
                if ((v79IsParsecChoice || v79IsDestChoice) && gameState != null
                        && context.getPlayerId() != null) {
                    boolean v79Verge = false;
                    boolean v79AtScarif = false;
                    String v79PlayerId = context.getPlayerId();
                    try {
                        for (PhysicalCard pc : gameState.getAllPermanentCards()) {
                            if (pc == null || !v79PlayerId.equals(pc.getOwner())) continue;
                            if (pc.getBlueprint() == null) continue;
                            String t = pc.getTitle() != null
                                ? pc.getTitle().toLowerCase(java.util.Locale.ROOT) : "";
                            if (t.contains("on the verge of greatness")
                                    || t.contains("taking control of the weapon")) {
                                v79Verge = true;
                            }
                            if (t.contains("death star")
                                    && pc.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
                                PhysicalCard dsLoc = pc.getAtLocation();
                                if (dsLoc != null && dsLoc.getTitle() != null
                                        && dsLoc.getTitle().toLowerCase(java.util.Locale.ROOT).contains("scarif")) {
                                    v79AtScarif = true;
                                }
                            }
                        }
                    } catch (Exception e) { /* ignore */ }

                    if (v79Verge && !v79AtScarif) {
                        if (v79IsParsecChoice) {
                            Integer parsec = null;
                            try { parsec = Integer.parseInt(actionText.trim()); }
                            catch (Exception e) {
                                java.util.regex.Matcher pm = java.util.regex.Pattern
                                    .compile("(\\d+)").matcher(actionText);
                                if (pm.find()) {
                                    try { parsec = Integer.parseInt(pm.group(1)); }
                                    catch (Exception ee) { /* ignore */ }
                                }
                            }
                            if (parsec != null) {
                                int dist = Math.abs(parsec - 7);
                                if (dist == 0) {
                                    action.addReasoning("V79 PARSEC 7 (Scarif!) — pick this", 1500.0f);
                                } else if (dist == 1) {
                                    action.addReasoning("V79 PARSEC " + parsec + " (1 hop from Scarif)", 1200.0f);
                                } else if (parsec > 4) {
                                    action.addReasoning("V79 PARSEC " + parsec + " (toward Scarif)", 800.0f);
                                } else {
                                    action.addReasoning("V79 PARSEC " + parsec + " — WRONG DIRECTION", -800.0f);
                                }
                                logger.warn("V79 PARSEC CHOICE: parsec {}", parsec);
                            }
                        } else if (v79IsDestChoice) {
                            if (textLower.contains("scarif")) {
                                action.addReasoning("V79 ORBIT SCARIF — must take!", 1500.0f);
                                logger.warn("V79 DESTINATION orbit Scarif → +1500");
                            } else {
                                action.addReasoning("V79 destination not Scarif — avoid", -200.0f);
                            }
                        }
                        // V79: Must add action to output list so engine gets a scored option.
                        actions.add(action);
                        continue;
                    }
                }
            }

            // V67bi (mirror of Rando, 2026-05-10) — FORCE LIGHTNING SELF-TARGET BLOCK.
            // Hard-block Force Lightning if there's no opponent character in
            // play. The engine already validates the granting card (Emperor)
            // is present before offering the action — we only check there's a
            // valid OPPONENT target so Rando doesn't self-target.
            {
                GameState v67biGs = context.getGameState();
                String v67biPid = context.getPlayerId();
                if (cardId != null && v67biGs != null && v67biPid != null) {
                    try {
                        PhysicalCard v67biSource = v67biGs.findCardById(Integer.parseInt(cardId));
                        if (v67biSource != null && v67biSource.getTitle() != null
                                && v67biSource.getTitle().toLowerCase(java.util.Locale.ROOT)
                                       .contains("force lightning")) {
                            int v67biOpps = 0;
                            for (PhysicalCard pc : v67biGs.getAllPermanentCards()) {
                                if (pc == null || pc.getBlueprint() == null) continue;
                                if (v67biPid.equals(pc.getOwner())) continue;
                                if (pc.getBlueprint().getCardCategory()
                                        != com.gempukku.swccgo.common.CardCategory.CHARACTER) continue;
                                if (pc.getZone() == null || !pc.getZone().isInPlay()) continue;
                                v67biOpps++;
                                if (v67biOpps > 0) break;
                            }
                            if (v67biOpps == 0) {
                                action.addReasoning(
                                    "V67bi FORCE LIGHTNING BLOCK: no opponent character in play — never self-target!",
                                    -9999.0f);
                                logger.warn("V67bi FORCE LIGHTNING BLOCK: 0 opponent chars in play — hard-block '{}'",
                                    actionText);
                            } else {
                                logger.info("V67bi FORCE LIGHTNING OK: opponent char(s) in play — allow targeting");
                            }
                        }
                    } catch (NumberFormatException nfe) { /* ignore */ }
                      catch (Exception e) {
                        logger.debug("V67bi check error: {}", e.getMessage());
                    }
                }
            }

            // ========== V38.3: "Not activated Force" — ALWAYS go back and activate ==========
            {
                String decisionTextCheck = context.getDecisionText() != null
                    ? context.getDecisionText().toLowerCase() : "";
                if (decisionTextCheck.contains("not activated force") || decisionTextCheck.contains("have not activated")) {
                    if (textLower.equals("no")) {
                        action.addReasoning("V38.3 MUST ACTIVATE: Go back and activate Force!", 9999.0f);
                    } else if (textLower.equals("yes")) {
                        action.addReasoning("V38.3 NEVER SKIP ACTIVATION!", -9999.0f);
                    }
                }
            }

            // ========== Skip ALL Deploy Actions ==========
            // Deploy actions should be handled EXCLUSIVELY by DeployEvaluator.
            // ========== V53c: BLOCK WOKLING EFFECT SEARCH (EARLY CHECK) ==========
            if (textLower.contains("effect") && textLower.contains("reserve deck")
                && textLower.contains("take")) {
                boolean isWoklingSource = false;
                if (cardId != null && gameState != null) {
                    try {
                        PhysicalCard wokSrc = gameState.findCardById(Integer.parseInt(cardId));
                        if (wokSrc != null && wokSrc.getTitle() != null
                            && wokSrc.getTitle().toLowerCase().contains("wokling")) {
                            isWoklingSource = true;
                        }
                        if (wokSrc != null && wokSrc.getBlueprintId(true) != null
                            && wokSrc.getBlueprintId(true).equals("200_47")) {
                            isWoklingSource = true;
                        }
                    } catch (Exception e) { /* ignore */ }
                }
                if (isWoklingSource && context.getTurnNumber() <= 3) {
                    action.setScore(-9999.0f);
                    action.addReasoning("V53c BLOCK WOKLING: Turns 1-3 — save force for deploys!", -9999.0f);
                    logger.warn("V53c WOKLING BLOCKED: Turn {} — HARD BLOCK!", context.getTurnNumber());
                    actions.add(action);
                    continue;
                }
            }

            if (actionText.equals("Deploy") ||
                (actionText.startsWith("Deploy ") && !textLower.contains("from"))) {
                // Skip this action - let DeployEvaluator handle it
                continue;
            }

            // ========== V24.4: LOCATIONS FIRST — DEPLOY LOCATIONS BEFORE ANYTHING ELSE ==========
            // Locations MUST be deployed before activating effects (AMSD, K&D, etc.).
            // If the bot has ANY location in hand, penalize all non-deploy actions heavily
            // so that deploy actions (handled by DeployEvaluator) always win priority.
            if (gameState != null && context.getPhase() == Phase.DEPLOY) {
                java.util.List<com.gempukku.swccgo.game.PhysicalCard> hand = context.getHand();
                if (hand != null) {
                    boolean hasLocationInHand = false;
                    for (com.gempukku.swccgo.game.PhysicalCard handCard : hand) {
                        if (handCard != null && handCard.getBlueprint() != null &&
                            handCard.getBlueprint().getCardCategory() == com.gempukku.swccgo.common.CardCategory.LOCATION) {
                            hasLocationInHand = true;
                            break;
                        }
                    }
                    if (hasLocationInHand) {
                        // Check if this action is a search that PULLS locations (TDIGWATT, I'm Sorry, etc.)
                        // Those are OK — they help GET locations. But effect activations like AMSD should wait.
                        // V24.9: Added "sorry" — I'm Sorry deploys interior CC sites from reserve!
                        boolean isLocationSearch = textLower.contains("bespin") || textLower.contains("location")
                            || textLower.contains("cloud city") || textLower.contains("site")
                            || textLower.contains("sorry");
                        // V24.15: Exempt AMSD from LOCATIONS FIRST penalty!
                        // AMSD deploys a Star Destroyer — it's effectively a deploy action, not an "effect".
                        // When Bespin is already on the table, AMSD should fire immediately to get Executor there.
                        boolean isAmsdAction = textLower.contains("alert my star destroyer") ||
                            textLower.contains("amsd") ||
                            (textLower.contains("reveal") && textLower.contains("pilot") && textLower.contains("star destroyer")) ||
                            (textLower.contains("star destroyer") && textLower.contains("deploy both"));
                        // V60 RESERVE PULL EXEMPTION: NEVER penalize Reserve Deck pulls.
                        // Steve's rule (feedback_reserve_deck_pulls.md): "[Download]" and
                        // "from Reserve Deck" actions are free value — thin the deck, bring
                        // key cards into play. Fire them every turn. They complement location
                        // deploys, they don't replace them. FIXES Issue #D from peaceful-pike
                        // replay: Sai'torr Kal Fas never fired Obi-Wan's Lightsaber because
                        // V24.4 blocked `[Download] a matching weapon` at -800.
                        boolean isReservePull = textLower.contains("[download]")
                            || textLower.contains("from reserve deck")
                            || textLower.contains("take an effect into hand")
                            || textLower.contains("take a character into hand");
                        // V67ba: EXEMPT generic deploy-from-hand entry actions.
                        boolean isDeployEntry = textLower.equals("play a card")
                            || textLower.equals("deploy")
                            || textLower.equals("deploy a card")
                            || textLower.startsWith("deploy ")
                            || textLower.startsWith("play a card ");
                        if (!isLocationSearch && !isAmsdAction && !isReservePull && !isDeployEntry) {
                            action.addReasoning("V24.4 LOCATIONS FIRST: Deploy locations in hand before activating effects!", -800.0f);
                            logger.warn("V24.4 LOCATIONS FIRST: Penalizing '{}' — location in hand needs deploying first! (-800)", actionText);
                        } else if (isAmsdAction) {
                            logger.warn("V24.15 AMSD EXEMPT: Not penalizing AMSD with LOCATIONS FIRST — AMSD deploys a Star Destroyer!");
                        } else if (isReservePull) {
                            logger.warn("V60 RESERVE PULL EXEMPT: '{}' is a Reserve Deck pull — NEVER penalize, always fire!", actionText);
                        } else if (isDeployEntry) {
                            logger.warn("V67ba DEPLOY-ENTRY EXEMPT: '{}' is the deploy-from-hand entry point — NEVER penalize!", actionText);
                        }
                    }
                }
            }

            // ========== V23: EMPTY PILE GUARD ==========
            // Block interrupts/actions that search piles which are empty.
            // Sith Fury on turn 1 wastes 4 force searching an empty Lost Pile.
            if (gameState != null) {
                String pid = context.getPlayerId();
                // Lost Pile searches
                if (textLower.contains("lost pile") && (textLower.contains("take") ||
                    textLower.contains("search") || textLower.contains("retrieve"))) {
                    int lostSize = gameState.getLostPile(pid).size();
                    if (lostSize == 0) {
                        action.addReasoning("V23 EMPTY PILE: Lost Pile is empty — search will fail!", -300.0f);
                        logger.warn("V23 EMPTY PILE GUARD: Blocking '{}' — Lost Pile is empty!", actionText);
                        actions.add(action);
                        continue;
                    } else if (lostSize <= 2) {
                        action.addReasoning("V23 LOW PILE: Lost Pile only has " + lostSize + " cards — risky search", -100.0f);
                        logger.warn("V23 LOW PILE: '{}' — Lost Pile only has {} cards", actionText, lostSize);
                    }
                }
                // Used Pile searches
                if (textLower.contains("used pile") && (textLower.contains("take") ||
                    textLower.contains("search"))) {
                    int usedSize = gameState.getUsedPile(pid).size();
                    if (usedSize == 0) {
                        action.addReasoning("V23 EMPTY PILE: Used Pile is empty — search will fail!", -300.0f);
                        logger.warn("V23 EMPTY PILE GUARD: Blocking '{}' — Used Pile is empty!", actionText);
                        actions.add(action);
                        continue;
                    }
                }
            }

            // ========== V24: AMSD BESPIN GATE ==========
            // Alert My Star Destroyer needs a system location to deploy the Star Destroyer to.
            // If Bespin isn't on the table yet, AMSD has nowhere to send the ship — block it.
            if (gameState != null && (textLower.contains("alert my star destroyer") ||
                textLower.contains("amsd") ||
                (textLower.contains("star destroyer") && textLower.contains("deploy both")) ||
                (textLower.contains("star destroyer") && textLower.contains("pilot") && textLower.contains("deploy")) ||
                (textLower.contains("reveal") && textLower.contains("pilot") && textLower.contains("star destroyer")))) {
                boolean bespinSystemOnTable = false;
                try {
                    for (com.gempukku.swccgo.game.PhysicalCard loc : gameState.getLocationsInOrder()) {
                        if (loc != null && loc.getTitle() != null &&
                            loc.getTitle().toLowerCase(java.util.Locale.ROOT).contains("bespin") &&
                            loc.getBlueprint() != null && loc.getBlueprint().getCardSubtype() != null &&
                            loc.getBlueprint().getCardSubtype() == com.gempukku.swccgo.common.CardSubtype.SYSTEM) {
                            bespinSystemOnTable = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.debug("V24 AMSD gate: Error checking Bespin: {}", e.getMessage());
                }
                if (!bespinSystemOnTable) {
                    action.addReasoning("V24 AMSD BLOCKED: No Bespin system on table — Star Destroyer has nowhere to deploy!", -9999.0f);
                    logger.warn("V24 AMSD GATE: HARD BLOCKING AMSD — Bespin system not on table yet! (-9999)");
                    actions.add(action);
                    continue;
                }
            }

            // ========== V24.10: AMSD — PIETT + EXECUTOR ONLY ==========
            // AMSD should ONLY fire when Piett is the pilot AND Executor is in reserve.
            // No other pilot (Chiraneau, Ozzel, Motti, Evazan, etc.) should use AMSD.
            // If Piett isn't the target or Executor isn't in reserve, block AMSD entirely.
            // AMSD can only be used TWICE per game — never waste an attempt!
            if (gameState != null && (textLower.contains("alert my star destroyer") ||
                textLower.contains("amsd") ||
                (textLower.contains("star destroyer") && textLower.contains("deploy both")) ||
                (textLower.contains("star destroyer") && textLower.contains("pilot") && textLower.contains("deploy")) ||
                (textLower.contains("reveal") && textLower.contains("pilot") && textLower.contains("star destroyer")))) {

                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle amsdOracle = context.getDeckOracle();
                int currentTurn = context.getTurnNumber();

                // V24.10: Check if AMSD already failed this turn — don't waste a second attempt.
                // AMSD can only be used twice per game, so every attempt must count.
                // If it failed, Piett/Executor aren't in the right zones yet.
                // Wait for recirculation on the next turn.
                if (amsdOracle != null && amsdOracle.hasAmsdFailedThisTurn(currentTurn)) {
                    action.addReasoning("V24.10 AMSD BLOCKED: Already failed this turn — save for next turn after recirculation!", -9999.0f);
                    logger.warn("V24.10 AMSD RETRY BLOCK: AMSD already failed on turn {} — don't waste another attempt!", currentTurn);
                    actions.add(action);
                    continue;
                }

                // V24.10: AMSD pilot check — two scenarios:
                // 1. Action text names a specific pilot (e.g., "deploy Piett's matching Star Destroyer")
                //    → Check if it's Piett. Block if not.
                // 2. Action text is generic (e.g., "Reveal pilot or Star Destroyer from hand")
                //    → Check DeckOracle: is Piett in hand AND Executor in reserve? If so, ALLOW.
                //    The actual pilot selection happens in CardSelectionEvaluator's AMSD guard.
                boolean isGenericReveal = textLower.contains("reveal") && !textLower.contains("piett")
                    && !textLower.contains("vader") && !textLower.contains("chiraneau")
                    && !textLower.contains("ozzel") && !textLower.contains("motti");

                if (isGenericReveal) {
                    // Generic "Reveal pilot or Star Destroyer from hand" — use DeckOracle to decide
                    if (amsdOracle != null && amsdOracle.isAnalyzed()) {
                        boolean piettInHand = amsdOracle.isCardInHand("Admiral Piett") || amsdOracle.isCardInHand("Piett");
                        boolean executorInReserve = amsdOracle.isCardInReserve("Executor") ||
                            amsdOracle.isCardInReserve("Flagship Executor");
                        // V24.14: Also check if Executor is in hand — AMSD pulls from RESERVE only!
                        boolean executorInHand = amsdOracle.isCardInHand("Executor") ||
                            amsdOracle.isCardInHand("Flagship Executor");
                        if (executorInHand) {
                            action.addReasoning("V24.14 AMSD BLOCKED: Executor in hand — deploy manually!", -9999.0f);
                            logger.warn("V24.14 AMSD BLOCK (generic): Executor in hand — can't pull from reserve!");
                            amsdOracle.recordAmsdFailedOnTurn(currentTurn);
                            actions.add(action);
                            continue;
                        }
                        if (piettInHand && executorInReserve) {
                            // V45: Check if we have enough force to pay for Piett + Executor
                            int amsdForceAvail = context.getForcePileSize();
                            int amsdMinForce = 7;
                            if (amsdForceAvail < amsdMinForce) {
                                action.addReasoning(String.format(
                                    "V45 AMSD UNAFFORDABLE: Need %d force for Piett+Executor but only %d available!",
                                    amsdMinForce, amsdForceAvail), -9999.0f);
                                logger.warn("V45 AMSD UNAFFORDABLE: Need {} force but only {} — HARD BLOCK!", amsdMinForce, amsdForceAvail);
                                actions.add(action);
                                continue;
                            }
                            // Perfect — Piett + Executor available. ALLOW AMSD, boost it!
                            // V24.15: On turn 1-2, AMSD is CRITICAL — must fire immediately after Bespin!
                            // Later turns: still high priority but less urgent.
                            float amsdBoost = 500.0f;
                            if (currentTurn <= 2) {
                                amsdBoost = 1500.0f;  // V24.15: Mega-boost on early turns — Executor MUST deploy ASAP!
                                action.addReasoning("V24.15 AMSD MEGA PRIORITY: Turn " + currentTurn + " — Executor MUST deploy NOW to control Bespin!", amsdBoost);
                                logger.warn("V24.15 AMSD MEGA PRIORITY: Turn {} — Piett + Executor ready, mega-boost +{} to ensure AMSD fires!", currentTurn, amsdBoost);
                            } else {
                                action.addReasoning("V24.10 AMSD APPROVED: Piett + Executor ready — fire AMSD!", amsdBoost);
                                logger.warn("V24.10 AMSD: Generic reveal — Piett in hand, Executor in reserve — APPROVED (+{})!", amsdBoost);
                            }
                        } else if (!piettInHand) {
                            action.addReasoning("V24.10 AMSD BLOCKED: Piett NOT in hand — can't use AMSD!", -9999.0f);
                            logger.warn("V24.10 AMSD BLOCK: Generic reveal but Piett not in hand — block!");
                            amsdOracle.recordAmsdFailedOnTurn(currentTurn);
                            actions.add(action);
                            continue;
                        } else {
                            action.addReasoning("V24.10 AMSD BLOCKED: Piett in hand but Executor NOT in reserve!", -9999.0f);
                            logger.warn("V24.10 AMSD BLOCK: Generic reveal — Piett in hand but Executor not in reserve!");
                            amsdOracle.recordAmsdFailedOnTurn(currentTurn);
                            actions.add(action);
                            continue;
                        }
                    }
                    // If oracle unavailable, allow generic reveal (best guess)
                } else if (!textLower.contains("piett")) {
                    // Specific pilot named in action text but it's NOT Piett — hard block
                    action.addReasoning("V24.10 AMSD BLOCKED: Only Piett may use AMSD — " +
                        "this action targets a different pilot!", -9999.0f);
                    logger.warn("V24.10 AMSD HARD BLOCK: Action does NOT target Piett — only Piett + Executor allowed!");
                    if (amsdOracle != null) {
                        amsdOracle.recordAmsdFailedOnTurn(currentTurn);
                    }
                    actions.add(action);
                    continue;
                } else {
                    // Action specifically names Piett — verify Piett in hand AND Executor in reserve
                    if (amsdOracle != null && amsdOracle.isAnalyzed()) {
                        boolean piettInHand = amsdOracle.isCardInHand("Admiral Piett") || amsdOracle.isCardInHand("Piett");
                        boolean executorInReserve = amsdOracle.isCardInReserve("Executor") ||
                            amsdOracle.isCardInReserve("Flagship Executor");
                        // V24.14: Also check if Executor is in hand — AMSD pulls from RESERVE only!
                        boolean executorInHand = amsdOracle.isCardInHand("Executor") ||
                            amsdOracle.isCardInHand("Flagship Executor");

                        if (!piettInHand) {
                            action.addReasoning("V24.10 AMSD BLOCKED: Piett is NOT in hand — can't use AMSD!", -9999.0f);
                            logger.warn("V24.10 AMSD GATE: Piett not in hand — HARD BLOCK");
                            amsdOracle.recordAmsdFailedOnTurn(currentTurn);
                            actions.add(action);
                            continue;
                        }
                        if (executorInHand) {
                            // V24.14: Executor is in hand — AMSD can only pull from reserve!
                            // Deploy Executor manually from hand instead.
                            action.addReasoning("V24.14 AMSD BLOCKED: Executor is in HAND, not reserve — deploy manually instead!", -9999.0f);
                            logger.warn("V24.14 AMSD BLOCK: Executor in hand! AMSD pulls from reserve only — deploy Executor from hand!");
                            amsdOracle.recordAmsdFailedOnTurn(currentTurn);
                            actions.add(action);
                            continue;
                        }
                        if (!executorInReserve) {
                            action.addReasoning("V24.10 AMSD BLOCKED: Piett in hand but Executor NOT in reserve!", -9999.0f);
                            logger.warn("V24.10 AMSD GATE: Piett in hand but Executor not in reserve — HARD BLOCK");
                            amsdOracle.recordAmsdFailedOnTurn(currentTurn);
                            actions.add(action);
                            continue;
                        }
                        // V45: Check if we have enough force to pay for Piett + Executor
                        int amsdForceAvailSpec = context.getForcePileSize();
                        int amsdMinForceSpec = 7;
                        if (amsdForceAvailSpec < amsdMinForceSpec) {
                            action.addReasoning(String.format(
                                "V45 AMSD UNAFFORDABLE: Need %d force for Piett+Executor but only %d available!",
                                amsdMinForceSpec, amsdForceAvailSpec), -9999.0f);
                            logger.warn("V45 AMSD UNAFFORDABLE: Need {} force but only {} — HARD BLOCK!", amsdMinForceSpec, amsdForceAvailSpec);
                            actions.add(action);
                            continue;
                        }
                        // Both confirmed — boost AMSD priority!
                        // V24.15: On turn 1-2, mega-boost to ensure Executor deploys ASAP
                        float amsdBoostSpecific = (currentTurn <= 2) ? 1500.0f : 500.0f;
                        if (currentTurn <= 2) {
                            action.addReasoning("V24.15 AMSD MEGA PRIORITY: Turn " + currentTurn + " — Executor MUST deploy NOW!", amsdBoostSpecific);
                            logger.warn("V24.15 AMSD MEGA PRIORITY (specific): Turn {} — +{} mega-boost!", currentTurn, amsdBoostSpecific);
                        } else {
                            action.addReasoning("V24.10 AMSD APPROVED: Piett + Executor ready!", amsdBoostSpecific);
                            logger.warn("V24.10 AMSD APPROVED: Piett in hand + Executor in reserve — +{}!", amsdBoostSpecific);
                        }
                    }
                    // V24.14: If oracle unavailable, also check hand directly via GameState
                    else if (gameState != null) {
                        // Fallback: scan hand for Executor
                        boolean executorFoundInHand = false;
                        try {
                            for (PhysicalCard hc : gameState.getHand(context.getPlayerId())) {
                                if (hc != null && hc.getTitle() != null &&
                                    hc.getTitle().toLowerCase(java.util.Locale.ROOT).contains("executor")) {
                                    executorFoundInHand = true;
                                    break;
                                }
                            }
                        } catch (Exception e) { /* ignore */ }
                        if (executorFoundInHand) {
                            action.addReasoning("V24.14 AMSD BLOCKED: Executor found in hand — deploy manually!", -9999.0f);
                            logger.warn("V24.14 AMSD FALLBACK: Executor in hand (no oracle) — block AMSD!");
                            actions.add(action);
                            continue;
                        }
                    }
                }
            }

            // ========== V24: TDIGWATT EXHAUSTED SEARCH GUARD ==========
            // TDIGWATT searches for "Cloud City Occupation, Dark Deal, Vader's Bounty, or Bespin".
            // Once all targets have been pulled, every search fails — stop wasting the action.
            if (textLower.contains("cloud city occupation") && textLower.contains("dark deal") &&
                textLower.contains("bespin")) {
                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle tdigOracle = context.getDeckOracle();
                if (tdigOracle != null && tdigOracle.isAnalyzed()) {
                    boolean anyTargetInReserve =
                        tdigOracle.isCardInReserve("Bespin") ||
                        tdigOracle.isCardInReserve("Dark Deal") ||
                        tdigOracle.isCardInReserve("Cloud City Occupation") ||
                        tdigOracle.isCardInReserve("Vader's Bounty");
                    if (!anyTargetInReserve) {
                        action.addReasoning("V24 TDIGWATT: All targets already pulled — search will fail!", -400.0f);
                        logger.warn("V24 TDIGWATT EXHAUSTED: All 4 targets (Bespin, Dark Deal, CC Occupation, Vader's Bounty) already pulled — blocking search!");
                        actions.add(action);
                        continue;
                    } else {
                        logger.info("V24 TDIGWATT: Targets still in reserve — search OK");
                    }
                }
            }

            // ========== V24.6B: I'M SORRY LOCATION PULL — USE UNTIL CC SITES EXHAUSTED ==========
            // I'm Sorry (V) deploys interior Cloud City sites from reserve deck.
            // Use EVERY turn until all CC interior sites are pulled from reserve.
            // DeckOracle tracks what's left — stop wasting the action when reserve is empty.
            if (textLower.contains("sorry") || textLower.contains("i'm sorry") ||
                (textLower.contains("interior") && textLower.contains("cloud city") && textLower.contains("site"))) {
                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer sorryObjAnalyzer =
                    context.getObjectiveAnalyzer();
                if (sorryObjAnalyzer != null && sorryObjAnalyzer.isAnalyzed()
                    && sorryObjAnalyzer.needsBespinSystemPresence()) {
                    // Use DeckOracle to check if any CC interior sites remain in reserve
                    com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle sorryOracle = context.getDeckOracle();
                    boolean ccSitesInReserve = true; // default to true if oracle unavailable
                    if (sorryOracle != null && sorryOracle.isAnalyzed()) {
                        ccSitesInReserve = sorryOracle.isCardInReserve("Cloud City: Upper Walkway")
                            || sorryOracle.isCardInReserve("Cloud City: Carbonite Chamber")
                            || sorryOracle.isCardInReserve("Cloud City: Dining Room")
                            || sorryOracle.isCardInReserve("Cloud City: Lower Corridor")
                            || sorryOracle.isCardInReserve("Cloud City: Security Tower")
                            || sorryOracle.isCardInReserve("Cloud City: West Gallery")
                            || sorryOracle.isCardInReserve("Cloud City: North Corridor")
                            || sorryOracle.isCardInReserve("Cloud City: Platform")
                            || sorryOracle.isCardInReserve("Cloud City: Incinerator")
                            || sorryOracle.isCardInReserve("Cloud City: Guest Quarters")
                            || sorryOracle.isCardInReserve("Cloud City")  // partial match catches any CC site
                            ;
                        logger.warn("V24.6 I'M SORRY: CC interior sites still in reserve? {}", ccSitesInReserve);
                    }
                    if (ccSitesInReserve) {
                        action.addReasoning("V24.6 I'M SORRY: CC sites still in reserve — pull one NOW for more drains + occupation!", 250.0f);
                        logger.warn("V24.6 I'M SORRY: Boosting +250 — CC interior sites available in reserve!");
                    } else {
                        action.addReasoning("V24.6 I'M SORRY: All CC interior sites already pulled — search will fail!", -300.0f);
                        logger.warn("V24.6 I'M SORRY: BLOCKING — no more CC interior sites in reserve deck! (-300)");
                    }
                }
            }

            // ========== V25: WE MUST ACCELERATE OUR PLANS — LOCATIONS ONLY ==========
            // Accelerate Our Plans costs 3 Force to search reserve deck.
            // User says: "Accelerate is for pull locations. Not a huge need to pull effects."
            // The action text tells us what type of card is being pulled:
            //   "plays ...Accelerate Our Plans... to take an Effect of any kind..."
            //   "plays ...Accelerate Our Plans... to take an Interrupt with the word 'Podracer(s)'..."
            //   "plays ...Accelerate Our Plans... to take a location..."
            // Restrict to locations; penalize effects/interrupts heavily.
            if (textLower.contains("accelerate our plans") || textLower.contains("accelerate")) {
                // Check what type of card this pull targets
                boolean isLocationPull = textLower.contains("location") || textLower.contains("site")
                    || textLower.contains("system");
                boolean isEffectPull = textLower.contains("effect");
                boolean isInterruptPull = textLower.contains("interrupt");
                boolean isCharacterPull = textLower.contains("character");
                boolean isStarshipPull = textLower.contains("starship") || textLower.contains("vehicle");

                if (isLocationPull) {
                    // Location pull — check if any locations remain in reserve
                    com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle accelOracle = context.getDeckOracle();
                    if (accelOracle != null && accelOracle.isAnalyzed()) {
                        java.util.List<com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.DeckCard> locsInReserve =
                            accelOracle.getCardsByCategory(com.gempukku.swccgo.common.CardCategory.LOCATION,
                                com.gempukku.swccgo.common.Zone.RESERVE_DECK);
                        if (locsInReserve.isEmpty()) {
                            action.addReasoning("V25 ACCELERATE: No locations in reserve — search will FAIL! Don't waste 3 Force!", -500.0f);
                            logger.warn("V25 ACCELERATE BLOCKED: Location pull but NO locations in reserve! (-500)");
                        } else {
                            action.addReasoning("V25 ACCELERATE: Pull location from reserve — " + locsInReserve.size() + " available!", 100.0f);
                            logger.info("V25 ACCELERATE: Location pull — {} locations in reserve", locsInReserve.size());
                        }
                    } else {
                        // No oracle — default mild positive for location pull
                        action.addReasoning("V25 ACCELERATE: Location pull (no oracle)", 50.0f);
                    }
                } else if (isEffectPull || isInterruptPull) {
                    // Effect or interrupt pull — PENALIZE. User explicitly says don't pull these.
                    // 3 Force for a search that gives opponent a free look at your reserve is wasteful.
                    action.addReasoning("V25 ACCELERATE: DON'T pull " + (isEffectPull ? "effects" : "interrupts")
                        + " — 3 Force wasted! Use Accelerate for LOCATIONS only!", -300.0f);
                    logger.warn("V25 ACCELERATE BLOCKED: {} pull penalized — Accelerate is for locations only! (-300)",
                        isEffectPull ? "Effect" : "Interrupt");
                } else if (isCharacterPull || isStarshipPull) {
                    // Character/starship pulls — mild penalty, not as bad as effects but still not ideal
                    action.addReasoning("V25 ACCELERATE: Prefer location pulls — " +
                        (isCharacterPull ? "character" : "starship") + " pull is suboptimal", -100.0f);
                    logger.info("V25 ACCELERATE: {} pull penalized (-100)", isCharacterPull ? "Character" : "Starship");
                } else {
                    // Unknown pull type — mild penalty
                    action.addReasoning("V25 ACCELERATE: Unknown pull type — prefer locations", -50.0f);
                }
            }

            // ========== V25: CRUSH THE REBELLION — CHECK RESERVE FOR TARGETS ==========
            // Crush The Rebellion (once per turn) searches reserve for I Have You Now or Evader.
            // If neither card is in reserve, the search fails and gives opponent a free look.
            // Use DeckOracle to check before wasting the action.
            if (textLower.contains("crush the rebellion") || textLower.contains("crush rebellion")) {
                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle crushOracle = context.getDeckOracle();
                if (crushOracle != null && crushOracle.isAnalyzed()) {
                    boolean hasTarget = crushOracle.isCardInReserve("I Have You Now")
                        || crushOracle.isCardInReserve("Evader");
                    if (!hasTarget) {
                        action.addReasoning("V25 CRUSH: No I Have You Now or Evader in reserve — search will FAIL! Stop wasting!", -400.0f);
                        logger.warn("V25 CRUSH BLOCKED: No targets in reserve — don't activate! (-400)");
                    } else {
                        action.addReasoning("V25 CRUSH: Target available in reserve — pull it!", 80.0f);
                        logger.info("V25 CRUSH: I Have You Now or Evader available in reserve");
                    }
                }
            }

            // ========== V37: I AM YOUR FATHER — DECKORACLE ZONE CHECK ==========
            // IAYF can pull Vader's Lightsaber from Reserve (free) or Lost Pile (lose 1 Force).
            // Use DeckOracle to verify the lightsaber is actually in the target zone.
            // Failed searches give opponent free intel about our deck composition.
            if (textLower.contains("i am your father") && textLower.contains("lightsaber")) {
                boolean pullFromReserve = textLower.contains("reserve");
                boolean pullFromLost = textLower.contains("lost");
                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle iayOracle = context.getDeckOracle();
                boolean saberInReserve = false;
                boolean saberInLost = false;
                if (iayOracle != null && iayOracle.isAnalyzed()) {
                    saberInReserve = iayOracle.isCardInReserve("Darth Vader's Lightsaber");
                    saberInLost = iayOracle.isCardLost("Darth Vader's Lightsaber");
                }
                if (pullFromReserve && !saberInReserve) {
                    action.addReasoning("V37 IAYF: Lightsaber NOT in Reserve — WILL FAIL and gives opponent deck intel!", -600.0f);
                    logger.warn("V37 IAYF BLOCKED: Saber not in reserve (in lost={})", saberInLost);
                } else if (pullFromLost && !saberInLost) {
                    action.addReasoning("V37 IAYF: Lightsaber NOT in Lost Pile — check Reserve instead.", -400.0f);
                } else {
                    // Saber IS in target zone — check if Vader needs it
                    boolean vaderArmed = false;
                    try {
                        String iayPid = context.getPlayerId();
                        for (PhysicalCard tc : gameState.getAllPermanentCards()) {
                            if (tc == null || !iayPid.equals(tc.getOwner())) continue;
                            if (tc.getBlueprint() == null) continue;
                            String tcTitle = tc.getTitle() != null ? tc.getTitle().toLowerCase(Locale.ROOT) : "";
                            if (!tcTitle.contains("vader") || tc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                            com.gempukku.swccgo.common.Zone tcZ = tc.getZone();
                            if (tcZ == null || !tcZ.isInPlay()) continue;
                            java.util.List<PhysicalCard> atts = gameState.getAttachedCards(tc);
                            if (atts != null) {
                                for (PhysicalCard att : atts) {
                                    if (att != null && att.getBlueprint() != null
                                        && att.getBlueprint().getCardCategory() == CardCategory.WEAPON) {
                                        vaderArmed = true;
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    } catch (Exception e) { /* ignore */ }
                    if (!vaderArmed) {
                        action.addReasoning(String.format("V37 IAYF: Vader UNARMED — retrieve lightsaber from %s NOW!",
                            pullFromLost ? "Lost Pile" : "Reserve"), 600.0f);
                    } else {
                        action.addReasoning("V37 IAYF: Vader armed — spare lightsaber", 50.0f);
                    }
                }
            } else if (textLower.contains("i am your father") && (textLower.contains("reserve") || textLower.contains("take"))) {
                // Non-lightsaber IAYF search — basic reserve size check
                int reserveSize = gameState != null ? gameState.getReserveDeckSize(context.getPlayerId()) : 10;
                if (reserveSize <= 2) {
                    action.addReasoning("V37 IAYF: Reserve nearly empty (" + reserveSize + ") — search gives opponent intel!", -200.0f);
                }
            }

            // ========== V35: HATRED CARD — CANCEL OPPONENT GAME TEXT ==========
            // Stacking a Hatred Card on an opponent's character cancels their game text.
            // This is CRITICAL because it removes attrition immunity and other protections.
            // Without Hatred, winning a battle does NOTHING if opponent is immune to attrition.
            // Action text variants:
            //   "Stack a 'Hatred Card'" (previous game)
            //   "USED: Stack 'Hatred' card on opponent's character" (this game)
            // BEST TIMING: Deploy phase — stack Hatred BEFORE initiating battle.
            // This way opponent's immunities are already gone when battle starts.
            if (textLower.contains("hatred")) {
                // V37.1: Only place hatred on OUR turn
                if (gameState != null && !context.isMyTurn()) {
                    action.addReasoning("V37.1 HATRED: Not our turn — save for deploy phase!", -600.0f);
                } else {

                String decisionText = context.getDecisionText() != null
                    ? context.getDecisionText().toLowerCase(Locale.ROOT) : "";
                boolean isDeployPhase = context.getPhase() == Phase.DEPLOY
                    || decisionText.contains("deploy");
                boolean isBattlePhase = context.getPhase() == Phase.BATTLE
                    || decisionText.contains("battle") || decisionText.contains("weapons segment");

                // V35.3: STRICT hatred scoring — ONLY place hatred when Vader or Inquisitor
                // is at the SAME SITE as an opponent character.
                boolean v35VaderOrInqWithOpponents = false;
                boolean v35InqOnTable = false;
                boolean v35JediAtSameSite = false;
                try {
                    if (gameState != null) {
                        String v35Pid = context.getPlayerId();
                        String v35Oid = gameState.getOpponent(v35Pid);
                        for (PhysicalCard tCard : gameState.getAllPermanentCards()) {
                            if (tCard == null || !v35Pid.equals(tCard.getOwner())) continue;
                            if (tCard.getBlueprint() == null) continue;
                            if (tCard.getBlueprint().getCardCategory() != com.gempukku.swccgo.common.CardCategory.CHARACTER) continue;
                            com.gempukku.swccgo.common.Zone tz = tCard.getZone();
                            if (tz == null || !tz.isInPlay()) continue;
                            String tTitle = tCard.getTitle() != null ? tCard.getTitle().toLowerCase(Locale.ROOT) : "";
                            // V35.7: Inquisitor ONLY (not Vader) — hatred requires "your Inquisitor"
                            if (isInquisitor(tTitle)) {
                                v35InqOnTable = true;
                                PhysicalCard charLoc = tCard.getAtLocation();
                                if (charLoc != null) {
                                    float oppPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, charLoc, v35Oid, false, false);
                                    if (oppPower > 0) {
                                        v35VaderOrInqWithOpponents = true;
                                        for (PhysicalCard lc : gameState.getCardsAtLocation(charLoc)) {
                                            if (lc == null || !v35Oid.equals(lc.getOwner())) continue;
                                            String lcT = lc.getTitle() != null ? lc.getTitle().toLowerCase(Locale.ROOT) : "";
                                            if (isJediOrPadawan(lcT)) { v35JediAtSameSite = true; break; }
                                        }
                                    }
                                }
                                if (v35VaderOrInqWithOpponents) break;
                            }
                        }
                    }
                } catch (Exception e) { /* ignore */ }

                if (!v35InqOnTable) {
                    action.addReasoning("V35.7 HATRED: No Inquisitor on table — hatred requires Inquisitor!", -500.0f);
                    logger.warn("V35.7 HATRED: No Inquisitor — hard block (-500)");
                } else if (v35VaderOrInqWithOpponents) {
                    float hatredScore = isDeployPhase ? (float) ChosenOneConfig.SCORE_HATRED_WITH_INQUISITOR : 350.0f;
                    if (v35JediAtSameSite) hatredScore += 150.0f;
                    action.addReasoning(String.format(
                        "V35.3 HATRED: Vader/Inquisitor WITH opponents%s — cancel game text! (+%.0f)",
                        v35JediAtSameSite ? " + JEDI" : "", hatredScore), hatredScore);
                    logger.warn("V35.3 HATRED: Vader/Inq with opponents (jedi={}) — score +{}",
                        v35JediAtSameSite, (int)hatredScore);
                } else {
                    action.addReasoning("V35.3 HATRED: Vader/Inquisitor not at same site as opponents — save!", -300.0f);
                    logger.warn("V35.3 HATRED: No co-location — blocked (-300)");
                }
            } // end V37.1 isMyTurn else
            }

            // ========== V35: FEEL MY FATHER'S DEADLY TOUCH (FMFTD) ==========
            // FMFTD has LOST mode (add battle destiny) and USED mode (place hatred).
            // Critical card for Inquisitor synergy with hatred and Jedi presence.
            if (textLower.contains("feel my father") || textLower.contains("fmftd")
                || textLower.contains("deadly touch")) {
                boolean isFmftdBattle = context.getPhase() == Phase.BATTLE;
                boolean isFmftdUsedMode = textLower.contains("stack") || textLower.contains("hatred")
                    || textLower.contains("used");
                boolean isFmftdLostMode = textLower.contains("destiny") || textLower.contains("lost")
                    || textLower.contains("add");

                if (isFmftdLostMode || isFmftdBattle) {
                    // LOST mode — check for Inquisitor/Jedi/Hatred synergy
                    boolean v35FmInq = false;
                    boolean v35FmJedi = false;
                    boolean v35FmHatred = false;
                    try {
                        if (gameState != null && gameState.getBattleState() != null) {
                            PhysicalCard fmBattleLoc = gameState.getBattleState().getBattleLocation();
                            if (fmBattleLoc != null) {
                                String fmPid = context.getPlayerId();
                                String fmOid = gameState.getOpponent(fmPid);
                                for (PhysicalCard bc : gameState.getCardsAtLocation(fmBattleLoc)) {
                                    if (bc == null) continue;
                                    String bcTitle = bc.getTitle() != null ? bc.getTitle().toLowerCase(Locale.ROOT) : "";
                                    if (fmPid.equals(bc.getOwner()) && isInquisitor(bcTitle)) v35FmInq = true;
                                    if (fmOid != null && fmOid.equals(bc.getOwner())) {
                                        if (isJediOrPadawan(bcTitle)) v35FmJedi = true;
                                        java.util.List<PhysicalCard> st = gameState.getStackedCards(bc);
                                        if (st != null && !st.isEmpty()) v35FmHatred = true;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) { /* ignore */ }

                    int synCount = (v35FmInq ? 1 : 0) + (v35FmJedi ? 1 : 0) + (v35FmHatred ? 1 : 0);
                    if (synCount >= 3) {
                        action.addReasoning("V35 FMFTD LOST: Inquisitor + Jedi + Hatred — ADD 2 BATTLE DESTINY!", (float) ChosenOneConfig.SCORE_FMFTD_FULL_SYNERGY);
                        logger.warn("V35 FMFTD: Full synergy! +{}", ChosenOneConfig.SCORE_FMFTD_FULL_SYNERGY);
                    } else if (synCount >= 2) {
                        action.addReasoning("V35 FMFTD LOST: Inquisitor with Jedi or Hatred — add 1 battle destiny!", 350.0f);
                    } else if (v35FmInq) {
                        action.addReasoning("V35 FMFTD LOST: Inquisitor in battle — add destiny!", 200.0f);
                    } else {
                        action.addReasoning("V35 FMFTD LOST: No Inquisitor in battle — limited value", 50.0f);
                    }
                } else if (isFmftdUsedMode) {
                    // USED mode — place hatred card
                    if (context.getPhase() == Phase.DEPLOY || context.getPhase() == Phase.MOVE) {
                        action.addReasoning("V35 FMFTD USED: Place hatred on opponent — cancel game text!", 350.0f);
                    } else {
                        action.addReasoning("V35 FMFTD USED: Place hatred — decent timing", 150.0f);
                    }
                } else if (isFmftdBattle) {
                    // Generic FMFTD during battle — likely the LOST mode
                    action.addReasoning("V35 FMFTD: Play during battle for extra destiny!", 250.0f);
                } else {
                    action.addReasoning("V35 FMFTD: Save for battle if possible", -100.0f);
                }
            }

            // ========== V35: VADER SELF-RECALL (Hunt Down V once-per-game) ==========
            // "Take Vader into hand" — allows redeploying Vader to hunt Jedi elsewhere
            // "Return an Inquisitor here to hand" — Eighth Brother repositioning
            else if (textLower.contains("take vader into hand") || (textLower.contains("return") && textLower.contains("inquisitor") && textLower.contains("hand"))) {
                if (textLower.contains("vader")) {
                    // Vader self-recall — check if there are Jedi elsewhere to hunt
                    boolean v35JediElsewhere = false;
                    try {
                        if (gameState != null) {
                            String v35Oid = gameState.getOpponent(context.getPlayerId());
                            for (PhysicalCard loc : gameState.getTopLocations()) {
                                if (loc == null) continue;
                                for (PhysicalCard c : gameState.getCardsAtLocation(loc)) {
                                    if (c == null || !v35Oid.equals(c.getOwner())) continue;
                                    String ct = c.getTitle() != null ? c.getTitle().toLowerCase(Locale.ROOT) : "";
                                    if (isJediOrPadawan(ct)) { v35JediElsewhere = true; break; }
                                }
                                if (v35JediElsewhere) break;
                            }
                        }
                    } catch (Exception e) { /* ignore */ }

                    if (v35JediElsewhere) {
                        action.addReasoning("V35 VADER RECALL: Take Vader into hand — Jedi elsewhere to hunt! Redeploy!", 300.0f);
                        logger.warn("V35 VADER RECALL: Jedi detected elsewhere — recalling Vader to redeploy (+300)");
                    } else {
                        action.addReasoning("V35 VADER RECALL: Take Vader into hand — no clear target, keep him deployed", -100.0f);
                    }
                } else {
                    // V35.1: Inquisitor recall — DON'T recall if opponents are nearby!
                    // Eighth Brother's ability returns an Inquisitor to hand. Only do this
                    // if there are NO opponents at adjacent sites. If opponents are nearby,
                    // keep the Inquisitor to fight!
                    boolean opponentsNearby = false;
                    try {
                        if (gameState != null) {
                            String recallPid = context.getPlayerId();
                            String recallOid = gameState.getOpponent(recallPid);
                            for (PhysicalCard loc : gameState.getTopLocations()) {
                                if (loc == null) continue;
                                float oppPwr = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, loc, recallOid, false, false);
                                if (oppPwr > 0) { opponentsNearby = true; break; }
                            }
                        }
                    } catch (Exception e) { /* ignore */ }

                    if (opponentsNearby) {
                        action.addReasoning("V35.1 INQUISITOR RECALL BLOCK: Opponents on the board — KEEP Inquisitor to fight!", -400.0f);
                        logger.warn("V35.1 INQUISITOR RECALL BLOCKED: Opponents present — don't pull back (-400)");
                    } else {
                        action.addReasoning("V35 INQUISITOR RECALL: No opponents on board — safe to reposition", 100.0f);
                    }
                }
            }

            // ========== V24.9: MASTERFUL MOVE EARLY-GAME GUARD ==========
            // Masterful Move searches reserve for Ghhhk (damage cancel combo card).
            // On turns 1-3, force should go to deploying Executor + characters, NOT searching for Ghhhk.
            // Only play Masterful Move when characters are on the table and need protecting.
            if (textLower.contains("masterful move")) {
                int mmTurn = context.getTurnNumber();
                boolean hasCharsOnTable = false;
                if (gameState != null) {
                    try {
                        for (PhysicalCard loc : gameState.getLocationsInOrder()) {
                            java.util.List<PhysicalCard> cardsHere = gameState.getCardsAtLocation(loc);
                            if (cardsHere != null) {
                                for (PhysicalCard c : cardsHere) {
                                    if (c != null && context.getPlayerId().equals(c.getOwner()) &&
                                        c.getBlueprint() != null &&
                                        c.getBlueprint().getCardCategory() == com.gempukku.swccgo.common.CardCategory.CHARACTER) {
                                        hasCharsOnTable = true;
                                        break;
                                    }
                                }
                            }
                            if (hasCharsOnTable) break;
                        }
                    } catch (Exception e) {
                        logger.debug("V24.9 MM guard: Error scanning for characters: {}", e.getMessage());
                    }
                }
                if (!hasCharsOnTable) {
                    action.addReasoning("V24.9 MASTERFUL MOVE: No characters on table — Ghhhk has nothing to protect! Save force for deployment!", -500.0f);
                    logger.warn("V24.9 MASTERFUL MOVE: BLOCKED — no characters on table, save force for Executor! (-500)");
                } else if (mmTurn <= 2) {
                    action.addReasoning("V24.9 MASTERFUL MOVE: Too early (turn " + mmTurn + ") — prioritize getting Executor out!", -300.0f);
                    logger.warn("V24.9 MASTERFUL MOVE: Penalized on turn {} — save force for Executor deployment! (-300)", mmTurn);
                }
            }

            // ========== Capacity Slot Selection (Pilot vs Passenger) ==========
            if (textLower.contains("capacity slot")) {
                if (textLower.contains("pilot capacity slot")) {
                    action.setScore(100.0f);
                    action.addReasoning("Pilot slot adds power to ship!", 100.0f);
                    action.setActionType(ActionType.MOVE);
                    logger.info("PILOT SLOT: Strongly preferring pilot capacity (+100)");
                } else if (textLower.contains("passenger capacity slot")) {
                    action.setScore(VERY_BAD_DELTA);
                    action.addReasoning("Passenger gives NO power bonus!", VERY_BAD_DELTA);
                    action.setActionType(ActionType.MOVE);
                    logger.warn("PASSENGER SLOT: Penalizing - no power contribution ({})", VERY_BAD_DELTA);
                }
                actions.add(action);
                continue;
            }

            // ========== V29.15 Epic Event Saga Choice ==========
            // "The Force Is Strong In My Family" presents choices:
            //   "My Father Has It", "I Have It", "You Have That Power, Too"
            // The correct choice depends on the deck name:
            //   Luke deck → "I Have It"
            //   Anakin deck → "My Father Has It"
            //   Rey deck → "You Have That Power, Too"
            if (textLower.contains("i have it") || textLower.contains("my father has it")
                || textLower.contains("you have that power")) {
                String deckName = context.getDeckName();
                String deckLower = (deckName != null) ? deckName.toLowerCase(java.util.Locale.ROOT) : "";
                boolean isCorrectChoice = false;

                if (deckLower.contains("luke") && textLower.contains("i have it")
                    && !textLower.contains("my father has it")) {
                    isCorrectChoice = true;
                } else if (deckLower.contains("anakin") && textLower.contains("my father has it")) {
                    isCorrectChoice = true;
                } else if (deckLower.contains("rey") && textLower.contains("you have that power")) {
                    isCorrectChoice = true;
                }

                if (isCorrectChoice) {
                    action.addReasoning("V29.15 EPIC EVENT: Correct saga choice for '" + deckName + "' deck!", 1000.0f);
                    logger.warn("V29.15 EPIC EVENT: Choosing '{}' — correct for deck '{}'", actionText, deckName);
                } else if (!deckLower.isEmpty()) {
                    action.addReasoning("V29.15 EPIC EVENT: Wrong saga choice for '" + deckName + "' deck", -500.0f);
                    logger.warn("V29.15 EPIC EVENT: Penalizing '{}' — wrong for deck '{}'", actionText, deckName);
                } else {
                    // No deck name available — default to "I Have It" (most common Luke deck)
                    if (textLower.contains("i have it") && !textLower.contains("my father has it")) {
                        action.addReasoning("V29.15 EPIC EVENT: Default to 'I Have It' (no deck name)", 500.0f);
                        logger.warn("V29.15 EPIC EVENT: No deck name — defaulting to 'I Have It'");
                    }
                }
                actions.add(action);
                continue;
            }

            // ========== Force Activation ==========
            if (actionText.equals("Activate Force")) {
                action.setActionType(ActionType.ACTIVATE_FORCE);
                evaluateActivateForce(action, context);
            }

            // ========== V53b: STACK JEDI HERE — Save Jedi Survivors ==========
            // Fallen Order lets you lose 1 force to stack a Jedi Survivor back on it,
            // saving them from being lost. ALWAYS do this — losing 1 force to save a
            // Jedi is the best trade in the game. They can redeploy next turn.
            else if (textLower.contains("stack") && textLower.contains("here")
                     && (textLower.contains("jedi") || textLower.contains("obi-wan")
                         || textLower.contains("quinlan") || textLower.contains("kelleran")
                         || textLower.contains("cal kestis") || textLower.contains("ezra")
                         || textLower.contains("ahsoka") || textLower.contains("cere")
                         || textLower.contains("sabine") || textLower.contains("luke"))) {
                action.addReasoning("V53b SAVE JEDI: Stack Jedi on Fallen Order — lose 1 force to save them!", 500.0f);
                logger.warn("V53b SAVE JEDI: '{}' — +500, always save Jedi Survivors!", actionText);
            }

            // ========== V53: BLOCK WOKLING EFFECT SEARCH ==========
            // Wokling (V) costs 3 Force to search for an Effect from Reserve Deck.
            // This wastes force — the search often fails (no valid targets) and even
            // when it succeeds, 3 force is better spent deploying characters.
            // Block Wokling from searching for effects entirely.
            else if (textLower.contains("effect") && textLower.contains("reserve deck")
                     && textLower.contains("deploy cost")) {
                // Check if source card is Wokling
                boolean isWokling = textLower.contains("wokling");
                if (!isWokling && cardId != null && gameState != null) {
                    try {
                        PhysicalCard wokSrc = gameState.findCardById(Integer.parseInt(cardId));
                        if (wokSrc != null && wokSrc.getTitle() != null
                            && wokSrc.getTitle().toLowerCase().contains("wokling")) {
                            isWokling = true;
                        }
                    } catch (Exception e) { /* ignore */ }
                }
                if (isWokling) {
                    action.addReasoning("V53 BLOCK WOKLING: Don't waste 3 force searching for effects!", -9999.0f);
                    logger.warn("V53 WOKLING BLOCKED: Wokling Effect search — 3 force wasted, HARD BLOCK!");
                } else {
                    action.addReasoning("Search for Effect from Reserve Deck", GOOD_DELTA);
                }
            }

            // ========== Force Drain ==========
            else if (actionText.equals("Force drain")) {
                action.setActionType(ActionType.FORCE_DRAIN);
                evaluateForceDrain(action, context, cardId);
            }

            // ========== Race Destiny ==========
            else if (actionText.equals("Draw race destiny")) {
                action.setActionType(ActionType.RACE_DESTINY);
                action.addReasoning("Race destiny always high priority", VERY_GOOD_DELTA);
            }

            // ========== Play a Card ==========
            else if (actionText.equals("Play a card")) {
                action.setActionType(ActionType.PLAY_CARD);
                evaluatePlayCard(action, context);
            }

            // ========== Fire Weapons ==========
            else if (actionText.contains("Fire")) {
                action.setActionType(ActionType.FIRE_WEAPON);
                // Check if there are valid (non-HIT) targets before firing
                // Ported from Python action_text_evaluator.py - don't fire at already-hit targets
                boolean hasValidTargets = checkForValidWeaponTargets(context);
                if (hasValidTargets) {
                    action.addReasoning("Firing weapons at valid targets", VERY_GOOD_DELTA);
                } else {
                    action.addReasoning("All targets already HIT - save weapon", BAD_DELTA);
                    logger.debug("Skipping weapon fire - no valid (unhit) targets");
                }
            }

            // ========== Add Battle Destiny ==========
            else if (textLower.contains("add") && textLower.contains("battle destiny")) {
                action.setActionType(ActionType.BATTLE_DESTINY);
                action.addReasoning("Adding battle destiny is great", VERY_GOOD_DELTA);
            }

            // ========== Battle Destiny Modifier (+1 to battle destiny) ==========
            else if ((actionText.contains("+1") || actionText.contains("+ 1") || textLower.contains("add 1"))
                     && textLower.contains("battle destiny")) {
                action.setActionType(ActionType.BATTLE_DESTINY);
                action.addReasoning("+1 to battle destiny - always use!", VERY_GOOD_DELTA);
            }

            // ========== V24.2: Force Drain Modifier (+1 to force drain) ==========
            // Cards like Lord Maul With Lightsaber add +1 to force drain as an optional response.
            // This should ALWAYS be accepted — free extra damage!
            else if ((actionText.contains("+1") || actionText.contains("+ 1") || textLower.contains("add 1"))
                     && textLower.contains("force drain")) {
                action.setActionType(ActionType.FORCE_DRAIN);
                action.addReasoning("V24.2 FORCE DRAIN BONUS: +1 to force drain — always use!", VERY_GOOD_DELTA + 30.0f);
                logger.warn("V24.2 DRAIN BONUS: Accepting +1 force drain — '{}'", actionText);
            }

            // ========== Weapon Destiny Modifier ==========
            else if (textLower.contains("weapon destiny") &&
                     (actionText.contains("+3") || actionText.contains("+2") || textLower.contains("add"))) {
                action.setActionType(ActionType.FIRE_WEAPON);
                action.addReasoning("Boost weapon destiny - increases hit chance!", VERY_GOOD_DELTA);
            }

            // ========== Protect Battle Destiny Draws ==========
            else if (textLower.contains("prevent") && textLower.contains("cancel") &&
                     textLower.contains("battle destiny") && textLower.contains("draw")) {
                action.setActionType(ActionType.BATTLE_DESTINY);
                evaluateDestinyProtection(action, context);
            }

            // ========== Prevent Opponent Adding Battle Destiny ==========
            else if (textLower.contains("prevent") && textLower.contains("battle destiny") &&
                     !textLower.contains("cancel")) {
                action.setActionType(ActionType.BATTLE_DESTINY);
                action.addReasoning("Prevent opponent battle destiny - denies their draw!", VERY_GOOD_DELTA);
            }

            // ========== Take Admiral/General Into Hand ==========
            // For TDIGWATT/Bespin objectives: an Imperial admiral pulled here is likely
            // a pilot (e.g., Admiral Chiraneau). That pilot enables deploying the Executor
            // to Bespin cheaply — the Executor + pilot simultaneous deploy is the critical
            // Turn 1 play for Cloud City objectives. Prioritise VERY highly when we have
            // no ship at Bespin yet.
            else if (textLower.contains("take") && textLower.contains("into hand") &&
                     (textLower.contains("admiral") || textLower.contains("general"))) {
                // Check if we're running a Bespin/Cloud City objective with no ship there yet
                boolean bespinChainActive = false;
                try {
                    com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer objAnalyzer =
                        context.getObjectiveAnalyzer();
                    if (objAnalyzer != null && objAnalyzer.isAnalyzed() &&
                        objAnalyzer.needsBespinSystemPresence()) {
                        // Check if we already have a ship at Bespin system
                        boolean hasBespinShip = false;
                        if (gameState != null) {
                            String pid = context.getPlayerId();
                            for (com.gempukku.swccgo.game.PhysicalCard loc : gameState.getLocationsInOrder()) {
                                if (loc != null && loc.getTitle() != null &&
                                    loc.getTitle().toLowerCase(java.util.Locale.ROOT).contains("bespin") &&
                                    loc.getBlueprint() != null &&
                                    loc.getBlueprint().getCardSubtype() ==
                                        com.gempukku.swccgo.common.CardSubtype.SYSTEM) {
                                    float ourPower = context.getGame().getModifiersQuerying()
                                        .getTotalPowerAtLocation(gameState, loc, pid, false, false);
                                    if (ourPower > 0) hasBespinShip = true;
                                    break;
                                }
                            }
                        }
                        if (!hasBespinShip) {
                            bespinChainActive = true;
                        }
                    }
                } catch (Exception e) {
                    // Ignore — fall back to default scoring
                }

                if (bespinChainActive) {
                    // Admiral pilot → Executor chain: this is Turn 1 critical for TDIGWATT.
                    // Score it as high as AMSD itself so we never skip this pull.
                    action.addReasoning(
                        "CRITICAL: Admiral pilot enables Executor deploy to Bespin — must pull T1!", 200.0f);
                    logger.warn("EXECUTOR CHAIN: Admiral pull with no Bespin ship — boosting to 200 (enables Executor pipeline)");
                } else {
                    action.addReasoning("Retrieve admiral/general into hand", GOOD_DELTA);
                }
            }

            // ========== Substitute Destiny ==========
            else if (textLower.contains("substitute destiny")) {
                action.setActionType(ActionType.SUBSTITUTE_DESTINY);
                action.addReasoning("Substituting destiny is good", GOOD_DELTA);
            }

            // ========== React ==========
            else if (textLower.contains("react")) {
                action.setActionType(ActionType.REACT);
                action.addReasoning("Avoid reacts (bot doesn't understand timing)", BAD_DELTA);
            }

            // ========== Steal ==========
            else if (textLower.contains("steal")) {
                action.setActionType(ActionType.STEAL);
                action.addReasoning("Stealing is good", GOOD_DELTA);
            }

            // ========== Sabacc ==========
            else if (textLower.contains("play sabacc")) {
                action.setActionType(ActionType.SABACC);
                action.addReasoning("Playing sabacc", GOOD_DELTA);
            }

            // ========== Cancel Own Cards (Bad!) ==========
            else if (textLower.contains("cancel your")) {
                action.setActionType(ActionType.CANCEL);
                action.addReasoning("Never cancel own cards", VERY_BAD_DELTA);
            }

            // ========== Cancel Opponent's Interrupt (Sense/Control) ==========
            else if (textLower.contains("cancel") &&
                     (textLower.contains("interrupt") || textLower.contains("sense") ||
                      textLower.contains("alter") || textLower.contains("effect") ||
                      textLower.contains("force drain")) &&
                     !textLower.contains("your")) {
                action.setActionType(ActionType.CANCEL);
                evaluateSenseCancel(action, context, actionText);
            }

            // ========== Cancel/Redraw Destiny ==========
            else if (textLower.contains("cancel and redraw") && textLower.contains("destiny")) {
                action.addReasoning("Redraw destiny (current may be low)", GOOD_DELTA);
            }

            // ========== Cancel Weapon Targeting ==========
            else if (textLower.contains("cancel") && textLower.contains("weapon") && textLower.contains("target")) {
                action.setActionType(ActionType.CANCEL);
                action.addReasoning("Cancel weapon targeting - protect our characters!", VERY_GOOD_DELTA);
            }

            // ========== Immune to Attrition ==========
            else if (textLower.contains("immune to attrition")) {
                action.addReasoning("Make character immune to attrition - valuable protection!", VERY_GOOD_DELTA);
            }

            // ========== Protect Forfeit ==========
            else if (textLower.contains("forfeit") &&
                     (textLower.contains("protect") || textLower.contains("preserved"))) {
                action.addReasoning("Protect forfeit value during battle", GOOD_DELTA + 10.0f);
            }

            // ========== Re-target Weapon ==========
            else if (textLower.contains("re-target") || textLower.contains("retarget")) {
                action.addReasoning("Re-target weapon at enemy - turn their weapon against them!", VERY_GOOD_DELTA);
            }

            // ========== Cancel Battle Damage (Houjix/Ghhhk) ==========
            else if (actionText.contains("Cancel all remaining battle damage")) {
                action.setActionType(ActionType.CANCEL_DAMAGE);
                evaluateHoujixGhhhk(action, context);
            }

            // ========== V67af: RETURN-OWN-CHARACTER-TO-HAND BOUNCE BLOCK ==========
            // Steve's report: Rando deploys General Grievous, then uses Grievous's
            // 'Lose 1 Force to return Grievous to hand' game text to bounce him —
            // wasting both the deploy cost AND the bounce cost. V29.7 BOUNCE only
            // fires for 'Take X into hand' actions; Grievous and similar cards say
            // 'Return X to hand', which V29.7 misses entirely.
            //
            // Rule: when an action says 'Return <X> to hand' AND the source card is
            // a character we own AND the action requires losing force, hard-block.
            // The tactical use case (escape death) is too rare to justify Rando's
            // pattern of deploy-then-bounce loops.
            else if (textLower.contains("return") && textLower.contains("to hand")
                    && cardId != null && gameState != null && context.getPlayerId() != null) {
                boolean v67afBlock = false;
                String v67afDetail = null;
                try {
                    PhysicalCard srcPc = gameState.findCardById(Integer.parseInt(cardId));
                    if (srcPc != null && srcPc.getBlueprint() != null
                            && context.getPlayerId().equals(srcPc.getOwner())
                            && srcPc.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                        v67afBlock = true;
                        v67afDetail = srcPc.getTitle();
                    }
                } catch (Exception e) { /* ignore */ }
                if (v67afBlock) {
                    action.addReasoning(String.format(
                        "V67af RETURN-TO-HAND BLOCK: bouncing %s wastes deploy cost — DON'T undo your deploy!",
                        v67afDetail), -9999.0f);
                    logger.warn("V67af RETURN BLOCK: source={} action='{}' — HARD BLOCK (-9999)",
                        v67afDetail, actionText);
                } else {
                    // Default: still discourage but lighter touch (handles edge cases
                    // like opponent-effect-induced returns we haven't classified yet).
                    action.addReasoning("V67af RETURN-TO-HAND: unclassified return action — light penalty",
                        -150.0f);
                    logger.info("V67af RETURN-TO-HAND: '{}' source unclassifiable — -150",
                        actionText);
                }
            }

            // ========== V67an (Steve, 2026-05-07): WEAPON SWAP TO FREE MATCHING SLOT ==========
            //
            // Steve's rule: if Rando has a non-unique/non-matching weapon attached to
            // a character (e.g., generic Dark Jedi Lightsaber on Vader) AND has a
            // unique persona-matched weapon for that character in hand (e.g., Vader's
            // Lightsaber), Rando should TRANSFER the wrong weapon to a buddy at the
            // same site. After the transfer the matching character is unarmed, so the
            // V67ad two-weapon hard-block lifts and the matching unique weapon can
            // deploy on its persona — net result: 2 characters armed, persona bonuses
            // active for the matching weapon (immune, fire-for-free, +power, etc.).
            //
            // Detection: action text starts with "Transfer" (rules-level transfer) or
            // contains "Transfer device" / "Transfer weapon".
            //
            // Bonus +400 fires when:
            //   - The transfer source weapon is NOT unique OR has no matchingCharacter
            //     filter pointing at its current attachee
            //   - Rando has another weapon in hand whose matchingCharacter filter
            //     DOES target the current attachee (or whose title matches the persona)
            //
            // If we can't determine matchingCharacter unambiguously, fall back to
            // a milder +150 ('transfers usually mean tactical swap').
            else if (actionText.contains("Transfer")
                    && (actionText.contains("weapon") || actionText.contains("device")
                        || textLower.startsWith("transfer "))) {
                action.setActionType(ActionType.UNKNOWN);
                float v67anBonus = 150.0f;
                String v67anReason = "transfer action — usually a tactical swap";
                try {
                    if (cardId != null && gameState != null && context.getPlayerId() != null) {
                        PhysicalCard transferSrc = gameState.findCardById(Integer.parseInt(cardId));
                        if (transferSrc != null && transferSrc.getBlueprint() != null
                                && transferSrc.getBlueprint().getCardCategory() == CardCategory.WEAPON) {
                            // Identify current attachee
                            PhysicalCard attachee = transferSrc.getAttachedTo();
                            if (attachee != null && attachee.getBlueprint() != null
                                    && context.getPlayerId().equals(attachee.getOwner())) {
                                String attacheeTitleLower = attachee.getTitle() != null
                                    ? attachee.getTitle().toLowerCase(java.util.Locale.ROOT) : "";
                                // Is the current weapon non-unique OR not matched to attachee?
                                boolean weaponIsNonUnique = transferSrc.getBlueprint().getUniqueness()
                                    != com.gempukku.swccgo.common.Uniqueness.UNIQUE;
                                boolean weaponMatchesAttachee = false;
                                try {
                                    com.gempukku.swccgo.filters.Filter mcFilter =
                                        transferSrc.getBlueprint().getMatchingCharacterFilter();
                                    if (mcFilter != null) {
                                        weaponMatchesAttachee = mcFilter.accepts(context.getGame(), attachee);
                                    }
                                } catch (Exception e) { /* ignore */ }

                                // Do we have a UNIQUE matching weapon for the attachee in hand?
                                // Steve's clarification: only swap when a UNIQUE persona-matched
                                // weapon is waiting (e.g. Ahsoka's Shoto Lightsaber for Ahsoka,
                                // Luke's Hunting Rifle for Luke). Generic-for-generic swaps don't
                                // gain anything.
                                boolean haveMatchingInHand = false;
                                String matchingTitle = null;
                                try {
                                    for (PhysicalCard hc : gameState.getHand(context.getPlayerId())) {
                                        if (hc == null || hc.getBlueprint() == null) continue;
                                        if (hc.getBlueprint().getCardCategory() != CardCategory.WEAPON) continue;
                                        // STRICT: only consider UNIQUE weapons.
                                        if (hc.getBlueprint().getUniqueness()
                                                != com.gempukku.swccgo.common.Uniqueness.UNIQUE) continue;
                                        // Persona-match the hand weapon to the attachee.
                                        try {
                                            com.gempukku.swccgo.filters.Filter handMc =
                                                hc.getBlueprint().getMatchingCharacterFilter();
                                            if (handMc != null
                                                    && handMc.accepts(context.getGame(), attachee)) {
                                                haveMatchingInHand = true;
                                                matchingTitle = hc.getTitle();
                                                break;
                                            }
                                        } catch (Exception e) { /* ignore */ }
                                        // Persona-name fallback for unique weapons whose
                                        // matchingCharacterFilter we couldn't query (rare).
                                        // E.g. "Ahsoka's Shoto Lightsaber" title contains "ahsoka"
                                        // → matches an Ahsoka attachee.
                                        if (!haveMatchingInHand && hc.getTitle() != null
                                                && !attacheeTitleLower.isEmpty()) {
                                            String htl = hc.getTitle().toLowerCase(java.util.Locale.ROOT);
                                            String[] parts = attacheeTitleLower.split("\\s+");
                                            for (String p : parts) {
                                                if (p.length() >= 4 && htl.contains(p)) {
                                                    haveMatchingInHand = true;
                                                    matchingTitle = hc.getTitle();
                                                    break;
                                                }
                                            }
                                            if (haveMatchingInHand) break;
                                        }
                                    }
                                } catch (Exception e) { /* ignore */ }

                                if ((weaponIsNonUnique || !weaponMatchesAttachee) && haveMatchingInHand) {
                                    v67anBonus = 400.0f;
                                    v67anReason = String.format(
                                        "transfer wrong/generic weapon off %s (have matching '%s' in hand) — frees slot for persona-matched deploy!",
                                        attachee.getTitle(), matchingTitle);
                                }
                                // V72 (Steve, 2026-05-15): WEAPON REDISTRIBUTION. Mirror of Rando V72.
                                try {
                                    int weaponsOnAttachee = 0;
                                    java.util.List<PhysicalCard> atts = gameState.getAttachedCards(attachee);
                                    if (atts != null) {
                                        for (PhysicalCard a : atts) {
                                            if (a != null && a.getBlueprint() != null
                                                    && a.getBlueprint().getCardCategory() == CardCategory.WEAPON) {
                                                weaponsOnAttachee++;
                                            }
                                        }
                                    }
                                    if (weaponsOnAttachee >= 2) {
                                        PhysicalCard attacheeLocation = attachee.getAtLocation();
                                        boolean unarmedBuddyExists = false;
                                        String buddyTitle = null;
                                        if (attacheeLocation != null) {
                                            java.util.Collection<PhysicalCard> sameSiteCards =
                                                gameState.getCardsAtLocation(attacheeLocation);
                                            if (sameSiteCards != null) {
                                                for (PhysicalCard sc : sameSiteCards) {
                                                    if (sc == null || sc.getBlueprint() == null) continue;
                                                    if (sc == attachee) continue;
                                                    if (!context.getPlayerId().equals(sc.getOwner())) continue;
                                                    if (sc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                                    boolean scArmed = false;
                                                    java.util.List<PhysicalCard> scAtts = gameState.getAttachedCards(sc);
                                                    if (scAtts != null) {
                                                        for (PhysicalCard sa : scAtts) {
                                                            if (sa != null && sa.getBlueprint() != null
                                                                    && sa.getBlueprint().getCardCategory() == CardCategory.WEAPON) {
                                                                scArmed = true;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if (!scArmed) {
                                                        unarmedBuddyExists = true;
                                                        buddyTitle = sc.getTitle();
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                        if (unarmedBuddyExists) {
                                            v67anBonus = 500.0f;
                                            v67anReason = String.format(
                                                "V72 REDISTRIBUTE: %s has %d weapons; transfer one to unarmed buddy '%s' at same site",
                                                attachee.getTitle(), weaponsOnAttachee, buddyTitle);
                                        }
                                    }
                                } catch (Exception e) { logger.debug("V72 redistribute check error: {}", e.getMessage()); }
                            }
                        }
                    }
                } catch (Exception e) { logger.debug("V67an error: {}", e.getMessage()); }
                action.addReasoning("V67an WEAPON SWAP: " + v67anReason, v67anBonus);
                logger.warn("V67an WEAPON TRANSFER: '{}' → +{} ({})",
                    actionText, (int) v67anBonus, v67anReason);
            }

            // ========== Take Card Into Hand ==========
            else if (actionText.contains("Take") && actionText.contains("into hand")) {
                evaluateTakeIntoHand(action, context, actionText, textLower);
            }

            // ========== Prevent Battle/Move (Barrier Cards) ==========
            else if (actionText.contains("Prevent") && actionText.contains("from battling or moving")) {
                evaluateBarrier(action, context, actionText);
            }

            // ========== Monnok-type (Reveal Hand) ==========
            else if (actionText.contains("LOST: Reveal opponent's hand")) {
                int theirHandSize = gameState != null ? gameState.getHand(context.getOpponentId()).size() : 0;
                if (theirHandSize > 6) {
                    action.addReasoning("Opponent has many cards - reveal worth it", VERY_GOOD_DELTA);
                } else {
                    action.addReasoning("Opponent has few cards - save reveal", VERY_BAD_DELTA);
                }
            }

            // ========== Dangerous Cards ==========
            else if (textLower.contains("stardust") || textLower.contains("on the edge")) {
                action.addReasoning("Known dangerous card", VERY_BAD_DELTA);
            }

            // ========== Draw Card Into Hand ==========
            else if (actionText.equals("Draw card into hand from Force Pile")) {
                action.setActionType(ActionType.DRAW);
                action.addReasoning("Draw option (see DrawEvaluator)", 0.0f);
            }

            // ========== V67ae: GAME-TEXT 'MOVE TO HERE' DRAIN GUARD ==========
            // Steve's report: Rando moved Vader from CC Lower Corridor (3-drain
            // battleground) to Mustafar: Vader's Castle (0 drain) using Castle's
            // 'may move character to here' game-text action. V67g MOVE-FROM-DRAIN
            // didn't fire because that's wired to landspeed/CardSelectionEvaluator,
            // not card-action moves through ActionTextEvaluator.
            //
            // Rule: if the source card's location has zero drain potential (no opp
            // icons) AND it's a 'move <character> to here' action, penalize. The
            // 'free move' attractiveness shouldn't outweigh losing drain pressure.
            else if ((textLower.contains("move from") && textLower.contains("to here"))
                    || textLower.contains("move to here")
                    || textLower.contains("relocate to here")) {
                action.setActionType(ActionType.MOVE);
                if (cardId != null && gameState != null && context.getPlayerId() != null) {
                    try {
                        PhysicalCard srcLoc = gameState.findCardById(Integer.parseInt(cardId));
                        if (srcLoc != null && srcLoc.getBlueprint() != null) {
                            // The destination IS the source card's location (it's a site itself)
                            String oppId = gameState.getOpponent(context.getPlayerId());
                            int destOppIcons = 0;
                            try {
                                if (context.getSide() == com.gempukku.swccgo.common.Side.LIGHT) {
                                    destOppIcons = srcLoc.getBlueprint().getIconCount(com.gempukku.swccgo.common.Icon.DARK_FORCE);
                                } else {
                                    destOppIcons = srcLoc.getBlueprint().getIconCount(com.gempukku.swccgo.common.Icon.LIGHT_FORCE);
                                }
                            } catch (Exception e) { /* ignore */ }

                            if (destOppIcons == 0) {
                                action.addReasoning(String.format(
                                    "V67ae MOVE-TO-NON-DRAIN: '%s' destination has 0 opp icons — losing drain pressure for a 'safe' retreat!",
                                    srcLoc.getTitle()), -300.0f);
                                logger.warn("V67ae MOVE-TO-NON-DRAIN: action='{}' dest={} 0-drain — penalize free retreat (-300)",
                                    actionText, srcLoc.getTitle());
                            }
                        }
                    } catch (Exception e) { logger.debug("V67ae error: {}", e.getMessage()); }
                }
                action.addReasoning("V67ae move-to-here action — see drain analysis", 0.0f);
            }

            // ========== Movement Actions ==========
            else if (actionText.contains("Move using") || actionText.contains("Shuttle") ||
                     actionText.contains("Docking bay transit") || actionText.contains("Transport")) {
                action.setActionType(ActionType.MOVE);
                action.addReasoning("Movement option (see MoveEvaluator)", 0.0f);

                // === V35.4: BOOST MOVEMENT WHEN ENEMY SPY/PRESENCE BLOCKS OUR DRAIN ===
                // If our character is at ANY location where an opponent (including undercover spy)
                // has presence, our force drain is blocked. Moving away lets us drain elsewhere.
                // Undercover spies deploy on OUR side but count as opponent presence!
                if (gameState != null && context.getPlayerId() != null) {
                    try {
                        String opponentId = gameState.getOpponent(context.getPlayerId());
                        for (com.gempukku.swccgo.game.PhysicalCard loc : gameState.getLocationsInOrder()) {
                            if (loc == null || loc.getTitle() == null) continue;

                            boolean weHavePresence = false;
                            boolean oppHasPresence = false;
                            boolean oppHasUndercoverSpy = false;
                            for (com.gempukku.swccgo.game.PhysicalCard card : gameState.getCardsAtLocation(loc)) {
                                if (card == null) continue;
                                if (context.getPlayerId().equals(card.getOwner())) {
                                    weHavePresence = true;
                                    // V35.4: Check if this is actually an opponent's undercover spy
                                    // Undercover spies appear on our side but are opponent cards
                                    if (card.isUndercover()) {
                                        oppHasUndercoverSpy = true;
                                    }
                                } else if (opponentId != null && opponentId.equals(card.getOwner())) {
                                    oppHasPresence = true;
                                }
                            }
                            // If opponent has presence (or undercover spy) at our location, drain is blocked
                            if (weHavePresence && (oppHasPresence || oppHasUndercoverSpy)) {
                                float spyBonus = oppHasUndercoverSpy ? 250.0f : 150.0f;
                                action.addReasoning(String.format(
                                    "V35.4: %s blocking drain at %s — move away to drain elsewhere!",
                                    oppHasUndercoverSpy ? "UNDERCOVER SPY" : "Enemy presence",
                                    loc.getTitle()), spyBonus);
                                logger.warn("V35.4: {} at {} blocking our drain — boosting movement (+{})",
                                    oppHasUndercoverSpy ? "UNDERCOVER SPY" : "Enemy",
                                    loc.getTitle(), (int)spyBonus);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        logger.debug("V35.4: Error checking spy-blocked sites: {}", e.getMessage());
                    }
                }
            }
            else if (actionText.equals("Take off") || actionText.equals("Land")) {
                action.setActionType(ActionType.MOVE);
                action.addReasoning("Take off/Land option (see MoveEvaluator)", 0.0f);
            }

            // ========== Make Opponent Lose Force ==========
            else if (actionText.contains("Make opponent lose")) {
                action.addReasoning("Making opponent lose force", GOOD_DELTA);
            }

            // ========== Deploy Docking Bay ==========
            else if (actionText.contains("Deploy docking bay")) {
                action.addReasoning("Deploying docking bay", GOOD_DELTA);
            }

            // ========== V25: HUNT DOWN V — VADER CASTLE DEPLOY ACTION ==========
            // If the action deploys Vader from Reserve Deck (via Vader's Castle), and
            // Hunt Down V is the objective, this is THE most important action in the game.
            // Vader must be on table for the deck to function.
            else if (actionText.contains("Deploy Vader from Reserve Deck") || actionText.contains("Deploy Vader here")) {
                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer vaderObjAnalyzer =
                    context.getObjectiveAnalyzer();
                if (vaderObjAnalyzer != null && vaderObjAnalyzer.isAnalyzed() && vaderObjAnalyzer.isHuntDownV()) {
                    boolean vaderOnTable = false;
                    int forceAvailable = 0;
                    if (context.getGame() != null && context.getGame().getGameState() != null) {
                        com.gempukku.swccgo.game.state.GameState vaderGs = context.getGame().getGameState();
                        vaderOnTable = vaderObjAnalyzer.isVaderOnTable(vaderGs, context.getPlayerId());
                        forceAvailable = vaderGs.getForcePileSize(context.getPlayerId());
                    }
                    if (!vaderOnTable) {
                        // V25: Don't attempt Vader Castle deploy if not enough Force
                        // Vader's deploy cost is typically 6. This is a once-per-game action
                        // so we must NOT waste it when we can't afford to deploy him.
                        if (forceAvailable < 6) {
                            action.addReasoning("V25 HUNT DOWN: NOT ENOUGH FORCE for Vader! Need 6, have " + forceAvailable + ". SAVE Castle action!", -500.0f);
                            logger.warn("V25 HUNT DOWN: Vader Castle deploy BLOCKED — only {} Force available (need 6)", forceAvailable);
                        } else {
                            action.addReasoning("V25 HUNT DOWN: DEPLOY VADER NOW! Have " + forceAvailable + " Force, deck cannot function without him!", VERY_GOOD_DELTA + 500.0f);
                            logger.warn("V25 HUNT DOWN: Vader Castle deploy action — TOP PRIORITY (+{}) with {} Force", (int)(VERY_GOOD_DELTA + 500.0f), forceAvailable);
                        }
                    } else {
                        action.addReasoning("Vader already on table — Castle deploy not urgent", 0.0f);
                    }
                } else {
                    action.addReasoning("Deploy Vader from reserve", VERY_GOOD_DELTA);
                }
            }

            // ========== Deploy From Reserve (Risky) ==========
            else if (actionText.contains("Deploy") && actionText.contains("from")) {
                action.addReasoning("Deploying from reserve - mild caution", -10.0f);
            }

            // ========== Embark ==========
            else if (actionText.contains("Embark")) {
                action.setActionType(ActionType.MOVE);
                evaluateEmbark(action, context, actionText);
            }

            // ========== Disembark/Relocate/Transfer ==========
            else if (actionText.contains("Disembark") || actionText.contains("Relocate") ||
                     actionText.contains("Transfer")) {
                action.setActionType(ActionType.MOVE);
                action.addReasoning("Usually avoid disembark/relocate/transfer", VERY_BAD_DELTA);
            }

            // ========== Ship-dock ==========
            else if (actionText.contains("Ship-dock")) {
                action.addReasoning("Avoid ship-docking", VERY_BAD_DELTA);
            }

            // ========== Place in Lost Pile ==========
            else if (actionText.contains("Place in Lost Pile")) {
                action.addReasoning("Avoid losing cards", VERY_BAD_DELTA);
            }

            // ========== Grab ==========
            else if (actionText.contains("Grab")) {
                evaluateGrab(action, context, actionText);
            }

            // ========== Break Cover ==========
            else if (actionText.contains("Break cover")) {
                evaluateBreakCover(action, context, actionText);
            }

            // ========== Retrieve Force ==========
            else if (textLower.contains("retrieve") || actionText.contains("Place out of play to retrieve")) {
                // === V29.14: WOKLING — Don't place out of play until generating 15+ Force ===
                if (actionText.contains("Place out of play to retrieve") && gameState != null) {
                    float forceGen = gameState.getPlayersTotalForceGeneration(context.getPlayerId());
                    if (forceGen < 15.0f) {
                        action.addReasoning("V29.14 WOKLING: Only generating " + forceGen + " Force — keep Wokling on table for force gen!", VERY_BAD_DELTA);
                        logger.warn("V29.14 WOKLING: Force gen={}, need 15+ before placing out of play. BLOCKING.", forceGen);
                    } else {
                        int lostPileSize = gameState.getLostPile(context.getPlayerId()).size();
                        if (lostPileSize > 5) {
                            action.addReasoning("V29.14 WOKLING: Force gen=" + forceGen + " (15+) and lost pile=" + lostPileSize + " — OK to place out of play", GOOD_DELTA);
                            logger.warn("V29.14 WOKLING: Force gen={}, lost pile={} — allowing place out of play", forceGen, lostPileSize);
                        } else {
                            action.addReasoning("V29.14 WOKLING: Force gen OK but lost pile too small (" + lostPileSize + ")", BAD_DELTA);
                        }
                    }
                } else {
                    int lostPileSize = gameState != null ? gameState.getLostPile(context.getPlayerId()).size() : 0;
                    if (lostPileSize > 15) {
                        action.addReasoning("High lost pile - retrieve worth it", GOOD_DELTA);
                    } else {
                        action.addReasoning("Low lost pile - save retrieve", BAD_DELTA);
                    }
                }
            }

            // ========== Defensive Shields ==========
            else if (actionText.contains("Play a Defensive Shield")) {
                if (!context.isMyTurn()) {
                    action.addReasoning("Defensive shield during opponent's turn - prefer pass", -10.0f);
                } else {
                    action.addReasoning("Defensive shield", VERY_GOOD_DELTA);
                }
            }

            // ========== Deploy on table/location ==========
            else if (actionText.startsWith("Deploy on")) {
                if (textLower.contains("projection") && textLower.contains("side")) {
                    action.addReasoning("Never put projection on side of table", VERY_BAD_DELTA);
                } else {
                    action.addReasoning("Deploy on location/table", GOOD_DELTA);
                }
            }

            // ========== Deploy unique ==========
            else if (actionText.startsWith("Deploy unique")) {
                action.addReasoning("Special battleground deploy", GOOD_DELTA);
            }

            // ========== USED: Peek at top ==========
            else if (actionText.startsWith("USED: Peek at top")) {
                action.addReasoning("Peek for card advantage", GOOD_DELTA);
            }

            // ========== Force Drain Cancellation ==========
            else if (actionText.contains("Cancel Force drain")) {
                if (context.isMyTurn()) {
                    action.addReasoning("V52 NEVER SELF-CANCEL: Don't cancel own force drain!", -9999.0f);
                    logger.warn("V52 SELF-CANCEL BLOCKED: Cancel Force drain on own turn — HARD BLOCKED!");
                } else {
                    action.addReasoning("Cancel opponent's force drain", GOOD_DELTA);
                }
            }

            // ========== V74: Maintenance Cost Satisfaction (replaces V22.3) ==========
            // Mirror of Rando V74. Detects maintenance from DECISION text (the
            // action text alone is too short to contain "maintenance").
            else if (context.getDecisionText() != null
                     && context.getDecisionText().toLowerCase(java.util.Locale.ROOT)
                        .contains("maintenance")) {
                if (textLower.contains("use ") && textLower.contains(" force")) {
                    action.addReasoning("V74 MAINTENANCE PAY: keep the card alive!", 400.0f);
                    logger.warn("V74 MAINTENANCE PAY: '{}' → +400", actionText);
                } else if (textLower.contains("out of play")) {
                    action.addReasoning("V74 MAINTENANCE SACRIFICE: place out of play is PERMANENT loss!", -800.0f);
                    logger.warn("V74 MAINTENANCE SACRIFICE: '{}' → -800", actionText);
                } else if (textLower.contains("lose ") && textLower.contains(" force")
                           && (textLower.contains("used pile") || textLower.contains("place in used"))) {
                    action.addReasoning("V74 MAINTENANCE USED-PILE: lose card to used pile, keep blueprint", -200.0f);
                    logger.warn("V74 MAINTENANCE USED-PILE: '{}' → -200", actionText);
                } else if (textLower.contains("sacrifice")) {
                    action.addReasoning("V74 MAINTENANCE SACRIFICE: avoid", -800.0f);
                    logger.warn("V74 MAINTENANCE SACRIFICE: '{}' → -800", actionText);
                }
            }

            // ========== Use/Lose Force Actions ==========
            else if (textLower.startsWith("use ") && textLower.contains(" force ")) {
                // V22.3: Check if this might be a maintenance payment
                // Maintenance decisions often just say "Use X Force" without "maintenance" keyword
                // If the decision context involves a maintenance card, prefer paying
                if (textLower.contains("cost") || textLower.contains("upkeep")) {
                    action.addReasoning("V22.3 MAINTENANCE: Pay upkeep cost!", 150.0f);
                    logger.warn("V22.3 MAINTENANCE: Likely upkeep payment - '{}'", actionText);
                } else {
                    // V24.5: No randomness — generic use force should be avoided
                    action.addReasoning("'Use Force' action — prefer not to use force unnecessarily", -20.0f);
                }
            }
            else if (textLower.startsWith("lose ") && textLower.contains(" force ")) {
                // V24.5: No randomness — losing force is almost always bad
                action.addReasoning("'Lose Force' action — avoid losing force", -30.0f);
            }
            // V22.3: Catch generic sacrifice options that aren't tagged as maintenance
            else if (textLower.contains("sacrifice") || textLower.contains("place out of play")) {
                action.addReasoning("V22.3: Avoid sacrificing cards — prefer alternatives", -150.0f);
                logger.info("V22.3 SACRIFICE PENALTY: '{}'", actionText);
            }

            // ========== V22.5: Alert My Star Destroyer / Ship Deployment Priority ==========
            // "Alert My Star Destroyer" deploys Executor + pilot for cheap.
            // This is CRITICAL for TDIGWATT — Bespin system occupation enables Dark Deal
            // and Cloud City Occupation, which are the deck's primary damage engines.
            else if (textLower.contains("reveal") && (textLower.contains("star destroyer") || textLower.contains("pilot"))) {
                // Check if we have a ship at Bespin system already
                boolean hasBespinShip = false;
                if (gameState != null) {
                    try {
                        String pid = context.getPlayerId();
                        for (com.gempukku.swccgo.game.PhysicalCard loc : gameState.getLocationsInOrder()) {
                            if (loc != null && loc.getTitle() != null &&
                                loc.getTitle().toLowerCase(java.util.Locale.ROOT).contains("bespin") &&
                                loc.getBlueprint() != null && loc.getBlueprint().getCardSubtype() != null &&
                                loc.getBlueprint().getCardSubtype() == com.gempukku.swccgo.common.CardSubtype.SYSTEM) {
                                float ourPower = context.getGame().getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, loc, pid, false, false);
                                if (ourPower > 0) hasBespinShip = true;
                                break;
                            }
                        }
                    } catch (Exception e) {
                        // Ignore
                    }
                }
                if (!hasBespinShip) {
                    action.addReasoning("V22.5 CRITICAL: Deploy ship to Bespin! Enables Dark Deal + CC Occupation!", 300.0f);
                    logger.warn("V22.5 BESPIN PRIORITY: Alert My Star Destroyer — no ship at Bespin yet! (+300)");
                } else {
                    action.addReasoning("V22.5: Deploy ship (Bespin already occupied)", 100.0f);
                    logger.info("V22.5: Alert My Star Destroyer — Bespin already has ship presence");
                }
            }
            // V22.5: Generic "deploy simultaneously" or ship+pilot combos
            else if (textLower.contains("deploy") && textLower.contains("simultaneously")) {
                action.addReasoning("V22.5: Deploy pilot+ship combo - efficient!", 120.0f);
                logger.info("V22.5: Simultaneous deploy detected");
            }

            // ========== V25: INITIATE BATTLE ==========
            // Battle initiation was previously unhandled (fell to default 0.0f) which
            // meant Rando NEVER chose to initiate battles because other actions always
            // outscored them. Now we evaluate the specific location's power differential.
            else if (actionText.contains("Initiate battle") || actionText.contains("initiate battle")) {
                action.setActionType(ActionType.BATTLE);
                boolean battleScored = false;

                SwccgGame battleGame = context.getGame();
                if (battleGame != null && context.getGame().getGameState() != null) {
                    com.gempukku.swccgo.game.state.GameState bGs = battleGame.getGameState();
                    String bPlayerId = context.getPlayerId();
                    String bOpponentId = bGs.getOpponent(bPlayerId);

                    if (bOpponentId != null) {
                        try {
                            // Find which location this battle targets
                            for (PhysicalCard bLoc : bGs.getTopLocations()) {
                                String bLocTitle = bLoc.getTitle();
                                if (bLocTitle != null && actionText.contains(bLocTitle)) {
                                    float ourPower = battleGame.getModifiersQuerying().getTotalPowerAtLocation(
                                        bGs, bLoc, bPlayerId, false, false);
                                    float theirPower = battleGame.getModifiersQuerying().getTotalPowerAtLocation(
                                        bGs, bLoc, bOpponentId, false, false);
                                    float ourAbility = battleGame.getModifiersQuerying().getTotalAbilityAtLocation(
                                        bGs, bPlayerId, bLoc);
                                    float theirAbility = battleGame.getModifiersQuerying().getTotalAbilityAtLocation(
                                        bGs, bOpponentId, bLoc);
                                    float powerDiff = ourPower - theirPower;
                                    float abilityDiff = ourAbility - theirAbility;
                                    // Ability matters: each point of ability = roughly 2.5 power via destiny draws
                                    float effectiveDiff = powerDiff + (abilityDiff * 2.5f);

                                    logger.warn("V25 BATTLE EVAL at {}: our power={} ability={}, their power={} ability={}, effectiveDiff={}",
                                        bLocTitle, (int)ourPower, (int)ourAbility, (int)theirPower, (int)theirAbility, (int)effectiveDiff);

                                    if (theirPower <= 0) {
                                        // No opponent here — can't battle
                                        action.addReasoning("V25 BATTLE: No opponent at " + bLocTitle, -100.0f);
                                    } else if (theirPower > ourPower * 2 && theirPower > 6) {
                                        // Suicidal — hard block
                                        action.addReasoning(String.format("V25 BATTLE SUICIDE: %.0f vs %.0f at %s — NEVER!",
                                            ourPower, theirPower, bLocTitle), -500.0f);
                                    } else if (effectiveDiff >= 8) {
                                        // Crushing advantage
                                        action.addReasoning(String.format("V25 BATTLE CRUSH at %s: %.0f vs %.0f — ATTACK!",
                                            bLocTitle, ourPower, theirPower), 200.0f);
                                    } else if (effectiveDiff >= 5) {
                                        // Strong advantage
                                        action.addReasoning(String.format("V25 BATTLE FAVORABLE at %s: %.0f vs %.0f",
                                            bLocTitle, ourPower, theirPower), 120.0f);
                                    } else if (effectiveDiff >= 2) {
                                        // Marginal advantage
                                        action.addReasoning(String.format("V25 BATTLE MARGINAL at %s: %.0f vs %.0f",
                                            bLocTitle, ourPower, theirPower), 60.0f);
                                    } else if (effectiveDiff >= -2) {
                                        // Even — slight positive to encourage aggression
                                        action.addReasoning(String.format("V25 BATTLE EVEN at %s: %.0f vs %.0f — risky but worth trying",
                                            bLocTitle, ourPower, theirPower), 20.0f);
                                    } else {
                                        // Unfavorable
                                        float penalty = -60.0f;
                                        if (effectiveDiff < -8) penalty = -120.0f;
                                        if (effectiveDiff < -15) penalty = -250.0f;
                                        action.addReasoning(String.format("V25 BATTLE UNFAVORABLE at %s: %.0f vs %.0f — avoid!",
                                            bLocTitle, ourPower, theirPower), penalty);
                                    }
                                    battleScored = true;
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            logger.warn("V25 BATTLE: Error evaluating battle: {}", e.getMessage());
                        }
                    }
                }

                if (!battleScored) {
                    // Fallback: give modest positive score to encourage battling
                    action.addReasoning("V25 BATTLE: Initiate battle (no location data)", 30.0f);
                }

                // Check reserve for destiny draws
                int battleReserve = 0;
                if (context.getGame() != null && context.getGame().getGameState() != null) {
                    battleReserve = context.getGame().getGameState().getReserveDeckSize(context.getPlayerId());
                }
                if (battleReserve < 3) {
                    action.addReasoning("V25 BATTLE: Low reserve (" + battleReserve + ") — bad destiny draws!", -50.0f);
                }

                logger.warn("V25 BATTLE: '{}' scored {}", actionText.length() > 60 ? actionText.substring(0,60) + "..." : actionText,
                    String.format("%.1f", action.getScore()));
            }

            // ========== V35.4: STUNNING LEADER — ONLY USE DEFENSIVELY ==========
            // Stunning Leader excludes characters from battle. This is ONLY useful when:
            // 1. OPPONENT initiated the battle (we're defending)
            // 2. Opponent has a clear power advantage (we need to reduce their forces)
            // NEVER use when WE initiated battle — we started it to WIN, not to exclude everyone!
            else if (textLower.contains("stunning leader") || textLower.contains("exclude") && textLower.contains("from battle")) {
                if (context.getPhase() == Phase.BATTLE && gameState != null) {
                    try {
                        com.gempukku.swccgo.game.state.BattleState bState = gameState.getBattleState();
                        if (bState != null) {
                            String slPlayerId = context.getPlayerId();
                            String slInitiator = bState.getPlayerInitiatedBattle();
                            boolean weInitiated = slPlayerId != null && slPlayerId.equals(slInitiator);

                            if (weInitiated) {
                                // WE started this battle — NEVER use Stunning Leader to exclude!
                                action.addReasoning("V35.4 STUNNING LEADER: WE initiated battle — do NOT exclude! Fight to WIN!", -600.0f);
                                logger.warn("V35.4 STUNNING LEADER BLOCKED: We initiated battle — don't exclude characters (-600)");
                            } else {
                                // Opponent initiated — check if they have advantage
                                PhysicalCard slBattleLoc = bState.getBattleLocation();
                                if (slBattleLoc != null) {
                                    String slOpp = gameState.getOpponent(slPlayerId);
                                    float slOurPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, slBattleLoc, slPlayerId, false, false);
                                    float slTheirPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, slBattleLoc, slOpp, false, false);
                                    if (slTheirPower > slOurPower) {
                                        // Opponent is stronger — Stunning Leader is valuable defensively
                                        action.addReasoning(String.format(
                                            "V35.4 STUNNING LEADER: Opponent stronger (%.0f vs %.0f) — exclude threats!",
                                            slOurPower, slTheirPower), 200.0f);
                                    } else {
                                        // We're winning even though they initiated — don't waste it
                                        action.addReasoning("V35.4 STUNNING LEADER: We're winning this battle — save it!", -200.0f);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.debug("V35.4 STUNNING LEADER: Error: {}", e.getMessage());
                    }
                } else {
                    // Not in battle — save for when we need it
                    action.addReasoning("V35.4 STUNNING LEADER: Save for defensive use during battle!", -100.0f);
                }
            }

            // ========== V35.4: YOU ARE BEATEN — DON'T WASTE ON UNDERCOVER SPIES ==========
            // You Are Beaten targets opponent characters. But undercover spies appear on OUR side
            // and aren't valid targets for combat effects. Don't waste this interrupt.
            // Also: only use during battle or when it will lead to meaningful attrition.
            else if (textLower.contains("you are beaten")) {
                if (context.getPhase() == Phase.BATTLE) {
                    action.addReasoning("V35.4 YOU ARE BEATEN: During battle — use for attrition!", 150.0f);
                } else {
                    // Outside battle — this is usually a waste
                    action.addReasoning("V35.4 YOU ARE BEATEN: Not in battle — save for combat!", -200.0f);
                    logger.info("V35.4 YOU ARE BEATEN: Not in battle — penalizing (-200)");
                }
            }

            // ========== V60 HIDDEN PATH TRANSIT — Underground Corridor game text ==========
            // "Move Jedi Survivor here to a site" is Underground Corridor's game-text action
            // that transits Jedi Survivors from Corridor to a Jabiim site or opponent's
            // battleground. This is THE action that flips the Hidden Path objective.
            // Previously scored 0.0 ("Unknown action type") while landspeed (which goes
            // backward to Safehouse) got +9999 from V53b. FIXES Issue #C from peaceful-pike.
            else if (textLower.contains("move jedi survivor here to a site")
                     || (textLower.contains("move jedi") && textLower.contains("to a site"))) {
                com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer hpTransit =
                    context.getObjectiveAnalyzer();
                boolean onHiddenPath = hpTransit != null && hpTransit.isAnalyzed()
                    && hpTransit.getObjectiveTitle() != null
                    && hpTransit.getObjectiveTitle().toLowerCase(Locale.ROOT).contains("hidden path");
                if (onHiddenPath) {
                    action.addReasoning("V60 HIDDEN PATH TRANSIT: Move Jedi OUT of Corridor — flips objective!", 9999.0f);
                    logger.warn("V60 HIDDEN PATH TRANSIT: '{}' — +9999 (CORRECT outward move, unlike landspeed)", actionText);
                } else {
                    action.addReasoning("Move Jedi transit action — tactical mobility", 200.0f);
                }
            }

            // ========== V60 RESERVE DECK PULLS — always positive, always fire ==========
            // Steve's rule (feedback_reserve_deck_pulls.md): Reserve Deck pull effects
            // are FREE VALUE — thin the deck, bring key cards into play. Always try them.
            // Covers [Download] actions (Sai'torr Kal Fas → matching weapon, Visage of
            // Emperor → lightsaber) and generic "X from Reserve Deck" / "Take X into hand"
            // actions (Mining Village → Tala Durith, Malachor STE → Padawan, IMBATS, etc.)
            // that weren't caught by earlier specific handlers.
            // Hard-block only when:
            //   1. DeckOracle confirms target NOT in Reserve (avoids deck reveal)
            //   2. Force can't cover the action cost (defer to next turn)
            //   3. This action has failed 2x in a row (shouldAvoidPulling)
            else if (textLower.contains("[download]")
                     || (textLower.contains("from reserve deck") && !textLower.contains("shuffle"))
                     || textLower.contains("take an effect into hand")
                     || textLower.contains("take a character into hand")) {

                // === V82 (Steve, 2026-05-16): EXPLICIT SOURCE-CARD SITE-PULL TRIGGER ===
                // Mirror of Rando V82. See Rando ActionTextEvaluator V82 comment for
                // full rationale. Reads source card blueprint game text and matches
                // "(site|location|battleground) [...] from reserve" → +2500.
                //
                // V82.1 (Steve, 2026-05-16): Dropped the verb anchor — just match
                // "from reserve". Catches any pull phrasing.
                {
                    GameState v82Gs = context.getGameState();
                    if (cardId != null && v82Gs != null) {
                        try {
                            PhysicalCard srcCard = v82Gs.findCardById(Integer.parseInt(cardId));
                            if (srcCard != null && srcCard.getBlueprint() != null) {
                                String srcGt = srcCard.getBlueprint().getGameText();
                                if (srcGt != null) {
                                    // V82.2: added "docking bay" and "system|sector" — all LOCATION-type pulls.
                                    java.util.regex.Matcher v82m = java.util.regex.Pattern.compile(
                                        "\\b(site|location|battleground|docking\\s+bay|system|sector)\\b[^.;]*?\\bfrom\\s+reserve",
                                        java.util.regex.Pattern.CASE_INSENSITIVE).matcher(srcGt);
                                    if (v82m.find()) {
                                        String matched = v82m.group(1);
                                        action.addReasoning(
                                            "V82 SITE PULL: source '" + srcCard.getTitle()
                                            + "' pulls a " + matched + " from Reserve — must take this every turn!",
                                            2500.0f);
                                        logger.warn("V82 SITE PULL: '{}' (src '{}', matched '{}') → +2500",
                                            actionText, srcCard.getTitle(), matched);
                                    }
                                }
                            }
                        } catch (NumberFormatException nfe) {
                            // skip
                        } catch (Exception e) {
                            logger.debug("V82 SITE PULL error: {}", e.getMessage());
                        }
                    }
                }

                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle pullOracle = context.getDeckOracle();
                GameState pullGs = context.getGameState();
                boolean hardBlocked = false;

                // Guard 1: Reserve deck nearly empty (< 3 cards) — reveal risk
                if (pullGs != null && !hardBlocked) {
                    try {
                        int reserveSize = pullGs.getReserveDeckSize(context.getPlayerId());
                        if (reserveSize <= 2) {
                            action.addReasoning("V60 RESERVE RISK: Reserve deck has " + reserveSize
                                + " cards — pull would reveal almost everything!", -400.0f);
                            logger.warn("V60 RESERVE RISK: '{}' — reserve {} cards — too risky (-400)",
                                actionText, reserveSize);
                            hardBlocked = true;
                        }
                    } catch (Exception e) { /* ignore */ }
                }

                // Guard 2: Failed 2x in a row — stop pulling this specific action
                if (pullOracle != null && !hardBlocked) {
                    String failKey = "action:" + actionText;
                    if (pullOracle.shouldAvoidPulling(failKey)) {
                        action.addReasoning("V60 RESERVE FAIL-STOP: '" + actionText
                            + "' failed 2x — stop trying this game!", -9999.0f);
                        logger.warn("V60 RESERVE FAIL-STOP: '{}' has failed 2+ times — hard-blocked",
                            actionText);
                        hardBlocked = true;
                    }
                }

                // Guard 3: Named-target downloads (e.g., "Deploy Tala Durith from Reserve Deck")
                // — if DeckOracle shows the specific target is NOT in reserve, hard-block.
                // Only blocks MULTI-WORD proper-noun targets (case-sensitive match).
                // FIXES Yarna "Deploy card from Reserve Deck" false positive.
                if (!hardBlocked && pullOracle != null) {
                    java.util.regex.Matcher nameMatch = java.util.regex.Pattern.compile(
                        "(?:Deploy|Take) ([A-Z][A-Za-z']+ [A-Z][A-Za-z' -]+?) (?:from Reserve|into hand from Reserve)")
                        .matcher(actionText);
                    if (nameMatch.find()) {
                        String targetName = nameMatch.group(1).trim();
                        if (!pullOracle.hasTargetInReserve(targetName.split(" "))) {
                            action.addReasoning("V60 RESERVE MISS: '" + targetName
                                + "' is NOT in Reserve Deck — pull will fail and reveal deck!", -9999.0f);
                            logger.warn("V60 RESERVE MISS: Target '{}' not in reserve — hard-blocked (reveal risk)",
                                targetName);
                            hardBlocked = true;
                        }
                    }
                }

                // V66 MEMORY AUDIT: Unified hand/in-play check for [Download] and pull actions.
                // Catches the common "already deployed" case that Guards 1-3 miss.
                // Example: Sai'torr Kal Fas "[Download] a matching weapon" when the matching
                // weapon (Obi-Wan's Lightsaber) is already attached to Obi-Wan — search fails
                // and reveals reserve.
                if (!hardBlocked && pullOracle != null && pullOracle.isAnalyzed()) {
                    com.gempukku.swccgo.common.Zone v66Zone =
                        com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.parseSourceZone(actionText);
                    if (v66Zone != null) {
                        String[] v66Keywords = null;
                        java.util.regex.Matcher v66Named = java.util.regex.Pattern.compile(
                            "(?:Deploy|Take) ([A-Z][A-Za-z']+ [A-Z][A-Za-z' -]+?) "
                                + "(?:from Reserve|from Lost|from Used|from Force|into hand from)")
                            .matcher(actionText);
                        if (v66Named.find()) {
                            v66Keywords = v66Named.group(1).trim().split(" ");
                        } else {
                            java.util.regex.Matcher v66Gen = java.util.regex.Pattern.compile(
                                "(?:Deploy|Take|\\[Download\\]) an? ([a-z]+) ?")
                                .matcher(actionText);
                            if (v66Gen.find()) {
                                String kw = v66Gen.group(1).trim();
                                if (kw.length() >= 3) v66Keywords = new String[] { kw };
                            }
                        }
                        if (v66Keywords != null && v66Keywords.length > 0) {
                            com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.PullValidation v66Result =
                                pullOracle.validatePull(v66Zone, v66Keywords);
                            if (v66Result.outcome ==
                                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.PullOutcome.WILL_FAIL) {
                                action.addReasoning("V66 MEMORY: " + v66Result.reason, -9999.0f);
                                logger.warn("V66 MEMORY WILL_FAIL: '{}' — {}", actionText, v66Result.reason);
                                hardBlocked = true;
                            } else if (v66Result.outcome ==
                                com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.PullOutcome.WASTEFUL) {
                                action.addReasoning("V66 MEMORY: " + v66Result.reason, -800.0f);
                                logger.warn("V66 MEMORY WASTEFUL: '{}' — {} (-800)", actionText, v66Result.reason);
                            }
                        }
                    }

                    // V67h: When the action is generic, use the SOURCE CARD's game text
                    // to determine what the filter targets. Catches cases where the regex
                    // can't extract a useful keyword from the displayed action text.
                    if (!hardBlocked && cardId != null && pullGs != null) {
                        try {
                            PhysicalCard sourceCard = pullGs.findCardById(Integer.parseInt(cardId));
                            if (sourceCard != null && sourceCard.getBlueprint() != null) {
                                String v67hGT = sourceCard.getBlueprint().getGameText();
                                if (v67hGT != null) {
                                    com.gempukku.swccgo.common.Zone v67hZone =
                                        com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.parseSourceZone(actionText);
                                    if (v67hZone != null) {
                                        com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.PullValidation v67hRes =
                                            pullOracle.validatePullFromSourceCard(v67hZone, v67hGT);
                                        if (v67hRes.outcome ==
                                            com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.PullOutcome.WILL_FAIL) {
                                            action.addReasoning("V67h MEMORY (game-text): " + v67hRes.reason, -9999.0f);
                                            logger.warn("V67h MEMORY WILL_FAIL: source={} — {}",
                                                sourceCard.getTitle(), v67hRes.reason);
                                            hardBlocked = true;
                                        } else if (v67hRes.outcome ==
                                            com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle.PullOutcome.WILL_SUCCEED) {
                                            logger.info("V67h MEMORY OK: source={} — {}",
                                                sourceCard.getTitle(), v67hRes.reason);
                                        }
                                    }
                                }
                            }
                        } catch (NumberFormatException nfe) { /* ignore */ }
                        catch (Exception e) { logger.debug("V67h: error: {}", e.getMessage()); }
                    }
                }

                // V67ac (Steve, 2026-05-04): FORCE-COST GUARD for card-action reserve pulls.
                // Symptom: Rando used Vader's Castle's 'deploy Vader from Reserve Deck'
                // action with only 4 force in pile. Vader costs 7 (6 with Castle reduction).
                // Action FAILED but the search revealed Rando's reserve deck to opponent.
                // V67h validates target EXISTS in zone but doesn't validate AFFORDABILITY.
                //
                // Approach: scan Rando's reserve deck for cards matching source card's
                // parsed targets. Find the cheapest match. If even the cheapest exceeds
                // available force pile size, hard-block (action would fail + leak reserve).
                if (!hardBlocked && cardId != null && pullGs != null) {
                    try {
                        PhysicalCard ssrc = pullGs.findCardById(Integer.parseInt(cardId));
                        if (ssrc != null && ssrc.getBlueprint() != null) {
                            String ssrcGT = ssrc.getBlueprint().getGameText();
                            String ssrcTitleLower = ssrc.getTitle() != null
                                ? ssrc.getTitle().toLowerCase(java.util.Locale.ROOT) : "";
                            if (ssrcGT != null) {
                                java.util.List<String> ssrcTargets =
                                    com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle
                                        .parseSourceCardPullTargets(ssrcGT);
                                if (!ssrcTargets.isEmpty() && context.getPlayerId() != null) {
                                    // Available force = force pile size (cards lost top-down)
                                    int availForce = 0;
                                    try { availForce = pullGs.getForcePileSize(context.getPlayerId()); }
                                    catch (Exception e) { /* ignore */ }

                                    // Find cheapest matching target in reserve deck.
                                    Integer cheapestCost = null;
                                    java.util.List<PhysicalCard> reserve = null;
                                    try { reserve = pullGs.getReserveDeck(context.getPlayerId()); }
                                    catch (Exception e) { /* ignore */ }
                                    if (reserve != null) {
                                        for (PhysicalCard rc : reserve) {
                                            if (rc == null || rc.getBlueprint() == null) continue;
                                            String rcTitleLower = rc.getTitle() != null
                                                ? rc.getTitle().toLowerCase(java.util.Locale.ROOT) : "";
                                            // Match: any target keyword (icon-stripped) matches title
                                            boolean matches = false;
                                            for (String t : ssrcTargets) {
                                                String tl = t.toLowerCase(java.util.Locale.ROOT);
                                                String stripped = tl.replaceAll("\\[[^\\]]*\\]", " ")
                                                    .replaceAll("\\s+", " ").trim();
                                                if (rcTitleLower.contains(tl)
                                                        || (!stripped.isEmpty() && rcTitleLower.contains(stripped))) {
                                                    matches = true;
                                                    break;
                                                }
                                            }
                                            if (!matches) continue;
                                            try {
                                                Float dc = rc.getBlueprint().getDeployCost();
                                                if (dc != null) {
                                                    int icost = dc.intValue();
                                                    // Apply common -1 reduction if source card text says so
                                                    if (ssrcGT.toLowerCase(java.util.Locale.ROOT).contains("less force")
                                                            || ssrcGT.toLowerCase(java.util.Locale.ROOT).contains("deploy -1")) {
                                                        icost = Math.max(0, icost - 1);
                                                    }
                                                    if (cheapestCost == null || icost < cheapestCost) {
                                                        cheapestCost = icost;
                                                    }
                                                }
                                            } catch (Exception ee) { /* card type may not support deploy cost */ }
                                        }
                                    }

                                    if (cheapestCost != null && cheapestCost > availForce) {
                                        action.addReasoning(String.format(
                                            "V67ac CAN'T AFFORD: '%s' would deploy a target costing %d Force, only %d available — search reveals reserve!",
                                            ssrc.getTitle(), cheapestCost, availForce), -9999.0f);
                                        logger.warn("V67ac FORCE-COST BLOCK: source={} cheapestTargetCost={} availForce={} — block (would leak reserve)",
                                            ssrc.getTitle(), cheapestCost, availForce);
                                        hardBlocked = true;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) { logger.debug("V67ac error: {}", e.getMessage()); }
                }

                if (!hardBlocked) {
                    // Free download actions get higher baseline than force-cost pulls
                    boolean isFreeDownload = textLower.contains("[download]");
                    float baseline = isFreeDownload ? 250.0f : 150.0f;
                    action.addReasoning("V60 RESERVE PULL: '" + actionText
                        + "' — thin deck, bring value into play!", baseline);
                    logger.warn("V60 RESERVE PULL: '{}' scored +{} — pull every turn!",
                        actionText, (int)baseline);

                    // V67l UNIVERSAL LOCATION-PULL PRIORITY (mirrors DeployEvaluator V67i)
                    // Steve's rule: "If an effect lets rando pull a location from his deck
                    // that should be a universal positive points move. He should do this
                    // as the first part of his deploy phase."
                    // Detection: action text or source-card game text contains a location
                    // keyword in its target list. Bonus is +1500 — dominates all other
                    // scoring so Rando ALWAYS fires location pulls before other deploys.
                    boolean v67lAddsLocation = false;
                    String v67lReason = null;
                    String[] v67lLocationKeywords = new String[] {
                        "site", "battleground", "location", "system", "farm",
                        "cantina", "mos eisley", "tatooine", "endor", "hoth",
                        "dagobah", "naboo", "yavin", "bespin", "cloud city",
                        "mustafar", "malachor", "mapuzo", "jabiim", "coruscant",
                        "kashyyyk", "kessel", "kamino", "geonosis", "alderaan",
                        "docking bay", "spaceport", "city", "palace", "temple",
                        "safehouse", "corridor", "village", "outpost"
                    };
                    for (String kw : v67lLocationKeywords) {
                        if (textLower.contains(kw)) {
                            v67lAddsLocation = true;
                            v67lReason = "actionText contains location keyword '" + kw + "'";
                            break;
                        }
                    }
                    // Fallback: parse source card game text
                    if (!v67lAddsLocation && cardId != null && pullGs != null) {
                        try {
                            PhysicalCard sc = pullGs.findCardById(Integer.parseInt(cardId));
                            if (sc != null && sc.getBlueprint() != null) {
                                String gt = sc.getBlueprint().getGameText();
                                if (gt != null) {
                                    java.util.List<String> tgts = com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle
                                        .parseSourceCardPullTargets(gt);
                                    for (String t : tgts) {
                                        for (String kw : v67lLocationKeywords) {
                                            if (t.contains(kw)) {
                                                v67lAddsLocation = true;
                                                v67lReason = "source card '" + sc.getTitle()
                                                    + "' game text targets location-like '" + t + "'";
                                                break;
                                            }
                                        }
                                        if (v67lAddsLocation) break;
                                    }
                                }
                            }
                        } catch (Exception e) { /* ignore */ }
                    }
                    if (v67lAddsLocation) {
                        // V67ai (Steve, 2026-05-07): TIERED LOCATION DEPLOY ORDER.
                        //
                        // Steve's rule: 'Rando should never under any circumstances avoid
                        // deploying locations.' Location-pull cards have a strict priority
                        // order so the cheapest source goes first and we keep the most
                        // future flexibility:
                        //   Tier 1: Objective pull         → +2000  (free, mandatory effect)
                        //   Tier 2: Effect-card pull       → +1800  (already on table, low cost)
                        //   Tier 3: Interrupt pull         → +1600  (one-shot, save for after objective/effect)
                        //   Tier 4: Hand deploy            → +1400  (DeployEvaluator handles this)
                        //
                        // Determine source category from the source card's blueprint.
                        int v67aiTier = 0;
                        String v67aiTierName = "unclassified";
                        if (cardId != null && pullGs != null) {
                            try {
                                PhysicalCard srcPc = pullGs.findCardById(Integer.parseInt(cardId));
                                if (srcPc != null && srcPc.getBlueprint() != null) {
                                    com.gempukku.swccgo.common.CardCategory srcCat =
                                        srcPc.getBlueprint().getCardCategory();
                                    if (srcCat == com.gempukku.swccgo.common.CardCategory.OBJECTIVE) {
                                        v67aiTier = 1; v67aiTierName = "OBJECTIVE";
                                    } else if (srcCat == com.gempukku.swccgo.common.CardCategory.EFFECT) {
                                        v67aiTier = 2; v67aiTierName = "EFFECT";
                                    } else if (srcCat == com.gempukku.swccgo.common.CardCategory.INTERRUPT) {
                                        v67aiTier = 3; v67aiTierName = "INTERRUPT";
                                    } else if (srcCat == com.gempukku.swccgo.common.CardCategory.LOCATION) {
                                        // A location pulling another location (e.g., Vader's Castle's
                                        // 'download' a Hoth/Endor sub-site). Treat as effect-tier.
                                        v67aiTier = 2; v67aiTierName = "LOCATION-EFFECT";
                                    }
                                }
                            } catch (Exception e) { /* fall through to default */ }
                        }
                        float v67aiBonus;
                        switch (v67aiTier) {
                            case 1: v67aiBonus = 2000.0f; break;
                            case 2: v67aiBonus = 1800.0f; break;
                            case 3: v67aiBonus = 1600.0f; break;
                            default: v67aiBonus = 1500.0f; break;  // legacy V67l score for unknown sources
                        }
                        action.addReasoning(
                            String.format("V67ai LOCATION DEPLOY ORDER [Tier %d %s]: %s — ALWAYS pull locations FIRST, in order: Objective → Effect → Interrupt → Hand!",
                                v67aiTier, v67aiTierName, v67lReason), v67aiBonus);
                        logger.warn("V67ai LOCATION TIER {} [{}]: '{}' → +{} ({})",
                            v67aiTier, v67aiTierName, actionText, (int) v67aiBonus, v67lReason);
                    }

                    // === V67m UNIVERSAL WEAPON-PULL PRIORITY ===
                    // Steve's rule: "There are other cards that pull weapons from reserve,
                    // after location pulls and character deploys, we should use those
                    // effects to deploy weapons from reserve with positive points."
                    //
                    // Score +200 — positive but below character deploy peaks (+300-500)
                    // so chars deploy first. Same dual-source detection as V67l.
                    boolean v67mAddsWeapon = false;
                    String v67mReason = null;
                    String[] v67mWeaponKeywords = new String[] {
                        "weapon", "lightsaber", "saber", "blaster",
                        "rifle", "pistol", "cannon", "bowcaster",
                        "thermal detonator", "vibroblade", "vibro-",
                        "force pike", "electrostaff"
                    };
                    for (String kw : v67mWeaponKeywords) {
                        if (textLower.contains(kw)) {
                            v67mAddsWeapon = true;
                            v67mReason = "actionText contains weapon keyword '" + kw + "'";
                            break;
                        }
                    }
                    // Fallback: source card game text
                    if (!v67mAddsWeapon && cardId != null && pullGs != null) {
                        try {
                            PhysicalCard sc = pullGs.findCardById(Integer.parseInt(cardId));
                            if (sc != null && sc.getBlueprint() != null) {
                                String gt = sc.getBlueprint().getGameText();
                                if (gt != null) {
                                    java.util.List<String> tgts = com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle
                                        .parseSourceCardPullTargets(gt);
                                    for (String t : tgts) {
                                        for (String kw : v67mWeaponKeywords) {
                                            if (t.contains(kw)) {
                                                v67mAddsWeapon = true;
                                                v67mReason = "source card '" + sc.getTitle()
                                                    + "' game text targets weapon-like '" + t + "'";
                                                break;
                                            }
                                        }
                                        if (v67mAddsWeapon) break;
                                    }
                                }
                            }
                        } catch (Exception e) { /* ignore */ }
                    }
                    // Don't double-bonus location pulls (V67l already gave +1500)
                    // V67am (Steve, 2026-05-07): Bumped V67m weapon-pull bonus +200 → +600.
                    //
                    // Steve's order: 'pull weapon from reserve via effect/interrupt/objective
                    // FIRST, then deploy from hand.' Old V67m at +200 was below hand-deploy
                    // bonuses (V29.11 LIGHTSABER +400-500), inverting Steve's priority.
                    //
                    // +600 ensures pull-from-reserve actions outscore hand-deploy of the
                    // same weapon class. Once-per-game/turn pull effects are precious — fire
                    // them first while available; hand cards can deploy any turn.
                    if (v67mAddsWeapon && !v67lAddsLocation) {
                        // V67ar (Steve, 2026-05-08): UNIVERSAL ONE-WEAPON RULE for pull path.
                        // Mirrors V67aq's logic from DeployEvaluator. Count UNARMED Rando
                        // characters on table — if zero unarmed (every char already armed),
                        // hard-block the pull because it would put a 2nd weapon on someone.
                        // Also blocks the 'no chars at all' case (V67ao original intent).
                        //
                        // No hardcoded character names. The same rule fires regardless of
                        // which weapon (Sidious' Lightsaber, Ventress' Lightsabers,
                        // Vader's Lightsaber, anything) and which character the pull would
                        // target.
                        int v67arUnarmed = 0;
                        int v67arArmed = 0;
                        if (pullGs != null && context.getPlayerId() != null) {
                            try {
                                for (PhysicalCard pc : pullGs.getAllPermanentCards()) {
                                    if (pc == null || pc.getBlueprint() == null) continue;
                                    if (!context.getPlayerId().equals(pc.getOwner())) continue;
                                    com.gempukku.swccgo.common.Zone z = pc.getZone();
                                    if (z == null || !z.isInPlay()) continue;
                                    if (pc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                    boolean armed = false;
                                    java.util.List<PhysicalCard> atts = pullGs.getAttachedCards(pc);
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
                            logger.warn("V67ar UNIVERSAL BLOCK (pull): '{}' — all {} chars armed, no 2nd weapon allowed",
                                actionText, v67arArmed);
                        } else if (v67arUnarmed == 0) {
                            action.addReasoning(
                                "V67ao ORDER GATE: weapon pull blocked — no Rando character on table to hold the weapon. Deploy a character first!",
                                -9999.0f);
                            logger.warn("V67ao ORDER GATE: weapon pull '{}' blocked (no chars on table)",
                                actionText);
                        } else {
                            action.addReasoning(String.format(
                                "V67am WEAPON PULL (universal, tier 1): %d unarmed character(s) on table — pull weapon from reserve!",
                                v67arUnarmed), 600.0f);
                            logger.warn("V67am WEAPON PULL: '{}' adds weapon ({}) → +600 ({} unarmed targets)",
                                actionText, v67mReason, v67arUnarmed);
                        }
                    }

                    // === V67am (Steve, 2026-05-07): UNIVERSAL DEVICE-PULL PRIORITY (tier 3) ===
                    //
                    // Devices are similar to weapons but mostly defensive. Order:
                    //   tier 1: Weapon pull from reserve  +600 (V67am)
                    //   tier 2: Weapon from hand          (V29.11/V67ad: +400-500)
                    //   tier 3: Device pull from reserve  +400 (THIS BLOCK)
                    //   tier 4: Device from hand          (DeployEvaluator default scoring)
                    //
                    // Detection: action/source-card text mentions device-class keyword.
                    // Same dual-source pattern as V67m.
                    boolean v67amAddsDevice = false;
                    String v67amDeviceReason = null;
                    String[] v67amDeviceKeywords = new String[] {
                        "device", "comlink", "bionic", "sensor", "lockblade",
                        "restraints", "macrobinoculars", "scanner", "datapad",
                        "tool kit", "fusion cutter", "bowcaster"  // bowcaster is dual-classed
                    };
                    for (String kw : v67amDeviceKeywords) {
                        if (textLower.contains(kw)) {
                            v67amAddsDevice = true;
                            v67amDeviceReason = "actionText contains device keyword '" + kw + "'";
                            break;
                        }
                    }
                    if (!v67amAddsDevice && cardId != null && pullGs != null) {
                        try {
                            PhysicalCard sc = pullGs.findCardById(Integer.parseInt(cardId));
                            if (sc != null && sc.getBlueprint() != null) {
                                String gt = sc.getBlueprint().getGameText();
                                if (gt != null) {
                                    java.util.List<String> tgts =
                                        com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle
                                            .parseSourceCardPullTargets(gt);
                                    for (String t : tgts) {
                                        for (String kw : v67amDeviceKeywords) {
                                            if (t.contains(kw)) {
                                                v67amAddsDevice = true;
                                                v67amDeviceReason = "source card '" + sc.getTitle()
                                                    + "' targets device-like '" + t + "'";
                                                break;
                                            }
                                        }
                                        if (v67amAddsDevice) break;
                                    }
                                }
                            }
                        } catch (Exception e) { /* ignore */ }
                    }
                    if (v67amAddsDevice && !v67lAddsLocation && !v67mAddsWeapon) {
                        // V67ar (mirror): same UNIVERSAL ONE-DEVICE-PER-CHARACTER rule.
                        // Count UNARMED-by-device characters. (Most cards allow only one
                        // device per character; for safety we use the same all-armed gate
                        // as weapons since a single Rando char rarely needs both.)
                        int v67arDevUnarmed = 0;
                        int v67arDevArmed = 0;
                        if (pullGs != null && context.getPlayerId() != null) {
                            try {
                                for (PhysicalCard pc : pullGs.getAllPermanentCards()) {
                                    if (pc == null || pc.getBlueprint() == null) continue;
                                    if (!context.getPlayerId().equals(pc.getOwner())) continue;
                                    com.gempukku.swccgo.common.Zone z = pc.getZone();
                                    if (z == null || !z.isInPlay()) continue;
                                    if (pc.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                                    boolean hasDevice = false;
                                    java.util.List<PhysicalCard> atts = pullGs.getAttachedCards(pc);
                                    if (atts != null) {
                                        for (PhysicalCard a : atts) {
                                            if (a != null && a.getBlueprint() != null
                                                    && a.getBlueprint().getCardCategory() == CardCategory.DEVICE) {
                                                hasDevice = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (hasDevice) v67arDevArmed++;
                                    else v67arDevUnarmed++;
                                }
                            } catch (Exception e) { /* ignore */ }
                        }
                        if (v67arDevUnarmed == 0 && v67arDevArmed > 0) {
                            action.addReasoning(String.format(
                                "V67ar UNIVERSAL BLOCK: every Rando character (%d) already has a device — second device on ANY character is wasteful!",
                                v67arDevArmed), -9999.0f);
                            logger.warn("V67ar UNIVERSAL BLOCK (device pull): '{}' — all {} chars have devices",
                                actionText, v67arDevArmed);
                        } else if (v67arDevUnarmed == 0) {
                            action.addReasoning(
                                "V67ao ORDER GATE: device pull blocked — no Rando character on table to host the device. Deploy a character first!",
                                -9999.0f);
                            logger.warn("V67ao ORDER GATE: device pull '{}' blocked (no chars on table)",
                                actionText);
                        } else {
                            action.addReasoning(
                                "V67am DEVICE PULL (universal, tier 3): pull device from reserve via card text — defensive support, fires after weapons.",
                                400.0f);
                            logger.warn("V67am DEVICE PULL: '{}' adds device ({}) → +400",
                                actionText, v67amDeviceReason);
                        }
                    }

                    // === V67ak (Steve, 2026-05-07): KEY-CHARACTER PULL PRIORITY ===
                    //
                    // If the source-card pull's parsed targets include a strategy-key
                    // character name (matched against ObjectiveAnalyzer tokens from
                    // objective + epic events), give the pull action a strong priority bonus.
                    // Mirrors V67ak in DeployEvaluator for hand deploys.
                    //
                    // Skip if the matched character is already on table — repeated key-char
                    // pulls would be wasteful (e.g., uniqueness blocks second Vader).
                    if (cardId != null && pullGs != null && context.getObjectiveAnalyzer() != null
                            && context.getObjectiveAnalyzer().isAnalyzed()) {
                        try {
                            PhysicalCard srcPc = pullGs.findCardById(Integer.parseInt(cardId));
                            if (srcPc != null && srcPc.getBlueprint() != null) {
                                String srcGT = srcPc.getBlueprint().getGameText();
                                if (srcGT != null) {
                                    java.util.List<String> srcTargets =
                                        com.gempukku.swccgo.ai.models.chosenone.strategy.DeckOracle
                                            .parseSourceCardPullTargets(srcGT);
                                    com.gempukku.swccgo.ai.models.chosenone.strategy.ObjectiveAnalyzer akObj =
                                        context.getObjectiveAnalyzer();
                                    java.util.Set<String> akTokens =
                                        akObj.getStrategyCharacterTokens(
                                            context.getGame(), context.getPlayerId());
                                    String matchedTok = null;
                                    for (String t : srcTargets) {
                                        String tl = t.toLowerCase(java.util.Locale.ROOT);
                                        for (String tok : akTokens) {
                                            if (tl.contains(tok)) { matchedTok = tok; break; }
                                        }
                                        if (matchedTok != null) break;
                                    }
                                    if (matchedTok != null) {
                                        // Check if persona role already filled on table
                                        boolean filled = false;
                                        for (PhysicalCard ex : pullGs.getAllPermanentCards()) {
                                            if (ex == null || ex.getBlueprint() == null) continue;
                                            if (!context.getPlayerId().equals(ex.getOwner())) continue;
                                            com.gempukku.swccgo.common.Zone ez = ex.getZone();
                                            if (ez == null || !ez.isInPlay()) continue;
                                            if (ex.getBlueprint().getCardCategory()
                                                    != com.gempukku.swccgo.common.CardCategory.CHARACTER) continue;
                                            String et = ex.getTitle();
                                            if (et != null && et.toLowerCase(java.util.Locale.ROOT).contains(matchedTok)) {
                                                filled = true; break;
                                            }
                                        }
                                        if (!filled) {
                                            action.addReasoning(String.format(
                                                "V67ak KEY-CHARACTER PULL: '%s' would pull '%s' (named in objective/epic-event) — flip-critical!",
                                                srcPc.getTitle(), matchedTok), 800.0f);
                                            logger.warn("V67ak KEY-CHARACTER PULL: source={} pulls token={} → +800",
                                                srcPc.getTitle(), matchedTok);
                                        } else {
                                            logger.info("V67ak KEY-CHARACTER PULL skip: token={} already filled on table",
                                                matchedTok);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) { logger.debug("V67ak (pull) error: {}", e.getMessage()); }
                    }

                    // V67ao: Per Steve, no soft penalties for character pulls when locations
                    // are in hand. The V67ai location tier bonuses (+1400 to +2000) already
                    // outscore character pulls (V67ak +800, others lower), so Combined
                    // Evaluator picks locations first naturally. The hard-block ordering
                    // gates only apply where the action would actually FAIL (weapon/device
                    // pull with no character host — see V67ao gates inside V67am blocks).
                }
            }

            // ========== Default/Unknown ==========
            else {
                action.addReasoning("Unknown action type", 0.0f);
                logger.trace("Unrecognized action: {}", actionText);
            }

            actions.add(action);
        }

        return actions;
    }

    // ========== Helper Methods ==========

    private void evaluateActivateForce(EvaluatedAction action, DecisionContext context) {
        // V38.3: ALWAYS activate Force. No exceptions.
        action.addReasoning("V38.3 ALWAYS ACTIVATE: Force is currency!", 500.0f);
    }

    private void evaluateForceDrain(EvaluatedAction action, DecisionContext context, String locationCardId) {
        // Force drains are generally good unless under Battle Order rules
        // Ported from Python action_text_evaluator.py lines 351-493

        GameState gameState = context.getGameState();
        String playerId = context.getPlayerId();

        // ========== V24.15: NEVER force drain at 0! ==========
        // Draining for 0 does nothing but opens us up to Surprise Assault and other traps.
        // Check the actual drain amount at the location before committing.
        if (gameState != null && locationCardId != null) {
            try {
                PhysicalCard drainLocation = gameState.findCardById(Integer.parseInt(locationCardId));
                if (drainLocation != null) {
                    SwccgGame drainGame = context.getGame();
                    if (drainGame != null) {
                        float drainAmount = drainGame.getModifiersQuerying().getForceDrainAmount(
                            gameState, drainLocation, playerId);
                        if (drainAmount <= 0) {
                            action.addReasoning("V24.15 DRAIN BLOCK: Force drain would be 0 — pointless and opens us to Surprise Assault!", -9999.0f);
                            logger.warn("V24.15 DRAIN BLOCK: Force drain at {} would be {} — HARD BLOCKING to avoid Surprise Assault trap!",
                                drainLocation.getTitle(), drainAmount);
                            return;
                        } else {
                            logger.info("V24.15 DRAIN CHECK: Force drain at {} will be {} — proceeding", drainLocation.getTitle(), drainAmount);
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("V24.15: Error checking drain amount: {}", e.getMessage());
            }
        }

        // === V25: SIMPLE TRICKS AND NONSENSE — avoid draining at non-battleground sites ===
        // Simple Tricks cancels Force drains at non-battleground sites. If opponent has it
        // on table, draining at non-battleground sites is pointless (gets cancelled).
        // Check for this BEFORE spending resources on the drain.
        if (gameState != null && locationCardId != null) {
            try {
                PhysicalCard drainLoc = gameState.findCardById(Integer.parseInt(locationCardId));
                if (drainLoc != null) {
                    SwccgCardBlueprint locBp = drainLoc.getBlueprint();
                    // Check if the drain location is a non-battleground site
                    boolean isBattlegroundSite = false;
                    if (locBp != null) {
                        // A battleground site typically has force icons from both sides
                        isBattlegroundSite = locBp.hasIcon(com.gempukku.swccgo.common.Icon.DARK_FORCE)
                            && locBp.hasIcon(com.gempukku.swccgo.common.Icon.LIGHT_FORCE);
                    }

                    if (!isBattlegroundSite) {
                        // Check if opponent has Simple Tricks And Nonsense on table
                        String opponentId = gameState.getOpponent(playerId);
                        boolean simpleTricksOnTable = false;
                        if (opponentId != null) {
                            for (PhysicalCard card : gameState.getAllPermanentCards()) {
                                if (card == null || !opponentId.equals(card.getOwner())) continue;
                                com.gempukku.swccgo.common.Zone zone = card.getZone();
                                if (zone == null || !zone.isInPlay()) continue;
                                String cardTitle = card.getTitle();
                                if (cardTitle != null && cardTitle.contains("Simple Tricks")) {
                                    simpleTricksOnTable = true;
                                    break;
                                }
                            }
                        }

                        if (simpleTricksOnTable) {
                            action.addReasoning("V25 SIMPLE TRICKS: Non-battleground drain will be CANCELLED by Simple Tricks And Nonsense!", -9999.0f);
                            logger.warn("V25 SIMPLE TRICKS: BLOCKING drain at non-battleground {} — opponent has Simple Tricks!",
                                drainLoc.getTitle());
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("V25 Simple Tricks check error: {}", e.getMessage());
            }
        }

        // Check if we're under Battle Order rules (force drains cost +3 extra)
        // Battle Order is typically triggered when opponent has mains + specific cards
        boolean underBattleOrder = false;
        com.gempukku.swccgo.ai.models.chosenone.strategy.StrategyController strategyController = context.getStrategyController();
        if (strategyController != null) {
            underBattleOrder = strategyController.isUnderBattleOrderRules();
        }

        // Get available force
        int forceAvailable = 0;
        if (gameState != null) {
            forceAvailable = gameState.getForcePileSize(playerId);
        }

        // Check if we have any deployable cards in hand
        boolean hasDeployableCard = false;
        int cheapestDeployCost = Integer.MAX_VALUE;
        if (gameState != null) {
            List<PhysicalCard> hand = gameState.getHand(playerId);
            if (hand != null) {
                for (PhysicalCard card : hand) {
                    if (card.getBlueprint() != null) {
                        CardCategory category = card.getBlueprint().getCardCategory();
                        if (category == CardCategory.CHARACTER || category == CardCategory.STARSHIP ||
                            category == CardCategory.VEHICLE) {
                            hasDeployableCard = true;
                            Float deployCost = card.getBlueprint().getDeployCost();
                            if (deployCost != null && deployCost < cheapestDeployCost) {
                                cheapestDeployCost = deployCost.intValue();
                            }
                        }
                    }
                }
            }
        }

        if (underBattleOrder) {
            // Under Battle Order rules - force drains cost extra (+3)
            int battleOrderCost = 3;

            // If we can't afford the drain (need 3+ force), skip it
            if (forceAvailable < battleOrderCost) {
                action.addReasoning("Under Battle Order but can't afford drain (need " + battleOrderCost + ", have " + forceAvailable + ")", VERY_BAD_DELTA);
                return;
            }

            // Check if we have deployable cards - if yes, save force for them
            if (hasDeployableCard && cheapestDeployCost < Integer.MAX_VALUE) {
                int forceAfterDrain = forceAvailable - battleOrderCost;
                if (forceAfterDrain < cheapestDeployCost) {
                    action.addReasoning("Under Battle Order - saving force for deploy (cost " + cheapestDeployCost + ")", VERY_BAD_DELTA);
                    return;
                }
            }

            // If NO deployable cards - drains are our only pressure! Boost them!
            if (!hasDeployableCard) {
                action.addReasoning("Under Battle Order but NO deployable cards - drain is our only pressure!", VERY_GOOD_DELTA + 20.0f);
                logger.info("🔥 FORCE DRAIN BOOST: No deployable cards under Battle Order");
                return;
            }

            // V52: After Turn 3, ALWAYS drain even under Battle Order.
            // Paying 3 force to drain 1 feels like a bad trade, but doing 0 damage is worse.
            // Over 5 turns, drain-1 every turn = 5 cards from opponent's life force.
            // Doing nothing = 0 damage = you lose. 1 damage > 0 damage. Always.
            int drainTurn = context.getTurnNumber();
            if (drainTurn >= 3) {
                action.addReasoning("V52 DRAIN ANYWAY: Turn " + drainTurn + " — any drain is damage, pay the Battle Order cost!", VERY_GOOD_DELTA);
                logger.warn("V52 DRAIN ANYWAY: Turn {} under Battle Order — draining anyway! 1 damage > 0 damage!", drainTurn);
            } else {
                // Turns 1-2: save force for deploys, Battle Order drain is too expensive early
                action.addReasoning("V48 BATTLE ORDER EARLY: Turn " + drainTurn + " — save force for deploys", VERY_BAD_DELTA);
                logger.warn("V48 BATTLE ORDER EARLY: Turn {} — skipping drain to save for deploys", drainTurn);
            }

        } else {
            // Not under Battle Order - drain is generally good
            if (!hasDeployableCard) {
                // NO deployable cards - drains are our only pressure!
                action.addReasoning("Force drain (no deployable cards - our only pressure!)", VERY_GOOD_DELTA + 20.0f);
                logger.info("🔥 FORCE DRAIN BOOST: No deployable cards");
            } else {
                action.addReasoning("Force drain is good", VERY_GOOD_DELTA);
            }
        }

        // === V52 FIX 14: MULTI-SITE DRAIN — Prioritize draining at multiple sites ===
        if (gameState != null && locationCardId != null) {
            try {
                SwccgGame drainGame14 = context.getGame();
                if (drainGame14 != null) {
                    int drainCapableSites = 0;
                    float thisDrainAmount = 0;
                    PhysicalCard thisDrainLoc = gameState.findCardById(Integer.parseInt(locationCardId));

                    for (PhysicalCard loc14 : gameState.getTopLocations()) {
                        if (loc14 == null) continue;
                        try {
                            float ourPower14 = drainGame14.getModifiersQuerying().getTotalPowerAtLocation(
                                gameState, loc14, playerId, false, false);
                            if (ourPower14 > 0) {
                                float drainAmt14 = drainGame14.getModifiersQuerying().getForceDrainAmount(
                                    gameState, loc14, playerId);
                                if (drainAmt14 > 0) {
                                    drainCapableSites++;
                                }
                            }
                        } catch (Exception e) { /* ignore */ }
                    }

                    if (thisDrainLoc != null) {
                        try {
                            thisDrainAmount = drainGame14.getModifiersQuerying().getForceDrainAmount(
                                gameState, thisDrainLoc, playerId);
                        } catch (Exception e) { /* ignore */ }
                    }

                    if (thisDrainAmount >= 3) {
                        action.addReasoning("V52 MULTI-DRAIN: Drain " + (int)thisDrainAmount + " — top priority!", 300.0f);
                        logger.warn("V52 MULTI-DRAIN: {} drains {} — +300", thisDrainLoc != null ? thisDrainLoc.getTitle() : "?", (int)thisDrainAmount);
                    } else if (thisDrainAmount >= 2) {
                        action.addReasoning("V52 MULTI-DRAIN: Drain " + (int)thisDrainAmount + " — high value!", 200.0f);
                        logger.warn("V52 MULTI-DRAIN: {} drains {} — +200", thisDrainLoc != null ? thisDrainLoc.getTitle() : "?", (int)thisDrainAmount);
                    } else if (drainCapableSites >= 2) {
                        action.addReasoning("V52 MULTI-DRAIN: " + drainCapableSites + " drain sites!", 100.0f);
                        logger.warn("V52 MULTI-DRAIN: {} — {} drain sites +100", thisDrainLoc != null ? thisDrainLoc.getTitle() : "?", drainCapableSites);
                    }
                }
            } catch (Exception e) {
                logger.debug("V52 MULTI-DRAIN: Error: {}", e.getMessage());
            }
        }
    }

    private void evaluatePlayCard(EvaluatedAction action, DecisionContext context) {
        int forcePile = context.getForcePileSize();
        if (forcePile == 0) {
            action.addReasoning("No Force available - can't play cards!", VERY_BAD_DELTA);
        } else if (forcePile <= 1) {
            action.addReasoning("Very low Force (" + forcePile + ") - unlikely to afford cards", BAD_DELTA);
        } else {
            // V24.5: No randomness — slight positive for playing cards when force available
            action.addReasoning("Generic play card — moderate priority", 5.0f);
        }
    }

    private void evaluateDestinyProtection(EvaluatedAction action, DecisionContext context) {
        Phase phase = context.getPhase();
        int turnNumber = context.getTurnNumber();

        // These cards only useful if battle is coming
        if (turnNumber <= 1) {
            action.addReasoning("SAVE for battle turn! Turn 1 rarely battles", VERY_BAD_DELTA);
        } else if (phase == Phase.BATTLE) {
            action.addReasoning("Protect destiny draws - IN BATTLE NOW!", VERY_GOOD_DELTA);
        } else if (phase == Phase.ACTIVATE || phase == Phase.CONTROL || phase == Phase.DEPLOY) {
            action.addReasoning("Protect destiny draws - battle opportunity exists", GOOD_DELTA);
        } else {
            action.addReasoning("Save destiny protection for clear battle turn", BAD_DELTA);
        }
    }

    private void evaluateSenseCancel(EvaluatedAction action, DecisionContext context, String actionText) {
        String textLower = actionText.toLowerCase();
        boolean isDestinyBased = textLower.contains("draw destiny") || textLower.contains("if destiny");

        // Check priority cards system for target value
        AiPriorityCards.SenseTargetResult senseResult = AiPriorityCards.getSenseTargetValue(actionText);

        if (isDestinyBased) {
            if (senseResult.isHighValue && senseResult.score >= 80) {
                action.addReasoning("Destiny cancel critical target: " + senseResult.matchedPattern, 10.0f);
            } else {
                action.addReasoning("Destiny-based cancel (unreliable, skip)", -10.0f);
            }
        } else if (senseResult.isHighValue && senseResult.score >= 80) {
            action.addReasoning("Cancel CRITICAL target: " + senseResult.matchedPattern + "!", VERY_GOOD_DELTA + 20.0f);
        } else if (senseResult.isHighValue && senseResult.score >= 60) {
            action.addReasoning("Cancel high-value target: " + senseResult.matchedPattern, VERY_GOOD_DELTA);
        } else if (senseResult.isHighValue) {
            action.addReasoning("Cancel valuable target: " + senseResult.matchedPattern, GOOD_DELTA + 15.0f);
        } else if (textLower.contains("force drain")) {
            // V52: NEVER cancel your OWN force drain! Surprise Assault on own drain = self-sabotage.
            if (context.isMyTurn()) {
                action.addReasoning("V52 NEVER SELF-CANCEL DRAIN: Canceling own force drain is suicide!", -9999.0f);
                logger.warn("V52 SELF-CANCEL BLOCKED: Tried to cancel OWN force drain — HARD BLOCKED!");
            } else {
                action.addReasoning("Cancel opponent's force drain", GOOD_DELTA + 5.0f);
            }
        } else if (!context.isMyTurn()) {
            action.addReasoning("Cancel opponent interrupt (their turn)", GOOD_DELTA);
        } else {
            action.addReasoning("Cancel opponent interrupt (our turn)", 15.0f);
        }
    }

    private void evaluateHoujixGhhhk(EvaluatedAction action, DecisionContext context) {
        // These are CRITICAL survival cards
        // For now, give moderate positive score - ideally we'd check damage remaining
        action.addReasoning("Cancel battle damage - valuable survival card", GOOD_DELTA);

        // TODO: Add proper damage analysis when we have access to battle state
        // Check attrition/damage remaining and cards available to forfeit
    }

    private void evaluateTakeIntoHand(EvaluatedAction action, DecisionContext context, String actionText, String textLower) {
        if (textLower.contains("palpatine")) {
            action.addReasoning("Avoid taking Palpatine", BAD_DELTA);
            return;
        }

        // V63 LOST PILE GUARD: "take a character into hand from Lost Pile"
        // (Jedi Levitation etc.) needs a matching card in Lost Pile. If there
        // isn't one, the search FAILS and opponent sees our entire Lost Pile.
        if (textLower.contains("from lost pile")) {
            GameState lpGs = context.getGameState();
            String lpPid = context.getPlayerId();
            if (lpGs != null && lpPid != null) {
                int matchingInLostPile = 0;
                try {
                    java.util.List<PhysicalCard> lp = lpGs.getLostPile(lpPid);
                    if (lp != null) {
                        boolean wantsCharacter = textLower.contains("character");
                        for (PhysicalCard c : lp) {
                            if (c == null || c.getBlueprint() == null) continue;
                            CardCategory cat = c.getBlueprint().getCardCategory();
                            if (wantsCharacter && cat != CardCategory.CHARACTER) continue;
                            matchingInLostPile++;
                        }
                    }
                } catch (Exception e) { /* ignore */ }
                if (matchingInLostPile == 0) {
                    action.addReasoning(
                        "V63 LOST PILE EMPTY: no matching target in Lost Pile — search will FAIL and reveal our pile!",
                        -9999.0f);
                    logger.warn("V63 LOST PILE EMPTY: '{}' has 0 matching targets — hard-blocked", actionText);
                    return;
                }
                logger.info("V63 LOST PILE OK: '{}' — {} matching targets in Lost Pile",
                    actionText, matchingInLostPile);
            }
            action.addReasoning("Take card into hand from Lost Pile", GOOD_DELTA);
            return;
        }

        String blueprintId = extractBlueprintFromText(actionText);
        if (blueprintId != null) {
            // Could look up card metadata here if needed
            action.addReasoning("Take card into hand", GOOD_DELTA);
        } else {
            action.addReasoning("Taking card into hand", GOOD_DELTA);
        }
    }

    /**
     * Evaluate barrier card (Imperial/Rebel Barrier) usage.
     * Ported from Python action_text_evaluator.py lines 973-1055
     *
     * Use barriers when:
     *   - Location IS contested (both players present)
     *   - Target is a significant threat (high power)
     *   - We're not already winning overwhelmingly
     * Save barriers when:
     *   - Location not contested (no point)
     *   - We're already dominating the location
     *   - Target already has a barrier on it this turn!
     */
    private void evaluateBarrier(EvaluatedAction action, DecisionContext context, String actionText) {
        String targetCardName = extractCardNameFromPreventText(actionText);
        int currentTurn = context.getTurnNumber();

        // Reset barrier tracking on new turn
        if (currentTurn != barrierTurn) {
            barrieredTargets.clear();
            barrierTurn = currentTurn;
        }

        // Check if we already barriered this target
        if (targetCardName != null && barrieredTargets.contains(targetCardName.toLowerCase())) {
            action.addReasoning("Already barriered " + targetCardName + " this turn - wasteful!", VERY_BAD_DELTA);
            return;
        }

        // V35.1 SELF-BARRIER BLOCK: Never barrier our own characters!
        GameState gameState = context.getGameState();
        String playerId = context.getPlayerId();
        if (gameState != null && playerId != null && targetCardName != null) {
            String targetLower = targetCardName.toLowerCase();
            for (PhysicalCard card : gameState.getAllPermanentCards()) {
                if (card == null || card.getTitle() == null) continue;
                if (card.getTitle().toLowerCase().contains(targetLower) || targetLower.contains(card.getTitle().toLowerCase())) {
                    if (playerId.equals(card.getOwner())) {
                        action.addReasoning(String.format(
                            "V35.1 SELF-BARRIER BLOCK: %s is OUR character — NEVER prevent our own from battling!",
                            targetCardName), -9999.0f);
                        logger.warn("V35.1 SELF-BARRIER: Blocking barrier on OWN character {} (-9999)", targetCardName);
                        return;
                    }
                    break;
                }
            }
        }

        // Try to analyze the target and location
        float targetPower = 0;
        float ourPower = 0;
        float theirPower = 0;
        boolean locationContested = false;

        if (gameState != null && playerId != null && targetCardName != null) {
            String opponentId = gameState.getOpponent(playerId);

            // Find the target card and analyze location
            for (PhysicalCard card : gameState.getAllPermanentCards()) {
                if (card == null) continue;
                String title = card.getTitle();
                if (title == null) continue;

                // Match by name
                if (title.toLowerCase().contains(targetCardName.toLowerCase()) ||
                    targetCardName.toLowerCase().contains(title.toLowerCase())) {

                    // Found the target - check its power
                    SwccgCardBlueprint blueprint = card.getBlueprint();
                    if (blueprint != null && blueprint.hasPowerAttribute()) {
                        Float power = blueprint.getPower();
                        if (power != null) {
                            targetPower = power;
                        }
                    }

                    // Find location and calculate power
                    PhysicalCard location = card.getAtLocation();
                    if (location != null) {
                        boolean hasOurPresence = false;
                        boolean hasTheirPresence = false;

                        for (PhysicalCard locCard : gameState.getCardsAtLocation(location)) {
                            if (locCard == null) continue;
                            String owner = locCard.getOwner();
                            SwccgCardBlueprint bp = locCard.getBlueprint();
                            if (bp == null) continue;

                            // Check presence
                            if (playerId.equals(owner)) {
                                hasOurPresence = true;
                                if (bp.hasPowerAttribute()) {
                                    Float power = bp.getPower();
                                    if (power != null) ourPower += power;
                                }
                            } else if (opponentId != null && opponentId.equals(owner)) {
                                hasTheirPresence = true;
                                if (bp.hasPowerAttribute()) {
                                    Float power = bp.getPower();
                                    if (power != null) theirPower += power;
                                }
                            }
                        }
                        locationContested = hasOurPresence && hasTheirPresence;
                    }
                    break;
                }
            }
        }

        logger.debug("🚧 Barrier analysis: {} (power {}) contested={}, our={}, their={}",
            targetCardName, targetPower, locationContested, ourPower, theirPower);

        // V48: Check if WE have any presence at the target's location
        boolean weHavePresence = ourPower > 0;

        // Apply scoring based on situation
        if (!weHavePresence) {
            // V48: We have NOBODY at this location — barrier is completely useless!
            action.addReasoning("V48 BARRIER USELESS: No friendly presence at location — serves no purpose!", -9999.0f);
            logger.warn("V48 BARRIER BLOCK: No friendly presence at target location — HARD BLOCK!");
        } else if (!locationContested) {
            // Location NOT contested - save barrier for when we need it
            action.addReasoning("Save barrier - location not contested", BAD_DELTA);
        } else if (ourPower >= theirPower + 8) {
            // We're already dominating - don't waste the barrier
            action.addReasoning("Save barrier - already dominating (" + (int)ourPower + " vs " + (int)theirPower + ")", BAD_DELTA);
        } else if (targetPower >= 5) {
            // High-power target at contested location - VERY valuable!
            action.addReasoning("Barrier on HIGH POWER target (" + (int)targetPower + ")!", VERY_GOOD_DELTA);
            if (targetCardName != null) {
                barrieredTargets.add(targetCardName.toLowerCase());
            }
        } else if (theirPower >= ourPower) {
            // They're winning or tied - barrier is valuable
            action.addReasoning("Barrier to protect (losing " + (int)ourPower + " vs " + (int)theirPower + ")", GOOD_DELTA + 10.0f);
            if (targetCardName != null) {
                barrieredTargets.add(targetCardName.toLowerCase());
            }
        } else {
            // We're ahead but not dominating - still useful
            action.addReasoning("Barrier at contested location", GOOD_DELTA);
            if (targetCardName != null) {
                barrieredTargets.add(targetCardName.toLowerCase());
            }
        }
    }

    private void evaluateEmbark(EvaluatedAction action, DecisionContext context, String actionText) {
        String blueprintId = extractBlueprintFromText(actionText);

        // Embarking pilots onto ships is good, non-pilots is usually bad
        // For now, give neutral score - could be improved with pilot detection
        action.addReasoning("Embark action", 0.0f);
    }

    private void evaluateGrab(EvaluatedAction action, DecisionContext context, String actionText) {
        // V53: Grabber shields (Allegations / A Tragedy) must ONLY grab OPPONENT's interrupts.
        // NEVER grab your own interrupts — that's self-sabotage.
        // Use game state to check card ownership when possible, fall back to name matching.

        Side mySide = context.getSide();
        GameState grabGs = context.getGameState();
        String textLower = actionText.toLowerCase();

        // V53: Try to determine ownership from game state (most reliable)
        boolean confirmedOwnCard = false;
        boolean confirmedOpponentCard = false;
        if (grabGs != null && context.getPlayerId() != null) {
            try {
                // Check if any card IDs in context belong to us
                String pid = context.getPlayerId();
                String oid = grabGs.getOpponent(pid);
                for (String cardId : context.getCardIds()) {
                    PhysicalCard grabCard = grabGs.findCardById(Integer.parseInt(cardId));
                    if (grabCard != null) {
                        if (pid.equals(grabCard.getOwner())) confirmedOwnCard = true;
                        if (oid != null && oid.equals(grabCard.getOwner())) confirmedOpponentCard = true;
                    }
                }
            } catch (Exception e) { /* fall through to name matching */ }
        }

        if (confirmedOwnCard && !confirmedOpponentCard) {
            action.setScore(-9999.0f);
            action.addReasoning("V53 NEVER GRAB OWN: Grabbing own interrupt is suicide!", -9999.0f);
            logger.warn("V53 GRAB BLOCKED: Confirmed own card — HARD BLOCKED! {}", actionText);
            return;
        } else if (confirmedOpponentCard) {
            action.addReasoning("V53 GRAB OPPONENT: Confirmed opponent's interrupt — grab it!", GOOD_DELTA);
            logger.warn("V53 GRAB: Confirmed opponent card — grabbing! {}", actionText);
            return;
        }

        // Fallback: name-based side detection
        boolean looksLightSide = textLower.contains("rebel") || textLower.contains("jedi") ||
                                  textLower.contains("alliance") || textLower.contains("luke") ||
                                  textLower.contains("leia") || textLower.contains("han solo") ||
                                  textLower.contains("chewie") || textLower.contains("yoda") ||
                                  textLower.contains("obi-wan") || textLower.contains("padme");
        boolean looksDarkSide = textLower.contains("imperial") || textLower.contains("sith") ||
                                 textLower.contains("vader") || textLower.contains("emperor") ||
                                 textLower.contains("stormtrooper") || textLower.contains("death star") ||
                                 textLower.contains("maul") || textLower.contains("dooku") ||
                                 textLower.contains("boba fett") || textLower.contains("jango");

        if (mySide == Side.DARK && looksLightSide) {
            action.addReasoning("Grab Light side card (we are Dark)", GOOD_DELTA);
        } else if (mySide == Side.LIGHT && looksDarkSide) {
            action.addReasoning("Grab Dark side card (we are Light)", GOOD_DELTA);
        } else if (mySide == Side.DARK && looksDarkSide) {
            action.setScore(-9999.0f);
            action.addReasoning("V53 NEVER GRAB OWN: Grabbing own Dark card!", -9999.0f);
            logger.warn("V53 GRAB BLOCKED: Likely own Dark card — {}", actionText);
        } else if (mySide == Side.LIGHT && looksLightSide) {
            action.setScore(-9999.0f);
            action.addReasoning("V53 NEVER GRAB OWN: Grabbing own Light card!", -9999.0f);
            logger.warn("V53 GRAB BLOCKED: Likely own Light card — {}", actionText);
        } else {
            // Unknown owner — only grab if it's opponent's turn (their interrupt just played)
            if (!context.isMyTurn()) {
                action.addReasoning("Grab unknown card (opponent's turn — likely theirs)", GOOD_DELTA);
            } else {
                action.addReasoning("V53 GRAB CAUTION: Unknown owner on our turn — avoid!", -200.0f);
                logger.info("V53 GRAB CAUTION: Unknown owner on our turn, avoiding: {}", actionText);
            }
        }
    }

    private void evaluateBreakCover(EvaluatedAction action, DecisionContext context, String actionText) {
        // V53: Breaking spy cover depends on context:
        // - Break OPPONENT's spy: always good (expose their spy)
        // - Break OWN spy when we have a friendly character at that location: +500
        //   (flip the spy to protect our deployed character — instant buddy system)
        // - Break OWN spy when we have NO friendly character there: -500
        //   (don't blow cover for nothing)

        Side mySide = context.getSide();
        GameState gameState = context.getGameState();

        // V59 OWNER RESOLUTION: Look up the spy's actual owner via cardId first.
        // FIXES Issue #6 from peaceful-pike replay: actionText was just "Break cover"
        // with no card name, so regex matching failed and we fell through to
        // the "unknown owner" -30 branch. Now we resolve via PhysicalCard.
        Boolean ownerIsUs = null;  // null = unknown, true = our spy, false = opponent's
        try {
            List<String> ctxCardIds = context.getCardIds();
            if (ctxCardIds != null && !ctxCardIds.isEmpty() && gameState != null) {
                String cardIdStr = ctxCardIds.get(0);
                PhysicalCard spyCard = gameState.findCardById(Integer.parseInt(cardIdStr));
                if (spyCard != null && spyCard.getOwner() != null) {
                    ownerIsUs = spyCard.getOwner().equals(context.getPlayerId());
                    logger.info("V59 BREAK COVER OWNER: spy {} owner={} (we are {})",
                        spyCard.getTitle(), spyCard.getOwner(), context.getPlayerId());
                }
            }
        } catch (Exception e) {
            logger.debug("V59 BREAK COVER: Error resolving owner: {}", e.getMessage());
        }

        // Fallback: Determine side from card name patterns in action text
        String textLower = actionText.toLowerCase();
        boolean looksLightSide = textLower.contains("rebel") || textLower.contains("bothan") ||
                                  textLower.contains("alliance") || textLower.contains("leia") ||
                                  textLower.contains("mon mothma") || textLower.contains("orrimaarko");
        boolean looksDarkSide = textLower.contains("imperial") || textLower.contains("ism-agent") ||
                                 textLower.contains("empire") || textLower.contains("probe droid") ||
                                 textLower.contains("mara jade");

        boolean isOwnSpy = (ownerIsUs != null && ownerIsUs)
            || (ownerIsUs == null && ((mySide == Side.DARK && looksDarkSide) || (mySide == Side.LIGHT && looksLightSide)));
        boolean isOpponentSpy = (ownerIsUs != null && !ownerIsUs)
            || (ownerIsUs == null && ((mySide == Side.DARK && looksLightSide) || (mySide == Side.LIGHT && looksDarkSide)));

        if (isOpponentSpy) {
            action.addReasoning("Break opponent's spy cover — expose them!", GOOD_DELTA);
        } else if (isOwnSpy) {
            // V53: Check if we have a non-spy friendly character at the spy's location.
            // If yes, flip the spy to fight alongside them (+500).
            // If no, don't blow cover for nothing (-500).
            boolean friendlyCharAtSpyLocation = false;
            if (gameState != null) {
                try {
                    String pid = context.getPlayerId();
                    for (PhysicalCard loc : gameState.getTopLocations()) {
                        if (loc == null) continue;
                        boolean hasOurSpy = false;
                        boolean hasOurCharacter = false;
                        for (PhysicalCard c : gameState.getCardsAtLocation(loc)) {
                            if (c == null || !pid.equals(c.getOwner())) continue;
                            if (c.isUndercover()) {
                                hasOurSpy = true;
                            } else if (c.getBlueprint() != null
                                && c.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                                hasOurCharacter = true;
                            }
                        }
                        if (hasOurSpy && hasOurCharacter) {
                            friendlyCharAtSpyLocation = true;
                            break;
                        }
                    }
                } catch (Exception e) { /* ignore */ }
            }

            if (friendlyCharAtSpyLocation) {
                action.addReasoning("V53 FLIP SPY: We have a character at spy's location — flip spy to protect them!", 500.0f);
                logger.warn("V53 FLIP SPY: Breaking own spy cover — friendly character present, +500!");
            } else {
                action.addReasoning("V53 KEEP COVER: No friendly character at spy location — don't blow cover!", -500.0f);
                logger.warn("V53 KEEP COVER: No friendly at spy location — blocking break cover, -500");
            }
        } else {
            // Unknown spy - check for friendly presence as tiebreaker
            action.addReasoning("Break cover (spy owner unknown - cautious)", BAD_DELTA);
            logger.info("Break cover owner unknown, avoiding: {}", actionText);
        }
    }

    // ========== Utility Methods ==========

    private String extractBlueprintFromText(String actionText) {
        if (actionText == null) return null;
        Matcher matcher = BLUEPRINT_PATTERN.matcher(actionText);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractCardNameFromPreventText(String actionText) {
        // Pattern: "Prevent <CARD NAME> from battling or moving"
        if (actionText != null && actionText.contains("Prevent") &&
            actionText.contains("from battling or moving")) {
            int startIdx = actionText.indexOf("Prevent") + "Prevent ".length();
            int endIdx = actionText.indexOf(" from battling or moving");
            if (startIdx > 0 && endIdx > startIdx) {
                return actionText.substring(startIdx, endIdx).trim();
            }
        }
        return null;
    }

    /**
     * Check if there are valid (non-HIT) weapon targets at the battle location.
     *
     * In SWCCG, firing at already-hit targets is wasteful since they're
     * already damaged. This method returns true only if there are unhit
     * enemy cards at the battle location.
     *
     * Ported from Python action_text_evaluator.py valid target check.
     */
    private boolean checkForValidWeaponTargets(DecisionContext context) {
        GameState gameState = context.getGameState();
        if (gameState == null) {
            return true;  // Default to allowing fire if we can't check
        }

        try {
            // Get the battle location
            PhysicalCard battleLocation = gameState.getBattleLocation();
            if (battleLocation == null) {
                return true;  // Not in battle, allow fire
            }

            // Find enemy cards at battle location
            String playerId = context.getPlayerId();
            String opponentId = gameState.getOpponent(playerId);
            if (opponentId == null) {
                return true;  // Can't determine opponent
            }

            // Check all enemy cards at battle location
            boolean foundUnhitEnemy = false;
            for (PhysicalCard card : gameState.getAllPermanentCards()) {
                if (card == null) continue;

                // Must be enemy card
                if (!opponentId.equals(card.getOwner())) continue;

                // Must be at battle location
                PhysicalCard cardLocation = card.getAtLocation();
                if (cardLocation == null || !cardLocation.equals(battleLocation)) continue;

                // Must be a valid weapon target (character, starship, vehicle)
                SwccgCardBlueprint bp = card.getBlueprint();
                if (bp == null) continue;
                CardCategory cat = bp.getCardCategory();
                if (cat != CardCategory.CHARACTER && cat != CardCategory.STARSHIP && cat != CardCategory.VEHICLE) {
                    continue;
                }

                // Check if this card is NOT hit
                if (!card.isHit()) {
                    foundUnhitEnemy = true;
                    logger.debug("Found unhit enemy target: {}", card.getTitle());
                    break;  // Found at least one valid target
                }
            }

            if (!foundUnhitEnemy) {
                logger.info("🎯 All enemy targets at battle location are HIT - no valid weapon targets");
            }

            return foundUnhitEnemy;

        } catch (Exception e) {
            logger.debug("Error checking weapon targets: {}", e.getMessage());
            return true;  // Default to allowing fire on error
        }
    }
}
