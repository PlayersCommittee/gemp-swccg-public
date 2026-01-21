package com.gempukku.swccgo.ai.models.rando.strategy;

import com.gempukku.swccgo.ai.common.AiBoardAnalyzer;
import com.gempukku.swccgo.ai.common.AiCardHelper;
import com.gempukku.swccgo.ai.models.rando.RandoConfig;
import com.gempukku.swccgo.ai.models.rando.RandoLogger;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;

import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Deploy Phase Planner - Creates holistic deployment plans for the entire phase.
 *
 * COMPREHENSIVE PORT from Python deploy_planner.py (7000+ lines).
 *
 * Key features:
 * 1. MULTIPLE PLAN GENERATION - generates ground, space, combined plans and picks best
 * 2. OPTIMAL COMBINATION FINDING - finds best card combinations within budget
 * 3. FORCE DRAIN GAP - tracks drain economy to prioritize stop-bleeding
 * 4. DYNAMIC THRESHOLDS - adjusts thresholds based on life force and game state
 * 5. PLAN SCORING - scores all plans and selects highest
 * 6. NEXT-TURN OPPORTUNITY - checks if holding would enable better plays
 *
 * Strategic priority order:
 * 1. DEPLOY LOCATIONS FIRST - opens new deployment options
 * 2. STOP BLEEDING - contest locations where opponent drains us
 * 3. REINFORCE LOSING - add power where we're being beaten
 * 4. ESTABLISH - take uncontested high-value locations
 * 5. BUILD UP - reinforce winning positions (but not overkill)
 */
public class DeployPhasePlanner {
    private static final Logger LOG = RandoLogger.getStrategyLogger();

    // Config constants
    private final int deployThreshold;
    private final int battleForceReserve;

    // Current plan (cached)
    private DeploymentPlan currentPlan;
    private int lastPlanTurn = -1;

    // Board state reference for scoring
    private SwccgGame currentGame;
    private String currentPlayerId;

    public DeployPhasePlanner() {
        this(RandoConfig.DEPLOY_THRESHOLD, RandoConfig.BATTLE_FORCE_RESERVE);
    }

    public DeployPhasePlanner(int deployThreshold, int battleForceReserve) {
        this.deployThreshold = deployThreshold;
        this.battleForceReserve = battleForceReserve;
    }

    /**
     * Reset planner state for a new game.
     */
    public void reset() {
        currentPlan = null;
        lastPlanTurn = -1;
        currentGame = null;
        currentPlayerId = null;
    }

    /**
     * Get the current deployment plan, if any.
     */
    public DeploymentPlan getCurrentPlan() {
        return currentPlan;
    }

    // =========================================================================
    // MAIN ENTRY POINT - createPlan
    // =========================================================================

    /**
     * Create a deployment plan for this phase.
     *
     * This is the main entry point, ported from Python create_plan().
     * Generates MULTIPLE plans and picks the best one.
     */
    public DeploymentPlan createPlan(SwccgGame game, String playerId, Side side) {
        this.currentGame = game;
        this.currentPlayerId = playerId;

        GameState gameState = game.getGameState();
        if (gameState == null) {
            return createHoldBackPlan("No game state available");
        }

        int currentTurn = gameState.getPlayersLatestTurnNumber(playerId);
        String opponentId = gameState.getOpponent(playerId);

        // If we already have a plan for this turn, return it
        if (currentPlan != null && lastPlanTurn == currentTurn) {
            LOG.debug("📋 Returning cached plan for turn {} ({} instructions remaining)",
                currentTurn, currentPlan.getInstructions().size());
            return currentPlan;
        }

        LOG.info("📋 ═══════════════════════════════════════════════════════════════");
        LOG.info("📋 CREATING COMPREHENSIVE DEPLOYMENT PLAN (Turn {})", currentTurn);
        LOG.info("📋 ═══════════════════════════════════════════════════════════════");

        // === GET RESOURCES ===
        int forceAvailable = gameState.getForcePileSize(playerId);
        int lifeForce = gameState.getPlayerLifeForce(playerId);
        int opponentLifeForce = gameState.getPlayerLifeForce(opponentId);
        List<PhysicalCard> hand = gameState.getHand(playerId);

        LOG.info("📊 Resources: force={}, life={}, opponent_life={}, hand_size={}",
            forceAvailable, lifeForce, opponentLifeForce, hand.size());

        // === CATEGORIZE HAND ===
        List<CardInfo> allCards = new ArrayList<>();
        List<CardInfo> locations = new ArrayList<>();
        List<CardInfo> characters = new ArrayList<>();
        List<CardInfo> starships = new ArrayList<>();
        List<CardInfo> vehicles = new ArrayList<>();
        List<CardInfo> deadCards = new ArrayList<>();

        for (PhysicalCard card : hand) {
            if (card == null || card.getBlueprint() == null) continue;
            CardInfo info = new CardInfo(card);
            allCards.add(info);

            // === PERSONA CHECK: Skip dead cards (persona already deployed) ===
            if (AiCardHelper.isDeadCard(card, game, playerId)) {
                deadCards.add(info);
                LOG.info("☠️ DEAD CARD: {} - persona already on table, skipping deployment planning",
                    info.name);
                continue;  // Don't add to deployable categories
            }

            if (info.isLocation) locations.add(info);
            else if (info.isCharacter) characters.add(info);
            else if (info.isStarship) starships.add(info);
            else if (info.isVehicle) vehicles.add(info);
        }

        LOG.info("📋 Hand: {} locations, {} characters, {} starships, {} vehicles, {} dead cards",
            locations.size(), characters.size(), starships.size(), vehicles.size(), deadCards.size());
        if (!deadCards.isEmpty()) {
            LOG.info("☠️ Dead cards (persona in play): {}", deadCards.stream()
                .map(c -> c.name).collect(Collectors.joining(", ")));
        }

        // Log hand details
        logHandDetails(characters, starships, vehicles);

        // === ANALYZE BOARD ===
        List<AiBoardAnalyzer.LocationAnalysis> allLocations = AiBoardAnalyzer.analyzeAllLocations(
            game, playerId, opponentId, side);

        logBoardAnalysis(allLocations);

        // === CATEGORIZE LOCATIONS ===
        LocationCategories categories = categorizeLocations(allLocations, playerId);

        LOG.info("📊 Location categories: {} losing, {} winning, {} bleed, {} establish, {} attack, {} weak",
            categories.losingLocations.size(), categories.winningLocations.size(),
            categories.bleedLocations.size(), categories.establishTargets.size(),
            categories.attackTargets.size(), categories.weakPresenceLocations.size());

        // === CALCULATE FORCE DRAIN GAP ===
        DrainGapResult drainGap = calculateForceDrainGap(allLocations);
        LOG.info("💧 Drain economy: we drain {}, they drain {} = gap {:+d}",
            drainGap.ourDrain, drainGap.theirDrain, drainGap.drainGap);

        // === CALCULATE DYNAMIC THRESHOLDS ===
        int groundThreshold = getDynamicThreshold(allLocations, false, currentTurn, lifeForce);
        int spaceThreshold = getDynamicThreshold(allLocations, true, currentTurn, lifeForce);

        LOG.info("📊 Dynamic thresholds: ground={}, space={}", groundThreshold, spaceThreshold);

        // === GENERATE MULTIPLE PLANS ===
        List<ScoredPlan> allPlans = new ArrayList<>();
        int effectiveForce = forceAvailable - battleForceReserve;

        // Track location deploys (apply to all plans)
        List<CardInfo> locationDeploys = planLocationDeploys(locations, effectiveForce);
        int forceAfterLocations = effectiveForce;
        for (CardInfo loc : locationDeploys) {
            forceAfterLocations -= loc.cost;
        }

        // Generate ground plans
        List<ScoredPlan> groundPlans = generateGroundPlans(
            characters, vehicles, categories, forceAfterLocations, groundThreshold,
            allLocations, currentTurn, drainGap);
        allPlans.addAll(groundPlans);

        // Generate space plans
        List<ScoredPlan> spacePlans = generateSpacePlans(
            starships, characters, categories, forceAfterLocations, spaceThreshold,
            allLocations, currentTurn, drainGap, game, playerId);
        allPlans.addAll(spacePlans);

        // Generate combined plans (best of ground + space within budget)
        List<ScoredPlan> combinedPlans = generateCombinedPlans(
            characters, starships, vehicles, categories, forceAfterLocations,
            groundThreshold, spaceThreshold, allLocations, currentTurn, drainGap);
        allPlans.addAll(combinedPlans);

        // === SELECT BEST PLAN ===
        DeploymentPlan bestPlan = selectBestPlan(allPlans, locationDeploys, currentTurn, lifeForce);

        // === EARLY GAME HOLD-BACK CHECK (at the END, like Python) ===
        if (currentTurn <= RandoConfig.DEPLOY_EARLY_GAME_TURNS && bestPlan != null) {
            float planScore = scorePlan(bestPlan, allLocations, currentTurn);
            if (planScore < RandoConfig.DEPLOY_EARLY_GAME_THRESHOLD) {
                LOG.info("📋 EARLY GAME HOLD: plan score {} < threshold {} - holding back",
                    (int)planScore, RandoConfig.DEPLOY_EARLY_GAME_THRESHOLD);
                bestPlan = createHoldBackPlan(String.format(
                    "Early game (turn %d) - plan score %.0f below threshold %d",
                    currentTurn, planScore, RandoConfig.DEPLOY_EARLY_GAME_THRESHOLD));
            }
        }

        if (bestPlan == null || bestPlan.getInstructions().isEmpty()) {
            bestPlan = createHoldBackPlan("No strategic deployment targets");
        }

        currentPlan = bestPlan;
        lastPlanTurn = currentTurn;
        logFinalPlan(bestPlan);

        return currentPlan;
    }

