package com.gempukku.swccgo.bot;

import com.gempukku.swccgo.db.BotStatsDAO;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.Consumer;

/**
 * Checks and awards achievements during bot games.
 *
 * This class tracks game state and awards achievements based on various triggers:
 * - Cards appearing in play (checked in real-time as cards enter play)
 * - Card combinations at locations (checked when cards move or enter play)
 * - Cards killed (checked when cards are removed from play)
 * - Game-end statistics (damage, route score, etc.)
 * - Meta achievements (games played, total score)
 * - Holiday achievements (during Life Day / December)
 *
 * Real-time achievement checking provides immediate feedback to players,
 * sending chat messages as soon as achievements are unlocked.
 */
public class AchievementChecker {
    private static final Logger LOG = LogManager.getLogger(AchievementChecker.class);

    private final BotStatsDAO _statsDAO;

    // Per-game tracking
    private final Set<String> _cardsSeen = new HashSet<>();
    private final Set<String> _cardsOnBoardPreviously = new HashSet<>();
    private final Set<Integer> _achievementsTriggeredThisGame = new HashSet<>();
    private int _battlesInitiated = 0;
    private int _battlesWon = 0;
    private int _holidayGamesThisSeason = 0;

    // Real-time tracking
    private int _playerId = -1;
    private String _humanPlayerId = null;
    private String _botPlayerId = null;

    public AchievementChecker(BotStatsDAO statsDAO) {
        _statsDAO = statsDAO;
    }

    /**
     * Initialize the checker with player information for real-time checking.
     *
     * @param playerId the human player's database ID
     * @param humanPlayerId the human player's game ID (username)
     * @param botPlayerId the bot's player ID (e.g., "~Rando_Cal")
     */
    public void initializeForGame(int playerId, String humanPlayerId, String botPlayerId) {
        _playerId = playerId;
        _humanPlayerId = humanPlayerId;
        _botPlayerId = botPlayerId;
        resetForGame();
        LOG.debug("AchievementChecker initialized for player {} (ID: {})", humanPlayerId, playerId);
    }

    /**
     * Check if the checker has been initialized with player info.
     */
    public boolean isInitialized() {
        return _playerId >= 0 && _humanPlayerId != null;
    }

    /**
     * Reset tracking state for a new game.
     */
    public void resetForGame() {
        _cardsSeen.clear();
        _cardsOnBoardPreviously.clear();
        _achievementsTriggeredThisGame.clear();
        _battlesInitiated = 0;
        _battlesWon = 0;
    }

    /**
     * Check if a holiday (Life Day) is currently active.
     * Life Day is active during December.
     */
    public boolean isHolidayActive() {
        LocalDate today = LocalDate.now();
        return today.getMonth() == Month.DECEMBER;
    }

    /**
     * Check board state for card-based achievements.
     *
     * @param playerId the player's database ID
     * @param cardsOnBoard map of card title (lowercase) to card type
     * @param myCards set of card titles belonging to the bot
     * @param theirCards set of card titles belonging to the opponent
     * @param cardsByLocation map of location name to list of card titles at that location
     * @return list of newly unlocked achievement quotes to send as messages
     */
    public List<String> checkBoardState(int playerId,
                                        Map<String, String> cardsOnBoard,
                                        Set<String> myCards,
                                        Set<String> theirCards,
                                        Map<String, List<String>> cardsByLocation) {
        List<String> messages = new ArrayList<>();

        // Check single card achievements
        for (Map.Entry<String, String> entry : cardsOnBoard.entrySet()) {
            String cardTitle = entry.getKey().toLowerCase();
            String cardType = entry.getValue();

            messages.addAll(checkSingleCard(playerId, cardTitle, cardType, myCards, theirCards));
        }

        // Check card combinations at locations
        messages.addAll(checkCardCombos(playerId, cardsByLocation));

        // Check for killed cards (was on board, now isn't)
        Set<String> currentTitles = new HashSet<>();
        for (String title : cardsOnBoard.keySet()) {
            currentTitles.add(title.toLowerCase());
        }
        messages.addAll(checkKilledCards(playerId, currentTitles, cardsOnBoard));

        // Check holiday-specific achievements
        if (isHolidayActive()) {
            messages.addAll(checkHolidayBoardAchievements(playerId, cardsOnBoard, cardsByLocation));
        }

        // Update tracking
        _cardsOnBoardPreviously.clear();
        _cardsOnBoardPreviously.addAll(currentTitles);
        _cardsSeen.addAll(currentTitles);

        return messages;
    }

