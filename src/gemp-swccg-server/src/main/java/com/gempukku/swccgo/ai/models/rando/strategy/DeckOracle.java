package com.gempukku.swccgo.ai.models.rando.strategy;

import com.gempukku.swccgo.ai.models.rando.RandoLogger;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;

import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * V22.6 DeckOracle — Full deck knowledge for Rando.
 *
 * Catalogs every card in Rando's deck at game start and tracks where each card
 * is (hand, reserve, force pile, used pile, lost pile, in play) across every decision.
 *
 * This enables evaluators to make informed decisions:
 * - "Is Executor still in my reserve deck?" before using Alert My Star Destroyer
 * - "What's the average destiny remaining in reserve?" for battle planning
 * - "Stop trying to pull a card that's been attempted 2+ times and failed"
 *
 * The oracle re-scans zones from GameState every decision (cheap and guaranteed accurate).
 * It does NOT need to infer card movements — it reads the ground truth directly.
 *
 * Integration:
 *   - Initialized in RandoCalAi constructor
 *   - analyze() called on first decision of each game
 *   - refresh() called every decision in buildEvaluatorContext()
 *   - Injected into DecisionContext for evaluator access
 */
public class DeckOracle {
    private static final Logger LOG = RandoLogger.getStrategyLogger();

    // =========================================================================
    // Inner Class: DeckCard
    // =========================================================================

    /**
     * Represents a single card instance in Rando's deck.
     * Tracks its identity, current zone, and key properties for strategic queries.
     */
    public static class DeckCard {
        private final String blueprintId;
        private final String title;
        private final float destiny;
        private final float deployCost;
        private final float power;
        private final float forfeit;
        private final float ability;
        private final CardCategory category;
        private final CardSubtype subtype;
        private final String gameText;
        // V82.2 (Steve, 2026-05-16): keep the blueprint so we can run
        // Icon/Keyword/Species checks without needing GameState (Filters
        // typically need a live GameState which DeckOracle doesn't have).
        private final SwccgCardBlueprint blueprint;
        private Zone currentZone;

        public DeckCard(PhysicalCard card, Zone zone) {
            this.blueprintId = card.getBlueprintId(true);
            this.title = card.getTitle() != null ? card.getTitle() : "Unknown";
            this.currentZone = zone;

            SwccgCardBlueprint bp = card.getBlueprint();
            this.blueprint = bp;
            float d = 0, cost = 0, pow = 0, forf = 0, abil = 0;
            CardCategory cat = null;
            CardSubtype sub = null;
            String gt = null;

            if (bp != null) {
                try { d = bp.getDestiny() != null ? bp.getDestiny() : 0; } catch (Exception e) { /* no destiny */ }
                try { cost = bp.getDeployCost() != null ? bp.getDeployCost() : 0; } catch (Exception e) { /* no cost */ }
                if (bp.hasPowerAttribute()) {
                    try { pow = bp.getPower() != null ? bp.getPower() : 0; } catch (Exception e) { /* no power */ }
                }
                try { forf = bp.getForfeit() != null ? bp.getForfeit() : 0; } catch (Exception e) { /* no forfeit */ }
                if (bp.hasAbilityAttribute()) {
                    try { abil = bp.getAbility() != null ? bp.getAbility() : 0; } catch (Exception e) { /* no ability */ }
                }
                cat = bp.getCardCategory();
                try { sub = bp.getCardSubtype(); } catch (Exception e) { /* no subtype */ }
                gt = bp.getGameText();
            }

            this.destiny = d;
            this.deployCost = cost;
            this.power = pow;
            this.forfeit = forf;
            this.ability = abil;
            this.category = cat;
            this.subtype = sub;
            this.gameText = gt;
        }

        // Getters
        public String getBlueprintId() { return blueprintId; }
        public String getTitle() { return title; }
        public Zone getCurrentZone() { return currentZone; }
        public float getDestiny() { return destiny; }
        public float getDeployCost() { return deployCost; }
        public float getPower() { return power; }
        public float getForfeit() { return forfeit; }
        public float getAbility() { return ability; }
        public CardCategory getCategory() { return category; }
        public CardSubtype getSubtype() { return subtype; }
        public String getGameText() { return gameText; }
        public SwccgCardBlueprint getBlueprint() { return blueprint; }

        // Zone update (called during refresh)
        void setCurrentZone(Zone zone) { this.currentZone = zone; }

        @Override
        public String toString() {
            return String.format("%s [%s] zone=%s dest=%.0f cost=%.0f",
                title, blueprintId, currentZone, destiny, deployCost);
        }
    }

    // =========================================================================
    // Fields
    // =========================================================================

    /** All cards indexed by blueprintId. Multiple copies share the same key. */
    private final Map<String, List<DeckCard>> catalogByBlueprint = new LinkedHashMap<>();

    /** All cards indexed by lowercase title for fuzzy lookup. */
    private final Map<String, List<DeckCard>> catalogByTitle = new LinkedHashMap<>();

    /** Master list of all DeckCard instances (flat). */
    private final List<DeckCard> allCards = new ArrayList<>();

    /** Failed pull tracking: blueprintId → consecutive failure count. */
    private final Map<String, Integer> failedPulls = new HashMap<>();

    /** V24.10: AMSD failed attempt tracker — turn number of last failed AMSD attempt.
     *  If AMSD fails on a turn, don't retry until the next turn (recirculation may fix it). */
    private int amsdFailedOnTurn = -1;

    /** Total cards in deck at game start (for reference). */
    private int totalDeckSize = 0;

    /** Whether analyze() has been called this game. */
    private boolean analyzed = false;

    // =========================================================================
    // Lifecycle
    // =========================================================================

    /**
     * Catalog the entire deck at game start.
     * Enumerates all zones and builds the card database.
     * Called once per game on the first decision.
     */
    public void analyze(SwccgGame game, String playerId, Side side) {
        if (game == null || playerId == null) return;
        GameState gs = game.getGameState();
        if (gs == null) return;

        LOG.warn("📚 [DeckOracle] analyze() — cataloging deck for {} ({})", playerId, side);

        // Clear any stale data
        catalogByBlueprint.clear();
        catalogByTitle.clear();
        allCards.clear();
        failedPulls.clear();
        totalDeckSize = 0;

        // Catalog each zone
        catalogZone(gs.getHand(playerId), Zone.HAND);
        catalogZone(gs.getReserveDeck(playerId), Zone.RESERVE_DECK);
        catalogZone(gs.getUsedPile(playerId), Zone.USED_PILE);
        catalogZone(gs.getLostPile(playerId), Zone.LOST_PILE);

        // V29.4: Force pile — now scanned directly via getForcePile()
        try {
            catalogZone(gs.getForcePile(playerId), Zone.FORCE_PILE);
        } catch (Exception e) {
            LOG.warn("📚 [DeckOracle] V29.4: Force pile catalog failed: {}", e.getMessage());
        }

        // Also catalog in-play cards and side-of-table (shields)
        for (PhysicalCard card : gs.getAllPermanentCards()) {
            if (card == null) continue;
            if (!playerId.equals(card.getOwner())) continue;
            Zone zone = card.getZone();
            if (zone == null) continue;

            // Only catalog zones we haven't already handled above
            if (zone == Zone.HAND || zone == Zone.RESERVE_DECK ||
                zone == Zone.USED_PILE || zone == Zone.LOST_PILE) {
                continue;  // Already cataloged
            }

            catalogCard(card, zone);
        }

        totalDeckSize = allCards.size();
        analyzed = true;

        logCatalogSummary();
    }

