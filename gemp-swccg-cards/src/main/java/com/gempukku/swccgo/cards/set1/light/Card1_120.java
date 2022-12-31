package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: We're Doomed
 */
public class Card1_120 extends AbstractUsedInterrupt {
    public Card1_120() {
        super(Side.LIGHT, 5, Title.Were_Doomed, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Threepio's many misadventures often threatened his continued operation. The luckless protocol droid developed a healthy sense of cynicism, and a keen grasp of the obvious.");
        setGameText("If you have less than 15 Life Force, play during opponent's control phase. For remainder of turn, each time you must lose Force, the loss is cut in half (round up). If R2-D2 or C-3PO on table, round down.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringOpponentsPhase(game, self, Phase.CONTROL)
                && GameConditions.hasLifeForceLessThan(game, playerId, 15)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Reduce Force loss until end of turn");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new SendMessageEffect(action, playerId + " reduces Force loss until end of the turn using " + GameUtils.getCardLink(self)));
                            Condition onTableCondition = new OnTableCondition(self, Filters.and(Filters.or(Filters.R2D2_or_has_R2D2_as_permanent_astromech, Filters.C3PO), Filters.canBeTargetedBy(self)));
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new SpecialFlagModifier(self, new NotCondition(onTableCondition),
                                                    ModifierFlag.HALVE_AND_ROUND_UP_FORCE_LOSS, playerId), null));
                            action.appendEffect(
                                    new AddUntilEndOfTurnModifierEffect(action,
                                            new SpecialFlagModifier(self, onTableCondition,
                                                    ModifierFlag.HALVE_AND_ROUND_DOWN_FORCE_LOSS, playerId), null));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}