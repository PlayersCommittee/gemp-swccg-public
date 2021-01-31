package com.gempukku.swccgo.game;

import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierHook;
import com.gempukku.swccgo.logic.timing.SnapshotData;

import java.util.*;

// This class is the implementation of a PhysicalCard.
//
// Methods implemented by this class allow the state information
// for the physical card to be set or retrieved.
//
public class PhysicalCardImpl implements PhysicalCard, Cloneable {
    private int _permanentCardId;
    private int _cardId;
    private List<Integer> _additionalCardIds = new LinkedList<Integer>();
    private String _frontBlueprintId;
    private String _backBlueprintId;
    private SwccgCardBlueprint _frontBlueprint;
    private SwccgCardBlueprint _backBlueprint;
    private PlayCardOptionId _playCardOptionId;
    private String _originalOwner;
    private String _owner;
    private Zone _zone;
    private String _zoneOwner;
    private int _locationZoneIndex;
    private boolean _isInserted;
    private boolean _isInsertCardRevealed;
    private PhysicalCard _attachedTo;
    private PhysicalCard _stackedOn;
    private boolean _isNotShownOnUserInterface;
    private boolean _isStackedAsInactive;
    private boolean _isStackedViaJediTest5;
    private PhysicalCard _atLocation;
    private List<PhysicalCard> _cardsAttached = new LinkedList<PhysicalCard>();
    private List<PhysicalCard> _cardsStacked = new LinkedList<PhysicalCard>();
    private List<PhysicalCard> _cardsAtLocation = new LinkedList<PhysicalCard>();
    private boolean _inPilotCapacitySlot;
    private boolean _inPassengerCapacitySlot;
    private boolean _inCargoHoldVehicleCapacitySlot;
    private boolean _inCargoHoldStarfighterOrTIECapacitySlot;
    private boolean _inCargoHoldCapitalStarshipCapacitySlot;
    private PhysicalCard _shipdockedWith;
    private boolean _startingLocation;
    private boolean _startingLocationBattleground;
    private boolean _isCrossedOver;
    private boolean _isInverted;
    private boolean _isBlownAway;
    private boolean _isCollapsed;
    private boolean _isSideways;
    private boolean _isHit;
    private boolean _damaged;
    private boolean _disarmed;
    private boolean _ionized;
    private boolean _gameTextCanceled;
    private boolean _suspended;
    private boolean _binaryOff;
    private boolean _mouthClosed;
    private Set<String> _gameTextCanceledForPlayer = new HashSet<String>();
    private Map<Side, Integer> _gameTextExpandedToSideFromCardId = new HashMap<Side, Integer>();
    private Map<Side, Side> _gameTextExpandedToSideFromSide = new HashMap<Side, Side>();
    private float _latestInPlayForfeitValue;
    private boolean _isCrashed;
    private boolean _isConcealed;
    private boolean _isUndercover;
    private boolean _isMissing;
    private boolean _isCaptive;
    private boolean _isCapturedStarship;
    private boolean _isImprisoned;
    private boolean _isFrozen;
    private boolean _isFlipped;
    private boolean _isObjectiveDeploymentComplete;
    private boolean _isProbeCard;
    private boolean _isHatredCard;
    private boolean _isCoaxiumCard;
    private boolean _isLiberationCard;
    private boolean _isBluffCard;
    private boolean _isCombatCard;
    private boolean _isSpaceSlugBelly;
    private boolean _isRotated;
    private boolean _isRotatedByTurboliftComplex;
    private Float _abilityWhenSoupEaten;
    private boolean _beheaded;
    private boolean _makingBombingRun;
    private boolean _isDejarik;
    private Float _destinyValueToUse;
    private CardState _previousCardState;
    private boolean _previouslyArmedWithLightsaber;
    private boolean _previouslyHit;
    private boolean _previouslyCanceledGameText;
    private Collection<PhysicalCard> _cardsPreviouslyAttached = new LinkedList<PhysicalCard>();
    private List<ModifierHook> _modifierHooks = new LinkedList<ModifierHook>();
    private WhileInPlayData _whileInPlayData;
    private boolean _leavingTable;
    private JediTestStatus _jediTestStatus;
    private UtinniEffectStatus _utinniEffectStatus;
    private Map<TargetId, Integer> _targetGroupIds = new HashMap<TargetId, Integer>();
    private Map<TargetId, Integer> _targetedCardIds = new HashMap<TargetId, Integer>();
    private Map<TargetId, Filter> _targetedCardFilters = new HashMap<TargetId, Filter>();
    private float _sabaccValue;
    private PhysicalCard _sabaccCardCloned;
    private String _raceDestinyForPlayer;
    private float _immunityToAttritionLessThan;
    private float _immunityToAttritionOfExactly;
    private int _parsec;
    private String _partOfSystem;
    private String _planetOrbited;
    private PhysicalCard _relatedStarshipOrVehicle;
    private MovementDirection _direction;
    // _forRemainderOfGameData stores card data, mapping cardId to an Object, that remains for the rest of the game,
    // which can be used to persist information long after the card leaves play (Example: Anger, Fear, Aggression from Dagobah)
    // and can be access by an ActionProxy created by that card.
    private Map<Integer, ForRemainderOfGameData> _forRemainderOfGameData = new HashMap<Integer, ForRemainderOfGameData>();

    /**
     * Needed to generate snapshot.
     */
    public PhysicalCardImpl() {
    }

