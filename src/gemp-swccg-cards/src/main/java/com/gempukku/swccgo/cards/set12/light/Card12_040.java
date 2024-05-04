package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractPoliticalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtAndReorderTopCardsOfReserveDeckEffect;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromHandEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Subtype: Political
 * Title: Ascertaining The Truth
 */
public class Card12_040 extends AbstractPoliticalEffect {
    public Card12_040() {
        super(Side.LIGHT, 3, "Ascertaining The Truth", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.U);
        setLore("'We must move quickly to stop the Trade Federation.'");
        setGameText("Deploy on table. If no senator here, you may place a senator here from hand to subtract 3 from any battle destiny just drawn. If an order agenda here, during your move phase you may peek at top 2 cards of any Reserve Deck; replace in any order.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.isBattleDestinyJustDrawn(game, effectResult)
                && !GameConditions.hasStackedCards(game, self, Filters.senator)
                && GameConditions.hasInHand(game, playerId, Filters.or(Filters.senator, Filters.grantedToBePlacedOnOwnersPoliticalEffect))) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Subtract 3 from battle destiny");
            // Pay cost(s)
            action.appendCost(
                    new StackCardFromHandEffect(action, playerId, self, Filters.or(Filters.senator, Filters.grantedToBePlacedOnOwnersPoliticalEffect)));
            // Perform result(s)
            action.appendEffect(
                    new ModifyDestinyEffect(action, -3));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.MOVE)
                && GameConditions.hasStackedCards(game, self, Filters.order_agenda)
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Peek at top 2 cards of Reserve Deck");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Choose target(s)
            action.appendTargeting(
                    new ChooseExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                        @Override
                        protected void pileChosen(SwccgGame game, final String cardPileOwner, Zone cardPile) {
                            action.setActionMsg("Peek at top 2 cards of " + cardPileOwner + "'s " + cardPile.getHumanReadable());
                            // Perform result(s)
                            action.appendEffect(
                                    new PeekAtAndReorderTopCardsOfReserveDeckEffect(action, cardPileOwner, 2));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}