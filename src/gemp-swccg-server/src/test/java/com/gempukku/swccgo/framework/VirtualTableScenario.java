package com.gempukku.swccgo.framework;

import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.formats.SwccgoFormatLibrary;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.DefaultSwccgGame;
import com.gempukku.swccgo.logic.timing.DefaultUserFeedback;
import com.gempukku.swccgo.logic.vo.SwccgDeck;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

public class VirtualTableScenario implements TestBase, Actions, AdHocEffects, Battles, CardProperties, Choices, Decisions,
        GameProcedures, GameProperties, PileProperties, ZoneManipulation {

    public static SwccgCardBlueprintLibrary _cardLibrary;
    public static SwccgoFormatLibrary _formatLibrary;

    static {
        _cardLibrary = new SwccgCardBlueprintLibrary();
        _formatLibrary = new SwccgoFormatLibrary(_cardLibrary);
    }

    // Player key, then name/card
    private Map<String, Map<String, PhysicalCardImpl>> Cards = new HashMap<>();
    private DefaultSwccgGame _game;
    private GameState _gameState;
    private DefaultUserFeedback _userFeedback;
    public DefaultSwccgGame game() { return _game; }
    public GameState gameState() { return _gameState; }
    public DefaultUserFeedback userFeedback() { return _userFeedback; }

    /**
     * Constructs a basic Virtual Table for testing with the provided Dark Side and Light Side decks, each populated
     * with 10 filler cards apiece, no shields or starting interrupts, and using a default pair of ground locations.
     * @param LSCards The Light Side cards to include besides the filler.
     * @param DSCards The Dight Side cards to include besides the filler.
     * @throws DecisionResultInvalidException
     */
    public VirtualTableScenario(HashMap<String, String> LSCards, HashMap<String, String> DSCards) throws DecisionResultInvalidException {
        this(LSCards, DSCards, 10, 10, DefaultGroundLSLocation, DefaultGroundDSLocation, NoLSStarters, NoDSStarters, NoLSShields, NoDSShields, Open);
    }

    /**
     * Constructs a Virtual Table with decks that are populated with the provided parameters.
     * @param LSCards The important Light-side cards to include in your testing deck.
     * @param DSCards The important Dark-side cards to include in your testing deck.
     * @param LSFillerCount How many filler cards will be inserted into the Light Side deck (for Force purposes).
     * @param DSFillerCount How many filler cards will be inserted into the Dark Side deck (for Force purposes).
     * @param LSStartingLocation Which starting location to insert into the Light Side deck and automatically play
     *                           during startup.
     * @param DSStartingLocation Which starting location to insert into the Light Side deck and automatically play
     *                           during startup
     * @param LSStarters Objectives or starting interrupts to include in the LS deck and automatically play during startup.
     * @param DSStarters Objectives or starting interrupts to include in the DS deck and automatically play during startup.
     * @param LSShields Shields or other out-of-play cards to include as a side deck for the Light Side.
     * @param DSShields Shields or other out-of-play cards to include as a side deck for the Dark Side.
     * @param format Which format to instantiate the table using.
     * @throws DecisionResultInvalidException
     */
    public VirtualTableScenario(HashMap<String, String> LSCards, HashMap<String, String> DSCards,
            int LSFillerCount, int DSFillerCount,
            String LSStartingLocation, String DSStartingLocation,
            HashMap<String, String> LSStarters, HashMap<String, String> DSStarters,
            HashMap<String, String> LSShields, HashMap<String, String> DSShields,
            String format) throws DecisionResultInvalidException {
        super();

        Map<String, SwccgDeck> decks = new HashMap<>();
        decks.put(DS, new SwccgDeck(DS + "'s deck"));
        decks.put(LS, new SwccgDeck(LS + "'s deck"));

        // Strictly speaking, the names don't matter all that much, but in the event that the tester wants to retrieve
        // a specific card from deck by name, then if there are any duplicates the one returned will be random, which
        // can lead to stochastic tests that randomly fail.

        for(int i = 1; i <= LSFillerCount; ++i) {
            String name = "filler-" + String.format("%02d", i);
            LSCards.put(name, DefaultLSFiller);
        }

        for(int i = 1; i <= DSFillerCount; ++i) {
            String name = "filler-" + String.format("%02d", i);
            DSCards.put(name, DefaultDSFiller);
        }

        LSCards.put("starting-location", LSStartingLocation);
        DSCards.put("starting-location", DSStartingLocation);

        LSCards.putAll(LSDestinyPack);
        DSCards.putAll(DSDestinyPack);

		LSCards.putAll(LSStarters);
        DSCards.putAll(DSStarters);

        // Now that all the helper parameters have been stuffed into the decklist, we now populate an actual deck for each player.

        for(String name : LSCards.keySet()) {
            String id = LSCards.get(name);
            decks.get(LS).addCard(id);
        }

        for(String name : DSCards.keySet()) {
            String id = DSCards.get(name);
            decks.get(DS).addCard(id);
        }

        for(String name : LSShields.keySet()) {
            String id = LSShields.get(name);
            decks.get(LS).addCardOutsideDeck(id);
        }

        for(String name : DSShields.keySet()) {
            String id = DSShields.get(name);
            decks.get(DS).addCardOutsideDeck(id);
        }

        InitializeGameWithDecks(decks, format);

        ResetGameState(LSCards, DSCards);


        // Now that the game has been initialized, we reset any automatic drawing that was performed as part of startup
        for(var card : _gameState.getHand(LS).stream().toList()) {
            MoveCardsToTopOfOwnReserveDeck((PhysicalCardImpl)card);
        }
        for(var card : _gameState.getHand(DS).stream().toList()) {
            MoveCardsToTopOfOwnReserveDeck((PhysicalCardImpl)card);
        }

        // We include the destiny packs for convenience, but we don't want them in the reserve deck by default or else
        // they'll cause havoc with random destiny draws.  Instead we'll remove them all from the game and let testers
        // retrieve them and stack the deck as necessary.
        for(int i = 0; i <= 7; ++i) {
            MoveOutOfPlay(GetLSDestiny(i));
            MoveOutOfPlay(GetDSDestiny(i));
        }
    }

    /**
     * Returns a Dark Side card by its human-readable test alias.
     * @param cardName The human-readable name assigned at the top of each test class.
     * @return The physical card that was instantiated for the game.
     */
    public PhysicalCardImpl GetDSCard(String cardName) {
        var card = Cards.get(DS).get(cardName);
        if(card == null)
            throw new IllegalArgumentException("Card '" + cardName + "' not found in the Dark Side deck.");
        return card;
    }
    /**
     * Returns a Light Side card by its human-readable test alias.
     * @param cardName The human-readable name assigned at the top of each test class.
     * @return The physical card that was instantiated for the game.
     */
    public PhysicalCardImpl GetLSCard(String cardName) {
        var card = Cards.get(LS).get(cardName);
        if(card == null)
            throw new IllegalArgumentException("Card '" + cardName + "' not found in the Light Side deck.");
        return card;
    }

    /**
     * Starts up a game of SWCCG with the given decks and format.  This is used internally but may have use in certain
     * complicated test scenarios.  The vast majority of the time you do not need this.
     * @param decks A map of decks for each player in the game; key is the player name.
     * @param formatName Name of the format this table should be following.
     */
    public void InitializeGameWithDecks(Map<String, SwccgDeck> decks, String formatName) {
        _userFeedback = new DefaultUserFeedback();

        var format = _formatLibrary.getFormat(formatName);

        var clocks = new HashMap<String, Integer>() {{
            put(DS, 0);
            put(LS, 0);
        }};

        _game = new DefaultSwccgGame(format, decks, _userFeedback, _cardLibrary, clocks, false);
        _userFeedback.setGame(_game);
        _game.startGame();

        //Enables additional information on optional responses; see TurnProcedure.getActionDecisionTextFromEffectResults
        _game.setTestEnvironment(true);
    }

    /**
     * After a revert, the old game state is now stale and needs to be reset.
     */
    public void ResetGameState() {
        var LSCards = new HashMap<String, String>();
        for(var pair : Cards.get(LS).entrySet()) {
            LSCards.put(pair.getKey(), pair.getValue().getBlueprintId(true));
        }

        var DSCards = new HashMap<String, String>();
        for(var pair : Cards.get(DS).entrySet()) {
            DSCards.put(pair.getKey(), pair.getValue().getBlueprintId(true));
        }
        ResetGameState(LSCards, DSCards);
    }
    public void ResetGameState(HashMap<String, String> LSCards, HashMap<String, String> DSCards) {
        _gameState = _game.getGameState();

        var allCards = _gameState.getAllPermanentCards();
        var allDSCards = allCards.stream().filter(x -> x.getBlueprint().getSide() == Side.DARK).toList();
        var allLSCards = allCards.stream().filter(x -> x.getBlueprint().getSide() == Side.LIGHT).toList();

        Cards.put(DS, new HashMap<>());
        Cards.put(LS, new HashMap<>());

        // Next we associate all the physically-instantiated cards with the human-readable names they were given by the
        // tester.  This will now permit us to nab the exact card from anywhere without searching or collisions.
        for (var card : allLSCards) {
            String name = LSCards.entrySet()
                    .stream()
                    .filter(x -> x.getValue().equals(card.getBlueprintId(true)) && !Cards.get(LS).containsKey(x.getKey()))
                    .map(Map.Entry::getKey)
                    .findFirst().get();

            Cards.get(LS).put(name, (PhysicalCardImpl) card);
        }

        for (var card : allDSCards) {
            String name = DSCards.entrySet()
                    .stream()
                    .filter(x -> x.getValue().equals(card.getBlueprintId(true)) && !Cards.get(DS).containsKey(x.getKey()))
                    .map(Map.Entry::getKey)
                    .findFirst().get();

            Cards.get(DS).put(name, (PhysicalCardImpl) card);
        }
    }

    /**
     * After a revert has occured, any references to cards may be referencing an old one from a timeline that no longer
     * exists.  To ensure continuity, this function can be used to "sanitize" an old card reference, returning a new
     * one that is guaranteed to be the "same" card in the new revert timeline.
     * @param card A card to look up
     * @return The new timeline post-revert version of the card.
     */
    public PhysicalCardImpl GetPostRevertCard(PhysicalCardImpl card) {
        return (PhysicalCardImpl) _gameState.findCardByPermanentId(card.getPermanentCardId());
    }



    /**
     * Passes through certain setup steps at the start of the game so our test may begin at the first player's (usually
     * Dark Side) Activate phase.  Resets the hand so that the only cards in hand are those the tester defines manually
     * before calling this function.
     * @throws DecisionResultInvalidException
     */
    public void StartGame() throws DecisionResultInvalidException {
        StartGame(true);
    }

    /**
     * Passes through certain setup steps at the start of the game so our test may begin at the first player's (usually
     * Dark Side) Activate phase.
     * @param resetHand If true, any cards drawn at the start of the game will be placed back on top of the Reserve Deck,
     *                  ensuring that each player only has the cards in their hand that the tester manually places
     *                  before calling StartGame.  This ensures that there are no confounding variables.
     *                  If false, the default drawn hand will remain untouched.
     * @throws DecisionResultInvalidException
     */
    public void StartGame(boolean resetHand) throws DecisionResultInvalidException {
        if(DSDecisionAvailable("Select OK to start game")) {
            DSDecided("0");
        }

        if(LSDecisionAvailable("Select OK to start game")) {
            LSDecided("0");
        }

        var initialLSHand = _gameState.getHand(LS).stream().toList();
        var initialDSHand = _gameState.getHand(DS).stream().toList();

        if(DSDecisionAvailable("Choose starting location")) {
            DSChooseCard(GetDSCard("starting-location"));
        }

        if(LSDecisionAvailable("Choose starting location")) {
            LSChooseCard(GetLSCard("starting-location"));
        }

        //TODO: Add support for starting interrupts/objectives here

        // As a convenience, we want the tester to be able to stack their hand and other piles before the game begins.
        // However, since a new hand will be drawn, this tramples over the careful stacking, so we will reset the
        // state of the deck + hand to what they were before the card draw.
        if(resetHand) {
            for(var card : _gameState.getHand(LS).stream().toList().reversed()) {
                if(!initialLSHand.contains(card)) {
                    MoveCardsToTopOfOwnReserveDeck((PhysicalCardImpl) card);
                }
            }

            for(var card : _gameState.getHand(DS).stream().toList().reversed()) {
                if(!initialDSHand.contains(card)) {
                    MoveCardsToTopOfOwnReserveDeck((PhysicalCardImpl) card);
                }
            }
        }
    }

    /**
     * Low-level function used by the rest of the test rig to return a decision result back to the server.  This is the
     * beating heart of what is essentially a headless client.  You do not need to call this manually during tests. If
     * you find a reason to call this, you have actually found a reason to add a new helper function to the test rig.
     * @param player The player making the decision
     * @param answer What decision is being returned to the server
     * @throws DecisionResultInvalidException If there is any mismatch between what the server is expecting and your
     * answer, this test will fail.
     */
    public void PlayerDecided(String player, String answer) throws DecisionResultInvalidException {
        var decision = userFeedback().getAwaitingDecision(player);
        userFeedback().participantDecided(player);
        try {
            decision.decisionMade(answer);
        } catch (DecisionResultInvalidException exp) {
            userFeedback().sendAwaitingDecision(player, decision);
            throw exp;
        }
        game().carryOutPendingActionsUntilDecisionNeeded();
    }




}