    // =========================================================================
    // Real-Time Achievement Checking Methods
    // These are called by BotStatsGameStateListener as game events occur
    // =========================================================================

    /**
     * Check achievements when a single card enters play.
     * Called in real-time by the GameStateListener.
     *
     * @param card the card that entered play
     * @param gameState the current game state
     * @return list of achievement messages to send
     */
    public List<String> onCardEntersPlay(PhysicalCard card, GameState gameState) {
        if (!isInitialized()) {
            return Collections.emptyList();
        }

        List<String> messages = new ArrayList<>();
        String cardTitle = card.getTitle();
        if (cardTitle == null || cardTitle.isEmpty()) {
            return messages;
        }

        String cardTitleLower = cardTitle.toLowerCase();
        String cardType = card.getBlueprint().getCardCategory().name();
        boolean isHumanCard = _humanPlayerId.equals(card.getOwner());
        boolean isBotCard = _botPlayerId.equals(card.getOwner());

        LOG.debug("Card enters play: '{}' (type: {}, owner: {})", cardTitle, cardType, card.getOwner());

        // Track the card
        _cardsSeen.add(cardTitleLower);
        _cardsOnBoardPreviously.add(cardTitleLower);

        // Check single card achievements
        for (Achievement ach : Achievement.values()) {
            Achievement.TriggerType trigger = ach.getTriggerType();
            if (trigger != Achievement.TriggerType.CARD_IN_PLAY &&
                trigger != Achievement.TriggerType.MY_CARD &&
                trigger != Achievement.TriggerType.THEIR_CARD &&
                trigger != Achievement.TriggerType.HOLIDAY_CARD_IN_PLAY) {
                continue;
            }

            if (_achievementsTriggeredThisGame.contains(ach.getBitPosition())) {
                continue;
            }

            if (ach.getCardMatch() == null) {
                continue;
            }

            // Skip non-holiday achievements that have holiday trigger
            if (trigger == Achievement.TriggerType.HOLIDAY_CARD_IN_PLAY && !isHolidayActive()) {
                continue;
            }

            // Check if card matches
            if (!cardMatches(cardTitleLower, ach.getCardMatch(), ach.getCardType(), cardType)) {
                continue;
            }

            // Check ownership if required
            // Note: "my_card" means bot's card (from bot's perspective), "their_card" means human's card
            if (trigger == Achievement.TriggerType.MY_CARD) {
                if (!isBotCard) {
                    continue;
                }
            } else if (trigger == Achievement.TriggerType.THEIR_CARD) {
                if (!isHumanCard) {
                    continue;
                }
            }

            // Award achievement
            String msg = awardAchievement(_playerId, ach);
            if (msg != null) {
                LOG.info("Achievement unlocked for {}: {} - {}", _humanPlayerId, ach.getKey(), ach.getQuote());
                messages.add(msg);
            }
        }

        // Check card combinations at the card's location
        PhysicalCard atLocation = card.getAtLocation();
        if (atLocation != null) {
            messages.addAll(checkCardCombosAtLocation(atLocation, gameState));
        }

        return messages;
    }

