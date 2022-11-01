package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractResistance;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.PeekAtTopCardOfCardPileEffect;
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
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LookedAtCardsInOwnCardPileResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 7
 * Type: Character
 * Subtype: Resistance
 * Title: Major Taslin Brance
 */
public class Card207_008 extends AbstractResistance {
    public Card207_008() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Major Taslin Brance", Uniqueness.UNIQUE, ExpansionSet.SET_7, Rarity.V);
        setGameText("If opponent just looked at cards in their deck or pile, after replacing, may peek at the top card of that deck or pile. At the start of your control phase, if the top card of your Lost Pile is a Resistance character or [Resistance] starship, opponent loses 1 Force.");
        addIcons(Icon.WARRIOR, Icon.EPISODE_VII, Icon.VIRTUAL_SET_7);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);

        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justLookedAtCardsInOwnCardPile(game, effectResult, opponent)) {
            Zone cardPile = ((LookedAtCardsInOwnCardPileResult) effectResult).getCardPile();
            GameState gameState = game.getGameState();
            if (gameState.getCardPileSize(opponent, cardPile) > 0
                    && !gameState.isCardPileFaceUp(playerId, cardPile)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
                action.setText("Peek at top card of opponent's " + cardPile.getHumanReadable());
                // Perform result(s)
                action.appendEffect(
                        new PeekAtTopCardOfCardPileEffect(action, playerId, opponent, cardPile));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_2;

        // Check condition(s)
        if (TriggerConditions.isStartOfYourPhase(game, self, effectResult, Phase.CONTROL)
                && GameConditions.isTopCardOfLostPileMatchTo(game, playerId, Filters.or(Filters.Resistance_character, Filters.and(Filters.starship, Icon.RESISTANCE)))) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setPerformingPlayer(playerId);
            action.setText("Make opponent lose 1 Force");
            // Perform result(s)
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