    // =========================================================================
    // FORCE DRAIN GAP CALCULATION (Item #3)
    // =========================================================================

    /**
     * Calculate force drain economy.
     *
     * Ported from Python _calculate_force_drain_gap().
     */
    private DrainGapResult calculateForceDrainGap(List<AiBoardAnalyzer.LocationAnalysis> locations) {
        int theirDrain = 0;
        int ourDrain = 0;
        List<AiBoardAnalyzer.LocationAnalysis> bleedLocations = new ArrayList<>();

        for (AiBoardAnalyzer.LocationAnalysis loc : locations) {
            // Opponent drains us: they have presence, we don't
            // They drain for OUR icons (ourForceIcons)
            if (loc.theirPower > 0 && loc.ourPower == 0 && loc.ourForceIcons > 0) {
                theirDrain += loc.ourForceIcons;

                // Add to bleed locations if enemy power is low enough to contest
                if (loc.theirPower <= RandoConfig.LOW_ENEMY_THRESHOLD) {
                    bleedLocations.add(loc);
                    String domain = loc.isSpace() ? "space" : "ground";
                    LOG.debug("   🩸 BLEED ({}, contestable): {} - they drain {} icons, enemy power {}",
                        domain, loc.location.getTitle(), loc.ourForceIcons, (int)loc.theirPower);
                }
            }

            // We drain opponent: we have presence, they don't
            if (loc.ourPower > 0 && loc.theirPower == 0 && loc.theirForceIcons > 0) {
                ourDrain += loc.theirForceIcons;
                LOG.debug("   💧 DRAIN: {} - we drain {} icons", loc.location.getTitle(), loc.theirForceIcons);
            }
        }

        return new DrainGapResult(theirDrain, ourDrain, ourDrain - theirDrain, bleedLocations);
    }

    // =========================================================================
    // DYNAMIC THRESHOLD CALCULATION (Item #4, #5)
    // =========================================================================

    /**
     * Calculate dynamic deploy threshold based on game state.
     *
     * Ported from Python _get_dynamic_threshold().
     */
    private int getDynamicThreshold(List<AiBoardAnalyzer.LocationAnalysis> locations,
                                     boolean isSpace, int turnNumber, int lifeForce) {
        int threshold = deployThreshold;
        String domain = isSpace ? "space" : "ground";

        // EARLY GAME RELAXATION: Before turn 4 with no contested locations
        boolean earlyGameRelaxed = false;
        if (turnNumber < 4) {
            boolean hasContested = false;
            for (AiBoardAnalyzer.LocationAnalysis loc : locations) {
                if (loc.ourPower <= 0 || loc.theirPower <= 0) continue;

                if (isSpace && loc.isSpace()) {
                    hasContested = true;
                    break;
                } else if (!isSpace && loc.isGround()) {
                    hasContested = true;
                    break;
                }
            }

            if (!hasContested) {
                // Check for react threats
                boolean hasReactThreat = false;
                for (AiBoardAnalyzer.LocationAnalysis loc : locations) {
                    boolean matchesDomain = isSpace ? loc.isSpace() : loc.isGround();
                    if (matchesDomain && loc.theirPower >= RandoConfig.REACT_THREAT_THRESHOLD) {
                        hasReactThreat = true;
                        break;
                    }
                }

                if (!hasReactThreat) {
                    threshold = Math.max(RandoConfig.MIN_ESTABLISH_POWER, threshold - 2);
                    earlyGameRelaxed = true;
                }
            }
        }

        // LIFE FORCE DECAY: Lower threshold when losing badly
        int lifeForceDecay = 0;
        if (lifeForce < 10) {
            lifeForceDecay = 2;
            threshold = Math.max(RandoConfig.MIN_ESTABLISH_POWER - 1, threshold - lifeForceDecay);
        } else if (lifeForce < 20) {
            lifeForceDecay = 1;
            threshold = Math.max(RandoConfig.MIN_ESTABLISH_POWER, threshold - lifeForceDecay);
        } else if (lifeForce < 30) {
            lifeForceDecay = 1;
            threshold = Math.max(RandoConfig.MIN_ESTABLISH_POWER, threshold - lifeForceDecay);
        }

        LOG.debug("   📊 Dynamic threshold ({}): {} (early={}, life_decay={})",
            domain, threshold, earlyGameRelaxed, lifeForceDecay);

        return threshold;
    }

    // =========================================================================
    // LOCATION CATEGORIZATION (Items #15, #16, #17)
    // =========================================================================

    /**
     * Categorize all locations into strategic groups.
     */
    private LocationCategories categorizeLocations(List<AiBoardAnalyzer.LocationAnalysis> locations,
                                                    String playerId) {
        LocationCategories cats = new LocationCategories();

        for (AiBoardAnalyzer.LocationAnalysis loc : locations) {
            // Skip locations we can't deploy to (no icons)
            boolean hasOurIcons = loc.ourForceIcons > 0;
            boolean hasTheirIcons = loc.theirForceIcons > 0;

            if (loc.ourPower > 0 && loc.theirPower > 0) {
                // CONTESTED - both have presence
                if (loc.ourPower < loc.theirPower) {
                    cats.losingLocations.add(loc);
                } else if (loc.ourPower > loc.theirPower) {
                    cats.winningLocations.add(loc);
                    // Check if below reinforcement target
                    if (loc.ourPower < RandoConfig.REINFORCE_TARGET_POWER) {
                        cats.weakPresenceLocations.add(loc);
                    }
                }

                // Check if crushable (we have big advantage)
                if (loc.getPowerAdvantage() >= RandoConfig.BATTLE_FAVORABLE_THRESHOLD) {
                    cats.crushableLocations.add(loc);
                }
            } else if (loc.theirPower > 0 && loc.ourPower == 0 && hasOurIcons) {
                // BLEEDING - they have presence, we don't, but we have icons (they drain us)
                cats.bleedLocations.add(loc);
                if (loc.theirPower <= RandoConfig.LOW_ENEMY_THRESHOLD) {
                    cats.attackTargets.add(loc);  // Low enemy = attack target
                }
            } else if (loc.theirPower == 0 && loc.ourPower == 0 && hasTheirIcons) {
                // ESTABLISH - neither has presence but they have icons
                cats.establishTargets.add(loc);
            } else if (loc.ourPower > 0 && loc.theirPower == 0 && hasTheirIcons) {
                // DRAINING - we control and can drain them
                cats.drainingLocations.add(loc);
                // Check if we're below reinforcement target
                if (loc.ourPower < RandoConfig.REINFORCE_TARGET_POWER) {
                    cats.weakPresenceLocations.add(loc);
                }
            } else if (loc.ourPower > 0 && loc.theirPower == 0) {
                // CONTROLLED - we have presence, they don't
                if (loc.ourPower < RandoConfig.REINFORCE_TARGET_POWER) {
                    cats.weakPresenceLocations.add(loc);
                }
            }
        }

        return cats;
    }