    /**
     * Refresh card zone positions from current game state.
     * Called every decision — re-scans all zones to update each DeckCard's currentZone.
     * This is cheap (single pass through zone lists) and guarantees accuracy.
     */
    public void refresh(GameState gameState, String playerId) {
        if (!analyzed || gameState == null || playerId == null) return;

        // Build a lookup: cardTitle+blueprintId → set of zones it's currently in
        // We need to match DeckCards to their current physical positions
        // Strategy: clear all zones, then re-assign based on current GameState

        // Track which DeckCards we've matched (by index in allCards)
        boolean[] matched = new boolean[allCards.size()];

        // Reset all to null (unmatched)
        for (DeckCard dc : allCards) {
            dc.setCurrentZone(null);
        }

        // Scan each zone and match physical cards to DeckCards
        matchCardsFromZone(gameState.getHand(playerId), Zone.HAND, matched);
        matchCardsFromZone(gameState.getReserveDeck(playerId), Zone.RESERVE_DECK, matched);
        matchCardsFromZone(gameState.getUsedPile(playerId), Zone.USED_PILE, matched);
        matchCardsFromZone(gameState.getLostPile(playerId), Zone.LOST_PILE, matched);
        // V29.4: Scan force pile — critical for tracking cards activated from reserve
        // Without this, cards in the force pile had currentZone=null which caused
        // AMSD and other oracle queries to lose track of Executor, Piett, etc.
        try {
            matchCardsFromZone(gameState.getForcePile(playerId), Zone.FORCE_PILE, matched);
        } catch (Exception e) {
            LOG.debug("📚 [DeckOracle] V29.4: Force pile scan failed: {}", e.getMessage());
        }

        // In-play and other zones via getAllPermanentCards
        List<PhysicalCard> inPlayCards = new ArrayList<>();
        for (PhysicalCard card : gameState.getAllPermanentCards()) {
            if (card == null) continue;
            if (!playerId.equals(card.getOwner())) continue;
            Zone zone = card.getZone();
            if (zone == null) continue;
            if (zone == Zone.HAND || zone == Zone.RESERVE_DECK ||
                zone == Zone.USED_PILE || zone == Zone.LOST_PILE) {
                continue;
            }
            inPlayCards.add(card);
        }
        matchCardsFromZone(inPlayCards, null, matched);  // null = use card's own zone

        // Any unmatched DeckCards may be new cards (pulled from outside deck)
        // or cards that moved to a zone we don't track. Mark them as unknown.
        for (int i = 0; i < allCards.size(); i++) {
            if (!matched[i] && allCards.get(i).getCurrentZone() == null) {
                // Card not found in any zone — might be stacked, out of play, etc.
                // Leave as null (callers should handle gracefully)
            }
        }
    }

    /**
     * Reset for a new game. Clears all catalog data.
     */
    public void reset() {
        catalogByBlueprint.clear();
        catalogByTitle.clear();
        allCards.clear();
        failedPulls.clear();
        amsdFailedOnTurn = -1;
        totalDeckSize = 0;
        analyzed = false;
        LOG.debug("📚 [DeckOracle] Reset for new game");
    }

    public boolean isAnalyzed() {
        return analyzed;
    }

    // =========================================================================
    // Zone Query Methods
    // =========================================================================

    /**
     * Check if any copy of a card (by blueprintId or title) is in the specified zone.
     */
    public boolean isCardInZone(String blueprintIdOrTitle, Zone zone) {
        if (blueprintIdOrTitle == null || zone == null) return false;

        // Try blueprintId first (exact match)
        List<DeckCard> byBp = catalogByBlueprint.get(blueprintIdOrTitle);
        if (byBp != null) {
            for (DeckCard dc : byBp) {
                if (zone.equals(dc.getCurrentZone())) return true;
            }
        }

        // Try title match (case-insensitive)
        String titleKey = blueprintIdOrTitle.toLowerCase(Locale.ROOT);
        List<DeckCard> byTitle = catalogByTitle.get(titleKey);
        if (byTitle != null) {
            for (DeckCard dc : byTitle) {
                if (zone.equals(dc.getCurrentZone())) return true;
            }
        }

        // Try partial title match (contains)
        for (Map.Entry<String, List<DeckCard>> entry : catalogByTitle.entrySet()) {
            if (entry.getKey().contains(titleKey) || titleKey.contains(entry.getKey())) {
                for (DeckCard dc : entry.getValue()) {
                    if (zone.equals(dc.getCurrentZone())) return true;
                }
            }
        }

        return false;
    }

    /** Is any copy of this card in the reserve deck? */
    public boolean isCardInReserve(String blueprintIdOrTitle) {
        return isCardInZone(blueprintIdOrTitle, Zone.RESERVE_DECK);
    }

    /** Is any copy of this card in hand? */
    public boolean isCardInHand(String blueprintIdOrTitle) {
        return isCardInZone(blueprintIdOrTitle, Zone.HAND);
    }

    /** Is any copy of this card deployed on the table? */
    public boolean isCardInPlay(String blueprintIdOrTitle) {
        if (blueprintIdOrTitle == null) return false;

        // "In play" means any zone where isInPlay() returns true
        List<DeckCard> cards = findCards(blueprintIdOrTitle);
        for (DeckCard dc : cards) {
            Zone z = dc.getCurrentZone();
            if (z != null && z.isInPlay()) return true;
        }
        return false;
    }

    /** Is any copy of this card in the lost pile (gone for good)? */
    public boolean isCardLost(String blueprintIdOrTitle) {
        return isCardInZone(blueprintIdOrTitle, Zone.LOST_PILE);
    }

    /** Get all DeckCards currently in a specific zone. */
    public List<DeckCard> getCardsInZone(Zone zone) {
        if (zone == null) return Collections.emptyList();
        return allCards.stream()
            .filter(dc -> zone.equals(dc.getCurrentZone()))
            .collect(Collectors.toList());
    }

    /** Count copies of a specific card (by blueprintId) in a zone. */
    public int countCopiesInZone(String blueprintId, Zone zone) {
        if (blueprintId == null || zone == null) return 0;
        List<DeckCard> cards = catalogByBlueprint.get(blueprintId);
        if (cards == null) return 0;
        int count = 0;
        for (DeckCard dc : cards) {
            if (zone.equals(dc.getCurrentZone())) count++;
        }
        return count;
    }

    /** Count total copies of a card across all zones. */
    public int countCopiesTotal(String blueprintId) {
        if (blueprintId == null) return 0;
        List<DeckCard> cards = catalogByBlueprint.get(blueprintId);
        return cards != null ? cards.size() : 0;
    }

    /** Total number of cards in the deck at game start. */
    public int getTotalDeckSize() {
        return totalDeckSize;
    }

    // =========================================================================
    // Analysis Methods
    // =========================================================================

    /** Get all cards of a specific category in a zone (e.g., all characters in reserve). */
    public List<DeckCard> getCardsByCategory(CardCategory category, Zone zone) {
        if (category == null || zone == null) return Collections.emptyList();
        return allCards.stream()
            .filter(dc -> category.equals(dc.getCategory()) && zone.equals(dc.getCurrentZone()))
            .collect(Collectors.toList());
    }

    /** Get cards with destiny >= threshold in a zone. */
    public List<DeckCard> getHighDestinyCards(Zone zone, float threshold) {
        if (zone == null) return Collections.emptyList();
        return allCards.stream()
            .filter(dc -> zone.equals(dc.getCurrentZone()) && dc.getDestiny() >= threshold)
            .sorted((a, b) -> Float.compare(b.getDestiny(), a.getDestiny()))
            .collect(Collectors.toList());
    }

