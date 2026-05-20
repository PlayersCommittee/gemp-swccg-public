package com.gempukku.swccgo.ai.models.chosenone.strategy;

import com.gempukku.swccgo.ai.models.chosenone.ChosenOneLogger;
import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgCardBlueprint;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;

import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * V21 ObjectiveAnalyzer - Runtime parser for objective game text.
 *
 * Reads the game text from Rando's own objective card and extracts:
 * 1. FLIP CONDITIONS - what locations to occupy/control, what cards need to be on table
 * 2. PRIORITY LOCATIONS - location names from flip conditions (where to deploy)
 * 3. KEY CARDS - card names that must be "on table" to flip
 * 4. PULLABLE CARDS - cards the objective can take from Reserve Deck
 * 5. FLIPPED STATUS - whether the objective is currently flipped
 *
 * Uses pattern matching on standard SWCCG objective text conventions:
 *   "Flip this card if [condition]"
 *   "occupy [location]"  /  "control [location]"
 *   "[Card Name] on table"
 *   "may take [Card Name] into hand from Reserve Deck"
 *
 * This is Approach B: universal, no hardcoded per-objective knowledge needed.
 */
public class ObjectiveAnalyzer {
    private static final Logger LOG = ChosenOneLogger.getStrategyLogger();

    private final Set<String> flipConditionLocations = new HashSet<>();
    private final Set<String> flipConditionLocationFragments = new HashSet<>();
    private final Set<String> requiredCardsOnTable = new HashSet<>();
    private final Set<String> pullableCards = new HashSet<>();
    // V22.2: Back side / flip-back protection
    private final Set<String> flipBackLocations = new HashSet<>();
    private final Set<String> flipBackLocationFragments = new HashSet<>();
    private String flipBackConditionText = null;
    private boolean flipBackRequiresOccupy = false;
    private boolean flipBackRequiresControl = false;
    private String flipConditionText = null;
    private String objectiveTitle = null;
    private String objectiveBlueprintId = null;
    private boolean analyzed = false;
    private boolean isFlipped = false;
    private boolean requiresOccupy = false;
    private boolean requiresControl = false;

    // V25: ISB Operations awareness
    private boolean isISBOperations = false;
    private int isbFlipAgentCount = 0;         // How many ISB agents needed on table to flip (e.g. 4)
    private int isbFlipLocationCount = 0;      // How many Rebel Base locations ISB agents must control (e.g. 2)
    private boolean isbFlipBackNoAgents = false; // Flips back if no ISB agents on table

    // V25: Hunt Down V awareness
    private boolean isHuntDownV = false;
    private boolean huntDownNeedsVader = false;       // Vader required at battleground to flip
    private boolean huntDownFlipBackNoVader = false;   // Flips back if Vader not on table

    private static final Pattern FLIP_PATTERN = Pattern.compile(
        "Flip this card if (.+?)(?:\\.|\\\\|$)", Pattern.CASE_INSENSITIVE);

    private static final Pattern OCCUPY_LOCATION_PATTERN = Pattern.compile(
        "(?:you )?occupy\\s+(.+?)(?:\\s+and\\s+|,\\s+|\\.|\\\\|$)", Pattern.CASE_INSENSITIVE);

    private static final Pattern CONTROL_LOCATION_PATTERN = Pattern.compile(
        "(?:you )?control\\s+(.+?)(?:\\s+and\\s+|,\\s+|\\.|\\\\|$)", Pattern.CASE_INSENSITIVE);

    private static final Pattern ON_TABLE_PATTERN = Pattern.compile(
        "([A-Z][\\w\\s',!\\-()]+?)\\s+(?:is )?on table", Pattern.CASE_INSENSITIVE);

    private static final Pattern TAKE_FROM_RESERVE_PATTERN = Pattern.compile(
        "may take\\s+(.+?)\\s+into hand from Reserve Deck", Pattern.CASE_INSENSITIVE);

    public void analyze(SwccgGame game, String playerId, Side side) {
        LOG.warn("[ObjectiveAnalyzer] analyze() CALLED - game={}, player={}, side={}", game != null, playerId, side);
        if (game == null || playerId == null) return;

        GameState gameState = game.getGameState();
        if (gameState == null) return;

        try {
            PhysicalCard objectiveCard = findOurObjective(gameState, playerId);
            if (objectiveCard == null) {
                LOG.warn("[ObjectiveAnalyzer] No objective found for {}", playerId);
                return;
            }

            SwccgCardBlueprint blueprint = objectiveCard.getBlueprint();
            if (blueprint == null) return;

            String title = objectiveCard.getTitle();
            String gameText = blueprint.getGameText();

            if (gameText == null || gameText.isEmpty()) {
                LOG.warn("[ObjectiveAnalyzer] Objective '{}' has no game text", title);
                return;
            }

            String bpId = objectiveCard.getBlueprintId(true);
            if (analyzed && bpId != null && bpId.equals(objectiveBlueprintId)) {
                updateFlipStatus(objectiveCard);
                return;
            }

            this.objectiveTitle = title;
            this.objectiveBlueprintId = bpId;
            LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] Analyzing objective: '{}'", title);
            LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] Game text: {}", gameText);

            parseGameText(gameText);
            updateFlipStatus(objectiveCard);
            this.analyzed = true;

