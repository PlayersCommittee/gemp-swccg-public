package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfCardPlayedModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ModifyManeuverUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.List;


/**
 * Set: A New Hope
 * Type: Interrupt
 * Subtype: Used
 * Title: Corellian Slip
 */
public class Card2_047 extends AbstractUsedInterrupt {
    public Card2_047() {
        super(Side.LIGHT, 4, Title.Corellian_Slip);
        setLore("First perfected by Corellian starship battle tacticians, this dangerous counter-maneuver has saved numerous hot-shot pilots in life-or-death situations.");
        setGameText("If opponent just initiated a Tallon Roll, add your maneuver + ability of one pilot on your targeted starfighter to your total. OR Decrease the maneuver of an opponent's starfighter by 1 for remainder of this turn.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, opponent, Filters.Tallon_Roll, Filters.and(Filters.your(self), Filters.starfighter))
                && TriggerConditions.isPlayingCardTargeting(game, effect, opponent, Filters.Tallon_Roll, Filters.and(Filters.opponents(self), Filters.starfighter))) {
            final PhysicalCard tallonRoll = ((RespondablePlayingCardEffect) effect).getCard();

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add maneuver + ability to total");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddUntilEndOfCardPlayedModifierEffect(action, tallonRoll,
                                            new ModifyGameTextModifier(self, Filters.samePermanentCardId(tallonRoll), ModifyGameTextType.TALLON_ROLL__OPPONENT_ADDS_MANEUVER_AND_ABILITY),
                                            "Adds maneuver + ability to total"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.starfighter, Filters.hasManeuver);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Reduce a starfighter's maneuver");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starfighter", targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Reduce " + GameUtils.getCardLink(targetedCard) + "'s maneuver by 1",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new ModifyManeuverUntilEndOfTurnEffect(action, finalTarget, -1));
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