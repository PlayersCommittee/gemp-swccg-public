package com.gempukku.swccgo.game;

import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.effects.choose.ChoosePlayerByExistingCardPileEffect;
import com.gempukku.swccgo.logic.effects.choose.ChoosePlayerBySideEffect;
import com.gempukku.swccgo.logic.effects.choose.ChoosePlayerEffect;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * This class is used to represent a way in which a card can be played, specifically needed when an Effect card
 * can be played in multiple ways.
 */
public class PlayCardOption {
    private PlayCardOptionId _playCardOptionId;
    private String _text;
    private PlayCardZoneOption _zoneOption;

    /**
     * Creates a play card option.
     * @param playCardOptionId the play card option id
     * @param zoneOption the play card zone option
     * @param text the text to show on User Interface to identify the action choice
     */
    public PlayCardOption(PlayCardOptionId playCardOptionId, PlayCardZoneOption zoneOption, String text) {
        _playCardOptionId = playCardOptionId;
        _zoneOption = zoneOption;
        _text = text;
    }

    /**
     * Gets the play card option id.
     * @return the id
     */
    public PlayCardOptionId getId() {
        return _playCardOptionId;
    }

    /**
     * Gets the text show on User Interface to identify the action choice.
     * @return the text
     */
    public String getText() {
        return _text;
    }

    /**
     * Gets the zone to play the card to.
     * @return the zone
     */
    public Zone getZone() {
        return _zoneOption.getZone();
    }

    /**
     * Determines if this play card option is valid given the current game state.
     * @param playerId the player performing the play card action
     * @param gameState the game state
     * @return true if option is valid, otherwise false
     */
    public boolean isValidOption(String playerId, GameState gameState) {
        if (_zoneOption.isAsInsertCard()
                && gameState.getGame().getModifiersQuerying().hasFlagActive(gameState, ModifierFlag.MAY_NOT_DEPLOY_INSERT_CARDS, playerId)) {
            return false;
        }

        Zone zoneToCheck = _zoneOption.getZoneThatCannotBeEmpty();
        if (zoneToCheck == null)
            return true;

        int minCardsInZone = _zoneOption.getMinimumCardsInZone();

        // Check number of cards in zone for players
        if (_zoneOption.isYourZoneAnOption()
                && gameState.getCardPileSize(playerId, zoneToCheck) >= minCardsInZone)
            return true;

        if (_zoneOption.isOpponentsZoneAnOption()
                && gameState.getCardPileSize(gameState.getOpponent(playerId), zoneToCheck) >= minCardsInZone)
            return true;

        return false;
    }

    /**
     * Gets the zone owner if the card can only be played to one zone owner.
     * If null is returned, then getChoosePlayerZoneEffect should be called to get an effect to choose the zone owner.
     * @param playerId the player performing the play card action
     * @param gameState the game state
     * @return the zone owner to play the card to, or null
     */
    public String getZoneOwner(String playerId, GameState gameState) {
        if (_zoneOption.isYourZoneAnOption() && !_zoneOption.isOpponentsZoneAnOption())
            return playerId;

        if (!_zoneOption.isYourZoneAnOption() && _zoneOption.isOpponentsZoneAnOption())
            return gameState.getOpponent(playerId);

        return null;
    }

    /**
     * Gets the effect that the specified action should perform to determine which player's zone to choose.
     * @param action the play card action
     * @param playerId the player performing the play card action
     * @param gameState the game state
     * @return the zone owner to play the card to, or null
     */
    public ChoosePlayerEffect getChoosePlayerZoneEffect(Action action, String playerId, GameState gameState) {
        if (!_zoneOption.isYourZoneAnOption() || !_zoneOption.isOpponentsZoneAnOption())
            throw new UnsupportedOperationException("Both players' zones were not an option");

        Zone zoneToChoose = _zoneOption.getZoneThatCannotBeEmpty();
        if (zoneToChoose != null) {
            if (gameState.isZoneEmpty(playerId, zoneToChoose)
                    && gameState.isZoneEmpty(gameState.getOpponent(playerId), zoneToChoose)) {
                throw new UnsupportedOperationException("Neither players' " + zoneToChoose.getHumanReadable() + " exist");
            }

            return new ChoosePlayerByExistingCardPileEffect(action, playerId, zoneToChoose);
        }
        else {
            return new ChoosePlayerBySideEffect(action, playerId);
        }
    }
}