    /**
     * Check achievements when a card moves to a new location.
     * Called in real-time by the GameStateListener.
     *
     * @param card the card that moved
     * @param gameState the current game state
     * @return list of achievement messages to send
     */
    public List<String> onCardMoved(PhysicalCard card, GameState gameState) {
        if (!isInitialized()) {
            return Collections.emptyList();
        }

        List<String> messages = new ArrayList<>();

        // Check card combinations at the new location
        PhysicalCard atLocation = card.getAtLocation();
        if (atLocation != null) {
            messages.addAll(checkCardCombosAtLocation(atLocation, gameState));
        }

        return messages;
    }

    /**
     * Check achievements when cards are removed from play.
     * Called in real-time by the GameStateListener.
     *
     * @param removedCards the cards that were removed
     * @param gameState the current game state
     * @return list of achievement messages to send
     */
    public List<String> onCardsRemoved(Collection<PhysicalCard> removedCards, GameState gameState) {
        if (!isInitialized()) {
            return Collections.emptyList();
        }

        // Debug log all removed cards to help diagnose false positive achievements
        if (removedCards != null && !removedCards.isEmpty()) {
            for (PhysicalCard card : removedCards) {
                String title = card != null ? card.getTitle() : "null";
                String zone = card != null && card.getZone() != null ? card.getZone().name() : "null";
                LOG.debug("onCardsRemoved: '{}' from zone {} (owner: {})",
                          title, zone, card != null ? card.getOwner() : "null");
            }
        }

        List<String> messages = new ArrayList<>();

        // Build set of current cards still in play for killed-by checks
        Set<String> currentCards = new HashSet<>();
        for (PhysicalCard card : gameState.getAllPermanentCards()) {
            if (card.getZone() != null && card.getZone().isInPlay() && card.getTitle() != null) {
                currentCards.add(card.getTitle().toLowerCase());
            }
        }

        for (PhysicalCard removed : removedCards) {
            String title = removed.getTitle();
            if (title == null || title.isEmpty()) {
                continue;
            }

            String titleLower = title.toLowerCase();
            _cardsOnBoardPreviously.remove(titleLower);

            // Check for card_killed achievements
            for (Achievement ach : Achievement.values()) {
                if (ach.getTriggerType() != Achievement.TriggerType.CARD_KILLED &&
                    ach.getTriggerType() != Achievement.TriggerType.CARD_KILLED_BY) {
                    continue;
                }

                if (_achievementsTriggeredThisGame.contains(ach.getBitPosition())) {
                    continue;
                }

                if (ach.getCardMatch() == null) {
                    continue;
                }

                // Check if this card matches the kill target
                if (!titleLower.contains(ach.getCardMatch())) {
                    continue;
                }

                // Log the match for debugging false positives
                LOG.info("CARD_KILLED match: card '{}' matches pattern '{}' for achievement {}",
                         title, ach.getCardMatch(), ach.getKey());

                // For CARD_KILLED_BY, check if killer is present
                if (ach.getTriggerType() == Achievement.TriggerType.CARD_KILLED_BY) {
                    List<String> killers = ach.getCards();
                    if (killers != null && !killers.isEmpty()) {
                        boolean killerPresent = false;
                        for (String killer : killers) {
                            for (String card : currentCards) {
                                if (card.contains(killer.toLowerCase())) {
                                    killerPresent = true;
                                    break;
                                }
                            }
                            if (killerPresent) break;
                        }
                        if (!killerPresent) {
                            continue;
                        }
                    }
                }

                String msg = awardAchievement(_playerId, ach);
                if (msg != null) {
                    LOG.info("Achievement unlocked for {}: {} - {}", _humanPlayerId, ach.getKey(), ach.getQuote());
                    messages.add(msg);
                }
            }
        }

        return messages;
    }

