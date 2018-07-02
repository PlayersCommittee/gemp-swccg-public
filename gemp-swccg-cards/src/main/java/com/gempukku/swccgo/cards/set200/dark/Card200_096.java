package com.gempukku.swccgo.cards.set200.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 0
 * Type: Effect
 * Title: Firepower (V)
 */
public class Card200_096 extends AbstractDefensiveShield {
    public Card200_096() {
        super(Side.DARK, Title.Firepower);
        setVirtualSuffix(true);
        setLore("'About twenty guns. Some on the surface, some on the towers.'");
        setGameText("Plays on table. If opponent moves from a location you occupy during your turn, they lose 2 Force. At end of opponent's turn, if you control two battlegrounds (a site and a system) and opponent deployed a card with ability and did not initiate a battle, may retrieve 1 Force.");
        addIcons(Icon.REFLECTIONS_III, Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.movedFromLocation(game, effectResult, Filters.opponents(self), Filters.occupies(playerId))
                && GameConditions.isDuringYourTurn(game, self)) {
            MovedResult movedResult = (MovedResult) effectResult;
            if (movedResult.isInitialMove()
                    && GameConditions.canTarget(game, self, Filters.in(movedResult.getMovedCards()))) {

                final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Make " + opponent + " lose 2 Force");
                // Perform result(s)
                action.appendEffect(
                        new LoseForceEffect(action, opponent, 2));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(self.getOwner());
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isEndOfOpponentsTurn(game, effectResult, self)
                && GameConditions.hasDeployedAtLeastXCardsWithAbilityThisTurn(game, opponent, 1, Filters.any)
                && !GameConditions.hasInitiatedBattleThisTurn(game, opponent)
                && GameConditions.controls(game, playerId, Filters.battleground_site)
                && GameConditions.controls(game, playerId, Filters.battleground_system)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}