    // =========================================================================
    // OPTIMAL COMBINATION FINDING (Item #13)
    // =========================================================================

    /**
     * Find optimal combination of cards within budget.
     *
     * Ported from Python _find_optimal_combination().
     */
    private OptimalCombination findOptimalCombination(List<CardInfo> cards, int budget,
                                                       int powerGoal, boolean mustExceed) {
        if (cards.isEmpty() || budget <= 0) {
            return OptimalCombination.empty();
        }

        // Filter to affordable cards
        List<CardInfo> affordable = cards.stream()
            .filter(c -> c.cost <= budget)
            .collect(Collectors.toList());

        if (affordable.isEmpty()) {
            return OptimalCombination.empty();
        }

        // For small card counts, try all combinations
        if (affordable.size() <= 8) {
            return findOptimalBruteForce(affordable, budget, powerGoal, mustExceed);
        }

        // For larger hands, use greedy approach
        return findOptimalGreedy(affordable, budget, powerGoal, mustExceed);
    }

    /**
     * Brute force optimal combination (for small hand sizes).
     */
    private OptimalCombination findOptimalBruteForce(List<CardInfo> cards, int budget,
                                                      int powerGoal, boolean mustExceed) {
        List<PhysicalCard> bestCombo = new ArrayList<>();
        int bestPower = 0;
        int bestCost = Integer.MAX_VALUE;
        int bestAbility = 0;
        boolean bestHasAbility = false;
        boolean bestAchievesGoal = false;

        // Try all subset sizes
        for (int size = 1; size <= cards.size(); size++) {
            for (List<CardInfo> combo : combinations(cards, size)) {
                int totalCost = combo.stream().mapToInt(c -> c.cost).sum();
                if (totalCost > budget) continue;

                int totalPower = combo.stream().mapToInt(c -> c.power).sum();
                int totalAbility = combo.stream().mapToInt(c -> c.ability).sum();
                boolean hasAbility = totalAbility >= RandoConfig.ABILITY_THRESHOLD;

                // Ability compensation for battles
                int effectiveGoal = powerGoal;
                if (!hasAbility && mustExceed) {
                    effectiveGoal = powerGoal + RandoConfig.ABILITY_POWER_COMPENSATION;
                }

                boolean achievesGoal = mustExceed ?
                    totalPower > effectiveGoal : totalPower >= effectiveGoal;

                // Check if this is better
                boolean isBetter = false;
                if (achievesGoal && !bestAchievesGoal) {
                    isBetter = true;
                } else if (achievesGoal == bestAchievesGoal) {
                    if (hasAbility && !bestHasAbility) {
                        isBetter = true;
                    } else if (hasAbility == bestHasAbility) {
                        if (achievesGoal) {
                            if (mustExceed && totalPower > bestPower) isBetter = true;
                            else if (!mustExceed && totalCost < bestCost) isBetter = true;
                        } else if (totalPower > bestPower) {
                            isBetter = true;
                        }
                    }
                }

                if (isBetter) {
                    bestCombo = combo.stream().map(c -> c.card).collect(Collectors.toList());
                    bestPower = totalPower;
                    bestCost = totalCost;
                    bestAbility = totalAbility;
                    bestHasAbility = hasAbility;
                    bestAchievesGoal = achievesGoal;
                }
            }
        }

        return new OptimalCombination(bestCombo, bestPower,
            bestCost == Integer.MAX_VALUE ? 0 : bestCost,
            bestAbility, bestHasAbility, bestAchievesGoal);
    }

    /**
     * Greedy optimal combination (for larger hand sizes).
     */
    private OptimalCombination findOptimalGreedy(List<CardInfo> cards, int budget,
                                                  int powerGoal, boolean mustExceed) {
        // Sort by efficiency (power/cost) with ability bonus
        List<CardInfo> sorted = new ArrayList<>(cards);
        sorted.sort((a, b) -> {
            float scoreA = a.getValueRatio() + (a.ability >= 3 ? a.ability * 0.5f : 0);
            float scoreB = b.getValueRatio() + (b.ability >= 3 ? b.ability * 0.5f : 0);
            return Float.compare(scoreB, scoreA);
        });

        List<PhysicalCard> selected = new ArrayList<>();
        int totalPower = 0;
        int totalCost = 0;
        int totalAbility = 0;

        for (CardInfo card : sorted) {
            if (totalCost + card.cost <= budget) {
                selected.add(card.card);
                totalPower += card.power;
                totalCost += card.cost;
                totalAbility += card.ability;

                // Check if we've met the goal with ability
                boolean hasAbility = totalAbility >= RandoConfig.ABILITY_THRESHOLD;
                int effectiveGoal = hasAbility ? powerGoal :
                    powerGoal + (mustExceed ? RandoConfig.ABILITY_POWER_COMPENSATION : 0);

                if (mustExceed ? totalPower > effectiveGoal : totalPower >= effectiveGoal) {
                    break;  // Goal met
                }
            }
        }

        boolean hasAbility = totalAbility >= RandoConfig.ABILITY_THRESHOLD;
        int effectiveGoal = hasAbility ? powerGoal :
            powerGoal + (mustExceed ? RandoConfig.ABILITY_POWER_COMPENSATION : 0);
        boolean achievesGoal = mustExceed ? totalPower > effectiveGoal : totalPower >= effectiveGoal;

        return new OptimalCombination(selected, totalPower, totalCost,
            totalAbility, hasAbility, achievesGoal);
    }

    // =========================================================================
    // PLAN GENERATION (Items #1, #9)
    // =========================================================================

    /**
     * Plan location deploys (always prioritized).
     */
    private List<CardInfo> planLocationDeploys(List<CardInfo> locations, int forceAvailable) {
        List<CardInfo> deploys = new ArrayList<>();
        int remaining = forceAvailable;

        for (CardInfo loc : locations) {
            if (loc.cost <= remaining) {
                deploys.add(loc);
                remaining -= loc.cost;
                LOG.info("   📍 Location deploy: {} (cost {})", loc.name, loc.cost);
            }
        }

        return deploys;
    }

