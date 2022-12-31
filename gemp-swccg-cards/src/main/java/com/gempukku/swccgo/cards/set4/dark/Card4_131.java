package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Effect
 * Title: Shot In The Dark
 */
public class Card4_131 extends AbstractNormalEffect {
    public Card4_131() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Shot_In_The_Dark, Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.U);
        setLore("To test his theory that he was not in a cave, Han blasted the floor. He was right.");
        setGameText("Deploy on your side of table. Once per turn, you may lose 1 Force to draw the top card of your Reserve Deck into your hand. If that card is a space creature, you may immediately deploy it for free.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.hasReserveDeck(game, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)) {
            int forceToLoseAsCost = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.SHOT_IN_THE_DARK__LOSE_ADDITIONAL_FORCE_TO_DRAW) ? 2 : 1;

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Draw top card of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, forceToLoseAsCost, true));
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