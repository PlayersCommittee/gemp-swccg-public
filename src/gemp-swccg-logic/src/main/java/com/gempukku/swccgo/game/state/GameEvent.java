package com.gempukku.swccgo.game.state;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.timing.GameStats;

import java.util.Collection;
import java.util.List;

public class GameEvent {
    public enum Type {
        P, GPC, TC,
        PCIP, PCIPAR, MCIP, RCFP, RCIP, ROCIP, FCIP, RLFP, TCO,
        // Attack
        SA, EA,
        // Battle
        SB, RFB, ATB, EB,
        // Duel
        SD, ED,
        // Lightsaber Combat
        SLC, ELC,
        // Sabacc
        SS, RSH, ES,
        M, W,
        GS,
        CAC, IP, DD, CA, D
    }

    private String _message;
    private String _side;
    private Type _type;
    private Zone _zone;
    private String _participantId;
    private String _zoneOwnerId;
    private Integer _locationIndex;
    private String _systemName;
    private List<String> _allParticipantIds;
    private Integer _index;
    private String _blueprintId;
    private String _testingText;
    private String _backSideTestingText;
    private Integer _cardId;
    private Integer _targetCardId;
    private String _phase;
    private Boolean _inverted;
    private Boolean _sideways;
    private Boolean _frozen;
    private Boolean _suspendedOrTurnedOff;
    private Boolean _collapsed;
    private Integer _count;
    private String _destinyText;
    private int[] _otherCardIds;
    private int[] _otherCardIds2;
    private String _playerAttacking;
    private String _playerDefending;
    private Collection<Integer> _locationIndexes;
    private GameStats _gameStats;
    private AwaitingDecision _awaitingDecision;

    public GameEvent(Type type) {
        _type = type;
    }

    public Integer getIndex() {
        return _index;
    }

    public GameEvent index(int index) {
        _index = index;
        return this;
    }

    public Type getType() {
        return _type;
    }

    public GameStats getGameStats() {
        return _gameStats;
    }

    public GameEvent gameStats(GameStats gameStats) {
        _gameStats = gameStats;
        return this;
    }

    public AwaitingDecision getAwaitingDecision() {
        return _awaitingDecision;
    }

    public GameEvent awaitingDecision(AwaitingDecision awaitingDecision) {
        _awaitingDecision = awaitingDecision;
        return this;
    }

    public Zone getZone() {
        return _zone;
    }

    public GameEvent zone(Zone zone) {
        _zone = zone;
        return this;
    }

    public String getMessage() {
        return _message;
    }

    public GameEvent message(String message) {
        _message = message;
        return this;
    }

    public Integer getCount() {
        return _count;
    }

    public GameEvent count(int count) {
        _count = count;
        return this;
    }

    public int[] getOtherCardIds() {
        return _otherCardIds;
    }

    public GameEvent otherCardIds(int[] otherCardIds) {
        _otherCardIds = otherCardIds;
        return this;
    }

    public int[] getOtherCardIds2() {
        return _otherCardIds2;
    }

    public GameEvent otherCardIds2(int[] otherCardIds2) {
        _otherCardIds2 = otherCardIds2;
        return this;
    }

    public String getPlayerAttacking() {
        return _playerAttacking;
    }

    public GameEvent playerAttacking(String playerAttacking) {
        _playerAttacking = playerAttacking;
        return this;
    }

    public String getPlayerDefending() {
        return _playerDefending;
    }

    public GameEvent playerDefending(String playerDefending) {
        _playerDefending = playerDefending;
        return this;
    }

    public String getParticipantId() {
        return _participantId;
    }

    public GameEvent participantId(String participantId) {
        _participantId = participantId;
        return this;
    }

    public List<String> getAllParticipantIds() {
        return _allParticipantIds;
    }

    public GameEvent allParticipantIds(List<String> allParticipantIds) {
        _allParticipantIds = allParticipantIds;
        return this;
    }

    public String getSide() {
        return _side;
    }

    public GameEvent side(String side) {
        _side = side;
        return this;
    }

    public String getZoneOwnerId() {
        return _zoneOwnerId;
    }

    public GameEvent zoneOwnerId(String zoneOwnerId) {
        _zoneOwnerId = zoneOwnerId;
        return this;
    }

    public Integer getLocationIndex() {
        return _locationIndex;
    }

    public GameEvent locationIndex(int locationIndex) {
        _locationIndex = locationIndex;
        return this;
    }

    public Collection<Integer> getLocationIndexes() {
        return _locationIndexes;
    }

    public GameEvent locationIndexes(Collection<Integer> locationIndexes) {
        _locationIndexes = locationIndexes;
        return this;
    }