    /**
     * Check hand size achievements.
     * Called periodically (e.g., at phase changes) by the GameStateListener.
     *
     * @param handSize the player's current hand size
     * @return list of achievement messages to send
     */
    public List<String> onHandSizeCheck(int handSize) {
        if (!isInitialized()) {
            return Collections.emptyList();
        }

        List<String> messages = new ArrayList<>();

        if (handSize >= 8) {
            String msg = awardAchievement(_playerId, Achievement.COLLECTOR);
            if (msg != null) {
                LOG.info("Achievement unlocked for {}: {} (hand size: {})", _humanPlayerId, Achievement.COLLECTOR.getKey(), handSize);
                messages.add(msg);
            }
        }

        return messages;
    }

    /**
     * Check location control achievements.
     * Called periodically by the GameStateListener.
     *
     * @param locationsControlled number of locations the human player controls
     * @return list of achievement messages to send
     */
    public List<String> onLocationControlCheck(int locationsControlled) {
        if (!isInitialized()) {
            return Collections.emptyList();
        }

        List<String> messages = new ArrayList<>();

        if (locationsControlled >= 5) {
            String msg = awardAchievement(_playerId, Achievement.FORTRESS);
            if (msg != null) {
                LOG.info("Achievement unlocked for {}: {} (locations: {})", _humanPlayerId, Achievement.FORTRESS.getKey(), locationsControlled);
                messages.add(msg);
            }
        }

        return messages;
    }

    /**
     * Record a battle being initiated (by any player).
     */
    public void onBattleStarted() {
        _battlesInitiated++;
    }

    /**
     * Check achievements when a battle finishes.
     * Called in real-time by the GameStateListener.
     *
     * @param humanWonBattle whether the human player won this battle
     * @return list of achievement messages to send
     */
    public List<String> onBattleFinished(boolean humanWonBattle) {
        if (!isInitialized()) {
            return Collections.emptyList();
        }

        List<String> messages = new ArrayList<>();

        if (humanWonBattle) {
            _battlesWon++;

            // Check Blitzkrieg (3+ battles won)
            if (_battlesWon >= 3) {
                String msg = awardAchievement(_playerId, Achievement.BLITZKRIEG);
                if (msg != null) {
                    LOG.info("Achievement unlocked for {}: {} (battles won: {})", _humanPlayerId, Achievement.BLITZKRIEG.getKey(), _battlesWon);
                    messages.add(msg);
                }
            }
        }

        return messages;
    }

