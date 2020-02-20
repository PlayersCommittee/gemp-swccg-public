package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 12
 * Type: Character
 * Subtype: Republic
 * Title: Admiral Trench
 */
public class Card501_043 extends AbstractRepublic {
    public Card501_043() {
        super(Side.DARK, 2, 3, 3, 3, 5, "Admiral Trench", Uniqueness.UNIQUE);
        setLore("Harch Commander.");
        setGameText("Adds 2 to power of anything he pilots. May place Trench in Used Pile to make an [E1] starship here immune to attrition for remainder of turn. While at opponent's system, their starships deploy +1 here.");
        addKeywords(Keyword.ADMIRAL, Keyword.COMMANDER);
        addIcons(Icon.VIRTUAL_SET_12, Icon.SEPARATIST, Icon.PILOT);
        setTestingText("Admiral Trench");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self.getOwner()), Filters.starship), new AtCondition(self, Filters.and(Filters.opponents(self.getOwner()), Filters.system)), 1, Filters.sameLocation(self)));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter ep1StarshipHere = Filters.and(Filters.icon(Icon.EPISODE_I), Filters.starship, Filters.atSameLocation(self));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, ep1StarshipHere)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Place character in Used Pile");
            action.setActionMsg("Place character in Used Pile");

            // Perform result(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Target an EP1 starship here", ep1StarshipHere) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Pay cost(s)
                            action.appendCost(
                                    new PlaceCardInUsedPileFromTableEffect(action, self));
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " immune to attrition for remainder of turn",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action, new ImmuneToAttritionModifier(self, finalTarget), GameUtils.getCardLink(targetedCard) + " is immune to attrition"));
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
