package com.gempukku.swccgo.cards.set200.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.FindMissingCharacterEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RetrieveCardIntoHandEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Set 0
 * Type: Interrupt
 * Subtype: Lost
 * Title: Found Someone You Have (V)
 */
public class Card200_052 extends AbstractLostInterrupt {
    public Card200_052() {
        super(Side.LIGHT, 3, "Found Someone You Have");
        setVirtualSuffix(true);
        setLore("'I'm looking for someone.' 'Looking? Found someone you have I would say.'");
        setGameText("Find one of your missing characters. OR Retrieve the topmost character of your Lost Pile into hand.");
        addIcons(Icon.DAGOBAH, Icon.VIRTUAL_SET_0);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        Filter missingFilter = Filters.and(Filters.your(self), Filters.missing, Filters.character);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, SpotOverride.INCLUDE_MISSING, missingFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Find missing character");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose missing character", SpotOverride.INCLUDE_MISSING, missingFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            // Allow response(s)
                            action.allowResponses("Find " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new FindMissingCharacterEffect(action, finalTarget, false));
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        GameTextActionId gameTextActionId = GameTextActionId.FOUND_SOMEONE_YOU_HAVE__RETRIEVE_TOPMOST_CHARACTER;

        // Check condition(s)
        if (GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Retrieve topmost character into hand");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RetrieveCardIntoHandEffect(action, playerId, true, Filters.character));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}