    /**
     * Check card combo achievements at a specific location.
     *
     * @param location the location to check
     * @param gameState the current game state
     * @return list of achievement messages
     */
    private List<String> checkCardCombosAtLocation(PhysicalCard location, GameState gameState) {
        List<String> messages = new ArrayList<>();
        String locationName = location.getTitle();
        if (locationName == null) {
            return messages;
        }

        String locationNameLower = locationName.toLowerCase();

        // Collect all cards at this location
        List<String> cardsHere = new ArrayList<>();
        for (PhysicalCard card : gameState.getAllPermanentCards()) {
            if (card.getZone() != null && card.getZone().isInPlay()) {
                PhysicalCard cardLocation = card.getAtLocation();
                if (cardLocation != null && cardLocation.getCardId() == location.getCardId()) {
                    if (card.getTitle() != null) {
                        cardsHere.add(card.getTitle().toLowerCase());
                    }
                }
            }
        }

        // Check combo achievements
        for (Achievement ach : Achievement.values()) {
            if (ach.getTriggerType() != Achievement.TriggerType.CARDS_TOGETHER &&
                ach.getTriggerType() != Achievement.TriggerType.CARDS_AT_SITE) {
                continue;
            }

            // Skip holiday achievements during non-holiday
            if (ach.isHolidayAchievement() && !isHolidayActive()) {
                continue;
            }

            if (_achievementsTriggeredThisGame.contains(ach.getBitPosition())) {
                continue;
            }

            List<String> requiredCards = ach.getCards();
            if (requiredCards == null || requiredCards.isEmpty()) {
                continue;
            }

            // For CARDS_AT_SITE, check the site filter
            String siteFilter = ach.getSiteFilter();
            int cardStartIndex = 0;
            if (ach.getTriggerType() == Achievement.TriggerType.CARDS_AT_SITE) {
                if (siteFilter != null && !locationNameLower.contains(siteFilter)) {
                    continue;
                }
                cardStartIndex = 1; // First element in cards list was the site filter
            }

            // Check if all required cards are present
            boolean foundAll = true;
            for (int i = cardStartIndex; i < requiredCards.size(); i++) {
                String required = requiredCards.get(i).toLowerCase();
                boolean found = false;
                for (String card : cardsHere) {
                    if (card.contains(required)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    foundAll = false;
                    break;
                }
            }

            if (foundAll) {
                String msg = awardAchievement(_playerId, ach);
                if (msg != null) {
                    LOG.info("Achievement unlocked for {}: {} (combo at {})", _humanPlayerId, ach.getKey(), locationName);
                    messages.add(msg);
                }
            }
        }

        return messages;
    }

    /**
     * Get the number of battles initiated this game.
     */
    public int getBattlesInitiated() {
        return _battlesInitiated;
    }

    /**
     * Get the number of battles won by the human player this game.
     */
    public int getBattlesWon() {
        return _battlesWon;
    }

    private List<String> checkSingleCard(int playerId, String cardTitle, String cardType,
                                         Set<String> myCards, Set<String> theirCards) {
        List<String> messages = new ArrayList<>();

        for (Achievement ach : Achievement.values()) {
            Achievement.TriggerType trigger = ach.getTriggerType();
            if (trigger != Achievement.TriggerType.CARD_IN_PLAY &&
                trigger != Achievement.TriggerType.MY_CARD &&
                trigger != Achievement.TriggerType.THEIR_CARD) {
                continue;
            }

            if (_achievementsTriggeredThisGame.contains(ach.getBitPosition())) {
                continue;
            }

            if (ach.getCardMatch() == null) {
                continue;
            }

            // Check if card matches
            if (!cardMatches(cardTitle, ach.getCardMatch(), ach.getCardType(), cardType)) {
                continue;
            }

            // Check ownership if required
            if (trigger == Achievement.TriggerType.MY_CARD) {
                if (!containsMatch(myCards, cardTitle)) {
                    continue;
                }
            } else if (trigger == Achievement.TriggerType.THEIR_CARD) {
                if (!containsMatch(theirCards, cardTitle)) {
                    continue;
                }
            }

            // Award achievement
            String msg = awardAchievement(playerId, ach);
            if (msg != null) {
                messages.add(msg);
            }
        }

        return messages;
    }

    private boolean cardMatches(String cardTitle, String cardMatch,
                                Achievement.CardType requiredType, String actualType) {
        // Basic contains check
        if (!cardTitle.contains(cardMatch)) {
            return false;
        }

        // Special case: "death star" should match the system cards, not the sites
        // System cards are "Death Star" or "Death Star II", sites have ":" like "Death Star: Docking Bay"
        if ("death star".equals(cardMatch) && cardTitle.contains(":")) {
            return false;
        }

        // Check type restriction if specified
        if (requiredType != null && actualType != null) {
            String reqLower = requiredType.name().toLowerCase();
            String actLower = actualType.toLowerCase();
            // Handle variations like "Character" vs "Characters"
            if (!actLower.startsWith(reqLower.substring(0, Math.min(4, reqLower.length())))) {
                return false;
            }
        }

        return true;
    }

    private boolean containsMatch(Set<String> cards, String match) {
        for (String card : cards) {
            if (card.toLowerCase().contains(match)) {
                return true;
            }
        }
        return false;
    }

    private List<String> checkCardCombos(int playerId, Map<String, List<String>> cardsByLocation) {
        List<String> messages = new ArrayList<>();

        for (Achievement ach : Achievement.values()) {
            if (ach.getTriggerType() != Achievement.TriggerType.CARDS_TOGETHER &&
                ach.getTriggerType() != Achievement.TriggerType.CARDS_AT_SITE) {
                continue;
            }

            if (_achievementsTriggeredThisGame.contains(ach.getBitPosition())) {
                continue;
            }

            List<String> requiredCards = ach.getCards();
            if (requiredCards == null || requiredCards.isEmpty()) {
                continue;
            }

            // For CARDS_AT_SITE, first card in list is the site filter
            String siteFilter = ach.getSiteFilter();
            int cardStartIndex = (ach.getTriggerType() == Achievement.TriggerType.CARDS_AT_SITE) ? 1 : 0;

            for (Map.Entry<String, List<String>> entry : cardsByLocation.entrySet()) {
                String locationName = entry.getKey().toLowerCase();
                List<String> cardsHere = entry.getValue();

                // Check site filter if required
                if (siteFilter != null && !locationName.contains(siteFilter)) {
                    continue;
                }

                // Check if all required cards are present
                boolean foundAll = true;
                for (int i = cardStartIndex; i < requiredCards.size(); i++) {
                    String required = requiredCards.get(i).toLowerCase();
                    boolean found = false;
                    for (String card : cardsHere) {
                        if (card.toLowerCase().contains(required)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        foundAll = false;
                        break;
                    }
                }

                if (foundAll) {
                    String msg = awardAchievement(playerId, ach);
                    if (msg != null) {
                        messages.add(msg);
                    }
                    break; // Only award once per achievement
                }
            }
        }

        return messages;
    }

    private List<String> checkKilledCards(int playerId, Set<String> currentCards,
                                          Map<String, String> cardsOnBoard) {
        List<String> messages = new ArrayList<>();

        // Find cards that were on board but now aren't
        Set<String> removed = new HashSet<>(_cardsOnBoardPreviously);
        removed.removeAll(currentCards);

        for (Achievement ach : Achievement.values()) {
            if (ach.getTriggerType() != Achievement.TriggerType.CARD_KILLED &&
                ach.getTriggerType() != Achievement.TriggerType.CARD_KILLED_BY) {
                continue;
            }

            if (_achievementsTriggeredThisGame.contains(ach.getBitPosition())) {
                continue;
            }

            if (ach.getCardMatch() == null) {
                continue;
            }

            // Check if the target card was removed
            boolean targetRemoved = false;
            for (String card : removed) {
                if (card.contains(ach.getCardMatch())) {
                    targetRemoved = true;
                    break;
                }
            }

            if (!targetRemoved) {
                continue;
            }

            // For CARD_KILLED_BY, check if killer is still present
            if (ach.getTriggerType() == Achievement.TriggerType.CARD_KILLED_BY) {
                List<String> killers = ach.getCards();
                if (killers != null && !killers.isEmpty()) {
                    boolean killerPresent = false;
                    for (String killer : killers) {
                        for (String card : currentCards) {
                            if (card.contains(killer.toLowerCase())) {
                                killerPresent = true;
                                break;
                            }
                        }
                        if (killerPresent) break;
                    }
                    if (!killerPresent) {
                        continue;
                    }
                }
            }

            String msg = awardAchievement(playerId, ach);
            if (msg != null) {
                messages.add(msg);
            }
        }

        return messages;
    }

    private List<String> checkHolidayBoardAchievements(int playerId,
                                                       Map<String, String> cardsOnBoard,
                                                       Map<String, List<String>> cardsByLocation) {
        List<String> messages = new ArrayList<>();

        // Check holiday card achievements (e.g., Boba Fett during Life Day)
        for (Achievement ach : Achievement.values()) {
            if (ach.getTriggerType() != Achievement.TriggerType.HOLIDAY_CARD_IN_PLAY) {
                continue;
            }

            if (!ach.isHolidayAchievement()) {
                continue;
            }

            if (_achievementsTriggeredThisGame.contains(ach.getBitPosition())) {
                continue;
            }

            if (ach.getCardMatch() == null) {
                continue;
            }

            // Check if the card is on the board
            for (String card : cardsOnBoard.keySet()) {
                if (card.toLowerCase().contains(ach.getCardMatch())) {
                    String msg = awardAchievement(playerId, ach);
                    if (msg != null) {
                        messages.add(msg);
                    }
                    break;
                }
            }
        }

        // Check Wookiee homecoming (Chewie at Kashyyyk)
        Achievement wookieeAch = Achievement.WOOKIEE_HOMECOMING;
        if (!_achievementsTriggeredThisGame.contains(wookieeAch.getBitPosition())) {
            for (Map.Entry<String, List<String>> entry : cardsByLocation.entrySet()) {
                String locationName = entry.getKey().toLowerCase();
                if (!locationName.contains("kashyyyk")) {
                    continue;
                }

                for (String card : entry.getValue()) {
                    if (card.toLowerCase().contains("chew")) {
                        String msg = awardAchievement(playerId, wookieeAch);
                        if (msg != null) {
                            messages.add(msg);
                        }
                        break;
                    }
                }
            }
        }

        return messages;
    }

    /**
     * Check achievements at game end.
     *
     * @param playerId the player's database ID
     * @param won whether the player won
     * @param routeScore the Astrogator route score
     * @param turns number of turns the game lasted
     * @param forceRemaining player's force pile at end
     * @param damage damage dealt during the game
     * @param gamesPlayed total games played (after this game)
     * @param totalAstScore total astrogation score (after this game)
     * @param achievementCount current achievement count
     * @return list of newly unlocked achievement quotes
     */
    public List<String> checkGameEnd(int playerId, boolean won, int routeScore, int turns,
                                     int forceRemaining, int damage, int gamesPlayed,
                                     int totalAstScore, int achievementCount) {
        List<String> messages = new ArrayList<>();

        // Damage achievement
        if (damage >= 60) {
            String msg = awardAchievement(playerId, Achievement.SIXTY_DAMAGE);
            if (msg != null) {
                messages.add(msg);
            }
        }

        if (won) {
            // Route score achievements
            if (routeScore >= 50) {
                String msg = awardAchievement(playerId, Achievement.PERFECT_ROUTE);
                if (msg != null) {
                    messages.add(msg);
                }
            }

            if (routeScore >= 30) {
                String msg = awardAchievement(playerId, Achievement.FIRST_SELLABLE);
                if (msg != null) {
                    messages.add(msg);
                }
            }

            // Speedrun (win in 5 or fewer turns)
            if (turns <= 5) {
                String msg = awardAchievement(playerId, Achievement.SPEEDRUN);
                if (msg != null) {
                    messages.add(msg);
                }
            }

            // Economist (high force remaining)
            if (forceRemaining >= 15) {
                String msg = awardAchievement(playerId, Achievement.ECONOMIST);
                if (msg != null) {
                    messages.add(msg);
                }
            }
        }

        // Meta achievements based on totals
        if (gamesPlayed >= 10) {
            String msg = awardAchievement(playerId, Achievement.REGULAR);
            if (msg != null) {
                messages.add(msg);
            }
        }

        if (gamesPlayed >= 50) {
            String msg = awardAchievement(playerId, Achievement.VETERAN);
            if (msg != null) {
                messages.add(msg);
            }
        }

        if (gamesPlayed >= 100) {
            String msg = awardAchievement(playerId, Achievement.LEGEND);
            if (msg != null) {
                messages.add(msg);
            }
        }

        if (totalAstScore >= 500) {
            String msg = awardAchievement(playerId, Achievement.HIGH_ROLLER);
            if (msg != null) {
                messages.add(msg);
            }
        }

        // Achievement count achievement - check current count after potential unlocks
        int currentCount = _statsDAO.getAchievementCount(playerId);
        if (currentCount >= 50) {
            String msg = awardAchievement(playerId, Achievement.PERFECTIONIST);
            if (msg != null) {
                messages.add(msg);
            }
        }

        // Holiday achievements
        if (isHolidayActive()) {
            messages.addAll(checkHolidayGameEnd(playerId, won, routeScore));
        }

        return messages;
    }

    private List<String> checkHolidayGameEnd(int playerId, boolean won, int routeScore) {
        List<String> messages = new ArrayList<>();

        // Life Day Celebrant - complete a game during the holiday
        String msg = awardAchievement(playerId, Achievement.LIFE_DAY_CELEBRANT);
        if (msg != null) {
            messages.add(msg);
        }

        // Life Day Orb - win with score 30+ during holiday
        if (won && routeScore >= 30) {
            msg = awardAchievement(playerId, Achievement.LIFE_DAY_ORB);
            if (msg != null) {
                messages.add(msg);
            }
        }

        // Holiday Special - complete 3 games during the holiday
        _holidayGamesThisSeason++;
        if (_holidayGamesThisSeason >= 3) {
            msg = awardAchievement(playerId, Achievement.HOLIDAY_SPECIAL);
            if (msg != null) {
                messages.add(msg);
            }
        }

        return messages;
    }

    /**
     * Record a battle initiation.
     */
    public void recordBattleInitiated() {
        _battlesInitiated++;
    }

    /**
     * Record a battle won by the player.
     */
    public void recordBattleWon() {
        _battlesWon++;
    }

    /**
     * Check battle-related achievements.
     *
     * @param playerId the player's database ID
     * @param won whether the player won the game
     * @return list of newly unlocked achievement quotes
     */
    public List<String> checkBattleAchievements(int playerId, boolean won) {
        List<String> messages = new ArrayList<>();

        // Pacifist - win without initiating battles
        if (won && _battlesInitiated == 0) {
            String msg = awardAchievement(playerId, Achievement.PACIFIST);
            if (msg != null) {
                messages.add(msg);
            }
        }

        // Blitzkrieg - win 3+ battles
        if (_battlesWon >= 3) {
            String msg = awardAchievement(playerId, Achievement.BLITZKRIEG);
            if (msg != null) {
                messages.add(msg);
            }
        }

        return messages;
    }

    /**
     * Check location control achievements.
     *
     * @param playerId the player's database ID
     * @param locationsControlled number of locations the player controls
     * @return list of newly unlocked achievement quotes
     */
    public List<String> checkLocationAchievements(int playerId, int locationsControlled) {
        List<String> messages = new ArrayList<>();

        if (locationsControlled >= 5) {
            String msg = awardAchievement(playerId, Achievement.FORTRESS);
            if (msg != null) {
                messages.add(msg);
            }
        }

        return messages;
    }

    /**
     * Check hand size achievements.
     *
     * @param playerId the player's database ID
     * @param handSize player's hand size
     * @return list of newly unlocked achievement quotes
     */
    public List<String> checkHandSizeAchievements(int playerId, int handSize) {
        List<String> messages = new ArrayList<>();

        if (handSize >= 8) {
            String msg = awardAchievement(playerId, Achievement.COLLECTOR);
            if (msg != null) {
                messages.add(msg);
            }
        }

        return messages;
    }

    private String awardAchievement(int playerId, Achievement ach) {
        _achievementsTriggeredThisGame.add(ach.getBitPosition());

        // Check if already has this achievement
        if (_statsDAO.hasAchievement(playerId, ach.getBitPosition())) {
            return null;
        }

        // Unlock it
        boolean newlyUnlocked = _statsDAO.unlockAchievement(playerId, ach.getBitPosition());

        if (newlyUnlocked) {
            int total = _statsDAO.getAchievementCount(playerId);
            return ach.getQuote() + " (" + total + "/" + Achievement.TOTAL_ACHIEVEMENTS + ")";
        }

        return null;
    }
}
