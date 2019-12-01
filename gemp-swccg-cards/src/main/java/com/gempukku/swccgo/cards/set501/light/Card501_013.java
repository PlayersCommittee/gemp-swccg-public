package com.gempukku.swccgo.cards.set501.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 5
 * Type: Effect
 * Title: Leia Of Alderaan (V) (Errata)
 */
public class Card501_013 extends AbstractNormalEffect {
    public Card501_013() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, Title.Leia_Of_Alderaan, Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("The face that launched a thousand starships.");
        setGameText("Deploy on your female. Opponent may not cancel or reduce Force drains at same battleground. If on Leia, she is a general, power +2 and, once per turn, may cancel the immunity to attrition of a character (or game text of a [Jabba's Palace] alien) here for remainder of turn.");
        addIcons(Icon.CLOUD_CITY, Icon.VIRTUAL_SET_5);
        setTestingText("Leia of Alderaan (V) (Errata)");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.female);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.female;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter sameBattleground = Filters.and(Filters.sameLocation(self), Filters.battleground);
        Condition isOnLeia = new AttachedCondition(self, Filters.Leia);
        Filter attachedTo = Filters.hasAttached(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, sameBattleground, opponent, null));
        modifiers.add(new ForceDrainsMayNotBeReducedModifier(self, sameBattleground, opponent, null));
        modifiers.add(new KeywordModifier(self, attachedTo, isOnLeia, Keyword.GENERAL));
        modifiers.add(new PowerModifier(self, attachedTo, isOnLeia, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        // Check condition(s)
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.isAttachedTo(game, self, Filters.Leia)) {
            Filter filter1 = Filters.and(Filters.character, Filters.here(self), Filters.hasAnyImmunityToAttrition);
            if (GameConditions.canTarget(game, self, filter1)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Cancel character's immunity to attrition");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose character", filter1) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Allow response(s)
                                action.allowResponses("Cancel " + GameUtils.getCardLink(cardTargeted) + "'s immunity to attrition",
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelImmunityToAttritionUntilEndOfTurnEffect(action, cardTargeted,
                                                                "Cancels " + GameUtils.getCardLink(cardTargeted) + "'s immunity to attrition"));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
            Filter filter2 = Filters.and(Icon.JABBAS_PALACE, Filters.alien, Filters.here(self));
            if (GameConditions.canTarget(game, self, filter2)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Cancel alien's game text");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerTurnEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose alien", filter2) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard cardTargeted) {
                                action.addAnimationGroup(cardTargeted);
                                // Allow response(s)
                                action.allowResponses("Cancel " + GameUtils.getCardLink(cardTargeted) + "'s game text",
                                        new UnrespondableEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelGameTextUntilEndOfTurnEffect(action, cardTargeted));
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}