    @Override
    public void generateSnapshot(PhysicalCard selfSnapshot, SnapshotData snapshotData) {
        PhysicalCardImpl snapshot = (PhysicalCardImpl) selfSnapshot;

        // Set each field
        snapshot._permanentCardId = _permanentCardId;
        snapshot._cardId = _cardId;
        snapshot._additionalCardIds.addAll(_additionalCardIds);
        snapshot._frontBlueprintId = _frontBlueprintId;
        snapshot._backBlueprintId = _backBlueprintId;
        snapshot._frontBlueprint = _frontBlueprint;
        snapshot._backBlueprint = _backBlueprint;
        snapshot._playCardOptionId = _playCardOptionId;
        snapshot._originalOwner = _originalOwner;
        snapshot._owner = _owner;
        snapshot._zone = _zone;
        snapshot._zoneOwner = _zoneOwner;
        snapshot._locationZoneIndex = _locationZoneIndex;
        snapshot._isInserted = _isInserted;
        snapshot._isInsertCardRevealed = _isInsertCardRevealed;
        snapshot._attachedTo = snapshotData.getDataForSnapshot(_attachedTo);
        snapshot._stackedOn = snapshotData.getDataForSnapshot(_stackedOn);
        snapshot._isNotShownOnUserInterface = _isNotShownOnUserInterface;
        snapshot._isStackedAsInactive = _isStackedAsInactive;
        snapshot._isStackedViaJediTest5 = _isStackedViaJediTest5;
        snapshot._atLocation = snapshotData.getDataForSnapshot(_atLocation);
        for (PhysicalCard card : _cardsAttached) {
            snapshot._cardsAttached.add(snapshotData.getDataForSnapshot(card));
        }
        for (PhysicalCard card : _cardsStacked) {
            snapshot._cardsStacked.add(snapshotData.getDataForSnapshot(card));
        }
        for (PhysicalCard card : _cardsAtLocation) {
            snapshot._cardsAtLocation.add(snapshotData.getDataForSnapshot(card));
        }
        snapshot._inPilotCapacitySlot = _inPilotCapacitySlot;
        snapshot._inPassengerCapacitySlot = _inPassengerCapacitySlot;
        snapshot._inCargoHoldVehicleCapacitySlot = _inCargoHoldVehicleCapacitySlot;
        snapshot._inCargoHoldStarfighterOrTIECapacitySlot = _inCargoHoldStarfighterOrTIECapacitySlot;
        snapshot._inCargoHoldCapitalStarshipCapacitySlot = _inCargoHoldCapitalStarshipCapacitySlot;
        snapshot._shipdockedWith = snapshotData.getDataForSnapshot(_shipdockedWith);
        snapshot._startingLocation = _startingLocation;
        snapshot._startingLocationBattleground = _startingLocationBattleground;
        snapshot._isCrossedOver = _isCrossedOver;
        snapshot._isInverted = _isInverted;
        snapshot._isBlownAway = _isBlownAway;
        snapshot._isCollapsed = _isCollapsed;
        snapshot._isSideways = _isSideways;
        snapshot._isHit = _isHit;
        snapshot._damaged = _damaged;
        snapshot._disarmed = _disarmed;
        snapshot._ionized = _ionized;
        snapshot._gameTextCanceled = _gameTextCanceled;
        snapshot._suspended = _suspended;
        snapshot._binaryOff = _binaryOff;
        snapshot._mouthClosed = _mouthClosed;
        snapshot._gameTextCanceledForPlayer.addAll(_gameTextCanceledForPlayer);
        snapshot._gameTextExpandedToSideFromCardId.putAll(_gameTextExpandedToSideFromCardId);
        snapshot._gameTextExpandedToSideFromSide.putAll(_gameTextExpandedToSideFromSide);
        snapshot._latestInPlayForfeitValue = _latestInPlayForfeitValue;
        snapshot._isCrashed = _isCrashed;
        snapshot._isConcealed = _isConcealed;
        snapshot._isUndercover = _isUndercover;
        snapshot._isMissing = _isMissing;
        snapshot._isCaptive = _isCaptive;
        snapshot._isCapturedStarship = _isCapturedStarship;
        snapshot._isImprisoned = _isImprisoned;
        snapshot._isFrozen = _isFrozen;
        snapshot._isFlipped = _isFlipped;
        snapshot._isObjectiveDeploymentComplete = _isObjectiveDeploymentComplete;
        snapshot._isProbeCard = _isProbeCard;
        snapshot._isHatredCard = _isHatredCard;
        snapshot._isCoaxiumCard = _isCoaxiumCard;
        snapshot._isBluffCard = _isBluffCard;
        snapshot._isLiberationCard = _isLiberationCard;
        snapshot._isCombatCard = _isCombatCard;
        snapshot._isSpaceSlugBelly = _isSpaceSlugBelly;
        snapshot._abilityWhenSoupEaten = _abilityWhenSoupEaten;
        snapshot._beheaded = _beheaded;
        snapshot._makingBombingRun = _makingBombingRun;
        snapshot._isDejarik = _isDejarik;
        snapshot._destinyValueToUse = _destinyValueToUse;
        snapshot._previousCardState = _previousCardState;
        snapshot._previouslyArmedWithLightsaber = _previouslyArmedWithLightsaber;
        snapshot._previouslyHit = _previouslyHit;
        snapshot._previouslyCanceledGameText = _previouslyCanceledGameText;
        for (PhysicalCard card : _cardsPreviouslyAttached) {
            snapshot._cardsPreviouslyAttached.add(snapshotData.getDataForSnapshot(card));
        }
        for (ModifierHook modifierHook : _modifierHooks) {
            snapshot._modifierHooks.add(snapshotData.getDataForSnapshot(modifierHook));
        }
        snapshot._whileInPlayData = snapshotData.getDataForSnapshot(_whileInPlayData);
        snapshot._leavingTable = _leavingTable;
        snapshot._jediTestStatus = _jediTestStatus;
        snapshot._utinniEffectStatus = _utinniEffectStatus;
        snapshot._targetGroupIds.putAll(_targetGroupIds);
        snapshot._targetedCardIds.putAll(_targetedCardIds);
        snapshot._targetedCardFilters.putAll(_targetedCardFilters);
        snapshot._sabaccValue = _sabaccValue;
        snapshot._sabaccCardCloned = snapshotData.getDataForSnapshot(_sabaccCardCloned);
        snapshot._raceDestinyForPlayer = _raceDestinyForPlayer;
        snapshot._immunityToAttritionLessThan = _immunityToAttritionLessThan;
        snapshot._immunityToAttritionOfExactly = _immunityToAttritionOfExactly;
        snapshot._parsec = _parsec;
        snapshot._partOfSystem = _partOfSystem;
        snapshot._planetOrbited = _planetOrbited;
        snapshot._relatedStarshipOrVehicle = snapshotData.getDataForSnapshot(_relatedStarshipOrVehicle);
        snapshot._direction = _direction;
        for (Map.Entry<Integer, ForRemainderOfGameData> entry : _forRemainderOfGameData.entrySet()) {
            snapshot._forRemainderOfGameData.put(entry.getKey(), snapshotData.getDataForSnapshot(entry.getValue()));
        }
    }

