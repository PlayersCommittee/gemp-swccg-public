package com.gempukku.swccgo.ai.models.rando.evaluators;

import com.gempukku.swccgo.ai.models.rando.RandoConfig;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Evaluates battle initiation decisions.
 *
 * Decision factors (from Python battle_evaluator.py):
 * - Power differential (my power - their power)
 * - Reserve deck (need cards for destiny draws)
 * - Strategic situation (ahead/behind on board/life force)
 *
 * Threat levels:
 * - CRUSH: Power advantage 8+ -> definitely battle
 * - FAVORABLE: Power advantage 5-7 -> battle recommended
 * - MARGINAL: Power advantage 2-4 -> battle worth considering
 * - RISKY: Power diff 0 to +1 -> cautious
 * - DANGEROUS: Power disadvantage -> avoid/retreat
 *
 * Ported from Python battle_evaluator.py
 */
public class BattleEvaluator extends ActionEvaluator {

    // Battle thresholds (power advantage needed)
    private static final int CRUSH_THRESHOLD = 8;      // Overwhelming advantage
    private static final int FAVORABLE_THRESHOLD = 5;  // Strong advantage
    private static final int MARGINAL_THRESHOLD = 2;   // Worth initiating
    private static final int RISKY_THRESHOLD = 0;      // Even or slight advantage

    // Minimum reserve deck for destiny draws
    private static final int MIN_RESERVE_FOR_BATTLE = 3;

    public BattleEvaluator() {
        super("Battle");
    }

