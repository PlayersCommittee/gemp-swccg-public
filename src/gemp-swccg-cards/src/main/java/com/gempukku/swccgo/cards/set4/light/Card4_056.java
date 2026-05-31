package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.LoseForceIfLostToAsteroidDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used
 * Title: Lost Relay
 */
public class Card4_056 extends AbstractUsedInterrupt {
    public Card4_056() {
        super(Side.LIGHT, 5, "Lost Relay", Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("Asteroid fields require starfighters to act as comm relays, forwarding orders to the squadron via subspace AE-35 Transceivers. If ships are lost, communications break down.");
        setGameText("Target one opponent's starfighter present with one of your starships at an asteroid sector, before asteroid destiny is drawn this turn. If target lost this turn due to asteroid destiny, opponent also loses Force equal to target's forfeit value.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final Filter targetFilter = Filters.and(Filters.opponents(self), Filters.starfighter, Filters.canBeTargetedBy(self), Filters.presentWith(self, Filters.and(Filters.your(self), Filters.starship, Filters.canBeTargetedBy(self))), Filters.at(Filters.asteroid_sector));

        if (!GameConditions.wasAsteroidDestinyDrawnThisTurn(game)
                && GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target opponent's starfighter");
            // Choose target
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starfighter", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard starfighter) {
                            action.addAnimationGroup(starfighter);
                            // Allow response(s)
                            action.allowResponses("cause force loss equal to " + GameUtils.getCardLink(starfighter) + "'s forfeit if lost due to asteroid destiny this turn",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new LoseForceIfLostToAsteroidDestinyModifier(self, finalTarget),null));
                                        }
                                    }
                            );
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}