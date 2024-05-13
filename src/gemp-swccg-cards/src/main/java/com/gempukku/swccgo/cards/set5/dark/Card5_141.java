package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddDuelDestinyEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.MayNotUseAbilityTowardDrawingBattleDestinyUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.ModifyPowerUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Focused Attack
 */
public class Card5_141 extends AbstractLostInterrupt {
    public Card5_141() {
        super(Side.DARK, 2, Title.Focused_Attack, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("Not since his duel with Obi-Wan had Vader unleashed the raw power of the dark side.");
        setGameText("If Vader is present during a battle at a site, for remainder of turn, he loses his immunity to attrition, but adds ability to power (he may not apply ability toward drawing battle destiny). OR During a duel, add one destiny to your total.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.Vader, Filters.hasAnyImmunityToAttrition, Filters.presentInBattle);

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add Vader's ability to power");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Vader", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard vader) {
                            action.addAnimationGroup(vader);
                            // Allow response(s)
                            action.allowResponses("Add " + GameUtils.getCardLink(vader) + "'s ability to power",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelImmunityToAttritionUntilEndOfTurnEffect(action, finalTarget,
                                                            "Cancels " + GameUtils.getCardLink(finalTarget) + "'s immunity to attrition"));
                                            float ability = game.getModifiersQuerying().getAbility(game.getGameState(), finalTarget);
                                            action.appendEffect(
                                                    new ModifyPowerUntilEndOfTurnEffect(action, finalTarget, ability));
                                            action.appendEffect(
                                                    new MayNotUseAbilityTowardDrawingBattleDestinyUntilEndOfTurnEffect(action, finalTarget));
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

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isDuelAddOrModifyDuelDestiniesStep(game, effectResult)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one duel destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddDuelDestinyEffect(action, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}