    public PhysicalCardImpl(int cardId, String frontBlueprintId, String backBlueprintId, String owner, SwccgCardBlueprint frontBlueprint, SwccgCardBlueprint backBlueprint) {
        _permanentCardId = cardId;
        _cardId = cardId;
        _frontBlueprintId = frontBlueprintId;
        _backBlueprintId = backBlueprintId;
        _originalOwner = owner;
        _owner = owner;
        _frontBlueprint = frontBlueprint;
        _destinyValueToUse = _frontBlueprint.getDestiny();
        if (frontBlueprint.getCardCategory() == CardCategory.LOCATION) {
            _partOfSystem = frontBlueprint.getSystemName();
        }
        _backBlueprint = backBlueprint;
    }

    @Override
    public PhysicalCard clone() throws CloneNotSupportedException {
        return (PhysicalCard) super.clone();
    }

    @Override
    public String getTitle() {
        SwccgCardBlueprint blueprint = getBlueprint();

        // Special case for Big One: Asteroid Cave Or Space Slug Belly
        if (blueprint.getTitle().equals(Title.Big_One_Asteroid_Cave_Or_Space_Slug_Belly)
                && _zone != null && (_zone.isInPlay() || _zone == Zone.CONVERTED_LOCATIONS)) {
            if (_isSpaceSlugBelly)
                return Title.Space_Slug_Belly;
            else
                return Title.Big_One_Asteroid_Cave;
        }
        if (blueprint.getCardCategory()==CardCategory.LOCATION && blueprint.getUniqueness() != null
                && blueprint.getUniqueness().isPerSystem() && _partOfSystem != null) {
            return _partOfSystem + ": " + blueprint.getTitle();
        }
        return blueprint.getTitle();
    }

    @Override
    public List<String> getTitles() {
        SwccgCardBlueprint blueprint = getBlueprint();
        if (blueprint.getCardCategory()==CardCategory.LOCATION) {
            return Collections.singletonList(getTitle());
        }
        return blueprint.getTitles();
    }

    @Override
    public boolean isDoubleSided() {
        return _backBlueprint != null;
    }

    @Override
    public String getBlueprintId(boolean alwaysShowCardFront) {
        return getBlueprintId(null, alwaysShowCardFront);
    }

    @Override
    public String getBlueprintId(GameState gameState, boolean alwaysShowCardFront) {
        if (alwaysShowCardFront)
            return _frontBlueprintId;

        if (isFlipped())
            return _backBlueprintId;

        if (_isBlownAway
                || (_zone != null
                && ((_zone.isFaceDown() && !_isInserted && (_zone != Zone.TOP_OF_USED_PILE || (gameState != null && !gameState.isUsedPilesTurnedOver())))
                || (gameState != null && _zone == Zone.TOP_OF_LOST_PILE && gameState.isLostPileTurnedOver(getZoneOwner()))))) {
            return _backBlueprintId;
        }

        return _frontBlueprintId;
    }

    @Override
    public String getTestingText(GameState gameState, boolean alwaysShowCardFront, boolean showOtherSide) {
        if (alwaysShowCardFront) {
            if (showOtherSide)
                return _backBlueprint != null ? _backBlueprint.getTestingText() : null;
            else
                return _frontBlueprint.getTestingText();
        }

        if (isFlipped()) {
            if (showOtherSide)
                return _frontBlueprint.getTestingText();
            else
                return _backBlueprint != null ? _backBlueprint.getTestingText() : null;
        }

        if (_isBlownAway
                || (_zone != null
                && ((_zone.isFaceDown() && !_isInserted && (_zone != Zone.TOP_OF_USED_PILE || (gameState != null && !gameState.isUsedPilesTurnedOver())))
                || (gameState != null && _zone == Zone.TOP_OF_LOST_PILE && gameState.isLostPileTurnedOver(getZoneOwner()))))) {
            if (showOtherSide)
                return null;
            else
                return _backBlueprint != null ? _backBlueprint.getTestingText() : null;
        }

        if (showOtherSide)
            return _backBlueprint != null ? _backBlueprint.getTestingText() : null;
        else
            return _frontBlueprint.getTestingText();
    }

    @Override
    public void setZone(Zone zone) {
        _zone = zone;
    }

