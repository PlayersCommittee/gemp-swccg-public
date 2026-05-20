package com.gempukku.swccgo.ai.models.rando.strategy;

import com.gempukku.swccgo.ai.models.rando.evaluators.DecisionContext;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.state.GameState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * V68 ActionAudit — unified pre-flight validation for any action under consideration.
 *
 * <p>
 * <b>Why this exists.</b> Rando's evaluators are organized by class
 * (DeployEvaluator, MoveEvaluator, ActionTextEvaluator, CardSelectionEvaluator).
 * The same logical concept — for example, "deploy this character" — flows through
 * different evaluators depending on the route (hand→table vs reserve-pull vs lost-pile-pull).
 * Each route had its own scoring path, so a fix wired to one path didn't catch the same
 * bug appearing on another path. Result: V67ac/V67h/V67ad/V67g all patched specific
 * routes, but the same logical bugs kept resurfacing through new routes.
 *
 * <p>
 * <b>What it does.</b> ActionAudit is a routing-agnostic layer that any evaluator can
 * consult before scoring a candidate action. It answers a uniform set of questions:
 * <ul>
 * <li>{@link AuditCategory#AFFORDABILITY} — Can Rando pay the cost? (consolidates V67ac)</li>
 * <li>{@link AuditCategory#TARGET_VALID} — Does Rando have a useful target? (consolidates V67ad)</li>
 * <li>{@link AuditCategory#ZONE_PRESENCE} — Is the target actually in the source zone? (consolidates V67h)</li>
 * <li>{@link AuditCategory#DRAIN_DELTA} — Does this move lose drain pressure? (consolidates V67g + V67ae)</li>
 * <li>{@link AuditCategory#OBJECTIVE_CRITICAL} — Would this lose a card the deck needs to flip? (consolidates V21)</li>
 * <li>{@link AuditCategory#REDUNDANT} — Is the action a no-op? (consolidates V66)</li>
 * <li>{@link AuditCategory#WASTEFUL_DEPLOY} — Is this a non-BG stack or other waste? (consolidates V67ag/ah)</li>
 * </ul>
 *
 * <p>
 * <b>Usage from any evaluator:</b>
 * <pre>{@code
 * AuditReport report = ActionAudit.audit(context, ActionDescriptor.deployFromHand(card));
 * if (report.hardBlock()) {
 *     action.addReasoning("AUDIT BLOCK: " + report.reason(), -9999);
 *     continue;  // skip rest of scoring
 * }
 * if (report.scoreModifier() != 0) {
 *     action.addReasoning("AUDIT: " + report.reason(), report.scoreModifier());
 * }
 * }</pre>
 *
 * <p>
 * <b>Migration plan.</b> This class starts as an additive layer alongside the existing
 * V67* checks. As checks are migrated INTO ActionAudit, the V67* code in evaluators
 * shrinks. Eventually the goal is each evaluator has only route-specific scoring
 * (e.g. "this is a battle action, score the destiny advantage") and all
 * cross-cutting safety checks live here.
 */
public final class ActionAudit {

    private static final Logger LOG = LogManager.getLogger(ActionAudit.class);

    // -------------------- Audit categories --------------------

    public enum AuditCategory {
        AFFORDABILITY,
        TARGET_VALID,
        ZONE_PRESENCE,
        DRAIN_DELTA,
        OBJECTIVE_CRITICAL,
        REDUNDANT,
        WASTEFUL_DEPLOY,
    }

    // -------------------- Result types --------------------

    /**
     * One observation about an action — e.g. "you can't afford this" or "stacks at non-BG."
     * Multiple findings can apply to one action (e.g. AFFORDABILITY ok but DRAIN_DELTA bad).
     */
    public static final class AuditFinding {
        public final AuditCategory category;
        public final boolean hardBlock;
        public final float scoreModifier;
        public final String reason;

        public AuditFinding(AuditCategory category, boolean hardBlock,
                            float scoreModifier, String reason) {
            this.category = category;
            this.hardBlock = hardBlock;
            this.scoreModifier = scoreModifier;
            this.reason = reason;
        }
    }

    /**
     * Aggregated result of {@link #audit}. Combines all findings into a single
     * decision: hardBlock if any finding hard-blocks; total scoreModifier is the sum
     * of all soft modifiers; reason is a "; "-joined summary.
     */
    public static final class AuditReport {
        private final List<AuditFinding> findings = new ArrayList<>();

        public List<AuditFinding> findings() { return findings; }

        public boolean hardBlock() {
            for (AuditFinding f : findings) if (f.hardBlock) return true;
            return false;
        }

        public float scoreModifier() {
            float sum = 0f;
            for (AuditFinding f : findings) sum += f.scoreModifier;
            return sum;
        }

        public String reason() {
            StringBuilder sb = new StringBuilder();
            for (AuditFinding f : findings) {
                if (sb.length() > 0) sb.append("; ");
                sb.append(f.reason);
            }
            return sb.toString();
        }

        public boolean isEmpty() { return findings.isEmpty(); }

        public AuditReport add(AuditFinding f) { findings.add(f); return this; }
    }

    // -------------------- Action descriptor --------------------

    /**
     * Describes the action being audited. Construct via static factories like
     * {@link #deployFromHand} or {@link #cardActionPullFromReserve}. Evaluators
     * tell ActionAudit "this action is a deploy of card X to location Y" — the
     * audit doesn't have to inspect ActionTextEvaluator-specific text patterns.
     */
    public static final class ActionDescriptor {
        public enum Kind {
            DEPLOY_FROM_HAND,
            DEPLOY_FROM_RESERVE_VIA_CARD_TEXT,
            DEPLOY_FROM_LOST_VIA_CARD_TEXT,
            ATTACH_WEAPON,
            MOVE_VIA_CARD_TEXT,
            MOVE_VIA_LANDSPEED,
            RETURN_TO_HAND,
            FORCE_LOSS,
            FORFEIT,
            OTHER
        }

        public final Kind kind;
        /** Card being deployed/moved/etc. (target of the action) */
        public final PhysicalCard subject;
        /** Source card whose game text grants the action (e.g. Vader's Castle for a pull action) */
        public final PhysicalCard sourceCard;
        /** Destination location for deploys/moves */
        public final PhysicalCard destination;

        private ActionDescriptor(Kind kind, PhysicalCard subject, PhysicalCard sourceCard,
                                 PhysicalCard destination) {
            this.kind = kind;
            this.subject = subject;
            this.sourceCard = sourceCard;
            this.destination = destination;
        }

        public static ActionDescriptor deployFromHand(PhysicalCard subject, PhysicalCard destination) {
            return new ActionDescriptor(Kind.DEPLOY_FROM_HAND, subject, null, destination);
        }

        public static ActionDescriptor cardActionPullFromReserve(PhysicalCard sourceCard) {
            return new ActionDescriptor(Kind.DEPLOY_FROM_RESERVE_VIA_CARD_TEXT, null, sourceCard, null);
        }

        public static ActionDescriptor attachWeapon(PhysicalCard weapon, PhysicalCard target) {
            return new ActionDescriptor(Kind.ATTACH_WEAPON, weapon, null, target);
        }

        public static ActionDescriptor moveViaCardText(PhysicalCard mover, PhysicalCard sourceCard,
                                                      PhysicalCard destination) {
            return new ActionDescriptor(Kind.MOVE_VIA_CARD_TEXT, mover, sourceCard, destination);
        }

        public static ActionDescriptor returnToHand(PhysicalCard subject) {
            return new ActionDescriptor(Kind.RETURN_TO_HAND, subject, null, null);
        }
    }

    // -------------------- Public API --------------------

    public static AuditReport audit(DecisionContext context, ActionDescriptor desc) {
        AuditReport report = new AuditReport();
        if (context == null || desc == null) return report;
        GameState gs = context.getGameState();
        SwccgGame game = context.getGame();
        if (gs == null || game == null) return report;

        switch (desc.kind) {
            case DEPLOY_FROM_HAND:
                checkAffordability(context, desc, report);
                checkObjectiveCritical(context, desc, report);
                checkWastefulDeploy(context, desc, report);
                break;
            case DEPLOY_FROM_RESERVE_VIA_CARD_TEXT:
            case DEPLOY_FROM_LOST_VIA_CARD_TEXT:
                checkAffordabilityForCardActionPull(context, desc, report);
                checkZonePresence(context, desc, report);
                break;
            case ATTACH_WEAPON:
                checkAttachTargetValid(context, desc, report);
                break;
            case MOVE_VIA_CARD_TEXT:
            case MOVE_VIA_LANDSPEED:
                checkDrainDelta(context, desc, report);
                break;
            case RETURN_TO_HAND:
                checkReturnToHandWaste(context, desc, report);
                break;
            default:
                break;
        }
        return report;
    }

    // -------------------- Individual checks --------------------

    private static void checkAffordability(DecisionContext ctx, ActionDescriptor desc, AuditReport report) {
        if (desc.subject == null || desc.subject.getBlueprint() == null) return;
        try {
            Float cost = desc.subject.getBlueprint().getDeployCost();
            if (cost == null) return;
            int avail = ctx.getGameState().getForcePileSize(ctx.getPlayerId());
            if (avail < cost.intValue()) {
                report.add(new AuditFinding(AuditCategory.AFFORDABILITY, true, -9999f,
                    String.format("AUDIT/AFFORDABILITY: deploy of '%s' costs %d, only %d Force available",
                        desc.subject.getTitle(), cost.intValue(), avail)));
            }
        } catch (Exception e) {
            LOG.debug("AUDIT/AFFORDABILITY: error: {}", e.getMessage());
        }
    }

    /**
     * For card-action pulls (Vader's Castle pulls Vader, Evil Is Everywhere pulls a lightsaber, etc.):
     * scan the player's reserve deck for cards matching the source card's parsed targets.
     * Find the cheapest match's deploy cost. If even the cheapest exceeds available force,
     * the action will fail at execution and reveal Rando's reserve deck. Hard-block.
     * (Consolidates V67ac.)
     */
    private static void checkAffordabilityForCardActionPull(DecisionContext ctx, ActionDescriptor desc,
                                                            AuditReport report) {
        if (desc.sourceCard == null || desc.sourceCard.getBlueprint() == null) return;
        String gt = desc.sourceCard.getBlueprint().getGameText();
        if (gt == null) return;

        List<String> targets = DeckOracle.parseSourceCardPullTargets(gt);
        if (targets.isEmpty()) return;

        GameState gs = ctx.getGameState();
        String me = ctx.getPlayerId();
        int avail;
        try { avail = gs.getForcePileSize(me); } catch (Exception e) { return; }

        Integer cheapestCost = null;
        try {
            List<PhysicalCard> reserve = gs.getReserveDeck(me);
            if (reserve == null) return;
            for (PhysicalCard rc : reserve) {
                if (rc == null || rc.getBlueprint() == null) continue;
                String rcLower = rc.getTitle() == null ? "" : rc.getTitle().toLowerCase(Locale.ROOT);
                boolean matches = false;
                for (String t : targets) {
                    String tl = t.toLowerCase(Locale.ROOT);
                    String stripped = tl.replaceAll("\\[[^\\]]*\\]", " ").replaceAll("\\s+", " ").trim();
                    if (rcLower.contains(tl) || (!stripped.isEmpty() && rcLower.contains(stripped))) {
                        matches = true; break;
                    }
                }
                if (!matches) continue;
                try {
                    Float dc = rc.getBlueprint().getDeployCost();
                    if (dc == null) continue;
                    int icost = dc.intValue();
                    if (gt.toLowerCase(Locale.ROOT).contains("less force")
                            || gt.toLowerCase(Locale.ROOT).contains("deploy -1")) {
                        icost = Math.max(0, icost - 1);
                    }
                    if (cheapestCost == null || icost < cheapestCost) cheapestCost = icost;
                } catch (Exception e) { /* card doesn't support deploy cost */ }
            }
        } catch (Exception e) {
            LOG.debug("AUDIT/AFFORDABILITY/cardAction: error: {}", e.getMessage());
            return;
        }

        if (cheapestCost != null && cheapestCost > avail) {
            report.add(new AuditFinding(AuditCategory.AFFORDABILITY, true, -9999f,
                String.format("AUDIT/AFFORDABILITY: '%s' would deploy a %d-cost target with only %d Force — search would fail and reveal reserve",
                    desc.sourceCard.getTitle(), cheapestCost, avail)));
        }
    }

    /**
     * For card-action pulls: confirm at least one target actually exists in the source zone.
     * If none exists, the search will fail and reveal the deck. (Consolidates V67h.)
     */
    private static void checkZonePresence(DecisionContext ctx, ActionDescriptor desc, AuditReport report) {
        if (desc.sourceCard == null || desc.sourceCard.getBlueprint() == null) return;
        String gt = desc.sourceCard.getBlueprint().getGameText();
        if (gt == null) return;

        Zone srcZone = (desc.kind == ActionDescriptor.Kind.DEPLOY_FROM_LOST_VIA_CARD_TEXT)
            ? Zone.LOST_PILE : Zone.RESERVE_DECK;

        try {
            DeckOracle pullOracle = ctx.getDeckOracle();
            if (pullOracle == null) return;
            DeckOracle.PullValidation v = pullOracle.validatePullFromSourceCard(srcZone, gt);
            if (v.outcome == DeckOracle.PullOutcome.WILL_FAIL) {
                report.add(new AuditFinding(AuditCategory.ZONE_PRESENCE, true, -9999f,
                    "AUDIT/ZONE_PRESENCE: " + v.reason));
            }
        } catch (Exception e) {
            LOG.debug("AUDIT/ZONE_PRESENCE: error: {}", e.getMessage());
        }
    }

    /**
     * For weapon attach actions: confirm at least one valid target exists that doesn't
     * already have a weapon. (Consolidates V67ad.)
     */
    private static void checkAttachTargetValid(DecisionContext ctx, ActionDescriptor desc, AuditReport report) {
        GameState gs = ctx.getGameState();
        String me = ctx.getPlayerId();
        if (gs == null || me == null) return;

        boolean foundAnyValid = false;
        boolean allArmed = true;
        try {
            for (PhysicalCard c : gs.getAllPermanentCards()) {
                if (c == null || !me.equals(c.getOwner())) continue;
                if (c.getZone() == null || !c.getZone().isInPlay()) continue;
                if (c.getBlueprint() == null) continue;
                if (c.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                foundAnyValid = true;
                List<PhysicalCard> atts = gs.getAttachedCards(c);
                boolean armed = false;
                if (atts != null) {
                    for (PhysicalCard a : atts) {
                        if (a != null && a.getBlueprint() != null
                                && a.getBlueprint().getCardCategory() == CardCategory.WEAPON) {
                            armed = true; break;
                        }
                    }
                }
                if (!armed) { allArmed = false; break; }
            }
        } catch (Exception e) {
            LOG.debug("AUDIT/TARGET_VALID: error: {}", e.getMessage());
            return;
        }

        if (foundAnyValid && allArmed) {
            report.add(new AuditFinding(AuditCategory.TARGET_VALID, true, -9999f,
                "AUDIT/TARGET_VALID: every Rando character on table is already armed — orphan weapon"));
        }
    }

    /**
     * For move actions: penalize moves that lose drain pressure (current location has more
     * opponent icons than destination). (Consolidates V67g + V67ae.)
     */
    private static void checkDrainDelta(DecisionContext ctx, ActionDescriptor desc, AuditReport report) {
        if (desc.subject == null || desc.destination == null) return;
        PhysicalCard fromLoc = desc.subject.getAtLocation();
        if (fromLoc == null || fromLoc == desc.destination) return;

        Side mySide = ctx.getSide();
        Icon oppIcon = (mySide == Side.LIGHT) ? Icon.DARK_FORCE : Icon.LIGHT_FORCE;
        try {
            int fromOpp = fromLoc.getBlueprint() != null
                ? fromLoc.getBlueprint().getIconCount(oppIcon) : 0;
            int destOpp = desc.destination.getBlueprint() != null
                ? desc.destination.getBlueprint().getIconCount(oppIcon) : 0;
            if (fromOpp > destOpp) {
                int delta = fromOpp - destOpp;
                float penalty = -150f * delta;
                report.add(new AuditFinding(AuditCategory.DRAIN_DELTA, false, penalty,
                    String.format("AUDIT/DRAIN_DELTA: leaving %s (drain %d) for %s (drain %d) — losing %d drain pressure",
                        fromLoc.getTitle(), fromOpp, desc.destination.getTitle(), destOpp, delta)));
            }
        } catch (Exception e) {
            LOG.debug("AUDIT/DRAIN_DELTA: error: {}", e.getMessage());
        }
    }

    private static void checkObjectiveCritical(DecisionContext ctx, ActionDescriptor desc, AuditReport report) {
        // Stub for V68 phase 2: ObjectiveAnalyzer integration.
        // Will check whether this action would put a flip-required card at risk.
    }

    private static void checkWastefulDeploy(DecisionContext ctx, ActionDescriptor desc, AuditReport report) {
        if (desc.subject == null || desc.destination == null) return;
        if (desc.subject.getBlueprint() == null) return;
        if (desc.subject.getBlueprint().getCardCategory() != CardCategory.CHARACTER) return;
        SwccgGame game = ctx.getGame();
        GameState gs = ctx.getGameState();
        String me = ctx.getPlayerId();
        if (game == null || gs == null) return;

        try {
            boolean isBg = game.getModifiersQuerying().isBattleground(gs, desc.destination, null);
            if (isBg) return;

            // Has any opponent icons we could drain?
            Side mySide = ctx.getSide();
            Icon oppIcon = (mySide == Side.LIGHT) ? Icon.DARK_FORCE : Icon.LIGHT_FORCE;
            int oppIcons = desc.destination.getBlueprint() != null
                ? desc.destination.getBlueprint().getIconCount(oppIcon) : 0;

            // Already have a friendly character there?
            boolean hasFriendlyChar = false;
            String existingTitle = null;
            for (PhysicalCard c : gs.getCardsAtLocation(desc.destination)) {
                if (c == null || !me.equals(c.getOwner())) continue;
                if (c.getBlueprint() == null) continue;
                if (c.getBlueprint().getCardCategory() != CardCategory.CHARACTER) continue;
                hasFriendlyChar = true;
                existingTitle = c.getTitle();
                break;
            }

            if (hasFriendlyChar) {
                report.add(new AuditFinding(AuditCategory.WASTEFUL_DEPLOY, false, -300f,
                    String.format("AUDIT/WASTEFUL_DEPLOY: %s already has friendly %s and is non-BG — extra characters can't battle here",
                        desc.destination.getTitle(), existingTitle)));
            } else if (oppIcons == 0) {
                report.add(new AuditFinding(AuditCategory.WASTEFUL_DEPLOY, false, -350f,
                    String.format("AUDIT/WASTEFUL_DEPLOY: %s is non-BG with zero opp icons — no battles, no drain",
                        desc.destination.getTitle())));
            } else {
                report.add(new AuditFinding(AuditCategory.WASTEFUL_DEPLOY, false, -100f,
                    String.format("AUDIT/WASTEFUL_DEPLOY: %s is non-BG (drain staging only)",
                        desc.destination.getTitle())));
            }
        } catch (Exception e) {
            LOG.debug("AUDIT/WASTEFUL_DEPLOY: error: {}", e.getMessage());
        }
    }

    private static void checkReturnToHandWaste(DecisionContext ctx, ActionDescriptor desc, AuditReport report) {
        if (desc.subject == null || desc.subject.getBlueprint() == null) return;
        if (desc.subject.getBlueprint().getCardCategory() != CardCategory.CHARACTER) return;
        if (!ctx.getPlayerId().equals(desc.subject.getOwner())) return;
        report.add(new AuditFinding(AuditCategory.WASTEFUL_DEPLOY, true, -9999f,
            String.format("AUDIT/RETURN_TO_HAND: bouncing %s wastes the deploy cost", desc.subject.getTitle())));
    }

    private ActionAudit() {}
}
