package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.communication.GameStateListener;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.*;
import com.gempukku.swccgo.game.layout.LocationPlacement;
import com.gempukku.swccgo.game.layout.LocationsLayout;
import com.gempukku.swccgo.game.state.actions.GameTextActionState;
import com.gempukku.swccgo.game.state.actions.PlayCardState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.PlayerOrder;
import com.gempukku.swccgo.logic.actions.GameTextAction;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GameStats;
import com.gempukku.swccgo.logic.timing.SnapshotData;
import com.gempukku.swccgo.logic.timing.Snapshotable;
import org.apache.log4j.Logger;

import java.util.*;

// This class contains the state information for a
// game of Gemp-Swccg.
//
public class GameState implements Snapshotable<GameState> {
    private static Logger _log = Logger.getLogger(GameState.class);
    private static final int LAST_MESSAGE_STORED_COUNT = 25000;
    private SwccgGame _game;
    private PlayerOrder _playerOrder;

    private Map<String, List<PhysicalCard>> _reserveDecks = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<PhysicalCard>> _hands = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<PhysicalCard>> _sabaccHands = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<PhysicalCard>> _forcePiles = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<PhysicalCard>> _usedPiles = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<PhysicalCard>> _lostPiles = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<PhysicalCard>> _unresolvedDestinyDraws = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<PhysicalCard>> _outOfPlayPiles = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<PhysicalCard>> _stacked = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<PhysicalCard>> _outsideOfDecks = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<PhysicalCard>> _sideOfTableNotInPlay = new HashMap<String, List<PhysicalCard>>();
    private Map<String, List<PhysicalCard>> _voids = new HashMap<String, List<PhysicalCard>>();
    private List<PhysicalCard> _inPlay = new LinkedList<PhysicalCard>();

    private Map<Integer, PhysicalCard> _allCards = new HashMap<Integer, PhysicalCard>();
    private Map<Integer, PhysicalCard> _allCardsByPermanentCardId = new HashMap<Integer, PhysicalCard>();
    private LocationsLayout _locationsLayout;

    private boolean _darkSideLostPileTurnedOver;
    private boolean _lightSideLostPileTurnedOver;
    private boolean _usedPilesTurnedOver;
    private boolean _darkSideTopOfReserveDeckTurnedOver;
    private boolean _lightSideTopOfReserveDeckTurnedOver;
    private boolean _tableChangedSinceStatsSent;
    private boolean _skipListenerUpdateAllowed;
    private boolean _insertFound;

    private PhysicalCard _podraceInitiatedByCard;
    private boolean _podraceFinishing;
    private String _podraceWinner;
    private String _podraceLoser;
    private float _podraceWinnerRaceTotal;
    private float _podraceLoserRaceTotal;

    private String _darkSidePlayer;
    private String _lightSidePlayer;
    private String _currentPlayerId;
    private Phase _currentPhase = Phase.PLAY_STARTING_CARDS;
    private int _darkSideTurnNumber;
    private int _lightSideTurnNumber;
    private float _darkSideTotalForceGeneration = 1;
    private float _lightSideTotalForceGeneration = 1;
    private boolean _darkSideLifeForceDepleted;
    private boolean _lightSideLifeForceDepleted;

    private Map<String, AwaitingDecision> _playerDecisions = new HashMap<String, AwaitingDecision>();

    // States of multi-step actions that can occur within a turn
    private AttackState _attackState;
    private BattleState _battleState;
    private DuelState _duelState;
    private LightsaberCombatState _lightsaberCombatState;
    private MoveAsReactState _moveAsReactState;
    private DeployAsReactState _deployAsReactState;
    private EpicEventState _epicEventState;
    private ForceDrainState _forceDrainState;
    private AsteroidDestinyDrawState _asteroidDestinyDrawState;
    private MovementDestinyDrawState _movementDestinyDrawState;
    private SabaccState _sabaccState;
    private SearchPartyState _searchPartyState;
    private WeaponFiringState _weaponFiringState;
    private UsingTractorBeamState _usingTractorBeamState;
    private Stack<ForceLossState> _forceLossState = new Stack<ForceLossState>();
    private Stack<ForceRetrievalState> _forceRetrievalState = new Stack<ForceRetrievalState>();
    private Stack<PlayCardState> _playCardState = new Stack<PlayCardState>();
    private Stack<GameTextActionState> _gameTextActionState = new Stack<GameTextActionState>();
    private Stack<DrawDestinyState> _drawDestinyState = new Stack<DrawDestinyState>();
    private Stack<EachDrawnDestinyState> _eachDrawnDestinyState = new Stack<EachDrawnDestinyState>();
    private Stack<BlowAwayState> _blowAwayState = new Stack<BlowAwayState>();
    private Set<String> _apprenticeTitles = new HashSet<String>();
    private Set<Persona> _apprenticePersonas = new HashSet<Persona>();

    private LinkedList<String> _lastMessages = new LinkedList<String>();

    private boolean _darkSidePlayerDeployedDeathStarLocation;
    private boolean _lightSidePlayerDeployedDeathStarLocation;

    private boolean _darkSidePlayerDeployedAhchToDagobahLocation;
    private boolean _lightSidePlayerDeployedAhchToDagobahLocation;

    private PhysicalCard _darkSideObjective;
    private PhysicalCard _lightSideObjective;
    private PhysicalCard _darkSideStartingInterrupt;
    private PhysicalCard _lightSideStartingInterrupt;
    private List<PhysicalCard> _darkSideRevealedAfterStartingEffect = new ArrayList<PhysicalCard>();
    private List<PhysicalCard> _lightSideRevealedAfterStartingEffect = new ArrayList<PhysicalCard>();
    private PhysicalCard _darkSideRep;
    private PhysicalCard _lightSideRep;

    private String _renegadePlanet;
    private String _subjugatedPlanet;

    private int _nextCardId;

    /**
     * Needed to generate snapshot.
     */
    public GameState() {
    }