            logAnalysisResults();

        } catch (Exception e) {
            LOG.warn("[ObjectiveAnalyzer] Error analyzing objective: {}", e.getMessage());
        }
    }

    public boolean isObjectiveRelevantLocation(String locationTitle) {
        if (!analyzed || locationTitle == null) return false;
        String titleLower = locationTitle.toLowerCase(Locale.ROOT);

        if (flipConditionLocations.contains(titleLower)) return true;

        for (String fragment : flipConditionLocationFragments) {
            if (titleLower.contains(fragment)) return true;
        }

        return false;
    }

    public boolean isRequiredCardForFlip(String cardTitle) {
        if (!analyzed || cardTitle == null) return false;
        String titleLower = cardTitle.toLowerCase(Locale.ROOT);
        for (String required : requiredCardsOnTable) {
            if (titleLower.startsWith(required) || titleLower.equals(required)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPullableCard(String cardTitle) {
        if (!analyzed || cardTitle == null) return false;
        String titleLower = cardTitle.toLowerCase(Locale.ROOT);
        for (String pullable : pullableCards) {
            if (titleLower.startsWith(pullable) || titleLower.equals(pullable)) {
                return true;
            }
        }
        return false;
    }

    public float getLocationObjectiveBonus(String locationTitle) {
        if (!analyzed) return 0.0f;

        if (isFlipped) {
            // V22.2: POST-FLIP — protecting locations is MORE important, not less!
            // Returning 0 here was a critical bug: after flipping, Rando stopped caring
            // about objective locations and deployed elsewhere. Now we return a HIGHER
            // bonus because losing these locations means the objective flips BACK.
            if (isFlipBackProtectionLocation(locationTitle)) {
                return 200.0f;  // Higher than pre-flip 150 — defense is critical
            }
            return 0.0f;
        }

        // Pre-flip: standard objective location bonus
        if (isObjectiveRelevantLocation(locationTitle)) {
            return 150.0f;
        }
        return 0.0f;
    }

    public Set<String> getFlipConditionLocationFragments() {
        return Collections.unmodifiableSet(flipConditionLocationFragments);
    }

    public boolean isAnalyzed() { return analyzed; }
    public boolean isFlipped() { return isFlipped; }
    public String getObjectiveTitle() { return objectiveTitle; }
    public String getFlipConditionText() { return flipConditionText; }
    public Set<String> getRequiredCardsOnTable() { return Collections.unmodifiableSet(requiredCardsOnTable); }
    public Set<String> getPullableCards() { return Collections.unmodifiableSet(pullableCards); }
    public boolean requiresOccupy() { return requiresOccupy; }
    public boolean requiresControl() { return requiresControl; }

    // V25: ISB Operations accessors
    public boolean isISBOperations() { return isISBOperations; }
    public int getISBFlipAgentCount() { return isbFlipAgentCount; }
    public int getISBFlipLocationCount() { return isbFlipLocationCount; }
    public boolean isbFlipBackRequiresAgents() { return isbFlipBackNoAgents; }

    // V25: Hunt Down V accessors
    public boolean isHuntDownV() { return isHuntDownV; }
    public boolean huntDownNeedsVader() { return huntDownNeedsVader; }
    public boolean huntDownFlipBackNoVader() { return huntDownFlipBackNoVader; }

    /**
     * V67ak (Steve, 2026-05-07): UNIVERSAL KEY-CHARACTER TOKEN EXTRACTOR.
     *
     * <p>Extracts strategy-relevant character/persona tokens from:
     * <ul>
     * <li>Active objective's full game text (not just flip condition)</li>
     * <li>Any Epic Event cards on Rando's side of table (helper cards for objective)</li>
     * <li>Any persistent Effect cards on Rando's side of table</li>
     * </ul>
     *
     * <p>Tokens are lowercased capitalized phrases matching persona-name-like patterns,
     * with generic words filtered out. Use the result to bonus deploy/pull actions for
     * cards whose title contains a token (e.g. Hunt Down V's text mentions "Vader" → any
     * Vader card in hand or pullable from reserve gets a deploy priority bonus).
     *
     * <p>Avoids hardcoding character lists per deck — one universal scan, applicable to
     * any objective that names characters in flip conditions or game text.
     */
    public Set<String> getStrategyCharacterTokens(SwccgGame game, String playerId) {
        Set<String> tokens = new HashSet<>();
        if (game == null || playerId == null) return tokens;
        GameState gs = game.getGameState();
        if (gs == null) return tokens;

        // Pattern: capitalized words / phrases (proper-noun-ish), 3-30 chars, not generic.
        // E.g. "Vader" (one word), "Lord Sidious" (two), "General Grievous" (two).
        Pattern personaPattern = Pattern.compile(
            "\\b([A-Z][a-z]+(?:\\s+[A-Z][a-z]+){0,2})\\b");

        // Source 1: active objective's full game text
        try {
            PhysicalCard obj = findOurObjective(gs, playerId);
            if (obj != null && obj.getBlueprint() != null) {
                String gt = obj.getBlueprint().getGameText();
                if (gt != null) extractTokensInto(gt, personaPattern, tokens);
            }
        } catch (Exception e) { /* ignore */ }

        // Source 2: Epic Event cards + Effects on Rando's side of table
        try {
            for (PhysicalCard pc : gs.getAllPermanentCards()) {
                if (pc == null || pc.getBlueprint() == null) continue;
                if (!playerId.equals(pc.getOwner())) continue;
                Zone z = pc.getZone();
                if (z == null || !z.isInPlay()) continue;
                CardCategory cat = pc.getBlueprint().getCardCategory();
                // Epic events (Fallen Order, etc.) and persistent Effects
                if (cat == CardCategory.EPIC_EVENT || cat == CardCategory.EFFECT) {
                    String gt = pc.getBlueprint().getGameText();
                    if (gt != null) extractTokensInto(gt, personaPattern, tokens);
                }
            }
        } catch (Exception e) { /* ignore */ }

        return tokens;
    }

    private void extractTokensInto(String text, Pattern personaPattern, Set<String> out) {
        Matcher m = personaPattern.matcher(text);
        while (m.find()) {
            String tok = m.group(1).trim();
            // Reject generic / structural words even in capitalized form.
            String lower = tok.toLowerCase(Locale.ROOT);
            if (lower.length() < 4) continue;
            if (isGenericWord(lower)) continue;
            // Skip pure-grammar capitals like sentence starts:
            // "Deploy", "While", "If", "Once", "May", "Your", "When", "Until"
            if (lower.equals("deploy") || lower.equals("while") || lower.equals("if")
                    || lower.equals("once") || lower.equals("may") || lower.equals("your")
                    || lower.equals("when") || lower.equals("until") || lower.equals("flip")
                    || lower.equals("during") || lower.equals("after") || lower.equals("for")
                    || lower.equals("from") || lower.equals("here") || lower.equals("then")
                    || lower.equals("this") || lower.equals("that") || lower.equals("each")
                    || lower.equals("force") || lower.equals("battle") || lower.equals("with")
                    || lower.equals("just") || lower.equals("must") || lower.equals("does")
                    || lower.equals("you") || lower.equals("opponent") || lower.equals("opponents")
                    || lower.equals("destiny") || lower.equals("starts") || lower.equals("ends")
                    || lower.equals("turn") || lower.equals("character") || lower.equals("characters")
                    || lower.equals("location") || lower.equals("locations")
                    || lower.equals("permanent") || lower.equals("immune") || lower.equals("attrition")
                    || lower.equals("immediately") || lower.equals("simultaneously")
                    || lower.equals("episode") || lower.equals("reserve") || lower.equals("hand")
                    || lower.equals("table") || lower.equals("control") || lower.equals("occupy")
                    || lower.equals("deploy as if from hand")) continue;
            out.add(lower);
        }
    }

    /**
     * V67ak: Convenience — does the candidate card's title contain ANY of the strategy
     * tokens extracted from objective + epic events? Used by DeployEvaluator and
     * ActionTextEvaluator to give key characters a strong deploy priority.
     */
    public boolean isStrategyKeyCharacter(SwccgGame game, String playerId, String cardTitle) {
        if (cardTitle == null) return false;
        String tl = cardTitle.toLowerCase(Locale.ROOT);
        for (String tok : getStrategyCharacterTokens(game, playerId)) {
            if (tl.contains(tok)) return true;
        }
        return false;
    }

    /**
     * V25: Check if Vader is currently on table for a player.
     */
    public boolean isVaderOnTable(GameState gameState, String playerId) {
        if (gameState == null || playerId == null) return false;
        for (PhysicalCard card : gameState.getAllPermanentCards()) {
            if (card == null) continue;
            if (!playerId.equals(card.getOwner())) continue;
            Zone zone = card.getZone();
            if (zone == null || !zone.isInPlay()) continue;
            String title = card.getTitle();
            if (title != null && title.toLowerCase(Locale.ROOT).contains("vader")
                && card.getBlueprint() != null
                && card.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
                return true;
            }
        }
        return false;
    }

    /**
     * V25: Check if a character card is Vader (any version).
     */
    public static boolean isVaderCard(SwccgCardBlueprint bp) {
        if (bp == null) return false;
        if (bp.getCardCategory() != CardCategory.CHARACTER) return false;
        String title = bp.getTitle();
        return title != null && title.toLowerCase(Locale.ROOT).contains("vader");
    }

    /**
     * V25: Check if a character is an ISB agent based on lore.
     * ISB Operations makes characters with 'ISB', 'Rebel', or 'Rebellion' in lore into ISB agents.
     */
    public static boolean isISBAgentByLore(PhysicalCard card) {
        if (card == null) return false;
        SwccgCardBlueprint bp = card.getBlueprint();
        if (bp == null || bp.getCardCategory() != CardCategory.CHARACTER) return false;
        String lore = bp.getLore();
        if (lore == null) return false;
        String loreLower = lore.toLowerCase(Locale.ROOT);
        return loreLower.contains("isb") || loreLower.contains("rebel") || loreLower.contains("rebellion");
    }

    /**
     * V25: Count ISB agents currently on table for a player.
     */
    public int countISBAgentsOnTable(GameState gameState, String playerId) {
        if (gameState == null || playerId == null) return 0;
        int count = 0;
        for (PhysicalCard card : gameState.getAllPermanentCards()) {
            if (card == null) continue;
            if (!playerId.equals(card.getOwner())) continue;
            Zone zone = card.getZone();
            if (zone == null || !zone.isInPlay()) continue;
            if (isISBAgentByLore(card)) {
                count++;
            }
        }
        return count;
    }

    public void reset() {
        flipConditionLocations.clear();
        flipConditionLocationFragments.clear();
        requiredCardsOnTable.clear();
        pullableCards.clear();
        flipBackLocations.clear();
        flipBackLocationFragments.clear();
        flipBackConditionText = null;
        flipBackRequiresOccupy = false;
        flipBackRequiresControl = false;
        flipConditionText = null;
        objectiveTitle = null;
        objectiveBlueprintId = null;
        analyzed = false;
        isFlipped = false;
        requiresOccupy = false;
        requiresControl = false;
        // V25: ISB Operations
        isISBOperations = false;
        isbFlipAgentCount = 0;
        isbFlipLocationCount = 0;
        isbFlipBackNoAgents = false;
        // V25: Hunt Down V
        isHuntDownV = false;
        huntDownNeedsVader = false;
        huntDownFlipBackNoVader = false;
    }

    /**
     * V25: Convert English number words to integers (for parsing "four ISB agents").
     */
    private static int wordToNumber(String word) {
        if (word == null) return 0;
        switch (word.toLowerCase(Locale.ROOT)) {
            case "one": case "1": return 1;
            case "two": case "2": return 2;
            case "three": case "3": return 3;
            case "four": case "4": return 4;
            case "five": case "5": return 5;
            case "six": case "6": return 6;
            case "seven": case "7": return 7;
            case "eight": case "8": return 8;
            default:
                try { return Integer.parseInt(word); }
                catch (NumberFormatException e) { return 0; }
        }
    }

    private PhysicalCard findOurObjective(GameState gameState, String playerId) {
        for (PhysicalCard card : gameState.getAllPermanentCards()) {
            if (card == null) continue;
            if (!playerId.equals(card.getOwner())) continue;

            Zone zone = card.getZone();
            if (zone == null || !zone.isInPlay()) continue;

            SwccgCardBlueprint bp = card.getBlueprint();
            if (bp != null && bp.getCardCategory() == CardCategory.OBJECTIVE) {
                return card;
            }
        }
        return null;
    }

    private void updateFlipStatus(PhysicalCard objectiveCard) {
        try {
            this.isFlipped = objectiveCard.isFlipped();
            LOG.debug("[ObjectiveAnalyzer] Objective flipped = {}", isFlipped);
        } catch (Exception e) {
            LOG.debug("[ObjectiveAnalyzer] Could not determine flip status: {}", e.getMessage());
        }
    }

    private void parseGameText(String gameText) {
        flipConditionLocations.clear();
        flipConditionLocationFragments.clear();
        requiredCardsOnTable.clear();
        pullableCards.clear();
        flipBackLocations.clear();
        flipBackLocationFragments.clear();
        flipBackConditionText = null;
        flipBackRequiresOccupy = false;
        flipBackRequiresControl = false;

        parseFlipCondition(gameText);
        parsePullableCards(gameText);
        parseLocationReferences(gameText);
        parseBackSideText(gameText);
    }

    /**
     * V21: Scan full objective text for planet/location references.
     * Catches things like "Cloud City battleground site" that appear
     * outside the flip condition but indicate where the deck operates.
     */
    private void parseLocationReferences(String gameText) {
        // Pattern: "[Planet Name] battleground/site/location/system"
        // or "[Planet Name]: [Site Name]" format
        Pattern planetRefPattern = Pattern.compile(
            "([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*)\\s+(?:battleground|site|location|system)",
            Pattern.CASE_INSENSITIVE);
        Matcher matcher = planetRefPattern.matcher(gameText);
        while (matcher.find()) {
            String planet = matcher.group(1).trim().toLowerCase(Locale.ROOT);
            if (!isGenericWord(planet) && planet.length() >= 3) {
                if (!flipConditionLocationFragments.contains(planet)) {
                    addLocationFragment(planet);
                    LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] Location ref from game text: '{}'", planet);
                }
            }
        }

        // Also catch "Site: Name" patterns like "Cloud City: Dining Room"
        Pattern colonPattern = Pattern.compile(
            "([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*):");
        Matcher colonMatcher = colonPattern.matcher(gameText);
        while (colonMatcher.find()) {
            String prefix = colonMatcher.group(1).trim().toLowerCase(Locale.ROOT);
            if (!isGenericWord(prefix) && prefix.length() >= 3) {
                if (!flipConditionLocationFragments.contains(prefix)) {
                    addLocationFragment(prefix);
                    LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] Colon-prefix location: '{}'", prefix);
                }
            }
        }
    }

    private void parseFlipCondition(String gameText) {
        Matcher flipMatcher = FLIP_PATTERN.matcher(gameText);
        if (!flipMatcher.find()) {
            LOG.warn("[ObjectiveAnalyzer] No 'Flip this card if' found in game text");
            return;
        }

        flipConditionText = flipMatcher.group(1).trim();
        LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] Flip condition: '{}'", flipConditionText);
        String condLower = flipConditionText.toLowerCase(Locale.ROOT);

        if (condLower.contains("occupy")) {
            requiresOccupy = true;
            extractLocationsFromCondition(flipConditionText, OCCUPY_LOCATION_PATTERN);
        }
        if (condLower.contains("control")) {
            requiresControl = true;
            extractLocationsFromCondition(flipConditionText, CONTROL_LOCATION_PATTERN);
        }

        Matcher onTableMatcher = ON_TABLE_PATTERN.matcher(flipConditionText);
        while (onTableMatcher.find()) {
            String cardName = cleanCardName(onTableMatcher.group(1));
            if (cardName != null && !cardName.isEmpty()) {
                requiredCardsOnTable.add(cardName.toLowerCase(Locale.ROOT));
                LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] Required on table: '{}'", cardName);
            }
        }

        // V25: ISB Operations — detect "four ISB agents are on table" or
        // "ISB agents control at least two Rebel Base locations"
        if (condLower.contains("isb agent")) {
            isISBOperations = true;
            LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] V25: ISB Operations objective detected!");

            // Parse "four ISB agents are on table" → need 4 agents
            Pattern isbCountPattern = Pattern.compile(
                "(\\w+)\\s+ISB\\s+agents?\\s+(?:are\\s+)?on\\s+table", Pattern.CASE_INSENSITIVE);
            Matcher isbCountMatcher = isbCountPattern.matcher(flipConditionText);
            if (isbCountMatcher.find()) {
                isbFlipAgentCount = wordToNumber(isbCountMatcher.group(1));
                LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] V25: Need {} ISB agents on table to flip", isbFlipAgentCount);
            }

            // Parse "ISB agents control at least two Rebel Base locations"
            Pattern isbLocPattern = Pattern.compile(
                "ISB\\s+agents?\\s+control\\s+(?:at\\s+least\\s+)?(\\w+)\\s+(.+?)\\s+locations?",
                Pattern.CASE_INSENSITIVE);
            Matcher isbLocMatcher = isbLocPattern.matcher(flipConditionText);
            if (isbLocMatcher.find()) {
                isbFlipLocationCount = wordToNumber(isbLocMatcher.group(1));
                String locType = isbLocMatcher.group(2).trim().toLowerCase(Locale.ROOT);
                LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] V25: Need ISB agents controlling {} {} locations",
                    isbFlipLocationCount, locType);
                // Add "rebel base" as a location fragment so we prioritize those locations
                if (locType.contains("rebel base")) {
                    addLocationFragment("rebel base");
                }
            }
        }

        // V25: Hunt Down V — detect "Vader is at a battleground site" flip condition
        // The flip condition text is: "Vader is at a battleground site unless Luke, a Jedi, or a Padawan at a battleground site"
        if (condLower.contains("vader") && condLower.contains("battleground")) {
            isHuntDownV = true;
            huntDownNeedsVader = true;
            LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] V25: Hunt Down V objective detected! Vader MUST be deployed to flip.");
        }

        if (!requiresOccupy && !requiresControl && !isISBOperations && !isHuntDownV) {
            extractLocationsDirectly(flipConditionText);
        }
    }

    private void extractLocationsFromCondition(String conditionText, Pattern pattern) {
        String[] parts = conditionText.split("\\s+and\\s+");

        for (String part : parts) {
            Matcher matcher = pattern.matcher(part);
            if (matcher.find()) {
                String locText = matcher.group(1).trim();
                addLocationFromText(locText);
            } else {
                extractLocationsDirectly(part);
            }
        }
    }

    private void extractLocationsDirectly(String text) {
        Pattern systemPattern = Pattern.compile(
            "([A-Z][\\w\\s']+?)\\s+(?:S|s)ystem", Pattern.CASE_INSENSITIVE);
        Matcher systemMatcher = systemPattern.matcher(text);
        while (systemMatcher.find()) {
            String systemName = systemMatcher.group(1).trim();
            addLocationFromText(systemName + " System");
            addLocationFragment(systemName.toLowerCase(Locale.ROOT));
        }

        Pattern sitePattern = Pattern.compile(
            "([A-Z][\\w\\s']+?:\\s*[A-Z][\\w\\s'()]+)");
        Matcher siteMatcher = sitePattern.matcher(text);
        while (siteMatcher.find()) {
            addLocationFromText(siteMatcher.group(1).trim());
        }

        Pattern planetLocPattern = Pattern.compile(
            "(?:at |related to |to )([A-Z][\\w\\s']+?)\\s+(?:locations?|sites?|battlegrounds?)",
            Pattern.CASE_INSENSITIVE);
        Matcher planetLocMatcher = planetLocPattern.matcher(text);
        while (planetLocMatcher.find()) {
            String planet = planetLocMatcher.group(1).trim();
            if (!isGenericWord(planet)) {
                addLocationFragment(planet.toLowerCase(Locale.ROOT));
                LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] Planet location group: '{}'", planet);
            }
        }
    }

    private void parsePullableCards(String gameText) {
        String frontText = gameText;
        int backIdx = gameText.indexOf("[Back Side]");
        if (backIdx > 0) {
            frontText = gameText.substring(0, backIdx);
        }
        int backslashIdx = gameText.indexOf("\\[Back Side]");
        if (backslashIdx > 0 && backslashIdx < frontText.length()) {
            frontText = gameText.substring(0, backslashIdx);
        }

        Matcher takeMatcher = TAKE_FROM_RESERVE_PATTERN.matcher(frontText);
        while (takeMatcher.find()) {
            String cardList = takeMatcher.group(1).trim();
            String[] cards = cardList.split("\\s*(?:,|\\bor\\b)\\s*");
            for (String cardName : cards) {
                String cleaned = cleanCardName(cardName.trim());
                if (cleaned != null && !cleaned.isEmpty() && !isGenericWord(cleaned)) {
                    pullableCards.add(cleaned.toLowerCase(Locale.ROOT));
                    LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] Pullable from Reserve: '{}'", cleaned);
                }
            }
        }
    }

    private void addLocationFromText(String locationText) {
        if (locationText == null || locationText.isEmpty()) return;

        String locLower = locationText.toLowerCase(Locale.ROOT);
        if (isGenericWord(locLower)) return;

        flipConditionLocations.add(locLower);
        LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] Flip location (exact): '{}'", locationText);

        if (locLower.contains(":")) {
            String[] parts = locLower.split(":\\s*");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty() && !isGenericWord(trimmed)) {
                    addLocationFragment(trimmed);
                }
            }
        } else if (locLower.endsWith(" system")) {
            String planet = locLower.replace(" system", "").trim();
            addLocationFragment(planet);
        } else {
            // Strip leading numbers (e.g. "3 Bespin locations" -> "Bespin locations")
            String cleaned = locLower.replaceFirst("^\\d+\\s+", "");
            // Strip trailing generic location words
            cleaned = cleaned.replaceAll("\\s+(?:locations?|sites?|battlegrounds?|systems?)\\s*$", "").trim();
            if (!cleaned.isEmpty() && !isGenericWord(cleaned)) {
                addLocationFragment(cleaned);
                LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] Cleaned fragment: '{}' (from '{}')", cleaned, locLower);
            }
        }
    }

    private void addLocationFragment(String fragment) {
        if (fragment != null && !fragment.isEmpty() && fragment.length() >= 3) {
            flipConditionLocationFragments.add(fragment);
            // Cloud City is on Bespin - if one is referenced, add the other
            if (fragment.equals("bespin") && !flipConditionLocationFragments.contains("cloud city")) {
                flipConditionLocationFragments.add("cloud city");
                LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] Auto-added 'cloud city' (Bespin planet)");
            } else if (fragment.equals("cloud city") && !flipConditionLocationFragments.contains("bespin")) {
                flipConditionLocationFragments.add("bespin");
                LOG.warn("\uD83C\uDFAF [ObjectiveAnalyzer] Auto-added 'bespin' (Cloud City planet)");
            }
        }
    }

    private String cleanCardName(String name) {
        if (name == null) return null;
        name = name.trim();
        name = name.replace("\u2022", "").trim();
        name = name.replaceFirst("^(?:a |an |the )(?=[A-Z])", "");
        return name;
    }

    private boolean isGenericWord(String word) {
        if (word == null) return true;
        String lower = word.toLowerCase(Locale.ROOT).trim();
        Set<String> generic = new HashSet<>(Arrays.asList(
            "you", "your", "opponent", "opponent's", "that", "this", "the",
            "a", "an", "at", "to", "from", "with", "two", "three", "four",
            "battleground", "battlegrounds", "site", "sites", "location", "locations",
            "system", "systems", "character", "characters", "card", "cards",
            "least", "more", "each", "all", "any", "where", "there",
            "if", "and", "or", "not", "no", "is", "are", "has", "have",
            "dark", "light", "side", "force"
        ));
        return generic.contains(lower) || lower.length() < 3;
    }

    /**
     * V22.2: Parse the back side of the objective card to understand flip-back conditions.
     * SWCCG objectives have two sides separated by "[Back Side]" or "\\[Back Side]" in game text.
     * The back side tells us what conditions would cause the objective to flip BACK —
     * which means we lose our advantage. We need to prevent that.
     *
     * Common flip-back patterns:
     *   "Flip this card if opponent controls [locations]"
     *   "Flip this card if you do not occupy [locations]"
     *   "Place out of play if [condition]"
     */
    private void parseBackSideText(String gameText) {
        if (gameText == null) return;

        // Find the back side text
        String backText = null;
        int backIdx = gameText.indexOf("[Back Side]");
        if (backIdx >= 0) {
            backText = gameText.substring(backIdx + "[Back Side]".length()).trim();
        } else {
            backIdx = gameText.indexOf("\\[Back Side]");
            if (backIdx >= 0) {
                backText = gameText.substring(backIdx + "\\[Back Side]".length()).trim();
            }
        }

        if (backText == null || backText.isEmpty()) {
            LOG.warn("[ObjectiveAnalyzer] No [Back Side] text found — single-sided objective?");
            return;
        }

        LOG.warn("\uD83D\uDEE1 [ObjectiveAnalyzer] Back side text: {}", backText);

        // Look for flip-back conditions: "Flip this card if..."
        Matcher flipBackMatcher = FLIP_PATTERN.matcher(backText);
        if (flipBackMatcher.find()) {
            flipBackConditionText = flipBackMatcher.group(1).trim();
            LOG.warn("\uD83D\uDEE1 [ObjectiveAnalyzer] FLIP-BACK condition: '{}'", flipBackConditionText);

            String condLower = flipBackConditionText.toLowerCase(Locale.ROOT);

            // Parse what the opponent must do to flip us back
            // "opponent controls" = they need to control our locations
            // "you do not occupy" = we need to keep occupying
            if (condLower.contains("do not occupy") || condLower.contains("don't occupy")
                    || condLower.contains("does not occupy") || condLower.contains("doesn't occupy")) {
                flipBackRequiresOccupy = true;
                LOG.warn("\uD83D\uDEE1 [ObjectiveAnalyzer] Flip-back if WE DON'T OCCUPY locations");
                extractFlipBackLocations(flipBackConditionText, OCCUPY_LOCATION_PATTERN);
            }
            if (condLower.contains("do not control") || condLower.contains("don't control")
                    || condLower.contains("does not control") || condLower.contains("doesn't control")) {
                flipBackRequiresControl = true;
                LOG.warn("\uD83D\uDEE1 [ObjectiveAnalyzer] Flip-back if WE DON'T CONTROL locations");
                extractFlipBackLocations(flipBackConditionText, CONTROL_LOCATION_PATTERN);
            }
            // V25: ISB Operations back side — "no ISB agents are on table"
            if (condLower.contains("no isb agent") || condLower.contains("no isb agents")) {
                isbFlipBackNoAgents = true;
                LOG.warn("\uD83D\uDEE1 [ObjectiveAnalyzer] V25: Flips BACK if no ISB agents on table — must maintain ISB presence!");
            }

            // V25: Hunt Down V back side — "Vader not on table"
            if (condLower.contains("vader not on table") || condLower.contains("vader is not on table")) {
                huntDownFlipBackNoVader = true;
                LOG.warn("\uD83D\uDEE1 [ObjectiveAnalyzer] V25: Hunt Down V flips BACK if Vader not on table — Vader is critical!");
            }

            // Also check for "opponent controls X" which implies we must defend X
            if (condLower.contains("opponent controls") || condLower.contains("opponent occupies")) {
                flipBackRequiresControl = true;
                LOG.warn("\uD83D\uDEE1 [ObjectiveAnalyzer] Flip-back if OPPONENT CONTROLS our locations");
                extractFlipBackLocations(flipBackConditionText, CONTROL_LOCATION_PATTERN);
                extractFlipBackLocations(flipBackConditionText, OCCUPY_LOCATION_PATTERN);
            }

            // If we found flip-back conditions but no specific locations,
            // assume the same locations as the front side flip conditions
            if (flipBackLocations.isEmpty() && flipBackLocationFragments.isEmpty()) {
                flipBackLocations.addAll(flipConditionLocations);
                flipBackLocationFragments.addAll(flipConditionLocationFragments);
                LOG.warn("\uD83D\uDEE1 [ObjectiveAnalyzer] No specific flip-back locations - using front-side locations as protection targets");
            }
        } else {
            LOG.warn("[ObjectiveAnalyzer] No 'Flip this card if' found on back side");
            // Even without explicit flip-back conditions, protect the same locations
            // Most objectives flip back if you lose presence at key locations
            flipBackLocations.addAll(flipConditionLocations);
            flipBackLocationFragments.addAll(flipConditionLocationFragments);
            LOG.warn("\uD83D\uDEE1 [ObjectiveAnalyzer] Defaulting flip-back protection to front-side locations");
        }

        // Also scan back side for additional location references we might have missed
        parseBackSideLocationReferences(backText);
    }

    /**
     * Extract locations from flip-back condition text.
     */
    private void extractFlipBackLocations(String conditionText, Pattern pattern) {
        String[] parts = conditionText.split("\\s+and\\s+");
        for (String part : parts) {
            Matcher matcher = pattern.matcher(part);
            if (matcher.find()) {
                String locText = matcher.group(1).trim();
                addFlipBackLocation(locText);
            }
        }
        // Also try direct extraction
        extractFlipBackLocationsDirectly(conditionText);
    }

    /**
     * Direct location extraction for flip-back conditions (same approach as front side).
     */
    private void extractFlipBackLocationsDirectly(String text) {
        Pattern planetLocPattern = Pattern.compile(
            "(?:at |related to |to )([A-Z][\\w\\s']+?)\\s+(?:locations?|sites?|battlegrounds?)",
            Pattern.CASE_INSENSITIVE);
        Matcher matcher = planetLocPattern.matcher(text);
        while (matcher.find()) {
            String planet = matcher.group(1).trim();
            if (!isGenericWord(planet)) {
                String planetLower = planet.toLowerCase(Locale.ROOT);
                flipBackLocationFragments.add(planetLower);
                // Auto-link Cloud City <-> Bespin
                if (planetLower.equals("bespin") && !flipBackLocationFragments.contains("cloud city")) {
                    flipBackLocationFragments.add("cloud city");
                } else if (planetLower.equals("cloud city") && !flipBackLocationFragments.contains("bespin")) {
                    flipBackLocationFragments.add("bespin");
                }
                LOG.warn("\uD83D\uDEE1 [ObjectiveAnalyzer] Flip-back protection planet: '{}'", planet);
            }
        }
    }

    /**
     * Scan back side text for additional location references.
     */
    private void parseBackSideLocationReferences(String backText) {
        // Look for "Cloud City" or other location planet references
        Pattern planetRefPattern = Pattern.compile(
            "([A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*)\\s+(?:battleground|site|location|system)",
            Pattern.CASE_INSENSITIVE);
        Matcher matcher = planetRefPattern.matcher(backText);
        while (matcher.find()) {
            String planet = matcher.group(1).trim().toLowerCase(Locale.ROOT);
            if (!isGenericWord(planet) && planet.length() >= 3) {
                if (!flipBackLocationFragments.contains(planet)) {
                    flipBackLocationFragments.add(planet);
                    // Auto-link Cloud City <-> Bespin
                    if (planet.equals("bespin") && !flipBackLocationFragments.contains("cloud city")) {
                        flipBackLocationFragments.add("cloud city");
                    } else if (planet.equals("cloud city") && !flipBackLocationFragments.contains("bespin")) {
                        flipBackLocationFragments.add("bespin");
                    }
                    LOG.warn("\uD83D\uDEE1 [ObjectiveAnalyzer] Back-side location ref: '{}'", planet);
                }
            }
        }
    }

    private void addFlipBackLocation(String locationText) {
        if (locationText == null || locationText.isEmpty()) return;
        String locLower = locationText.toLowerCase(Locale.ROOT);
        if (isGenericWord(locLower)) return;

        flipBackLocations.add(locLower);
        LOG.warn("\uD83D\uDEE1 [ObjectiveAnalyzer] Flip-back location (exact): '{}'", locationText);

        // Extract fragments just like addLocationFromText does
        if (locLower.contains(":")) {
            String[] parts = locLower.split(":\\s*");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty() && !isGenericWord(trimmed)) {
                    flipBackLocationFragments.add(trimmed);
                    // Auto-link Cloud City <-> Bespin
                    if (trimmed.equals("bespin") && !flipBackLocationFragments.contains("cloud city")) {
                        flipBackLocationFragments.add("cloud city");
                    } else if (trimmed.equals("cloud city") && !flipBackLocationFragments.contains("bespin")) {
                        flipBackLocationFragments.add("bespin");
                    }
                }
            }
        } else {
            String cleaned = locLower.replaceFirst("^\\d+\\s+", "");
            cleaned = cleaned.replaceAll("\\s+(?:locations?|sites?|battlegrounds?|systems?)\\s*$", "").trim();
            if (!cleaned.isEmpty() && !isGenericWord(cleaned)) {
                flipBackLocationFragments.add(cleaned);
                if (cleaned.equals("bespin") && !flipBackLocationFragments.contains("cloud city")) {
                    flipBackLocationFragments.add("cloud city");
                } else if (cleaned.equals("cloud city") && !flipBackLocationFragments.contains("bespin")) {
                    flipBackLocationFragments.add("bespin");
                }
            }
        }
    }

    /**
     * V22.2: Check if a location is relevant for flip-back protection (post-flip defense).
     * After objective flips, these are the locations we MUST hold to prevent flip-back.
     */
    public boolean isFlipBackProtectionLocation(String locationTitle) {
        if (!analyzed || locationTitle == null) return false;
        String titleLower = locationTitle.toLowerCase(Locale.ROOT);

        if (flipBackLocations.contains(titleLower)) return true;

        for (String fragment : flipBackLocationFragments) {
            if (titleLower.contains(fragment)) return true;
        }

        // Fallback: if no specific flip-back locations found, use front-side locations
        if (flipBackLocations.isEmpty() && flipBackLocationFragments.isEmpty()) {
            return isObjectiveRelevantLocation(locationTitle);
        }

        return false;
    }

    /**
     * V22.2: Get the set of location fragments that need protection post-flip.
     */
    public Set<String> getFlipBackLocationFragments() {
        return Collections.unmodifiableSet(flipBackLocationFragments);
    }

    /**
     * V22.2: Does the objective flip back if we don't occupy locations?
     */
    public boolean flipBackRequiresOccupy() { return flipBackRequiresOccupy; }

    /**
     * V22.2: Does the objective flip back if we don't control locations?
     */
    public boolean flipBackRequiresControl() { return flipBackRequiresControl; }

    /**
     * V22.2: Get the raw flip-back condition text for logging.
     */
    public String getFlipBackConditionText() { return flipBackConditionText; }

    /**
     * V22.5: Check if this objective references Bespin/Cloud City.
     * If so, occupying Bespin system with a ship is critical for enabling
     * Dark Deal and Cloud City Occupation effects.
     */
    public boolean needsBespinSystemPresence() {
        if (!analyzed) return false;
        return flipConditionLocationFragments.contains("bespin") ||
               flipConditionLocationFragments.contains("cloud city") ||
               flipBackLocationFragments.contains("bespin") ||
               flipBackLocationFragments.contains("cloud city");
    }

    private void logAnalysisResults() {
        LOG.warn("\uD83C\uDFAF ===================================================================");
        LOG.warn("\uD83C\uDFAF OBJECTIVE ANALYSIS COMPLETE: '{}'", objectiveTitle);
        LOG.warn("\uD83C\uDFAF ===================================================================");
        LOG.warn("\uD83C\uDFAF Flip condition: '{}'", flipConditionText != null ? flipConditionText : "NONE FOUND");
        LOG.warn("\uD83C\uDFAF Requires occupy: {}, control: {}", requiresOccupy, requiresControl);
        LOG.warn("\uD83C\uDFAF Flip locations (exact): {}", flipConditionLocations);
        LOG.warn("\uD83C\uDFAF Flip location fragments: {}", flipConditionLocationFragments);
        LOG.warn("\uD83C\uDFAF Required on table: {}", requiredCardsOnTable);
        LOG.warn("\uD83C\uDFAF Pullable from reserve: {}", pullableCards);
        LOG.warn("\uD83C\uDFAF Currently flipped: {}", isFlipped);
        LOG.warn("\uD83D\uDEE1 --- FLIP-BACK PROTECTION (V22.2) ---");
        LOG.warn("\uD83D\uDEE1 Flip-back condition: '{}'", flipBackConditionText != null ? flipBackConditionText : "NONE/DEFAULT");
        LOG.warn("\uD83D\uDEE1 Flip-back requires occupy: {}, control: {}", flipBackRequiresOccupy, flipBackRequiresControl);
        LOG.warn("\uD83D\uDEE1 Flip-back locations (exact): {}", flipBackLocations);
        LOG.warn("\uD83D\uDEE1 Flip-back location fragments: {}", flipBackLocationFragments);
        if (isISBOperations) {
            LOG.warn("\uD83D\uDD75 --- ISB OPERATIONS (V25) ---");
            LOG.warn("\uD83D\uDD75 ISB agents needed on table to flip: {}", isbFlipAgentCount);
            LOG.warn("\uD83D\uDD75 Rebel Base locations to control: {}", isbFlipLocationCount);
            LOG.warn("\uD83D\uDD75 Flips back if no ISB agents: {}", isbFlipBackNoAgents);
        }
        if (isHuntDownV) {
            LOG.warn("\u2694 --- HUNT DOWN V (V25) ---");
            LOG.warn("\u2694 Vader needed at battleground to flip: {}", huntDownNeedsVader);
            LOG.warn("\u2694 Flips back if Vader not on table: {}", huntDownFlipBackNoVader);
        }
        LOG.warn("\uD83C\uDFAF ===================================================================");
    }
}