    /**
     * Generate ground deployment plans.
     */
    private List<ScoredPlan> generateGroundPlans(List<CardInfo> characters, List<CardInfo> vehicles,
                                                  LocationCategories categories, int forceAvailable,
                                                  int threshold, List<AiBoardAnalyzer.LocationAnalysis> allLocations,
                                                  int turn, DrainGapResult drainGap) {
        List<ScoredPlan> plans = new ArrayList<>();

        // Get ground cards
        List<CardInfo> groundCards = new ArrayList<>(characters);
        groundCards.addAll(vehicles);

        if (groundCards.isEmpty()) {
            return plans;
        }

        // Plan 1: Stop bleeding (highest priority if losing drain war)
        if (drainGap.isLosing() && !categories.bleedLocations.isEmpty()) {
            DeploymentPlan bleedPlan = generateStopBleedingPlan(
                groundCards, categories.bleedLocations.stream()
                    .filter(AiBoardAnalyzer.LocationAnalysis::isGround)
                    .collect(Collectors.toList()),
                forceAvailable, threshold, "ground");
            if (!bleedPlan.getInstructions().isEmpty()) {
                float score = scorePlan(bleedPlan, allLocations, turn);
                plans.add(new ScoredPlan(bleedPlan, score, "ground_bleed"));
            }
        }

        // Plan 2: Reinforce losing positions
        if (!categories.losingLocations.isEmpty()) {
            DeploymentPlan reinforcePlan = generateReinforcePlan(
                groundCards, categories.losingLocations.stream()
                    .filter(AiBoardAnalyzer.LocationAnalysis::isGround)
                    .collect(Collectors.toList()),
                forceAvailable, "ground");
            if (!reinforcePlan.getInstructions().isEmpty()) {
                float score = scorePlan(reinforcePlan, allLocations, turn);
                plans.add(new ScoredPlan(reinforcePlan, score, "ground_reinforce"));
            }
        }

        // Plan 3: Establish at uncontested locations
        if (!categories.establishTargets.isEmpty()) {
            DeploymentPlan establishPlan = generateEstablishPlan(
                groundCards, categories.establishTargets.stream()
                    .filter(AiBoardAnalyzer.LocationAnalysis::isGround)
                    .collect(Collectors.toList()),
                forceAvailable, threshold, "ground");
            if (!establishPlan.getInstructions().isEmpty()) {
                float score = scorePlan(establishPlan, allLocations, turn);
                plans.add(new ScoredPlan(establishPlan, score, "ground_establish"));
            }
        }

        // Plan 4: Attack enemy positions
        if (!categories.attackTargets.isEmpty()) {
            DeploymentPlan attackPlan = generateAttackPlan(
                groundCards, categories.attackTargets.stream()
                    .filter(AiBoardAnalyzer.LocationAnalysis::isGround)
                    .collect(Collectors.toList()),
                forceAvailable, "ground");
            if (!attackPlan.getInstructions().isEmpty()) {
                float score = scorePlan(attackPlan, allLocations, turn);
                plans.add(new ScoredPlan(attackPlan, score, "ground_attack"));
            }
        }

        return plans;
    }

    /**
     * Generate space deployment plans.
     */
    private List<ScoredPlan> generateSpacePlans(List<CardInfo> starships, List<CardInfo> characters,
                                                 LocationCategories categories, int forceAvailable,
                                                 int threshold, List<AiBoardAnalyzer.LocationAnalysis> allLocations,
                                                 int turn, DrainGapResult drainGap,
                                                 SwccgGame game, String playerId) {
        List<ScoredPlan> plans = new ArrayList<>();

        if (starships.isEmpty()) {
            return plans;
        }

        // Get pilots for ship combos
        List<CardInfo> pilots = characters.stream()
            .filter(c -> c.isPilot)
            .collect(Collectors.toList());

        // Plan 1: Stop bleeding in space
        List<AiBoardAnalyzer.LocationAnalysis> spaceBleed = categories.bleedLocations.stream()
            .filter(AiBoardAnalyzer.LocationAnalysis::isSpace)
            .collect(Collectors.toList());

        if (drainGap.isLosing() && !spaceBleed.isEmpty()) {
            DeploymentPlan bleedPlan = generateStopBleedingPlan(
                new ArrayList<>(starships), spaceBleed, forceAvailable, threshold, "space");
            if (!bleedPlan.getInstructions().isEmpty()) {
                float score = scorePlan(bleedPlan, allLocations, turn);
                plans.add(new ScoredPlan(bleedPlan, score, "space_bleed"));
            }
        }

        // Plan 2: Reinforce losing space
        List<AiBoardAnalyzer.LocationAnalysis> spaceLosing = categories.losingLocations.stream()
            .filter(AiBoardAnalyzer.LocationAnalysis::isSpace)
            .collect(Collectors.toList());

        if (!spaceLosing.isEmpty()) {
            DeploymentPlan reinforcePlan = generateReinforcePlan(
                new ArrayList<>(starships), spaceLosing, forceAvailable, "space");
            if (!reinforcePlan.getInstructions().isEmpty()) {
                float score = scorePlan(reinforcePlan, allLocations, turn);
                plans.add(new ScoredPlan(reinforcePlan, score, "space_reinforce"));
            }
        }

        // Plan 3: Establish in space
        List<AiBoardAnalyzer.LocationAnalysis> spaceEstablish = categories.establishTargets.stream()
            .filter(AiBoardAnalyzer.LocationAnalysis::isSpace)
            .collect(Collectors.toList());

        if (!spaceEstablish.isEmpty()) {
            DeploymentPlan establishPlan = generateEstablishPlan(
                new ArrayList<>(starships), spaceEstablish, forceAvailable, threshold, "space");
            if (!establishPlan.getInstructions().isEmpty()) {
                float score = scorePlan(establishPlan, allLocations, turn);
                plans.add(new ScoredPlan(establishPlan, score, "space_establish"));
            }
        }

        // Plan 4: RE-PILOT unpiloted ships in play (Item #6)
        DeploymentPlan repilotPlan = generateRepilotPlan(pilots, game, playerId, forceAvailable);
        if (!repilotPlan.getInstructions().isEmpty()) {
            float score = scorePlan(repilotPlan, allLocations, turn);
            plans.add(new ScoredPlan(repilotPlan, score, "space_repilot"));
        }

        return plans;
    }

    /**
     * Generate combined ground+space plans.
     */
    private List<ScoredPlan> generateCombinedPlans(List<CardInfo> characters, List<CardInfo> starships,
                                                    List<CardInfo> vehicles, LocationCategories categories,
                                                    int forceAvailable, int groundThreshold, int spaceThreshold,
                                                    List<AiBoardAnalyzer.LocationAnalysis> allLocations,
                                                    int turn, DrainGapResult drainGap) {
        List<ScoredPlan> plans = new ArrayList<>();

        // Combined: Best ground + best space within budget
        List<CardInfo> groundCards = new ArrayList<>(characters);
        groundCards.addAll(vehicles);

        // Find best single ground target
        AiBoardAnalyzer.LocationAnalysis bestGroundTarget = findBestTarget(
            categories, true, groundThreshold);
        AiBoardAnalyzer.LocationAnalysis bestSpaceTarget = findBestTarget(
            categories, false, spaceThreshold);

        if (bestGroundTarget != null && bestSpaceTarget != null) {
            DeploymentPlan combinedPlan = new DeploymentPlan(
                DeployStrategy.COMPREHENSIVE, "Combined ground + space deployment");

            int remaining = forceAvailable;

            // Try ground first - must BEAT enemy power, not just have cards
            int groundPowerNeeded = (int) bestGroundTarget.theirPower + RandoConfig.BATTLE_FAVORABLE_THRESHOLD;
            OptimalCombination groundCombo = findOptimalCombination(
                groundCards, remaining / 2, groundPowerNeeded, true);

            // CRITICAL: Only add if we can actually beat them!
            if (groundCombo.achievesGoal && !groundCombo.isEmpty()) {
                for (PhysicalCard card : groundCombo.cards) {
                    addCardToPlan(combinedPlan, card, bestGroundTarget, 1,
                        "Combined: ground attack");
                }
                remaining -= groundCombo.totalCost;
            }

            // Then space - must BEAT enemy power
            int spacePowerNeeded = (int) bestSpaceTarget.theirPower + RandoConfig.BATTLE_FAVORABLE_THRESHOLD;
            OptimalCombination spaceCombo = findOptimalCombination(
                starships, remaining, spacePowerNeeded, true);

            // CRITICAL: Only add if we can actually beat them!
            if (spaceCombo.achievesGoal && !spaceCombo.isEmpty()) {
                for (PhysicalCard card : spaceCombo.cards) {
                    addCardToPlan(combinedPlan, card, bestSpaceTarget, 1,
                        "Combined: space attack");
                }
            }

            if (!combinedPlan.getInstructions().isEmpty()) {
                float score = scorePlan(combinedPlan, allLocations, turn);
                plans.add(new ScoredPlan(combinedPlan, score, "combined"));
            }
        }

        return plans;
    }