    /** Average destiny of cards remaining in reserve deck. */
    public double getAverageDestinyInReserve() {
        List<DeckCard> reserveCards = getCardsInZone(Zone.RESERVE_DECK);
        if (reserveCards.isEmpty()) return 0.0;
        double sum = 0;
        for (DeckCard dc : reserveCards) {
            sum += dc.getDestiny();
        }
        return sum / reserveCards.size();
    }

    /** Can we afford to deploy a card given current force? */
    public boolean canAffordToDeploy(String blueprintId, int currentForce) {
        if (blueprintId == null) return false;
        List<DeckCard> cards = catalogByBlueprint.get(blueprintId);
        if (cards == null || cards.isEmpty()) return false;
        return cards.get(0).getDeployCost() <= currentForce;
    }

    /** Get a DeckCard's deploy cost (returns -1 if not found). */
    public float getDeployCost(String blueprintId) {
        if (blueprintId == null) return -1;
        List<DeckCard> cards = catalogByBlueprint.get(blueprintId);
        if (cards == null || cards.isEmpty()) return -1;
        return cards.get(0).getDeployCost();
    }

    /** Get a DeckCard's destiny value (returns 0 if not found). */
    public float getDestinyValue(String blueprintId) {
        if (blueprintId == null) return 0;
        List<DeckCard> cards = catalogByBlueprint.get(blueprintId);
        if (cards == null || cards.isEmpty()) return 0;
        return cards.get(0).getDestiny();
    }

    // =========================================================================
    // V29.7: Reserve Deck Target Validation
    // =========================================================================

    /**
     * V67bg (Steve, 2026-05-10): Resolve a SWCCG game-text noun to the typed Filter
     * the engine itself uses for that category. Returns null when the noun is NOT
     * a known game-vocabulary term (likely a proper-noun card title — let the
     * named-target check handle it).
     *
     * Standing rule (memory: feedback_card_search_by_type_not_text.md):
     *   When card text says "site" / "weapon" / "battleground" / "Sith" / etc.,
     *   that's the SWCCG vocabulary mapping to a CardCategory / CardSubtype /
     *   Icon / Keyword / Filters constant. NEVER substring-match against card
     *   titles to look for a category — no card is literally titled "location".
     *
     * Append a row here when a new noun shows up that needs mapping.
     */
    public static com.gempukku.swccgo.filters.Filter resolveCommonNounToFilter(String noun) {
        if (noun == null) return null;
        switch (noun.toLowerCase(Locale.ROOT).trim()) {
            // --- card categories ---
            case "location":      return com.gempukku.swccgo.filters.Filters.location;
            case "character":     return com.gempukku.swccgo.filters.Filters.character;
            case "weapon":        return com.gempukku.swccgo.filters.Filters.weapon;
            case "device":        return com.gempukku.swccgo.filters.Filters.device;
            case "starship":      return com.gempukku.swccgo.filters.Filters.starship;
            case "vehicle":       return com.gempukku.swccgo.filters.Filters.vehicle;
            case "creature":      return com.gempukku.swccgo.filters.Filters.creature;
            case "interrupt":     return com.gempukku.swccgo.filters.Filters.Interrupt;
            case "effect":        return com.gempukku.swccgo.filters.Filters.Effect_of_any_Kind;
            case "shield":        return com.gempukku.swccgo.filters.Filters.Defensive_Shield;
            // --- location subtypes / classes ---
            case "site":          return com.gempukku.swccgo.filters.Filters.site;
            case "system":        return com.gempukku.swccgo.filters.Filters.system;
            case "sector":        return com.gempukku.swccgo.filters.Filters.sector;
            case "battleground":
            case "battleground_site":
                                  return com.gempukku.swccgo.filters.Filters.battleground_site;
            // --- weapon classes ---
            case "lightsaber":    return com.gempukku.swccgo.filters.Filters.lightsaber;
            case "blaster":       return com.gempukku.swccgo.filters.Filters.blaster;
            // --- character classes / icons ---
            case "jedi":          return com.gempukku.swccgo.filters.Filters.Jedi;
            case "sith":          return com.gempukku.swccgo.filters.Filters.Sith;
            case "dark_jedi":
            case "dark jedi":     return com.gempukku.swccgo.filters.Filters.Dark_Jedi;
            case "imperial":      return com.gempukku.swccgo.filters.Filters.Imperial;
            case "rebel":         return com.gempukku.swccgo.filters.Filters.Rebel;
            case "alien":         return com.gempukku.swccgo.filters.Filters.alien;
            case "droid":         return com.gempukku.swccgo.filters.Filters.droid;
            case "warrior":       return com.gempukku.swccgo.filters.Filters.warrior;
            case "spy":           return com.gempukku.swccgo.filters.Filters.spy;
            case "leader":        return com.gempukku.swccgo.filters.Filters.leader;
            case "trooper":       return com.gempukku.swccgo.filters.Filters.trooper;
            case "admiral":       return com.gempukku.swccgo.filters.Filters.admiral;
            case "general":       return com.gempukku.swccgo.filters.Filters.general;
            case "inquisitor":    return com.gempukku.swccgo.filters.Filters.inquisitor;
            case "padawan":       return com.gempukku.swccgo.filters.Filters.padawan;
            case "bounty hunter":
            case "bounty_hunter": return com.gempukku.swccgo.filters.Filters.bounty_hunter;
            // (Append more nouns here as they surface in card text.)
            default:
                return null;
        }
    }

    /**
     * V67bg: Type-aware reserve check. Returns true if at least one card in
     * Reserve Deck satisfies the given Filter, using the engine's own filter
     * semantics (the same way the card's deploy effect would search). This is
     * the correct way to ask "can this pull find a target?" — instead of
     * substring-matching a generic noun against card titles.
     *
     * Caller should obtain `filter` via {@link #resolveCommonNounToFilter(String)}
     * (translate noun → typed filter) when validating a generic pull action.
     */
    public boolean hasFilterMatchInReserve(com.gempukku.swccgo.game.SwccgGame game,
                                            String playerId,
                                            com.gempukku.swccgo.filters.Filter filter) {
        if (game == null || filter == null || playerId == null) return false;
        try {
            com.gempukku.swccgo.game.state.GameState gs = game.getGameState();
            if (gs == null) return false;
            for (com.gempukku.swccgo.game.PhysicalCard pc : gs.getReserveDeck(playerId)) {
                if (pc == null) continue;
                try {
                    if (filter.accepts(gs, game.getModifiersQuerying(), pc)) {
                        return true;
                    }
                } catch (Exception ignored) { /* continue scanning */ }
            }
        } catch (Exception ignored) { /* fall through */ }
        return false;
    }

