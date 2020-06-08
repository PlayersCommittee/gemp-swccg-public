package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.ModifyEachBattleDestinyDrawUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeRandomStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;


/**
 * Set: Death Star II
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Our Only Hope
 */
public class Card9_053 extends AbstractUsedOrLostInterrupt {
    public Card9_053() {
        super(Side.LIGHT, 4, "Our Only Hope", Uniqueness.UNIQUE);
        setLore("'The Emperor knew, as I did, if Anakin were to have any offspring, they would be a threat to him.'");
        setGameText("USED: If your Rebel of ability > 2 just won a battle or duel, randomly take into hand one card stacked on Insignificant Rebellion. (Immune to Sense.) LOST: If Luke is in battle (except with Vader), subtract 2 from each of opponent's battle destiny draws.");
        addIcons(Icon.DEATH_STAR_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.Rebel, Filters.abilityMoreThan(2));

        // Check condition(s)
        if (TriggerConditions.wonBattle(game, effectResult, filter)
                || TriggerConditions.wonDuel(game, effectResult, filter)) {
            final PhysicalCard insignificantRebellion = Filters.findFirstActive(game, self, Filters.and(Filters.Insignificant_Rebellion, Filters.hasStacked(Filters.any)));
            if (insignificantRebellion != null) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
                action.setImmuneTo(Title.Sense);
                action.setText("Take stacked card into hand");
                // Allow response(s)
                action.allowResponses("Randomly take a card stacked on " + GameUtils.getCardLink(insignificantRebellion) + " into hand",
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new TakeRandomStackedCardIntoHandEffect(action, playerId, insignificantRebellion));
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Luke)
                && !GameConditions.isDuringBattleWithParticipant(game, Filters.Vader)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Subtract 2 from battle destiny draws");
            // Allow response(s)
            action.allowResponses("Subtract 2 from each of opponent's battle destiny draws",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyEachBattleDestinyDrawUntilEndOfBattleEffect(action, opponent, -2));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}