    @Override
    public Zone getZone() {
        return _zone;
    }

    @Override
    public void setZoneOwner(String playerId) {
        _zoneOwner = playerId;
    }

    @Override
    public String getZoneOwner() {
        return _zoneOwner;
    }

    @Override
    public void setLocationZoneIndex(int index) {
        _locationZoneIndex = index;
    }

    @Override
    public int getLocationZoneIndex() {
        if (_atLocation!=null)
            return _atLocation.getLocationZoneIndex();

        return _locationZoneIndex;
    }

    @Override
    public void setOwner(String playerId) {
        if (!playerId.equals(_owner)) {
            if ("-1_1".equals(_backBlueprintId)) {
                _backBlueprintId = "-1_2";
            }
            else if ("-1_2".equals(_backBlueprintId)) {
                _backBlueprintId = "-1_1";
            }
            _owner = playerId;
        }
    }

    @Override
    public String getOwner() {
        return _owner;
    }

    @Override
    public boolean isStolen() {
        return getBlueprint().isAlwaysStolen() || !_owner.equals(_originalOwner);
    }

    @Override
    public void startAffectingGame(SwccgGame game) {
        SwccgCardBlueprint blueprint = getBlueprint();

        List<Modifier> modifiers;
        if (_zone == Zone.STACKED)
            modifiers = blueprint.getWhileStackedModifiers(game, this);
        else
            modifiers = blueprint.getWhileInPlayModifiers(game, this);

        for (Modifier modifier : modifiers) {
            _modifierHooks.add(game.getModifiersEnvironment().addAlwaysOnModifier(modifier));
        }
    }

    @Override
    public void stopAffectingGame() {
        for (ModifierHook modifierHook : _modifierHooks) {
            modifierHook.stop();
        }
        _modifierHooks.clear();
    }

    @Override
    public int getPermanentCardId() {
        return _permanentCardId;
    }

    @Override
    public void setCardId(int cardId) {
        _cardId = cardId;
    }

    @Override
    public int getCardId() {
        return _cardId;
    }

    /**
     * Sets the additional IDs for a card that changes anytime the card enters/leaves play or changes off-table zones.
     * Note: This is used when a squadron replaces starfighters so the squadron keeps all the replaced starfighters card IDs.
     * @param cardIds the card IDs
     */
    @Override
    public void setAdditionalCardIds(List<Integer> cardIds) {
        _additionalCardIds.clear();
        if (cardIds != null) {
            _additionalCardIds.addAll(cardIds);
        }
    }

    /**
     * Gets the additional ID for a card that changes anytime the card enters/leaves play or changes off-table zones.
     * Note: This is used when a squadron replaces starfighters so the squadron keeps all the replaced starfighters card IDs.
     * @return the card IDs
     */
    @Override
    public List<Integer> getAdditionalCardIds() {
        return _additionalCardIds;
    }

    @Override
    public PlayCardOptionId getPlayCardOptionId() {
        return _playCardOptionId;
    }

    @Override
    public void setPlayCardOptionId(PlayCardOptionId optionId) {
        _playCardOptionId = optionId;
    }

    @Override
    public SwccgCardBlueprint getBlueprint() {
        return (isDoubleSided() && isFlipped()) ? _backBlueprint : _frontBlueprint;
    }

    @Override
    public SwccgCardBlueprint getOtherSideBlueprint() {
        if (isDoubleSided()) {
            return isFlipped() ? _frontBlueprint : _backBlueprint;
        }
        return null;
    }

    /**
     * Attaches the card to the specified card in a specified way.
     * @param physicalCard the card to attach this card to
     * @param asPilot true if attaching in pilot capacity slot, otherwise false
     * @param asPassenger true if attaching as passenger capacity slot, otherwise false
     * @param asVehicle true if attaching in cargo bay vehicle capacity slot, otherwise false
     * @param asStarfighterOrTIE true if attaching in cargo bay starfighter or TIE capacity slot, otherwise false
     * @param asCapitalStarship true if attaching in cargo bay capital starship capacity slot, otherwise false
     */
    @Override
    public void attachTo(PhysicalCard physicalCard, boolean asPilot, boolean asPassenger, boolean asVehicle, boolean asStarfighterOrTIE, boolean asCapitalStarship) {
        if (_attachedTo != null) {
            _attachedTo.getCardsAttached().remove(this);
        }
        _attachedTo = physicalCard;

        if (physicalCard == null) {
            _inPilotCapacitySlot = false;
            _inPassengerCapacitySlot = false;
            _inCargoHoldVehicleCapacitySlot = false;
            _inCargoHoldStarfighterOrTIECapacitySlot = false;
            _inCargoHoldCapitalStarshipCapacitySlot = false;
            return;
        }

        _inPilotCapacitySlot = asPilot;
        _inPassengerCapacitySlot = asPassenger;
        _inCargoHoldVehicleCapacitySlot = asVehicle;
        _inCargoHoldStarfighterOrTIECapacitySlot = asStarfighterOrTIE;
        _inCargoHoldCapitalStarshipCapacitySlot = asCapitalStarship;
        _attachedTo.getCardsAttached().add(this);
    }

    /**
     * Gets the card that this card is attached to.
     * @return the card this is attached to, or null
     */
    @Override
    public PhysicalCard getAttachedTo() {
        return _attachedTo;
    }

    /**
     * Determines if this card in the pilot capacity slot of the card it is attached to.
     * @return true or false
     */
    @Override
    public boolean isPilotOf() {
        return _inPilotCapacitySlot;
    }

    /**
     * Determines if this card in the passenger capacity slot of the card it is attached to.
     * @return true or false
     */
    @Override
    public boolean isPassengerOf() {
        return _inPassengerCapacitySlot;
    }

