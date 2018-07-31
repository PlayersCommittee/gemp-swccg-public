package com.gempukku.swccgo.cards.set209.dark;


import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHavePowerIncreasedByCardModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * Set: Set 9
 * Type: Interrupt
 * Subtype: Lost
 * Title: Apology Accepted (V)
 */
public class Card209_046 extends AbstractLostInterrupt {
    public Card209_046() {
        super(Side.DARK, 6, "Apology Accepted", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("\"I shall assume full responsibility for losing them and apologize to Lord Vader.\" Needa discovered that Vader was only slightly more forgiving than the Emperor.");
        setGameText("During battle, lose an Imperial leader piloting your Star Destroyer. For remainder of turn, that Star Destroyer draws two battle destiny if unable to otherwise, is immune to attrition, and its power may not be increased by Imperial pilots aboard (except Vader).");
        addIcons(Icon.DAGOBAH, Icon.VIRTUAL_SET_9);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s) for action
        Filter filter = Filters.and(Filters.your(self), Filters.participatingInBattle, Filters.Imperial, Filters.leader, Filters.piloting(Filters.Star_Destroyer));
        if (GameConditions.isDuringBattle(game) && GameConditions.canSpot(game, self, filter)) {

            // Generate action using common method
            PlayInterruptAction action = generatePlayInterruptAction(playerId, game, self, filter);
            if (action != null) {
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private PlayInterruptAction generatePlayInterruptAction(final String playerId, final SwccgGame game, final PhysicalCard self, Filter filter) {
        final PlayInterruptAction action = new PlayInterruptAction(game, self);
        action.setText("Lose Imperial leader piloting your Star Destroyer");
        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, playerId, "Choose Star Destroyer pilot", filter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                        final PhysicalCard starDestroyer = targetedCard.getAttachedTo();
                        action.addAnimationGroup(targetedCard);

                        // Pay cost(s)
                        action.appendCost(
                                new LoseCardFromTableEffect(action, targetedCard));

                        // Allow response(s)
                        action.allowResponses("Make " + GameUtils.getCardLink(starDestroyer) + " immune to attrition and draw two battle destiny if unable to otherwise",
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {

                                        // Perform result(s)
                                        action.appendEffect(
                                                new DrawsBattleDestinyIfUnableToOtherwiseUntilEndOfTurnEffect(action, starDestroyer, 2,
                                                        "Causes " + GameUtils.getCardLink(starDestroyer) + " to draw two battle destiny if unable to otherwise"));
                                        action.appendEffect(
                                                new AddUntilEndOfTurnModifierEffect(action,
                                                        new ImmuneToAttritionModifier(self, starDestroyer),
                                                        "Makes " + GameUtils.getCardLink(starDestroyer) + " immune to attrition"));
                                        action.appendEffect(
                                                new AddUntilEndOfTurnModifierEffect(action,
                                                        new MayNotHavePowerIncreasedByCardModifier(self, starDestroyer, Filters.and(Filters.Imperial, Filters.pilot, Filters.not(Filters.Vader))),
                                                        "Prevents the power of " + GameUtils.getCardLink(starDestroyer) + " from being increased by Imperial pilots aboard (except Vader)"));


                                    }
                                }
                        );
                    }
                }
        );
        return action;
    }
}
