package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceUsedPileFaceUpOnReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Lost
 * Title: Through The Force Things You Will See
 */
public class Card4_064 extends AbstractLostInterrupt {
    public Card4_064() {
        super(Side.LIGHT, 3, Title.Through_The_Force_Things_You_Will_See, Uniqueness.UNRESTRICTED, ExpansionSet.DAGOBAH, Rarity.R);
        setLore("One training exercise for a Jedi's apprentice is to invert one's view to see things from a different perspective. 'The future, the past. Old friends long gone.'");
        setGameText("At the end of any player's draw phase, cause that player to place Used Pile face up on top of Reserve Deck. Shuffle, cut, and replace. When face-up cards are removed from Reserve Deck, they are treated as normal (no longer remain face up).");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        if (TriggerConditions.isEndOfEachPhase(game, effectResult, Phase.DRAW)) {
            final String drawPhasePlayer = game.getGameState().getCurrentPlayerId();
            if (GameConditions.hasUsedPile(game, drawPhasePlayer)) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Place " + drawPhasePlayer + "'s Used Pile face up on Reserve Deck");
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                action.appendEffect(
                                        new PlaceUsedPileFaceUpOnReserveDeckEffect(action, drawPhasePlayer));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