    public GameEvent card(PhysicalCard physicalCard, GameState gameState, boolean alwaysShowCardFront) {
        GameEvent gameEvent = cardId(physicalCard.getCardId()).blueprintId(physicalCard.getBlueprintId(gameState, alwaysShowCardFront));
        gameEvent = gameEvent.testingText(physicalCard.getTestingText(gameState, alwaysShowCardFront, false));
        gameEvent = gameEvent.backSideTestingText(physicalCard.getTestingText(gameState, alwaysShowCardFront, true));
        gameEvent = gameEvent.participantId(physicalCard.getOwner()).zone(physicalCard.getZone()).zoneOwnerId(physicalCard.getZoneOwner());
        gameEvent = gameEvent.locationIndex(physicalCard.getLocationZoneIndex()).inverted(physicalCard.isInverted()).sideways(physicalCard.isSideways());
        gameEvent = gameEvent.frozen(physicalCard.isFrozen()).suspendedOrTurnedOff(physicalCard.isSuspended() || physicalCard.isBinaryOff() || physicalCard.isMissing());
        gameEvent = gameEvent.collapsed(physicalCard.isCollapsed()).phase(gameState.getCurrentPhase().toString());

        // Get the system name (or starship/vehicle persona or card id) related to this location
        if (physicalCard.getBlueprint().getCardCategory() == CardCategory.LOCATION) {
            if (physicalCard.getPartOfSystem() != null) {
                gameEvent = gameEvent.systemName(physicalCard.getPartOfSystem());
            } else if (physicalCard.getSystemOrbited() != null) {
                gameEvent = gameEvent.systemName(physicalCard.getSystemOrbited());
            } else if (physicalCard.getBlueprint().getRelatedStarshipOrVehiclePersona() != null) {
                gameEvent = gameEvent.systemName(physicalCard.getBlueprint().getRelatedStarshipOrVehiclePersona().name());
            } else if (physicalCard.getRelatedStarshipOrVehicle() != null) {
                if (!physicalCard.getRelatedStarshipOrVehicle().getBlueprint().getPersonas().isEmpty())
                    gameEvent = gameEvent.systemName(physicalCard.getRelatedStarshipOrVehicle().getBlueprint().getPersonas().iterator().next().name());
                else
                    gameEvent = gameEvent.systemName(String.valueOf(physicalCard.getRelatedStarshipOrVehicle().getCardId()));
            }
        }

        PhysicalCard attachedTo = physicalCard.getAttachedTo();
        if (attachedTo != null)
            gameEvent = gameEvent.targetCardId(attachedTo.getCardId());
        PhysicalCard stackedOn = physicalCard.getStackedOn();
        if (stackedOn != null)
            gameEvent = gameEvent.targetCardId(stackedOn.getCardId());
        return gameEvent;
    }

    public String getBlueprintId() {
        return _blueprintId;
    }

    public GameEvent blueprintId(String blueprintId) {
        _blueprintId = blueprintId;
        return this;
    }

    public String getTestingText() {
        return _testingText;
    }

    public GameEvent testingText(String text) {
        _testingText = text;
        return this;
    }

    public String getBackSideTestingText() {
        return _backSideTestingText;
    }

    public GameEvent backSideTestingText(String text) {
        _backSideTestingText = text;
        return this;
    }

    public Integer getCardId() {
        return _cardId;
    }

    public GameEvent cardId(int cardId) {
        _cardId = cardId;
        return this;
    }

    public String getSystemName() {
        return _systemName;
    }

    public GameEvent systemName(String systemName) {
        _systemName = systemName;
        return this;
    }

    public Integer getTargetCardId() {
        return _targetCardId;
    }

    public GameEvent targetCardId(int targetCardId) {
        _targetCardId = targetCardId;
        return this;
    }

    public String getPhase() {
        return _phase;
    }

    public GameEvent phase(String phase) {
        _phase = phase;
        return this;
    }

    public Boolean isInverted() {
        return _inverted;
    }

    public GameEvent inverted(Boolean inverted) {
        _inverted = inverted;
        return this;
    }

    public Boolean isSideways() {
        return _sideways;
    }

    public GameEvent sideways(Boolean sideways) {
        _sideways = sideways;
        return this;
    }

    public Boolean isFrozen() {
        return _frozen;
    }

    public GameEvent frozen(Boolean frozen) {
        _frozen = frozen;
        return this;
    }

    public Boolean isSuspendedOrTurnedOff() {
        return _suspendedOrTurnedOff;
    }

    public GameEvent suspendedOrTurnedOff(Boolean suspendedOrTurnedOff) {
        _suspendedOrTurnedOff = suspendedOrTurnedOff;
        return this;
    }

    public Boolean isCollapsed() {
        return _collapsed;
    }

    public GameEvent collapsed(Boolean collapsed) {
        _collapsed = collapsed;
        return this;
    }

    public String getDestinyText() {
        return _destinyText;
    }

    public GameEvent destinyText(String destinyType) {
        _destinyText = destinyType;
        return this;
    }
}