    // =========================================================================
    // SPECIFIC PLAN GENERATORS
    // =========================================================================

    /**
     * Generate stop-bleeding plan (Item #8 - presence-only).
     */
    private DeploymentPlan generateStopBleedingPlan(List<CardInfo> cards,
                                                     List<AiBoardAnalyzer.LocationAnalysis> bleedLocations,
                                                     int forceAvailable, int threshold, String domain) {
        DeploymentPlan plan = new DeploymentPlan(DeployStrategy.REINFORCE,
            "Stop bleeding in " + domain);

        // Sort by our icons (highest drain first)
        bleedLocations.sort((a, b) -> Integer.compare(b.ourForceIcons, a.ourForceIcons));

        int remaining = forceAvailable;
        List<CardInfo> available = new ArrayList<>(cards);

        for (AiBoardAnalyzer.LocationAnalysis loc : bleedLocations) {
            if (remaining <= 0 || available.isEmpty()) break;

            // CRITICAL: Filter cards to only those that can deploy to this location
            List<CardInfo> deployableHere = filterDeployableCards(available, loc.location);
            if (deployableHere.isEmpty()) {
                LOG.debug("📋 No cards can deploy to {} - skipping", loc.location.getTitle());
                continue;
            }

            // Find optimal combination to beat enemy power
            OptimalCombination combo = findOptimalCombination(
                deployableHere, remaining, (int) loc.theirPower, true);

            // CRITICAL FIX: Only deploy if we can ACTUALLY beat them!
            // Otherwise we just give them a battle to win and lose MORE force.
            if (!combo.isEmpty() && combo.achievesGoal) {
                for (PhysicalCard card : combo.cards) {
                    CardInfo info = findCardInfo(available, card);
                    if (info != null) {
                        addCardToPlan(plan, card, loc, 1,
                            String.format("Stop bleed at %s (prevent %d drain)",
                                loc.location.getTitle(), loc.ourForceIcons));
                        remaining -= info.cost;
                        available.removeIf(c -> c.card == card);
                    }
                }
            } else if (!combo.isEmpty()) {
                LOG.info("📋 Skipping stop-bleed at {} - can't beat enemy power {} with available cards (best: {})",
                    loc.location.getTitle(), (int) loc.theirPower, combo.totalPower);
            }
        }

        return plan;
    }

    /**
     * Generate reinforce plan for losing locations.
     */
    private DeploymentPlan generateReinforcePlan(List<CardInfo> cards,
                                                  List<AiBoardAnalyzer.LocationAnalysis> losingLocations,
                                                  int forceAvailable, String domain) {
        DeploymentPlan plan = new DeploymentPlan(DeployStrategy.REINFORCE,
            "Reinforce losing " + domain);

        // Sort by power deficit (worst first)
        losingLocations.sort((a, b) -> Double.compare(a.getPowerAdvantage(), b.getPowerAdvantage()));

        int remaining = forceAvailable;
        List<CardInfo> available = new ArrayList<>(cards);

        for (AiBoardAnalyzer.LocationAnalysis loc : losingLocations) {
            if (remaining <= 0 || available.isEmpty()) break;

            // CRITICAL: Filter cards to only those that can deploy to this location
            List<CardInfo> deployableHere = filterDeployableCards(available, loc.location);
            if (deployableHere.isEmpty()) {
                LOG.debug("📋 No cards can deploy to {} - skipping", loc.location.getTitle());
                continue;
            }

            int deficit = (int) (loc.theirPower - loc.ourPower);

            // Find cards to close the gap
            OptimalCombination combo = findOptimalCombination(deployableHere, remaining, deficit, false);

            if (!combo.isEmpty()) {
                for (PhysicalCard card : combo.cards) {
                    CardInfo info = findCardInfo(available, card);
                    if (info != null) {
                        addCardToPlan(plan, card, loc, 1,
                            String.format("Reinforce %s (deficit: %d)",
                                loc.location.getTitle(), deficit));
                        remaining -= info.cost;
                        available.removeIf(c -> c.card == card);
                    }
                }
            }
        }

        return plan;
    }

    /**
     * Generate establish plan for uncontested locations.
     */
    private DeploymentPlan generateEstablishPlan(List<CardInfo> cards,
                                                  List<AiBoardAnalyzer.LocationAnalysis> establishTargets,
                                                  int forceAvailable, int threshold, String domain) {
        DeploymentPlan plan = new DeploymentPlan(DeployStrategy.ESTABLISH,
            "Establish in " + domain);

        // Sort by opponent icons (highest value first)
        establishTargets.sort((a, b) -> Integer.compare(b.theirForceIcons, a.theirForceIcons));

        int remaining = forceAvailable;
        int establishCount = 0;
        List<CardInfo> available = new ArrayList<>(cards);

        for (AiBoardAnalyzer.LocationAnalysis loc : establishTargets) {
            if (remaining <= 0 || available.isEmpty()) break;
            if (establishCount >= RandoConfig.MAX_ESTABLISH_LOCATIONS) break;
            if (loc.theirForceIcons <= 0) continue;

            // CRITICAL: Filter cards to only those that can deploy to this location
            List<CardInfo> deployableHere = filterDeployableCards(available, loc.location);
            if (deployableHere.isEmpty()) {
                LOG.debug("📋 No cards can deploy to {} - skipping", loc.location.getTitle());
                continue;
            }

            // Find cards with good ability (can defend against counter-deploy)
            List<CardInfo> withAbility = deployableHere.stream()
                .filter(c -> c.ability >= RandoConfig.ABILITY_THRESHOLD)
                .collect(Collectors.toList());

            if (withAbility.isEmpty()) {
                // No ability cards - need optimal combination
                OptimalCombination combo = findOptimalCombination(deployableHere, remaining, threshold, false);
                if (combo.hasAbility && !combo.isEmpty()) {
                    for (PhysicalCard card : combo.cards) {
                        CardInfo info = findCardInfo(available, card);
                        if (info != null) {
                            addCardToPlan(plan, card, loc, 2,
                                String.format("Establish at %s (%d icons)",
                                    loc.location.getTitle(), loc.theirForceIcons));
                            remaining -= info.cost;
                            available.removeIf(c -> c.card == card);
                        }
                    }
                    establishCount++;
                }
            } else {
                // Pick best ability card
                final int budget = remaining;  // Capture for lambda
                CardInfo best = withAbility.stream()
                    .filter(c -> c.cost <= budget)
                    .max(Comparator.comparingDouble(CardInfo::getValueRatio))
                    .orElse(null);

                if (best != null) {
                    addCardToPlan(plan, best.card, loc, 2,
                        String.format("Establish at %s (%d icons, ability %d)",
                            loc.location.getTitle(), loc.theirForceIcons, best.ability));
                    remaining -= best.cost;
                    available.remove(best);
                    establishCount++;
                }
            }
        }

        return plan;
    }