    /**
     * Determines if this card in cargo bay vehicle capacity slot of the card it is attached to.
     * @return true or false
     */
    @Override
    public boolean isInCargoHoldAsVehicle() {
        return _inCargoHoldVehicleCapacitySlot;
    }

    /**
     * Determines if this card in cargo bay starship capacity slot of the card it is attached to.
     * @return true or false
     */
    @Override
    public boolean isInCargoHoldAsStarfighterOrTIE() {
        return _inCargoHoldStarfighterOrTIECapacitySlot;
    }

    /**
     * Determines if this card in cargo bay capital starship capacity slot of the card it is attached to.
     * @return true or false
     */
    @Override
    public boolean isInCargoHoldAsCapitalStarship() {
        return _inCargoHoldCapitalStarshipCapacitySlot;
    }

    @Override
    public List<PhysicalCard> getCardsAttached() {
        return _cardsAttached;
    }

    @Override
    public List<PhysicalCard> getCardsStacked() {
        return _cardsStacked;
    }

    @Override
    public List<PhysicalCard> getCardsAtLocation() {
        return _cardsAtLocation;
    }

    @Override
    public PhysicalCard getCardAttachedToAtLocation() {
        PhysicalCard curCard = this;
        while (true) {
            if (curCard == null) {
                return null;
            }
            else if (curCard.getZone()==Zone.AT_LOCATION) {
                return curCard;
            }
            else if (curCard.getZone()==Zone.ATTACHED) {
                curCard = curCard.getAttachedTo();
            }
            else {
                return null;
            }
        }
    }

    @Override
    public void updateRememberedInPlayCardInfo(SwccgGame game) {
        if (_zone == Zone.VOID)
            return;

        // Remember previous card state
        _previousCardState = game.getModifiersQuerying().getCardState(game.getGameState(), this, false, false, false, false, false, false, false, false);

        if (!_zone.isInPlay()) {
            _cardsPreviouslyAttached.clear();
            _previouslyArmedWithLightsaber = false;
            _previouslyHit = false;
            _previouslyCanceledGameText = false;
            return;
        }

        // Remember if this card was armed with a lightsaber
        _previouslyArmedWithLightsaber = Filters.armedWith(Filters.lightsaber).accepts(game, this);

        // Remember if this card was 'hit'
        _previouslyHit = _isHit;

        // Remember if this card had canceled game text
        _previouslyCanceledGameText = game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), this);

        // Remember the cards that are attached to this card
        _cardsPreviouslyAttached.clear();
        _cardsPreviouslyAttached.addAll(game.getGameState().getAttachedCards(this));
    }

    @Override
    public Collection<PhysicalCard> getCardsPreviouslyAttached() {
        return _cardsPreviouslyAttached;
    }

    @Override
    public void stackOn(PhysicalCard physicalCard, boolean asInactive, boolean viaJediTest5) {
        if (_stackedOn != null) {
            _stackedOn.getCardsStacked().remove(this);
        }
        _stackedOn = physicalCard;

        if (physicalCard == null) {
            _isStackedAsInactive = false;
            _isNotShownOnUserInterface = false;
            _isStackedViaJediTest5 = false;
            _isInverted = false;
            return;
        }

        _isStackedAsInactive = asInactive;
        _isNotShownOnUserInterface = _stackedOn.getBlueprint().getCardCategory() == CardCategory.EFFECT && _stackedOn.getBlueprint().getCardSubtype() == CardSubtype.STARTING;
        _isStackedViaJediTest5 = viaJediTest5;
        _isInverted = viaJediTest5;
        _stackedOn.getCardsStacked().add(this);
    }

    @Override
    public PhysicalCard getStackedOn() {
        return _stackedOn;
    }

    @Override
    public boolean isNotShownOnUserInterface() {
        return _isNotShownOnUserInterface;
    }

    @Override
    public boolean isStackedAsInactive() {
        return _isStackedAsInactive;
    }

    @Override
    public boolean isStackedAsViaJediTest5() {
        return _isStackedViaJediTest5;
    }

    @Override
    public void shipdockedWith(PhysicalCard physicalCard) {
        _shipdockedWith = physicalCard;
    }

    @Override
    public PhysicalCard getShipdockedWith() {
        return _shipdockedWith;
    }

    @Override
    public void startingLocation(boolean startingLocation, boolean isBattleground) {
        _startingLocation = startingLocation;
        _startingLocationBattleground = isBattleground;
    }

    @Override
    public boolean isStartingLocation() {
        return _startingLocation;
    }

    @Override
    public boolean isStartingLocationBattleground() {
        return _startingLocationBattleground;
    }

    @Override
    public void setCrossedOver(boolean crossedOver) {
        _isCrossedOver = crossedOver;
    }

    @Override
    public boolean isCrossedOver() {
        return _isCrossedOver;
    }


    @Override
    public void setInverted(boolean inverted) {
        _isInverted = inverted;
    }

    @Override
    public boolean isInverted() {
        return _isInverted;
    }

    @Override
    public void setBlownAway(boolean faceDown) {
        _isBlownAway = faceDown;
    }

    @Override
    public boolean isBlownAway() {
        return _isBlownAway;
    }

    @Override
    public void setCollapsed(boolean collapsed) {
        _isCollapsed = collapsed;
    }

    @Override
    public boolean isCollapsed() {
        return _isCollapsed;
    }

    @Override
    public void setSideways(boolean sideways) {
        _isSideways = sideways;
    }

    @Override
    public boolean isSideways() {
        return _isSideways;
    }

    @Override
    public void setHit(boolean hit) {
        _isHit = hit;
    }

    @Override
    public boolean isDisarmed() {
        return _disarmed;
    }

    @Override
    public void setDisarmed(boolean disarmed) {
        _disarmed = disarmed;
    }

    @Override
    public boolean isHit() {
        return _isHit;
    }

    @Override
    public void setDamaged(boolean damaged) {
        _damaged = damaged;
    }

    @Override
    public boolean isDamaged() {
        return _damaged;
    }

    @Override
    public void setCrashed(boolean crashed) {
        _isCrashed = crashed;
    }

    @Override
    public boolean isCrashed() {
        return _isCrashed;
    }

    @Override
    public boolean isIonized() {
        return _ionized;
    }

    @Override
    public void setIonized(boolean ionized) {
        _ionized = ionized;
    }