    @Override
    public boolean canEvaluate(DecisionContext context) {
        // Handle CARD_ACTION_CHOICE with battle-related actions
        if (!"CARD_ACTION_CHOICE".equals(context.getDecisionType())) {
            return false;
        }

        // Check for battle phase or battle-related decision
        Phase phase = context.getPhase();
        String decisionText = context.getDecisionText();
        String decisionLower = decisionText != null ? decisionText.toLowerCase(Locale.ROOT) : "";

        // During battle phase
        if (phase == Phase.BATTLE) {
            return true;
        }

        // Or if decision text mentions battle/initiate
        if (decisionLower.contains("battle") || decisionLower.contains("initiate")) {
            return true;
        }

        // Check if any action mentions battle
        List<String> actionTexts = context.getActionTexts();
        if (actionTexts != null) {
            for (String actionText : actionTexts) {
                if (actionText != null) {
                    String actionLower = actionText.toLowerCase(Locale.ROOT);
                    if (actionLower.contains("initiate battle") || actionLower.contains("battle")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public List<EvaluatedAction> evaluate(DecisionContext context) {
        List<EvaluatedAction> actions = new ArrayList<>();
        GameState gameState = context.getGameState();

        logger.info("[BattleEvaluator] Evaluating battle decision");

        List<String> actionIds = context.getActionIds();
        List<String> actionTexts = context.getActionTexts();

        if (actionIds == null || actionTexts == null) {
            logger.warn("[BattleEvaluator] No action IDs or texts available");
            return actions;
        }

        logger.debug("[BattleEvaluator] Phase={}, actions={}", context.getPhase(), actionIds.size());

        // Get game state info
        int reserveDeck = context.getReserveDeckSize();
        int lifeForce = context.getLifeForce();
        int forcePile = context.getForcePileSize();

        // Calculate board position
        boolean isBehindOnLifeForce = false;
        boolean isAheadOnLifeForce = false;
        if (gameState != null) {
            String playerId = context.getPlayerId();
            String opponentId = gameState.getOpponent(playerId);
            if (opponentId != null) {
                int opponentLifeForce = gameState.getPlayerLifeForce(opponentId);
                isBehindOnLifeForce = lifeForce < opponentLifeForce - 5;
                isAheadOnLifeForce = lifeForce > opponentLifeForce + 5;
            }
        }

        for (int i = 0; i < actionIds.size(); i++) {
            String actionId = actionIds.get(i);
            String actionText = i < actionTexts.size() ? actionTexts.get(i) : "";
            String actionLower = actionText.toLowerCase(Locale.ROOT);

            // Only handle battle-related actions
            if (!actionLower.contains("battle") && !actionLower.contains("fire")) {
                continue;
            }

            EvaluatedAction action = new EvaluatedAction(
                actionId,
                ActionType.BATTLE,
                100.0f,  // V34: Raised base score from 50 — Rando needs to actually fight
                actionText
            );

            // === INITIATE BATTLE SCORING ===
            if (actionLower.contains("initiate battle")) {
                // V22.4: LOCATION-SPECIFIC battle evaluation
                // OLD BUG: Checked ALL locations — if ANY was favorable, approved initiation.
                // But the action is for a SPECIFIC location! Rando initiated battle at Dining Room
                // (3 vs 26 power) because another location was favorable.
                // NEW: Try to extract the specific location from the action text first.

                SwccgGame game = context.getGame();
                boolean foundFavorableBattle = false;
                boolean foundAnyContestedLocation = false;
                boolean checkedSpecificLocation = false;

                if (game != null && gameState != null) {
                    String playerId = context.getPlayerId();
                    String opponentId = gameState.getOpponent(playerId);

                    if (opponentId != null) {
                        try {
                            // V22.4: First try to find the SPECIFIC location this action targets
                            PhysicalCard targetLocation = null;
                            for (PhysicalCard location : gameState.getTopLocations()) {
                                String locTitle = location.getTitle();
                                if (locTitle != null && actionLower.contains(locTitle.toLowerCase(Locale.ROOT))) {
                                    targetLocation = location;
                                    break;
                                }
                            }

                            if (targetLocation != null) {
                                // V22.4: Evaluate THIS SPECIFIC location only
                                checkedSpecificLocation = true;
                                float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, targetLocation, playerId, false, false);
                                float theirPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                    gameState, targetLocation, opponentId, false, false);
                                float ourAbility = game.getModifiersQuerying().getTotalAbilityAtLocation(
                                    gameState, playerId, targetLocation);
                                float theirAbility = game.getModifiersQuerying().getTotalAbilityAtLocation(
                                    gameState, opponentId, targetLocation);
                                float powerDiff = ourPower - theirPower;
                                float abilityDiff = ourAbility - theirAbility;
                                float effectiveDiff = powerDiff + (abilityDiff * 2.5f);

                                logger.info("V22.4 [BattleEvaluator] SPECIFIC location {}: power={}/{}, ability={}/{}, effectiveDiff={}",
                                    targetLocation.getTitle(), ourPower, theirPower, ourAbility, theirAbility, effectiveDiff);

                                // === V29.7: WEAPON COMBAT AWARENESS ===
                                // Raw power comparison misses the massive advantage weapons provide.
                                // Vader (power 6) + lightsaber = hit + throw destiny = effectively +4-6 power.
                                // IHYN in hand adds 2-3 more battle destiny draws.
                                // Check our characters for weapons and adjust effective power.
                                float weaponBonus = 0;
                                boolean ourVaderHere = false;
                                boolean lukeHere = false;
                                boolean hasIHYN = false;
                                java.util.List<PhysicalCard> cardsHere = null;
                                try {
                                    cardsHere = gameState.getCardsAtLocation(targetLocation);
                                    for (PhysicalCard locCard : cardsHere) {
                                        if (locCard == null || locCard.getBlueprint() == null) continue;
                                        if (locCard.getBlueprint().getCardCategory() != com.gempukku.swccgo.common.CardCategory.CHARACTER) continue;

                                        String cardOwner = locCard.getOwner();
                                        String locCardTitle = locCard.getTitle() != null ? locCard.getTitle().toLowerCase(Locale.ROOT) : "";

                                        if (playerId.equals(cardOwner)) {
                                            // Our character — check for weapons
                                            if (locCardTitle.contains("vader")) ourVaderHere = true;
                                            java.util.List<PhysicalCard> attachments = gameState.getAttachedCards(locCard);
                                            if (attachments != null) {
                                                for (PhysicalCard att : attachments) {
                                                    if (att == null || att.getBlueprint() == null) continue;
                                                    if (att.getBlueprint().getCardCategory() == com.gempukku.swccgo.common.CardCategory.WEAPON) {
                                                        String wepTitle = att.getTitle() != null ? att.getTitle().toLowerCase(Locale.ROOT) : "";
                                                        if (wepTitle.contains("lightsaber")) {
                                                            weaponBonus += 5.0f; // Lightsaber: hit + throw destiny
                                                        } else {
                                                            weaponBonus += 3.0f; // Other weapons: hit
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (opponentId != null && opponentId.equals(cardOwner)) {
                                            // Opponent character — check for key targets
                                            if (locCardTitle.contains("luke")) lukeHere = true;
                                        }
                                    }

                                    // Check for IHYN in hand (devastating with Vader)
                                    if (ourVaderHere) {
                                        java.util.List<PhysicalCard> hand = gameState.getHand(playerId);
                                        if (hand != null) {
                                            for (PhysicalCard hCard : hand) {
                                                if (hCard != null && hCard.getTitle() != null
                                                    && hCard.getTitle().toLowerCase(Locale.ROOT).contains("i have you now")) {
                                                    hasIHYN = true;
                                                    weaponBonus += 3.0f; // Extra destiny draws
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.debug("V29.7: Error checking weapons for battle: {}", e.getMessage());
                                }

                                // Adjust effective diff with weapon bonus
                                float weaponEffectiveDiff = effectiveDiff + weaponBonus;
                                if (weaponBonus > 0) {
                                    logger.info("V29.7 WEAPON AWARENESS at {}: base effectiveDiff={}, weaponBonus=+{}, adjusted={}{}{}",
                                        targetLocation.getTitle(), effectiveDiff, weaponBonus, weaponEffectiveDiff,
                                        ourVaderHere ? " [VADER]" : "", hasIHYN ? " [IHYN]" : "");
                                }

                                // === V29.9: REBEL BARRIER RISK ASSESSMENT ===
                                // If opponent might have Rebel Barrier, they can EXCLUDE our strongest
                                // character from battle. If we initiate with Vader + Tarkin vs opponents,
                                // and they Barrier Vader, suddenly Tarkin fights ALONE vs everyone.
                                // When our strength is concentrated in one key character (Vader),
                                // initiating battle is very risky because Barrier negates that character.
                                float barrierRiskPenalty = 0;
                                if (ourVaderHere && ourPower > 0 && theirPower > 0 && cardsHere != null) {
                                    // Calculate power WITHOUT Vader to see what happens if he's Barriered
                                    float powerWithoutVader = 0;
                                    int charCountWithoutVader = 0;
                                    for (PhysicalCard locCard : cardsHere) {
                                        if (locCard == null || locCard.getBlueprint() == null) continue;
                                        if (locCard.getBlueprint().getCardCategory() != com.gempukku.swccgo.common.CardCategory.CHARACTER) continue;
                                        if (!playerId.equals(locCard.getOwner())) continue;
                                        String lcTitle = locCard.getTitle() != null ? locCard.getTitle().toLowerCase(Locale.ROOT) : "";
                                        if (lcTitle.contains("vader")) continue; // Skip Vader
                                        Float pw = locCard.getBlueprint().getPower();
                                        powerWithoutVader += (pw != null ? pw : 0);
                                        charCountWithoutVader++;
                                    }

                                    // If without Vader we'd be crushed, battle is risky
                                    float powerDeficitWithoutVader = theirPower - powerWithoutVader;
                                    if (powerDeficitWithoutVader > 5) {
                                        // Opponent can Barrier Vader and our remaining force gets destroyed
                                        barrierRiskPenalty = -150.0f;
                                        if (charCountWithoutVader <= 1) barrierRiskPenalty = -250.0f; // Solo char left = suicide
                                        if (powerDeficitWithoutVader > 10) barrierRiskPenalty -= 100.0f; // Even worse

                                        // V35: VADER EXPENDABILITY — In Hunt Down V, Vader is expendable.
                                        // Multiple copies in deck, lightsaber retrievable from Lost Pile.
                                        // Reduce barrier risk to encourage aggressive Vader battles.
                                        com.gempukku.swccgo.ai.models.rando.strategy.ObjectiveAnalyzer expendAnalyzer =
                                            context.getObjectiveAnalyzer();
                                        if (expendAnalyzer != null && expendAnalyzer.isAnalyzed() && expendAnalyzer.isHuntDownV()) {
                                            barrierRiskPenalty = barrierRiskPenalty * RandoConfig.VADER_EXPENDABILITY_FACTOR;
                                            logger.warn("V35 VADER EXPENDABLE: Barrier risk reduced to {} (Hunt Down — Vader is replaceable)",
                                                (int)barrierRiskPenalty);
                                        }

                                        action.addReasoning(String.format(
                                            "V29.9 BARRIER RISK: If opponent Barriers Vader, remaining power %.0f vs %.0f — %s!",
                                            powerWithoutVader, theirPower,
                                            charCountWithoutVader == 0 ? "NO ONE LEFT" : "crushed"),
                                            barrierRiskPenalty);
                                        logger.warn("V29.9 BARRIER RISK at {}: Without Vader: {} vs {} (deficit {}), penalty {}",
                                            targetLocation.getTitle(), (int)powerWithoutVader, (int)theirPower,
                                            (int)powerDeficitWithoutVader, (int)barrierRiskPenalty);
                                    }
                                }

                                // === V29.9: HUNT DOWN VADER BATTLE AGGRESSIVENESS ===
                                // When playing Hunt Down with armed Vader, he SHOULD be fighting.
                                // Boost battle initiation significantly when Vader is armed and present.
                                if (ourVaderHere && weaponBonus > 0) {
                                    com.gempukku.swccgo.ai.models.rando.strategy.ObjectiveAnalyzer battleObjAnalyzer =
                                        context.getObjectiveAnalyzer();
                                    if (battleObjAnalyzer != null && battleObjAnalyzer.isAnalyzed() && battleObjAnalyzer.isHuntDownV()) {
                                        float huntBonus = 80.0f;
                                        if (lukeHere) huntBonus = 200.0f;
                                        action.addReasoning(String.format(
                                            "V29.9 HUNT DOWN: Armed Vader should FIGHT! %s (+%.0f)",
                                            lukeHere ? "LUKE IS HERE — THIS IS THE OBJECTIVE!" : "Vader hunts and destroys!",
                                            huntBonus), huntBonus);
                                        logger.warn("V29.9 HUNT DOWN: Armed Vader aggressiveness boost +{} (Luke: {})",
                                            (int)huntBonus, lukeHere);
                                    }
                                }

                                // === V35: INQUISITOR BATTLE DESTINY BONUS ===
                                // Hunt Down V objective gives +1 total battle destiny where you have
                                // an Inquisitor (+2 if hatred card present). This is like 1-2 extra
                                // destiny draws — massive advantage. Also check for Jedi opponents.
                                {
                                    com.gempukku.swccgo.ai.models.rando.strategy.ObjectiveAnalyzer v35ObjAnalyzer =
                                        context.getObjectiveAnalyzer();
                                    if (v35ObjAnalyzer != null && v35ObjAnalyzer.isAnalyzed() && v35ObjAnalyzer.isHuntDownV()
                                        && cardsHere != null) {
                                        boolean inquisitorInBattle = false;
                                        boolean hatredAtLocation = false;
                                        boolean jediAtLocation = false;

                                        for (PhysicalCard bCard : cardsHere) {
                                            if (bCard == null || bCard.getBlueprint() == null) continue;
                                            String bTitle = bCard.getTitle() != null ? bCard.getTitle().toLowerCase(Locale.ROOT) : "";

                                            if (playerId.equals(bCard.getOwner())) {
                                                // Our characters — check for Inquisitors
                                                if (isInquisitor(bTitle)) {
                                                    inquisitorInBattle = true;
                                                }
                                            } else {
                                                // Opponent characters — check for Jedi/Padawan and stacked hatred
                                                if (isJediOrPadawan(bTitle)) {
                                                    jediAtLocation = true;
                                                }
                                                try {
                                                    java.util.List<PhysicalCard> stacked = gameState.getStackedCards(bCard);
                                                    if (stacked != null && !stacked.isEmpty()) {
                                                        hatredAtLocation = true;
                                                    }
                                                } catch (Exception e) { /* ignore */ }
                                            }
                                        }

                                        if (inquisitorInBattle) {
                                            float destinyBonus = 120.0f; // +1 battle destiny from objective
                                            if (hatredAtLocation) destinyBonus = 250.0f; // +2 battle destiny
                                            if (jediAtLocation) destinyBonus += 100.0f; // FMFTD and Fifth Brother bonuses
                                            action.addReasoning(String.format(
                                                "V35 HUNT DESTINY: Inquisitor in battle%s%s — +%d total battle destiny!",
                                                hatredAtLocation ? " + HATRED" : "",
                                                jediAtLocation ? " vs JEDI" : "",
                                                hatredAtLocation ? 2 : 1), destinyBonus);
                                            logger.warn("V35 HUNT DESTINY at {}: Inquisitor={}, hatred={}, jedi={} — bonus +{}",
                                                targetLocation.getTitle(), inquisitorInBattle, hatredAtLocation,
                                                jediAtLocation, (int)destinyBonus);
                                        }
                                    }
                                }

                                if (ourPower > 0 && theirPower > 0) {
                                    foundAnyContestedLocation = true;

                                    // === V76 (Steve, 2026-05-15): BATTLE PREDICTION GATE ===
                                    // Use the Monte Carlo BattlePredictor BEFORE the power-tier
                                    // scoring. If the simulation projects bad outcomes:
                                    //   - winRate < 35% → hard block (probable defeat)
                                    //   - avgDamageTaken >= 10 → hard block (even if winning, too costly)
                                    // Otherwise, fall through to the V22.4/V29.7 power scoring.
                                    //
                                    // Replay May 15: Rando initiated battle at Lars Farm and took
                                    // 13 attrition + 23 battle damage. Raw power comparison passed
                                    // CRUSH/FAVORABLE; destiny variance crushed him. BattlePredictor
                                    // exists with 311 lines of simulation logic but was never
                                    // wired into BattleEvaluator. This wiring closes the gap.
                                    try {
                                        // Estimate destiny draws per side: count chars with ability >= 1
                                        // at the location, capped between 1 and 4 (typical SWCCG range).
                                        int myDraws = 1, oppDraws = 1;
                                        try {
                                            int myCh = 0, oppCh = 0;
                                            for (PhysicalCard c : cardsHere) {
                                                if (c == null || c.getBlueprint() == null) continue;
                                                if (c.getBlueprint().getCardCategory() != com.gempukku.swccgo.common.CardCategory.CHARACTER) continue;
                                                if (playerId.equals(c.getOwner())) myCh++;
                                                else if (opponentId.equals(c.getOwner())) oppCh++;
                                            }
                                            myDraws = Math.max(1, Math.min(4, myCh));
                                            oppDraws = Math.max(1, Math.min(4, oppCh));
                                        } catch (Exception e) { /* use defaults */ }

                                        com.gempukku.swccgo.ai.models.rando.strategy.DeckOracle v76DeckOracle =
                                            context.getDeckOracle();
                                        com.gempukku.swccgo.ai.models.rando.strategy.OpponentDeckTracker v76OppTracker =
                                            context.getOpponentDeckTracker();
                                        BattlePredictor.BattleOutcome v76Outcome = BattlePredictor.predictBattle(
                                            (int) (ourPower + weaponBonus), myDraws,
                                            (int) theirPower, oppDraws,
                                            v76DeckOracle, v76OppTracker);

                                        logger.warn("V76 BATTLE PREDICT at {}: winRate={} avgDamageTaken={} avgDamageDealt={} (myPow={}+wb{} draws={} vs oppPow={} draws={})",
                                            targetLocation.getTitle(),
                                            String.format("%.2f", v76Outcome.winProbability),
                                            String.format("%.1f", v76Outcome.expectedDamageTaken),
                                            String.format("%.1f", v76Outcome.expectedDamageDealt),
                                            (int) ourPower, (int) weaponBonus, myDraws,
                                            (int) theirPower, oppDraws);

                                        if (v76Outcome.winProbability < 0.35f) {
                                            action.addReasoning(String.format(
                                                "V76 BATTLE PREDICT: winRate %.0f%% at %s — probable defeat, don't initiate!",
                                                v76Outcome.winProbability * 100, targetLocation.getTitle()), -800.0f);
                                            logger.warn("V76 BATTLE BLOCK: predicted defeat at {} (winRate {})",
                                                targetLocation.getTitle(), String.format("%.2f", v76Outcome.winProbability));
                                        } else if (v76Outcome.expectedDamageTaken >= 10f) {
                                            action.addReasoning(String.format(
                                                "V76 BATTLE PREDICT: avg damage taken %.1f at %s — pyrrhic, don't initiate!",
                                                v76Outcome.expectedDamageTaken, targetLocation.getTitle()), -500.0f);
                                            logger.warn("V76 BATTLE COSTLY: predicted damage {} at {} — too high",
                                                String.format("%.1f", v76Outcome.expectedDamageTaken), targetLocation.getTitle());
                                        }
                                    } catch (Exception v76Ex) {
                                        logger.debug("V76 prediction error: {}", v76Ex.getMessage());
                                    }

                                    // V29.7: Use weapon-adjusted effective diff for battle decisions.
                                    // A weapon-equipped character with base power equal or slightly
                                    // less than opponent is actually FAVORED in battle.
                                    // Only block if we're outgunned EVEN WITH weapons.
                                    if (theirPower > ourPower && weaponBonus == 0) {
                                        // No weapons and opponent has more power — NEVER initiate!
                                        float penalty = -300.0f;
                                        if (theirPower > ourPower * 2) penalty = -600.0f;
                                        action.addReasoning(String.format("V29 DON'T INITIATE: %.0f vs %.0f power — we're outgunned!",
                                            ourPower, theirPower), penalty);
                                        logger.warn("V29 BATTLE BLOCK at {}: our {} vs their {} — BLOCKED (penalty {})",
                                            targetLocation.getTitle(), (int)ourPower, (int)theirPower, (int)penalty);
                                    } else if (theirPower > ourPower && weaponBonus > 0 && weaponEffectiveDiff < MARGINAL_THRESHOLD) {
                                        // We have weapons but still outpowered even with weapon bonus
                                        action.addReasoning(String.format("V29.7 WEAPONS NOT ENOUGH: power %.0f+weapons vs %.0f — still risky",
                                            ourPower, theirPower), -150.0f);
                                    } else if (weaponEffectiveDiff >= FAVORABLE_THRESHOLD) {
                                        // V34: Strong advantage (with weapons) — FIGHT!
                                        foundFavorableBattle = true;
                                        float battleBonus = 150.0f; // V34: Raised from 40 — Rando was too passive
                                        String battleReason;
                                        if (weaponBonus > 0) {
                                            battleBonus += weaponBonus * 10.0f; // V34: Raised from 5x — weapons are devastating
                                            if (ourVaderHere && lukeHere) {
                                                battleBonus += 100.0f; // HUGE bonus: Vader vs Luke (Hunt Down!)
                                                battleReason = String.format("V29.7 VADER vs LUKE at %s! Power %.0f + weapons vs %.0f — CHALLENGE!",
                                                    targetLocation.getTitle(), ourPower, theirPower);
                                            } else {
                                                battleReason = String.format("V29.7 ARMED BATTLE at %s (power %.0f + weapons vs %.0f, effective diff=%.0f)",
                                                    targetLocation.getTitle(), ourPower, theirPower, weaponEffectiveDiff);
                                            }
                                            if (hasIHYN) battleReason += " + IHYN!";
                                        } else {
                                            battleReason = String.format("Favorable battle at %s (power %.0f vs %.0f, ability %.0f vs %.0f)",
                                                targetLocation.getTitle(), ourPower, theirPower, ourAbility, theirAbility);
                                        }
                                        action.addReasoning(battleReason, battleBonus);
                                    } else if (weaponEffectiveDiff >= MARGINAL_THRESHOLD) {
                                        if (weaponBonus > 0) {
                                            // V34: Marginal with weapons — weapons tip the balance, GO FIGHT
                                            action.addReasoning(String.format("V34 ARMED MARGINAL at %s (power %.0f + weapons vs %.0f) — weapons help!",
                                                targetLocation.getTitle(), ourPower, theirPower), 80.0f);
                                        } else {
                                            // Slight advantage but risky without weapons
                                            action.addReasoning(String.format("V29 MARGINAL at %s (power %.0f vs %.0f) — risky with weapons",
                                                targetLocation.getTitle(), ourPower, theirPower), -50.0f);
                                        }
                                    } else {
                                        // Even or worse — don't initiate
                                        float penalty = -100.0f;
                                        if (weaponEffectiveDiff < -8) penalty = -200.0f;
                                        if (weaponEffectiveDiff < -15) penalty = -400.0f;
                                        action.addReasoning(String.format("V29: UNFAVORABLE at %s (power %.0f vs %.0f) - don't initiate!",
                                            targetLocation.getTitle(), ourPower, theirPower), penalty);
                                    }
                                } else if (ourPower > 0 && theirPower == 0) {
                                    // We're alone here - no battle possible
                                    action.addReasoning("No opponent here", -20.0f);
                                }
                            }

                            // V22.4: Fallback — if we couldn't identify the specific location,
                            // check all locations but be MORE conservative
                            if (!checkedSpecificLocation) {
                                for (PhysicalCard location : gameState.getTopLocations()) {
                                    float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, location, playerId, false, false);
                                    float theirPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, location, opponentId, false, false);

                                    if (ourPower > 0 && theirPower > 0) {
                                        foundAnyContestedLocation = true;
                                        float powerDiff = ourPower - theirPower;
                                        float ourAbility = game.getModifiersQuerying().getTotalAbilityAtLocation(
                                            gameState, playerId, location);
                                        float theirAbility = game.getModifiersQuerying().getTotalAbilityAtLocation(
                                            gameState, opponentId, location);
                                        float abilityDiff = ourAbility - theirAbility;
                                        float effectiveDiff = powerDiff + (abilityDiff * 2.5f);

                                        logger.info("[BattleEvaluator] Checking {}: power={}/{} (diff={}), ability={}/{} (diff={})",
                                            location.getTitle(), ourPower, theirPower, powerDiff,
                                            ourAbility, theirAbility, abilityDiff);

                                        // V22.4: Check for any suicidal locations — if ANY location
                                        // has our power < 50% of theirs, add strong warning
                                        if (theirPower > ourPower * 2 && theirPower > 6) {
                                            action.addReasoning(String.format("V22.4 DANGER at %s (%.0f vs %.0f) - might battle here!",
                                                location.getTitle(), ourPower, theirPower), -80.0f);
                                        }

                                        if (effectiveDiff >= MARGINAL_THRESHOLD) {
                                            foundFavorableBattle = true;
                                            action.addReasoning(String.format("Favorable battle at %s (power %.0f vs %.0f, ability %.0f vs %.0f)",
                                                location.getTitle(), ourPower, theirPower, ourAbility, theirAbility), 40.0f);
                                            break;
                                        } else if (abilityDiff < -1) {
                                            action.addReasoning(String.format("Ability disadvantage at %s (%.0f vs %.0f) - enemy draws more destiny",
                                                location.getTitle(), ourAbility, theirAbility), -25.0f);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.warn("[BattleEvaluator] Could not check locations: {}", e.getMessage());
                        }
                    }
                }

                if (!foundFavorableBattle && foundAnyContestedLocation) {
                    action.addReasoning("No favorable battles available - don't initiate", -60.0f);
                } else if (!foundAnyContestedLocation) {
                    action.addReasoning("No contested locations", -20.0f);
                }

                // V22: STRATEGIC MUST-FIGHT OVERRIDE
                // If opponent is draining us from multiple uncontested locations and we're behind
                // on life force, inaction guarantees defeat. Force engagement.
                if (!foundFavorableBattle && game != null && gameState != null) {
                    try {
                        String playerId = context.getPlayerId();
                        String opponentId = gameState.getOpponent(playerId);
                        int theirDrain = 0;
                        boolean hasEngageableOpponent = false;
                        for (PhysicalCard location : gameState.getTopLocations()) {
                            float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                gameState, location, playerId, false, false);
                            float theirPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                gameState, location, opponentId, false, false);
                            if (theirPower > 0 && ourPower == 0) {
                                theirDrain += 1;
                            }
                            if (ourPower > 0 && theirPower > 0) {
                                hasEngageableOpponent = true;
                            }
                        }
                        // V29: Only apply MUST-FIGHT if we have at least one location where
                        // we're NOT outpowered. Don't force a battle with solo Lando vs Rey.
                        boolean hasWinnableBattle = false;
                        for (PhysicalCard location : gameState.getTopLocations()) {
                            float ourP = game.getModifiersQuerying().getTotalPowerAtLocation(
                                gameState, location, playerId, false, false);
                            float theirP = game.getModifiersQuerying().getTotalPowerAtLocation(
                                gameState, location, opponentId, false, false);
                            if (ourP > 0 && theirP > 0 && ourP >= theirP) {
                                hasWinnableBattle = true;
                                break;
                            }
                        }
                        if (theirDrain >= 2 && hasWinnableBattle && isBehindOnLifeForce) {
                            action.addReasoning(
                                String.format("V34 MUST-FIGHT: Opponent draining from %d uncontested locations, we're behind - must engage!", theirDrain),
                                200.0f); // V34: Raised from 80 — inaction = guaranteed loss
                            logger.warn("[BattleEvaluator] V22 MUST-FIGHT override: drain threat={}, behind=true", theirDrain);
                        } else if (theirDrain >= 2 && !hasWinnableBattle && isBehindOnLifeForce) {
                            logger.warn("V29 MUST-FIGHT BLOCKED: Behind on life but outpowered everywhere — don't suicide!");
                        }
                    } catch (Exception e) {
                        logger.debug("[BattleEvaluator] V22 must-fight check failed: {}", e.getMessage());
                    }
                }

                // V61 RESERVE DECK GUARD — battle destiny needs Reserve Deck cards!
                // FIXES is9j46shx6t0swby replay: Rando initiated battle with Reserve=0
                // (log: "No cards in Reserve Deck. Rando can't draw battle destiny")
                // auto-losing every destiny draw. This is a hard auto-lose trap.
                // Scale penalty to severity — 0 cards = hard block, 1-2 = heavy penalty.
                if (reserveDeck == 0) {
                    action.addReasoning(
                        "V61 RESERVE EMPTY: 0 cards in Reserve — CANNOT draw battle destiny, auto-lose!",
                        -800.0f);
                    logger.warn("V61 RESERVE EMPTY: Blocking battle initiation — Reserve=0!");
                } else if (reserveDeck == 1) {
                    action.addReasoning(
                        "V61 RESERVE CRITICAL: 1 card in Reserve — can draw 1 destiny max, very risky!",
                        -400.0f);
                    logger.warn("V61 RESERVE CRITICAL: 1 card in reserve — heavy penalty -400");
                } else if (reserveDeck == 2) {
                    action.addReasoning(
                        "V61 RESERVE LOW: 2 cards in Reserve — weapon destiny + 1 battle destiny only",
                        -200.0f);
                } else if (reserveDeck < MIN_RESERVE_FOR_BATTLE) {
                    action.addReasoning(
                        String.format("Low reserve deck (%d) - risky destiny draws", reserveDeck),
                        -80.0f);
                }

                // === V27: BATTLE INTERRUPT FORCE RESERVATION ===
                // If opponent has "Draw Their Fire" on table, playing ANY interrupt
                // during battles THEY initiate costs 1 extra Force. This means Ghhhk
                // (Used Interrupt, normally free to play) needs 1 Force just from the tax.
                // Without Force in pile, ALL battle interrupts are unusable and we take
                // full attrition from heavy losses.
                // Also applies when WE initiate: defender (us) still loses 1 Force when
                // battle is initiated, and if opponent initiates, we need extra Force per interrupt.
                if (gameState != null) {
                    int battleForcePile = context.getForcePileSize();
                    int handSize = context.getHandSize();

                    // V27.1: Detect "Draw Their Fire" on opponent's table
                    boolean opponentHasDrawTheirFire = false;
                    try {
                        String opponentIdDtf = gameState.getOpponent(context.getPlayerId());
                        for (PhysicalCard dtfCard : gameState.getAllPermanentCards()) {
                            if (dtfCard == null) continue;
                            if (opponentIdDtf != null && opponentIdDtf.equals(dtfCard.getOwner())
                                && dtfCard.getBlueprint() != null
                                && dtfCard.getBlueprint().getTitle() != null) {
                                String dtfTitle = dtfCard.getBlueprint().getTitle().toLowerCase(Locale.ROOT);
                                if (dtfTitle.contains("draw their fire")) {
                                    com.gempukku.swccgo.common.Zone dtfZone = dtfCard.getZone();
                                    if (dtfZone != null && dtfZone.isInPlay()) {
                                        opponentHasDrawTheirFire = true;
                                        break;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.debug("V27.1: Error checking for Draw Their Fire: {}", e.getMessage());
                    }

                    if (opponentHasDrawTheirFire) {
                        // Draw Their Fire is active! Each interrupt costs 1 extra Force.
                        // We need at least 2 Force per interrupt (1 tax + interrupt cost).
                        // For Ghhhk (Used Interrupt = free), still need 1 Force for tax.
                        // Also: when battle is initiated, defender loses 1 Force automatically.
                        int forceNeededForInterrupts = 3; // 1 for DTF defender loss + 1 for interrupt tax + 1 buffer
                        if (battleForcePile < forceNeededForInterrupts) {
                            float dtfPenalty = -60.0f;
                            if (battleForcePile == 0) dtfPenalty = -100.0f;
                            action.addReasoning(String.format(
                                "V27.1 DRAW THEIR FIRE: Opponent has DTF on table! Need %d Force for interrupts (tax+loss), " +
                                "only %d in pile — Ghhhk UNUSABLE!", forceNeededForInterrupts, battleForcePile), dtfPenalty);
                            logger.warn("V27.1 DTF ACTIVE: Only {} Force, need {} for interrupt tax — battle interrupts blocked!",
                                battleForcePile, forceNeededForInterrupts);
                        } else {
                            action.addReasoning(String.format(
                                "V27.1 DRAW THEIR FIRE: DTF on table, %d Force available — interrupts usable but costly", battleForcePile), 0.0f);
                        }
                    } else {
                        // No DTF — standard Force check for battle readiness
                        if (battleForcePile < 2) {
                            action.addReasoning(String.format(
                                "V27 BATTLE FORCE WARNING: Only %d Force in pile — limited interrupt capacity! " +
                                "Battle losses come from hand (%d cards)!", battleForcePile, handSize), -40.0f);
                            logger.warn("V27 BATTLE FORCE: Only {} Force available — battle interrupts may be unusable!", battleForcePile);
                        } else if (battleForcePile < 4) {
                            action.addReasoning(String.format(
                                "V27 BATTLE FORCE: Low Force (%d) — limited interrupt capacity in battle", battleForcePile), -15.0f);
                        }
                    }
                }

                // Strategic position adjustments (reduced impact - power diff is more important)
                if (isBehindOnLifeForce) {
                    // When behind, slight encouragement but power still matters most
                    action.addReasoning("Behind on life force - slightly more aggressive", 15.0f);
                } else if (isAheadOnLifeForce) {
                    // When ahead, be more conservative
                    action.addReasoning("Ahead on life force - can afford to wait", -20.0f);
                }

                // Life force critical - more aggressive but still check power
                if (lifeForce <= RandoConfig.CRITICAL_LIFE_FORCE) {
                    action.addReasoning("Low life force - need to act", 30.0f);
                }
            }

            // === WEAPON FIRING ===
            if (actionLower.contains("fire")) {
                action.addReasoning("Fire weapon", 40.0f);

                // Target selection bonuses
                if (actionLower.contains("character")) {
                    action.addReasoning("Target character", 10.0f);
                }
                if (actionLower.contains("unique") || actionLower.contains("•")) {
                    action.addReasoning("Target unique card", 20.0f);
                }
            }

            // === CANCEL BATTLE (It's A Trap, etc.) ===
            if (actionLower.contains("cancel battle") || actionLower.contains("cancel the battle")) {
                SwccgGame game = context.getGame();
                if (game != null && gameState != null) {
                    BattleState battleState = gameState.getBattleState();
                    String playerId = context.getPlayerId();

                    if (battleState != null) {
                        String initiator = battleState.getPlayerInitiatedBattle();
                        boolean weInitiated = playerId != null && playerId.equals(initiator);

                        if (weInitiated) {
                            // NEVER cancel a battle we started - that's wasteful
                            action.addReasoning("DO NOT cancel our own battle! Waste of interrupt.", -150.0f);
                            logger.info("[BattleEvaluator] Penalizing cancel - WE initiated this battle");
                        } else {
                            // Opponent initiated - check if we should cancel
                            String opponentId = gameState.getOpponent(playerId);
                            PhysicalCard battleLocation = battleState.getBattleLocation();

                            if (battleLocation != null && opponentId != null) {
                                try {
                                    float ourPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, battleLocation, playerId, false, false);
                                    float theirPower = game.getModifiersQuerying().getTotalPowerAtLocation(
                                        gameState, battleLocation, opponentId, false, false);
                                    float powerDiff = ourPower - theirPower;

                                    if (powerDiff < -FAVORABLE_THRESHOLD) {
                                        // We're badly losing - cancel is valuable
                                        action.addReasoning(String.format("Cancel losing battle (%.0f vs %.0f)", ourPower, theirPower), 60.0f);
                                    } else if (powerDiff < 0) {
                                        // Slight disadvantage - cancel might be worth it
                                        action.addReasoning(String.format("Cancel unfavorable battle (%.0f vs %.0f)", ourPower, theirPower), 20.0f);
                                    } else {
                                        // We're winning or even - don't waste the interrupt
                                        action.addReasoning(String.format("Don't cancel - we're not losing (%.0f vs %.0f)", ourPower, theirPower), -60.0f);
                                    }
                                } catch (Exception e) {
                                    logger.warn("[BattleEvaluator] Could not get power for cancel decision: {}", e.getMessage());
                                }
                            }
                        }
                    }
                }
            }

            // === BATTLE TACTICS (during battle) ===
            if (context.getPhase() == Phase.BATTLE) {
                // Fire before forfeit
                if (actionLower.contains("fire")) {
                    action.addReasoning("Fire weapons during battle", 50.0f);
                }

                // Draw battle destiny
                if (actionLower.contains("draw") && actionLower.contains("destiny")) {
                    action.addReasoning("Draw battle destiny", 30.0f);
                }
            }

            logger.debug("[BattleEvaluator] Scored '{}' -> {}",
                actionText.length() > 40 ? actionText.substring(0, 40) + "..." : actionText,
                String.format("%.1f", action.getScore()));

            actions.add(action);
        }

        logger.info("[BattleEvaluator] Evaluated {} battle actions", actions.size());
        return actions;
    }
}
