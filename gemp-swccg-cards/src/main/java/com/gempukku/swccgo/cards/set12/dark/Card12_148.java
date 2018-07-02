package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
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
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used
 * Title: Imperial Artillery
 */
public class Card12_148 extends AbstractUsedInterrupt {
    public Card12_148() {
        super(Side.DARK, 7, "Imperial Artillery", Uniqueness.RESTRICTED_2);
        setLore("The Empire always had a large weapons cache held in reserve, in case of an emergency.");
        setGameText("During your control phase, if you have a weapon (except a lightsaber or [Permanent Weapon]) at a battleground you control, opponent loses 1 Force. OR If you just used a weapon (except a lightsaber or [Permanent Weapon]) to 'hit' a character or starship, opponent loses 1 Force. (Immune to Sense.)");
        addIcons(Icon.CORUSCANT);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.canSpot(game, self, Filters.and(Filters.your(self), Filters.weapon, Filters.except(Filters.or(Filters.lightsaber, Icon.PERMANENT_WEAPON)),
                Filters.at(Filters.and(Filters.battleground, Filters.controls(playerId)))))) {

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
        if (TriggerConditions.justHitBy(game, effectResult, Filters.or(Filters.character, Filters.starship),
                Filters.and(Filters.your(self), Filters.weapon, Filters.except(Filters.or(Filters.lightsaber, Icon.PERMANENT_WEAPON))))) {

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