package com.gempukku.swccgo.cards.set222.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 22
 * Type: Interrupt
 * Subtype: Lost
 * Title: Walker Garrison (V)
 */
public class Card222_017 extends AbstractLostInterrupt {
    public Card222_017() {
        super(Side.DARK, 4, Title.Walker_Garrison, Uniqueness.UNRESTRICTED, ExpansionSet.SET_22, Rarity.V);
        setVirtualSuffix(true);
        setLore("When efficiently deployed, a squadron of AT-ATs can quickly take control of a wide area, making it easy for imperial forces to dominate a planet.");
        setGameText("If your [Hoth] objective on table, choose: [upload] [Premium] Veers or a [Premium] AT-AT. " +
                "OR Cancel a Force drain at a site if your AT-AT occupies a related battleground site. OR Retrieve an AT-AT Cannon.");
        addIcons(Icon.PREMIUM, Icon.VIRTUAL_SET_22);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        GameTextActionId gameTextActionId = GameTextActionId.WALKER_GARRISON_V__UPLOAD_CARD;

        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.icon(Icon.HOTH), Filters.Objective))
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take Veers or an AT-AT into hand from Reserve Deck");

            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.and(Icon.PREMIUM, Filters.Veers), Filters.and(Icon.PREMIUM, Filters.AT_AT)), true));
                        }
                    }
            );
            actions.add(action);
        }

        gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;
        // Check condition(s)
        if (GameConditions.canSpot(game, self, Filters.and(Filters.icon(Icon.HOTH), Filters.Objective))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Retrieve AT-AT Canon");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(new RetrieveCardEffect(action, playerId, Filters.AT_AT_Cannon));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.and(Filters.site, Filters.relatedLocationTo(self,
                Filters.and(Filters.occupies(playerId), Filters.sameSiteAs(self, Filters.and(Filters.your(self), Filters.AT_AT, Filters.piloted))))))
                && GameConditions.canSpot(game, self, Filters.and(Filters.icon(Icon.HOTH), Filters.Objective))
                && GameConditions.canCancelForceDrain(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Force drain");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelForceDrainEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}