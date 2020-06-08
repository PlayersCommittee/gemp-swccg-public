package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.CancelCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Location, Location, Location
 */
public class Card4_126 extends AbstractNormalEffect {
    public Card4_126() {
        super(Side.DARK, 2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Location, Location, Location", Uniqueness.UNIQUE);
        setLore("'Mudhole? Slimy? My home this is!'");
        setGameText("Deploy on table. At the end of each player's deploy phase, if they did not deploy a location, they lose 1 Force. Effect canceled if any player deploys three or more locations in a single turn.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String currentPlayer = game.getGameState().getCurrentPlayerId();
        String otherPlayer = game.getOpponent(currentPlayer);

        // Check condition(s)
        if (TriggerConditions.isEndOfPlayersPhase(game, effectResult, Phase.DEPLOY, currentPlayer)
                && !GameConditions.hasDeployedAtLeastXCardsThisTurn(game, currentPlayer, 1, Filters.location)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + currentPlayer + " lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, currentPlayer, 1));
            return Collections.singletonList(action);
        }

        // Check condition(s)
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canBeCanceled(game, self)
                && (GameConditions.hasDeployedAtLeastXCardsThisTurn(game, currentPlayer, 3, Filters.location)
                || GameConditions.hasDeployedAtLeastXCardsThisTurn(game, otherPlayer, 3, Filters.location))) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Cancel");
            action.setActionMsg("Cancel " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new CancelCardOnTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}