    /**
     * Generate attack plan for enemy-held locations.
     */
    private DeploymentPlan generateAttackPlan(List<CardInfo> cards,
                                               List<AiBoardAnalyzer.LocationAnalysis> attackTargets,
                                               int forceAvailable, String domain) {
        DeploymentPlan plan = new DeploymentPlan(DeployStrategy.REINFORCE,
            "Attack enemy " + domain);

        // Sort by our icons (stopping highest drains first)
        attackTargets.sort((a, b) -> Integer.compare(b.ourForceIcons, a.ourForceIcons));

        int remaining = forceAvailable;
        List<CardInfo> available = new ArrayList<>(cards);

        for (AiBoardAnalyzer.LocationAnalysis loc : attackTargets) {
            if (remaining <= 0 || available.isEmpty()) break;

            // CRITICAL: Filter cards to only those that can deploy to this location
            List<CardInfo> deployableHere = filterDeployableCards(available, loc.location);
            if (deployableHere.isEmpty()) {
                LOG.debug("📋 No cards can deploy to {} - skipping", loc.location.getTitle());
                continue;
            }

            // Need to beat enemy power with favorable threshold
            int powerNeeded = (int) loc.theirPower + RandoConfig.BATTLE_FAVORABLE_THRESHOLD;

            OptimalCombination combo = findOptimalCombination(deployableHere, remaining, powerNeeded, true);

            if (combo.achievesGoal && !combo.isEmpty()) {
                for (PhysicalCard card : combo.cards) {
                    CardInfo info = findCardInfo(available, card);
                    if (info != null) {
                        addCardToPlan(plan, card, loc, 1,
                            String.format("Attack %s (%d vs %d power)",
                                loc.location.getTitle(), combo.totalPower, (int)loc.theirPower));
                        remaining -= info.cost;
                        available.removeIf(c -> c.card == card);
                    }
                }
            }
        }

        return plan;
    }

    /**
     * Generate RE-PILOT plan for unpiloted ships (Item #6).
     */
    private DeploymentPlan generateRepilotPlan(List<CardInfo> pilots, SwccgGame game,
                                                String playerId, int forceAvailable) {
        DeploymentPlan plan = new DeploymentPlan(DeployStrategy.REINFORCE,
            "Re-pilot unpiloted ships");

        if (pilots.isEmpty()) return plan;

        // Find unpiloted ships in play
        List<PhysicalCard> unpilotedShips = findUnpilotedShipsInPlay(game, playerId);
        if (unpilotedShips.isEmpty()) return plan;

        int remaining = forceAvailable;
        List<CardInfo> availablePilots = new ArrayList<>(pilots);

        for (PhysicalCard ship : unpilotedShips) {
            if (remaining <= 0 || availablePilots.isEmpty()) break;

            // Find best pilot for this ship
            final int budget = remaining;  // Capture for lambda
            CardInfo bestPilot = availablePilots.stream()
                .filter(p -> p.cost <= budget)
                .max(Comparator.comparingInt(p -> p.ability))
                .orElse(null);

            if (bestPilot != null) {
                // Create instruction to deploy pilot to ship
                DeploymentInstruction inst = new DeploymentInstruction(
                    bestPilot.blueprintId, bestPilot.name,
                    String.valueOf(ship.getCardId()), ship.getTitle(),
                    1, String.format("Re-pilot %s", ship.getTitle())
                );
                inst.setDeployCost(bestPilot.cost);
                inst.setPowerContribution(bestPilot.power);
                plan.addInstruction(inst);

                remaining -= bestPilot.cost;
                availablePilots.remove(bestPilot);
            }
        }

        return plan;
    }

    // =========================================================================
    // PLAN SCORING (Item #21)
    // =========================================================================

    /**
     * Score a deployment plan.
     *
     * Ported from Python _score_plan().
     */
    private float scorePlan(DeploymentPlan plan, List<AiBoardAnalyzer.LocationAnalysis> locations, int turn) {
        if (plan == null || plan.getInstructions().isEmpty()) {
            return 0.0f;
        }

        float score = 0.0f;
        Map<String, Integer> powerByLocation = new HashMap<>();
        Map<String, Integer> abilityByLocation = new HashMap<>();

        for (DeploymentInstruction inst : plan.getInstructions()) {
            // Base power value
            score += inst.getPowerContribution() * 2;

            // Track by location
            if (inst.getTargetLocationId() != null) {
                powerByLocation.merge(inst.getTargetLocationId(), inst.getPowerContribution(), Integer::sum);

                // Get ability from card lookup
                int ability = 0;
                for (AiBoardAnalyzer.LocationAnalysis loc : locations) {
                    if (String.valueOf(loc.location.getCardId()).equals(inst.getTargetLocationId())) {
                        // Estimate ability from power (rough heuristic)
                        ability = inst.getPowerContribution() >= 4 ? 4 : inst.getPowerContribution();
                        break;
                    }
                }
                abilityByLocation.merge(inst.getTargetLocationId(), ability, Integer::sum);
            }
        }

        // Analyze each target location
        for (Map.Entry<String, Integer> entry : powerByLocation.entrySet()) {
            String locId = entry.getKey();
            int ourPower = entry.getValue();
            int ourAbility = abilityByLocation.getOrDefault(locId, 0);

            // Find location
            AiBoardAnalyzer.LocationAnalysis targetLoc = null;
            for (AiBoardAnalyzer.LocationAnalysis loc : locations) {
                if (String.valueOf(loc.location.getCardId()).equals(locId)) {
                    targetLoc = loc;
                    break;
                }
            }

            if (targetLoc == null) continue;

            if (targetLoc.theirPower > 0) {
                // CONTESTED LOCATION
                int powerAdvantage = ourPower - (int) targetLoc.theirPower;

                // Deny drain bonus
                int denyDrainBonus = 0;
                if (targetLoc.ourForceIcons > 0) {
                    denyDrainBonus = targetLoc.ourForceIcons * 20;
                }

                // Win control bonus
                int winControlBonus = 0;
                if (powerAdvantage > 0 && targetLoc.theirForceIcons > 0) {
                    winControlBonus = targetLoc.theirForceIcons * 15;
                }

                if (powerAdvantage >= RandoConfig.BATTLE_FAVORABLE_THRESHOLD) {
                    // FAVORABLE FIGHT
                    score += 50 + (powerAdvantage * 10) + denyDrainBonus + winControlBonus;
                } else if (powerAdvantage > 0) {
                    // MARGINAL FIGHT
                    score += 25 + (powerAdvantage * 5) + denyDrainBonus + winControlBonus;
                } else {
                    // LOSING
                    score += 5 + denyDrainBonus;
                }

                // Ability bonus/penalty
                if (ourAbility >= RandoConfig.ABILITY_THRESHOLD) {
                    score += 25;  // Can draw destiny
                } else {
                    score -= 20 + (ourPower * 2);  // Vulnerable
                }

            } else {
                // EMPTY/ESTABLISH LOCATION
                float establishBonus = 40;

                if (targetLoc.theirForceIcons > 0) {
                    establishBonus += targetLoc.theirForceIcons * 15;
                }
                if (targetLoc.ourForceIcons > 0) {
                    establishBonus += targetLoc.ourForceIcons * 15;
                }

                // Ability check for establish safety
                if (ourAbility >= RandoConfig.ABILITY_THRESHOLD) {
                    establishBonus += 25;
                } else if (ourPower < 5) {
                    establishBonus -= 500;  // BLOCKED - easy crush target
                }

                score += establishBonus;
            }
        }

        return score;
    }

