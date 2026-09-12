package com.gempukku.swccgo.logic.modifiers.querying;

import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.modifiers.LimitCounter;

public interface Limits extends BaseQuery {
    LimitCounter getUntilStartOfPhaseLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase);
    LimitCounter getUntilEndOfPhaseLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId, Phase phase);
    LimitCounter getUntilEndOfBattleLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId);
    LimitCounter getUntilEndOfAttackLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId);
    LimitCounter getUntilEndOfDuelLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId);
    LimitCounter getUntilEndOfTurnLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId);
    LimitCounter getUntilEndOfTurnForCardTitleLimitCounter(String title, GameTextActionId cardAction);
    LimitCounter getUntilEndOfCaptivityLimitCounter(String title, GameTextActionId cardAction, PhysicalCard captive);
    LimitCounter getPerRaceTotalLimitCounter(String title, GameTextActionId cardAction, float raceTotal);
    LimitCounter getUntilEndOfGameLimitCounter(String title, GameTextActionId cardAction);
    LimitCounter getUntilEndOfForceDrainLimitCounter(String title, GameTextActionId cardAction);
    LimitCounter getUntilEndOfForceLossLimitCounter(PhysicalCard card, String playerId, int gameTextSourceCardId, GameTextActionId gameTextActionId);
    LimitCounter getCardTitlePlayedTurnLimitCounter(String title);

    /**
     * Records that the specified persona was played this turn.
     * @param persona the persona
     */
    void recordPersonaPlayedThisTurn(Persona persona);

    /**
     * Determines if the specified persona has already been played this turn.
     * @param persona the persona
     * @return true if the persona has been played this turn, otherwise false
     */
    boolean isPersonaPlayedThisTurn(Persona persona);
}