    @Override
    public void generateSnapshot(GameState selfSnapshot, SnapshotData snapshotData) {
        GameState snapshot = selfSnapshot;

        // Set each field
        snapshot._game = _game;
        snapshot._playerOrder = _playerOrder;
        for (String playerId : _reserveDecks.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._reserveDecks.put(playerId, snapshotList);
            for (PhysicalCard card : _reserveDecks.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _hands.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._hands.put(playerId, snapshotList);
            for (PhysicalCard card : _hands.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _sabaccHands.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._sabaccHands.put(playerId, snapshotList);
            for (PhysicalCard card : _sabaccHands.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _unresolvedDestinyDraws.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._unresolvedDestinyDraws.put(playerId, snapshotList);
            for (PhysicalCard card : _unresolvedDestinyDraws.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _voids.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._voids.put(playerId, snapshotList);
            for (PhysicalCard card : _voids.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _forcePiles.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._forcePiles.put(playerId, snapshotList);
            for (PhysicalCard card : _forcePiles.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _usedPiles.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._usedPiles.put(playerId, snapshotList);
            for (PhysicalCard card : _usedPiles.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _lostPiles.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._lostPiles.put(playerId, snapshotList);
            for (PhysicalCard card : _lostPiles.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _outOfPlayPiles.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._outOfPlayPiles.put(playerId, snapshotList);
            for (PhysicalCard card : _outOfPlayPiles.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _stacked.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._stacked.put(playerId, snapshotList);
            for (PhysicalCard card : _stacked.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _outsideOfDecks.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._outsideOfDecks.put(playerId, snapshotList);
            for (PhysicalCard card : _outsideOfDecks.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (String playerId : _sideOfTableNotInPlay.keySet()) {
            List<PhysicalCard> snapshotList = new LinkedList<PhysicalCard>();
            snapshot._sideOfTableNotInPlay.put(playerId, snapshotList);
            for (PhysicalCard card : _sideOfTableNotInPlay.get(playerId)) {
                snapshotList.add(snapshotData.getDataForSnapshot(card));
            }
        }
        for (PhysicalCard card : _inPlay) {
            snapshot._inPlay.add(snapshotData.getDataForSnapshot(card));
        }
        for (Integer cardId : _allCards.keySet()) {
            PhysicalCard card = _allCards.get(cardId);
            snapshot._allCards.put(cardId, snapshotData.getDataForSnapshot(card));
        }
        for (Integer cardId : _allCardsByPermanentCardId.keySet()) {
            PhysicalCard card = _allCardsByPermanentCardId.get(cardId);
            snapshot._allCardsByPermanentCardId.put(cardId, snapshotData.getDataForSnapshot(card));
        }
        snapshot._locationsLayout = snapshotData.getDataForSnapshot(_locationsLayout);
        snapshot._darkSideLostPileTurnedOver = _darkSideLostPileTurnedOver;
        snapshot._lightSideLostPileTurnedOver = _lightSideLostPileTurnedOver;
        snapshot._darkSideTopOfReserveDeckTurnedOver = _darkSideTopOfReserveDeckTurnedOver;
        snapshot._lightSideTopOfReserveDeckTurnedOver = _lightSideTopOfReserveDeckTurnedOver;
        snapshot._usedPilesTurnedOver = _usedPilesTurnedOver;
        snapshot._tableChangedSinceStatsSent = _tableChangedSinceStatsSent;
        snapshot._skipListenerUpdateAllowed = _skipListenerUpdateAllowed;
        snapshot._insertFound = _insertFound;
        snapshot._podraceInitiatedByCard = snapshotData.getDataForSnapshot(_podraceInitiatedByCard);
        snapshot._podraceFinishing = _podraceFinishing;
        snapshot._podraceWinner = _podraceWinner;
        snapshot._podraceLoser = _podraceLoser;
        snapshot._podraceWinnerRaceTotal = _podraceWinnerRaceTotal;
        snapshot._podraceLoserRaceTotal = _podraceLoserRaceTotal;
        snapshot._darkSidePlayer = _darkSidePlayer;
        snapshot._lightSidePlayer = _lightSidePlayer;
        snapshot._currentPlayerId = _currentPlayerId;
        snapshot._currentPhase = _currentPhase;
        snapshot._darkSideTurnNumber = _darkSideTurnNumber;
        snapshot._lightSideTurnNumber = _lightSideTurnNumber;
        snapshot._darkSideTotalForceGeneration = _darkSideTotalForceGeneration;
        snapshot._lightSideTotalForceGeneration = _lightSideTotalForceGeneration;
        snapshot._darkSideLifeForceDepleted = _darkSideLifeForceDepleted;
        snapshot._lightSideLifeForceDepleted = _lightSideLifeForceDepleted;
        snapshot._playerDecisions.putAll(_playerDecisions);
        snapshot._attackState = snapshotData.getDataForSnapshot(_attackState);
        snapshot._battleState = snapshotData.getDataForSnapshot(_battleState);
        if (_duelState != null) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with DuelState");
        }
        if (_lightsaberCombatState != null) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with LightsaberCombatState");
        }
        if (_moveAsReactState != null) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with MoveAsReactState");
        }
        if (_deployAsReactState != null) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with DeployAsReactState");
        }
        if (_epicEventState != null) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with EpicEventState");
        }
        if (_forceDrainState != null) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with ForceDrainState");
        }
        if (_asteroidDestinyDrawState != null) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with AsteroidDestinyDrawState");
        }
        if (_movementDestinyDrawState != null) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with MovementDestinyDrawState");
        }
        if (_sabaccState != null) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with SabaccState");
        }
        if (_searchPartyState != null) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with SearchPartyState");
        }
        if (_weaponFiringState != null) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with WeaponFiringState");
        }
        if (!_forceLossState.isEmpty()) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with ForceLossState");
        }
        if (!_forceRetrievalState.isEmpty()) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with ForceRetrievalState");
        }
        if (!_playCardState.isEmpty()) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with PlayCardState: " + GameUtils.getFullName(_playCardState.peek().getPlayCardAction().getPlayedCard()));
        }
        for (GameTextActionState gameTextActionStateInStack : _gameTextActionState) {
            snapshot._gameTextActionState.add(snapshotData.getDataForSnapshot(gameTextActionStateInStack));
        }
        if (!_drawDestinyState.isEmpty()) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with DrawDestinyState");
        }
        if (!_eachDrawnDestinyState.isEmpty()) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with EachDrawnDestinyState");
        }
        if (!_blowAwayState.isEmpty()) {
            throw new UnsupportedOperationException("Cannot generate snapshot of " + getClass().getSimpleName() + " with BlowAwayState");
        }
        snapshot._apprenticeTitles.addAll(_apprenticeTitles);
        snapshot._apprenticePersonas.addAll(_apprenticePersonas);
        snapshot._lastMessages.addAll(_lastMessages);
        snapshot._darkSidePlayerDeployedDeathStarLocation = _darkSidePlayerDeployedDeathStarLocation;
        snapshot._lightSidePlayerDeployedDeathStarLocation = _lightSidePlayerDeployedDeathStarLocation;
        snapshot._darkSidePlayerDeployedAhchToDagobahLocation = _darkSidePlayerDeployedAhchToDagobahLocation;
        snapshot._lightSidePlayerDeployedAhchToDagobahLocation = _lightSidePlayerDeployedAhchToDagobahLocation;
        snapshot._darkSideObjective = snapshotData.getDataForSnapshot(_darkSideObjective);
        snapshot._lightSideObjective = snapshotData.getDataForSnapshot(_lightSideObjective);
        snapshot._darkSideStartingInterrupt = snapshotData.getDataForSnapshot(_darkSideStartingInterrupt);
        snapshot._lightSideStartingInterrupt = snapshotData.getDataForSnapshot(_lightSideStartingInterrupt);
        for (PhysicalCard card : _darkSideRevealedAfterStartingEffect) {
            snapshot._darkSideRevealedAfterStartingEffect.add(snapshotData.getDataForSnapshot(card));
        }
        for (PhysicalCard card : _lightSideRevealedAfterStartingEffect) {
            snapshot._lightSideRevealedAfterStartingEffect.add(snapshotData.getDataForSnapshot(card));
        }
        snapshot._darkSideRep = snapshotData.getDataForSnapshot(_darkSideRep);
        snapshot._lightSideRep = snapshotData.getDataForSnapshot(_lightSideRep);
        snapshot._renegadePlanet = _renegadePlanet;
        snapshot._subjugatedPlanet = _subjugatedPlanet;
        snapshot._nextCardId = _nextCardId;
    }

    public GameState(SwccgGame game) {
        _game = game;
    }

    public void init(PlayerOrder playerOrder, String darkSidePlayer, String lightSidePlayer, Map<String, List<String>> cards, Map<String, List<String>> outsideOfDeckCards, SwccgCardBlueprintLibrary library) {
        _playerOrder = playerOrder;
        _currentPlayerId = darkSidePlayer;
        _darkSidePlayer = darkSidePlayer;
        _lightSidePlayer = lightSidePlayer;

        for (GameStateListener listener : getAllGameStateListeners()) {
            listener.setPlayerOrder(Arrays.asList(_darkSidePlayer, _lightSidePlayer));
        }

        for (Map.Entry<String, List<String>> stringListEntry : cards.entrySet()) {
            String playerId = stringListEntry.getKey();
            List<String> decks = stringListEntry.getValue();

            _reserveDecks.put(playerId, new LinkedList<PhysicalCard>());
            _hands.put(playerId, new LinkedList<PhysicalCard>());
            _sabaccHands.put(playerId, new LinkedList<PhysicalCard>());
            _unresolvedDestinyDraws.put(playerId, new LinkedList<PhysicalCard>());
            _voids.put(playerId, new LinkedList<PhysicalCard>());
            _forcePiles.put(playerId, new LinkedList<PhysicalCard>());
            _usedPiles.put(playerId, new LinkedList<PhysicalCard>());
            _lostPiles.put(playerId, new LinkedList<PhysicalCard>());
            _outOfPlayPiles.put(playerId, new LinkedList<PhysicalCard>());
            _stacked.put(playerId, new LinkedList<PhysicalCard>());
            _outsideOfDecks.put(playerId, new LinkedList<PhysicalCard>());
            _sideOfTableNotInPlay.put(playerId, new LinkedList<PhysicalCard>());

            addPlayerCards(playerId, decks, outsideOfDeckCards.get(playerId), library);
        }
    }

    public void setLocationsLayout(LocationsLayout locationsLayout) {
        _locationsLayout = locationsLayout;
    }

    private void addPlayerCards(String playerId, List<String> cards, List<String> outsideOfDeckCards, SwccgCardBlueprintLibrary library) {
        for (String blueprintId : outsideOfDeckCards) {
            PhysicalCard physicalCard = createPhysicalCard(playerId, library, blueprintId);
            physicalCard.setZone(Zone.OUTSIDE_OF_DECK);
            physicalCard.setZoneOwner(playerId);
            _outsideOfDecks.get(playerId).add(physicalCard);
            _game.getModifiersEnvironment().addCardSpecificAlwaysOnModifiers(_game, physicalCard);
        }
        for (String blueprintId : cards) {
            PhysicalCard physicalCard = createPhysicalCard(playerId, library, blueprintId);
            physicalCard.setZone(Zone.RESERVE_DECK);
            physicalCard.setZoneOwner(playerId);
            _reserveDecks.get(playerId).add(physicalCard);
            _game.getModifiersEnvironment().addCardSpecificAlwaysOnModifiers(_game, physicalCard);
        }

        // Shuffle the Reserve Deck and then make sure that top card is not a double-sided card
        List<PhysicalCard> reserveDeck = _reserveDecks.get(playerId);
        Collections.shuffle(reserveDeck);
        PhysicalCard topCard = reserveDeck.get(0);
        for (int i=0; i < reserveDeck.size(); ++i) {
            if (!topCard.getBlueprint().isFrontOfDoubleSidedCard()) {
                break;
            }
            reserveDeck.remove(0);
            reserveDeck.add(topCard);
            topCard = reserveDeck.get(0);
        }
        topCard.setZone(Zone.TOP_OF_RESERVE_DECK);
        _tableChangedSinceStatsSent = true;

        // Tell game listener to create top card
        for (GameStateListener listener : getAllGameStateListeners()) {
            listener.cardCreated(topCard, this, false);
        }
    }

    /**
     * Create the physical card used in the game. If the card is two-sided, then it will also have a blueprint for the back side.
     * @param playerId the player
     * @param library the blueprint library
     * @param blueprintId the blueprint ID
     * @return the physical card
     */
    private PhysicalCard createPhysicalCard(String playerId, SwccgCardBlueprintLibrary library, String blueprintId) {
        SwccgCardBlueprint cardFront = library.getSwccgoCardBlueprint(blueprintId);
        String backBlueprintId = playerId.equals(_darkSidePlayer) ? "-1_2" : "-1_1";
        SwccgCardBlueprint cardBack = null;
        if (cardFront.isFrontOfDoubleSidedCard()) {
            backBlueprintId = library.stripBlueprintModifiers(blueprintId) + "_BACK" + (library.isFoil(blueprintId) ? "*" : "") + (library.isAlternateImage(blueprintId) ? "^" : "");
            cardBack = library.getSwccgoCardBlueprint(backBlueprintId);
        }

        int cardId = nextCardId();
        PhysicalCard result = new PhysicalCardImpl(cardId, blueprintId, backBlueprintId, playerId, cardFront, cardBack);
        // Set the physical card in the permanent weapons and permanents aboard
        result.getBlueprint().getPermanentWeapon(result);
        result.getBlueprint().getPermanentsAboard(result);

        _allCards.put(cardId, result);
        _allCardsByPermanentCardId.put(cardId, result);

        return result;
    }

    private int nextCardId() {
        return ++_nextCardId;
    }

    public SwccgGame getGame() {
        return _game;
    }

    public PlayerOrder getPlayerOrder() {
        return _playerOrder;
    }

    public void setFirstPlayer(String playerId) {
        _currentPlayerId = getOpponent(playerId);
        _playerOrder = new PlayerOrder(new ArrayList<String>(Arrays.asList(playerId, _currentPlayerId)));
    }

    public String getPlayer(Side side) {
        if (side==Side.DARK)
            return _darkSidePlayer;
        else
            return _lightSidePlayer;
    }

    public String getLightPlayer() {
        return _lightSidePlayer;
    }

    public String getDarkPlayer() {
        return _darkSidePlayer;
    }

    public String getOpponent(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            return _lightSidePlayer;
        else
            return _darkSidePlayer;
    }

    public Side getSide(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            return Side.DARK;
        else
            return Side.LIGHT;
    }

    private String getPhaseString() {
        int turnNumber = _currentPlayerId.equals(_darkSidePlayer) ? _darkSideTurnNumber : _lightSideTurnNumber;
        if (turnNumber > 0)
            return _currentPhase.getHumanReadable() + " (turn #" + turnNumber + ")";
        else
            return _currentPhase.getHumanReadable();
    }

    public void sendStateToPlayer(String playerId, GameStateListener listener, GameStats gameStats, boolean restoreSnapshot) {
        if (_playerOrder != null) {

            listener.setPlayerOrder(Arrays.asList(_darkSidePlayer, _lightSidePlayer));
            if (_currentPlayerId != null)
                listener.setCurrentPlayerId(_currentPlayerId);
            if (_currentPhase != null)
                listener.setCurrentPhase(getPhaseString());

            Set<PhysicalCard> cardsLeftToSent = new LinkedHashSet<PhysicalCard>(_inPlay);
            Set<PhysicalCard> sentCardsFromPlay = new HashSet<PhysicalCard>();

            // Send locations in order
            List<PhysicalCard> topLocations = getLocationsInOrder();
            for (PhysicalCard location : topLocations)
                listener.cardCreated(location, this, restoreSnapshot);

            // Send remaining cards in play
            int cardsToSendAtLoopStart;
            do {
                cardsToSendAtLoopStart = cardsLeftToSent.size();
                Iterator<PhysicalCard> cardIterator = cardsLeftToSent.iterator();
                while (cardIterator.hasNext()) {
                    PhysicalCard physicalCard = cardIterator.next();
                    if (physicalCard.getZone()!=Zone.LOCATIONS) {
                        PhysicalCard attachedTo = physicalCard.getAttachedTo();
                        if (attachedTo == null || attachedTo.getZone()==Zone.LOCATIONS || sentCardsFromPlay.contains(attachedTo)) {
                            listener.cardCreated(physicalCard, this, restoreSnapshot);
                            sentCardsFromPlay.add(physicalCard);

                            cardIterator.remove();
                        }
                    }
                }
            } while (cardsToSendAtLoopStart != cardsLeftToSent.size() && !cardsLeftToSent.isEmpty());

            // Send side of table not in play cards
            for (List<PhysicalCard> physicalCards : _sideOfTableNotInPlay.values())
                for (PhysicalCard physicalCard : physicalCards)
                    listener.cardCreated(physicalCard, this, restoreSnapshot);

            // Send stacked cards
            for (List<PhysicalCard> physicalCards : _stacked.values())
                for (PhysicalCard physicalCard : physicalCards) {
                    listener.cardCreated(physicalCard, this, restoreSnapshot);
                }

            // Send top of card piles (reserve decks, force piles, used piles, lost piles, unresolved destiny draw)
            for (PhysicalCard physicalCard : getTopCardsOfPiles())
                listener.cardCreated(physicalCard, this, restoreSnapshot);

            // Send out of play piles
            for (List<PhysicalCard> physicalCards : _outOfPlayPiles.values())
                for (PhysicalCard physicalCard : physicalCards)
                    listener.cardCreated(physicalCard, this, restoreSnapshot);

            // Send hand
            List<PhysicalCard> hand = _hands.get(playerId);
            if (hand != null) {
                for (PhysicalCard physicalCard : hand)
                    listener.cardCreated(physicalCard, this, restoreSnapshot);
            }

            // Send attack in progress
            if (_attackState != null) {
                // TODO:
            }

            // Send battle in progress
            if (_battleState != null) {
                Collection<PhysicalCard> allCardsParticipating = new ArrayList<PhysicalCard>();
                allCardsParticipating.addAll(_battleState.getDarkCardsParticipating());
                allCardsParticipating.addAll(_battleState.getLightCardsParticipating());
                listener.startBattle(_battleState.getBattleLocation(), allCardsParticipating);
            }

            // Send duel in progress
            if (_duelState != null) {
                List<PhysicalCard> cardsToShowInDuel = new ArrayList<PhysicalCard>();
                for (PhysicalCard cardInDuel : _duelState.getDuelParticipants()) {
                    PhysicalCard cardToShowInDuel = cardInDuel.getCardAttachedToAtLocation();
                    if (cardToShowInDuel != null) {
                        cardsToShowInDuel.add(cardToShowInDuel);
                    }
                }
                listener.startDuel(_duelState.getLocation(), cardsToShowInDuel);
            }

            // Send sabacc in progress
            if (_sabaccState != null) {
                if (_sabaccState.isHandsRevealed())
                    listener.revealSabaccHands();
                else
                    listener.startSabacc();

                // Send sabacc hands
                for (List<PhysicalCard> physicalCards : _sabaccHands.values()) {
                    for (PhysicalCard physicalCard : physicalCards)
                        listener.cardCreated(physicalCard, this, restoreSnapshot);
                }
            }

            listener.sendGameStats(gameStats);
        }

        for (String lastMessage : _lastMessages)
            listener.sendMessage(lastMessage);

        final AwaitingDecision awaitingDecision = _playerDecisions.get(playerId);
        if (awaitingDecision != null)
            listener.decisionRequired(playerId, awaitingDecision);
    }

    public void sendMessage(String message) {
        _lastMessages.add(message);
        if (_lastMessages.size() > LAST_MESSAGE_STORED_COUNT)
            _lastMessages.removeFirst();
        for (GameStateListener listener : getAllGameStateListeners())
            listener.sendMessage(message);
    }

    public void playerDecisionStarted(String playerId, AwaitingDecision awaitingDecision) {
        _playerDecisions.put(playerId, awaitingDecision);
        for (GameStateListener listener : getAllGameStateListeners())
            listener.decisionRequired(playerId, awaitingDecision);
    }

    public void playerDecisionFinished(String playerId) {
        _playerDecisions.remove(playerId);
    }


    /**
     * Sets the card as a 'collapsed' site.
     * @param card the card
     */
    public void collapseSite(PhysicalCard card) {
        card.setCollapsed(true);
        List<PhysicalCard> convertedSites = _game.getGameState().getConvertedLocationsUnderTopLocation(card);
        for (PhysicalCard convertedSite : convertedSites) {
            convertedSite.setCollapsed(true);
        }

        // Use cardRotated from listener since it just updates the image/overlays and doesn't move/replace the card
        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardRotated(card, this);
    }

    /**
     * Sets the card as a frozen captive.
     * @param card the card
     */
    public void freezeCharacter(PhysicalCard card) {
        card.setFrozen(true);
        card.setCaptive(true);

        // Use cardRotated from listener since it just updates the image/overlays and doesn't move/replace the card
        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardRotated(card, this);
    }

    public void suspendCard(PhysicalCard card) {
        card.setSuspended(true);

        // Use cardRotated from listener since it just updates the image/overlays and doesn't move/replace the card
        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardRotated(card, this);
    }

    public void resumeCard(PhysicalCard card) {
        card.setSuspended(false);

        // Use cardRotated from listener since it just updates the image/overlays and doesn't move/replace the card
        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardRotated(card, this);
    }

    public void turnOffBinaryDroid(PhysicalCard card) {
        card.setBinaryOff(true);

        // Use cardRotated from listener since it just updates the image/overlays and doesn't move/replace the card
        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardRotated(card, this);
    }

    public void turnOnBinaryDroid(PhysicalCard card) {
        card.setBinaryOff(false);

        // Use cardRotated from listener since it just updates the image/overlays and doesn't move/replace the card
        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardRotated(card, this);
    }

    /**
     * Puts a card undercover.
     * @param card the card
     */
    public void putUndercover(PhysicalCard card) {
        card.setUndercover(true);
        moveCardToLocation(card, _game.getModifiersQuerying().getLocationThatCardIsAt(this, card), false);
    }

    /**
     * Breaks a card's cover.
     * @param card the card
     */
    public void breakCover(PhysicalCard card) {
        card.setUndercover(false);
        moveCardToLocation(card, _game.getModifiersQuerying().getLocationThatCardIsAt(this, card), true);
    }

    /**
     * Sets the card as 'missing', disembarks if needed, and frees captive if needed.
     * @param game the game
     * @param card the card
     */
    public void makeGoMissing(SwccgGame game, PhysicalCard card) {
        card.setMissing(true);
        if (!card.isFrozen()) {
            card.setCaptive(false);
        }
        moveCardToLocation(card, _game.getModifiersQuerying().getLocationThatCardIsAt(this, card), !card.isUndercover() && !card.isFrozen());
        if (!card.isInverted()) {
            invertCard(game, card, true);
        }
    }

    /**
     * Sets the card as no longer 'missing'.
     * @param game the game
     * @param card the card
     */
    public void findMissingCharacter(SwccgGame game, PhysicalCard card) {
        card.setMissing(false);
        if (card.isInverted()) {
            invertCard(game, card, false);
        }
    }

    /**
     * Sets the card as 'imprisoned' in the specified prison.
     * @param game the game
     * @param card the card
     * @param prison the prison
     */
    public void imprisonCharacter(SwccgGame game, PhysicalCard card, PhysicalCard prison) {
        // Move or attach card based on if card was already in play
        if (Filters.in_play.accepts(game.getGameState(), game.getModifiersQuerying(), card)) {
            card.setCaptive(true);
            card.setImprisoned(true);
            moveCardToAttached(card, prison);
        }
        else {
            game.getGameState().removeCardsFromZone(Collections.singleton(card));
            card.setCaptive(true);
            card.setImprisoned(true);
            attachCard(card, prison);
        }
    }

    /**
     * Sets the card as a 'captive' of the specified escort.
     * @param game the game
     * @param card the card
     * @param escort the escort
     */
    public void seizeCharacter(SwccgGame game, PhysicalCard card, PhysicalCard escort) {
        // Move or attach card based on if card was already in play
        if (Filters.in_play.accepts(game.getGameState(), game.getModifiersQuerying(), card)) {
            card.setCaptive(true);
            card.setImprisoned(false);
            moveCardToAttached(card, escort);
        }
        else {
            game.getGameState().removeCardsFromZone(Collections.singleton(card));
            card.setCaptive(true);
            card.setImprisoned(false);
            attachCard(card, escort);
        }
    }

    /**
     * Sets the card as a captured starship and attaches it to the specified card
     * @param game the game
     * @param starship the starship that is being captured
     * @param attachTo the card the starship will be attached to
     */
    public void captureStarship(SwccgGame game, PhysicalCard starship, PhysicalCard attachTo) {
        if (Filters.in_play.accepts(game.getGameState(), game.getModifiersQuerying(), starship)) {
            starship.setCapturedStarship(true);
            moveCardToAttached(starship, attachTo);
        }
    }

    /**
     * Puts the card (not already on table) on the specified player's side of the specified location.
     * @param card the card
     * @param atLocation the location
     * @param zoneOwner the side of the location
     */
    public void playCardToLocation(PhysicalCard card, PhysicalCard atLocation, String zoneOwner) {
        card.atLocation(atLocation);
        addCardToZone(card, Zone.AT_LOCATION, zoneOwner);
    }

    /**
     * Puts the card (already on table) on current side of the specified location.
     * @param card the card
     * @param moveTo the location
     */
    public void moveCardToLocation(PhysicalCard card, PhysicalCard moveTo) {
        moveCardToLocation(card, moveTo, card.getZoneOwner().equals(card.getOwner()));
    }

    /**
     * Puts the card (already on table) on the specified player's side of the specified location.
     * @param card the card
     * @param moveTo the location
     * @param ownerZone true if put on the card owner's side, otherwise false
     */
    public void moveCardToLocation(PhysicalCard card, PhysicalCard moveTo, boolean ownerZone) {
        if (getBattleLocation() != null
                && getBattleLocation().getCardId() != moveTo.getCardId()) {
            removeCardFromBattleGroup(card);
        }
        String newZoneOwner = ownerZone ? card.getOwner() : _game.getOpponent(card.getOwner());
        if (!getZoneCards(newZoneOwner, Zone.AT_LOCATION).contains(card)) {
            getZoneCards(card.getZoneOwner(), card.getZone()).remove(card);
            getZoneCards(newZoneOwner, Zone.AT_LOCATION).add(card);
        }
        card.atLocation(moveTo);
        card.attachTo(null, false, false, false, false, false);
        card.stackOn(null, false, false);
        if (!card.isFrozen()) {
            card.setCaptive(false);
        }
        card.setImprisoned(false);
        card.setZone(Zone.AT_LOCATION);
        card.setZoneOwner(newZoneOwner);

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardMoved(card, this);
    }

    /**
     * Attaches the card (not already on table) to another card.
     * @param card the card
     * @param attachTo the card to attach to
     */
    public void attachCard(PhysicalCard card, PhysicalCard attachTo) {
        card.attachTo(attachTo, false, false, false, false, false);
        card.stackOn(null, false, false);
        addCardToZone(card, Zone.ATTACHED, attachTo.getZoneOwner());
    }

    /**
     * Attaches the card (not already on table) to another card to be in pilot capacity slot.
     * @param card the card
     * @param attachTo the card to attach to
     */
    public void attachCardInPilotCapacitySlot(PhysicalCard card, PhysicalCard attachTo) {
        card.attachTo(attachTo, true, false, false, false, false);
        card.stackOn(null, false, false);
        addCardToZone(card, Zone.ATTACHED, attachTo.getZoneOwner());
    }

    /**
     * Attaches the card (not already on table) to another card to be in passenger capacity slot.
     * @param card the card
     * @param attachTo the card to attach to
     */
    public void attachCardInPassengerCapacitySlot(PhysicalCard card, PhysicalCard attachTo) {
        card.attachTo(attachTo, false, true, false, false, false);
        card.stackOn(null, false, false);
        addCardToZone(card, Zone.ATTACHED, attachTo.getZoneOwner());
    }

    /**
     * Attaches the card (not already on table) to another card to be in vehicle capacity slot of cargo bay.
     * @param card the card
     * @param attachTo the card to attach to
     */
    public void attachCardInVehicleCapacitySlot(PhysicalCard card, PhysicalCard attachTo) {
        card.attachTo(attachTo, false, false, true, false, false);
        card.stackOn(null, false, false);
        addCardToZone(card, Zone.ATTACHED, attachTo.getZoneOwner());
    }

    /**
     * Attaches the card (not already on table) to another card to be in starfighter or TIE capacity slot of cargo bay.
     * @param card the card
     * @param attachTo the card to attach to
     */
    public void attachCardInStarfighterOrTIECapacitySlot(PhysicalCard card, PhysicalCard attachTo) {
        card.attachTo(attachTo, false, false, false, true, false);
        card.stackOn(null, false, false);
        addCardToZone(card, Zone.ATTACHED, attachTo.getZoneOwner());
    }

    /**
     * Attaches the card (not already on table) to another card to be in capital starship capacity slot of cargo bay.
     * @param card the card
     * @param attachTo the card to attach to
     */
    public void attachCardInCapitalStarshipCapacitySlot(PhysicalCard card, PhysicalCard attachTo) {
        card.attachTo(attachTo, false, false, false, false, true);
        card.stackOn(null, false, false);
        addCardToZone(card, Zone.ATTACHED, attachTo.getZoneOwner());
    }

    /**
     * Attaches the card (already on table) to another card.
     * @param card the card
     * @param moveTo the card to attach to
     */
    public void moveCardToAttached(PhysicalCard card, PhysicalCard moveTo) {
        if (!getZoneCards(moveTo.getZoneOwner(), Zone.ATTACHED).contains(card)) {
            getZoneCards(card.getZoneOwner(), card.getZone()).remove(card);
            getZoneCards(moveTo.getZoneOwner(), Zone.ATTACHED).add(card);
        }
        card.atLocation(null);
        card.stackOn(null, false, false);
        card.attachTo(moveTo, false, false, false, false, false);
        card.setTargetedCard(TargetId.DEPLOY_TARGET, moveTo.getTargetGroupId(TargetId.DEPLOY_TARGET), moveTo, Filters.sameCardId(moveTo));
        card.setZone(Zone.ATTACHED);
        card.setZoneOwner(moveTo.getZoneOwner());

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardMoved(card, this);
    }

    /**
     * Attaches the card (already on table) to another card to be in pilot capacity slot.
     * @param card the card
     * @param moveTo the card to attach to
     */
    public void moveCardToAttachedInPilotCapacitySlot(PhysicalCard card, PhysicalCard moveTo) {
        if (!getZoneCards(moveTo.getZoneOwner(), Zone.ATTACHED).contains(card)) {
            getZoneCards(card.getZoneOwner(), card.getZone()).remove(card);
            getZoneCards(moveTo.getZoneOwner(), Zone.ATTACHED).add(card);
        }
        card.atLocation(null);
        card.stackOn(null, false, false);
        card.attachTo(moveTo, true, false, false, false, false);
        card.setZone(Zone.ATTACHED);
        card.setZoneOwner(moveTo.getZoneOwner());

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardMoved(card, this);
    }

    /**
     * Attaches the card (already on table) to another card to be in passenger capacity slot.
     * @param card the card
     * @param moveTo the card to attach to
     */
    public void moveCardToAttachedInPassengerCapacitySlot(PhysicalCard card, PhysicalCard moveTo) {
        if (!getZoneCards(moveTo.getZoneOwner(), Zone.ATTACHED).contains(card)) {
            getZoneCards(card.getZoneOwner(), card.getZone()).remove(card);
            getZoneCards(moveTo.getZoneOwner(), Zone.ATTACHED).add(card);
        }
        card.atLocation(null);
        card.stackOn(null, false, false);
        card.attachTo(moveTo, false, true, false, false, false);
        card.setZone(Zone.ATTACHED);
        card.setZoneOwner(moveTo.getZoneOwner());

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardMoved(card, this);
    }

    /**
     * Attaches the card (already on table) to another card to be in vehicle capacity slot of cargo bay.
     * @param card the card
     * @param moveTo the card to attach to
     */
    public void moveCardToAttachedInVehicleCapacitySlot(PhysicalCard card, PhysicalCard moveTo) {
        if (!getZoneCards(moveTo.getZoneOwner(), Zone.ATTACHED).contains(card)) {
            getZoneCards(card.getZoneOwner(), card.getZone()).remove(card);
            getZoneCards(moveTo.getZoneOwner(), Zone.ATTACHED).add(card);
        }
        card.atLocation(null);
        card.stackOn(null, false, false);
        card.attachTo(moveTo, false, false, true, false, false);
        card.setZone(Zone.ATTACHED);
        card.setZoneOwner(moveTo.getZoneOwner());

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardMoved(card, this);
    }

    /**
     * Attaches the card (already on table) to another card to be in starfighter or TIE capacity slot of cargo bay.
     * @param card the card
     * @param moveTo the card to attach to
     */
    public void moveCardToAttachedInStarfighterOrTIECapacitySlot(PhysicalCard card, PhysicalCard moveTo) {
        if (!getZoneCards(moveTo.getZoneOwner(), Zone.ATTACHED).contains(card)) {
            getZoneCards(card.getZoneOwner(), card.getZone()).remove(card);
            getZoneCards(moveTo.getZoneOwner(), Zone.ATTACHED).add(card);
        }
        card.atLocation(null);
        card.stackOn(null, false, false);
        card.attachTo(moveTo, false, false, false, true, false);
        card.setZone(Zone.ATTACHED);
        card.setZoneOwner(moveTo.getZoneOwner());

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardMoved(card, this);
    }

    /**
     * Attaches the card (already on table) to another card to be in capital starship slot of cargo bay.
     * @param card the card
     * @param moveTo the card to attach to
     */
    public void moveCardToAttachedInCapitalStarshipCapacitySlot(PhysicalCard card, PhysicalCard moveTo) {
        if (!getZoneCards(moveTo.getZoneOwner(), Zone.ATTACHED).contains(card)) {
            getZoneCards(card.getZoneOwner(), card.getZone()).remove(card);
            getZoneCards(moveTo.getZoneOwner(), Zone.ATTACHED).add(card);
        }
        card.atLocation(null);
        card.stackOn(null, false, false);
        card.attachTo(moveTo, false, false, false, false, true);
        card.setZone(Zone.ATTACHED);
        card.setZoneOwner(moveTo.getZoneOwner());

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardMoved(card, this);
    }

    /**
     * Stacks the card on another card.
     * @param card the card
     * @param stackOn the card to stack on
     * @param faceDown true if stacked face down, otherwise false
     * @param asInactive true if card is stacked as inactive state, otherwise card is stacked as supporting state
     * @param fromJediTest5 true if stacked upside-down to be used as substitute destiny via Jedi Test #5, otherwise false
     */
    public void stackCard(PhysicalCard card, PhysicalCard stackOn, boolean faceDown, boolean asInactive, boolean fromJediTest5) {
        card.stackOn(stackOn, asInactive, fromJediTest5);
        addCardToZone(card, faceDown ? Zone.STACKED_FACE_DOWN : Zone.STACKED, stackOn.getZoneOwner());
    }

    /**
     * Stacks the card (already on table) to another card.
     * @param card the card
     * @param stackOn the card to stack on
     * @param faceDown true if stacked face down, otherwise false
     * @param asInactive true if card is stacked as inactive state, otherwise card is stacked as supporting state
     */
    public void relocateCardAsStacked(PhysicalCard card, PhysicalCard stackOn, boolean faceDown, boolean asInactive) {
        if (!getZoneCards(stackOn.getZoneOwner(), faceDown ? Zone.STACKED_FACE_DOWN : Zone.STACKED).contains(card)) {
            getZoneCards(card.getZoneOwner(), card.getZone()).remove(card);
            getZoneCards(stackOn.getZoneOwner(), faceDown ? Zone.STACKED_FACE_DOWN : Zone.STACKED).add(card);
        }
        card.atLocation(null);
        card.attachTo(null, false, false, false, false, false);
        card.stackOn(stackOn, asInactive, false);
        card.setZone(faceDown ? Zone.STACKED_FACE_DOWN : Zone.STACKED);
        card.setZoneOwner(stackOn.getZoneOwner());

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardMoved(card, this);
    }

    /**
     * Relocates the card (already on table) to specified player's side of table.
     * @param card the card
     * @param zoneOwner the player's whose side of table to relocate to
     */
    public void relocateCardToSideOfTable(PhysicalCard card, String zoneOwner) {
        if (!getZoneCards(zoneOwner, Zone.SIDE_OF_TABLE).contains(card)) {
            getZoneCards(card.getZoneOwner(), card.getZone()).remove(card);
            getZoneCards(zoneOwner, Zone.SIDE_OF_TABLE).add(card);
        }
        card.atLocation(null);
        card.attachTo(null, false, false, false, false, false);
        card.stackOn(null, false, false);
        card.setZone(Zone.SIDE_OF_TABLE);
        card.setZoneOwner(zoneOwner);

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardMoved(card, this);
    }

    /**
     * Notifies the user interface to show an animation of the card affecting another card.
     * @param playerPerforming the player performing the action
     * @param card the card
     * @param affectedCard the card being affected
     */
    public void cardAffectsCard(String playerPerforming, PhysicalCard card, PhysicalCard affectedCard) {
        cardAffectsCards(playerPerforming, card, Collections.singleton(affectedCard));
    }

    /**
     * Notifies the user interface to show an animation of the card affecting other cards.
     * @param playerPerforming the player performing the action
     * @param card the card
     * @param affectedCards the cards being affected
     */
    public void cardAffectsCards(String playerPerforming, PhysicalCard card, Collection<PhysicalCard> affectedCards) {
        if (card!=null && !affectedCards.isEmpty()) {
            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardAffectedByCard(playerPerforming, card, affectedCards, this);
        }
    }

    /**
     * Notifies the user interface to show an interrupt being played.
     * @param card the interrupt
     */
    public void interruptPlayed(PhysicalCard card) {
        for (GameStateListener listener : getAllGameStateListeners())
            listener.interruptPlayed(card, this);
    }

    /**
     * Notifies the user interface to show an Epic Event being played.
     * @param card the interrupt
     */
    public void epicEventPlayed(PhysicalCard card) {
        for (GameStateListener listener : getAllGameStateListeners())
            listener.interruptPlayed(card, this);
    }

    /**
     * Notifies the user interface to shown a card on the screen.
     * @param card the card
     */
    public void showCardOnScreen(PhysicalCard card) {
        for (GameStateListener listener : getAllGameStateListeners())
            listener.interruptPlayed(card, this);
    }

    /**
     * Notifies the user interface to shown an animation of a destiny being drawn.
     * @param card the card drawn for destiny
     * @param destinyText the description text to show
     */
    public void destinyDrawn(PhysicalCard card, String destinyText) {
        for (GameStateListener listener : getAllGameStateListeners())
            listener.destinyDrawn(card, this, destinyText);
    }

    /**
     * Notifies the user interface to shown an animation that a card is being used.
     * @param playerPerforming the player performing the action
     * @param card the card
     */
    public void activatedCard(String playerPerforming, PhysicalCard card) {
        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardActivated(playerPerforming, card, this);
    }

    private List<PhysicalCard> getZoneCards(String playerId, Zone zone) {
        if (zone == Zone.RESERVE_DECK || zone == Zone.TOP_OF_RESERVE_DECK)
            return _reserveDecks.get(playerId);
        else if (zone == Zone.FORCE_PILE || zone == Zone.TOP_OF_FORCE_PILE)
            return _forcePiles.get(playerId);
        else if (zone == Zone.USED_PILE || zone == Zone.TOP_OF_USED_PILE)
            return _usedPiles.get(playerId);
        else if (zone == Zone.LOST_PILE || zone == Zone.TOP_OF_LOST_PILE)
            return _lostPiles.get(playerId);
        else if (zone == Zone.HAND)
            return _hands.get(playerId);
        else if (zone == Zone.SABACC_HAND || zone == Zone.REVEALED_SABACC_HAND)
            return _sabaccHands.get(playerId);
        else if (zone == Zone.UNRESOLVED_DESTINY_DRAW || zone == Zone.TOP_OF_UNRESOLVED_DESTINY_DRAW)
            return _unresolvedDestinyDraws.get(playerId);
        else if (zone == Zone.VOID)
            return _voids.get(playerId);
        else if (zone == Zone.OUT_OF_PLAY)
            return _outOfPlayPiles.get(playerId);
        else if (zone == Zone.STACKED || zone == Zone.STACKED_FACE_DOWN)
            return _stacked.get(playerId);
        else if (zone == Zone.OUTSIDE_OF_DECK)
            return _outsideOfDecks.get(playerId);
        else if (zone == Zone.SIDE_OF_TABLE_NOT_IN_PLAY || zone == Zone.SIDE_OF_TABLE_FACE_DOWN_NOT_IN_PLAY)
            return _sideOfTableNotInPlay.get(playerId);
        else
            return _inPlay;
    }

    public boolean isZoneEmpty(String playerId, Zone zone) {
        return getZoneCards(playerId, zone).isEmpty();
    }

    // If something like an insert card triggers, the pile may change, so we cannot shortcut anymore.
    public void setInsertCardFound(boolean insertFound) {
        _insertFound = insertFound;
    }

    // If something like an insert card triggers, the pile may change, so we cannot shortcut anymore.
    public boolean isInsertCardFound() {
        return _insertFound;
    }

    /**
     * Sets if a Podrace has started.
     * @param sourceCard the card that started the Podrace
     */
    public void setPodraceStarted(PhysicalCard sourceCard) {
        _podraceInitiatedByCard = sourceCard;
    }

    /**
     * Determines if during a Podrace.
     * @return true or false
     */
    public boolean isDuringPodrace() {
        return _podraceInitiatedByCard != null;
    }

    /**
     * Determines if during a Podrace that was started by the specified card.
     * @param card the card
     * @return true or false
     */
    public boolean isDuringPodraceStartedByCard(PhysicalCard card) {
        return _podraceInitiatedByCard != null && card.getCardId() == _podraceInitiatedByCard.getCardId();
    }

    /**
     * Sets if a Podrace is finishing.
     */
    public void setPodraceFinishing() {
        _podraceFinishing = true;
    }

    /**
     * Determines if Podrace is finishing.
=     * @return true or false
     */
    public boolean isPodraceFinishing() {
        return _podraceFinishing;
    }

    /**
     * Sets if a Podrace has finished.
     */
    public void setPodraceFinished() {
        _game.getModifiersEnvironment().removeEndOfPodrace();
        _podraceInitiatedByCard = null;
        _podraceFinishing = false;
    }

    /**
     * Sets the Podrace winner.
     * @param winner the winner
     */
    public void setPodraceWinner(String winner) {
        _podraceWinner = winner;
    }

    /**
     * Gets the Podrace winner.
     * @return the winner, or null
     */
    public String getPodraceWinner() {
        return _podraceWinner;
    }

    /**
     * Sets the Podrace loser.
     * @param loser the loser
     */
    public void setPodraceLoser(String loser) {
        _podraceLoser = loser;
    }

    /**
     * Gets the Podrace loser.
     * @return the loser, or null
     */
    public String getPodraceLoser() {
        return _podraceLoser;
    }

    /**
     * Sets the Podrace winner race total.
     * @param raceTotal the winner race total
     */
    public void setPodraceWinnerRaceTotal(float raceTotal) {
        _podraceWinnerRaceTotal = raceTotal;
    }

    /**
     * Gets the Podrace winner race total.
     * @return the winner race total
     */
    public float getWinnerRaceTotal() {
        return _podraceWinnerRaceTotal;
    }

    /**
     * Sets the Podrace loser race total.
     * @param raceTotal the loser race total
     */
    public void setPodraceLoserRaceTotal(float raceTotal) {
        _podraceLoserRaceTotal = raceTotal;
    }

    /**
     * Gets the Podrace loser race total.
     * @return the loser race total
     */
    public float getLoserRaceTotal() {
        return _podraceLoserRaceTotal;
    }

    // To minimize updates sent to listener and card pile actions (like activating multiple Force),
    // only the first remove and last add are send as updates.  However, if something like an insert card
    // triggers, the pile may change, so we cannot shortcut anymore.
    public void setSkipListenerUpdateAllowed(boolean allowed) {
        _skipListenerUpdateAllowed = allowed;
    }

    // To minimize updates sent to listener and card pile actions (like activating multiple Force),
    // only the first remove and last add are send as updates.  However, if something like an insert card
    // triggers, the pile may change, so we cannot shortcut anymore.
    public boolean isSkipListenerUpdateAllowed() {
        return _skipListenerUpdateAllowed;
    }

    public void removeCardFromZone(PhysicalCard card) {
        removeCardsFromZone(Collections.singleton(card));
    }

    public void removeCardFromZone(PhysicalCard card, boolean skipListenerUpdateOnRemove, boolean skipListenerUpdateOnAdd) {
        removeCardsFromZone(Collections.singleton(card), skipListenerUpdateOnRemove, skipListenerUpdateOnAdd);
    }

    public void removeCardsFromZone(Collection<PhysicalCard> cards) {
        removeCardsFromZone(cards, false, false);
    }

    public void removeCardsFromZone(Collection<PhysicalCard> cards, boolean skipListenerUpdateOnRemove, boolean skipListenerUpdateOnAdd) {
        if (cards.isEmpty())
            return;

        setInsertCardFound(false);
        setSkipListenerUpdateAllowed(skipListenerUpdateOnAdd || skipListenerUpdateOnRemove);

        // Cards to remove from user interface
        List<PhysicalCard> cardsToRemove = new ArrayList<PhysicalCard>();

        for (PhysicalCard card : cards) {
            if (card.getZone().isPublic() || card.getZone().isVisibleByOwner()) {
                cardsToRemove.add(card);
            }
            List<PhysicalCard> zoneCards = getZoneCards(card.getZoneOwner(), card.getZone());
            if (!zoneCards.contains(card)) {
                throw new UnsupportedOperationException(GameUtils.getFullName(card) + " was not found in " + card.getZone());
            }
        }

        // List of "top of pile" cards to tell listener about
        Collection<PhysicalCard> cardsToAdd = new ArrayList<PhysicalCard>();
        Set<Integer> locationIndexesToRemove = new HashSet<Integer>();
        boolean needToRefreshLocationIndexes = false;

        for (PhysicalCard card : cards) {
            Zone zone = card.getZone();

            // Any "while active in play" modifiers are removed
            stopAffecting(card);

            List<PhysicalCard> zoneCards = getZoneCards(card.getZoneOwner(), zone);
            zoneCards.remove(card);

            // Special case for "location" and "converted location" zone, need to remove from location layout too
            if (zone == Zone.LOCATIONS) {
                Collection<PhysicalCard> convertedLocations = _locationsLayout.getConvertedLocationsOfTopLocation(card);
                Integer locationZoneIndexToRemove = card.getLocationZoneIndex();
                _locationsLayout.removeLocationFromLayout(_game, _game.getModifiersQuerying(), card, false);
                // Need to show the new top location (if any)
                for (PhysicalCard convertedLocation : convertedLocations) {
                    if (convertedLocation.getZone() == Zone.LOCATIONS && !cardsToRemove.contains(convertedLocation)) {
                        cardsToAdd.add(convertedLocation);
                        locationZoneIndexToRemove = null;
                    }
                }
                if (locationZoneIndexToRemove != null) {
                    locationIndexesToRemove.add(locationZoneIndexToRemove);
                    needToRefreshLocationIndexes = true;
                }
            }
            else if (zone == Zone.CONVERTED_LOCATIONS) {
                _locationsLayout.removeLocationFromLayout(_game, _game.getModifiersQuerying(), card, false);
            }

            // Special case for "top of pile" card
            if (!zoneCards.isEmpty() &&
                (zone == Zone.TOP_OF_RESERVE_DECK || zone == Zone.TOP_OF_FORCE_PILE || zone == Zone.TOP_OF_USED_PILE || zone == Zone.TOP_OF_LOST_PILE || zone == Zone.TOP_OF_UNRESOLVED_DESTINY_DRAW))
            {
                // set zone of new "top of pile" card
                zoneCards.get(0).setZone(zone);
                cardsToAdd.add(zoneCards.get(0));

                // if card, or new top card, is an 'insert' card then disallow shortcut
                if (card.isInserted() || zoneCards.get(0).isInserted()) {
                    setInsertCardFound(true);
                    setSkipListenerUpdateAllowed(false);
                }
            }

            card.atLocation(null);
            card.attachTo(null, false, false, false, false, false);
            card.stackOn(null, false, false);
        }

        if (needToRefreshLocationIndexes) {
            _locationsLayout.refreshLocationIndexes();
        }
        removeLocationIndexesFromTable(locationIndexesToRemove);

        if (!skipListenerUpdateOnRemove || !isSkipListenerUpdateAllowed()) {
            for (GameStateListener listener : getAllGameStateListeners()) {
                listener.cardsRemoved(null, cardsToRemove);
            }
        }

        for (PhysicalCard card : cards) {
            clearCardStats(card);
            cardsToAdd.remove(card);
        }

        _tableChangedSinceStatsSent = true;

        // tell listener about new "top of pile cards" or "top locations"
        for (PhysicalCard card : cardsToAdd) {
            if (!skipListenerUpdateOnAdd || !isSkipListenerUpdateAllowed()) {
                for (GameStateListener listener : getAllGameStateListeners())
                    listener.cardCreated(card, this, false);
            }
        }
    }

    public void copyCardStats(PhysicalCard fromCard, PhysicalCard toCard) {
        toCard.setZone(fromCard.getZone());
        toCard.setFlipped(fromCard.isFlipped());
        toCard.setInverted(fromCard.isInverted());
        toCard.setSideways(fromCard.isSideways());
        toCard.setBlownAway(fromCard.isBlownAway());
        toCard.setInserted(fromCard.isInserted());
        toCard.setInsertCardRevealed(fromCard.isInsertCardRevealed());
        toCard.setHit(fromCard.isHit());
        toCard.setDisarmed(fromCard.isDisarmed());
        toCard.setDamaged(fromCard.isDamaged());
        toCard.setConcealed(fromCard.isConcealed());
        toCard.setCollapsed(fromCard.isCollapsed());
        toCard.setCrashed(fromCard.isCrashed());
        toCard.setIonization(fromCard.getIonization());
        toCard.setLeavingTable(fromCard.isLeavingTable());
        toCard.setGameTextCanceled(fromCard.isGameTextCanceled());
        toCard.setLocationGameTextCanceledForPlayer(fromCard.isLocationGameTextCanceledForPlayer(_darkSidePlayer), _darkSidePlayer);
        toCard.setLocationGameTextCanceledForPlayer(fromCard.isLocationGameTextCanceledForPlayer(_lightSidePlayer), _lightSidePlayer);
        toCard.setSuspended(fromCard.isSuspended());
        toCard.setBinaryOff(fromCard.isBinaryOff());
        toCard.setMouthClosed(fromCard.isMouthClosed());
        toCard.setUndercover(fromCard.isUndercover());
        toCard.setMissing(fromCard.isMissing());
        toCard.setCapturedStarship(fromCard.isCapturedStarship());
        toCard.setCaptive(fromCard.isCaptive());
        toCard.setImprisoned(fromCard.isImprisoned());
        toCard.setFrozen(fromCard.isFrozen());
        toCard.setProbeCard(fromCard.isProbeCard());
        toCard.setHatredCard(fromCard.isHatredCard());
        toCard.setEnslavedCard(fromCard.isEnslavedCard());
        toCard.setCoaxiumCard(fromCard.isCoaxiumCard());
        toCard.setLiberationCard(fromCard.isLiberationCard());
        toCard.setBluffCard(fromCard.isBluffCard());
        toCard.setCombatCard(fromCard.isCombatCard());
        toCard.setSpaceSlugBelly(fromCard.isSpaceSlugBelly());
        toCard.setMakingBombingRun(fromCard.isMakingBombingRun());
        toCard.setDejarikHologramAtHolosite(fromCard.isDejarikHologramAtHolosite());
        toCard.setPlayCardOptionId(fromCard.getPlayCardOptionId());
        toCard.setLatestInPlayForfeitValue(fromCard.getLatestInPlayForfeitValue());
        toCard.setMovementDirection(fromCard.getMovementDirection());
        toCard.setRaceDestinyForPlayer(fromCard.getRaceDestinyForPlayer());
    }

    public void clearCardStats(PhysicalCard card) {
        card.setZone(null);
        card.setFlipped(false);
        card.setInverted(false);
        card.setSideways(false);
        card.setBlownAway(false);
        card.setInserted(false);
        card.setInsertCardRevealed(false);
        card.setHit(false);
        card.setDisarmed(false);
        card.setDamaged(false);
        card.setConcealed(false);
        card.setCollapsed(false);
        card.setCrashed(false);
        card.resetIonization();
        card.setLeavingTable(false);
        card.setGameTextCanceled(false);
        card.setLocationGameTextCanceledForPlayer(false, _darkSidePlayer);
        card.setLocationGameTextCanceledForPlayer(false, _lightSidePlayer);
        card.setSuspended(false);
        card.setBinaryOff(false);
        card.setMouthClosed(false);
        card.setUndercover(false);
        card.setMissing(false);
        card.setCapturedStarship(false);
        card.setCaptive(false);
        card.setImprisoned(false);
        card.setFrozen(false);
        card.setProbeCard(false);
        card.setHatredCard(false);
        card.setEnslavedCard(false);
        card.setLiberationCard(false);
        card.setBluffCard(false);
        card.setCombatCard(false);
        card.setSpaceSlugBelly(false);
        card.setMakingBombingRun(false);
        card.setDejarikHologramAtHolosite(false);
        card.setPlayCardOptionId(null);
        card.setLatestInPlayForfeitValue(0);
        card.setMovementDirection(MovementDirection.NONE);
        card.setJediTestStatus(null);
        card.setUtinniEffectStatus(null);
        card.setRaceDestinyForPlayer(null);
    }

    /**
     * Recirculates the Used Pile under the Reserved Deck for the specified player.
     * @param player the player
     */
    public void recirculate(String player) {
        List<PhysicalCard> usedPile = getZoneCards(player, Zone.USED_PILE);
        List<PhysicalCard> cardsToRecirculate = new ArrayList<PhysicalCard>(usedPile);

        // Remove cards from the bottom (so user interface is only updated once when top card is reached
        while (!usedPile.isEmpty()) {
            removeCardFromZone(usedPile.get(usedPile.size() - 1), usedPile.size() > 1, false);
        }

        // Reverse order if Used Pile was face up
        if (_usedPilesTurnedOver) {
            Collections.reverse(cardsToRecirculate);
        }

        boolean firstCard = true;
        while (!cardsToRecirculate.isEmpty()) {
            PhysicalCard card = cardsToRecirculate.get(0);

            // Add card to bottom of Reserve Deck
            addCardToZone(card, Zone.RESERVE_DECK, player, true, false, !firstCard);
            cardsToRecirculate.remove(card);
            firstCard = false;
        }
    }

    /**
     * Places the cards from a card pile on top of another card pile for the specified player.
     * @param player the player
     * @param fromPile the card pile to place on the other card pile
     * @param toPile the card pile to move the cards to
     */
    public void placeCardPileOnCardPile(String player, Zone fromPile, Zone toPile) {
        List<PhysicalCard> fromPileCards = getZoneCards(player, fromPile);
        boolean firstCard = true;
        while (!fromPileCards.isEmpty()) {
            boolean lastCard = fromPileCards.size()==1;

            // Remove bottom card of "from pile"
            PhysicalCard card = fromPileCards.get(fromPileCards.size()-1);
            removeCardsFromZone(Collections.singleton(card), !lastCard, !lastCard);
            fromPileCards = getZoneCards(player, fromPile);

            // Add card to top of "top pile"
            addCardToTopOfZone(card, toPile, player, true, !firstCard, !lastCard);
            firstCard = false;
        }
    }


    public void turnOverLostPile(String playerId, boolean upsideDown) {
        if (playerId.equals(_darkSidePlayer)) {
            if (_darkSideLostPileTurnedOver == upsideDown)
                return;
            else
                _darkSideLostPileTurnedOver = upsideDown;
        }
        else {
            if (_lightSideLostPileTurnedOver == upsideDown)
                return;
            else
                _lightSideLostPileTurnedOver = upsideDown;
        }

        List<PhysicalCard> lostPile = _lostPiles.get(playerId);
        if (!lostPile.isEmpty()) {
            // Tell game listener to remove top card before turning over
            PhysicalCard card = lostPile.get(0);
            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardsRemoved(playerId, Collections.singleton(card));

            card.setZone(Zone.LOST_PILE);
            Collections.reverse(lostPile);
            card = lostPile.get(0);
            lostPile.get(0).setZone(Zone.TOP_OF_LOST_PILE);
            _tableChangedSinceStatsSent = true;

            // Tell game listener to create top card after turning pile over
            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardCreated(card, this, false);
        }
    }

    public boolean isLostPileTurnedOver(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            return _darkSideLostPileTurnedOver;
        else
            return _lightSideLostPileTurnedOver;
    }

    public void turnOverUsedPiles(boolean faceUp) {
        if (_usedPilesTurnedOver == faceUp)
            return;

        _usedPilesTurnedOver = faceUp;
        turnOverUsedPile(_darkSidePlayer);
        turnOverUsedPile(_lightSidePlayer);
    }

    private void turnOverUsedPile(String playerId) {

        List<PhysicalCard> usedPile = _usedPiles.get(playerId);
        if (!usedPile.isEmpty()) {
            // Tell game listener to remove top card before turning over
            PhysicalCard card = usedPile.get(0);
            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardsRemoved(playerId, Collections.singleton(card));

            card.setZone(Zone.USED_PILE);
            Collections.reverse(usedPile);
            card = usedPile.get(0);
            usedPile.get(0).setZone(Zone.TOP_OF_USED_PILE);
            _tableChangedSinceStatsSent = true;

            // Tell game listener to create top card after turning pile over
            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardCreated(card, this, false);
        }
    }

    public boolean isTopCardOfReserveDeckRevealed(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            return _darkSideTopOfReserveDeckTurnedOver;
        else
            return _lightSideTopOfReserveDeckTurnedOver;
    }

    public void turnOverTopCardOfReserveDeck(String playerId, boolean faceUp) {
        if (playerId.equals(_darkSidePlayer)) {
            if (_darkSideTopOfReserveDeckTurnedOver == faceUp)
                return;
            else
                _darkSideTopOfReserveDeckTurnedOver = faceUp;
        }
        else {
            if (_lightSideTopOfReserveDeckTurnedOver == faceUp)
                return;
            else
                _lightSideTopOfReserveDeckTurnedOver = faceUp;
        }

        _tableChangedSinceStatsSent = true;
        PhysicalCard card = _reserveDecks.get(playerId).get(0);
        if (card != null) {
            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardTurnedOver(card, this);
        }
    }


    public boolean isCardPileFaceUp(String playerId, Zone cardPile) {
        if (cardPile == Zone.USED_PILE) {
            return isUsedPilesTurnedOver();
        }
        else if (cardPile == Zone.LOST_PILE) {
            return !isLostPileTurnedOver(playerId);
        }
        else if (cardPile == Zone.TOP_OF_RESERVE_DECK) {
            return isTopCardOfReserveDeckRevealed(playerId);
        }
        else {
            return false;
        }
    }

    public boolean isUsedPilesTurnedOver() {
        return _usedPilesTurnedOver;
    }

    public void invertCard(SwccgGame game, PhysicalCard card, boolean upsideDown) {
        if (card.isInverted()==upsideDown)
            return;

        card.setInverted(upsideDown);
        if (card.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
            game.getGameState().reapplyAffectingForCard(game, card);
        }

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardRotated(card, this);
    }

    public void flipCard(SwccgGame game, PhysicalCard card, boolean toBack) {
        if (card.getZone() != Zone.STACKED && card.getZone() != Zone.STACKED_FACE_DOWN
                && card.getZone() != Zone.SIDE_OF_TABLE_NOT_IN_PLAY && card.getZone() != Zone.SIDE_OF_TABLE_FACE_DOWN_NOT_IN_PLAY
                && card.isFlipped() == toBack)
            return;

        // Update zone if needed
        if (card.getZone() == Zone.STACKED || card.getZone() == Zone.STACKED_FACE_DOWN) {
            card.setZone(toBack ? Zone.STACKED_FACE_DOWN : Zone.STACKED);
        }
        else if (card.getZone() == Zone.SIDE_OF_TABLE_NOT_IN_PLAY || card.getZone() == Zone.SIDE_OF_TABLE_FACE_DOWN_NOT_IN_PLAY) {
            card.setZone(toBack ? Zone.SIDE_OF_TABLE_FACE_DOWN_NOT_IN_PLAY : Zone.SIDE_OF_TABLE_NOT_IN_PLAY);
        }
        else {
            card.setFlipped(toBack);
        }

        game.getGameState().reapplyAffectingForCard(game, card);

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardFlipped(card, this);
    }

    public void flipBlownAwayLocations(SwccgGame game, Collection<PhysicalCard> locations) {
        for (PhysicalCard location : locations) {

            location.setBlownAway(true);
            location.setCollapsed(false);
            location.setSideways(location.getBlueprint().getCardSubtype() == CardSubtype.SITE);
            game.getGameState().stopAffecting(location);

            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardFlipped(location, this);
        }
    }

    public void turnCardSideways(SwccgGame game, PhysicalCard card, boolean restore) {
        if (card.isSideways()!=restore)
            return;

        card.setSideways(!restore);

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardRotated(card, this);
    }

    /**
     * This is much faster since it doesn't figure out the orders, etc.
     * @return the top locations
     */
    public List<PhysicalCard> getTopLocations() {
        List<PhysicalCard> topLocations = new ArrayList<PhysicalCard>();
        for (PhysicalCard physicalCard : _inPlay) {
            if (physicalCard.getZone() == Zone.LOCATIONS)
                topLocations.add(physicalCard);
        }

        return topLocations;
    }

    public List<PhysicalCard> getLocationsInOrder() {
        return _locationsLayout.getTopLocationsInOrder();
    }

    public PhysicalCard getLocationAtTopOfConvertedLocation(PhysicalCard location) {
        if (location.getZone()!=Zone.CONVERTED_LOCATIONS)
            return null;

        return _locationsLayout.getTopLocationOfConvertedLocation(location);
    }

    public List<PhysicalCard> getConvertedLocationsUnderTopLocation(PhysicalCard topLocation) {
        if (topLocation.getZone() != Zone.LOCATIONS)
            return Collections.emptyList();

        return _locationsLayout.getConvertedLocationsOfTopLocation(topLocation);
    }

    public PhysicalCard getLocationFromMovementDirection(PhysicalCard location, MovementDirection movementDirection) {
        if (movementDirection == MovementDirection.LEFT)
            return getLocationToLeftOf(location);
        else if (movementDirection == MovementDirection.RIGHT)
            return getLocationToRightOf(location);
        else
            return null;
    }

    public PhysicalCard getLocationToLeftOf(PhysicalCard location) {
        List<PhysicalCard> locationsInOrder = getLocationsInOrder();
        for (int i=1; i<locationsInOrder.size(); i++) {
            if (locationsInOrder.get(i).getCardId()==location.getCardId()) {
                return locationsInOrder.get(i-1);
            }
        }
        return null;
    }

    public PhysicalCard getLocationToRightOf(PhysicalCard location) {
        List<PhysicalCard> locationsInOrder = getLocationsInOrder();
        for (int i=0; i<locationsInOrder.size()-1; i++) {
            if (locationsInOrder.get(i).getCardId()==location.getCardId()) {
                return locationsInOrder.get(i+1);
            }
        }
        return null;
    }

    /**
     * Gets the deployment options for deploying the specified location.
     * @param game the game
     * @param card the location
     * @param targetSystem the system the card must be deployed to
     * @param specialLocationConditions a filter for special conditions that deployed location must satisfy, or null
     * @return the list of deployment options
     */
    public List<LocationPlacement> getLocationPlacement(SwccgGame game, PhysicalCard card, String targetSystem, Filter specialLocationConditions) {
        return _locationsLayout.getPlacesToDeployLocation(game, game.getModifiersQuerying(), card, targetSystem, specialLocationConditions);
    }

    /**
     * Places the location on the table according to the specified placement.
     * @param game the game
     * @param card the location
     * @param placement the placement
     */
    public void addLocationToTable(SwccgGame game, PhysicalCard card, LocationPlacement placement) {

        if (placement.getDirection() == LocationPlacementDirection.REPLACE) {
            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardsRemoved(card.getOwner(), Collections.singleton(placement.getOtherCard()));

            stopAffecting(placement.getOtherCard());
            placement.getOtherCard().setZone(Zone.CONVERTED_LOCATIONS);
            placement.getOtherCard().setWhileInPlayData(null);
            // Give new location the card ID of the converted location
            int prevCardId = placement.getOtherCard().getCardId();
            assignNewCardId(placement.getOtherCard());
            assignCardId(card, prevCardId);
            // Set whether Space Slug Belly to same as converted location
            card.setSpaceSlugBelly(placement.getOtherCard().isSpaceSlugBelly());
        }
        else {
            // Set to new card ID
            assignNewCardId(card);
        }

        // Set parent info for generic locations and non-unique starship/vehicle sites
        if (Filters.or(Filters.asteroid_sector, Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly).accepts(game.getGameState(), game.getModifiersQuerying(), card))
            card.setSystemOrbited(placement.getParentSystem());
        else
            card.setPartOfSystem(placement.getParentSystem());
        card.setRelatedStarshipOrVehicle(placement.getParentStarshipOrVehicleCard());

        // If system, set the parsec and orbited planet
        if (card.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM) {
            if (placement.getDirection() == LocationPlacementDirection.REPLACE) {
                card.setParsec(placement.getOtherCard().getParsec());
                card.setSystemOrbited(placement.getOtherCard().getSystemOrbited());
            }
            else {
                String orbitingSystem = card.getBlueprint().getDeploysOrbitingSystem();
                if (orbitingSystem != null) {
                    PhysicalCard systemOrbited = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.system, Filters.title(orbitingSystem)));
                    card.setParsec(systemOrbited.getParsec());
                }
                else {
                    card.setParsec(card.getBlueprint().getParsec());
                }
                card.setSystemOrbited(orbitingSystem);
            }
        }

        // Add location to the layout map
        _locationsLayout.deployLocationToLayout(game, game.getModifiersQuerying(), card, placement, false);
        _locationsLayout.refreshLocationIndexes();
        card.setZone(Zone.LOCATIONS);

        // Add card to "in play"
        List<PhysicalCard> zoneCards = getZoneCards(card.getZoneOwner(), Zone.LOCATIONS);
        zoneCards.add(card);

        if (placement.getDirection() == LocationPlacementDirection.REPLACE) {
            List<PhysicalCard> attachedCardList = getAttachedCards(placement.getOtherCard(), true);
            List<PhysicalCard> stackedCardList = getStackedCards(placement.getOtherCard());
            List<PhysicalCard> atLocationCardList = getCardsAtLocation(placement.getOtherCard());

            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardReplaced(card, this);

            // Transfer attached cards to the converted location
            for (PhysicalCard attachedCard : attachedCardList) {
                moveCardToAttached(attachedCard, card);
            }

            // Transfer stacked cards to the converted location
            for (PhysicalCard stackedCard : stackedCardList) {
                stackedCard.stackOn(card, stackedCard.isStackedAsInactive(), stackedCard.isStackedAsViaJediTest5());
            }

            // Update cards at location to the converted location
            for (PhysicalCard cardAtLocation : atLocationCardList) {
                cardAtLocation.atLocation(card);
            }
        }
        else {
            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardCreated(card, this, false);
        }

        startAffecting(_game, card);
    }

    /**
     * Temporarily places the location on the table (but not shown in the User Interface) according to the specified placement,
     * checks if the location fulfills special conditions, and then removes the location from the table. This is needed,
     * for example, when checking if a location can be deployed as a battleground, since it needs to be checked if the
     * location would be a battleground when it is actually on the table.
     * @param game the game
     * @param card the location
     * @param placement the placement
     */
    public boolean checkLocationConditionsWithLocationAddedToTable(SwccgGame game, PhysicalCard card, LocationPlacement placement, Filter specialLocationConditions) {
        // Remember things about the locations
        int originalCardId = card.getCardId();
        Zone originalZone = card.getZone();
        String originalPartOfSystem = card.getPartOfSystem();
        String originalSystemOrbited = card.getSystemOrbited();
        PhysicalCard otherCard = placement.getOtherCard();
        int otherCardPrevCardId = otherCard != null ? otherCard.getCardId() : -1;
        WhileInPlayData otherCardWhileInPlayData = null;

        // Temp values (do not increment id counters)
        int nextCardId = _nextCardId + 1;

        if (placement.getDirection() == LocationPlacementDirection.REPLACE && otherCard != null) {
            stopAffecting(otherCard);
            otherCard.setZone(Zone.CONVERTED_LOCATIONS);
            otherCardWhileInPlayData = otherCard.getWhileInPlayData();
            otherCard.setWhileInPlayData(null);
            // Give new location the card ID of the converted location
            assignCardId(placement.getOtherCard(), nextCardId);
            assignCardId(card, otherCardPrevCardId);
            // Set whether Space Slug Belly to same as converted location
            card.setSpaceSlugBelly(otherCard.isSpaceSlugBelly());
        }
        else {
            // Set to new location id
            assignCardId(card, nextCardId);
        }

        // Set parent info for generic locations and non-unique starship/vehicle sites
        if (Filters.or(Filters.asteroid_sector, Filters.Big_One_Asteroid_Cave_Or_Space_Slug_Belly).accepts(game.getGameState(), game.getModifiersQuerying(), card))
            card.setSystemOrbited(placement.getParentSystem());
        else
            card.setPartOfSystem(placement.getParentSystem());
        card.setRelatedStarshipOrVehicle(placement.getParentStarshipOrVehicleCard());

        // If system, set the parsec and orbited planet
        if (card.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM) {
            if (placement.getDirection() == LocationPlacementDirection.REPLACE && otherCard != null) {
                card.setParsec(otherCard.getParsec());
                card.setSystemOrbited(otherCard.getSystemOrbited());
            }
            else {
                String orbitingSystem = card.getBlueprint().getDeploysOrbitingSystem();
                if (orbitingSystem != null) {
                    PhysicalCard systemOrbited = Filters.findFirstFromTopLocationsOnTable(game, Filters.and(Filters.system, Filters.title(orbitingSystem)));
                    card.setParsec(systemOrbited.getParsec());
                }
                else {
                    card.setParsec(card.getBlueprint().getParsec());
                }
                card.setSystemOrbited(orbitingSystem);
            }
        }

        // Add location to the layout map
        _locationsLayout.deployLocationToLayout(game, game.getModifiersQuerying(), card, placement, true);
        _locationsLayout.refreshLocationIndexes();
        card.setZone(Zone.LOCATIONS);

        // Add card to "in play". (It is not removed from original zone cards list, so we do not need to try to put it back in the same place.)
        boolean addedToInPlayZone = false;
        List<PhysicalCard> zoneCards = getZoneCards(card.getZoneOwner(), Zone.LOCATIONS);
        if (!zoneCards.contains(card)) {
            zoneCards.add(card);
            addedToInPlayZone = true;
        }

        if (placement.getDirection() == LocationPlacementDirection.REPLACE) {
            List<PhysicalCard> attachedCardList = getAttachedCards(otherCard, true);
            List<PhysicalCard> stackedCardList = getStackedCards(otherCard);
            List<PhysicalCard> atLocationCardList = getCardsAtLocation(otherCard);

            // Transfer attached cards to the converted location
            for (PhysicalCard attachedCard : attachedCardList) {
                attachedCard.atLocation(null);
                attachedCard.setZone(Zone.ATTACHED);
                attachedCard.attachTo(card, false, false, false, false, false);
                attachedCard.stackOn(null, false, false);
                attachedCard.setZoneOwner(card.getZoneOwner());
            }

            // Transfer stacked cards to the converted location
            for (PhysicalCard stackedCard : stackedCardList) {
                stackedCard.stackOn(card, stackedCard.isStackedAsInactive(), stackedCard.isStackedAsViaJediTest5());
            }

            // Update cards at location to the converted location
            for (PhysicalCard cardAtLocation : atLocationCardList) {
                cardAtLocation.atLocation(card);
            }
        }

        startAffecting(_game, card);

        // Check if the location fulfills the special conditions
        boolean retVal = specialLocationConditions.accepts(game, card);

        // Put everything back the way it was
        stopAffecting(card);
        assignCardId(card, originalCardId);
        card.setLocationZoneIndex(0);
        if (placement.getDirection() == LocationPlacementDirection.REPLACE && otherCard != null) {
            _allCards.remove(otherCard.getCardId());
            assignCardId(otherCard, otherCardPrevCardId);
            otherCard.setZone(Zone.LOCATIONS);
            otherCard.setWhileInPlayData(otherCardWhileInPlayData);
        }
        if (card.getBlueprint().getCardSubtype() == CardSubtype.SYSTEM) {
            card.setParsec(card.getBlueprint().getParsec());
        }
        card.setPartOfSystem(originalPartOfSystem);
        card.setSystemOrbited(originalSystemOrbited);

        // Remove the location from the layout
        _locationsLayout.removeLocationFromLayout(game, game.getModifiersQuerying(), card, true);
        _locationsLayout.refreshLocationIndexes();
        card.setZone(originalZone);
        if (addedToInPlayZone) {
            zoneCards.remove(card);
        }

        if (placement.getDirection() == LocationPlacementDirection.REPLACE && otherCard != null) {
            List<PhysicalCard> attachedCardList = getAttachedCards(card, true);
            List<PhysicalCard> stackedCardList = getStackedCards(card);
            List<PhysicalCard> atLocationCardList = getCardsAtLocation(card);

            // Transfer attached cards to the converted location
            for (PhysicalCard attachedCard : attachedCardList) {
                attachedCard.atLocation(null);
                attachedCard.setZone(Zone.ATTACHED);
                attachedCard.attachTo(otherCard, false, false, false, false, false);
                attachedCard.stackOn(null, false, false);
                attachedCard.setZoneOwner(otherCard.getZoneOwner());
            }

            // Transfer stacked cards to the converted location
            for (PhysicalCard stackedCard : stackedCardList) {
                stackedCard.stackOn(otherCard, stackedCard.isStackedAsInactive(), stackedCard.isStackedAsViaJediTest5());
            }

            // Update cards at location to the converted location
            for (PhysicalCard cardAtLocation : atLocationCardList) {
                cardAtLocation.atLocation(otherCard);
            }
        }

        return retVal;
    }

    /**
     * Removes the location indexes from the table. This is for when locations (and all cards at those locations) are actually
     * removed from the table. Example: When a planet is 'blown away' related sites, etc. are completely removed from the table.
     * @param locationIndexes the location indexes to lose from table
     */
    private void removeLocationIndexesFromTable(Set<Integer> locationIndexes) {
        if (locationIndexes.isEmpty())
            return;

        // Updates the location indexes and removes any empty starship/vehicle layouts in the location layout now that
        // locations were removed.
        _locationsLayout.removeEmptyStarshipOrVehicleLayouts();

        // Tell the user interface to remove the specified location indexes (in descending order), which should not
        // contain any cards at this point.
        for (GameStateListener listener : getAllGameStateListeners())
            listener.locationsRemoved(locationIndexes);
    }

    public void addCardToZone(PhysicalCard card, Zone zone, String zoneOwner) {
        addCardToZone(card, zone, zoneOwner, false, false, false, false, null);
    }

    public void addCardToZone(PhysicalCard card, Zone zone, String zoneOwner, boolean skipListenerUpdateOnRemove, boolean skipListenerUpdateOnAdd) {
        addCardToZone(card, zone, zoneOwner, false, false, skipListenerUpdateOnRemove, skipListenerUpdateOnAdd, null);
    }

    public void addCardToZone(PhysicalCard card, Zone zone, String zoneOwner, boolean skipNewCardId, boolean skipListenerUpdateOnRemove, boolean skipListenerUpdateOnAdd) {
        addCardToZone(card, zone, zoneOwner, skipNewCardId, false, skipListenerUpdateOnRemove, skipListenerUpdateOnAdd, null);
    }

    public void addCardToSpecificPositionInZone(PhysicalCard card, Zone zone, String zoneOwner, Integer indexOf) {
        addCardToZone(card, zone, zoneOwner, false, indexOf == 0, false, false, indexOf);
    }

    public void addCardToTopOfZone(PhysicalCard card, Zone zone, String zoneOwner) {
        addCardToZone(card, zone, zoneOwner, false, true, false, false, null);
    }

    public void addCardToTopOfZone(PhysicalCard card, Zone zone, String zoneOwner, boolean skipNewCardId, boolean skipListenerUpdateOnRemove, boolean skipListenerUpdateOnAdd) {
        addCardToZone(card, zone, zoneOwner, skipNewCardId, true, skipListenerUpdateOnRemove, skipListenerUpdateOnAdd, null);
    }

    private void addCardToZone(PhysicalCard card, Zone zone, String zoneOwner, boolean skipNewCardId, boolean top, boolean skipListenerUpdateOnRemove, boolean skipListenerUpdateOnAdd, Integer indexOf) {
        if (!zone.isInPlay() && !card.isInserted() && zone != Zone.VOID) {
            card.setWhileInPlayData(null);
            card.clearTargetedCards();
        }
        if (zone != Zone.VOID && !skipNewCardId) {
            assignNewCardId(card);
        }

        List<PhysicalCard> zoneCards = getZoneCards(zoneOwner, zone);
        Zone cardZone = zone;
        Zone topZone = GameUtils.getZoneTopFromZone(zone);
        boolean skipCreateCard = false;

        // Special case for top of each pile
        if (zone != topZone) {
            if (zoneCards.isEmpty()) {
                cardZone = topZone;
            }
            else if (top) {
                if (!skipListenerUpdateOnRemove || !isSkipListenerUpdateAllowed()) {
                    // Tell listener about card that is no longer "top of pile"
                    for (GameStateListener listener : getAllGameStateListeners())
                        listener.cardsRemoved(null, Collections.singleton(zoneCards.get(0)));
                }

                zoneCards.get(0).setZone(zone);
                cardZone = topZone;
            }
            else {
                skipCreateCard = true;
            }
        }

        if (top)
            zoneCards.add(0, card);
        else if (indexOf != null)
            zoneCards.add(indexOf, card);
        else
            zoneCards.add(card);

        if (card.getZone() != null) {
            throw new UnsupportedOperationException(GameUtils.getFullName(card) + " was in " + card.getZone() + " when tried to add to " + zone);
        }

        card.setZone(cardZone);
        card.setZoneOwner(zoneOwner);

        _tableChangedSinceStatsSent = true;

        if (!skipCreateCard && (!skipListenerUpdateOnAdd || !isSkipListenerUpdateAllowed())) {
            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardCreated(card, this, false);
        }

        // If the card is now "in play", "inserted", "stacked", or "out of play" then turn on its "while active in play", "while stacked" or "while out of play" modifiers.
        if (zone.isInPlay() || card.isInserted() || card.getZone() == Zone.STACKED || card.getZone() == Zone.OUT_OF_PLAY) {
            startAffecting(_game, card);
        }
    }

    /**
     * Replaces a character on table with another character, placing the replaced character in Lost Pile.
     * This is used for character persona replacement.
     * @param oldCard the old card
     * @param newCard the new card
     * @return cards to place in Lost Pile
     */
    public Collection<PhysicalCard> replaceCharacterOnTable(PhysicalCard oldCard, PhysicalCard newCard) {
        List<PhysicalCard> cardsToPlaceInLostPile = new ArrayList<PhysicalCard>();
        int prevCardId = oldCard.getCardId();
        String newOwner = newCard.getOwner();
        boolean isConversion = !oldCard.getOwner().equals(newOwner);

        // Remove new card from zone
        removeCardFromZone(newCard);

        // Put the card were the old card was
        copyCardStats(oldCard, newCard);
        if (isConversion) {
            newCard.atLocation(_game.getModifiersQuerying().getLocationThatCardIsAt(this, oldCard));
            newCard.setZone(Zone.AT_LOCATION);
            newCard.setZoneOwner(newOwner);
        }
        else {
            newCard.setZoneOwner(oldCard.getZoneOwner());
            newCard.atLocation(oldCard.getAtLocation());
            newCard.attachTo(oldCard.getAttachedTo(), oldCard.isPilotOf(), oldCard.isPassengerOf(), oldCard.isInCargoHoldAsVehicle(), oldCard.isInCargoHoldAsStarfighterOrTIE(), oldCard.isInCargoHoldAsCapitalStarship());
        }

        // If character only deploys as undercover spy, set it as undercover
        if (newCard.getBlueprint().isOnlyDeploysAsUndercoverSpy(_game, newCard)) {
            newCard.setZone(Zone.AT_LOCATION);
            newCard.setZoneOwner(_game.getOpponent(newOwner));
            newCard.setUndercover(true);
        }

        // Remove old card from zone
        cardsToPlaceInLostPile.add(oldCard);
        removeCardFromZone(oldCard);
        addCardToTopOfZone(oldCard, Zone.VOID, oldCard.getOwner());
        assignNewCardId(oldCard);

        // Give new card the card ID of the replaced card
        assignCardId(newCard, prevCardId);

        // Add new card to zone
        getZoneCards(newCard.getZoneOwner(), newCard.getZone()).add(newCard);

        List<PhysicalCard> attachedCardList = getAttachedCards(oldCard, true);
        List<PhysicalCard> stackedCardList = getStackedCards(oldCard);

        for (GameStateListener listener : getAllGameStateListeners())
            listener.cardReplaced(newCard, this);

        // Transfer attached cards to the new card (if allowed)
        for (PhysicalCard attachedCard : attachedCardList) {
            if (attachedCard.getBlueprint().getValidTransferDuringCharacterReplacementTargetFilter(_game, attachedCard).accepts(_game, newCard)) {
                //Only change owners if the persona replacement is a conversion
                if (isConversion) {
                    attachedCard.setOwner(newOwner);
                }
                moveCardToAttached(attachedCard, newCard);
            }
            else {
                cardsToPlaceInLostPile.add(attachedCard);
                removeCardFromZone(attachedCard);
                addCardToTopOfZone(attachedCard, Zone.VOID, attachedCard.getOwner());
            }
        }

        // Transfer stacked cards to the new card
        for (PhysicalCard stackedCard : stackedCardList) {
            stackedCard.setOwner(newOwner);
            stackedCard.stackOn(newCard, stackedCard.isStackedAsInactive(), stackedCard.isStackedAsViaJediTest5());
        }

        startAffecting(_game, newCard);
        for (PhysicalCard attachedCard : getAttachedCards(newCard, true)) {
            reapplyAffectingForCard(_game, attachedCard);
        }

        return cardsToPlaceInLostPile;
    }

    /**
     * Crosses over a character on table.
     * @param character the character
     * @return cards to place in Lost Pile
     */
    public Collection<PhysicalCard> crossOverCharacterOnTable(PhysicalCard character) {
        List<PhysicalCard> cardsToPlaceInLostPile = new ArrayList<PhysicalCard>();

        String oldOwner = character.getOwner();
        String newOwner = getOpponent(oldOwner);
        character.setOwner(newOwner);
        character.setZoneOwner(newOwner);
        character.setCrossedOver(true);
        moveCardToLocation(character, _game.getModifiersQuerying().getLocationThatCardIsAt(this, character));

        List<PhysicalCard> attachedCardList = getAttachedCards(character, true);
        List<PhysicalCard> stackedCardList = getStackedCards(character);

        // Change owner of attached cards (if allowed)
        for (PhysicalCard attachedCard : attachedCardList) {
            if (attachedCard.getBlueprint().getValidTargetFilterToRemainAttachedToAfterCrossingOver(_game, attachedCard).accepts(_game, character)) {
                attachedCard.setOwner(newOwner);
            }
            else {
                cardsToPlaceInLostPile.add(attachedCard);
                removeCardFromZone(attachedCard);
                addCardToTopOfZone(attachedCard, Zone.VOID, attachedCard.getOwner());
            }
        }

        // Change owner of stacked cards
        for (PhysicalCard stackedCard : stackedCardList) {
            stackedCard.setOwner(newOwner);
            stackedCard.setZoneOwner(newOwner);
        }

        // Change owner and move jedi tests
        Collection<PhysicalCard> jediTestsToMove = Filters.filterAllOnTable(_game, Filters.and(Filters.jediTestTargetingApprentice(Filters.sameCardId(character)), Filters.zoneOfPlayer(Zone.SIDE_OF_TABLE, oldOwner)));
        for (PhysicalCard jediTestToMove : jediTestsToMove) {
            jediTestToMove.setOwner(newOwner);
            relocateCardToSideOfTable(jediTestToMove, newOwner);
        }

        reapplyAffectingForCard(_game, character);
        for (PhysicalCard attachedCard : getAttachedCards(character, true)) {
            reapplyAffectingForCard(_game, attachedCard);
        }
        for (PhysicalCard jediTestToMove : jediTestsToMove) {
            reapplyAffectingForCard(_game, jediTestToMove);
        }

        return cardsToPlaceInLostPile;
    }

    // Call listeners so the card is now shown in the battle group
    public void addCardToBattleGroup(PhysicalCard card) {
        if (isDuringBattle()) {
            for (GameStateListener listener : getAllGameStateListeners()) {
                listener.addToBattle(card, this);
            }
        }
    }

    // Call listeners so the card is now shown not in the battle group
    public void removeCardFromBattleGroup(PhysicalCard card) {
        if (isDuringBattle()) {
            for (GameStateListener listener : getAllGameStateListeners()) {
                listener.removeFromBattle(card, this);
            }
        }
    }

    public void assignNewCardId(PhysicalCard card) {
        int newCardId = nextCardId();
        card.setCardId(newCardId);
        _allCards.put(newCardId, card);
        assignAdditionalCardIds(card, Collections.<Integer>emptyList());
    }

    public void assignCardId(PhysicalCard card, int newCardId) {
        card.setCardId(newCardId);
        _allCards.put(newCardId, card);
        assignAdditionalCardIds(card, Collections.<Integer>emptyList());
    }

    public void assignAdditionalCardIds(PhysicalCard card, List<Integer> additionalCardIds) {
        card.setAdditionalCardIds(additionalCardIds);
        for (Integer additionalCardId : additionalCardIds) {
            _allCards.put(additionalCardId, card);
        }
    }

    /**
     * This method will iterate through each of the cards that are "in play" (regardless of card state) right now.
     * Optionally 'insert' cards and/or converted locations can be included in the search.
     * This is primarily used to look for cards on the table.
     *
     * @param physicalCardVisitor the card visitor
     * @param includeInactiveStackedCards true if 'inactive' stacked cards are included, otherwise false
     * @param includeInsertCards true if 'insert' cards are included, otherwise false
     * @param includeConvertedLocations true if converted locations are included, otherwise false
     * @return true if visitor did not need to visit all cards, otherwise false
     */
    public boolean iterateAllCardsOnTable(PhysicalCardVisitor physicalCardVisitor, boolean includeInactiveStackedCards, boolean includeInsertCards, boolean includeConvertedLocations) {
        for (PhysicalCard physicalCard : _inPlay) {
            if (includeConvertedLocations || physicalCard.getZone() != Zone.CONVERTED_LOCATIONS) {
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
            }
        }

        // Include "stacked" cards that are inactive state
        if (includeInactiveStackedCards) {
            for (List<PhysicalCard> stacked : _stacked.values()) {
                for (PhysicalCard physicalCard : stacked) {
                    if (physicalCard.isStackedAsInactive()) {
                        if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                            return true;
                    }
                }
            }
        }

        // Include "insert" cards in Reserve Decks
        if (includeInsertCards) {
            for (List<PhysicalCard> reserveDeck : _reserveDecks.values()) {
                for (PhysicalCard physicalCard : reserveDeck) {
                    if (physicalCard.isInserted())
                        if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                            return true;
                }
            }
        }

        return false;
    }

    /**
     * This method will iterate through each of the locations that are "on table" right now. Either top locations or
     * converted locations can be included in the search.
     * Note: Cards in the "in play" zone that have not completed deployment are not considered "in play".
     * This is primarily used to look for locations on the table.
     *
     * @param physicalCardVisitor the card visitor
     * @param convertedLocations true if converted locations are searched, false if top locations are searched
     * @return true if visitor did not need to visit all cards, otherwise false
     */
    public boolean iterateLocationsOnTable(PhysicalCardVisitor physicalCardVisitor, boolean convertedLocations) {
        for (PhysicalCard physicalCard : _inPlay) {
            if ((!convertedLocations && physicalCard.getZone() == Zone.LOCATIONS)
                        || (convertedLocations && physicalCard.getZone() == Zone.CONVERTED_LOCATIONS)) {
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
            }
        }

        return false;
    }

    /**
     * This method will iterate through each of the cards that are considered "active" right now.
     * This is primarily used to look if particular cards can be spotted on the table.
     *
     * @param physicalCardVisitor the card visitor
     * @param modifiersQuerying the modifiers querying
     * @param source the card performing the search, or null if the game itself is performing the search
     * @param spotOverrides the spot overrides to determine which "inactive" cards can also be seen
     * @param targetFiltersMap map of targeting reason to filter
     * @return true if visitor did not need to visit all cards, otherwise false
     */
    public boolean iterateActiveCards(PhysicalCardVisitor physicalCardVisitor, ModifiersQuerying modifiersQuerying, PhysicalCard source,
                                      Map<InactiveReason, Boolean> spotOverrides, Map<TargetingReason, Filterable> targetFiltersMap) {
        boolean includeExcludedFromBattle = false;
        boolean includeUndercover = false;
        boolean includeCaptives = false;
        boolean includeMissing = false;
        boolean includeConcealed = false;
        boolean includeSuspended = false;

        // If any spotOverrides were supplied, then apply them
        if (spotOverrides != null) {
            if (spotOverrides.get(InactiveReason.EXCLUDED_FROM_BATTLE) != null) {
                includeExcludedFromBattle = spotOverrides.get(InactiveReason.EXCLUDED_FROM_BATTLE);
            }
            if (spotOverrides.get(InactiveReason.UNDERCOVER) != null) {
                includeUndercover = spotOverrides.get(InactiveReason.UNDERCOVER);
            }
            if (spotOverrides.get(InactiveReason.CAPTIVE) != null) {
                includeCaptives = spotOverrides.get(InactiveReason.CAPTIVE);
            }
            if (spotOverrides.get(InactiveReason.MISSING) != null) {
                includeMissing = spotOverrides.get(InactiveReason.MISSING);
            }
            if (spotOverrides.get(InactiveReason.CONCEALED) != null) {
                includeConcealed = spotOverrides.get(InactiveReason.CONCEALED);
            }
            if (spotOverrides.get(InactiveReason.SUSPENDED) != null) {
                includeSuspended = spotOverrides.get(InactiveReason.SUSPENDED);
            }
        }

        for (PhysicalCard physicalCard : _inPlay) {

            // Special rule:
            // For 'undercover' if source card of an Interrupt or if targeting reason is "to be dueled, hit, or lost (include 'choked');
            // if during a battle (by checking "isBattleStarted", which checks that initial battle participants have been added),
            // then 'undercover' cards cannot be spotted, otherwise they can be spotted
            boolean includeUndercoverForThisCard = includeUndercover;
            if ((source != null && source.getBlueprint().getCardCategory() == CardCategory.INTERRUPT)
                    || (targetFiltersMap != null && targetFiltersMap.get(TargetingReason.TO_BE_DUELED) != null && Filters.and(targetFiltersMap.get(TargetingReason.TO_BE_DUELED)).accepts(this, modifiersQuerying, physicalCard))
                    || (targetFiltersMap != null && targetFiltersMap.get(TargetingReason.TO_BE_HIT) != null && Filters.and(targetFiltersMap.get(TargetingReason.TO_BE_HIT)).accepts(this, modifiersQuerying, physicalCard))
                    || (targetFiltersMap != null && targetFiltersMap.get(TargetingReason.TO_BE_CHOKED) != null && Filters.and(targetFiltersMap.get(TargetingReason.TO_BE_CHOKED)).accepts(this, modifiersQuerying, physicalCard))
                    || (targetFiltersMap != null && targetFiltersMap.get(TargetingReason.TO_BE_LOST) != null && Filters.and(targetFiltersMap.get(TargetingReason.TO_BE_LOST)).accepts(this, modifiersQuerying, physicalCard))) {
                includeUndercoverForThisCard = !_game.getGameState().isBattleStarted();
            }

            // Special rule:
            // For 'undercover' if source card is owner's Effect, weapon, or device and targeting reason includes TO_BE_DEPLOYED_ON,
            // then 'undercover' cards can be spotted.
            if (source != null
                    && targetFiltersMap != null && targetFiltersMap.get(TargetingReason.TO_BE_DEPLOYED_ON) != null && Filters.and(targetFiltersMap.get(TargetingReason.TO_BE_DEPLOYED_ON)).accepts(this, modifiersQuerying, physicalCard)
                    && Filters.and(Filters.owner(physicalCard.getOwner()), Filters.or(Filters.Effect_of_any_Kind, Filters.weapon, Filters.device)).accepts(this, modifiersQuerying, source)) {
                includeUndercoverForThisCard = true;
            }

            // Special rule:
            // If the character device or character weapon is being targeted "to be stolen", then include weapons for stealing
            boolean includeWeaponsForStealingForThisCard = false;
            if (physicalCard.getBlueprint().getCardSubtype() == CardSubtype.CHARACTER
                    && targetFiltersMap != null && (targetFiltersMap.get(TargetingReason.TO_BE_STOLEN) != null && Filters.and(targetFiltersMap.get(TargetingReason.TO_BE_STOLEN)).accepts(this, modifiersQuerying, physicalCard))) {
                includeWeaponsForStealingForThisCard = true;
            }

            // Check if the card can be spotted as "active" and include it if it can be.
            if (isCardInPlayActive(physicalCard, includeExcludedFromBattle, includeUndercoverForThisCard, includeCaptives, includeConcealed, includeWeaponsForStealingForThisCard, includeMissing, false, includeSuspended))
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
        }

        return false;
    }

    /**
     * This method will iterate through each of the stacked on other cards right now.
     * This is primarily used to look for stacked cards.
     *
     * @param physicalCardVisitor the card visitor
     * @return true if visitor did not need to visit all cards, otherwise false
     */
    public boolean iterateStackedCards(PhysicalCardVisitor physicalCardVisitor) {
        for (List<PhysicalCard> stacked : _stacked.values()) {
            for (PhysicalCard physicalCard : stacked) {
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
            }
        }

        return false;
    }

    /**
     * This method will iterate through each of the 'insert' cards right now.
     * This is primarily used to look for 'insert' cards.
     *
     * @param physicalCardVisitor the card visitor
     * @return true if visitor did not need to visit all cards, otherwise false
     */
    public boolean iterateInsertCards(PhysicalCardVisitor physicalCardVisitor) {
        // Include "insert" cards in Reserve decks and Force piles
        for (List<PhysicalCard> reserveDeck : _reserveDecks.values()) {
            for (PhysicalCard physicalCard : reserveDeck) {
                if (physicalCard.isInserted())
                    if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                        return true;
            }
        }

        return false;
    }

    /**
     * This method will iterate through each of the cards that may contain an action that is required to be performed right now.
     * This includes cards in the following states:
     * 1) Any "active" cards in play.
     * 2) Any Undercover spies in play.
     * 3) Any "insert" cards inserted in Reserve Decks.
     *
     * @param physicalCardVisitor the card visitor
     * @return true if visitor did not need to visit all cards, otherwise false
     */
    public boolean iterateCardsWithRequiredActions(PhysicalCardVisitor physicalCardVisitor) {
        for (PhysicalCard physicalCard : _inPlay) {
            if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                return true;
        }


        //need to visit stacked cards for Communing
        for (PhysicalCard physicalCard : _stacked.get(_darkSidePlayer)) {
            if (physicalCard.getZone() == Zone.STACKED
                    && physicalCard.getBlueprint().getCardCategory() != CardCategory.INTERRUPT
                    && physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION
                    && physicalCard.getBlueprint().getCardCategory() != CardCategory.EPIC_EVENT) {
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
            }
        }

        for (PhysicalCard physicalCard : _stacked.get(_lightSidePlayer)) {
            if (physicalCard.getZone() == Zone.STACKED
                    && physicalCard.getBlueprint().getCardCategory() != CardCategory.INTERRUPT
                    && physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION
                    && physicalCard.getBlueprint().getCardCategory() != CardCategory.EPIC_EVENT) {
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
            }
        }

        // Include "insert" cards on top of reserve decks
        for (PhysicalCard topOfReserveDeck : getTopCardsOfReserveDecks()) {
            if (topOfReserveDeck.isInserted())
                if (physicalCardVisitor.visitPhysicalCard(topOfReserveDeck))
                    return true;
        }

        return false;
    }

    /**
     * This method will iterate through each of the 'outside of deck' cards that may contain an action that is required
     * to be performed right now.
     *
     * @param physicalCardVisitor the card visitor
     * @return true if visitor did not need to visit all cards, otherwise false
     */
    public boolean iterateOutsideOfDeckCardsWithRequiredActions(PhysicalCardVisitor physicalCardVisitor) {
        for (List<PhysicalCard> outsideOfDeckCards : _outsideOfDecks.values()) {
            for (PhysicalCard physicalCard : outsideOfDeckCards) {
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
            }
        }

        return false;
    }

    /**
     * This method will iterate through each of the cards that may contain a card pile action that the player may optionally perform right now.
     * This includes cards in the following states:
     * 1) The top card of player's Reserve Deck (for the purpose of activating a Force by clicking on that card,
     *    or for downloading a battleground if the rule is being used).
     * 2) The top card of player's Force Pile (for the purpose of drawing a card by clicking on that card).
     *
     * @param physicalCardVisitor the card visitor
     * @param playerId the player
     * @return true if visitor did not need to visit all cards, otherwise false
     */
    public boolean iterateCardsWithCardPileActions(PhysicalCardVisitor physicalCardVisitor, String playerId) {
        if (getCurrentPlayerId().equals(playerId)) {
            PhysicalCard topOfReserveDeck = getTopOfReserveDeck(playerId);
            if (topOfReserveDeck != null) {

                // During the player's activate phase, the player may click on the top card of Reserve Deck to activate a Force.
                if (getCurrentPhase() == Phase.ACTIVATE) {
                    if (physicalCardVisitor.visitPhysicalCard(topOfReserveDeck))
                        return true;
                }

                // During the player's deploy phase, the player may click on the top card of Reserve Deck to download a
                // battleground in games where the rule is in use.
                if (_game.getFormat().hasDownloadBattlegroundRule()) {
                    if (getCurrentPhase() == Phase.DEPLOY) {
                        if (physicalCardVisitor.visitPhysicalCard(topOfReserveDeck))
                            return true;
                    }
                }
            }
            PhysicalCard topOfForcePile = getTopOfForcePile(playerId);
            if (topOfForcePile != null) {

                // During the player's draw phase, the player may click on the top card of Force Pile to draw a card.
                if (getCurrentPhase() == Phase.DRAW) {
                    if (physicalCardVisitor.visitPhysicalCard(topOfForcePile))
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * This method will iterate through each of the cards that may contain an action that the player may optionally perform right now.
     * This includes cards in the following states:
     * 1) Any "active" cards in play either owned by player or locations.
     * 2) Any Undercover spies in play owned by player.
     * 3) Any cards in the player's hand. (For "optionalActions" but not for "optionalTriggers")
     *
     * @param physicalCardVisitor the card visitor
     * @param playerId the player
     * @param includeInHand true if cards in hand are included, otherwise false
     * @return true if visitor did not need to visit all cards, otherwise false
     */
    public boolean iterateCardsWithOptionalActions(PhysicalCardVisitor physicalCardVisitor, String playerId, boolean includeInHand) {
        // The player may use the "active" card in play if the player owns the card
        // or if the card is a location.
        for (PhysicalCard physicalCard : _inPlay) {
            if ((physicalCard.getOwner().equals(playerId) || physicalCard.getZone() == Zone.LOCATIONS)
                    && physicalCard.getZone().isInPlay())
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
        }
        if (includeInHand) {
            // The player may use any cards in hand.
            for (PhysicalCard physicalCard : _hands.get(playerId)) {
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
            }
/*
            // Visit stacked cards to check for any actions, either whileStacked actions or any stacked cards that may deploy "as if from hand"
            for (PhysicalCard physicalCard : _stacked.get(playerId)) {
                if (physicalCard.getZone() == Zone.STACKED) {
                    if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                        return true;
                }
            }
*/
        }
        // Visit stacked cards to check for any actions, either whileStacked actions or any stacked cards that may deploy "as if from hand"
        for (PhysicalCard physicalCard : getAllStackedCards()) {
            if (physicalCard.getZone() == Zone.STACKED
                    && physicalCard.getOwner().equals(playerId)
                    && physicalCard.getBlueprint().getCardCategory() != CardCategory.INTERRUPT
                    && physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION
                    && physicalCard.getBlueprint().getCardCategory() != CardCategory.EPIC_EVENT) {
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
            }
        }

        return false;
    }

    /**
     * This method will iterate through each of the cards that may contain an action that the player may optionally perform right now.
     * This includes cards in the following states:
     * 1) Any "active" cards or Undercover spies in play that the player does not own and are not locations.
     *
     * @param physicalCardVisitor the card visitor
     * @param playerId the player
     * @return true if visitor did not need to visit all cards, otherwise false
     */
    public boolean iterateOpponentsCardsWithOptionalActions(PhysicalCardVisitor physicalCardVisitor, String playerId) {
        // The player may use the "active" card in play if the player does not own the card and
        // the card is not a location.
        for (PhysicalCard physicalCard : _inPlay) {
            if (!physicalCard.getOwner().equals(playerId) && physicalCard.getBlueprint().getCardCategory() != CardCategory.LOCATION
                    && physicalCard.getZone().isInPlay())
                if (physicalCardVisitor.visitPhysicalCard(physicalCard))
                    return true;
        }
        return false;
    }

    public PhysicalCard findCardById(Integer cardId) {
        if (cardId == null) {
            return null;
        }
        return _allCards.get(cardId);
    }

    public PhysicalCard findCardByPermanentId(Integer permanentCardId) {
        if (permanentCardId == null) {
            return null;
        }
        return _allCardsByPermanentCardId.get(permanentCardId);
    }


    public List<PhysicalCard> getCardPile(String playerId, Zone zone) {
        return getCardPile(playerId, zone, true);
    }

    public List<PhysicalCard> getCardPile(String playerId, Zone zone, boolean skipInsertedCards) {
        if (zone == Zone.RESERVE_DECK)
            return getReserveDeck(playerId, skipInsertedCards);
        if (zone == Zone.FORCE_PILE)
            return getForcePile(playerId);
        if (zone == Zone.USED_PILE)
            return getUsedPile(playerId);
        if (zone == Zone.LOST_PILE)
            return getLostPile(playerId);
        if (zone == Zone.UNRESOLVED_DESTINY_DRAW)
            return getUnresolvedDestinyDraw(playerId);
        if (zone == Zone.OUTSIDE_OF_DECK)
            return getOutsideOfDeck(playerId);
        return null;
    }

    public List<PhysicalCard> getHand(String playerId) {
        return Collections.unmodifiableList(_hands.get(playerId));
    }

    public List<PhysicalCard> getSabaccHand(String playerId) {
        return Collections.unmodifiableList(_sabaccHands.get(playerId));
    }

    public List<PhysicalCard> getVoid(String playerId) {
        return Collections.unmodifiableList(_voids.get(playerId));
    }

    public List<PhysicalCard> getUnresolvedDestinyDraw(String playerId) {
        return Collections.unmodifiableList(_unresolvedDestinyDraws.get(playerId));
    }

    public List<PhysicalCard> getAllStackedCards() {
        List<PhysicalCard> stackedCards = new LinkedList<PhysicalCard>();
        stackedCards.addAll(_stacked.get(_darkSidePlayer));
        stackedCards.addAll(_stacked.get(_lightSidePlayer));
        return Collections.unmodifiableList(stackedCards);
    }

    public List<PhysicalCard> getOutOfPlayPile(String playerId) {
        return Collections.unmodifiableList(_outOfPlayPiles.get(playerId));
    }

    public List<PhysicalCard> getAllOutOfPlayCards() {
        List<PhysicalCard> outOfPlayCards = new LinkedList<PhysicalCard>();
        outOfPlayCards.addAll(_outOfPlayPiles.get(_darkSidePlayer));
        outOfPlayCards.addAll(_outOfPlayPiles.get(_lightSidePlayer));
        outOfPlayCards.addAll(_game.getModifiersQuerying().getCardsConsideredOutOfPlay(this));
        return Collections.unmodifiableList(outOfPlayCards);
    }

    public List<PhysicalCard> getOutsideOfDeck(String playerId) {
        return Collections.unmodifiableList(_outsideOfDecks.get(playerId));
    }

    public List<PhysicalCard> getSideOfTableFaceDown(String playerId) {
        return Collections.unmodifiableList(_sideOfTableNotInPlay.get(playerId));
    }

    public List<PhysicalCard> getReserveDeck(String playerId) {
        return getReserveDeck(playerId, true);
    }

    public List<PhysicalCard> getReserveDeck(String playerId, boolean skipInsertedCards) {
        if (!skipInsertedCards)
            return Collections.unmodifiableList(_reserveDecks.get(playerId));

        List<PhysicalCard> cards = new LinkedList<PhysicalCard>();
        for (PhysicalCard card : _reserveDecks.get(playerId))
            if (!card.isInserted())
                cards.add(card);

        return Collections.unmodifiableList(cards);
    }

    public List<PhysicalCard> getForcePile(String playerId) {
        return Collections.unmodifiableList(_forcePiles.get(playerId));
    }

    public List<PhysicalCard> getUsedPile(String playerId) {
        return Collections.unmodifiableList(_usedPiles.get(playerId));
    }

    public List<PhysicalCard> getLostPile(String playerId) {
        return Collections.unmodifiableList(_lostPiles.get(playerId));
    }

    // Get sizes of card piles
    public int getCardPileSize(String playerId, Zone zone) {
        if (zone == Zone.RESERVE_DECK)
            return getReserveDeckSize(playerId);
        if (zone == Zone.FORCE_PILE)
            return getForcePile(playerId).size();
        if (zone == Zone.USED_PILE)
            return getUsedPile(playerId).size();
        if (zone == Zone.LOST_PILE)
            return getLostPile(playerId).size();

        return 0;
    }

    public int getReserveDeckSize(String playerId) {
        return getReserveDeckSize(playerId, true);
    }

    private int getReserveDeckSize(String playerId, boolean skipInsertedCards) {
        List<PhysicalCard> reserveDeck = _reserveDecks.get(playerId);
        int retVal = reserveDeck.size();
        if (skipInsertedCards)
            for (PhysicalCard card : reserveDeck)
                if (card.isInserted())
                    retVal--;

        return retVal;
    }

    public int getForcePileSize(String playerId) {
        return getCardPileSize(playerId, Zone.FORCE_PILE);
    }

    public PhysicalCard getBottomOfCardPile(String playerId, Zone zone) {
        List<? extends PhysicalCard> cards = getCardPile(playerId, zone);
        if (cards==null || cards.isEmpty())
            return null;
        return cards.get(cards.size() - 1);
    }

    public PhysicalCard getTopOfCardPile(String playerId, Zone zone) {
        List<? extends PhysicalCard> cards = getCardPile(playerId, zone);
        if (cards==null || cards.isEmpty())
            return null;
        return cards.get(0);
    }

    public PhysicalCard getTopOfReserveDeck(String playerId) {
        if (_reserveDecks.get(playerId).isEmpty())
            return null;
        return _reserveDecks.get(playerId).get(0);
    }

    public PhysicalCard getTopOfForcePile(String playerId) {
        if (_forcePiles.get(playerId).isEmpty())
            return null;
        return _forcePiles.get(playerId).get(0);
    }

    public PhysicalCard getTopOfUsedPile(String playerId) {
        if (_usedPiles.get(playerId).isEmpty())
            return null;
        return _usedPiles.get(playerId).get(0);
    }

    public PhysicalCard getTopOfLostPile(String playerId) {
        if (_lostPiles.get(playerId).isEmpty())
            return null;
        return _lostPiles.get(playerId).get(0);
    }

    public PhysicalCard getTopOfUnresolvedDestinyDraws(String playerId) {
        if (_unresolvedDestinyDraws.get(playerId).isEmpty())
            return null;
        return _unresolvedDestinyDraws.get(playerId).get(0);
    }

    public List<PhysicalCard> getTopCardsOfReserveDecks() {
        List<PhysicalCard> cards = new LinkedList<PhysicalCard>();
        for (List<PhysicalCard> pile : _reserveDecks.values())
            if (!pile.isEmpty())
                cards.add(pile.get(0));
        return cards;
    }

    public List<PhysicalCard> getTopCardsOfLostPiles() {
        List<PhysicalCard> cards = new LinkedList<PhysicalCard>();
        for (List<PhysicalCard> pile : _lostPiles.values())
            if (!pile.isEmpty())
                cards.add(pile.get(0));
        return cards;
    }

    public List<PhysicalCard> getTopCardsOfPiles() {
        List<PhysicalCard> cards = new LinkedList<PhysicalCard>();
        for (List<PhysicalCard> pile : _reserveDecks.values())
            if (!pile.isEmpty())
                cards.add(pile.get(0));
        for (List<PhysicalCard> pile : _forcePiles.values())
            if (!pile.isEmpty())
                cards.add(pile.get(0));
        for (List<PhysicalCard> pile : _usedPiles.values())
            if (!pile.isEmpty())
                cards.add(pile.get(0));
        for (List<PhysicalCard> pile : _lostPiles.values())
            if (!pile.isEmpty())
                cards.add(pile.get(0));
        for (List<PhysicalCard> pile : _unresolvedDestinyDraws.values())
            if (!pile.isEmpty())
                cards.add(pile.get(0));

        return cards;
    }

    public List<PhysicalCard> getTopCardsOfPiles(String playerId) {
        if (playerId==null)
            return getTopCardsOfPiles();

        List<PhysicalCard> cards = new LinkedList<PhysicalCard>();
        List<PhysicalCard> reserveDeck = _reserveDecks.get(playerId);
        if (!reserveDeck.isEmpty())
            cards.add(reserveDeck.get(0));
        List<PhysicalCard> forcePile = _forcePiles.get(playerId);
        if (!forcePile.isEmpty())
            cards.add(forcePile.get(0));
        List<PhysicalCard> usedPile = _usedPiles.get(playerId);
        if (!usedPile.isEmpty())
            cards.add(usedPile.get(0));
        List<PhysicalCard> lostPile = _lostPiles.get(playerId);
        if (!lostPile.isEmpty())
            cards.add(lostPile.get(0));
        List<PhysicalCard> unresolvedDestinyDraws = _unresolvedDestinyDraws.get(playerId);
        if (!unresolvedDestinyDraws.isEmpty())
            cards.add(unresolvedDestinyDraws.get(0));

        return cards;
    }

    public String getCurrentPlayerId() {
        return _currentPlayerId;
    }

    public int incrementAndGetCurrentTurnNumber() {
        if (_currentPlayerId.equals(_darkSidePlayer))
            return ++_darkSideTurnNumber;
        else
            return ++_lightSideTurnNumber;
    }

    public int getPlayersLatestTurnNumber(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            return _darkSideTurnNumber;
        else
            return _lightSideTurnNumber;
    }

    public void setPlayersTotalForceGeneration(String playerId, float totalForceGeneration) {
        if (playerId.equals(_darkSidePlayer))
            _darkSideTotalForceGeneration = totalForceGeneration;
        else
            _lightSideTotalForceGeneration = totalForceGeneration;
    }

    public float getPlayersTotalForceGeneration(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            return _darkSideTotalForceGeneration;
        else
            return _lightSideTotalForceGeneration;
    }

    public void lifeForceDepleted(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            _darkSideLifeForceDepleted = true;
        else
            _lightSideLifeForceDepleted = true;
    }

    public int getPlayerLifeForce(String playerId) {
        if (_darkSideLifeForceDepleted && playerId.equals(_darkSidePlayer)) {
            return 0;
        }
        if (_lightSideLifeForceDepleted && playerId.equals(_lightSidePlayer)) {
            return 0;
        }
        return getReserveDeckSize(playerId) + _forcePiles.get(playerId).size() + _usedPiles.get(playerId).size()
                + _unresolvedDestinyDraws.get(playerId).size() + _sabaccHands.get(playerId).size();
    }

    public List<PhysicalCard> getCaptivesOfEscort(PhysicalCard card) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        if (card.getBlueprint().getCardCategory() == CardCategory.CHARACTER) {
            for (PhysicalCard physicalCard : getAttachedCards(card, false)) {
                if (physicalCard.isCaptive()) {
                    result.add(physicalCard);
                }
            }
        }
        return result;
    }

    public List<PhysicalCard> getCaptivesInPrison(PhysicalCard prison) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        for (PhysicalCard physicalCard : getAttachedCards(prison, false)) {
            if (physicalCard.isCaptive() && physicalCard.isImprisoned()) {
                result.add(physicalCard);
            }
        }
        return result;
    }

    public List<PhysicalCard> getNonCaptiveCharactersCarried(PhysicalCard card) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        if (card.getBlueprint().getCardCategory() != CardCategory.STARSHIP && card.getBlueprint().getCardCategory() != CardCategory.VEHICLE) {
            for (PhysicalCard physicalCard : getAllAttachedRecursively(card)) {
                if (physicalCard.getBlueprint().getCardCategory() == CardCategory.CHARACTER && !physicalCard.isCaptive()) {
                    result.add(physicalCard);
                }
            }
        }
        return result;
    }

    public List<PhysicalCard> getAttachedCards(PhysicalCard card) {
        return getAttachedCards(card, false);
    }

    public List<PhysicalCard> getAttachedCards(PhysicalCard card, boolean includeAboard) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        for (PhysicalCard attachedCard : card.getCardsAttached()) {
            if (includeAboard || (!attachedCard.isPilotOf() && !attachedCard.isPassengerOf() && !attachedCard.isInCargoHoldAsVehicle() && !attachedCard.isInCargoHoldAsStarfighterOrTIE())) {
                result.add(attachedCard);
            }
        }
        return result;
    }


    public List<PhysicalCard> getAllAttachedRecursively(PhysicalCard card) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        for (PhysicalCard attachedCard : card.getCardsAttached()) {
            result.add(attachedCard);
            result.addAll(getAllAttachedRecursively(attachedCard));
        }
        return result;
    }

    public List<PhysicalCard> getAboardCards(PhysicalCard card, boolean includeAboardCargoOf) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        for (PhysicalCard attachedCard : card.getCardsAttached()) {
            if (attachedCard.isPilotOf() || attachedCard.isPassengerOf() || attachedCard.isInCargoHoldAsVehicle() || attachedCard.isInCargoHoldAsStarfighterOrTIE()) {
                result.add(attachedCard);
                if (attachedCard.isPilotOf() || attachedCard.isPassengerOf()) {
                    result.addAll(getAttachedCards(attachedCard));
                }
                if (includeAboardCargoOf && (attachedCard.isInCargoHoldAsVehicle() || attachedCard.isInCargoHoldAsStarfighterOrTIE())) {
                    result.addAll(getAboardCards(attachedCard, true));
                }
            }
        }
        return result;
    }

    // TODO: Move some of this to modifiersquerying
    public List<PhysicalCard> getPilotCardsAboard(ModifiersQuerying modifiersQuerying, PhysicalCard card, boolean skipPilotsUnableToPilot) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        for (PhysicalCard attachedCard : card.getCardsAttached()) {
            if (attachedCard.isPilotOf()) {
                if (!skipPilotsUnableToPilot || !modifiersQuerying.cannotDriveOrPilot(this, attachedCard)) {
                    result.add(attachedCard);
                }
            }
        }
        return result;
    }

    public List<PhysicalCard> getDriverCardsAboard(ModifiersQuerying modifiersQuerying, PhysicalCard card, boolean skipPilotsUnableToDrive) {
        return getPilotCardsAboard(modifiersQuerying, card, skipPilotsUnableToDrive);
    }

    /**
     * Gets the number of pilot capacity slots available.
     * @param modifiersQuerying the modifiers querying
     * @param card the card with the capacity slots
     * @param cardToCheckFor the card to check capacity slot for, or null
     * @return the number of available slots
     */
    public int getAvailablePilotCapacity(ModifiersQuerying modifiersQuerying, PhysicalCard card, PhysicalCard cardToCheckFor) {
        SwccgCardBlueprint blueprint = card.getBlueprint();
        if (modifiersQuerying.getPilotCapacity(this, card)==Integer.MAX_VALUE || blueprint.getPilotOrPassengerCapacity()==Integer.MAX_VALUE)
            return Integer.MAX_VALUE;

        List<PhysicalCard> pilots = getPilotCardsAboard(modifiersQuerying, card, false);
        List<PhysicalCard> passengers = getPassengerCardsAboard(card);
        int astromechOnly = modifiersQuerying.getAstromechCapacity(this, card);
        int passengerOnly = blueprint.getPassengerCapacity();
        int pilotOrPassSlotsFilledByPass = 0;
        boolean isCardAlreadyAboard = cardToCheckFor != null && (pilots.contains(cardToCheckFor) || passengers.contains(cardToCheckFor));

        for (PhysicalCard pilot : pilots) {
            // Passenger slots are also filled by captives, so count any captives that are escorted by pilots or passengers.
            if (!isCardAlreadyAboard || cardToCheckFor.getCardId() != pilot.getCardId()) {
                int numCaptives = getCaptivesOfEscort(pilot).size();
                if (numCaptives > passengerOnly) {
                    pilotOrPassSlotsFilledByPass += (numCaptives - passengerOnly);
                    passengerOnly = 0;
                }
                else {
                    passengerOnly -= numCaptives;
                }
            }
        }

        for (PhysicalCard passengerCard : passengers) {
            if (!isCardAlreadyAboard || cardToCheckFor.getCardId() != passengerCard.getCardId()) {

                if (passengerCard.getBlueprint().isMovesLikeCharacter())
                    ; // Cards that only "move like characters" do not count toward capacity
                else if (Filters.astromech_droid.accepts(this, modifiersQuerying, passengerCard) && astromechOnly > 0)
                    astromechOnly--;
                else if (passengerOnly > 0)
                    passengerOnly--;
                else
                    pilotOrPassSlotsFilledByPass++;

                // Passenger slots are also filled by captives, so count any captives that are escorted by pilots or passengers.
                int numCaptives = getCaptivesOfEscort(passengerCard).size();
                if (numCaptives > passengerOnly) {
                    pilotOrPassSlotsFilledByPass += (numCaptives - passengerOnly);
                    passengerOnly = 0;
                }
                else {
                    passengerOnly -= numCaptives;
                }
            }
        }

        // Check if not enough room when considering captives take up passenger slots
        if (passengerOnly < 0 || astromechOnly < 0 || pilotOrPassSlotsFilledByPass > blueprint.getPilotOrPassengerCapacity()) {
            return 0;
        }

        int pilotCapacity = modifiersQuerying.getPilotCapacity(this, card) + Math.max(0, blueprint.getPilotOrPassengerCapacity() - pilotOrPassSlotsFilledByPass);

        return Math.max(0, pilotCapacity - pilots.size());
    }

    public List<PhysicalCard> getPassengerCardsAboard(PhysicalCard card) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        for (PhysicalCard attachedCard : card.getCardsAttached()) {
            if (attachedCard.isPassengerOf()) {
                result.add(attachedCard);
            }
        }
        return result;
    }

    /**
     * Gets the number of passenger capacity slots available.
     * @param modifiersQuerying the modifiers querying
     * @param card the card with the capacity slots
     * @param cardToCheckFor the card to check capacity slot for, or null
     * @return the number of available slots
     */
    public int getAvailablePassengerCapacity(ModifiersQuerying modifiersQuerying, PhysicalCard card, PhysicalCard cardToCheckFor) {
        SwccgCardBlueprint blueprint = card.getBlueprint();

        // Check if card can specifically carry a passenger as if a creature vehicle
        if (blueprint.getCardCategory() != CardCategory.VEHICLE
                && blueprint.getCardCategory() != CardCategory.STARSHIP) {
            if (cardToCheckFor != null && cardToCheckFor.getBlueprint().isMovesLikeCharacter()) {
                return 0;
            }
            if (!modifiersQuerying.canCarryPassengerAsIfCreatureVehicle(this, card, cardToCheckFor)) {
                return 0;
            }
            List<PhysicalCard> nonCaptivesCarried = getNonCaptiveCharactersCarried(card);
            if (!nonCaptivesCarried.isEmpty() && (cardToCheckFor == null || !nonCaptivesCarried.contains(cardToCheckFor))) {
                return 0;
            }
            PhysicalCard carriedBy = card;
            while (carriedBy != null && carriedBy.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
                carriedBy = carriedBy.getAttachedTo();
            }
            if (carriedBy != null && carriedBy.getAttachedTo() != null && cardToCheckFor != null
                    && Filters.or(Filters.starship, Filters.vehicle).accepts(this, modifiersQuerying, carriedBy.getAttachedTo())) {
                return (getAvailablePassengerCapacity(modifiersQuerying, carriedBy.getAttachedTo(), cardToCheckFor) >= 1) ? 1 : 0;
            }
            return 1;
        }

        // Check if unlimited capacity
        if (blueprint.getPassengerCapacity()==Integer.MAX_VALUE || blueprint.getPilotOrPassengerCapacity()==Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        // If moves like a character, then card does not require an open passenger slot
        if (cardToCheckFor != null && cardToCheckFor.getBlueprint().isMovesLikeCharacter()) {
            return 1;
        }

        List<PhysicalCard> pilots = getPilotCardsAboard(modifiersQuerying, card, false);
        List<PhysicalCard> passengers = getPassengerCardsAboard(card);
        int pilotOnly = modifiersQuerying.getPilotCapacity(this, card);
        int astromechOnly = modifiersQuerying.getAstromechCapacity(this, card);
        int passengerCapacity = blueprint.getPassengerCapacity();
        boolean isCardAlreadyAboard = cardToCheckFor != null && (pilots.contains(cardToCheckFor) || passengers.contains(cardToCheckFor));

        if (blueprint.getPilotOrPassengerCapacity() > 0) {
            int pilotOrPassSlotsFilledByPilot = pilots.size() - pilotOnly;
            for (PhysicalCard pilot : pilots) {
                if (isCardAlreadyAboard && cardToCheckFor.getCardId() == pilot.getCardId()) {
                    pilotOrPassSlotsFilledByPilot--;
                }
            }
            passengerCapacity += Math.max(0, blueprint.getPilotOrPassengerCapacity() - Math.max(0, pilotOrPassSlotsFilledByPilot));
        }

        // Passenger slots are also filled by captives, so count any captives that are escorted by the card if not already aboard.
        if (!isCardAlreadyAboard && cardToCheckFor != null) {
            passengerCapacity -= getCaptivesOfEscort(cardToCheckFor).size();
            passengerCapacity -= getNonCaptiveCharactersCarried(cardToCheckFor).size();
        }

        for (PhysicalCard pilot : pilots) {
            // Passenger slots are also filled by captives, so count any captives that are escorted by pilots or passengers.
            if (!isCardAlreadyAboard || cardToCheckFor.getCardId() != pilot.getCardId()) {
                passengerCapacity -= getCaptivesOfEscort(pilot).size();
                passengerCapacity -= getNonCaptiveCharactersCarried(pilot).size();
            }
        }

        for (PhysicalCard passengerCard : passengers) {
            // Passenger slots are also filled by captives, so count any captives that are escorted by pilots or passengers.
            if (!isCardAlreadyAboard || cardToCheckFor.getCardId() != passengerCard.getCardId()) {
                passengerCapacity -= getCaptivesOfEscort(passengerCard).size();
                passengerCapacity -= getNonCaptiveCharactersCarried(passengerCard).size();
            }

            if (passengerCard.getBlueprint().isMovesLikeCharacter())
                ; // Cards that only "move like characters" do not count toward capacity
            else if (Filters.astromech_droid.accepts(this, modifiersQuerying, passengerCard) && astromechOnly>0)
                astromechOnly--;
            else
                passengerCapacity--;
        }

        // Check if not enough room when considering captives take up passenger slots
        if (passengerCapacity < 0 || astromechOnly < 0) {
            return 0;
        }

        return Math.max(0, passengerCapacity);
    }

    /**
     * Gets the number of passenger capacity slots for an astromech are available.
     * @param modifiersQuerying the modifiers querying
     * @param card the card with the capacity slots
     * @param cardToCheckFor the card to check capacity slot for, or null
     * @return the number of available slots
     */
    public int getAvailablePassengerCapacityForAstromech(ModifiersQuerying modifiersQuerying, PhysicalCard card, PhysicalCard cardToCheckFor) {
        SwccgCardBlueprint blueprint = card.getBlueprint();

        // Check if card can specifically carry a passenger as if a creature vehicle
        if (blueprint.getCardCategory() != CardCategory.VEHICLE
                && blueprint.getCardCategory() != CardCategory.STARSHIP) {
            if (cardToCheckFor != null && cardToCheckFor.getBlueprint().isMovesLikeCharacter()) {
                return 0;
            }
            if (!modifiersQuerying.canCarryPassengerAsIfCreatureVehicle(this, card, cardToCheckFor)) {
                return 0;
            }
            List<PhysicalCard> nonCaptivesCarried = getNonCaptiveCharactersCarried(card);
            if (!nonCaptivesCarried.isEmpty() && (cardToCheckFor == null || !nonCaptivesCarried.contains(cardToCheckFor))) {
                return 0;
            }
            PhysicalCard carriedBy = card;
            while (carriedBy != null && carriedBy.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
                carriedBy = carriedBy.getAttachedTo();
            }
            if (carriedBy != null && carriedBy.getAttachedTo() != null && cardToCheckFor != null
                    && Filters.or(Filters.starship, Filters.vehicle).accepts(this, modifiersQuerying, carriedBy.getAttachedTo())) {
                return (getAvailablePassengerCapacity(modifiersQuerying, carriedBy.getAttachedTo(), cardToCheckFor) >= 1) ? 1 : 0;
            }
            return 1;
        }

        // Check if unlimited capacity
        if (blueprint.getPassengerCapacity()==Integer.MAX_VALUE || blueprint.getPilotOrPassengerCapacity()==Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        // If moves like a character, then card does not require an open passenger slot
        if (cardToCheckFor != null && cardToCheckFor.getBlueprint().isMovesLikeCharacter()) {
            return 1;
        }

        List<PhysicalCard> pilots = getPilotCardsAboard(modifiersQuerying, card, false);
        List<PhysicalCard> passengers = getPassengerCardsAboard(card);

        int pilotOnly = modifiersQuerying.getPilotCapacity(this, card);
        int astromechOnly = modifiersQuerying.getAstromechCapacity(this, card);
        int anyPassenger = blueprint.getPassengerCapacity();
        boolean isCardAlreadyAboard = cardToCheckFor != null && (pilots.contains(cardToCheckFor) || passengers.contains(cardToCheckFor));

        // Check if card can specifically carry a passenger as if a creature vehicle
        if (anyPassenger == 0) {
            if (modifiersQuerying.canCarryPassengerAsIfCreatureVehicle(this, card, cardToCheckFor)) {
                PhysicalCard carriedBy = card;
                while (carriedBy != null && carriedBy.getBlueprint().getCardCategory() != CardCategory.CHARACTER) {
                    carriedBy = carriedBy.getAttachedTo();
                }
                if (carriedBy != null && carriedBy.getAttachedTo() != null && cardToCheckFor != null
                        && Filters.or(Filters.starship, Filters.vehicle).accepts(this, modifiersQuerying, carriedBy.getAttachedTo())) {
                    anyPassenger = (getAvailablePassengerCapacity(modifiersQuerying, carriedBy.getAttachedTo(), cardToCheckFor) >= 1) ? 1 : 0;
                }
                else {
                    anyPassenger = 1;
                }
            }
            else {
                if (cardToCheckFor != null && cardToCheckFor.getBlueprint().isMovesLikeCharacter()) {
                    return 0;
                }
            }
        }

        // If moves like a character, then card does not require an open passenger slot
        if (cardToCheckFor != null && cardToCheckFor.getBlueprint().isMovesLikeCharacter()) {
            return 1;
        }

        if (blueprint.getPilotOrPassengerCapacity() > 0) {
            int pilotOrPassSlotsFilledByPilot = pilots.size() - pilotOnly;
            for (PhysicalCard pilot : pilots) {
                if (isCardAlreadyAboard && cardToCheckFor.getCardId() == pilot.getCardId()) {
                    pilotOrPassSlotsFilledByPilot--;
                }
            }
            anyPassenger += Math.max(0, blueprint.getPilotOrPassengerCapacity() - Math.max(0, pilotOrPassSlotsFilledByPilot));
        }

        // Passenger slots are also filled by captives, so count any captives that are escorted by the card if not already aboard.
        if (!isCardAlreadyAboard && cardToCheckFor != null) {
            anyPassenger -= getCaptivesOfEscort(cardToCheckFor).size();
            anyPassenger -= getNonCaptiveCharactersCarried(cardToCheckFor).size();
        }

        for (PhysicalCard pilot : pilots) {
            // Passenger slots are also filled by captives, so count any captives that are escorted by pilots or passengers.
            if (!isCardAlreadyAboard || cardToCheckFor.getCardId() != pilot.getCardId()) {
                anyPassenger -= getCaptivesOfEscort(pilot).size();
                anyPassenger -= getNonCaptiveCharactersCarried(pilot).size();
            }
        }

        for (PhysicalCard passengerCard : passengers) {
            // Passenger slots are also filled by captives, so count any captives that are escorted by pilots or passengers.
            if (!isCardAlreadyAboard || cardToCheckFor.getCardId() != passengerCard.getCardId()) {
                anyPassenger -= getCaptivesOfEscort(passengerCard).size();
                anyPassenger -= getNonCaptiveCharactersCarried(passengerCard).size();
            }

            if (passengerCard.getBlueprint().isMovesLikeCharacter())
                ; // Cards that only "move like characters" do not count toward capacity
            else if (Filters.astromech_droid.accepts(this, modifiersQuerying, passengerCard) && astromechOnly>0)
                astromechOnly--;
            else
                anyPassenger--;
        }

        // Check if not enough room when considering captives take up passenger slots
        if (anyPassenger < 0 || astromechOnly < 0) {
            return 0;
        }

        return Math.max(0, anyPassenger + astromechOnly);
    }

    public List<PhysicalCard> getCardsInVehicleCapacitySlots(PhysicalCard card) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        for (PhysicalCard attachedCard : card.getCardsAttached()) {
            if (attachedCard.isInCargoHoldAsVehicle()) {
                result.add(attachedCard);
            }
        }
        return result;
    }

    /**
     * Gets the number of vehicle capacity slots that are available.
     * @param card the card with the capacity slots
     * @return the number of available slots
     */
    public int getAvailableVehicleCapacity(PhysicalCard card) {
        if (card.getBlueprint().getVehicleCapacity() == Integer.MAX_VALUE)
            return Integer.MAX_VALUE;

        return Math.max(0, card.getBlueprint().getVehicleCapacity() - getCardsInVehicleCapacitySlots(card).size());
    }

    public List<PhysicalCard> getCardsInStarfighterOrTIECapacitySlots(PhysicalCard card) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        for (PhysicalCard attachedCard : card.getCardsAttached()) {
            if (attachedCard.isInCargoHoldAsStarfighterOrTIE())
                result.add(attachedCard);
        }
        return result;
    }

    /**
     * Gets the number of starfighter or TIE capacity slots for a specified starfighter or TIE that are available.
     * @param card the card with the capacity slots
     * @param cardToCheckFor the card to check capacity slot for, or null
     * @return the number of available slots
     */
    public int getAvailableStarfighterOrTIECapacity(PhysicalCard card, PhysicalCard cardToCheckFor) {
        if (card.getBlueprint().getStarfighterOrTIECapacity() == Integer.MAX_VALUE)
            return Integer.MAX_VALUE;

        int starfighterOrTIECapacity = card.getBlueprint().getStarfighterOrTIECapacity();
        List<PhysicalCard> cardsInStarfighterOrTIESlots = getCardsInStarfighterOrTIECapacitySlots(card);
        boolean isCardAlreadyAboard = cardToCheckFor != null && cardsInStarfighterOrTIESlots.contains(cardToCheckFor);
        Filter starfighterOrTIECapacityFilter = card.getBlueprint().getStarfighterOrTIECapacityFilter();

        for (PhysicalCard cardInStarfighterOrTIESlot : cardsInStarfighterOrTIESlots) {
            if (!isCardAlreadyAboard || cardToCheckFor.getCardId() != cardInStarfighterOrTIESlot.getCardId()) {
                starfighterOrTIECapacity -= starfighterOrTIECapacityFilter.acceptsCount(this, _game.getModifiersQuerying(), cardInStarfighterOrTIESlot);
            }
        }

        return Math.max(0, starfighterOrTIECapacity);
    }

    public List<PhysicalCard> getCardsInCapitalStarshipCapacitySlots(PhysicalCard card) {
        List<PhysicalCard> result = new LinkedList<PhysicalCard>();
        for (PhysicalCard attachedCard : card.getCardsAttached()) {
            if (attachedCard.isInCargoHoldAsCapitalStarship())
                result.add(attachedCard);
        }
        return result;
    }

    /**
     * Gets the number of capital starship capacity slots for a specified capital starship that are available.
     * @param card the card with the capacity slots
     * @param cardToCheckFor the card to check capacity slot for, or null
     * @return the number of available slots
     */
    public int getAvailableCapitalStarshipCapacity(PhysicalCard card, PhysicalCard cardToCheckFor) {
        if (card.getBlueprint().getCapitalStarshipCapacity() == Integer.MAX_VALUE)
            return Integer.MAX_VALUE;

        int capitalStarshipCapacity = card.getBlueprint().getCapitalStarshipCapacity();
        List<PhysicalCard> cardsInCapitalStarshipSlots = getCardsInCapitalStarshipCapacitySlots(card);
        boolean isCardAlreadyAboard = cardToCheckFor != null && cardsInCapitalStarshipSlots.contains(cardToCheckFor);
        Filter capitalStarshipCapacityFilter = card.getBlueprint().getCapitalStarshipCapacityFilter();

        for (PhysicalCard cardInCapitalStarshipSlot : cardsInCapitalStarshipSlots) {
            if (!isCardAlreadyAboard || cardToCheckFor.getCardId() != cardInCapitalStarshipSlot.getCardId()) {
                capitalStarshipCapacity -= capitalStarshipCapacityFilter.acceptsCount(this, _game.getModifiersQuerying(), cardInCapitalStarshipSlot);
            }
        }

        return Math.max(0, capitalStarshipCapacity);
    }

    // This should only be used to get cards that are physically at the location and not attached to anything
    // else at that location.
    public List<PhysicalCard> getCardsAtLocation(PhysicalCard card) {
        return new ArrayList<PhysicalCard>(card.getCardsAtLocation());
    }

    public List<PhysicalCard> getStackedCards(PhysicalCard card) {
        return new ArrayList<PhysicalCard>(card.getCardsStacked());
    }

    public void startPlayerTurn(String playerId) {
        _currentPlayerId = playerId;
        incrementAndGetCurrentTurnNumber();

        for (GameStateListener listener : getAllGameStateListeners())
            listener.setCurrentPlayerId(_currentPlayerId);
    }

    public boolean isCardInPlayActive(PhysicalCard card) {
        return isCardInPlayActive(card, false, false, false, false, false, false, false, false);
    }

    public boolean isCardInPlayActive(PhysicalCard card, boolean includeExcludedFromBattle, boolean includeUndercover, boolean includeCaptives, boolean includeConcealed, boolean includeWeaponsForStealing, boolean includeMissing, boolean includeBinaryOff, boolean includeSuspended) {
        return getGame().getModifiersQuerying().getCardState(this, card, includeExcludedFromBattle, includeUndercover, includeCaptives, includeConcealed, includeWeaponsForStealing, includeMissing, includeBinaryOff, includeSuspended)==CardState.ACTIVE;
    }

    public void reapplyAffectingForCard(SwccgGame game, PhysicalCard card) {
        card.stopAffectingGame();
        card.startAffectingGame(game);
    }

    private void startAffecting(SwccgGame game, PhysicalCard card) {
        card.startAffectingGame(game);
    }

    private void stopAffecting(PhysicalCard card) {
        card.stopAffectingGame();
    }

    public void setCurrentPhase(Phase phase) {
        _currentPhase = phase;
        for (GameStateListener listener : getAllGameStateListeners())
            listener.setCurrentPhase(getPhaseString());
    }

    public Phase getCurrentPhase() {
        return _currentPhase;
    }


    //
    // Attack state info
    //

    /**
     * Creates the state information for an attack, records the attack participants, and tells the user interface to display
     * the attack information.
     * @param playerId the player initiating attack
     * @param location the attack location
     * @param cardBeingAttacked the card being attacked
     * @param creatureAttacking the creature attacking, otherwise null if non-creature is attacking a creature
     */
    public void beginAttack(String playerId, PhysicalCard location, PhysicalCard cardBeingAttacked, PhysicalCard creatureAttacking) {
        _attackState = new AttackState(_game, playerId, location);

        if (creatureAttacking == null) {
            _attackState.addParticipantsForNonCreatureAttackOnCreature(Filters.filterActive(_game, null,
                    Filters.initiallyParticipatesInAttackOnCreature(playerId, cardBeingAttacked, false)), cardBeingAttacked);
        }
        else if (!Filters.creature.accepts(_game, cardBeingAttacked)) {
            _attackState.addParticipantsForCreatureAttackOnNonCreature(creatureAttacking, cardBeingAttacked);
        }
        else {
            _attackState.addParticipantsForCreaturesAttackingEachOther(creatureAttacking, cardBeingAttacked);
        }

        _attackState.attackStarted();
        
        if (_attackState.isNonCreatureAttackingCreature()) {
            _game.getModifiersQuerying().attackOnCreatureInitiatedAtLocation(location);
            Collection<PhysicalCard> allCardsAttacking = _attackState.getCardsAttacking();
            for (PhysicalCard cardAttacking : allCardsAttacking) {
                _game.getModifiersQuerying().participatedInAttackOnCreature(cardAttacking);
            }
        }
        else if (_attackState.isCreatureAttackingNonCreature()) {
            Collection<PhysicalCard> allCardsAttacking = _attackState.getCardsAttacking();
            for (PhysicalCard cardAttacking : allCardsAttacking) {
                _game.getModifiersQuerying().participatedInAttackOnNonCreature(cardAttacking);
            }
        }

        List<PhysicalCard> attackingCardsToShowInAttack = new ArrayList<>();
        for (PhysicalCard cardAttacking : _attackState.getCardsAttacking()) {
            PhysicalCard cardToShowAttacking = cardAttacking.getCardAttachedToAtLocation();
            if (cardToShowAttacking != null
                    && !attackingCardsToShowInAttack.contains(cardToShowAttacking)) {
                attackingCardsToShowInAttack.add(cardToShowAttacking);
            }
        }

        List<PhysicalCard> defendingCardsToShowInAttack = new ArrayList<PhysicalCard>();
        for (PhysicalCard cardDefending : _attackState.getCardsDefending()) {
            PhysicalCard cardToShowDefending = cardDefending.getCardAttachedToAtLocation();
            if (cardToShowDefending != null
                    && !defendingCardsToShowInAttack.contains(cardToShowDefending)
                    && !attackingCardsToShowInAttack.contains(cardToShowDefending)) {
                defendingCardsToShowInAttack.add(cardToShowDefending);
            }
        }

        for (GameStateListener listener : getAllGameStateListeners())
            listener.startAttack(location, _attackState.getAttackerOwner(), _attackState.getDefenderOwner(), attackingCardsToShowInAttack, defendingCardsToShowInAttack);
    }

    public boolean isParticipatingInAttack(PhysicalCard physicalCard) {
        return (_attackState != null
                && (_attackState.isCardParticipatingInAttack(physicalCard)));
    }

    public AttackState getAttackState() {
        return _attackState;
    }

    public boolean isDuringAttack() {
        return (_attackState != null);
    }

    public boolean isDuringNonCreatureAttackOnCreature() {
        return (_attackState != null && _attackState.isNonCreatureAttackingCreature());
    }

    public PhysicalCard getAttackLocation() {
        return (_attackState != null ? _attackState.getAttackLocation() : null);
    }

    public boolean isAttackStarted() {
        return (_attackState !=null && _attackState.isAttackStarted());
    }

    public void endAttack() {
        if (_attackState != null) {
            _attackState = null;

            _game.getModifiersEnvironment().removeEndOfAttack();

            for (GameStateListener listener : getAllGameStateListeners())
                listener.finishAttack();
        }
    }

    //
    // Battle state info
    //

    /**
     * Creates the state information for a battle, records the battle participants, and tells the user interface to display
     * the battle information.
     * @param playerId the player initiating battle
     * @param location the battle location
     * @param isLocalTrouble true if battle is a Local Trouble battle, otherwise false
     * @param localTroubleParticipants the Local Trouble battle participants, or null if not a Local Trouble battle
     */
    public void beginBattle(String playerId, PhysicalCard location, boolean isLocalTrouble, Collection<PhysicalCard> localTroubleParticipants) {
        _battleState = new BattleState(getGame(), playerId, location, isLocalTrouble);

        if (isLocalTrouble) {
            _battleState.setLocalTroubleParticipants(localTroubleParticipants);
            _battleState.addParticipants(this, localTroubleParticipants);
        }
        else {
            _battleState.addParticipants(this, Filters.filterActive(_game, null, Filters.initiallyParticipatesInBattle(location)));
        }

        Collection<PhysicalCard> allCardsParticipating = _battleState.getAllCardsParticipating();

        _battleState.battleStarted();

        _game.getModifiersQuerying().battleInitiatedAtLocation(playerId, location);

        for (PhysicalCard currentParticipant : allCardsParticipating)
            _game.getModifiersQuerying().participatedInBattle(currentParticipant, location);

        for (GameStateListener listener : getAllGameStateListeners())
            listener.startBattle(location, allCardsParticipating);
    }

    public boolean isParticipatingInBattle(PhysicalCard physicalCard) {
        return (_battleState !=null
                && (_battleState.getDarkCardsParticipating().contains(physicalCard)
                    || _battleState.getLightCardsParticipating().contains(physicalCard)));
    }

    public BattleState getBattleState() {
        return _battleState;
    }

    public boolean isDuringBattle() {
        return (_battleState != null);
    }

    public boolean isDuringDamageSegmentOfBattle() {
        return (_battleState != null && _battleState.isReachedDamageSegment());
    }

    public boolean isDuringBombingRunBattle() {
        return (_battleState != null && _battleState.isBombingRun());
    }

    public boolean isDuringBesiegedBattle() {
        return (_battleState != null && _battleState.isBesieged());
    }

    public boolean isDuringLocalTroubleBattle() {
        return (_battleState != null && _battleState.isLocalTrouble());
    }

    public boolean isDuringBattleInitiatedBy(String playerId) {
        return (_battleState != null && _battleState.getPlayerInitiatedBattle().equals(playerId));
    }

    public PhysicalCard getBattleLocation() {
        return (_battleState != null ? _battleState.getBattleLocation() : null);
    }

    public PhysicalCard getBattleOrForceDrainLocation() {
        return (_battleState != null ? _battleState.getBattleLocation() : (_forceDrainState != null ? _forceDrainState.getLocation() : null));
    }

    public boolean isBattleStarted() {
        return (_battleState !=null && _battleState.isBattleStarted());
    }

    public void endBattle() {
        if (_battleState != null) {
            _battleState = null;

            _game.getModifiersEnvironment().removeEndOfBattle();
            _game.getActionsEnvironment().removeEndOfBattleActionProxies();

            for (GameStateListener listener : getAllGameStateListeners())
                listener.finishBattle();
        }
    }

    //
    // Duel state info
    //

    /**
     * Creates the state information for a duel and tells the user interface to display the duel information.
     * @param playerId the player initiating duel
     * @param cardToInitiateDuel the card that initiated the duel
     * @param location the duel location
     * @param darkSideCharacter the dark side character to duel
     * @param lightSideCharacter the light side character to duel
     * @param duelDirections the duel directions
     */
    public void beginDuel(String playerId, PhysicalCard cardToInitiateDuel, PhysicalCard location, PhysicalCard darkSideCharacter, PhysicalCard lightSideCharacter, DuelDirections duelDirections) {
        _duelState = new DuelState(_game, playerId, cardToInitiateDuel, location, darkSideCharacter, lightSideCharacter, duelDirections);

        List<PhysicalCard> cardsToShowInDuel = new ArrayList<PhysicalCard>();
        PhysicalCard cardToShowInDuel = darkSideCharacter.getCardAttachedToAtLocation();
        if (cardToShowInDuel != null) {
            cardsToShowInDuel.add(cardToShowInDuel);
        }
        cardToShowInDuel = lightSideCharacter.getCardAttachedToAtLocation();
        if (cardToShowInDuel != null) {
            cardsToShowInDuel.add(cardToShowInDuel);
        }

        for (GameStateListener listener : getAllGameStateListeners())
            listener.startDuel(location, cardsToShowInDuel);
    }

    public boolean isParticipatingInDuel(PhysicalCard physicalCard) {
        return (_duelState !=null && _duelState.getDuelParticipants().contains(physicalCard));
    }

    public DuelState getDuelState() {
        return _duelState;
    }

    public boolean isDuringDuel() {
        return (_duelState != null);
    }

    public PhysicalCard getDuelLocation() {
        return (_duelState != null ? _duelState.getLocation() : null);
    }

    public void endDuel() {
        if (_duelState != null) {
            _duelState = null;

            _game.getModifiersEnvironment().removeEndOfDuel();
            _game.getActionsEnvironment().removeEndOfDuelActionProxies();

            for (GameStateListener listener : getAllGameStateListeners()) {
                listener.finishDuel();

                if (isDuringBattle()) {
                    // Send the game stats so the battle stats are shown again
                    listener.sendGameStats(getGame().getGameStats());
                }
            }
        }
    }

    //
    // Lightsaber Combat state info
    //

    /**
     * Creates the state information for lightsaber combat and tells the user interface to display the lightsaber combat information.
     * @param playerId the player initiating lightsaber combat
     * @param location the lightsaber combat location
     * @param darkSideCharacter the dark side character to participate in lightsaber combat
     * @param lightSideCharacter the light side character to participate in lightsaber combat
     * @param lightsaberCombatDirections the lightsaber combat directions
     */
    public void beginLightsaberCombat(String playerId, PhysicalCard location, PhysicalCard darkSideCharacter, PhysicalCard lightSideCharacter, LightsaberCombatDirections lightsaberCombatDirections) {
        _lightsaberCombatState = new LightsaberCombatState(_game, playerId, location, darkSideCharacter, lightSideCharacter, lightsaberCombatDirections);

        List<PhysicalCard> cardsToShowInLightsaberCombat = new ArrayList<PhysicalCard>();
        cardsToShowInLightsaberCombat.add(darkSideCharacter.getCardAttachedToAtLocation());
        cardsToShowInLightsaberCombat.add(lightSideCharacter.getCardAttachedToAtLocation());

        for (GameStateListener listener : getAllGameStateListeners())
            listener.startLightsaberCombat(location, cardsToShowInLightsaberCombat);
    }

    public boolean isParticipatingInLightsaberCombat(PhysicalCard physicalCard) {
        return (_lightsaberCombatState !=null && _lightsaberCombatState.getLightsaberCombatParticipants().contains(physicalCard));
    }

    public LightsaberCombatState getLightsaberCombatState() {
        return _lightsaberCombatState;
    }

    public boolean isDuringLightsaberCombat() {
        return (_lightsaberCombatState != null);
    }

    public PhysicalCard getLightsaberCombatLocation() {
        return (_lightsaberCombatState != null ? _lightsaberCombatState.getLocation() : null);
    }

    public void endLightsaberCombat() {
        if (_lightsaberCombatState != null) {
            _lightsaberCombatState = null;

            _game.getModifiersEnvironment().removeEndOfLightsaberCombat();
            _game.getActionsEnvironment().removeEndOfLightsaberCombatActionProxies();

            for (GameStateListener listener : getAllGameStateListeners()) {
                listener.finishLightsaberCombat();
            }
        }
    }

    //
    // Deploy as react state info
    //
    public void beginDeployAsReact(RespondableDeployAsReactEffect effect) {
        _deployAsReactState = new DeployAsReactState(effect);
    }

    public DeployAsReactState getDeployAsReactState() {
        return _deployAsReactState;
    }

    public boolean isDuringDeployAsReact() {
        return (_deployAsReactState != null);
    }

    public void finishDeployAsReact() {
        if (_deployAsReactState != null) {
            _deployAsReactState = null;
        }
    }

    //
    // Move as react state info
    //
    public void beginMoveAsReact(PhysicalCard cardReacting, ReactActionOption reactActionOption) {
        _moveAsReactState = new MoveAsReactState(cardReacting, reactActionOption);
    }

    public MoveAsReactState getMoveAsReactState() {
        return _moveAsReactState;
    }

    public boolean isDuringMoveAsReact() {
        return (_moveAsReactState != null);
    }

    public void finishMoveAsReact() {
        if (_moveAsReactState != null)
            _moveAsReactState = null;
    }

    //
    // Force drain state info
    //
    public void beginForceDrain(String playerId, PhysicalCard location) {
        _forceDrainState = new ForceDrainState(_game, playerId, location);
    }

    public ForceDrainState getForceDrainState() {
        return _forceDrainState;
    }

    public boolean isDuringForceDrain() {
        return (_forceDrainState != null);
    }

    public boolean isDuringForceDrainInitiatedBy(String playerId) {
        return (_forceDrainState != null && _forceDrainState.getPlayerId().equals(playerId));
    }

    public PhysicalCard getForceDrainLocation() {
        return (_forceDrainState != null ? _forceDrainState.getLocation() : null);
    }

    public void endForceDrain() {
        _game.getModifiersEnvironment().removeEndOfForceDrain();
        _forceDrainState = null;
    }

    //
    // Play card state info
    //
    public void beginPlayCard(PlayCardAction action) {
        int id = _playCardState.size();
        _playCardState.push(new PlayCardState(id, action));
    }

    /**
     * Gets the top play card state, or the 2nd to top play card state if the source card is the top.
     * @param sourceCardToSkip the sourceCard of the top play card state to skip, or null
     * @return the current top play card state, or null
     */
    public PlayCardState getTopPlayCardState(PhysicalCard sourceCardToSkip) {
        if (_playCardState.isEmpty())
            return null;

        PlayCardState topPlayCardState = _playCardState.peek();
        if (sourceCardToSkip != null
                && topPlayCardState != null
                && topPlayCardState.getPlayCardAction().getPlayedCard().getPermanentCardId() == sourceCardToSkip.getPermanentCardId()) {
            int numPlayCardStates = _playCardState.size();
            return (numPlayCardStates > 1 ? _playCardState.subList(numPlayCardStates - 2, numPlayCardStates - 1).get(0) : null);
        }
        return topPlayCardState;
    }

    /**
     * Gets all the play card states.
     * @return the play card states
     */
    public List<PlayCardState> getPlayCardStates() {
        if (_playCardState.isEmpty())
            return Collections.emptyList();

        return _playCardState.subList(0, _playCardState.size());
    }

    public void endPlayCard() {
        PlayCardState state = getTopPlayCardState(null);
        if (state == null) {
            return;
        }
        PlayCardAction action = state.getPlayCardAction();
        _game.getModifiersEnvironment().removeEndOfCardPlayed(action.getPlayedCard());
        if (action.getOtherPlayedCard() != null) {
            _game.getModifiersEnvironment().removeEndOfCardPlayed(action.getOtherPlayedCard());
        }
        _playCardState.pop();
    }

    //
    // Game text action state info
    //
    public void beginGameTextAction(GameTextAction action) {
        int id = _gameTextActionState.size();
        _gameTextActionState.push(new GameTextActionState(id, action));
    }

    /**
     * Gets the top game text action state.
     * @return the current game text action state, or null
     */
    public GameTextActionState getTopGameTextActionState() {
        if (_gameTextActionState.isEmpty())
            return null;

        return _gameTextActionState.peek();
    }

    /**
     * Gets the game text action state for the specific game text action.
     * @return the current game text action state, or null
     */
    public GameTextActionState getGameTextActionState(GameTextAction gameTextAction) {
        for (GameTextActionState state : _gameTextActionState) {
            if (state.getGameTextAction().equals(gameTextAction)) {
                return state;
            }
        }
        return null;
    }

    public void endGameTextAction() {
        _game.getModifiersEnvironment().removeEndOfGameTextAction();
        _gameTextActionState.pop();
    }

    //
    // Force loss state info
    //
    public void beginForceLoss(LoseForceEffect loseForceEffect) {
        int id = _forceLossState.size();
        _forceLossState.push(new ForceLossState(id, loseForceEffect));
    }

    /**
     * Gets the top Force loss state (not including Force loss due to battle damage).
     * @return the current Force loss state, or null
     */
    public ForceLossState getTopForceLossState() {
        if (_forceLossState.isEmpty())
            return null;

        return _forceLossState.peek();
    }

    public void endForceLoss() {
        _game.getModifiersEnvironment().removeEndOfForceLoss();
        _forceLossState.pop();
    }

    //
    // Force retrieval state info
    //
    public void beginForceRetrieval(ForceRetrievalEffect forceRetrievalEffect) {
        int id = _forceRetrievalState.size();
        _forceRetrievalState.push(new ForceRetrievalState(id, forceRetrievalEffect));
    }

    /**
     * Gets the top Force retrieval state.
     * @return the current Force retrieval state, or null
     */
    public ForceRetrievalState getTopForceRetrievalState() {
        if (_forceRetrievalState.isEmpty())
            return null;

        return _forceRetrievalState.peek();
    }

    public void endForceRetrieval() {
        _game.getModifiersEnvironment().removeEndOfForceRetrieval();
        _forceRetrievalState.pop();
    }


    //
    // Draw destiny state info
    //
    public void beginDrawDestiny(DrawDestinyEffect drawDestinyEffect) {
        int id = _drawDestinyState.size();
        _drawDestinyState.push(new DrawDestinyState(id, drawDestinyEffect));
    }

    public DrawDestinyState getTopDrawDestinyState() {
        if (_drawDestinyState.isEmpty())
            return null;

        return _drawDestinyState.peek();
    }

    public void endDrawDestiny() {
        _game.getModifiersEnvironment().removeEndOfDrawDestiny();
        _game.getActionsEnvironment().removeEndOfDrawDestinyActionProxies();
        _drawDestinyState.pop();
    }

    public void beginEachDrawnDestiny(DrawDestinyEffect drawDestinyEffect) {
        int id = _eachDrawnDestinyState.size();
        _eachDrawnDestinyState.push(new EachDrawnDestinyState(id, drawDestinyEffect));
    }

    public EachDrawnDestinyState getTopEachDrawnDestinyState() {
        if (_eachDrawnDestinyState.isEmpty())
            return null;

        return _eachDrawnDestinyState.peek();
    }

    public void endEachDrawnDestiny() {
        _game.getModifiersEnvironment().removeEndOfEachDrawnDestiny();
        _eachDrawnDestinyState.pop();
    }

    //
    // Epic Event state info
    //
    public void beginEpicEvent(EpicEventState epicEventState) {
        _epicEventState = epicEventState;
    }

    public EpicEventState getEpicEventState() {
        return _epicEventState;
    }

    public boolean isDuringEpicEvent() {
        return _epicEventState != null;
    }

    public boolean isDuringAttackRun() {
        return _epicEventState != null && _epicEventState.getEpicEventType() == EpicEventState.Type.ATTACK_RUN;
    }

    public void finishEpicEvent() {
        if (_epicEventState != null) {
            _epicEventState = null;

            _game.getModifiersEnvironment().removeEndOfEpicEvent();
        }
    }

    //
    // Blow away info
    //
    public void beginBlowAway(BlowAwayEffect blowAwayEffect) {
        int id = _blowAwayState.size();
        _blowAwayState.push(new BlowAwayState(id, blowAwayEffect));
    }

    public BlowAwayState getTopBlowAwayState() {
        if (_blowAwayState.isEmpty())
            return null;

        return _blowAwayState.peek();
    }

    public void endBlowAway() {
        _game.getModifiersEnvironment().removeEndOfBlowAway();
        _game.getActionsEnvironment().removeEndOfBlowAwayActionProxies();
        _blowAwayState.pop();
    }

    //
    // Sabacc state info
    //
    public void beginSabacc(PhysicalCard sabaccInterrupt, PhysicalCard sabaccPlayer1, PhysicalCard sabaccPlayer2) {
        _sabaccState = new SabaccState(sabaccInterrupt, sabaccPlayer1, sabaccPlayer2);

        // Tell user interface to show sabacc area
        for (GameStateListener listener : getAllGameStateListeners())
            listener.startSabacc();
    }

    public SabaccState getSabaccState() {
        return _sabaccState;
    }

    public boolean isDuringSabacc() {
        return (_sabaccState !=null);
    }

    public void revealSabaccHands() {
        if (_sabaccState != null) {
            _sabaccState.setHandsRevealed(true);

            List<PhysicalCard> darkSabaccHand = getSabaccHand(_darkSidePlayer);
            List<PhysicalCard> lightSabaccHand = getSabaccHand(_lightSidePlayer);

            // Tell user interface to remove cards from sabacc hands
            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardsRemoved(_darkSidePlayer, darkSabaccHand);

            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardsRemoved(_lightSidePlayer, lightSabaccHand);

            // Tell user interface to hide sabacc hands area and show revealed sabacc hands area
            for (GameStateListener listener : getAllGameStateListeners()) {
                listener.revealSabaccHands();
                // Send the game stats since each players revealed sabacc hands are now visible
                listener.sendGameStats(getGame().getGameStats());
            }

            // Change zone of cards in sabacc hands to revealed sabacc hands
            for (PhysicalCard sabaccCard : darkSabaccHand)
                sabaccCard.setZone(Zone.REVEALED_SABACC_HAND);

            for (PhysicalCard sabaccCard : lightSabaccHand)
                sabaccCard.setZone(Zone.REVEALED_SABACC_HAND);

            // Tell user interface to add cards to revealed sabacc hands
            for (GameStateListener listener : getAllGameStateListeners())
                for (PhysicalCard sabaccCard : darkSabaccHand)
                    listener.cardCreated(sabaccCard, this, false);

            for (GameStateListener listener : getAllGameStateListeners())
                for (PhysicalCard sabaccCard : lightSabaccHand)
                    listener.cardCreated(sabaccCard, this, false);
        }
    }

    public void endSabacc() {
        if (_sabaccState != null) {
            _game.getModifiersEnvironment().removeEndOfSabacc();
            _sabaccState = null;
            // Tell user interface to hide sabacc area
            for (GameStateListener listener : getAllGameStateListeners())
                listener.finishSabacc();
        }
    }

    //
    // Asteroid destiny draw state info
    //
    public void beginAsteroidDestinyDraw(PhysicalCard starship, PhysicalCard location) {
        _asteroidDestinyDrawState = new AsteroidDestinyDrawState(starship, location);
    }

    public PhysicalCard getStarshipDrawingAsteroidDestinyAgainst() {
        return (_asteroidDestinyDrawState != null ? _asteroidDestinyDrawState.getStarship() : null);
    }

    public PhysicalCard getLocationDrawingAsteroidDestinyAt() {
        return (_asteroidDestinyDrawState != null ? _asteroidDestinyDrawState.getLocation() : null);
    }

    public void endAsteroidDestinyDraw() {
        _asteroidDestinyDrawState = null;
    }

    //
    // Movement destiny draw state info
    //
    public void beginMovementDestinyDraw(PhysicalCard starship, PhysicalCard location) {
        _movementDestinyDrawState = new MovementDestinyDrawState(starship, location);
    }

    public PhysicalCard getStarshipDrawingMovementDestinyAgainst() {
        return (_movementDestinyDrawState != null ? _movementDestinyDrawState.getStarship() : null);
    }

    public PhysicalCard getLocationDrawingMovementDestinyAt() {
        return (_movementDestinyDrawState != null ? _movementDestinyDrawState.getLocation() : null);
    }

    public void endMovementDestinyDraw() {
        _movementDestinyDrawState = null;
    }


    //
    // Search party state info
    //
    public void beginSearchParty(Collection<PhysicalCard> searchParty, PhysicalCard location) {
        _searchPartyState = new SearchPartyState(searchParty, location);
    }

    public List<PhysicalCard> getSearchParty() {
        return (_searchPartyState != null ? _searchPartyState.getSearchParty() : Collections.<PhysicalCard>emptyList());
    }

    public PhysicalCard getSearchPartyLocation() {
        return (_searchPartyState != null ? _searchPartyState.getLocation() : null);
    }

    public boolean isDuringSearchParty() {
        return (_searchPartyState !=null);
    }

    public void finishSearchParty() {
        if (_searchPartyState != null)
            _searchPartyState = null;
    }

    //
    // Weapon firing state info
    //
    public void beginWeaponFiring(PhysicalCard weaponFiring, SwccgBuiltInCardBlueprint permanentWeapon) {
        _weaponFiringState = new WeaponFiringState(_game, weaponFiring, permanentWeapon);
    }

    public void beginCombinedWeaponFiring() {
        // _weaponFiringState = new WeaponFiringState(true);
    }

    public WeaponFiringState getWeaponFiringState() {
        return _weaponFiringState;
    }

    public boolean isDuringWeaponFiring() {
        return (_weaponFiringState != null);
    }

    public void finishWeaponFiring() {
        if (_weaponFiringState != null) {
            _game.getModifiersEnvironment().removeEndOfWeaponFiring();
            _game.getActionsEnvironment().removeEndOfWeaponFiringActionProxies();
            _weaponFiringState = null;
        }
    }

    //
    // Tractor beam state info
    //
    public void beginUsingTractorBeam(PhysicalCard tractorBeam) {
        _usingTractorBeamState = new UsingTractorBeamState(_game, tractorBeam);
    }

    public UsingTractorBeamState getUsingTractorBeamState() {
        return _usingTractorBeamState;
    }

    public boolean isDuringUsingTractorBeam() {
        return (_usingTractorBeamState != null);
    }

    public void finishUsingTractorBeam() {
        if (_usingTractorBeamState != null) {
            _game.getModifiersEnvironment().removeEndOfTractorBeam();
            _usingTractorBeamState = null;
        }
    }

    /**
     * Determines if the specified player as deployed any Death Star location or Death Star II location this game.
     * @param playerId the player
     * @return true or false
     */
    public boolean isDeployedDeathStarLocation(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            return _darkSidePlayerDeployedDeathStarLocation;
        else
            return _lightSidePlayerDeployedDeathStarLocation;
    }

    /**
     * Sets that the specified player has deployed a Death Star location or Death Star II location this game.
     * @param playerId the player
     */
    public void setDeployedDeathStarLocation(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            _darkSidePlayerDeployedDeathStarLocation = true;
        else
            _lightSidePlayerDeployedDeathStarLocation = true;
    }

    /**
     * Determines if the specified player as deployed any Ahch-To location or Dagobah location this game.
     * @param playerId the player
     * @return true or false
     */
    public boolean isDeployedAhchToDagobahLocation(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            return _darkSidePlayerDeployedAhchToDagobahLocation;
        else
            return _lightSidePlayerDeployedAhchToDagobahLocation;
    }

    /**
     * Sets that the specified player has deployed an Ahch-To location or Dagobah location this game.
     * @param playerId the player
     */
    public void setDeployedAhchToDagobahLocation(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            _darkSidePlayerDeployedAhchToDagobahLocation = true;
        else
            _lightSidePlayerDeployedAhchToDagobahLocation = true;
    }

    public PhysicalCard getObjectivePlayed(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            return _darkSideObjective;
        else
            return _lightSideObjective;
    }

    public void setObjectivePlayed(String playerId, PhysicalCard objective) {
        if (playerId.equals(_darkSidePlayer))
            _darkSideObjective = objective;
        else
            _lightSideObjective = objective;
    }

    public PhysicalCard getStartingInterruptPlayed(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            return _darkSideStartingInterrupt;
        else
            return _lightSideStartingInterrupt;
    }

    public void setStartingInterruptPlayed(String playerId, PhysicalCard startingInterrupt) {
        if (playerId.equals(_darkSidePlayer))
            _darkSideStartingInterrupt = startingInterrupt;
        else
            _lightSideStartingInterrupt = startingInterrupt;
    }

    public PhysicalCard getRep(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            return _darkSideRep;
        else
            return _lightSideRep;
    }

    public void setRep(String playerId, PhysicalCard rep) {
        if (playerId.equals(_darkSidePlayer))
            _darkSideRep = rep;
        else
            _lightSideRep = rep;
    }

    /**
     * Gets the card's revealed by the specified player after deploying Starting Effect.
     * @param playerId the player
     * @return the revealed cards
     */
    public List<PhysicalCard> getCardsRevealedAfterStartingEffect(String playerId) {
        if (playerId.equals(_darkSidePlayer))
            return _darkSideRevealedAfterStartingEffect;
        else
            return _lightSideRevealedAfterStartingEffect;
    }

    /**
     * Gets the Renegade planet, or null if there is none.
     * @return the Renegade planet, or null
     */
    public String getRenegadePlanet() {
        return _renegadePlanet;
    }

    /**
     * Sets the Renegade planet.
     * @param renegadePlanet the Renegade planet
     */
    public void setRenegadePlanet(String renegadePlanet) {
        _renegadePlanet = renegadePlanet;
    }

    /**
     * Gets the Subjugated planet, or null if there is none.
     * @return the Subjugated planet, or null
     */
    public String getSubjugatedPlanet() {
        return _subjugatedPlanet;
    }

    /**
     * Sets the Subjugated planet.
     * @param subjugatedPlanet the Subjugated planet
     */
    public void setSubjugatedPlanet(String subjugatedPlanet) {
        _subjugatedPlanet = subjugatedPlanet;
    }

    /**
     * Adds a card as being an apprentice.
     */
    public void addApprentice(PhysicalCard card) {
        Set<Persona> personas = card.getBlueprint().getPersonas();
        if (!personas.isEmpty()) {
            _apprenticePersonas.addAll(card.getBlueprint().getPersonas());
        }
        else if (card.getBlueprint().getUniqueness() == Uniqueness.UNIQUE) {
            _apprenticeTitles.addAll(card.getBlueprint().getTitles());
        }
    }

    /**
     * Adds a card as being an apprentice.
     */
    public boolean isApprentice(PhysicalCard card) {
        if (card.getBlueprint().getCardCategory() != CardCategory.CHARACTER)
            return false;

        for (Persona persona : card.getBlueprint().getPersonas()) {
            if (_apprenticePersonas.contains(persona)) {
                return true;
            }
        }
        if (card.getBlueprint().getUniqueness() == Uniqueness.UNIQUE) {
            for (String title : card.getBlueprint().getTitles()) {
                if (_apprenticeTitles.contains(title)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes a card as being an apprentice.
     */
    public void removeApprentice(PhysicalCard card) {
        Set<Persona> personas = card.getBlueprint().getPersonas();
        if (!personas.isEmpty()) {
            _apprenticePersonas.removeAll(card.getBlueprint().getPersonas());
        }
        else if (card.getBlueprint().getUniqueness() == Uniqueness.UNIQUE) {
            _apprenticeTitles.removeAll(card.getBlueprint().getTitles());
        }
    }

    public void playerDrawsCardsIntoStartingHandFromReserveDeck(String player, int openingHandSize) {
        // Make sure no 'insert' card is revealed during drawing of starting hand
        for (int i = 0; i < openingHandSize; ++i) {
            List<PhysicalCard> reserveDeck = getReserveDeck(player, true);
            if (reserveDeck.size() < 2) {
                return;
            }
            PhysicalCard card = reserveDeck.get(reserveDeck.size() - 1);
            removeCardsFromZone(Collections.singleton(card));
            addCardToZone(card, Zone.HAND, player);
        }
        shuffleReserveDeck(player);
    }

    public void playerActivatesForce(String player, boolean firstActivated, boolean lastActivated) {
        boolean doNotSkipListenerUpdates = _game.getModifiersQuerying().hasFlagActive(this, ModifierFlag.DO_NOT_SKIP_LISTENER_UPDATES_DURING_FORCE_ACTIVATION, player);

        List<PhysicalCard> deck = _reserveDecks.get(player);
        if (!deck.isEmpty()) {
            PhysicalCard card = deck.get(0);
            removeCardsFromZone(Collections.singleton(card), !doNotSkipListenerUpdates && !firstActivated, !doNotSkipListenerUpdates && !lastActivated);
            boolean insertCardFound = isInsertCardFound();
            addCardToTopOfZone(card, Zone.FORCE_PILE, player, true, !doNotSkipListenerUpdates && !firstActivated, !doNotSkipListenerUpdates && !lastActivated && !insertCardFound);
        }
    }

    public void playerUsesForce(String player, boolean firstUsed, boolean lastUsed) {
        List<PhysicalCard> forcePile = _forcePiles.get(player);
        if (!forcePile.isEmpty()) {
            PhysicalCard card = forcePile.get(0);
            removeCardsFromZone(Collections.singleton(card), !firstUsed, !lastUsed);
            addCardToTopOfZone(card, Zone.USED_PILE, player, true, !firstUsed, !lastUsed);
        }
    }

    public void shuffleReserveDeck(String player) {
        shufflePile(player, Zone.RESERVE_DECK);
    }

    public void shufflePile(String player, Zone zone) {
        if (zone!=Zone.RESERVE_DECK && zone!=Zone.FORCE_PILE
                && zone!=Zone.USED_PILE && zone!=Zone.LOST_PILE)
            return;

        List<PhysicalCard> cardsInPile = getZoneCards(player, zone);
        if (cardsInPile.size() > 1) {
            // Tell game listener to remove top card before shuffling
            PhysicalCard topCard = cardsInPile.get(0);
            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardsRemoved(player, Collections.singleton(topCard));

            topCard.setZone(zone);
            // Keep shuffling until top card in pile is not an "inserted" card,
            // or minimum times to shuffle reached.
            int minTimesToShuffle = (cardsInPile.size() / 30) + 1;
            int timesShuffled = 0;
            do {
                Collections.shuffle(cardsInPile);
                timesShuffled++;
                topCard = cardsInPile.get(0);
            } while (timesShuffled < minTimesToShuffle || topCard.isInserted());

            Zone topZone = GameUtils.getZoneTopFromZone(zone);
            cardsInPile.get(0).setZone(topZone);
            _tableChangedSinceStatsSent = true;

            // Tell game listener to create top card after shuffling
            for (GameStateListener listener : getAllGameStateListeners())
                listener.cardCreated(topCard, this, false);
        }
    }

    public void shuffleCardsIntoPile(Collection<? extends PhysicalCard> cards, String zoneOwner, Zone zone) {
        if (zone!=Zone.RESERVE_DECK && zone!=Zone.FORCE_PILE
                && zone!=Zone.USED_PILE && zone!=Zone.LOST_PILE)
            return;

        for (PhysicalCard card : cards) {
            addCardToZone(card, zone, zoneOwner);
        }

        shufflePile(zoneOwner, zone);
    }

    private Collection<GameStateListener> getAllGameStateListeners() {
        return _game.getAllGameStateListeners();
    }

    public boolean isTableChangedSinceStatsSent() {
        return _tableChangedSinceStatsSent;
    }

    public void sendGameStats(GameStats gameStats) {
        for (GameStateListener listener : getAllGameStateListeners())
            listener.sendGameStats(gameStats);

        _tableChangedSinceStatsSent = false;
    }
}
