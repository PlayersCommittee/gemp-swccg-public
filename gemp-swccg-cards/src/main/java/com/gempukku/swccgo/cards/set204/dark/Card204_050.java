package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 4
 * Type: Interrupt
 * Subtype: Used
 * Title: Hunting Party (V)
 */
public class Card204_050 extends AbstractUsedInterrupt {
    public Card204_050() {
        super(Side.DARK, 7, "Hunting Party", Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("Sometimes a missing person is found by the wrong search party.");
        setGameText("During your control phase, if your bounty hunter or Trandoshan is escorting a non-frozen captive at a battleground site you control, opponent loses 1 Force. OR If your bounty hunter or Trandoshan just captured a character, opponent loses 1 Force. [Immune to Sense]");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.or(Filters.bounty_hunter, Filters.Trandoshan),
                Filters.escorting(Filters.nonFrozenCaptive), Filters.at(Filters.and(Filters.battleground_site, Filters.controls(playerId)))))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent lose 1 Force");
            action.setImmuneTo(Title.Sense);
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                             // Perform result(s)
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.capturedBy(game, effectResult, Filters.character, Filters.and(Filters.your(self), Filters.or(Filters.bounty_hunter, Filters.Trandoshan)))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent lose 1 Force");
            action.setImmuneTo(Title.Sense);
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new LoseForceEffect(action, opponent, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}