//    @Override
    public void setRotated(boolean rotated) {
        _isRotated = rotated;
    }

//    @Override
    public boolean isRotated() {
        return _isRotated;
    }

    public void setRotatedByTurboliftComplex (boolean rotated) {_isRotatedByTurboliftComplex = rotated;}

    public boolean isRotatedByTurboliftComplex () {return _isRotatedByTurboliftComplex;}

    @Override
    public float getLatestInPlayForfeitValue() {
        return _latestInPlayForfeitValue;
    }

    @Override
    public void setLatestInPlayForfeitValue(float value) {
        _latestInPlayForfeitValue = value;
    }

    @Override
    public void setGameTextCanceled(boolean canceled) {
        _gameTextCanceled = canceled;
    }

    @Override
    public boolean isGameTextCanceled() {
        return _gameTextCanceled;
    }

    @Override
    public void setLocationGameTextCanceledForPlayer(boolean canceled, String playerId) {
        if (canceled)
            _gameTextCanceledForPlayer.add(playerId);
        else
            _gameTextCanceledForPlayer.remove(playerId);
    }

    @Override
    public boolean isLocationGameTextCanceledForPlayer(String playerId) {
        return _gameTextCanceledForPlayer.contains(playerId);
    }

    @Override
    public CardState getPreviousCardState() {
        return _previousCardState;
    }

    @Override
    public boolean wasPreviouslyArmedWithLightsaber() {
        return _previouslyArmedWithLightsaber;
    }

    @Override
    public boolean wasPreviouslyHit() {
        return _previouslyHit;
    }

    @Override
    public boolean wasPreviouslyCanceledGameText() {
        return _previouslyCanceledGameText;
    }

    @Override
    public void setLocationGameTextExpandedToSideFromCardId(Side toSide, Integer fromCardId) {
        _gameTextExpandedToSideFromCardId.put(toSide, fromCardId);
    }

    @Override
    public Integer getLocationGameTextExpandedToSideFromCardId(Side toSide) {
        return _gameTextExpandedToSideFromCardId.get(toSide);
    }

    @Override
    public void setLocationGameTextExpandedToSideFromSide(Side toSide, Side fromSide) {
        _gameTextExpandedToSideFromSide.put(toSide, fromSide);
    }

    @Override
    public Side getLocationGameTextExpandedToSideFromSide(Side side) {
        return _gameTextExpandedToSideFromSide.get(side);
    }

    @Override
    public void setSuspended(boolean suspended) {
        _suspended = suspended;
    }

    @Override
    public boolean isSuspended() {
        return _suspended;
    }

    @Override
    public void setBinaryOff(boolean binaryOff) {
        _binaryOff = binaryOff;
    }

    @Override
    public boolean isBinaryOff() {
        return _binaryOff;
    }

    @Override
    public void setMouthClosed(boolean mouthClosed) {
        _mouthClosed = mouthClosed;
    }

    @Override
    public boolean isMouthClosed() {
        return _mouthClosed;
    }

    @Override
    public void setDestinyValueToUse(float value) {
        _destinyValueToUse = value;
    }

    @Override
    public Float getDestinyValueToUse() {
        return _destinyValueToUse;
    }

    @Override
    public void atLocation(PhysicalCard physicalCard) {
        if (_atLocation != null) {
            _atLocation.getCardsAtLocation().remove(this);
        }

        _atLocation = physicalCard;

        if (_atLocation != null) {
            _atLocation.getCardsAtLocation().add(this);
        }
    }

    @Override
    public PhysicalCard getAtLocation() {
        return _atLocation;
    }

    @Override
    public WhileInPlayData getWhileInPlayData() {
        return _whileInPlayData;
    }

    @Override
    public void setWhileInPlayData(WhileInPlayData data) {
        _whileInPlayData = data;
    }

    /**
     * Sets that the card is in process of leaving the table.
     * @param leavingTable true or false
     */
    @Override
    public void setLeavingTable(boolean leavingTable) {
        _leavingTable = leavingTable;
    }

    /**
     * Determines whether the card is in process of leaving the table. This is checked so a card leaving table doesn't
     * recapture a captive that just released due to escort leaving the table.
     * @return true
     */
    @Override
    public boolean isLeavingTable() {
        return _leavingTable;
    }

    /**
     * Sets the Jedi Test status. This is only relevant if the card is a Jedi Test in play.
     * @param status the Jedi Test status
     */
    @Override
    public void setJediTestStatus(JediTestStatus status) {
        _jediTestStatus = status;
    }

    /**
     * Gets the Jedi Test status. This is only relevant if the card is a Jedi Test in play.
     * @return the Jedi Test status
     */
    @Override
    public JediTestStatus getJediTestStatus() {
        return _jediTestStatus;
    }

    /**
     * Sets the Utinni Effect status. This is only relevant if the card is an Utinni Effect in play.
     * @param status the Utinni Effect status
     */
    @Override
    public void setUtinniEffectStatus(UtinniEffectStatus status) {
        _utinniEffectStatus = status;
    }

    /**
     * Gets the Utinni Effect status. This is only relevant if the card is an Utinni Effect in play.
     * @return the Utinni Effect status
     */
    @Override
    public UtinniEffectStatus getUtinniEffectStatus() {
        return _utinniEffectStatus;
    }

    /**
     * Sets a card as explicitly targeted by this card.
     * @param targetId the target id
     * @param targetGroupId the target group id of the action that performed targeting, or null
     * @param targetCard the card to target
     * @param validTargetFilter the filter for cards that would be valid to target
     */
    @Override
    public void setTargetedCard(TargetId targetId, Integer targetGroupId, PhysicalCard targetCard, Filter validTargetFilter) {
        if (targetCard == null || validTargetFilter == null) {
            _targetGroupIds.remove(targetId);
            _targetedCardIds.remove(targetId);
            _targetedCardFilters.remove(targetId);
        }
        else {
            _targetGroupIds.put(targetId, targetGroupId);
            _targetedCardIds.put(targetId, targetCard.getCardId());
            _targetedCardFilters.put(targetId, validTargetFilter);
        }
    }

    /**
     * Gets the target group id of the action that was used to set the specified target id.
     * @param targetId the target id
     * @return the target group id, or null
     */
    @Override
    public Integer getTargetGroupId(TargetId targetId) {
        return _targetGroupIds.get(targetId);
    }

    /**
     * Gets the card targeted by this card with the specified target id.
     * @param gameState the game state
     * @param targetId the target id
     * @return the targeted card
     */
    @Override
    public PhysicalCard getTargetedCard(GameState gameState, TargetId targetId) {
        Integer targetCardId = _targetedCardIds.get(targetId);
        if (targetCardId != null) {
            PhysicalCard targetCard = gameState.findCardById(targetCardId);
            if (targetCard == null) {
                _targetedCardIds.remove(targetId);
                _targetedCardFilters.remove(targetId);
            }
            return targetCard;
        }
        return null;
    }

    /**
     * Gets the cards targeted by this card by the currently set target ids.
     * @param gameState the game state
     * @return the map of target ids to targeted cards
     */
    @Override
    public Map<TargetId, PhysicalCard> getTargetedCards(GameState gameState) {
        Map<TargetId, PhysicalCard> targetedCards = new HashMap<TargetId, PhysicalCard>();

        Iterator<TargetId> targetIterator = _targetedCardIds.keySet().iterator();
        while (targetIterator.hasNext()) {
            TargetId targetId = targetIterator.next();
            PhysicalCard targetedCard = gameState.findCardById(_targetedCardIds.get(targetId));
            if (targetedCard == null) {
                targetIterator.remove();
                _targetedCardFilters.remove(targetId);
            } else {
                targetedCards.put(targetId, targetedCard);
            }
        }
        return targetedCards;
    }

    /**
     * Gets the filter for cards that would be valid to target by this card with the specified target id.
     * @param targetId the target id
     * @return the targeted card
     */
    @Override
    public Filter getValidTargetedFilter(TargetId targetId) {
        return _targetedCardFilters.get(targetId);
    }

    /**
     * Updates the filter for cards that would be valid to target by this card with the specified target id.
     * @param targetId the target id
     * @param validTargetFilter the filter for cards that would be valid to target
     */
    @Override
    public void updateValidTargetedFilter(TargetId targetId, Filter validTargetFilter) {
        if (!_targetedCardFilters.containsKey(targetId)) {
            throw new UnsupportedOperationException("Called updateValidTargetedFilter() on invalid targetGroupId " + targetId);
        }
        _targetedCardFilters.put(targetId, validTargetFilter);
    }

    /**
     * Clears the collection of cards explicitly targeted by this card.
     */
    @Override
    public void clearTargetedCards() {
        _targetGroupIds.clear();
        _targetedCardIds.clear();
        _targetedCardFilters.clear();
    }

    @Override
    public void setInserted(boolean inserted) {
        _isInserted = inserted;
    }

    @Override
    public boolean isInserted() {
        return _isInserted;
    }

    @Override
    public void setInsertCardRevealed(boolean revealed) {
        _isInsertCardRevealed = revealed;
    }

    @Override
    public boolean isInsertCardRevealed() {
        return _isInsertCardRevealed;
    }

    @Override
    public void setConcealed(boolean concealed) {
        _isConcealed = concealed;
    }

    @Override
    public boolean isConcealed() {
        return _isConcealed;
    }

    @Override
    public void setUndercover(boolean undercover) {
        _isUndercover = undercover;
    }

    @Override
    public boolean isUndercover() {
        return _isUndercover;
    }

    @Override
    public void setMissing(boolean missing) {
        _isMissing = missing;
    }

    @Override
    public boolean isMissing() {
        return _isMissing;
    }

    @Override
    public void setCapturedStarship(boolean capturedStarship) {
        _isCapturedStarship = capturedStarship;
    }

    @Override
    public boolean isCapturedStarship() {
        return _isCapturedStarship;
    }

    @Override
    public void setCaptive(boolean captive) {
        _isCaptive = captive;
    }

    @Override
    public boolean isCaptive() {
        return _isCaptive;
    }

    @Override
    public void setImprisoned(boolean imprisoned) {
        _isImprisoned = imprisoned;
    }

    @Override
    public boolean isImprisoned() {
        return _isImprisoned;
    }

    @Override
    public void setFrozen(boolean frozen) {
        _isFrozen = frozen;
    }

    @Override
    public boolean isFrozen() {
        return _isFrozen;
    }

    @Override
    public void setFlipped(boolean flipped) {
        _isFlipped = flipped;
        _destinyValueToUse = getBlueprint().getDestiny();
    }

    @Override
    public boolean isFlipped() {
        return _isFlipped;
    }

    @Override
    public void setObjectiveDeploymentComplete(boolean complete) {
        _isObjectiveDeploymentComplete = complete;
    }

    @Override
    public boolean isObjectiveDeploymentComplete() {
        return _isObjectiveDeploymentComplete;
    }

    @Override
    public void setProbeCard(boolean probeCard) {
        _isProbeCard = probeCard;
    }

    @Override
    public boolean isProbeCard() {
        return _isProbeCard;
    }

    @Override
    public void setHatredCard(boolean hatredCard) {
        _isHatredCard = hatredCard;
    }

    @Override
    public boolean isHatredCard() {
        return _isHatredCard;
    }

    @Override
    public boolean isCoaxiumCard() {
        return _isCoaxiumCard;
    }

    @Override
    public void setCoaxiumCard(boolean coaxiumCard) {
        _isCoaxiumCard = coaxiumCard;
    }

    @Override
    public void setLiberationCard(boolean liberationCard) {
        _isLiberationCard = liberationCard;
    }

    @Override
    public boolean isLiberationCard() {
        return _isLiberationCard;
    }

    @Override
    public void setBluffCard(boolean bluffCard) {
        _isBluffCard = bluffCard;
    }

    @Override
    public boolean isBluffCard() {
        return _isBluffCard;
    }

    @Override
    public void setCombatCard(boolean combatCard) {
        _isCombatCard = combatCard;
    }

    @Override
    public boolean isCombatCard() {
        return _isCombatCard;
    }

    @Override
    public void setSpaceSlugBelly(boolean spaceSlugBelly) {
        _isSpaceSlugBelly = spaceSlugBelly;
    }

    @Override
    public boolean isSpaceSlugBelly() {
        return _isSpaceSlugBelly;
    }

    @Override
    public void setSoupEaten(float abilityWhenSoupEaten) {
        _abilityWhenSoupEaten = abilityWhenSoupEaten;
    }

    @Override
    public Float getSoupEaten() {
        return _abilityWhenSoupEaten;
    }

    @Override
    public void setBeheaded() {
        _beheaded = true;
    }

    @Override
    public boolean isBeheaded() {
        return _beheaded;
    }

    @Override
    public void setMakingBombingRun(boolean makingBombingRun) {
        _makingBombingRun = makingBombingRun;
    }

    @Override
    public boolean isMakingBombingRun() {
        return _makingBombingRun;
    }

    @Override
    public void setDejarikHologramAtHolosite(boolean dejarik) {
        _isDejarik = dejarik;
    }

    @Override
    public boolean isDejarikHologramAtHolosite() {
        return _isDejarik;
    }

    @Override
    public void setSabaccValue(float value) {
        setSabaccCardCloned(null);
        _sabaccValue = value;
    }

    @Override
    public float getSabaccValue() {
        if (_sabaccCardCloned != null)
            return _sabaccCardCloned.getSabaccValue();

        return _sabaccValue;
    }

    @Override
    public void setSabaccCardCloned(PhysicalCard clonedCard) {
        _sabaccCardCloned = clonedCard;
    }

    @Override
    public PhysicalCard getSabaccCardCloned() {
        return _sabaccCardCloned;
    }

    @Override
    public void setImmunityToAttritionLessThan(float immunity) {
        _immunityToAttritionLessThan = immunity;
    }

    @Override
    public float getImmunityToAttritionLessThan() {
        return _immunityToAttritionLessThan;
    }

    @Override
    public void setImmunityToAttritionOfExactly(float immunity) {
        _immunityToAttritionOfExactly = immunity;
    }

    @Override
    public float getImmunityToAttritionOfExactly() {
        return _immunityToAttritionOfExactly;
    }

    @Override
    public void setParsec(int parsec) {
        _parsec = parsec;
    }

    @Override
    public int getParsec() {
        return _parsec;
    }

    @Override
    public void setPartOfSystem(String system) {
        _partOfSystem = system;
    }

    @Override
    public String getPartOfSystem() {
        return _partOfSystem;
    }

    @Override
    public void setSystemOrbited(String planet) {
        _planetOrbited = planet;
    }

    @Override
    public String getSystemOrbited() {
        return _planetOrbited;
    }

    @Override
    public String getRaceDestinyForPlayer() {
        return _raceDestinyForPlayer;
    }

    @Override
    public void setRaceDestinyForPlayer(String playerId) {
        _raceDestinyForPlayer = playerId;
    }

    @Override
    public PhysicalCard getRelatedStarshipOrVehicle() {
        return _relatedStarshipOrVehicle;
    }

    @Override
    public void setRelatedStarshipOrVehicle(PhysicalCard starshipOrVehicle) {
        _relatedStarshipOrVehicle = starshipOrVehicle;
    }

    @Override
    public void setMovementDirection(MovementDirection direction) {
        _direction = direction;
    }

    @Override
    public MovementDirection getMovementDirection() {
        return _direction;
    }

    @Override
    public void setForRemainderOfGameData(Integer cardId, ForRemainderOfGameData data) {
        _forRemainderOfGameData.put(cardId, data);
    }

    @Override
    public void clearForRemainderOfGameData() {
        _forRemainderOfGameData.clear();
    }

    @Override
    public Map<Integer, ForRemainderOfGameData> getForRemainderOfGameData() {
        return _forRemainderOfGameData;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
