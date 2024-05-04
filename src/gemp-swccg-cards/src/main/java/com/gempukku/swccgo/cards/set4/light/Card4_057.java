package com.gempukku.swccgo.cards.set4.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.MayNotMoveUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Lost
 * Title: Moving To Attack Position
 */
public class Card4_057 extends AbstractLostInterrupt {
    public Card4_057() {
        super(Side.LIGHT, 4, "Moving To Attack Position", Uniqueness.UNIQUE, ExpansionSet.DAGOBAH, Rarity.C);
        setLore("Sometimes the best defense is an insane offense.");
        setGameText("Target one starfighter participating in a battle at a system. For remainder of turn, starfighter may not move, is immune to attrition and, if piloted by any Corellian, is power +2.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.starfighter, Filters.participatingInBattle);

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.system)
                && GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target a starfighter");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starfighter", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Target " + GameUtils.getCardLink(targetedCard),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new MayNotMoveUntilEndOfTurnEffect(action, finalTarget));
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new ImmuneToAttritionModifier(self, finalTarget),
                                                            "Makes " + GameUtils.getCardLink(finalTarget) + " immune to attrition"));
                                            if (Filters.hasPiloting(self, Filters.Corellian).accepts(game, finalTarget)) {
                                                action.appendEffect(
                                                        new AddUntilEndOfTurnModifierEffect(action,
                                                                new PowerModifier(self, finalTarget, 2),
                                                                "Makes " + GameUtils.getCardLink(finalTarget) + " power +2"));
                                            }
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