    /**
     * V29.7: Check if any card in Reserve Deck has a title containing ANY of the given keywords.
     * Used to validate pulls before scoring them — don't waste Force pulling from Reserve
     * when no valid targets remain there.
     *
     * Example: hasTargetInReserve("admiral") checks if any card with "Admiral" in its title
     * is currently in the Reserve Deck zone.
     *
     * @param keywords One or more keywords to match against card titles (case-insensitive)
     * @return true if at least one card in Reserve matches any keyword
     */
    public boolean hasTargetInReserve(String... keywords) {
        if (keywords == null || keywords.length == 0) return false;
        for (DeckCard dc : allCards) {
            if (!Zone.RESERVE_DECK.equals(dc.getCurrentZone())) continue;
            String titleLower = dc.getTitle().toLowerCase(Locale.ROOT);
            for (String kw : keywords) {
                if (kw != null && titleLower.contains(kw.toLowerCase(Locale.ROOT))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * V29.7: Count how many cards in Reserve Deck match any of the given title keywords.
     */
    public int countTargetsInReserve(String... keywords) {
        if (keywords == null || keywords.length == 0) return 0;
        int count = 0;
        for (DeckCard dc : allCards) {
            if (!Zone.RESERVE_DECK.equals(dc.getCurrentZone())) continue;
            String titleLower = dc.getTitle().toLowerCase(Locale.ROOT);
            for (String kw : keywords) {
                if (kw != null && titleLower.contains(kw.toLowerCase(Locale.ROOT))) {
                    count++;
                    break;  // Don't double-count same card for multiple keywords
                }
            }
        }
        return count;
    }

    /**
     * V29.7: Check if Reserve Deck has any characters (useful for general "pull character" checks).
     */
    public boolean hasCharactersInReserve() {
        for (DeckCard dc : allCards) {
            if (Zone.RESERVE_DECK.equals(dc.getCurrentZone())
                && CardCategory.CHARACTER.equals(dc.getCategory())) {
                return true;
            }
        }
        return false;
    }

    // =========================================================================
    // V66 MEMORY AUDIT: Zone-aware target checks for ALL piles
    // =========================================================================
    // Steve's feedback: "Rando doesn't seem to remember what's in his hand,
    // force pile, reserve, used or lost pile."
    // These helpers let every evaluator verify a pull/search target exists in
    // the right zone BEFORE firing. Previously we only checked Reserve Deck;
    // now we check all 5 piles + in-play state.
    // =========================================================================

    /**
     * V66: Check if any card matching the keywords is in the given zone.
     * Generic version of hasTargetInReserve — parameterized by zone.
     */
    public boolean hasTargetInZone(Zone zone, String... keywords) {
        if (zone == null || keywords == null || keywords.length == 0) return false;
        for (DeckCard dc : allCards) {
            if (!zone.equals(dc.getCurrentZone())) continue;
            String titleLower = dc.getTitle().toLowerCase(Locale.ROOT);
            for (String kw : keywords) {
                if (kw == null) continue;
                String kwLower = kw.toLowerCase(Locale.ROOT);
                // 1. Literal match
                if (titleLower.contains(kwLower)) {
                    return true;
                }
                // 2. V67s: SWCCG card-text targets often include icon prefixes like
                // "[Episode I] lightsaber" — these icons are METADATA on the card,
                // not part of the title. Strip [...] and trim, then re-check.
                // "[episode i] lightsaber" → "lightsaber" → matches "Sidious' Lightsaber"
                String kwStripped = kwLower.replaceAll("\\[[^\\]]*\\]", " ").replaceAll("\\s+", " ").trim();
                if (!kwStripped.isEmpty() && !kwStripped.equals(kwLower)
                        && titleLower.contains(kwStripped)) {
                    return true;
                }
                // 3. V67s: Also try the last word of the stripped keyword (often the
                // card type: "lightsaber", "site", "starship", "weapon"). This catches
                // multi-word targets where only the type word matches a title.
                if (!kwStripped.isEmpty() && kwStripped.contains(" ")) {
                    String lastWord = kwStripped.substring(kwStripped.lastIndexOf(' ') + 1);
                    if (lastWord.length() >= 4 && titleLower.contains(lastWord)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * V66: Check if any matching card is in our HAND.
     * Used to block redundant pulls — "Deploy LMF(V) from Reserve" when LMF(V)
     * is already in hand will fail and reveal the reserve to opponent.
     */
    public boolean hasTargetInHand(String... keywords) {
        return hasTargetInZone(Zone.HAND, keywords);
    }

    /**
     * V66: Check if any matching card is in our LOST PILE.
     * Used to block Jedi Levitation / Retrieval-style actions when source is empty.
     */
    public boolean hasTargetInLostPile(String... keywords) {
        return hasTargetInZone(Zone.LOST_PILE, keywords);
    }

    /**
     * V66: Check if any matching card is in our USED PILE.
     */
    public boolean hasTargetInUsedPile(String... keywords) {
        return hasTargetInZone(Zone.USED_PILE, keywords);
    }

    /**
     * V66: Check if any matching card is in our FORCE PILE.
     */
    public boolean hasTargetInForcePile(String... keywords) {
        return hasTargetInZone(Zone.FORCE_PILE, keywords);
    }

    /**
     * V66: Check if any matching card is currently IN PLAY (deployed on table).
     * Used to block "pull a UNIQUE card" when the unique is already deployed.
     * Example: Sai'torr Kal Fas "[Download] matching weapon" when Obi-Wan's
     * Lightsaber is already attached to Obi-Wan → search will fail.
     */
    public boolean hasTargetInPlay(String... keywords) {
        if (keywords == null || keywords.length == 0) return false;
        for (DeckCard dc : allCards) {
            Zone z = dc.getCurrentZone();
            if (z == null) continue;
            // "In play" = any table zone: AT_LOCATION, ATTACHED, STACKED (wait — stacked
            // is typically not "deployed"). Use isInPlay() if available; otherwise
            // check for the main play zones.
            boolean inPlay = z.isInPlay();
            if (!inPlay) continue;
            String titleLower = dc.getTitle().toLowerCase(Locale.ROOT);
            for (String kw : keywords) {
                if (kw != null && titleLower.contains(kw.toLowerCase(Locale.ROOT))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * V66: Classify an action's source zone from its text.
     * Returns null if the action doesn't involve a zone pull.
     */
    public static Zone parseSourceZone(String actionText) {
        if (actionText == null) return null;
        String lower = actionText.toLowerCase(Locale.ROOT);
        if (lower.contains("from reserve deck") || lower.contains("[download]")) return Zone.RESERVE_DECK;
        if (lower.contains("from lost pile")) return Zone.LOST_PILE;
        if (lower.contains("from used pile")) return Zone.USED_PILE;
        if (lower.contains("from force pile")) return Zone.FORCE_PILE;
        if (lower.contains("from hand")) return Zone.HAND;
        return null;
    }

    /**
     * V66: Comprehensive pull-validity check.
     *
     * Given an action text that performs a zone pull, determine if the pull
     * is likely to succeed based on what we know about each pile.
     *
     * Returns a PullOutcome:
     *   - WILL_SUCCEED: at least one target exists in source zone AND target
     *     isn't already satisfied elsewhere (e.g., unique in play)
     *   - WILL_FAIL: no target in source zone — search will fail and reveal pile
     *   - WASTEFUL: target in source but ALSO already in hand/play — search
     *     wastes tempo even if it "succeeds"
     *   - UNKNOWN: can't parse target from action text — let existing weights handle
     */
    public enum PullOutcome { WILL_SUCCEED, WILL_FAIL, WASTEFUL, UNKNOWN }

    public static class PullValidation {
        public final PullOutcome outcome;
        public final String reason;
        public PullValidation(PullOutcome outcome, String reason) {
            this.outcome = outcome;
            this.reason = reason;
        }
    }

    /**
     * V66: Validate a pull action. sourceZone comes from parseSourceZone,
     * targetKeywords are the card names/categories the action is looking for.
     */
    public PullValidation validatePull(Zone sourceZone, String... targetKeywords) {
        if (sourceZone == null || targetKeywords == null || targetKeywords.length == 0) {
            return new PullValidation(PullOutcome.UNKNOWN, "Cannot parse zone or target");
        }
        // 1. Does source zone have the target?
        if (!hasTargetInZone(sourceZone, targetKeywords)) {
            return new PullValidation(PullOutcome.WILL_FAIL,
                "No match for '" + String.join("/", targetKeywords)
                    + "' in " + sourceZone + " — search will FAIL and reveal zone");
        }
        // 2. Is the target (if unique) already in play? Skip check for generic
        //    categories like "character"/"weapon" — those are multi-copy.
        boolean isGenericCategory = targetKeywords.length == 1
            && targetKeywords[0] != null
            && (targetKeywords[0].equalsIgnoreCase("character")
                || targetKeywords[0].equalsIgnoreCase("weapon")
                || targetKeywords[0].equalsIgnoreCase("starship")
                || targetKeywords[0].equalsIgnoreCase("location")
                || targetKeywords[0].equalsIgnoreCase("alien")
                || targetKeywords[0].equalsIgnoreCase("interrupt")
                || targetKeywords[0].equalsIgnoreCase("effect")
                || targetKeywords[0].equalsIgnoreCase("device")
                || targetKeywords[0].equalsIgnoreCase("vehicle")
                || targetKeywords[0].equalsIgnoreCase("holocron")
                || targetKeywords[0].equalsIgnoreCase("farm")
                || targetKeywords[0].equalsIgnoreCase("padawan")
                || targetKeywords[0].equalsIgnoreCase("jedi")
                || targetKeywords[0].equalsIgnoreCase("droid"));
        if (!isGenericCategory && sourceZone == Zone.RESERVE_DECK) {
            // For specifically-named pulls, check if already in play (duplicate)
            if (hasTargetInPlay(targetKeywords)) {
                return new PullValidation(PullOutcome.WASTEFUL,
                    "'" + String.join("/", targetKeywords)
                        + "' already in play — pull would be redundant");
            }
            // Or already in hand (no point pulling a second copy usually)
            if (hasTargetInHand(targetKeywords)) {
                return new PullValidation(PullOutcome.WASTEFUL,
                    "'" + String.join("/", targetKeywords)
                        + "' already in hand — don't pull a duplicate");
            }
        }
        return new PullValidation(PullOutcome.WILL_SUCCEED,
            "'" + String.join("/", targetKeywords) + "' is available in " + sourceZone);
    }

    // =========================================================================
    // V67h SOURCE-CARD GAME-TEXT PARSER
    //
    // Generic action texts like "Choose card to deploy from Reserve Deck" or
    // "[Download] a matching weapon" don't carry the target list. We parse the
    // source card's game text to extract what categories or names the action
    // pulls. Steve's expectation: "Rando is already aware of what's in his deck
    // at the start of game and would know when he would have a successful search."
    // This makes that true for any card whose game text describes its filter.
    // =========================================================================

    /**
     * Parse a source card's game text to extract the list of pull targets.
     * Returns keyword groups suitable for hasTargetInZone(...).
     *
     * Recognizes patterns like:
     *   "[download] Arleil, Doallyn, Tessek, Wild Karrde, or a Tatooine battleground"
     *   "deploy Tala Durith from Reserve Deck"
     *   "take Sabine, Under Attack, or a blaster into hand from Reserve Deck"
     *
     * Each returned String is a keyword to match against card titles.
     */
    public static List<String> parseSourceCardPullTargets(String gameText) {
        List<String> targets = new ArrayList<>();
        if (gameText == null || gameText.isEmpty()) return targets;

        // Patterns that introduce a list of targets:
        //   [download] X, Y, Z, or W
        //   ... X, Y, ... from Reserve Deck         (any verb)
        // V82.1 (Steve, 2026-05-16): Drop the "deploy" anchor entirely.
        // Old "\\bdeploy\\s+(...)\\s+from\\s+reserve\\s+deck" matched the
        // FIRST "deploy" — in Invasion ("once during your deploy phase may
        // deploy a Naboo site from Reserve Deck") it captured "phase may
        // deploy a naboo site" as target. V67h then hard-blocked with -9999.
        //
        // Per Steve: "Just do 'from reserve deck' and don't search for the
        // text 'deploy' at all. This will cover any deploy from reserve."
        //
        // New approach: capture EVERYTHING in the same clause before "from
        // Reserve Deck", then aggressively strip leading verb/article noise
        // in the normalization loop. Works for any phrasing — "may deploy
        // X", "take X into hand", "search for X", "[download] X", etc.
        java.util.regex.Pattern[] patterns = new java.util.regex.Pattern[] {
            java.util.regex.Pattern.compile(
                "\\[download\\]\\s+([^.;]+?)(?=\\.|;|$)",
                java.util.regex.Pattern.CASE_INSENSITIVE),
            java.util.regex.Pattern.compile(
                "([^.;]+?)\\s+from\\s+reserve\\s+deck",
                java.util.regex.Pattern.CASE_INSENSITIVE),
        };

        for (java.util.regex.Pattern p : patterns) {
            java.util.regex.Matcher m = p.matcher(gameText);
            while (m.find()) {
                String list = m.group(1);
                if (list == null) continue;
                // Normalize OR → comma, then split.
                String norm = list.replaceAll("(?i)\\bor\\b", ",")
                                  .replaceAll(",\\s*,", ",")
                                  .replaceAll("\\s+", " ");
                for (String part : norm.split(",")) {
                    String t = part.trim().toLowerCase(Locale.ROOT);
                    if (t.isEmpty()) continue;
                    // V82.1: aggressively peel known verb/article prefixes
                    // until the string stabilizes. Wide capture above means
                    // we may see "once during your deploy phase may deploy a
                    // naboo site" — strip down to "naboo site".
                    String prev;
                    do {
                        prev = t;
                        // "once per turn", "once during your deploy phase", etc.
                        // GREEDY [^,]* so we match the LAST occurrence of the
                        // phase-ender (turn/phase/deploy/...). Otherwise
                        // "once during your deploy phase may deploy" stops at
                        // the first "deploy" and leaves "phase may deploy" behind.
                        t = t.replaceFirst("^once\\s+(per|during|each)\\b[^,]*\\b(turn|phase|game|deployment|battle|move|draw|control|activate|deploy)\\b\\s*", "");
                        t = t.replaceFirst("^(may|can|must|will)\\s+", "");
                        t = t.replaceFirst("^(deploy|take|download|reveal|use|put|place|move|search\\s+for)\\s+", "");
                        t = t.replaceFirst("^(a|an|the)\\s+", "");
                        t = t.replaceFirst("^to\\s+", "");
                    } while (!t.equals(prev));
                    // Strip trailing noise.
                    t = t.replaceFirst("\\s+into\\s+hand$", "");
                    t = t.replaceFirst("\\s+aboard\\b.*$", "");
                    t = t.replaceFirst("\\s+(card|cards)$", "");
                    // V82.3 (Steve, 2026-05-16): strip leftover parens and
                    // brackets. Begin Landing's text is "(or Coruscant) docking
                    // bay" — after or→comma split we get "[episode i] (" and
                    // "coruscant) docking bay". Paren-strip cleans both so the
                    // category/predicate fallback sees clean words like
                    // "coruscant docking bay" → can match docking_bay keyword.
                    t = t.replaceAll("[\\[\\]\\(\\)]", " ").replaceAll("\\s+", " ").trim();
                    // Re-run prefix strips after paren-removal — leftover noise
                    // like " coruscant docking bay" should also pass through.
                    String prev2;
                    do {
                        prev2 = t;
                        t = t.replaceFirst("^(may|can|must|will)\\s+", "");
                        t = t.replaceFirst("^(deploy|take|download|reveal|use|put|place|move|search\\s+for)\\s+", "");
                        t = t.replaceFirst("^(a|an|the)\\s+", "");
                        t = t.replaceFirst("^to\\s+", "");
                    } while (!t.equals(prev2));
                    if (t.length() < 3) continue;
                    // V82.1 SANITY CHECK: if obvious stopwords remain, our
                    // parse failed — drop silently so caller sees UNKNOWN
                    // instead of WILL_FAIL on a garbage target.
                    if (t.matches(".*\\b(phase|deploy|once|may|during|each|turn)\\b.*")) continue;
                    if (!targets.contains(t)) targets.add(t);
                }
            }
        }
        return targets;
    }

    /**
     * Validate a pull where the actionText is generic ("Choose card to deploy
     * from Reserve Deck") but the source card's game text describes the filter.
     * Returns WILL_FAIL if NO parsed target is in the source zone.
     * Returns UNKNOWN if game text didn't yield any parsable targets.
     */
    public PullValidation validatePullFromSourceCard(Zone sourceZone, String sourceCardGameText) {
        if (sourceZone == null) {
            return new PullValidation(PullOutcome.UNKNOWN, "no source zone");
        }
        List<String> targets = parseSourceCardPullTargets(sourceCardGameText);
        if (targets.isEmpty()) {
            return new PullValidation(PullOutcome.UNKNOWN, "could not parse targets from game text");
        }
        // If ANY target keyword is in the source zone, the pull can succeed.
        for (String t : targets) {
            if (hasTargetInZone(sourceZone, t)) {
                return new PullValidation(PullOutcome.WILL_SUCCEED,
                    "found '" + t + "' in " + sourceZone + " (parsed from source game text)");
            }
        }
        // V82.1 (Steve, 2026-05-16): TYPE-API FALLBACK.
        // hasTargetInZone is a substring check on titles — fails for generic
        // type-words ("site", "character", "weapon") that aren't IN titles.
        // Per Steve's standing rule (feedback_card_search_by_type_not_text.md):
        // search by CardCategory, not substring. Map known type-words to
        // categories and check the zone by category.
        for (String t : targets) {
            CardCategory cat = mapTypeWordToCategory(t);
            if (cat != null && !getCardsByCategory(cat, sourceZone).isEmpty()) {
                return new PullValidation(PullOutcome.WILL_SUCCEED,
                    "type-word '" + t + "' → category " + cat
                        + " present in " + sourceZone + " (V82.1 category fallback)");
            }
        }
        // V82.2 (Steve, 2026-05-16): BLUEPRINT-PREDICATE FALLBACK for multi-word
        // specialty pulls like "neimoidian pilot" (Blockade Flagship), "jedi
        // pilot", "imperial leader", etc. For each parsed target we split on
        // spaces and AND together the per-word checks (Icon/Keyword/Species)
        // against each card blueprint in the zone. Catches Icon/Species/Keyword
        // targets that don't map to a single CardCategory.
        for (String t : targets) {
            String[] words = t.toLowerCase(Locale.ROOT).trim().split("\\s+");
            // Count how many of the words have a known predicate. If none, we
            // can't validate via this path.
            int recognizedWords = 0;
            for (String w : words) if (wordHasPredicate(w)) recognizedWords++;
            if (recognizedWords == 0) continue;
            for (DeckCard dc : allCards) {
                if (!sourceZone.equals(dc.getCurrentZone())) continue;
                SwccgCardBlueprint bp = dc.getBlueprint();
                if (bp == null) continue;
                boolean allMatch = true;
                for (String w : words) {
                    if (!wordHasPredicate(w)) continue; // unrecognized words don't constrain
                    if (!blueprintMatchesWord(bp, w)) { allMatch = false; break; }
                }
                if (allMatch) {
                    return new PullValidation(PullOutcome.WILL_SUCCEED,
                        "blueprint-predicate match for '" + t + "' on '" + dc.getTitle()
                            + "' in " + sourceZone + " (V82.2 fallback)");
                }
            }
        }
        // V82.2: Decide WILL_FAIL vs UNKNOWN.
        // - If ANY target had a recognized type-word (category OR predicate),
        //   we DID validate authoritatively → WILL_FAIL is safe.
        // - If NO target had any recognized type-word AND substring also
        //   failed, target is fully proper-noun → WILL_FAIL is safe (substring
        //   would have matched if title were in zone).
        // So WILL_FAIL is correct in both cases.
        return new PullValidation(PullOutcome.WILL_FAIL,
            "no parsed target [" + String.join(", ", targets)
                + "] in " + sourceZone + " — search will FAIL and reveal zone");
    }

    /** V82.2: Does this single word have a recognized predicate? */
    private static boolean wordHasPredicate(String word) {
        if (word == null) return false;
        switch (word) {
            case "pilot": case "pilots":
            case "neimoidian": case "neimoidians":
            case "jedi":
            case "sith":
            case "imperial": case "imperials":
            case "rebel": case "rebels":
            case "republic":
            case "leader": case "leaders":
            case "warrior": case "warriors":
            case "droid": case "droids":
            case "capital":
                return true;
            default:
                return false;
        }
    }

    /**
     * V82.2: Does this card blueprint match this single word?
     * Uses Icon/Keyword/Species/Subtype checks (type-by-API per Steve's rule).
     */
    private static boolean blueprintMatchesWord(SwccgCardBlueprint bp, String word) {
        if (bp == null || word == null) return false;
        try {
            switch (word) {
                case "pilot": case "pilots":
                    return bp.hasIcon(com.gempukku.swccgo.common.Icon.PILOT);
                case "neimoidian": case "neimoidians":
                    return bp.getSpecies() == com.gempukku.swccgo.common.Species.NEIMOIDIAN;
                case "jedi":
                    // Approximate: any Jedi Master, or any LIGHT character
                    // with ability >= 6 (Filters.Jedi definition).
                    if (bp.hasIcon(com.gempukku.swccgo.common.Icon.JEDI_MASTER)) return true;
                    return bp.getCardCategory() == CardCategory.CHARACTER
                        && bp.getSide() == com.gempukku.swccgo.common.Side.LIGHT
                        && bp.hasAbilityAttribute() && bp.getAbility() != null
                        && bp.getAbility() >= 6f;
                case "sith":
                    return bp.hasIcon(com.gempukku.swccgo.common.Icon.DARK_JEDI_MASTER);
                case "imperial": case "imperials":
                    return bp.hasIcon(com.gempukku.swccgo.common.Icon.IMPERIAL);
                case "rebel": case "rebels":
                    return bp.hasIcon(com.gempukku.swccgo.common.Icon.REBEL);
                case "republic":
                    return bp.hasIcon(com.gempukku.swccgo.common.Icon.REPUBLIC);
                case "leader": case "leaders":
                    return bp.hasKeyword(com.gempukku.swccgo.common.Keyword.LEADER);
                case "warrior": case "warriors":
                    return bp.hasIcon(com.gempukku.swccgo.common.Icon.WARRIOR);
                case "droid": case "droids":
                    return bp.hasIcon(com.gempukku.swccgo.common.Icon.DROID);
                case "capital":
                    return bp.getCardSubtype() == CardSubtype.CAPITAL;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * V82.1: Map generic SWCCG type-words to CardCategory enum values.
     * Returns null for proper-noun targets like "Tala Durith" or "Naboo".
     * "site", "location", "battleground" → LOCATION
     * "character" → CHARACTER, etc.
     */
    private static CardCategory mapTypeWordToCategory(String target) {
        if (target == null) return null;
        // Last word is usually the type if it's generic (e.g., "naboo site",
        // "tatooine battleground", "blaster weapon"). Check last word first,
        // then full string.
        String lower = target.toLowerCase(Locale.ROOT).trim();
        // V82.3: docking bay is a LOCATION (Keyword.DOCKING_BAY) — match the
        // phrase before falling through to single-word lastWord.
        if (lower.contains("docking bay")) return CardCategory.LOCATION;
        String lastWord = lower.contains(" ")
            ? lower.substring(lower.lastIndexOf(' ') + 1)
            : lower;
        switch (lastWord) {
            case "site": case "sites":
            case "location": case "locations":
            case "battleground": case "battlegrounds":
                return CardCategory.LOCATION;
            case "character": case "characters":
                return CardCategory.CHARACTER;
            case "weapon": case "weapons":
            case "blaster": case "blasters":
            case "lightsaber": case "lightsabers":
                return CardCategory.WEAPON;
            case "device": case "devices":
                return CardCategory.DEVICE;
            case "starship": case "starships":
            case "fighter": case "fighters":
                return CardCategory.STARSHIP;
            case "vehicle": case "vehicles":
                return CardCategory.VEHICLE;
            case "creature": case "creatures":
                return CardCategory.CREATURE;
            case "effect": case "effects":
                return CardCategory.EFFECT;
            case "interrupt": case "interrupts":
                return CardCategory.INTERRUPT;
            default:
                return null;
        }
    }

    // =========================================================================
    // Failed Pull Tracking
    // =========================================================================

    /**
     * Record a failed attempt to pull a card from the reserve deck.
     * After 2 failures, shouldAvoidPulling() returns true.
     */
    public void recordFailedPull(String blueprintId) {
        if (blueprintId == null) return;
        int count = failedPulls.getOrDefault(blueprintId, 0) + 1;
        failedPulls.put(blueprintId, count);
        LOG.warn("📚 [DeckOracle] Failed pull recorded for {} (attempt #{})", blueprintId, count);
    }

    /**
     * Should we avoid trying to pull this card? True if 2+ consecutive failures.
     */
    public boolean shouldAvoidPulling(String blueprintId) {
        if (blueprintId == null) return false;
        return failedPulls.getOrDefault(blueprintId, 0) >= 2;
    }

    /**
     * Also check by title (for cases where we only have the card name).
     */
    public boolean shouldAvoidPullingByTitle(String title) {
        if (title == null) return false;
        String titleLower = title.toLowerCase(Locale.ROOT);
        // Check if any blueprintId with this title has failed pulls
        List<DeckCard> cards = catalogByTitle.get(titleLower);
        if (cards != null) {
            for (DeckCard dc : cards) {
                if (shouldAvoidPulling(dc.getBlueprintId())) return true;
            }
        }
        return false;
    }

    /** Clear failed pull tracking (call on new turn or phase change). */
    public void clearFailedPulls() {
        if (!failedPulls.isEmpty()) {
            LOG.debug("📚 [DeckOracle] Clearing {} failed pull records", failedPulls.size());
            failedPulls.clear();
        }
    }

    // =========================================================================
    // V24.10: AMSD Per-Turn Failure Tracking
    // =========================================================================

    /**
     * Record that AMSD failed on this turn.
     * Prevents retrying AMSD on the same turn — Piett won't magically appear in reserve
     * mid-turn. Wait for recirculation on the next turn.
     */
    public void recordAmsdFailedOnTurn(int turnNumber) {
        amsdFailedOnTurn = turnNumber;
        LOG.warn("📚 [DeckOracle] V24.10: AMSD failed on turn {} — blocking retries until next turn", turnNumber);
    }

    /**
     * Should we skip AMSD this turn because it already failed?
     * Returns true if AMSD failed on the given turn number.
     */
    public boolean hasAmsdFailedThisTurn(int turnNumber) {
        return amsdFailedOnTurn == turnNumber;
    }

    // =========================================================================
    // Internal Helpers
    // =========================================================================

    /** Find all DeckCards matching a blueprintId or title. */
    private List<DeckCard> findCards(String blueprintIdOrTitle) {
        if (blueprintIdOrTitle == null) return Collections.emptyList();

        // Exact blueprintId match
        List<DeckCard> byBp = catalogByBlueprint.get(blueprintIdOrTitle);
        if (byBp != null && !byBp.isEmpty()) return byBp;

        // Title match
        String titleKey = blueprintIdOrTitle.toLowerCase(Locale.ROOT);
        List<DeckCard> byTitle = catalogByTitle.get(titleKey);
        if (byTitle != null && !byTitle.isEmpty()) return byTitle;

        return Collections.emptyList();
    }

    /** Catalog a list of PhysicalCards from a specific zone. */
    private void catalogZone(List<PhysicalCard> cards, Zone zone) {
        if (cards == null) return;
        for (PhysicalCard card : cards) {
            if (card == null) continue;
            catalogCard(card, zone);
        }
    }

    /** Catalog a single card into the indexes. */
    private void catalogCard(PhysicalCard card, Zone zone) {
        DeckCard dc = new DeckCard(card, zone);

        allCards.add(dc);

        // Index by blueprintId
        catalogByBlueprint.computeIfAbsent(dc.getBlueprintId(), k -> new ArrayList<>()).add(dc);

        // Index by lowercase title
        String titleKey = dc.getTitle().toLowerCase(Locale.ROOT);
        catalogByTitle.computeIfAbsent(titleKey, k -> new ArrayList<>()).add(dc);
    }

    /**
     * Match physical cards from a zone to existing DeckCard entries.
     * Updates the currentZone of matched DeckCards.
     *
     * @param cards Physical cards from a zone
     * @param zone  The zone these cards are in (null = use card's own zone)
     * @param matched Tracking array for which DeckCards have been matched
     */
    private void matchCardsFromZone(List<PhysicalCard> cards, Zone zone, boolean[] matched) {
        if (cards == null) return;

        for (PhysicalCard card : cards) {
            if (card == null) continue;
            String bpId = card.getBlueprintId(true);
            Zone actualZone = (zone != null) ? zone : card.getZone();

            // Find an unmatched DeckCard with this blueprintId
            List<DeckCard> candidates = catalogByBlueprint.get(bpId);
            if (candidates != null) {
                for (int i = 0; i < allCards.size(); i++) {
                    if (matched[i]) continue;
                    DeckCard dc = allCards.get(i);
                    if (dc.getBlueprintId().equals(bpId)) {
                        dc.setCurrentZone(actualZone);
                        matched[i] = true;
                        break;
                    }
                }
            } else {
                // Card not in catalog — new card acquired mid-game (rare but possible)
                // Add it to the catalog dynamically
                DeckCard dc = new DeckCard(card, actualZone);
                allCards.add(dc);
                catalogByBlueprint.computeIfAbsent(bpId, k -> new ArrayList<>()).add(dc);
                String titleKey = dc.getTitle().toLowerCase(Locale.ROOT);
                catalogByTitle.computeIfAbsent(titleKey, k -> new ArrayList<>()).add(dc);
                // Expand matched array (we can't resize, but new cards are auto-matched)
                LOG.debug("📚 [DeckOracle] New card discovered mid-game: {} [{}]", dc.getTitle(), bpId);
            }
        }
    }

    // =========================================================================
    // Deck Manifest — full inventory for strategic planning
    // =========================================================================

    /**
     * Get the full deck manifest: every unique card with copy count and zone breakdown.
     * Returns a list of ManifestEntry sorted by category then title.
     * Evaluators can use this to plan combos, check availability, etc.
     */
    public List<ManifestEntry> getDeckManifest() {
        Map<String, ManifestEntry> manifest = new LinkedHashMap<>();

        for (DeckCard dc : allCards) {
            ManifestEntry entry = manifest.get(dc.getBlueprintId());
            if (entry == null) {
                entry = new ManifestEntry(dc.getBlueprintId(), dc.getTitle(),
                    dc.getCategory(), dc.getSubtype(), dc.getDestiny(), dc.getDeployCost());
                manifest.put(dc.getBlueprintId(), entry);
            }
            entry.addCopy(dc.getCurrentZone());
        }

        // Sort by category name, then by title
        List<ManifestEntry> sorted = new ArrayList<>(manifest.values());
        sorted.sort((a, b) -> {
            String catA = a.category != null ? a.category.name() : "ZZZ";
            String catB = b.category != null ? b.category.name() : "ZZZ";
            int cmp = catA.compareTo(catB);
            if (cmp != 0) return cmp;
            return a.title.compareToIgnoreCase(b.title);
        });

        return sorted;
    }

    /**
     * A single entry in the deck manifest — one unique card with zone breakdown.
     */
    public static class ManifestEntry {
        public final String blueprintId;
        public final String title;
        public final CardCategory category;
        public final CardSubtype subtype;
        public final float destiny;
        public final float deployCost;
        public int totalCopies = 0;
        public int inHand = 0;
        public int inReserve = 0;
        public int inPlay = 0;
        public int inUsedPile = 0;
        public int inLostPile = 0;
        public int inForcePile = 0;
        public int other = 0;

        public ManifestEntry(String blueprintId, String title, CardCategory category,
                             CardSubtype subtype, float destiny, float deployCost) {
            this.blueprintId = blueprintId;
            this.title = title;
            this.category = category;
            this.subtype = subtype;
            this.destiny = destiny;
            this.deployCost = deployCost;
        }

        void addCopy(Zone zone) {
            totalCopies++;
            if (zone == null) { other++; return; }
            switch (zone) {
                case HAND: inHand++; break;
                case RESERVE_DECK: inReserve++; break;
                case USED_PILE: inUsedPile++; break;
                case LOST_PILE: inLostPile++; break;
                case FORCE_PILE: inForcePile++; break;
                default:
                    if (zone.isInPlay()) inPlay++;
                    else other++;
                    break;
            }
        }

        /** Short zone summary string, only showing non-zero zones. */
        public String getZoneSummary() {
            List<String> parts = new ArrayList<>();
            if (inHand > 0)      parts.add(inHand + " hand");
            if (inReserve > 0)   parts.add(inReserve + " reserve");
            if (inPlay > 0)      parts.add(inPlay + " in-play");
            if (inUsedPile > 0)  parts.add(inUsedPile + " used");
            if (inLostPile > 0)  parts.add(inLostPile + " lost");
            if (inForcePile > 0) parts.add(inForcePile + " force");
            if (other > 0)       parts.add(other + " other");
            return String.join(", ", parts);
        }

        @Override
        public String toString() {
            return String.format("%s [%s] x%d (dest=%.0f cost=%.0f) — %s",
                title, blueprintId, totalCopies, destiny, deployCost, getZoneSummary());
        }
    }

    // =========================================================================
    // Logging
    // =========================================================================

    /** Log the full deck manifest after analysis. */
    private void logCatalogSummary() {
        // Zone totals
        int handCount = 0, reserveCount = 0, inPlayCount = 0, otherCount = 0;
        float totalDestiny = 0;
        int destinyCardCount = 0;

        Map<CardCategory, Integer> categoryCount = new EnumMap<>(CardCategory.class);

        for (DeckCard dc : allCards) {
            Zone z = dc.getCurrentZone();
            if (z == Zone.HAND) handCount++;
            else if (z == Zone.RESERVE_DECK) reserveCount++;
            else if (z != null && z.isInPlay()) inPlayCount++;
            else otherCount++;

            if (dc.getDestiny() > 0) {
                totalDestiny += dc.getDestiny();
                destinyCardCount++;
            }

            if (dc.getCategory() != null) {
                categoryCount.merge(dc.getCategory(), 1, Integer::sum);
            }
        }

        float avgDestiny = destinyCardCount > 0 ? totalDestiny / destinyCardCount : 0;

        LOG.warn("📚 ===================================================================");
        LOG.warn("📚 DECK ORACLE — FULL DECK MANIFEST");
        LOG.warn("📚 ===================================================================");
        LOG.warn("📚 Total cards: {} | Unique blueprints: {}", totalDeckSize, catalogByBlueprint.size());
        LOG.warn("📚 Hand: {} | Reserve: {} | In play: {} | Other: {}",
            handCount, reserveCount, inPlayCount, otherCount);
        LOG.warn("📚 Average destiny: {} ({} cards with destiny)",
            String.format("%.1f", avgDestiny), destinyCardCount);
        LOG.warn("📚 Card categories: {}", categoryCount);
        LOG.warn("📚 -------------------------------------------------------------------");

        // Full manifest grouped by card category
        List<ManifestEntry> manifest = getDeckManifest();
        CardCategory lastCategory = null;

        for (ManifestEntry entry : manifest) {
            // Print category header when it changes
            if (entry.category != lastCategory) {
                lastCategory = entry.category;
                String catName = lastCategory != null ? lastCategory.name() : "UNKNOWN";
                int catCount = categoryCount.getOrDefault(lastCategory, 0);
                LOG.warn("📚");
                LOG.warn("📚 ── {} ({} cards) ──", catName, catCount);
            }

            // Print each card: count × title (destiny/cost) — zone breakdown
            String copyLabel = entry.totalCopies > 1
                ? entry.totalCopies + "× "
                : "   ";
            LOG.warn("📚   {}{} (dest={} cost={}) — {}",
                copyLabel, entry.title,
                String.format("%.0f", entry.destiny),
                String.format("%.0f", entry.deployCost),
                entry.getZoneSummary());
        }

        LOG.warn("📚 -------------------------------------------------------------------");

        // Highlight high-destiny cards in reserve (destiny >= 5)
        List<DeckCard> highDestiny = getHighDestinyCards(Zone.RESERVE_DECK, 5);
        if (!highDestiny.isEmpty()) {
            LOG.warn("📚 ⭐ High destiny in reserve (5+): {}", highDestiny.stream()
                .map(dc -> dc.getTitle() + "(" + String.format("%.0f", dc.getDestiny()) + ")")
                .collect(Collectors.joining(", ")));
        }

        // Highlight cards with multiple copies
        long multiCopyCount = manifest.stream().filter(e -> e.totalCopies > 1).count();
        if (multiCopyCount > 0) {
            LOG.warn("📚 🔢 Cards with multiple copies:");
            for (ManifestEntry entry : manifest) {
                if (entry.totalCopies > 1) {
                    LOG.warn("📚      {}× {} — {}", entry.totalCopies, entry.title, entry.getZoneSummary());
                }
            }
        }

        LOG.warn("📚 ===================================================================");
    }
}
