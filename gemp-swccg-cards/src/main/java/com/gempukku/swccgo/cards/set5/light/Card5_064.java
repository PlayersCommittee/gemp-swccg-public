package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Punch It!
 */
public class Card5_064 extends AbstractLostInterrupt {
    public Card5_064() {
        super(Side.LIGHT, 4, "Punch It!", Uniqueness.UNIQUE);
        setLore("Ever since Lando's 'little' maneuver at the Battle of Taanab, his piloting skills had become legendary.");
        setGameText("If Han or your Lando is piloting a starfighter which is defending a battle at a system, add one battle destiny (add two if starfighter is Falcon). Also, starfighter is immune to attrition for remainder of turn.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.your(self), Filters.starfighter, Filters.hasPiloting(self, Filters.and(Filters.your(self), Filters.or(Filters.Han, Filters.Lando))), Filters.defendingBattle, Filters.canBeTargetedBy(self));

        // Check condition(s)
        if (GameConditions.isDuringBattleAt(game, Filters.system)
                && GameConditions.isDuringBattleWithParticipant(game, filter)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add battle destiny and immunity to attrition");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starfighter", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard starfighter) {
                            action.addAnimationGroup(starfighter);
                            String msgText;
                            if (Filters.Falcon.accepts(game, starfighter)) {
                                msgText = "Add two battle destiny and make " + GameUtils.getCardLink(starfighter) + " immune to attrition";
                            }
                            else {
                                msgText = "Add one battle destiny and make " + GameUtils.getCardLink(starfighter) + " immune to attrition";
                            }
                            // Allow response(s)
                            action.allowResponses(msgText,
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalStarship = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new AddBattleDestinyEffect(action, Filters.Falcon.accepts(game, finalStarship) ? 2 : 1));
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new ImmuneToAttritionModifier(self, finalStarship),
                                                            "Makes " + GameUtils.getCardLink(finalStarship) + " immune to attrition"));
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