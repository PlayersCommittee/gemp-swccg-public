package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;

import java.util.Collections;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Flash Of Insight
 */
public class Card4_023 extends AbstractNormalEffect {
    public Card4_023() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Flash Of Insight", Uniqueness.UNIQUE);
        setLore("Occasionally Han was capable of such feats, even without Threepio there to tell him these things.");
        setGameText("Deploy on your side of table. At any time, you may use 3 Force to draw the top card of your Reserve Deck into your hand. If that card is a space creature, you may immediately deploy it for free.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.canUseForce(game, playerId, 3)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Draw top card of Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 3));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId) {
                        @Override
                        protected void cardDrawnIntoHand(final PhysicalCard cardDrawn) {
                            if (Filters.and(Filters.space_creature, Filters.deployable(self, null, true, 0)).accepts(game, cardDrawn)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Do you want to deploy " + GameUtils.getCardLink(cardDrawn) + " for free?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.appendEffect(
                                                                new DeployCardFromHandEffect(action, cardDrawn, true));
                                                    }
                                                }));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}