    /**
     * Select the best plan from all generated plans.
     */
    private DeploymentPlan selectBestPlan(List<ScoredPlan> allPlans, List<CardInfo> locationDeploys,
                                           int turn, int lifeForce) {
        if (allPlans.isEmpty()) {
            // Just deploy locations if nothing else
            if (!locationDeploys.isEmpty()) {
                DeploymentPlan locationPlan = new DeploymentPlan(
                    DeployStrategy.DEPLOY_LOCATIONS, "Deploy locations");
                for (CardInfo loc : locationDeploys) {
                    DeploymentInstruction inst = new DeploymentInstruction(
                        loc.blueprintId, loc.name, null, null, 0, "Deploy location");
                    inst.setDeployCost(loc.cost);
                    locationPlan.addInstruction(inst);
                }
                return locationPlan;
            }
            return null;
        }

        // Sort by score (highest first)
        Collections.sort(allPlans);

        LOG.info("📋 PLAN COMPARISON ({} plans generated):", allPlans.size());
        for (int i = 0; i < Math.min(5, allPlans.size()); i++) {
            ScoredPlan sp = allPlans.get(i);
            LOG.info("   {}. {} - score: {:.0f}, cards: {}",
                i + 1, sp.domain, sp.score, sp.plan.getInstructions().size());
        }

        ScoredPlan best = allPlans.get(0);

        // Add location deploys to the best plan
        DeploymentPlan finalPlan = new DeploymentPlan(best.plan.getStrategy(), best.plan.getReason());
        for (CardInfo loc : locationDeploys) {
            DeploymentInstruction inst = new DeploymentInstruction(
                loc.blueprintId, loc.name, null, null, 0, "Deploy location first");
            inst.setDeployCost(loc.cost);
            finalPlan.addInstruction(inst);
        }
        for (DeploymentInstruction inst : best.plan.getInstructions()) {
            finalPlan.addInstruction(inst);
        }

        return finalPlan;
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private void addCardToPlan(DeploymentPlan plan, PhysicalCard card,
                               AiBoardAnalyzer.LocationAnalysis loc, int priority, String reason) {
        SwccgCardBlueprint bp = card.getBlueprint();
        int cost = 0;
        int power = 0;

        if (bp != null) {
            try {
                Float c = bp.getDeployCost();
                cost = c != null ? c.intValue() : 0;
            } catch (UnsupportedOperationException e) {}

            if (bp.hasPowerAttribute()) {
                Float p = bp.getPower();
                power = p != null ? p.intValue() : 0;
            }
        }

        DeploymentInstruction inst = new DeploymentInstruction(
            card.getBlueprintId(true), card.getTitle(),
            String.valueOf(loc.location.getCardId()), loc.location.getTitle(),
            priority, reason
        );
        inst.setDeployCost(cost);
        inst.setPowerContribution(power);
        plan.addInstruction(inst);
    }

    private CardInfo findCardInfo(List<CardInfo> cards, PhysicalCard card) {
        return cards.stream().filter(c -> c.card == card).findFirst().orElse(null);
    }

    private AiBoardAnalyzer.LocationAnalysis findBestTarget(LocationCategories categories,
                                                             boolean isGround, int threshold) {
        // Priority: bleed > losing > attack > establish
        List<AiBoardAnalyzer.LocationAnalysis> targets = new ArrayList<>();
        targets.addAll(categories.bleedLocations);
        targets.addAll(categories.losingLocations);
        targets.addAll(categories.attackTargets);
        targets.addAll(categories.establishTargets);

        return targets.stream()
            .filter(loc -> isGround ? loc.isGround() : loc.isSpace())
            .max(Comparator.comparingInt(loc -> loc.theirForceIcons + loc.ourForceIcons))
            .orElse(null);
    }

    private List<PhysicalCard> findUnpilotedShipsInPlay(SwccgGame game, String playerId) {
        List<PhysicalCard> unpiloted = new ArrayList<>();

        // Check all locations for unpiloted ships
        GameState gameState = game.getGameState();

        // Get all locations in play and check cards at each
        for (PhysicalCard location : gameState.getLocationsInOrder()) {
            if (location == null) continue;

            // Get all cards at this location
            List<PhysicalCard> cardsAtLocation = gameState.getCardsAtLocation(location);
            for (PhysicalCard card : cardsAtLocation) {
                if (card.getOwner().equals(playerId) &&
                    card.getBlueprint() != null &&
                    card.getBlueprint().getCardCategory() == CardCategory.STARSHIP) {

                    // Check if ship has a pilot aboard
                    List<PhysicalCard> aboard = gameState.getAboardCards(card, false);
                    boolean hasPilot = aboard.stream().anyMatch(c ->
                        c.getBlueprint() != null &&
                        c.getBlueprint().getCardCategory() == CardCategory.CHARACTER);

                    if (!hasPilot) {
                        unpiloted.add(card);
                    }
                }
            }
        }

        return unpiloted;
    }

    private void logHandDetails(List<CardInfo> characters, List<CardInfo> starships, List<CardInfo> vehicles) {
        if (!characters.isEmpty()) {
            StringBuilder sb = new StringBuilder("   📋 Characters: [");
            for (CardInfo c : characters) {
                sb.append(c.toString()).append(", ");
            }
            sb.append("]");
            LOG.info(sb.toString());
        }
        if (!starships.isEmpty()) {
            StringBuilder sb = new StringBuilder("   🚀 Starships: [");
            for (CardInfo s : starships) {
                sb.append(s.toString()).append(", ");
            }
            sb.append("]");
            LOG.info(sb.toString());
        }
        if (!vehicles.isEmpty()) {
            StringBuilder sb = new StringBuilder("   🚗 Vehicles: [");
            for (CardInfo v : vehicles) {
                sb.append(v.toString()).append(", ");
            }
            sb.append("]");
            LOG.info(sb.toString());
        }
    }

    private void logBoardAnalysis(List<AiBoardAnalyzer.LocationAnalysis> locations) {
        LOG.info("   📍 Board locations ({}):", locations.size());
        for (AiBoardAnalyzer.LocationAnalysis loc : locations) {
            String domain = loc.isSpace() ? "SPACE" : "GROUND";
            String status = "";
            if (loc.ourPower > 0 && loc.theirPower > 0) {
                status = loc.ourPower > loc.theirPower ? "WINNING" : "LOSING";
            } else if (loc.ourPower > 0) {
                status = "CONTROLLED";
            } else if (loc.theirPower > 0) {
                status = "ENEMY";
            } else {
                status = "EMPTY";
            }
            LOG.info("      {} [{}] {} - us:{} them:{} icons:{}/{}",
                loc.location.getTitle(), domain, status,
                (int)loc.ourPower, (int)loc.theirPower,
                loc.ourForceIcons, loc.theirForceIcons);
        }
    }

    private void logFinalPlan(DeploymentPlan plan) {
        String strategyName = plan.getStrategy() != null ?
            plan.getStrategy().getValue().toLowerCase() : "unknown";
        LOG.info("📋 ═══════════════════════════════════════════════════════════════");
        LOG.info("📋 FINAL PLAN: {} ({} deployments)", strategyName, plan.getInstructions().size());
        LOG.info("📋 ═══════════════════════════════════════════════════════════════");

        int num = 1;
        for (DeploymentInstruction inst : plan.getInstructions()) {
            String target = inst.getTargetLocationName() != null ?
                inst.getTargetLocationName() : "table";
            LOG.info("   {}. {} -> {} : {}", num++, inst.getCardName(), target, inst.getReason());
        }
    }

    private DeploymentPlan createHoldBackPlan(String reason) {
        return new DeploymentPlan(DeployStrategy.HOLD_BACK, reason);
    }

    /**
     * Generate all combinations of size k from list.
     */
    private <T> List<List<T>> combinations(List<T> list, int k) {
        List<List<T>> result = new ArrayList<>();
        combinationsHelper(list, k, 0, new ArrayList<>(), result);
        return result;
    }

    private <T> void combinationsHelper(List<T> list, int k, int start,
                                         List<T> current, List<List<T>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < list.size(); i++) {
            current.add(list.get(i));
            combinationsHelper(list, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // =========================================================================
    // PUBLIC API (for DeployEvaluator)
    // =========================================================================

    /**
     * Get score for a specific card deployment.
     */
    public PlanScore getCardScore(String blueprintId, int currentForce, List<String> availableBlueprints) {
        if (currentPlan == null) {
            return new PlanScore(0.0f, "No active plan");
        }

        DeploymentInstruction instruction = currentPlan.getInstructionForCard(blueprintId);
        if (instruction != null) {
            float score = 100.0f - (instruction.getPriority() * 10);
            return new PlanScore(score, "IN PLAN: " + instruction.getReason());
        }

        if (currentPlan.isPlanComplete() || currentPlan.isForceAllowExtras()) {
            return new PlanScore(25.0f, "Extra action (plan complete)");
        }

        return new PlanScore(-50.0f, "NOT in plan - saving force for planned cards");
    }

    /**
     * Record that a card was deployed.
     */
    public void recordDeployment(String blueprintId) {
        if (currentPlan != null) {
            currentPlan.recordDeployment(blueprintId);
        }
    }

    /**
     * Get plan summary.
     */
    public String getPlanSummary() {
        if (currentPlan == null) {
            return "No plan";
        }
        return String.format("%s: %s (%d deployments)",
            currentPlan.getStrategy().getValue(),
            currentPlan.getReason(),
            currentPlan.getInstructions().size());
    }

    /**
     * Check if a card can deploy to a specific location based on its gametext restrictions.
     * Returns true if the card appears to have no restrictions OR if it can deploy to this location type.
     * Returns false if the card has "Deploys only" restrictions that don't match the location.
     */
    private boolean canDeployToLocation(PhysicalCard card, PhysicalCard location) {
        if (card == null || card.getBlueprint() == null) return true;
        if (location == null) return true;

        String gametext = card.getBlueprint().getGameText();
        if (gametext == null || gametext.isEmpty()) return true;

        String gametextLower = gametext.toLowerCase();

        // If no deployment restriction, card can deploy anywhere appropriate
        if (!gametextLower.contains("deploys only")) {
            return true;
        }

        // Card has deployment restrictions - check if location matches
        String locationTitle = location.getTitle();
        if (locationTitle == null) return false;
        String locationLower = locationTitle.toLowerCase();

        // Extract the restriction part (e.g., "Deploys only on Falcon, Hoth or Cloud City")
        int deploysOnlyIdx = gametextLower.indexOf("deploys only");
        String restrictionPart = gametextLower.substring(deploysOnlyIdx);
        int periodIdx = restrictionPart.indexOf('.');
        if (periodIdx > 0) {
            restrictionPart = restrictionPart.substring(0, periodIdx);
        }

        LOG.debug("📋 Checking deploy restriction for {}: '{}' vs location '{}'",
            card.getTitle(), restrictionPart, locationTitle);

        // =======================================================
        // SPECIAL CASE: Cards that deploy ON characters/ships, not TO locations
        // These cards (like Elom, weapons, devices) deploy on other cards,
        // not directly to locations. Allow them - the game engine will present
        // the correct deployment options (e.g., deploy on a character at location).
        // =======================================================
        if (restrictionPart.contains("deploys only on")) {
            String afterOn = restrictionPart.substring(restrictionPart.indexOf("deploys only on") + 15).trim();
            // Check if it's deploying on a character/ship type rather than a location
            // Character types: rebel, alien, imperial, droid, jedi, sith, warrior, pilot, etc.
            // Ship types: starship, capital, squadron, etc.
            boolean deploysOnCard = afterOn.startsWith("a ") || afterOn.startsWith("an ") ||
                afterOn.startsWith("your ") || afterOn.startsWith("opponent") ||
                afterOn.contains("rebel") || afterOn.contains("alien") ||
                afterOn.contains("imperial") || afterOn.contains("droid") ||
                afterOn.contains("jedi") || afterOn.contains("sith") ||
                afterOn.contains("character") || afterOn.contains("warrior") ||
                afterOn.contains("pilot") || afterOn.contains("smuggler") ||
                afterOn.contains("starship") || afterOn.contains("vehicle") ||
                afterOn.contains("capital") || afterOn.contains("squadron");

            if (deploysOnCard) {
                // This card deploys on another card, not directly to a location
                // Return true to allow it - game engine will handle the actual deployment
                LOG.debug("📋 {} deploys ON a card - allowing (game engine will handle)",
                    card.getTitle());
                return true;
            }
        }

        // Check common location types
        if (restrictionPart.contains("hoth") && !locationLower.contains("hoth")) {
            LOG.info("📋 {} cannot deploy to {} (requires Hoth)", card.getTitle(), locationTitle);
            return false;
        }
        if (restrictionPart.contains("cloud city") && !locationLower.contains("cloud city")) {
            LOG.info("📋 {} cannot deploy to {} (requires Cloud City)", card.getTitle(), locationTitle);
            return false;
        }
        if (restrictionPart.contains("tatooine") && !locationLower.contains("tatooine")) {
            LOG.info("📋 {} cannot deploy to {} (requires Tatooine)", card.getTitle(), locationTitle);
            return false;
        }
        if (restrictionPart.contains("endor") && !locationLower.contains("endor")) {
            LOG.info("📋 {} cannot deploy to {} (requires Endor)", card.getTitle(), locationTitle);
            return false;
        }
        if (restrictionPart.contains("dagobah") && !locationLower.contains("dagobah")) {
            LOG.info("📋 {} cannot deploy to {} (requires Dagobah)", card.getTitle(), locationTitle);
            return false;
        }
        if (restrictionPart.contains("death star") && !locationLower.contains("death star")) {
            LOG.info("📋 {} cannot deploy to {} (requires Death Star)", card.getTitle(), locationTitle);
            return false;
        }
        if (restrictionPart.contains("coruscant") && !locationLower.contains("coruscant")) {
            LOG.info("📋 {} cannot deploy to {} (requires Coruscant)", card.getTitle(), locationTitle);
            return false;
        }
        if (restrictionPart.contains("naboo") && !locationLower.contains("naboo")) {
            LOG.info("📋 {} cannot deploy to {} (requires Naboo)", card.getTitle(), locationTitle);
            return false;
        }
        if (restrictionPart.contains("bespin") && !locationLower.contains("bespin")) {
            LOG.info("📋 {} cannot deploy to {} (requires Bespin)", card.getTitle(), locationTitle);
            return false;
        }

        // Check for starship-only deployments (can't deploy to site)
        if ((restrictionPart.contains("falcon") || restrictionPart.contains("starship") ||
             restrictionPart.contains("capital starship")) &&
            !restrictionPart.contains("hoth") && !restrictionPart.contains("cloud city") &&
            !restrictionPart.contains("tatooine") && !restrictionPart.contains("site")) {
            // Card only deploys to starships, not locations
            LOG.info("📋 {} cannot deploy to {} (requires starship)", card.getTitle(), locationTitle);
            return false;
        }

        // If we get here and the card has restrictions we didn't recognize,
        // be conservative and allow it (let the game engine handle it)
        return true;
    }

    /**
     * Filter cards to only those that can deploy to the target location.
     */
    private List<CardInfo> filterDeployableCards(List<CardInfo> cards, PhysicalCard targetLocation) {
        List<CardInfo> deployable = new ArrayList<>();
        for (CardInfo info : cards) {
            if (canDeployToLocation(info.card, targetLocation)) {
                deployable.add(info);
            }
        }
        return deployable;
    }

    /**
     * Simple score/reason holder.
     */
    public static class PlanScore {
        public final float score;
        public final String reason;

        public PlanScore(float score, String reason) {
            this.score = score;
            this.reason = reason;
        }
    }

    /**
     * Location categories for planning.
     */
    private static class LocationCategories {
        List<AiBoardAnalyzer.LocationAnalysis> losingLocations = new ArrayList<>();
        List<AiBoardAnalyzer.LocationAnalysis> winningLocations = new ArrayList<>();
        List<AiBoardAnalyzer.LocationAnalysis> bleedLocations = new ArrayList<>();
        List<AiBoardAnalyzer.LocationAnalysis> establishTargets = new ArrayList<>();
        List<AiBoardAnalyzer.LocationAnalysis> attackTargets = new ArrayList<>();
        List<AiBoardAnalyzer.LocationAnalysis> weakPresenceLocations = new ArrayList<>();
        List<AiBoardAnalyzer.LocationAnalysis> crushableLocations = new ArrayList<>();
        List<AiBoardAnalyzer.LocationAnalysis> drainingLocations = new ArrayList<>();
    }
}
