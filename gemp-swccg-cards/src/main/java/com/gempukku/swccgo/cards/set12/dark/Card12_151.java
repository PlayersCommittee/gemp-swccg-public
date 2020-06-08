package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextAndResetPoliticsUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Lost
 * Title: Little Real Power
 */
public class Card12_151 extends AbstractLostInterrupt {
    public Card12_151() {
        super(Side.DARK, 5, "Little Real Power", Uniqueness.UNIQUE);
        setLore("'He is mired by baseless accusations of corruption.'");
        setGameText("During any control phase, target an opponent's character with politics present with your senator at Galactic Senate. For remainder of turn, target's game text is canceled and target is politics = 0.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.opponents(self), Filters.character_with_politics, Filters.at(Filters.Galactic_Senate), Filters.presentWith(self, Filters.and(Filters.your(self), Filters.senator)));

        // Check condition(s)
        if (GameConditions.isDuringEitherPlayersPhase(game, Phase.CONTROL)
                && GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target opponent's character");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text and reset politics to 0",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextAndResetPoliticsUntilEndOfTurnEffect(action, finalTarget, 0));
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