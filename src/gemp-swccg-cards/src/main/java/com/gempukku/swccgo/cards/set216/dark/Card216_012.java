package com.gempukku.swccgo.cards.set216.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.DrawOneCardFromForcePileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.AbilityRequiredForBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Interrupt
 * Subtype: Used
 * Title: Projective Telepathy (V)
 */
public class Card216_012 extends AbstractUsedInterrupt {
    public Card216_012() {
        super(Side.DARK, 3, "Projective Telepathy", Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLore("'Luke.' 'Father.' 'Son, come with me.'");
        setGameText("If drawn for destiny, may take into hand. Draw top card of Force Pile. " +
                "OR During opponent's control phase, target a location. Total ability of 7 or more required for opponent to draw battle destiny there for remainder of turn.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalDrawnAsDestinyTriggers(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isDestinyCardMatchTo(game, self)
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Take into hand");
            action.setActionMsg("Take into hand");
            // Pay cost(s)
            action.appendEffect(
                    new TakeDestinyCardIntoHandEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (GameConditions.hasForcePile(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Draw top card of Force Pile");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawOneCardFromForcePileEffect(action, playerId));
                        }
                    }
            );
            actions.add(action);
        }

        if (GameConditions.isDuringOpponentsPhase(game, playerId, Phase.CONTROL)
            && GameConditions.canTarget(game, self, Filters.location)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);

            action.setText("Target location");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target a location", Filters.location) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    // Allow response(s)
                    action.allowResponses("Require 7 or more ability for " + game.getOpponent(playerId) + " to draw battle destiny at " + GameUtils.getCardLink(targetedCard),
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Get the targeted card(s) from the action using the targetGroupId.
                                    // This needs to be done in case the target(s) were changed during the responses.
                                    PhysicalCard finalLocation = action.getPrimaryTargetCard(targetGroupId);

                                    // Perform result(s)
                                    action.appendEffect(new AddUntilEndOfTurnModifierEffect(action, new AbilityRequiredForBattleDestinyModifier(self, finalLocation, 7, game.getOpponent(playerId)), null));
                                }
                            }
                    );
                }
            });


            actions.add(action);
        }

        return actions;
    }
}
